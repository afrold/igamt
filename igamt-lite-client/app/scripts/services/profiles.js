'use strict';

/**
 * @ngdoc function
 * @description
 * # AboutCtrl
 * Controller of the clientApp
 */

// Declare factory
angular.module('igl').factory('Profiles', function(Restangular) {
     return Restangular.service('profiles');
});





