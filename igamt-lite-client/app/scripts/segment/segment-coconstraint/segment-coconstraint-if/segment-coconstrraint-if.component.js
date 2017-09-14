/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('CoConstraintIFDefinitionCtrl', function($scope, $mdDialog, coConstraintIFDefinition, $rootScope) {
  $scope.selectedCoConstraintIFDefinition = angular.copy(coConstraintIFDefinition);

  $scope.coConstraintType = 'value';
  $scope.selectedFieldPosition = null;
  $scope.selectedComponentPosition = null;
  $scope.selectedSubComponentPosition = null;
  $scope.components = null;
  $scope.subComponents = null;
  $scope.primitive = true;

  $scope.targetNode = null;


  if($scope.selectedCoConstraintIFDefinition){
    $scope.primitive = $scope.selectedCoConstraintIFDefinition.primitive;
    $scope.coConstraintType = $scope.selectedCoConstraintIFDefinition.constraintType;
    var splitLocation = $scope.selectedCoConstraintIFDefinition.path.split('.');
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



  $scope.updateField = function(){
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;

    var field = _.find($rootScope.segment.fields, function(f) {
      return f.position == $scope.selectedFieldPosition;
    });

    $scope.targetNode = field;

    if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
      $scope.primitive = false;
      $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
    }
  };

  $scope.updateComponent = function(){
    $scope.selectedSubComponentPosition = null;
    $scope.subComponents = null;
    $scope.primitive = true;

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
    var subComponent =  _.find($scope.subComponents, function(sc) {
      return sc.position == $scope.selectedSubComponentPosition;
    });
    $scope.targetNode = subComponent;
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.close = function() {
    var ifColumnDefinition = {};
    ifColumnDefinition.id = new ObjectId().toString();
    ifColumnDefinition.constraintType = $scope.coConstraintType;
    ifColumnDefinition.name = $scope.targetNode.name;
    ifColumnDefinition.usage = $scope.targetNode.usage;
    ifColumnDefinition.dtId = $scope.targetNode.datatype.id;
    ifColumnDefinition.primitive = $scope.primitive;

    if($scope.selectedFieldPosition){
      ifColumnDefinition.path = "" + $scope.selectedFieldPosition;
      ifColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
      ifColumnDefinition.type = "field";
      if($scope.selectedComponentPosition){
        ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedComponentPosition;
        ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
        ifColumnDefinition.type = "component";
        if($scope.selectedSubComponentPosition){
          ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
          ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
          ifColumnDefinition.type = "subcomponent";
        }
      }
    }

    $mdDialog.hide(ifColumnDefinition);
  };

});
