/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('DeleteDatatypePredicateCtrl', function($scope, $mdDialog, position, datatype, $rootScope) {
  $scope.selectedDatatype = datatype;
  $scope.position = position;
  $scope.delete = function() {
    for (var i = 0, len1 = $scope.selectedDatatype.predicates.length; i < len1; i++) {
      if ($scope.selectedDatatype.predicates[i].constraintTarget.indexOf($scope.position + '[') === 0) {
        $scope.selectedDatatype.predicates.splice($scope.selectedDatatype.predicates.indexOf($scope.selectedDatatype.predicates[i]), 1);
          $mdDialog.hide('ok');
        return;
      }
    }
      $mdDialog.hide('ok');
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
