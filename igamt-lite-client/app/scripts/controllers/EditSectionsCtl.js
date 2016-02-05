angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope, CloneDeleteSvc) {
    	
	    	$scope.cloneSection = function(section) {
        		CloneDeleteSvc.cloneSection(section);
	    	};
	    	
        $scope.close = function () {
            $rootScope.section = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };
        
        $scope.delete = function(section) {
        		CloneDeleteSvc.deleteSection(section);
        } 
});