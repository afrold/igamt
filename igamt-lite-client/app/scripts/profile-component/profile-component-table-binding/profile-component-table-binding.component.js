/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('TableBindingForPcCtrl', function($scope, $mdDialog, currentNode, $rootScope, blockUI, TableService) {
  $scope.changed = false;
  console.log(currentNode);
  $scope.currentNode = currentNode;
  $scope.currentNode.locationPath = currentNode.path;
  $scope.isSingleValueSetAllowed = false;
  $scope.valueSetSelectedForSingleCode = null;
  $scope.mCode = null;
  $scope.mCodeSystem = null;
  $scope.codedElement = false;

  $scope.singleCodeInit = function() {
    $scope.valueSetSelectedForSingleCode = null;
    $scope.mCode = null;
    $scope.mCodeSystem = null;
  };
  $scope.addManualCode = function() {
    $scope.selectedValueSetBindings = [];
    var code = {};
    code.value = $scope.mCode;
    code.codeSystem = $scope.mCodeSystem;
    $scope.selectedValueSetBindings.push({ tableId: null, location: positionPath, usage: $scope.currentNode.usage, type: "singlecode", code: code, codedElement : $scope.codedElement});
    $scope.changed = true;
  };

  if (_.find($rootScope.config.singleValueSetDTs, function(singleValueSetDTs) {
      if ($scope.currentNode.attributes.datatype) {
        return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].name;

      } else {
        return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].name;

      }
    })) $scope.isSingleValueSetAllowed = true;

  var positionPath = '';

  var index = currentNode.path.indexOf(".");
  positionPath = currentNode.path.substr(index + 1);
  if (!currentNode.valueSetBindings) {
    $scope.selectedValueSetBindings = angular.copy(_.filter(currentNode.oldValueSetBindings, function(binding) { return binding.location == positionPath; }));

  } else {
    $scope.selectedValueSetBindings = angular.copy(_.filter(currentNode.valueSetBindings, function(binding) { return binding.location == positionPath; }));

  }
  $scope.listOfBindingLocations = null;

  if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
      if ($scope.currentNode.attributes.datatype) {
        return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].name;

      } else {
        return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].name;

      }
    })) {
    $scope.codedElement = true;
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if (!$scope.selectedValueSetBindings[i].bindingLocation || $scope.selectedValueSetBindings[i].bindingLocation == '') {
        $scope.selectedValueSetBindings[i].bindingLocation = "1";
      }
    }
    var hl7Version = null
    if ($scope.currentNode.attributes.datatype) {
      hl7Version = $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].hl7Version;

    } else {
      hl7Version = $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].hl7Version;

    }
    if (!hl7Version) hl7Version = "2.5.1";

    $scope.listOfBindingLocations = $rootScope.config.bindingLocationListByHL7Version[hl7Version];
  };

  $scope.deleteBinding = function(binding) {
    var index = $scope.selectedValueSetBindings.indexOf(binding);
    if (index >= 0) {
      $scope.selectedValueSetBindings.splice(index, 1);
    }
    $scope.changed = true;
  };

  $scope.isSelected = function(v) {
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if ($scope.selectedValueSetBindings[i].tableId == v.id) return true;
    }
    return false;
  };

  $scope.selectValueSet = function(v) {
    if ($scope.isSingleValueSetAllowed) $scope.selectedValueSetBindings = [];
    if ($scope.selectedValueSetBindings.length > 0 && $scope.selectedValueSetBindings[0].type == 'singlecode') $scope.selectedValueSetBindings = [];
    if ($scope.listOfBindingLocations) {
      $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: positionPath, bindingLocation: "1", usage: currentNode.usage, type: "valueset" });
    } else {
      $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: positionPath, usage: currentNode.usage, type: "valueset" });
    }
    $scope.changed = true;
  };

  $scope.unselectValueSet = function(v) {
    var toBeDelBinding = _.find($scope.selectedValueSetBindings, function(binding) {
      return binding.tableId == v.id;
    });
    var index = $scope.selectedValueSetBindings.indexOf(toBeDelBinding);
    if (index >= 0) {
      $scope.selectedValueSetBindings.splice(index, 1);
    }
    $scope.changed = true;
  };
  $scope.selectValueSetForSingleCode = function(v) {
    console.log(v);
    TableService.getOne(v.id).then(function(tbl) {
      $scope.valueSetSelectedForSingleCode = tbl;
    }, function() {});
  };
  $scope.isCodeSelected = function(c) {
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if ($scope.selectedValueSetBindings[i].code) {
        if ($scope.selectedValueSetBindings[i].code.id == c.id) return true;
      }
    }
    return false;
  };
  $scope.selectCode = function(c) {
    $scope.selectedValueSetBindings = [];
    $scope.selectedValueSetBindings.push({ tableId: $scope.valueSetSelectedForSingleCode.id, location: positionPath, usage: currentNode.usage, type: "singlecode", code: c, codedElement : $scope.codedElement});
    $scope.changed = true;
  };
  $scope.toggle=function(v){
    if(!$scope.isSelected(v)){
      $scope.selectValueSet(v);
    }else{
      $scope.unselectValueSet(v);
    }

  };
  $scope.unselectCode = function(c) {
    $scope.selectedValueSetBindings = [];
    $scope.changed = true;
  };
  $scope.toggleCode=function(c){
    if(!$scope.isCodeSelected(c)){
      $scope.selectCode(c);
    }else{
      $scope.unselectCode(c);
    }
  }


  $scope.saveMapping = function() {
    blockUI.start();
    // var otherValueSetBindings = angular.copy(_.filter($rootScope.message.valueSetBindings, function(binding) { return binding.location != positionPath; }));
    //$rootScope.message.valueSetBindings = $scope.selectedValueSetBindings.concat(otherValueSetBindings);
    currentNode.valueSetBindings = $scope.selectedValueSetBindings;
    blockUI.stop();

    $mdDialog.hide(currentNode);
  };

  $scope.ok = function() {
    $mdDialog.hide();
  };

});
