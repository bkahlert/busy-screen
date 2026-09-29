# shellcheck shell=sh
if [ "$1" = "purge" ]; then
  rm -rf /var/lib/busy-screen
  if getent passwd busy-screen >/dev/null; then
    deluser --quiet --system busy-screen >/dev/null 2>&1 || true
  fi
fi
