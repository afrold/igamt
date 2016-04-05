/**
 * Created by haffo on 2/13/15.
 */
angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc, ViewSettings,DatatypeService) {
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.datatypeCopy = null;
        $scope.viewSettings = ViewSettings;

        $scope.init = function () {
       };

        $scope.copy = function (datatype) {
        		CloneDeleteSvc.copyDatatype(datatype);
        };

        $scope.recordDatatypeChange = function (type, command, id, valueType, value) {
            var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $rootScope.datatype.id);
            if (datatypeFromChanges === undefined) {
                $rootScope.recordChangeForEdit2(type, command, id, valueType, value);
            }
        };

        $scope.close = function () {
            $rootScope.datatype = null;
            $scope.refreshTree();
            $scope.loadingSelection = false;
        };

        $scope.delete = function (datatype) {
        		CloneDeleteSvc.deleteDatatype(datatype);
			$rootScope.$broadcast('event:SetToC');
       };

        $scope.hasChildren = function (node) {
            return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype) != undefined && $rootScope.getDatatype(node.datatype).components != null && $rootScope.getDatatype(node.datatype).components.length > 0;
        };

        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
        };

        $scope.onDatatypeChange = function (node) {
            $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'datatype', node.datatype);
            $scope.refreshTree(); // TODO: Refresh only the node
        };

        $scope.refreshTree = function () {
            if ($scope.datatypesParams)
                $scope.datatypesParams.refresh();
        };

        $scope.goToTable = function (table) {
            $scope.$emit('event:openTable', table);
        };

        $scope.deleteTable = function (node) {
            node.table = null;
            $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'table', null);
        };

        $scope.mapTable = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'TableMappingDatatypeCtrl.html',
                controller: 'TableMappingDatatypeCtrl',
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

        $scope.managePredicate = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateDatatypeCtrl.html',
                controller: 'PredicateDatatypeCtrl',
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
                templateUrl: 'ConformanceStatementDatatypeCtrl.html',
                controller: 'ConformanceStatementDatatypeCtrl',
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

        $scope.isSubDT = function (component) {
            if ($rootScope.datatype != null) {
                for (var i = 0, len = $rootScope.datatype.components.length; i < len; i++) {
                    if ($rootScope.datatype.components[i].id === component.id)
                        return false;
                }
            }
            return true;
        };

        $scope.findDTByComponentId = function (componentId) {
            return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
        };

        $scope.countConformanceStatements = function (position) {
            var count = 0;
            if ($rootScope.datatype != null)
                for (var i = 0, len1 = $rootScope.datatype.conformanceStatements.length; i < len1; i++) {
                    if ($rootScope.datatype.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
                        count = count + 1;
                }

            return count;
        };

        $scope.countPredicate = function (position) {
            if ($rootScope.datatype != null)
                for (var i = 0, len1 = $rootScope.datatype.predicates.length; i < len1; i++) {
                    if ($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                        return 1;
                }

            return 0;
        };
        
        $scope.countPredicateOnSubComponent = function (position, componentId) {
        	var dt = $scope.findDTByComponentId(componentId);
        	if (dt != null)
                for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
                    if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                        return 1;
                }

            return 0;
        };


        $scope.isRelevant = function (node) {
            return DatatypeService.isRelevant(node);
        };

        $scope.isBranch = function (node) {
            return DatatypeService.isBranch(node);
        };


        $scope.isVisible = function (node) {
            return DatatypeService.isVisible(node);
         };

        $scope.children = function (node) {
            return DatatypeService.getNodes(node);
        };

        $scope.getParent = function (node) {
            return DatatypeService.getParent(node);
        };

        $scope.getDatatypeLevelConfStatements = function (element) {
            return DatatypeService.getDatatypeLevelConfStatements(element);
        };

        $scope.getDatatypeLevelPredicates = function (element) {
            return DatatypeService.getDatatypeLevelPredicates(element);
        };

    });


angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });


angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function ($scope, $modalInstance, dtToDelete, $rootScope) {
    $scope.dtToDelete = dtToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        // We must delete from two collections.
        var index = $rootScope.datatypes.indexOf($scope.dtToDelete);
       if (index > -1) $rootScope.datatypes.splice(index, 1);
        if ($rootScope.datatype === $scope.dtToDelete) {
            $rootScope.datatype = null;
        }
        var index = $rootScope.igdocument.profile.datatypes.children.indexOf($scope.dtToDelete);
        if (index > -1) $rootScope.igdocument.profile.datatypes.children.splice(index, 1);
        $rootScope.datatypesMap[$scope.dtToDelete.id] = null;
        $rootScope.references = [];
        if ($scope.dtToDelete.id < 0) { //datatype flavor
//            var index = $rootScope.changes["datatype"]["add"].indexOf($scope.dtToDelete);
//            if (index > -1) $rootScope.changes["datatype"]["add"].splice(index, 1);
//            if ($rootScope.changes["datatype"]["add"] && $rootScope.changes["datatype"]["add"].length === 0) {
//                delete  $rootScope.changes["datatype"]["add"];
//            }
//            if ($rootScope.changes["datatype"] && Object.getOwnPropertyNames($rootScope.changes["datatype"]).length === 0) {
//                delete  $rootScope.changes["datatype"];
//            }
        } else {
            $rootScope.recordDelete("datatype", "edit", $scope.dtToDelete.id);
            if ($scope.dtToDelete.components != undefined && $scope.dtToDelete.components != null && $scope.dtToDelete.components.length > 0) {

                //clear components changes
                angular.forEach($scope.dtToDelete.components, function (component) {
                    $rootScope.recordDelete("component", "edit", component.id);
//                    $rootScope.removeObjectFromChanges("component", "delete", component.id);
                });
//                if ($rootScope.changes["component"]["delete"] && $rootScope.changes["component"]["delete"].length === 0) {
//                    delete  $rootScope.changes["component"]["delete"];
//                }

//                if ($rootScope.changes["component"] && Object.getOwnPropertyNames($rootScope.changes["component"]).length === 0) {
//                    delete  $rootScope.changes["component"];
//                }

            }

            if ($scope.dtToDelete.predicates != undefined && $scope.dtToDelete.predicates != null && $scope.dtToDelete.predicates.length > 0) {
                //clear predicates changes
                angular.forEach($scope.dtToDelete.predicates, function (predicate) {
                    $rootScope.recordDelete("predicate", "edit", predicate.id);
//                    $rootScope.removeObjectFromChanges("predicate", "delete", predicate.id);
                });
//                if ($rootScope.changes["predicate"]["delete"] && $rootScope.changes["predicate"]["delete"].length === 0) {
//                    delete  $rootScope.changes["predicate"]["delete"];
//                }

//                if ($rootScope.changes["predicate"] && Object.getOwnPropertyNames($rootScope.changes["predicate"]).length === 0) {
//                    delete  $rootScope.changes["predicate"];
//                }

            }

            if ($scope.dtToDelete.conformanceStatements != undefined && $scope.dtToDelete.conformanceStatements != null && $scope.dtToDelete.conformanceStatements.length > 0) {
                //clear conforamance statement changes
                angular.forEach($scope.dtToDelete.conformanceStatements, function (confStatement) {
                    $rootScope.recordDelete("conformanceStatement", "edit", confStatement.id);
//                    $rootScope.removeObjectFromChanges("conformanceStatement", "delete", confStatement.id);
                });
//                if ($rootScope.changes["conformanceStatement"]["delete"] && $rootScope.changes["conformanceStatement"]["delete"].length === 0) {
//                    delete  $rootScope.changes["conformanceStatement"]["delete"];
//                }

//                if ($rootScope.changes["conformanceStatement"] && Object.getOwnPropertyNames($rootScope.changes["conformanceStatement"]).length === 0) {
//                    delete  $rootScope.changes["conformanceStatement"];
//                }
            }
        }
        $rootScope.msg().text = "dtDeleteSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
		$rootScope.$broadcast('event:SetToC');
        $modalInstance.close($scope.dtToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('DatatypeReferencesCtrl', function ($scope, $modalInstance, dtToDelete) {

    $scope.dtToDelete = dtToDelete;

    $scope.ok = function () {
        $modalInstance.close($scope.dtToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('TableMappingDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.changed = false;
    $scope.selectedNode = selectedNode;
    $scope.selectedTable = null;
    if (selectedNode.table != undefined) {
        $scope.selectedTable = $rootScope.tablesMap[selectedNode.table];
    }

    $scope.selectTable = function (table) {
    	$scope.changed = true;
        $scope.selectedTable = table;
    };

    $scope.mappingTable = function () {
        $scope.selectedNode.table = $scope.selectedTable.id;
        $rootScope.recordChangeForEdit2('component', 'edit', $scope.selectedNode.id, 'table', $scope.selectedTable.id);
        $scope.ok();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('ConformanceStatementDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.selectedNode = selectedNode;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraint = [];
    
    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    angular.copy($rootScope.datatype.conformanceStatements, $scope.tempComformanceStatements);
    
    
    $scope.setChanged = function () {
    	$scope.changed = true;
    }
    
    $scope.initConformanceStatement = function () {
    	$scope.newConstraint = angular.fromJson({
    		position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            datatype: '',
            component_1: null,
            subComponent_1: null,
            component_2: null,
            subComponent_2: null,
            verb: null,
            constraintId: $rootScope.calNextCSID(),
            contraintType: null,
            value: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.datatype = $rootScope.datatype.name;
    }
    
    $scope.initComplexStatement = function () {
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.newComplexConstraintId = '';
    }
    
    $scope.initConformanceStatement();

    $scope.deleteConformanceStatement = function (conformanceStatement) {
    	$scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
    };

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.genLocation = function (datatype, component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
        	location = datatype + '.' + component.position + "(" + component.name +")";
        } else if (component != null && subComponent != null) {
        	location = datatype + '.' + component.position + '.' + subComponent.position  + "(" + subComponent.name +")";
        }

        return location;
    };

    $scope.genPosition = function (component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
        	position = component.position + '[1]';
        } else if (component != null && subComponent != null) {
        	Position = component.position + '[1]' + '.' + subComponent.position + '[1]';
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
        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
        	$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
        	var positionPath = $scope.selectedNode.position + '[1]';
        	var cs = $rootScope.generateConformanceStatement(positionPath, $scope.newConstraint);
            $scope.tempComformanceStatements.push(cs);
            $scope.changed = true;
            if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        }
        $scope.initConformanceStatement();
        
        
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };
    
    $scope.saveclose = function () {
    	angular.forEach($scope.tempComformanceStatements, function (cs) {
    		if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    	});
    	angular.copy($scope.tempComformanceStatements, $rootScope.datatype.conformanceStatements);
    	$rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
});


angular.module('igl').controller('PredicateDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.selectedNode = selectedNode;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;
    
    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy($rootScope.datatype.predicates, $scope.tempPredicates);

    
    $scope.setChanged = function () {
    	$scope.changed = true;
    }

    $scope.initPredicate = function () {
    	$scope.newConstraint = angular.fromJson({
    		position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            datatype: '',
            component_1: null,
            subComponent_1: null,
            component_2: null,
            subComponent_2: null,
            verb: null,
            contraintType: null,
            value: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });
        $scope.newConstraint.datatype = $rootScope.datatype.name;
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

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    
    $scope.genLocation = function (datatype, component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
        	location = datatype + '.' + component.position + "(" + component.name +")";
        } else if (component != null && subComponent != null) {
        	location = datatype + '.' + component.position + '.' + subComponent.position  + "(" + subComponent.name +")";
        }

        return location;
    };

    $scope.genPosition = function (component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
        	position = component.position + '[1]';
        } else if (component != null && subComponent != null) {
        	Position = component.position + '[1]' + '.' + subComponent.position + '[1]';
        }

        return position;
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
    
    $scope.addComplexPredicate = function(){
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;
    	$scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
    	$scope.tempPredicates.push($scope.complexConstraint);
    	$scope.initComplexPredicate();
        $scope.changed = true;
    };
    
    $scope.updatePredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        
        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
        	var positionPath = $scope.selectedNode.position + '[1]';
        	var cp = $rootScope.generatePredicate(positionPath, $scope.newConstraint);
            $scope.tempPredicates.push(cp);
            $scope.changed = true;
        }
        $scope.initPredicate();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function () {
    	angular.copy($scope.tempPredicates, $rootScope.datatype.predicates);
    	$rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
});
