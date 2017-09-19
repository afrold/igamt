/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfirmDatatypeLibraryDeleteCtrl', function($scope, $rootScope, $http, $mdDialog, datatypeLibraryToDelete) {

  $rootScope.datatypeLibraryToDelete = datatypeLibraryToDelete;
  $scope.loading = false;

  $scope.delete = function() {
    $mdDialog.hide($rootScope.datatypeLibraryToDelete);
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
