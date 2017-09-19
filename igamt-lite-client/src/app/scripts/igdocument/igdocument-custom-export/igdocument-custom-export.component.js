/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('CustomExportCtrl', function ($scope, $modalInstance, $http, IgDocumentService, $rootScope) {
  $scope.selectedType = {};
  $scope.exportType = [{
    type: "XML",
    layout: []
  }, {
    type: "Word",
    layout: ["Compact", "Verbose"]
  }, {
    type: "HTML",
    layout: ["Compact", "Verbose"]
  }];

  $scope.selectedLayout = {};


  $scope.ok = function () {
    if ($scope.selectedType.selected) {
      if ($scope.selectedType.selected === "XML") {
        $scope.exportAs($scope.selectedType.selected);
      } else {
        if ($scope.selectedLayout.selected) {
          $scope.exportAsWithLayout($scope.selectedType.selected, $scope.selectedLayout.selected);
        } else {
          $scope.exportAs($scope.selectedType.selected);
        }
      }
    }
    $modalInstance.hide();
  };
  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
