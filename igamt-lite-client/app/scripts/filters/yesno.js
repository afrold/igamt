'use strict';

angular.module('igl').filter('yesno', [ function () {
    return function (input) {
        return input ? 'YES' : 'NO';
    };
}]);