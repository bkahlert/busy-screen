"""The device directory tier 2 boots: the sample with the testkit's user and the local apt repository, and without the panel."""
import re
from pathlib import Path

from pihero_testkit import device_file

ROOT = Path(__file__).resolve().parents[1]
SAMPLE = ROOT / "devices" / "sample" / "user-data"
OUT = ROOT / "dist" / "vm-device"
SOURCE = "/etc/apt/sources.list.d/busy-screen.sources"
BUSY_SCREEN_SOURCES = f"  - path: {SOURCE}"
KIOSK_CONF = "  - path: /etc/pihero/kiosk.conf"
PANEL_CONF = "  - path: /etc/systemd/system/pihero-kiosk.service.d/panel.conf"
# The VM has neither the SPI panel nor HDMI: config.txt has no effect there, and the two console lines are the panel's.
PANEL_RUNCMD = (
    "  - /usr/lib/pihero/bootconfig set config dtoverlay piscreen,drm,rotate=180 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig set config gpu_mem 16 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig add cmdline fbcon=map:1 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig add cmdline video=HDMI-A-1:d --package busy-screen-display",
)


def render(sample: str, key: str) -> str:
    text = device_file.with_source(device_file.with_user(sample, key), SOURCE)
    kiosk_conf = device_file.block(text, KIOSK_CONF)
    text = text.replace(kiosk_conf, without_cog_args(kiosk_conf), 1)
    for start in (PANEL_CONF, *PANEL_RUNCMD):
        text = device_file.drop(text, start)
    return text


def without_cog_args(kiosk_conf: str) -> str:
    stripped, lines = re.subn(r"^      COG_ARGS=.*\n", "", kiosk_conf, flags=re.M)
    if lines == 0:
        raise ValueError("expected a COG_ARGS line in the sample's kiosk.conf")
    return stripped


def write(out: Path = OUT, sample: Path = SAMPLE) -> Path:
    return device_file.write(out, render(sample.read_text(), device_file.PUBLIC_KEY.read_text().strip()))


if __name__ == "__main__":
    print(write())
