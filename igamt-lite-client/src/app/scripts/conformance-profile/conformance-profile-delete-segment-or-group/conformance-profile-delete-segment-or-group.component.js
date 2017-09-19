/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DeleteSegmentRefOrGrpCtrl', function($scope, $mdDialog, segOrGrpToDelete, $rootScope, MessageService, blockUI) {
  $scope.segOrGrpToDelete = segOrGrpToDelete;
  $scope.loading = false;
  $scope.updatePosition = function(node) {
    angular.forEach(node.children, function(child) {
      child.position = node.children.indexOf(child) + 1;

    })

  };
  $scope.delete = function() {
    $scope.loading = true;
    blockUI.start();
    $scope.path = segOrGrpToDelete.path.replace(/\[[0-9]+\]/g, '');
    $scope.path = $scope.path.split(".");
    MessageService.deleteSegFromPath($scope.path, $rootScope.message).then(function() {
      if (segOrGrpToDelete.obj.type === 'group') {
        $rootScope.msg().text = "GrpDeleteSuccess";
      } else {
        $rootScope.msg().text = "SegmentRefDeleteSuccess";
      }


      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $rootScope.manualHandle = true;
      $scope.loading = false;
      $scope.updatePosition($rootScope.parentGroup);
      $rootScope.messageTree = null;
      $rootScope.processMessageTree($rootScope.message);
      blockUI.stop();
      $mdDialog.hide($scope.segOrGrpToDelete);
    }, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      $rootScope.manualHandle = true;
      $scope.loading = false;
      blockUI.stop();
    });


  };


  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };


});
