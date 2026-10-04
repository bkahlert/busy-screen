// noinspection JSUnresolvedReference

// The preview's pages and the VM kiosk reach the dev server on 8082 (netmon has 8081, so both can preview at once). The
// kiosk asks for Host: 10.0.2.2:8082, which webpack-dev-server rejects unless allowedHosts says otherwise. With a host set,
// the client would reconnect to that host, which is the guest's own loopback in the VM, so it takes the address the page
// came from.
;(function (config) {
  'use strict'

  config.devServer = Object.assign(config.devServer || {}, {
    host: '127.0.0.1',
    port: 8082,
    allowedHosts: 'all',
    client: { webSocketURL: 'auto://0.0.0.0:0/ws' },
  })
})(config)
