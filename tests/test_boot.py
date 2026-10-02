import json
from pathlib import Path

import pytest
from pihero_testkit.ssh import SshTarget

from booted import info_until, journal_until, unexpected_recoverable_errors

pytestmark = pytest.mark.boot


class TestProvisioning:
    def test_cloud_init_finished_without_errors(self, host):
        # cloud-init exits 2 for "degraded done", which the recoverable-errors filter judges; the JSON is read either way.
        status = json.loads(host.run("cloud-init status --long --format json").stdout)

        assert status["errors"] == []
        assert unexpected_recoverable_errors(status) == []
        assert status["status"] == "done", status

    def test_no_unit_failed(self, host):
        failed = host.check_output("systemctl --failed --no-legend --plain").strip()

        assert failed == ""

    def test_the_bootconfig_line_reached_the_kernel(self, host):
        cmdline = host.file("/proc/cmdline").content_string.split()

        assert "cgroup_enable=memory" in cmdline

    def test_posted_the_setup_status(self, host, request):
        if request.config.getoption("--target") == "ssh":
            pytest.skip("a board's status is whatever was posted last")

        answer = info_until(host, "setting up")

        assert answer, "Node-RED did not answer"
        info = json.loads(answer)
        assert info.get("status"), f"Node-RED reports no status: {info}"
        assert info["status"]["name"] == "setting up"
        assert info["status"]["task"] == "busy-screen on Pi Hero 2"
        assert info["status"]["duration"] == 600_000


class TestKiosk:
    def test_is_skipped_by_its_condition_without_a_display_adapter(self, host):
        if host.file("/dev/dri").exists:
            pytest.skip("has a display adapter")

        states = host.check_output("systemctl show --property=ActiveState --property=ConditionResult --value pihero-kiosk.service").split()

        assert states == ["inactive", "no"]

    def test_runs_without_a_restart_once_the_page_has_loaded(self, host, target):
        if isinstance(target, SshTarget):
            pytest.skip("a board's restart count spans its uptime")
        if target.display is None:
            pytest.skip("no virtual display")

        log = journal_until(host, "pihero-kiosk", "Loaded successfully")

        assert "Loaded successfully" in log, log
        assert host.service("pihero-kiosk").is_running
        assert host.check_output("systemctl show -p NRestarts --value pihero-kiosk.service").strip() == "0"

    def test_is_pictured_at_the_displays_size(self, host, target):
        if isinstance(target, SshTarget):
            pytest.skip("no screendump of a board")
        if target.display is None:
            pytest.skip("no virtual display")
        journal_until(host, "pihero-kiosk", "Loaded successfully")

        picture = target.screenshot(Path.cwd() / "dist" / "tier2" / "kiosk.png")

        assert png_size(picture) == target.display


class TestServer:
    def test_reports_its_memory_once_node_red_answers(self, host, request, capfd):
        info = info_until(host, "hostname")
        assert "hostname" in info

        show = host.check_output("systemctl show -p MemoryCurrent -p MemoryPeak busy-screen-server.service").splitlines()

        reporter = request.config.pluginmanager.get_plugin("terminalreporter")
        with capfd.disabled():
            reporter.ensure_newline()
            reporter.write_line(f"busy-screen-server once Node-RED answers: {' '.join(show)}")
        assert "MemoryCurrent=[not set]" not in show
        assert any(line.startswith("MemoryCurrent=") for line in show), show


def png_size(path: Path) -> tuple[int, int]:
    header = path.read_bytes()[:24]
    assert header[:8] == b"\x89PNG\r\n\x1a\n"
    return int.from_bytes(header[16:20], "big"), int.from_bytes(header[20:24], "big")
