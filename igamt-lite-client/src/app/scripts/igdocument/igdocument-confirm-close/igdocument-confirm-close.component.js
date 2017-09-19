/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('ConfirmIGDocumentCloseCtrl', function ($scope, $modalInstance, $rootScope, $http) {
  $scope.loading = false;
  $scope.discardChangesAndClose = function () {
    $scope.loading = true;
    $http.get('api/igdocuments/' + $rootScope.igdocument.id, {timeout: 60000}).then(function (response) {
      var index = $rootScope.igs.indexOf($rootScope.igdocument);
      $rootScope.igs[index] = angular.fromJson(response.data);
      $scope.loading = false;
      $scope.clear();
    }, function (error) {
      $scope.loading = false;
      $rootScope.msg().text = "igResetFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;

      $modalInstance.dismiss('cancel');
    });
  };

  $scope.clear = function () {
    $rootScope.closeIGDocument();
    $modalInstance.close();
  };

  $scope.ConfirmIGDocumentOpenCtrl = function () {
    $scope.loading = true;
    var changes = angular.toJson($rootScope.changes);
    var data = {"changes": changes, "igDocument": $rootScope.igdocument};
    $http.post('api/igdocuments/save', data, {timeout: 60000}).then(function (response) {
      var saveResponse = angular.fromJson(response.data);
      $rootScope.igdocument.metaData.date = saveResponse.date;
      $rootScope.igdocument.metaData.version = saveResponse.version;
      $scope.loading = false;
      $scope.clear();
    }, function (error) {
      $rootScope.msg().text = "igSaveFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;

      $scope.loading = false;
      $modalInstance.dismiss('cancel');
    });
  };
  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});

