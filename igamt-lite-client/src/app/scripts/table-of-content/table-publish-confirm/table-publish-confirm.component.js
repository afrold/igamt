/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfirmTablePublishCtl', function($scope, $rootScope, $http, $modalInstance, tableToPublish) {

  $scope.tableToPublish = tableToPublish;
  $scope.loading = false;

  $scope.confirm = function() {
    console.log("confirming")

    $modalInstance.close($scope.tableToPublish);
  };

  $scope.cancel = function() {
    $scope.tableToPublish.status = "UNPUBLISHED";
    $modalInstance.dismiss('cancel');
  };
});
