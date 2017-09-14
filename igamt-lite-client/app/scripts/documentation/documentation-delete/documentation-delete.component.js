/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('confirmDocumentationDeleteCtrl', function($scope, $rootScope, $http, $modalInstance, documentationToDelete,DocumentationService) {

  $scope.documentationtoDelete=documentationToDelete;
  $scope.ok = function() {

    $modalInstance.close($scope.documentationtoDelete);

  };
  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
