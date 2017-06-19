angular.module('igl').controller('ListProfileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, PcLibraryService, PcService, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, MessageService, TableService, $mdDialog) {

    $scope.changes = false;

    $scope.editProfileComponent = false;
    $scope.edit = false;
    $scope.profileComponents = [];
    $scope.tabStatus = {
        active: 1
    };
    $scope.accordStatus = {
        isCustomHeaderOpen: false,
        isFirstOpen: false,
        isSecondOpen: true,
        isThirdOpen: false

    };

    $scope.editL = function(node){
        node.attributes.minLength = null;
        node.attributes.maxLength = null;
        node.minLength =  node.attributes.oldMinLength != 'NA' ? node.attributes.oldMinLength: '';
        node.maxLength =  node.attributes.oldMaxLength != 'NA' ? node.attributes.oldMaxLength: '';
        $scope.setDirty();
    };

    $scope.editConfL = function(node){
        node.confLength =  node.attributes.oldConfLength != 'NA' ? node.attributes.oldConfLength : '';
        node.attributes.confLength =  null;
        $scope.setDirty();
    };

    $scope.clearConfL = function(node){
        node.confLength = "NA";
        node.attributes.confLength = "NA";
        $scope.setDirty();
    };

    $scope.clearL = function(node){
        node.minLength = "NA";
        node.maxLength = "NA";
        node.attributes.minLength = "NA";
        node.attributes.maxLength = "NA";
        $scope.setDirty();
    };

    $scope.confLengthPattern= '[1-9]\\d*[#=]{0,1}';


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
    $scope.openAddGlobalPredicateDialogMsg = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'GlobalPredicateCtrl.html',
            controller: 'GlobalPredicateCtrlInPc',
            windowClass: 'app-modal-window',
            keyboard: false,
            resolve: {

                node: function() {
                    return node;
                }
            }
        });
        modalInstance.result.then(function(predicate) {
            console.log("----");
            console.log(predicate);
            console.log(node);
            console.log("----");
            if (predicate) {
                //$rootScope.message = message;
                //$scope.findAllGlobalConstraints();
                node.predicates = [];
                node.predicates.push(predicate);
                $scope.setDirty();
            }

        }, function() {});
    };
    $scope.openAddGlobalPredicateDialogSeg = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'PredicateSegmentCtrl.html',
            controller: 'PredicateSegmentCtrlInPc',
            windowClass: 'app-modal-window',
            resolve: {
                node: function() {
                    return node;
                }
            }
        });
        modalInstance.result.then(function(predicate) {
            // if (segment) {
            //     $rootScope.segment.predicates = segment.predicates;
            //     $scope.setDirty();
            // }
            if (predicate) {
                //$rootScope.message = message;
                //$scope.findAllGlobalConstraints();
                console.log("PREDICATE");
                console.log(predicate);
                node.predicates = [];
                node.predicates.push(predicate);
                $scope.setDirty();
            }
        }, function() {});
    };
    $scope.findingPredicatesFromDtContexts = function(node) {
        var result = null;
        if (node && node.type === 'component') {
            console.log("----");
            console.log(DatatypeService.getDatatypeLevelPredicates(node));
        }
    };
    $scope.findingPredicates = function(node) {
        var result = null;

        if (node) {

            var index = node.path.indexOf(".");
            var path = node.path.substr(index + 1);


            if (!node.predicates || node.predicates.length <= 0) {
                if (node.from === "message") {

                    result = _.find(node.oldPredicates, function(p) {
                        if (p.context && p.context.type === "group") {
                            var tempath = path.replace(p.context.path + '.', '');
                            return $rootScope.refinePath(p.constraintTarget) == tempath
                        } else {
                            return $rootScope.refinePath(p.constraintTarget) == path;
                        }

                    });
                    if (result) {
                        result.bindingFrom = 'message';
                        return result;
                    }
                } else if (node.from === "segment") {
                    result = _.find(node.oldPredicates, function(p) { return $rootScope.refinePath(p.constraintTarget) == path; });
                    if (result) {
                        result.bindingFrom = 'segment';
                        return result;
                    }
                }


            } else {
                if (node.from === "message") {

                    result = _.find(node.predicates, function(p) {
                        if (p.context && p.context.type === "group") {
                            var tempath = path.replace(p.context.path + '.', '');
                            //path = p.context.path + '.' + path;
                            return $rootScope.refinePath(p.constraintTarget) == tempath
                        } else {
                            return $rootScope.refinePath(p.constraintTarget) == path;
                        }

                    });

                    if (result) {
                        result.bindingFrom = 'message';
                        return result;
                    }
                } else if (node.from === "segment") {
                    result = _.find(node.predicates, function(p) { return $rootScope.refinePath(p.constraintTarget) == path; });
                    if (result) {
                        result.bindingFrom = 'segment';
                        return result;
                    }
                }

            }
            if (node.type === "component") {

                if (node.source.fieldDt) {

                    var pArray = path.split(".");
                    var tempPath = pArray[pArray.length - 1];


                    var temp = _.find($rootScope.datatypesMap[node.source.fieldDt].predicates, function(p) { return $rootScope.refinePath(p.constraintTarget) === tempPath; });
                    if (temp) {
                        result = temp;
                        result.bindingFrom = 'field';
                        return result;
                    }
                }
                if (node.source.componentDt) {
                    var pArray = path.split(".");
                    var tempPath = pArray[pArray.length - 1];
                    var temp = _.find($rootScope.datatypesMap[node.source.componentDt].predicates, function(p) { return $rootScope.refinePath(p.constraintTarget) == tempPath; });
                    if (temp) {
                        result = temp;
                        result.bindingFrom = 'component';
                        return result;
                    }
                }
            }
            if (node.type === "field") {
                if (node.source.segmentId) {
                    var pArray = path.split(".");
                    var tempPath = pArray[pArray.length - 1];
                    var temp = _.find($rootScope.segmentsMap[node.source.segmentId].predicates, function(p) { return $rootScope.refinePath(p.constraintTarget) === tempPath; });
                    if (temp) {
                        result = temp;
                        result.bindingFrom = 'segment';
                        return result;
                    }
                }

            }



            if (result) {
                return result;
            }


        }

        return result;
    };


    $scope.getValueSetContext = function(node) {
        if (node.path) {
            var context = node.path.split(".");
            return context[0];
        }
    };

    $scope.findingBindings = function(node) {
        var result = [];

        if (node && (node.type === "field" || node.type === "component")) {
            var index = node.path.indexOf(".");
            var path = node.path.substr(index + 1);
            if (!node.valueSetBindings || node.valueSetBindings.length <= 0) {
                if (node.from === "message") {
                    result = _.filter(node.oldValueSetBindings, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].bindingFrom = 'message';
                    }
                } else if (node.from === "segment") {
                    result = _.filter(node.oldValueSetBindings, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].bindingFrom = 'segment';
                    }
                }


            } else {
                if (node.from === "message") {
                    result = _.filter(node.valueSetBindings, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].bindingFrom = 'message';
                    }
                } else if (node.from === "segment") {
                    result = _.filter(node.valueSetBindings, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].bindingFrom = 'segment';
                    }
                }

            }

            if (result && result.length > 0) {
                return result;
            }


        }

        return result;
    };

    $scope.isAvailableForValueSet = function(node) {

        if (node && (node.type === "field" || node.type === "component")) {
            var currentDT = $rootScope.datatypesMap[node.attributes.oldDatatype.id];

            if (currentDT && _.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT) {
                    return valueSetAllowedDT == currentDT.name;
                })) return true;
        }



        return false;
    };
    $scope.isAvailableConstantValue = function(node) {
        if (node.type === "field" || node.type === "component") {
            if ($scope.hasChildren(node)) return false;
            var bindings = $scope.findingBindings(node);
            if (bindings && bindings.length > 0) return false;
            if ($rootScope.datatypesMap[node.datatype.id].name == 'ID' || $rootScope.datatypesMap[node.datatype.id].name == "IS") return false;
            return true;
        } else {
            return false;
        }

    };

    $scope.findingSingleElement = function(node) {
        var result = null;

        if (node && (node.type === "field" || node.type === "component")) {
            var index = node.path.indexOf(".");
            var path = node.path.substr(index + 1);
            if (!node.singleElementValues || node.singleElementValues.length <= 0) {
                if (node.from === "message") {
                    result = _.find(node.oldSingleElementValues, function(binding) { return binding.location == path; });
                    if (result)
                        result.from = 'message';
                }
            } else if (node.from === "segment") {
                result = _.find(node.oldSingleElementValues, function(binding) { return binding.location == path; });
                if (result)
                    result.from = 'segment';

            }


        } else {
            if (node.from === "message") {
                result = _.find(node.singleElementValues, function(binding) { return binding.location == path; });
                if (result)
                    result.from = 'message';

            } else if (node.from === "segment") {
                result = _.find(node.singleElementValues, function(binding) { return binding.location == path; });
                if (result)
                    result.from = 'segment';


            }

        }

        if (result) {
            return result;
        }

        return result;
    };
    $scope.findingConfSt = function(node) {
        if (node) {
            if (!node.attributes.conformanceStatements || node.attributes.conformanceStatements.length <= 0) {
                return node.attributes.oldConformanceStatements;
            } else {
                return node.attributes.conformanceStatements;
            }
        }

    };
    $scope.findingComments = function(node) {
        var result = [];

        if (node) {
            var index = node.path.indexOf(".");
            var path = node.path.substr(index + 1);
            if (!node.comments || node.comments.length <= 0) {
                if (node.from === "message") {
                    result = _.filter(node.oldComments, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].from = 'message';
                    }
                } else if (node.from === "segment") {
                    result = _.filter(node.oldComments, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].from = 'segment';
                    }
                }


            } else {
                if (node.from === "message") {
                    result = _.filter(node.comments, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].from = 'message';
                    }
                } else if (node.from === "segment") {
                    result = _.filter(node.comments, function(binding) { return binding.location == path; });
                    for (var i = 0; i < result.length; i++) {
                        result[i].from = 'segment';
                    }
                }

            }

            if (result && result.length > 0) {
                return result;
            }


        }

        return result;
    };

    $scope.editCommentDlg = function(node, comment, disabled, type) {
        var modalInstance = $mdDialog.show({
            templateUrl: 'EditCommentMd.html',
            controller: 'EditCommentCtrlInPc',
            locals: {
                currentNode: node,
                currentComment:comment,
                disabled: disabled,
                type: type
                }

        });

        modalInstance.then(function() {
            $scope.setDirty();
        });
    };
    $scope.editModalBindingForMsg = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'TableMappingMessageCtrl.html',
            controller: 'TableBindingForMsgCtrl',
            windowClass: 'app-modal-window',
            resolve: {
                currentNode: function() {
                    return node;
                }
            }
        });

        modalInstance.result.then(function(node) {
            console.log("node");
            console.log(node);
            $scope.setDirty();
        });
    };
    $scope.showConfSt = false;
    $scope.seeConfSt = function(node) {
        $scope.showConfSt = true;
        $scope.currentNode = node;
    };
    $scope.unseeConfSt = function(node) {
        $scope.showConfSt = false;
        $scope.currentNode = null;
    };

    $scope.openAddGlobalConformanceStatementDialogSeg = function(node, context) {
        $mdDialog.show({
            templateUrl: 'ConformanceStatementSegmentCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'ConformanceStatementSegmentCtrlInPc',
            locals: {
                node: node,
                context: context
            }

        }).then(function(confs) {
            if (confs) {
                console.log(confs);
                node.attributes.conformanceStatements = confs;
                $scope.setDirty();
            }
        });

    };
    $scope.hasDynamicMapping = function(node) {
        if (node.type === "segmentRef") {
            var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
                return item.hl7Version == $rootScope.segmentsMap[node.attributes.ref.id].hl7Version && item.segmentName == $rootScope.segmentsMap[node.attributes.ref.id].name;
            });
            if (mappingStructure) {
                return true;
            }
        }
        return false;
    };

    $scope.findDynamicMapping = function(node) {
        if (node.type === "segmentRef") {
            if (node.attributes.dynamicMappingDefinition && node.attributes.dynamicMappingDefinition.dynamicMappingItems.length > 0) {
                return node.attributes.dynamicMappingDefinition;
            } else {
                return node.attributes.oldDynamicMappingDefinition;
            }
        }
        return null;
    };
    $scope.openAddDynamicMappingDialog = function(node, context) {
        $mdDialog.show({
            templateUrl: 'AddDynamicMappingCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'AddDynamicMappingCtrlInPc',
            locals: {
                node: node,
                context: context
            }

        }).then(function(mapping) {
            if (mapping) {
                console.log(mapping);
                node.attributes.dynamicMappingDefinition = mapping;
                $scope.setDirty();
            }
        });
    };
    $scope.hasCoConstraints = function(node) {
        if (node.type === "segmentRef") {
            return true
        }
        return false;
    };

    $scope.findCoConstraints = function(node) {
        if (node.type === "segmentRef") {
            if (node.attributes.coConstraintsTable && node.attributes.coConstraintsTable.rowSize > 0) {
                return node.attributes.coConstraintsTable;
            } else {
                return node.attributes.oldCoConstraintsTable;
            }
        }
        return null;
    };
    $scope.openAddCoConstraintsDialog = function(node, context) {
        $mdDialog.show({
            templateUrl: 'AddCoConstraintCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'AddCoConstraintCtrlInPc',
            locals: {
                node: node,
                context: context
            }

        }).then(function(coCon) {
            if (coCon) {
                console.log(coCon);
                node.attributes.coConstraintsTable = coCon;
                $scope.setDirty();
            }
        });
    };
    $scope.openAddGlobalConformanceStatementDialog = function(node, context) {
        $mdDialog.show({
            templateUrl: 'GlobalConformanceStatementCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'GlobalConformanceStatementCtrlInPc',
            locals: {
                node: node,
                context: context
            }

        }).then(function(confs) {
            if (confs) {
                console.log(confs);
                node.attributes.conformanceStatements = confs;
                $scope.setDirty();
            }
        });

    };

    $scope.addSev = function(node) {
        var sev = {};
        var index = node.path.indexOf(".");
        sev.location = node.path.substr(index + 1);
        sev.value = '';
        sev.name = node.name;
        console.log(sev);
        node.singleElementValues = sev;
        $scope.setDirty();
    };

    $scope.openDialogForEditSev = function(node) {
        var modalInstance = $mdDialog.show({
            templateUrl: 'EditSingleElement.html',
            controller: 'EditSingleElementCtrlInPc',
            locals: {
                currentNode: node
                }

        });

        modalInstance.then(function(value) {
            console.log(value);
            $scope.addSev(node);
            // node.singleElementValues = angular.copy(node.oldSingleElementValues);
            node.singleElementValues.value = value;
            $scope.initSev(node);
            $scope.setDirty();
        });
    };
    $scope.hasChildren = function(node) {
        if (node && node != null) {
            if (node.type === 'field' || node.type === 'component') {
                if (node.attributes.datatype) {
                    return $rootScope.datatypesMap[node.attributes.datatype.id] && $rootScope.datatypesMap[node.attributes.datatype.id].components && $rootScope.datatypesMap[node.attributes.datatype.id].components.length > 0;

                } else {
                    return $rootScope.datatypesMap[node.attributes.oldDatatype.id] && $rootScope.datatypesMap[node.attributes.oldDatatype.id].components && $rootScope.datatypesMap[node.attributes.oldDatatype.id].components.length > 0;

                }
            }
            return false;
        } else {
            return false;
        }

    };
    $scope.updatePosition = function() {
        console.log($rootScope.profileComponent);
        for (var i = 0; i < $rootScope.profileComponent.children.length; i++) {
            $rootScope.profileComponent.children[i].position = i + 1;
        }
        $scope.save();
    };
    $scope.addPComponents = function() {
        $mdDialog.show({
            templateUrl: 'addComponents.html',
            parent: angular.element(document).find('body'),
            controller: 'addComponentsCtrl',
            locals: {
                messages: angular.copy($rootScope.messages.children),
                segments: angular.copy($rootScope.segments),
                segmentsMap: angular.copy($rootScope.segmentsMap),
                datatypes: angular.copy($rootScope.datatypes),
                datatypesMap: $rootScope.datatypesMap,
                currentPc: $rootScope.profileComponent
            }

        }).then(function(results) {
            $scope.setDirty();
            if ($scope.profileComponentParams) {
                $scope.profileComponentParams.refresh();
            }
        });

    };
    $scope.removePcEntry = function(node) {
        $rootScope.profileComponent.children = orderByFilter($rootScope.profileComponent.children, 'position');
        var index = $rootScope.profileComponent.children.indexOf(node);
        if (index > -1) $rootScope.profileComponent.children.splice(index, 1);
        for (var i = index; i < $rootScope.profileComponent.children.length; i++) {
            $rootScope.profileComponent.children[i].position--;
        }
        $scope.setDirty();
        if ($scope.profileComponentParams) {
            $scope.profileComponentParams.refresh();
        }
    };

    $scope.addComponent = function() {

        PcLibraryService.addComponentToLib($rootScope.igdocument.id, $scope.newPc).then(function(ig) {
            $rootScope.igdocument.profile.profileComponentLibrary.children = ig.profile.profileComponentLibrary.children;
            if ($scope.profileComponentParams) {
                $scope.profileComponentParams.refresh();
            }
        });
    };
    $scope.save = function() {
        var children = $rootScope.profileComponent.children;
        var bindingParam = $rootScope.profileComponent.appliedTo;
        console.log("before");
        console.log($rootScope.profileComponent);

        PcService.save($rootScope.igdocument.profile.profileComponentLibrary.id, $rootScope.profileComponent).then(function(result) {
            // $rootScope.profileComponent = result;
            for (var i = 0; i < $rootScope.igdocument.profile.profileComponentLibrary.children.length; i++) {
                if ($rootScope.igdocument.profile.profileComponentLibrary.children[i].id === $rootScope.profileComponent.id) {
                    $rootScope.igdocument.profile.profileComponentLibrary.children[i].name = $rootScope.profileComponent.name;
                    $rootScope.igdocument.profile.profileComponentLibrary.children[i].comment = $rootScope.profileComponent.comment;
                    $rootScope.igdocument.profile.profileComponentLibrary.children[i].description = $rootScope.profileComponent.description;
                }

            }
            for (var i = 0; i < $rootScope.profileComponents.length; i++) {
                if ($rootScope.profileComponents[i].id === $rootScope.profileComponent.id) {
                    $rootScope.profileComponents[i] = $rootScope.profileComponent;
                }
            }
            $rootScope.profileComponentsMap[$rootScope.profileComponent.id] = $rootScope.profileComponent;

            $scope.changes = false;
            $scope.clearDirty();
            console.log("------Profile Component------");
            console.log(result);
        });
        // });
    };
    $scope.initSev = function(node) {
        if (node.singleElementValues && node.singleElementValues.value !== null && node.singleElementValues.location !== null) {
            node.sev = node.singleElementValues;
        } else {
            node.sev = node.oldSingleElementValues;
        }
    };
    $scope.cancelSev = function(node) {
        node.singleElementValues = null;
        $scope.initSev(node);
    };
    $scope.cancelBinding = function(node) {
        node.valueSetBindings = null;
        $scope.setDirty();

    };
    $scope.cancelComments = function(node) {
        node.comments = null;
        $scope.setDirty();

    };
    $scope.cancelPredicate = function(node) {
        node.predicates = [];
        $scope.setDirty();

    };
    $scope.cancelConfSt = function(node) {
        node.attributes.conformanceStatements = [];
        $scope.setDirty();
    };
    $scope.cancelDynMap = function(node) {
        node.attributes.dynamicMappingDefinition = null;
        $scope.setDirty();
    };
    $scope.cancelCoCon = function(node) {
        node.attributes.coConstraintsTable = null;
        $scope.setDirty();
    };
    $scope.initUsage = function(node) {
        if (node.attributes.usage) {
            node.usage = node.attributes.usage;
        } else {
            node.usage = node.attributes.oldUsage;
        }
    };
    $scope.updateUsage = function(node) {
        if (node.usage === node.attributes.oldUsage) {
            node.attributes.usage = null;
        } else {
            node.attributes.usage = node.usage;
        }
    };
    $scope.cancelUsage = function(field) {
        field.attributes.usage = null;
        field.usage = field.attributes.oldUsage;
        $scope.setDirty();
    };
    $scope.initMinCard = function(node) {
        if (node.attributes.min) {
            node.min = node.attributes.min;
        } else {
            node.min = node.attributes.oldMin;
        }
    };
    $scope.updateMinCard = function(node) {
        if (parseInt(node.min) === node.attributes.oldMin) {
            node.attributes.min = null;
        } else {
            node.attributes.min = parseInt(node.min);
        }
    };

    $scope.cancelMinCard = function(field) {
        field.attributes.min = null;
        field.min = field.attributes.oldMin;
        $scope.setDirty();
    };

    $scope.cancelCard = function(field) {
        field.attributes.min = null;
        field.min = field.attributes.oldMin;
        field.attributes.max = null;
        field.max = field.attributes.oldMax;
        $scope.setDirty();
    };

    $scope.initMaxCard = function(node) {
        if (node.attributes.max) {
            node.max = node.attributes.max;
        } else {
            node.max = node.attributes.oldMax;
        }
    };
    $scope.updateMaxCard = function(node) {
        if (node.max === node.attributes.oldMax) {
            node.attributes.max = null;
        } else {
            node.attributes.max = node.max;
        }
    };


    $scope.cancelMaxCard = function(field) {
        field.attributes.max = null;
        field.max = field.attributes.oldMax;

        $scope.setDirty();
    };
    $scope.initMinL = function(node) {
         if (node.attributes.minLength) {
            node.minLength = node.attributes.minLength;
        } else {
            node.minLength = node.attributes.oldMinLength;
        }
        console.log("initMinL, node.minLength=" + node.minLength + ", node.attributes.minLength=" + node.attributes.minLength + ", node.attributes.oldMinLength="+ node.attributes.oldMinLength);

    };

    $scope.initL = function(node) {
        if (node.attributes.minLength) {
            node.minLength = node.attributes.minLength;
        } else {
            node.minLength = node.attributes.oldMinLength;
        }
        if (node.attributes.maxLength) {
            node.maxLength = node.attributes.maxLength;
        } else {
            node.maxLength = node.attributes.oldMaxLength;
        }
    };

    $scope.updateMinL = function(node) {
        if (node.minLength === node.attributes.oldMinLength) {
            node.attributes.minLength = null;
        } else {
            node.attributes.minLength = node.minLength;
        }
        // node.attributes.confLength = "NA";
        // node.confLength = node.attributes.confLength;
    };

    $scope.cancelL = function(field) {
        field.attributes.minLength = null;
        field.minLength = field.attributes.oldMinLength;
        field.attributes.maxLength = null;
        field.maxLength = field.attributes.oldMaxLength;
        // field.attributes.confLength = null;
        // field.confLength = field.attributes.oldConfLength;
        $scope.setDirty();
    };

    $scope.cancelMinL = function(field) {
        field.attributes.minLength = null;
        field.minLength = field.attributes.oldMinLength;

        $scope.setDirty();
    };
    $scope.initMaxL = function(node) {
        console.log("initMaxL is called ");
        if (node.attributes.maxLength) {
            node.maxLength = node.attributes.maxLength;
        } else {
            node.maxLength = node.attributes.oldMaxLength;
        }
        console.log("initMaxL, node.maxLength=" + node.maxLength + ",  node.attributes.maxLength=" +   node.attributes.maxLength + "node.attributes.oldMaxLength="+ node.attributes.oldMaxLength);

    };
    $scope.updateMaxL = function(node) {
        if (node.maxLength === node.attributes.oldMaxLength) {
            node.attributes.maxLength = null;
        } else {
            node.attributes.maxLength = node.maxLength;
        }
        // node.attributes.confLength = "NA";
        // node.confLength = field.attributes.confLength;
    };
    $scope.cancelMaxL = function(field) {
        field.attributes.maxLength = null;
        field.maxLength = field.attributes.oldMaxLength;
        $scope.setDirty();
    };

    $scope.initConfL = function(node) {
        if (node.attributes.confLength) {
            node.confLength = node.attributes.confLength;
        } else {
            node.confLength = node.attributes.oldConfLength;
        }
        console.log("initMinL, node.confLength=" + node.confLength + ",   node.attributes.confLength=" + node.attributes.confLength + "node.attributes.oldConfLength="+ node.attributes.oldConfLength);

    };
    $scope.updateConfL = function(node) {
        console.log("updateConfL called");
        if (node.confLength === node.attributes.oldConfLength) {
            node.attributes.confLength = null;
        } else {
            node.attributes.confLength = node.confLength;
        }
        // node.attributes.minLength = "NA";
        // node.minLength = node.attributes.minLength;
        // node.attributes.maxLength = "NA";
        // node.maxLength = node.attributes.maxLength;
    };

    $scope.cancelConfL = function(field) {
        // field.attributes.minLength = null;
        // field.minLength = field.attributes.oldMinLength;
        // field.attributes.maxLength = null;
        // field.maxLength = field.attributes.oldMaxLength;
        field.attributes.confLength = null;
        field.confLength = field.attributes.oldConfLength;
        $scope.setDirty();
    };
    $scope.initDatatype = function(node) {
        if (node.attributes.datatype) {
            node.datatype = node.attributes.datatype;
        } else {
            node.datatype = node.attributes.oldDatatype;
        }
    };
    $scope.updateDatatype = function(node) {
        if (node.datatype.id === node.attributes.oldDatatype.id) {
            node.attributes.datatype = null;
        } else {
            node.attributes.datatype = node.datatype;
        }
    };
    $scope.cancelDatatype = function(field) {
        field.attributes.datatype = null;
        field.datatype = field.attributes.oldDatatype;
        $scope.editableDT = '';
        $scope.setDirty();
    };
    $scope.backDT = function() {
        $scope.editableDT = '';

    };
    $scope.cancelDefText = function(field) {
        field.attributes.text = null;
        $scope.setDirty();
    };
    $scope.cancelTables = function(field) {
        field.attributes.tables = field.attributes.oldTables;
        $scope.setDirty();
    };


    $scope.cancelRef = function(field) {
        field.attributes.ref = field.attributes.oldRef;
        $scope.setDirty();
    }
    $scope.editableDT = '';

    $scope.selectDT = function(field) {


        if (field.datatype && field.datatype !== "Others") {
            $scope.DTselected = true;
            $scope.editableDT = '';

            if (field.datatype.id === field.attributes.oldDatatype.id) {
                field.attributes.datatype = null;
                field.datatype = field.attributes.oldDatatype;
            } else {
                console.log("else");
                console.log(field);
                field.attributes.datatype = field.datatype;
                field.attributes.datatype = {};
                field.attributes.datatype.ext = field.datatype.ext;
                field.attributes.datatype.id = field.datatype.id;
                field.attributes.datatype.label = field.datatype.label;
                field.attributes.datatype.name = field.datatype.name;
                field.datatype = field.attributes.datatype;

            }


            $scope.setDirty();
            if ($scope.profileComponentParams)
                $scope.profileComponentParams.refresh();
            $scope.DTselected = false;

        } else {
            $scope.otherDT(field);
        }


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

        });

    };

    $scope.addComment = function(field) {
        var modalInstance = $modal.open({
            templateUrl: 'addCommentModal.html',
            controller: 'addCommentCtrl',
            windowClass: 'edit-VS-modal',
            resolve: {



                field: function() {
                    return field;
                }

            }
        });
        modalInstance.result.then(function(field) {
            $scope.setDirty();

        });

    };
    $scope.addDefText = function(field) {
        var modalInstance = $modal.open({
            templateUrl: 'addDefTextModal.html',
            controller: 'addDefTextCtrl',
            windowClass: 'edit-VS-modal',
            resolve: {



                field: function() {
                    return field;
                }

            }
        });
        modalInstance.result.then(function(field) {
            $scope.setDirty();

        });

    };
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
                    return field.attributes;
                }

            }
        });
        modalInstance.result.then(function(attr) {
            console.log("===");
            console.log(attr);
            console.log(field);
            field.datatype = attr.datatype;
            $scope.setDirty();
            $scope.editableDT = '';
            if ($scope.profileComponentParams) {
                $scope.profileComponentParams.refresh();
            }
        });

    };
    $scope.editableRef = '';
    $scope.editRef = function(field) {
        $scope.editableRef = field.id;
        $scope.segFlavors = [];
        for (var i = 0; i < $rootScope.segments.length; i++) {
            if ($rootScope.segments[i].name === field.attributes.ref.name) {
                $scope.segFlavors.push({
                    id: $rootScope.segments[i].id,
                    name: $rootScope.segments[i].name,
                    ext: $rootScope.segments[i].ext,
                    label: $rootScope.segments[i].label

                });
            }
        }
    };
    $scope.selectFlavor = function(field, flavor) {
        if (flavor) {
            field.attributes.ref = {
                id: flavor.id,
                name: flavor.name,
                ext: flavor.ext,
                label: flavor.label
            };

            $scope.setDirty();
            $scope.editableRef = '';
        };

    };

    $scope.editDT = function(field) {
        $scope.editableDT = field.id;
        $scope.editableDT = field.id;

        $scope.datatypes = [];
        angular.forEach($rootScope.datatypeLibrary.children, function(dtLink) {
            if (dtLink.name && dtLink.name === field.datatype.name) {
                $scope.datatypes.push(dtLink);
            }
        });

    };


    $scope.showEditDynamicMappingDlg = function(node) {
        var modalInstance = $modal.open({
            templateUrl: 'DynamicMappingCtrl.html',
            controller: 'DynamicMappingCtrlInPc',
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

angular.module('igl').controller('AddCoConstraintCtrlInPc', function($scope, $mdDialog, node, context, $rootScope, TableService) {
    $scope.node = angular.copy(node);
    $scope.seg = angular.copy($rootScope.segmentsMap[node.attributes.ref.id]);
    console.log(node);

    $scope.changed = false;
    $scope.setDirty = function() {
        $scope.changed = true;
    };
    $scope.findCoConstraints = function() {
        if ($scope.node.type === "segmentRef") {
            if ($scope.node.attributes.coConstraintsTable && $scope.node.attributes.coConstraintsTable.rowSize > 0) {
                $scope.coConstraintsTable = $scope.node.attributes.coConstraintsTable;
            } else {
                $scope.coConstraintsTable = $scope.node.attributes.oldCoConstraintsTable;
            }
        }

    };
    $scope.findCoConstraints();
    console.log($scope.coConstraintsTable);


    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.userAddCoCon = false;
    $scope.thenData = false;

    $scope.saveIF = function() {
        var ifColumnDefinition = {};
        ifColumnDefinition.id = new ObjectId().toString();
        ifColumnDefinition.constraintType = $scope.coConstraintType;
        ifColumnDefinition.name = $scope.targetNode.name;
        ifColumnDefinition.usage = $scope.targetNode.usage;
        ifColumnDefinition.dtId = $scope.targetNode.datatype.id;
        ifColumnDefinition.primitive = $scope.primitive;

        if ($scope.selectedFieldPosition) {
            ifColumnDefinition.path = "" + $scope.selectedFieldPosition;
            ifColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
            ifColumnDefinition.type = "field";
            if ($scope.selectedComponentPosition) {
                ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedComponentPosition;
                ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
                ifColumnDefinition.type = "component";
                if ($scope.selectedSubComponentPosition) {
                    ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
                    ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
                    ifColumnDefinition.type = "subcomponent";
                }
            }
        }
        if (ifColumnDefinition) {
            if (!$scope.coConstraintsTable) {
                $scope.coConstraintsTable = {};
                $scope.coConstraintsTable.rowSize = 0;
            }

            if (!$scope.coConstraintsTable.ifColumnDefinition) {
                $scope.coConstraintsTable.ifColumnData = [];
                for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
                    $scope.coConstraintsTable.ifColumnData.push({});
                }
            }

            $scope.coConstraintsTable.ifColumnDefinition = ifColumnDefinition;
        }
        $scope.setDirty();
    };


    $scope.updateFieldIF = function() {
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;
        $scope.primitive = true;

        var field = _.find($scope.seg.fields, function(f) {
            return f.position == $scope.selectedFieldPosition;
        });

        $scope.targetNode = field;

        if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
            $scope.primitive = false;
            $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

        }
    };
    $scope.updateComponentIF = function() {
        $scope.selectedSubComponentPosition = null;
        $scope.subComponents = null;
        $scope.primitive = true;

        var component = _.find($scope.components, function(c) {
            return c.position == $scope.selectedComponentPosition;
        });

        $scope.targetNode = component;
        if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
            $scope.primitive = false;
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }
    };
    $scope.updateSubComponentIF = function() {
        $scope.primitive = true;
        var subComponent = _.find($scope.subComponents, function(sc) {
            return sc.position == $scope.selectedSubComponentPosition;
        });
        $scope.targetNode = subComponent;
    };


    $scope.coConstraintIFDefinition = function() {
        $scope.ifAddCoCon = true;
        $scope.thenAddCoCon = false;
        $scope.userAddCoCon = false;
        $scope.thenData = false;
        $scope.selectedCoConstraintIFDefinition = angular.copy($scope.coConstraintsTable.ifColumnDefinition);


        if ($scope.selectedCoConstraintIFDefinition) {
            $scope.primitive = $scope.selectedCoConstraintIFDefinition.primitive;
            $scope.coConstraintType = $scope.selectedCoConstraintIFDefinition.constraintType;
            var splitLocation = $scope.selectedCoConstraintIFDefinition.path.split('.');
            if (splitLocation.length > 0) {
                $scope.selectedFieldPosition = splitLocation[0];

                var field = _.find($scope.seg.fields, function(f) {
                    return f.position == splitLocation[0];
                });

                $scope.targetNode = field;

                if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
                    $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

                    if (splitLocation.length > 1 && $scope.components) {
                        $scope.selectedComponentPosition = splitLocation[1];
                        var component = _.find($scope.components, function(c) {
                            return c.position == splitLocation[1];
                        });
                        $scope.targetNode = component;
                        if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
                            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

                            if (splitLocation.length > 2 && $scope.subComponents) {
                                $scope.selectedSubComponentPosition = splitLocation[2];
                                var subComponent = _.find($scope.subComponents, function(sc) {
                                    return sc.position == splitLocation[2];
                                });
                                $scope.targetNode = subComponent;
                            }
                        }
                    }
                }
            }
        };
    };
    $scope.coConstraintTHENDefinition = function(columnDefinition) {
        $scope.ifAddCoCon = false;
        $scope.thenAddCoCon = true;
        $scope.userAddCoCon = false;
        $scope.thenData = false;


        $scope.selectedCoConstraintTHENDefinition = angular.copy(columnDefinition);
        console.log($scope.selectedCoConstraintTHENDefinition);

        $scope.coConstraintType = 'value';
        $scope.selectedFieldPosition = null;
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;
        $scope.primitive = true;
        $scope.dMReference = false;

        $scope.targetNode = null;


        if ($scope.selectedCoConstraintTHENDefinition) {
            $scope.primitive = $scope.selectedCoConstraintTHENDefinition.primitive;
            $scope.coConstraintType = $scope.selectedCoConstraintTHENDefinition.constraintType;
            $scope.isDMReference = $scope.selectedCoConstraintTHENDefinition.dMReference;
            var splitLocation = $scope.selectedCoConstraintTHENDefinition.path.split('.');
            if (splitLocation.length > 0) {
                $scope.selectedFieldPosition = splitLocation[0];

                var field = _.find($scope.seg.fields, function(f) {
                    return f.position == splitLocation[0];
                });

                $scope.targetNode = field;

                if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
                    $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

                    if (splitLocation.length > 1 && $scope.components) {
                        $scope.selectedComponentPosition = splitLocation[1];
                        var component = _.find($scope.components, function(c) {
                            return c.position == splitLocation[1];
                        });
                        $scope.targetNode = component;
                        if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
                            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

                            if (splitLocation.length > 2 && $scope.subComponents) {
                                $scope.selectedSubComponentPosition = splitLocation[2];
                                var subComponent = _.find($scope.subComponents, function(sc) {
                                    return sc.position == splitLocation[2];
                                });
                                $scope.targetNode = subComponent;
                            }
                        }
                    }
                }
            }
        };
    };
    $scope.coConstraintUSERDefinitionF = function(coConstraintUSERDefinition) {
        $scope.ifAddCoCon = false;
        $scope.thenAddCoCon = false;
        $scope.thenData = false;
        $scope.userAddCoCon = true;
        console.log(coConstraintUSERDefinition);

        $scope.coConstraintUSERDefinition = angular.copy(coConstraintUSERDefinition);
        $scope.title = null;

        if ($scope.coConstraintUSERDefinition) {
            $scope.title = $scope.coConstraintUSERDefinition.title;
        }




    };

    $scope.editValueSetThenMapData = function(currentId, currentIndex) {
        $scope.currentId = currentId;
        $scope.currentIndex = currentIndex;
        $scope.ifAddCoCon = false;
        $scope.thenAddCoCon = false;
        $scope.thenData = true;
        $scope.userAddCoCon = false;
        $scope.data = angular.copy($scope.coConstraintsTable.thenMapData[currentId][currentIndex]);
        $scope.listOfBindingLocations = null;
        $scope.columnDefinition = _.find($scope.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
            return columnDefinition.id == currentId;
        });

        if ($scope.columnDefinition) {
            var dtId = $scope.columnDefinition.dtId;

            if ($rootScope.datatypesMap[dtId].name.toLowerCase() == 'varies') {
                var referenceColumnDefinition = _.find($scope.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
                    return columnDefinition.dMReference;
                });

                if (referenceColumnDefinition) {
                    dtId = $scope.coConstraintsTable.thenMapData[referenceColumnDefinition.id][currentIndex].datatypeId;
                }

                $scope.listOfBindingLocations = $scope.findOptionsVS(dtId);
            } else {
                if (!$scope.columnDefinition.primitive) {
                    $scope.listOfBindingLocations = $scope.findOptionsVS(dtId);
                } else {
                    $scope.listOfBindingLocations = null;
                }
            }
        } else {
            $scope.listOfBindingLocations = null;
        }

    };
    $scope.findOptionsVS = function(dtId) {
        var result = [];
        result.push('1');


        if (!dtId) return result;

        if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
                return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
            })) {
            var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

            var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

            if (bls && bls.length > 0) return bls;
        }

        return result;
    };

    $scope.isSelected = function(v) {
        if ($scope.data && $scope.data.valueSets) {
            for (var i = 0; i < $scope.data.valueSets.length; i++) {
                if ($scope.data.valueSets[i].tableId == v.id) return true;
            }
        }
        return false;
    };

    $scope.selectValueSet = function(v) {
        if (!$scope.data) $scope.data = {};
        if (!$scope.data.valueSets) $scope.data.valueSets = [];
        $scope.data.valueSets.push({ tableId: v.id, bindingStrength: "R" });
    };

    $scope.deleteValueSet = function(index) {
        if (index >= 0) {
            $scope.data.valueSets.splice(index, 1);
        }
    };

    $scope.unselectValueSet = function(v) {
        var toBeDelBinding = _.find($scope.data.valueSets, function(binding) {
            return binding.tableId == v.id;
        });
        var index = $scope.data.valueSets.indexOf(toBeDelBinding);
        if (index >= 0) {
            $scope.data.valueSets.splice(index, 1);
        }
    };
    $scope.saveValueSet = function() {
        if ($scope.data) {
            $scope.coConstraintsTable.thenMapData[$scope.currentId][$scope.currentIndex] = angular.copy($scope.data);
            $scope.setDirty();
        }
    };
    $scope.saveUSER = function() {
        console.log($scope.coConstraintUSERDefinition);
        if ($scope.coConstraintUSERDefinition) {
            for (i in $scope.coConstraintsTable.userColumnDefinitionList) {
                if ($scope.coConstraintsTable.userColumnDefinitionList[i].id === $scope.coConstraintUSERDefinition.id) {
                    $scope.coConstraintsTable.userColumnDefinitionList[i].title = angular.copy($scope.title);
                }
            }
        } else {
            var userColumnDefinition = {};
            userColumnDefinition.title = $scope.title;
            if (userColumnDefinition) {
                userColumnDefinition.title = $scope.title;

                if (!$scope.coConstraintsTable) {
                    $scope.coConstraintsTable = {};
                    $scope.coConstraintsTable.rowSize = 0;
                }

                if (!$scope.coConstraintsTable.userColumnDefinitionList) {
                    $scope.coConstraintsTable.userColumnDefinitionList = [];
                    $scope.coConstraintsTable.userMapData = {};
                }

                if (!userColumnDefinition.id) {
                    userColumnDefinition.id = new ObjectId().toString();
                    $scope.coConstraintsTable.userColumnDefinitionList.push(userColumnDefinition);

                    $scope.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

                    for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
                        $scope.coConstraintsTable.userMapData[userColumnDefinition.id].push({});
                    }
                } else {

                    for (var i in $scope.coConstraintsTable.userColumnDefinitionList) {
                        if ($scope.coConstraintsTable.userColumnDefinitionList[i].id == userColumnDefinition.id) {
                            $scope.coConstraintsTable.userColumnDefinitionList[i] = userColumnDefinition;
                        }
                    }
                }
            }
        }

        $scope.setDirty();

    };
    $scope.isVariesDT = function() {
        if ($scope.targetNode) {
            if ($rootScope.datatypesMap[$scope.targetNode.datatype.id].name.toLowerCase() == 'varies') {
                $scope.coConstraintType = 'valueset';
                return true;
            }
        }

        return false;
    };

    $scope.updateFieldTHEN = function() {
        $scope.selectedComponentPosition = null;
        $scope.selectedSubComponentPosition = null;
        $scope.components = null;
        $scope.subComponents = null;
        $scope.primitive = true;
        $scope.dMReference = false;

        var field = _.find($scope.seg.fields, function(f) {
            return f.position == $scope.selectedFieldPosition;
        });

        $scope.targetNode = field;

        if ($rootScope.isDynamicMappingSegment) {
            console.log("=========This is DM segment!!=========");
            var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
                return item.hl7Version == $scope.seg.hl7Version && item.segmentName == $scope.seg.name;
            });

            if (mappingStructure) {
                if ($scope.seg.dynamicMappingDefinition && $scope.seg.dynamicMappingDefinition.mappingStructure) {
                    console.log("=========Found mapping structure!!=========");
                    mappingStructure = $scope.seg.dynamicMappingDefinition.mappingStructure;
                } else {
                    console.log("=========Not Found mapping structure and Default setting will be used!!=========");
                }

                var valueSetBinding = _.find($scope.seg.valueSetBindings, function(vsb) {
                    return vsb.location == mappingStructure.referenceLocation;
                });

                if (valueSetBinding) {
                    TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
                        $rootScope.dynamicMappingTable = tbl;
                    }, function() {

                    });
                }

                if ($scope.selectedFieldPosition == mappingStructure.referenceLocation) {
                    $scope.dMReference = true;
                }
            }
        }

        if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
            $scope.primitive = false;
            $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
        }
    };

    $scope.updateComponentTHEN = function() {
        $scope.selectedSubComponentPosition = null;
        $scope.subComponents = null;
        $scope.primitive = true;
        $scope.dMReference = false;

        var component = _.find($scope.components, function(c) {
            return c.position == $scope.selectedComponentPosition;
        });

        $scope.targetNode = component;
        if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
            $scope.primitive = false;
            $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
        }
    };

    $scope.updateSubComponentTHEN = function() {
        $scope.primitive = true;
        $scope.dMReference = false;
        var subComponent = _.find($scope.subComponents, function(sc) {
            return sc.position == $scope.selectedSubComponentPosition;
        });
        $scope.targetNode = subComponent;
    };
    $scope.saveTHEN = function() {
        var thenColumnDefinition = {};
        if ($scope.selectedCoConstraintTHENDefinition) {
            thenColumnDefinition.id = $scope.selectedCoConstraintTHENDefinition.id;
        }

        thenColumnDefinition.constraintType = $scope.coConstraintType;
        thenColumnDefinition.name = $scope.targetNode.name;
        thenColumnDefinition.usage = $scope.targetNode.usage;
        thenColumnDefinition.dtId = $scope.targetNode.datatype.id;
        thenColumnDefinition.primitive = $scope.primitive;
        thenColumnDefinition.dMReference = $scope.dMReference;

        if (thenColumnDefinition.dMReference) {
            thenColumnDefinition.constraintType = 'dmr';
        }

        if ($scope.selectedFieldPosition) {
            thenColumnDefinition.path = "" + $scope.selectedFieldPosition;
            thenColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
            thenColumnDefinition.type = "field";
            if ($scope.selectedComponentPosition) {
                thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedComponentPosition;
                thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
                thenColumnDefinition.type = "component";
                if ($scope.selectedSubComponentPosition) {
                    thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
                    thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
                    thenColumnDefinition.type = "subcomponent";
                }
            }
        }
        if (thenColumnDefinition) {
            if (!$scope.coConstraintsTable) {
                $scope.coConstraintsTable = {};
                $scope.coConstraintsTable.rowSize = 0;
            }

            if (!$scope.coConstraintsTable.thenColumnDefinitionList) {
                $scope.coConstraintsTable.thenColumnDefinitionList = [];
                $scope.coConstraintsTable.thenMapData = {};
            }

            if (!thenColumnDefinition.id) {
                thenColumnDefinition.id = new ObjectId().toString();
                $scope.coConstraintsTable.thenColumnDefinitionList.push(thenColumnDefinition);
                $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

                for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
                    $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id].push({});
                }
            } else {
                for (var i in $scope.coConstraintsTable.thenColumnDefinitionList) {
                    if ($scope.coConstraintsTable.thenColumnDefinitionList[i].id == thenColumnDefinition.id) {
                        $scope.coConstraintsTable.thenColumnDefinitionList[i] = thenColumnDefinition;
                    }
                }
            }
        }
        $scope.setDirty();
    };




    $scope.delCoConstraintIFDefinition = function(ifColumnDefinition) {
        $scope.coConstraintsTable.ifColumnDefinition = null;
        $scope.coConstraintsTable.ifColumnData = [];

        $scope.resetCoConstraintsTable();
        $scope.setDirty();
    };
    $scope.delCoConstraintTHENDefinition = function(columnDefinition) {
        var index = $scope.coConstraintsTable.thenColumnDefinitionList.indexOf(columnDefinition);

        if (index > -1) {
            $scope.coConstraintsTable.thenMapData[columnDefinition.id] = null;
            $scope.coConstraintsTable.thenColumnDefinitionList.splice(index, 1);
        };

        $scope.resetCoConstraintsTable();
        $scope.setDirty();
    };
    $scope.delCoConstraintUSERDefinition = function(columnDefinition) {
        console.log(columnDefinition);
        var index = $scope.coConstraintsTable.userColumnDefinitionList.indexOf(columnDefinition);
        console.log(index);
        if (index > -1) {
            $scope.coConstraintsTable.userMapData[columnDefinition.id] = null;
            $scope.coConstraintsTable.userColumnDefinitionList.splice(index, 1);
        };

        $scope.resetCoConstraintsTable();
        $scope.setDirty();
    };
    $scope.resetCoConstraintsTable = function() {
        if (!$scope.coConstraintsTable.ifColumnDefinition) {
            if (!$scope.coConstraintsTable.thenColumnDefinitionList || $scope.coConstraintsTable.thenColumnDefinitionList.length == 0) {
                if (!$scope.coConstraintsTable.userColumnDefinitionList || $scope.coConstraintsTable.userColumnDefinitionList.length == 0) {
                    $scope.coConstraintsTable = {};
                }
            }
        }
    };

    $scope.findOptions = function(dtId) {
        var result = [];
        result.push('1');


        if (!dtId) return result;

        if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
                return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
            })) {
            var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

            var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

            if (bls && bls.length > 0) return bls;
        }

        return result;
    };



    $scope.updateDynamicMappingInfo = function() {
        $scope.isDynamicMappingSegment = false;
        $scope.dynamicMappingTable = null;

        var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
            return item.hl7Version == $scope.seg.hl7Version && item.segmentName == $scope.seg.name;
        });

        if (mappingStructure) {
            $scope.isDynamicMappingSegment = true;
            console.log("=========This is DM segment!!=========");

            if ($scope.seg.dynamicMappingDefinition && $scope.seg.dynamicMappingDefinition.mappingStructure) {
                console.log("=========Found mapping structure!!=========");
                mappingStructure = $scope.seg.dynamicMappingDefinition.mappingStructure;
            } else {
                console.log("=========Not Found mapping structure and Default setting will be used!!=========");
            }

            var valueSetBinding = _.find($scope.seg.valueSetBindings, function(vsb) {
                return vsb.location == mappingStructure.referenceLocation;
            });

            if (valueSetBinding) {
                TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
                    $scope.dynamicMappingTable = tbl;
                }, function() {

                });
            }
        }
    };
    $scope.updateDynamicMappingInfo();



     $scope.addCoConstraintRow = function() {
        var isAdded = false;
        if(!$scope.coConstraintsTable.ifColumnData) $scope.coConstraintsTable.ifColumnData = [];
        if(!$scope.coConstraintsTable.thenMapData) $scope.coConstraintsTable.thenMapData = {};
        if(!$scope.coConstraintsTable.userMapData) $scope.coConstraintsTable.userMapData = {};

        if($scope.coConstraintsTable.ifColumnDefinition){
            var newIFData = {};
            newIFData.valueData = {};
            newIFData.bindingLocation = null;

            $scope.coConstraintsTable.ifColumnData.push(newIFData);
            isAdded = true;
        }

        if($scope.coConstraintsTable.thenColumnDefinitionList){
            for (var i = 0, len1 = $scope.coConstraintsTable.thenColumnDefinitionList.length; i < len1; i++) {
                var thenColumnDefinition = $scope.coConstraintsTable.thenColumnDefinitionList[i];

                var newTHENData = {};
                newTHENData.valueData = {};
                newTHENData.valueSets = [];

                if(!$scope.coConstraintsTable.thenMapData[thenColumnDefinition.id]) $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

                $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id].push(newTHENData);
                isAdded = true;
            };
        }

        if($scope.coConstraintsTable.userColumnDefinitionList){
            for (var i = 0, len1 = $scope.coConstraintsTable.userColumnDefinitionList.length; i < len1; i++) {
                var userColumnDefinition = $scope.coConstraintsTable.userColumnDefinitionList[i];

                var newUSERData = {};
                newUSERData.text = "";

                if(!$scope.coConstraintsTable.userMapData[userColumnDefinition.id]) $scope.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

                $scope.coConstraintsTable.userMapData[userColumnDefinition.id].push(newUSERData);
                isAdded = true;
            };
        }

        if(isAdded) {
            $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize + 1;
            $scope.setDirty();
        }
    };
    $scope.cloneCoConstraintRow = function (rowIndex){
        if($scope.coConstraintsTable.ifColumnDefinition){
            if($scope.coConstraintsTable.ifColumnData){
                $scope.coConstraintsTable.ifColumnData.push(angular.copy($scope.coConstraintsTable.ifColumnData[rowIndex]));
            }
        }

        if($scope.coConstraintsTable.thenColumnDefinitionList && $scope.coConstraintsTable.thenColumnDefinitionList.length > 0){
            if($scope.coConstraintsTable.thenMapData){
                for(var i in $scope.coConstraintsTable.thenColumnDefinitionList){
                    if($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id]){
                        $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id].push(angular.copy($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id][rowIndex]));
                    }
                }
            }
        }

        if($scope.coConstraintsTable.userColumnDefinitionList && $scope.coConstraintsTable.userColumnDefinitionList.length > 0){
            if($scope.coConstraintsTable.userMapData){
                for(var i in $scope.coConstraintsTable.userColumnDefinitionList){
                    if($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id]){
                        $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id].push(angular.copy($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id][rowIndex]));
                    }
                }
            }
        }

        $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize + 1;

        $scope.setDirty();
    };


    $scope.delCoConstraintRow = function (rowIndex){
        if($scope.coConstraintsTable.ifColumnDefinition){
            $scope.coConstraintsTable.ifColumnData.splice(rowIndex, 1);
        }

        if($scope.coConstraintsTable.thenColumnDefinitionList && $scope.coConstraintsTable.thenColumnDefinitionList.length > 0){
            if($scope.coConstraintsTable.thenMapData){
                for(var i in $scope.coConstraintsTable.thenColumnDefinitionList){
                    if($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id]){
                        $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id].splice(rowIndex, 1);
                    }
                }
            }
        }

        if($scope.coConstraintsTable.userColumnDefinitionList && $scope.coConstraintsTable.userColumnDefinitionList.length > 0){
            if($scope.coConstraintsTable.userMapData){
                for(var i in $scope.coConstraintsTable.userColumnDefinitionList){
                    if($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id]){
                        $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id].splice(rowIndex, 1);
                    }
                }
            }
        }

        $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize - 1;

        $scope.setDirty();
    };




    $scope.cancel = function() {
        $mdDialog.hide();
    }
    $scope.saveclose = function() {
        $mdDialog.hide($scope.coConstraintsTable);
        
    }

});
angular.module('igl').controller('AddDynamicMappingCtrlInPc', function($scope, $mdDialog, node, context, $rootScope, TableService) {
    $scope.node = angular.copy(node);
    console.log($rootScope.dynamicMappingTable);
    $scope.changed = false;
    $scope.setDirty = function() {
        $scope.changed = true;
    };
    $scope.findDynamicMapping = function(node) {
        if (node.type === "segmentRef") {
            if (node.attributes.dynamicMappingDefinition && node.attributes.dynamicMappingDefinition.dynamicMappingItems.length > 0) {
                return node.attributes.dynamicMappingDefinition;
            } else {
                return node.attributes.oldDynamicMappingDefinition;
            }
        }
        return null;
    };
    $scope.findingBindings = function(node) {
        if (node.type === "segmentRef") {
            if (node.valueSetBindings && node.valueSetBindings.length > 0) {
                return node.valueSetBindings;
            } else {
                return node.oldValueSetBindings;
            }
        }
        return null;
    };
    $scope.updateDynamicMappingInfo = function() {
        $scope.isDynamicMappingSegment = false;
        $scope.dynamicMappingTable = null;

        var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
            return item.hl7Version == $rootScope.segmentsMap[$scope.node.attributes.ref.id].hl7Version && item.segmentName == $rootScope.segmentsMap[$scope.node.attributes.ref.id].name;
        });

        if (mappingStructure) {
            $rootScope.isDynamicMappingSegment = true;
            console.log("=========This is DM segment!!=========");

            if ($scope.findDynamicMapping($scope.node) && $scope.findDynamicMapping($scope.node).mappingStructure) {
                console.log("=========Found mapping structure!!=========");
                mappingStructure = $scope.findDynamicMapping($scope.node).mappingStructure;
            } else {
                console.log("=========Not Found mapping structure and Default setting will be used!!=========");
            }

            var valueSetBinding = _.find($scope.findingBindings($scope.node), function(vsb) {
                return vsb.location == mappingStructure.referenceLocation;
            });

            if (valueSetBinding) {
                TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
                    $scope.dynamicMappingTable = tbl;
                }, function() {

                });
            }
        }
    };
    $scope.updateDynamicMappingInfo();



    $scope.dynamicMappingDefinition = $scope.findDynamicMapping($scope.node);
    $scope.deleteMappingItem = function(item) {
        var index = $scope.dynamicMappingDefinition.dynamicMappingItems.indexOf(item);
        if (index >= 0) {
            $scope.dynamicMappingDefinition.dynamicMappingItems.splice(index, 1);
            $scope.setDirty();
        }
    };

    $scope.addMappingItem = function() {
        var newItem = {};
        newItem.firstReferenceValue = null;
        newItem.secondReferenceValue = null;
        newItem.datatypeId = null;
        $scope.dynamicMappingDefinition.dynamicMappingItems.push(newItem);
        $scope.setDirty();
    };

    $scope.getDefaultStatus = function(code) {
        var item = _.find($scope.dynamicMappingDefinition.dynamicMappingItems, function(item) {
            return item.firstReferenceValue == code.value;
        });

        if (!item) return 'full';
        if (item) {
            if (item.secondReferenceValue && item.secondReferenceValue != '') return 'partial';
            return 'empty';
        }
    };

    console.log($scope.node);
    $scope.cancel = function() {
        $mdDialog.hide();
    }
    $scope.saveclose = function() {
        $mdDialog.hide($scope.dynamicMappingDefinition);
    }

});
angular.module('igl').controller('addCommentCtrl',
    function($scope, $rootScope, $modalInstance, field, PcService, $http, SegmentLibrarySvc) {
        $scope.field = field;
        $scope.close = function() {
            //$scope.field.attributes.comment = $scope.comment;
            $modalInstance.close();
        };
    });

angular.module('igl').controller('addDefTextCtrl',
    function($scope, $rootScope, $modalInstance, field, PcService, $http, SegmentLibrarySvc) {
        $scope.field = field;
        $scope.close = function() {
            //$scope.field.attributes.comment = $scope.comment;
            $modalInstance.close();
        };
    });


angular.module('igl').controller('applyPcToCtrl',
    function($scope, $rootScope, $modalInstance, pc, PcService, messages, $http, SegmentLibrarySvc, ngTreetableParams, CompositeMessageService) {
        if (pc.appliedTo === null) {
            pc.appliedTo = [];
        }
        $scope.applyToList = [];
        $scope.messages = messages;
        $scope.msgs = [];
        for (var i = 0; i < $scope.messages.length; i++) {
            $scope.msgs.push({
                id: $scope.messages[i].id,
                name: $scope.messages[i].name
            });
        }
        for (var j = 0; j < pc.appliedTo.length; j++) {
            for (var i = 0; i < $scope.msgs.length; i++) {
                if (pc.appliedTo[j].id === $scope.msgs[i].id) {
                    $scope.msgs.splice(i, 1);
                }
            }
        };
        $scope.ApplyToComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if ($scope.msgs !== undefined) {

                    if (parent) {
                        if (parent.children) {

                            return parent.children;
                        }

                    } else {
                        return $scope.msgs;
                    }

                }
            },
            getTemplate: function(node) {
                return 'applyTable';
            }
        });
        $scope.addApplyToMsg = function(node) {
            $scope.applyToList.push(node);
            var index = $scope.msgs.indexOf(node);
            if (index > -1) $scope.msgs.splice(index, 1);
            if ($scope.ApplyToComponentParams) {
                $scope.ApplyToComponentParams.refresh();
            }
        };
        $scope.removeSelectedApplyTo = function(applyTo) {
            var index = $scope.applyToList.indexOf(applyTo);
            if (index > -1) $scope.applyToList.splice(index, 1);
            $scope.msgs.push(applyTo);
            if ($scope.ApplyToComponentParams) {
                $scope.ApplyToComponentParams.refresh();
            }
        };
        $scope.apply = function() {

            for (var j = 0; j < $scope.applyToList.length; j++) {
                $rootScope.profileComponent.appliedTo.push({
                    id: $scope.applyToList[j].id,
                    name: $scope.applyToList[j].name
                });
                for (var i = 0; i < $rootScope.messages.children.length; i++) {
                    if ($rootScope.messages.children[i].id === $scope.applyToList[j].id) {
                        if (!$rootScope.messages.children[i].appliedPc) {
                            $rootScope.messages.children[i].appliedPc = [];
                        }
                        $rootScope.messages.children[i].appliedPc.push({
                            id: $scope.applyToList[j].id,
                            name: $scope.applyToList[j].name
                        });
                    }
                }


            }

            var processFields = function(fields) {
                for (var i = 0; i < fields.length; i++) {
                    fields[i].datatype = $rootScope.datatypesMap[fields[i].datatype.id];
                    if (fields[i].datatype.components.length > 0) {
                        fields[i].datatype.components = processFields(fields[i].datatype.components);
                    }

                }
                return fields;
            };
            var processMessage = function(message) {
                for (var i = 0; i < message.children.length; i++) {
                    if (message.children[i].type === "segmentRef") {
                        message.children[i].ref = $rootScope.segmentsMap[message.children[i].ref.id];
                        message.children[i].ref.fields = processFields(message.children[i].ref.fields);
                    } else if (message.children[i].type === "group") {
                        processMessage(message.children[i]);
                    }
                }
                return message;
            };

            var message = angular.copy($rootScope.messages.children[0]);

            var processedMsg = processMessage(message);

            CompositeMessageService.create(processedMsg).then(function(result) {
                $modalInstance.close();
            });


        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });



angular.module('igl').controller('addComponentsCtrl',
    function($scope, $rootScope, $mdDialog, messages, segments, segmentsMap, datatypesMap, currentPc, PcLibraryService, datatypes, ngTreetableParams, $http, SegmentLibrarySvc, PcService, orderByFilter) {
        $scope.selectedPC = [];
        console.log("current pc");
        console.log(currentPc);
        $scope.findPredicates = function(node) {

            var result = [];
            if (node) {
                var index = node.path.indexOf(".");
                var path = node.path.substr(index + 1);

                result = _.filter(node.parentGroupPredicates, function(p) {
                    if (p.context && p.context.type === "group") {
                        var tempath = path.replace(p.context.path + '.', '');
                        return $rootScope.refinePath(p.constraintTarget) == tempath
                    } else {
                        return $rootScope.refinePath(p.constraintTarget) == tempath;
                    }

                });
                console.log("$$$$$$$$$");
                console.log(result);
                if (result && result.length > 0) {
                    return result;
                }

                result = _.filter(node.parentPredicates, function(predicate) { return $rootScope.refinePath(predicate.constraintTarget) === path; });
                if (result && result.length > 0) {
                    return result;
                }


            }
            return result;

        };
        $scope.findingBindings = function(node) {
            var result = [];

            if (node && (node.type === "field" || node.type === "component")) {
                var index = node.path.indexOf(".");
                var path = node.path.substr(index + 1);
                result = _.filter(node.parentValueSetBindings, function(binding) { return binding.location == path; });
                for (var i = 0; i < result.length; i++) {
                    result[i].bindingFrom = 'segment';
                }

                if (result && result.length > 0) {
                    return result;
                }
            }
            return result;
        };
        $scope.findingComments = function(node) {
            var result = [];

            if (node) {
                var index = node.path.indexOf(".");
                var path = node.path.substr(index + 1);
                result = _.filter(node.parentComments, function(binding) { return binding.location == path; });
                for (var i = 0; i < result.length; i++) {
                    result[i].from = 'segment';
                }

                if (result && result.length > 0) {
                    return result;
                }
            }
            return result;
        };
        $scope.findingSingleElement = function(node) {
            var result = null;

            if (node && (node.type === "field" || node.type === "component")) {
                var index = node.path.indexOf(".");
                var path = node.path.substr(index + 1);
                result = _.find(node.parentSingleElementValues, function(binding) { return binding.location == path; });
                if (result) {
                    result.from = 'segment';
                    return result;
                }
            }
            return result;
        };


        $scope.MsgProfileComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if (messages !== undefined) {

                    if (parent) {
                        if (parent.children) {
                            for (var i = 0; i < parent.children.length; i++) {
                                if (parent.type === 'group') {

                                    parent.children[i].parent = parent.parent + '.' + parent.position;
                                    parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.children[i].parentComments = parent.parentComments;
                                    parent.children[i].parentPredicates = parent.parentPredicates;
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                                    parent.children[i].parentGroupPredicates = parent.predicates;
                                    var index = parent.children[i].parent.indexOf(".");
                                    var grpPath = parent.children[i].parent.substr(index + 1);
                                    for (var k = 0; k < parent.children[i].parentGroupPredicates.length; k++) {
                                        parent.children[i].parentGroupPredicates[k].context = {
                                            type: parent.type,
                                            name: parent.name,
                                            path: grpPath

                                        }
                                    }
                                    //parent.children[i].sourceId = parent.sourceId;
                                    parent.children[i].source = parent.source;

                                    if (parent.children[i].type === 'segmentRef') {

                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                                        parent.children[i].parentPredicates = parent.parentPredicates;
                                        //parent.children[i].parentGroupPredicates = parent.parentGroupPredicates;
                                        // parent.children[i].sourceId = parent.sourceId;
                                        parent.children[i].source = parent.source;
                                        parent.children[i].from = "message";


                                    }
                                } else if (parent.type === 'message') {
                                    parent.children[i].parent = parent.structID;
                                    parent.children[i].parentValueSetBindings = parent.valueSetBindings;
                                    parent.children[i].parentComments = parent.comments;
                                    parent.children[i].parentSingleElementValues = parent.singleElementValues;


                                    parent.children[i].parentPredicates = parent.predicates;
                                    //parent.children[i].sourceId = parent.id;
                                    parent.children[i].source = {};
                                    parent.children[i].source.messageId = parent.id;


                                    if (parent.children[i].type === 'segmentRef') {
                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                                        //parent.children[i].parentPredicates = parent.parentPredicates;

                                        parent.children[i].from = "message";

                                    }
                                } else if (parent.type === 'segmentRef') {
                                    parent.children[i].parent = parent.parent + '.' + parent.position;
                                    parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                                    parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.children[i].parentComments = parent.parentComments;
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                                    parent.children[i].parentPredicates = parent.parentPredicates;
                                    parent.children[i].parentGroupPredicates = parent.parentGroupPredicates;

                                    //parent.children[i].sourceId = parent.sourceId;
                                    parent.children[i].source = parent.source;
                                    parent.children[i].source.segmentId = parent.ref.id;

                                    parent.children[i].from = "message";


                                } else if (parent.type === 'field' || parent.type === 'component') {

                                    parent.children[i].parent = parent.parent + '.' + parent.position;
                                    parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                                    parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.children[i].parentComments = parent.parentComments;
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                                    parent.children[i].parentPredicates = parent.parentPredicates;
                                    parent.children[i].parentGroupPredicates = parent.parentGroupPredicates;

                                    //parent.children[i].sourceId = parent.sourceId;
                                    parent.children[i].source = parent.source;

                                    if (parent.type === "field") {
                                        parent.children[i].source.fieldDt = parent.datatype.id;
                                    } else if (parent.type === "component") {
                                        // parent.children[i].source.fieldDt = parent.source.fieldDt;
                                        parent.children[i].source.componentDt = parent.datatype.id;

                                    }

                                    parent.children[i].from = "message";


                                }

                            }
                            return parent.children;
                        }

                    } else {
                        return messages;
                    }

                }
            },
            getTemplate: function(node) {
                return 'MsgProfileComponentTable';
            }
        });
        $scope.removeSelectedComp = function(pc) {
            var index = $scope.selectedPC.indexOf(pc);
            if (index > -1) $scope.selectedPC.splice(index, 1);
        };

        $scope.addElementPc = function(node, event) {
            var currentScope = angular.element(event.target).scope();
            var pc = currentScope.node;
            var parent = currentScope.parentNode;
            console.log('parent Node details: ', currentScope.parentNode);
            console.log('selected Node details: ', currentScope.node);
            if (pc.type === 'message') {

                var newPc = {
                    id: new ObjectId().toString(),
                    type: pc.type,
                    name: pc.name,
                    path: pc.structID,
                    source: {
                        messageId: pc.id,
                    },
                    oldConformanceStatements: pc.conformanceStatements,
                    from: "message",
                    attributes: {
                        oldConformanceStatements: pc.conformanceStatements,
                        conformanceStatements: [],
                    },
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'segmentRef') {
                var newPc = {
                    id: new ObjectId().toString(),
                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    itemId: pc.id,
                    parentPredicates: pc.parentPredicates,
                    parentGroupPredicates: pc.parentGroupPredicates,

                    oldValueSetBindings: $rootScope.segmentsMap[pc.ref.id].valueSetBindings,
                    source: pc.source,
                    from: "message",
                    attributes: {
                        oldRef: {
                            id: $rootScope.segmentsMap[pc.ref.id].id,
                            name: $rootScope.segmentsMap[pc.ref.id].name,
                            ext: $rootScope.segmentsMap[pc.ref.id].ext,
                            label: $rootScope.segmentsMap[pc.ref.id].label,

                        },
                        ref: {
                            id: $rootScope.segmentsMap[pc.ref.id].id,
                            name: $rootScope.segmentsMap[pc.ref.id].name,
                            ext: $rootScope.segmentsMap[pc.ref.id].ext,
                            label: $rootScope.segmentsMap[pc.ref.id].label,

                        },
                        oldDynamicMappingDefinition: $rootScope.segmentsMap[pc.ref.id].dynamicMappingDefinition,
                        oldCoConstraintsTable: $rootScope.segmentsMap[pc.ref.id].coConstraintsTable,
                        oldUsage: pc.usage,
                        usage: null,
                        oldMin: pc.min,
                        min: null,
                        oldMax: pc.max,
                        max: null,
                        oldComment: pc.comment,
                        comment: null,
                        oldConformanceStatements: pc.conformanceStatements,
                        conformanceStatements: [],
                    },
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'group') {
                var newPc = {
                    id: new ObjectId().toString(),
                    name: pc.name,
                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    itemId: pc.id,
                    parentPredicates: pc.parentPredicates,
                    parentGroupPredicates: pc.parentGroupPredicates,

                    source: pc.source,
                    from: "message",
                    attributes: {
                        oldUsage: pc.usage,
                        usage: null,
                        oldMin: pc.min,
                        min: null,
                        oldMax: pc.max,
                        max: null,
                        oldComment: pc.comment,
                        comment: null,
                        oldConformanceStatements: pc.conformanceStatements,
                        conformanceStatements: [],
                    },
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'field') {
                if (parent.type === 'segment') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.label + '.' + pc.position,
                        pathExp: parent.label + '.' + pc.position,
                        itemId: pc.id,
                        parentValueSetBindings: pc.parentValueSetBindings,
                        parentComments: pc.parentComments,
                        parentSingleElementValues: pc.parentSingleElementValues,
                        parentPredicates: pc.parentPredicates,
                        parentGroupPredicates: pc.parentGroupPredicates,

                        source: pc.source,
                        from: "segment",
                        // oldValueSetBindings: $scope.findingBindings(pc),
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables,
                            oldUsage: pc.usage,
                            usage: null,
                            oldMin: pc.min,
                            min: null,
                            oldMax: pc.max,
                            max: null,
                            oldMinLength: pc.minLength,
                            minLength: null,
                            oldMaxLength: pc.maxLength,
                            maxLength: null,
                            oldConfLength: pc.confLength,
                            confLength: null,
                            oldComment: pc.comment,
                            comment: null,
                            text: pc.text
                        },
                        appliedTo: [],
                        version: ""
                    };
                } else if (parent.type === 'segmentRef') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.parent + '.' + parent.position + '.' + pc.position,
                        itemId: pc.id,
                        from: "message",
                        parentValueSetBindings: pc.parentValueSetBindings,
                        parentComments: pc.parentComments,
                        parentSingleElementValues: pc.parentSingleElementValues,
                        parentPredicates: pc.parentPredicates,
                        source: pc.source,
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables,
                            oldUsage: pc.usage,
                            usage: null,
                            oldMin: pc.min,
                            min: null,
                            oldMax: pc.max,
                            max: null,
                            oldMinLength: pc.minLength,
                            minLength: null,
                            oldMaxLength: pc.maxLength,
                            maxLength: null,
                            oldConfLength: pc.confLength,
                            confLength: null,
                            oldComment: pc.comment,
                            comment: null,
                            text: pc.text
                        },
                        appliedTo: [],
                        version: ""
                    };
                }
            } else if (pc.type === 'component') {

                var newPc = {
                    id: new ObjectId().toString(),
                    name: pc.name,
                    type: pc.type,
                    path: parent.parent + '.' + parent.position + '.' + pc.position,
                    parentValueSetBindings: pc.parentValueSetBindings,
                    parentSingleElementValues: pc.parentSingleElementValues,
                    parentComments: pc.parentComments,
                    parentPredicates: pc.parentPredicates,
                    parentGroupPredicates: pc.parentGroupPredicates,

                    source: pc.source,
                    itemId: pc.id,
                    from: parent.from,
                    attributes: {
                        oldDatatype: pc.datatype,
                        oldTables: pc.tables,
                        oldUsage: pc.usage,
                        usage: null,
                        oldMin: pc.min,
                        min: null,
                        oldMax: pc.max,
                        max: null,
                        oldMinLength: pc.minLength,
                        minLength: null,
                        oldMaxLength: pc.maxLength,
                        maxLength: null,
                        oldConfLength: pc.confLength,
                        confLength: null,
                        oldComment: pc.comment,
                        comment: null,
                        text: pc.text
                    },
                    appliedTo: [],
                    version: ""
                };



            } else if (pc.type === 'segment') {
                var newPc = {
                    id: new ObjectId().toString(),
                    name: $rootScope.segmentsMap[pc.id].name,
                    ext: $rootScope.segmentsMap[pc.id].ex,
                    type: "segmentRef",
                    path: $rootScope.segmentsMap[pc.id].label,
                    pathExp: $rootScope.segmentsMap[pc.id].label,
                    parentPredicates: pc.parentPredicates,
                    parentGroupPredicates: pc.parentGroupPredicates,

                    oldValueSetBindings: pc.valueSetBindings,

                    source: pc.source,
                    from: "segment",
                    itemId: pc.id,
                    attributes: {
                        ref: {
                            id: $rootScope.segmentsMap[pc.id].id,
                            name: $rootScope.segmentsMap[pc.id].name,
                            ext: $rootScope.segmentsMap[pc.id].ext,
                            label: $rootScope.segmentsMap[pc.id].label,

                        },
                        oldRef: {
                            id: $rootScope.segmentsMap[pc.id].id,
                            name: $rootScope.segmentsMap[pc.id].name,
                            ext: $rootScope.segmentsMap[pc.id].ext,
                            label: $rootScope.segmentsMap[pc.id].label,

                        },
                        oldDynamicMappingDefinition: pc.dynamicMappingDefinition,
                        oldConformanceStatements: pc.conformanceStatements,
                        conformanceStatements: [],
                        oldCoConstraintsTable: $rootScope.segmentsMap[pc.ref.id].coConstraintsTable,

                    },
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'datatype') {

                var newPc = {
                    id: new ObjectId().toString(),
                    name: $rootScope.datatypesMap[pc.id].label,
                    ext: $rootScope.datatypesMap[pc.id].ex,
                    type: pc.type,
                    path: $rootScope.datatypesMap[pc.id].label,
                    itemId: pc.id,
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };


            };
            console.log("newPc");
            console.log(newPc);
            if (newPc.type !== "segmentRef") {
                newPc.oldValueSetBindings = $scope.findingBindings(newPc);
            }

            // newPc.oldSingleElementValues = $scope.findingSingleElement(newPc);
            newPc.oldComments = $scope.findingComments(newPc);
            newPc.oldPredicates = $scope.findPredicates(newPc);
            $scope.selectedPC.push(newPc);
        };

        $scope.SegProfileComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if (segments !== undefined) {
                    if (parent) {
                        if (parent.fields) {
                            for (var i = 0; i < parent.fields.length; i++) {
                                if (parent.type === "segment") {
                                    parent.fields[i].parent = parent.label;
                                    parent.fields[i].parentValueSetBindings = parent.valueSetBindings;
                                    parent.fields[i].parentSingleElementValues = parent.singleElementValues;
                                    parent.fields[i].parentComments = parent.comments;
                                    parent.fields[i].parentPredicates = parent.predicates;
                                    parent.fields[i].source = {};
                                    parent.fields[i].source.segmentId = parent.id;
                                    //parent.fields[i].sourceId = parent.id;

                                    parent.fields[i].from = "segment";
                                }
                                if (parent.type === "field" || parent.type === "component") {
                                    parent.fields[i].parent = parent.parent + '.' + parent.position;
                                    parent.fields[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.fields[i].parentSingleElementValues = parent.parentSingleElementValues;
                                    parent.fields[i].parentComments = parent.parentComments;
                                    parent.fields[i].parentPredicates = parent.parentPredicates;
                                    parent.fields[i].source = {};
                                    parent.fields[i].source.segmentId = parent.source.segmentId;
                                    if (parent.type === "field") {
                                        parent.fields[i].source.fieldDt = parent.datatype.id;
                                    } else if (parent.type === "component") {
                                        parent.fields[i].source.fieldDt = parent.source.fieldDt;
                                        parent.fields[i].source.componentDt = parent.datatype.id;

                                    }

                                    parent.fields[i].from = "segment";

                                }
                                if (parent.fields[i].datatype) {
                                    parent.fields[i].fields = datatypesMap[parent.fields[i].datatype.id].components;
                                }
                            }
                            return parent.fields;
                        }

                    } else {
                        return orderByFilter(segments, 'name');;
                    }

                }
            },
            getTemplate: function(node) {
                return 'SegProfileComponentTable';
            }
        });

        $scope.DTProfileComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if (datatypes !== undefined) {
                    if (parent) {
                        if (parent.components) {
                            for (var i = 0; i < parent.components.length; i++) {
                                if (parent.type === "datatype") {
                                    parent.components[i].parent = parent.label;
                                }
                                if (parent.type === "component") {
                                    parent.components[i].parent = parent.parent + '.' + parent.position;
                                }
                                if (parent.components[i].datatype) {
                                    parent.components[i].components = datatypesMap[parent.components[i].datatype.id].components;
                                }
                            }
                            return parent.components;
                        }

                    } else {
                        return datatypes;
                    }

                }
            },
            getTemplate: function(node) {
                return 'DTProfileComponentTable';
            }
        });
        $scope.add = function() {
            // PcLibraryService.addComponentsToLib($rootScope.igdocument.id, $scope.selectedPC).then(function(ig) {
            //     $rootScope.igdocument.profile.profileComponentLibrary.children = ig.profile.profileComponentLibrary.children;
            //     if ($scope.profileComponentParams) {
            //         $scope.profileComponentParams.refresh();
            //     }
            //     $modalInstance.close();
            // });
            var position = currentPc.children.length + 1;
            for (var i = 0; i < $scope.selectedPC.length; i++) {
                $scope.selectedPC[i].position = position;
                position++;
                $rootScope.profileComponent.children.push($scope.selectedPC[i]);
            }
            console.log($rootScope.profileComponent);
            $mdDialog.hide();

            // PcService.addPCs(currentPc.id, $scope.selectedPC).then(function(profileC) {
            //     $rootScope.profileComponent = profileC;
            //     
            // });
        };


        $scope.cancel = function() {
            $mdDialog.hide();
        };
    });
angular.module('igl').controller('EditSingleElementCtrlInPc', function($scope, $rootScope, $mdDialog, userInfoService, currentNode) {
    $scope.currentNode = currentNode;

    $scope.sevVale = '';
    if (currentNode.singleElementValues && currentNode.singleElementValues.value !== null && currentNode.singleElementValues.location !== null) {
        $scope.sevVale = currentNode.singleElementValues.value;

    } else if (currentNode.oldSingleElementValues && currentNode.oldSingleElementValues.value !== null && currentNode.oldSingleElementValues.location !== null) {
        $scope.sevVale = currentNode.oldSingleElementValues.value;

    }


    $scope.cancel = function() {
        $mdDialog.hide();
    };

    $scope.close = function() {
        $mdDialog.hide($scope.sevVale);
    };
});
angular.module('igl').controller('EditCommentCtrlInPc', function($scope, $rootScope, $mdDialog, userInfoService, currentNode, currentComment, disabled, type) {
    $scope.currentNode = currentNode;
    $scope.currentComment = currentComment;
    var currentPath = null;
    var index = currentNode.path.indexOf(".");
    currentPath = currentNode.path.substr(index + 1);


    $scope.disabled = disabled;
    $scope.title = '';


    $scope.title = 'Comment of ' + $scope.currentNode.path;

    $scope.descriptionText = '';

    if ($scope.currentComment) $scope.descriptionText = $scope.currentComment.description;

    $scope.cancel = function() {
       $mdDialog.hide();
    };

    $scope.close = function() {
        if ($scope.currentComment) {
            $scope.currentComment.description = $scope.descriptionText;
            $scope.currentComment.lastUpdatedDate = new Date();
        } else {
            var newComment = {};

            newComment.description = $scope.descriptionText;
            newComment.location = currentPath;
            newComment.lastUpdatedDate = new Date();
            if (!currentNode.comments) {
                currentNode.comments = [];
            }
            currentNode.comments.push(newComment);
        }

        $mdDialog.hide($scope.currentNode);
    };
});



angular.module('igl').controller('TableBindingForMsgCtrl', function($scope, $modalInstance, currentNode, $rootScope, blockUI, TableService) {
    $scope.changed = false;
    console.log(currentNode);
    $scope.currentNode = currentNode;
    $scope.currentNode.locationPath = currentNode.path;
    $scope.isSingleValueSetAllowed = false;
    $scope.valueSetSelectedForSingleCode = null;
    $scope.mCode = null;
    $scope.mCodeSystem = null;

    $scope.singleCodeInit = function() {
        $scope.valueSetSelectedForSingleCode = null;
        $scope.mCode = null;
        $scope.mCodeSystem = null;
    };
    $scope.addManualCode = function() {
        $scope.selectedValueSetBindings = [];
        var code = {};
        code.value = $scope.mCode;
        code.codeSystem = $scope.mCodeSystem;
        $scope.selectedValueSetBindings.push({ tableId: null, location: positionPath, usage: $scope.currentNode.usage, type: "singlecode", code: code });
        $scope.changed = true;
    };

    if (_.find($rootScope.config.singleValueSetDTs, function(singleValueSetDTs) {
            if ($scope.currentNode.attributes.datatype) {
                return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].name;

            } else {
                return singleValueSetDTs == $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].name;

            }
        })) $scope.isSingleValueSetAllowed = true;

    var positionPath = '';

    var index = currentNode.path.indexOf(".");
    positionPath = currentNode.path.substr(index + 1);
    if (!currentNode.valueSetBindings) {
        $scope.selectedValueSetBindings = angular.copy(_.filter(currentNode.oldValueSetBindings, function(binding) { return binding.location == positionPath; }));

    } else {
        $scope.selectedValueSetBindings = angular.copy(_.filter(currentNode.valueSetBindings, function(binding) { return binding.location == positionPath; }));

    }
    $scope.listOfBindingLocations = null;

    if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
            if ($scope.currentNode.attributes.datatype) {
                return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].name;

            } else {
                return valueSetAllowedDT == $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].name;

            }
        })) {
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if (!$scope.selectedValueSetBindings[i].bindingLocation || $scope.selectedValueSetBindings[i].bindingLocation == '') {
                $scope.selectedValueSetBindings[i].bindingLocation = "1";
            }
        }
        var hl7Version = null
        if ($scope.currentNode.attributes.datatype) {
            hl7Version = $rootScope.datatypesMap[$scope.currentNode.attributes.datatype.id].hl7Version;

        } else {
            hl7Version = $rootScope.datatypesMap[$scope.currentNode.attributes.oldDatatype.id].hl7Version;

        }
        if (!hl7Version) hl7Version = "2.5.1";

        $scope.listOfBindingLocations = $rootScope.config.bindingLocationListByHL7Version[hl7Version];
    };

    $scope.deleteBinding = function(binding) {
        var index = $scope.selectedValueSetBindings.indexOf(binding);
        if (index >= 0) {
            $scope.selectedValueSetBindings.splice(index, 1);
        }
        $scope.changed = true;
    };

    $scope.isSelected = function(v) {
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if ($scope.selectedValueSetBindings[i].tableId == v.id) return true;
        }
        return false;
    };

    $scope.selectValueSet = function(v) {
        if ($scope.isSingleValueSetAllowed) $scope.selectedValueSetBindings = [];
        if ($scope.selectedValueSetBindings.length > 0 && $scope.selectedValueSetBindings[0].type == 'singlecode') $scope.selectedValueSetBindings = [];
        if ($scope.listOfBindingLocations) {
            $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: positionPath, bindingLocation: "1", usage: currentNode.usage, type: "valueset" });
        } else {
            $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: positionPath, usage: currentNode.usage, type: "valueset" });
        }
        $scope.changed = true;
    };

    $scope.unselectValueSet = function(v) {
        var toBeDelBinding = _.find($scope.selectedValueSetBindings, function(binding) {
            return binding.tableId == v.id;
        });
        var index = $scope.selectedValueSetBindings.indexOf(toBeDelBinding);
        if (index >= 0) {
            $scope.selectedValueSetBindings.splice(index, 1);
        }
        $scope.changed = true;
    };
    $scope.selectValueSetForSingleCode = function(v) {
        console.log(v);
        TableService.getOne(v.id).then(function(tbl) {
            $scope.valueSetSelectedForSingleCode = tbl;
        }, function() {});
    };
    $scope.isCodeSelected = function(c) {
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if ($scope.selectedValueSetBindings[i].code) {
                if ($scope.selectedValueSetBindings[i].code.id == c.id) return true;
            }
        }
        return false;
    };
    $scope.selectCode = function(c) {
        $scope.selectedValueSetBindings = [];
        $scope.selectedValueSetBindings.push({ tableId: $scope.valueSetSelectedForSingleCode.id, location: positionPath, usage: currentNode.usage, type: "singlecode", code: c });
        $scope.changed = true;
    };
    $scope.unselectCode = function(c) {
        $scope.selectedValueSetBindings = [];
        $scope.changed = true;
    };


    $scope.saveMapping = function() {
        blockUI.start();
        // var otherValueSetBindings = angular.copy(_.filter($rootScope.message.valueSetBindings, function(binding) { return binding.location != positionPath; }));
        //$rootScope.message.valueSetBindings = $scope.selectedValueSetBindings.concat(otherValueSetBindings);
        currentNode.valueSetBindings = $scope.selectedValueSetBindings;
        blockUI.stop();

        $modalInstance.close(currentNode);
    };

    $scope.ok = function() {
        $modalInstance.dismiss('cancel');
    };

});


angular.module('igl').controller('GlobalPredicateCtrlInPc', function($scope, $modalInstance, node, $rootScope, $q) {
    console.log(node);
    var index = node.path.indexOf(".");
    var path = node.path.substr(index + 1);
    var pathArray = [];

    if (path) pathArray = path.split('.');

    var positionPath = '';
    for (var i in pathArray) {

        if (positionPath === '') {

            positionPath = pathArray[i] + '[1]';
        } else {

            positionPath = positionPath + '.' + pathArray[i] + '[1]';
        }

        // var position = pathArray[i].split('[')[0];
        //positionPath = positionPath + '.' + position;
    }

    //if (positionPath != '') positionPath = positionPath.substr(1);

    $scope.selectedMessage = angular.copy($rootScope.messagesMap[node.source.messageId]);


    $scope.selectedMessage.pathInfoSet = [];
    $scope.selectedNode = angular.copy(node);
    $scope.selectedNode.locationPath = angular.copy($scope.selectedNode.path);
    $scope.selectedNode.path = positionPath;
    console.log("$scope.selectedNode.path ");
    console.log($scope.selectedNode.path);

    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = null;
    $scope.targetContext = null;
    $scope.treeDataForMessage = [];
    $scope.constraintType = 'Plain';
    $scope.firstNodeData = null;
    $scope.secondNodeData = null;
    $scope.changed = false;
    $rootScope.processMessageTree($scope.selectedMessage);
    $scope.treeDataForMessage.push($scope.selectedMessage);
    $scope.draggingStatus = null;
    $scope.listGlobalPredicates = [];
    $scope.existingPredicate = null;
    $scope.existingContext = null;
    $scope.tempPredicates = [];
    $scope.predicateData = null;
    $scope.contextKey = null;

    $scope.setChanged = function() {
        $scope.changed = true;
    };

    $scope.toggleChildren = function(data) {
        data.childrenVisible = !data.childrenVisible;
        data.folderClass = data.childrenVisible ? "fa-minus" : "fa-plus";
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

    $scope.beforeNodeDrop = function() {
        var deferred = $q.defer();
        if ($scope.draggingStatus === 'ContextTreeNodeDragging') {
            deferred.resolve();
        } else {
            deferred.reject();
        }
        return deferred.promise;
    };

    $scope.afterPredicateDrop = function() {
        $scope.draggingStatus = null;
        $scope.existingPredicate = $scope.predicateData;
        $scope.existingContext = $scope.selectedContextNode;
        if (!$scope.existingContext.positionPath || $scope.existingContext.positionPath == '') {
            $scope.existingPredicate.constraintTarget = $scope.selectedNode.path;
            $scope.existingPredicate.context = {
                type: $scope.existingContext.type,
                name: $scope.existingContext.structID,
                path: ''
            };
        } else {
            $scope.existingPredicate.constraintTarget = $scope.selectedNode.path.replace($scope.existingContext.positionPath + '.', '');
            $scope.existingPredicate.context = {
                type: $scope.existingContext.type,
                name: $scope.existingContext.name,
                path: $rootScope.refinePath($scope.existingContext.positionPath)
            };
        }


    };

    $scope.selectContext = function(selectedContextNode) {
        $scope.contextKey = new ObjectId().toString();
        $scope.selectedContextNode = selectedContextNode;
        $scope.selectedContextNode.pathInfoSet = [];
        $scope.generatePathInfo($scope.selectedContextNode, ".", ".", "1", false, null, $scope.contextKey);
        console.log("$scope.selectedContextNode");
        console.log($scope.selectedContextNode);

        $scope.initPredicate();
        $scope.initComplexPredicate();
        $scope.tempPredicates = [];
    };

    $scope.afterFirstNodeDrop = function() {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_1 = $scope.firstNodeData.pathInfoSet;
        $scope.generateFirstPositionAndLocationPath();
    };

    $scope.afterSecondNodeDrop = function() {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_2 = $scope.secondNodeData.pathInfoSet;
        $scope.generateSecondPositionAndLocationPath();
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
            $scope.newConstraint.location_1 = locationPath.substr(1);
        }
    }

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
            $scope.newConstraint.location_2 = locationPath.substr(1);
        }
    }

    $scope.draggingPredicate = function(event, ui, nodeData) {
        $scope.draggingStatus = 'PredicateDragging';
    };

    $scope.draggingNodeFromContextTree = function(event, ui, nodeData) {
        $scope.draggingStatus = 'ContextTreeNodeDragging';
    };

    $scope.generatePathInfo = function(current, positionNumber, locationName, instanceNumber, isInstanceNumberEditable, nodeName, key) {
        var pathInfo = {};
        pathInfo.positionNumber = positionNumber;
        pathInfo.locationName = locationName;
        pathInfo.nodeName = nodeName;
        pathInfo.instanceNumber = instanceNumber;
        pathInfo.isInstanceNumberEditable = isInstanceNumberEditable;
        current.pathInfoSet.push(pathInfo);
        current.contextKey = key;

        if (current.type == 'message' || current.type == 'group') {
            for (var i in current.children) {
                var segGroup = current.children[i];
                segGroup.pathInfoSet = angular.copy(current.pathInfoSet);
                var childPositionNumber = segGroup.position;
                var childLocationName = '';
                var childNodeName = '';
                var childInstanceNumber = "1";
                var childisInstanceNumberEditable = false;
                if (segGroup.max != '1') {
                    childInstanceNumber = '*';
                    childisInstanceNumberEditable = true;
                }
                if (segGroup.type == 'group') {
                    childNodeName = segGroup.name;
                    childLocationName = segGroup.name.substr(segGroup.name.lastIndexOf('.') + 1);
                } else {
                    var s = angular.copy($rootScope.segmentsMap[segGroup.ref.id]);
                    s.id = new ObjectId().toString();
                    childLocationName = s.name;
                    childNodeName = s.name;
                    segGroup.segment = s;
                }
                $scope.generatePathInfo(segGroup, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
            }
        } else if (current.type == 'segmentRef') {
            var seg = current.segment;
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
                $scope.generatePathInfo(f, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
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
                $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
            }
        }
    };

    $scope.initComplexPredicate = function() {
        $scope.constraints = [];
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
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
            constraintId: null,
            contraintType: null,
            value: null,
            value2: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            trueUsage: null,
            falseUsage: null,
        });
    };

    $scope.addFreeTextPredicate = function() {
        var cp = $rootScope.generateFreeTextPredicate('NOT Assigned', $scope.newConstraint);
        $scope.tempPredicates.push(cp);
        $scope.initPredicate();
    };

    $scope.addPredicate = function() {
        var cp = $rootScope.generatePredicate('NOT Assigned', $scope.newConstraint);
        $scope.tempPredicates.push(cp);
        $scope.initPredicate();
    };

    $scope.addComplexPredicate = function() {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
        $scope.tempPredicates.push($scope.complexConstraint);
        $scope.initComplexPredicate();
    };

    $scope.deletePredicate = function() {
        $scope.existingPredicate = null;
        $scope.existingContext = null;
        $scope.setChanged();
    };

    $scope.deleteTempPredicate = function(predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss();
    };

    $scope.saveclose = function() {
        $scope.deleteExistingPredicate($scope.selectedMessage);

        if ($scope.existingPredicate != null) {
            //$scope.addChangedPredicate($scope.selectedMessage);
            console.log("$scope.existingPredicate");
            console.log($scope.existingPredicate);

        }

        // $rootScope.recordChanged();
        // $modalInstance.close($scope.selectedMessage);
        $modalInstance.close($scope.existingPredicate);

    };

    $scope.addChangedPredicate = function(current) {
        console.log("----------------------------------");
        console.log("CurrentPATH::::" + current.positionPath);
        console.log($scope.existingContext.positionPath);
        if (current.positionPath == $scope.existingContext.positionPath) {
            console.log($scope.existingPredicate);
            current.predicates.push($scope.existingPredicate);
        }

        if (current.type == 'message' || current.type == 'group') {
            for (var i in current.children) {
                $scope.addChangedPredicate(current.children[i]);
            }
        }
    }

    $scope.deleteExistingPredicate = function(current) {
        if (current.predicates && current.predicates.length > 0) {
            var toBeDeletePredicate = null;
            for (var i in current.predicates) {
                var positionPath = null;
                if (current.positionPath == null || current.positionPath == '') {
                    var positionPath = current.predicates[i].constraintTarget;
                } else {
                    var positionPath = current.positionPath + '.' + current.predicates[i].constraintTarget;
                }
                if (positionPath == $scope.selectedNode.path) {
                    toBeDeletePredicate = i;
                }
            }
            if (toBeDeletePredicate != null) current.predicates.splice(toBeDeletePredicate, 1);
        }
        if (current.type == 'message' || current.type == 'group') {
            for (var i in current.children) {
                $scope.deleteExistingPredicate(current.children[i]);
            }
        }
    };

    $scope.findAllGlobalPredicates = function() {
        $scope.listGlobalPredicates = [];
        $scope.travelMessage($scope.selectedMessage, '');

    };
    $scope.findExistingPredicate = function() {


        if ($scope.selectedNode.predicates && $scope.selectedNode.predicates.length > 0) {

            return $scope.selectedNode.predicates[0];

        } else {

            return $scope.selectedNode.oldPredicates[0];

        }


    };


    $scope.travelMessage = function(current, parrentPositionPath) {
        if (current.predicates && current.predicates.length > 0) {
            $scope.listGlobalPredicates.push(current);

            for (var i in current.predicates) {
                var positionPath = null;
                if (current.positionPath == null || current.positionPath == '') {
                    var positionPath = current.predicates[i].constraintTarget;
                } else {
                    var positionPath = current.positionPath + '.' + current.predicates[i].constraintTarget;
                }
                if (positionPath == $scope.selectedNode.path) {
                    $scope.existingPredicate = current.predicates[i];
                    console.log("-----------");
                    console.log(current);
                    $scope.existingContext = current;
                }
            }
        }

        if (current.type == 'message' || current.type == 'group') {
            for (var i in current.children) {
                var segGroup = current.children[i];

                if (parrentPositionPath == '') {
                    segGroup.positionPath = segGroup.position + '[1]';
                } else {
                    segGroup.positionPath = parrentPositionPath + '.' + segGroup.position + '[1]';
                }

                $scope.travelMessage(segGroup, segGroup.positionPath);
            }
        }
    };

    $scope.initPredicate();
    $scope.initComplexPredicate();
    $scope.findAllGlobalPredicates();
    $scope.existingPredicate = $scope.findExistingPredicate();
    console.log("first");
    console.log($scope.existingPredicate);
    $scope.generatePathInfo($scope.selectedMessage, ".", ".", "1", false, null, 'default');

});


angular.module('igl').controller('PredicateSegmentCtrlInPc', function($scope, $modalInstance, node, $rootScope, $q) {
    console.log(node);
    var index = node.path.indexOf(".");
    var path = node.path.substr(index + 1);
    var pathArray = [];

    if (path) pathArray = path.split('.');

    var positionPath = '';
    for (var i in pathArray) {

        if (positionPath === '') {

            positionPath = pathArray[i] + '[1]';
        } else {

            positionPath = positionPath + '.' + pathArray[i] + '[1]';
        }

        // var position = pathArray[i].split('[')[0];
        //positionPath = positionPath + '.' + position;
    }

    //if (positionPath != '') positionPath = positionPath.substr(1);
    console.log(path);
    console.log(positionPath);

    $scope.selectedNode = angular.copy(node);
    $scope.selectedNode.locationPath = angular.copy($scope.selectedNode.path);
    $scope.selectedNode.position = path;
    $scope.constraintType = 'Plain';
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.changed = false;
    $scope.existingPredicate = null;
    $scope.tempPredicates = [];
    $scope.selectedSegment = angular.copy($rootScope.segmentsMap[node.source.segmentId]);
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

    $scope.afterFirstNodeDrop = function() {
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
            $scope.newConstraint.location_1 = $scope.selectedSegment.name + '-' + locationPath.substr(1);
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
            $scope.newConstraint.location_2 = $scope.selectedSegment.name + '-' + locationPath.substr(1);
        }
    };

    $scope.findExistingPredicate = function() {
        console.log("seg");
        console.log($scope.selectedSegment);
        console.log($scope.selectedNode);
        if ($scope.selectedNode.predicates && $scope.selectedNode.predicates.length > 0) {
            for (var i = 0, len1 = $scope.selectedNode.predicates.length; i < len1; i++) {
                if ($scope.selectedNode.predicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
                    return $scope.selectedNode.predicates[i];
            }
        } else {
            for (var i = 0, len1 = $scope.selectedNode.oldPredicates.length; i < len1; i++) {
                if ($scope.selectedNode.oldPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0)
                    return $scope.selectedNode.oldPredicates[i];
            }
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
        // $scope.selectedSegment.predicates.push($scope.existingPredicate);
        // $rootScope.recordChanged();
        $modalInstance.close($scope.existingPredicate);
    };

    $scope.initPredicate();
    $scope.initComplexPredicate();
    $scope.existingPredicate = $scope.findExistingPredicate();

});


angular.module('igl').controller('GlobalConformanceStatementCtrlInPc', function($scope, $mdDialog, node, context, $rootScope, $q) {
    //$scope.selectedMessage = angular.copy(selectedMessage);

    $scope.seeOrEdit = context;
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = null;
    $scope.selectedContextNode = null;
    $scope.treeDataForMessage = [];
    $scope.constraintType = 'Plain';
    $scope.firstNodeData = null;
    $scope.secondNodeData = null;
    $scope.changed = false;
    // $scope.selectedMessage.pathInfoSet = [];
    // $scope.treeDataForMessage.push($scope.selectedMessage);
    $scope.draggingStatus = null;
    $scope.contextKey = null;

    $scope.setChanged = function() {
        $scope.changed = true;
    };

    $scope.toggleChildren = function(data) {
        data.childrenVisible = !data.childrenVisible;
        data.folderClass = data.childrenVisible ? "fa-minus" : "fa-plus";
    };

    $scope.beforeNodeDrop = function() {
        var deferred = $q.defer();
        if ($scope.draggingStatus === 'MessageTreeNodeDragging') {
            deferred.resolve();
        } else {
            deferred.reject();
        }
        return deferred.promise;
    };

    $scope.afterFirstNodeDrop = function() {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_1 = $scope.firstNodeData.pathInfoSet;
        $scope.generateFirstPositionAndLocationPath();
    };

    $scope.afterSecondNodeDrop = function() {
        $scope.draggingStatus = null;
        $scope.newConstraint.pathInfoSet_2 = $scope.secondNodeData.pathInfoSet;
        $scope.generateSecondPositionAndLocationPath();
    };

    $scope.draggingNodeFromContextTree = function(event, ui, nodeData) {
        $scope.draggingStatus = 'MessageTreeNodeDragging';
    };

    $scope.findSegRefOrGrpFromMsgByPath = function(children, path) {

        var pathArray = path.split('.');

        if (pathArray.length > 1) {
            for (i in children) {
                if (children[i].position === pathArray[0] && children[i].type === "group") {
                    pathArray.splice(0, 1);
                    return $scope.findSegRefOrGrpFromMsgByPath(children[i].children, pathArray.join("."));
                }
            }
        } else if (pathArray.length === 1) {
            for (i in children) {


                if (children[i].position === parseInt(pathArray[0])) {
                    return children[i];
                }
            }
        }

    };
    $scope.selectContext = function(selectedContextNode) {
        $scope.contextKey = new ObjectId().toString();
        $scope.selectedContextNode = selectedContextNode;
        $scope.selectedContextNode.pathInfoSet = [];
        console.log($scope.contextKey);

        console.log($scope.selectedContextNode);
        $scope.generatePathInfo($scope.selectedContextNode, ".", ".", "1", false, null, $scope.contextKey);
        console.log("$scope.selectedContextNode");

        $scope.initConformanceStatement();
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
            $scope.newConstraint.location_1 = locationPath.substr(1);
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
            $scope.newConstraint.location_2 = locationPath.substr(1);
        }
    };

    $scope.draggingNodeFromMessageTree = function(event, ui, nodeData) {
        $scope.draggingStatus = 'MessageTreeNodeDragging';
    };

    $scope.generatePathInfo = function(current, positionNumber, locationName, instanceNumber, isInstanceNumberEditable, nodeName, key) {
        var pathInfo = {};
        pathInfo.positionNumber = positionNumber;
        pathInfo.locationName = locationName;
        pathInfo.nodeName = nodeName;
        pathInfo.instanceNumber = instanceNumber;
        pathInfo.isInstanceNumberEditable = isInstanceNumberEditable;
        current.contextKey = key;
        current.pathInfoSet.push(pathInfo);

        if (current.type == 'message' || current.type == 'group') {
            for (var i in current.children) {
                var segGroup = current.children[i];
                segGroup.pathInfoSet = angular.copy(current.pathInfoSet);
                var childPositionNumber = segGroup.position;
                var childLocationName = '';
                var childNodeName = '';
                var childInstanceNumber = "1";
                var childisInstanceNumberEditable = false;
                if (segGroup.max != '1') {
                    childInstanceNumber = '*';
                    childisInstanceNumberEditable = true;
                }
                if (segGroup.type == 'group') {
                    childNodeName = segGroup.name;
                    childLocationName = segGroup.name.substr(segGroup.name.lastIndexOf('.') + 1);
                } else {
                    var s = angular.copy($rootScope.segmentsMap[segGroup.ref.id]);
                    s.id = new ObjectId().toString();
                    childLocationName = s.name;
                    childNodeName = s.name;
                    segGroup.segment = s;
                }
                $scope.generatePathInfo(segGroup, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
            }
        } else if (current.type == 'segmentRef') {
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
                $scope.generatePathInfo(f, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
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
                $scope.generatePathInfo(c, childPositionNumber, childLocationName, childInstanceNumber, childisInstanceNumberEditable, childNodeName, key);
            }
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
            constraintId: null,
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
        $scope.complexConstraint = null;
        $scope.newComplexConstraintId = null;
    }

    $scope.addConformanceStatement = function() {
        var cs = $rootScope.generateConformanceStatement($scope.newConstraint);
        console.log($scope.selectedContextNode);
        $scope.selectedContextNode.conformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.deleteConformanceStatement = function(conformanceStatement) {
        $scope.selectedContextNode.conformanceStatements.splice($scope.selectedContextNode.conformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
    };

    $scope.addFreeTextConformanceStatement = function() {
        var cs = $rootScope.generateFreeTextConformanceStatement($scope.newConstraint);
        $scope.selectedContextNode.conformanceStatements.push(cs);
        $scope.changed = true;
        $scope.initConformanceStatement();
    };

    $scope.addComplexConformanceStatement = function() {
        $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint, $scope.constraints);
        $scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
        $scope.selectedContextNode.conformanceStatements.push($scope.complexConstraint);
        $scope.initComplexStatement();
        $scope.changed = true;
    };

    $scope.cancel = function() {
        $mdDialog.hide();
    };

    $scope.saveclose = function() {
        //$rootScope.recordChanged();

        $mdDialog.hide($scope.selectedContextNode.conformanceStatements);
    };
    $scope.findingConfSt = function(node) {
        if (node) {
            if (!node.attributes.conformanceStatements || node.attributes.conformanceStatements.length <= 0) {
                return node.attributes.oldConformanceStatements;
            } else {
                return node.attributes.conformanceStatements;
            }
        }

    };

    $scope.initConformanceStatement();
    $scope.initComplexStatement();
    if (node.type === "group") {
        var index = node.path.indexOf(".");
        var path = node.path.substr(index + 1);
        $scope.selectedMessage = angular.copy($rootScope.messagesMap[node.source.messageId]);
        $scope.selectedMessage = $scope.findSegRefOrGrpFromMsgByPath($scope.selectedMessage.children, path);
        $scope.selectedMessage.conformanceStatements = $scope.findingConfSt(node);
        $scope.selectedMessage.pathInfoSet = [];
        $scope.treeDataForMessage.push($scope.selectedMessage);
        $scope.selectContext($scope.selectedMessage);
    } else if (node.type === "message") {
        console.log("MESSAGE");
        $scope.selectedMessage = angular.copy($rootScope.messagesMap[node.source.messageId]);
        $scope.selectedMessage.conformanceStatements = $scope.findingConfSt(node);
        $scope.selectedMessage.pathInfoSet = [];
        $scope.treeDataForMessage.push($scope.selectedMessage);
        $scope.selectContext($scope.selectedMessage);
    }

    //$scope.generatePathInfo($scope.selectedMessage, ".", ".", "1", false, null, 'default');


});

angular.module('igl').controller('ConformanceStatementSegmentCtrlInPc', function($scope, $rootScope, $q, node, context, $mdDialog) {
    $scope.segment = angular.copy($rootScope.segmentsMap[node.attributes.ref.id]);
    $scope.seeOrEdit = context;
    $scope.constraintType = 'Plain';
    $scope.constraints = [];
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    if ($rootScope.segmentsMap[node.attributes.ref.id].ext && $rootScope.segmentsMap[node.attributes.ref.id].ext !== null) {
        $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segmentsMap[node.attributes.ref.id].name + "_" + $rootScope.segmentsMap[node.attributes.ref.id].ext);

    } else {
        $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segmentsMap[node.attributes.ref.id].name);
    }
    $scope.newComplexConstraint = [];
    $scope.firstNodeData = null;
    $scope.secondNodeData = null;
    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    //angular.copy($rootScope.segmentsMap[node.attributes.ref.id].conformanceStatements, $scope.tempComformanceStatements);
    $scope.findingConfSt = function(node) {
        if (node) {
            if (!node.attributes.conformanceStatements || node.attributes.conformanceStatements.length <= 0) {
                return node.attributes.oldConformanceStatements;
            } else {
                return node.attributes.conformanceStatements;
            }
        }

    };
    angular.copy($scope.findingConfSt(node), $scope.tempComformanceStatements);

    $scope.treeDataForContext = [];
    $scope.treeDataForContext.push(angular.copy($rootScope.segmentsMap[node.attributes.ref.id]));
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

    $scope.afterFirstNodeDrop = function() {
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
            constraintId: $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segmentsMap[node.attributes.ref.id].name + "_" + $rootScope.segmentsMap[node.attributes.ref.id].ext),
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
        $scope.newComplexConstraintId = $rootScope.calNextCSID($rootScope.igdocument.metaData.ext, $rootScope.segmentsMap[node.attributes.ref.id].name + "_" + $rootScope.segmentsMap[node.attributes.ref.id].ext);
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
            $scope.newConstraint.location_1 = $rootScope.segmentsMap[node.attributes.ref.id].name + '-' + locationPath.substr(1);
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
            $scope.newConstraint.location_2 = $rootScope.segmentsMap[node.attributes.ref.id].name + '-' + locationPath.substr(1);
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

    $scope.cancel = function() {
        //$modalInstance.close();
        $mdDialog.hide();
    };
    console.log($scope.treeDataForContext);

    $scope.saveclose = function() {
        //angular.copy($scope.tempComformanceStatements, $rootScope.segment.conformanceStatements);
        //$rootScope.recordChanged($rootScope.segment);
        // $modalInstance.close();
        console.log($scope.tempComformanceStatements);
        console.log($rootScope.segmentsMap[node.attributes.ref.id].conformanceStatements)
        $mdDialog.hide($scope.tempComformanceStatements);
    };
});