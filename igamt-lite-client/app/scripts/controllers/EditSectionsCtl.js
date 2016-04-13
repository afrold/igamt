angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope, CloneDeleteSvc, ToCSvc) {
        $scope.fixedSectionTitles = [
            'Message Infrastructure','Metadata','Introduction','Conformance Profiles','Segments and Field Descriptions','Datatypes','Value Sets'
        ];

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
        };

        $scope.isFixedSectionTitle = function(section){
            return $scope.fixedSectionTitles.indexOf(section.sectionTitle) >= 0;
        };
});