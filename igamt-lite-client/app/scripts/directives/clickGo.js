/**
 * Created by haffo on 4/5/15.
 */
angular.module('igl').directive('click', ['$location', function($location) {
        return {
            link: function(scope, element, attrs) {
                element.on('click', function() {
                    scope.$apply(function() {
                        $location.path(attrs.clickGo);
                    });
                });
            }
        }
    }]);


//angular.module('igl').directive('csSelect', function () {
//    return {
//        require: '^stTable',
//        template: '',
//        scope: {
//            row: '=csSelect'
//        },
//        link: function (scope, element, attr, ctrl) {
//
//            element.bind('change', function (evt) {
//                scope.$apply(function () {
//                    ctrl.select(scope.row, 'single');
//                });
//            });
//
//            scope.$watch('row.isSelected', function (newValue, oldValue) {
//                if (newValue === true) {
//                    element.parent().addClass('st-selected');
//                } else {
//                    element.parent().removeClass('st-selected');
//                }
//            });
//        }
//    };
//});
