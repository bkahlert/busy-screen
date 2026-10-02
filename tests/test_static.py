"""Static checks over the packages, as pihero's own tier 0 runs them: shellcheck, systemd-analyze verify, cloud-init schema,
and the flow's palette against what the server package vendors."""
import json
import shlex
from pathlib import Path

import pytest

from pihero_testkit import tools

import vm_device

pytestmark = pytest.mark.tier0

ROOT = Path.cwd()
# The units call binaries the tools container does not have; verify only needs them to exist and be executable.
STUBBED_COMMANDS = ("/usr/bin/node",)
STRIP_RPI_KEYS = (
    "import sys, yaml; d = yaml.safe_load(open(sys.argv[1])); "
    "[d.pop(k, None) for k in ('rpi', 'enable_ssh')]; "
    "open(sys.argv[2], 'w').write('#cloud-config\\n' + yaml.safe_dump(d))"
)
FLOW = ROOT / "packages/busy-screen-server/flows.json"
SERVER_PACKAGE = ROOT / "packages/busy-screen-server/server/package.json"
# Node types of Node-RED's core palette that the flow may use (@node-red/nodes), plus those the vendored modules add.
CORE_TYPES = {
    "tab", "subflow", "group", "junction", "comment", "global-config",
    "inject", "debug", "complete", "catch", "status", "link in", "link out", "link call",
    "function", "switch", "change", "range", "template", "delay", "trigger", "exec", "rbe",
    "http in", "http response", "http request", "websocket in", "websocket out", "websocket-listener", "websocket-client",
    "tcp in", "tcp out", "tcp request", "udp in", "udp out", "mqtt in", "mqtt out", "mqtt-broker", "tls-config", "http proxy",
    "split", "join", "sort", "batch", "csv", "html", "json", "xml", "yaml",
    "file", "file in", "watch", "tail",
}
VENDORED_TYPES = {"node-red-contrib-ip": {"ip"}}


def shell_files():
    for path in (ROOT / "packages").rglob("*"):
        if not path.is_file() or path.is_symlink() or ".build" in path.parts or "node_modules" in path.parts:
            continue
        first_line = path.open("rb").readline()
        if path.suffix == ".sh" or (first_line.startswith(b"#!") and b"sh" in first_line):
            yield path


def unit_files():
    yield from (ROOT / "packages").glob("*/root/usr/lib/systemd/system/*.service")


def device_files():
    yield from (ROOT / "devices").glob("*/user-data")
    yield vm_device.write() / "user-data"


@pytest.mark.parametrize("script", sorted(shell_files()), ids=lambda p: str(p.relative_to(ROOT)))
def test_shell_file_passes_shellcheck(script):
    result = tools.run(["shellcheck", f"/work/{script.relative_to(ROOT)}"], check=False, capture=True)

    assert result.returncode == 0, result.stdout


@pytest.mark.parametrize("unit", sorted(unit_files()), ids=lambda p: p.name)
def test_unit_passes_systemd_analyze_verify(unit):
    stubs = ROOT / "dist" / "stubs"
    stubs.mkdir(parents=True, exist_ok=True)
    mounts = []
    for command in STUBBED_COMMANDS:
        stub = stubs / Path(command).name
        stub.write_text("#!/bin/sh\n")
        stub.chmod(0o755)
        mounts.append(f"{stub}:{command}:ro")

    result = tools.run(["systemd-analyze", "verify", "--man=no", f"/work/{unit.relative_to(ROOT)}"], mounts=mounts, check=False, capture=True)

    assert result.returncode == 0, result.stdout + result.stderr


@pytest.mark.parametrize("unit", sorted(unit_files()), ids=lambda p: p.name)
def test_unit_environment_assignments_are_well_formed(unit):
    """systemd splits Environment= like a shell; a value with spaces that is not quoted silently loses its tail."""
    for line in unit.read_text().splitlines():
        if not line.startswith("Environment="):
            continue
        for word in shlex.split(line.removeprefix("Environment=")):
            name, sep, _ = word.partition("=")
            assert sep and name.isidentifier(), f"{unit.name}: {line!r} does not assign NAME=value"


@pytest.mark.parametrize("device", sorted(device_files()), ids=lambda p: p.parent.name)
def test_device_file_passes_the_cloud_init_schema(device):
    # The schema does not know Raspberry Pi OS's `rpi:` module nor `enable_ssh`; the rest is validated.
    stripped = ROOT / "dist" / "schema" / device.parent.name
    stripped.mkdir(parents=True, exist_ok=True)
    tools.run(["python3", "-c", STRIP_RPI_KEYS, f"/work/{device.relative_to(ROOT)}", f"/work/{(stripped / 'user-data').relative_to(ROOT)}"])

    result = tools.run(["cloud-init", "schema", "--config-file", f"/work/{(stripped / 'user-data').relative_to(ROOT)}"], check=False, capture=True)

    assert result.returncode == 0, result.stdout + result.stderr


class TestFlow:
    def test_is_a_node_red_flow(self):
        flow = json.loads(FLOW.read_text())

        assert isinstance(flow, list)
        assert {n["type"] for n in flow} >= {"tab", "http in", "websocket-listener"}

    def test_uses_only_the_vendored_palette(self):
        """A node type Node-RED cannot find leaves the flow half-started with no error at the API."""
        flow = json.loads(FLOW.read_text())
        vendored = set(json.loads(SERVER_PACKAGE.read_text())["dependencies"])
        allowed = CORE_TYPES | {t for module, types in VENDORED_TYPES.items() if module in vendored for t in types}
        subflows = {n["id"] for n in flow if n["type"] == "subflow"}

        used = {n["type"] for n in flow}
        instances = {t for t in used if t.startswith("subflow:")}
        assert {t.removeprefix("subflow:") for t in instances} <= subflows
        assert (used - instances) <= allowed, sorted((used - instances) - allowed)

    def test_wires_point_at_nodes_of_the_flow(self):
        flow = json.loads(FLOW.read_text())
        ids = {n["id"] for n in flow}

        dangling = [(n["id"], w) for n in flow for port in n.get("wires", []) for w in port if w not in ids]

        assert not dangling

    def test_exposes_the_status_api_and_the_info_socket(self):
        flow = json.loads(FLOW.read_text())

        endpoints = {(n["method"], n["url"]) for n in flow if n["type"] == "http in"}
        sockets = {n["path"] for n in flow if n["type"] == "websocket-listener"}

        assert endpoints == {("put", "/status"), ("get", "/info")}
        assert sockets == {"/info"}
