/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl')
    .controller('SegmentListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, CloneDeleteSvc, $filter, $http, $modal, $timeout) {
//        $scope.loading = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.segmentCopy = null;

        $scope.reset = function () {
//            $scope.loadingSelection = true;
//            $scope.message = "Segment " + $scope.segmentCopy.label + " reset successfully";
//            angular.extend($rootScope.segment, $scope.segmentCopy);
//             $scope.loadingSelection = false;
        };

        $scope.close = function () {
            $rootScope.segment = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };
        
        $scope.copy = function(segment) {
        		CloneDeleteSvc.copySegment(segment);
        }

        $scope.delete = function (segment) {
        		CloneDeleteSvc.deleteSegment(segment);
			$rootScope.$broadcast('event:SetToC');
        };
        
        $scope.hasChildren = function (node) {
            return node && node != null && ((node.fields && node.fields.length > 0 ) || (node.datatype && $rootScope.getDatatype(node.datatype) && $rootScope.getDatatype(node.datatype).components && $rootScope.getDatatype(node.datatype).components.length > 0));
        };


        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
        };

        $scope.onDatatypeChange = function (node) {
            $rootScope.recordChangeForEdit2('field', 'edit', node.id, 'datatype', node.datatype);
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
            return $rootScope.parentsMap && $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId].datatype : null;
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
    });

angular.module('igl')
    .controller('SegmentRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });

angular.module('igl').controller('TableMappingSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {

    $scope.selectedNode = selectedNode;
    $scope.selectedTable = null;
    if (selectedNode.table != undefined) {
        $scope.selectedTable = $rootScope.tablesMap[selectedNode.table];
    }

    $scope.selectTable = function (table) {
        $scope.selectedTable = table;
    };

    $scope.mappingTable = function () {
        $scope.selectedNode.table = $scope.selectedTable.id;
        $rootScope.recordChangeForEdit2('field', 'edit', $scope.selectedNode.id, 'table', $scope.selectedNode.table);
        $scope.ok();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('PredicateSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];
    
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
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.segment = $rootScope.segment.name;
    }
    
    $scope.initPredicate();
    
    $scope.deletePredicate = function (predicate) {
        $rootScope.segment.predicates.splice($rootScope.segment.predicates.indexOf(predicate), 1);
    };
    
    $scope.deletePredicateForComplex = function (predicate) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(predicate), 1);
    };
    
    $scope.changeConstraintType = function () {
    	$scope.initPredicate();
		
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }

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
        for (var i = 0, len1 = $rootScope.segment.predicates.length; i < len1; i++) {
            if ($rootScope.segment.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
                $scope.deletePredicate($rootScope.segment.predicates[i]);
                return true;
            }
        }
        return false;
    };
    
    $scope.addComplexConformanceStatement = function(){
        $scope.deletePredicateByTarget();
        $scope.complexConstraint.constraintId = $scope.newConstraint.segment + '-' + $scope.selectedNode.position;
        $scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
        $scope.complexConstraint.assertion = "<Condition>" + $scope.complexConstraint.assertion + "</Condition>";
        $rootScope.segment.predicates.push($scope.complexConstraint);
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        $scope.complexConstraint = null;
        $scope.newComplexConstraintClassification = 'E';
    };
    
    $scope.compositeConformanceStatements = function(){
        $scope.newComplexConstraint.push($rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint));
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };

    $scope.updatePredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        if($scope.constraintType === 'Plain'){
        	$scope.deletePredicateByTarget();
        }

        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2  = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        
        if ($scope.newConstraint.position_1 != null) {
        	var positionPath = $scope.selectedNode.position + '[1]';
        	var cp = $rootScope.generatePredicate(positionPath, $scope.newConstraint);
            if($scope.constraintType === 'Plain'){
            	cp.assertion = "<Condition>" + cp.assertion + "</Condition>";
            	$scope.segment.predicates.push(cp);
                $rootScope.recordChanged();
            }else if ($scope.constraintType === 'Complex'){
            	$scope.newComplexConstraint.push(cp);
            }
        }
        $scope.initPredicate();
    };

    $scope.genPosition = function (segment, field, component, subComponent) {
        var position = null;
        if (field != null && component == null && subComponent == null) {
            position = segment + '-' + field.position;
        } else if (field != null && component != null && subComponent == null) {
            position = segment + '-' + field.position + '.' + component.position;
        } else if (field != null && component != null && subComponent != null) {
            position = segment + '-' + field.position + '.' + component.position + '.' + subComponent.position;
        }

        return position;
    };

    $scope.genLocation = function (field, component, subComponent) {
        var location = null;
        if (field != null && component == null && subComponent == null) {
            location = field.position + '[1]';
        } else if (field != null && component != null && subComponent == null) {
            location = field.position + '[1].' + component.position + '[1]';
        } else if (field != null && component != null && subComponent != null) {
            location = field.position + '[1].' + component.position + '[1].' + subComponent.position + '[1]';
        }

        return location;
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('ConformanceStatementSegmentCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];
    
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
            constraintId: null,
            contraintType: null,
            value: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.segment = $rootScope.segment.name;
    }
    $scope.initConformanceStatement();

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $rootScope.segment.conformanceStatements.splice($rootScope.segment.conformanceStatements.indexOf(conformanceStatement), 1);
    };
    
    $scope.deleteConformanceStatementForComplex = function (conformanceStatement) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(conformanceStatement), 1);
    };
    
    $scope.changeConstraintType = function () {
    	$scope.initConformanceStatement();
		
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }

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

    $scope.genPosition = function (segment, field, component, subComponent) {
        var position = null;
        if (field != null && component == null && subComponent == null) {
            position = segment + '-' + field.position;
        } else if (field != null && component != null && subComponent == null) {
            position = segment + '-' + field.position + '.' + component.position;
        } else if (field != null && component != null && subComponent != null) {
            position = segment + '-' + field.position + '.' + component.position + '.' + subComponent.position;
        }

        return position;
    };

    $scope.genLocation = function (field, component, subComponent) {
        var location = null;
        if (field != null && component == null && subComponent == null) {
            location = field.position + '[1]';
        } else if (field != null && component != null && subComponent == null) {
            location = field.position + '[1].' + component.position + '[1]';
        } else if (field != null && component != null && subComponent != null) {
            location = field.position + '[1].' + component.position + '[1].' + subComponent.position + '[1]';
        }

        return location;
    };
    
    $scope.addComplexConformanceStatement = function(){
        $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    	$scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
    	$scope.complexConstraint.assertion = "<Assertion>" + $scope.complexConstraint.assertion + "</Assertion>";
    	$rootScope.segment.conformanceStatements.push($scope.complexConstraint);
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        $scope.complexConstraint = null;
        $scope.newComplexConstraintId = '';
        $scope.newComplexConstraintClassification = 'E';
    };
    
    $scope.compositeConformanceStatements = function(){
    	$scope.newComplexConstraint.push($rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint));
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };

    $scope.addConformanceStatement = function () {
        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
        	$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
        	var positionPath = $scope.selectedNode.position + '[1]';
        	var cs = $rootScope.generateConformanceStatement(positionPath, $scope.newConstraint);
            if($scope.constraintType === 'Plain'){
            	cs.assertion = "<Assertion>" + cs.assertion + "</Assertion>";
            	$scope.segment.conformanceStatements.push(cs);
                $rootScope.recordChanged();
            }else if ($scope.constraintType === 'Complex'){
            	$scope.newComplexConstraint.push(cs);
            }
        }
        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});


angular.module('igl').controller('ConfirmSegmentDeleteCtrl', function ($scope, $modalInstance, segToDelete, $rootScope) {
    $scope.segToDelete = segToDelete;
    $scope.loading = false;
    
    $scope.delete = function () {
        $scope.loading = true;
        // Contrary to popular belief, we must remove the segment from both places.
        var index = _.findIndex($rootScope.igdocument.profile.segments.children, function(child) {
        		return child.id === $scope.segToDelete.id;
	    });
	    if (index > -1) $rootScope.igdocument.profile.segments.children.splice(index, 1);
        var index = _.findIndex($rootScope.segments, function(child) {
        		return child.id === $scope.segToDelete.id;
        });        
        if (index > -1) $rootScope.segments.splice(index, 1);
        
        if ($rootScope.segment === $scope.segToDelete) {
            $rootScope.segment = null;
        }
        $rootScope.segmentsMap[$scope.segToDelete.id] = null;
        $rootScope.references = [];
        if ($scope.segToDelete.id < 0) {
//            var index = $rootScope.changes["segment"]["add"].indexOf($scope.segToDelete);
//            if (index > -1) $rootScope.changes["segment"]["add"].splice(index, 1);
//            if ($rootScope.changes["segment"]["add"] && $rootScope.changes["segment"]["add"].length === 0) {
//                delete  $rootScope.changes["segment"]["add"];
//            }
//            if ($rootScope.changes["segment"] && Object.getOwnPropertyNames($rootScope.changes["segment"]).length === 0) {
//                delete  $rootScope.changes["segment"];
//            }
        } else {
            $rootScope.recordDelete("segment", "edit", $scope.segToDelete.id);
//            if ($scope.segToDelete.components != undefined && $scope.segToDelete.components != null && $scope.segToDelete.components.length > 0) {
//
//                //clear components changes
//                angular.forEach($scope.dtToDelete.components, function (component) {
//                    $rootScope.recordDelete("component", "edit", component.id);
//                    $rootScope.removeObjectFromChanges("component", "delete", component.id);
//                });
//                if ($rootScope.changes["component"]["delete"] && $rootScope.changes["component"]["delete"].length === 0) {
//                    delete  $rootScope.changes["component"]["delete"];
//                }
//
//                if ($rootScope.changes["component"] && Object.getOwnPropertyNames($rootScope.changes["component"]).length === 0) {
//                    delete  $rootScope.changes["component"];
//                }
//
//            }
//
//            if ($scope.segToDelete.predicates != undefined && $scope.segToDelete.predicates != null && $scope.segToDelete.predicates.length > 0) {
//                //clear predicates changes
//                angular.forEach($scope.segToDelete.predicates, function (predicate) {
//                    $rootScope.recordDelete("predicate", "edit", predicate.id);
//                    $rootScope.removeObjectFromChanges("predicate", "delete", predicate.id);
//                });
//                if ($rootScope.changes["predicate"]["delete"] && $rootScope.changes["predicate"]["delete"].length === 0) {
//                    delete  $rootScope.changes["predicate"]["delete"];
//                }
//
//                if ($rootScope.changes["predicate"] && Object.getOwnPropertyNames($rootScope.changes["predicate"]).length === 0) {
//                    delete  $rootScope.changes["predicate"];
//                }
//
//            }
//
//            if ($scope.dtToDelete.conformanceStatements != undefined && $scope.dtToDelete.conformanceStatements != null && $scope.dtToDelete.conformanceStatements.length > 0) {
//                //clear conforamance statement changes
//                angular.forEach($scope.dtToDelete.conformanceStatements, function (confStatement) {
//                    $rootScope.recordDelete("conformanceStatement", "edit", confStatement.id);
//                    $rootScope.removeObjectFromChanges("conformanceStatement", "delete", confStatement.id);
//                });
//                if ($rootScope.changes["conformanceStatement"]["delete"] && $rootScope.changes["conformanceStatement"]["delete"].length === 0) {
//                    delete  $rootScope.changes["conformanceStatement"]["delete"];
//                }
//
//                if ($rootScope.changes["conformanceStatement"] && Object.getOwnPropertyNames($rootScope.changes["conformanceStatement"]).length === 0) {
//                    delete  $rootScope.changes["conformanceStatement"];
//                }
//            }
        }


        $rootScope.msg().text = "segDeleteSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
		$rootScope.$broadcast('event:SetToC');
        $modalInstance.close($scope.segToDelete);

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
