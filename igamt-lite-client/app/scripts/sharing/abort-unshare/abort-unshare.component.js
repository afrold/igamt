/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('abortUnshare', function($scope, $rootScope, $http, $modalInstance,datatypeTo) {
  $scope.datatypeTo=datatypeTo;
  $scope.getMessage=function(){
    if($scope.datatypeTo.type==='table'){
      return "Value Set";
    }else{
      return "Data Type"
    }
  };
  $scope.ok = function() {
    $modalInstance.dismiss('cancel');
  };
});
