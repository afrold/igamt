var webpack = require('webpack');
var helpers = require('./helpers');
var apiMocker = require('connect-api-mocker');

module.exports = {
  devtool: 'inline-source-map',

  resolve: {
    extensions: ['.ts', '.js']
  },

  module: {
    rules: [
      {
        test: /\.ts$/,
        loaders: [
          {
            loader: 'awesome-typescript-loader',
            options: { configFileName: helpers.root('./', 'tsconfig.json') }
           } , 'angular2-template-loader'
        ]
      },
      {
        test: /\.html$/,
        loader: 'html-loader'

      },
      {
        test: /\.(png|jpe?g|gif|svg|woff|woff2|ttf|eot|ico)$/,
        loader: 'null-loader'
      },
      {
        test: /\.css$/,
        exclude: helpers.root('./', 'app'),
        loader: 'null-loader'
      },
      {
        test: /\.css$/,
        include: helpers.root('./', 'app'),
        loader: 'raw-loader'
      }
    ]
  },

  plugins: [
    new webpack.ContextReplacementPlugin(
      // The (\\|\/) piece accounts for path separators in *nix and Windows
      // /angular(\\|\/)core(\\|\/)(esm(\\|\/)src|src)(\\|\/)linker/,
      /angular(\\|\/)core(\\|\/)@angular/,
      helpers.root('./', 'app'), // location of your src
      {} // a map of your routes
    )
  ],

  devServer: {
    historyApiFallback: true,
    stats: 'minimal',
    setup: function(app) {
      app.use(apiMocker('/api', 'mocks/api'));
    }
  }
}

