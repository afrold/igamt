/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('SegmentListCtrl', function($scope, $rootScope, Restangular, ngTreetableParams, CloneDeleteSvc, $filter, $http, $modal, $timeout, $q, SegmentService, FieldService, FilteringSvc, MastermapSvc, SegmentLibrarySvc, DatatypeLibrarySvc, MessageService, DatatypeService, TableService, blockUI, ValidationService,$mdDialog) {
  $scope.accordStatus = {
    isCustomHeaderOpen: false,
    isFirstOpen: true,
    isSecondOpen: true,
    isThirdOpen: true,
    isFourthOpen: true,
    isFifthOpen: true,
    isSixthOpen: true,
    isSeventhOpen: true,
    isFirstDisabled: false
  };

  $scope.defTabStatus = {
    active:1
  };

  $scope.deltaTabStatus = {
    active : 0
  };

  $scope.tabStatus = {
    active: 1
  };

  $scope.initSegment = function() {
    $scope.accordStatus = {
      isCustomHeaderOpen: false,
      isFirstOpen: false,
      isSecondOpen: true,
      isThirdOpen: false,
      isFourthOpen: false,
      isFifthOpen: false,
      isSixthOpen: false,
      isSeventhOpen: false,
      isFirstDisabled: false
    };

    $scope.defTabStatus = {
      active:1
    };

    $scope.deltaTabStatus = {
      active : 0
    };

    $scope.tabStatus = {
      active: 1
    };
  };

  $scope.editableDT = '';
  $scope.editableVS = '';

  $scope.readonly = false;
  $scope.saved = false;
  $scope.message = false;
  $scope.segmentCopy = null;
  $scope.selectedChildren = [];
  $scope.saving = false;
  $scope.OtoX = function(message) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'OtoX.html',
      controller: 'OtoXCtrl',
      size: 'md',
      scope:$scope,
      preserveScope:true,
      locals: {
        message: message
      }
    });
    modalInstance.then(function() {
      $scope.setDirty();

      if ($scope.segmentsParams)
        $scope.segmentsParams.refresh();
    });
  };

  $scope.cloneCoConstraintRow = function (rowIndex){
    if($rootScope.segment.coConstraintsTable.ifColumnDefinition){
      if($rootScope.segment.coConstraintsTable.ifColumnData){
        var copy = angular.copy($rootScope.segment.coConstraintsTable.ifColumnData[rowIndex]);
        copy.isNew = true;
        $rootScope.segment.coConstraintsTable.ifColumnData.splice(rowIndex + 1, 0, copy);
      }
    }
    if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList && $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length > 0){
      if($rootScope.segment.coConstraintsTable.thenMapData){
        for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
          if($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id]){
            var copy = angular.copy($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id][rowIndex]);
            copy.isNew = true;
            $rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id].splice(rowIndex + 1, 0, copy);
          }
        }
      }
    }
    if($rootScope.segment.coConstraintsTable.userColumnDefinitionList && $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length > 0){
      if($rootScope.segment.coConstraintsTable.userMapData){
        for(var i in $rootScope.segment.coConstraintsTable.userColumnDefinitionList){
          if($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id]){
            var copy = angular.copy($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id][rowIndex]);
            copy.isNew = true;
            $rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id].splice(rowIndex + 1, 0, copy);
          }
        }
      }
    }
    $rootScope.segment.coConstraintsTable.rowSize = $rootScope.segment.coConstraintsTable.rowSize + 1;
    SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
      $rootScope.coConRowIndexList = result;
    });
    $scope.setDirty();
  };


  $scope.delCoConstraintRow = function (rowIndex){
    if($rootScope.segment.coConstraintsTable.ifColumnDefinition){
      $rootScope.segment.coConstraintsTable.ifColumnData.splice(rowIndex, 1);
    }

    if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList && $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length > 0){
      if($rootScope.segment.coConstraintsTable.thenMapData){
        for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
          if($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id]){
            $rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id].splice(rowIndex, 1);
          }
        }
      }
    }

    if($rootScope.segment.coConstraintsTable.userColumnDefinitionList && $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length > 0){
      if($rootScope.segment.coConstraintsTable.userMapData){
        for(var i in $rootScope.segment.coConstraintsTable.userColumnDefinitionList){
          if($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id]){
            $rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id].splice(rowIndex, 1);
          }
        }
      }
    }

    $rootScope.segment.coConstraintsTable.rowSize = $rootScope.segment.coConstraintsTable.rowSize - 1;
    SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
      $rootScope.coConRowIndexList = result;
    });
    $scope.setDirty();
  };


  $scope.delCoConstraintIFDefinition = function (ifColumnDefinition){
    $rootScope.segment.coConstraintsTable.ifColumnDefinition = null;
    $rootScope.segment.coConstraintsTable.ifColumnData = [];

    $scope.resetCoConstraintsTable();
    SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
      $rootScope.coConRowIndexList = result;
    });

    $scope.setDirty();
  };

  $scope.delCoConstraintTHENDefinition = function (columnDefinition) {
    var index = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.indexOf(columnDefinition);

    if (index > -1) {
      $rootScope.segment.coConstraintsTable.thenMapData[columnDefinition.id] = null;
      $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.splice(index, 1);
    }

    $scope.resetCoConstraintsTable();
    SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
      $rootScope.coConRowIndexList = result;
    });
    $scope.setDirty();
  };

  $scope.checkIfDataDuplicated = function (value, list) {
    if(value && value !== ''){
      var count = 0;
      for (var i = 0; i < list.length; i++) {
        if(list[i].valueData.value === value){
          count = count + 1;
          if(count === 2) return true;
        }
      }
    }
    return false;
  };

  $scope.checkDuplicatedDM = function (item) {
    if($rootScope.segment !== null &&
        $rootScope.segment.dynamicMappingDefinition != null &&
        $rootScope.segment.dynamicMappingDefinition.dynamicMappingItems !== null &&
        item !== null &&
        item.firstReferenceValue !== null){

      var count = 0;

      for (var i = 0; i < $rootScope.segment.dynamicMappingDefinition.dynamicMappingItems.length; i++) {
        if($rootScope.segment.dynamicMappingDefinition.dynamicMappingItems[i].firstReferenceValue === item.firstReferenceValue){
          count = count + 1;
          if(count === 2) return true;
        }
      }
    }
    return false;
  };

  $scope.checkThenData = function (def, data, dynamicMappingTableCodes, dtLib, tablesMap){
    if(data){
      if(def.path=='2'){
        if(def.constraintType === "dmr"){
          // if(!data.valueData.value || data.valueData.value === '') return 'Missing OBX-2 value';
          // var found = _.find(dynamicMappingTableCodes, function(code){ return code.value === data.valueData.value; });
          // if(!found) return 'Missing OBX-2 value';
        }else if(def.constraintType === "dmf"){
          if(data.valueData.value){
            var foundName = _.find(dynamicMappingTableCodes, function(code){ return code.value === data.valueData.value; });
            if(foundName) {
                if(!data.datatypeId || data.datatypeId === '') return 'Missing datatype';
                var found = _.find(dtLib, function(link){ return link.id === data.datatypeId; });
                if(!found) return 'Missing datatype';
            }
          }
        }
      }else {
        if(def.constraintType === 'valueset' && data.valueSets && data.valueSets.length > 0) {
          for (var i = 0; i < data.valueSets.length; i++) {
            if(!tablesMap[data.valueSets[i].tableId]) return 'Value Set binding is broken.';
            else if(tablesMap[data.valueSets[i].tableId].numberOfCodes > 500 ) return 'For internally managed value sets, This value set exceeds limit of 500. It will be ignored at validation';
            else if(tablesMap[data.valueSets[i].tableId].numberOfCodes === 0 ) return 'This value set has no codes. It will be ignored at validation';
          }
        }
        // if(def.constraintType === 'value' && data.valueData.value.replace(/\s/g,'')  === '') return 'Missing value data';
      }
    }else {
      return null;
    }
  };

  $scope.delCoConstraintUSERDefinition = function (columnDefinition) {
    var index = $rootScope.segment.coConstraintsTable.userColumnDefinitionList.indexOf(columnDefinition);

    if (index > -1) {
      $rootScope.segment.coConstraintsTable.userMapData[columnDefinition.id] = null;
      $rootScope.segment.coConstraintsTable.userColumnDefinitionList.splice(index, 1);
    };
    $scope.resetCoConstraintsTable();
    $scope.setDirty();
  };

  $scope.resetCoConstraintsTable = function (){
    if(!$rootScope.segment.coConstraintsTable.ifColumnDefinition){
      if(!$rootScope.segment.coConstraintsTable.thenColumnDefinitionList || $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length == 0){
        if(!$rootScope.segment.coConstraintsTable.userColumnDefinitionList || $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length == 0){
          $rootScope.segment.coConstraintsTable = {};
        }
      }
    }
    SegmentService.initCoConstraintsTable($rootScope.segment).then(function(result) {
      if(result) SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result2) {
        $rootScope.coConRowIndexList = result2;
      });
    });
  };

  $scope.openDialogForEditCoConstraintIFDefinition = function(coConstraintIFDefinition){
    var modalInstance = $mdDialog.show({
      templateUrl: 'CoConstraintIFDefinition.html',
      controller: 'CoConstraintIFDefinitionCtrl',
      scope: $rootScope,        // use parent scope in template
      preserveScope: true,
      locals: {
        coConstraintIFDefinition:coConstraintIFDefinition
      }

    });
    modalInstance.then(function(ifColumnDefinition) {
      if(ifColumnDefinition){
        if(!$rootScope.segment.coConstraintsTable) {
          $rootScope.segment.coConstraintsTable = {};
          $rootScope.segment.coConstraintsTable.rowSize = 0;
        }

        if(!$rootScope.segment.coConstraintsTable.ifColumnDefinition) {
          $rootScope.segment.coConstraintsTable.ifColumnData = [];
          for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.rowSize; i < len1; i++) {
            $rootScope.segment.coConstraintsTable.ifColumnData.push({});
          }
        }

        $rootScope.segment.coConstraintsTable.ifColumnDefinition = ifColumnDefinition;
      }
      $scope.setDirty();
    });
  };

  $scope.openDialogForEditCoConstraintTHENDefinition = function(coConstraintTHENDefinition){
    var modalInstance = $mdDialog.show({
      templateUrl: 'CoConstraintTHENDefinition.html',
      scope: $rootScope,
      preserveScope: true,
      controller: 'CoConstraintTHENDefinitionCtrl',
      locals: {
        coConstraintTHENDefinition: coConstraintTHENDefinition

      }
    });
    modalInstance.then(function(thenColumnDefinition) {
      if(thenColumnDefinition){
        if(!$rootScope.segment.coConstraintsTable) {
          $rootScope.segment.coConstraintsTable = {};
          $rootScope.segment.coConstraintsTable.rowSize = 0;
        }

        if(!$rootScope.segment.coConstraintsTable.thenColumnDefinitionList) {
          $rootScope.segment.coConstraintsTable.thenColumnDefinitionList = [];
          $rootScope.segment.coConstraintsTable.thenMapData = {};
        }

        if(!thenColumnDefinition.id){
          thenColumnDefinition.id = new ObjectId().toString();
          $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.push(thenColumnDefinition);
          $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

          for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.rowSize; i < len1; i++) {
            $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id].push({});
          }
        }else{
          for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
            if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id == thenColumnDefinition.id) {
              $rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i] = thenColumnDefinition;
            }
          }
        }
      }
      SegmentService.initCoConstraintsTable($rootScope.segment).then(function(result) {});
      $scope.setDirty();
    });
  };

  $scope.openDialogForEditCoConstraintUSERDefinition = function (coConstraintUSERDefinition){
    var modalInstance = $mdDialog.show({
      templateUrl: 'CoConstraintUSERDefinition.html',
      controller: 'CoConstraintUSERDefinitionCtrl',
      scope: $rootScope,
      preserveScope: true,
      locals: {
        coConstraintUSERDefinition:  coConstraintUSERDefinition
      }

    });
    modalInstance.then(function(userColumnDefinition) {
      if(userColumnDefinition){
        if(!$rootScope.segment.coConstraintsTable) {
          $rootScope.segment.coConstraintsTable = {};
          $rootScope.segment.coConstraintsTable.rowSize = 0;
        }

        if(!$rootScope.segment.coConstraintsTable.userColumnDefinitionList) {
          $rootScope.segment.coConstraintsTable.userColumnDefinitionList = [];
          $rootScope.segment.coConstraintsTable.userMapData = {};
        }

        if(!userColumnDefinition.id){
          userColumnDefinition.id = new ObjectId().toString();
          $rootScope.segment.coConstraintsTable.userColumnDefinitionList.push(userColumnDefinition);

          $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

          for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.rowSize; i < len1; i++) {
            $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id].push({});
          }
        }else{
          for(var i in $rootScope.segment.coConstraintsTable.userColumnDefinitionList){
            if($rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id == userColumnDefinition.id) {
              $rootScope.segment.coConstraintsTable.userColumnDefinitionList[i] = userColumnDefinition;
            }
          }
        }
      }
      $scope.setDirty();
    });
  };

  $scope.openPredicateDialog = function(node) {
    if (node.usage == 'C') $scope.managePredicate(node);
  };

  $scope.coConSortableOption = {
    update: function(e, ui) {
    },
    stop: function(e, ui) {
      $timeout(
        function () {
          var newIfColumnData = [];
          for(var i=0, len1=$rootScope.coConRowIndexList.length; i < len1; i++){
            var rowIndex = $rootScope.coConRowIndexList[i].rowIndex;
            newIfColumnData.push($rootScope.segment.coConstraintsTable.ifColumnData[rowIndex]);
          }
          $rootScope.segment.coConstraintsTable.ifColumnData = newIfColumnData;

          for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList) {
            if ($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id]) {
              var oldThenMapData = $rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id];
              var newThenMapData = [];

              for(var j=0, len1=$rootScope.coConRowIndexList.length; j < len1; j++){
                var rowIndex = $rootScope.coConRowIndexList[j].rowIndex;
                newThenMapData.push(oldThenMapData[rowIndex]);
              }
              $rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id] = newThenMapData;
            }
          }

          for(var i in $rootScope.segment.coConstraintsTable.userColumnDefinitionList) {
            if ($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id]) {
              var oldUserMapData = $rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id];
              var newUserMapData = [];

              for(var j=0, len1=$rootScope.coConRowIndexList.length; j < len1; j++){
                var rowIndex = $rootScope.coConRowIndexList[j].rowIndex;
                newUserMapData.push(oldUserMapData[rowIndex]);
              }
              $rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id] = newUserMapData;
            }
          }
          SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
            $rootScope.coConRowIndexList = result;
          });
          $scope.setDirty();
        }, 100);
    }
  };

  $scope.validateSegment = function() {
    ValidationService.validateSegment($rootScope.segment, $rootScope.igdocument.profile.metaData.hl7Version).then(function(result) {
      $rootScope.validationMap = {};
      $rootScope.childValidationMap={};
      $rootScope.showSegErrorNotification = true;
      $rootScope.validationResult = result;
      $rootScope.buildValidationMap($rootScope.validationResult);
    });
  };
  $scope.isSegmentValidated = function() {
    if ($rootScope.segment && ($rootScope.validationResult.targetId === $rootScope.segment.id || $rootScope.childValidationMap[$rootScope.segment.id])) {
      return true;
    } else {
      return false;
    }
  };

  $scope.setErrorNotification = function() {
    $rootScope.showSegErrorNotification  = !$rootScope.showSegErrorNotification;
  };

  $scope.deleteField = function(fieldToDelete, segment) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeleteField.html',
      controller: 'DeleteFieldCtrl',
      locals: {
        fieldToDelete: fieldToDelete,
        segment: segment
      }
    });
    modalInstance.then(function(result) {
      if(result&&result!=='cancel'){
        $scope.setDirty();

        if ($scope.segmentsParams)
          $scope.segmentsParams.refresh();
      }

    });
  };
  $scope.editableField = '';
  $scope.editField = function(field) {
    $scope.editableField = field.id;
    $scope.fieldName = field.name;

  };

  $scope.deleteConformanceStatementFromList = function(c) {
    $rootScope.segment.conformanceStatements.splice($rootScope.segment.conformanceStatements.indexOf(c), 1);

    $scope.setDirty();
  };

  $scope.deletePredicateFromList = function(p) {
    $rootScope.segment.predicates.splice($rootScope.segment.predicates.indexOf(p), 1);

    $scope.setDirty();
  };

  $scope.AddBindingForSegment = function(segment) {
    var modalInstance = $modal.open({
      templateUrl: 'AddBindingForSegment.html',
      controller: 'AddBindingForSegment',
      windowClass: 'conformance-profiles-modal',
      resolve: {
        segment: function() {
          return segment;
        }
      }
    });
    modalInstance.result.then(function() {
      $scope.setDirty();
    });
  };


  $scope.isDual = function (def){
    if(def) {
      if($rootScope.segment.name === 'OBX' && def.path + "" === "5") return true;
    }
    return false;
  };

  $scope.editValueSetForVaries = function (id, index){
    $rootScope.segment.coConstraintsTable.thenMapData[id][index].valueData = {};
    $scope.openDialogForEditValueSetThenMapData(id,index);
  };

  $scope.editValueForVaries = function (id, index){
    $rootScope.segment.coConstraintsTable.thenMapData[id][index].valueData = {};
    $rootScope.segment.coConstraintsTable.thenMapData[id][index].valueData.value = " ";

    $rootScope.segment.coConstraintsTable.thenMapData[id][index].valueSets = [];

  };




  $scope.changeSegmentLink = function(segmentLink) {
    segmentLink.isChanged = true;

    var t = $rootScope.segmentsMap[segmentLink.id];

    if (t == null) {
      segmentLink.name = null;
      segmentLink.ext = null;
      segmentLink.label = null;
    } else {
      segmentLink.name = t.name;
      segmentLink.ext = t.ext;
      segmentLink.label = t.label;
    }
  };


  $scope.backField = function() {
    $scope.editableField = '';
  };
  $scope.applyField = function(segment, field, name, position) {
    blockUI.start();
    $scope.editableField = '';
    if (field) {
      field.name = name;


    }
    if (position) {
      MessageService.updatePosition(segment.fields, field.position - 1, position - 1);
    }
    $scope.setDirty();

    if ($scope.segmentsParams)
      $scope.segmentsParams.refresh();
    $scope.Posselected = false;
    blockUI.stop();

  };




  $scope.selectDT = function(field, datatype) {
    if (datatype) {
      $scope.DTselected = true;
      blockUI.start();
      $scope.editableDT = '';

      field.datatype.ext = JSON.parse(datatype).ext;
      field.datatype.id = JSON.parse(datatype).id;
      field.datatype.label = JSON.parse(datatype).label;
      field.datatype.name = JSON.parse(datatype).name;
      $scope.setDirty();
      $rootScope.processElement(field);
      if ($scope.segmentsParams)
        $scope.segmentsParams.refresh();
      $scope.DTselected = false;
      blockUI.stop();
    } else {
      $scope.otherDT(field);
    }
  };
  // $scope.applyDT = function(field, datatype) {
  //     blockUI.start();
  //     $scope.editableDT = '';

  //     field.datatype.ext = JSON.parse(datatype).ext;
  //     field.datatype.id = JSON.parse(datatype).id;
  //     field.datatype.label = JSON.parse(datatype).label;
  //     field.datatype.name = JSON.parse(datatype).name;
  //     $scope.setDirty();
  //     $rootScope.processElement(field);
  //     if ($scope.segmentsParams)
  //         $scope.segmentsParams.refresh();
  //     $scope.DTselected = false;
  //     blockUI.stop();

  // };
  $scope.otherDT = function(field) {
    console.log("Changing a data type from field")
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
      $scope.editableDT = '';
      if ($scope.segmentsParams) {
        $scope.segmentsParams.refresh();
      }
    });

  };

  $scope.editDT = function(field) {
    $scope.editableDT = field.id;
    $scope.editDTMap={};

    $scope.results = [];

    angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
      if (dtLink.name && dtLink.name === field.datatype.name) {
        if(!$scope.editDTMap[dtLink.id]){
          $scope.editDTMap[dtLink.id]=dtLink;
          $scope.results.push(dtLink);
        }
      }
    });
    //
    // angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
    //     if (dtLink.name && dtLink.name === field.datatype.name) {
    //         $scope.results.push(dtLink);
    //     }
    // });

  };

  $scope.backDT = function() {
    $scope.editableDT = '';
    if ($scope.segmentsParams) {
      $scope.segmentsParams.refresh();
    }
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
        if(result&&result!=='cancel'){
          $rootScope.editDatatype(datatype);
        }
      });



    });
  };

  $scope.loadVS = function($query) {


    return $rootScope.tables.filter(function(table) {
      return table.bindingIdentifier.toLowerCase().indexOf($query.toLowerCase()) != -1;
    });

  };

  $scope.editVSModal = function(field) {
    var modalInstance = $modal.open({
      templateUrl: 'editVSModal.html',
      controller: 'EditVSCtrl',
      windowClass: 'edit-VS-modal',
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
      $scope.setDirty();
      if ($scope.segmentsParams) {
        $scope.segmentsParams.refresh();
      }
    });

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
  // $scope.applyVS = function(field) {
  //     $scope.editableVS = '';
  //     if (field.table === null) {
  //         field.table = {
  //             id: '',
  //             bindingIdentifier: ''

  //         };
  //     }
  //     field.table.id = $scope.selectedValueSet.id;
  //     field.table.bindingIdentifier = $scope.selectedValueSet.bindingIdentifier;
  //     $scope.setDirty();
  //     $scope.VSselected = false;

  // };


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
        if(result&&result!=='cancel'){
          $rootScope.editTable(valueSet);
        }
      });
    });
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
    $scope.VSselected = false;


  };

  $scope.selectedVS = function() {
    return ($scope.selectedValueSet !== undefined);
  };
  $scope.unselectVS = function() {
    $scope.selectedValueSet = undefined;
    $scope.VSselected = false;

    //$scope.newSeg = undefined;
  };
  $scope.isVSActive = function(id) {
    if ($scope.selectedValueSet) {
      return $scope.selectedValueSet.id === id;
    } else {
      return false;
    }

  };

  $scope.addFieldModal = function(segment) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'AddFieldModal.html',
      controller: 'AddFieldCtrl',
      scope: $scope,
      preserveScope: true,
      locals: {

        valueSets: $rootScope.tables,
        datatypes:  $rootScope.datatypes,
        segment:  segment,
        messageTree: $rootScope.messageTree
      }


    });
    modalInstance.then(function(field) {
      $scope.setDirty();

      if ($scope.segmentsParams) {
        $scope.segmentsParams.refresh();
      }
    });
  };

  $scope.findOptions = function(dtId) {
    var result = [];
    result.push('1');
    if(!dtId || !$rootScope.datatypesMap[dtId]) return result;

    if(_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT){
        return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
      })){
      var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

      var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

      if(bls && bls.length > 0) return bls;
    }

    return result;
  };

  $scope.deleteVS = function (item, array){
    var index = array.indexOf(item);
    if (index >= 0) {
      array.splice(index, 1);
      $scope.setDirty();
    }
  };

  $scope.addCoConstraintRow = function() {
    var isAdded = false;
    if(!$rootScope.segment.coConstraintsTable.ifColumnData) $rootScope.segment.coConstraintsTable.ifColumnData = [];
    if(!$rootScope.segment.coConstraintsTable.thenMapData) $rootScope.segment.coConstraintsTable.thenMapData = {};
    if(!$rootScope.segment.coConstraintsTable.userMapData) $rootScope.segment.coConstraintsTable.userMapData = {};

    if($rootScope.segment.coConstraintsTable.ifColumnDefinition){
      var newIFData = {};
      newIFData.valueData = {};
      newIFData.bindingLocation = null;
      newIFData.isNew = true;

      $rootScope.segment.coConstraintsTable.ifColumnData.unshift(newIFData);
      isAdded = true;
    }

    if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
      for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length; i < len1; i++) {
        var thenColumnDefinition = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i];

        var newTHENData = {};
        newTHENData.valueData = {};
        newTHENData.valueSets = [];
        newTHENData.isNew = true;

        if(!$rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id]) $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

        $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id].unshift(newTHENData);
        isAdded = true;
      };
    }

    if($rootScope.segment.coConstraintsTable.userColumnDefinitionList){
      for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length; i < len1; i++) {
        var userColumnDefinition = $rootScope.segment.coConstraintsTable.userColumnDefinitionList[i];

        var newUSERData = {};
        newUSERData.text = "";
        newUSERData.isNew = true;

        if(!$rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id]) $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

        $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id].unshift(newUSERData);
        isAdded = true;
      };
    }

    if(isAdded) {
      $rootScope.segment.coConstraintsTable.rowSize = $rootScope.segment.coConstraintsTable.rowSize + 1;
      SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
        $rootScope.coConRowIndexList = result;
      });
      $scope.setDirty();
    }
  };

  $scope.deleteCoConstraints = function() {
    $rootScope.segment.coConstraintsTable = {};
    $rootScope.segment.coConstraintsTable.rowSize = 0;
    SegmentService.initCoConstraintsTable($rootScope.segment).then(function(result) {
      SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
        $rootScope.coConRowIndexList = result;
      });
      $scope.setDirty();
    });
  };


  $scope.headerChanged = function() {
  };

  $scope.reset = function() {
    blockUI.start();
    SegmentService.reset();

    if ($scope.editForm) {
      $scope.editForm.$dirty = false;
      $scope.editForm.$setPristine();
    }
    $rootScope.clearChanges();

    $rootScope.addedDatatypes = [];
    $rootScope.addedTables = [];
    if ($scope.segmentsParams) {
      $scope.segmentsParams.refresh();
    }
    SegmentService.initRowIndexForCocon($rootScope.segment.coConstraintsTable).then(function(result) {
      $rootScope.coConRowIndexList = result;
    });
    blockUI.stop();
  };

  $scope.close = function() {
    $rootScope.segment = null;
    $scope.refreshTree();
    $scope.loadingSelection = false;
  };

  $scope.copy = function(segment) {
    CloneDeleteSvc.copySegment(segment);
  };

  $scope.delete = function(segment) {
    CloneDeleteSvc.deleteSegment(segment);
    $rootScope.$broadcast('event:SetToC');
  };


  $scope.hasChildren = function(node) {
    if (node && node != null) {
      if (node.fields && node.fields.length > 0) return true;
      else {
        if (node.datatype && $rootScope.getDatatype(node.datatype.id)) {
          if ($rootScope.getDatatype(node.datatype.id).components && $rootScope.getDatatype(node.datatype.id).components.length > 0) return true;
        }
      }
    }
    return false;
  };

  $scope.isAvailableConstantValue = function(node) {
    if($scope.hasChildren(node)) return false;
    var bindings = $scope.findingBindings(node);
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
    $rootScope.recordChangeForEdit2('field', 'edit', node.id, 'datatype', node.datatype.id);
    $scope.refreshTree();
  };

  $scope.refreshTree = function() {
    if ($scope.segmentsParams)
      $scope.segmentsParams.refresh();
  };

  $scope.goToTable = function(table) {
    $scope.$emit('event:openTable', table);
  };

  $scope.goToDatatype = function(datatype) {
    $scope.$emit('event:openDatatype', datatype);
  };

  $scope.deleteTable = function(node) {
    node.table = null;
    $rootScope.recordChangeForEdit2('field', 'edit', node.id, 'table', null);
  };

  $scope.findDTByComponentId = function(componentId) {
    return $rootScope.parentsMap && $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
  };

  $scope.isSub = function(component) {
    return $scope.isSubDT(component);
  };

  $scope.isSubDT = function(component) {
    return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
  };

  $scope.managePredicate = function(node) {
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'PredicateSegmentCtrl.html',
      controller: 'PredicateSegmentCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        selectedSegment: $rootScope.segment,
        currentPredicate: null,
        selectedNode: node,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "segment"
      }
    }).then(function(segment) {
      if (segment) {
        $rootScope.segment = segment;
        $scope.setDirty();
      }
    });
  };

  $scope.manageConformanceStatement = function() {
    $mdDialog.show({
      parent: angular.element(document).find('body'),
      templateUrl: 'ConformanceStatementSegmentCtrl.html',
      controller: 'ConformanceStatementSegmentCtrl',
      scope:$scope,
      preserveScope:true,
      locals: {
        selectedSegment : $rootScope.segment,
        currentConformanceStatements : null,
        config : $rootScope.config,
        tables : $rootScope.tables,
        mode : "segment"
      }
    }).then(function(segment) {
      if (segment) {
        $rootScope.segment = segment;
        $scope.setDirty();
      }
    });
  };

  $scope.show = function(segment) {
    return true;
  };

  $scope.countConformanceStatements = function(position) {
    var count = 0;
    if ($rootScope.segment != null) {
      for (var i = 0, len1 = $rootScope.segment.conformanceStatements.length; i < len1; i++) {
        if ($rootScope.segment.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
          count = count + 1;
      }
    }
    return count;
  };

  $scope.countPredicate = function(position) {
    var count = 0;
    if ($rootScope.segment != null) {
      for (var i = 0, len1 = $rootScope.segment.predicates.length; i < len1; i++) {
        if ($rootScope.segment.predicates[i].constraintTarget.indexOf(position + '[') === 0)
          count = count + 1;
      }
    }
    return count;
  };

  $scope.deletePredicateByPosition = function(position, segment) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'DeletePredicate.html',
      controller: 'DeleteSegmentPredicateCtrl',
      size: 'md',
      locals: {
        position: position,
        segment: segment

      }
    });
    modalInstance.then(function(result){
      if(result&&result!=='cancel'){
          $rootScope.recordChanged();
      }
    });
  };

  $scope.countPredicateOnComponent = function(position, componentId) {
    return $scope.getComponentPredicate(position, componentId) != null ? 1:0;
  };

  $scope.getComponentPredicate = function(position, componentId) {
    var dt = $scope.findDTByComponentId(componentId);
    if (dt != null) {
      for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
        if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
          return dt.predicates[i];
      }
    }
    return null;
  };



  $scope.isRelevant = function(node) {
    return SegmentService.isRelevant(node);
  };

  $scope.isBranch = function(node) {
    SegmentService.isBranch(node);
  };

  $scope.isVisible = function(node) {
    return SegmentService.isVisible(node);
  };

  $scope.children = function(node) {
    return SegmentService.getNodes(node);
  };

  $scope.getParent = function(node) {
    return SegmentService.getParent(node);
  };

  $scope.getSegmentLevelConfStatements = function(element) {
    return SegmentService.getSegmentLevelConfStatements(element);
  };

  $scope.getSegmentLevelPredicates = function(element) {
    return SegmentService.getSegmentLevelPredicates(element);
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
    for (var i = 0; i < $rootScope.segment.fields.length; i++) {
      var component = $rootScope.segment.fields[i];
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
    return $rootScope.segment && $rootScope.segment != null && $rootScope.segment.fields && $scope.selectedChildren.length === $rootScope.segment.fields.length;
  };


  /**
   * TODO: update master map
   */
  $scope.createNewField = function() {
    if ($rootScope.segment != null) {
      if (!$rootScope.segment.fields || $rootScope.segment.fields === null)
        $rootScope.segment.fields = [];
      var child = FieldService.create($rootScope.segment.fields.length + 1);
      $rootScope.segment.fields.push(child);
      //TODO update master map
      //TODO:remove as legacy code
      $rootScope.parentsMap[child.id] = $rootScope.segment;
      if ($scope.segmentsParams)
        $scope.segmentsParams.refresh();
    }
  };

  /**
   * TODO: update master map
   */
  $scope.deleteFields = function() {
    if ($rootScope.segment != null && $scope.selectedChildren != null && $scope.selectedChildren.length > 0) {
      FieldService.deleteList($scope.selectedChildren, $rootScope.segment);
      //TODO update master map
      //TODO:remove as legacy code
      angular.forEach($scope.selectedChildren, function(child) {
        delete $rootScope.parentsMap[child.id];
      });
      $scope.selectedChildren = [];
      if ($scope.segmentsParams)
        $scope.segmentsParams.refresh();
    }
  };

  $scope.isValidCoConstraints = function() {
    if($rootScope.segment.coConstraintsTable){
      if($rootScope.segment.coConstraintsTable.ifColumnDefinition && $rootScope.segment.coConstraintsTable.ifColumnData){
        var tempIfData = [];
        for(var i = 0; i < $rootScope.segment.coConstraintsTable.ifColumnData.length; i++){
          if($rootScope.segment.coConstraintsTable.ifColumnData[i]){
            if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData){
              if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value){
                if($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value === '') return false;

                else{
                  if(tempIfData.indexOf($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value) > -1) return false;
                  tempIfData.push($rootScope.segment.coConstraintsTable.ifColumnData[i].valueData.value);
                }
              } else return false;
            } else return false;
          } else return false;
        }
      } else return false;

      if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
        for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
          var def = $rootScope.segment.coConstraintsTable.thenColumnDefinitionListForDisplay[i];
          if(def && def.id){
            if(def.path === '2'){
              if(def.constraintType === 'dmf'){
                var thenDataList = $rootScope.segment.coConstraintsTable.thenMapData[def.id];
                if(thenDataList){
                  for(var j = 0; j < thenDataList.length; j++){
                    var data = thenDataList[j];
                    if(data){
                      if(data.valueData.value){
                        var foundName = _.find($rootScope.dynamicMappingTable.codes, function(code){ return code.value === data.valueData.value; });
                        if(foundName) {
                          if(!data.datatypeId || data.datatypeId === '') return false;
                          var found = _.find($rootScope.igdocument.profile.datatypeLibrary.children, function(link){ return link.id === data.datatypeId; });
                          if(!found) return false;
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    return true;
  };


  $scope.cleanState = function() {
    $scope.saving = false;
    $scope.selectedChildren = [];
    if ($scope.editForm) {
      $scope.editForm.$setPristine();
      $scope.editForm.$dirty = false;
    }
    $rootScope.clearChanges();
    if ($scope.segmentsParams)
      $scope.segmentsParams.refresh();
  };
  $scope.callSegDelta = function() {

    $rootScope.$emit("event:openSegDelta");
  };


  $scope.save = function() {
    $scope.saving = true;
    var segment = $rootScope.segment;
    $rootScope.$emit("event:saveSegForDelta");
    var ext = segment.ext;
    if (segment.libIds === undefined) segment.libIds = [];
    if (segment.libIds.indexOf($rootScope.igdocument.profile.segmentLibrary.id) == -1) {
      segment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
    }
    SegmentService.save($rootScope.segment).then(function(result) {
      $rootScope.segment.dateUpdated = result.dateUpdated;
      $rootScope.$emit("event:updateIgDate");
      SegmentService.updateDynamicMappingInfo();
      var oldLink = SegmentLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.segmentLibrary.children);
      var newLink = SegmentService.getSegmentLink(result);
      SegmentLibrarySvc.updateChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function(link) {
        SegmentService.saveNewElements().then(function() {
          SegmentService.merge($rootScope.segmentsMap[result.id], result);
          if (oldLink && oldLink !== null) {
            oldLink.ext = newLink.ext;
            oldLink.name = newLink.name;
          }
          $scope.cleanState();
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


    }, function(error) {
      $scope.saving = false;
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

    $rootScope.saveBindingForSegment();
  };


  var searchById = function(id) {
    var children = $rootScope.igdocument.profile.segmentLibrary.children;
    for (var i = 0; i < $rootScope.igdocument.profile.segmentLibrary.children; i++) {
      if (children[i].id === id) {
        return children[i];
      }
    }
    return null;
  };

  var indexOf = function(id) {
    var children = $rootScope.igdocument.profile.segmentLibrary.children;
    for (var i = 0; i < children; i++) {
      if (children[i].id === id) {
        return i;
      }
    }
    return -1;

  };


  $scope.showSelectDatatypeFlavorDlg = function(field) {
    var modalInstance = $modal.open({
      templateUrl: 'SelectDatatypeFlavor.html',
      controller: 'SelectDatatypeFlavorCtrl',
      windowClass: 'flavor-modal-window',
      resolve: {
        currentDatatype: function() {
          return $rootScope.datatypesMap[field.datatype.id];
        },

        hl7Version: function() {
          return $rootScope.igdocument.profile.metaData.hl7Version;
        },

        datatypeLibrary: function() {
          return $rootScope.igdocument.profile.datatypeLibrary;
        }
      }
    });
    modalInstance.result.then(function(datatype) {
      //                MastermapSvc.deleteElementChildren(field.datatype.id, "datatype", field.id, field.type);
      field.datatype.id = datatype.id;

      field.datatype.name = datatype.name;
      field.datatype.ext = datatype.ext;
      $rootScope.processElement(field);
      $scope.setDirty();
      //                MastermapSvc.addDatatypeId(datatype.id, [field.id, field.type]);
      if ($scope.segmentsParams)
        $scope.segmentsParams.refresh();
    });

  };

  $scope.showEditDynamicMappingDlg = function(node) {
    var modalInstance = $modal.open({
      templateUrl: 'DynamicMappingCtrl.html',
      controller: 'DynamicMappingCtrl',
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
      $scope.setDirty();
      $scope.segmentsParams.refresh();
    }, function() {});
  };

  $scope.editModalBindingForSeg = function(node) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'TableMappingSegmentCtrl.html',
      scope: $scope,        // use parent scope in template
      preserveScope: true,
      controller: 'TableMappingSegmentCtrl',
      locals: {
        currentNode:node
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

  $scope.editUserData = function (definition, obj, disabled) {
      var modalInstance = $mdDialog.show({
          templateUrl: 'EditUserData.html',
          controller: 'EditUserDataCtrl',
          scope: $scope,        // use parent scope in template
          preserveScope: true,
          locals: {
              definition:  definition,
              text:  obj.text,
              disabled:  disabled
          }
      });
      modalInstance.then(function(textData) {
          obj.text = textData;
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

  $scope.openDialogForEditValueSetThenMapData = function(id, index) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditThenData.html',
      controller: 'EditThenDataCtrl',
      scope:$rootScope,
      preserveScope:true,
      locals: {
        currentId:  id,
        currentIndex:  index
      }

    });

    modalInstance.then(function(value) {
      if(value&&value!=='cancel'){
        $rootScope.segment.coConstraintsTable.thenMapData[id][index] = value;
        $scope.setDirty();
      }
    });
  };

  $scope.openDialogForEditSev = function(node) {
    var modalInstance = $mdDialog.show({
      templateUrl: 'EditSingleElement.html',
      controller: 'EditSingleElementCtrl',
      locals: {
        currentNode:
        node

      }
    });

    modalInstance.then(function(value) {
      $scope.addSev(node);
      node.sev.value = value;
      $scope.setDirty();
    });
  };

  $scope.isAvailableForValueSet = function (node){
    if(node && node.datatype){
      var currentDT = $rootScope.datatypesMap[node.datatype.id];
      if(currentDT && _.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT){
          return valueSetAllowedDT == currentDT.name;
        })) return true;
    }

    if(node && node.fieldDT && !node.componentDT){
      var parentDT = $rootScope.datatypesMap[node.fieldDT];
      var pathSplit = node.path.split(".");
      if(parentDT && _.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent){
          return valueSetAllowedComponent.dtName == parentDT.name && valueSetAllowedComponent.location == pathSplit[1];
        })) return true;
    }

    if(node && node.componentDT){
      var parentDT = $rootScope.datatypesMap[node.componentDT];
      var pathSplit = node.path.split(".");
      if(parentDT && _.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent){
          return valueSetAllowedComponent.dtName == parentDT.name && valueSetAllowedComponent.location == pathSplit[2];
        })) return true;
    }

    return false;
  };

  $scope.findingComments = function(node) {
    var result = [];
    if(node && $rootScope.segment){
      result = _.filter($rootScope.segment.comments, function(comment){ return comment.location == node.path; });
      for (var i = 0; i < result.length; i++) {
        result[i].from = 'segment';
        result[i].index = i + 1;
      }


      if(node.fieldDT) {
        var parentDT = $rootScope.datatypesMap[node.fieldDT];
        var subPath = node.path.substr(node.path.indexOf('.') + 1);
        var subResult = _.filter(parentDT.comments, function(comment){ return comment.location == subPath; });
        for (var i = 0; i < subResult.length; i++) {
          subResult[i].from = 'field';
          subResult[i].index = i + 1;
        }

        result = result.concat(subResult);
      }


      if(node.componentDT) {
        var parentDT = $rootScope.datatypesMap[node.componentDT];
        var subPath = node.path.substr(node.path.split('.', 2).join('.').length + 1);
        var subSubResult = _.filter(parentDT.comments, function(comment){ return comment.location == subPath; });
        for (var i = 0; i < subSubResult.length; i++) {
          subSubResult[i].from = 'component';
          subSubResult[i].index = i + 1;
        }

        result = result.concat(subSubResult);
      }
    }
    return result;
  };

  $scope.findingBindings = function(node) {
    var result = [];
    if(node && $rootScope.segment){
      result = _.filter($rootScope.segment.valueSetBindings, function(binding){ return binding.location == node.path; });
      for (var i = 0; i < result.length; i++) {
        result[i].bindingFrom = 'segment';
      }

      if(result && result.length > 0) {
        return result;
      }

      if(node.fieldDT) {
        var parentDT = $rootScope.datatypesMap[node.fieldDT];
        var subPath = node.path.substr(node.path.indexOf('.') + 1);
        result = _.filter(parentDT.valueSetBindings, function(binding){ return binding.location == subPath; });
        for (var i = 0; i < result.length; i++) {
          result[i].bindingFrom = 'field';
        }
      }

      if(result && result.length > 0) {
        return result;
      }

      if(node.componentDT) {
        var parentDT = $rootScope.datatypesMap[node.componentDT];
        var subPath = node.path.substr(node.path.split('.', 2).join('.').length + 1);
        result = _.filter(parentDT.valueSetBindings, function(binding){ return binding.location == subPath; });
        for (var i = 0; i < result.length; i++) {
          result[i].bindingFrom = 'component';
        }
      }
    }


    return result;
  };

  $scope.deleteValueSetBinding = function(binding){
    var index = $rootScope.segment.valueSetBindings.indexOf(binding);
    if (index >= 0) {
      $rootScope.segment.valueSetBindings.splice(index, 1);
      $scope.setDirty();
    }
  };

  $scope.deleteComment = function(comment){
    var index = $rootScope.segment.comments.indexOf(comment);
    if (index >= 0) {
      $rootScope.segment.comments.splice(index, 1);
      $scope.setDirty();
    }
  };

  $scope.addSev = function (node){
    var sev = {};
    sev.location = node.path;
    sev.value = '';
    sev.profilePath = $rootScope.getSegmentLabel($rootScope.segment) + "-" + node.path;
    sev.name = node.name;

      var diff= _.filter($rootScope.segment.singleElementValues, function (r) {
          return sev.profilePath!==r.profilePath;
      });
      diff.push(sev);
      $rootScope.segment.singleElementValues=diff;
    node.sev = sev;
    node.sev.from = 'segment';
    $scope.setDirty();
  };

  $scope.deleteSev = function (node){
    var index = $rootScope.segment.singleElementValues.indexOf(node.sev);
    if (index >= 0) {
      $rootScope.segment.singleElementValues.splice(index, 1);
      $scope.setDirty();
    }

    if(node.componentDT) {
      var componentPath = node.path.substr(node.path.split('.', 2).join('.').length + 1);
      var foundSev = _.find($rootScope.datatypesMap[node.componentDT].singleElementValues, function (sev) { return sev.location == componentPath;});
      if (foundSev) {
        foundSev.from = 'component';
        node.sev = foundSev;
      }
    }

    if(node.fieldDT) {
      var fieldPath = node.path.substr(node.path.indexOf('.') + 1);
      var foundSev = _.find($rootScope.datatypesMap[node.fieldDT].singleElementValues, function(sev){ return sev.location  ==  fieldPath; });
      if(foundSev) {
        foundSev.from = 'field';
        node.sev = foundSev;
      }
    }

    if(node.sev && node.sev.from == 'segment'){
      node.sev = null;
    }
  };

  $scope.editDMSecondReferenceDlg = function() {
    var modalInstance = $modal.open({
      templateUrl: 'EditDMSecondReference.html',
      controller: 'EditDMSecondReferenceCtrl',
      backdrop: true,
      keyboard: true,
      windowClass: 'input-text-modal-window',
      backdropClick: false,
      resolve: {
        currentMappingStructure: function() {
          return $rootScope.segment.dynamicMappingDefinition.mappingStructure;
        }
      }
    });

    modalInstance.result.then(function(currentMappingStructure) {
      $rootScope.segment.dynamicMappingDefinition.mappingStructure = currentMappingStructure;
      $scope.setDirty();
    });
  };

  $scope.deleteMappingItem = function (item){
    var index = $rootScope.segment.dynamicMappingDefinition.dynamicMappingItems.indexOf(item);
    if (index >= 0) {
      $rootScope.segment.dynamicMappingDefinition.dynamicMappingItems.splice(index, 1);
      $scope.setDirty();
    }
  };

  $scope.addMappingItem = function () {
    var newItem = {};
    newItem.firstReferenceValue = null;
    newItem.secondReferenceValue = null;
    newItem.datatypeId = null;
    $rootScope.segment.dynamicMappingDefinition.dynamicMappingItems.push(newItem);
    $scope.setDirty();
  };

  $scope.getDefaultStatus = function (code) {
    var item = _.find($rootScope.segment.dynamicMappingDefinition.dynamicMappingItems, function(item) {
      return item.firstReferenceValue == code.value;
    });

    if(!item) return 'full';
    if(item){
      if(item.secondReferenceValue && item.secondReferenceValue != '') return 'partial';
      return 'empty';
    }
  };
});
