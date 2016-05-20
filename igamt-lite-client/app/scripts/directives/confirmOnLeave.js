/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function ($rootScope) {
    return {
        link: function ($scope, elem, attrs) {
            window.onbeforeunload = function () {
                if ($rootScope.hasChanges()) {
                    return "You have unsaved changes, Do you want to stay on the page?";
                }
            };
            $scope.$on('$locationChangeStart', function (event, next, current) {
                if ($rootScope.hasChanges()) {
                    if (!confirm("You have unsaved changes, Do you want to stay on the page?")) {
                        event.preventDefault();
                    }else{
                        $rootScope.clearChanges();
                    }
                }
            });
        }
    };
});

angular.module('igl').directive("confirmClick",
    function ($rootScope) {
        return {
            priority: 1,
            link: function (scope, element, attr) {
                var clickAction = attr.ngClick;
                attr.ngClick = "";
                element.bind('click', function (event) {
                    var message = "You have unsaved changes, Do you want to stay on the page?";
                    if (confirm(message)) {
                        scope.$eval(clickAction);
                    }else{
                        $rootScope.clearChanges();
                    }
//                    if ($rootScope.hasChanges()) {
//                        var message = "You have unsaved changes, Do you want to stay on the page?";
//                        if (confirm(message)) {
//                            scope.$eval(clickAction);
//                        }else{
//                            $rootScope.clearChanges();
//                        }
//                    } else {
//                        scope.$eval(clickAction);
//                    }
                });

//                element.bind('click', function (e) {
//                    if ($rootScope.hasChanges()) {
//                        // message defaults to "Are you sure?"
//                        var message = "You have unsaved changes, Do you want to stay on the page?";
//                        if (confirm(message)) {
//                            scope.confirmFunction();
//                        }
//                    } else {
//                        scope.confirmFunction();
//                    }
//                });
            }
        }
    });

//angular.module('igl').directive('a', function() {
//    return {
//        restrict: 'E',
//        link: function(scope, elem, attrs) {
//                 elem.on('click', function(e){
//                     if ($rootScope.hasChanges()) {
//                         if(!confirm("You have unsaved changes, Do you want to stay on the page?")) {
//                             e.preventDefault();
//                         }
//                     }
//                });
//         }
//    };
//});
//
//angular.module('igl').directive('li', function() {
//    return {
//        restrict: 'E',
//        link: function(scope, elem, attrs) {
//            elem.on('click', function(e){
//                if ($rootScope.hasChanges()) {
//                    if(!confirm("You have unsaved changes, Do you want to stay on the page?")) {
//                        e.preventDefault();
//                    }
//                }
//            });
//        }
//    };
//});
