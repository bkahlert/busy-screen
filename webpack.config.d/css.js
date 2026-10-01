// noinspection JSUnresolvedReference

// must be in the jsMain/resource folder
const mainCssFile = 'styles.css'

; // noinspection FunctionWithMultipleReturnPointsJS
(function (config) {
  'use strict'
  if (!config || !config.entry) return // test run

  const mainCssFilePath = config.output && config.output.path
    ? config.output.path + '/../../../processedResources/js/main/' + mainCssFile
    : './kotlin/' + mainCssFile
  config.entry.main.push(mainCssFilePath)
  // The stylesheet lives outside the package directory, so the vendored packages are not found by walking up from it.
  config.resolve.modules.push(require('path').resolve(__dirname, '../../node_modules'))
  config.module.rules.push({
    test: /\.css$/,
    use: [
      { loader: 'style-loader' },
      // css-loader resolves the @imports, including the vendored packages; importLoaders runs PostCSS on them too.
      { loader: 'css-loader', options: { importLoaders: 1 } },
      {
        loader: 'postcss-loader',
        options: {
          postcssOptions: {
            plugins: [
              require('autoprefixer')({
                overrideBrowserslist: [
                  'defaults',
                  'chrome >= 86',
                ],
              }),
              require('cssnano'),
            ],
          },
        },
      },
    ],
  })
})(config)
