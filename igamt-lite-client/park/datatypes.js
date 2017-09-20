/**
 * Created by haffo on 2/13/15.
 */
angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc, ViewSettings, DatatypeService, ComponentService, MastermapSvc, FilteringSvc, DatatypeLibrarySvc) {
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

        $scope.save = function () {
            $scope.saving = true;
            var datatype = $rootScope.datatype;
            var ext = datatype.ext;
            datatype.ext = null;
            if (datatype.libIds.indexOf($rootScope.igdocument.profile.datatypeLibrary.id) == -1) {
                datatype.libIds.push($rootScope.igdocument.profile.datatypeLibrary.id);
            }
            DatatypeService.save(datatype).then(function (result) {
                if ($rootScope.datatypesMap[result.id] === null || $rootScope.datatypesMap[result.id] == undefined) { // new datatype
                    $rootScope.datatypes.push(result);
                    $rootScope.datatypesMap[result.id] = result;
                    var newLink = DatatypeLibrarySvc.createEmptyLink();
                    newLink.id = result.id;
                    newLink.ext = ext;
                    newLink.name = datatype.name;
                    // save link to datatypeLibrary
                    DatatypeLibrarySvc.addChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function (link) {
                        $rootScope.igdocument.profile.datatypeLibrary.children.push(newLink);
                        $rootScope.$broadcast('event:SetToC');
                    }, function (error) {
                        $scope.saving = false;
                        $rootScope.msg().text = error.data.text;
                        $rootScope.msg().type = error.data.type;
                        $rootScope.msg().show = true;
                    });
                } else {
                    var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.datatypeLibrary);
                    if (oldLink != null) {
                        DatatypeService.merge($rootScope.datatypesMap[result.id], result);
                        var newLink = DatatypeService.getDatatypeLink(result);
                        newLink.ext = ext;
                        DatatypeLibrarySvc.updateChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function (link) {
                            oldLink.ext = newLink;
                            $rootScope.$broadcast('event:SetToC');
                        }, function (error) {
                            $scope.saving = false;
                            $rootScope.msg().text = error.data.text;
                            $rootScope.msg().type = error.data.type;
                            $rootScope.msg().show = true;
                        });
                    }
                }
                $scope.saving = false;
                $scope.selectedChildren = [];
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();
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
//            angular.forEach($rootScope.datatype.components, function (child) {
//                if ($scope.isChildNew(child.status)) {
//                    delete $rootScope.parentsMap[child.id];
//                }
//            });
//            $rootScope.datatype = null;
//            $scope.selectedChildren = [];
//            // revert
//        };


        $scope.reset = function () {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $rootScope.datatype = angular.copy($rootScope.datatypesMap[$rootScope.datatype.id]);
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
            var children = collection;
            for (var i = 0; i < children; i++) {
                if (children[i].id === id) {
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
                    currentNode: function () {
                        return component;
                    },
                    hl7Version: function () {
                        return $rootScope.igdocument.metaData.hl7Version;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                component.datatype.id = datatype.id;
                $rootScope.processElement(datatype);
                //TODO: load master map
                $rootScope.datatypesMap[component.datatype.id] = datatype;
                MastermapSvc.addDatatypeObject(datatype, [component.id, component.type]);
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();
            });

        };


    });


angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });

angular.module('igl')
    .controller('SelectDatatypeFlavorCtrl', function ($scope, $filter, $modalInstance, $rootScope, $http, currentNode, DatatypeService, $rootScope, hl7Version, ngTreetableParams, ViewSettings, DatatypeLibrarySvc) {
        $scope.resultsError = null;
        $scope.viewSettings = ViewSettings;
        $scope.resultsLoading = null;
        $scope.librariesLoading = false;
        $scope.librariesError = null;
        $scope.libraries = [];
        $scope.tmpLibraries = [].concat($scope.libraries);
        $scope.results = [];
        $scope.tmpResults = [].concat($scope.results);

        $scope.currentNode = currentNode;
        $scope.currentDatatype = $rootScope.datatypesMap[currentNode.datatype];
        $scope.selection = {library: null, scope: $scope.currentDatatype != null && $scope.currentDatatype ? $scope.currentDatatype.scope : null, hl7Version: hl7Version, datatype: null, name: $scope.currentDatatype != null && $scope.currentDatatype ? $scope.currentDatatype.name : null};

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
            return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype) != undefined && $rootScope.getDatatype(node.datatype).components != null && $rootScope.getDatatype(node.datatype).components.length > 0;
        };

        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
        };


        $scope.loadFlavors = function (library) {
            if (library != null) {
                $scope.selection.library = library;
                $scope.libariesError = null;
                $scope.librariesLoading = true;
                $scope.results = [];
                $scope.tmpResults = [];
                var ids = [];
                _.each(library.children, function (igd) {
                    ids.push(link.id);
                });
                DatatypeLibrarySvc.getDatatypes(ids).then(function (datatypes) {
                    $scope.resultsLoading = false;
                    $scope.results = datatypes;
                    $scope.tmpResults = [].concat($scope.results);
                }, function (error) {
                    $scope.resultsLoading = false;
                    $scope.resultsError = error;
                });
            }
        };

        $scope.loadLibrariesByFlavorName = function () {
            $scope.librariesError = null;
            $scope.librariesLoading = true;
            $scope.libraries = [];
            $scope.tmpLibraries = [].concat($scope.libraries);
            $scope.results = [];
            $scope.tmpResults = [];
            $scope.added = [];
            DatatypeLibrarySvc.findLibrariesByFlavorName($scope.selection.name, $scope.selection.scope, $scope.selection.hl7Version).then(function (libraries) {
                $scope.libraries = libraries;
                $scope.tmpLibraries = [].concat($scope.libraries);
                $scope.librariesLoading = false;
            }, function (error) {
                $scope.librariesError = null;
                $scope.librariesLoading = false;
            });
        };

        $scope.isDatatypeSubDT = function (component) {
            return DatatypeService.isDatatypeSubDT(component, $scope.selection.datatype);
        };

        $scope.isSelectedDatatype = function (datatype) {
            return  $scope.selection.datatype != null && datatype != null && $scope.selection.datatype.id == datatype.id;
        };

        $scope.isSelectedLibrary = function (library) {
            return  $scope.selection.library != null && library != null && $scope.selection.library.id == library.id;
        };

        $scope.getDatatypeLabel = function (datatypeLink) {
            return $rootScope.getLabel(datatypeLink.name, datatypeLink.ext);
        };

        $scope.showDatatype = function (datatype) {
            if (datatype && datatype != null) {
                $scope.loadingSelection = true;
                $scope.added = [];
                $scope.bindingError = null;
                DatatypeService.collectDatatypes(datatype.id).then(function (datatypes) {
                    $scope.bindingError = null;
                    angular.forEach(datatypes, function (child) {
                        if ($rootScope.datatypesMap[id] === null || $rootScope.datatypesMap[id] === undefined) {
                            $rootScope.datatypesMap[child.id] = child;
                            $scope.added.push(child.id);
                        }
                    });
                    $scope.selection.datatype = datatype;
                    $scope.selection.datatype["type"] = "datatype";
                    $rootScope.tableWidth = null;
                    $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                    $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 890);
                    $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 890);
                    $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 890);
                    $scope.loadingSelection = false;
                    if ($scope.datatypeFlavorParams)
                        $scope.datatypeFlavorParams.refresh();
                }, function (error) {
                    $scope.loadingSelection = false;
                    $scope.bindingError = error;
                });


            }
        };

        $scope.getLocalDatatypeLabel = function (datatype) {
            return $scope.selection.library != null && datatype != null ? $rootScope.getExtensionInLibrary(datatype.id, $scope.selection.library, "ext") :null;
        };


        $scope.submit = function () {
            $modalInstance.close($scope.selection.datatype);
        };
        $scope.cancel = function () {
            if( $scope.added = null) {
                angular.forEach( $scope.added, function (child) {
                    delete $rootScope.datatypesMap[child];
                });
            }
            $modalInstance.dismiss('cancel');
        };
    });


angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function ($scope, $modalInstance, dtToDelete, $rootScope, DatatypeLibrarySvc, DatatypeService) {
    $scope.dtToDelete = dtToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        DatatypeService.delete($scope.dtToDelete).then(function (result) {
                DatatypeLibrarySvc.deleteChild($scope.dtToDelete.id).then(function (res) {
                    // We must delete from two collections.
                    var index = $rootScope.datatypes.indexOf($scope.dtToDelete);
                    $rootScope.datatypes.splice(index, 1);
                    var tmp = DatatypeLibrarySvc.findOneChild($scope.dtToDelete.id, $rootScope.igdocument.profile.datatypeLibrary);
                    index = $rootScope.igdocument.profile.datatypeLibrary.children.indexOf(tmp);
                    $rootScope.igdocument.profile.datatypeLibrary.children.splice(index, 1);
                    $rootScope.datatypesMap[$scope.dtToDelete.id] = null;
                    $rootScope.references = [];
                    if ($rootScope.datatype === $scope.dtToDelete) {
                        $rootScope.datatype = null;
                    }
                    $rootScope.recordDelete("datatype", "edit", $scope.dtToDelete.id);
                    $rootScope.msg().text = "dtDeleteSuccess";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;
                    $rootScope.manualHandle = true;
                    $scope.loading = false;
                    $rootScope.$broadcast('event:SetToC');
                    $modalInstance.close($scope.dtToDelete);
                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    $rootScope.manualHandle = true;
                    $scope.loading = false;
                });
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                $rootScope.manualHandle = true;
                $scope.loading = false;
            }
        );
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
            value2: null,
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
            Position = component.position + '[1]' + '.' + subComponent.position + '[1]';
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
            var positionPath = $scope.selectedNode.position + '[1]';
            var cs = $rootScope.generateConformanceStatement(positionPath, $scope.newConstraint);
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
