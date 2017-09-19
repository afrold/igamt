/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('MessageRowCtrl', function($scope) {
  $scope.formName = "form_" + new Date().getTime();


  //        $scope.init = function(){
  //            $scope.$watch(function(){
  //            return  $scope.formName.$dirty;
  //        }, function(newValue, oldValue) {
  //            $scope.editForm.$dirty = newValue !=null &&  oldValue != null;
  //        });
  //
  //        }

});
