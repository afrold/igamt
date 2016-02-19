/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc) {
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.datatypeCopy = null;
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
            $scope.accordion.datatypeStatus = false;
            $scope.accordion.listStatus = !$scope.accordion.datatypeStatus;
        };

        $scope.delete = function (datatype) {
        		  CloneDeleteSvc.deleteDatatype(datatype);
//            $rootScope.references = [];
//            angular.forEach($rootScope.segments, function (segment) {
//                $rootScope.findDatatypeRefs(datatype, segment);
//            });
//            if ($rootScope.references != null && $rootScope.references.length > 0) {
//                $scope.abortDelete(datatype);
//            } else {
//                $scope.confirmDelete(datatype);
//            }
			$rootScope.$broadcast('event:SetToC');
       };

//        $scope.abortDelete = function (datatype) {
//            var modalInstance = $modal.open({
//                templateUrl: 'DatatypeReferencesCtrl.html',
//                controller: 'DatatypeReferencesCtrl',
//                resolve: {
//                    dtToDelete: function () {
//                        return datatype;
//                    }
//                }
//            });
//            modalInstance.result.then(function (datatype) {
//                $scope.dtToDelete = datatype;
//            }, function () {
//            });
//        };

//        $scope.confirmDelete = function (datatype) {
//            var modalInstance = $modal.open({
//                templateUrl: 'ConfirmDatatypeDeleteCtrl.html',
//                controller: 'ConfirmDatatypeDeleteCtrl',
//                resolve: {
//                    dtToDelete: function () {
//                        return datatype;
//                    }
//                }
//            });
//            modalInstance.result.then(function (datatype) {
//                $scope.dtToDelete = datatype;
//            }, function () {
//            });
//        };


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
            return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId].datatype : null;
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
        var index = $rootScope.datatypes.indexOf($scope.dtToDelete);
        if (index > -1) $rootScope.datatypes.splice(index, 1);
        if ($rootScope.datatype === $scope.dtToDelete) {
            $rootScope.datatype = null;
        }
        $rootScope.datatypesMap[$scope.dtToDelete.id] = null;
        $rootScope.references = [];
        if ($scope.dtToDelete.id < 0) { //datatype flavor
            var index = $rootScope.changes["datatype"]["add"].indexOf($scope.dtToDelete);
            if (index > -1) $rootScope.changes["datatype"]["add"].splice(index, 1);
            if ($rootScope.changes["datatype"]["add"] && $rootScope.changes["datatype"]["add"].length === 0) {
                delete  $rootScope.changes["datatype"]["add"];
            }
            if ($rootScope.changes["datatype"] && Object.getOwnPropertyNames($rootScope.changes["datatype"]).length === 0) {
                delete  $rootScope.changes["datatype"];
            }
        } else {
            $rootScope.recordDelete("datatype", "edit", $scope.dtToDelete.id);
            if ($scope.dtToDelete.components != undefined && $scope.dtToDelete.components != null && $scope.dtToDelete.components.length > 0) {

                //clear components changes
                angular.forEach($scope.dtToDelete.components, function (component) {
                    $rootScope.recordDelete("component", "edit", component.id);
                    $rootScope.removeObjectFromChanges("component", "delete", component.id);
                });
                if ($rootScope.changes["component"]["delete"] && $rootScope.changes["component"]["delete"].length === 0) {
                    delete  $rootScope.changes["component"]["delete"];
                }

                if ($rootScope.changes["component"] && Object.getOwnPropertyNames($rootScope.changes["component"]).length === 0) {
                    delete  $rootScope.changes["component"];
                }

            }

            if ($scope.dtToDelete.predicates != undefined && $scope.dtToDelete.predicates != null && $scope.dtToDelete.predicates.length > 0) {
                //clear predicates changes
                angular.forEach($scope.dtToDelete.predicates, function (predicate) {
                    $rootScope.recordDelete("predicate", "edit", predicate.id);
                    $rootScope.removeObjectFromChanges("predicate", "delete", predicate.id);
                });
                if ($rootScope.changes["predicate"]["delete"] && $rootScope.changes["predicate"]["delete"].length === 0) {
                    delete  $rootScope.changes["predicate"]["delete"];
                }

                if ($rootScope.changes["predicate"] && Object.getOwnPropertyNames($rootScope.changes["predicate"]).length === 0) {
                    delete  $rootScope.changes["predicate"];
                }

            }

            if ($scope.dtToDelete.conformanceStatements != undefined && $scope.dtToDelete.conformanceStatements != null && $scope.dtToDelete.conformanceStatements.length > 0) {
                //clear conforamance statement changes
                angular.forEach($scope.dtToDelete.conformanceStatements, function (confStatement) {
                    $rootScope.recordDelete("conformanceStatement", "edit", confStatement.id);
                    $rootScope.removeObjectFromChanges("conformanceStatement", "delete", confStatement.id);
                });
                if ($rootScope.changes["conformanceStatement"]["delete"] && $rootScope.changes["conformanceStatement"]["delete"].length === 0) {
                    delete  $rootScope.changes["conformanceStatement"]["delete"];
                }

                if ($rootScope.changes["conformanceStatement"] && Object.getOwnPropertyNames($rootScope.changes["conformanceStatement"]).length === 0) {
                    delete  $rootScope.changes["conformanceStatement"];
                }
            }
        }


        $rootScope.msg().text = "dtDeleteSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
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
        $rootScope.recordChangeForEdit2('component', 'edit', $scope.selectedNode.id, 'table', $scope.selectedTable.id);
        $scope.ok();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('ConformanceStatementDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];

    $scope.newConstraint = angular.fromJson({
        datatype: '',
        component_1: null,
        subComponent_1: null,
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
    $scope.newConstraint.datatype = $rootScope.datatype.name;
    
    
    $scope.initConformanceStatement = function () {
    	$scope.newConstraint = angular.fromJson({
            datatype: '',
            component_1: null,
            subComponent_1: null,
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
        $scope.newConstraint.datatype = $rootScope.datatype.name;
    }
    
    

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $rootScope.datatype.conformanceStatements.splice($rootScope.datatype.conformanceStatements.indexOf(conformanceStatement), 1);
        $rootScope.datatypeConformanceStatements.splice($rootScope.datatypeConformanceStatements.indexOf(conformanceStatement), 1);
        if (!$scope.isNewCS(conformanceStatement.id)) {
            $rootScope.recordChangeForEdit2('conformanceStatement', "delete", conformanceStatement.id, 'id', conformanceStatement.id);
        }
    };
    
    $scope.deleteConformanceStatementForComplex = function (conformanceStatement) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(conformanceStatement), 1);
    };


    $scope.isNewCS = function (id) {
        if ($rootScope.isNewObject('conformanceStatement', 'add', id)) {
            if ($rootScope.changes['conformanceStatement'] !== undefined && $rootScope.changes['conformanceStatement']['add'] !== undefined) {
                for (var i = 0; i < $rootScope.changes['conformanceStatement']['add'].length; i++) {
                    var tmp = $rootScope.changes['conformanceStatement']['add'][i];
                    if (tmp.obj.id === id) {
                        $rootScope.changes['conformanceStatement']['add'].splice(i, 1);
                        if ($rootScope.changes["conformanceStatement"]["add"] && $rootScope.changes["conformanceStatement"]["add"].length === 0) {
                            delete  $rootScope.changes["conformanceStatement"]["add"];
                        }

                        if ($rootScope.changes["conformanceStatement"] && Object.getOwnPropertyNames($rootScope.changes["conformanceStatement"]).length === 0) {
                            delete  $rootScope.changes["conformanceStatement"];
                        }
                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    };
    
    $scope.changeConstraintType = function () {
    	$scope.newConstraint = angular.fromJson({
    		datatype: '',
            component_1: null,
            subComponent_1: null,
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
		$scope.newConstraint.datatype = $rootScope.datatype.name;
		
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.genPosition = function (datatype, component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
            position = datatype + '.' + component.position;
        } else if (component != null && subComponent != null) {
            position = datatype + '.' + component.position + '.' + subComponent.position;
        }

        return position;
    };

    $scope.genLocation = function (component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
            location = component.position + '[1]';
        } else if (component != null && subComponent != null) {
            location = component.position + '[1]' + '.' + subComponent.position + '[1]';
        }

        return location;
    };
    
    $scope.addComplexConformanceStatement = function(){
    	$scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    	$scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
    	
    	$rootScope.datatype.conformanceStatements.push($scope.complexConstraint);
    	$rootScope.datatypeConformanceStatements.push($scope.complexConstraint);
        var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: $scope.complexConstraint};
        $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
        
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        
        $scope.complexConstraint = null;
        $scope.newComplexConstraintId = '';
        $scope.newComplexConstraintClassification = 'E';
    };
    
    $scope.compositeConformanceStatements = function(){
    	if($scope.compositeType === 'AND'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'AND' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<AND>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</AND>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'OR'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'OR' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<OR>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</OR>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'IFTHEN'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'IF ['+ $scope.firstConstraint.description + '] ' + 'THEN ' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<IMPLY>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</IMPLY>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}
    	
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };

    $scope.addConformanceStatement = function () {
        $rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;

        var position_1 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var position_2 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        var location_1 = $scope.genLocation($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var location_2 = $scope.genLocation($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if (position_1 != null) {
            if ($scope.newConstraint.contraintType === 'valued') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                    assertion: '<Presence Path=\"' + location_1 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'a literal value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                    assertion: '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'one of codes in ValueSet') {
                var cs = {
                        id: new ObjectId().toString(),
                        constraintId: $scope.newConstraint.constraintId,
                        constraintTarget: $scope.selectedNode.position + '[1]',
                        constraintClassification: $scope.newConstraint.constraintClassification,
                        description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                        assertion: '<ValueSet Path=\"' + location_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                    $scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'formatted value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<Format Path=\"' + location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'not-equal to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="NE" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'greater than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GT" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GE" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'less than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LT" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or less than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LE" Path2=\"' + location_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'not-equal to') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'greater than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'less than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or less than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$rootScope.datatype.conformanceStatements.push(cs);
                    $rootScope.datatypeConformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                    $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
                }else if ($scope.constraintType === 'Complex'){
                  	$scope.newComplexConstraint.push(cs);
                }
            }
        }
        
        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };
});


angular.module('igl').controller('PredicateDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
	$scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];

    $scope.newConstraint = angular.fromJson({
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
        bindingLocation: '1',
        constraintClassification: 'E'
    });
    $scope.newConstraint.datatype = $rootScope.datatype.name;
    
    $scope.initPredicate = function () {
    	$scope.newConstraint = angular.fromJson({
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
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.datatype = $rootScope.datatype.name;
    }
    
    
    $scope.deletePredicateForComplex = function (predicate) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(predicate), 1);
    };
    
    $scope.deletePredicate = function (predicate) {
        $rootScope.datatype.predicates.splice($rootScope.datatype.predicates.indexOf(predicate), 1);
        $rootScope.datatypePredicates.splice($rootScope.datatypePredicates.indexOf(predicate), 1);
        if (!$scope.isNewCP(predicate.id)) {
            $rootScope.recordChangeForEdit2('predicate', "delete", predicate.id, 'id', predicate.id);
        }
    };

    $scope.isNewCP = function (id) {
        if ($rootScope.isNewObject("predicate", "add", id)) {
            if ($rootScope.changes['predicate'] !== undefined && $rootScope.changes['predicate']['add'] !== undefined) {
                for (var i = 0; i < $rootScope.changes['predicate']['add'].length; i++) {
                    var tmp = $rootScope.changes['predicate']['add'][i];
                    if (tmp.obj.id === id) {
                        $rootScope.changes['predicate']['add'].splice(i, 1);

                        if ($rootScope.changes["predicate"]["add"] && $rootScope.changes["predicate"]["add"].length === 0) {
                            delete  $rootScope.changes["predicate"]["add"];
                        }

                        if ($rootScope.changes["predicate"] && Object.getOwnPropertyNames($rootScope.changes["predicate"]).length === 0) {
                            delete  $rootScope.changes["predicate"];
                        }

                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    };
    
    $scope.changeConstraintType = function () {
    	$scope.newConstraint = angular.fromJson({
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
            bindingLocation: '1',
            constraintClassification: 'E'
	    });
		$scope.newConstraint.datatype = $rootScope.datatype.name;
		
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.genPosition = function (datatype, component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
            position = datatype + '.' + component.position;
        } else if (component != null && subComponent != null) {
            position = datatype + '.' + component.position + '.' + subComponent.position;
        }

        return position;
    };

    $scope.genLocation = function (component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
            location = component.position + '[1]';
        } else if (component != null && subComponent != null) {
            location = component.position + '[1]' + '.' + subComponent.position + '[1]';
        }

        return location;
    };

    $scope.deletePredicateByTarget = function () {
        for (var i = 0, len1 = $rootScope.datatype.predicates.length; i < len1; i++) {
            if ($rootScope.datatype.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
                $scope.deletePredicate($rootScope.datatype.predicates[i]);
                return true;
            }
        }
        return false;
    };
    
    $scope.addComplexConformanceStatement = function(){
        $scope.deletePredicateByTarget();
        $scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
        $scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
        $rootScope.datatype.predicates.push($scope.complexConstraint);
        $rootScope.datatypePredicates.push($scope.complexConstraint);
        var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: $scope.complexConstraint};
        $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        
        $scope.complexConstraint = null;
        $scope.newComplexConstraintClassification = 'E';
        
    };
    
    $scope.compositeConformanceStatements = function(){
    	if($scope.compositeType === 'AND'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'AND' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<AND>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</AND>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'OR'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'OR' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<OR>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</OR>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'IFTHEN'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'IF ['+ $scope.firstConstraint.description + '] ' + 'THEN ' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<IMPLY>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</IMPLY>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}
    	
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };
    

    $scope.updatePredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        if ($scope.constraintType === 'Plain'){
        	$scope.deletePredicateByTarget();
        }
        
        var position_1 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var position_2 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        var location_1 = $scope.genLocation($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var location_2 = $scope.genLocation($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if (position_1 != null) {
            if ($scope.newConstraint.contraintType === 'valued') {
            	if($scope.constraintType === 'Plain'){
                	var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Presence Path=\"' + location_1 + '\"/>'
                    };
                	
                	$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
                }else if ($scope.constraintType === 'Complex'){
                	var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Presence Path=\"' + location_1 + '\"/>'
                    };
                	$scope.newComplexConstraint.push(cp);
                }
            } else if ($scope.newConstraint.contraintType === 'a literal value') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'one of codes in ValueSet') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<ValueSet Path=\"' + location_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<ValueSet Path=\"' + location_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'formatted value') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Format Path=\"' + location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Format Path=\"' + location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'not-equal to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="NE" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="NE" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'greater than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GT" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GT" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GE" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="GE" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'less than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LT" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LT" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or less than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LE" Path2=\"' + location_2 + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + position_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="LE" Path2=\"' + location_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'not-equal to') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'greater than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'less than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or less than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$rootScope.datatype.predicates.push(cp);
                    $rootScope.datatypePredicates.push(cp);
                    var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                    $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.selectedNode.position + '[1]',
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + location_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            }
        }
        
        $scope.initPredicate();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});
