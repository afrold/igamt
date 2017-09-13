/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('DynamicMappingCtrl', function($scope, $modalInstance, selectedNode, $rootScope) {
  $scope.changed = false;
  $scope.selectedNode = selectedNode;
  $scope.selectedMapping = angular.copy(_.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
    return mapping.position == $scope.selectedNode.position;
  }));
  if (!$scope.selectedMapping) {
    $scope.selectedMapping = {};
    $scope.selectedMapping.cases = [];
    $scope.selectedMapping.position = $scope.selectedNode.position;
  }

  $scope.deleteCase = function(c) {
    var index = $scope.selectedMapping.cases.indexOf(c);
    $scope.selectedMapping.cases.splice(index, 1);
    $scope.recordChange();
  };

  $scope.addCase = function() {
    var newCase = {
      id: new ObjectId().toString(),
      type: 'case',
      value: '',
      datatype: null
    };

    $scope.selectedMapping.cases.unshift(newCase);
    $scope.recordChange();
  };

  $scope.recordChange = function() {
    $scope.changed = true;
    // $scope.editForm.$dirty = true;
  };


  $scope.updateMapping = function() {
    var oldMapping = _.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
      return mapping.position == $scope.selectedNode.position;
    });
    var index = $rootScope.segment.dynamicMapping.mappings.indexOf(oldMapping);
    $rootScope.segment.dynamicMapping.mappings.splice(index, 1);
    $rootScope.segment.dynamicMapping.mappings.unshift($scope.selectedMapping);
    $scope.changed = false;
    $scope.ok();
  };

  $scope.ok = function() {
    $modalInstance.close($scope.selectedNode);
  };

});
