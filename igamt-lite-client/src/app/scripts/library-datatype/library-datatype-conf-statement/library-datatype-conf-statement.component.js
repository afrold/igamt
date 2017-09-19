/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ConformanceStatementDatatypeCtrlForLib', function($scope, $modalInstance, selectedNode, $rootScope) {
  $scope.constraintType = 'Plain';
  $scope.selectedNode = selectedNode;
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.libEXT, $scope.datatype.name + "_" + $scope.datatype.ext);
  $scope.newComplexConstraint = [];
  $scope.constraints = [];

  $scope.changed = false;
  $scope.tempComformanceStatements = [];
  angular.copy($scope.datatype.conformanceStatements, $scope.tempComformanceStatements);


  $scope.setChanged = function() {
    $scope.changed = true;
  }

  $scope.initConformanceStatement = function() {
    $scope.newConstraint = angular.fromJson({
      position_1: null,
      position_2: null,
      location_1: null,
      location_2: null,
      datatype: '',
      component_1: null,
      subComponent_1: null,
      component_2: null,
      subComponent_2: null,
      freeText: null,
      verb: null,
      ignoreCase: false,
      constraintId: $rootScope.calNextCSID($rootScope.libEXT, $scope.datatype.name + "_" + $scope.datatype.ext),
      contraintType: null,
      value: null,
      value2: null,
      valueSetId: null,
      bindingStrength: 'R',
      bindingLocation: '1'
    });
    $scope.newConstraint.datatype = $rootScope.datatype.name;
  }

  $scope.initComplexStatement = function() {
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.libEXT, $scope.datatype.name + "_" + $scope.datatype.ext);
  }

  $scope.initConformanceStatement();

  $scope.deleteConformanceStatement = function(conformanceStatement) {
    $rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf($scope.tempComformanceStatements.constraintId), 1);
    $scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
    $scope.changed = true;
  };

  $scope.updateComponent_1 = function() {
    $scope.newConstraint.subComponent_1 = null;
  };

  $scope.updateComponent_2 = function() {
    $scope.newConstraint.subComponent_2 = null;
  };

  $scope.genLocation = function(datatype, component, subComponent) {
    var location = null;
    if (component != null && subComponent == null) {
      location = datatype + '.' + component.position + "(" + component.name + ")";
    } else if (component != null && subComponent != null) {
      location = datatype + '.' + component.position + '.' + subComponent.position + "(" + subComponent.name + ")";
    }

    return location;
  };

  $scope.genPosition = function(component, subComponent) {
    var position = null;
    if (component != null && subComponent == null) {
      position = component.position + '[1]';
    } else if (component != null && subComponent != null) {
      position = component.position + '[1]' + '.' + subComponent.position + '[1]';
    }

    return position;
  };

  $scope.addComplexConformanceStatement = function() {
    $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    if ($rootScope.conformanceStatementIdList.indexOf($scope.complexConstraint.constraintId) == -1) $rootScope.conformanceStatementIdList.push($scope.complexConstraint.constraintId);
    $scope.tempComformanceStatements.push($scope.complexConstraint);
    $scope.initComplexStatement();
    $scope.changed = true;
  };

  $scope.addFreeTextConformanceStatement = function() {
    $rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
    var cs = null;
    if ($scope.selectedNode === null) {
      var cs = $rootScope.generateFreeTextConformanceStatement(".", $scope.newConstraint);
    } else {
      var cs = $rootScope.generateFreeTextConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
    }
    $scope.tempComformanceStatements.push(cs);
    $scope.changed = true;
    if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    $scope.initConformanceStatement();
  };

  $scope.addConformanceStatement = function() {
    $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
    $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
    $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
    $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

    if ($scope.newConstraint.position_1 != null) {
      $rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
      var cs = $rootScope.generateConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
      $scope.tempComformanceStatements.push(cs);
      $scope.changed = true;
      if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    }
    $scope.initConformanceStatement();
  };

  $scope.ok = function() {
    angular.forEach($scope.tempComformanceStatements, function(cs) {
      $rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf(cs.constraintId), 1);
    });

    angular.forEach($rootScope.datatype.conformanceStatements, function(cs) {
      if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    });

    $modalInstance.close($scope.selectedNode);
  };

  $scope.saveclose = function() {
    angular.forEach($scope.tempComformanceStatements, function(cs) {
      if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    });
    angular.copy($scope.tempComformanceStatements, $rootScope.datatype.conformanceStatements);
    $rootScope.recordChanged();
    $modalInstance.close($scope.selectedNode);
  };
});
