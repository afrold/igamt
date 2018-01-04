/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('TableListCtrl', function($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, CloneDeleteSvc, TableService, TableLibrarySvc, blockUI, SegmentService,$mdDialog) {
  $scope.readonly = false;
  $scope.codeSysEditMode = false;
  $scope.codeSysForm = {};
  $scope.saved = false;
  $scope.message = false;
  $scope.params = null;
  $scope.predicate = 'value';
  $scope.reverse = false;
  $scope.selectedCodes = [];
  $scope.isDeltaCalled = false;
  $scope.itemsByPage = 30;
  $scope.tempCodeSys = '';
  $scope.applyCodeSysValue=null;
  $scope.tabStatus = {
    active: 1
  };
  $scope.defTabStatus = {
    active: 1
  };

  $scope.deltaTabStatus = {
    active: 0
  };
  $scope.init = function() {


    $scope.tabStatus = {
      active: 1
    };
    console.log("INITIALISING")
    $scope.selectedCodes = [];
    $rootScope.$on('event:cloneTableFlavor', function(event, table) {
      $scope.copyTable(table);
    });
  };

  $scope.codeCompare = function (a, b) {
    if (a.value < b.value)
      return -1;
    if (a.value > b.value)
      return 1;
    return 0;
  };
  $scope.reset = function() {
    console.log("Reset Table")
    blockUI.start();
    cleanState();
    $rootScope.table = angular.copy($rootScope.entireTable);
    // $rootScope.table.smallCodes=angular.copy($rootScope.table.codes);
    blockUI.stop();
  };
  $scope.redirectSeg = function(segmentRef) {
    SegmentService.get(segmentRef.id).then(function(segment) {
      var modalInstance = $modal.open({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        resolve: {
          destination: function() {
            return segment;
          }
        }
      });
      modalInstance.result.then(function() {
        $rootScope.editSeg(segment);
      });
    });
  };

  $scope.redirectDT = function(datatype) {
    console.log(datatype);
    DatatypeService.getOne(datatype.id).then(function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'redirectCtrl.html',
        controller: 'redirectCtrl',
        size: 'md',
        resolve: {
          destination: function() {
            return datatype;
          }
        }



      });
      modalInstance.result.then(function() {
        $rootScope.editDatatype(datatype);
      });

    });
  };

  $scope.submitNewCodeSystem = function (code, codeSys) {
    $rootScope.codeSystems.push(codeSys);
    code.codeSystem = codeSys;
  };

  $scope.applyAllCodeSys = function (newValue) {

    for (var i = 0, len = $scope.selectedCodes.length; i < len; i++) {
      $scope.selectedCodes[i].codeSystem = newValue;
    }
    $scope.selectedCodes = [];
    // $scope.setDirty();
  };

  $scope.codeSystemToApply = {
    label: function() {
      return "Apply Code system for selected codes";
    }
  };


  $scope.isBindingChanged = function() {
    for (var i = 0; i < $rootScope.references.length; i++) {
      var ref = $rootScope.references[i];

      if (ref.tableLink && ref.tableLink.isChanged) return true;
    }
    return false;
  };

  var cleanState = function() {
    $scope.saving = false;
    $scope.selectedChildren = [];
    if ($scope.editForm) {
      $scope.editForm.$setPristine();
      $scope.editForm.$dirty = false;
    }
    $rootScope.clearChanges();
  };
  $scope.callVSDelta = function() {

    $rootScope.$emit("event:openVSDelta");
  };

  $scope.changeTableLink = function(tableLink) {
    tableLink.isChanged = true;

    var t = $rootScope.tablesMap[tableLink.id];

    if (t == null) {
      tableLink.bindingIdentifier = null;
      tableLink.bindingLocation = null;
      tableLink.bindingStrength = null;
    } else {
      tableLink.bindingIdentifier = t.bindingIdentifier;
    }
  };

  $scope.AddBindingForValueSet = function(table) {
    var modalInstance = $modal.open({
      templateUrl: 'AddBindingForValueSet.html',
      controller: 'AddBindingForValueSet',
      windowClass: 'conformance-profiles-modal',
      resolve: {
        table: function() {
          return table;
        }
      }
    });
    modalInstance.result.then(function() {
      $scope.setDirty();
    });
  };
  $scope.getAttributeSelectLabel=function(label){
    if(label=='Undefined'){
      return "Not Defined";
    }else{
      return label;
    }
  };

  $scope.save = function() {
    console.log("EDIT FORM")
    console.log($scope.editForm);
    if ($rootScope.table.scope === 'USER') {
      $scope.saving = true;
      var table = $rootScope.table;
      var bindingIdentifier = table.bindingIdentifier;


      if (table.libIds == undefined) table.libIds = [];
      if (table.libIds.indexOf($rootScope.tableLibrary.id) == -1) {
        table.libIds.push($rootScope.tableLibrary.id);
      }

      TableService.save(table).then(function(result) {
        $rootScope.entireTable=angular.copy(result);
        $rootScope.table.codes=$rootScope.entireTable.codes;


        var oldLink = TableLibrarySvc.findOneChild(result.id, $rootScope.tableLibrary.children);

        $rootScope.table.dateUpdated = result.dateUpdated;
        $rootScope.$emit("event:updateIgDate");
        TableService.merge($rootScope.tablesMap[result.id], result);
        var newLink = TableService.getTableLink(result);
        newLink.bindingIdentifier = bindingIdentifier;
        TableLibrarySvc.updateChild($rootScope.tableLibrary.id, newLink).then(function(link) {
          oldLink.bindingIdentifier = link.bindingIdentifier;
          oldLink.ext = link.ext;
          cleanState();
          $rootScope.msg().text = "tableSaved";
          $rootScope.msg().type = "success";
          $rootScope.msg().show = true;
        }, function(error) {
          $scope.saving = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }, function(error) {
        $scope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    }
    $rootScope.saveBindingForValueSet();
    $rootScope.clearChanges();
  };

  $scope.addTable = function() {
    $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
    var newTable = angular.fromJson({
      id: new ObjectId().toString(),
      type: 'table',
      bindingIdentifier: '',
      name: '',
      version: '',
      oid: '',
      tableType: '',
      stability: '',
      extensibility: '',
      codes: []
    });
    $rootScope.tables.push(newTable);
    $rootScope.tablesMap[newTable.id] = newTable;
    $rootScope.table = newTable;
    $rootScope.recordChangeForEdit2('table', "add", newTable.id, 'table', newTable);
    $scope.setDirty();
  };


  $scope.makeCodeSystemEditable = function() {
    $scope.codeSysEditMode = true;
  };


  $scope.addCodeSystem = function() {
    if ($rootScope.codeSystems.indexOf($scope.codeSysForm.str) < 0) {
      if ($scope.codeSysForm.str && $scope.codeSysForm.str !== '') {
        $rootScope.codeSystems.push($scope.codeSysForm.str);
      }
    }
    $scope.codeSysForm.str = '';
    $scope.codeSysEditMode = false;
  };

  $scope.delCodeSystem = function(value) {
    $rootScope.table.codeSystems.splice($rootScope.codeSystems.indexOf(value), 1);
  }

  $scope.updateCodeSystem = function(table, codeSystem) {
    for (var i = 0; i < $rootScope.table.codes.length; i++) {
      $rootScope.table.codes[i].codeSystem = codeSystem;
      $scope.recordChangeValue($rootScope.table.codes[i], 'codeSystem', $rootScope.table.codes[i].codeSystem, table.id);
    }
  }

  $scope.addValue = function() {
    $rootScope.newValueFakeId = $rootScope.newValueFakeId ? $rootScope.newValueFakeId - 1 : -1;
    var newValue = {
      id: new ObjectId().toString(),
      type: 'value',
      value: '',
      label: '',
      codeSystem: '',
      codeUsage: 'R'
    };
    $rootScope.searchObject={};


    // $rootScope.table.smallCodes.unshift(newValue);
    $rootScope.table.codes.unshift(newValue);
    var newValueBlock = { targetType: 'table', targetId: $rootScope.table.id, obj: newValue };
    if (!$scope.isNewObject('table', 'add', $rootScope.table.id)) {
      $rootScope.recordChangeForEdit2('value', "add", null, 'value', newValueBlock);
    }
    $rootScope.table.numberOfCodes+=1;
    $scope.setDirty();
  };

  $scope.isValidTable = function () {
    var valueCodeSystemList = [];

    for (var i = 0; i < $rootScope.table.codes.length; i++) {
      var value = $rootScope.table.codes[i].value;
      var codeSystem = $rootScope.table.codes[i].codeSystem;

      if(!value || value === '') return false;
      if(!codeSystem || codeSystem === '') return false;

      var valueCodeSystem = value + codeSystem;


      if ($.inArray(valueCodeSystem,valueCodeSystemList) === -1) {
        valueCodeSystemList.push(valueCodeSystem);
      }else {
        return false;
      }
    }
    return true;
  };

  $rootScope.checkAll = false;
  $scope.toggleAll = function() {
    if ($scope.selectedCodes.length===0) {

      $scope.checkAllValues();
    } else {
      $scope.uncheckAllValues();
    }

  };
  $scope.isAllSelected=function () {
    return $scope.selectedCodes.length===$rootScope.table.codes.length;
  }

  $scope.addOrRemoveValue = function(c) {
    if (c.selected === true) {
      $scope.selectedCodes.push(c);
        $rootScope.table.numberOfCodes+=1;
    } else if (c.selected === false) {
      var index = $scope.selectedCodes.indexOf(c);
      if (index > -1) {
          $rootScope.table.numberOfCodes-=1;
        $scope.selectedCodes.splice(index, 1);
      }
    }
  };
  $scope.isSelected=function(c){
    var index = $scope.selectedCodes.indexOf(c);
    if (index > -1) {
      return true;

    }else{
      return false;
    }
  }
  $scope.selectCode=function(c){

    $scope.selectedCodes.push(c);

  }
  $scope.unSelectCode= function(c){
    var index = $scope.selectedCodes.indexOf(c);
    if (index > -1) {
      $scope.selectedCodes.splice(index, 1);
    }

  };
  $scope.toggleCode=function(c){
    if($scope.isSelected(c)){
      $scope.unSelectCode(c);
    }else{
      $scope.selectCode(c);
    }
  }
  $scope.deleteSlectedValues = function() {
    $rootScope.table.codes = _.difference($rootScope.table.codes, $scope.selectedCodes);
      $rootScope.table.numberOfCodes-=$scope.selectedCodes.length;
    // $rootScope.table.smallCodes = _.difference($rootScope.table.smallCodes, $scope.selectedCodes);
    $scope.selectedCodes = [];
    $rootScope.recordChanged();
  };
  $scope.checkAllValues = function() {
    $scope.selectedCodes = [];

    angular.forEach($rootScope.table.codes, function(c) {
      c.selected = true;
      $scope.selectedCodes.push(c);
    });
  };
  $scope.uncheckAllValues = function() {
    angular.forEach($rootScope.table.codes, function(c) {
      if (c.selected && c.selected === true) {
        c.selected = false;
      }
    });
    $scope.selectedCodes = [];
  };
  $scope.deleteValue = function(value) {
    // if (!$scope.isNewValueThenDelete(value.id)) {
    //     $rootScope.recordChangeForEdit2('value', "delete", value.id, 'id', value.id);
    // }
    console.log($scope.selectedCodes);
    $rootScope.table.codes.splice($rootScope.table.codes.indexOf(value), 1);
      $rootScope.table.numberOfCodes-=1;

      $scope.setDirty();
  };

  $scope.isNewValueThenDelete = function(id) {
    if ($rootScope.isNewObject('value', 'add', id)) {
      if ($rootScope.changes['value'] !== undefined && $rootScope.changes['value']['add'] !== undefined) {
        for (var i = 0; i < $rootScope.changes['value']['add'].length; i++) {
          var tmp = $rootScope.changes['value']['add'][i];
          if (tmp.obj.id === id) {
            $rootScope.changes['value']['add'].splice(i, 1);
            if ($rootScope.changes["value"]["add"] && $rootScope.changes["value"]["add"].length === 0) {
              delete $rootScope.changes["value"]["add"];
            }

            if ($rootScope.changes["value"] && Object.getOwnPropertyNames($rootScope.changes["value"]).length === 0) {
              delete $rootScope.changes["value"];
            }
            return true;
          }
        }
      }
      return true;
    }
    if ($rootScope.changes['value'] !== undefined && $rootScope.changes['value']['edit'] !== undefined) {
      for (var i = 0; i < $rootScope.changes['value']['edit'].length; i++) {
        var tmp = $rootScope.changes['value']['edit'][i];
        if (tmp.id === id) {
          $rootScope.changes['value']['edit'].splice(i, 1);
          if ($rootScope.changes["value"]["edit"] && $rootScope.changes["value"]["edit"].length === 0) {
            delete $rootScope.changes["value"]["edit"];
          }

          if ($rootScope.changes["value"] && Object.getOwnPropertyNames($rootScope.changes["value"]).length === 0) {
            delete $rootScope.changes["value"];
          }
          return false;
        }
      }
      return false;
    }
    return false;
  };

  $scope.isNewValue = function(id) {
    return $scope.isNewObject('value', 'add', id);
  };
  $scope.confirmSwitchSourceType=function (table) {
    if(table.sourceType&&table.sourceType=='EXTERNAL'){
      $scope.openConfirmToExternal(table);

    }
  };
  $scope.openConfirmToExternal=function(table){

    var modalInstance = $mdDialog.show({
      templateUrl: 'confirmToExternal.html',
      controller: ConfirmToExternal,
      preserveScope: true
    });

    function ConfirmToExternal($scope,$rootScope, $mdDialog,TableService) {
      $scope.url=$rootScope.table.referenceUrl;

      $scope.cancel=function(){
        $rootScope.table.sourceType="INTERNAL";
        $rootScope.table.referenceUrl=null;
        $mdDialog.hide();

      }
      $scope.confirm= function () {
        $rootScope.table.referenceUrl=$scope.url;
        $rootScope.table.sourceType="EXTERNAL";
        $rootScope.table.codes=[];
        $rootScope.recordChanged();
        $mdDialog.hide("OK");


      }
    }
    modalInstance.then(function(result) {
      if(result==='OK'){

        $scope.tabStatus = {
          active: 1
        };
        $scope.defTabStatus = {
          active: 1
        };

        $scope.deltaTabStatus = {
          active: 0
        };
      }
    }, function() {});

  };

  $scope.isNewTable = function(id) {
    return $scope.isNewObject('table', 'add', id);
  };

  $scope.close = function() {
    $rootScope.table = null;
  };

  $scope.copyTable = function(table) {
    CloneDeleteSvc.copyTable(table);
  };

  $scope.recordChangeValue = function(value, valueType, tableId) {
    if (!$scope.isNewTable(tableId)) {
      if (!$scope.isNewValue(value.id)) {
        $rootScope.recordChangeForEdit2('value', 'edit', value.id, valueType, value);
      }
    }
    $scope.setDirty();
  };

  $scope.recordChangeTable = function(table, valueType, value) {
    if (!$scope.isNewTable(table.id)) {
      $rootScope.recordChangeForEdit2('table', 'edit', table.id, valueType, value);
    }
    $scope.setDirty();
  };


  /**
   * @deprecated. Use $scope.setSelectedCodesUsage
   * @param table
   * @param usage
   */
  $scope.setAllCodeUsage = function(table, usage) {
    for (var i = 0, len = table.codes.length; i < len; i++) {
      if (table.codes[i].codeUsage !== usage) {
        table.codes[i].codeUsage = usage;
        if (!$scope.isNewTable(table.id) && !$scope.isNewValue(table.codes[i].id)) {
          $rootScope.recordChangeForEdit2('value', 'edit', table.codes[i].id, 'codeUsage', usage);
        }
      }
    }
    $scope.setDirty();
  };

  $scope.setSelectedCodesUsage = function(table, usage) {
    for (var i = 0, len = $scope.selectedCodes.length; i < len; i++) {
      if ($scope.selectedCodes[i].codeUsage !== usage) {
        $scope.selectedCodes[i].codeUsage = usage;
        if (!$scope.isNewTable(table.id) && !$scope.isNewValue($scope.selectedCodes[i].id)) {
          $rootScope.recordChangeForEdit2('value', 'edit', $scope.selectedCodes[i].id, 'codeUsage', usage);
        }
      }
    }
    $scope.setDirty();
  };



  $scope.delete = function(table) {
    CloneDeleteSvc.deleteValueSet(table);
  };


  $scope.shareModal = function(table) {
    $http.get('api/usernames').then(function(response) {
      var userList = response.data;
      var filteredUserList = userList.filter(function(user) {
        // Add accountId var
        user.accountId = user.id;
        var isPresent = false;
        if (table.shareParticipantIds) {
          for (var i = 0; i < table.shareParticipantIds.length; i++) {
            if (table.shareParticipantIds[i].accountId == user.id) {
              isPresent = true;
            }
          }
        }
        if (!isPresent) return user;
      });

      var modalTemplate = "ShareTableErrorModal.html";
      if (table.status === "PUBLISHED") {
        modalTemplate = "ShareTableModal.html";
      }
      var modalInstance = $modal.open({
        templateUrl: modalTemplate,
        controller: 'ShareTableCtrl',
        size: 'lg',
        resolve: {
          igdocumentSelected: function() {
            return table;
          },
          userList: function() {
            return _.filter(filteredUserList, function(user) {
              return user.id != $rootScope.accountId && table.shareParticipantIds && table.shareParticipantIds != null && table.shareParticipantIds.indexOf(user.id) == -1;
            });
          }
        }
      });

      modalInstance.result.then(function(result) {}, function() {
        if (modalTemplate === 'ShareDatatypeModal.html') {}
        // $log.info('Modal dismissed at: ' + new Date());
      });

    }, function(error) {
      console.log(error);
    });
  };
});
