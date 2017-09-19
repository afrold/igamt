/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('InputTextCtrl', ['$scope', '$mdDialog', 'editorTarget', function($scope, $mdDialog, editorTarget) {
  $scope.editorTarget = editorTarget;

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.close = function() {
    $mdDialog.hide($scope.editorTarget);
  };
}]);
