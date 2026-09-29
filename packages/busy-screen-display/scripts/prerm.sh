# shellcheck shell=sh
lighty-disable-mod busy-screen >/dev/null 2>&1 || true
if [ -d /run/systemd/system ]; then
  deb-systemd-invoke try-restart lighttpd.service >/dev/null || true
fi
