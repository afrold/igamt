/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('redirectCtrl', function($scope, $mdDialog, destination, $rootScope) {
  $scope.destination = destination;
  $scope.loading = false;

  $scope.confirm = function() {
    $mdDialog.hide($scope.destination);
  };


  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };


});
