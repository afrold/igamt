/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('DeleteFieldCtrl', function($scope, $mdDialog, fieldToDelete, segment, $rootScope, SegmentService, blockUI) {
  $scope.fieldToDelete = fieldToDelete;
  $scope.loading = false;
  $scope.updatePosition = function(node) {
    angular.forEach(node.fields, function(field) {
      field.position = node.fields.indexOf(field) + 1;

    })

  };
  $scope.delete = function() {
    blockUI.start();
    $scope.loading = true;
    segment.fields.splice(fieldToDelete.position - 1, 1);


    $rootScope.msg().text = "FieldDeleteSuccess";

    $rootScope.msg().type = "success";
    $rootScope.msg().show = true;
    $rootScope.manualHandle = true;
    $scope.loading = false;
    $scope.updatePosition(segment);
    blockUI.stop();
    $mdDialog.hide($scope.fieldToDelete);
  };


  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };


});
