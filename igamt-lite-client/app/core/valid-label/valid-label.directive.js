/**
 * Created by haffo on 5/19/16.
 */


angular.module('igl').directive('validExtension', [ '$resource',
    function ($resource) {
        return {
            restrict: 'AC',
            require: 'ngModel',
            link: function (scope, element, attrs, ctrl) {

            }
        };
    }
]);
