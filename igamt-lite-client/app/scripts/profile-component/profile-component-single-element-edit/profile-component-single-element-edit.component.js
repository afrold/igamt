/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('EditSingleElementCtrlInPc', function($scope, $rootScope, $mdDialog, userInfoService, currentNode) {
  $scope.currentNode = currentNode;

  $scope.sevVale = '';
  if (currentNode.singleElementValues && currentNode.singleElementValues.value !== null && currentNode.singleElementValues.location !== null) {
    $scope.sevVale = currentNode.singleElementValues.value;

  } else if (currentNode.oldSingleElementValues && currentNode.oldSingleElementValues.value !== null && currentNode.oldSingleElementValues.location !== null) {
    $scope.sevVale = currentNode.oldSingleElementValues.value;

  }


  $scope.cancel = function() {
    $mdDialog.hide("cancel");
  };

  $scope.close = function() {
    $mdDialog.hide($scope.sevVale);
  };
});
