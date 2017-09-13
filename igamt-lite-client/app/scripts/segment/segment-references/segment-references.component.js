/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('SegmentReferencesCtrl', function($scope, $mdDialog, segToDelete) {

  $scope.segToDelete = segToDelete;

  $scope.ok = function() {
    $mdDialog.hide($scope.segToDelete);
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
