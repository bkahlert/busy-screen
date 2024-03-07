/**
 * This is the default settings file provided by Node-RED.
 *
 * It can contain any valid JavaScript code that will get run when Node-RED
 * is started.
 *
 * Lines that start with // are commented out.
 * Each entry should be separated from the entries above and below by a comma ','
 *
 * For more information about individual settings, refer to the documentation:
 *    https://nodered.org/docs/user-guide/runtime/configuration
 **/

module.exports = {
  // the tcp port that the Node-RED web server is listening on
  uiPort: process.env.PORT || 1880,

  flowFile: 'flows.json',

  externalModules: {
    autoInstall: true,
    autoInstallRetry: 30,
    palette: {
      allowInstall: true,
      allowUpload: true,
      allowList: [],
      denyList: [],
    },
    modules: {
      allowInstall: true,
      allowList: [],
      denyList: [],
    },
  },

  // The following property can be used to seed Global Context with predefined
  // values. This allows extra node modules to be made available with the
  // Function node.
  // For example,
  //    functionGlobalContext: { os:require('os') }
  // can be accessed in a function block as:
  //    global.get("os")
  functionGlobalContext: {
    // fs: require('fs'),
    moment: require('moment'),
    // os: require('os'),
  },
}
