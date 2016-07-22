'use strict';

angular.module('igl').directive('igCheckFullName', [ '$resource',
    function ($resource) {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                var FullName = $resource('api/sooa/fullnames/:fullName', {fullName: '@fullName'});

                element.on('keyup', function() {
                    if ( element.val().length >= 4) {
                        var fullNameToCheck = new FullName({fullName:element.val()});
                        fullNameToCheck.$get(function() {
                            scope.fullNameUnique  = ((fullNameToCheck.text === 'fullNameNotFound') ? 'valid' : undefined);
                            scope.fullNameValid = 'valid';
                            if(scope.fullNameUnique && scope.fullNameValid) {
                                ctrl.$setValidity('fullName', true);
                            } else {
                                ctrl.$setValidity('fullName', false);
                            }

                        }, function() {
//                            console.log('FAILURE to check email address');
                        });
                    }
                    else {
                        scope.fullNameUnique  = undefined;
                        scope.fullNameValid = undefined;
                        ctrl.$setValidity('fullName', false);
                    }
                });
            }
        };
    }
]);
