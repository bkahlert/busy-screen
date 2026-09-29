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

Three `runcmd` lines are specific to the display and the board. `dtoverlay=piscreen,drm,rotate=90` is the Waveshare 3.5-inch RPi
LCD (A) on SPI: the upstream `piscreen` overlay's `drm` parameter selects the mainline ILI9486 KMS driver, whose pins are this panel's;
`rotate` turns the 320×480 panel to landscape. `gpu_mem=16` leaves the firmware the minimum, since KMS takes its memory from CMA.
`cgroup_enable=memory` turns on the memory controller Raspberry Pi OS boots without, so the units' `MemoryMax=` binds.

For an HDMI display drop the `dtoverlay` line; a panel that reports no EDID needs
`bootconfig add cmdline video=HDMI-A-1:<width>x<height>M@60e` instead, and `COG_PLATFORM_DRM_VIDEO_MODE=<width>x<height>` in
`kiosk.conf` when the connector offers several modes.

## The backend

`busy-screen-server` runs Node-RED as the system user `busy-screen` with `/var/lib/busy-screen` as its user directory. The flow is
copied there from `/usr/share/busy-screen/flows.json` on the first start; the editor at `http://<host>.local:1880/` saves to that
copy, and upgrades leave it alone. `/etc/busy-screen/server.conf` is read as an environment file (`PORT`, `NODE_OPTIONS`).
