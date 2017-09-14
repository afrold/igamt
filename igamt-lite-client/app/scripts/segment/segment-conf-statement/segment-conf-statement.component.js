/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('ConformanceStatementSegmentCtrl', function($scope, config, tables, selectedSegment, currentConformanceStatements, mode, $rootScope, $q, $mdDialog) {
  $scope.selectedSegment = angular.copy(selectedSegment);
  $scope.config = config;
  $scope.tables = tables;
  $scope.constraintType = 'Plain';
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext,   $scope.selectedSegment.name + "_" + $scope.selectedSegment.ext);
  $scope.newComplexConstraint = [];
  $scope.firstNodeData = null;
  $scope.secondNodeData = null;
  $scope.changed = false;
  $scope.treeDataForContext = [];
  $scope.treeDataForContext.push($scope.selectedSegment);
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
    var childPositionNumber;
    var childLocationName;
    var childNodeName;
    var childInstanceNumber;
    var childisInstanceNumberEditable;
    var child;

    if (current.type === 'segment') {
      var seg = current;
      for (var i in seg.fields) {
        var f = seg.fields[i];
        f.pathInfoSet = angular.copy(current.pathInfoSet);

        childPositionNumber = f.position;
        childLocationName = f.position;
        childNodeName = f.name;
        childInstanceNumber = "1";
        childisInstanceNumberEditable = false;
        if (f.max !== '1') {
          childInstanceNumber = '*';
          childisInstanceNumberEditable = true;
        }
        child = angular.copy($rootScope.datatypesMap[f.datatype.id]);
        child.id = new ObjectId().toString();
        f.child = child;
        $scope.generatePathInfo(f, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
      }
    } else if (current.type === 'field' || current.type === 'component') {
      var dt = current.child;
      for (var i in dt.components) {
        var c = dt.components[i];
        c.pathInfoSet = angular.copy(current.pathInfoSet);
        childPositionNumber = c.position;
        childLocationName = c.position;
        childNodeName = c.name;
        childInstanceNumber = "1";
        childisInstanceNumberEditable = false;
        if($rootScope.datatypesMap[c.datatype.id]){
          child = angular.copy($rootScope.datatypesMap[c.datatype.id]);
          child.id = new ObjectId().toString();
          c.child = child;
          $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
        }

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
      constraintId: $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $scope.selectedSegment.name + "_" + $scope.selectedSegment.ext),
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
    $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $scope.selectedSegment.name + "_" + $scope.selectedSegment.ext);
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

          if (i === $scope.newConstraint.pathInfoSet_1.length - 1) {
            locationPath = locationPath + " (" + pathInfo.nodeName + ")";
          }
        }
      }

      $scope.newConstraint.position_1 = positionPath.substr(1);
      $scope.newConstraint.location_1 = $scope.selectedSegment.name + '-' + locationPath.substr(1);
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

          if (i === $scope.newConstraint.pathInfoSet_2.length - 1) {
            locationPath = locationPath + " (" + pathInfo.nodeName + ")";
          }
        }
      }

      $scope.newConstraint.position_2 = positionPath.substr(1);
      $scope.newConstraint.location_2 = $scope.selectedSegment.name + '-' + locationPath.substr(1);
    }
  };

  $scope.deleteConformanceStatement = function(conformanceStatement) {
    $scope.selectedSegment.conformanceStatements.splice($scope.selectedSegment.conformanceStatements.indexOf(conformanceStatement), 1);
    $scope.changed = true;
  };

  $scope.addComplexConformanceStatement = function() {
    $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    if(!$scope.selectedSegment.conformanceStatements) $scope.selectedSegment.conformanceStatements = [];
    $scope.selectedSegment.conformanceStatements.push($scope.complexConstraint);
    $scope.initComplexStatement();
    $scope.changed = true;
  };

  $scope.addFreeTextConformanceStatement = function() {
    var cs = $rootScope.generateFreeTextConformanceStatement($scope.newConstraint);
    if(!$scope.selectedSegment.conformanceStatements) $scope.selectedSegment.conformanceStatements = [];
    $scope.selectedSegment.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.addConformanceStatement = function() {
    var cs = $rootScope.generateConformanceStatement($scope.newConstraint);
    if(!$scope.selectedSegment.conformanceStatements) $scope.selectedSegment.conformanceStatements = [];
    $scope.selectedSegment.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.saveClose = function() {
    $rootScope.recordChanged();
    $mdDialog.hide($scope.selectedSegment);
  };

  if(mode === 'pc'){
    $scope.selectedSegment.conformanceStatements = angular.copy(currentConformanceStatements);
  }
});

