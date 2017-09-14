'use strict';

angular.module('igl')
.directive('focus', [function () {
    return {
        restrict: 'EAC',
        link: function(scope, element, attrs) {
//            element[0].focus();
        }
    };
}]);
