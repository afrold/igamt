angular.module('igl').controller('ListProfileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, PcLibraryService, PcService, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc) {

    $scope.editProfileComponent = false;
    $scope.edit = false;
    $scope.profileComponents = [];
    $scope.accordStatus = {
        isCustomHeaderOpen: false,
        isFirstOpen: true,
        isSecondOpen: false,
        isFirstDisabled: false
    };
    $scope.pcTypes = [{
        name: "message",
        alias: "Message",
        sub: "Segment Ref/Group"
    }, {
        name: "segment",
        alias: "Segment",
        sub: "Field"
    }, {
        name: "datatype",
        alias: "Datatype",
        sub: "Component"
    }];
    $scope.setPcType = function(type) {
        $scope.element = type;
        if (type.name === 'segment') {
            $scope.segs = angular.copy($rootScope.segments);
        } else if (type.name === 'datatype') {
            $scope.dts = angular.copy($rootScope.datatypes);
        } else if (type.name === 'message') {
            console.log($rootScope.messages);
            $scope.msgs = angular.copy($rootScope.messages.children);
        }
    };
    $scope.applyPcToParams = new ngTreetableParams({
        getNodes: function(parent) {
            if ($rootScope.profileComponent.appliedTo && $rootScope.profileComponent.appliedTo.length > 0) {
                console.log("==========");
                console.log($rootScope.profileComponent);
                return $rootScope.profileComponent.appliedTo;


            }
        },
        getTemplate: function(node) {
            return 'applyPcToTable';
        }
    });
    $scope.profileComponentParams = new ngTreetableParams({
        getNodes: function(parent) {
            if ($rootScope.igdocument.profile.profileComponentLibrary !== undefined) {
                console.log("$rootScope.profileComponent");

                console.log($rootScope.profileComponent);
                return $rootScope.profileComponent.children;
                // return $rootScope.profileComponent.children;
                // if (parent) {
                //     if (parent.fields) {
                //         return parent.fields;
                //     } else if (parent.components) {
                //         return parent.components;
                //     } else if (parent.segments) {
                //         return parent.segments;
                //     } else if (parent.codes) {
                //         return parent.codes;
                //     }

                // } else {
                // console.log($rootScope.igdocument.profile.profileComponentLibrary.children);
                // return $rootScope.igdocument.profile.profileComponentLibrary.children;
                // }

            }
        },
        getTemplate: function(node) {
            return 'profileComponentTable';
        }
    });

    $scope.applyPcTo = function(node) {
        var applyPcToInstance = $modal.open({
            templateUrl: 'applyPcTo.html',
            controller: 'applyPcToCtrl',
            size: 'lg',
            windowClass: 'conformance-profiles-modal',
            resolve: {
                pc: function() {
                    return $rootScope.profileComponent;
                },
                messages: function() {
                    return $rootScope.messages.children;
                }

            }
        }).result.then(function(results) {

            console.log("+++====+++++");
            console.log($rootScope.profileComponent);

            if ($scope.applyPcToParams) {
                $scope.applyPcToParams.refresh();
            }



        });

    };
    $scope.removeApply = function(node) {
        var index = $rootScope.profileComponent.appliedTo.indexOf(node);
        if (index > -1) $rootScope.profileComponent.appliedTo.splice(index, 1);
        PcService.save($rootScope.profileComponent).then(function(result) {
            if ($scope.applyPcToParams) {
                $scope.applyPcToParams.refresh();
            }
        });

    };
    $scope.addPComponents = function() {
        var applyPcToInstance = $modal.open({
            templateUrl: 'addComponents.html',
            controller: 'addComponentsCtrl',
            size: 'lg',
            windowClass: 'conformance-profiles-modal',
            resolve: {
                messages: function() {
                    return angular.copy($rootScope.messages.children);
                },
                segments: function() {
                    return angular.copy($rootScope.segments);
                },
                segmentsMap: function() {
                    return angular.copy($rootScope.segmentsMap);
                },
                datatypes: function() {
                    return angular.copy($rootScope.datatypes);
                },
                datatypesMap: function() {
                    return $rootScope.datatypesMap;
                },
                currentPc: function() {
                    return $rootScope.profileComponent;
                }

            }
        }).result.then(function(results) {
            if ($scope.profileComponentParams) {
                $scope.profileComponentParams.refresh();
            }
        });

    };
    $scope.removePcEntry = function(node) {

        var index = $rootScope.profileComponent.children.indexOf(node);
        if (index > -1) $rootScope.profileComponent.children.splice(index, 1);
        if ($scope.profileComponentParams) {
            $scope.profileComponentParams.refresh();
        }
    };
    $scope.setPcChild = function(pc) {
        console.log(pc);
        if (pc.type === 'segmentRef') {
            console.log($scope.parent);
            $scope.newPc = {
                id: new ObjectId().toString(),
                name: $rootScope.segmentsMap[pc.ref.id].label,
                type: pc.type,
                path: $scope.parent.structID + '.' + pc.position,
                attributes: {},
                appliedTo: [],
                version: ""
            };
        } else if (pc.type === 'field') {
            console.log($scope.parent);
            $scope.newPc = {
                id: new ObjectId().toString(),
                name: pc.name,
                type: pc.type,
                path: $rootScope.segmentsMap[$scope.parent.id].label + '.' + pc.position,
                attributes: {},
                appliedTo: [],
                version: ""
            };
        } else if (pc.type === 'component') {
            console.log($scope.parent);

            $scope.newPc = {
                id: new ObjectId().toString(),
                name: pc.name,
                type: pc.type,
                path: $rootScope.datatypesMap[$scope.parent.id].label + '.' + pc.position,
                attributes: {},
                appliedTo: [],
                version: ""
            };
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
        console.log($rootScope.profileComponent);
        var children = $rootScope.profileComponent.children;
        var childrenToSave = [];
        PcService.save($rootScope.profileComponent).then(function(result) {
            console.log(result);


        });
        // for (var i = 0; i < children.length; i++) {
        //     var subPcToSave = {
        //         path: children[i].path,
        //         name: children[i].name
        //     };
        //     if (children[i].newUsage && children[i].newUsage !== null) {
        //         subPcToSave.usage = children[i].newUsage;
        //     }
        //     if (children[i].newMinCard && children[i].newMinCard !== null) {
        //         subPcToSave.min = children[i].newMinCard;
        //     }
        //     if (children[i].newMaxCard && children[i].newMaxCard !== null) {
        //         subPcToSave.max = children[i].newMaxCard;
        //     }
        //     if (children[i].newMinLength && children[i].newMinLength !== null) {
        //         subPcToSave.minLength = children[i].newMinLength;
        //     }
        //     if (children[i].newMaxLength && children[i].newMaxLength !== null) {
        //         subPcToSave.maxLength = children[i].newMaxLength;
        //     }
        //     if (children[i].newConfLength && children[i].newConfLength !== null) {
        //         subPcToSave.confLength = children[i].newConfLength;
        //     }
        //     childrenToSave.push(subPcToSave);

        // }
        // console.log(childrenToSave);
    };

    $scope.editUsage = function(field) {
        console.log(field);
        //field.oldUsage = field.usage;


    };
    $scope.setUsage = function(field) {
        //field.newUsage = field.usage;
        field.attributes.usage = field;
    };
    $scope.cancelUsage = function(field) {
        //field.usage = field.oldUsage;
        field.attributes.usage = null;
    };
    $scope.editMinCard = function(field) {
        field.oldMinCard = field.min;
    };
    $scope.setMinCard = function(field) {
        field.newMinCard = field.min;
    };
    $scope.cancelMinCard = function(field) {
        field.attributes.min = null;
        //field.newMinCard = null;
    }
    $scope.editMaxCard = function(field) {
        field.oldMaxCard = field.max;
    };
    $scope.setMaxCard = function(field) {
        field.newMaxCard = field.max;
    };
    $scope.cancelMaxCard = function(field) {
        field.attributes.max = null;
        //field.newMaxCard = null;
    }
    $scope.editMinL = function(field) {
        field.oldMinLength = field.minLength;
    };
    $scope.setMinL = function(field) {
        field.newMinLength = field.minLength;
    };
    $scope.cancelMinL = function(field) {
        field.attributes.minLength = null;
        //field.newMinLength = null;
    }
    $scope.editMaxL = function(field) {
        field.oldMaxLength = field.maxLength;
    };
    $scope.setMaxL = function(field) {
        field.newMaxLength = field.maxLength;
    };
    $scope.cancelMaxL = function(field) {
        field.attributes.maxLength = null;
        //field.newMaxLength = null;
    };
    $scope.editConfL = function(field) {
        field.oldConfLength = field.confLength;
    };
    $scope.setMaxL = function(field) {
        field.newConfLength = field.confLength;
    };
    $scope.cancelConfL = function(field) {
        field.attributes.confLength = null;
        //field.newconfLength = null;
    }


});


angular.module('igl').controller('applyPcToCtrl',
    function($scope, $rootScope, $modalInstance, pc, PcService, messages, $http, SegmentLibrarySvc) {
        if (pc.appliedTo === null) {
            pc.appliedTo = [];
        }

        $scope.messages = messages;
        console.log($scope.messages);
        $scope.apply = function() {
            pc.appliedTo.push({
                id: $scope.applyTo.id,
                name: $scope.applyTo.name
            });
            PcService.save(pc).then(function(result) {
                console.log(result);

                $modalInstance.close();

            });


        };
        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });



angular.module('igl').controller('addComponentsCtrl',
    function($scope, $rootScope, $modalInstance, messages, segments, segmentsMap, datatypesMap, currentPc, PcLibraryService, datatypes, ngTreetableParams, $http, SegmentLibrarySvc, PcService) {
        $scope.selectedPC = [];
        $scope.MsgProfileComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if (messages !== undefined) {

                    if (parent) {
                        if (parent.children) {
                            for (var i = 0; i < parent.children.length; i++) {
                                if (parent.type === 'group') {

                                    parent.children[i].parent = parent.parent + '.' + parent.name;
                                    if (parent.children[i].type === 'segmentRef') {

                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;

                                    }
                                } else if (parent.type === 'message') {
                                    parent.children[i].parent = parent.structID;
                                    if (parent.children[i].type === 'segmentRef') {

                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;

                                    }
                                } else if (parent.type === 'segmentRef') {
                                    parent.children[i].parent = parent.parent + '.' + segmentsMap[parent.ref.id].label;
                                    parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
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
            if (pc.type === 'segmentRef') {
                var newPc = {
                    id: new ObjectId().toString(),
                    name: $rootScope.segmentsMap[pc.ref.id].name,
                    ext: $rootScope.segmentsMap[pc.ref.id].ex,
                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'group') {
                var newPc = {
                    id: new ObjectId().toString(),
                    name: pc.name,
                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'field') {
                console.log()
                if (parent.type === 'segment') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: $rootScope.segmentsMap[parent.id].label + '.' + pc.position,
                        attributes: {},
                        appliedTo: [],
                        version: ""
                    };
                } else if (parent.type === 'segmentRef') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.parent + '.' + $rootScope.segmentsMap[parent.ref.id].label + '.' + pc.position,
                        attributes: {},
                        appliedTo: [],
                        version: ""
                    };
                }

            } else if (pc.type === 'component') {
                if (parent.type === 'field') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.parent + '.' + parent.position + '.' + pc.position,
                        attributes: {},
                        appliedTo: [],
                        version: ""
                    };
                } else {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: $rootScope.datatypesMap[parent.id].label + '.' + pc.position,
                        attributes: {},
                        appliedTo: [],
                        version: ""
                    };
                }


            } else if (pc.type === 'segment') {
                var newPc = {
                    id: new ObjectId().toString(),
                    name: $rootScope.segmentsMap[pc.id].name,
                    ext: $rootScope.segmentsMap[pc.id].ex,
                    type: pc.type,
                    path: '*',
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };
            } else if (pc.type === 'datatype') {

                var newPc = {
                    id: new ObjectId().toString(),
                    name: $rootScope.datatypesMap[pc.id].name,
                    ext: $rootScope.datatypesMap[pc.id].ex,
                    type: pc.type,
                    path: '*',
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };


            };
            $scope.selectedPC.push(newPc);
        };

        $scope.SegProfileComponentParams = new ngTreetableParams({
            getNodes: function(parent) {
                if (segments !== undefined) {
                    if (parent) {
                        if (parent.fields) {
                            return parent.fields;
                        }

                    } else {
                        return segments;
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
                        console.log(parent);
                        if (parent.components) {
                            return parent.components;
                        }

                    } else {
                        console.log(datatypes);
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
            PcService.addPCs(currentPc.id, $scope.selectedPC).then(function(profileC) {
                $rootScope.profileComponent = profileC;
                $modalInstance.close();
            });
        };


        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });