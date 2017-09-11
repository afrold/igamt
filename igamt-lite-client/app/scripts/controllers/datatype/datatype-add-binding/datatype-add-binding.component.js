/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddBindingForDatatype', function($scope, $modalInstance, $rootScope, datatype) {
  $scope.datatype = datatype;
  $scope.selectedSegmentForBinding = null;
  $scope.selectedFieldForBinding = null;
  $scope.selectedDatatypeForBinding = null;
  $scope.selectedComponentForBinding = null;

  $scope.pathForBinding = null;
  $scope.bindingTargetType = 'DATATYPE';

  $scope.init = function() {
    $scope.selectedSegmentForBinding = null;
    $scope.selectedFieldForBinding = null;
    $scope.selectedDatatypeForBinding = null;
    $scope.selectedComponentForBinding = null;
    $scope.pathForBinding = null;
    $scope.currentField = null;
    $scope.currentComp = null;

  };

  $scope.checkDuplicated = function(path) {
    for (var i = 0; i < $rootScope.references.length; i++) {
      var ref = $rootScope.references[i];
      if (ref.path==path) return true;
    }
    return false;
  };

  $scope.selectSegment = function() {
    $scope.selectedFieldForBinding = null;
    $scope.currentField = null;
  };
  $scope.selectField = function() {
    if ($scope.selectedFieldForBinding) {
      $scope.currentField = JSON.parse($scope.selectedFieldForBinding);
    }
  };
  $scope.selectComp = function() {
    if ($scope.selectedComponentForBinding) {
      $scope.currentComp = JSON.parse($scope.selectedComponentForBinding);

    }
  };


  $scope.selectDatatype = function() {
    $scope.selectedComponentForBinding = null;
    $scope.currentComp = null;
  };

  $scope.save = function(bindingTargetType) {
    var datatypeLink = {};
    datatypeLink.id = $scope.datatype.id;
    datatypeLink.name = $scope.datatype.bindingIdentifier;
    datatypeLink.ext = $scope.datatype.ext;
    datatypeLink.label = $scope.datatype.label;
    datatypeLink.isChanged = true;
    datatypeLink.isNew = true;

    if (bindingTargetType == 'SEGMENT') {
      $scope.selectedFieldForBinding = JSON.parse($scope.selectedFieldForBinding);
      $scope.pathForBinding = $rootScope.getSegmentLabel($scope.selectedSegmentForBinding) + '-' + $scope.selectedFieldForBinding.position;

      var ref = angular.copy($scope.selectedFieldForBinding);
      ref.path = $scope.pathForBinding;
      ref.target = angular.copy($scope.selectedSegmentForBinding);
      ref.datatypeLink = angular.copy(datatypeLink);
      $rootScope.references.push(ref);
    } else {
      $scope.selectedComponentForBinding = JSON.parse($scope.selectedComponentForBinding);
      $scope.pathForBinding = $rootScope.getDatatypeLabel($scope.selectedDatatypeForBinding) + '-' + $scope.selectedComponentForBinding.position;

      var ref = angular.copy($scope.selectedComponentForBinding);
      ref.path = $scope.pathForBinding;
      ref.target = angular.copy($scope.selectedDatatypeForBinding);
      ref.datatypeLink = angular.copy(datatypeLink);
      $rootScope.references.push(ref);
    }

    $modalInstance.close();
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
