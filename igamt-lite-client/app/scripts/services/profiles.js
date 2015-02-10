'use strict';

/**
 * @ngdoc function
 * @description
 * # AboutCtrl
 * Controller of the clientApp
 */

// Declare factory
angular.module('igl').factory('CustomProfiles', function(Restangular) {
    return Restangular.service('customProfiles');
});


// Declare factory
angular.module('igl').factory('PredefinedProfiles', function(Restangular) {
    return Restangular.service('predefinedProfiles');
});


// Declare factory
angular.module('igl').factory('Profiles', function(Restangular) {
    return {
        customs: Restangular.service('customProfiles'),
        predefined: Restangular.service('predefinedProfiles')
    }
});


angular.module('igl').factory('Profile', function(Restangular) {
    return {
        object: Restangular.service('customProfiles'),
        clone: Restangular.service('predefinedProfiles')
    }
});



