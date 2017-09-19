var webpackConfig = require('./webpack.test');

module.exports = function (config) {
  var _config = {
    basePath: '../',

    frameworks: ['jasmine'],

    files: [
      // bower:js
      'app/bower_components/jquery/dist/jquery.js',
      'app/bower_components/es5-shim/es5-shim.js',
      'app/bower_components/angular/angular.js',
      'app/bower_components/angular-aria/angular-aria.js',
      'app/bower_components/angular-animate/angular-animate.js',
      'app/bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      'app/bower_components/angular-cookies/angular-cookies.js',
      'app/bower_components/angular-messages/angular-messages.js',
      'app/bower_components/angular-resource/angular-resource.js',
      'app/bower_components/angular-route/angular-route.js',
      'app/bower_components/angular-sanitize/angular-sanitize.js',
      'app/bower_components/angular-smart-table/dist/smart-table.js',
      'app/bower_components/angular-touch/angular-touch.js',
      'app/bower_components/angular-ui-router/release/angular-ui-router.js',
      'app/bower_components/json3/lib/json3.js',
      'app/bower_components/ng-idle/angular-idle.js',
      'app/bower_components/lodash/lodash.js',
      'app/bower_components/restangular/dist/restangular.js',
      'app/bower_components/angular-local-storage/dist/angular-local-storage.js',
      'app/bower_components/angularjs-dropdown-multiselect/src/angularjs-dropdown-multiselect.js',
      'app/bower_components/angular-drag-and-drop-lists/angular-drag-and-drop-lists.js',
      'app/bower_components/froala-wysiwyg-editor/js/froala_editor.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/froala_editor.pkgd.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/align.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/char_counter.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/code_beautifier.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/code_view.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/colors.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/draggable.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/emoticons.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/entities.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/file.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/font_family.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/font_size.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/fullscreen.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/image.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/image_manager.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/inline_style.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/line_breaker.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/link.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/lists.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/paragraph_format.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/paragraph_style.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/quick_insert.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/quote.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/save.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/table.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/url.min.js',
      'app/bower_components/froala-wysiwyg-editor/js/plugins/video.min.js',
      'app/bower_components/angular-froala/src/angular-froala.js',
      'app/bower_components/ng-notifications-bar/dist/ngNotificationsBar.min.js',
      'app/bower_components/jquery-ui/jquery-ui.js',
      'app/bower_components/angular-dragdrop/src/angular-dragdrop.js',
      'app/bower_components/angular-ui-tree/dist/angular-ui-tree.js',
      'app/bower_components/angular-bootstrap-contextmenu/contextMenu.js',
      'app/bower_components/angular-block-ui/dist/angular-block-ui.js',
      'app/bower_components/angular-ui-select/dist/select.js',
      'app/bower_components/nsPopover/src/nsPopover.js',
      'app/bower_components/angular-ui-notification/dist/angular-ui-notification.js',
      'app/bower_components/angular-strap/dist/angular-strap.js',
      'app/bower_components/angular-strap/dist/angular-strap.tpl.js',
      'app/bower_components/angular-object-diff/dist/angular-object-diff.js',
      'app/bower_components/ng-tags-input/ng-tags-input.js',
      'app/bower_components/angularjs-slider/dist/rzslider.js',
      'app/bower_components/flow.js/dist/flow.js',
      'app/bower_components/ng-flow/dist/ng-flow.js',
      'app/bower_components/angular-material/angular-material.js',
      'app/bower_components/angular-ui-sortable/sortable.js',
      'app/bower_components/crypto-js/index.js',
      'app/bower_components/angular-md5/angular-md5.js',
      'app/bower_components/bootstrap/dist/js/bootstrap.js',
      'app/bower_components/angular-clipboard/angular-clipboard.js',
      'app/bower_components/angular-scenario/angular-scenario.js',
      'app/bower_components/angular-mocks/angular-mocks.js',
      // endbower
      'app/scripts/**/*.js',
      'app/lib/**/*.js',
      'app/lib/*.js',
      'mocks/**/*.js',
      'spec/**/*.spec.js',
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
