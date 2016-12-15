/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl').controller('SegmentListCtrl', function($scope, $rootScope, Restangular, ngTreetableParams, CloneDeleteSvc, $filter, $http, $modal, $timeout, $q, SegmentService, FieldService, FilteringSvc, MastermapSvc, SegmentLibrarySvc, DatatypeLibrarySvc, MessageService, DatatypeService, TableService, blockUI) {
    //        $scope.loading = false;

    // console.log("IN SEGMENTS========");
    $scope.accordStatus = {
        isCustomHeaderOpen: false,
        isFirstOpen: true,
        isSecondOpen: true,
        isThirdOpen: true,
        isFourthOpen: true,
        isFirstDisabled: false
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
            isFourthOpen: false,
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

    // console.log($rootScope.tables);
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

    $scope.openPredicateDialog = function(node) {
        if (node.usage == 'C') $scope.managePredicate(node);
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
        // console.log(field);
        $scope.editableField = field.id;
        $scope.fieldName = field.name;

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
        // console.log(segmentLink);
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
            // console.log(field);
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
    //     console.log(field);
    //     $scope.setDirty();
    //     $rootScope.processElement(field);
    //     if ($scope.segmentsParams)
    //         $scope.segmentsParams.refresh();
    //     $scope.DTselected = false;
    //     blockUI.stop();

    // };
    $scope.otherDT = function(field) {
        // console.log("heeere");
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
        $scope.loadLibrariesByFlavorName = function() {
            // console.log($rootScope.igdocument);
            var delay = $q.defer();
            $scope.ext = null;
            $scope.results = [];
            $scope.tmpResults = [];
            $scope.results = $scope.results.concat(filterFlavors($rootScope.igdocument.profile.datatypeLibrary, field.datatype.name));
            $scope.results = _.uniq($scope.results, function(item, key, a) {
                return item.id;
            });
            $scope.tmpResults = [].concat($scope.results);
            //            DatatypeLibrarySvc.findLibrariesByFlavorName(field.datatype.name, 'HL7STANDARD', $rootScope.igdocument.profile.metaData.hl7Version).then(function(libraries) {
            //                if (libraries != null) {
            //                    _.each(libraries, function(library) {
            //                        $scope.results = $scope.results.concat(filterFlavors(library, field.datatype.name));
            //                    });
            //                }
            //
            //                $scope.results = _.uniq($scope.results, function(item, key, a) {
            //                    return item.id;
            //                });
            //                $scope.tmpResults = [].concat($scope.results);
            //
            //                delay.resolve(true);
            //            }, function(error) {
            //                $rootScope.msg().text = "Sorry could not load the data types";
            //                $rootScope.msg().type = error.data.type;
            //                $rootScope.msg().show = true;
            //                delay.reject(error);
            //            });
            return delay.promise;
        };


        var filterFlavors = function(library, name) {
            var results = [];
            _.each(library.children, function(link) {
                if (link.name === name) {
                    link.libraryName = library.metaData.name;
                    link.hl7Version = $rootScope.datatypesMap[link.id].hl7Version;
                    //link.hl7Version = library.metaData.hl7Version;
                    results.push(link);
                }
            });
            return results;
        };




        $scope.loadLibrariesByFlavorName().then(function(done) {
            // console.log($scope.results);
            // $scope.selection.selected = $scope.currentDatatype.id;
            // $scope.showSelectedDetails($scope.currentDatatype);
        });
    };
    $scope.backDT = function() {
        $scope.editableDT = '';
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
                $rootScope.editDataType(datatype);
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
        // console.log(field);
        $scope.editableVS = field.id;
        if (field.table !== null) {
            $scope.VSselected = true;
            $scope.selectedValueSet = field.table;
            // console.log($scope.selectedValueSet);

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
    //         console.log(field);

    //     }
    //     console.log(field);


    //     field.table.id = $scope.selectedValueSet.id;
    //     field.table.bindingIdentifier = $scope.selectedValueSet.bindingIdentifier;
    //     $scope.setDirty();
    //     $scope.VSselected = false;

    // };


    $scope.redirectVS = function(valueSet) {
        TableService.getOne(valueSet.id).then(function(valueSet) {
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
        // console.log("valueSet");
        // console.log(valueSet);

        $scope.selectedValueSet = valueSet;
        $scope.VSselected = true;
        $scope.editableVS = '';
        if (field.table === null) {
            field.table = {
                id: '',
                bindingIdentifier: ''

            };
            // console.log(field);

        }
        // console.log(field);


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

    $scope.addCoConstraint = function() {
        var valueList = [];

        for (var i = 0, len1 = $rootScope.segment.coConstraints.columnList.length; i < len1; i++) {
            var v = {};
            v.value = '';
            valueList.push(v);
        };

        var cc = {
            description: "",
            comments: "",
            values: valueList
        };
        $scope.setDirty();
        $rootScope.segment.coConstraints.constraints.push(cc);
    };

    $scope.manageCoConstaintsTableModal = function(segment) {
        var modalInstance = $modal.open({
            templateUrl: 'ManageCoConstraintsTableModal.html',
            controller: 'ManageCoConstraintsTableCtrl',
            windowClass: 'creation-modal-window',
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


    $scope.deleteColumn = function(column) {
        var index = $rootScope.segment.coConstraints.columnList.indexOf(column);

        if (index > -1) {
            var columnPosition = $rootScope.segment.coConstraints.columnList[index].columnPosition;
            $rootScope.segment.coConstraints.columnList.splice(index, 1);

            for (var i = 0, len1 = $rootScope.segment.coConstraints.columnList.length; i < len1; i++) {
                if ($rootScope.segment.coConstraints.columnList[i].columnPosition > columnPosition) {
                    $rootScope.segment.coConstraints.columnList[i].columnPosition = $rootScope.segment.coConstraints.columnList[i].columnPosition - 1;
                }
            }

            for (var i = 0, len1 = $rootScope.segment.coConstraints.constraints.length; i < len1; i++) {
                $rootScope.segment.coConstraints.constraints[i].values.splice(columnPosition, 1);
            };
        }

        $scope.setDirty();
    };

    $scope.deleteCoConstraint = function(cc) {
        var index = $rootScope.segment.coConstraints.constraints.indexOf(cc);

        if (index > -1) {
            $rootScope.segment.coConstraints.constraints.splice(index, 1);
        };
        $scope.setDirty();
    };

    $scope.deleteCoConstraints = function() {
        $rootScope.segment.coConstraints.columnList = [];
        $rootScope.segment.coConstraints.constraints = [];
        $scope.setDirty();
    };

    $scope.getConstraintType = function(data, cc) {
        var index = cc.values.indexOf(data);
        return $rootScope.segment.coConstraints.columnList[index].constraintType;
    };

    $scope.headerChanged = function() {
        // console.log("WWWWWW");
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
                if (node.type === 'case') {
                    if ($rootScope.getDatatype(node.datatype).components && $rootScope.getDatatype(node.datatype).components.length > 0) return true;
                } else {
                    if (node.datatype && $rootScope.getDatatype(node.datatype.id)) {
                        if ($rootScope.getDatatype(node.datatype.id).components && $rootScope.getDatatype(node.datatype.id).components.length > 0) return true;
                        else {
                            if ($rootScope.getDatatype(node.datatype.id).name === 'varies') {
                                var mapping = _.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
                                    return mapping.position == node.position;
                                });
                                if (mapping && mapping.cases && mapping.cases.length > 0) return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
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

    $scope.mapTable = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'TableMappingSegmentCtrl.html',
            controller: 'TableMappingSegmentCtrl',
            windowClass: 'app-modal-window',
            resolve: {
                selectedNode: function() {
                    return node;
                }
            }
        });
        modalInstance.result.then(function(node) {
            $scope.selectedNode = node;
            $scope.setDirty();
        }, function() {});
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
        modalInstance.result.then(function(node) {
            $scope.selectedNode = node;
            $scope.setDirty();
        }, function() {});
    };

    $scope.manageConformanceStatement = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'ConformanceStatementSegmentCtrl.html',
            controller: 'ConformanceStatementSegmentCtrl',
            windowClass: 'app-modal-window',
            resolve: {
                selectedNode: function() {
                    return node;
                }
            }
        });
        modalInstance.result.then(function(node) {
            $scope.selectedNode = node;
            $scope.setDirty();
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

});
angular.module('igl').controller('SegmentRowCtrl', function($scope, $filter) {
    $scope.formName = "form_" + new Date().getTime();
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
angular.module('igl').controller('TableMappingSegmentCtrl', function($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.changed = false;
    $scope.selectedNode = selectedNode;
    $scope.selectedTable = null;
    if (selectedNode.table != undefined) {
        $scope.selectedTable = $rootScope.tablesMap[selectedNode.table.id];
    }

    $scope.selectTable = function(table) {
        $scope.selectedTable = table;
        $scope.changed = true;
    };


    $scope.mappingTable = function() {
        if ($scope.selectedNode.table == null || $scope.selectedNode.table == undefined) $scope.selectedNode.table = {};
        $scope.selectedNode.table.id = $scope.selectedTable.id;
        $scope.selectedNode.table.bindingIdentifier = $scope.selectedTable.bindingIdentifier;
        $rootScope.recordChangeForEdit2('field', 'edit', $scope.selectedNode.id, 'table', $scope.selectedNode.table.id);
        $scope.ok();
    };

    $scope.ok = function() {
        $modalInstance.close($scope.selectedNode);
    };

});
angular.module('igl').controller('ManageCoConstraintsTableCtrl', function($scope, $modalInstance, segment, $rootScope) {
    $scope.newColumnField = '';
    $scope.newColumnConstraintType = '';
    $scope.selectedSegment = angular.copy(segment);
    $scope.addColumnCoConstraints = function() {
        var newColumn = {
            field: JSON.parse($scope.newColumnField),
            constraintType: $scope.newColumnConstraintType,
            columnPosition: $scope.selectedSegment.coConstraints.columnList.length
        };
        for (var i = 0, len1 = $scope.selectedSegment.coConstraints.constraints.length; i < len1; i++) {
            var v = {};
            v.value = '';
            $scope.selectedSegment.coConstraints.constraints[i].values.push(v);
        };
        $scope.selectedSegment.coConstraints.columnList.push(newColumn);

        $scope.newColumnField = '';
        $scope.newColumnConstraintType = '';
    };

    $scope.deleteColumn = function(column) {
        var index = $scope.selectedSegment.coConstraints.columnList.indexOf(column);

        if (index > -1) {
            var columnPosition = $scope.selectedSegment.coConstraints.columnList[index].columnPosition;
            $scope.selectedSegment.coConstraints.columnList.splice(index, 1);

            for (var i = 0, len1 = $scope.selectedSegment.coConstraints.columnList.length; i < len1; i++) {
                if ($scope.selectedSegment.coConstraints.columnList[i].columnPosition > columnPosition) {
                    $scope.selectedSegment.coConstraints.columnList[i].columnPosition = $rootScope.segment.coConstraints.columnList[i].columnPosition - 1;
                }
            }

            for (var i = 0, len1 = $scope.selectedSegment.coConstraints.constraints.length; i < len1; i++) {
                $scope.selectedSegment.coConstraints.constraints[i].values.splice(columnPosition, 1);
            };
        }

    };

    $scope.checkFieldExisting = function(field) {
        for (var i = 0, len1 = $scope.selectedSegment.coConstraints.columnList.length; i < len1; i++) {
            if ($scope.selectedSegment.coConstraints.columnList[i].field.position == field.position) {
                return true;
            }
        }
        return false;
    };

    $scope.saveAndClose = function() {
        $rootScope.segment.coConstraints = $scope.selectedSegment.coConstraints;
        $modalInstance.close();
    };

    $scope.close = function() {
        $modalInstance.close();
    };

});
angular.module('igl').controller('PredicateSegmentCtrl', function($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;

    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy($rootScope.segment.predicates, $scope.tempPredicates);

    $scope.countPredicateForTemp = function() {
        var count = 0;

        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
                count = count + 1;
        }
        return count;
    };

    $scope.setChanged = function() {
        $scope.changed = true;
    }

    $scope.initPredicate = function() {
        $scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            segment: '',
            field_1: null,
            component_1: null,
            subComponent_1: null,
            field_2: null,
            component_2: null,
            subComponent_2: null,
            freeText: null,
            verb: null,
            ignoreCase: false,
            contraintType: null,
            value: null,
            value2: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });
        $scope.newConstraint.segment = $rootScope.segment.name;
    }

    $scope.initComplexPredicate = function() {
        $scope.constraints = [];
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.complexConstraintTrueUsage = null;
        $scope.complexConstraintFalseUsage = null;
    }

    $scope.initPredicate();

    $scope.deletePredicate = function(predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
        $scope.changed = true;
    };

    $scope.updateField_1 = function() {
        $scope.newConstraint.component_1 = null;
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_1 = function() {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateField_2 = function() {
        $scope.newConstraint.component_2 = null;
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.updateComponent_2 = function() {
        $scope.newConstraint.subComponent_2 = null;
    };


    $scope.deletePredicateByTarget = function() {
        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
                $scope.deletePredicate($scope.tempPredicates[i]);
                return true;
            }
        }
        return false;
    };

    $scope.addComplexPredicate = function() {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;
        if ($scope.selectedNode === null) {
            $scope.complexConstraint.constraintId = '.';
        } else {
            $scope.complexConstraint.constraintId = $scope.newConstraint.segment + '-' + $scope.selectedNode.position;
        }
        $scope.tempPredicates.push($scope.complexConstraint);
        $scope.initComplexPredicate();
        $scope.changed = true;
    };

    $scope.addFreeTextPredicate = function() {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        var cp = null;
        if ($scope.selectedNode === null) {
            var cp = $rootScope.generateFreeTextPredicate(".", $scope.newConstraint);
        } else {
            var cp = $rootScope.generateFreeTextPredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
        }

        $scope.tempPredicates.push(cp);
        $scope.changed = true;
        $scope.initPredicate();
    };

    $scope.addPredicate = function() {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
            var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
            $scope.tempPredicates.push(cp);
            $scope.changed = true;
        }
        $scope.initPredicate();
    };

    $scope.genLocation = function(segment, field, component, subComponent) {
        var location = null;
        if (field != null && component == null && subComponent == null) {
            location = segment + '-' + field.position + "(" + field.name + ")";
        } else if (field != null && component != null && subComponent == null) {
            location = segment + '-' + field.position + '.' + component.position + "(" + component.name + ")";
        } else if (field != null && component != null && subComponent != null) {
            location = segment + '-' + field.position + '.' + component.position + '.' + subComponent.position + "(" + subComponent.name + ")";
        }

        return location;
    };

    $scope.genPosition = function(field, component, subComponent) {
        var position = null;
        if (field != null && component == null && subComponent == null) {
            position = field.position + '[1]';
        } else if (field != null && component != null && subComponent == null) {
            position = field.position + '[1].' + component.position + '[1]';
        } else if (field != null && component != null && subComponent != null) {
            position = field.position + '[1].' + component.position + '[1].' + subComponent.position + '[1]';
        }

        return position;
    };

    $scope.ok = function() {
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function() {
        angular.copy($scope.tempPredicates, $rootScope.segment.predicates);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };

});
angular.module('igl').controller('ConformanceStatementSegmentCtrl', function($scope, $modalInstance, selectedNode, $rootScope, $q) {
    $scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
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

        if(current.type == 'segment'){
            var seg = current;
            for(var i in seg.fields){
                var f = seg.fields[i];
                f.pathInfoSet = angular.copy(current.pathInfoSet);

                var childPositionNumber = f.position;
                var childLocationName = f.position;
                var childNodeName = f.name;
                var childInstanceNumber = "1";
                var childisInstanceNumberEditable = false;
                if(f.max != '1') {
                    childInstanceNumber = '*';
                    childisInstanceNumberEditable = true;
                }
                var child = angular.copy($rootScope.datatypesMap[f.datatype.id]);
                child.id = new ObjectId().toString();
                f.child = child;
                $scope.generatePathInfo(f, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName);
            }
        }else if(current.type == 'field' || current.type == 'component'){
            var dt = current.child;
            for(var i in dt.components){
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
        data.folderClass = data.childrenVisible?"fa-minus":"fa-plus";
    };

    $scope.beforeFieldDrop = function() {
        var deferred = $q.defer();

        if($scope.draggingStatus === 'ContextTreeNodeDragging_Field') {
            deferred.resolve();
        }else {
            deferred.reject();
        }
        return deferred.promise;
    };

    $scope.beforeNodeDrop = function() {
        var deferred = $q.defer();
        deferred.resolve();
        return deferred.promise;
    };

    $scope.afterFieldDrop = function() {
        $scope.draggingStatus = null;
        $scope.initConformanceStatement();
    };

    $scope.afterNodeDrop = function () {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_1 = $scope.firstNodeData.pathInfoSet;
        $scope.generateFirstPositionAndLocationPath();
    };

    $scope.afterSecondNodeDrop = function () {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_2 = $scope.secondNodeData.pathInfoSet;
        $scope.generateSecondPositionAndLocationPath();
    };

    $scope.draggingNodeFromContextTree = function (event, ui, data) {
        $scope.draggingStatus = 'ContextTreeNodeDragging_Component';
        for(var f in $scope.treeDataForContext[0].fields){
            if($scope.treeDataForContext[0].fields[f].id == data.nodeData.id) $scope.draggingStatus = 'ContextTreeNodeDragging_Field';
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

    $scope.generateFirstPositionAndLocationPath = function (){
        if($scope.newConstraint.pathInfoSet_1){
            var positionPath = '';
            var locationPath = '';
            for (var i in $scope.newConstraint.pathInfoSet_1){
                if(i>0){
                    var pathInfo = $scope.newConstraint.pathInfoSet_1[i];
                    positionPath = positionPath + "." + pathInfo.positionNumber + "[" + pathInfo.instanceNumber + "]";
                    locationPath = locationPath + "." + pathInfo.locationName + "[" + pathInfo.instanceNumber + "]";

                    if(i == $scope.newConstraint.pathInfoSet_1.length -1){
                        locationPath = locationPath + " (" + pathInfo.nodeName + ")";
                    }
                }
            }

            $scope.newConstraint.position_1 = positionPath.substr(1);
            $scope.newConstraint.location_1 = $rootScope.segment.name + '-' + locationPath.substr(1);
        }
    };

    $scope.generateSecondPositionAndLocationPath = function (){
        if($scope.newConstraint.pathInfoSet_2){
            var positionPath = '';
            var locationPath = '';
            for (var i in $scope.newConstraint.pathInfoSet_2){
                if(i>0){
                    var pathInfo = $scope.newConstraint.pathInfoSet_2[i];
                    positionPath = positionPath + "." + pathInfo.positionNumber + "[" + pathInfo.instanceNumber + "]";
                    locationPath = locationPath + "." + pathInfo.locationName + "[" + pathInfo.instanceNumber + "]";

                    if(i == $scope.newConstraint.pathInfoSet_2.length -1){
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
        var cs = $rootScope.generateFreeTextConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
        $scope.tempComformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.addConformanceStatement = function() {
        var cs = $rootScope.generateConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
        $scope.tempComformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.ok = function() {
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function() {
        angular.copy($scope.tempComformanceStatements, $rootScope.segment.conformanceStatements);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
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

    // console.log("$scope.valueSets");
    // console.log($scope.valueSets);
    // console.log("$scope.datatypes");
    // console.log($scope.datatypes);


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
        added:"yes",
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
        // console.log($scope.DT);

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
    //     console.log($scope.VS);

    // }, true);

    $scope.selectUsage = function(usage) {
        // console.log(usage);
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
    // console.log(segment);
    // console.log($scope.fieldToDelete);
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
    $scope.vs = angular.copy(field.tables);
    $scope.tableList = angular.copy(field.tables);;
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
        field.tables = $scope.tableList;

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
            ext: $scope.newDt.ext
        };

        blockUI.stop();

        $modalInstance.close();


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
        // console.log("HEEEEEEEEEEREREEE");
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
            if (element.ext !== null) {
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
            ctrl.segmentId = -1;
            //$scope.setIG2($scope.ig2);
            $scope.variable = !$scope.variable;
            $scope.segList1 = angular.copy($rootScope.segments);
            $scope.dtList1 = angular.copy($rootScope.datatypes);
            $scope.version2 = angular.copy($scope.version1);
            // console.log($scope.scopes);
            // console.log($scope.scopes[1]);
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
        // console.log("$scope.isDeltaCalled");
        // console.log($scope.isDeltaCalled);
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
    //     console.log("hereere=======");
    //     console.log($scope.segment2)
    //     console.log($scope.segments2);
    //     $scope.initt();
    // });

    $scope.version1 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);
    $scope.scope1 = "USER";
    $scope.ig1 = angular.copy($rootScope.igdocument.profile.metaData.name);

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
            // console.log("+++++++++++++++++++++++++++");
            IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
                if (result) {
                    // console.log($scope.scope2);
                    if ($scope.scope2 === "HL7STANDARD") {
                        $scope.igDisabled2 = true;
                        $scope.ig2 = {
                            id: result[0].id,
                            title: result[0].metaData.title
                        };
                        // console.log($scope.ig2);
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
        // console.log("hg==========");
        // console.log($scope.dataList);
        $scope.loadingSelection = false;
        if ($scope.dynamicSeg_params) {
            // console.log($scope.dataList);
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
    // console.log($rootScope.references);
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

    $scope.travelMessage = function (children, positionPath, namePath){
        angular.forEach(children, function(child) {
            if(child.type === 'group'){
                var groupNames = child.name.split(".");
                var groupName = groupNames[groupNames.length - 1];
                $scope.travelMessage(child.children, positionPath + '.' + child.position, namePath + '.' + groupName);
            }else {
                var s = $rootScope.segmentsMap[child.ref.id];
                if(s.name === $scope.segment.name){
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