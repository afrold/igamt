/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('cannotPublish', function($scope, $rootScope, $http, $modalInstance, datatype, derived) {

  $scope.datatypeTo = datatype;
  $scope.delete = function() {
    $modalInstance.close($scope.datatypeTo);
  };

  $scope.cancel = function() {
    console.log("sssss");
    $modalInstance.dismiss('cancel');
  };
});
