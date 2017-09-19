/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddComponentCtrl', function($scope, $modalInstance, datatypes, datatype, valueSets, $rootScope, $http, ngTreetableParams, SegmentService, DatatypeLibrarySvc, MessageService, blockUI) {

  $scope.valueSets = valueSets;
  $scope.datatypes = datatypes;


  $scope.newComponent = {
    comment: "",
    confLength: "",
    datatype: {
      ext: null,
      id: "",
      label: "",
      name: "",
    },
    hide: false,
    id: "",
    maxLength: "",
    minLength: "",
    name: "",
    position: "",
    table: {
      bindingIdentifier: "",
      bindingLocation: null,
      bindingStrength: null,
      id: ""
    },
    text: "",
    type: "component",
    usage: ""


  };

  $scope.$watch('DT', function() {
    if ($scope.DT) {
      $scope.newComponent.datatype.ext = $scope.DT.ext;
      $scope.newComponent.datatype.id = $scope.DT.id;
      $scope.newComponent.datatype.name = $scope.DT.name;
      $scope.newComponent.datatype.label = $scope.DT.label;
    }
  }, true);

  $scope.$watch('VS', function() {
    if ($scope.VS) {
      $scope.newComponent.table.bindingIdentifier = $scope.VS.bindingIdentifier;
      $scope.newComponent.table.id = $scope.VS.id;


    }

  }, true);


  $scope.selectDT = function(datatype) {
    $scope.DT = datatype;
  };
  $scope.selectedDT = function() {
    return ($scope.DT !== undefined);
  };
  $scope.unselectDT = function() {
    $scope.DT = undefined;
  };
  $scope.isDTActive = function(id) {
    if ($scope.DT) {
      return $scope.DT.id === id;
    } else {
      return false;
    }

  };
  $scope.selectUsage = function(usage) {
    if (usage === 'X' || usage === 'W') {
      $scope.newComponent.max = 0;
      $scope.newComponent.min = 0;
      $scope.disableMin = true;
      $scope.disableMax = true;
    } else if (usage === 'R') {
      $scope.newComponent.min = 1;
      $scope.disableMin = true;
      $scope.disableMax = false;
    } else if (usage === 'RE' || usage === 'O') {
      $scope.newComponent.min = 0;
      $scope.disableMin = true;
      $scope.disableMax = false;
    } else {
      $scope.disableMin = false;
      $scope.disableMax = false;
    }
  };


  $scope.selectVS = function(valueSet) {
    $scope.VS = valueSet;
  };
  $scope.selectedVS = function() {
    return ($scope.VS !== undefined);
  };
  $scope.unselectVS = function() {
    $scope.VS = undefined;
  };
  $scope.isVSActive = function(id) {
    if ($scope.VS) {
      return $scope.VS.id === id;
    } else {
      return false;
    }

  };


  $scope.addComponent = function() {
    blockUI.start();
    if ($rootScope.datatype.components.length !== 0) {
      $scope.newComponent.position = $rootScope.datatype.components[$rootScope.datatype.components.length - 1].position + 1;

    } else {
      $scope.newComponent.position = 1;
    }

    $scope.newComponent.id = new ObjectId().toString();

    if ($rootScope.datatype != null) {
      if (!$rootScope.datatype.components || $rootScope.datatype.components === null)
        $rootScope.datatype.components = [];
      $rootScope.datatype.components.push($scope.newComponent);
      MessageService.updatePosition(datatype.components, $scope.newComponent.position - 1, $scope.position - 1);





    }
    blockUI.stop();
    $modalInstance.close();

  };


  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };


});
