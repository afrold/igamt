/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function ($rootScope) {
    return {
        link: function ($scope, elem, attrs) {
            window.onbeforeunload = function () {
                if ($rootScope.hasChanges()) {
                    return "You'll lose your changes if you leave. Are you sure you want to leave this page?";
                }
            };
            $scope.$on('$locationChangeStart', function (event, next, current) {
                if ($rootScope.hasChanges()) {
                    if (!confirm("You'll lose your changes if you leave. Are you sure you want to leave this page?")) {
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
            priority: 100,
            link: {
                pre: function (scope, element, attr) {
                    element.bind('click', function (event) {
                        if ($rootScope.hasChanges()) {
                            var message = "This section contains unsaved data. \n\n Are you sure you want to leave this page? Data you have entered will not be saved";
                            if (!confirm(message)) {
                                event.stopImmediatePropagation();
                                event.preventDefault();
                            }
                        }
                    });
                }
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
