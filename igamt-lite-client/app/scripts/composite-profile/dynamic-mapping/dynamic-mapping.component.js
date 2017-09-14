/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SeeDynMapDlgCtl', function($scope, $rootScope, $mdDialog, node, context, TableService) {
  $scope.node = angular.copy(node);
  console.log($scope.node);
  $scope.seeOrEdit = context;
  $scope.findDynamicMapping = function(node) {
    if (node.type === "segmentRef") {

      return node.ref.dynamicMappingDefinition;

    }
    return null;
  };
  $scope.findingBindings = function(node) {
    if (node.type === "segmentRef") {
      if (node.ref.valueSetBindings && node.ref.valueSetBindings.length > 0) {
        return node.ref.valueSetBindings;
      }
    }
    return null;
  };
  $scope.updateDynamicMappingInfo = function() {
    $scope.isDynamicMappingSegment = false;
    $scope.dynamicMappingTable = null;

    var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
      return item.hl7Version == $rootScope.compositeProfile.segmentsMap[$scope.node.ref.id].hl7Version && item.segmentName == $rootScope.compositeProfile.segmentsMap[$scope.node.ref.id].name;
    });

    if (mappingStructure) {
      $scope.isDynamicMappingSegment = true;
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
  console.log("+++++");
  console.log($scope.dynamicMappingDefinition);

  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
