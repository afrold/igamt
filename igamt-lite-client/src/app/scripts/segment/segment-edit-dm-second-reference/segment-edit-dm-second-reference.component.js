/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('EditDMSecondReferenceCtrl', function($scope, $modalInstance, currentMappingStructure, $rootScope) {
  $scope.currentMappingStructure = angular.copy(currentMappingStructure);
  $scope.selectedFieldPosition = null;
  $scope.selectedComponentPosition = null;
  $scope.selectedSubComponentPosition = null;
  $scope.components = null;
  $scope.subComponents = null;

  if($scope.currentMappingStructure.secondRefereceLocation){
    var splitLocation = $scope.currentMappingStructure.secondRefereceLocation.split('.');
    if(splitLocation.length > 0){
      $scope.selectedFieldPosition = splitLocation[0];

      var field = _.find($rootScope.segment.fields, function(f) {
        return f.position == splitLocation[0];
      });

      if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
        $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
      }

      if(splitLocation.length > 1 && $scope.components){
        $scope.selectedComponentPosition = splitLocation[1];
        var component =  _.find($scope.components, function(c) {
          return c.position == splitLocation[1];
        });
        if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
          $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }

        if(splitLocation.length > 2 && $scope.subComponents){
          $scope.selectedSubComponentPosition = splitLocation[2];
        }

      }
    }
  };

  $scope.updateField = function(){
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;

    var field = _.find($rootScope.segment.fields, function(f) {
      return f.position == $scope.selectedFieldPosition;
    });

    if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
      $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
    }
  };

  $scope.updateComponent = function(){
    $scope.selectedSubComponentPosition = null;
    $scope.subComponents = null;

    var component =  _.find($scope.components, function(c) {
      return c.position == $scope.selectedComponentPosition;
    });
    if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
      $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
    }
  };

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };

  $scope.close = function() {
    var path = null;
    if($scope.selectedFieldPosition){
      var path = $scope.selectedFieldPosition;
      if($scope.selectedComponentPosition){
        path = path + "." + $scope.selectedComponentPosition;
        if($scope.selectedSubComponentPosition){
          path = path + "." + $scope.selectedSubComponentPosition;
        }
      }
    }

    if(path){
      $scope.currentMappingStructure.secondRefereceLocation = path;
    }
    $modalInstance.close($scope.currentMappingStructure);
  };
});
