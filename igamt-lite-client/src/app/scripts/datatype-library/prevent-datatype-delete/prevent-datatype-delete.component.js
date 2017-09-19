/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('PreventDatatypeDeleteCtl', function($scope, $rootScope, $http, $modalInstance, datatypeToDelete) {

  $scope.datatypeToDelete = datatypeToDelete;
  $scope.loading = false;

  $scope.delete = function() {
    $modalInstance.close($scope.datatypeToDelete);
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
