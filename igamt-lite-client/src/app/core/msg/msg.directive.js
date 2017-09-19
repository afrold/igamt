'use strict';

angular.module('igl')
.directive('msg', [function () {
    return {
        restrict: 'EA',
        replace: true,
        link: function (scope, element, attrs) {
            //console.log("Dir");
            var key = attrs.key;
            if (attrs.keyExpr) {
                scope.$watch(attrs.keyExpr, function (value) {
                    key = value;
                    element.text($.i18n.prop(value));
                });
            }
            scope.$watch('language()', function (value) {
                element.text($.i18n.prop(key));
            });
        }
    };
}]);
