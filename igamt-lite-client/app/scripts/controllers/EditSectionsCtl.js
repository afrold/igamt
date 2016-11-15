angular.module('igl')
    .controller('SectionsListCtrl', function ($scope, $rootScope, CloneDeleteSvc, ToCSvc,SectionSvc) {
        $scope.saving = false;
        $scope.saved = false;

        $scope.fixedSectionTitles = [
            'Message Infrastructure','Metadata','Introduction','Conformance Profiles','Segments and Field Descriptions','Datatypes','Value Sets'
        ];
//
//	    	$scope.copy = function(section) {
//	    		var tocSection = ToCSvc.findEntryFromRefId(section.id, $rootScope.tocData);
//        		CloneDeleteSvc.copySection(tocSection);
//	    	};
//

        $scope.close = function () {
            $rootScope.section = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };
        
//        $scope.delete = function(section) {
//    		var tocSection = ToCSvc.findEntryFromRefId(section.id, $rootScope.tocData);
//            SectionSvc.delete($rootScope.igdocument.id,tocSection.id).then(function (result) {
//                $scope.saving = false;
//                $scope.saved = true;
//                CloneDeleteSvc.deleteSection(tocSection);
//                $rootScope.$broadcast('event:SetToC');
//            }, function (error) {
//                $rootScope.msg().text = error.data.text;
//                $rootScope.msg().type = error.data.type;
//                $rootScope.msg().show = true;
//                $scope.saved = false;
//                $scope.saving = false;
//            });
//        };

        $scope.isFixedSectionTitle = function(section){
            return $scope.fixedSectionTitles.indexOf(section.sectionTitle) >= 0;
        };

        $scope.save = function () {
            if($rootScope.igdocument != null && $rootScope.section != null) {
                SectionSvc.update($rootScope.igdocument.id, $rootScope.section).then(function (dateUpdated) {
                    $scope.saving = false;
                    $scope.saved = true;
                    $rootScope.section.dateUpdated = dateUpdated;
                    $rootScope.igdocument.dateUpdated = dateUpdated;
                    SectionSvc.merge($rootScope.originalSection,$rootScope.section);
                    if($scope.editForm) {
                        $scope.editForm.$setPristine();
                        $scope.editForm.$dirty = false;
                    }
                    $rootScope.clearChanges();
                    $rootScope.msg().text = "sectionSaved";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;
                 }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                    $scope.saved = false;
                    $scope.saving = false;
                });
            }
        };

        $scope.reset = function () {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $rootScope.clearChanges();
            $rootScope.section = angular.copy($rootScope.originalSection);
        };


//        $scope.$watch(
//            function(){
//              return $scope.editForm.$dirty;
//            },
//            function handleFormState( newValue) {
//                if(newValue){
//                    $rootScope.recordChanged();
//                }else{
//                    $rootScope.clearChanges();
//                }
//            }
//        );



});