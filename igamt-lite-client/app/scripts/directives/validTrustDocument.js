'use strict';

//Angular doesn't perform any validation on file input.
//We bridge the gap by linking the required directive to the
//presence of a value on the input.
angular.module('igl').directive('validTrustDocument', [
    function () {
        return {
            require:'ngModel',
            link:function(scope,el,attrs,ngModel){
                //change event is fired when file is selected
                el.bind('change', function() {
                    scope.$apply( function() {
                        ngModel.$setViewValue(el.val());
                        //console.log("validTrustDocument Val=", el.val());
                        //ngModel.$render();
                    });
                });
            }
        };
    }
]);
