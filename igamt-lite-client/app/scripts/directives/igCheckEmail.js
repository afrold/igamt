'use strict';

angular.module('igl').directive('igCheckEmail', [ '$resource',
    function ($resource) {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {
                var Email = $resource('api/sooa/emails/:email', {email: '@email'});

                var EMAIL_REGEXP = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}$/;

                element.on('keyup', function() {
                    if ( element.val().length !== 0 && EMAIL_REGEXP.test(element.val()) ) {
                        var emailToCheck = new Email({email:element.val()});
                        emailToCheck.$get(function() {
                            scope.emailUnique  = ((emailToCheck.text === 'emailNotFound') ? 'valid' : undefined);
                            scope.emailValid = (EMAIL_REGEXP.test(element.val()) ? 'valid' : undefined);
                            if(scope.emailUnique && scope.emailValid) {
                                ctrl.$setValidity('email', true);
                            } else {
                                ctrl.$setValidity('email', false);
                            }

                        }, function() {
//                            console.log('FAILURE to check email address');
                        });
                    }
                    else {
                        scope.emailUnique  = undefined;
                        scope.emailValid = undefined;
                        ctrl.$setValidity('email', false);
                    }
                });
            }
        };
    }
]);
