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
      "bower_components/angular-drag-and-drop-lists/angular-drag-and-drop-lists.js",
 
      // gcr: Jasmine inject() will not execute if this file is included. 'app/lib/angular-mocks/angular-mocks.js',
      'app/dev/**/*.js',
      'app/lib/utils.js',
      'app/lib/Objectid.js',
      'app/lib/angular-treetable/js/jquery.treetable.js',
      'app/lib/angular-treetable/js/angular-treetable.min.js',
      'app/lib/jquery.i18n.properties-1.0.9.js',
       'app/scripts/**/*.js',
//      'test/spec/controllers/**/hl7VersionsMessagesTest.js',
      'test/spec/services/**/ProfileAccessSvcTest.js',
//      'test/spec/services/**/ToCSvcTest.js',
      'test/spec/services/**/CloneDeleteSvcTest.js',
     { pattern:  'test/fixtures/igDocuments/*.json',
          watched:  true,
          served:   true,
          included: false }
    ],
// The above "pattern" must be in order to load any of the profile-*.json files.     

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
//      'PhantomJS',
      'Chrome'
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
