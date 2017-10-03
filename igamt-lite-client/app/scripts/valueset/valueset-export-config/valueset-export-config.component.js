/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ValueSetExportConfigCtrl', function($scope, $rootScope, IGDocumentExportConfigService, TableLibrarySvc, blockUI) {


  $scope.init = function(){



  };


  $scope.save = function () {

    if($rootScope.igdocument != null && $rootScope.section != null) {
      IGDocumentExportConfigService.save($rootScope.igdocument.id, $rootScope.igExportConfig).then(function (dateUpdated) {
        $scope.saving = false;
        $scope.saved = true;
        $rootScope.igdocument['exportConfig'] = $rootScope.igExportConfig;
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
    $rootScope.igExportConfig = angular.copy($rootScope.igdocument.exportConfig);
  };


});
