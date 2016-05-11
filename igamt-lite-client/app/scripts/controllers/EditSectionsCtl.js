angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope, CloneDeleteSvc, ToCSvc,SectionSvc) {
        $scope.saving = false;
        $scope.saved = false;

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
            SectionSvc.delete($rootScope.igDocument.id,tocSection.id).then(function (result) {
                $scope.saving = false;
                $scope.saved = true;
                CloneDeleteSvc.deleteSection(tocSection);
                $rootScope.$broadcast('event:SetToC');
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
                $scope.saved = false;
                $scope.saving = false;
            });
        };

        $scope.isFixedSectionTitle = function(section){
            return $scope.fixedSectionTitles.indexOf(section.sectionTitle) >= 0;
        };

        $scope.save = function () {
            $scope.saving = true;
            $scope.saved = false;
            if($rootScope.igDocument != null && $rootScope.section != null) {
                SectionSvc.save($rootScope.igDocument.id, $rootScope.section).then(function (result) {
                    $scope.saving = false;
                    $scope.saved = true;
                    $rootScope.entry.reference = angular.copy($rootScope.section);
                 }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                    $scope.saved = false;
                    $scope.saving = false;
                });
            }
        };

        $scope.cancel = function () {
            $rootScope.metaData = null;
        };

});