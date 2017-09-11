/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('SeeCoConDlgCtl', function($scope, $rootScope, $mdDialog, node, context, TableService) {
  $scope.node = angular.copy(node);
  console.log($scope.node);
  $scope.coConstraintsTable = $scope.node.ref.coConstraintsTable;
  $scope.seeOrEdit = context;


  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
