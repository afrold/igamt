/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('DeleteSegmentPredicateCtrl', function($scope, $mdDialog, position, segment, $rootScope) {
  $scope.selectedSegment = segment;
  $scope.position = position;
  $scope.delete = function() {
    for (var i = 0, len1 = $scope.selectedSegment.predicates.length; i < len1; i++) {
      if ($scope.selectedSegment.predicates[i].constraintTarget.indexOf($scope.position + '[') === 0) {
        $scope.selectedSegment.predicates.splice($scope.selectedSegment.predicates.indexOf($scope.selectedSegment.predicates[i]), 1);
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
