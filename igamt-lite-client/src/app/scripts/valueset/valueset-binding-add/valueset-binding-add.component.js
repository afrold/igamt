/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('AddBindingForValueSet', function($scope, $modalInstance, $rootScope, table) {
  console.log($rootScope.references);
  $scope.table = table;
  $scope.componentsToSelect=[];
  $scope.selectedSegmentForBinding = null;
  $scope.selectedFieldForBinding = null;
  $scope.selectedDatatypeForBinding = null;
  $scope.selectedComponentForBinding = null;
  $scope.selectedBindingLocation = null;
  $scope.selectedBindingStrength = null;
  $scope.pathForBinding = null;
  $scope.bindingTargetType ='DATATYPE';


  $scope.init = function() {
    // $scope.selectedSegmentForBinding = null;
    // $scope.selectedFieldForBinding = null;
    // $scope.selectedDatatypeForBinding = null;
    // $scope.selectedComponentForBinding = null;
    // $scope.selectedBindingLocation = null;
    // $scope.selectedBindingStrength = null;
    // $scope.pathForBinding = null;
  };
  $scope.switchTo=function(type){
    $scope.bindingTargetType=type;
  }
  $scope.checkDuplicated = function(path) {
    for (var i = 0; i < $rootScope.references.length; i++) {
      var ref = $rootScope.references[i];
      if (ref.path == path) return true;
    }
    return false;
  };

  $scope.selectSegment = function() {
    $scope.selectedFieldForBinding = null;
  };

  $scope.selectDatatype = function(dt) {
    $scope.selectedDatatypeForBinding = JSON.parse(dt);
    console.log($scope.selectedDatatypeForBinding);
    $scope.componentsToSelect=$scope.selectedDatatypeForBinding.components

    //$scope.selectedComponentForBinding = null;
  };
  $scope.selectComponent=function(c){
    $scope.selectedComponentForBinding=c;

  };

  $scope.save = function(bindingTargetType) {
    var tableLink = {};
    tableLink.id = $scope.table.id;
    tableLink.bindingIdentifier = $scope.table.bindingIdentifier;
    tableLink.bindingLocation = $scope.selectedBindingLocation;
    tableLink.bindingStrength = $scope.selectedBindingStrength;
    tableLink.isChanged = true;
    tableLink.isNew = true;

    if (bindingTargetType == 'SEGMENT') {
      $scope.selectedFieldForBinding = JSON.parse($scope.selectedFieldForBinding);
      $scope.pathForBinding = $rootScope.getSegmentLabel($scope.selectedSegmentForBinding) + '-' + $scope.selectedFieldForBinding.position;

      var ref = angular.copy($scope.selectedFieldForBinding);
      ref.path = $scope.pathForBinding;
      ref.target = angular.copy($scope.selectedSegmentForBinding);
      ref.tableLink = angular.copy(tableLink);
      $rootScope.references.push(ref);
    } else {
      $scope.selectedComponentForBinding = JSON.parse($scope.selectedComponentForBinding);
      $scope.pathForBinding = $rootScope.getDatatypeLabel($scope.selectedDatatypeForBinding) + '-' + $scope.selectedComponentForBinding.position;

      var ref = angular.copy($scope.selectedComponentForBinding);
      ref.path = $scope.pathForBinding;
      ref.target = angular.copy($scope.selectedDatatypeForBinding);
      ref.tableLink = angular.copy(tableLink);
      $rootScope.references.push(ref);
    }

    $modalInstance.close();
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };
});
