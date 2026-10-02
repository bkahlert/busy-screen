"""Diagnostics for the kiosk's webfont under software emulation: screendumps over several minutes after a kiosk restart with lighttpd's
access log on, each with the kiosk's journal and the access log of that moment."""
import time
from pathlib import Path

import pytest

pytestmark = pytest.mark.boot

OUT = Path.cwd() / "dist" / "tier2" / "diag"
FONT = "http://localhost/fonts/press-start-2p-v9-latin-ext_latin_greek_cyrillic-ext_cyrillic-regular.woff2"


def test_kiosk_font_over_time(host, target):
    if target.display is None:
        pytest.skip("no virtual display")
    OUT.mkdir(parents=True, exist_ok=True)
    (OUT / "versions.txt").write_text(host.run(f"dpkg -l | grep -E 'cog|wpe|lighttpd|busy-screen'; curl -sI {FONT}; head -12 /proc/cpuinfo; nproc; free -m").stdout)
    host.run("sudo lighty-enable-mod accesslog; sudo systemctl restart lighttpd; sudo systemctl restart pihero-kiosk")
    start = time.monotonic()
    for _ in range(30):
        elapsed = int(time.monotonic() - start)
        target.screenshot(OUT / f"{elapsed:03d}.png")
        journal = host.run("journalctl -u pihero-kiosk -b --no-pager -o short-monotonic | tail -12").stdout
        access = host.run("sudo cat /var/log/lighttpd/access.log").stdout
        (OUT / f"{elapsed:03d}.txt").write_text(f"{host.run('cat /proc/uptime').stdout}\n--- journal\n{journal}\n--- access\n{access}")
        time.sleep(10)
