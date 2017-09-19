/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AbortPublishCtl', function($scope, $rootScope, $http, $modalInstance, datatypeToPublish, unpublishedDatatypes, unpublishedTables) {

  $scope.datatypeToPublish = datatypeToPublish;
  $scope.loading = false;
  $scope.unpublishedDatatypes = unpublishedDatatypes;
  $scope.unpublishedTables = unpublishedTables;

  $scope.delete = function() {
    $modalInstance.close($scope.datatypeToPublish);
  };

  $scope.cancel = function() {
    //$scope.datatypeToPublish.status = "'UNPUBLISHED'";
    $modalInstance.dismiss('cancel');
  };
});
