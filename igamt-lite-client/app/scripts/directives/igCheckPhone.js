'use strict';

angular.module('igl').directive('igCheckPhone', [
    function () {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                 element.on('keyup', function() {
                     scope.phoneIsNumber  = element.val() && isFinite(element.val()) && angular.isNumber(element.val()) ? 'valid' : undefined;
                     scope.phoneValidLength  = element.val() && element.val().length >= 7  ? 'valid' : undefined;
                     ctrl.$setValidity('phone', scope.phoneIsNumber && scope.phoneValidLength);
                });
            }
        };
    }
]);
