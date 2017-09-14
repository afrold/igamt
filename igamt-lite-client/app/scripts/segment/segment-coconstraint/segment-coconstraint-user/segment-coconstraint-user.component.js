/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('CoConstraintUSERDefinitionCtrl', function($scope, $mdDialog, coConstraintUSERDefinition) {
  $scope.coConstraintUSERDefinition = angular.copy(coConstraintUSERDefinition);
  $scope.title = null;

  if($scope.coConstraintUSERDefinition) {
    $scope.title = $scope.coConstraintUSERDefinition.title;
  }

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.close = function() {
    var userColumnDefinition = {};
    if($scope.coConstraintUSERDefinition) {
      userColumnDefinition = $scope.coConstraintUSERDefinition;
    }else {
      userColumnDefinition = {};
    }
    userColumnDefinition.title = $scope.title;

    $mdDialog.hide(userColumnDefinition);
  };
});
