/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('DeleteComponentCtrl', function($scope, $modalInstance, componentToDelete, datatype, $rootScope, SegmentService, blockUI) {
  $scope.componentToDelete = componentToDelete;
  $scope.loading = false;
  $scope.updatePosition = function(node) {
    angular.forEach(node.components, function(component) {
      component.position = node.components.indexOf(component) + 1;

    })

  };
  $scope.delete = function() {
    blockUI.start();
    $scope.loading = true;
    datatype.components.splice(componentToDelete.position - 1, 1);


    $rootScope.msg().text = "ComponentDeleteSuccess";

    $rootScope.msg().type = "success";
    $rootScope.msg().show = true;
    $rootScope.manualHandle = true;
    $scope.loading = false;
    $scope.updatePosition(datatype);
    $modalInstance.close($scope.componentToDelete);
    blockUI.stop();

  };


  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };


});
