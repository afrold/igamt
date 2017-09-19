/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function($scope, $mdDialog, dtToDelete, $rootScope, DatatypeLibrarySvc, DatatypeService, MastermapSvc, CloneDeleteSvc) {
  $scope.dtToDelete = dtToDelete;
  $scope.loading = false;
  $scope.delete = function() {
    $scope.loading = true;
    if ($scope.dtToDelete.scope === 'USER' && $scope.dtToDelete.status === 'UNPUBLISHED') {
      CloneDeleteSvc.deleteDatatypeAndDatatypeLink($scope.dtToDelete);
    } else {
      CloneDeleteSvc.deleteDatatypeLink($scope.dtToDelete);
    }

    $mdDialog.hide($scope.dtToDelete);
    $scope.loading = false;
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
