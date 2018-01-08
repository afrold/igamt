/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('AddDynamicMappingCtrlInPc', function($scope, $mdDialog, node, context, $rootScope, TableService) {
  $scope.node = angular.copy(node);
  console.log($rootScope.dynamicMappingTable);
  $scope.changed = false;
  $scope.setChanged  = function() {
    $scope.changed = true;
  };
  $scope.findDynamicMapping = function(node) {
    if (node.type === "segmentRef") {
      if (node.attributes.dynamicMappingDefinition && node.attributes.dynamicMappingDefinition.dynamicMappingItems.length > 0) {
        return node.attributes.dynamicMappingDefinition;
      } else {
        return node.attributes.oldDynamicMappingDefinition;
      }
    }
    return null;
  };
  $scope.findingBindings = function(node) {
    if (node.type === "segmentRef") {
      if (node.valueSetBindings && node.valueSetBindings.length > 0) {
        return node.valueSetBindings;
      } else {
        return node.oldValueSetBindings;
      }
    }
    return null;
  };
  $scope.updateDynamicMappingInfo = function() {
    $scope.isDynamicMappingSegment = false;
    $scope.dynamicMappingTable = null;

    var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
      return item.hl7Version == $rootScope.segmentsMap[$scope.node.attributes.ref.id].hl7Version && item.segmentName == $rootScope.segmentsMap[$scope.node.attributes.ref.id].name;
    });

    if (mappingStructure) {
      $rootScope.isDynamicMappingSegment = true;
      console.log("=========This is DM segment!!=========");

      if ($scope.findDynamicMapping($scope.node) && $scope.findDynamicMapping($scope.node).mappingStructure) {
        console.log("=========Found mapping structure!!=========");
        mappingStructure = $scope.findDynamicMapping($scope.node).mappingStructure;
      } else {
        console.log("=========Not Found mapping structure and Default setting will be used!!=========");
      }

      var valueSetBinding = _.find($scope.findingBindings($scope.node), function(vsb) {
        return vsb.location == mappingStructure.referenceLocation;
      });

      if (valueSetBinding) {
        TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
          $scope.dynamicMappingTable = tbl;
        }, function() {

        });
      }
    }
  };
  $scope.updateDynamicMappingInfo();



  $scope.dynamicMappingDefinition = $scope.findDynamicMapping($scope.node);
  $scope.deleteMappingItem = function(item) {
    var index = $scope.dynamicMappingDefinition.dynamicMappingItems.indexOf(item);
    if (index >= 0) {
      $scope.dynamicMappingDefinition.dynamicMappingItems.splice(index, 1);
      $scope.setChanged();
    }
  };

  $scope.addMappingItem = function() {
    var newItem = {};
    newItem.firstReferenceValue = null;
    newItem.secondReferenceValue = null;
    newItem.datatypeId = null;
    $scope.dynamicMappingDefinition.dynamicMappingItems.push(newItem);
    $scope.setChanged();
  };

  $scope.getDefaultStatus = function(code) {
    var item = _.find($scope.dynamicMappingDefinition.dynamicMappingItems, function(item) {
      return item.firstReferenceValue == code.value;
    });

    if (!item) return 'full';
    if (item) {
      if (item.secondReferenceValue && item.secondReferenceValue != '') return 'partial';
      return 'empty';
    }
  };

  console.log($scope.node);
  $scope.cancel = function() {
    $mdDialog.hide();
  }
  $scope.saveclose = function() {
    $mdDialog.hide($scope.dynamicMappingDefinition);
  }

});
