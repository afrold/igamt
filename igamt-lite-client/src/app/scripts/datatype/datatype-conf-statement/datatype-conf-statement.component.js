/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConformanceStatementDatatypeCtrl', function($scope, config, tables, selectedDatatype, $rootScope, $q, $mdDialog) {
  $scope.selectedDatatype = angular.copy(selectedDatatype);
  $scope.config = config;
  $scope.tables = tables;
  $scope.constraintType = 'Plain';
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.ext = null;
  if ($rootScope.igdocument) $scope.ext = $rootScope.igdocument.metaData.ext;
  $scope.newComplexConstraintId = $rootScope.calNextCSID($scope.ext, $rootScope.datatype.name + "_" + $rootScope.datatype.ext);
  $scope.newComplexConstraint = [];
  $scope.firstNodeData = null;
  $scope.secondNodeData = null;
  $scope.changed = false;

  $scope.treeDataForContext = [];
  $scope.treeDataForContext.push(angular.copy($rootScope.datatype));
  $scope.treeDataForContext[0].pathInfoSet = [];
  $scope.generatePathInfo = function(current, positionNumber, locationName, instanceNumber, isInstanceNumberEditable, nodeName) {
    var pathInfo = {};
    pathInfo.positionNumber = positionNumber;
    pathInfo.locationName = locationName;
    pathInfo.nodeName = nodeName;
    pathInfo.instanceNumber = instanceNumber;
    pathInfo.isInstanceNumberEditable = isInstanceNumberEditable;
    current.pathInfoSet.push(pathInfo);
    current.childrenVisible = false;
    if (current.type == 'datatype') {
      var dt = current;
      for (var i in dt.components) {
        var c = dt.components[i];
        c.pathInfoSet = angular.copy(current.pathInfoSet);

        var childPositionNumber = c.position;
        var childLocationName = c.position;
        var childNodeName = c.name;
        var childInstanceNumber = "1";
        var childisInstanceNumberEditable = false;
        var child = angular.copy($rootScope.datatypesMap[c.datatype.id]);
        child.id = new ObjectId().toString();
        c.child = child;
        $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
      }
    } else if (current.type == 'component') {
      var dt = current.child;
      for (var i in dt.components) {
        var c = dt.components[i];
        c.pathInfoSet = angular.copy(current.pathInfoSet);
        var childPositionNumber = c.position;
        var childLocationName = c.position;
        var childNodeName = c.name;
        var childInstanceNumber = "1";
        var childisInstanceNumberEditable = false;
        var child = angular.copy($rootScope.datatypesMap[c.datatype.id]);
        child.id = new ObjectId().toString();
        c.child = child;
        $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
      }
    }
  };

  $scope.generatePathInfo($scope.treeDataForContext[0], ".", ".", "1", false);
  $scope.treeDataForContext[0].childrenVisible = true;

  $scope.isEmptyConstraintID = function(newConstraint) {
    if (newConstraint && newConstraint.constraintId === null) return true;
    if (newConstraint && newConstraint.constraintId === '') return true;

    return false;
  };

  $scope.isEmptyConstraintVerb = function(newConstraint) {
    if (newConstraint && newConstraint.verb === null) return true;

    return false;
  };

  $scope.isEmptyConstraintPattern = function(newConstraint) {
    if (newConstraint && newConstraint.contraintType === null) return true;

    return false;
  };

  $scope.isEmptyConstraintValue = function(newConstraint) {
    if (newConstraint && newConstraint.value === null) return true;

    return false;
  };

  $scope.isEmptyConstraintValue2 = function(newConstraint) {
    if (newConstraint && newConstraint.value2 === null) return true;

    return false;
  };

  $scope.getUpdatedBindingIdentifier = function(table) {
    if (table.hl7Version && table.hl7Version !== '') {
      return table.bindingIdentifier + "_" + table.hl7Version.split(".").join("-");
    }
    return table.bindingIdentifier;
  };

  $scope.setChanged = function() {
    $scope.changed = true;
  };

  $scope.toggleChildren = function(data) {
    data.childrenVisible = !data.childrenVisible;
    data.folderClass = data.childrenVisible ? "fa-caret-down" : "fa-caret-right";
  };

  $scope.beforeComponentDrop = function() {
    var deferred = $q.defer();

    if ($scope.draggingStatus === 'ContextTreeNodeDragging') {
      deferred.resolve();
    } else {
      deferred.reject();
    }
    return deferred.promise;
  };

  $scope.beforeNodeDrop = function() {
    var deferred = $q.defer();
    deferred.resolve();
    return deferred.promise;
  };

  $scope.afterFirstNodeDrop = function() {
    $scope.draggingStatus = null;
    $scope.newConstraint.pathInfoSet_1 = $scope.firstNodeData.pathInfoSet;
    $scope.generateFirstPositionAndLocationPath();
  };

  $scope.afterSecondNodeDrop = function() {
    $scope.draggingStatus = null;
    $scope.newConstraint.pathInfoSet_2 = $scope.secondNodeData.pathInfoSet;
    $scope.generateSecondPositionAndLocationPath();
  };

  $scope.draggingNodeFromContextTree = function(event, ui, data) {
    $scope.draggingStatus = 'ContextTreeNodeDragging';
  };

  $scope.initConformanceStatement = function() {
    $scope.newConstraint = angular.fromJson({
      pathInfoSet_1: null,
      pathInfoSet_2: null,
      position_1: null,
      position_2: null,
      location_1: null,
      location_2: null,
      freeText: null,
      verb: null,
      ignoreCase: false,
      constraintId: $rootScope.calNextCSID($scope.ext, $rootScope.datatype.name + "_" + $rootScope.datatype.ext),
      contraintType: null,
      value: null,
      value2: null,
      valueSetId: null,
      bindingStrength: 'R',
      bindingLocation: '1'
    });
  };

  $scope.initComplexStatement = function() {
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID($scope.ext, $rootScope.datatype.name + "_" + $rootScope.datatype.ext);
  };

  $scope.initConformanceStatement();

  $scope.generateFirstPositionAndLocationPath = function() {
    if ($scope.newConstraint.pathInfoSet_1) {
      var positionPath = '';
      var locationPath = '';
      for (var i in $scope.newConstraint.pathInfoSet_1) {
        if (i > 0) {
          var pathInfo = $scope.newConstraint.pathInfoSet_1[i];
          positionPath = positionPath + "." + pathInfo.positionNumber + "[" + pathInfo.instanceNumber + "]";
          locationPath = locationPath + "." + pathInfo.locationName + "[" + pathInfo.instanceNumber + "]";

          if (i == $scope.newConstraint.pathInfoSet_1.length - 1) {
            locationPath = locationPath + " (" + pathInfo.nodeName + ")";
          }
        }
      }
      $scope.newConstraint.position_1 = positionPath.substr(1);
      $scope.newConstraint.location_1 = $rootScope.datatype.name + '-' + locationPath.substr(1);
    }
  };

  $scope.generateSecondPositionAndLocationPath = function() {
    if ($scope.newConstraint.pathInfoSet_2) {
      var positionPath = '';
      var locationPath = '';
      for (var i in $scope.newConstraint.pathInfoSet_2) {
        if (i > 0) {
          var pathInfo = $scope.newConstraint.pathInfoSet_2[i];
          positionPath = positionPath + "." + pathInfo.positionNumber + "[" + pathInfo.instanceNumber + "]";
          locationPath = locationPath + "." + pathInfo.locationName + "[" + pathInfo.instanceNumber + "]";

          if (i == $scope.newConstraint.pathInfoSet_2.length - 1) {
            locationPath = locationPath + " (" + pathInfo.nodeName + ")";
          }
        }
      }
      $scope.newConstraint.position_2 = positionPath.substr(1);
      $scope.newConstraint.location_2 = $rootScope.datatype.name + '-' + locationPath.substr(1);
    }
  };

  $scope.deleteConformanceStatement = function(conformanceStatement) {
    $scope.selectedDatatype.conformanceStatements.splice($scope.selectedDatatype.conformanceStatements.indexOf(conformanceStatement), 1);
    $scope.changed = true;
  };

  $scope.addComplexConformanceStatement = function() {
    $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    $scope.selectedDatatype.conformanceStatements.push($scope.complexConstraint);
    $scope.initComplexStatement();
    $scope.changed = true;
  };

  $scope.addFreeTextConformanceStatement = function() {
    var cs = $rootScope.generateFreeTextConformanceStatement($scope.newConstraint);
    $scope.selectedDatatype.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.addConformanceStatement = function() {
    var cs = $rootScope.generateConformanceStatement($scope.newConstraint);
    $scope.selectedDatatype.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.save = function() {
    $mdDialog.hide($scope.selectedDatatype);
  };
});
