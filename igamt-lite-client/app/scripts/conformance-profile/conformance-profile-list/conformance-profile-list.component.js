/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('MessageListCtrl', function($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, $q, CloneDeleteSvc, MastermapSvc, FilteringSvc, MessageService, SegmentService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, TableService, DatatypeService, blockUI, ViewSettings, ValidationService, $mdDialog) {

  $scope.viewSettings = ViewSettings;

  $scope.accordStatus = {
    isCustomHeaderOpen: false,
    isFirstOpen: true,
    isSecondOpen: false,
    isThirdOpen: false,
    isFifthOpen: false,
    isSixOpen: false,
    isFirstDisabled: false
  };
  $scope.defTabStatus = {
    active: 1
  };

  $scope.deltaTabStatus = {
    active: 0
  };

  $scope.tabStatus = {
    active: 1
  };

  $scope.init = function() {
    $scope.accordStatus = {
      isCustomHeaderOpen: false,
      isFirstOpen: true,
      isSecondOpen: false,
      isThirdOpen: false,
      isFifthOpen: false,
      isSixOpen: false,
      isFirstDisabled: false
    };
    $scope.tabStatus = {
      active: 1
    };

    $scope.findAllGlobalConstraints();

  };
  $scope.validateMessage = function() {
    console.log($rootScope.message);
    ValidationService.validateMessage($rootScope.message, $rootScope.igdocument.profile.metaData.hl7Version).then(function(result) {
      $rootScope.validationMap = {};
      $rootScope.childValidationMap = {};
      $rootScope.showMsgErrorNotification = true;
      $rootScope.validationResult = result;
      console.log($rootScope.validationResult);
      $rootScope.buildValidationMap($rootScope.validationResult);
      console.log($rootScope.validationMap);
      console.log($rootScope.childValidationMap);


    }, function(error) {
      console.log(error);
    });
  };
  $scope.isMessageValidated = function() {
    if ($rootScope.message && ($rootScope.validationResult.targetId === $rootScope.message.id || $rootScope.childValidationMap[$rootScope.message.id])) {
      return true;
    } else {
      return false;
    }
  };
  $scope.setErrorNotification = function() {
    $rootScope.showMsgErrorNotification = !$rootScope.showMsgErrorNotification;
  };

  $scope.redirectSeg = function(segmentRef) {
    SegmentService.get(segmentRef.id).then(function(segment) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        locals: {
          destination: segment
        }

      });
      modalInstance.then(function(result) {
        if(result&&result!=='cancel'){
          $rootScope.editSeg(segment);
        }

      });



    });
  };
  $scope.redirectDT = function(datatype) {
    DatatypeService.getOne(datatype.id).then(function(datatype) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        locals: {
          destination:  datatype
        }

      });
      modalInstance.then(function(result) {
        if(result&&result!=='cancel') {
          $rootScope.editDatatype(datatype);
        }
      });


    });

  };
  $scope.redirectVS = function(binding) {
    TableService.getOne(binding.tableId).then(function(valueSet) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        locals: {
          destination: valueSet
        }
      });
      modalInstance.then(function(result) {
        if(result&&result!=='cancel') {
          $rootScope.editTable(valueSet);
        }
      });
    });
  };

  $scope.deleteConformanceStatement = function(cs, target) {
    target.conformanceStatements.splice(target.conformanceStatements.indexOf(cs), 1);
    $scope.setDirty();
  };


  $scope.OtoX = function(message) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'OtoX.html',
      controller: 'OtoXCtrl',
      locals: {
        message: message
      },
      scope: $scope,
      preserveScope:true


    });
    modalInstance.then(function() {
      $scope.setDirty();

      if ($scope.messagesParams)
        $scope.messagesParams.refresh();
    });
  };

  $scope.expanded = true;

  $scope.expandAll = function() {
    $scope.expanded = !$scope.expanded;

    $('#messageTable').treetable('expandAll');
  };

  $scope.collapseAll = function() {
    $scope.expanded = !$scope.expanded;
    $('#messageTable').treetable('collapseAll');
  };

  $scope.copy = function(message) {
    CloneDeleteSvc.copyMessage(message);
    $rootScope.$broadcast('event:SetToC');
  };

  $scope.reset = function() {
    blockUI.start();
    MessageService.reset();
    $rootScope.processMessageTree($rootScope.message);
    $scope.findAllGlobalConstraints();
    cleanState();
    blockUI.stop();
  };

  var findIndex = function(id) {
    for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
      if ($rootScope.igdocument.profile.messages.children[i].id === id) {
        return i;
      }
    }
    return -1;
  };

  var indexIn = function(id, collection) {
    for (var i = 0; i < collection.length; i++) {
      if (collection[i].id === id) {
        return i;
      }
    }
    return -1;
  };

  var cleanState = function() {
    $rootScope.addedSegments = [];
    $rootScope.addedDatatypes = [];
    $rootScope.addedTables = [];
    $scope.clearDirty();
    $scope.editForm.$setPristine();
    $scope.editForm.$dirty = false;
    $rootScope.clearChanges();
    if ($scope.messagesParams) {
      $scope.messagesParams.refresh();
    }
  };
  $scope.callMsgDelta = function() {
    $rootScope.$emit("event:openMsgDelta");
  };

  $scope.save = function() {
    $scope.saving = true;
    var message = $rootScope.message;
    $rootScope.$emit("event:saveMsgForDelta");
    console.log($rootScope.message);
    MessageService.save(message).then(function(result) {
      $rootScope.message.dateUpdated = result.dateUpdated;
      $rootScope.$emit("event:updateIgDate");
      var index = findIndex(message.id);
      if (index < 0) {
        $rootScope.igdocument.profile.messages.children.splice(0, 0, message);
      }

      MessageService.saveNewElements().then(function() {
        MessageService.merge($rootScope.messagesMap[message.id], message);
        cleanState();
      }, function(error) {
        $rootScope.msg().text = "Sorry an error occured. Please try again";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });
    }, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });
  };





  $scope.delete = function(message) {
    CloneDeleteSvc.deleteMessage(message);
    $rootScope.$broadcast('event:SetToC');
  };


  $scope.deleteSeg = function(segmentRefOrGrp) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeleteSegmentRefOrGrp.html',
      controller: 'DeleteSegmentRefOrGrpCtrl',
      locals: {
        segOrGrpToDelete:segmentRefOrGrp
      }



    });
    modalInstance.then(function() {
      $scope.setDirty();

      if ($scope.messagesParams)
        $scope.messagesParams.refresh();
    });
  };
  $scope.editableGrp = '';

  $scope.isGroupNameValid = function (gName) {
    var pattern = new RegExp("\^[A-Z0-9_\.]*$");

    return pattern.test(gName);

  };

  $scope.editGrp = function(group, message) {
    $scope.path = group.path.replace(/\[[0-9]+\]/g, '');
    $scope.path = $scope.path.split(".");
    MessageService.findParentByPath($scope.path, message).then(function() {});


    $scope.editableGrp = group.obj.id;
    $scope.grpName = group.obj.name;
    $scope.group = group.obj.position;


  };
  $scope.backGrp = function() {
    $scope.editableGrp = '';
  };
  $scope.applyGrp = function(group, name, position) {
    blockUI.start();
    $scope.editableGrp = '';
    if (group) {
      group.obj.name = name;


    }
    if (position) {
      MessageService.updatePosition($rootScope.segParent.children, group.obj.position - 1, position - 1);
    }
    $scope.setDirty();

    $rootScope.processMessageTree($rootScope.message);
    if ($scope.messagesParams)
      $scope.messagesParams.refresh();
    $scope.Posselected = false;

    blockUI.stop();
  };



  $scope.editableSeg = '';

  $scope.editSgmt = function(segmentRef, message) {
    blockUI.start();
    $scope.path = segmentRef.path.replace(/\[[0-9]+\]/g, '');
    $scope.path = $scope.path.split(".");
    MessageService.findParentByPath($scope.path, message).then(function() {
      // $scope.parentLength=$rootScope.segParent.children.length;
    });

    $scope.editableSeg = segmentRef.obj.id;
    $scope.loadLibrariesByFlavorName = function() {
      var delay = $q.defer();

      $scope.ext = null;
      $scope.results = [];
      $scope.tmpResults = [];
      $scope.results = $scope.results.concat(filterFlavors($rootScope.igdocument.profile.segmentLibrary, segmentRef.obj.ref.name));
      $scope.results = _.uniq($scope.results, function(item, key, a) {
        return item.id;
      });
      $scope.tmpResults = [].concat($scope.results);
      //                SegmentLibrarySvc.findLibrariesByFlavorName(segmentRef.obj.ref.name, 'HL7STANDARD', $rootScope.igdocument.profile.metaData.hl7Version).then(function(libraries) {
      //                    if (libraries != null) {
      //                        _.each(libraries, function(library) {
      //                            $scope.results = $scope.results.concat(filterFlavors(library, segmentRef.obj.ref.name));
      //
      //                        });
      //                    }
      //
      //                    $scope.results = _.uniq($scope.results, function(item, key, a) {
      //                        return item.id;
      //                    });
      //
      //                    $scope.tmpResults = [].concat($scope.results);
      //                    console.log($scope.tmpResults);
      //
      //                    delay.resolve(true);
      //                }, function(error) {
      //                    $rootScope.msg().text = "Sorry could not load the segments";
      //                    $rootScope.msg().type = error.data.type;
      //                    $rootScope.msg().show = true;
      //                    delay.reject(error);
      //                });
      blockUI.stop();
      return delay.promise;

    };

    var filterFlavors = function(library, name) {
      var results = [];
      _.each(library.children, function(link) {
        console.log("++++++++++");
        console.log(link);
        if (link.name === name) {
          link.libraryName = library.metaData.name;
          link.hl7Version = $rootScope.segmentsMap[link.id].hl7Version;
          //link.hl7Version = library.metaData.hl7Version;
          results.push(link);
        }
      });
      return results;
    };
    $scope.loadLibrariesByFlavorName().then(function(done) {
      // $scope.selection.selected = $scope.currentSegment.id;
      // $scope.selectSegment($scope.currentSegment);
    });


  };

  $scope.backSeg = function() {
    blockUI.start();
    $scope.editableSeg = '';
    // segmentRef.obj.position=$scope.initialPosition;
    blockUI.stop();
  };





  $scope.selectSeg = function(segmentRef, segment) {
    $scope.Segselected = true;
    $scope.editableSeg = '';

    blockUI.start();
    console.log(segment);
    console.log(segmentRef);


    segmentRef.obj.ref.id = JSON.parse(segment).id;
    segmentRef.obj.ref.ext = JSON.parse(segment).ext;
    segmentRef.obj.ref.label = JSON.parse(segment).label;
    segmentRef.obj.ref.name = JSON.parse(segment).name;



    console.log(segmentRef);
    $scope.setDirty();
    var ref = $rootScope.segmentsMap[segmentRef.obj.ref.id];
    $rootScope.processMessageTree($rootScope.message);


    if ($scope.messagesParams)
      $scope.messagesParams.refresh();
    $scope.Segselected = false;
    $scope.Posselected = false;
    blockUI.stop();

  };

  $scope.selectPos = function(segmentRef, position) {

    // $scope.Posselected = true;
    $scope.editableSeg = '';

    MessageService.updatePosition($rootScope.segParent.children, segmentRef.obj.position - 1, position - 1);
    $scope.setDirty();

    $rootScope.processMessageTree($rootScope.message);


    if ($scope.messagesParams)
      $scope.messagesParams.refresh();




  };


  // $scope.selectSeg = function() {

  //     $scope.Segselected = true;




  // };

  $scope.selectedSeg = function() {
    return ($scope.tempSeg !== undefined);
  };
  $scope.unselectSeg = function() {
    $scope.tempSeg = undefined;
    //$scope.newSeg = undefined;
  };
  $scope.isSegActive = function(id) {
    if ($scope.tempSeg) {
      return $scope.tempSeg.id === id;
    } else {
      return false;
    }

  };




  $scope.goToSegment = function(segmentId) {
    $scope.$emit('event:openSegment', $rootScope.segmentsMap[segmentId]);
  };
  $scope.segOption = [

    ['Add segment',
      function($itemScope) {
        $scope.addSegmentModal($itemScope.node);
        /*
         console.log($itemScope);
         $itemScope.node.children.push($rootScope.messageTree.children[0]);
         if ($scope.messagesParams) {
         $scope.messagesParams.refresh();
         }
         */

      }
    ],
    null, ['Add group',
      function($itemScope) {
        $scope.addGroupModal($itemScope.node);
        //$itemScope.node.children.push($rootScope.messageTree.children[3]);
        //$scope.messagesParams.refresh();
      }
    ]

  ];

  $scope.addSegmentModal = function(place) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'AddSegmentModal.html',
      controller: 'AddSegmentCtrl',
      scope: $rootScope,
      preserveScope: true,
      locals: {
        segments: $rootScope.segments,
        place:  place,
        messageTree:$rootScope.messageTree
      }


    });
    modalInstance.then(function(segment) {

       console.log($rootScope.messageTree);
      $scope.setDirty();


      if ($scope.messagesParams)
        $scope.messagesParams.refresh();
    });
  };
  $scope.addGroupModal = function(place) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'AddGroupModal.html',
      scope: $rootScope,
      preserveScope: true,
      controller: 'AddGroupCtrl',
      locals: {
        segments: $rootScope.segments,
        place:  place,
        messageTree: $rootScope.messageTree
      }

    });
    modalInstance.then(function(segment) {
      $scope.setDirty();

      if ($scope.messagesParams)
        $scope.messagesParams.refresh();
    });
  };


  $scope.showSelectSegmentFlavorDlg = function(segmentRef) {
    console.log(segmentRef);
    var modalInstance = $modal.open({
      templateUrl: 'SelectSegmentFlavor.html',
      controller: 'SelectSegmentFlavorCtrl',
      windowClass: 'flavor-modal-window',
      resolve: {
        currentSegment: function() {
          return $rootScope.segmentsMap[segmentRef.ref.id];
        },
        datatypeLibrary: function() {
          return $rootScope.igdocument.profile.datatypeLibrary;
        },
        segmentLibrary: function() {
          return $rootScope.igdocument.profile.segmentLibrary;
        },

        hl7Version: function() {
          return $rootScope.igdocument.profile.metaData.hl7Version;
        }
      }
    });
    modalInstance.result.then(function(segment) {
      if (segment && segment != null) {
        $scope.loadingSelection = true;
        segmentRef.obj.ref.id = segment.id;
        segmentRef.obj.ref.ext = segment.ext;
        segmentRef.obj.ref.name = segment.name;
        segmentRef.children = [];
        $scope.setDirty();
        var ref = $rootScope.segmentsMap[segmentRef.obj.ref.id];
        $rootScope.processMessageTree(ref, segmentRef);
        if ($scope.messagesParams)
          $scope.messagesParams.refresh();
        $scope.loadingSelection = false;
      }
    });
  };


  $scope.goToDatatype = function(datatype) {
    $scope.$emit('event:openDatatype', datatype);
  };

  $scope.goToTable = function(table) {
    $scope.$emit('event:openTable', table);
  };

  $scope.hasChildren = function(node) {
    if (node && node != null) {
      if (node.type === 'group') {
        return node.children && node.children.length > 0;
      } else if (node.type === 'segmentRef') {
        return $rootScope.segmentsMap[node.ref.id] && $rootScope.segmentsMap[node.ref.id].fields && $rootScope.segmentsMap[node.ref.id].fields.length > 0;
      } else if (node.type === 'field' || node.type === 'component') {
        return $rootScope.datatypesMap[node.datatype.id] && $rootScope.datatypesMap[node.datatype.id].components && $rootScope.datatypesMap[node.datatype.id].components.length > 0;
      }
      return false;
    } else {
      return false;
    }

  };

  $scope.isAvailableConstantValue = function(node) {
    if($scope.hasChildren(node)) return false;
    var bindings = $scope.findingBindings(node);
    if(bindings && bindings.length > 0) return false;
    if($rootScope.datatypesMap[node.obj.datatype.id].name == 'ID' || $rootScope.datatypesMap[node.obj.datatype.id].name == "IS") return false;
    return true;
  };

  $scope.isSub = function(component) {
    return $scope.isSubDT(component);
  };

  $scope.isSubDT = function(component) {
    return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
  };

  $scope.isVisible = function(node) {
    if (node && node != null) {
      //                return FilteringSvc.show(node);
      return true;
    } else {
      return true;
    }
  };

  $scope.isVisibleInner = function(node, nodeParent) {
    if (node && node != null && nodeParent && nodeParent != null) {
      //                return FilteringSvc.showInnerHtml(node, nodeParent);
      return true;
    } else {
      return true;
    }
  };

  $scope.isUsagefiltered = function(node, nodeParent) {
    if ($rootScope.usageF) {
      console.log(nodeParent);
    }
    return true;

  };

  //For Constraints

  $scope.findAllGlobalConstraints = function() {
    $scope.listGlobalConformanceStatements = [];
    $scope.listGlobalPredicates = [];
    $scope.travelMessage($rootScope.message, '');
  };

  $scope.travelMessage = function(current, positionPath) {
    if (current.conformanceStatements && current.conformanceStatements.length > 0) {
      $scope.listGlobalConformanceStatements.push(current);
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

  $scope.openPredicateDialog = function(node) {
    if (node.obj.usage == 'C') $scope.openAddGlobalPredicateDialog(node, $rootScope.message);
  };

  $scope.openAddGlobalConformanceStatementDialog = function(message) {
    $mdDialog.show({
      templateUrl: 'GlobalConformanceStatementCtrl.html',
      controller: 'GlobalConformanceStatementCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        selectedMessage: message,
        contextPath: null,
        currentConformanceStatements : null,
        segmentsMap: $rootScope.segmentsMap,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode: "message"
      }
    }).then(function(message) {
      if (message) {
        $rootScope.message = message;
        $scope.findAllGlobalConstraints();
        $scope.setDirty();
      }
    });

  };

  $scope.openAddGlobalPredicateDialog = function(node, message) {
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'GlobalPredicateCtrl.html',
      controller: 'GlobalPredicateCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        selectedMessage: message,
        currentPredicate : null,
        selectedNode: node,
        segmentsMap: $rootScope.segmentsMap,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : 'message'
      }
    }).then(function(message) {
      if (message) {
        $rootScope.message = message;
        $scope.findAllGlobalConstraints();
        $scope.setDirty();
      }
    });

  };

  $scope.countPredicate = function(position) {
    var count = 0
    for (var i = 0, len1 = $scope.listGlobalPredicates.length; i < len1; i++) {
      for (var j = 0, len2 = $scope.listGlobalPredicates[i].predicates.length; j < len2; j++) {
        var positionPath = '';
        if (!$scope.listGlobalPredicates[i].positionPath || $scope.listGlobalPredicates[i].positionPath == '') {
          positionPath = $scope.listGlobalPredicates[i].predicates[j].constraintTarget;
        } else {
          positionPath = $scope.listGlobalPredicates[i].positionPath + '.' + $scope.listGlobalPredicates[i].predicates[j].constraintTarget;
        }

        if (positionPath == position) {
          count = count + 1;
        }
      }
    }
    return count;
  };

  $scope.findPredicateByPath = function(position) {
    for (var i = 0, len1 = $scope.listGlobalPredicates.length; i < len1; i++) {
      for (var j = 0, len2 = $scope.listGlobalPredicates[i].predicates.length; j < len2; j++) {
        var positionPath = '';
        if (!$scope.listGlobalPredicates[i].positionPath || $scope.listGlobalPredicates[i].positionPath == '') {
          positionPath = $scope.listGlobalPredicates[i].predicates[j].constraintTarget;
        } else {
          positionPath = $scope.listGlobalPredicates[i].positionPath + '.' + $scope.listGlobalPredicates[i].predicates[j].constraintTarget;
        }

        if (positionPath == position) {
          return $scope.listGlobalPredicates[i].predicates[j];
        }
      }
    }
    return null;
  };


  $scope.deletePredicateByPath = function(position, message) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeletePredicate.html',
      controller: 'DeleteMessagePredicateCtrl',
      size: 'md',
      locals: {
        position: position,
        message:  message

      }
    });
    modalInstance.then(function(result) {
      if(result&&result!=='cancel'){
          $scope.setDirty();
          $scope.findAllGlobalConstraints();
      }

    });
  };

  $scope.isAvailableForValueSet = function (node){
    if(node && node.obj){
      var currentDT = $rootScope.datatypesMap[node.obj.datatype.id];
      if(_.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT){
          return valueSetAllowedDT === currentDT.name;
        })) return true;
    }

    if(node && node.fieldDT && !node.componentDT){
      var parentDT = $rootScope.datatypesMap[node.fieldDT];
      var pathSplit = node.segmentPath.split(".");
      if(_.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent){
          return valueSetAllowedComponent.dtName === parentDT.name && valueSetAllowedComponent.location == pathSplit[1];
        })) return true;
    }

    if(node && node.componentDT){
      var parentDT = $rootScope.datatypesMap[node.componentDT];
      var pathSplit = node.segmentPath.split(".");
      if(_.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent){
          return valueSetAllowedComponent.dtName === parentDT.name && valueSetAllowedComponent.location == pathSplit[2];
        })) return true;
    }

    return false;
  };

  $scope.findingBindings = function(node) {
    var result = [];
    if(node && $rootScope.message){
      result = _.filter($rootScope.message.valueSetBindings, function(binding){
        return binding.location === $rootScope.refinePath(node.path);
      });
      for (var i = 0; i < result.length; i++) {
        result[i].bindingFrom = 'message';
      }

      if(result && result.length > 0) {
        return result;
      }

      if(node.segment) {
        var parentSeg = $rootScope.segmentsMap[node.segment];
        result = _.filter(parentSeg.valueSetBindings, function(binding){
          return binding.location === node.segmentPath;
        });
        for (var i = 0; i < result.length; i++) {
          result[i].bindingFrom = 'segment';
        }
      }

      if(result && result.length > 0) {
        return result;
      }

      if(node.fieldDT) {
        var parentDT = $rootScope.datatypesMap[node.fieldDT];
        var subPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
        result = _.filter(parentDT.valueSetBindings, function(binding){
          return binding.location === subPath;
        });
        for (var i = 0; i < result.length; i++) {
          result[i].bindingFrom = 'field';
        }
      }

      if(result && result.length > 0) {
        return result;
      }

      if(node.componentDT) {
        var parentDT = $rootScope.datatypesMap[node.componentDT];
        var subPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
        result = _.filter(parentDT.valueSetBindings, function(binding){
          return binding.location === subPath;
        });
        for (var i = 0; i < result.length; i++) {
          result[i].bindingFrom = 'component';
        }
      }
    }

    return result;
  };

  $scope.findingComments = function(node) {
    var result = [];
    if(node && $rootScope.message){
      result = _.filter($rootScope.message.comments, function(comment){
        return comment.location === $rootScope.refinePath(node.path);
      });
      for (var i = 0; i < result.length; i++) {
        result[i].from = 'message';
        result[i].index = i + 1;
      }

      if(node.segment) {
        var parentSeg = $rootScope.segmentsMap[node.segment];
        var subResult = _.filter(parentSeg.comments, function(comment){
          return comment.location === node.segmentPath;
        });
        for (var i = 0; i < subResult.length; i++) {
          subResult[i].from = 'segment';
          subResult[i].index = i + 1;
        }
        result = result.concat(subResult);
      }

      if(node.fieldDT) {
        var parentDT = $rootScope.datatypesMap[node.fieldDT];
        var subPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
        var subSubResult = _.filter(parentDT.comments, function(comment){
          return comment.location === subPath;
        });
        for (var i = 0; i < subSubResult.length; i++) {
          subSubResult[i].from = 'field';
          subSubResult[i].index = i + 1;
        }
        result = result.concat(subSubResult);
      }

      if(node.componentDT) {
        var parentDT = $rootScope.datatypesMap[node.componentDT];
        var subPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
        var subSubSubResult = _.filter(parentDT.comments, function(comment){
          return comment.location === subPath;
        });
        for (var i = 0; i < subSubSubResult.length; i++) {
          subSubSubResult[i].from = 'component';
          subSubSubResult[i].index = i + 1;
        }
        result = result.concat(subSubSubResult);
      }
    }

    return result;
  };

  $scope.deleteBinding = function(binding){
    var index = $rootScope.message.valueSetBindings.indexOf(binding);
    if (index >= 0) {
      $rootScope.message.valueSetBindings.splice(index, 1);
      $scope.setDirty();
    }
  };

  $scope.deleteComment = function(comment){
    var index = $rootScope.message.comments.indexOf(comment);
    if (index >= 0) {
      $rootScope.message.comments.splice(index, 1);
      $rootScope.recordChanged();
    }
  };

  $scope.editModalBindingForMsg = function(node) {
    var modalInstance = $mdDialog.show({

      templateUrl: 'TableMappingMessageCtrl.html',
      scope: $scope,
      preserveScope: true,
      controller: 'TableMappingMessageCtrl',
      locals: {
        currentNode: node
      }

    });

    modalInstance.then(function(node) {
      $scope.setDirty();
    });
  };
  $scope.editCommentDlg = function(node, comment, disabled, type) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditCommentMd.html',
      controller: 'EditCommentCtrl',
      locals: {
        currentNode: node,
        currentComment:  comment,
        disabled: disabled,
        type: type

      }
    });

    modalInstance.then(function() {
      $scope.setDirty();
    });
  };


  $scope.openDialogForEditSev = function(node) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditSingleElement.html',
      controller: 'EditSingleElementCtrl',
      locals: {
        currentNode:  node
      }

    });

    modalInstance.then(function(value) {
      $scope.addSev(node);
      node.sev.value = value;
      $scope.setDirty();
    });
  };

  $scope.confirmDatatypeSingleElementDuplicated = function (node) {
    var modalInstance = $modal.open({
      templateUrl: 'ConfirmSingleElementDuplicatedCtrl.html',
      controller: 'ConfirmSingleElementDuplicatedCtrl',
      resolve: {
        selectedNode: function () {
          return node;
        }
      }
    });
    modalInstance.result.then(function (node) {
      $scope.openDialogForEditSev(node);
    }, function () {
    });
  };

  $scope.addSev = function (node){
    var sev = {};
    sev.location = $rootScope.refinePath(node.path);
    sev.value = '';
    sev.profilePath = $rootScope.refinePath(node.locationPath);
    sev.name = node.obj.name;
      var diff= _.filter($rootScope.message.singleElementValues, function (r) {
          return sev.profilePath!==r.profilePath;
      });
      diff.push(sev);
      $rootScope.message.singleElementValues=diff;
    node.sev = sev;
    node.sev.from = 'message';
    $scope.setDirty();
  };

  $scope.deleteSev = function (node){
    var index = $rootScope.message.singleElementValues.indexOf(node.sev);
    if (index >= 0) {
      $rootScope.message.singleElementValues.splice(index, 1);
      $scope.setDirty();
    }

    if(node.componentDT) {
      var componentPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
      var foundSev = _.find($rootScope.datatypesMap[node.componentDT].singleElementValues, function (sev) {return sev.location == componentPath;});
      if (foundSev) {
        foundSev.from = 'component';
        node.sev = foundSev;
      }
    }

    if(node.fieldDT) {
      var fieldPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
      var foundSev = _.find($rootScope.datatypesMap[node.fieldDT].singleElementValues, function(sev){ return sev.location  ==  fieldPath; });
      if(foundSev) {
        foundSev.from = 'field';
        node.sev = foundSev;
      }
    }

    if(node.segment) {
      var foundSev = _.find($rootScope.segmentsMap[node.segment].singleElementValues, function(sev){ return sev.location  == node.segmentPath; });
      if(foundSev) {
        foundSev.from = 'segment';
        node.sev = foundSev;
      }
    }

    if(node.sev && node.sev.from == 'message'){
      node.sev = null;
    }
  }




});
