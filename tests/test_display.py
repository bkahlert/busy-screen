import json
import re
from pathlib import Path

import pytest
from playwright.sync_api import Error as PlaywrightError
from playwright.sync_api import TimeoutError as PlaywrightTimeoutError
from playwright.sync_api import expect, sync_playwright

from booted import Tunnel, info_until

pytestmark = pytest.mark.boot
PANEL = {"width": 480, "height": 320}
READY = re.compile(r"\bready\b")
FONT_REQUESTS = re.compile(r"/fonts/")
FONT = "1em 'Press Start 2P'"


class TestDisplay:
    def test_shows_the_status_the_backend_reports(self, host, page, tunnel, screenshot):
        answer = info_until(host, "hostname")
        assert answer, "Node-RED did not answer"
        info = json.loads(answer)
        assert info.get("status"), f"Node-RED reports no status: {info}"

        page.goto(f"http://127.0.0.1:{tunnel.http}/?address=http://127.0.0.1:{tunnel.backend}")
        expect(page.locator(".status__name .nes-text")).to_have_text(info["status"]["name"], timeout=60_000)
        expect(page).to_have_title(info["status"]["name"])
        expect(page.locator(".status")).to_have_css("opacity", "1")
        page.screenshot(path=str(screenshot))

        assert screenshot.stat().st_size > 0

    def test_is_shown_only_once_its_font_has_loaded(self, page, tunnel):
        held = []
        page.route(FONT_REQUESTS, lambda route: held.append(route))
        url = f"http://127.0.0.1:{tunnel.http}/?address=http://127.0.0.1:{tunnel.backend}/&refresh-rate=PT1S"
        with page.expect_request(FONT_REQUESTS, timeout=60_000):
            page.goto(url, wait_until="commit")
        assert page.url == url

        with pytest.raises(PlaywrightTimeoutError):
            page.wait_for_function("document.documentElement.classList.contains('ready')", timeout=5_000)
        for route in held:
            route.continue_()
        page.unroute(FONT_REQUESTS)

        expect(page.locator("html")).to_have_class(READY)
        assert page.evaluate(f"document.fonts.check({json.dumps(FONT)})")


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
