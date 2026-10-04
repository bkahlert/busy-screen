"""make preview-browser, preview-vm and preview-board: the page from the dev server in a browser, a VM's kiosk or a board's kiosk."""
import sys
from pathlib import Path

from pihero_testkit import device_file
from pihero_testkit.preview import DevServer, Served, Settings, main

import preview_backend
import vm_device

ROOT = Path(__file__).resolve().parents[1]
DEV_PORT = 8082
PACKAGES = ("busy-screen-server", "busy-screen-display")
KIOSK_PACKAGE = "pihero-kiosk"


def render(sample: str, key: str) -> str:
    """Return the sample for the preview's VM: the tier-2 rendering minus busy-screen's source, packages and boot-config lines, with the kiosk named."""
    text = device_file.drop(vm_device.render(sample, key), vm_device.BUSY_SCREEN_SOURCES)
    for package in PACKAGES:
        text = text.replace(f"  - {package}\n", f"  - {KIOSK_PACKAGE}\n" if package == "busy-screen-display" else "")
    return "".join(line for line in text.splitlines(keepends=True) if "--package busy-screen-" not in line)


class BusyScreen:
    name = "busy-screen"
    root = ROOT
    display = (480, 320)

    def user_data(self) -> str:
        return render(vm_device.SAMPLE.read_text(), device_file.PUBLIC_KEY.read_text().strip())

    def dev_server(self, settings: Settings) -> DevServer:
        return DevServer(["./gradlew", "--console=plain", "jsBrowserDevelopmentRun", "--continuous"], DEV_PORT)

    def backend(self, settings: Settings) -> preview_backend.NodeRedBackend:
        address = preview_backend.parse_backend(settings.environ.get("BACKEND"))
        if address.kind == preview_backend.BOARD and settings.flavor != "board":
            raise ValueError("BACKEND=board is only for preview-board")
        return preview_backend.NodeRedBackend(address, preview_backend.parse_status(settings.environ.get("STATUS")))

    def page_url(self, backend: preview_backend.NodeRedBackend, served: Served) -> str:
        return f"http://{served.address(DEV_PORT)}/?address={backend.address_for(served)}"


if __name__ == "__main__":
    sys.exit(main(BusyScreen()))
