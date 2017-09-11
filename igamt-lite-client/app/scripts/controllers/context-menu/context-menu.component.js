angular.module('igl').controller('ContextMenuCtl', function ($scope, $rootScope, ContextMenuSvc) {

    $scope.clicked = function (item) {
        ContextMenuSvc.put(item);
    };
});