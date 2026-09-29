// Node-RED settings of the Busy Screen backend. The unit passes this file with --settings and the state directory
// /var/lib/busy-screen as --userDir; the flow file lives there. https://nodered.org/docs/user-guide/runtime/configuration
module.exports = {
  uiPort: process.env.PORT || 1880,
  flowFile: 'flows.json',
  // The flow holds no credentials; an unset secret would make Node-RED generate one and warn about it on every start.
  credentialSecret: false,
  // The web display is served from port 80 and calls the API on port 1880: a cross-origin request in every browser.
  httpNodeCors: { origin: '*', methods: 'GET,PUT,POST,OPTIONS' },
  // Everything the flow needs is vendored; the device has no npm.
  externalModules: {
    autoInstall: false,
    palette: { allowInstall: false, allowUpload: false },
    modules: { allowInstall: false },
  },
  editorTheme: {
    projects: { enabled: false },
    tours: false,
  },
  functionGlobalContext: {
    moment: require('moment'),
  },
  logging: {
    console: { level: 'info', metrics: false, audit: false },
  },
}
