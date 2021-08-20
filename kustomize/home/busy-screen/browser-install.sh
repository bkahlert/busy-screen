#!/bin/bash

# https://maker-tutorials.com/autostart-midori-browser-vollbild-kiosk-mode-via-konsole-ohne-desktop/
# https://www.matteomattei.com/web-kiosk-with-raspberry-pi-and-read-only-sd/

apt-get -qq install midori matchbox-window-manager xserver-xorg x11-xserver-utils unclutter xinit
apt-get -qq install ttf-mscorefonts-installer fonts-dejavu fonts-dejavu-extra

# user needs to handle /dev/tty devices
gpasswd -a "$1" tty

# set permissions of /dev/tty to defined state
sed -i 's#^\(\s*exit .*\)#chmod g+rw /dev/tty?\n\1#g' /etc/rc.local

# autostart
cat <<EOF >>"/home/$1/.bashrc"
# start kiosk unless logged in through SSH session
if [ -z "\${SSH_TTY}" ]; then
  ~/busy-screen/ui-start.sh
fi
EOF
