import subprocess
import sys
from pathlib import Path

import pytest

import vm_device

pytestmark = pytest.mark.tier0
ROOT = Path(__file__).resolve().parents[1]
SAMPLE = (ROOT / "devices" / "sample" / "user-data").read_text()
KEY = "ssh-ed25519 AAAATEST pihero-testkit"


class TestRender:
    def test_renames_the_user_and_sets_the_testkit_key(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert "  - name: pihero\n" in result
        assert "  - name: pi\n" not in result
        assert f"    ssh_authorized_keys:\n      - {KEY}\n" in result

    def test_points_the_busy_screen_source_at_the_local_repository(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert "      URIs: http://10.0.2.2:8000/\n      Suites: ./\n      Trusted: yes\n" in result
        assert "https://bkahlert.github.io/busy-screen/apt" not in result
        assert "https://bkahlert.github.io/pihero/apt" in result

    def test_drops_cog_args_and_keeps_the_kiosk_url(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert "COG_ARGS" not in result
        assert "  - path: /etc/pihero/kiosk.conf\n    content: |\n      URL=http://localhost/\n" in result

    def test_drops_the_panel_drop_in(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert "panel.conf" not in result
        assert "DeviceAllow" not in result

    def test_drops_the_panel_lines_and_keeps_the_memory_controller_line(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert "bootconfig set config" not in result
        assert "fbcon=map:1" not in result
        assert "video=HDMI-A-1:d" not in result
        assert "  - /usr/lib/pihero/bootconfig add cmdline cgroup_enable=memory --package busy-screen-server\n" in result

    def test_changes_nothing_else(self):
        result = vm_device.render(SAMPLE, key=KEY)

        assert without_edited_blocks(result) == without_edited_blocks(SAMPLE)

    def test_on_a_file_without_a_users_block_raises(self):
        with pytest.raises(ValueError, match="users:"):
            vm_device.render("#cloud-config\nhostname: x\n", key=KEY)

    def test_on_two_users_raises(self):
        two = SAMPLE.replace("rpi:\n", "  - name: second\n    ssh_authorized_keys:\n      - ssh-ed25519 BBBB second\nrpi:\n")

        with pytest.raises(ValueError, match="one user"):
            vm_device.render(two, key=KEY)

    def test_on_a_users_block_without_a_key_raises(self):
        key_line = "      - ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIExampleExampleExampleExampleExampleExampleEx you@example.com\n"
        keyless = SAMPLE.replace("    ssh_authorized_keys:\n" + key_line, "")

        with pytest.raises(ValueError, match="ssh_authorized_keys"):
            vm_device.render(keyless, key=KEY)

    def test_on_a_users_block_without_a_name_line_raises(self):
        indented = SAMPLE.replace("  - name: pi\n", "    - name: pi\n")

        with pytest.raises(ValueError, match="name"):
            vm_device.render(indented, key=KEY)

    def test_on_a_kiosk_conf_without_cog_args_raises(self):
        without = SAMPLE.replace("      COG_ARGS=--platform-params=renderer=gles\n", "")

        with pytest.raises(ValueError, match="COG_ARGS"):
            vm_device.render(without, key=KEY)

    def test_on_a_missing_panel_line_raises(self):
        without = SAMPLE.replace("  - /usr/lib/pihero/bootconfig set config gpu_mem 16 --package busy-screen-display\n", "")

        with pytest.raises(ValueError, match="gpu_mem"):
            vm_device.render(without, key=KEY)


class TestWrite:
    def test_writes_only_user_data_with_the_testkit_key(self, tmp_path):
        out = vm_device.write(tmp_path / "vm-device")

        assert [p.name for p in out.iterdir()] == ["user-data"]
        text = (out / "user-data").read_text()
        assert text.startswith("#cloud-config\n")
        assert vm_device.PUBLIC_KEY.read_text().strip() in text


class TestPytestConfigure:
    def test_on_the_vm_target_without_a_device_generates_it(self):
        (vm_device.OUT / "user-data").unlink(missing_ok=True)

        collect_only("--target=vm")

        assert (vm_device.OUT / "user-data").exists()

    def test_on_an_explicit_device_leaves_it_alone(self):
        (vm_device.OUT / "user-data").unlink(missing_ok=True)

        collect_only("--target=vm", "--device=devices/sample")

        assert not (vm_device.OUT / "user-data").exists()

    @pytest.fixture(autouse=True)
    def restored_device_file(self):
        yield
        vm_device.write()


EDITED = ("users:", vm_device.BUSY_SCREEN_SOURCES, vm_device.KIOSK_CONF, vm_device.PANEL_CONF, *vm_device.PANEL_RUNCMD)


def without_edited_blocks(text: str) -> str:
    for start in EDITED:
        if start in text.splitlines():
            text = text.replace(vm_device.block(text, start), "", 1)
    return text


def collect_only(*options: str) -> None:
    subprocess.run(
        [sys.executable, "-m", "pytest", "--collect-only", "-q", "-p", "no:cacheprovider", *options, "tests/test_vm_device.py"],
        cwd=ROOT, check=True, capture_output=True, text=True,
    )
