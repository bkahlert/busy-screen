# shellcheck shell=sh
if ! getent passwd busy-screen >/dev/null; then
  adduser --quiet --system --group --no-create-home --home /var/lib/busy-screen --shell /usr/sbin/nologin busy-screen
fi
