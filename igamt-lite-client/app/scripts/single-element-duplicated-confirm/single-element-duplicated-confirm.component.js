/**
 * Created by haffo on 9/11/17.
 */



angular.module('igl').controller('ConfirmSingleElementDuplicatedCtrl', function($scope, $mdDialog, $rootScope, selectedNode) {
  $scope.yes = function() {
    $mdDialog.hide(selectedNode);
  };

  $scope.no = function() {
    $mdDialog.hide('cancel');
  };
});
