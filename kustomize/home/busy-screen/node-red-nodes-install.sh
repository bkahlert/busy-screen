#!/bin/bash

apt-get -qq install libavahi-compat-libdnssd-dev libudev-dev

cd "/home/$1/busy-screen" || exit 1

npm install --unsafe-perm --save --no-update-notifier --no-audit --no-fund --production \
  node-red-dashboard \
  node-red-contrib-huemagic \
  node-red-contrib-ip \
  node-red-contrib-play-audio \
  node-red-node-discovery \
  node-red-node-ping \
  node-red-node-random \
  node-red-node-smooth \
  node-red-node-ui-iframe \
  node-red-node-ui-webcam \
  moment \
  url

#npm install --unsafe-perm --save --no-update-notifier --no-audit --no-fund --production \
#  node-red-node-pi-gpio \
#  node-red-node-serialconsole
