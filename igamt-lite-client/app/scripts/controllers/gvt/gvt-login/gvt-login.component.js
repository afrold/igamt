/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('GVTLoginCtrl', ['$scope', 'user', 'GVTSvc', '$mdDialog', function($scope, user, GVTSvc, $mdDialog) {
  $scope.user = user;
  $scope.error = { text: undefined, show: false };
  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.login = function() {
    $scope.error = { text: undefined, show: false };
    GVTSvc.login($scope.user.username, $scope.user.password).then(function(auth) {
      $mdDialog.hide(auth);
    }, function(error) {
      $scope.error.text = error.data.text;
      $scope.error.show = true;
      $scope.error.type = 'danger';

    });
  };
}]);
