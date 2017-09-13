/**
 * Created by haffo on 9/11/17.
 */



angular.module('igl').controller('ConfirmSingleElementDuplicatedCtrl', function($scope, $modalInstance, $rootScope, selectedNode) {
  $scope.yes = function() {
    $modalInstance.close(selectedNode);
  };

  $scope.no = function() {
    $modalInstance.dismiss('cancel');
  };
});
