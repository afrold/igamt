// Karma configuration
// http://karma-runner.github.io/0.12/config/configuration-file.html
// Generated on 2015-01-12 using
// generator-karma 0.8.3

module.exports = function(config) {
  'use strict';

  config.set({
    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

    // base path, that will be used to resolve files and exclude
    basePath: '../',

    // testing framework to use (jasmine/mocha/qunit/...)
    frameworks: ['jasmine-jquery','jasmine'],

    // list of files / patterns to load in the browser
    files: [
      'bower_components/jquery/dist/jquery.js',
      'bower_components/angular/angular.js',
      'bower_components/ng-idle/angular-idle.js',
      'bower_components/angular-bootstrap/ui-bootstrap-tpls.js',
      'bower_components/angular-smart-table/dist/smart-table.js',
      'bower_components/angular-mocks/angular-mocks.js',
      'bower_components/angular-animate/angular-animate.js',
      'bower_components/angular-cookies/angular-cookies.js',
      'bower_components/angular-messages/angular-messages.js',
      'bower_components/angular-resource/angular-resource.js',
      'bower_components/angular-route/angular-route.js',
      'bower_components/angular-sanitize/angular-sanitize.js',
      'bower_components/angular-touch/angular-touch.js',
      'bower_components/lodash/lodash.js',
      'bower_components/restangular/dist/restangular.js',
      'bower_components/rangy/rangy-core.js',
      'bower_components/rangy/rangy-selectionsaverestore.js',
      'bower_components/textAngular/src/textAngular.js',
      'bower_components/textAngular/src/textAngularSetup.js',
      'bower_components/ng-context-menu/dist/ng-context-menu.js',
      'bower_components/angular-local-storage/dist/angular-local-storage.js',
      'bower_components/angularjs-dropdown-multiselect/src/angularjs-dropdown-multiselect.js',
      'bower_components/angular-drag-and-drop-lists/angular-drag-and-drop-lists.js',
      'bower_components/froala-wysiwyg-editor/js/froala_editor.min.js',
      'bower_components/froala-wysiwyg-editor/js/froala_editor.pkgd.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/align.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/char_counter.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/code_beautifier.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/code_view.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/colors.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/draggable.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/emoticons.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/entities.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/file.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/font_family.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/font_size.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/fullscreen.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/image.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/image_manager.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/inline_style.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/line_breaker.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/link.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/lists.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/paragraph_format.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/paragraph_style.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/quick_insert.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/quote.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/save.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/table.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/url.min.js',
      'bower_components/froala-wysiwyg-editor/js/plugins/video.min.js',
      'bower_components/angular-froala/src/angular-froala.js',
      'bower_components/ng-notifications-bar/dist/ngNotificationsBar.min.js',
      'bower_components/angular-ui-router/release/angular-ui-router.min.js',
      'bower_components/angular-bootstrap-contextmenu/contextMenu.js',
      'bower_components/angular-ui-tree/dist/angular-ui-tree.min.js',
      'bower_components/angular-block-ui/dist/angular-block-ui.min.js',

      'app/dev/**/*.js',
      'app/lib/utils.js',
      'app/lib/Objectid.js',
      'app/lib/angular-treetable/js/jquery.treetable.js',
      'app/lib/angular-treetable/js/angular-treetable.min.js',
      'app/lib/jquery.i18n.properties-1.0.9.js',
      'app/lib/froala-plugins/js/plugins/align.min.js',
      'app/lib/froala-plugins/js/plugins/char_counter.min.js',
      'app/lib/froala-plugins/js/plugins/colors.min.js',
      'app/lib/froala-plugins/js/plugins/draggable.min.js',
      'app/lib/froala-plugins/js/plugins/emoticons.min.js',
      'app/lib/froala-plugins/js/plugins/entities.min.js',
      'app/lib/froala-plugins/js/plugins/file.min.js',
      'app/lib/froala-plugins/js/plugins/font_family.min.js',
      'app/lib/froala-plugins/js/plugins/font_size.min.js',
      'app/lib/froala-plugins/js/plugins/fullscreen.min.js',
      'app/lib/froala-plugins/js/plugins/image.min.js',
      'app/lib/froala-plugins/js/plugins/inline_style.min.js',
      'app/lib/froala-plugins/js/plugins/line_breaker.min.js',
      'app/lib/froala-plugins/js/plugins/link.min.js',
      'app/lib/froala-plugins/js/plugins/lists.min.js',
      'app/lib/froala-plugins/js/plugins/paragraph_format.min.js',
      'app/lib/froala-plugins/js/plugins/paragraph_style.min.js',
      'app/lib/froala-plugins/js/plugins/quick_insert.min.js',
      'app/lib/froala-plugins/js/plugins/quote.min.js',
      'app/lib/froala-plugins/js/plugins/save.min.js',
      'app/lib/froala-plugins/js/plugins/table.min.js',
      'app/lib/froala-plugins/js/plugins/url.min.js',

//      'app/scripts/**/*.js',
//      'test/spec/controllers/**/hl7VersionsMessagesTest.js',
//      'test/spec/services/**/ProfileAccessSvcTest.js',
//      'test/spec/services/**/ToCSvcTest.js',
//      'test/spec/services/**/CloneDeleteSvcTest.js',
//      'test/spec/services/**/DatatypeLibrarySvcTest.js',
        'test/spec/services/**/ProfileAccessSvcTest.js',
     { pattern:  'test/fixtures/*.json',
          watched:  true,
          served:   true,
          included: false }
    ],
// The above 'pattern' must be in order to load any of the profile-*.json files.

    // list of files / patterns to exclude
    exclude: [],

    // web server port
    port: 8080,

    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera
    // - Safari (only Mac)
    // - PhantomJS
    // - IE (only Windows)
    browsers: [
      'PhantomJS',
//      'Chrome'
    ],

    // Which plugins to enable
    plugins: [
      'karma-phantomjs-launcher',
      'karma-chrome-launcher',
      'karma-jasmine-jquery',
      'karma-jasmine'
    ],

    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,

    colors: true,

    // level of logging
    // possible values: LOG_DISABLE || LOG_ERROR || LOG_WARN || LOG_INFO || LOG_DEBUG
    logLevel: config.LOG_INFO,

    // Uncomment the following lines if you are using grunt's server to run the tests
    // proxies: {
    //   '/': 'http://localhost:9000/'
    // },
    // URL root prevent conflicts with the site root
    // urlRoot: '_karma_'
  });
};
