/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function ($rootScope) {
    return {
        link: function ($scope, elem, attrs) {
            window.onbeforeunload = function () {
                if ($rootScope.hasChanges()) {
                    return "You have unsaved data on this page. If you leave this page your data will be lost.\n\n Are you sure you want to leave this page?";
                }
            };
            $scope.$on('$locationChangeStart', function (event, next, current) {
                if ($rootScope.hasChanges()) {

                    $rootScope.openConfirmLeaveDlg().result.then(function () {
                        $rootScope.clearChanges();
                    },function(){
                        event.stopImmediatePropagation();
                        event.preventDefault();
                    });

//                    if (!confirm("You have unsaved data. If you leave this section your data will be lost.\n\n Are you sure you want to leave this page?")) {
//                        event.stopImmediatePropagation();
//                        event.preventDefault();
//                    }else{
//                        $rootScope.clearChanges();
//                    }
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

                            $rootScope.openConfirmLeaveDlg().result.then(function () {
                                $rootScope.clearChanges();
                            },function(){
                                event.stopImmediatePropagation();
                                event.preventDefault();
                            });


//                            var message = "You have unsaved data. If you leave this section your data will be lost.\n\n Are you sure you want to leave this page?";
//                            if (!confirm(message)) {
//                                event.stopImmediatePropagation();
//                                event.preventDefault();
//                            }else{
//                                $rootScope.clearChanges();
//                            }
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
