/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('confirmDocumentationDeleteCtrl', function($scope, $rootScope, $http, $mdDialog, documentationToDelete) {

  $scope.documentationtoDelete=documentationToDelete;
  $scope.ok = function() {

    $mdDialog.hide($scope.documentationtoDelete);

  };
  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };
});
