/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl')
    .controller('MessageListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc, MastermapSvc, FilteringSvc, MessageService) {

        $scope.init = function () {
        };

        $scope.copy = function (message) {
            CloneDeleteSvc.copyMessage(message);
            $rootScope.$broadcast('event:SetToC');
        };

        $scope.reset = function () {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
            $rootScope.message = angular.copy($rootScope.messagesMap[$rootScope.message.id]);
            $rootScope.clearChanges();
            if ($scope.messagesParams) {
                $scope.messagesParams.refresh();
            }
        };


        var findIndex = function (id) {
            for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
                if ($rootScope.igdocument.profile.messages.children[i].id === i) {
                    return i;
                }
            }
            return -1;
        };

        $scope.save = function () {
            $scope.saving = true;
            var message = $rootScope.message;
            MessageService.save(message).then(function (result) {
                $rootScope.processElement(message);
                var index = findIndex(message.id);
                if (index < 0) {
                    $rootScope.igdocument.profile.messages.children.splice(0, 0, message);
//                    FilteringSvc.addMsgInFilter(message.name, message.id);
                }
                MastermapSvc.addMessage(message, []);
                $rootScope.$broadcast('event:SetToC');
                $rootScope.message = angular.copy(message);
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };


        $scope.delete = function (message) {
            CloneDeleteSvc.deleteMessage(message);
            $rootScope.$broadcast('event:SetToC');
        };

        $scope.goToSegment = function (segmentId) {
            $scope.$emit('event:openSegment', $rootScope.segmentsMap[segmentId]);
        };

        $scope.showSelectSegmentFlavorDlg = function (segmentRef) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectSegmentFlavor.html',
                controller: 'SelectSegmentFlavorCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    currentSegment: function () {
                        return $rootScope.segmentsMap[segmentRef.ref.id];
                    },
                    datatypeLibrary: function () {
                        return $rootScope.igdocument.profile.datatypeLibrary;
                    },
                    segmentLibrary: function () {
                        return $rootScope.igdocument.profile.segmentLibrary;
                    },
                    hl7Version: function () {
                        return $rootScope.igdocument.metaData.hl7Version;
                    }
                }
            });
            modalInstance.result.then(function (segment) {
                segmentRef.ref.id = segment.id;
                segmentRef.ref.ext = segment.ext;
                segmentRef.ref.name = segment.name;
                MastermapSvc.addSegmentObject(segment, [segmentRef.id, segmentRef.type]);
                if ($scope.messagesParams)
                    $scope.messagesParams.refresh();
            });
        };

        $scope.goToDatatype = function (datatype) {
            $scope.$emit('event:openDatatype', datatype);
        };

        $scope.goToTable = function (table) {
            $scope.$emit('event:openTable', table);
        };

        $scope.hasChildren = function (node) {
            if (node && node != null) {
                if (node.type === 'group') {
                    return node.children && node.children.length > 0;
                } else if (node.type === 'segmentRef') {
                    return $rootScope.segmentsMap[node.ref.id].fields && $rootScope.segmentsMap[node.ref.id].fields.length > 0;
                } else if (node.type === 'field' || node.type === 'component') {
                    return $rootScope.datatypesMap[node.datatype.id].components && $rootScope.datatypesMap[node.datatype.id].components.length > 0;
                }
                return false;
            } else {
                return false;
            }

        };

        $scope.isSub = function (component) {
            return $scope.isSubDT(component);
        };

        $scope.isSubDT = function (component) {
            return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
        };

        $scope.manageConformanceStatement = function (node, message) {
            var modalInstance = $modal.open({
                templateUrl: 'ConformanceStatementMessageCtrl.html',
                controller: 'ConformanceStatementMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedMessage: function () {
                        return message;
                    },
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

        $scope.managePredicate = function (node, message) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateMessageCtrl.html',
                controller: 'PredicateMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedMessage: function () {
                        return message;
                    },
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

        $scope.countPredicate = function (position) {
            if ($rootScope.message != null) {
                for (var i = 0, len1 = $rootScope.message.predicates.length; i < len1; i++) {
                    if ($rootScope.message.predicates[i].constraintTarget.indexOf(position) === 0)
                        return 1;
                }
            }
            return 0;
        };

        $scope.isVisible = function (node) {
            if (node && node != null) {
//                return FilteringSvc.show(node);
            	return true;
            } else {
                return true;
            }
        };

        $scope.isVisibleInner = function (node, nodeParent) {
            if (node && node != null && nodeParent && nodeParent != null) {
//                return FilteringSvc.showInnerHtml(node, nodeParent);
            	return true;
            } else {
                return true;
            }
        };

//        $scope.$watch(function(){
//            return $rootScope.message;
//        }, function(newValue, oldValue) {
//            $scope.editForm.$dirty = newValue !=null &&  oldValue != null;
//        });

    });


angular.module('igl')
    .controller('MessageRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();


//        $scope.init = function(){
//            $scope.$watch(function(){
//            return  $scope.formName.$dirty;
//        }, function(newValue, oldValue) {
//            $scope.editForm.$dirty = newValue !=null &&  oldValue != null;
//        });
//
//        }

    });


angular.module('igl')
    .controller('SelectSegmentFlavorCtrl', function ($scope, $filter,$q, $modalInstance, $rootScope, $http, segmentLibrary, SegmentService, $rootScope, hl7Version, ngTreetableParams, ViewSettings, SegmentLibrarySvc, datatypeLibrary, DatatypeLibrarySvc,currentSegment) {
        $scope.segmentLibrary = segmentLibrary;
        $scope.datatypeLibrary = datatypeLibrary;
        $scope.resultsError = null;
        $scope.viewSettings = ViewSettings;
        $scope.resultsLoading = null;
        $scope.results = [];
        $scope.tmpResults = [].concat($scope.results);
        $scope.currentSegment = currentSegment;
        $scope.selection = {library: null, scope: null, hl7Version: hl7Version, segment: null, name: $scope.currentSegment != null && $scope.currentSegment ? $scope.currentSegment.name : null, selected: null};


        $scope.segmentFlavorParams = new ngTreetableParams({
            getNodes: function (parent) {
                return SegmentService.getNodes(parent, $scope.selection.segment);
            },
            getTemplate: function (node) {
                return SegmentService.getReadTemplate(node, $scope.selection.segment);
            }
        });

        $scope.loadLibrariesByFlavorName = function (scope) {
            var delay = $q.defer();
            $scope.selection.scope = scope;
            $scope.selection.segment = null;
            $scope.selection.selected = null;
            $scope.resetMap();
            $scope.ext = null;
            $scope.results = [];
            $scope.tmpResults = [];
            if ($scope.selection.scope !== 'USER') {
                SegmentLibrarySvc.findLibrariesByFlavorName($scope.selection.name, $scope.selection.scope, $scope.selection.hl7Version).then(function (libraries) {
                    if (libraries != null) {
                        $scope.results = [];
                        _.each(libraries, function (library) {
                            $scope.results = $scope.results.concat(filterFlavors(library, $scope.selection.name));
                        });
                        $scope.tmpResults = [].concat($scope.results);

                    }
                    delay.resolve(true);
                }, function (error) {
                    $rootScope.msg().text = "Sorry could not load the data types";
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                    delay.reject(error);
                });
            } else {
                $scope.results = $scope.results.concat(filterFlavors(segmentLibrary, $scope.selection.name));
                delay.resolve(true);
            }
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


        $scope.showSelectedDetails = function (segment) {
            if (segment && segment != null) {
                $scope.loadingSelection = true;
                $scope.selection.segment = null;
                $scope.resetMap();
                $scope.bindingError = null;
                $scope.added = [];
                $scope.ext = null;
                SegmentService.get(segment.id).then(function (full) {
                    SegmentService.collectDatatypes(full.id).then(function (datatypes) {
                        angular.forEach(datatypes, function (child) {
                            if ($rootScope.datatypesMap[child.id] === null || $rootScope.datatypesMap[child.id] === undefined) {
                                $rootScope.datatypesMap[child.id] = child;
                                $scope.added.push(child.id);
                            }
                        });
                        $rootScope.processElement(full);
                        $scope.ext = segment.ext;
                        $scope.selection.segment = full;
                        $scope.selection.segment["type"] = "segment";
                        $rootScope.tableWidth = null;
                        $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                        $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 990);
                        $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 990);
                        $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 990);
                        $scope.loadingSelection = false;
                        if ($scope.segmentFlavorParams)
                            $scope.segmentFlavorParams.refresh();
                    }, function (error) {
                        $scope.loadingSelection = false;
                        $rootScope.msg().text = "Sorry could not load the data type";
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                        $scope.selection.segment = null;
                    });
                }, function (error) {
                    $scope.resultsLoading = false;
                    $rootScope.msg().text = "Sorry could not load the data type";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    $scope.selection.segment = null;
                });
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

        var addDatatypes = function () {
            _.each($scope.added, function (datatype) {
                if (indexIn(datatype.id, $rootScope.datatypes) < 0) {
                    $rootScope.datatypes.push(datatype);
                    $rootScope.datatypesMap[datatype.id] = datatype;
                }
            });
        };

        var getNewDatatypeLinks = function () {
            var links = [];
            _.each($scope.added, function (datatype) {
                if (indexIn(datatype.id,  $scope.datatypeLibrary.children) < 0) {
                    var link = getNewLink(datatype);
                    links.push(link);
                }
            });
            return links;
        };

        var getNewLink = function (obj) {
            var link = {};
            link['id'] = obj.id;
            link['name'] = obj.name;
            link['ext'] = obj.ext;
            return link;
        };


        $scope.submit = function () {
            var index = indexIn($scope.selection.segment.id, $scope.segmentLibrary.children);
            if (index < 0) {
                var link = getNewLink($scope.selection.segment);
                SegmentLibrarySvc.addChild($scope.segmentLibrary.id, link).then(function () {
                    $scope.segmentLibrary.children.push(link);
                    $rootScope.segmentsMap[link.id] = $scope.selection.segment;
                    if (indexIn($scope.selection.segment.id, $rootScope.segments) < 0) {
                        $rootScope.segments.push($scope.selection.segment);
                    }
                    if ($scope.added === null)
                        $scope.added = [];
                    var links = getNewDatatypeLinks();
                    if (links.length > 0) {
                        DatatypeLibrarySvc.addChildren($scope.datatypeLibrary.id, links).then(function () {
                            $scope.datatypeLibrary.children = $scope.datatypeLibrary.children.concat(links);
                            addDatatypes();
                            $modalInstance.close($scope.selection.segment);
                        }, function (error) {
                            $rootScope.msg().text = "Sorry an error occured. Please try again";
                            $rootScope.msg().type = "danger";
                            $rootScope.msg().show = true;
                        });
                    } else {
                        addDatatypes();
                        $modalInstance.close($scope.selection.segment);
                    }
                }, function (error) {
                    $rootScope.msg().text = "Sorry an error occured. Please try again";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                });
            } else {
                $modalInstance.close($scope.selection.segment);
            }
        };
        $scope.cancel = function () {
            $scope.resetMap();
            $modalInstance.dismiss('cancel');
        };


        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
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

        $scope.hasChildren = function (node) {
            return node && node != null && ((node.fields && node.fields.length > 0 ) || (node.datatype && $rootScope.getDatatype(node.datatype.id) && $rootScope.getDatatype(node.datatype.id).components && $rootScope.getDatatype(node.datatype.id).components.length > 0));
        };


        $scope.validateLabel = function (label, name) {
            if (label && !label.startsWith(name)) {
                return false;
            }
            return true;
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

        var containsId = function (id, library) {
            for (var i = 0; i < library.children.length; i++) {
                if (library.children[i].id === id) {
                    return  true;
                }
            }
        };

        $scope.resetMap = function () {
            if ($scope.added = null) {
                angular.forEach($scope.added, function (child) {
                    delete $rootScope.datatypesMap[child];
                });
            }
        };

        $scope.getLocalDatatypeLabel = function (link) {
            return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
        };

        $scope.getLocalSegmentLabel = function (link) {
            return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
        };

        $scope.loadLibrariesByFlavorName('USER').then(function (done) {
            $scope.selection.selected = $scope.currentSegment.id;
            $scope.showSelectedDetails($scope.currentSegment);
        });

    });


angular.module('igl')
    .controller('MessageViewCtrl', function ($scope, $rootScope, Restangular) {
        $scope.loading = false;
        $scope.msg = null;
        $scope.messageData = [];
        $scope.setData = function (node) {
            if (node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.setData(segmentRefOrGroup);
                    });
                } else if (node.type === 'group') {
                    $scope.messageData.push({ name: "-- " + node.name + " begin"});
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.setData(segmentRefOrGroup);
                        });
                    }
                    $scope.messageData.push({ name: "-- " + node.name + " end"});
                } else if (node.type === 'segment') {
                    $scope.messageData.push + (node);
                }
            }
        };


        $scope.init = function (message) {
            $scope.loading = true;
            $scope.msg = message;
            console.log(message.id);
            $scope.setData($scope.msg);
            $scope.loading = false;
        };

//        $scope.hasChildren = function (node) {
//            return node && node != null && node.type !== 'segment' && node.children && node.children.length > 0;
//        };

    });

angular.module('igl').controller('PredicateMessageCtrl', function ($scope, $modalInstance, selectedNode, selectedMessage, $rootScope) {
    $scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
    $scope.selectedMessage = selectedMessage;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;

    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy($scope.selectedMessage.predicates, $scope.tempPredicates);

    $scope.setChanged = function () {
        $scope.changed = true;
    }

    $scope.initPredicate = function () {
        $scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_1: [],
            childNodes_2: [],
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

        for (var i = 0, len1 = $scope.selectedMessage.children.length; i < len1; i++) {
            if ($scope.selectedMessage.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedMessage.children[i].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'group',
                    node: $scope.selectedMessage.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedMessage.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref.id].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref.id]
                };
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
    }

    $scope.initComplexPredicate = function () {
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.complexConstraintTrueUsage = null;
        $scope.complexConstraintFalseUsage = null;
    }

    $scope.deletePredicate = function (predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
        $scope.changed = true;
    };


    $scope.deletePredicateByTarget = function () {
        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget === $scope.selectedNode.path) {
                $scope.deletePredicate($scope.tempPredicates[i]);
                return true;
            }
        }
        return false;
    };

    $scope.updateLocation1 = function () {
        $scope.newConstraint.location_1 = $scope.newConstraint.currentNode_1.name;
        if ($scope.newConstraint.position_1 != null) {
            $scope.newConstraint.position_1 = $scope.newConstraint.position_1 + '.' + $scope.newConstraint.currentNode_1.position + '[1]';
        } else {
            $scope.newConstraint.position_1 = $scope.newConstraint.currentNode_1.position + '[1]';
        }

        $scope.newConstraint.childNodes_1 = [];

        if ($scope.newConstraint.currentNode_1.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_1.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_1.node.children[i].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_1.node.children[i]
                    };
                    $scope.newConstraint.childNodes_1.push(groupModel);
                } else if ($scope.newConstraint.currentNode_1.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_1 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref.id].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref.id]
                    };
                    $scope.newConstraint.childNodes_1.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_1 + '-' + $scope.newConstraint.currentNode_1.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_1.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.fields[i].datatype.id]
                };
                $scope.newConstraint.childNodes_1.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.components[i].datatype.id]
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_1 = null;

    };

    $scope.updateLocation2 = function () {
        $scope.newConstraint.location_2 = $scope.newConstraint.currentNode_2.name;
        if ($scope.newConstraint.position_2 != null) {
            $scope.newConstraint.position_2 = $scope.newConstraint.position_2 + '.' + $scope.newConstraint.currentNode_2.position + '[1]';
        } else {
            $scope.newConstraint.position_2 = $scope.newConstraint.currentNode_2.position + '[1]';
        }

        $scope.newConstraint.childNodes_2 = [];

        if ($scope.newConstraint.currentNode_2.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_2.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_2.node.children[i].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_2.node.children[i]
                    };
                    $scope.newConstraint.childNodes_2.push(groupModel);
                } else if ($scope.newConstraint.currentNode_2.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_2 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref.id].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref.id]
                    };
                    $scope.newConstraint.childNodes_2.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_2 + '-' + $scope.newConstraint.currentNode_2.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_2.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.fields[i].datatype.id]
                };
                $scope.newConstraint.childNodes_2.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.components[i].datatype.id]
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_2 = null;

    };

    $scope.addComplexPredicate = function () {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;
        $scope.complexConstraint.constraintId = $scope.newConstraint.datatype.id + '-' + $scope.selectedNode.position;
        $scope.tempPredicates.push($scope.complexConstraint);
        $scope.initComplexPredicate();
        $scope.changed = true;
    };


    $scope.addPredicate = function () {
        if ($scope.newConstraint.position_1 != null) {
            $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
            var positionPath = selectedNode.path;
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
        angular.copy($scope.tempPredicates, $scope.selectedMessage.predicates);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };

    $scope.initPredicate();

});


angular.module('igl').controller('ConformanceStatementMessageCtrl', function ($scope, $modalInstance, selectedMessage, selectedNode, $rootScope) {
    $scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;
    $scope.selectedMessage = selectedMessage;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID();
    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    angular.copy($scope.selectedMessage.conformanceStatements, $scope.tempComformanceStatements);

    $scope.setChanged = function () {
        $scope.changed = true;
    }

    $scope.initComplexStatement = function () {
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.newComplexConstraintId = $rootScope.calNextCSID();
    }

    $scope.initConformanceStatement = function () {
        $scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_1: [],
            childNodes_2: [],
            verb: null,
            constraintId: $rootScope.calNextCSID(),
            contraintType: null,
            value: null,
            value2: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });

        for (var i = 0, len1 = $scope.selectedMessage.children.length; i < len1; i++) {
            if ($scope.selectedMessage.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedMessage.children[i].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'group',
                    node: $scope.selectedMessage.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedMessage.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref.id].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref.id]
                };
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
    }

    $scope.initConformanceStatement();

    $scope.updateLocation1 = function () {
        $scope.newConstraint.location_1 = $scope.newConstraint.currentNode_1.name;
        if ($scope.newConstraint.position_1 != null) {
            $scope.newConstraint.position_1 = $scope.newConstraint.position_1 + '.' + $scope.newConstraint.currentNode_1.position + '[1]';
        } else {
            $scope.newConstraint.position_1 = $scope.newConstraint.currentNode_1.position + '[1]';
        }

        $scope.newConstraint.childNodes_1 = [];

        if ($scope.newConstraint.currentNode_1.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_1.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_1.node.children[i].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_1.node.children[i]
                    };
                    $scope.newConstraint.childNodes_1.push(groupModel);
                } else if ($scope.newConstraint.currentNode_1.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_1 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref.id].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref.id]
                    };
                    $scope.newConstraint.childNodes_1.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_1 + '-' + $scope.newConstraint.currentNode_1.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_1.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.fields[i].datatype.id]
                };
                $scope.newConstraint.childNodes_1.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'component',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.components[i].datatype.id]
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'component') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_1 = null;

    };

    $scope.updateLocation2 = function () {
        $scope.newConstraint.location_2 = $scope.newConstraint.currentNode_2.name;
        if ($scope.newConstraint.position_2 != null) {
            $scope.newConstraint.position_2 = $scope.newConstraint.position_2 + '.' + $scope.newConstraint.currentNode_2.position + '[1]';
        } else {
            $scope.newConstraint.position_2 = $scope.newConstraint.currentNode_2.position + '[1]';
        }

        $scope.newConstraint.childNodes_2 = [];

        if ($scope.newConstraint.currentNode_2.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_2.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_2.node.children[i].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_2.node.children[i]
                    };
                    $scope.newConstraint.childNodes_2.push(groupModel);
                } else if ($scope.newConstraint.currentNode_2.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_2 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref.id].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref.id]
                    };
                    $scope.newConstraint.childNodes_2.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_2 + '-' + $scope.newConstraint.currentNode_2.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_2.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.fields[i].datatype.id]
                };
                $scope.newConstraint.childNodes_2.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'component',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.components[i].datatype.id]
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'component') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_2 = null;

    };

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
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
        if ($scope.newConstraint.position_1 != null) {
            $rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
            var positionPath = selectedNode.path;
            var cs = $rootScope.generateConformanceStatement(positionPath, $scope.newConstraint);
            $scope.tempComformanceStatements.push(cs);
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
            $scope.changed = true;
        }

        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            $rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf(cs.constraintId), 1);
        });

        angular.forEach($scope.selectedMessage.conformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function () {
        angular.forEach($scope.tempComformanceStatements, function (cs) {
            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
        });
        angular.copy($scope.tempComformanceStatements, $scope.selectedMessage.conformanceStatements);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
});


angular.module('igl').controller('ConfirmMessageDeleteCtrl', function ($scope, $modalInstance, messageToDelete, $rootScope, MessagesSvc, IgDocumentService, CloneDeleteSvc) {
    $scope.messageToDelete = messageToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        MessagesSvc.delete($scope.messageToDelete).then(function (result) {
            IgDocumentService.deleteMessage($scope.messageToDelete.id).then(function (res) {
                // We must delete from two collections.
                CloneDeleteSvc.execDeleteMessage($scope.messageToDelete);
                var index = $rootScope.messages.indexOf($scope.messageToDelete);
                $rootScope.messages.splice(index, 1);
                var tmp = MessagesSvc.findOneChild($scope.messageToDelete.id, $rootScope.igdocument.profile.messages);
                var index = $rootScope.igdocument.profile.messages.children.indexOf(tmp);
                $rootScope.igdocument.profile.messages.children.splice(index, 1);
                $rootScope.messagesMap[$scope.messageToDelete.id] = null;
                $rootScope.references = [];
                if ($rootScope.message === $scope.messageToDelete) {
                    $rootScope.message = null;
                }
                $rootScope.recordDelete("message", "edit", $scope.messageToDelete.id);
                $rootScope.msg().text = "messageDeleteSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $rootScope.manualHandle = true;
                $scope.loading = false;
                $rootScope.$broadcast('event:SetToC');
                $modalInstance.close($scope.messageToDelete);
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
        });
    };


    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };


});
