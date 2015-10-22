/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout) {
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.datatypeCopy = null;
        $scope.init = function () {
        };

        $scope.flavor = function (datatype) {
            var flavor = angular.copy(datatype);
            flavor.id = new ObjectId().toString();
            flavor.label = $rootScope.createNewFlavorName(datatype.label);
            if (flavor.components != undefined && flavor.components != null && flavor.components.length != 0) {
                for (var i = 0; i < flavor.components.length; i++) {
                    flavor.components[i].id = new ObjectId().toString();
                }
            }
            var predicates = flavor['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function (predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }
            var conformanceStatements = flavor['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function (conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }
            $rootScope.datatypes.splice(0, 0, flavor);
            $rootScope.datatype = flavor;
            $rootScope.datatypesMap[flavor.id] = flavor;
            $rootScope.recordChangeForEdit2('datatype', "add", flavor.id, 'datatype', flavor);
            $scope.selectDatatype(flavor);
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
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                $rootScope.findDatatypeRefs(datatype, segment);
            });
            if ($rootScope.references != null && $rootScope.references.length > 0) {
                $scope.abortDelete(datatype);
            } else {
                $scope.confirmDelete(datatype);
            }
        };

        $scope.abortDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'DatatypeReferencesCtrl.html',
                controller: 'DatatypeReferencesCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
            });
        };

        $scope.confirmDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeDeleteCtrl.html',
                controller: 'ConfirmDatatypeDeleteCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
            });
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

    $scope.newConstraint = angular.fromJson({
        datatype: '',
        component_1: null,
        subComponent_1: null,
        component_2: null,
        subComponent_2: null,
        verb: null,
        constraintId: null,
        contraintType: null,
        value: null
    });
    $scope.newConstraint.datatype = $rootScope.datatype.name;

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $rootScope.datatype.conformanceStatements.splice($rootScope.datatype.conformanceStatements.indexOf(conformanceStatement), 1);
        $rootScope.datatypeConformanceStatements.splice($rootScope.datatypeConformanceStatements.indexOf(conformanceStatement), 1);
        if (!$scope.isNewCS(conformanceStatement.id)) {
            $rootScope.recordChangeForEdit2('conformanceStatement', "delete", conformanceStatement.id, 'id', conformanceStatement.id);
        }
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
                    description: position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                    assertion: '<Presence Path=\"' + location_1 + '\"/>'
                };
                $rootScope.datatype.conformanceStatements.push(cs);
                $rootScope.datatypeConformanceStatements.push(cs);
                var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
            } else if ($scope.newConstraint.contraintType === 'a literal value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                };
                $rootScope.datatype.conformanceStatements.push(cs);
                $rootScope.datatypeConformanceStatements.push(cs);
                var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                    assertion: '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                };
                $rootScope.datatype.conformanceStatements.push(cs);
                $rootScope.datatypeConformanceStatements.push(cs);
                var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
            } else if ($scope.newConstraint.contraintType === 'formatted value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<Format Path=\"' + location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                };
                $rootScope.datatype.conformanceStatements.push(cs);
                $rootScope.datatypeConformanceStatements.push(cs);
                var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'The value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                };
                $rootScope.datatype.conformanceStatements.push(cs);
                $rootScope.datatypeConformanceStatements.push(cs);
                var newCSBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cs};
                $rootScope.recordChangeForEdit2('conformanceStatement', "add", null, 'conformanceStatement', newCSBlock);
            }
        }
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };
});


angular.module('igl').controller('PredicateDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;

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
        falseUsage: null
    });
    $scope.newConstraint.datatype = $rootScope.datatype.name;

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

    $scope.updatePredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        $scope.deletePredicateByTarget();

        var position_1 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var position_2 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        var location_1 = $scope.genLocation($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        var location_2 = $scope.genLocation($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if (position_1 != null) {
            if ($scope.newConstraint.contraintType === 'valued') {
                var cp = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'If ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                    trueUsage: $scope.newConstraint.trueUsage,
                    falseUsage: $scope.newConstraint.falseUsage,
                    assertion: '<Presence Path=\"' + location_1 + '\"/>'
                };
                $rootScope.datatype.predicates.push(cp);
                $rootScope.datatypePredicates.push(cp);
                $rootScope.datatypePredicates.push({position: $scope.newConstraint.datatype + '.' + cp.constraintTarget, cp: cp});
                var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            } else if ($scope.newConstraint.contraintType === 'a literal value') {
                var cp = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                    trueUsage: $scope.newConstraint.trueUsage,
                    falseUsage: $scope.newConstraint.falseUsage,
                    assertion: '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                };
                $rootScope.datatype.predicates.push(cp);
                $rootScope.datatypePredicates.push(cp);
                var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
                var cp = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                    trueUsage: $scope.newConstraint.trueUsage,
                    falseUsage: $scope.newConstraint.falseUsage,
                    assertion: '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                };
                $rootScope.datatype.predicates.push(cp);
                $rootScope.datatypePredicates.push(cp);
                var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            } else if ($scope.newConstraint.contraintType === 'formatted value') {
                var cp = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                    trueUsage: $scope.newConstraint.trueUsage,
                    falseUsage: $scope.newConstraint.falseUsage,
                    assertion: '<Format Path=\"' + location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                };
                $rootScope.datatype.predicates.push(cp);
                $rootScope.datatypePredicates.push(cp);
                var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
                var cp = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.datatype + '-' + $scope.selectedNode.position,
                    constraintTarget: $scope.selectedNode.position + '[1]',
                    description: 'If the value of ' + position_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
                    trueUsage: $scope.newConstraint.trueUsage,
                    falseUsage: $scope.newConstraint.falseUsage,
                    assertion: '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
                };
                $rootScope.datatype.predicates.push(cp);
                $rootScope.datatypePredicates.push(cp);
                var newCPBlock = {targetType: 'datatype', targetId: $rootScope.datatype.id, obj: cp};
                $rootScope.recordChangeForEdit2('predicate', "add", null, 'predicate', newCPBlock);
            }
        }
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

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});
