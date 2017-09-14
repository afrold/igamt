/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('ConfirmLogoutCtrl', ["$scope", "$mdDialog", "$rootScope", "$http", function($scope, $mdDialog, $rootScope, $http) {
  $scope.logout = function() {
    $mdDialog.hide(true);
  };

  $scope.cancel = function() {
    $mdDialog.hide(false);
  };
}]);
