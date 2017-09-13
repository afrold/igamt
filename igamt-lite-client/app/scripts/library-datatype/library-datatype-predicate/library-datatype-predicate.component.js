/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('PredicateDatatypeCtrlForLib', function($scope, $modalInstance, selectedNode, $rootScope) {
  $scope.constraintType = 'Plain';
  $scope.selectedNode = selectedNode;
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.complexConstraintTrueUsage = null;
  $scope.complexConstraintFalseUsage = null;

  $scope.changed = false;
  $scope.tempPredicates = [];
  angular.copy($rootScope.datatype.predicates, $scope.tempPredicates);


  $scope.countPredicateForTemp = function() {
    var count = 0;

    for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
      if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
        count = count + 1;
    }
    return count;
  };


  $scope.setChanged = function() {
    $scope.changed = true;
  }

  $scope.initPredicate = function() {
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
      verb: null,
      freeText: null,
      contraintType: null,
      value: null,
      ignoreCase: false,
      value2: null,
      trueUsage: null,
      falseUsage: null,
      valueSetId: null,
      bindingStrength: 'R',
      bindingLocation: '1'
    });
    $scope.newConstraint.datatype = $rootScope.datatype.name;
  }

  $scope.initComplexPredicate = function() {
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;
  }

  $scope.initPredicate();


  $scope.deletePredicate = function(predicate) {
    $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
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


  $scope.deletePredicateByTarget = function() {
    for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
      if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
        $scope.deletePredicate($scope.tempPredicates[i]);
        return true;
      }
    }
    return false;
  };

  $scope.addComplexPredicate = function() {
    $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
    $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;

    if ($scope.selectedNode === null) {
      $scope.complexConstraint.constraintId = '.';
    } else {
      $scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
    }

    $scope.tempPredicates.push($scope.complexConstraint);
    $scope.initComplexPredicate();
    $scope.changed = true;
  };

  $scope.addFreeTextPredicate = function() {
    $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
    var cp = null;
    if ($scope.selectedNode === null) {
      var cp = $rootScope.generateFreeTextPredicate(".", $scope.newConstraint);
    } else {
      var cp = $rootScope.generateFreeTextPredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
    }

    $scope.tempPredicates.push(cp);
    $scope.changed = true;
    $scope.initPredicate();
  };

  $scope.addPredicate = function() {

    $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

    $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
    $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
    $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
    $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

    if ($scope.newConstraint.position_1 != null) {
      var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
      $scope.tempPredicates.push(cp);
      $scope.changed = true;
    }
    $scope.initPredicate();
  };

  $scope.ok = function() {
    $modalInstance.close($scope.selectedNode);
  };

  $scope.saveclose = function() {
    angular.copy($scope.tempPredicates, $rootScope.datatype.predicates);
    $rootScope.recordChanged();
    $modalInstance.close($scope.selectedNode);
  };
});
