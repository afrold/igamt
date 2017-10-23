/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('TableMappingDatatypeCtrl', function($scope, $mdDialog, currentNode, $rootScope, blockUI, TableService) {
  $scope.changed = false;
  $scope.currentNode = currentNode;
  $scope.selectedValueSetBindings = angular.copy(_.filter($rootScope.datatype.valueSetBindings, function(binding){ return "" + binding.location ===  "" + currentNode.path; }));
  $scope.listOfBindingLocations = null;
  $scope.isSingleValueSetAllowed = false;
  $scope.valueSetSelectedForSingleCode = null;
  $scope.mCode = null;
  $scope.mCodeSystem = null;
  $scope.codedElement = false;

  $scope.setChanged = function(){
    $scope.changed = true;
  };

  $scope.singleCodeInit = function (){
    $scope.valueSetSelectedForSingleCode = null;
    $scope.mCode = null;
    $scope.mCodeSystem = null;
  };

  $scope.addManualCode = function () {
    $scope.selectedValueSetBindings = [];
    var code = {};
    code.value = $scope.mCode;
    code.codeSystem = $scope.mCodeSystem;
    $scope.selectedValueSetBindings.push({ tableId: null, location: currentNode.path, usage: $scope.currentNode.usage, type: "singlecode", code : code, codedElement : $scope.codedElement});
    $scope.changed = true;
  };

  if(_.find($rootScope.config.singleValueSetDTs, function(singleValueSetDTs){
      return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.datatype.id].name;
    })) $scope.isSingleValueSetAllowed = true;

  if(_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT){
      return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.datatype.id].name;
    })) {
    $scope.codedElement = true;
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if (!$scope.selectedValueSetBindings[i].bindingLocation || $scope.selectedValueSetBindings[i].bindingLocation == '') {
        $scope.selectedValueSetBindings[i].bindingLocation = "1";
      }
    }

    var hl7Version = $rootScope.datatypesMap[$scope.currentNode.datatype.id].hl7Version;
    if(!hl7Version) hl7Version = "2.5.1";

    $scope.listOfBindingLocations = $rootScope.config.bindingLocationListByHL7Version[hl7Version];
  };

  $scope.deleteBinding = function(binding){
    var index = $scope.selectedValueSetBindings.indexOf(binding);
    if (index >= 0) {
      $scope.selectedValueSetBindings.splice(index, 1);
    }
    $scope.changed = true;
  };

  $scope.isSelected = function (v){
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if($scope.selectedValueSetBindings[i].tableId == v.id) return true;
    }
    return false;
  };
  $scope.toggle=function(v){
    if(!$scope.isSelected(v)){
      $scope.selectValueSet(v);
    }else{
      $scope.unselectValueSet(v);
    }

  };

  $scope.selectValueSet = function (v){
    if($scope.isSingleValueSetAllowed) $scope.selectedValueSetBindings = [];
    if($scope.selectedValueSetBindings.length > 0 && $scope.selectedValueSetBindings[0].type == 'singlecode') $scope.selectedValueSetBindings = [];
    if($scope.listOfBindingLocations){
      $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: currentNode.path, bindingLocation: "1", usage: currentNode.usage, type: "valueset"});
    }else {
      $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: currentNode.path, usage: currentNode.usage, type: "valueset"});
    }
    $scope.changed = true;
  };

  $scope.unselectValueSet = function (v){
    var toBeDelBinding =_.find($scope.selectedValueSetBindings, function(binding){
      return binding.tableId == v.id;
    });
    var index = $scope.selectedValueSetBindings.indexOf(toBeDelBinding);
    if (index >= 0) {
      $scope.selectedValueSetBindings.splice(index, 1);
    }
    $scope.changed = true;
  };

  $scope.selectValueSetForSingleCode = function (v){
    TableService.getOne(v.id).then(function(tbl) {
      $scope.valueSetSelectedForSingleCode = tbl;
    }, function() {
    });
  };

  $scope.isCodeSelected = function (c){
    for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
      if($scope.selectedValueSetBindings[i].code){
        if($scope.selectedValueSetBindings[i].code.id == c.id) return true;
      }
    }
    return false;
  };
  $scope.toggleCode=function(c){
    if(!$scope.isCodeSelected(c)){
      $scope.selectCode(c);
    }else{
      $scope.unselectCode(c);
    }
  }
  $scope.selectCode = function (c){
    $scope.selectedValueSetBindings = [];
    $scope.selectedValueSetBindings.push({ tableId: $scope.valueSetSelectedForSingleCode.id, location: currentNode.path, usage: currentNode.usage, type: "singlecode", code : c, codedElement : $scope.codedElement});
    $scope.changed = true;
  };

  $scope.unselectCode = function(c){
    $scope.selectedValueSetBindings = [];
    $scope.changed = true;
  };

  $scope.saveMapping = function() {
    blockUI.start();
    var otherValueSetBindings = angular.copy(_.filter($rootScope.datatype.valueSetBindings, function(binding){ return "" + binding.location !==  "" + currentNode.path; }));
    $rootScope.datatype.valueSetBindings= $scope.selectedValueSetBindings.concat(otherValueSetBindings);
    blockUI.stop();

    $mdDialog.hide();
  };

  $scope.ok = function() {
    $mdDialog.hide();
  };

});
