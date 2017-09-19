/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('PredicateSegmentCtrl', function($scope, config, tables, selectedSegment, selectedNode, $rootScope, $q, $mdDialog, mode, currentPredicate){
  $scope.dialogStep = 0;
  $scope.config = config;
  $scope.tables = tables;
  $scope.selectedSegment = angular.copy(selectedSegment);
  $scope.selectedNode = selectedNode;
  $scope.constraintType = 'Plain';
  $scope.constraints = [];
  $scope.firstConstraint = null;
  $scope.secondConstraint = null;
  $scope.compositeType = null;
  $scope.complexConstraint = null;
  $scope.changed = false;
  $scope.existingPredicate = null;
  $scope.tempPredicates = [];
  $scope.predicateData = null;

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

    if (current.type === 'segment') {
      var seg = current;
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
    } else if (current.type === 'field' || current.type === 'component') {
      var dt = current.child;
      for (var i in dt.components) {
        var c = dt.components[i];
        c.pathInfoSet = angular.copy(current.pathInfoSet);
        var childPositionNumber = c.position;
        var childLocationName = c.position;
        var childNodeName = c.name;
        var childInstanceNumber = "1";
        var childisInstanceNumberEditable = false;
        if($rootScope.datatypesMap[c.datatype.id]){
          var child = angular.copy($rootScope.datatypesMap[c.datatype.id]);
          child.id = new ObjectId().toString();
          c.child = child;
          $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
        }

      }
    }
  };

  $scope.generatePathInfo($scope.treeDataForContext[0], ".", ".", "1", false);
  $scope.treeDataForContext[0].childrenVisible = true;

  $scope.getDialogStyle = function(){
    if ($scope.dialogStep === 0) return "width: 70%";
    if ($scope.dialogStep === 1) return "width: 90%";
    if ($scope.dialogStep === 2) return "width: 50%";
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

    if(mode === 'pc'){
      var newPath = $scope.selectedNode.path.split(".");

      if(newPath.length === 1){
        $scope.existingPredicate.constraintTarget = ".";
      }else if(newPath.length > 1){
        newPath.shift();
        $scope.existingPredicate.constraintTarget = newPath.join("[1].") + "[1]";
      }
    }else {
      $scope.existingPredicate.constraintTarget = $scope.selectedNode.position + '[1]';
    }
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

  $scope.beforePredicateDrop = function() {
    var deferred = $q.defer();

    if ($scope.draggingStatus === 'PredicateDragging') {
      $scope.predicateData = null;
      deferred.resolve();
    } else {
      deferred.reject();
    }
    return deferred.promise;
  };

  $scope.draggingPredicate = function(event, ui, nodeData) {
    $scope.draggingStatus = 'PredicateDragging';
  };

  $scope.draggingNodeFromContextTree = function(event, ui, data) {
    $scope.draggingStatus = 'ContextTreeNodeDragging';
  };

  $scope.setChanged = function() {
    $scope.changed = true;
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
      contraintType: null,
      value: null,
      value2: null,
      valueSetId: null,
      bindingStrength: 'R',
      bindingLocation: '1'
    });
  };

  $scope.initComplexPredicate = function() {
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
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

          if (i === $scope.newConstraint.pathInfoSet_1.length - 1) {
            locationPath = locationPath + " (" + pathInfo.nodeName + ")";
          }
        }
      }

      $scope.newConstraint.position_1 = positionPath.substr(1);
      $scope.newConstraint.location_1 = selectedSegment.name + '-' + locationPath.substr(1);
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
      $scope.newConstraint.location_2 = selectedSegment.name + '-' + locationPath.substr(1);
    }
  };

  $scope.findExistingPredicate = function() {
    if(mode === 'pc'){
      return angular.copy(currentPredicate);
    }else {
      for (var i = 0, len1 = $scope.selectedSegment.predicates.length; i < len1; i++) {
        if ($scope.selectedSegment.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
          return $scope.selectedSegment.predicates[i];
      }
    }
  };

  $scope.deletePredicate = function() {
    $scope.existingPredicate = null;
    $scope.setChanged();
  };

  $scope.deleteTempPredicate = function(predicate) {
    $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
  };

  $scope.deletePredicateByTarget = function() {
    for (var i = 0, len1 = $scope.selectedSegment.predicates.length; i < len1; i++) {
      if ($scope.selectedSegment.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
        $scope.selectedSegment.predicates.splice($scope.selectedSegment.predicates.indexOf($scope.selectedSegment.predicates[i]), 1);
        return true;
      }
    }
    return false;
  };

  $scope.addComplexPredicate = function() {
    $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
    $scope.complexConstraint.constraintId = $scope.newConstraint.segment + '-' + $scope.selectedNode.position;
    $scope.tempPredicates.push($scope.complexConstraint);
    $scope.initComplexPredicate();
    $scope.changed = true;
  };

  $scope.addFreeTextPredicate = function() {
    var cp = $rootScope.generateFreeTextPredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
    $scope.tempPredicates.push(cp);
    $scope.changed = true;
    $scope.initPredicate();
  };

  $scope.addPredicate = function() {
    var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
    $scope.tempPredicates.push(cp);
    $scope.changed = true;
    $scope.initPredicate();
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.saveClose = function() {
    if(mode === 'pc'){
      $mdDialog.hide($scope.existingPredicate);
    }else {
      $scope.deletePredicateByTarget();
      $scope.selectedSegment.predicates.push($scope.existingPredicate);
      $mdDialog.hide($scope.selectedSegment);
    }
  };

  $scope.initPredicate();
  $scope.initComplexPredicate();
  $scope.existingPredicate = $scope.findExistingPredicate();

  if(!$scope.existingPredicate) $scope.dialogStep = 1;

});
