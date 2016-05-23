/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function ($rootScope) {
    return {
        link: function ($scope, elem, attrs) {
//            window.onbeforeunload = function () {
//                if ($rootScope.hasChanges()) {
//                    return "This page is asking you to confirm that you want to leave - data you have entered may not be saved";
//                }
//            };
//            $scope.$on('$locationChangeStart', function (event, next, current) {
//                if ($rootScope.hasChanges()) {
//                    if (!confirm("This page is asking you to confirm that you want to leave - data you have entered may not be saved")) {
//                        event.preventDefault();
//                    }else{
//                        $rootScope.clearChanges();
//                    }
//                }
//            });
        }
    };
});

angular.module('igl').directive("confirmClick",
    function ($rootScope) {
        return {
            priority: 1000,
            link: function (scope, element, attr) {
//                var clickAction = attr.ngClick;
//                attr.ngClick = "";
//                element.bind('click', function (event) {
//                    if ($rootScope.hasChanges()) {
//                        var message = "This page is asking you to confirm that you want to leave - data you have entered may not be saved";
//                        if (confirm(message)) {
//                            $rootScope.clearChanges();
//                            //scope.$eval(clickAction);
//                        }else{
//                            event.preventDefault();
//                        }
//                    }
////                    if ($rootScope.hasChanges()) {
////                        var message = "You have unsaved changes, Do you want to stay on the page?";
////                        if (confirm(message)) {
////                            scope.$eval(clickAction);
////                        }else{
////                            $rootScope.clearChanges();
////                        }
////                    } else {
////                        scope.$eval(clickAction);
////                    }
//                });
//
////                element.bind('click', function (e) {
////                    if ($rootScope.hasChanges()) {
////                        // message defaults to "Are you sure?"
////                        var message = "You have unsaved changes, Do you want to stay on the page?";
////                        if (confirm(message)) {
////                            scope.confirmFunction();
////                        }
////                    } else {
////                        scope.confirmFunction();
////                    }
////                });
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
