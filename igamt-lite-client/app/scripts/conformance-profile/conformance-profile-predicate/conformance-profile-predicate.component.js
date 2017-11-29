/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('GlobalPredicateCtrl', function($scope, segmentsMap, config, tables, selectedMessage, $rootScope, $q, $mdDialog, selectedNode, currentPredicate, mode) {
  $scope.dialogStep = 0;
  $scope.segmentsMap = segmentsMap;
  $scope.config = config;
  $scope.tables = tables;
  $scope.selectedMessage = angular.copy(selectedMessage);
  $scope.selectedMessage.pathInfoSet = [];
  $scope.selectedNode = selectedNode;
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.newComplexConstraintId = null;
  $scope.targetContext = null;
  $scope.treeDataForMessage = [];
  $scope.treeDataForContext = [];
  $scope.constraintType = 'Plain';
  $scope.firstNodeData = null;
  $scope.secondNodeData = null;
  $scope.changed = false;
  $scope.treeDataForMessage.push($scope.selectedMessage);
  $scope.draggingStatus = null;
  $scope.listGlobalPredicates = [];
  $scope.existingPredicate = null;
  $scope.existingContext = null;
  $scope.tempPredicates = [];
  $scope.contextKey = null;

  $scope.getDialogStyle = function(){
    if ($scope.dialogStep === 0) return "width: 70%";
    if ($scope.dialogStep === 1) return "width: 30%";
    if ($scope.dialogStep === 2) return "width: 90%";
    if ($scope.dialogStep === 3) return "width: 50%";
    return "width: 90%";
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

  $scope.goNext = function() {
    $scope.dialogStep = $scope.dialogStep + 1;
    if($scope.dialogStep === 1){
        $scope.contextKey = null;
        $scope.selectedContextNode = null;
        $scope.initPredicate();
        $scope.initComplexPredicate();
        $scope.treeDataForContext=[];
    }
  };

  $scope.goBack = function () {
    $scope.dialogStep = $scope.dialogStep - 1;
  };

  $scope.selectPredicate = function (c){
    angular.forEach($scope.tempPredicates, function(p) {
      p.selected = false;
    });
    c.selected = true;
    $scope.existingPredicate = c;
    $scope.existingContext = $scope.selectedContextNode;

    if (!$scope.existingContext.positionPath || $scope.existingContext.positionPath == '') {
      $scope.existingPredicate.constraintTarget = $scope.selectedNode.path;
    } else {
      $scope.existingPredicate.constraintTarget = $scope.selectedNode.path.replace($scope.existingContext.positionPath + '.', '');
    }
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
    if ($scope.draggingStatus === 'ContextTreeNodeDragging') {
      deferred.resolve();
    } else {
      deferred.reject();
    }
    return deferred.promise;
  };


  $scope.selectContext = function(selectedContextNode) {
    if($scope.selectedContextNode && $scope.selectedContextNode  === selectedContextNode){
      $scope.contextKey = null;
      $scope.selectedContextNode = null;
      $scope.initPredicate();
      $scope.initComplexPredicate();
      $scope.treeDataForContext=[];
    }else {
      $scope.contextKey = new ObjectId().toString();
      $scope.selectedContextNode = selectedContextNode;
      $scope.selectedContextNode.contextKey = $scope.contextKey;
      $scope.selectedContextNode.pathInfoSet = [];
      $scope.generatePathInfo($scope.selectedContextNode, '.', '.', '1', false, null);
      $scope.initPredicate();
      $scope.initComplexPredicate();
      $scope.treeDataForContext=[];
      $scope.treeDataForContext.push($scope.selectedContextNode);
    }
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
  }

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
  }

  $scope.draggingPredicate = function(event, ui, nodeData) {
    $scope.draggingStatus = 'PredicateDragging';
  };

  $scope.draggingNodeFromContextTree = function(event, ui, nodeData) {
    $scope.draggingStatus = 'ContextTreeNodeDragging';
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
    if (current.type === 'message' || current.type === 'group') {
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
    } else if (current.type === 'segmentRef') {
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

  $scope.initComplexPredicate = function() {
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
  }

  $scope.initPredicate = function() {
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
      constraintId: null,
      contraintType: null,
      value: null,
      value2: null,
      valueSetId: null,
      bindingStrength: 'R',
      bindingLocation: '1',
      trueUsage: null,
      falseUsage: null,
    });
  };

  $scope.addFreeTextPredicate = function() {
    var cp = $rootScope.generateFreeTextPredicate('NOT Assigned', $scope.newConstraint);
    $scope.tempPredicates.push(cp);
    $scope.initPredicate();
  };

  $scope.addPredicate = function() {
    var cp = $rootScope.generatePredicate('NOT Assigned', $scope.newConstraint);
    $scope.tempPredicates.push(cp);
    $scope.initPredicate();
  };

  $scope.addComplexPredicate = function() {
    $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    $scope.tempPredicates.push($scope.complexConstraint);
    $scope.initComplexPredicate();
  };

  $scope.deletePredicate = function() {
    $scope.existingPredicate = null;
    $scope.existingContext = null;
    $scope.setChanged();
  };

  $scope.deleteTempPredicate = function(predicate) {
    $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.saveClose = function() {
    if(mode === 'pc'){
      $scope.existingPredicate.context = {};
      $scope.existingPredicate.context.type = 'message';
      $scope.existingPredicate.context.id = selectedMessage.id;
      if($scope.existingContext.positionPath && $scope.existingContext.positionPath !== '.'){
        $scope.existingPredicate.context.path = $scope.existingContext.positionPath.split('[1]').join('');
      }
      $mdDialog.hide($scope.existingPredicate);
    }else {
      $scope.deleteExistingPredicate($scope.selectedMessage);
      if ($scope.existingPredicate != null) {
        $scope.addChangedPredicate($scope.selectedMessage);
      }
      $mdDialog.hide($scope.selectedMessage);
    }
  };

  $scope.addChangedPredicate = function(current) {
    if (current.positionPath == $scope.existingContext.positionPath) {
      current.predicates.push($scope.existingPredicate);
    }

    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        $scope.addChangedPredicate(current.children[i]);
      }
    }
  }

  $scope.deleteExistingPredicate = function(current) {
    if (current.predicates && current.predicates.length > 0) {
      var toBeDeletePredicate = null;
      for (var i in current.predicates) {
        var positionPath = null;
        if (current.positionPath == null || current.positionPath == '') {
          var positionPath = current.predicates[i].constraintTarget;
        } else {
          var positionPath = current.positionPath + '.' + current.predicates[i].constraintTarget;
        }
        if (positionPath == $scope.selectedNode.path) {
          toBeDeletePredicate = i;
        }
      }
      if (toBeDeletePredicate != null) current.predicates.splice(toBeDeletePredicate, 1);
    }
    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        $scope.deleteExistingPredicate(current.children[i]);
      }
    }
  };

  $scope.findAllGlobalPredicates = function() {
    if(mode === 'pc'){
      $scope.listGlobalPredicates = [];
      $scope.travelMessage($scope.selectedMessage, '');
      $scope.existingPredicate = angular.copy(currentPredicate);
    }else {
      $scope.listGlobalPredicates = [];
      $scope.travelMessage($scope.selectedMessage, '');
    }
  };

  $scope.travelMessage = function(current, parrentPositionPath) {
    if (current.predicates && current.predicates.length > 0) {
      $scope.listGlobalPredicates.push(current);

      for (var i in current.predicates) {
        var positionPath = null;
        if (current.positionPath == null || current.positionPath == '') {
          var positionPath = current.predicates[i].constraintTarget;
        } else {
          var positionPath = current.positionPath + '.' + current.predicates[i].constraintTarget;
        }
        if (positionPath == $scope.selectedNode.path) {
          $scope.existingPredicate = current.predicates[i];
          $scope.existingContext = current;
          $scope.selectContext(current);
        }
      }
    }

    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        var segGroup = current.children[i];

        if (parrentPositionPath == '') {
          segGroup.positionPath = segGroup.position + '[1]';
        } else {
          segGroup.positionPath = parrentPositionPath + '.' + segGroup.position + '[1]';
        }

        $scope.travelMessage(segGroup, segGroup.positionPath);
      }
    }
  };

  $scope.initPredicate();
  $scope.initComplexPredicate();
  $scope.findAllGlobalPredicates();
  $scope.generatePathInfo($scope.selectedMessage, '.', '.', '1', false, null, 'default');
  $scope.selectedMessage.childrenVisible = true;

  if(!$scope.existingPredicate) $scope.dialogStep = 1;
});
