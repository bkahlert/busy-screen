#!/bin/bash

# start node-red for some time to have settings.json created
timeout 3m node-red-pi --max-old-space-size=256 -u "/home/$1/busy-screen"

# monaco editor
sed -i 's/\(.*lib: \).*/\1"monaco",/' "/home/$1/busy-screen/settings.js"

# add moment dependency now while we are online
sed -i 's#// os:require('"'"'os'"'"'),#moment:require('"'"'moment'"'"')#' "/home/$1/busy-screen/settings.js"

# external dependencies in node's setup tab
sed -i 's/\(.*functionExternalModules: \).*/\1true,/' "/home/$1/busy-screen/settings.js"

# runtime will attempt to automatically install missing modules
sed -i 's/\(.*\)\/\/ \(.*autoInstall: \)\w*,\(.*\)/\1\2true\3/' "/home/$1/busy-screen/settings.js"

apt-get -qq install apache2
sed -i "s/\(.*DocumentRoot \).*/\1\/home\/$1\/busy-screen\/public_html/g" /etc/apache2/sites-enabled/000-default.conf
sed -i "s/^.*DocumentRoot.*/&\n        <Directory \/home\/$1\/busy-screen\/public_html>\n          Options Indexes\n          Require all granted\n        <\/Directory>/" /etc/apache2/sites-enabled/000-default.conf

mkdir -p "/home/$1/busy-screen/public_html"
chown -R "$1:$1" "/home/$1"
chmod -R a+x "/home/$1/busy-screen"

systemctl enable apache2
systemctl enable nodered
