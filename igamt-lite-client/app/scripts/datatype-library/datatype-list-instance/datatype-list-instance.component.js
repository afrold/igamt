/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('DatatypeListInstanceDlgCtl',
  function($scope, $rootScope, $modalInstance, hl7Version, datatypeLibsStruct, DatatypeLibrarySvc, DatatypeService) {

    $scope.hl7Version = hl7Version;
    $scope.datatypesLibStruct = datatypeLibsStruct;
    $scope.selectedLib;
    $scope.dtSelections = [];

    $scope.trackSelections = function(bool, event) {
      if (bool) {
        $scope.dtSelections.push(event);
      } else {
        for (var i = 0; i < $scope.dtSelections.length; i++) {
          if ($scope.dtSelections[i].id === event.id) {
            $scope.dtSelections.splice(i, 1);
          }
        }
      }
      $scope.okDisabled = $scope.dtSelections.length === 0;
    };

    $scope.libSelected = function(datatypeLib) {
      $scope.selectedLib = datatypeLib;
    };

    $scope.ok = function() {
      $modalInstance.close($scope.dtSelections);
    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };
  });
