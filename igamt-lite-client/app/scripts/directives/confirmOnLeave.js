/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function ($rootScope,$location) {
    return {
        priority:-100,
        link: function ($scope, elem, attrs) {
            window.onbeforeunload = function () {
                if ($rootScope.hasChanges()) {
                    return "You have unsaved data on this page. If you leave this page your data will be lost.\n\n Are you sure you want to leave this page?";
                }
            };
            $scope.$on('$locationChangeStart', function (event, next, current) {
                if ($rootScope.hasChanges()) {
                    event.preventDefault();
                    $rootScope.openConfirmLeaveDlg().result.then(function () {
                        $rootScope.clearChanges();
                        var go = next.substring(next.indexOf("#")+1,next.length);
                        $location.path(go);
                    },function(){

                    });
                }
            });
        }




    };
});