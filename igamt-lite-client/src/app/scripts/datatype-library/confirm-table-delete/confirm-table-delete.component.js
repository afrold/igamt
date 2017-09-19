/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmTablesDeleteCtl', function($scope, $rootScope, $http, $modalInstance, tableToDelete) {

  $scope.tableToDelete = tableToDelete;
  $scope.loading = false;

  $scope.delete = function() {
    $modalInstance.close($scope.tableToDelete);
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
