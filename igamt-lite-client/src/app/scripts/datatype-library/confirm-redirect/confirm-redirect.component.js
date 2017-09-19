/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmRedirect', function($scope, $rootScope, $http, $modalInstance, datatypeTo) {

  $scope.datatypeTo = datatypeTo;
  $scope.loading = false;

  $scope.delete = function() {
    $modalInstance.close($scope.datatypeTo);
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});

