/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ErrorCtrl', [ '$scope', '$modalInstance', 'StorageService', '$window',
  function ($scope, $modalInstance, StorageService, $window) {
    $scope.refresh = function () {
      $modalInstance.close($window.location.reload());
    };
  }
]);
