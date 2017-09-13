/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DatatypeReferencesCtrlMd', function($scope, $mdDialog, dtToDelete,refs, $rootScope) {

  $scope.dtToDelete = dtToDelete
  $scope.refs=refs;
  $scope.crossRefsForDelete=refs;


  $scope.ok = function() {
    $mdDialog.hide($scope.dtToDelete);
  };

  $scope.cancel = function() {
    console.log(refs);
    $mdDialog.hide('cancel');
  };
});
