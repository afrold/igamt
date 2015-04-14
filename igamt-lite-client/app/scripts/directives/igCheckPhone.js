'use strict';

angular.module('igl').directive('igCheckPhone', [
    function () {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
//                var Email = $resource('api/sooa/emails/:email', {email: '@email'});
//
//                var EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;
                element.on('keyup', function() {
                    ctrl.$setValidity('phone', element.val().length === 10);
                });
            }
        };
    }
]);
