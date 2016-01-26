angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope) {
    	
        $scope.close = function () {
            $rootScope.section = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };

});