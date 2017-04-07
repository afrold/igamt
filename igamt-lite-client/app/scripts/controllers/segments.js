/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl').controller('SegmentListCtrl', function($scope, $rootScope, Restangular, ngTreetableParams, CloneDeleteSvc, $filter, $http, $modal, $timeout, $q, SegmentService, FieldService, FilteringSvc, MastermapSvc, SegmentLibrarySvc, DatatypeLibrarySvc, MessageService, DatatypeService, TableService, blockUI, ValidationService) {
    //        $scope.loading = false;
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

    $scope.tabStatus = {
        active: 1
    };

    $scope.init = function() {

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
        var modalInstance = $modal.open({
            templateUrl: 'OtoX.html',
            controller: 'OtoXCtrl',
            size: 'md',
            resolve: {
                message: function() {
                    return message;
                }


            }
        });
        modalInstance.result.then(function() {
            $scope.setDirty();

            if ($scope.segmentsParams)
                $scope.segmentsParams.refresh();
        });
    };

    $scope.cloneCoConstraintRow = function (rowIndex){
        if($rootScope.segment.coConstraintsTable.ifColumnDefinition){
            if($rootScope.segment.coConstraintsTable.ifColumnData){
                $rootScope.segment.coConstraintsTable.ifColumnData.push(angular.copy($rootScope.segment.coConstraintsTable.ifColumnData[rowIndex]));
            }
        }

        if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList && $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length > 0){
            if($rootScope.segment.coConstraintsTable.thenMapData){
                for(var i in $rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
                    if($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id]){
                        $rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id].push(angular.copy($rootScope.segment.coConstraintsTable.thenMapData[$rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i].id][rowIndex]));
                    }
                }
            }
        }

        if($rootScope.segment.coConstraintsTable.userColumnDefinitionList && $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length > 0){
            if($rootScope.segment.coConstraintsTable.userMapData){
                for(var i in $rootScope.segment.coConstraintsTable.userColumnDefinitionList){
                    if($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id]){
                        $rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id].push(angular.copy($rootScope.segment.coConstraintsTable.userMapData[$rootScope.segment.coConstraintsTable.userColumnDefinitionList[i].id][rowIndex]));
                    }
                }
            }
        }

        $rootScope.segment.coConstraintsTable.rowSize = $rootScope.segment.coConstraintsTable.rowSize + 1;

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

        $scope.setDirty();
    };


    $scope.delCoConstraintIFDefinition = function (ifColumnDefinition){
        $rootScope.segment.coConstraintsTable.ifColumnDefinition = null;
        $rootScope.segment.coConstraintsTable.ifColumnData = [];

        $scope.resetCoConstraintsTable();
        $scope.setDirty();
    };

    $scope.delCoConstraintTHENDefinition = function (columnDefinition) {
        var index = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.indexOf(columnDefinition);

        if (index > -1) {
            $rootScope.segment.coConstraintsTable.thenMapData[columnDefinition.id] = null;
            $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.splice(index, 1);
        };

        $scope.resetCoConstraintsTable();
        $scope.setDirty();
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
    };

    $scope.openDialogForEditCoConstraintIFDefinition = function(coConstraintIFDefinition){
        var modalInstance = $modal.open({
            templateUrl: 'CoConstraintIFDefinition.html',
            controller: 'CoConstraintIFDefinitionCtrl',
            size: 'md',
            resolve: {
                coConstraintIFDefinition: function() {
                    return coConstraintIFDefinition;
                }
            }
        });
        modalInstance.result.then(function(ifColumnDefinition) {
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
        var modalInstance = $modal.open({
            templateUrl: 'CoConstraintTHENDefinition.html',
            controller: 'CoConstraintTHENDefinitionCtrl',
            size: 'md',
            resolve: {
                coConstraintTHENDefinition: function() {
                    return coConstraintTHENDefinition;
                }
            }
        });
        modalInstance.result.then(function(thenColumnDefinition) {
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
            $scope.setDirty();
        });
    };

    $scope.openDialogForEditCoConstraintUSERDefinition = function (coConstraintUSERDefinition){
        var modalInstance = $modal.open({
            templateUrl: 'CoConstraintUSERDefinition.html',
            controller: 'CoConstraintUSERDefinitionCtrl',
            size: 'md',
            resolve: {
                coConstraintUSERDefinition: function() {
                    return coConstraintUSERDefinition;
                }
            }
        });
        modalInstance.result.then(function(userColumnDefinition) {
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
        var modalInstance = $modal.open({
            templateUrl: 'DeleteField.html',
            controller: 'DeleteFieldCtrl',
            size: 'md',
            resolve: {
                fieldToDelete: function() {
                    return fieldToDelete;
                },
                segment: function() {
                    return segment;
                }


            }
        });
        modalInstance.result.then(function() {
            $scope.setDirty();

            if ($scope.segmentsParams)
                $scope.segmentsParams.refresh();
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
        var modalInstance = $modal.open({
            templateUrl: 'otherDTModal.html',
            controller: 'otherDTCtrl',
            windowClass: 'edit-VS-modal',
            resolve: {

                datatypes: function() {
                    return $rootScope.datatypes;
                },

                field: function() {
                    return field;
                }

            }
        });
        modalInstance.result.then(function(field) {
            $scope.setDirty();
            $scope.editableDT = '';
            if ($scope.segmentsParams) {
                $scope.segmentsParams.refresh();
            }
        });

    };

    $scope.editDT = function(field) {
        $scope.editableDT = field.id;

        $scope.results = [];
        angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
            if (dtLink.name && dtLink.name === field.datatype.name) {
                $scope.results.push(dtLink);
            }
        });
    };

    $scope.backDT = function() {
        $scope.editableDT = '';
        if ($scope.segmentsParams) {
            $scope.segmentsParams.refresh();
        }
    };


    $scope.redirectDT = function(datatype) {
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
        var modalInstance = $modal.open({
            templateUrl: 'AddFieldModal.html',
            controller: 'AddFieldCtrl',
            windowClass: 'creation-modal-window',
            resolve: {

                valueSets: function() {
                    return $rootScope.tables;
                },
                datatypes: function() {
                    return $rootScope.datatypes;
                },
                segment: function() {
                    return segment;
                },
                messageTree: function() {
                    return $rootScope.messageTree;
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

    $scope.findOptions = function(dtId) {
        var result = [];
        result.push('1');


        if(!dtId) return result;

        if(_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT){
            return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
        })){
            var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

            var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

            if(bls && bls.length > 0) return bls;
        }

        return result;
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

            $rootScope.segment.coConstraintsTable.ifColumnData.push(newIFData);
            isAdded = true;
        }

        if($rootScope.segment.coConstraintsTable.thenColumnDefinitionList){
            for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList.length; i < len1; i++) {
                var thenColumnDefinition = $rootScope.segment.coConstraintsTable.thenColumnDefinitionList[i];

                var newTHENData = {};
                newTHENData.valueData = {};
                newTHENData.valueSets = [];

                if(!$rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id]) $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

                $rootScope.segment.coConstraintsTable.thenMapData[thenColumnDefinition.id].push(newTHENData);
                isAdded = true;
            };
        }

        if($rootScope.segment.coConstraintsTable.userColumnDefinitionList){
            for (var i = 0, len1 = $rootScope.segment.coConstraintsTable.userColumnDefinitionList.length; i < len1; i++) {
                var userColumnDefinition = $rootScope.segment.coConstraintsTable.userColumnDefinitionList[i];

                var newUSERData = {};
                newUSERData.text = "";

                if(!$rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id]) $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

                $rootScope.segment.coConstraintsTable.userMapData[userColumnDefinition.id].push(newUSERData);
                isAdded = true;
            };
        }

        if(isAdded) {
            $rootScope.segment.coConstraintsTable.rowSize = $rootScope.segment.coConstraintsTable.rowSize + 1;
            $scope.setDirty();
        }
    };

    $scope.deleteCoConstraints = function() {
        $rootScope.segment.coConstraintsTable = {};
        $rootScope.segment.coConstraintsTable.rowSize = 0;
        $scope.setDirty();
    };

    $scope.headerChanged = function() {
    }

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
        blockUI.stop();
    };

    $scope.close = function() {
        $rootScope.segment = null;
        $scope.refreshTree();
        $scope.loadingSelection = false;
    };

    $scope.copy = function(segment) {
        CloneDeleteSvc.copySegment(segment);
    }

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
        var modalInstance = $modal.open({
            templateUrl: 'PredicateSegmentCtrl.html',
            controller: 'PredicateSegmentCtrl',
            windowClass: 'app-modal-window',
            resolve: {
                selectedNode: function() {
                    return node;
                }
            }
        });
        modalInstance.result.then(function(segment) {
            if (segment) {
                $rootScope.segment.predicates = segment.predicates;
                $scope.setDirty();
            }
        }, function() {});
    };

    $scope.manageConformanceStatement = function() {
        var modalInstance = $modal.open({
            templateUrl: 'ConformanceStatementSegmentCtrl.html',
            controller: 'ConformanceStatementSegmentCtrl',
            windowClass: 'app-modal-window',
            resolve: {}
        });
        modalInstance.result.then(function(segment) {
            if (segment) {
                $scope.setDirty();
            }
        }, function() {

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
        var modalInstance = $modal.open({
            templateUrl: 'DeleteSegmentPredicate.html',
            controller: 'DeleteSegmentPredicateCtrl',
            size: 'md',
            resolve: {
                position: function() {
                    return position;
                },
                segment: function() {
                    return segment;
                }
            }
        });
        modalInstance.result.then(function() {
            $scope.setDirty();
        });
    };

    $scope.countPredicateOnComponent = function(position, componentId) {
        var dt = $scope.findDTByComponentId(componentId);
        if (dt != null)
            for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
                if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                    return 1;
            }

        return 0;
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
            if ($rootScope.selectedSegment !== null && $rootScope.segment.id === $rootScope.selectedSegment.id) {
                $rootScope.processSegmentsTree($rootScope.segment, null);
            }

            $rootScope.updateDynamicMappingInfo();

            var oldLink = SegmentLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.segmentLibrary.children);
            var newLink = SegmentService.getSegmentLink(result);
            SegmentLibrarySvc.updateChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function(link) {
                SegmentService.saveNewElements().then(function() {
                    SegmentService.merge($rootScope.segmentsMap[result.id], result);
                    if (oldLink && oldLink != null) {
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
        var modalInstance = $modal.open({
            templateUrl: 'TableMappingSegmentCtrl.html',
            controller: 'TableMappingSegmentCtrl',
            windowClass: 'app-modal-window',
            resolve: {
                currentNode: function() {
                    return node;
                }
            }
        });

        modalInstance.result.then(function(node) {
            $scope.setDirty();
        });
    };

    $scope.editCommentDlg = function(node, comment, disabled, type) {
        var modalInstance = $modal.open({
            templateUrl: 'EditComment.html',
            controller: 'EditCommentCtrl',
            backdrop: true,
            keyboard: true,
            windowClass: 'input-text-modal-window',
            backdropClick: false,
            resolve: {
                currentNode: function() {
                    return node;
                },
                currentComment: function() {
                    return comment;
                },
                disabled: function() {
                    return disabled;
                },
                type: function() {
                    return type;
                }
            }
        });

        modalInstance.result.then(function() {
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
        var modalInstance = $modal.open({
            templateUrl: 'EditThenData.html',
            controller: 'EditThenDataCtrl',
            backdrop: true,
            keyboard: true,
            windowClass: 'app-modal-window',
            backdropClick: false,
            resolve: {
                currentId: function() {
                    return id;
                },
                currentIndex: function() {
                    return index;
                }

            }
        });

        modalInstance.result.then(function(value) {
            if(value){
                $rootScope.segment.coConstraintsTable.thenMapData[id][index] = value;
                $scope.setDirty();
            }
        });
    };

    $scope.openDialogForEditSev = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'EditSingleElement.html',
            controller: 'EditSingleElementCtrl',
            backdrop: true,
            keyboard: true,
            windowClass: 'input-text-modal-window',
            backdropClick: false,
            resolve: {
                currentNode: function() {
                    return node;
                }
            }
        });

        modalInstance.result.then(function(value) {
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

    $scope.deleteBinding = function(binding){
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
        $rootScope.segment.singleElementValues.push(sev);
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
            console.log($rootScope.segment.dynamicMappingDefinition);
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

angular.module('igl').controller('CoConstraintUSERDefinitionCtrl', function($scope, $modalInstance, coConstraintUSERDefinition, $rootScope) {
    $scope.coConstraintUSERDefinition = angular.copy(coConstraintUSERDefinition);
    $scope.title = null;

    if($scope.coConstraintUSERDefinition) {
        $scope.title = $scope.coConstraintUSERDefinition.title;
    }

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        var userColumnDefinition = {};
        userColumnDefinition.title = $scope.title;

        $modalInstance.close(userColumnDefinition);
    };
});

angular.module('igl').controller('CoConstraintTHENDefinitionCtrl', function($scope, $modalInstance, coConstraintTHENDefinition, $rootScope, TableService) {
    $scope.selectedCoConstraintTHENDefinition = angular.copy(coConstraintTHENDefinition);

    $scope.coConstraintType = 'value';
    $scope.selectedFieldPosition = null;
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    $scope.targetNode = null;


    if($scope.selectedCoConstraintTHENDefinition){
        $scope.primitive = $scope.selectedCoConstraintTHENDefinition.primitive;
        $scope.coConstraintType = $scope.selectedCoConstraintTHENDefinition.constraintType;
        $scope.isDMReference = $scope.selectedCoConstraintTHENDefinition.dMReference;
        var splitLocation = $scope.selectedCoConstraintTHENDefinition.path.split('.');
        if(splitLocation.length > 0){
            $scope.selectedFieldPosition = splitLocation[0];

            var field = _.find($rootScope.segment.fields, function(f) {
                return f.position == splitLocation[0];
            });

            $scope.targetNode = field;

            if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
                $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

                if(splitLocation.length > 1 && $scope.components){
                    $scope.selectedComponentPosition = splitLocation[1];
                    var component =  _.find($scope.components, function(c) {
                        return c.position == splitLocation[1];
                    });
                    $scope.targetNode = component;
                    if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
                        $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

                        if(splitLocation.length > 2 && $scope.subComponents){
                            $scope.selectedSubComponentPosition = splitLocation[2];
                            var subComponent =  _.find($scope.subComponents, function(sc) {
                                return sc.position == splitLocation[2];
                            });
                            $scope.targetNode = subComponent;
                        }
                    }
                }
            }
        }
    };

    $scope.isVariesDT = function (){
        if($scope.targetNode) {
            if($rootScope.datatypesMap[$scope.targetNode.datatype.id].name.toLowerCase() == 'varies') {
                $scope.coConstraintType = 'valueset';
                return true;
            }
        }

        return false;
    };

    $scope.updateField = function(){
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;
        $scope.primitive = true;
        $scope.dMReference = false;

        var field = _.find($rootScope.segment.fields, function(f) {
            return f.position == $scope.selectedFieldPosition;
        });

        $scope.targetNode = field;

        if($rootScope.isDynamicMappingSegment){
            console.log("=========This is DM segment!!=========");
            var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
                return item.hl7Version == $rootScope.segment.hl7Version && item.segmentName == $rootScope.segment.name;
            });

            if(mappingStructure){
                if($rootScope.segment.dynamicMappingDefinition && $rootScope.segment.dynamicMappingDefinition.mappingStructure){
                    console.log("=========Found mapping structure!!=========");
                    mappingStructure = $rootScope.segment.dynamicMappingDefinition.mappingStructure;
                }else{
                    console.log("=========Not Found mapping structure and Default setting will be used!!=========");
                }

                var valueSetBinding = _.find($rootScope.segment.valueSetBindings, function(vsb) {
                    return vsb.location == mappingStructure.referenceLocation;
                });

                if(valueSetBinding) {
                    TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
                        $rootScope.dynamicMappingTable = tbl;
                    }, function() {

                    });
                }

                if($scope.selectedFieldPosition == mappingStructure.referenceLocation){
                    $scope.dMReference = true;
                }
            }
        }

        if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
            $scope.primitive = false;
            $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
        }
    };

    $scope.updateComponent = function(){
        $scope.selectedSubComponentPosition = null;
        $scope.subComponents = null;
        $scope.primitive = true;
        $scope.dMReference = false;

        var component =  _.find($scope.components, function(c) {
            return c.position == $scope.selectedComponentPosition;
        });

        $scope.targetNode = component;
        if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
            $scope.primitive = false;
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }
    };

    $scope.updateSubComponent = function(){
        $scope.primitive = true;
        $scope.dMReference = false;
        var subComponent =  _.find($scope.subComponents, function(sc) {
            return sc.position == $scope.selectedSubComponentPosition;
        });
        $scope.targetNode = subComponent;
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        var thenColumnDefinition = {};
        if($scope.selectedCoConstraintTHENDefinition){
            thenColumnDefinition.id = $scope.selectedCoConstraintTHENDefinition.id;
        }

        thenColumnDefinition.constraintType = $scope.coConstraintType;
        thenColumnDefinition.name = $scope.targetNode.name;
        thenColumnDefinition.usage = $scope.targetNode.usage;
        thenColumnDefinition.dtId = $scope.targetNode.datatype.id;
        thenColumnDefinition.primitive = $scope.primitive;
        thenColumnDefinition.dMReference = $scope.dMReference;

        if(thenColumnDefinition.dMReference) {
            thenColumnDefinition.constraintType = 'dmr';
        }

        if($scope.selectedFieldPosition){
            thenColumnDefinition.path = "" + $scope.selectedFieldPosition;
            thenColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
            thenColumnDefinition.type = "field";
            if($scope.selectedComponentPosition){
                thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedComponentPosition;
                thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
                thenColumnDefinition.type = "component";
                if($scope.selectedSubComponentPosition){
                    thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
                    thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
                    thenColumnDefinition.type = "subcomponent";
                }
            }
        }

        $modalInstance.close(thenColumnDefinition);
    };

});


angular.module('igl').controller('CoConstraintIFDefinitionCtrl', function($scope, $modalInstance, coConstraintIFDefinition, $rootScope) {
    $scope.selectedCoConstraintIFDefinition = angular.copy(coConstraintIFDefinition);

    $scope.coConstraintType = 'value';
    $scope.selectedFieldPosition = null;
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;

    $scope.targetNode = null;


    if($scope.selectedCoConstraintIFDefinition){
        $scope.primitive = $scope.selectedCoConstraintIFDefinition.primitive;
        $scope.coConstraintType = $scope.selectedCoConstraintIFDefinition.constraintType;
        var splitLocation = $scope.selectedCoConstraintIFDefinition.path.split('.');
        if(splitLocation.length > 0){
            $scope.selectedFieldPosition = splitLocation[0];

            var field = _.find($rootScope.segment.fields, function(f) {
                return f.position == splitLocation[0];
            });

            $scope.targetNode = field;

            if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
                $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

                if(splitLocation.length > 1 && $scope.components){
                    $scope.selectedComponentPosition = splitLocation[1];
                    var component =  _.find($scope.components, function(c) {
                        return c.position == splitLocation[1];
                    });
                    $scope.targetNode = component;
                    if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
                        $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

                        if(splitLocation.length > 2 && $scope.subComponents){
                            $scope.selectedSubComponentPosition = splitLocation[2];
                            var subComponent =  _.find($scope.subComponents, function(sc) {
                                return sc.position == splitLocation[2];
                            });
                            $scope.targetNode = subComponent;
                        }
                    }
                }
            }
        }
    };



    $scope.updateField = function(){
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;
        $scope.primitive = true;

        var field = _.find($rootScope.segment.fields, function(f) {
            return f.position == $scope.selectedFieldPosition;
        });

        $scope.targetNode = field;

        if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
            $scope.primitive = false;
            $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
        }
    };

    $scope.updateComponent = function(){
        $scope.selectedSubComponentPosition = null;
        $scope.subComponents = null;
        $scope.primitive = true;

        var component =  _.find($scope.components, function(c) {
            return c.position == $scope.selectedComponentPosition;
        });

        $scope.targetNode = component;
        if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
            $scope.primitive = false;
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }
    };

    $scope.updateSubComponent = function(){
        $scope.primitive = true;
        var subComponent =  _.find($scope.subComponents, function(sc) {
            return sc.position == $scope.selectedSubComponentPosition;
        });
        $scope.targetNode = subComponent;
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        var ifColumnDefinition = {};
        ifColumnDefinition.id = new ObjectId().toString();
        ifColumnDefinition.constraintType = $scope.coConstraintType;
        ifColumnDefinition.name = $scope.targetNode.name;
        ifColumnDefinition.usage = $scope.targetNode.usage;
        ifColumnDefinition.dtId = $scope.targetNode.datatype.id;
        ifColumnDefinition.primitive = $scope.primitive;

        if($scope.selectedFieldPosition){
            ifColumnDefinition.path = "" + $scope.selectedFieldPosition;
            ifColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
            ifColumnDefinition.type = "field";
            if($scope.selectedComponentPosition){
                ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedComponentPosition;
                ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
                ifColumnDefinition.type = "component";
                if($scope.selectedSubComponentPosition){
                    ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
                    ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
                    ifColumnDefinition.type = "subcomponent";
                }
            }
        }

        $modalInstance.close(ifColumnDefinition);
    };

});

angular.module('igl').controller('SegmentRowCtrl', function($scope, $filter) {
    $scope.formName = "form_" + new Date().getTime();
});
angular.module('igl').controller('EditDMSecondReferenceCtrl', function($scope, $modalInstance, currentMappingStructure, $rootScope) {
    $scope.currentMappingStructure = angular.copy(currentMappingStructure);
    $scope.selectedFieldPosition = null;
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;

    if($scope.currentMappingStructure.secondRefereceLocation){
        var splitLocation = $scope.currentMappingStructure.secondRefereceLocation.split('.');
        if(splitLocation.length > 0){
            $scope.selectedFieldPosition = splitLocation[0];

            var field = _.find($rootScope.segment.fields, function(f) {
                return f.position == splitLocation[0];
            });

            if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
                $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
            }

            if(splitLocation.length > 1 && $scope.components){
                $scope.selectedComponentPosition = splitLocation[1];
                var component =  _.find($scope.components, function(c) {
                    return c.position == splitLocation[1];
                });
                if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
                    $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
                }

                if(splitLocation.length > 2 && $scope.subComponents){
                    $scope.selectedSubComponentPosition = splitLocation[2];
                }

            }
        }
    };

    $scope.updateField = function(){
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;

        var field = _.find($rootScope.segment.fields, function(f) {
            return f.position == $scope.selectedFieldPosition;
        });

        if(field && $rootScope.datatypesMap[field.datatype.id].components.length > 0){
            $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
        }
    };

    $scope.updateComponent = function(){
        $scope.selectedSubComponentPosition = null;
        $scope.subComponents = null;

        var component =  _.find($scope.components, function(c) {
            return c.position == $scope.selectedComponentPosition;
        });
        if(component && $rootScope.datatypesMap[component.datatype.id].components.length > 0){
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        var path = null;
        if($scope.selectedFieldPosition){
            var path = $scope.selectedFieldPosition;
            if($scope.selectedComponentPosition){
                path = path + "." + $scope.selectedComponentPosition;
                if($scope.selectedSubComponentPosition){
                    path = path + "." + $scope.selectedSubComponentPosition;
                }
            }
        }

        if(path){
            $scope.currentMappingStructure.secondRefereceLocation = path;
        }
        $modalInstance.close($scope.currentMappingStructure);
    };
});

angular.module('igl').controller('DynamicMappingCtrl', function($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.changed = false;
    $scope.selectedNode = selectedNode;
    $scope.selectedMapping = angular.copy(_.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
        return mapping.position == $scope.selectedNode.position;
    }));
    if (!$scope.selectedMapping) {
        $scope.selectedMapping = {};
        $scope.selectedMapping.cases = [];
        $scope.selectedMapping.position = $scope.selectedNode.position;
    }

    $scope.deleteCase = function(c) {
        var index = $scope.selectedMapping.cases.indexOf(c);
        $scope.selectedMapping.cases.splice(index, 1);
        $scope.recordChange();
    };

    $scope.addCase = function() {
        var newCase = {
            id: new ObjectId().toString(),
            type: 'case',
            value: '',
            datatype: null
        };

        $scope.selectedMapping.cases.unshift(newCase);
        $scope.recordChange();
    };

    $scope.recordChange = function() {
        $scope.changed = true;
        // $scope.editForm.$dirty = true;
    };


    $scope.updateMapping = function() {
        var oldMapping = _.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
            return mapping.position == $scope.selectedNode.position;
        });
        var index = $rootScope.segment.dynamicMapping.mappings.indexOf(oldMapping);
        $rootScope.segment.dynamicMapping.mappings.splice(index, 1);
        $rootScope.segment.dynamicMapping.mappings.unshift($scope.selectedMapping);
        $scope.changed = false;
        $scope.ok();
    };

    $scope.ok = function() {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('PredicateSegmentCtrl', function($scope, $modalInstance, selectedNode, $rootScope, $q) {
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
    $scope.selectedSegment = angular.copy($rootScope.segment);
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

        if (current.type == 'segment') {
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

    $scope.generatePathInfo($scope.treeDataForContext[0], ".", ".", "1", false);

    $scope.toggleChildren = function(data) {
        data.childrenVisible = !data.childrenVisible;
        data.folderClass = data.childrenVisible ? "fa-minus" : "fa-plus";
    };

    $scope.beforeNodeDrop = function() {
        var deferred = $q.defer();
        deferred.resolve();
        return deferred.promise;
    };

    $scope.afterNodeDrop = function() {
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

    $scope.afterPredicateDrop = function() {
        $scope.draggingStatus = null;
        $scope.existingPredicate = $scope.predicateData;
        $scope.existingPredicate.constraintTarget = $scope.selectedNode.position + '[1]';
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

                    if (i == $scope.newConstraint.pathInfoSet_1.length - 1) {
                        locationPath = locationPath + " (" + pathInfo.nodeName + ")";
                    }
                }
            }

            $scope.newConstraint.position_1 = positionPath.substr(1);
            $scope.newConstraint.location_1 = $rootScope.segment.name + '-' + locationPath.substr(1);
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
            $scope.newConstraint.location_2 = $rootScope.segment.name + '-' + locationPath.substr(1);
        }
    };

    $scope.findExistingPredicate = function() {
        for (var i = 0, len1 = $scope.selectedSegment.predicates.length; i < len1; i++) {
            if ($scope.selectedSegment.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
                return $scope.selectedSegment.predicates[i];
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

    $scope.ok = function() {
        $modalInstance.close();
    };

    $scope.saveclose = function() {
        $scope.deletePredicateByTarget();
        $scope.selectedSegment.predicates.push($scope.existingPredicate);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedSegment);
    };

    $scope.initPredicate();
    $scope.initComplexPredicate();
    $scope.existingPredicate = $scope.findExistingPredicate();

});

angular.module('igl').controller('ConformanceStatementSegmentCtrl', function($scope, $modalInstance, $rootScope, $q) {
    $scope.constraintType = 'Plain';
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segment.name + "_" + $rootScope.segment.ext);
    $scope.newComplexConstraint = [];
    $scope.firstNodeData = null;
    $scope.secondNodeData = null;
    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    angular.copy($rootScope.segment.conformanceStatements, $scope.tempComformanceStatements);

    $scope.treeDataForContext = [];
    $scope.treeDataForContext.push(angular.copy($rootScope.segment));
    $scope.treeDataForContext[0].pathInfoSet = [];
    $scope.generatePathInfo = function(current, positionNumber, locationName, instanceNumber, isInstanceNumberEditable, nodeName) {
        var pathInfo = {};
        pathInfo.positionNumber = positionNumber;
        pathInfo.locationName = locationName;
        pathInfo.nodeName = nodeName;
        pathInfo.instanceNumber = instanceNumber;
        pathInfo.isInstanceNumberEditable = isInstanceNumberEditable;
        current.pathInfoSet.push(pathInfo);

        if (current.type == 'segment') {
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

    $scope.generatePathInfo($scope.treeDataForContext[0], ".", ".", "1", false);


    $scope.setChanged = function() {
        $scope.changed = true;
    };

    $scope.toggleChildren = function(data) {
        data.childrenVisible = !data.childrenVisible;
        data.folderClass = data.childrenVisible ? "fa-minus" : "fa-plus";
    };

    $scope.beforeNodeDrop = function() {
        var deferred = $q.defer();
        deferred.resolve();
        return deferred.promise;
    };

    $scope.afterNodeDrop = function() {
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
            constraintId: $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segment.name + "_" + $rootScope.segment.ext),
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
        $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segment.name + "_" + $rootScope.segment.ext);
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

                    if (i == $scope.newConstraint.pathInfoSet_1.length - 1) {
                        locationPath = locationPath + " (" + pathInfo.nodeName + ")";
                    }
                }
            }

            $scope.newConstraint.position_1 = positionPath.substr(1);
            $scope.newConstraint.location_1 = $rootScope.segment.name + '-' + locationPath.substr(1);
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
            $scope.newConstraint.location_2 = $rootScope.segment.name + '-' + locationPath.substr(1);
        }
    };

    $scope.deleteConformanceStatement = function(conformanceStatement) {
        $scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
    };

    $scope.addComplexConformanceStatement = function() {
        $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
        $scope.tempComformanceStatements.push($scope.complexConstraint);
        $scope.initComplexStatement();
        $scope.changed = true;
    };

    $scope.addFreeTextConformanceStatement = function() {
        var cs = $rootScope.generateFreeTextConformanceStatement($scope.newConstraint);
        $scope.tempComformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.addConformanceStatement = function() {
        var cs = $rootScope.generateConformanceStatement($scope.newConstraint);
        $scope.tempComformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.ok = function() {
        $modalInstance.close();
    };

    $scope.saveclose = function() {
        angular.copy($scope.tempComformanceStatements, $rootScope.segment.conformanceStatements);
        $rootScope.recordChanged($rootScope.segment);
        $modalInstance.close();
    };
});
angular.module('igl').controller('ConfirmSegmentDeleteCtrl', function($scope, $rootScope, $modalInstance, segToDelete, $rootScope, SegmentService, SegmentLibrarySvc, MastermapSvc, CloneDeleteSvc) {
    $scope.segToDelete = segToDelete;
    $scope.loading = false;

    $scope.delete = function() {
        $scope.loading = true;
        if ($scope.segToDelete.scope === 'USER') {
            CloneDeleteSvc.deleteSegmentAndSegmentLink($scope.segToDelete);
        } else {
            CloneDeleteSvc.deleteSegmentLink($scope.segToDelete);
        }
        $modalInstance.close($scope.segToDelete);
        $scope.loading = false;
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});
angular.module('igl').controller('SegmentReferencesCtrl', function($scope, $modalInstance, segToDelete) {

    $scope.segToDelete = segToDelete;

    $scope.ok = function() {
        $modalInstance.close($scope.segToDelete);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});
angular.module('igl').controller('AddFieldCtrl', function($scope, $modalInstance, datatypes, segment, valueSets, $rootScope, $http, ngTreetableParams, SegmentService, DatatypeLibrarySvc, MessageService, blockUI) {;
    $scope.valueSets = valueSets;
    $scope.datatypes = datatypes;

    $scope.newField = {
        comment: "",
        confLength: "",
        datatype: {
            ext: null,
            id: "",
            label: "",
            name: ""
        },
        hide: false,
        added: "yes",
        id: "",
        itemNo: "",
        max: "",
        maxLength: "",
        min: "",
        minLength: "",
        name: "",
        position: "",
        table: {
            bindingIdentifier: "",
            bindingLocation: null,
            bindingStrength: null,
            id: ""
        },
        text: "",
        type: "field",
        usage: ""


    };

    $scope.$watch('DT', function() {
        if ($scope.DT) {
            $scope.newField.datatype.ext = $scope.DT.ext;
            $scope.newField.datatype.id = $scope.DT.id;
            $scope.newField.datatype.name = $scope.DT.name;
        }
    }, true);
    $scope.loadVS = function($query) {


        return valueSets.filter(function(table) {
            return table.bindingIdentifier.toLowerCase().indexOf($query.toLowerCase()) != -1;
        });

    };
    $scope.tableList = [];
    $scope.tagAdded = function(tag) {
        $scope.vsChanged = true;
        $scope.tableList.push({
            id: tag.id,
            bindingIdentifier: tag.bindingIdentifier,
            bindingLocation: null,
            bindingStrength: null
        });


        //$scope.log.push('Added: ' + tag.text);
    };

    $scope.tagRemoved = function(tag) {
        $scope.vsChanged = true;

        for (var i = 0; i < $scope.tableList.length; i++) {
            if ($scope.tableList[i].id === tag.id) {
                $scope.tableList.splice(i, 1);
            }
        };


    };

    // $scope.$watch('VS', function() {
    //     if ($scope.VS) {
    //         $scope.newField.table.bindingIdentifier = $scope.VS.bindingIdentifier;
    //         $scope.newField.table.id = $scope.VS.id;


    //     }
    // }, true);

    $scope.selectUsage = function(usage) {
        if (usage === 'X' || usage === 'W') {
            $scope.newField.max = 0;
            $scope.newField.min = 0;
            $scope.disableMin = true;
            $scope.disableMax = true;

        } else if (usage === 'R') {
            $scope.newField.min = 1;

            $scope.disableMin = true;
            $scope.disableMax = false;
        } else if (usage === 'RE' || usage === 'O') {
            $scope.newField.min = 0;

            $scope.disableMin = true;
            $scope.disableMax = false;

        } else {
            $scope.disableMin = false;
            $scope.disableMax = false;
        }
    };



    $scope.selectDT = function(datatype) {
        $scope.DT = datatype;
        //$scope.newSeg = segment;
    };
    $scope.selectedDT = function() {
        return ($scope.DT !== undefined);
        //return ($scope.newSeg !== undefined);
    };
    $scope.unselectDT = function() {
        $scope.DT = undefined;
        //$scope.newSeg = undefined;
    };
    $scope.isDTActive = function(id) {
        if ($scope.DT) {
            return $scope.DT.id === id;
        } else {
            return false;
        }

    };


    $scope.selectVS = function(valueSet) {
        $scope.VS = valueSet;
        //$scope.newSeg = segment;
    };
    $scope.selectedVS = function() {
        return ($scope.VS !== undefined);
        //return ($scope.newSeg !== undefined);
    };
    $scope.unselectVS = function() {
        $scope.VS = undefined;
        //$scope.newSeg = undefined;
    };
    $scope.isVSActive = function(id) {
        if ($scope.VS) {
            return $scope.VS.id === id;
        } else {
            return false;
        }

    };


    $scope.addField = function() {
        blockUI.start();
        if ($rootScope.segment.fields.length !== 0) {
            $scope.newField.position = $rootScope.segment.fields[$rootScope.segment.fields.length - 1].position + 1;

        } else {
            $scope.newField.position = 1
        }
        $scope.newField.id = new ObjectId().toString();
        $scope.newField.tables = $scope.tableList;

        if ($rootScope.segment != null) {
            if (!$rootScope.segment.fields || $rootScope.segment.fields === null)
                $rootScope.segment.fields = [];
            $rootScope.segment.fields.push($scope.newField);
            MessageService.updatePosition(segment.fields, $scope.newField.position - 1, $scope.position - 1);



            if ($scope.segmentsParams) {
                $scope.segmentsParams.refresh();
            }

        }
        blockUI.stop();
        $modalInstance.close();

    };


    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };


});
angular.module('igl').controller('DeleteFieldCtrl', function($scope, $modalInstance, fieldToDelete, segment, $rootScope, SegmentService, blockUI) {
    $scope.fieldToDelete = fieldToDelete;
    $scope.loading = false;
    $scope.updatePosition = function(node) {
        angular.forEach(node.fields, function(field) {
            field.position = node.fields.indexOf(field) + 1;

        })

    };
    $scope.delete = function() {
        blockUI.start();
        $scope.loading = true;
        segment.fields.splice(fieldToDelete.position - 1, 1);


        $rootScope.msg().text = "FieldDeleteSuccess";

        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
        $scope.loading = false;
        $scope.updatePosition(segment);
        blockUI.stop();
        $modalInstance.close($scope.fieldToDelete);
    };


    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };


});
angular.module('igl').controller('EditVSCtrl', function($scope, $modalInstance, valueSets, field, $rootScope, SegmentService, blockUI) {
    $scope.vsChanged = false;
    $scope.field = field;
    if (field.attributes) {
        if (field.attributes.tables && field.attributes.tables.length > 0) {
            $scope.vs = angular.copy(field.attributes.tables);
            $scope.tableList = angular.copy(field.attributes.tables);
        } else {
            $scope.vs = [];
            $scope.tableList = [];
        }

    } else {
        $scope.vs = angular.copy(field.tables);
        $scope.tableList = angular.copy(field.tables);
    }

    $scope.loadVS = function($query) {


        return valueSets.filter(function(table) {
            return table.bindingIdentifier.toLowerCase().indexOf($query.toLowerCase()) != -1;
        });

    };
    $scope.tagAdded = function(tag) {
        $scope.vsChanged = true;
        $scope.tableList.push({
            id: tag.id,
            bindingIdentifier: tag.bindingIdentifier,
            bindingLocation: null,
            bindingStrength: null
        });


        //$scope.log.push('Added: ' + tag.text);
    };

    $scope.tagRemoved = function(tag) {
        $scope.vsChanged = true;

        for (var i = 0; i < $scope.tableList.length; i++) {
            if ($scope.tableList[i].id === tag.id) {
                $scope.tableList.splice(i, 1);
            }
        };


    };

    $scope.addVS = function() {
        blockUI.start();

        $scope.vsChanged = false;
        if (field.attributes) {
            field.attributes.tables = $scope.tableList;
        } else {
            field.tables = $scope.tableList;
        }



        blockUI.stop();

        $modalInstance.close();


    };


    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };


});
angular.module('igl').controller('otherDTCtrl', function($scope, $modalInstance, datatypes, field, $rootScope, SegmentService, blockUI) {

    $scope.dtChanged = false;
    $scope.field = field;
    var oldDt = angular.copy(field.datatype);
    $scope.datatypes = datatypes;
    //$scope.vs = angular.copy(field.tables);
    //$scope.tableList = angular.copy(field.tables);;

    $scope.isInDts = function(datatype) {

        if (datatype && $scope.datatypes.indexOf(datatype) === -1) {
            return false;
        } else {
            return true;
        }

    };
    $scope.getDtLabel = function(element) {
        if (element) {
            if (element.ext !== null) {
                return element.name + "_" + element.ext;
            } else {
                return element.name;
            }
        }
        return "";
    };
    $scope.addDT = function() {
        blockUI.start();

        $scope.dtChanged = false;

        field.datatype = {
            id: $scope.newDt.id,
            name: $scope.newDt.name,
            ext: $scope.newDt.ext,
            label:$scope.newDt.label
        };

        blockUI.stop();

        $modalInstance.close(field);


    };


    $scope.cancel = function() {
        field.datatype = oldDt;
        $modalInstance.close();
    };


});
angular.module('igl').controller('cmpSegmentCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService) {
    $scope.segChanged = false;
    $scope.isDeltaCalled = false;
    var ctrl = this;
    this.segmentId = -1;
    $scope.setDeltaToF = function() {
        $scope.isDeltaCalled = false;
    }


    $scope.scopes = [{
        name: "USER",
        alias: "My IG"
    }, {
        name: "HL7STANDARD",
        alias: "Base HL7"
    }];
    $scope.getLabel = function(element) {
        if (element) {
            if (element.ext !== null && element.ext !== "") {
                return element.name + "_" + element.ext;
            } else {
                return element.name;
            }
        }
        return "";
    };
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
        isSecondOpen: false,
        isFirstDisabled: false
    };
    $scope.variable = false;

    $scope.initt = function() {
        $scope.isDeltaCalled = true;
        $scope.dataList = [];
        listHL7Versions().then(function(versions) {
            $scope.versions = versions;
            $scope.segment1 = angular.copy($rootScope.segment);
            $scope.version1 = angular.copy($scope.segment1.hl7Version);
            ctrl.segmentId = -1;
            //$scope.setIG2($scope.ig2);
            $scope.variable = !$scope.variable;
            $scope.segList1 = angular.copy($rootScope.segments);
            $scope.dtList1 = angular.copy($rootScope.datatypes);
            $scope.version2 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);
            //$scope.status.isFirstOpen = true;
            $scope.scope2 = "HL7STANDARD";
            if ($scope.dynamicSeg_params) {
                $scope.showDelta = true;
                $scope.status.isFirstOpen = true;
                $scope.dynamicSeg_params.refresh();
            }

        });


    };

    $scope.$on('event:loginConfirmed', function(event) {
        $scope.initt();
    });

    //$scope.initt();

    $rootScope.$on('event:initSegment', function(event) {
        if ($scope.isDeltaCalled) {
            $scope.initt();
        }
        // $scope.initt();
    });

    $rootScope.$on('event:openSegDelta', function(event) {
        $scope.initt();
    });


    // $rootScope.$on('event:saveSegForDelta', function(event) {
    //     $scope.dataList = [];
    //     $scope.initt();
    // });

    $scope.scope1 = "USER";
    $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);

    $scope.setVersion2 = function(vr) {
        $scope.version2 = vr;

    };
    $scope.setScope2 = function(scope) {

        $scope.scope2 = scope;
    };

    $scope.$watchGroup(['segment1', 'segment2'], function() {
        $scope.segChanged = true;
        //$scope.segment1 = angular.copy($rootScope.activeSegment);


    }, true);
    $scope.$watchGroup(['version2', 'scope2', 'variable'], function() {
        $scope.igList2 = [];
        $scope.segments2 = [];
        $scope.ig2 = "";
        if ($scope.scope2 && $scope.version2) {
            IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
                if (result) {
                    if ($scope.scope2 === "HL7STANDARD") {
                        $scope.igDisabled2 = true;
                        $scope.ig2 = {
                            id: result[0].id,
                            title: result[0].metaData.title
                        };
                        $scope.igList2.push($scope.ig2);

                        $scope.setIG2($scope.ig2);
                    } else {
                        $scope.igDisabled2 = false;
                        for (var i = 0; i < result.length; i++) {
                            $scope.igList2.push({
                                id: result[i].id,
                                title: result[i].metaData.title,
                            });
                        }
                    }
                }
            });

        }

    }, true);
    $scope.setSegment2 = function(segment) {

        if (segment === -1) {
            $scope.segment2 = {};
        } else {
            $scope.segment2 = $scope.segments2[segment];
        }
        //$scope.segment2 = segment;
    };
    $scope.setIG2 = function(ig) {
        if (ig) {
            IgDocumentService.getOne(ig.id).then(function(igDoc) {
                SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
                    DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
                        TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
                            $scope.segments2 = [];
                            if (igDoc) {
                                $scope.segList2 = angular.copy(segments);
                                //$scope.segList2 = orderByFilter($scope.segList2, 'name');
                                $scope.dtList2 = angular.copy(datatypes);
                                $scope.tableList2 = angular.copy(tables);
                                //$scope.messages2 = orderByFilter(igDoc.profile.messages.children, 'name');
                                $scope.segments2 = orderByFilter(segments, 'name');

                            }
                        });
                    });
                });

            });

            //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

        }

    };

    $scope.hideSeg = function(seg1, seg2) {

        if (seg2) {
            return !(seg1.name === seg2.name);
        } else {
            return false;
        }
    };
    $scope.disableSeg = function(seg1, seg2) {

        if (seg2) {
            return (seg1.id === seg2.id);
        } else {
            return false;
        }
    };




    $scope.dynamicSeg_params = new ngTreetableParams({
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
    $scope.cmpSegment = function(segment1, segment2) {

        $scope.loadingSelection = true;
        $scope.segChanged = false;
        $scope.vsTemplate = false;
        $scope.dataList = CompareService.cmpSegment(JSON.stringify(segment1), JSON.stringify(segment2), $scope.dtList1, $scope.dtList2, $scope.segList1, $scope.segList2);

        $scope.loadingSelection = false;
        if ($scope.dynamicSeg_params) {
            $scope.showDelta = true;
            $scope.status.isSecondOpen = true;
            $scope.dynamicSeg_params.refresh();
        }

    };


});
angular.module('igl').controller('DeleteSegmentPredicateCtrl', function($scope, $modalInstance, position, segment, $rootScope) {
    $scope.selectedSegment = segment;
    $scope.position = position;
    $scope.delete = function() {
        for (var i = 0, len1 = $scope.selectedSegment.predicates.length; i < len1; i++) {
            if ($scope.selectedSegment.predicates[i].constraintTarget.indexOf($scope.position + '[') === 0) {
                $scope.selectedSegment.predicates.splice($scope.selectedSegment.predicates.indexOf($scope.selectedSegment.predicates[i]), 1);
                $modalInstance.close();
                return;
            }
        }
        $modalInstance.close();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});
angular.module('igl').controller('AddBindingForSegment', function($scope, $modalInstance, $rootScope, segment) {
    $scope.segment = segment;
    $scope.selectedMessageForBinding = null;
    $scope.selectedSegRefForBinding = null;
    $scope.segRefsList = [];
    $scope.pathForBinding = null;

    $scope.init = function() {
        $scope.selectedMessageForBinding = null;
        $scope.selectedSegRefForBinding = null;
        $scope.segRefsList = [];
        $scope.pathForBinding = null;

    };

    $scope.checkDuplicated = function(positionPath) {
        for (var i = 0; i < $rootScope.references.length; i++) {
            var ref = $rootScope.references[i];
            if (ref.positionPath == positionPath) return true;
        }
        return false;
    };


    $scope.selectMessage = function() {
        $scope.selectedSegRefForBinding = null;
        $scope.segRefsList = [];
        $scope.pathForBinding = null;

        $scope.travelMessage($scope.selectedMessageForBinding.children, $scope.selectedMessageForBinding.name + '-' + $scope.selectedMessageForBinding.identifier, $scope.selectedMessageForBinding.name + '-' + $scope.selectedMessageForBinding.identifier);
    };

    $scope.travelMessage = function(children, positionPath, namePath) {
        angular.forEach(children, function(child) {
            if (child.type === 'group') {
                var groupNames = child.name.split(".");
                var groupName = groupNames[groupNames.length - 1];
                $scope.travelMessage(child.children, positionPath + '.' + child.position, namePath + '.' + groupName);
            } else {
                var s = $rootScope.segmentsMap[child.ref.id];
                if (s.name === $scope.segment.name) {
                    var segRef = {};
                    segRef.obj = child;
                    segRef.path = namePath + '.' + s.name;
                    segRef.positionPath = positionPath + '.' + child.position;
                    $scope.segRefsList.push(segRef);
                }
            }

        });
    }

    $scope.save = function() {
        var segmentLink = {};
        segmentLink.id = $scope.segment.id;
        segmentLink.name = $scope.segment.name;
        segmentLink.ext = $scope.segment.ext;
        segmentLink.isChanged = true;
        segmentLink.isNew = true;

        $scope.selectedSegRefForBinding = JSON.parse($scope.selectedSegRefForBinding);

        var ref = angular.copy($scope.selectedSegRefForBinding.obj);
        ref.path = $scope.selectedSegRefForBinding.path;
        ref.positionPath = $scope.selectedSegRefForBinding.positionPath;
        ref.target = angular.copy($scope.selectedMessageForBinding);
        ref.segmentLink = segmentLink;
        $rootScope.references.push(ref);

        $modalInstance.close();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('TableMappingSegmentCtrl', function($scope, $modalInstance, currentNode, $rootScope, blockUI, TableService) {
    $scope.changed = false;
    $scope.currentNode = currentNode;
    $scope.selectedValueSetBindings = angular.copy(_.filter($rootScope.segment.valueSetBindings, function(binding){ return binding.location == currentNode.path; }));
    $scope.listOfBindingLocations = null;
    $scope.isSingleValueSetAllowed = false;
    $scope.valueSetSelectedForSingleCode = null;
    $scope.mCode = null;
    $scope.mCodeSystem = null;

    $scope.singleCodeInit = function (){
        $scope.valueSetSelectedForSingleCode = null;
        $scope.mCode = null;
        $scope.mCodeSystem = null;
    };

    $scope.addManualCode = function () {
        $scope.selectedValueSetBindings = [];
        var code = {};
        code.value = $scope.mCode;
        code.codeSystem = $scope.mCodeSystem;
        $scope.selectedValueSetBindings.push({ tableId: null, location: currentNode.path, usage: $scope.currentNode.usage, type: "singlecode", code : code});
        $scope.changed = true;
    };

    if(_.find($rootScope.config.singleValueSetDTs, function(singleValueSetDTs){
            return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.datatype.id].name;
        })) $scope.isSingleValueSetAllowed = true;

    if(_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT){
            return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.datatype.id].name;
        })) {
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if (!$scope.selectedValueSetBindings[i].bindingLocation || $scope.selectedValueSetBindings[i].bindingLocation == '') {
                $scope.selectedValueSetBindings[i].bindingLocation = "1";
            }
        }

        var hl7Version = $rootScope.datatypesMap[$scope.currentNode.datatype.id].hl7Version;
        if(!hl7Version) hl7Version = "2.5.1";

        $scope.listOfBindingLocations = $rootScope.config.bindingLocationListByHL7Version[hl7Version];
    };

    $scope.deleteBinding = function(binding){
        var index = $scope.selectedValueSetBindings.indexOf(binding);
        if (index >= 0) {
            $scope.selectedValueSetBindings.splice(index, 1);
        }
        $scope.changed = true;
    };

    $scope.isSelected = function (v){
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if($scope.selectedValueSetBindings[i].tableId == v.id) return true;
        }
        return false;
    };

    $scope.selectValueSet = function (v){
        if($scope.isSingleValueSetAllowed) $scope.selectedValueSetBindings = [];
        if($scope.selectedValueSetBindings.length > 0 && $scope.selectedValueSetBindings[0].type == 'singlecode') $scope.selectedValueSetBindings = [];
        if($scope.listOfBindingLocations){
            $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: currentNode.path, bindingLocation: "1", usage: currentNode.usage, type: "valueset" });
        }else {
            $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: currentNode.path, usage: currentNode.usage, type: "valueset" });
        }
        $scope.changed = true;
    };

    $scope.unselectValueSet = function (v){
        var toBeDelBinding =_.find($scope.selectedValueSetBindings, function(binding){
            return binding.tableId == v.id;
        });
        var index = $scope.selectedValueSetBindings.indexOf(toBeDelBinding);
        if (index >= 0) {
            $scope.selectedValueSetBindings.splice(index, 1);
        }
        $scope.changed = true;
    };

    $scope.selectValueSetForSingleCode = function (v){
        TableService.getOne(v.id).then(function(tbl) {
            $scope.valueSetSelectedForSingleCode = tbl;
        }, function() {
        });
    };

    $scope.isCodeSelected = function (c){
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if($scope.selectedValueSetBindings[i].code){
                if($scope.selectedValueSetBindings[i].code.id == c.id) return true;
            }
        }
        return false;
    };

    $scope.selectCode = function (c){
        $scope.selectedValueSetBindings = [];
        $scope.selectedValueSetBindings.push({ tableId: $scope.valueSetSelectedForSingleCode.id, location: currentNode.path, usage: currentNode.usage, type: "singlecode", code : c});
        $scope.changed = true;
    };

    $scope.unselectCode = function(c){
        $scope.selectedValueSetBindings = [];
        $scope.changed = true;
    };

    $scope.saveMapping = function() {
        blockUI.start();
        var otherValueSetBindings = angular.copy(_.filter($rootScope.segment.valueSetBindings, function(binding){ return binding.location != currentNode.path; }));
        $rootScope.segment.valueSetBindings= $scope.selectedValueSetBindings.concat(otherValueSetBindings);
        blockUI.stop();

        $modalInstance.close();
    };

    $scope.ok = function() {
        $modalInstance.dismiss('cancel');
    };

});