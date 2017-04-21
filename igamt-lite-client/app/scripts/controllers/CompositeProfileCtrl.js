angular.module('igl').controller('ListCompositeProfileCtrl', function($scope, $rootScope, $http, $modal, CompositeProfileService, TableService, DatatypeService, $mdDialog) {

    $scope.accordStatus = {
        isCustomHeaderOpen: false,
        isFirstOpen: false,
        isSecondOpen: true,
        isThirdOpen: false,
        isFourthOpen: false,
        isFifthOpen : false,

    };
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
    $scope.checkCompositeExt = function(ext) {
        for (var i = 0; i < $rootScope.compositeProfiles.length; i++) {

            if (ext === $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].ext && $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].id !== $rootScope.compositeProfileStructure.id) {
                return true;
            }

        }
        return false;
    }


    $scope.save = function() {
        console.log($rootScope.compositeProfileStructure);
        $scope.saving = true;
        CompositeProfileService.save($rootScope.compositeProfileStructure).then(function(result) {

                console.log("in BUild");
                $rootScope.compositeProfileStructure = result;
                $rootScope.$emit("event:updateIgDate");
                for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
                    if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === result.id) {
                        $rootScope.igdocument.profile.compositeProfiles.children[i] = result;
                    }
                }
                $rootScope.compositeProfilesStructureMap[result.id] = result;
                console.log(result);
                cleanState();
                $rootScope.editCM(result);


            },
            function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
    };
    var updatePcPosition = function(pcList) {
        for (var i = 0; i < pcList.length; i++) {
            pcList[i].position = i + 1;
        }
    };
    $scope.changePosition = function() {
        console.log($rootScope.compositeProfile);
        updatePcPosition($rootScope.compositeProfile.appliedProfileComponents);
        for (var i = 0; i < $rootScope.compositeProfileStructure.profileComponentsInfo.length; i++) {
            for (var j = 0; j < $rootScope.compositeProfile.appliedProfileComponents.length; j++) {
                if ($rootScope.compositeProfileStructure.profileComponentsInfo[i].id === $rootScope.compositeProfile.appliedProfileComponents[j].pc.id) {
                    $rootScope.compositeProfileStructure.profileComponentsInfo[i].position = $rootScope.compositeProfile.appliedProfileComponents[j].position;
                }
            }
        }
        $scope.save();
        console.log($rootScope.compositeProfileStructure);
    };
    $scope.removePc = function(pcId) {
        for (var i = 0; i < $rootScope.compositeProfileStructure.profileComponentsInfo.length; i++) {

            if ($rootScope.compositeProfileStructure.profileComponentsInfo[i].id === pcId) {
                $rootScope.compositeProfileStructure.profileComponentsInfo.splice(i, 1);
            }
        }
        updatePcPosition($rootScope.compositeProfileStructure.profileComponentsInfo);
        console.log("------");
        console.log($rootScope.compositeProfileStructure);
        console.log("------");

        CompositeProfileService.removePc($rootScope.compositeProfileStructure, pcId).then(function(result) {
            CompositeProfileService.build(result).then(function(profile) {
                $rootScope.compositeProfileStructure = result;
                $rootScope.compositeProfilesStructureMap[result.id] = result;
                for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
                    if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === $rootScope.compositeProfileStructure.id) {
                        $rootScope.igdocument.profile.compositeProfiles.children[i] = $rootScope.compositeProfileStructure;
                    }
                }
                for (var i = 0; i < $rootScope.profileComponents.length; i++) {
                    if ($rootScope.profileComponents[i].id === pcId) {
                        for (var j = 0; j < $rootScope.profileComponents[i].compositeProfileStructureList.length; j++) {
                            if ($rootScope.profileComponents[i].compositeProfileStructureList[j] === result.id) {
                                $rootScope.profileComponents[i].compositeProfileStructureList.splice(j, 1);
                                $rootScope.profileComponentsMap[$rootScope.profileComponents[i].id] = $rootScope.profileComponents[i];
                            }
                        }
                    }
                }
                $rootScope.editCM(result);
            });

        }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
        });


    };

    var cleanState = function() {

        $scope.clearDirty();
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        $rootScope.clearChanges();
        if ($scope.compositeMessageParams) {
            $scope.compositeMessageParams.refresh();
        }
    };
    $scope.editCommentDlg = function(node, comment, disabled, type) {
        var modalInstance = $modal.open({
            templateUrl: 'EditComment.html',
            controller: 'EditCommentCtrlInPc',
            backdrop: true,
            keyboard: true,
            windowClass: 'input-text-modal-window',
            backdropClick: false,
            resolve: {
                currentNode: function() {
                    return node;
                },
                currentComment: function() {
                    return comment;
                },
                disabled: function() {
                    return disabled;
                },
                type: function() {
                    return type;
                }
            }
        });

        modalInstance.result.then(function() {

        });

    };
    $scope.seeDynMap = function(node, context) {
        $mdDialog.show({
            templateUrl: 'AddDynamicMappingCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'SeeDynMapDlgCtl',
            clickOutsideToClose: true,
            locals: {
                node: node,
                context: context
            }

        }).then(function() {

        });
    };
    $scope.findingDynMap = function(node) {
        if (node.type === "segmentRef") {
            if (node.ref.dynamicMappingDefinition && node.ref.dynamicMappingDefinition.dynamicMappingItems.length > 0) {
                return node.ref.dynamicMappingDefinition;
            }
        }
    };
    $scope.findingCoCon = function(node) {
        if (node.type === "segmentRef") {
            if (node.ref.coConstraintsTable && node.ref.coConstraintsTable.rowSize > 0) {
                return node.ref.coConstraintsTable;
            }
        }
    };
    $scope.seeCoCon = function(node, context) {
        $mdDialog.show({
            templateUrl: 'AddConstraintsCtrlInCP.html',
            parent: angular.element(document).find('body'),
            controller: 'SeeCoConDlgCtl',
            clickOutsideToClose: true,
            locals: {
                node: node,
                context: context
            }

        }).then(function() {

        });
    };
    $scope.seeConfSt = function(node, context) {
        $mdDialog.show({
            templateUrl: 'GlobalConformanceStatementCtrlInPc.html',
            parent: angular.element(document).find('body'),
            controller: 'SeeConfStDlgCtl',
            clickOutsideToClose: true,
            locals: {
                node: node,
                context: context
            }

        }).then(function() {

        });
    };
    $scope.findingConfSt = function(node) {
        if (node.type === "group") {
            if (node.conformanceStatements && node.conformanceStatements.length > 0) {
                return node.conformanceStatements;
            }
        } else if (node.type === "segmentRef") {
            if ($rootScope.compositeProfile.segmentsMap[node.ref.id] && $rootScope.compositeProfile.segmentsMap[node.ref.id].conformanceStatements && $rootScope.compositeProfile.segmentsMap[node.ref.id].conformanceStatements.length > 0) {
                return $rootScope.compositeProfile.segmentsMap[node.ref.id].conformanceStatements;
            }
        }

    }
    $scope.findingConfStInMsg = function(profile) {
        var results = [];
        if (profile.conformanceStatements && profile.conformanceStatements.length > 0) {
            results.push(profile);
        }
        for (i in profile.children) {

            if (profile.children[i].type === 'group') {
                results.push.apply($scope.findingConfStInMsg(profile.children[i]));
            } else {
                if (profile.children[i].conformanceStatements && profile.children[i].conformanceStatements.length > 0) {
                    results.push(profile.children[i].conformanceStatements);
                }
            }
        }
        return results;
    };
    $scope.isAvailableConstantValue = function(node) {
        if (node.type === "field" || node.type === "component") {
            if ($scope.hasChildren(node)) return false;
            var bindings = $scope.findingBindings(node);
            if (bindings && bindings.length > 0) return false;
            if ($rootScope.compositeProfile.datatypesMap[node.datatype.id].name == 'ID' || $rootScope.compositeProfile.datatypesMap[node.datatype.id].name == "IS") return false;
            return true;
        } else {
            return false;
        }

    };
    $scope.hasChildren = function(node) {
        return node && node != null && ((node.fields && node.fields.length > 0) || (node.datatype && $rootScope.compositeProfile.datatypesMap[node.datatype.id] && $rootScope.compositeProfile.datatypesMap[node.datatype.id].components && $rootScope.compositeProfile.datatypesMap[node.datatype.id].components.length > 0));
    };


    $scope.isAvailableForValueSet = function(node) {

        if (node && node.datatype) {
            var currentDT = $rootScope.compositeProfile.datatypesMap[node.datatype.id];
            if (_.find($rootScope.config.valueSetAllowedDTs, function(valueSetAllowedDT) {
                    return valueSetAllowedDT == currentDT.name;
                })) return true;
        }

        if (node && node.fieldDT && !node.componentDT) {
            var parentDT = $rootScope.compositeProfile.datatypesMap[node.fieldDT];
            var pathSplit = node.segmentPath.split(".");
            if (_.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent) {
                    return valueSetAllowedComponent.dtName == parentDT.name && valueSetAllowedComponent.location == pathSplit[1];
                })) return true;
        }

        if (node && node.componentDT) {
            var parentDT = $rootScope.compositeProfile.datatypesMap[node.componentDT];
            var pathSplit = node.segmentPath.split(".");
            if (_.find($rootScope.config.valueSetAllowedComponents, function(valueSetAllowedComponent) {
                    return valueSetAllowedComponent.dtName == parentDT.name && valueSetAllowedComponent.location == pathSplit[2];
                })) return true;
        }

        return false;
    };
    $scope.findingComments = function(node) {
        var result = [];
        if (node && $rootScope.compositeProfile) {
            result = _.filter($rootScope.compositeProfile.comments, function(comment) {
                return comment.location == $rootScope.refinePath(node.path);
            });
            for (var i = 0; i < result.length; i++) {
                result[i].from = 'compositeProfile';
                result[i].index = i + 1;
            }

            if (node.segment) {
                var parentSeg = $rootScope.compositeProfile.segmentsMap[node.segment];
                var subResult = _.filter(parentSeg.comments, function(comment) {
                    return comment.location == node.segmentPath;
                });
                for (var i = 0; i < subResult.length; i++) {
                    subResult[i].from = 'segment';
                    subResult[i].index = i + 1;
                }
                result = result.concat(subResult);
            }

            if (node.fieldDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.fieldDT];
                var subPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
                var subSubResult = _.filter(parentDT.comments, function(comment) {
                    return comment.location == subPath;
                });
                for (var i = 0; i < subSubResult.length; i++) {
                    subSubResult[i].from = 'field';
                    subSubResult[i].index = i + 1;
                }
                result = result.concat(subSubResult);
            }

            if (node.componentDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.componentDT];
                var subPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
                var subSubSubResult = _.filter(parentDT.comments, function(comment) {
                    return comment.location == subPath;
                });
                for (var i = 0; i < subSubSubResult.length; i++) {
                    subSubSubResult[i].from = 'component';
                    subSubSubResult[i].index = i + 1;
                }
                result = result.concat(subSubSubResult);
            }
        }

        return result;
    };


    $scope.findingBindings = function(node) {
        var result = [];

        if (node && $rootScope.compositeProfile) {
            result = _.filter($rootScope.compositeProfile.valueSetBindings, function(binding) {

                return binding.location == $rootScope.refinePath(node.path);
            });
            for (var i = 0; i < result.length; i++) {
                result[i].bindingFrom = 'compositeProfile';
            }

            if (result && result.length > 0) {
                return result;
            }

            if (node.segment) {
                var parentSeg = $rootScope.compositeProfile.segmentsMap[node.segment];
                result = _.filter(parentSeg.valueSetBindings, function(binding) {
                    return binding.location == node.segmentPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].bindingFrom = 'segment';
                }
            }

            if (result && result.length > 0) {
                return result;
            }

            if (node.fieldDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.fieldDT];
                var subPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
                result = _.filter(parentDT.valueSetBindings, function(binding) {
                    return binding.location == subPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].bindingFrom = 'field';
                }
            }

            if (result && result.length > 0) {
                return result;
            }

            if (node.componentDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.componentDT];
                var subPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
                result = _.filter(parentDT.valueSetBindings, function(binding) {
                    return binding.location == subPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].bindingFrom = 'component';
                }
            }
        }

        return result;
    };
    $scope.findingSingleElement = function(node) {
        var result = [];

        if (node && $rootScope.compositeProfile) {
            result = _.filter($rootScope.compositeProfile.singleElementValues, function(binding) {
                return binding.location == $rootScope.refinePath(node.path);
            });
            for (var i = 0; i < result.length; i++) {
                result[i].from = 'message';
            }
            if (result && result.length > 0) {

                return result;
            }

            if (node.segment) {
                var parentSeg = $rootScope.compositeProfile.segmentsMap[node.segment];
                result = _.filter(parentSeg.singleElementValues, function(binding) {
                    return binding.location == node.segmentPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].from = 'segment';
                }
            }

            if (result && result.length > 0) {
                return result;
            }

            if (node.fieldDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.fieldDT];
                var subPath = node.segmentPath.substr(node.segmentPath.indexOf('.') + 1);
                result = _.filter(parentDT.singleElementValues, function(binding) {
                    return binding.location == subPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].from = 'field';
                }
            }

            if (result && result.length > 0) {
                return result;
            }

            if (node.componentDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.componentDT];
                var subPath = node.segmentPath.substr(node.segmentPath.split('.', 2).join('.').length + 1);
                result = _.filter(parentDT.singleElementValues, function(binding) {
                    return binding.location == subPath;
                });
                for (var i = 0; i < result.length; i++) {
                    result[i].from = 'component';
                }
            }
        }


        return result;
    };
    $scope.groupsPredicates = [];

    $scope.findGroupsPredicates = function(children) {
        for (var i = 0; i < children.length; i++) {
            if (children[i].type === 'group') {
                if (children[i].predicates && children[i].predicates.length > 0) {
                    for (var j = 0; j < children[i].predicates.length; j++) {
                        if (children[i].predicates[j].context === null) {
                            children[i].predicates[j].context = {
                                type: children[i].type,
                                name: children[i].name,
                                path: $rootScope.refinePath(children[i].path),
                            }
                        }
                    }
                    $scope.groupsPredicates.push.apply($scope.groupsPredicates, children[i].predicates);
                }
                $scope.findGroupsPredicates(children[i].children);

            }
        }
    }
    $scope.findGroupsPredicates($rootScope.compositeProfile.children);
    console.log("$scope.groupsPredicates");
    console.log($scope.groupsPredicates);

    $scope.findingGlobalPredicates = function() {   
        $scope.msgPredicates = $rootScope.compositeProfile.predicates;  
    }
    $scope.findingGlobalPredicates();

    $scope.findingPredicates = function(node) {

        var result = null;
        if (node && $rootScope.compositeProfile) {
            result = _.find($scope.groupsPredicates, function(p) {
                if (p.context && p.context.type === "group") {
                    //var tempath = node.path.replace(p.context.path + '.', '');
                    if (node.type === 'group' && p.context.path === $rootScope.refinePath(p.constraintTarget) && parseInt($rootScope.refinePath(p.constraintTarget)) === node.position) {
                        return true;
                    } else {
                        return (p.context.path + '.' + $rootScope.refinePath(p.constraintTarget)) == $rootScope.refinePath(node.path);

                    }
                }

            });
            if (result) {

                result.from = 'message';
                return result;
            }
            result = _.find($rootScope.compositeProfile.predicates, function(binding) {
                return binding.constraintTarget == node.path;
            });
            if (result) {
                result.from = 'message';
                return result;
            }



            if (node.segment) {
                var parentSeg = $rootScope.compositeProfile.segmentsMap[node.segment];
                var index = node.path.indexOf(".");
                var pa = node.path.split(".");
                if (node.type === "field") {
                    var ind = 0;
                    while (pa.length > 1) {
                        pa.splice(0, 1);
                    }
                }

                //var tempPath = node.path.substr(index + 1);
                if (parentSeg) {
                    result = _.find(parentSeg.predicates, function(binding) {
                        return binding.constraintTarget == pa.join(".");
                    });
                }

                if (result) {
                    result.from = 'segment';
                    return result;
                }
            }



            if (node.fieldDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.fieldDT];
                var pa = node.path.split(".");
                if (node.type === "component") {
                    var ind = 0;
                    while (pa.length > 1) {
                        pa.splice(0, 1);
                    }
                }

                //var tempPath = node.path.substr(index + 1);
                if (parentDT) {
                    result = _.find(parentDT.predicates, function(binding) {
                        return binding.constraintTarget == pa.join(".");
                    });
                }

                if (result) {
                    result.from = 'field';
                    return result;
                }
            }

            if (node.componentDT) {
                var parentDT = $rootScope.compositeProfile.datatypesMap[node.componentDT];
                //var subPath = node.constraintTarget.substr(node.constraintTarget.split('.', 2).join('.').length + 1);
                var pa = node.path.split(".");
                if (node.type === "component") {
                    var ind = 0;
                    while (pa.length > 1) {
                        pa.splice(0, 1);
                    }
                }

                //var tempPath = node.path.substr(index + 1);
                if (parentDT) {
                    result = _.find(parentDT.predicates, function(binding) {
                        return binding.constraintTarget == pa.join(".");
                    });
                }

                if (result) {
                    result.from = 'component';
                    return result;
                }
            }
        }


        return result;
    }

});

angular.module('igl').controller('SeeConfStDlgCtl', function($scope, $rootScope, $mdDialog, node, context) {
    if (node.type === "group") {
        $scope.selectedContextNode = node;

    } else if (node.type === "segmentRef") {
        $scope.selectedContextNode = $rootScope.compositeProfile.segmentsMap[node.ref.id];

    }
    $scope.seeOrEdit = context;


    $scope.cancel = function() {
        $mdDialog.hide();
    };


});

angular.module('igl').controller('SeeCoConDlgCtl', function($scope, $rootScope, $mdDialog, node, context, TableService) {
    $scope.node = angular.copy(node);
    console.log($scope.node);
    $scope.coConstraintsTable = $scope.node.ref.coConstraintsTable;
    $scope.seeOrEdit = context;


    $scope.cancel = function() {
        $mdDialog.hide();
    };


});

angular.module('igl').controller('SeeDynMapDlgCtl', function($scope, $rootScope, $mdDialog, node, context, TableService) {
    $scope.node = angular.copy(node);
    console.log($scope.node);
    $scope.seeOrEdit = context;
    $scope.findDynamicMapping = function(node) {
        if (node.type === "segmentRef") {

            return node.ref.dynamicMappingDefinition;

        }
        return null;
    };
    $scope.findingBindings = function(node) {
        if (node.type === "segmentRef") {
            if (node.ref.valueSetBindings && node.ref.valueSetBindings.length > 0) {
                return node.ref.valueSetBindings;
            }
        }
        return null;
    };
    $scope.updateDynamicMappingInfo = function() {
        $scope.isDynamicMappingSegment = false;
        $scope.dynamicMappingTable = null;

        var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
            return item.hl7Version == $rootScope.compositeProfile.segmentsMap[$scope.node.ref.id].hl7Version && item.segmentName == $rootScope.compositeProfile.segmentsMap[$scope.node.ref.id].name;
        });

        if (mappingStructure) {
            $scope.isDynamicMappingSegment = true;
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
    console.log("+++++");
    console.log($scope.dynamicMappingDefinition);

    $scope.cancel = function() {
        $mdDialog.hide();
    };


});