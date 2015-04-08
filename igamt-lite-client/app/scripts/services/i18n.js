'use strict';

angular.module('igl').factory('i18n', function() {
    // AngularJS will instantiate a singleton by calling "new" on this function   
    var language;
    var setLanguage = function (theLanguage) {
        $.i18n.properties({
            name: 'messages',
            path: 'lang/',
            mode: 'map',
            language: theLanguage,
            callback: function () {
                language = theLanguage;
            }
        });
    };
    setLanguage('en');
    return {
        setLanguage: setLanguage
    };
});

/*angular.module('ehrRandomizerApp')
  .service('i18n', function i18n() {
    // AngularJS will instantiate a singleton by calling "new" on this function
    var self = this;
    this.setLanguage = function (language) {
        $.i18n.properties({
            name: 'messages',
            path: 'lang/',
            mode: 'map',
            language: language,
            callback: function () {
                self.language = language;
            }
        });
    };
    this.setLanguage('en');
  });*/