/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('GlobalConformanceStatementCtrl', function($scope, segmentsMap, config, tables, selectedMessage, contextPath,  currentConformanceStatements, $rootScope, $q, $mdDialog, mode) {
  $scope.dialogStep = 0;
  $scope.segmentsMap = segmentsMap;
  $scope.config = config;
  $scope.tables = tables;
  $scope.selectedMessage = null;
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.newComplexConstraintId = null;
  $scope.selectedContextNode = null;
  $scope.treeDataForMessage = [];
  $scope.treeDataForContext = [];
  $scope.constraintType = 'Plain';
  $scope.firstNodeData = null;
  $scope.secondNodeData = null;
  $scope.changed = false;
  $scope.draggingStatus = null;
  $scope.contextKey = null;
  $scope.mode = mode;

  $scope.setChanged = function() {
    $scope.changed = true;
  };

  $scope.toggleChildren = function(data) {
    data.childrenVisible = !data.childrenVisible;
    data.folderClass = data.childrenVisible ? "fa-caret-down" : "fa-caret-right";
  };

  $scope.beforeNodeDrop = function() {
    var deferred = $q.defer();
    if ($scope.draggingStatus === 'MessageTreeNodeDragging') {
      deferred.resolve();
    } else {
      deferred.reject();
    }
    return deferred.promise;
  };

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

  $scope.draggingNodeFromContextTree = function(event, ui, nodeData) {
    $scope.draggingStatus = 'MessageTreeNodeDragging';
  };


  $scope.selectContext = function(selectedContextNode) {
    if($scope.selectedContextNode && $scope.selectedContextNode === selectedContextNode){
      $scope.contextKey = null;
      $scope.selectedContextNode = null;
      $scope.initConformanceStatement();
      $scope.initComplexPredicate();
      $scope.treeDataForContext=[];
    }else {
      $scope.treeDataForContext = [];
      $scope.contextKey = new ObjectId().toString();
      $scope.selectedContextNode = selectedContextNode;
      $scope.selectedContextNode.contextKey = $scope.contextKey;
      $scope.selectedContextNode.pathInfoSet = [];
      $scope.generatePathInfo($scope.selectedContextNode, ".", ".", "1", false, null);
      $scope.initConformanceStatement();
      $scope.treeDataForContext.push($scope.selectedContextNode);
    }

  };

  $scope.goNext = function() {
    $scope.dialogStep = $scope.dialogStep + 1;
  };

  $scope.goBack = function () {
    $scope.dialogStep = $scope.dialogStep - 1;
  };

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
      $scope.newConstraint.location_1 = locationPath.substr(1);
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
      $scope.newConstraint.location_2 = locationPath.substr(1);
    }
  };

  $scope.draggingNodeFromMessageTree = function(event, ui, nodeData) {
    $scope.draggingStatus = 'MessageTreeNodeDragging';
  };

  $scope.generatePathInfo = function(current, positionNumber, locationName, instanceNumber, isInstanceNumberEditable, nodeName) {
    var pathInfo = {};
    pathInfo.positionNumber = positionNumber;
    pathInfo.locationName = locationName;
    pathInfo.nodeName = nodeName;
    pathInfo.instanceNumber = instanceNumber;
    pathInfo.isInstanceNumberEditable = isInstanceNumberEditable;
    current.pathInfoSet.push(pathInfo);
    current.childrenVisible = false;

    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        var segGroup = current.children[i];
        segGroup.pathInfoSet = angular.copy(current.pathInfoSet);
        var childPositionNumber = segGroup.position;
        var childLocationName = '';
        var childNodeName = '';
        var childInstanceNumber = "1";
        var childisInstanceNumberEditable = false;
        if (segGroup.max != '1') {
          childInstanceNumber = '*';
          childisInstanceNumberEditable = true;
        }
        if (segGroup.type == 'group') {
          childNodeName = segGroup.name;
          childLocationName = segGroup.name.substr(segGroup.name.lastIndexOf('.') + 1);
        } else {
          var s = angular.copy($rootScope.segmentsMap[segGroup.ref.id]);
          s.id = new ObjectId().toString();
          childLocationName = s.name;
          childNodeName = s.name;
          segGroup.segment = s;
        }
        $scope.generatePathInfo(segGroup, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
      }
    } else if (current.type == 'segmentRef') {
      var seg = current.segment;
      for (var i in seg.fields) {
        var f = seg.fields[i];
        f.pathInfoSet = angular.copy(current.pathInfoSet);

        var childPositionNumber = f.position;
        var childLocationName = f.position;
        var childNodeName = f.name;
        var childInstanceNumber = "1";
        var childisInstanceNumberEditable = false;
        if (f.max != '1') {
          childInstanceNumber = '*';
          childisInstanceNumberEditable = true;
        }
        var child = angular.copy($rootScope.datatypesMap[f.datatype.id]);
        child.id = new ObjectId().toString();
        f.child = child;
        $scope.generatePathInfo(f, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
      }
    } else if (current.type == 'field' || current.type == 'component') {
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
      constraintId: $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, null),
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
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, null);
  };

  $scope.addConformanceStatement = function() {

    var cs = $rootScope.generateConformanceStatement($scope.newConstraint);
    if(!$scope.selectedContextNode.conformanceStatements) $scope.selectedContextNode.conformanceStatements = [];
    $scope.selectedContextNode.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.deleteConformanceStatement = function(conformanceStatement) {
    $scope.selectedContextNode.conformanceStatements.splice($scope.selectedContextNode.conformanceStatements.indexOf(conformanceStatement), 1);
    $scope.changed = true;
  };

  $scope.addFreeTextConformanceStatement = function() {
    var cs = $rootScope.generateFreeTextConformanceStatement($scope.newConstraint);
    if(!$scope.selectedContextNode.conformanceStatements) $scope.selectedContextNode.conformanceStatements = [];
    $scope.selectedContextNode.conformanceStatements.push(cs);
    $scope.changed = true;
    $scope.initConformanceStatement();
  };

  $scope.addComplexConformanceStatement = function() {
    $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    if(!$scope.selectedContextNode.conformanceStatements) $scope.selectedContextNode.conformanceStatements = [];
    $scope.selectedContextNode.conformanceStatements.push($scope.complexConstraint);
    $scope.initComplexStatement();
    $scope.changed = true;
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.saveClose = function() {
    if(mode === 'pc'){
      $rootScope.recordChanged();
      $mdDialog.hide($scope.selectedContextNode);
    }else{
      $rootScope.recordChanged();
      $mdDialog.hide($scope.selectedMessage);
    }
  };

  $scope.travelByContextPath = function (obj, path){
    var splittedPath = path.split(".");

    if(splittedPath.length > 1){
      var currentPath = splittedPath[1];
      var childObj = _.find(obj.children, function(child){ return child.position + ""  == currentPath + ""; });

      if(childObj){
        splittedPath.splice(0, 1);
        var newPath = splittedPath.join(".");
        console.log(newPath);
        $scope.travelByContextPath(childObj, newPath);
      }
    }else {
      $scope.treeDataForContext = [];
      $scope.contextKey = new ObjectId().toString();
      $scope.selectedContextNode = obj;
      $scope.selectedContextNode.conformanceStatements = angular.copy(currentConformanceStatements);
      $scope.selectedContextNode.contextKey = $scope.contextKey;
      $scope.selectedContextNode.pathInfoSet = [];
      $scope.generatePathInfo($scope.selectedContextNode, ".", ".", "1", false, null);
      $scope.initConformanceStatement();
      $scope.treeDataForContext.push($scope.selectedContextNode);
      $scope.dialogStep = 1;
    }

  };

  $scope.init=function(){
    $scope.selectedMessage = angular.copy(selectedMessage);
    $scope.selectedMessage.pathInfoSet = [];
    $scope.treeDataForMessage.push($scope.selectedMessage);
    $rootScope.processMessageTree($scope.selectedMessage);
    $scope.initConformanceStatement();
    $scope.initComplexStatement();
    if(contextPath){
      if(contextPath.indexOf('.') < 0){
        $scope.treeDataForContext = [];
        $scope.contextKey = new ObjectId().toString();
        $scope.selectedContextNode = $scope.selectedMessage;
        $scope.selectedContextNode.conformanceStatements = angular.copy(currentConformanceStatements);
        $scope.selectedContextNode.contextKey = $scope.contextKey;
        $scope.selectedContextNode.pathInfoSet = [];
        $scope.treeDataForContext.push($scope.selectedContextNode);
        $scope.dialogStep = 1;
      }else {
        $scope.travelByContextPath($scope.selectedMessage, contextPath);
      }
    }else {
      $scope.generatePathInfo($scope.selectedMessage, ".", ".", "1", false, null, 'default');
    }
    $scope.selectedMessage.childrenVisible = true;
  }
});
