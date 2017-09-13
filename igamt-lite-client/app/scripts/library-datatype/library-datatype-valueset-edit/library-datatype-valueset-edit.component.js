/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('EditVSCtrlForLib', function($scope, $modalInstance, valueSets, component, $rootScope, SegmentService, blockUI) {

  $scope.vsChanged = false;
  $scope.component = component;
  $scope.vs = angular.copy(component.tables);
  $scope.tableList = angular.copy(component.tables);;
  $scope.loadVS = function($query) {


    return valueSets;

//        filter(function(table) {
//            return table.bindingIdentifier.toLowerCase().indexOf($query.toLowerCase()) != -1;
//        });

  };
  $scope.tagAdded = function(tag) {
    $scope.vsChanged = true;
    $scope.tableList.push({
      id: tag.id,
      bindingIdentifier: tag.bindingIdentifier,
      bindingLocation: null,
      bindingStrength: null
    });


    //$scope.log.push('Added: ' + tag.text);
  };

  $scope.tagRemoved = function(tag) {
    $scope.vsChanged = true;

    for (var i = 0; i < $scope.tableList.length; i++) {
      if ($scope.tableList[i].id === tag.id) {
        $scope.tableList.splice(i, 1);
      }
    };


  };

  $scope.addVS = function() {
    blockUI.start();

    $scope.vsChanged = false;
    component.tables = $scope.tableList;

    blockUI.stop();

    $modalInstance.close();


  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };


});

