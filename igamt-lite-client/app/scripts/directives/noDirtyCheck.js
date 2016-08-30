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