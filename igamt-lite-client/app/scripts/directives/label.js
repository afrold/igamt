



angular.module('igl')
    .directive('displayLabel', function () {
        return {
            restrict: 'E', //E = element, A = attribute, C = class, M = comment
            scope: {
                //@ reads the attribute value, = provides two-way binding, & works with functions
                element: '=of',
                description:'=desc'

            },


            templateUrl: 'label.html',
            controller: 'labelController', //Embed a custom controller in the directive
            link: function ($scope, element, attrs) {

            } //DOM manipulation
        }
    });
angular.module('igl').directive('noDirtyCheck', function() {
    // Interacting with input elements having this directive won't cause the
    // form to be marked dirty.
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function(scope, elm, attrs, ctrl) {
            ctrl.$setDirty = angular.noop;
        }
    };
});