'use strict';

/**
 * @ngdoc function
 * @description
 * # AboutCtrl
 * Controller of the clientApp
 */
//
//// Declare factory
//angular.module('igl').factory('Profiles', function(Restangular) {
//     return Restangular.service('profiles');
//});



angular.module('igl').factory('Section', function ($http, $q) {
    var Section = function () {
        this.data = null;
        this.type = null;
        this.sections = [];
    };
    return Section;
});




