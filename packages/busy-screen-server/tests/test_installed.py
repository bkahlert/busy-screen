import json
import time

import pytest

pytestmark = pytest.mark.installed
FETCH = "python3 -c 'import urllib.request,sys; print(urllib.request.urlopen(sys.argv[1], timeout=5).read().decode())' "
PUT = (
    "python3 -c 'import urllib.request,sys,json; "
    "r = urllib.request.Request(sys.argv[1], data=sys.argv[2].encode(), method=\"PUT\", headers={\"Content-Type\": \"application/json\"}); "
    "print(urllib.request.urlopen(r, timeout=10).status)' "
)


class TestPackage:
    def test_is_installed_at_the_built_version(self, host, version):
        package = host.package("busy-screen-server")

        assert package.is_installed
        assert package.version == version

    def test_pulls_in_node(self, host):
        assert host.package("nodejs").is_installed
        assert host.file("/usr/bin/node").exists

    def test_vendors_node_red_and_the_flows_nodes(self, host):
        assert host.file("/usr/lib/busy-screen/server/node_modules/node-red/red.js").is_file
        assert host.file("/usr/lib/busy-screen/server/node_modules/node-red-contrib-ip/package.json").is_file
        assert host.file("/usr/lib/busy-screen/server/node_modules/moment/package.json").is_file
        assert host.file("/usr/share/busy-screen/flows.json").is_file


class TestUser:
    def test_busy_screen_is_a_system_user_without_a_login(self, host):
        user = host.user("busy-screen")

        assert user.exists
        assert user.uid < 1000
        assert user.shell in ("/usr/sbin/nologin", "/bin/false")


class TestUnit:
    def test_is_enabled_and_running(self, host):
        service = host.service("busy-screen-server")

        assert service.is_enabled
        assert service.is_running

    def test_runs_as_the_system_user_with_the_memory_cap(self, host):
        show = host.check_output("systemctl show -p User -p MemoryMax -p KillSignal busy-screen-server.service")

        assert "User=busy-screen" in show
        assert "MemoryMax=234881024" in show
        assert "KillSignal=2" in show

    def test_copies_the_shipped_flow_into_the_state_directory(self, host):
        wait_for_port(host, 1880)

        flow = host.file("/var/lib/busy-screen/flows.json")

        assert flow.is_file
        assert flow.user == "busy-screen"


class TestApi:
    def test_listens_on_1880(self, host):
        assert wait_for_port(host, 1880)

    def test_takes_a_status_and_reports_it(self, host):
        wait_for_port(host, 1880)

        status = host.check_output(PUT + "http://localhost:1880/status '" + json.dumps({"name": "installed test", "task": "T1", "duration": "PT2M"}) + "'")
        info = json.loads(fetch_until(host, "http://localhost:1880/info", "installed test"))

        assert status.strip() == "200"
        assert info["status"]["name"] == "installed test"
        assert info["status"]["duration"] == 120_000
        assert info["hostname"]
        assert info["username"] == "busy-screen"

    def test_answers_cross_origin_requests(self, host):
        wait_for_port(host, 1880)

        headers = host.check_output(
            "python3 -c 'import urllib.request; r = urllib.request.Request(\"http://localhost:1880/info\", headers={\"Origin\": \"http://localhost\"}); "
            "print(urllib.request.urlopen(r, timeout=5).headers.get(\"Access-Control-Allow-Origin\"))'"
        )

        assert headers.strip() == "*"

    def test_serves_the_editor(self, host):
        wait_for_port(host, 1880)

        page = host.check_output(FETCH + "http://localhost:1880/")

        assert "Node-RED" in page


class TestRemoval:
    @pytest.mark.mutating
    def test_purge_removes_the_state_and_the_user(self, host, target):
        target.purge(["busy-screen-server"])

        assert not host.file("/var/lib/busy-screen").exists
        assert not host.file("/usr/lib/busy-screen/server").exists
        assert not host.user("busy-screen").exists

        target.reinstall()


def wait_for_port(host, port: int, attempts: int = 120) -> bool:
    """Node-RED takes a while to start on a small board or under emulation."""
    for _ in range(attempts):
        if f":{port} " in host.run("ss -Hltn").stdout:
            return True
        time.sleep(2)
    return False


def fetch_until(host, url: str, needle: str, attempts: int = 15) -> str:
    out = ""
    for _ in range(attempts):
        out = host.run(FETCH + url).stdout
        if needle in out:
            return out
        time.sleep(1)
    return out
