#!/bin/bash

apt-get update
apt-get -qq install build-essential git curl
apt-get -qq install nodejs npm

#NODE_VERSION=16.7.0
## script extracted from bash https://raw.githubusercontent.com/node-red/linux-installers/master/deb/update-nodejs-and-nodered
## cannot be used directly because there's no option to install upfront using firstboot scripts
## any attempt to do so (su -u; settings env vars) corrupts permissions of sudo command
#if [[ "$(uname -m)" =~ "i686" ]]; then
#  echo "Using i686" | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  curl -sSL -o /tmp/node.tgz https://unofficial-builds.nodejs.org/download/release/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-x86.tar.gz 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  # unpack it into the correct places
#  hd=$(head -c 9 /tmp/node.tgz)
#  if [ "$hd" == "<!DOCTYPE" ]; then
#      CHAR="\033[1;31m\u2718\033[0m File not downloaded";
#  else
#      if sudo tar -zxf /tmp/node.tgz --strip-components=1 -C /usr 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null; then CHAR='\033[1;32m\u2714\033[0m'; else CHAR='\033[1;31m\u2718\033[0m'; fi
#  fi
#  rm /tmp/node.tgz 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  echo -ne "Install Node.js for i686            $CHAR"
#  echo ""
#elif uname -m | grep -q armv6l ; then
#  sudo apt remove -y nodejs nodejs-legacy npm 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo rm -rf /etc/apt/sources.d/nodesource.list /usr/lib/node_modules/npm*
#  echo -ne "Install Node.js for Armv6           \r\n"
#  # f=$(curl -sL https://nodejs.org/download/release/latest-dubnium/ | grep "armv6l.tar.gz" | cut -d '"' -f 2)
#  # curl -sL -o node.tgz https://nodejs.org/download/release/latest-dubnium/$f 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  curl -sSL -o /tmp/node.tgz https://unofficial-builds.nodejs.org/download/release/v${NODE_VERSION}/node-v${NODE_VERSION}-linux-armv6l.tar.gz 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  # unpack it into the correct places
#  hd=$(head -c 9 /tmp/node.tgz)
#  if [ "$hd" == "<!DOCTYPE" ]; then
#      CHAR="\033[1;31m\u2718\033[0m File not downloaded";
#  else
#      if sudo tar -zxf /tmp/node.tgz --strip-components=1 -C /usr 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null; then CHAR='\033[1;32m\u2714\033[0m'; else CHAR='\033[1;31m\u2718\033[0m'; fi
#  fi
#  # remove the tgz file to save space
#  rm /tmp/node.tgz 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  echo -ne "Install Node.js for Armv6           $CHAR"
#  echo ""
#else
#  echo "Installing nodejs $NODE_VERSION" | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  # clean out old nodejs stuff
#  npv=$(npm -v 2>/dev/null | head -n 1 | cut -d "." -f1)
#  sudo apt remove -y nodejs nodejs-legacy npm 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo dpkg -r nodejs 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo dpkg -r node 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo rm -rf /opt/nodejs 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo rm -rf /usr/local/lib/nodejs* 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo rm -f /usr/local/bin/node* 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  sudo rm -rf /usr/local/bin/npm* /usr/local/bin/npx* /usr/lib/node_modules/npm* 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  if [ "$npv" = "1" ]; then
#      sudo rm -rf /usr/local/lib/node_modules/node-red* /usr/lib/node_modules/node-red* 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  fi
#  sudo apt -y autoremove 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  echo -ne "  Remove old version of Node.js       \033[1;32m\u2714\033[0m\r\n"
#  echo "Grab the LTS bundle" | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  echo -ne "Install Node.js $NODE_VERSION LTS              \r\n"
#  # use the official script to install for other debian platforms
#  sudo apt install -y curl 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  curl -sSL https://deb.nodesource.com/setup_$NODE_VERSION.x | sudo -E bash - 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
#  if sudo apt install -y nodejs 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null; then CHAR='\033[1;32m\u2714\033[0m'; else CHAR='\033[1;31m\u2718\033[0m'; fi
#  echo -ne "Install Node.js $NODE_VERSION LTS              $CHAR"
#  echo ""
#fi
