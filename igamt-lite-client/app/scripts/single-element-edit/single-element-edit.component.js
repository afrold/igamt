/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('EditSingleElementCtrl', function($scope, $rootScope, $mdDialog, userInfoService, currentNode) {
  $scope.currentNode = currentNode;

  $scope.sevVale = '';

  if ($scope.currentNode.sev) $scope.sevVale = $scope.currentNode.sev.value;

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };

  $scope.close = function() {
    $mdDialog.hide($scope.sevVale);
  };
});
