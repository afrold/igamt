/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl')
  .controller('DatatypeListCtrl', function($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $q, $modal, $timeout, CloneDeleteSvc, ViewSettings, DatatypeService, ComponentService, MastermapSvc, FilteringSvc, DatatypeLibrarySvc, TableLibrarySvc, MessageService, TableService, blockUI, SegmentService, VersionAndUseService, CompareService, ValidationService,$mdDialog) {
    $scope.defTabStatus = {
      active: 1
    };
    $scope.deltaTabStatus = {
      active: 0
    };
    $scope.tabStatus = {
      active: 1
    };
    $scope.availbleVersionOfDt = [];
    $scope.editableDT = '';
    $scope.editableVS = '';
    $scope.readonly = false;
    $scope.saved = false;
    $scope.message = false;
    $scope.datatypeCopy = null;
    $scope.viewSettings = ViewSettings;
    $scope.selectedChildren = [];
    $scope.saving = false;
    $scope.init = function() {

      $scope.accordStatus = {
        isCustomHeaderOpen: false,
        isFirstOpen: true,
        isSecondOpen: false,
        isThirdOpen: false,
        isFirstDisabled: false
      };

      $scope.tabStatus = {
        active: 1
      };

    };


    $scope.deleteConformanceStatementFromList = function(c) {
      $rootScope.datatype.conformanceStatements.splice($rootScope.datatype.conformanceStatements.indexOf(c), 1);

      $scope.setDirty();
      $rootScope.recordChanged();
    };

    $scope.deletePredicateFromList = function(p) {
      $rootScope.datatype.predicates.splice($rootScope.datatype.predicates.indexOf(p), 1);

      $scope.setDirty();
      $rootScope.recordChanged();
    };
    $scope.validateDatatype = function() {
      ValidationService.validatedatatype($rootScope.datatype, $rootScope.igdocument.profile.metaData.hl7Version).then(function(result) {
        $rootScope.validationMap = {};
        $rootScope.childValidationMap = {};
        $rootScope.showDtErrorNotification = true;
        $rootScope.validationResult = result;
        $rootScope.buildValidationMap($rootScope.validationResult);
      });
    };
    $scope.isDatatypeValidated = function() {
      if ($rootScope.datatype && ($rootScope.validationResult.targetId === $rootScope.datatype.id || $rootScope.childValidationMap[$rootScope.datatype.id])) {
        return true;
      } else {
        return false;
      }
    };
    $scope.setErrorNotification = function() {
      $rootScope.showDtErrorNotification = !$rootScope.showDtErrorNotification;
    };


    $scope.updateDTMConstraints = function (dtmComponentDefinition){
      if(dtmComponentDefinition.usage !== 'C') {
        dtmComponentDefinition.dtmPredicate = null;
      }

      if($rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length !== dtmComponentDefinition.position){
        if(dtmComponentDefinition.usage === 'R'){
          for (var i = 0, len = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length - 1; i < len; i++) {
            var item = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i];
            if(item.position < dtmComponentDefinition.position){
              item.usage = 'R';
              item.dtmPredicate = null;
            }
          }
        }else if (dtmComponentDefinition.usage === 'X'){
          for (var i = 0, len = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length - 1; i < len; i++) {
            var item = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i];
            if(item.position > dtmComponentDefinition.position){
              item.usage = 'X';
              item.dtmPredicate = null;
            }
          }
        }else if (dtmComponentDefinition.usage === 'C'){
          for (var i = 0, len = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length - 1; i < len; i++) {
            var item = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i];
            if(item.position > dtmComponentDefinition.position && item.usage !== 'X'){
              item.usage = 'C';
              item.dtmPredicate = {};
              item.dtmPredicate.trueUsage = "O";
              item.dtmPredicate.falseUsage = "X";
              item.dtmPredicate.target = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i-1];
              item.dtmPredicate.verb = "is valued";
            }
            if(item.position < dtmComponentDefinition.position){
              item.usage = 'R';
              item.dtmPredicate = null;
            }
          }
        }else if (dtmComponentDefinition.usage === 'RE'){
          for (var i = 0, len = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length - 1; i < len; i++) {
            var item = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i];
            if(item.position > dtmComponentDefinition.position && item.usage !== 'X'){
              item.usage = 'C';
              item.dtmPredicate = {};
              item.dtmPredicate.trueUsage = "O";
              item.dtmPredicate.falseUsage = "X";
              item.dtmPredicate.target = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i-1];
              item.dtmPredicate.verb = "is valued";
            }
            if(item.position < dtmComponentDefinition.position){
              item.usage = 'R';
              item.dtmPredicate = null;
            }
          }
        }else if (dtmComponentDefinition.usage === 'O'){
          for (var i = 0, len = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions.length - 1; i < len; i++) {
            var item = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i];
            if(item.position > dtmComponentDefinition.position && item.usage !== 'X'){
              item.usage = 'C';
              item.dtmPredicate = {};
              item.dtmPredicate.trueUsage = "O";
              item.dtmPredicate.falseUsage = "X";
              item.dtmPredicate.target = $rootScope.datatype.dtmConstraints.dtmComponentDefinitions[i-1];
              item.dtmPredicate.verb = "is valued";
            }
            if(item.position < dtmComponentDefinition.position){
              item.usage = 'R';
              item.dtmPredicate = null;
            }
          }
        }
      }
    };

    $scope.changeDatatypeLink = function(datatypeLink) {
      datatypeLink.isChanged = true;

      var t = $rootScope.datatypesMap[datatypeLink.id];

      if (t == null) {
        datatypeLink.name = null;
        datatypeLink.ext = null;
        datatypeLink.label = null;
      } else {
        datatypeLink.name = t.name;
        datatypeLink.ext = t.ext;
        datatypeLink.label = t.label;
      }
    };

    $scope.dtmSliderOptions = {
      ceil: 7,
      floor: 0,
      showSelectionBar: true,
      onChange: function(id) {
        $scope.setDirty();
        $rootScope.recordChanged();
      },
      showTicks: true,
      getTickColor: function(value) {
        if (value < 3)
          return 'red';
        if (value < 6)
          return 'orange';
        if (value < 8)
          return 'yellow';
        return '#2AE02A';
      }
    };

    $scope.refreshSlider = function() {
      setTimeout(function() {
        $scope.$broadcast('reCalcViewDimensions');
      }, 1000);
    };

    $scope.deleteComponent = function(componentToDelete, datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'DeleteComponent.html',
        controller: 'DeleteComponentCtrl',
        size: 'md',
        resolve: {
          componentToDelete: function() {
            return componentToDelete;
          },
          datatype: function() {
            return datatype;
          }


        }
      });
      modalInstance.result.then(function() {

        $scope.setDirty();
        $rootScope.recordChanged();
        try {
          if ($scope.datatypesParams)
            $scope.datatypesParams.refresh();
        } catch (e) {

        }
      });
    };
    $scope.testCall = function() {
      console.log($rootScope.references);
    };

    $scope.deleteDatatypePredicate= function(position, datatype) {
      console.log("Deleting Predicate");
      var modalInstance = $mdDialog.show({
        templateUrl: 'DeletePredicate.html',
        controller: 'DeleteDatatypePredicateCtrl',
        size: 'md',
        locals: {
          position:position,
          datatype: datatype

        }
      });
      modalInstance.then(function(result) {
        if(result&&result!=='cancel'){
            $rootScope.recordChanged();
        }
      });
    };

    $scope.openPredicateDialog = function(node) {
      if (node.usage == 'C') $scope.managePredicate(node);
    };
    $scope.alerts = [
      { type: 'warning', msg: ' Warning: This Datatype is being deprecated, there are new versions availables' },

    ];

    $scope.closeAlert = function(index) {
      $scope.alerts.splice(index, 1);
    };

    $scope.OtoX = function(message) {
      console.log("========kor3raycha");
      var modalInstance = $mdDialog.show({
        templateUrl: 'OtoX.html',
        controller: 'OtoXCtrl',
        scope: $scope,
        preserveScope:true,
        size: 'md',
        locals: {
          message: message

        }
      });
      modalInstance.then(function() {

        $scope.setDirty();
        $rootScope.recordChanged();
        try {
          if ($scope.datatypesParams)
            $scope.datatypesParams.refresh();
        } catch (e) {

        }
      });
    };

    $scope.getAllVersionsOfDT = function(id) {
      $scope.checked = {};
      var ancestors = [];
      if (!$rootScope.versionAndUseMap[id]) {
        return "";
      }
      if ($rootScope.versionAndUseMap[id].ancestors && $rootScope.versionAndUseMap[id].ancestors.length > 0) {
        var ancestors = $rootScope.versionAndUseMap[id].ancestors;

      }
      ancestors.push($rootScope.versionAndUseMap[id].id);
      var derived = $rootScope.versionAndUseMap[id].derived;
      angular.forEach(ancestors, function(ancestor) {
        derived.push(ancestor);
      });
      var all = derived;
      VersionAndUseService.findAllByIds(all).then(function(result) {
        //$rootScope.datatypes = result;
        $scope.availbleVersionOfDt = result;
        if ($scope.dynamicDt_Evolution) {
        }

      }, function(error) {
        $rootScope.msg().text = "DatatypesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);

      });

    };

    $scope.dynamicDt_Evolution = new ngTreetableParams({
      getNodes: function(parent) {
        if ($scope.dataList !== undefined) {

          //return parent ? parent.fields : $scope.test;
          if (parent) {
            if (parent.fields) {
              return parent.fields;
            } else if (parent.components) {
              return parent.components;
            } else if (parent.segments) {
              return parent.segments;
            } else if (parent.codes) {
              return parent.codes;
            }

          } else {
            return $scope.dataList;
          }

        }
      },
      getTemplate: function(node) {
        return 'tree_node';
      }
    });
    $scope.compareWithCurrent = function(id) {
      $scope.checked = id;
      $rootScope.clearChanges();
      $scope.cleanState();
      DatatypeService.getOne($rootScope.datatype.parentVersion).then(function(sourceParent) {
        DatatypeService.getOne(id).then(function (result) {
          $scope.dtChanged = false;
          $scope.vsTemplate = false;
          $scope.dataList = CompareService.cmpDatatype(sourceParent, result, [], [], [], []);
          $scope.hideEvolution = false;
          $rootScope.clearChanges();
          $scope.cleanState();
          $scope.loadingSelection = false;
          if ($scope.dynamicDt_Evolution) {
            $scope.dynamicDt_Evolution.refresh();
          }
        });
      });
    };

    $scope.editableComp = '';
    $scope.editComponent = function(component) {
      $scope.editableComp = component.id;
      $scope.compName = component.name;

    };

    $scope.backComp = function() {
      $scope.editableComp = '';
    };
    $scope.applyComp = function(datatype, component, name, position) {
      blockUI.start();
      $scope.editableComp = '';
      if (component) {
        component.name = name;
      }
      if (position) {
        MessageService.updatePosition(datatype.components, component.position - 1, position - 1);
      }
      $scope.setDirty();
      $rootScope.recordChanged();
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
      $scope.Posselected = false;
      blockUI.stop();

    };
    $scope.selectPos = function() {
      $scope.Posselected = true;
    };
    $scope.selectDT = function(field, datatype) {
      if (datatype) {
        $scope.DTselected = true;
        blockUI.start();
        field.datatype.ext = JSON.parse(datatype).ext;
        field.datatype.id = JSON.parse(datatype).id;
        field.datatype.label = JSON.parse(datatype).label;
        field.datatype.name = JSON.parse(datatype).name;
        $scope.setDirty();
        $rootScope.recordChanged();
        // $rootScope.processElement(field);
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
        $scope.editableDT = '';
        $scope.DTselected = false;
        blockUI.stop();
      } else {
        $scope.otherDT(field);
      }
    };

    $scope.otherDT = function(field) {

      var modalInstance = $mdDialog.show({
        templateUrl: 'otherDTModal.html',
        controller: 'otherDTCtrl',
        scope: $scope,        // use parent scope in template
        preserveScope: true,
        locals: {

          datatypes:  $rootScope.datatypes,

          field:  field
        }

      });
      modalInstance.then(function(field) {
        $scope.setDirty();
        $rootScope.recordChanged();
        $scope.editableDT = '';
        if ($scope.datatypesParams) {
          $scope.datatypesParams.refresh();
        }
      });

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
        modalInstance.result.then(function(result) {
          if(result&result!=='cancel'){
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
            destination: datatype
          }

        });
        modalInstance.then(function(result) {
          if(result&&result!=='cancel') {

            if ($rootScope.libraryDoc) {
              $scope.$emit('event:openDatatypeInLib', datatype);
            } else if($rootScope.igdocument){
              $rootScope.editDatatype(datatype);

            }
          }
        });
      });
    };
    $scope.editDT = function(field) {
      $scope.editDTMap={};
      $scope.editableDT = field.id;

      $scope.results = [];
      if($rootScope.igdocument){
        angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
            if(dtLink.name && field.datatype.name){
                if (field.datatype.name.split('\\_')[0] === dtLink.name.split('\\_')[0]) {
                    if(!$scope.editDTMap[dtLink.id]){
                        $scope.editDTMap[dtLink.id]=dtLink;
                        $scope.results.push(dtLink);
                    }
                }
            }
        });
      }else{
        var dt= $rootScope.datatypesMap[field.datatype.id];
        var versions =dt.hl7versions;
        angular.forEach($rootScope.datatypeLibrary.children,function(dtLink){
          var d=$rootScope.datatypesMap[dtLink.id];
          if(d.name===dt.name&&_.intersection(d.hl7versions, versions).length===versions.length){
            var dtLink={};
            dtLink.id=d.id;
            dtLink.name=d.name;
            dtLink.ext=d.ext;
            if(!$scope.editDTMap[d.id]){
              $scope.editDTMap[d.id]=dtLink;
              $scope.results.push(dtLink);
            }
          }
        });
      }
    };
    $scope.backDT = function() {
      $scope.editableDT = '';
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
    };
    $scope.getLabel = function(name, ext) {
      var label = name;
      if (ext && ext !== null && ext !== "") {
        label = label + "_" + ext;
      }
      return label;
    };
    $scope.editVS = function(field) {
      $scope.editableVS = field.id;
      if (field.table !== null) {
        $scope.VSselected = true;
        $scope.selectedValueSet = field.table;
      } else {
        $scope.VSselected = false;
      }
    };
    $scope.backVS = function() {
      $scope.editableVS = '';
    };
    $scope.selectVS = function(field, valueSet) {
      $scope.selectedValueSet = valueSet;
      $scope.VSselected = true;
      $scope.editableVS = '';
      if (field.table === null) {
        field.table = {
          id: '',
          bindingIdentifier: ''
        };
      }
      field.table.id = $scope.selectedValueSet.id;
      field.table.bindingIdentifier = $scope.selectedValueSet.bindingIdentifier;
      $scope.setDirty();
      $rootScope.recordChanged();
      $scope.VSselected = false;
    };

    $scope.ContainUnpublished = function(element) {
      if (element && element.type && element.type === "datatype") {
        angular.forEach(element.components, function(component) {
          component.location = element.name + "_" + element.ext + "." + component.position
          $scope.ContainUnpublished(component);
        });
      } else if (element && element.type && element.type === "component") {
        if (element.tables && element.tables != null) {
          angular.forEach(element.tables, function(table) {
            if ($rootScope.tablesMap[table.id] && $rootScope.tablesMap[table.id]) {
              if ($rootScope.tablesMap[table.id].scope !== "HL7STANDARD" && $rootScope.tablesMap[table.id].status !== "PUBLISHED") {
                $scope.containUnpublished = true;
                $scope.unpublishedTables.push({ table: table, location: element.location });
              }
            }
          });
        }
        if (element.datatype !== null || element.datatype !== undefined) {
          if ($rootScope.datatypesMap[element.datatype.id] && $rootScope.datatypesMap[element.datatype.id]) {
            if ($rootScope.datatypesMap[element.datatype.id].status !== "PUBLISHED" && $rootScope.datatypesMap[element.datatype.id].scope !== "HL7STANDARD") {
              $scope.containUnpublished = true;
              $scope.unpublishedDatatypes.push({ datatype: element.datatype, location: element.location });
            }
          }
        }
      }
    };

    $scope.confirmPublish = function(datatypeCopy) {
      console.log("here")
      var modalInstance = $mdDialog.show({
        templateUrl: 'ConfirmDatatypePublishCtlMd.html',
        controller: 'ConfirmDatatypePublishCtlMd',
        locals: {
          datatypeToPublish: datatypeCopy
        }
      });
      modalInstance.then(function(datatypetoPublish) {
        var ext = $rootScope.datatype.ext;
        DatatypeService.publish($rootScope.datatype).then(function(result) {
          var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.datatypeLibrary.children);
          var newLink = DatatypeService.getDatatypeLink(result);
          newLink.ext = ext;
          DatatypeLibrarySvc.updateChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
            DatatypeService.merge($rootScope.datatypesMap[result.id], result);
            $rootScope.datatypesMap[result.id].status = "PUBLISHED";
            $rootScope.datatype.status = "PUBLISHED";
            if ($scope.editForm) {
              $scope.editForm.$setPristine();
              $scope.editForm.$dirty = false;
              $scope.editForm.$invalid = false;
            }
            $rootScope.clearChanges();
            DatatypeService.merge($rootScope.datatype, result);
            if ($scope.datatypesParams) {
              $scope.datatypesParams.refresh();
            }
            VersionAndUseService.findById(result.id).then(function(inf) {
              $rootScope.versionAndUseMap[inf.id] = inf;
              if ($rootScope.versionAndUseMap[inf.sourceId]) {
                $rootScope.versionAndUseMap[inf.sourceId].deprecated = true;
              }
            });
            oldLink.ext = newLink.ext;
            oldLink.name = newLink.name;
            $scope.saving = false;
          }, function(error) {
            $scope.saving = false;
            $rootScope.msg().text = "Sorry an error occured. Please try again";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
          });
        }, function(error) {
          $scope.saving = false;
          $rootScope.msg().text = "Sorry an error occured. Please try again";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
        });
      });
    };

    $scope.abortPublish = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'AbortPublishCtl.html',
        controller: 'AbortPublishCtl',
        resolve: {
          datatypeToPublish: function() {
            return datatype;
          },
          unpublishedDatatypes: function() {
            return $scope.unpublishedDatatypes;
          },
          unpublishedTables: function() {
            return $scope.unpublishedTables;
          }
        }
      });
    };
    $scope.publishDatatype = function(datatype) {
      $scope.containUnpublished = false;
      $scope.unpublishedTables = [];
      $scope.unpublishedDatatypes = [];
      $scope.ContainUnpublished(datatype);
      if ($scope.containUnpublished) {
        $scope.abortPublish(datatype);
        datatype.status = "UNPUBLISHED";
      } else {
        $scope.confirmPublish(datatype);
      }
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
            if (!$rootScope.SharingScope) {
              $rootScope.editTable(valueSet);
            } else {
              $scope.editTable(valueSet);
            }
          }
        });
      });
    };
    $scope.selectedVS = function() {
      return ($scope.selectedValueSet !== undefined);
    };
    $scope.unselectVS = function() {
      $scope.selectedValueSet = undefined;
      $scope.VSselected = false;
    };
    $scope.isVSActive = function(id) {
      if ($scope.selectedValueSet) {
        return $scope.selectedValueSet.id === id;
      } else {
        return false;
      }
    };

    $scope.addComponentModal = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'AddComponentModal.html',
        controller: 'AddComponentCtrl',
        windowClass: 'app-modal-window',
        resolve: {

          valueSets: function() {
            return $rootScope.tables;
          },
          datatypes: function() {
            return $rootScope.datatypes;
          },
          datatype: function() {
            return datatype;
          },
          messageTree: function() {
            return $rootScope.messageTree;
          }

        }
      });
      modalInstance.result.then(function(datatype) {
        $scope.setDirty();
        $rootScope.recordChanged();

        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      });
    };

    $scope.copy = function(datatype) {
      CloneDeleteSvc.copyDatatype(datatype);
    };

    $scope.reset = function() {
      blockUI.start();
      DatatypeService.reset();
      $scope.cleanState();
      $rootScope.references = [];
      angular.forEach($rootScope.segments, function(segment) {
        $rootScope.findDatatypeRefs($rootScope.datatype, segment, $rootScope.getSegmentLabel(segment), segment);
      });
      angular.forEach($rootScope.datatypes, function(dt) {
        $rootScope.findDatatypeRefs($rootScope.datatype, dt, $rootScope.getDatatypeLabel(dt), dt);
      });
      blockUI.stop();
    };

    $scope.recordDatatypeChange = function(type, command, id, valueType, value) {
      var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $rootScope.datatype.id);
      if (datatypeFromChanges === undefined) {
        $rootScope.recordChangeForEdit2(type, command, id, valueType, value);
      }
    };

    $scope.close = function() {
      $rootScope.datatype = null;
      $scope.refreshTree();
      $scope.loadingSelection = false;
    };

    $scope.delete = function(datatype) {
      CloneDeleteSvc.deleteDatatype(datatype);
    };

    $scope.hasChildren = function(node) {
      return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype.id) != undefined && $rootScope.getDatatype(node.datatype.id).components != null && $rootScope.getDatatype(node.datatype.id).components.length > 0;
    };

    $scope.isAvailableConstantValue = function(node, parent) {
      var bindings = $scope.findingBindings(node.path, parent);
      if($scope.hasChildren(node)) return false;
      if(bindings && bindings.length > 0) return false;
      if($rootScope.datatypesMap[node.datatype.id].name == 'ID' || $rootScope.datatypesMap[node.datatype.id].name == "IS") return false;
      return true;
    };

    $scope.validateLabel = function(label, name) {
      if (label && !label.startsWith(name)) {
        return false;
      }
      return true;
    };

    $scope.onDatatypeChange = function(node) {
      $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'datatype', node.datatype);
      $scope.refreshTree(); // TODO: Refresh only the node
    };

    $scope.refreshTree = function() {
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
    };

    $scope.goToTable = function(table) {
      $scope.$emit('event:openTable', table);
    };

    $scope.deleteTable = function(node) {
      node.table = null;
      $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'table', null);
    };

    $scope.updateLabel=function (obj) {
          console.log(obj);
          obj.label=obj.name+"_"+obj.ext;

    };

    $scope.managePredicate = function(node) {
      console.log($rootScope.datatype);
      $mdDialog.show({
        parent: angular.element(document).find('body'),
        templateUrl: 'PredicateDatatypeCtrl.html',
        controller: 'PredicateDatatypeCtrl',
        scope:$scope,
        preserveScope:true,
        locals: {
          selectedDatatype: $rootScope.datatype,
          selectedNode: node,
          config : $rootScope.config,
          tables : $rootScope.tables
        }
      }).then(function(datatype) {
        if (datatype) {
          $rootScope.datatype = datatype;
          $scope.setDirty();
          $rootScope.recordChanged();
        }
      });
    };

    $scope.manageConformanceStatement = function() {
      $mdDialog.show({
        parent: angular.element(document).find('body'),
        templateUrl: 'ConformanceStatementDatatypeCtrl.html',
        controller: 'ConformanceStatementDatatypeCtrl',
        scope:$scope,
        preserveScope:true,
        locals: {
          selectedDatatype : $rootScope.datatype,
          config : $rootScope.config,
          tables : $rootScope.tables
        }
      }).then(function(datatype) {
        if (datatype) {
          $rootScope.datatype = datatype;
          $scope.setDirty();
          $rootScope.recordChanged();
        }
      });
    };

    $scope.isSubDT = function(component) {
      if ($rootScope.datatype != null) {
        for (var i = 0, len = $rootScope.datatype.components.length; i < len; i++) {
          if ($rootScope.datatype.components[i].id === component.id)
            return false;
        }
      }
      return true;
    };

    $scope.findDTByComponentId = function(componentId) {
      return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
    };

    $scope.countConformanceStatements = function(position) {
      var count = 0;
      if ($rootScope.datatype != null)
        for (var i = 0, len1 = $rootScope.datatype.conformanceStatements.length; i < len1; i++) {
          if ($rootScope.datatype.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
            count = count + 1;
        }
      return count;
    };

    $scope.countPredicate = function(position) {
      var count = 0;
      if ($rootScope.datatype != null)
        for (var i = 0, len1 = $rootScope.datatype.predicates.length; i < len1; i++) {
          if ($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
            count = count + 1;
        }

      return count;
    };

    $scope.countPredicateOnSubComponent = function(position, componentId) {
      var dt = $scope.findDTByComponentId(componentId);
      if (dt != null)
        for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
          if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
            return 1;
        }

      return 0;
    };


    $scope.isRelevant = function(node) {
      return DatatypeService.isRelevant(node);
    };

    $scope.isBranch = function(node) {
      return DatatypeService.isBranch(node);
    };


    $scope.isVisible = function(node) {
      return DatatypeService.isVisible(node);
    };

    $scope.children = function(node) {
      return DatatypeService.getNodes(node);
    };

    $scope.getParent = function(node) {
      return DatatypeService.getParent(node);
    };

    $scope.getDatatypeLevelConfStatements = function(element) {
      return DatatypeService.getDatatypeLevelConfStatements(element);
    };

    $scope.getDatatypeLevelPredicates = function(element) {
      return DatatypeService.getDatatypeLevelPredicates(element);
    };

    $scope.isChildSelected = function(component) {
      return $scope.selectedChildren.indexOf(component) >= 0;
    };

    $scope.isChildNew = function(component) {
      return component && component != null && component.status === 'DRAFT';
    };


    $scope.selectChild = function($event, child) {
      var checkbox = $event.target;
      var action = (checkbox.checked ? 'add' : 'remove');
      updateSelected(action, child);
    };


    $scope.selectAllChildren = function($event) {
      var checkbox = $event.target;
      var action = (checkbox.checked ? 'add' : 'remove');
      for (var i = 0; i < $rootScope.datatype.components.length; i++) {
        var component = $rootScope.datatype.components[i];
        updateSelected(action, component);
      }
    };

    var updateSelected = function(action, child) {
      if (action === 'add' && !$scope.isChildSelected(child)) {
        $scope.selectedChildren.push(child);
      }
      if (action === 'remove' && $scope.isChildSelected(child)) {
        $scope.selectedChildren.splice($scope.selectedChildren.indexOf(child), 1);
      }
    };

    //something extra I couldn't resist adding :)
    $scope.isSelectedAllChildren = function() {
      return $rootScope.datatype && $rootScope.datatype != null && $rootScope.datatype.components && $scope.selectedChildren.length === $rootScope.datatype.components.length;
    };


    $scope.changeDatatype = function(id) {
      $mdDialog.show({

        templateUrl:'referenceTochange.html',
        locals: {
          item: $rootScope.datatype,
          references:$rootScope.references,
          tmpReferences:$rootScope.tmpReferences
        },
        controller: DialogController
      });
      function DialogController($scope,$rootScope, $mdDialog, item, references, tmpReferences) {
        $scope.references=references;
        $scope.tmpReferences=tmpReferences;
        $scope.item = item;


        $scope.selected=$scope.tmpReferences;
        $scope.toggle = function (item, list) {
          var idx = list.indexOf(item);
          if (idx > -1) {
            list.splice(idx, 1);
          }
          else {
            list.push(item);
          }
        };
        $scope.exists = function (item, list) {
          return list.indexOf(item) > -1;
        };
        $scope.isChecked = function() {
          return $scope.selected.length === $scope.tmpReferences.length;
        };

        $scope.toggleAll = function() {
          if ($scope.selected.length === $scope.tmpReferences.length) {
            $scope.selected = [];
          } else if ($scope.selected.length === 0 || $scope.selected.length > 0) {
            $scope.selected = $scope.tmpReferences.slice(0);
          }
        };



        $scope.closeDialog = function() {
          console.log($scope.references);
          console.log($scope.item);
          console.log($rootScope.datatypesMap);
          $rootScope.upgradeOrDowngrade(id,$rootScope.datatype,$scope.selected);
          $mdDialog.hide();
        }
        $scope.cancel=function(){
          $mdDialog.hide();
        }
      }
    };

    $scope.createNewComponent = function() {
      if ($rootScope.datatype != null) {
        if (!$rootScope.datatype.components || $rootScope.datatype.components === null)
          $rootScope.datatype.components = [];
        var child = ComponentService.create($rootScope.datatype.components.length + 1);
        $rootScope.datatype.components.push(child);
        //TODO update master map
        //MastermapSvc.addDatatypeObject($rootScope.datatype, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
        //TODO:remove as legacy code
        $rootScope.parentsMap[child.id] = $rootScope.datatype;
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      }
    };

    $scope.deleteComponents = function() {
      if ($rootScope.datatype != null && $scope.selectedChildren != null && $scope.selectedChildren.length > 0) {
        ComponentService.deleteList($scope.selectedChildren, $rootScope.datatype);
        //TODO update master map
        //TODO:remove as legacy code
        angular.forEach($scope.selectedChildren, function(child) {
          delete $rootScope.parentsMap[child.id];
        });
        $scope.selectedChildren = [];
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      }
    };


    $scope.cleanState = function() {
      $scope.selectedChildren = [];
      $rootScope.addedDatatypes = [];
      $rootScope.addedTables = [];
      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
      }
      $rootScope.clearChanges();
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
    };

    $scope.callDTDelta = function() {

      $rootScope.$emit("event:openDTDelta");
    };

    $scope.AddBindingForDatatype = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'AddBindingForDatatype.html',
        controller: 'AddBindingForDatatype',
        windowClass: 'conformance-profiles-modal',
        resolve: {
          datatype: function() {
            return datatype;
          }
        }
      });
      modalInstance.result.then(function() {
        $scope.setDirty();
        $rootScope.recordChanged();
      });
    };

    $scope.saveDatatype = function() {
      var ext = $rootScope.datatype.ext;

      DatatypeService.save($rootScope.datatype).then(function(result) {
        var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.datatypeLibrary.children);
        var newLink = DatatypeService.getDatatypeLink(result);
        newLink.ext = ext;
        DatatypeLibrarySvc.updateChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
          DatatypeService.merge($rootScope.datatypesMap[result.id], result);
          DatatypeService.merge($rootScope.datatype, result);
          //  if ($scope.datatypesParams){
          //      $scope.datatypesParams
          //         $scope.datatypesParams.refresh();
          //     }
          $rootScope.clearChanges();
          if ($scope.datatypesParams) {
            // $scope.datatypesParams.refresh();
          }
          $rootScope.datatype.dateUpdated = result.dateUpdated;
          $rootScope.$emit("event:updateIgDate");
          DatatypeLibrarySvc.updateChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
            DatatypeService.saveNewElements().then(function() {


              oldLink.ext = newLink.ext;
              oldLink.name = newLink.name;
              $scope.saving = false;
              $scope.cleanState();
            }, function(error) {
              $scope.saving = false;
              $rootScope.msg().text = "Sorry an error occured. Please try again";
              $rootScope.msg().type = "danger";
              $rootScope.msg().show = true;
            });
          }, function(error) {
            $scope.saving = false;
            $rootScope.msg().text = "Sorry an error occured. Please try again";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
          });

        }, function(error) {
          $scope.saving = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
        $rootScope.saveBindingForDatatype();
      });
    };

    $scope.cancel = function() {
      //TODO: remove changes from master ma
      angular.forEach($rootScope.datatype.components, function(child) {
        if ($scope.isChildNew(child.status)) {
          delete $rootScope.parentsMap[child.id];
        }
      });
      $rootScope.datatype = null;
      $scope.selectedChildren = [];
      $rootScope.clearChanges();
    };

    var searchById = function(id) {
      var children = $rootScope.datatypeLibrary.children;
      for (var i = 0; i < $rootScope.datatypeLibrary.children; i++) {
        if (children[i].id === id) {
          return children[i];
        }
      }
      return null;
    };

    var indexIn = function(id, collection) {
      for (var i = 0; i < collection.length; i++) {
        if (collection[i].id === id) {
          return i;
        }
      }
      return -1;
    };

    $scope.showSelectDatatypeFlavorDlg = function(component) {
      var modalInstance = $modal.open({
        templateUrl: 'SelectDatatypeFlavor.html',
        controller: 'SelectDatatypeFlavorCtrl',
        windowClass: 'app-modal-window',
        resolve: {
          currentDatatype: function() {
            return $rootScope.datatypesMap[component.datatype.id];
          },

          hl7Version: function() {
            return $rootScope.igdocument.profile.metaData.hl7Version;
          },
          datatypeLibrary: function() {
            return $rootScope.datatypeLibrary;
          }
        }
      });
      modalInstance.result.then(function(datatype, ext) {
        //                MastermapSvc.deleteElementChildren(component.datatype.id, "datatype", component.id, component.type);
        //                MastermapSvc.addDatatypeObject(datatype, [[component.id, component.type]]);
        component.datatype.id = datatype.id;
        component.datatype.name = datatype.name;
        component.datatype.ext = datatype.ext;
        $rootScope.processElement(component);
        $scope.setDirty();
        $rootScope.recordChanged();
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();

      });

    };

    $scope.shareModal = function(datatype) {


      $http.get('api/usernames').then(function(response) {
        var userList = response.data;
        angular.forEach(userList, function (user) {
          $rootScope.generateHash(user.email);

        });
        var filteredUserList = userList.filter(function(user) {
          // Add accountId var
          user.accountId = user.id;
          var isPresent = false;
          if (datatype.shareParticipantIds) {
            for (var i = 0; i < datatype.shareParticipantIds.length; i++) {
              if (datatype.shareParticipantIds[i].accountId == user.id) {
                isPresent = true;
              }
            }
          }
          if (!isPresent) return user;
        });
        var modalInstance = $mdDialog.show({
          templateUrl: 'ShareDatatypeModal.html',
          controller: 'ShareDatatypeCtrl',
          scope:$scope,
          preserveScope:true,
          size: 'lg',
          locals: {
            igdocumentSelected:  datatype,
            userList:  _.filter(filteredUserList, function(user) {

              return user.id != $rootScope.accountId && datatype.shareParticipantIds && datatype.shareParticipantIds != null && datatype.shareParticipantIds.indexOf(user.id) == -1;
            })
          }
        });

        modalInstance.then(function(result) {
          $scope.saveDatatypeAfterShare();
        }, function() {
          $scope.saveDatatypeAfterShare();

        });

      }, function(error) {
      });
    };

    $scope.saveDatatypeAfterShare = function() {

      var ext = $rootScope.datatype.ext;

      DatatypeService.save($rootScope.datatype).then(function(result) {
        var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.datatypeLibrary.children);
        var newLink = DatatypeService.getDatatypeLink(result);
        newLink.ext = ext;
        DatatypeLibrarySvc.updateChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
          DatatypeService.merge($rootScope.datatype, result);

          DatatypeService.saveNewElements(true).then(function() {


            oldLink.ext = newLink.ext;
            oldLink.name = newLink.name;
            $scope.saving = false;
            $scope.cleanState();
          }, function(error) {
            $scope.saving = false;
          });
        }, function(error) {
          $scope.saving = false;
        });

      }, function(error) {
        $scope.saving = false;
      });
      $rootScope.saveBindingForDatatype();
    };

    $scope.editModalBindingForDT = function(node) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'TableMappingDatatypeCtrl.html',
        scope: $scope,        // use parent scope in template
        preserveScope: true,
        controller: 'TableMappingDatatypeCtrl',
        locals: {
          currentNode: node

        }
      });

      modalInstance.then(function(node) {
        $scope.setDirty();
        $rootScope.recordChanged();
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
        $rootScope.recordChanged();
      });
    };

    $scope.openDialogForEditSev = function(node) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'EditSingleElement.html',
        controller: 'EditSingleElementCtrl',
        locals: {
          currentNode: node
        }
      });

      modalInstance.then(function(value) {
        if(value!=='cancel'){
            $scope.addSev(node);
            node.sev.value = value;
            $rootScope.recordChanged();
        }

      });
    };

    $scope.confirmDatatypeSingleElementDuplicated = function (node) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'ConfirmSingleElementDuplicatedCtrl.html',
        controller: 'ConfirmSingleElementDuplicatedCtrl',
        resolve: {
          selectedNode: function () {
            return node;
          }
        }
      });
      modalInstance.then(function (node) {
        $scope.openDialogForEditSev(node);
      }, function () {
      });
    };

    $scope.isAvailableForValueSet = function (currentDT, parentDT, path){
      if(currentDT){
        if(_.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT){
            return valueSetAllowedDT == currentDT.name;
          })) return true;
      }

      if(parentDT && path){
        if(_.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent){
            return valueSetAllowedComponent.dtName == parentDT.name && valueSetAllowedComponent.location == path;
          })) return true;
      }

      return false;
    };

    $scope.findingComments = function(path, parent) {
      var result = [];
      if($rootScope.datatype){
        result = result.concat(_.filter($rootScope.datatype.comments, function(comment){ return comment.location == path; }));

        for (var i = 0; i < result.length; i++) {
          result[i].isMain = true;
          result[i].index = i + 1;
        }
        if(parent){
          if(path.indexOf('.') > -1){
            var subPath = path.substr(path.indexOf('.') + 1);
            var subResult = _.filter(parent.comments, function(comment){ return comment.location == subPath; });

            for (var i = 0; i < subResult.length; i++) {
              subResult[i].index = (i + 1);
              subResult[i].isMain = false;
            }

            result = result.concat(subResult);
          }
        }
      }
      return result;
    };

    $scope.findingBindings = function(path, parent) {
      var result = [];
      if($rootScope.datatype){
        result = _.filter($rootScope.datatype.valueSetBindings, function(binding){ return binding.location == path; });

        for (var i = 0; i < result.length; i++) {
          result[i].isMain = true;
        }


        if(result && result.length > 0) return result;
        else if(!parent) return result;
        else {
          if(path.indexOf('.') > -1){
            var subPath = path.substr(path.indexOf('.') + 1);
            var subResult = _.filter(parent.valueSetBindings, function(binding){ return binding.location == subPath; });

            for (var i = 0; i < subResult.length; i++) {
              subResult[i].isMain = false;
            }
            return subResult;
          }else {
            return [];
          }
        }
      }
      return result;
    };

    $scope.deleteValueSetBinding = function(binding){
      var index = $rootScope.datatype.valueSetBindings.indexOf(binding);
      if (index >= 0) {
        $rootScope.datatype.valueSetBindings.splice(index, 1);
        $scope.setDirty();
      }
    };

    $scope.deleteComment = function(comment){
      var index = $rootScope.datatype.comments.indexOf(comment);
      if (index >= 0) {
        $rootScope.datatype.comments.splice(index, 1);
        $scope.setDirty();
        $rootScope.recordChanged();
      }
    };

    $scope.addSev = function (node){
      var sev = {};
      sev.location = node.path;
      sev.value = '';
      sev.profilePath = $rootScope.getDatatypeLabel($rootScope.datatype) + "." + node.path;
      sev.name = node.name;
        var diff= _.filter($rootScope.datatype.singleElementValues, function (r) {
            return sev.profilePath!==r.profilePath;
        });
        diff.push(sev);
        $rootScope.datatype.singleElementValues=diff;

        node.sev = sev;

        node.sev.isMain = true;
        $rootScope.recordChanged();
    };

    $scope.deleteSev = function (node, parent){
      var index = $rootScope.datatype.singleElementValues.indexOf(node.sev);
      if (index >= 0) {
        $rootScope.datatype.singleElementValues.splice(index, 1);
        $scope.setDirty();
        $rootScope.recordChanged();
      }
      if(parent){
        node.sev = _.find(parent.singleElementValues, function(sev){ return sev.location  ==  node.position; });
        if(node.sev) {
          node.sev.isMain = false;
        }
      }else {
        node.sev = null;
      }
    }
  });
