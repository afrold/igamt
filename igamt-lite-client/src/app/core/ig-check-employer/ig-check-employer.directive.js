'use strict';

//This directive is used to make sure both passwords match
angular.module('igl').directive('igCheckEmployer', [
    function () {
        return {
            require: 'ngModel',
            link: function (scope, elem, attrs, ctrl) {
                var employer = '#' + attrs.igCheckEmployer;
                elem.add(employer).on('keyup', function () {
                    scope.$apply(function () {
//                        console.log('Pass1=', elem.val(), ' Pass2=', $(firstPassword).val());
                        var v = elem.val()===$(firstPassword).val();
                        ctrl.$setValidity('noMatch', v);
                    });
                });
            }
        };
    }
]);