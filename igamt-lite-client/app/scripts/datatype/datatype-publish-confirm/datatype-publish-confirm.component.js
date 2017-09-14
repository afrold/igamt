/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfirmDatatypePublishCtlMd', function($scope, $rootScope, $http, $mdDialog, datatypeToPublish) {

  $scope.datatypeToPublish = datatypeToPublish;
  $scope.loading = false;

  $scope.confirm = function() {
    $mdDialog.hide($scope.datatypeToPublish);
  };

  $scope.cancel = function() {
    $scope.datatypeToPublish.status = 'UNPUBLISHED';
    $rootScope.clearChanges();
    $mdDialog.hide('cancel');
  };
});
