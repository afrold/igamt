/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('FailureCtrl', [ '$scope', '$modalInstance', 'StorageService', '$window', 'error',
  function ($scope, $modalInstance, StorageService, $window, error) {
    $scope.error = error;
    $scope.close = function () {
      $modalInstance.close();
    };
  }
]);
