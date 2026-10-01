from pathlib import Path
from types import SimpleNamespace

import pytest
from pihero_testkit.ssh import SshTarget

from booted import tunnel_command, unexpected_recoverable_errors

pytestmark = pytest.mark.tier0


class TestTunnelCommand:
    def test_for_the_vm_uses_its_key_port_and_user_without_connection_sharing(self):
        vm = SimpleNamespace(key=Path("/tmp/vm/pihero-testkit"), port=5022, user="pihero")

        result = tunnel_command(vm, http=18080, backend=18081)

        assert result[:2] == ["ssh", "-N"]
        assert "-L" in result and "127.0.0.1:18080:127.0.0.1:80" in result and "127.0.0.1:18081:127.0.0.1:1880" in result
        assert "-i" in result and "/tmp/vm/pihero-testkit" in result
        assert "-p" in result and "5022" in result
        assert result[-1] == "pihero@127.0.0.1"
        assert "ControlMaster=no" in result and "ControlPath=none" in result

    def test_for_an_ssh_target_uses_the_uri_and_its_port(self):
        result = tunnel_command(SshTarget("pi@busy-screen.local:2222", []), http=18080, backend=18081)

        assert result[-1] == "pi@busy-screen.local"
        assert "-p" in result and "2222" in result
        assert "ControlPath=none" in result

    def test_for_an_ssh_target_without_a_port_passes_none(self):
        result = tunnel_command(SshTarget("pi@busy-screen.local", []), http=18080, backend=18081)

        assert "-p" not in result


class TestUnexpectedRecoverableErrors:
    def test_accepts_the_netplan_warning_of_raspberry_pi_os(self):
        status = {"recoverable_errors": {"WARNING": ["Could not find module named cc_netplan_nm_patch"]}}

        result = unexpected_recoverable_errors(status)

        assert result == []

    def test_reports_any_other_warning(self):
        status = {"recoverable_errors": {"WARNING": ["Could not find module named cc_netplan_nm_patch", "Failed to install packages"]}}

        result = unexpected_recoverable_errors(status)

        assert result == ["Failed to install packages"]

    def test_on_no_recoverable_errors_is_empty(self):
        result = unexpected_recoverable_errors({"status": "done"})

        assert result == []
