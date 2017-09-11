/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('confirmSwitch', function($scope, $rootScope, $http, $modalInstance, source, dest) {

  $scope.source = source;
  $scope.dest = dest;
  $scope.confirm = function() {
    $modalInstance.close();
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
