/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('CoConstraintTHENDefinitionCtrl', function($scope, $mdDialog, coConstraintTHENDefinition, $rootScope, TableService) {
  $scope.selectedCoConstraintTHENDefinition = angular.copy(coConstraintTHENDefinition);

  $scope.coConstraintType = 'value';
  $scope.selectedFieldPosition = null;
  $scope.selectedComponentPosition = null;
  $scope.selectedSubComponentPosition = null;
  $scope.components = null;
  $scope.subComponents = null;
  $scope.primitive = true;
  $scope.dMReference = false;

  $scope.targetNode = null;


  if($scope.selectedCoConstraintTHENDefinition){
    $scope.primitive = $scope.selectedCoConstraintTHENDefinition.primitive;
    $scope.coConstraintType = $scope.selectedCoConstraintTHENDefinition.constraintType;
    $scope.isDMReference = $scope.selectedCoConstraintTHENDefinition.dMReference;
    var splitLocation = $scope.selectedCoConstraintTHENDefinition.path.split('.');
    if(splitLocation.length > 0){
      $scope.selectedFieldPosition = splitLocation[0];

      var field = _.find($rootScope.segment.fields, function(f) {
        return f.position == splitLocation[0];
      });

      $scope.targetNode = field;

      if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
        $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

        if(splitLocation.length > 1 && $scope.components){
          $scope.selectedComponentPosition = splitLocation[1];
          var component =  _.find($scope.components, function(c) {
            return c.position == splitLocation[1];
          });
          $scope.targetNode = component;
          if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

            if(splitLocation.length > 2 && $scope.subComponents){
              $scope.selectedSubComponentPosition = splitLocation[2];
              var subComponent =  _.find($scope.subComponents, function(sc) {
                return sc.position == splitLocation[2];
              });
              $scope.targetNode = subComponent;
            }
          }
        }
      }
    }
  };

  $scope.isVariesDT = function (){
    if($scope.targetNode) {
      if($rootScope.datatypesMap[$scope.targetNode.datatype.id].name.toLowerCase() == 'varies') {
        return true;
      }
    }

    return false;
  };

  $scope.updateField = function(){
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    var field = _.find($rootScope.segment.fields, function(f) {
      return f.position == $scope.selectedFieldPosition;
    });

    $scope.targetNode = field;

    if($rootScope.isDynamicMappingSegment){
      console.log("=========This is DM segment!!=========");
      var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
        return item.hl7Version == $rootScope.segment.hl7Version && item.segmentName == $rootScope.segment.name;
      });

      if(mappingStructure){
        if($rootScope.segment.dynamicMappingDefinition && $rootScope.segment.dynamicMappingDefinition.mappingStructure){
          console.log("=========Found mapping structure!!=========");
          mappingStructure = $rootScope.segment.dynamicMappingDefinition.mappingStructure;
        }else{
          console.log("=========Not Found mapping structure and Default setting will be used!!=========");
        }

        var valueSetBinding = _.find($rootScope.segment.valueSetBindings, function(vsb) {
          return vsb.location == mappingStructure.referenceLocation;
        });

        if(valueSetBinding) {
          TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
            $rootScope.dynamicMappingTable = tbl;
          }, function() {

          });
        }

        if($scope.selectedFieldPosition == mappingStructure.referenceLocation){
          $scope.dMReference = true;
        }
      }
    }

    if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
      $scope.primitive = false;
      $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
    }
  };

  $scope.updateComponent = function(){
    $scope.selectedSubComponentPosition = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    var component =  _.find($scope.components, function(c) {
      return c.position == $scope.selectedComponentPosition;
    });

    $scope.targetNode = component;
    if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
      $scope.primitive = false;
      $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
    }
  };

  $scope.updateSubComponent = function(){
    $scope.primitive = true;
    $scope.dMReference = false;
    var subComponent =  _.find($scope.subComponents, function(sc) {
      return sc.position == $scope.selectedSubComponentPosition;
    });
    $scope.targetNode = subComponent;
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.close = function() {
    var thenColumnDefinition = {};
    if($scope.selectedCoConstraintTHENDefinition){
      thenColumnDefinition.id = $scope.selectedCoConstraintTHENDefinition.id;
    }

    thenColumnDefinition.constraintType = $scope.coConstraintType;
    thenColumnDefinition.name = $scope.targetNode.name;
    thenColumnDefinition.usage = $scope.targetNode.usage;
    thenColumnDefinition.dtId = $scope.targetNode.datatype.id;
    thenColumnDefinition.primitive = $scope.primitive;
    thenColumnDefinition.dMReference = $scope.dMReference;

    if(thenColumnDefinition.dMReference) {
      thenColumnDefinition.constraintType = 'dmr';
    }

    if($scope.selectedFieldPosition){
      thenColumnDefinition.path = "" + $scope.selectedFieldPosition;
      thenColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
      thenColumnDefinition.type = "field";
      if($scope.selectedComponentPosition){
        thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedComponentPosition;
        thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
        thenColumnDefinition.type = "component";
        if($scope.selectedSubComponentPosition){
          thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
          thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
          thenColumnDefinition.type = "subcomponent";
        }
      }
    }

    $mdDialog.hide(thenColumnDefinition);
  };

});
