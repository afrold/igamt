/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('EditThenDataCtrl', function($scope, $rootScope, $mdDialog, userInfoService, currentId, currentIndex) {
  $scope.data = angular.copy($rootScope.segment.coConstraintsTable.thenMapData[currentId][currentIndex]);

  $scope.listOfBindingLocations = null;

  $scope.findOptions = function(dtId) {
    var result = [];
    result.push('1');

    if(!dtId || !$rootScope.datatypesMap[dtId]) return result;

    if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
        return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
      })) {
      var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

      var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

      if (bls && bls.length > 0) return bls;
    }

    return result;
  };

  $scope.isSelected = function(v) {
    if ($scope.data && $scope.data.valueSets) {
      for (var i = 0; i < $scope.data.valueSets.length; i++) {
        if ($scope.data.valueSets[i].tableId == v.id) return true;
      }
    }
    return false;
  };

  $scope.toggle=function (v) {
    if($scope.isSelected(v)){
      $scope.unselectValueSet(v);
    }else{
      $scope.selectValueSet(v);
    }

  }

  $scope.selectValueSet = function(v) {
    if (!$scope.data) $scope.data = {};
    if (!$scope.data.valueSets) $scope.data.valueSets = [];
    $scope.data.valueSets.push({ tableId: v.id, bindingStrength: "R" });
  };

  $scope.deleteValueSet = function(index) {
    if (index >= 0) {
      $scope.data.valueSets.splice(index, 1);
    }
  };

  $scope.unselectValueSet = function(v) {
    var toBeDelBinding = _.find($scope.data.valueSets, function(binding) {
      return binding.tableId == v.id;
    });
    var index = $scope.data.valueSets.indexOf(toBeDelBinding);
    if (index >= 0) {
      $scope.data.valueSets.splice(index, 1);
    }
  };

  $scope.columnDefinition = _.find($rootScope.segment.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
    return columnDefinition.id == currentId;
  });

  if ($scope.columnDefinition) {
    var dtId = $scope.columnDefinition.dtId;

    if ($rootScope.datatypesMap[dtId].name.toLowerCase() == 'varies') {
      var referenceColumnDefinition = _.find($rootScope.segment.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
        return columnDefinition.dMReference;
      });

      if (referenceColumnDefinition) {
        dtId = $rootScope.segment.coConstraintsTable.thenMapData[referenceColumnDefinition.id][currentIndex].datatypeId;
      }

      $scope.listOfBindingLocations = $scope.findOptions(dtId);
    } else {
      if (!$scope.columnDefinition.primitive) {
        $scope.listOfBindingLocations = $scope.findOptions(dtId);
      } else {
        $scope.listOfBindingLocations = null;
      }
    }
  } else {
    $scope.listOfBindingLocations = null;
  }

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };

  $scope.close = function() {
    $mdDialog.hide($scope.data);
  };
});
