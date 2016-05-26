/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl')
    .controller('SegmentListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, CloneDeleteSvc, $filter, $http, $modal, $timeout, SegmentService, FieldService, FilteringSvc, MastermapSvc, SegmentLibrarySvc) {
//        $scope.loading = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.segmentCopy = null;
        $scope.selectedChildren = [];
        $scope.saving = false;

        $scope.reset = function () {
            if($scope.editForm){
                $scope.editForm.$dirty = false;
                $scope.editForm.$setPristine();

            }
            $rootScope.segment = angular.copy($rootScope.segmentsMap[$rootScope.segment.id]);
            $rootScope.clearChanges();
            if ($scope.segmentsParams) {
                $scope.segmentsParams.refresh();
            }
        };

        $scope.close = function () {
            $rootScope.segment = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };

        $scope.copy = function (segment) {
            CloneDeleteSvc.copySegment(segment);
        }

        $scope.delete = function (segment) {
            CloneDeleteSvc.deleteSegment(segment);
            $rootScope.$broadcast('event:SetToC');
        };

        $scope.hasChildren = function (node) {
            return node && node != null && ((node.fields && node.fields.length > 0 ) || (node.datatype && $rootScope.getDatatype(node.datatype.id) && $rootScope.getDatatype(node.datatype.id).components && $rootScope.getDatatype(node.datatype.id).components.length > 0));
        };


        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
        };

        $scope.onDatatypeChange = function (node) {
            $rootScope.recordChangeForEdit2('field', 'edit', node.id, 'datatype', node.datatype.id);
            $scope.refreshTree();
        };

        $scope.refreshTree = function () {
            if ($scope.segmentsParams)
                $scope.segmentsParams.refresh();
        };

        $scope.goToTable = function (table) {
            $scope.$emit('event:openTable', table);
        };

        $scope.goToDatatype = function (datatype) {
            $scope.$emit('event:openDatatype', datatype);
        };

        $scope.deleteTable = function (node) {
            node.table = null;
            $rootScope.recordChangeForEdit2('field', 'edit', node.id, 'table', null);
        };

        $scope.mapTable = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'TableMappingSegmentCtrl.html',
                controller: 'TableMappingSegmentCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function () {
                        return node;
                    }
                }
            });
            modalInstance.result.then(function (node) {
                $scope.selectedNode = node;
            }, function () {
            });
        };

        $scope.findDTByComponentId = function (componentId) {
            return $rootScope.parentsMap && $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
        };

        $scope.isSub = function (component) {
            return $scope.isSubDT(component);
        };

        $scope.isSubDT = function (component) {
            return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
        };

        $scope.managePredicate = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateSegmentCtrl.html',
                controller: 'PredicateSegmentCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function () {
                        return node;
                    }
                }
            });
            modalInstance.result.then(function (node) {
                $scope.selectedNode = node;
            }, function () {
            });
        };

        $scope.manageConformanceStatement = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'ConformanceStatementSegmentCtrl.html',
                controller: 'ConformanceStatementSegmentCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function () {
                        return node;
                    }
                }
            });
            modalInstance.result.then(function (node) {
                $scope.selectedNode = node;
            }, function () {
            });
        };

        $scope.show = function (segment) {
            return true;
        };

        $scope.countConformanceStatements = function (position) {
            var count = 0;
            if ($rootScope.segment != null) {
                for (var i = 0, len1 = $rootScope.segment.conformanceStatements.length; i < len1; i++) {
                    if ($rootScope.segment.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
                        count = count + 1;
                }
            }
            return count;
        };

        $scope.countPredicate = function (position) {
            if ($rootScope.segment != null) {
                for (var i = 0, len1 = $rootScope.segment.predicates.length; i < len1; i++) {
                    if ($rootScope.segment.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                        return 1;
                }
            }
            return 0;
        };

        $scope.countPredicateOnComponent = function (position, componentId) {
            var dt = $scope.findDTByComponentId(componentId);
            if (dt != null)
                for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
                    if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                        return 1;
                }

            return 0;
        };

        $scope.isRelevant = function (node) {
            return SegmentService.isRelevant(node);
        };

        $scope.isBranch = function (node) {
            SegmentService.isBranch(node);
        };

        $scope.isVisible = function (node) {
            return SegmentService.isVisible(node);
        };

        $scope.children = function (node) {
            return SegmentService.getNodes(node);
        };

        $scope.getParent = function (node) {
            return SegmentService.getParent(node);
        };

        $scope.getSegmentLevelConfStatements = function (element) {
            return SegmentService.getSegmentLevelConfStatements(element);
        };

        $scope.getSegmentLevelPredicates = function (element) {
            return SegmentService.getSegmentLevelPredicates(element);
        };


        $scope.isChildSelected = function (component) {
            return  $scope.selectedChildren.indexOf(component) >= 0;
        };

        $scope.isChildNew = function (component) {
            return component && component != null && component.status === 'DRAFT';
        };


        $scope.selectChild = function ($event, child) {
            var checkbox = $event.target;
            var action = (checkbox.checked ? 'add' : 'remove');
            updateSelected(action, child);
        };


        $scope.selectAllChildren = function ($event) {
            var checkbox = $event.target;
            var action = (checkbox.checked ? 'add' : 'remove');
            for (var i = 0; i < $rootScope.segment.fields.length; i++) {
                var component = $rootScope.segment.fields[i];
                updateSelected(action, component);
            }
        };

        var updateSelected = function (action, child) {
            if (action === 'add' && !$scope.isChildSelected(child)) {
                $scope.selectedChildren.push(child);
            }
            if (action === 'remove' && $scope.isChildSelected(child)) {
                $scope.selectedChildren.splice($scope.selectedChildren.indexOf(child), 1);
            }
        };

        //something extra I couldn't resist adding :)
        $scope.isSelectedAllChildren = function () {
            return $rootScope.segment && $rootScope.segment != null && $rootScope.segment.fields && $scope.selectedChildren.length === $rootScope.segment.fields.length;
        };


        /**
         * TODO: update master map
         */
        $scope.createNewField = function () {
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
        $scope.deleteFields = function () {
            if ($rootScope.segment != null && $scope.selectedChildren != null && $scope.selectedChildren.length > 0) {
                FieldService.deleteList($scope.selectedChildren, $rootScope.segment);
                //TODO update master map
                //TODO:remove as legacy code
                angular.forEach($scope.selectedChildren, function (child) {
                    delete $rootScope.parentsMap[child.id];
                });
                $scope.selectedChildren = [];
                if ($scope.segmentsParams)
                    $scope.segmentsParams.refresh();
            }
        };

        $scope.save = function () {
            $scope.saving = true;
            var segment = $rootScope.segment;
            var ext = segment.ext;
             if(segment.libIds === undefined) segment.libIds = [];
            if (segment.libIds.indexOf($rootScope.igdocument.profile.segmentLibrary.id) == -1) {
                segment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
            }
            SegmentService.save($rootScope.segment).then(function (result) {
                 var oldLink = SegmentLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.segmentLibrary);
                if (oldLink != null) {
                    SegmentService.merge($rootScope.segmentsMap[result.id], result);
                    var newLink = SegmentService.getSegmentLink(result);
                    newLink.ext = ext;
                    SegmentLibrarySvc.updateChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function (link) {
                        oldLink.ext = newLink.ext;
                        oldLink.name = newLink.name;
                        $scope.saving = false;
                        $scope.selectedChildren = [];
                        if($scope.editForm) {
                            $scope.editForm.$setPristine();
                            $scope.editForm.$dirty = false;
                        }
                        $rootScope.clearChanges();

                        if ($scope.segmentsParams)
                            $scope.segmentsParams.refresh();
                        $rootScope.msg().text = "segmentSaved";
                        $rootScope.msg().type = "success";
                        $rootScope.msg().show = true;

                      }, function (error) {
                        $scope.saving = false;
                        $rootScope.msg().text = error.data.text;
                        $rootScope.msg().type = error.data.type;
                        $rootScope.msg().show = true;
                     });
                }
                //TODO update Toc
            }, function (error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

//        $scope.cancel = function () {
//            //TODO: remove changes from master ma
//            angular.forEach($rootScope.segment.fields, function (child) {
//                if ($scope.isChildNew(child.status)) {
//                    delete $rootScope.parentsMap[child.id];
//                }
//            });
//            $rootScope.segment = null;
//            $scope.selectedChildren = [];
//            $rootScope.clearChanges();
//            // revert
//        };

        $scope.reset = function () {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $rootScope.segment = angular.copy($rootScope.segmentsMap[$rootScope.segment.id]);
            $rootScope.clearChanges();
            if ($scope.segmentsParams) {
                $scope.segmentsParams.refresh();
            }
        };


        var searchById = function (id) {
            var children = $rootScope.igdocument.profile.segmentLibrary.children;
            for (var i = 0; i < $rootScope.igdocument.profile.segmentLibrary.children; i++) {
                if (children[i].id === id) {
                    return children[i];
                }
            }
            return null;
        };

        var indexOf = function (id) {
            var children = $rootScope.igdocument.profile.segmentLibrary.children;
            for (var i = 0; i < children; i++) {
                if (children[i].id === id) {
                    return i;
                }
            }
            return -1;

        };


        $scope.showSelectDatatypeFlavorDlg = function (field) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectDatatypeFlavor.html',
                controller: 'SelectDatatypeFlavorCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    currentDatatype: function () {
                        return $rootScope.datatypesMap[field.datatype.id];
                    },
                    hl7Version: function () {
                        return $rootScope.igdocument.metaData.hl7Version;
                    },
                    datatypeLibrary:function(){
                        return $rootScope.igdocument.profile.datatypeLibrary;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                MastermapSvc.deleteElementChildren(field.datatype.id, "datatype", field.id, field.type);
                field.datatype.id = datatype.id;
                MastermapSvc.addDatatypeId(datatype.id, [field.id, field.type]);
                if ($scope.segmentsParams)
                    $scope.segmentsParams.refresh();
            });

        };

    });

angular.module('igl')
    .controller('SegmentRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });

angular.module('igl').controller('TableMappingSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.changed = false;
    $scope.selectedNode = selectedNode;
    $scope.selectedTable = null;
    if (selectedNode.table != undefined) {
        $scope.selectedTable = $rootScope.tablesMap[selectedNode.table.id];
    }

    $scope.selectTable = function (table) {
        $scope.selectedTable = table;
        $scope.changed = true;
    };

    $scope.mappingTable = function () {
        $scope.selectedNode.table.id = $scope.selectedTable.id;
        $rootScope.recordChangeForEdit2('field', 'edit', $scope.selectedNode.id, 'table', $scope.selectedNode.table.id);
        $scope.ok();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('PredicateSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;

    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy($rootScope.segment.predicates, $scope.tempPredicates);

    $scope.setChanged = function () {
        $scope.changed = true;
    }

    $scope.initPredicate = function () {
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
            verb: null,
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

    $scope.initComplexPredicate = function () {
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.complexConstraintTrueUsage = null;
        $scope.complexConstraintFalseUsage = null;
    }

    $scope.initPredicate();

    $scope.deletePredicate = function (predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
        $scope.changed = true;
    };

    $scope.updateField_1 = function () {
        $scope.newConstraint.component_1 = null;
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateField_2 = function () {
        $scope.newConstraint.component_2 = null;
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };


    $scope.deletePredicateByTarget = function () {
        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
                $scope.deletePredicate($scope.tempPredicates[i]);
                return true;
            }
        }
        return false;
    };

    $scope.addComplexPredicate = function () {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;
        if($scope.selectedNode === null) {
        	$scope.complexConstraint.constraintId = '.';
    	}else {
    		$scope.complexConstraint.constraintId = $scope.newConstraint.segment + '-' + $scope.selectedNode.position;
    	}
    	$scope.tempPredicates.push($scope.complexConstraint);
    	$scope.initComplexPredicate();
        $scope.changed = true;
    };
    
    $scope.addPredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
        	var cp = null;
        	if($scope.selectedNode === null) {
        		var cp = $rootScope.generatePredicate(".", $scope.newConstraint);
        	}else {
        		var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
        	}
        	$scope.tempPredicates.push(cp);
            $scope.changed = true;
        }
        $scope.initPredicate();
    };

    $scope.genLocation = function (segment, field, component, subComponent) {
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

    $scope.genPosition = function (field, component, subComponent) {
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

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function () {
        angular.copy($scope.tempPredicates, $rootScope.segment.predicates);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('ConformanceStatementSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID();
    $scope.newComplexConstraint = [];

    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    angular.copy($rootScope.segment.conformanceStatements, $scope.tempComformanceStatements);

    $scope.setChanged = function () {
        $scope.changed = true;
    }

    $scope.initConformanceStatement = function () {
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
            verb: null,
            constraintId: $rootScope.calNextCSID(),
            contraintType: null,
            value: null,
            value2: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });
        $scope.newConstraint.segment = $rootScope.segment.name;
    }

    $scope.initComplexStatement = function () {
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.newComplexConstraintId = $rootScope.calNextCSID();
    }

    $scope.initConformanceStatement();

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
    };

    $scope.updateField_1 = function () {
        $scope.newConstraint.component_1 = null;
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateField_2 = function () {
        $scope.newConstraint.component_2 = null;
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.genLocation = function (segment, field, component, subComponent) {
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

    $scope.genPosition = function (field, component, subComponent) {
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
    $scope.addComplexConformanceStatement = function(){
    	$scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
    	$scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    	if($rootScope.conformanceStatementIdList.indexOf($scope.complexConstraint.constraintId) == -1) $rootScope.conformanceStatementIdList.push($scope.complexConstraint.constraintId);
    	$scope.tempComformanceStatements.push($scope.complexConstraint);
    	$scope.initComplexStatement();
        $scope.changed = true;
    };


    $scope.addConformanceStatement = function () {
        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
        	$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
        	if($scope.selectedNode === null) {
        		var cs = $rootScope.generateConformanceStatement(".", $scope.newConstraint);
        	}else {
        		var cs = $rootScope.generateConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
        	}
            $scope.tempComformanceStatements.push(cs);
            if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
            $scope.changed = true;
        }
        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            $rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf(cs.constraintId), 1);
        });

        angular.forEach($rootScope.segment.conformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });

        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });
        angular.copy($scope.tempComformanceStatements, $rootScope.segment.conformanceStatements);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };

});


angular.module('igl').controller('ConfirmSegmentDeleteCtrl', function ($scope, $rootScope, $modalInstance, segToDelete, $rootScope, SegmentService, SegmentLibrarySvc,MastermapSvc, CloneDeleteSvc) {
    $scope.segToDelete = segToDelete;
    $scope.loading = false;

    $scope.delete = function () {
    	$scope.loading = true;
        if($scope.segToDelete.scope === 'USER'){
        	CloneDeleteSvc.deleteSegmentAndSegmentLink($scope.segToDelete);
        }else {
        	CloneDeleteSvc.deleteSegmentLink($scope.segToDelete);
        }
        $modalInstance.close($scope.segToDelete);
        $scope.loading = false;
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('SegmentReferencesCtrl', function ($scope, $modalInstance, segToDelete) {

    $scope.segToDelete = segToDelete;

    $scope.ok = function () {
        $modalInstance.close($scope.segToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
