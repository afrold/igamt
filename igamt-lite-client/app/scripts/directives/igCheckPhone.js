'use strict';

angular.module('igl').directive('igCheckPhone', [
    function () {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                var NUMBER_REGEXP = /[0-9]*/;
                element.on('keyup', function() {
                     if ( element.val() &&  element.val() != null && element.val() != "") {
                             scope.phoneIsNumber  =  (NUMBER_REGEXP.test(element.val()))   && element.val() > 0 ? 'valid' : undefined;
                             scope.phoneValidLength  = element.val().length >= 7 ? 'valid' : undefined;
                             if(scope.phoneIsNumber && scope.phoneValidLength ) {
                                 ctrl.$setValidity('phone', true);
                             } else {
                                 ctrl.$setValidity('phone', false);
                             }
                     }
                     else {
                         scope.phoneIsNumber = undefined;
                         scope.phoneValidLength = undefined;
                         ctrl.$setValidity('phone', true);
                     }
                 });
            }
        };
    }
]);
