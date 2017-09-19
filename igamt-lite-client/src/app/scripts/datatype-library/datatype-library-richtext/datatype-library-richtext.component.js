/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('RichTextCtrlLIB', ['$scope', '$modalInstance', 'editorTarget', function($scope, $modalInstance, editorTarget) {
  $scope.editorTarget = editorTarget;

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };

  $scope.close = function() {
    $modalInstance.close($scope.editorTarget);
  };
}]);
