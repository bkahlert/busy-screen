# shellcheck shell=sh
lighty-enable-mod busy-screen >/dev/null 2>&1 || true
if [ -d /run/systemd/system ]; then
  deb-systemd-invoke try-restart lighttpd.service >/dev/null || true
  # The kiosk keeps the bundle it loaded; a running kiosk is restarted so it picks up the new one.
  deb-systemd-invoke try-restart pihero-kiosk.service >/dev/null || true
fi
