/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SeeConfStDlgCtl', function($scope, $rootScope, $mdDialog, node, context) {

  console.log(node);
  if (node.type === "group") {
    $scope.selectedContextNode = node;

  } else if (node.type === "segmentRef") {
    $scope.selectedContextNode = $rootScope.compositeProfile.segmentsMap[node.ref.id];

  }
  $scope.seeOrEdit = context;


  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
