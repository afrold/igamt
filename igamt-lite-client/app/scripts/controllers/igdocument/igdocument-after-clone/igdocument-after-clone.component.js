/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AfterClonedIgCtrl', ['$rootScope', '$scope', '$mdDialog', 'clonedIgDocument', function ($rootScope, $scope, $mdDialog, clonedIgDocument) {
  $scope.clonedIgDocument = clonedIgDocument;

  $scope.cancel = function () {
    $mdDialog.hide();
  };

  $scope.edit = function () {
    // ////console.log("logging in...");
    $mdDialog.hide($scope.clonedIgDocument);
  };
}]);

