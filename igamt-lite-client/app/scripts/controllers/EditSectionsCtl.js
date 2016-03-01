angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope, CloneDeleteSvc, ToCSvc) {
    	
	    	$scope.copy = function(section) {
	    		var tocSection = ToCSvc.findEntryFromRefId(section.id, $rootScope.tocData);
        		CloneDeleteSvc.copySection(tocSection);
	    	};
	    	
        $scope.close = function () {
            $rootScope.section = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };
        
        $scope.delete = function(section) {
    			var tocSection = ToCSvc.findEntryFromRefId(section.id, $rootScope.tocData);
        		CloneDeleteSvc.deleteSection(tocSection);
			$rootScope.$broadcast('event:SetToC');
        } 
});