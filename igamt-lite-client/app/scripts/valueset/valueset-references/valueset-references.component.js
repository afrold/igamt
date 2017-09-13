/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('ValueSetReferencesCtrl', function($scope, $mdDialog, tableToDelete) {

  $scope.tableToDelete = tableToDelete;

  $scope.ok = function() {
    $mdDialog.hide($scope.tableToDelete);
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };
});
