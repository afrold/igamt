/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ConfirmSegmentDeleteCtrl', function($scope, $mdDialog, segToDelete, $rootScope, SegmentService, SegmentLibrarySvc, MastermapSvc, CloneDeleteSvc) {
  $scope.segToDelete = segToDelete;
  $scope.loading = false;

  $scope.delete = function() {
    $scope.loading = true;
    if ($scope.segToDelete.scope === 'USER') {
      CloneDeleteSvc.deleteSegmentAndSegmentLink($scope.segToDelete);
    } else {
      CloneDeleteSvc.deleteSegmentLink($scope.segToDelete);
    }
    $mdDialog.hide($scope.segToDelete);
    $scope.loading = false;
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
