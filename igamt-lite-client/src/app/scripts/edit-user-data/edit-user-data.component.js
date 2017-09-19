/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('EditUserDataCtrl', function($scope, $rootScope, $modalInstance, userInfoService, definition, text, disabled) {
  $scope.definition = definition;
  $scope.textData = text;
  $scope.disabled = disabled;

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };

  $scope.close = function() {
    $modalInstance.close($scope.textData);
  };

});
