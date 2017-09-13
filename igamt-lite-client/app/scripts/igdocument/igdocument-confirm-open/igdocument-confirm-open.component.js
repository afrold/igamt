/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmIGDocumentOpenCtrl', function ($scope, $mdDialog, igdocumentToOpen, $rootScope, $http) {
  $scope.igdocumentToOpen = igdocumentToOpen;
  $scope.loading = false;

  $scope.discardChangesAndOpen = function () {
    $scope.loading = true;
    $http.get('api/igdocuments/' + $rootScope.igdocument.id, {timeout: 60000}).then(function (response) {
      var index = $rootScope.igs.indexOf($rootScope.igdocument);
      $rootScope.igs[index] = angular.fromJson(response.data);
      $scope.loading = false;
      $mdDialog.hide($scope.igdocumentToOpen);
    }, function (error) {
      $scope.loading = false;
      $rootScope.msg().text = "igResetFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;

      $modalInstance.dismiss('cancel');
    });
  };

  $scope.saveChangesAndOpen = function () {
    $scope.loading = true;
    var changes = angular.toJson($rootScope.changes);
    var data = {"changes": changes, "igDocument": $rootScope.igdocument};
    $http.post('api/igdocuments/save', data, {timeout: 60000}).then(function (response) {
      var saveResponse = angular.fromJson(response.data);
      $rootScope.igdocument.metaData.date = saveResponse.date;
      $rootScope.igdocument.metaData.version = saveResponse.version;
      $scope.loading = false;
      $mdDialog.hide($scope.igdocumentToOpen);
    }, function (error) {
      $rootScope.msg().text = "igSaveFailed";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      $scope.loading = false;
      $mdDialog.hide('cancel');
    });
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
