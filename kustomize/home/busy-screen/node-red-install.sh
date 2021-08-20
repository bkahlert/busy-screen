#!/bin/bash
echo -ne "Install Node-RED               \r\n"
# "-g" and "--unsafe-perm" were removed to avoid all /usr/bin/* permission being fucked up
cd "/home/$1/busy-screen" && npm install --no-update-notifier --no-audit --no-fund --production node-red
ln -s "/home/$1/busy-screen/node_modules/node-red/bin/node-red-pi" /usr/local/bin/node-red-pi
echo -ne "Install Node-RED               \033[1;32m\u2714\033[0m\r\n"
curl -sL -o /usr/bin/node-red-start https://raw.githubusercontent.com/node-red/linux-installers/master/resources/node-red-start 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
curl -sL -o /usr/bin/node-red-stop https://raw.githubusercontent.com/node-red/linux-installers/master/resources/node-red-stop 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
curl -sL -o /usr/bin/node-red-restart https://raw.githubusercontent.com/node-red/linux-installers/master/resources/node-red-restart 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
curl -sL -o /usr/bin/node-red-reload https://raw.githubusercontent.com/node-red/linux-installers/master/resources/node-red-reload 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
curl -sL -o /usr/bin/node-red-log https://raw.githubusercontent.com/node-red/linux-installers/master/resources/node-red-log 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
curl -sL -o /etc/logrotate.d/nodered https://raw.githubusercontent.com/node-red/linux-installers/master/resources/nodered.rotate 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
chmod +x /usr/bin/node-red-start
chmod +x /usr/bin/node-red-stop
chmod +x /usr/bin/node-red-restart
chmod +x /usr/bin/node-red-reload
chmod +x /usr/bin/node-red-log

# add systemd script and configure it for user $1
echo "Now add systemd script and configure it for $1" | sudo tee -a /var/log/nodered-install.log >>/dev/null
if sudo curl -sL -o /lib/systemd/system/nodered.service https://raw.githubusercontent.com/node-red/linux-installers/master/resources/nodered.service 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null; then CHAR='\033[1;32m\u2714\033[0m'; else CHAR='\033[1;31m\u2718\033[0m'; fi
# set the memory, User Group and WorkingDirectory in nodered.service
if [ $(cat /proc/meminfo | grep MemTotal | cut -d ":" -f 2 | cut -d "k" -f 1 | xargs) -lt 894000 ]; then mem="256"; else mem="512"; fi
if [ $(cat /proc/meminfo | grep MemTotal | cut -d ":" -f 2 | cut -d "k" -f 1 | xargs) -gt 1894000 ]; then mem="1024"; fi
if [ $(cat /proc/meminfo | grep MemTotal | cut -d ":" -f 2 | cut -d "k" -f 1 | xargs) -gt 3894000 ]; then mem="2048"; fi
# if [ $(cat /proc/meminfo | grep MemTotal | cut -d ":" -f 2 | cut -d "k" -f 1 | xargs) -gt 7894000 ]; then mem="4096"; fi
sudo sed -i 's#=512#='$mem'#;' /lib/systemd/system/nodered.service
sudo sed -i 's#^User=pi#User='"$1"'#;s#^Group=pi#Group='"$1"'#;s#^WorkingDirectory=/home/pi#WorkingDirectory=/home/'"$1"'/busy-screen#;s#$NODE_RED_OPTIONS#-u /home/'"$1"'/busy-screen $NODE_RED_OPTIONS#;' /lib/systemd/system/nodered.service
sudo systemctl daemon-reload 2>&1 | sudo tee -a /var/log/nodered-install.log >>/dev/null
echo -ne "Update systemd script                     $CHAR\r\n"
