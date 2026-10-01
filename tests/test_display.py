import json
import urllib.request
from pathlib import Path

import pytest
from playwright.sync_api import Error as PlaywrightError
from playwright.sync_api import expect, sync_playwright

from booted import Tunnel

pytestmark = pytest.mark.boot
PANEL = {"width": 480, "height": 320}


class TestDisplay:
    def test_shows_the_status_the_backend_reports(self, page, tunnel, screenshot):
        info = json.loads(urllib.request.urlopen(f"http://127.0.0.1:{tunnel.backend}/info", timeout=30).read())
        assert info["status"], info

        page.goto(f"http://127.0.0.1:{tunnel.http}/?address=http://127.0.0.1:{tunnel.backend}")
        expect(page.locator(".status__name .nes-text")).to_have_text(info["status"]["name"], timeout=60_000)
        expect(page).to_have_title(info["status"]["name"])
        expect(page.locator(".status")).to_have_css("opacity", "1")
        page.screenshot(path=str(screenshot))

        assert screenshot.stat().st_size > 0


@pytest.fixture(scope="module")
def tunnel(target):
    tunnel = Tunnel(target)
    yield tunnel
    tunnel.close()


@pytest.fixture(scope="module")
def page():
    with sync_playwright() as playwright:
        try:
            browser = playwright.webkit.launch()
        except PlaywrightError as e:
            pytest.fail(f"Playwright's WebKit is not installed; run `make browser`\n{e}")
        page = browser.new_page(viewport=PANEL)
        yield page
        browser.close()


@pytest.fixture(scope="module")
def screenshot(request):
    target = request.config.getoption("--target")
    path = Path.cwd() / "dist" / ("tier2" if target == "vm" else target) / "display.png"
    path.parent.mkdir(parents=True, exist_ok=True)
    return path
