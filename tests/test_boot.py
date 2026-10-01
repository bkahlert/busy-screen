import json

import pytest

from booted import info_until, unexpected_recoverable_errors

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

        info = json.loads(info_until(host, "setting up"))

        assert info["status"]["name"] == "setting up"
        assert info["status"]["task"] == "busy-screen on Pi Hero 2"
        assert info["status"]["duration"] == 600_000


class TestKiosk:
    def test_is_skipped_by_its_condition_without_a_display_adapter(self, host):
        if host.file("/dev/dri").exists:
            pytest.skip("has a display adapter")

        states = host.check_output("systemctl show --property=ActiveState --property=ConditionResult --value pihero-kiosk.service").split()

        assert states == ["inactive", "no"]


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
