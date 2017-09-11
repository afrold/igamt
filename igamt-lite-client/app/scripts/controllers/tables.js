/**
 * Created by Jungyub on 4/01/15.
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
        //$rootScope.findValueSetBindings();
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
        $rootScope.codeSystems.splice($rootScope.codeSystems.indexOf(value), 1);
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
            codeSystem: null,
            codeUsage: 'R'
        };

        $rootScope.table.smallCodes.unshift(newValue);
        $rootScope.table.codes.unshift(newValue);
        var newValueBlock = { targetType: 'table', targetId: $rootScope.table.id, obj: newValue };
        if (!$scope.isNewObject('table', 'add', $rootScope.table.id)) {
            $rootScope.recordChangeForEdit2('value', "add", null, 'value', newValueBlock);
        }
        $scope.setDirty();
    };

    $scope.isValidTable = function () {
        var valueCodeSystemList = [];
        var labelCodeSystemList = [];

        for (var i = 0; i < $rootScope.table.smallCodes.length; i++) {
            var value = $rootScope.table.smallCodes[i].value;
            var label = $rootScope.table.smallCodes[i].label;
            var codeSystem = $rootScope.table.smallCodes[i].codeSystem;

            if(!value || value === '') return false;
            if(!label || label === '') return false;
            if(!codeSystem || codeSystem === '') return false;

            var valueCodeSystem = value + codeSystem;
            var labelCodeSystem = label + codeSystem;


            if ($.inArray(valueCodeSystem,valueCodeSystemList) === -1) {
                valueCodeSystemList.push(valueCodeSystem);
            }else {
                return false;
            }

            if ($.inArray(labelCodeSystem,labelCodeSystemList) === -1) {
                labelCodeSystemList.push(labelCodeSystem);
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
        } else if (c.selected === false) {
            var index = $scope.selectedCodes.indexOf(c);
            if (index > -1) {
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
        $rootScope.table.smallCodes = _.difference($rootScope.table.smallCodes, $scope.selectedCodes);
        $scope.selectedCodes = [];
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
                $mdDialog.hide();

            }
            $scope.confirm= function () {
                $rootScope.table.referenceUrl=$scope.url;
                $rootScope.table.sourceType="EXTERNAL";
                $rootScope.table.codes=[];
                $rootScope.table.smallCodes=[];
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

angular.module('igl').controller('TableModalCtrl', function($scope) {
    $scope.showModal = false;
    $scope.toggleModal = function() {
        $scope.showModal = !$scope.showModal;
    };
});

angular.module('igl').controller('ConfirmValueSetDeleteCtrl', function($scope, $mdDialog, tableToDelete, $rootScope, TableService, TableLibrarySvc, CloneDeleteSvc) {
    $scope.tableToDelete = tableToDelete;
    $scope.loading = false;


    $scope.delete = function() {
        $scope.loading = true;
        if ($scope.tableToDelete.scope === 'USER') {
            CloneDeleteSvc.deleteTableAndTableLink($scope.tableToDelete);
        } else {
            CloneDeleteSvc.deleteTableLink($scope.tableToDelete);
        }
        $mdDialog.hide($scope.tableToDelete);
        $scope.loading = false;
    };

    $scope.cancel = function() {
        $mdDialog.hide();
    };


    $scope.isNewTableThenDelete = function(id) {
        if ($rootScope.isNewObject('table', 'add', id)) {
            if ($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['add'] !== undefined) {
                for (var i = 0; i < $rootScope.changes['table']['add'].length; i++) {
                    var tmp = $rootScope.changes['table']['add'][i];
                    if (tmp.id == id) {
                        $rootScope.changes['table']['add'].splice(i, 1);
                        if ($rootScope.changes["table"]["add"] && $rootScope.changes["table"]["add"].length === 0) {
                            delete $rootScope.changes["table"]["add"];
                        }

                        if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
                            delete $rootScope.changes["table"];
                        }
                        return true;
                    }
                }
            }
            return true;
        }
        if ($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['edit'] !== undefined) {
            for (var i = 0; i < $rootScope.changes['table']['edit'].length; i++) {
                var tmp = $rootScope.changes['table']['edit'][i];
                if (tmp.id === id) {
                    $rootScope.changes['table']['edit'].splice(i, 1);
                    if ($rootScope.changes["table"]["edit"] && $rootScope.changes["table"]["edit"].length === 0) {
                        delete $rootScope.changes["table"]["edit"];
                    }

                    if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
                        delete $rootScope.changes["table"];
                    }
                    return false;
                }
            }
            return false;
        }
        return false;
    };
});

angular.module('igl').controller('ValueSetReferencesCtrl', function($scope, $mdDialog, tableToDelete) {

    $scope.tableToDelete = tableToDelete;

    $scope.ok = function() {
        $mdDialog.hide($scope.tableToDelete);
    };

    $scope.cancel = function() {
        $mdDialog.hide();
    };
});


angular.module('igl').controller('cmpTableCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService, TableService) {
    var ctrl = this;
    this.tableId = -1;
    $scope.vsChanged = false;
    $scope.variable = false;
    $scope.isDeltaCalled = false;

    $scope.setDeltaToF = function() {
        console.log("HEEEEEERREEEEE");
        $scope.isDeltaCalled = false;
    }



    $scope.scopes = [{
        name: "USER",
        alias: "My IG"
    }, {
        name: "HL7STANDARD",
        alias: "Base HL7"
    }];
    var listHL7Versions = function() {
        return $http.get('api/igdocuments/findVersions', {
            timeout: 60000
        }).then(function(response) {
            var hl7Versions = [];
            var length = response.data.length;
            for (var i = 0; i < length; i++) {
                hl7Versions.push(response.data[i]);
            }
            return hl7Versions;
        });
    };
    $scope.status = {
        isCustomHeaderOpen: false,
        isFirstOpen: true,
        isSecondOpen: true,
        isFirstDisabled: false
    };

    $scope.initt = function() {
        $scope.isDeltaCalled = true;
        $scope.dataList = [];
        listHL7Versions().then(function(versions) {
            $scope.versions = versions;
            $scope.version1 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);
            $scope.scope1 = "USER";
            $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);
            $scope.table1 = angular.copy($rootScope.table);
            this.tableId = -1;
            $scope.variable = !$scope.variable;
            $scope.tables = null;
            //$scope.setIG2($scope.ig2);
            $scope.version2 = angular.copy($scope.version1);
            //$scope.status.isFirstOpen = true;
            $scope.scope2 = "HL7STANDARD";
            if ($scope.dynamicVs_params) {
                $scope.showDelta = false;
                $scope.status.isFirstOpen = true;
                $scope.dynamicVs_params.refresh();
            }
        });



    };

    $scope.$on('event:loginConfirmed', function(event) {
        $scope.initt();
    });

    //$scope.initt();

    $rootScope.$on('event:initTable', function(event) {
        if ($scope.isDeltaCalled) {
            $scope.initt();
        }
    });

    $rootScope.$on('event:openVSDelta', function(event) {
        $scope.initt();
    });



    $scope.setVersion2 = function(vr) {
        $scope.version2 = vr;

    };
    $scope.setScope2 = function(scope) {

        $scope.scope2 = scope;
    };

    $scope.$watchGroup(['table1', 'table2'], function() {
        $scope.vsChanged = true;
        //$scope.segment1 = angular.copy($rootScope.activeSegment);


    }, true);
    $scope.$watchGroup(['version2', 'scope2'], function() {
        $scope.igList2 = [];
        $scope.tables2 = [];
        $scope.ig2 = "";
        if ($scope.scope2 && $scope.version2) {
            IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
                if (result) {
                    if ($scope.scope2 === "HL7STANDARD") {
                        console.log("====");
                        $scope.ig2 = {
                            id: result[0].id,
                            title: result[0].metaData.title
                        };
                        console.log($scope.ig2);
                        
                        $scope.igList2.push($scope.ig2);

                        $scope.setIG2($scope.ig2);
                        $scope.igDisabled2 = true;
                    } else {
                        for (var i = 0; i < result.length; i++) {
                            $scope.igList2.push({
                                id: result[i].id,
                                title: result[i].metaData.title,
                            });
                        }
                        $scope.igDisabled2 = false;


                    }
                }
            });

        }

    }, true);
    $scope.setTable2 = function(table) {
        if (table === -1) {
            $scope.table2 = {};
        } else {
            $scope.table2 = $scope.tables2[table];

        }
    };
    $scope.setIG2 = function(ig) {
        if (ig) {
            IgDocumentService.getOne(ig.id).then(function(igDoc) {
                SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
                    DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
                        TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
                            $scope.tables2 = [];
                            this.tableId = -1;

                            if (igDoc) {
                                //$scope.segList2 = angular.copy(segments);
                                //$scope.segList2 = orderByFilter($scope.segList2, 'name');
                                //$scope.dtList2 = angular.copy(datatypes);
                                $scope.tableList2 = angular.copy(tables);
                                //$scope.messages2 = orderByFilter(igDoc.profile.messages.children, 'name');
                                //$scope.segments2 = orderByFilter(segments, 'name');
                                //$scope.datatypes2 = orderByFilter(datatypes, 'name');
                                $scope.tables2 = orderByFilter(tables, 'bindingIdentifier');
                            }
                        });
                    });
                });

            });

            //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

        }

    };

    $scope.hideVS = function(vs1, vs2) {

        if (vs2) {
            return !(vs1.name === vs2.name);
        } else {
            return false;
        }
    };
    $scope.disableVS = function(vs1, vs2) {

        if (vs2) {
            return (vs1.id === vs2.id);
        } else {
            return false;
        }
    };




    $scope.dynamicVs_params = new ngTreetableParams({
        getNodes: function(parent) {
            if ($scope.dataList !== undefined) {
                if (parent) {
                    if (parent.codes) {
                        return parent.codes;
                    }

                } else {
                    return $scope.dataList;
                }

            }
        },
        getTemplate: function(node) {
            $scope.vsTemplate = true;
            return 'valueSet_node';
        }
    });
    $scope.cmpTable = function(table1, table2) {

        $scope.loadingSelection = true;
        $scope.vsChanged = false;
        $scope.vsTemplate = false;
         TableService.getOne(table2.id).then(function(vs2) {
            $scope.dataList = CompareService.cmpValueSet(JSON.stringify(table1), JSON.stringify(vs2));

            $scope.loadingSelection = false;
            if ($scope.dynamicVs_params) {
                console.log($scope.dataList);
                $scope.showDelta = true;
                $scope.status.isSecondOpen = true;
                $scope.dynamicVs_params.refresh();
            }
            $scope.deltaTabStatus.active = 1;
        });


    };


});

angular.module('igl').controller('AddBindingForValueSet', function($scope, $modalInstance, $rootScope, table) {
    console.log($rootScope.references);
    $scope.table = table;
    $scope.componentsToSelect=[];
    $scope.selectedSegmentForBinding = null;
    $scope.selectedFieldForBinding = null;
    $scope.selectedDatatypeForBinding = null;
    $scope.selectedComponentForBinding = null;
    $scope.selectedBindingLocation = null;
    $scope.selectedBindingStrength = null;
    $scope.pathForBinding = null;
    $scope.bindingTargetType ='DATATYPE';


    $scope.init = function() {
        // $scope.selectedSegmentForBinding = null;
        // $scope.selectedFieldForBinding = null;
        // $scope.selectedDatatypeForBinding = null;
        // $scope.selectedComponentForBinding = null;
        // $scope.selectedBindingLocation = null;
        // $scope.selectedBindingStrength = null;
        // $scope.pathForBinding = null;
    };
    $scope.switchTo=function(type){
        $scope.bindingTargetType=type;
    }
    $scope.checkDuplicated = function(path) {
        for (var i = 0; i < $rootScope.references.length; i++) {
            var ref = $rootScope.references[i];
            if (ref.path == path) return true;
        }
        return false;
    };

    $scope.selectSegment = function() {
        $scope.selectedFieldForBinding = null;
    };

    $scope.selectDatatype = function(dt) {
        $scope.selectedDatatypeForBinding = JSON.parse(dt);
        console.log($scope.selectedDatatypeForBinding);
        $scope.componentsToSelect=$scope.selectedDatatypeForBinding.components

              //$scope.selectedComponentForBinding = null;
    };
    $scope.selectComponent=function(c){
        $scope.selectedComponentForBinding=c;
        
    };

    $scope.save = function(bindingTargetType) {
        var tableLink = {};
        tableLink.id = $scope.table.id;
        tableLink.bindingIdentifier = $scope.table.bindingIdentifier;
        tableLink.bindingLocation = $scope.selectedBindingLocation;
        tableLink.bindingStrength = $scope.selectedBindingStrength;
        tableLink.isChanged = true;
        tableLink.isNew = true;

        if (bindingTargetType == 'SEGMENT') {
            $scope.selectedFieldForBinding = JSON.parse($scope.selectedFieldForBinding);
            $scope.pathForBinding = $rootScope.getSegmentLabel($scope.selectedSegmentForBinding) + '-' + $scope.selectedFieldForBinding.position;

            var ref = angular.copy($scope.selectedFieldForBinding);
            ref.path = $scope.pathForBinding;
            ref.target = angular.copy($scope.selectedSegmentForBinding);
            ref.tableLink = angular.copy(tableLink);
            $rootScope.references.push(ref);
        } else {
            $scope.selectedComponentForBinding = JSON.parse($scope.selectedComponentForBinding);
            $scope.pathForBinding = $rootScope.getDatatypeLabel($scope.selectedDatatypeForBinding) + '-' + $scope.selectedComponentForBinding.position;

            var ref = angular.copy($scope.selectedComponentForBinding);
            ref.path = $scope.pathForBinding;
            ref.target = angular.copy($scope.selectedDatatypeForBinding);
            ref.tableLink = angular.copy(tableLink);
            $rootScope.references.push(ref);
        }

        $modalInstance.close();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ShareTableCtrl', function($scope, $modalInstance, $http, igdocumentSelected, userList, TableService, $rootScope) {

    $scope.igdocumentSelected = igdocumentSelected;

    // Add participants username and fullname
    // Find share participants
    if ($scope.igdocumentSelected.shareParticipantIds && $scope.igdocumentSelected.shareParticipantIds.length > 0) {
        $scope.igdocumentSelected.shareParticipantIds.forEach(function(participant) {
            $http.get('api/shareparticipant', { params: { id: participant.accountId } })
                .then(
                    function(response) {
                        participant.username = response.data.username;
                        participant.fullname = response.data.fullname;
                    },
                    function(error) {
                        console.log(error);
                    }
                );
        });
    }
    $scope.userList = userList;
    $scope.error = "";
    $scope.tags = [];
    $scope.ok = function() {
        var idsTab = $scope.tags.map(function(user) {
            return user.accountId;
        });

        TableService.share($scope.igdocumentSelected.id, idsTab, $rootScope.accountId).then(function(result) {
            // Add participants for direct view
            $scope.igdocumentSelected.shareParticipantIds = $scope.igdocumentSelected.shareParticipantIds || [];
            $scope.tags.forEach(function(tag) {
                tag.permission = $scope.selectedItem.selected;
                tag.pendingApproval = true;
                $scope.igdocumentSelected.shareParticipantIds.push(tag);
            });
            $rootScope.msg().text = "vsSharedSuccessfully";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            $modalInstance.close();
        }, function(error) {
            $scope.error = error.data;
            console.log(error);
        });
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.selectedItem = {
        selected: "VIEW"
    };
    $scope.itemArray = ["VIEW"];

    $scope.loadUsernames = function($query) {
        return userList.filter(function(user) {
            return user.username.toLowerCase().indexOf($query.toLowerCase()) != -1;
        });
    };

    $scope.unshare = function(shareParticipant) {
        $scope.loading = false;
        TableService.unshare($scope.igdocumentSelected.id, shareParticipant.accountId).then(function(res) {
            var indexOfId = $scope.igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.accountId);
            if (indexOfId > -1) {
                $scope.igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
            }
            var participantIndex = -1;
            for (var i = 0; i < $scope.igdocumentSelected.shareParticipantIds.length; i++) {
                if ($scope.igdocumentSelected.shareParticipantIds[i].accountId === shareParticipant.accountId) {
                    participantIndex = i;
                    $scope.userList.push($scope.igdocumentSelected.shareParticipantIds[i]);
                    break;
                }
            }
            if (participantIndex > -1) {
                $scope.igdocumentSelected.shareParticipantIds.splice(participantIndex, 1);
            }
            $scope.loading = false;
            $rootScope.msg().text = "vsUnSharedSuccessfully";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
            $scope.loading = false;
        });
    };


});