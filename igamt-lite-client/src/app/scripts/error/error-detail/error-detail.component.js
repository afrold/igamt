/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ErrorDetailsCtrl', function ($scope, $modalInstance, error) {
  $scope.error = error;
  $scope.ok = function () {
    $modalInstance.close($scope.error);
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
