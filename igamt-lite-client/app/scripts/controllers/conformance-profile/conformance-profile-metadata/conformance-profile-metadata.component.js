/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ProfileMetaDataCtrl', function ($scope, $rootScope, $http, ProfileSvc, blockUI) {
  $scope.saving = false;
  $scope.saved = false;
  $scope.save = function () {
    $scope.saving = true;
    $scope.saved = false;
    if ($rootScope.igdocument != null && $rootScope.metaData != null) {

      ProfileSvc.saveMetaData($rootScope.igdocument.id, $rootScope.metaData).then(function (dateUpdated) {
        $scope.saving = false;
        $scope.saved = true;
        $rootScope.igdocument.profile.metaData = angular.copy($rootScope.metaData);
        $rootScope.igdocument.dateUpdated = dateUpdated;
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        $rootScope.clearChanges();
        $rootScope.msg().text = "messageInfrasctructureSaved";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;

      }, function (error) {
        $scope.saving = false;
        $scope.saved = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    }
  };
  $scope.reset = function () {
    blockUI.start();
    $scope.editForm.$dirty = false;
    $scope.editForm.$setPristine();
    $rootScope.clearChanges();
    $rootScope.metaData = angular.copy($rootScope.igdocument.profile.metaData);
    blockUI.stop();

  };
});
