
// An example configuration file.
exports.config = {
  directConnect: true,
  allScriptsTimeout: 5000000,


  // Capabilities to be passed to the webdriver instance.
  capabilities: {
    'browserName': 'chrome'
  },

  // Framework to use. Jasmine is recommended.
  framework: 'jasmine',

  seleniumAddress : "http://localhost:4444/wd/hub",

  // Spec patterns are relative to the current working directory when
  // protractor is called.
  specs: ['e2e/**/*.e2e-spec.js'],

  // Options to be passed to Jasmine.
  jasmineNodeOpts: {
    defaultTimeoutInterval: 30000
  },
  onPrepare: function() {
    // By default, Protractor use data:text/html,<html></html> as resetUrl, but
    // location.replace from the data: to the file: protocol is not allowed
    // (we'll get ‘not allowed local resource’ error), so we replace resetUrl with one
    // with the file: protocol (this particular one will open system's root folder)
    browser.ignoreSynchronization = true;
    browser.waitForAngular();
  },
  baseUrl: 'http://localhost:9002'

};

