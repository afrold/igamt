/**
 * Created by ena3 on 1/23/18.
 */
angular.module('igl').directive('attachForm', function() {
    return {
        restrict: 'A',
        require: 'ngModel',
        link: function (scope, element, attrs,ngModelCtr) {
            var form =scope.$eval(attrs.formSource);
            ngModelCtr.$name = scope.$eval(attrs.inputName);

            form.$addControl(ngModelCtr);

            scope.$on('$destroy', function() {
                form.$removeControl(ngModelCtr);
            });

        }
    };
});