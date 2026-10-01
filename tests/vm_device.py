"""The device directory tier 2 boots: the sample with the testkit's user and the local apt repository, and without the panel."""
import re
from importlib.resources import files
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SAMPLE = ROOT / "devices" / "sample" / "user-data"
OUT = ROOT / "dist" / "vm-device"
PUBLIC_KEY = Path(str(files("pihero_testkit") / "keys" / "pihero-testkit.pub"))
USER = "pihero"
REPO_URL = "http://10.0.2.2:8000/"
BUSY_SCREEN_SOURCES = "  - path: /etc/apt/sources.list.d/busy-screen.sources"
BUSY_SCREEN_SOURCE = BUSY_SCREEN_SOURCES + """
    content: |
      Types: deb
      URIs: {url}
      Suites: ./
      Trusted: yes
"""
KIOSK_CONF = "  - path: /etc/pihero/kiosk.conf"
PANEL_CONF = "  - path: /etc/systemd/system/pihero-kiosk.service.d/panel.conf"
# The VM has neither the SPI panel nor HDMI: config.txt has no effect there, and the two console lines are the panel's.
PANEL_RUNCMD = (
    "  - /usr/lib/pihero/bootconfig set config dtoverlay piscreen,drm,rotate=180 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig set config gpu_mem 16 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig add cmdline fbcon=map:1 --package busy-screen-display",
    "  - /usr/lib/pihero/bootconfig add cmdline video=HDMI-A-1:d --package busy-screen-display",
)


def render(sample: str, key: str, user: str = USER, url: str = REPO_URL) -> str:
    text = with_user(sample, user, key)
    text = text.replace(block(text, BUSY_SCREEN_SOURCES), BUSY_SCREEN_SOURCE.format(url=url), 1)
    kiosk_conf = block(text, KIOSK_CONF)
    text = text.replace(kiosk_conf, without_cog_args(kiosk_conf), 1)
    text = text.replace(block(text, PANEL_CONF), "", 1)
    for line in PANEL_RUNCMD:
        text = text.replace(block(text, line), "", 1)
    return text


def with_user(text: str, user: str, key: str) -> str:
    users = block(text, "users:")
    if users.count("  - name: ") != 1:
        raise ValueError("expected one user in the sample device file")
    renamed = re.sub(r"^(?P<prefix>  - name: ).*$", lambda m: m["prefix"] + user, users, count=1, flags=re.M)
    rekeyed, keys = re.subn(r"^(?P<prefix>    ssh_authorized_keys:\n      - ).*$", lambda m: m["prefix"] + key, renamed, count=1, flags=re.M)
    if keys == 0:
        raise ValueError("expected an ssh_authorized_keys entry in the sample device file")
    return text.replace(users, rekeyed, 1)


def without_cog_args(kiosk_conf: str) -> str:
    stripped, lines = re.subn(r"^      COG_ARGS=.*\n", "", kiosk_conf, flags=re.M)
    if lines == 0:
        raise ValueError("expected a COG_ARGS line in the sample's kiosk.conf")
    return stripped


def block(text: str, start: str) -> str:
    """Return the line equal to `start` and every following line indented deeper than it."""
    lines = text.splitlines(keepends=True)
    try:
        begin = next(i for i, line in enumerate(lines) if line.rstrip("\n") == start)
    except StopIteration:
        raise ValueError(f"the device file has no line {start!r}") from None
    indent = len(start) - len(start.lstrip(" "))
    end = begin + 1
    while end < len(lines) and (not lines[end].strip() or len(lines[end]) - len(lines[end].lstrip(" ")) > indent):
        end += 1
    return "".join(lines[begin:end])


def write(out: Path = OUT, sample: Path = SAMPLE) -> Path:
    out.mkdir(parents=True, exist_ok=True)
    (out / "user-data").write_text(render(sample.read_text(), key=PUBLIC_KEY.read_text().strip()))
    return out


if __name__ == "__main__":
    print(write())
