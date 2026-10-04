import pytest
from pihero_testkit.preview import Settings
from pihero_testkit.preview.flavors import BoardServed, BrowserServed, VmServed

import preview
import preview_backend
import vm_device

pytestmark = pytest.mark.tier0
SAMPLE = vm_device.SAMPLE.read_text()
KEY = "ssh-ed25519 AAAATEST pihero-testkit"


def settings(flavor: str, **environ: str) -> Settings:
    return Settings.from_environ(flavor, {**({"TARGET": "pi@busy-screen.local"} if flavor == "board" else {}), **environ})


def without_comments(text: str) -> str:
    return "".join(line for line in text.splitlines(keepends=True) if not line.lstrip().startswith("#"))


class TestRender:
    def test_leaves_out_busy_screens_packages(self):
        text = preview.render(SAMPLE, KEY)

        assert "  - pihero\n" in text
        assert "busy-screen-server" not in text
        assert "busy-screen-display" not in text

    def test_installs_the_kiosk_from_pi_heros_own_source(self):
        text = preview.render(SAMPLE, KEY)

        assert "  - pihero-kiosk\n" in text
        assert "bkahlert.github.io/pihero/apt" in text

    def test_leaves_out_busy_screens_apt_source(self):
        text = without_comments(preview.render(SAMPLE, KEY))

        assert "busy-screen.sources" not in text
        assert "bkahlert.github.io/busy-screen" not in text
        assert "10.0.2.2:8000" not in text

    def test_leaves_out_the_boot_config_lines_of_busy_screens_packages(self):
        text = without_comments(preview.render(SAMPLE, KEY))

        assert "--package busy-screen-" not in text
        assert "bootconfig" not in text

    def test_keeps_the_kiosk_url_without_cog_args_and_the_testkit_user(self):
        text = preview.render(SAMPLE, KEY)

        assert "  - path: /etc/pihero/kiosk.conf\n    content: |\n      URL=http://localhost/\n" in text
        assert "COG_ARGS" not in text
        assert "  - name: pihero\n" in text
        assert f"      - {KEY}\n" in text

    def test_changes_nothing_but_what_it_leaves_out(self):
        rendered = vm_device.render(SAMPLE, KEY).splitlines()

        kept = preview.render(SAMPLE, KEY).splitlines()

        assert [line for line in kept if line not in rendered] == ["  - pihero-kiosk"]


class TestApp:
    def test_is_named_for_the_record_and_the_boards_run_directory(self):
        assert preview.BusyScreen.name == "busy-screen"
        assert preview.BusyScreen.root == preview.ROOT
        assert preview.BusyScreen.display == (480, 320)

    def test_gives_the_vm_the_rendered_sample(self):
        text = preview.BusyScreen().user_data()

        assert "  - pihero-kiosk\n" in text and "busy-screen.sources" not in text


class TestDevServer:
    @pytest.mark.parametrize("flavor", ["browser", "vm", "board"])
    def test_runs_gradles_continuous_development_server_on_8082(self, flavor):
        server = preview.BusyScreen().dev_server(settings(flavor))

        assert server.argv == ["./gradlew", "--console=plain", "jsBrowserDevelopmentRun", "--continuous"]
        assert server.port == 8082
        assert server.env == {}


class TestBackend:
    def test_is_the_fake_with_a_default_status(self):
        backend = preview.BusyScreen().backend(settings("vm"))

        assert backend.managed and backend.describe() == "fake on localhost:1880"
        assert backend.status == preview_backend.DEFAULT_STATUS

    def test_takes_the_address_and_the_status_from_the_environment(self):
        backend = preview.BusyScreen().backend(settings("vm", BACKEND="busy-screen.local:1880", STATUS='{"name": "Ada"}'))

        assert not backend.managed and backend.describe() == "busy-screen.local:1880"
        assert backend.status == {"name": "Ada"}

    def test_offers_the_boards_own_node_red_on_the_board(self):
        assert preview.BusyScreen().backend(settings("board", BACKEND="board")).describe() == "the board's own, 127.0.0.1:1880 on the board"

    @pytest.mark.parametrize("flavor", ["browser", "vm"])
    def test_refuses_the_boards_own_node_red_elsewhere(self, flavor):
        with pytest.raises(ValueError, match="BACKEND=board is only for preview-board"):
            preview.BusyScreen().backend(settings(flavor, BACKEND="board"))

    def test_names_a_malformed_variable(self):
        with pytest.raises(ValueError, match="BACKEND must be fake, board or HOST:PORT"):
            preview.BusyScreen().backend(settings("vm", BACKEND="nope"))
        with pytest.raises(ValueError, match="STATUS must be a JSON object"):
            preview.BusyScreen().backend(settings("vm", STATUS="nope"))


class TestPageUrl:
    @pytest.mark.parametrize("flavor, served, expected", [
        ("browser", BrowserServed(), "http://localhost:8082/?address=http://localhost:1880"),
        ("vm", VmServed(), "http://10.0.2.2:8082/?address=http://10.0.2.2:1880"),
        ("board", BoardServed(), "http://127.0.0.1:18082/?address=http://127.0.0.1:11880"),
    ])
    def test_reaches_the_dev_server_and_the_fake_on_the_mac_as_the_flavor_does(self, flavor, served, expected):
        app = preview.BusyScreen()

        assert app.page_url(app.backend(settings(flavor)), served) == expected

    def test_reaches_a_backend_on_the_mac_by_its_own_port(self):
        app = preview.BusyScreen()

        url = app.page_url(app.backend(settings("vm", BACKEND="localhost:9000")), VmServed())

        assert url == "http://10.0.2.2:8082/?address=http://10.0.2.2:9000"

    def test_reaches_a_remote_backend_where_it_is(self):
        app = preview.BusyScreen()

        url = app.page_url(app.backend(settings("vm", BACKEND="busy-screen.local:1880")), VmServed())

        assert url == "http://10.0.2.2:8082/?address=http://busy-screen.local:1880"

    def test_reaches_the_boards_own_node_red_on_its_loopback(self):
        app = preview.BusyScreen()

        url = app.page_url(app.backend(settings("board", BACKEND="board")), BoardServed())

        assert url == "http://127.0.0.1:18082/?address=http://127.0.0.1:1880"
