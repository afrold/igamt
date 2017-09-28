/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('EditUserDataCtrl', function($scope, $rootScope, $mdDialog, userInfoService, definition, text, disabled) {
  $scope.definition = definition;
  $scope.textData = text;
  $scope.disabled = disabled;

  $scope.cancel = function() {
      $mdDialog.hide();
  };

  $scope.close = function() {
      $mdDialog.hide($scope.textData);
  };

});
