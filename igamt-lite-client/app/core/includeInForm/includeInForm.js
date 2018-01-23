/**
 * Created by ena3 on 1/18/18.
 */
angular.module('igl')
    .directive('attach', function () {
        return {
            require: "ngModel",
            link: function (scope, element, attrs,ngModelCtr) {
                    console.log("calling attach ")
                    var form =scope.$eval(attrs.formSource);
                ngModelCtr.$name = scope.$eval(attrs.inputName);
                form.$addControl(ngModelCtr);

            }
        }});