/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ListProfileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, PcLibraryService, PcService, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, MessageService, TableService, $mdDialog) {

  $scope.changes = false;

  $scope.editProfileComponent = false;
  $scope.edit = false;

  $scope.tabStatus = {
    active: 1
  };
  $scope.defTabStatus = {
    active: 1
  };
  $scope.deltaTabStatus = {
    active: 0
  };

  $scope.accordStatus = {
    isCustomHeaderOpen: false,
    isFirstOpen: false,
    isSecondOpen: true,
    isThirdOpen: false

  };

  $scope.editL = function(node){
    node.attributes.minLength = null;
    node.attributes.maxLength = null;
    node.minLength =  node.attributes.oldMinLength != 'NA' ? node.attributes.oldMinLength: '';
    node.maxLength =  node.attributes.oldMaxLength != 'NA' ? node.attributes.oldMaxLength: '';
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.editConfL = function(node){
    node.confLength =  node.attributes.oldConfLength != 'NA' ? node.attributes.oldConfLength : '';
    node.attributes.confLength =  null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.clearConfL = function(node){
    node.confLength = "NA";
    node.attributes.confLength = "NA";
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.clearL = function(node){
    node.minLength = "NA";
    node.maxLength = "NA";
    node.attributes.minLength = "NA";
    node.attributes.maxLength = "NA";
    $rootScope.recordChanged ();
  };

  // $scope.confLengthPattern= '[1-9]\\d*[#=]{0,1}';


  $scope.redirectVS = function(binding) {

    TableService.getOne(binding.tableId).then(function(valueSet) {
      var modalInstance = $modal.open({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        resolve: {
          destination: function() {
            return valueSet;
          }
        }



      });
      modalInstance.result.then(function() {
        $rootScope.editTable(valueSet);
      });
    });
  };

  $scope.findPredicate = function(node){
    if(node.attributes.predicate) return node.attributes.predicate;
    if(node.oldPredicate) return node.oldPredicate;
    return null;
  };

  $scope.findingPredicatesFromDtContexts = function(node) {
    var result = null;
    if (node && node.type === 'component') {
      console.log("----");
      console.log(DatatypeService.getDatatypeLevelPredicates(node));
    }
  };

  $scope.getValueSetContext = function(node) {
    if (node.path) {
      var context = node.path.split(".");
      return context[0];
    }
  };

  $scope.print=function(x){

    console.log(x);
  }
  $scope.findingBindingsPc = function(node) {
    var result = [];

    if (node && (node.type === "field" || node.type === "component")) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      if (!node.valueSetBindings || node.valueSetBindings.length <= 0) {
        if (node.from === "message") {
          result = _.filter(node.oldValueSetBindings, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].bindingFrom = 'message';
          }
        } else if (node.from === "segment") {
          result = _.filter(node.oldValueSetBindings, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].bindingFrom = 'segment';
          }
        }


      } else {
        if (node.from === "message") {
          result = _.filter(node.valueSetBindings, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].bindingFrom = 'message';
          }
        } else if (node.from === "segment") {
          result = _.filter(node.valueSetBindings, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].bindingFrom = 'segment';
          }
        }

      }

      if (result && result.length > 0) {
        return result;
      }


    }

    return result;
  };

  $scope.isAvailableForValueSet = function(node) {

    if (node && (node.type === "field" || node.type === "component")) {
      var currentDT = $rootScope.datatypesMap[node.attributes.oldDatatype.id];

      if (currentDT && _.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT) {
          return valueSetAllowedDT == currentDT.name;
        })) return true;
    }



    return false;
  };

  $scope.printNode = function (node) {
    console.log(node);
  };

  $scope.isAvailableConstantValue = function(node) {
    if (node.type === "field" || node.type === "component") {
      if ($scope.hasChildren(node)) return false;
      var bindings = $scope.findingBindingsPc(node);
      if (bindings && bindings.length > 0) return false;
      if ($rootScope.datatypesMap[node.datatype.id].name == 'ID' || $rootScope.datatypesMap[node.datatype.id].name == "IS") return false;
      return true;
    } else {
      return false;
    }

  };

  $scope.findingSingleElement = function(node) {
    var result = null;

    if (node && (node.type === "field" || node.type === "component")) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      if (!node.singleElementValues || node.singleElementValues.length <= 0) {
        if (node.from === "message") {
          result = _.find(node.oldSingleElementValues, function(binding) { return binding.location == path; });
          if (result)
            result.from = 'message';
        }
      } else if (node.from === "segment") {
        result = _.find(node.oldSingleElementValues, function(binding) { return binding.location == path; });
        if (result)
          result.from = 'segment';

      }


    } else {
      if (node.from === "message") {
        result = _.find(node.singleElementValues, function(binding) { return binding.location == path; });
        if (result)
          result.from = 'message';

      } else if (node.from === "segment") {
        result = _.find(node.singleElementValues, function(binding) { return binding.location == path; });
        if (result)
          result.from = 'segment';


      }

    }

    if (result) {
      return result;
    }

    return result;
  };

  $scope.findingConfSt = function(node) {
    if (node) {
      if (!node.attributes.conformanceStatements) {
        if(node.attributes.oldConformanceStatements && node.attributes.oldConformanceStatements.length > 0) return node.attributes.oldConformanceStatements;
        return null;
      } else {
        return node.attributes.conformanceStatements;
      }
    }

  };
  $scope.findingCommentsInPC = function(node) {
    var result = [];

    if (node) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      if (!node.comments || node.comments.length <= 0) {
        if (node.from === "message") {
          result = _.filter(node.oldComments, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].from = 'message';
          }
        } else if (node.from === "segment") {
          result = _.filter(node.oldComments, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].from = 'segment';
          }
        }


      } else {
        if (node.from === "message") {
          result = _.filter(node.comments, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].from = 'message';
          }
        } else if (node.from === "segment") {
          result = _.filter(node.comments, function(binding) { return binding.location == path; });
          for (var i = 0; i < result.length; i++) {
            result[i].from = 'segment';
          }
        }

      }

      if (result && result.length > 0) {
        return result;
      }


    }

    return result;
  };

  $scope.editCommentDlg = function(node, comment, disabled, type) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditCommentMd.html',
      controller: 'EditCommentCtrlInPc',
      locals: {
        currentNode: node,
        currentComment:comment,
        disabled: disabled,
        type: type
      }

    });

    modalInstance.then(function() {
      $rootScope.recordChanged();
      $scope.editForm.$pristine=false;
    });
  };
  $scope.editModalBindingForMsg = function(node) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'TableMappingMessageCtrl.html',
      controller: 'TableBindingForPcCtrl',
      scope: $scope,        // use parent scope in template
      preserveScope: true,
      locals: {
        currentNode:node
      }

    });

    modalInstance.then(function(node) {
      console.log("node");
      console.log(node);
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
    });
  };
  $scope.showConfSt = false;
  $scope.seeConfSt = function(node) {
    $scope.showConfSt = true;
    $scope.currentNode = node;
  };
  $scope.unseeConfSt = function(node) {
    $scope.showConfSt = false;
    $scope.currentNode = null;
  };
  $scope.hasDynamicMapping = function(node) {
    if (node.type === "segmentRef") {
      var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
        return item.hl7Version == $rootScope.segmentsMap[node.attributes.ref.id].hl7Version && item.segmentName == $rootScope.segmentsMap[node.attributes.ref.id].name;
      });
      if (mappingStructure) {
        return true;
      }
    }
    return false;
  };

  $scope.findDynamicMapping = function(node) {
    if (node.type === "segmentRef") {
      if (node.attributes.dynamicMappingDefinition && node.attributes.dynamicMappingDefinition.dynamicMappingItems.length > 0) {
        return node.attributes.dynamicMappingDefinition;
      } else {
        return node.attributes.oldDynamicMappingDefinition;
      }
    }
    return null;
  };
  $scope.openAddDynamicMappingDialog = function(node, context) {
    $mdDialog.show({
      templateUrl: 'AddDynamicMappingCtrlInPc.html',
      parent: angular.element(document).find('body'),
      controller: 'AddDynamicMappingCtrlInPc',
      locals: {
        node: node,
        context: context
      }

    }).then(function(mapping) {
      if (mapping) {
        console.log(mapping);
        node.attributes.dynamicMappingDefinition = mapping;
        $rootScope.recordChanged();
        console.log("calling")
      }
    });
  };
  $scope.hasCoConstraints = function(node) {
    if (node.type === "segmentRef") {
      return true
    }
    return false;
  };

  $scope.findCoConstraints = function(node) {
    if (node.type === "segmentRef") {
      if (node.attributes.coConstraintsTable && node.attributes.coConstraintsTable.rowSize > 0) {
        return node.attributes.coConstraintsTable;
      } else {
        return node.attributes.oldCoConstraintsTable;
      }
    }
    return null;
  };
  $scope.openAddCoConstraintsDialog = function(node, context) {
    $mdDialog.show({
      templateUrl: 'AddCoConstraintCtrlInPc.html',
      parent: angular.element(document).find('body'),
      controller: 'AddCoConstraintCtrlInPc',
      locals: {
          node: node,
          context: context
      }

    }).then(function(coCon) {
      if (coCon) {
        console.log(coCon);
        node.attributes.coConstraintsTable = coCon;
        $rootScope.recordChanged();
        $scope.editForm.$pristine=false;
      }
    });
  };

  $scope.openDialogPCMessageConformanceStatements = function(node) {
    var selectedMessage = $rootScope.messagesMap[node.source.messageId];
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'GlobalConformanceStatementCtrl.html',
      controller: 'GlobalConformanceStatementCtrl',
      locals: {
        selectedMessage : selectedMessage,
        contextPath : node.path,
        currentConformanceStatements : $scope.findingConfSt(node),
        segmentsMap : $rootScope.segmentsMap,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "pc"
      }
    }).then(function(obj) {
      if (obj) {
        node.attributes.conformanceStatements = obj.conformanceStatements;
        $rootScope.recordChanged ();
      }
    });
  };

  $scope.openDialogPCSegmentConformanceStatements = function(node) {
    var selectedSegment = $rootScope.segmentsMap[node.attributes.ref.id];
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'ConformanceStatementSegmentCtrl.html',
      controller: 'ConformanceStatementSegmentCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        selectedSegment : selectedSegment,
        currentConformanceStatements : $scope.findingConfSt(node),
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "pc"
      }

    }).then(function(obj) {
      if (obj) {
        node.attributes.conformanceStatements = obj.conformanceStatements;
        $rootScope.recordChanged ();
        $scope.editForm.$pristine=false;
      }
    });
  };

  $scope.openDialogPCSegmentPredicate = function(node) {
    var selectedSegment = $rootScope.segmentsMap[node.source.segmentId];
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'PredicateSegmentCtrl.html',
      controller: 'PredicateSegmentCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        selectedSegment: selectedSegment,
        currentPredicate : $scope.findPredicate(node),
        selectedNode: node,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "pc"
      }

    }).then(function(predicate) {
      if (predicate) {
        predicate.context = {};
        predicate.context.type = 'segment';
        predicate.context.id = selectedSegment.id;
        node.attributes.predicate = predicate;

        console.log(predicate);
        $rootScope.recordChanged ();
        $scope.editForm.$pristine=false;
      }
    }, function() {});
  };

  $scope.openDialogPCMessagePredicate = function(node) {
    var selectedMessage = $rootScope.messagesMap[node.source.messageId];
    var oldPath = node.path;
    var seletecdNode = node;
    seletecdNode.path = seletecdNode.path.substring(seletecdNode.path.indexOf('.') + 1);
    seletecdNode.path = seletecdNode.path.split('.').join('[1].') + '[1]';
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'GlobalPredicateCtrl.html',
      controller: 'GlobalPredicateCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        selectedMessage: selectedMessage,
        currentPredicate : $scope.findPredicate(node),
        selectedNode: seletecdNode,
        segmentsMap: $rootScope.segmentsMap,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "pc"
      }

    }).then(function(predicate) {
      if (predicate) {
        node.attributes.predicate = predicate;
        $rootScope.recordChanged ();
        $scope.editForm.$pristine=false;
      }
      node.path = oldPath;
    }, function() {
      node.path = oldPath;
    });
  };

  $scope.addSev = function(node) {
    var sev = {};
    var index = node.path.indexOf(".");
    sev.location = node.path.substr(index + 1);
    sev.value = '';
    sev.name = node.name;
    console.log(sev);
    node.singleElementValues = sev;
    $rootScope.recordChanged ();
    // $scope.editForm.$pristine=false;
  };
  $scope.findAllGlobalConstraints = function() {
    $scope.listGlobalConformanceStatements = [];
    $scope.listGlobalPredicates = [];
    $scope.travelMessage($rootScope.message, '');
  };

  $scope.travelMessage = function(current, positionPath) {
    if (current.conformanceStatements && current.conformanceStatements.length > 0) {
      $scope.listGlobalConformanceStatements=_.union(current.conformanceStatements,$scope.listGlobalConformanceStatements);
    }

    if (current.predicates && current.predicates.length > 0) {
      $scope.listGlobalPredicates.push(current);
    }

    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        var segGroup = current.children[i];

        if (positionPath == '') {
          segGroup.positionPath = segGroup.position + '[1]';
        } else {
          segGroup.positionPath = positionPath + '.' + segGroup.position + '[1]';
        }

        $scope.travelMessage(segGroup, segGroup.positionPath);
      }
    }
  };

  $scope.openDialogForEditSev = function(node) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditSingleElement.html',
      controller: 'EditSingleElementCtrlInPc',
      locals: {
        currentNode: node
      }

    });

    modalInstance.then(function(value) {
      if(value !=='cancel'){
          console.log(value);
          $scope.addSev(node);
          node.singleElementValues.value = value;
          $scope.initSev(node);
          $rootScope.recordChanged();
      }

    });
  };
  $scope.hasChildren = function(node) {
    if (node && node != null) {
      if (node.type === 'field' || node.type === 'component') {
        if (node.attributes.datatype) {
          return $rootScope.datatypesMap[node.attributes.datatype.id] && $rootScope.datatypesMap[node.attributes.datatype.id].components && $rootScope.datatypesMap[node.attributes.datatype.id].components.length > 0;

        } else {
          return $rootScope.datatypesMap[node.attributes.oldDatatype.id] && $rootScope.datatypesMap[node.attributes.oldDatatype.id].components && $rootScope.datatypesMap[node.attributes.oldDatatype.id].components.length > 0;

        }
      }
      return false;
    } else {
      return false;
    }

  };
  $scope.updatePosition = function() {
    console.log($rootScope.profileComponent);
    for (var i = 0; i < $rootScope.profileComponent.children.length; i++) {
      $rootScope.profileComponent.children[i].position = i + 1;
    }
    $scope.save();
  };
  $scope.addPComponents = function() {
    $mdDialog.show({
      templateUrl: 'addComponents.html',
      parent: angular.element(document).find('body'),
      controller: 'addComponentsCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {
        messages: angular.copy($rootScope.messages.children),
        segments: angular.copy($rootScope.segments),
        segmentsMap: angular.copy($rootScope.segmentsMap),
        datatypes: angular.copy($rootScope.datatypes),
        datatypesMap: $rootScope.datatypesMap,
        currentPc: $rootScope.profileComponent
      }

    }).then(function(results) {
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
      if ($scope.profileComponentParams) {
        $scope.profileComponentParams.refresh();
      }
    });

  };
  $scope.removePcEntry = function(node) {
    $rootScope.profileComponent.children = orderByFilter($rootScope.profileComponent.children, 'position');
    var index = $rootScope.profileComponent.children.indexOf(node);
    if (index > -1) $rootScope.profileComponent.children.splice(index, 1);
    for (var i = index; i < $rootScope.profileComponent.children.length; i++) {
      $rootScope.profileComponent.children[i].position--;
    }
    $rootScope.recordChanged ();
    //  $scope.editForm.$pristine=false;
    if ($scope.profileComponentParams) {
      $scope.profileComponentParams.refresh();
    }
  };

  $scope.addComponent = function() {

    PcLibraryService.addComponentToLib($rootScope.igdocument.id, $scope.newPc).then(function(ig) {
      $rootScope.igdocument.profile.profileComponentLibrary.children = ig.profile.profileComponentLibrary.children;
      if ($scope.profileComponentParams) {
        $scope.profileComponentParams.refresh();
      }
    });
  };
  $scope.save = function() {
    var children = $rootScope.profileComponent.children;
    var bindingParam = $rootScope.profileComponent.appliedTo;
    console.log("before");
    console.log($rootScope.profileComponent);

    PcService.save($rootScope.igdocument.profile.profileComponentLibrary.id, $rootScope.profileComponent).then(function(result) {
      // $rootScope.profileComponent = result;
      for (var i = 0; i < $rootScope.igdocument.profile.profileComponentLibrary.children.length; i++) {
        if ($rootScope.igdocument.profile.profileComponentLibrary.children[i].id === $rootScope.profileComponent.id) {
          $rootScope.igdocument.profile.profileComponentLibrary.children[i].name = $rootScope.profileComponent.name;
          $rootScope.igdocument.profile.profileComponentLibrary.children[i].comment = $rootScope.profileComponent.comment;
          $rootScope.igdocument.profile.profileComponentLibrary.children[i].description = $rootScope.profileComponent.description;
        }

      }
      for (var i = 0; i < $rootScope.profileComponents.length; i++) {
        if ($rootScope.profileComponents[i].id === $rootScope.profileComponent.id) {
          $rootScope.profileComponents[i] = $rootScope.profileComponent;
        }
      }
      $rootScope.profileComponentsMap[$rootScope.profileComponent.id] = $rootScope.profileComponent;

      $scope.changes = false;
      $scope.clearDirty();
      console.log("------Profile Component------");
      console.log(result);
    });
    // });
  };
  $scope.initSev = function(node) {
    if (node.singleElementValues && node.singleElementValues.value !== null && node.singleElementValues.location !== null) {
      node.sev = node.singleElementValues;
    } else {
      node.sev = node.oldSingleElementValues;
    }
  };
  $scope.cancelSev = function(node) {
    node.singleElementValues = null;
    $scope.initSev(node);
  };
  $scope.cancelBinding = function(node) {
    node.valueSetBindings = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;

  };
  $scope.cancelComments = function(node) {
    node.comments = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;

  };
  $scope.cancelPredicate = function(node) {
    node.attributes.predicate = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;

  };
  $scope.cancelConfSt = function(node) {
    node.attributes.conformanceStatements = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.cancelDynMap = function(node) {
    node.attributes.dynamicMappingDefinition = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.cancelCoCon = function(node) {
    node.attributes.coConstraintsTable = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.initUsage = function(node) {
    if (node.attributes.usage) {
      node.usage = node.attributes.usage;
    } else {
      node.usage = node.attributes.oldUsage;
    }
  };
  $scope.updateUsage = function(node) {
    if (node.usage === node.attributes.oldUsage) {
      node.attributes.usage = null;
    } else {
      node.attributes.usage = node.usage;
    }
  };
  $scope.cancelUsage = function(field) {
    field.attributes.usage = null;
    field.usage = field.attributes.oldUsage;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.initMinCard = function(node) {
    if (node.attributes.min) {
      node.min = node.attributes.min;
    } else {
      node.min = node.attributes.oldMin;
    }
  };
  $scope.updateMinCard = function(node) {
    if (parseInt(node.min) === node.attributes.oldMin) {
      node.attributes.min = null;
    } else {
      node.attributes.min = parseInt(node.min);
    }
  };

  $scope.cancelMinCard = function(field) {
    field.attributes.min = null;
    field.min = field.attributes.oldMin;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.cancelCard = function(field) {
    field.attributes.min = null;
    field.min = field.attributes.oldMin;
    field.attributes.max = null;
    field.max = field.attributes.oldMax;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.initMaxCard = function(node) {
    if (node.attributes.max) {
      node.max = node.attributes.max;
    } else {
      node.max = node.attributes.oldMax;
    }
  };
  $scope.updateMaxCard = function(node) {
    if (node.max === node.attributes.oldMax) {
      node.attributes.max = null;
    } else {
      node.attributes.max = node.max;
    }
  };


  $scope.cancelMaxCard = function(field) {
    field.attributes.max = null;
    field.max = field.attributes.oldMax;

    $rootScope.recordChanged ()
    $scope.editForm.$pristine=false;
  };
  $scope.initMinL = function(node) {
    if (node.attributes.minLength) {
      node.minLength = node.attributes.minLength;
    } else {
      node.minLength = node.attributes.oldMinLength;
    }
    console.log("initMinL, node.minLength=" + node.minLength + ", node.attributes.minLength=" + node.attributes.minLength + ", node.attributes.oldMinLength="+ node.attributes.oldMinLength);

  };

  $scope.initL = function(node) {
    if (node.attributes.minLength) {
      node.minLength = node.attributes.minLength;
    } else {
      node.minLength = node.attributes.oldMinLength;
    }
    if (node.attributes.maxLength) {
      node.maxLength = node.attributes.maxLength;
    } else {
      node.maxLength = node.attributes.oldMaxLength;
    }
  };

  $scope.updateMinL = function(node) {
    if (node.minLength === node.attributes.oldMinLength) {
      node.attributes.minLength = null;
    } else {
      node.attributes.minLength = node.minLength;
      if(node.minLength=='NA'){
          node.maxLength='NA';
          $scope.updateMaxL(node);
      }
    }
    // node.attributes.confLength = "NA";
    // node.confLength = node.attributes.confLength;
  };

  $scope.cancelL = function(field) {
    field.attributes.minLength = null;
    field.minLength = field.attributes.oldMinLength;
    field.attributes.maxLength = null;
    field.maxLength = field.attributes.oldMaxLength;
    // field.attributes.confLength = null;
    // field.confLength = field.attributes.oldConfLength;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.cancelMinL = function(field) {
    field.attributes.minLength = null;
    field.minLength = field.attributes.oldMinLength;

    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.initMaxL = function(node) {
    console.log("initMaxL is called ");
    if (node.attributes.maxLength) {
      node.maxLength = node.attributes.maxLength;
    } else {
      node.maxLength = node.attributes.oldMaxLength;
    }
    console.log("initMaxL, node.maxLength=" + node.maxLength + ",  node.attributes.maxLength=" +   node.attributes.maxLength + "node.attributes.oldMaxLength="+ node.attributes.oldMaxLength);

  };
  $scope.updateMaxL = function(node) {
    if (node.maxLength === node.attributes.oldMaxLength) {
      node.attributes.maxLength = null;
    } else {

      node.attributes.maxLength = node.maxLength;
        if(node.maxLength=='NA'){
            node.minLength='NA';
            $scope.updateMaxL(node);
        }

    }
    // node.attributes.confLength = "NA";
    // node.confLength = field.attributes.confLength;
  };
  $scope.cancelMaxL = function(field) {
    field.attributes.maxLength = null;
    field.maxLength = field.attributes.oldMaxLength;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };

  $scope.initConfL = function(node) {
    if (node.attributes.confLength) {
      node.confLength = node.attributes.confLength;
    } else {
      node.confLength = node.attributes.oldConfLength;
    }
    console.log("initMinL, node.confLength=" + node.confLength + ",   node.attributes.confLength=" + node.attributes.confLength + "node.attributes.oldConfLength="+ node.attributes.oldConfLength);

  };
  $scope.updateConfL = function(node) {
    console.log("updateConfL called");
    if (node.confLength === node.attributes.oldConfLength) {
      node.attributes.confLength = null;
    } else {
      node.attributes.confLength = node.confLength;
    }
    // node.attributes.minLength = "NA";
    // node.minLength = node.attributes.minLength;
    // node.attributes.maxLength = "NA";
    // node.maxLength = node.attributes.maxLength;
  };

  $scope.cancelConfL = function(field) {
    // field.attributes.minLength = null;
    // field.minLength = field.attributes.oldMinLength;
    // field.attributes.maxLength = null;
    // field.maxLength = field.attributes.oldMaxLength;
    field.attributes.confLength = null;
    field.confLength = field.attributes.oldConfLength;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.initDatatype = function(node) {
    if (node.attributes.datatype) {
      node.datatype = node.attributes.datatype;
    } else {
      node.datatype = node.attributes.oldDatatype;
    }
  };
  $scope.updateDatatype = function(node) {
    if (node.datatype.id === node.attributes.oldDatatype.id) {
      node.attributes.datatype = null;
    } else {
      node.attributes.datatype = node.datatype;
    }
  };
  $scope.cancelDatatype = function(field) {



    if($rootScope.datatypesMap[field.attributes.oldDatatype.id]){
      field.attributes.datatype = null;
      field.datatype = field.attributes.oldDatatype;
      $scope.editableDT = '';
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
    }else{
      $scope.cannotFindOldValue(field.attributes.oldDatatype);
    }

  };
  $scope.cannotFindOldValue= function(old){
    $mdDialog.show({
      templateUrl: 'cannotFindOld.html',
      controller: 'cannotFindOld',
      locals: {
        old:old
      }
    })
  }
  $scope.backDT = function() {
    $scope.editableDT = '';

  };
  $scope.showSelect=function(node){
    $scope.editableRef=node.id;

  };
  $scope.isSelectOpen=function (node) {
    return $scope.editableRef&&$scope.editableRef==node.id;
  };
  $scope.backRef=function(){
    $scope.editableRef=false;
  };
  $scope.cancelDefText = function(field) {
    field.attributes.text = null;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };
  $scope.cancelTables = function(field) {
    field.attributes.tables = field.attributes.oldTables;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  };


  $scope.cancelRef = function(field) {
    field.attributes.ref = field.attributes.oldRef;
    $rootScope.recordChanged ();
    $scope.editForm.$pristine=false;
  }
  $scope.editableDT = '';

  $scope.selectDT = function(field) {


    if (field.datatype && field.datatype !== "Others") {
      $scope.DTselected = true;
      $scope.editableDT = '';

      if (field.datatype.id === field.attributes.oldDatatype.id) {
        field.attributes.datatype = null;
        field.datatype = field.attributes.oldDatatype;
      } else {
        console.log("else");
        console.log(field);
        field.attributes.datatype = field.datatype;
        field.attributes.datatype = {};
        field.attributes.datatype.ext = field.datatype.ext;
        field.attributes.datatype.id = field.datatype.id;
        field.attributes.datatype.label = field.datatype.label;
        field.attributes.datatype.name = field.datatype.name;
        field.datatype = field.attributes.datatype;

      }


      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
      if ($scope.profileComponentParams)
        $scope.profileComponentParams.refresh();
      $scope.DTselected = false;

    } else {
      $scope.otherDT(field);
    }


  };
  $scope.editVSModal = function(field) {
    console.log("calling this ")
    var modalInstance = $modal.open({
      templateUrl: 'editVSModal.html',
      controller: 'EditVSCtrl',
      scope:$scope,

      resolve: {

        valueSets: function() {
          return $rootScope.tables;
        },

        field: function() {
          return field;
        }

      }
    });
    modalInstance.result.then(function(field) {
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;

    });

  };

  $scope.addComment = function(field) {
    var modalInstance = $modal.open({
      templateUrl: 'addCommentModal.html',
      controller: 'addCommentCtrl',
      windowClass: 'edit-VS-modal',
      resolve: {



        field: function() {
          return field;
        }

      }
    });
    modalInstance.result.then(function(field) {
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=true;

    });

  };
  $scope.addDefText = function(field) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'addDefTextModal.html',
      controller: 'addDefTextCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        field: field
      }

    });
    modalInstance.then(function(field) {
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;

    });

  };
  $scope.otherDT = function(field) {
    var modalInstance = $modal.open({
      templateUrl: 'otherDTModal.html',
      controller: 'otherDTCtrl',
      windowClass: 'edit-VS-modal',
      resolve: {

        datatypes: function() {
          return $rootScope.datatypes;
        },

        field: function() {
          return field.attributes;
        }

      }
    });
    modalInstance.result.then(function(attr) {
      console.log("===");
      console.log(attr);
      console.log(field);
      field.datatype = attr.datatype;
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
      $scope.editableDT = '';
      if ($scope.profileComponentParams) {
        $scope.profileComponentParams.refresh();
      }
    });

  };
  $scope.editableRef = false;
  $scope.editRef = function(field) {
    $scope.editableRef = field.id;
    $scope.segFlavors = [];
    for (var i = 0; i < $rootScope.segments.length; i++) {
      if ($rootScope.segments[i].name === field.attributes.ref.name) {
        $scope.segFlavors.push({
          id: $rootScope.segments[i].id,
          name: $rootScope.segments[i].name,
          ext: $rootScope.segments[i].ext,
          label: $rootScope.segments[i].label

        });
      }
    }
  };
  $scope.selectFlavor = function(field) {

    field.source.segmentId=field.attributes.ref.id;

    $rootScope.recordChanged();
    $scope.editForm.$pristine=false;
    $scope.editableRef = false;


  };

  $scope.editDT = function(field) {
    $scope.editableDT = field.id;
    $scope.editableDT = field.id;

    $scope.datatypes = [];
    angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
      if (dtLink.name && dtLink.name === field.datatype.name) {
        $scope.datatypes.push(dtLink);
      }
    });

  };


  $scope.showEditDynamicMappingDlg = function(node) {
    var modalInstance = $modal.open({
      templateUrl: 'DynamicMappingCtrl.html',
      controller: 'DynamicMappingCtrlInPc',
      windowClass: 'app-modal-window',
      resolve: {
        selectedNode: function(

        ) {
          return node;
        }
      }
    });
    modalInstance.result.then(function(node) {
      $scope.selectedNode = node;
      $rootScope.recordChanged ();
      $scope.editForm.$pristine=false;
      $scope.segmentsParams.refresh();
    }, function() {});
  };



});
