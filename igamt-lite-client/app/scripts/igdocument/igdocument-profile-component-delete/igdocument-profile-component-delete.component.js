/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DeleteProfileComponentCtrl', function ($scope, $mdDialog, pcLibId, profileComponentToDelete, $rootScope, $http, PcService) {
  $scope.profileComponentToDelete = profileComponentToDelete;
  $scope.loading = false;
  $scope.delete = function () {
    $scope.loading = true;
    PcService.delete(pcLibId, $scope.profileComponentToDelete).then(function (profileComponentLib) {
      console.log(profileComponentLib);
      $rootScope.igdocument.profile.profileComponentLibrary = profileComponentLib;
      for (i = 0; i < $rootScope.profileComponents.length; i++) {
        if ($scope.profileComponentToDelete.id === $rootScope.profileComponents[i].id) {
          $rootScope.profileComponents.splice(i, 1);
        }
      }
      if ($rootScope.profileComponent && $rootScope.profileComponent.id === $scope.profileComponentToDelete.id) {
        $rootScope.profileComponent = null;
        $rootScope.subview = null;
      }
      $mdDialog.hide();

    });

  };

  $scope.cancel = function () {
    $mdDialog.hide();
  };
});
