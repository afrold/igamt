/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('DocumentMetaDataCtrl', function ($scope, $rootScope, $http, IgDocumentService, blockUI) {
  $scope.saving = false;
  $scope.saved = false;
  $scope.uploader = {};
  $scope.copy=angular.copy()

  $scope.init=function(){
    if(!$rootScope.metaData.implementationNotes){
      $rootScope.metaData.implementationNotes="";
    };
  };



  $scope.successUpload = function ($file, $message, $data) {

      $rootScope.recordChanged();
    var link = JSON.parse($message);
    $rootScope.metaData.coverPicture = link.link;
  };

  $scope.removeCover = function () {
      $rootScope.recordChanged();
      $rootScope.metaData.coverPicture = null;
  };

  $scope.save = function () {
    $scope.saving = true;
    $scope.saved = false;
    if ($rootScope.igdocument != null && $rootScope.metaData != null) {

      IgDocumentService.saveMetadata($rootScope.igdocument.id, $rootScope.metaData).then(function (dateUpdated) {
        $scope.saving = false;
        $scope.saved = true;
        $rootScope.igdocument.metaData = angular.copy($rootScope.metaData);
        $rootScope.igdocument.dateUpdated = dateUpdated;
        if ($scope.editForm) {
          $scope.editForm.$setPristine();
          $scope.editForm.$dirty = false;
        }
        $rootScope.clearChanges();
        $rootScope.msg().text = "documentMetaDataSaved";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;

      }, function (error) {
        $scope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
        $scope.saved = false;

      });
    }
  };
  $scope.reset = function () {
    blockUI.start();
    $scope.editForm.$dirty = false;
    $scope.editForm.$setPristine();
    $scope.uploader.flow.cancel();
    $rootScope.clearChanges();
    $rootScope.metaData = angular.copy($rootScope.igdocument.metaData);
    blockUI.stop();
  };
});
