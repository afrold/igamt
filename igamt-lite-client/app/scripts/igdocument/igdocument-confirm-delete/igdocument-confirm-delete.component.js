/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmIGDocumentDeleteCtrl', function ($scope, $mdDialog, igdocumentToDelete, $rootScope, $http) {
  $scope.igdocumentToDelete = igdocumentToDelete;
  $scope.loading = false;
  $scope.delete = function () {
    $scope.loading = true;
    $http.post($rootScope.api('api/igdocuments/' + $scope.igdocumentToDelete.id + '/delete')).then(function (response) {
      var index = $rootScope.igs.indexOf($scope.igdocumentToDelete);
      // if (index > -1) $rootScope.igs.splice(index, 1);
      $rootScope.backUp = null;
      if ($scope.igdocumentToDelete === $rootScope.igdocument) {
        $rootScope.closeIGDocument();
      }

      $rootScope.msg().text = "igDeleteSuccess";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $rootScope.manualHandle = true;
      $scope.loading = false;
      $mdDialog.hide($scope.igdocumentToDelete);

    }, function (error) {
      $scope.error = error;
      $scope.loading = false;
      $rootScope.msg().text = "igDeleteFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
    });
  };

  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };
});
