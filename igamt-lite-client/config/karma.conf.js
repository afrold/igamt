var webpackConfig = require('./webpack.test');

module.exports = function (config) {
  var _config = {
    basePath: '',

    frameworks: ['jasmine'],

    files: [
      // bower:js
      '../src/bower_components/jquery/dist/jquery.js',
      '../src/bower_components/es5-shim/es5-shim.js',
      '../src/bower_components/angular/angular.js',
      '../src/bower_components/angular-aria/angular-aria.js',
      '../src/bower_components/angular-animate/angular-animate.js',
      '../src/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      '../src/bower_components/angular-cookies/angular-cookies.js',
      '../src/bower_components/angular-messages/angular-messages.js',
      '../src/bower_components/angular-resource/angular-resource.js',
      '../src/bower_components/angular-route/angular-route.js',
      '../src/bower_components/angular-sanitize/angular-sanitize.js',
      '../src/bower_components/angular-smart-table/dist/smart-table.js',
      '../src/bower_components/angular-touch/angular-touch.js',
      '../src/bower_components/angular-ui-router/release/angular-ui-router.js',
      '../src/bower_components/json3/lib/json3.js',
      '../src/bower_components/ng-idle/angular-idle.js',
      '../src/bower_components/lodash/lodash.js',
      '../src/bower_components/restangular/dist/restangular.js',
      '../src/bower_components/angular-local-storage/dist/angular-local-storage.js',
      '../src/bower_components/angularjs-dropdown-multiselect/src/angularjs-dropdown-multiselect.js',
      '../src/bower_components/angular-drag-and-drop-lists/angular-drag-and-drop-lists.js',
      '../src/bower_components/froala-wysiwyg-editor/js/froala_editor.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/froala_editor.pkgd.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/align.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/char_counter.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/code_beautifier.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/code_view.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/colors.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/draggable.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/emoticons.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/entities.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/file.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/font_family.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/font_size.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/fullscreen.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/image.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/image_manager.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/inline_style.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/line_breaker.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/link.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/lists.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/paragraph_format.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/paragraph_style.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/quick_insert.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/quote.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/save.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/table.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/url.min.js',
      '../src/bower_components/froala-wysiwyg-editor/js/plugins/video.min.js',
      '../src/bower_components/angular-froala/src/angular-froala.js',
      '../src/bower_components/ng-notifications-bar/dist/ngNotificationsBar.min.js',
      '../src/bower_components/jquery-ui/jquery-ui.js',
      '../src/bower_components/angular-dragdrop/src/angular-dragdrop.js',
      '../src/bower_components/angular-ui-tree/dist/angular-ui-tree.js',
      '../src/bower_components/angular-bootstrap-contextmenu/contextMenu.js',
      '../src/bower_components/angular-block-ui/dist/angular-block-ui.js',
      '../src/bower_components/angular-ui-select/dist/select.js',
      '../src/bower_components/nsPopover/src/nsPopover.js',
      '../src/bower_components/angular-ui-notification/dist/angular-ui-notification.js',
      '../src/bower_components/angular-strap/dist/angular-strap.js',
      '../src/bower_components/angular-strap/dist/angular-strap.tpl.js',
      '../src/bower_components/angular-object-diff/dist/angular-object-diff.js',
      '../src/bower_components/ng-tags-input/ng-tags-input.js',
      '../src/bower_components/angularjs-slider/dist/rzslider.js',
      '../src/bower_components/flow.js/dist/flow.js',
      '../src/bower_components/ng-flow/dist/ng-flow.js',
      '../src/bower_components/angular-material/angular-material.js',
      '../src/bower_components/angular-ui-sortable/sortable.js',
      '../src/bower_components/crypto-js/index.js',
      '../src/bower_components/angular-md5/angular-md5.js',
      '../src/bower_components/bootstrap/dist/js/bootstrap.js',
      '../src/bower_components/angular-clipboard/angular-clipboard.js',
      '../src/bower_components/angular-scenario/angular-scenario.js',
      '../src/bower_components/angular-mocks/angular-mocks.js',
      // endbower
      '../src/app/scripts/**/*.js',
      '../src/app/lib/**/*.js',
      '../src/app/lib/*.js',
      '../mocks/**/*.js',
      '../spec/**/*.spec.js',
      {pattern: './config/karma-test-shim.js', watched: false}
    ],

    preprocessors: {
      './config/karma-test-shim.js': ['webpack', 'sourcemap']
    },

    webpack: webpackConfig,

    webpackMiddleware: {
      stats: 'errors-only'
    },

    webpackServer: {
      noInfo: true
    },

    reporters: ['kjhtml'],
    port: 9876,
    colors: true,
    logLevel: config.LOG_INFO,
    autoWatch: false,
    browsers: ['Chrome'],
    singleRun: true
  };

  config.set(_config);
};
