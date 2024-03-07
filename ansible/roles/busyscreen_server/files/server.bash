#!/usr/bin/env bash

SCRIPT_DIR="$(cd "$(dirname "$(readlink -f "${BASH_SOURCE[0]}" || true)")" >/dev/null 2>&1 && pwd)"

# shellcheck source=./../../busyscreen/files/lib/lib.bash
. "$SCRIPT_DIR/lib/lib.bash"

declare server_services=(busy-screen-server)

+diag() {
    check_start "Server diagnostics"

    for service in "${server_services[@]}"; do
        check_unit "$service"
        case "$service" in
        nodered)
            check "Node is installed" sh -c 'command -v node >/dev/null 2>&1'
            check "Node is operational" sh -c 'node -version >/dev/null 2>&1'
            ;;
        esac
        check "service is running" systemctl -q is-active "$service.service"
    done

    # shellcheck disable=SC2016
    {
        for service in "${server_services[@]}"; do
            check_further_unit '%s service' "$service"
            check_further '- check status:\n  `%s`' "systemctl status $service.service"
            check_further '- check logs:\n  `%s`' "journalctl -b -e -u $service.service"
            check_further '- stop service:\n  `%s`' "sudo systemctl stop $service.service"
            check_further '- start service interactively:\n  `%s`' "$(service_start_cmdline "$service.service")"
        done
    }
    check_summary
}

+start() {
    for service in "${server_services[@]}"; do
        sudo systemctl start "$service.service"
    done
}

+restart() {
    sudo systemctl daemon-reload
    for service in "${server_services[@]}"; do
        sudo systemctl restart "$service.service"
    done
}

+stop() {
    for service in "${server_services[@]}"; do
        sudo systemctl stop "$service.service"
    done
}

+debug() {
    +stop
    local cmdline=(
        java
        -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5005
        -Xdebug
        -Dws.host="localhost"
        -Dws.port="1880"
        -jar "/opt/busy-screen/nodered/"*.jar
        "$@"
    )
    printf "Starting the scanner in debug mode using the following command line (including the provided arguments):\n%s\n" "${cmdline[*]}"
    sudo "${cmdline[@]}"
}
