/**
 * Created by haffo on 2/13/15.
 */
angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc, ViewSettings, DatatypeService, ComponentService, MastermapSvc, FilteringSvc, DatatypeLibrarySvc, TableLibrarySvc) {
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.datatypeCopy = null;
        $scope.viewSettings = ViewSettings;
        $scope.selectedChildren = [];
        $scope.saving = false;
        $scope.init = function () {
        };

        $scope.copy = function (datatype) {
            CloneDeleteSvc.copyDatatype(datatype);
        };

        $scope.reset = function () {
            DatatypeService.reset();
            cleanState();
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
            return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype.id) != undefined && $rootScope.getDatatype(node.datatype.id).components != null && $rootScope.getDatatype(node.datatype.id).components.length > 0;
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
                $scope.setDirty();
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
                $scope.setDirty();
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
                $scope.setDirty();
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
            for (var i = 0; i < $rootScope.datatype.components.length; i++) {
                var component = $rootScope.datatype.components[i];
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
            return $rootScope.datatype && $rootScope.datatype != null && $rootScope.datatype.components && $scope.selectedChildren.length === $rootScope.datatype.components.length;
        };


        /**
         * TODO: update master map
         */
        $scope.createNewComponent = function () {
            if ($rootScope.datatype != null) {
                if (!$rootScope.datatype.components || $rootScope.datatype.components === null)
                    $rootScope.datatype.components = [];
                var child = ComponentService.create($rootScope.datatype.components.length + 1);
                $rootScope.datatype.components.push(child);
                //TODO update master map
                //MastermapSvc.addDatatypeObject($rootScope.datatype, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
                //TODO:remove as legacy code
                $rootScope.parentsMap[child.id] = $rootScope.datatype;
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();
            }
        };

        /**
         * TODO: update master map
         */
        $scope.deleteComponents = function () {
            if ($rootScope.datatype != null && $scope.selectedChildren != null && $scope.selectedChildren.length > 0) {
                ComponentService.deleteList($scope.selectedChildren, $rootScope.datatype);
                //TODO update master map
                //TODO:remove as legacy code
                angular.forEach($scope.selectedChildren, function (child) {
                    delete $rootScope.parentsMap[child.id];
                });
                $scope.selectedChildren = [];
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();
            }
        };


        var cleanState = function () {
            $scope.selectedChildren = [];
            $rootScope.addedDatatypes = [];
            $rootScope.addedTables = [];
            if ($scope.editForm) {
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
            }
            $rootScope.clearChanges();
            if ($scope.datatypesParams)
                $scope.datatypesParams.refresh();
        };

        $scope.save = function () {
            var datatype = $rootScope.datatype;
            var ext = datatype.ext;
            if (datatype.libIds == undefined) datatype.libIds = [];
            if (datatype.libIds.indexOf($rootScope.igdocument.profile.datatypeLibrary.id) == -1) {
                datatype.libIds.push($rootScope.igdocument.profile.datatypeLibrary.id);
            }
            DatatypeService.save(datatype).then(function (result) {
                var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.datatypeLibrary);
                var newLink = DatatypeService.getDatatypeLink(result);
                newLink.ext = ext;
                DatatypeLibrarySvc.updateChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function (link) {
                    DatatypeService.saveNewElements().then(function () {
                        DatatypeService.merge($rootScope.datatypesMap[result.id], result);
                        oldLink.ext = newLink.ext;
                        oldLink.name = newLink.name;
                        $scope.saving = false;
                        cleanState();
                    }, function (error) {
                        $scope.saving = false;
                        $rootScope.msg().text = "Sorry an error occured. Please try again";
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                    });
                }, function (error) {
                    $scope.saving = false;
                    $rootScope.msg().text = "Sorry an error occured. Please try again";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                });

            }, function (error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

        $scope.cancel = function () {
            //TODO: remove changes from master ma
            angular.forEach($rootScope.datatype.components, function (child) {
                if ($scope.isChildNew(child.status)) {
                    delete $rootScope.parentsMap[child.id];
                }
            });
            $rootScope.datatype = null;
            $scope.selectedChildren = [];
            $rootScope.clearChanges();
        };

        var searchById = function (id) {
            var children = $rootScope.igdocument.profile.datatypeLibrary.children;
            for (var i = 0; i < $rootScope.igdocument.profile.datatypeLibrary.children; i++) {
                if (children[i].id === id) {
                    return children[i];
                }
            }
            return null;
        };

        var indexIn = function (id, collection) {
            for (var i = 0; i < collection.length; i++) {
                if (collection[i].id === id) {
                    return i;
                }
            }
            return -1;
        };


        $scope.showSelectDatatypeFlavorDlg = function (component) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectDatatypeFlavor.html',
                controller: 'SelectDatatypeFlavorCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    currentDatatype: function () {
                        return $rootScope.datatypesMap[component.datatype.id];
                    },
                    hl7Version: function () {
                        return $rootScope.igdocument.profile.metaData.hl7Version;
                    },
                    datatypeLibrary: function () {
                        return $rootScope.igdocument.profile.datatypeLibrary;
                    }
                }
            });
            modalInstance.result.then(function (datatype, ext) {
//                MastermapSvc.deleteElementChildren(component.datatype.id, "datatype", component.id, component.type);
//                MastermapSvc.addDatatypeObject(datatype, [[component.id, component.type]]);
                component.datatype.id = datatype.id;
                component.datatype.name = datatype.name;
                component.datatype.ext = datatype.ext;
                $rootScope.processElement(component);
                $scope.setDirty();
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();

            });

        };

//        $scope.$watch(function(){
//            return $rootScope.datatype;
//        }, function() {
//            $rootScope.recordChanged();
//        }, true);


    });


angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });

angular.module('igl')
    .controller('SelectDatatypeFlavorCtrl', function ($scope, $filter, $modalInstance, $rootScope, $http, currentDatatype, DatatypeService, $rootScope, hl7Version, ngTreetableParams, ViewSettings, DatatypeLibrarySvc, $q, datatypeLibrary, TableService) {
        $scope.resultsError = null;
        $scope.viewSettings = ViewSettings;
        $scope.resultsLoading = null;
        $scope.results = [];
        $scope.tmpResults = [].concat($scope.results);
        $scope.datatypeLibrary = datatypeLibrary;

        $scope.currentDatatype = angular.copy(currentDatatype);
        $scope.selection = {library: null, scope: null, hl7Version: hl7Version, datatype: null, name: $scope.currentDatatype != null && $scope.currentDatatype ? $scope.currentDatatype.name : null, selected: null};
        $scope.dataypesMap = {};
        $scope.datatypeFlavorParams = new ngTreetableParams({
            getNodes: function (parent) {
                return DatatypeService.getNodes(parent, $scope.selection.datatype);
            },
            getTemplate: function (node) {
                return DatatypeService.getReadTemplate(node, $scope.selection.datatype);
            }
        });

        $scope.isRelevant = function (node) {
            var rel = DatatypeService.isRelevant(node);
            return rel;
        };

        $scope.isBranch = function (node) {
            var isBran = DatatypeService.isBranch(node);
            return isBran;
        };


        $scope.isVisible = function (node) {
            var isVis = DatatypeService.isVisible(node);
            return isVis;
        };

        $scope.children = function (node) {
            var chil = DatatypeService.getNodes(node);
            return chil;
        };

        $scope.getParent = function (node) {
            var par = DatatypeService.getParent(node);
            return par;
        };

        $scope.isChildSelected = function (component) {
            return  $scope.selectedChildren.indexOf(component) >= 0;
        };

        $scope.isChildNew = function (component) {
            return component && component != null && component.status === 'DRAFT';
        };


        $scope.hasChildren = function (node) {
            return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype.id) != undefined && $rootScope.getDatatype(node.datatype.id).components != null && $rootScope.getDatatype(node.datatype.id).components.length > 0;
        };

        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
        };

        $scope.loadLibrariesByFlavorName = function () {
            var delay = $q.defer();
            $scope.selection.datatype = null;
            $scope.selection.selected = null;
            $scope.ext = null;
            $scope.results = [];
            $scope.tmpResults = [];
            $scope.results = $scope.results.concat(filterFlavors(datatypeLibrary, $scope.selection.name));
            $scope.tmpResults = [].concat($scope.results);
            DatatypeLibrarySvc.findLibrariesByFlavorName($scope.selection.name, 'HL7STANDARD', $scope.selection.hl7Version).then(function (libraries) {
                if (libraries != null) {
                    _.each(libraries, function (library) {
                        $scope.results = $scope.results.concat(filterFlavors(library, $scope.selection.name));
                    });
                }

                $scope.results = _.uniq($scope.results, function (item, key, a) {
                    return item.id;
                });
                $scope.tmpResults = [].concat($scope.results);

                delay.resolve(true);
            }, function (error) {
                $rootScope.msg().text = "Sorry could not load the data types";
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
                delay.reject(error);
            });
            return delay.promise;
        };

        var filterFlavors = function (library, name) {
            var results = [];
            _.each(library.children, function (link) {
                if (link.name === name) {
                    link.libraryName = library.metaData.name;
                    link.hl7Version = library.metaData.hl7Version;
                    results.push(link);
                }
            });
            return results;
        };

        $scope.isDatatypeSubDT = function (component) {
            return DatatypeService.isDatatypeSubDT(component, $scope.selection.datatype);
        };

        $scope.isSelectedDatatype = function (datatype) {
            return  $scope.selection.selected != null && datatype != null && $scope.selection.selected == datatype.id;
        };

        $scope.isSelectedLibrary = function (library) {
            return  $scope.selection.library != null && library != null && $scope.selection.library.id == library.id;
        };

        $scope.showSelectedDetails = function (datatype) {
            if (datatype && datatype != null) {
                $scope.selection.datatype = datatype;
                $scope.selection.datatype["type"] = "datatype";
            }
        };

        var indexIn = function (id, collection) {
            for (var i = 0; i < collection.length; i++) {
                if (collection[i].id === id) {
                    return i;
                }
            }
            return -1;
        };


        var collectNewDatatypesAndTables = function (root, datatypes) {
            $rootScope.datatypesMap[root.id] = root;
            if (indexIn(root.id, $rootScope.addedDatatypes) < 0) {
                $rootScope.addedDatatypes.push(root);
            }
            var tmpTables = [];
            angular.forEach(datatypes, function (child) {
                $rootScope.datatypesMap[child.id] = child;
                if (indexIn(child.id, $rootScope.addedDatatypes) < 0) {
                    $rootScope.addedDatatypes.push(child);
                }

                if (indexIn(child.table.id, $rootScope.addedTables) < 0) {
                    tmpTables.push(child.table.id);
                }
            });

            if (tmpTables.length > 0) {
                TableService.findAllByIds(tmpTables).then(function (tables) {
                    $rootScope.addedTables = $rootScope.addedTables.concat(tables);
                    angular.forEach(tables, function (table) {
                        $rootScope.tablesMap[table.id] = table;
                    });
                    $modalInstance.close($scope.selection.datatype);
                }, function (error) {
                    $rootScope.msg().text = "Sorry an error occured. Please try again";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                });
            } else {
                $modalInstance.close($scope.selection.datatype);
            }
        };


        $scope.submit = function () {
            var indexFromLibrary = indexIn($scope.selection.datatype.id, $scope.datatypeLibrary.children);
            var indexFromCollection = indexIn($scope.selection.datatype.id, $rootScope.datatypes);
            var indexFromMap = $rootScope.datatypesMap[$scope.selection.datatype.id] != undefined && $rootScope.datatypesMap[$scope.selection.datatype.id] != null ? 100 : -1;

            if (indexFromLibrary < 0 | indexFromCollection < 0 | indexFromMap < 0) {
                DatatypeService.getOne($scope.selection.datatype.id).then(function (full) {
                    DatatypeService.collectDatatypes(full.id).then(function (datatypes) {
                        $scope.ext = full.ext;
                        $scope.selection.datatype = full;
                        $scope.selection.datatype["type"] = "datatype";
                        collectNewDatatypesAndTables($scope.selection.datatype, datatypes);
                    }, function (error) {
                        $scope.loadingSelection = false;
                        $rootScope.msg().text = "Sorry could not load the data type";
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                    });
                }, function (error) {
                    $scope.resultsLoading = false;
                    $rootScope.msg().text = "Sorry could not load the data type";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                });
            } else {
                $modalInstance.close($scope.selection.datatype);
            }
        };


        $scope.cancel = function () {
            $scope.resetMap();
            $modalInstance.dismiss('cancel');
        };

        $scope.getLocalDatatypeLabel = function (link) {
            return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
        };


        $scope.resetMap = function () {
            if ($rootScope.addedDatatypes = null) {
                angular.forEach($rootScope.addedDatatypes, function (child) {
                    var dt = $rootScope.datatypesMap[child];
                    if (dt.id !== $scope.currentDatatype.id) {
                        delete $rootScope.datatypesMap[child];
                    }
                });
            }
        };

        $scope.loadLibrariesByFlavorName().then(function (done) {
            $scope.selection.selected = $scope.currentDatatype.id;
            $scope.showSelectedDetails($scope.currentDatatype);
        });

    }
);


angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function ($scope, $modalInstance, dtToDelete, $rootScope, DatatypeLibrarySvc, DatatypeService, MastermapSvc, CloneDeleteSvc) {
    $scope.dtToDelete = dtToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        if ($scope.dtToDelete.scope === 'USER') {
            CloneDeleteSvc.deleteDatatypeAndDatatypeLink($scope.dtToDelete);
        } else {
            CloneDeleteSvc.deleteDatatypeLink($scope.dtToDelete);
        }

        $modalInstance.close($scope.dtToDelete);
        $scope.loading = false;
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
})
;


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
        $scope.selectedTable = $rootScope.tablesMap[selectedNode.table.id];
    }

    $scope.selectTable = function (table) {
        $scope.changed = true;
        $scope.selectedTable = table;
    };

    $scope.mappingTable = function () {
        if($scope.selectedNode.table == null || $scope.selectedNode.table == undefined) $scope.selectedNode.table = {};

        $scope.selectedNode.table.id = $scope.selectedTable.id;
        $scope.selectedNode.table.bindingIdentifier = $scope.selectedTable.bindingIdentifier;
        $rootScope.recordChanged();
        $scope.ok();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});

angular.module('igl').controller('ConformanceStatementDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
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
            value2: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });
        $scope.newConstraint.datatype = $rootScope.datatype.name;
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

    $scope.updateComponent_1 = function () {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function () {
        $scope.newConstraint.subComponent_2 = null;
    };

    $scope.genLocation = function (datatype, component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
            location = datatype + '.' + component.position + "(" + component.name + ")";
        } else if (component != null && subComponent != null) {
            location = datatype + '.' + component.position + '.' + subComponent.position + "(" + subComponent.name + ")";
        }

        return location;
    };

    $scope.genPosition = function (component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
            position = component.position + '[1]';
        } else if (component != null && subComponent != null) {
            position = component.position + '[1]' + '.' + subComponent.position + '[1]';
        }

        return position;
    };

    $scope.addComplexConformanceStatement = function () {
        $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
        if ($rootScope.conformanceStatementIdList.indexOf($scope.complexConstraint.constraintId) == -1) $rootScope.conformanceStatementIdList.push($scope.complexConstraint.constraintId);
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
            var cs = null;
            if ($scope.selectedNode === null) {
                var cs = $rootScope.generateConformanceStatement(".", $scope.newConstraint);
            } else {
                var cs = $rootScope.generateConformanceStatement($scope.selectedNode.position + '[1]', $scope.newConstraint);
            }
            $scope.tempComformanceStatements.push(cs);
            $scope.changed = true;
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        }
        $scope.initConformanceStatement();


    };

    $scope.ok = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            $rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf(cs.constraintId), 1);
        });

        angular.forEach($rootScope.datatype.conformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });

        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });
        angular.copy($scope.tempComformanceStatements, $rootScope.datatype.conformanceStatements);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
});


angular.module('igl').controller('PredicateDatatypeCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
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
            value2: null,
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
            location = datatype + '.' + component.position + "(" + component.name + ")";
        } else if (component != null && subComponent != null) {
            location = datatype + '.' + component.position + '.' + subComponent.position + "(" + subComponent.name + ")";
        }

        return location;
    };

    $scope.genPosition = function (component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
            position = component.position + '[1]';
        } else if (component != null && subComponent != null) {
            position = component.position + '[1]' + '.' + subComponent.position + '[1]';
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

    $scope.addComplexPredicate = function () {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;

        if ($scope.selectedNode === null) {
            $scope.complexConstraint.constraintId = '.';
        } else {
            $scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
        }

        $scope.tempPredicates.push($scope.complexConstraint);
        $scope.initComplexPredicate();
        $scope.changed = true;
    };

    $scope.addPredicate = function () {

        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
            var cp = null;
            if ($scope.selectedNode === null) {
                var cp = $rootScope.generatePredicate(".", $scope.newConstraint);
            } else {
                var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
            }

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
