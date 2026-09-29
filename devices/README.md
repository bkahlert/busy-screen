# Device files

`sample/` is a complete Busy Screen device for [Pi Hero 2](https://github.com/bkahlert/pihero): pihero's sample device plus the
busy-screen apt source (its key inline, so the device trusts nothing else), the packages `busy-screen-server` and
`busy-screen-display`, `/etc/pihero/kiosk.conf` with the page the kiosk shows, and the lines for the Waveshare 3.5-inch LCD. Every
other key is explained in pihero's [devices/README.md](https://github.com/bkahlert/pihero/blob/main/devices/README.md). Directories
other than `sample/` are gitignored: keep your own here or in a private repository.

Copy `user-data`, set the hostname, your SSH public key and the pretty name; a board with Wi-Fi also takes pihero's
`network-config` next to it. Then, in a pihero checkout, `make flash DEVICE=<path to your directory> DISK=diskN`. The sample names
the 32-bit image (`# image: raspios_lite_armhf`) for the Raspberry Pi Model B it was written for; a Zero 2 W or newer takes
`raspios_lite_arm64`. First boot takes several minutes on an old board and reboots once; then the panel shows the loading screen
until Node-RED is up, and any browser on the LAN shows the same page at `http://<host>.local/` (the page talks to port 1880 of the
host it was loaded from; `?address=http://other-host:1880` overrides that).

## The display

The sample drives the Waveshare 3.5-inch RPi LCD (A) on SPI as the only display; four `runcmd` lines and one drop-in make that
work on a board that also has HDMI:

- `dtoverlay=piscreen,drm,rotate=270`: the upstream `piscreen` overlay's `drm` parameter selects the mainline ILI9486 KMS
  driver, whose pins are this panel's; `rotate` turns the 320×480 panel to landscape. Mesa drives it with its `kmsro`
  driver, rendering on vc4's render node and scanning out on the SPI device.
- `gpu_mem=16` leaves the firmware the minimum, since KMS takes its memory from CMA.
- `fbcon=map:1` puts the console on the panel's framebuffer (`fb1`; `fb0` is vc4's): cog needs the panel's CRTC lit before
  it starts, and only the console lights it. `video=HDMI-A-1:d` switches the HDMI scanout off, since nothing shows there.
- `/etc/systemd/system/pihero-kiosk.service.d/panel.conf` hides vc4's card from the kiosk unit (`DevicePolicy=closed` plus
  `DeviceAllow=` for the panel's card, the render node and the input devices): cog takes the first DRM card it may open and
  never falls back to another.
- `cgroup_enable=memory` turns on the memory controller Raspberry Pi OS boots without, so the units' `MemoryMax=` binds.

For an HDMI display drop the `dtoverlay`, `fbcon` and `video` lines and the drop-in; a panel that reports no EDID needs
`bootconfig add cmdline video=HDMI-A-1:<width>x<height>M@60e` instead, and `COG_PLATFORM_DRM_VIDEO_MODE=<width>x<height>` in
`kiosk.conf` when the connector offers several modes.

## The backend

`busy-screen-server` runs Node-RED as the system user `busy-screen` with `/var/lib/busy-screen` as its user directory. The flow is
copied there from `/usr/share/busy-screen/flows.json` on the first start; the editor at `http://<host>.local:1880/` saves to that
copy, and upgrades leave it alone. `/etc/busy-screen/server.conf` is read as an environment file (`PORT`, `NODE_OPTIONS`).
