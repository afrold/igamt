/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('LoginCtrl', [ '$rootScope','$scope', '$mdDialog', 'user', function($rootScope,$scope, $mdDialog, user) {
  $scope.user = user;

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.login = function() {
    // ////console.log("logging in...");
    $mdDialog.hide($scope.user);
  };
}]);
