'use strict';

angular.module('igl').directive('igCheckPhone', [
    function () {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                 element.on('keyup', function() {
                    ctrl.$setValidity('phone', angular.isNumber(element.val()) && element.val().length === 10);
                });
            }
        };
    }
]);
