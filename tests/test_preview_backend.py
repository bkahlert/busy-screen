import json
import shutil
import signal
import subprocess
import urllib.request

import pytest
from pihero_testkit.preview import process
from pihero_testkit.preview.flavors import BoardServed, BrowserServed, VmServed

import preview_backend
from preview_backend import BOARD, DEFAULT_STATUS, EXTERNAL, FAKE, Address, NodeRedBackend


@pytest.mark.tier0
class TestParseBackend:
    @pytest.mark.parametrize("text", [None, "", "fake"])
    def test_is_the_fake_on_the_macs_1880_by_default(self, text):
        assert preview_backend.parse_backend(text) == Address(FAKE, "localhost", 1880)

    def test_is_the_boards_own_node_red_on_its_loopback_for_board(self):
        assert preview_backend.parse_backend("board") == Address(BOARD, "127.0.0.1", 1880)

    @pytest.mark.parametrize("text, host, port", [("localhost:1880", "localhost", 1880), ("busy-screen.local:1880", "busy-screen.local", 1880), ("127.0.0.1:9000", "127.0.0.1", 9000)])
    def test_uses_a_host_and_port_as_it_is(self, text, host, port):
        assert preview_backend.parse_backend(text) == Address(EXTERNAL, host, port)

    @pytest.mark.parametrize("text", ["busy-screen.local", ":1880", "host:", "host:abc", "host:0", "host:70000", "host:-1", "Fake", "device"])
    def test_rejects_anything_but_the_three_forms(self, text):
        with pytest.raises(ValueError, match="BACKEND must be fake, board or HOST:PORT"):
            preview_backend.parse_backend(text)


@pytest.mark.tier0
class TestAddress:
    @pytest.mark.parametrize("text, described", [
        ("fake", "fake on localhost:1880"),
        ("board", "the board's own, 127.0.0.1:1880 on the board"),
        ("busy-screen.local:1880", "busy-screen.local:1880"),
    ])
    def test_describes_itself_for_the_ready_message(self, text, described):
        assert preview_backend.parse_backend(text).describe() == described


@pytest.mark.tier0
class TestParseStatus:
    @pytest.mark.parametrize("text", [None, ""])
    def test_is_the_default_without_a_value(self, text):
        assert preview_backend.parse_status(text) == DEFAULT_STATUS

    def test_reads_a_json_object(self):
        assert preview_backend.parse_status('{"name": "Ada", "task": "review", "duration": "PT5M"}') == {"name": "Ada", "task": "review", "duration": "PT5M"}

    @pytest.mark.parametrize("text", ["[]", '"busy"', "3", "null"])
    def test_names_the_expected_shape_for_json_that_is_no_object(self, text):
        with pytest.raises(ValueError, match="STATUS must be a JSON object"):
            preview_backend.parse_status(text)

    def test_names_the_variable_for_text_that_is_no_json(self):
        with pytest.raises(ValueError, match="STATUS must be a JSON object"):
            preview_backend.parse_status("{nope")


@pytest.mark.tier0
class TestNodeRedCommand:
    def test_runs_the_vendored_node_red_with_the_flow_and_the_settings_in_the_state_directory(self, tmp_path):
        command = preview_backend.node_red_command(tmp_path)

        assert command[0] == "node"
        assert command[1] == str(preview_backend.SERVER / "node_modules" / "node-red" / "red.js")
        assert command[command.index("--userDir") + 1] == str(tmp_path)
        assert command[command.index("--settings") + 1] == str(tmp_path / "settings.js")
        assert command[-1] == "flows.json"


@pytest.mark.tier0
class TestNodeRedBackend:
    @pytest.mark.parametrize("text, managed, mac_port", [
        ("fake", True, 1880),
        ("board", False, None),
        ("localhost:1880", False, 1880),
        ("127.0.0.1:9000", False, 9000),
        ("busy-screen.local:1880", False, None),
    ])
    def test_knows_what_it_starts_and_which_mac_port_the_kiosk_must_reach(self, text, managed, mac_port):
        backend = NodeRedBackend(preview_backend.parse_backend(text), DEFAULT_STATUS)

        assert (backend.managed, backend.mac_port) == (managed, mac_port)

    @pytest.mark.parametrize("text, served, origin", [
        ("fake", BrowserServed(), "http://localhost:1880"),
        ("fake", VmServed(), "http://10.0.2.2:1880"),
        ("fake", BoardServed(), "http://127.0.0.1:11880"),
        ("localhost:9000", VmServed(), "http://10.0.2.2:9000"),
        ("board", BoardServed(), "http://127.0.0.1:1880"),
        ("busy-screen.local:1880", VmServed(), "http://busy-screen.local:1880"),
    ])
    def test_gives_the_origin_as_the_page_reaches_it(self, text, served, origin):
        backend = NodeRedBackend(preview_backend.parse_backend(text), DEFAULT_STATUS)

        assert backend.address_for(served) == origin

    def test_describes_its_address(self):
        assert NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS).describe() == "fake on localhost:1880"

    def test_stops_without_ever_having_run(self, tmp_path):
        NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path).stop()

    def test_ends_the_node_red_a_session_left_running_and_stops_again_without_harm(self, tmp_path):
        left = subprocess.Popen(["sleep", "60"], start_new_session=True)
        (tmp_path / "pid").write_text(json.dumps(process.entry(left.pid)))
        backend = NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path)

        try:
            backend.stop()
            exit_code = left.wait(timeout=10)
            backend.stop()
        finally:
            left.kill()

        assert exit_code == -signal.SIGINT
        assert not (tmp_path / "pid").exists()

    def test_leaves_a_process_alone_that_is_not_the_one_it_started(self, tmp_path):
        other = subprocess.Popen(["sleep", "60"], start_new_session=True)
        (tmp_path / "pid").write_text(json.dumps([other.pid, "Thu Jan  1 00:00:00 1970"]))
        backend = NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path)

        try:
            backend.stop()
            still_running = other.poll() is None
        finally:
            other.kill()

        assert still_running
        assert not (tmp_path / "pid").exists()

    def test_stops_with_a_pid_file_it_cannot_read(self, tmp_path):
        (tmp_path / "pid").write_text("not json")

        NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path).stop()

        assert not (tmp_path / "pid").exists()

    def test_refuses_a_port_that_already_answers_and_names_the_way_to_use_it(self, tmp_path, monkeypatch):
        monkeypatch.setattr(process, "answers", lambda host, port, timeout=1.0: True)

        with pytest.raises(RuntimeError, match="BACKEND=localhost:1880"):
            NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path).start()

    def test_names_the_missing_node(self, tmp_path, monkeypatch):
        monkeypatch.setattr(process, "answers", lambda host, port, timeout=1.0: False)
        monkeypatch.setattr(preview_backend.shutil, "which", lambda name: None)

        with pytest.raises(RuntimeError, match="brew install node"):
            NodeRedBackend(preview_backend.parse_backend("fake"), DEFAULT_STATUS, tmp_path).start()


@pytest.mark.tier0
class TestMain:
    def test_refuses_a_backend_it_does_not_manage(self, capsys):
        status = preview_backend.main({"BACKEND": "busy-screen.local:1880"})

        assert status == 2
        assert "busy-screen.local:1880" in capsys.readouterr().err

    @pytest.mark.parametrize("environ, message", [({"BACKEND": "nope"}, "BACKEND must be"), ({"STATUS": "nope"}, "STATUS must be")])
    def test_names_a_malformed_variable(self, capsys, environ, message):
        status = preview_backend.main(environ)

        assert status == 2
        assert message in capsys.readouterr().err


@pytest.mark.preview
@pytest.mark.skipif(not (preview_backend.SERVER / "node_modules" / "node-red").exists() or not shutil.which("node"), reason="needs node and make npm")
class TestNodeRedFake:
    def test_serves_the_flow_with_the_status_seeded_and_ends_with_stop(self, tmp_path):
        status = {"name": "preview", "task": "a test", "duration": "PT10M"}
        backend = NodeRedBackend(Address(FAKE, "localhost", process.free_port()), status, tmp_path)
        try:
            backend.start()
            with urllib.request.urlopen(f"http://127.0.0.1:{backend.address.port}/info", timeout=10) as response:
                info = json.load(response)
        finally:
            backend.stop()

        assert info["status"]["name"] == "preview" and info["status"]["task"] == "a test"
        assert not process.answers("127.0.0.1", backend.address.port)
