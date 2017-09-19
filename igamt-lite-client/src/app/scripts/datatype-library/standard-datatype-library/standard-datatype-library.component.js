/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
  function($scope, $rootScope, $modalInstance, $timeout, hl7Versions, DatatypeLibrarySvc, DatatypeService) {

    $scope.okDisabled = true;

    $scope.scope = "HL7STANDARD";
    $scope.hl7Versions = hl7Versions;
    $scope.standard = {};
    $scope.standard.hl7Version = null;
    $scope.name = null;
    $scope.standard.ext = null;

    $scope.getDisplayLabel = function(dt) {
      if (dt) {
        return dt.label;
      }
    }

    $scope.ok = function() {
      $modalInstance.close($scope.standard);
    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

  });
