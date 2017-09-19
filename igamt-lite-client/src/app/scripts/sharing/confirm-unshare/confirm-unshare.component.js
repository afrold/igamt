/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('confirmUnshare', function($scope, $rootScope, $http, $modalInstance,datatypeTo) {
  $scope.datatypeTo=datatypeTo;
  $scope.getMessage=function(){
    if($scope.datatypeTo.type==='table'){
      return "Value Set";
    }else{
      return "Data Type"
    }
  };

  $scope.confirm = function() {
    $modalInstance.close($scope.datatypeTo);
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
