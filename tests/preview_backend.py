"""busy-screen's backend for a preview: Node-RED from the vendored server directory with the repository's flow, seeded with a status."""
import json
import os
import shutil
import signal
import subprocess
import time
import urllib.error
import urllib.request
from dataclasses import dataclass
from pathlib import Path

from pihero_testkit.preview import process

ROOT = Path(__file__).resolve().parents[1]
SERVER = ROOT / "packages" / "busy-screen-server" / "server"
FLOW = ROOT / "packages" / "busy-screen-server" / "flows.json"
STATE = ROOT / "dist" / "preview" / "node-red"
PORT = 1880
FAKE, BOARD, EXTERNAL = "fake", "board", "external"
LOOPBACK = ("localhost", "127.0.0.1", "::1")
DEFAULT_STATUS = {"name": "preview", "task": "busy-screen on the Mac", "duration": "PT10M"}
# The editor runs any code its flow says, and the server's settings leave it open: the fake listens on the loopback only.
SETTINGS = "module.exports = {{ ...require({server_settings}), uiHost: '127.0.0.1' }}\n"


@dataclass(frozen=True)
class Address:
    kind: str
    host: str
    port: int

    def describe(self) -> str:
        if self.kind == FAKE:
            return f"fake on {self.host}:{self.port}"
        if self.kind == BOARD:
            return f"the board's own, {self.host}:{self.port} on the board"
        return f"{self.host}:{self.port}"


def parse_backend(text: str | None) -> Address:
    """Return the backend BACKEND names: unset or `fake`, `board`, or HOST:PORT; raise ValueError for anything else."""
    if text in (None, "", FAKE):
        return Address(FAKE, "localhost", PORT)
    if text == BOARD:
        return Address(BOARD, "127.0.0.1", PORT)
    host, separator, port = text.rpartition(":")
    if not separator or not host or not port.isdigit() or not 0 < int(port) < 65536:
        raise ValueError(f"BACKEND must be fake, board or HOST:PORT, not {text!r}")
    return Address(EXTERNAL, host, int(port))


def parse_status(text: str | None) -> dict:
    """Return the status STATUS holds as JSON, or the default; raise ValueError for text that is not a JSON object."""
    if not text:
        return DEFAULT_STATUS
    try:
        status = json.loads(text)
    except json.JSONDecodeError:
        status = None
    if not isinstance(status, dict):
        raise ValueError("STATUS must be a JSON object such as " + json.dumps(DEFAULT_STATUS))
    return status


def node_red_command(state: Path) -> list[str]:
    return ["node", str(SERVER / "node_modules" / "node-red" / "red.js"), "--userDir", str(state), "--settings", str(state / "settings.js"), "flows.json"]


class NodeRedBackend:
    """busy-screen's backend for one session: the flow in a local Node-RED, the board's own, or an address given."""

    def __init__(self, address: Address, status: dict, state: Path = STATE):
        self.address, self.status, self.state = address, status, state
        self.managed = address.kind == FAKE
        self.mac_port = address.port if address.kind == FAKE or (address.kind == EXTERNAL and address.host in LOOPBACK) else None

    def start(self) -> None:
        """Start Node-RED on the Mac port with the repository's flow, wait for it, and put the status.

        Raise RuntimeError when the port is taken, node or the vendored Node-RED is missing or Node-RED ends early,
        and TimeoutError when it does not answer within two minutes.
        """
        port = self.address.port
        if process.answers("127.0.0.1", port):
            raise RuntimeError(f"port {port} is taken; to use the backend there, run with BACKEND=localhost:{port}")
        if not shutil.which("node"):
            raise RuntimeError("node is not installed; brew install node")
        if not (SERVER / "node_modules" / "node-red").exists():
            raise RuntimeError("Node-RED is not vendored; run make npm")
        self.stop()
        shutil.rmtree(self.state, ignore_errors=True)
        self.state.mkdir(parents=True)
        shutil.copy(FLOW, self.state / "flows.json")
        (self.state / "settings.js").write_text(SETTINGS.format(server_settings=json.dumps(str(SERVER / "settings.js"))))
        log = self.state / "node-red.log"
        with log.open("w") as out:
            node_red = subprocess.Popen(node_red_command(self.state), cwd=self.state, env={**os.environ, "PORT": str(port)}, stdout=out, stderr=subprocess.STDOUT, start_new_session=True)
        (self.state / "pid").write_text(json.dumps(process.entry(node_red.pid)))
        deadline = time.monotonic() + 120
        while not process.answers("127.0.0.1", port):
            if node_red.poll() is not None:
                raise RuntimeError(f"Node-RED exited with status {node_red.returncode}; see {log}")
            if time.monotonic() >= deadline:
                raise TimeoutError(f"Node-RED did not answer on {port} within 120 s; see {log}")
            time.sleep(0.5)
        put_status(port, self.status)

    def stop(self) -> None:
        """End the Node-RED a session left running, found by the pid file and its start time; nothing when there is none."""
        pid_file = self.state / "pid"
        try:
            entry = json.loads(pid_file.read_text())
        except (OSError, ValueError):
            entry = None
        pid_file.unlink(missing_ok=True)
        if not process.alive(entry):
            return
        for signum in (signal.SIGINT, signal.SIGKILL):
            try:
                os.killpg(entry[0], signum)
                process.wait_until_gone([entry[0]], timeout=10)
                return
            except ProcessLookupError:
                return
            except TimeoutError:
                continue

    def describe(self) -> str:
        return self.address.describe()

    def address_for(self, served) -> str:
        """Return the backend's http origin as the page of `served` reaches it."""
        if self.mac_port is not None:
            return f"http://{served.address(self.mac_port)}"
        return f"http://{self.address.host}:{self.address.port}"


def put_status(port: int, status: dict, attempts: int = 60) -> None:
    """PUT the status to the flow, retrying while Node-RED still deploys it, and return once /info shows it (the flow sets it after answering)."""
    request = urllib.request.Request(f"http://127.0.0.1:{port}/status", data=json.dumps(status).encode(), method="PUT", headers={"Content-Type": "application/json"})
    for _ in range(attempts):
        try:
            with urllib.request.urlopen(request, timeout=5):
                break
        except (urllib.error.URLError, OSError):
            time.sleep(1)
    else:
        raise RuntimeError(f"the flow on port {port} did not accept the status")
    for _ in range(attempts):
        with urllib.request.urlopen(f"http://127.0.0.1:{port}/info", timeout=5) as response:
            if json.load(response)["status"]:
                return
        time.sleep(0.25)
    raise RuntimeError(f"the flow on port {port} did not show the status it accepted")
