angular.module('igl').controller('ListProfileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, PcLibraryService, PcService, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc) {
    $scope.changes = false;

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
            $scope.setDirty();



        });

    };
    $scope.removeApply = function(node) {
        var index = $rootScope.profileComponent.appliedTo.indexOf(node);
        if (index > -1) $rootScope.profileComponent.appliedTo.splice(index, 1);
        if ($scope.applyPcToParams) {
            $scope.applyPcToParams.refresh();
        }
        $scope.setDirty();
        // PcService.save($rootScope.profileComponent).then(function(result) {
        //     if ($scope.applyPcToParams) {
        //         $scope.applyPcToParams.refresh();
        //     }
        // });

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
            $scope.setDirty();
            if ($scope.profileComponentParams) {
                $scope.profileComponentParams.refresh();
            }
        });

    };
    $scope.removePcEntry = function(node) {

        var index = $rootScope.profileComponent.children.indexOf(node);
        if (index > -1) $rootScope.profileComponent.children.splice(index, 1);
        $scope.setDirty();
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
        PcService.save($rootScope.igdocument.profile.profileComponentLibrary.id, $rootScope.profileComponent).then(function(result) {
            for (var i = 0; i < $rootScope.igdocument.profile.profileComponentLibrary.children.length; i++) {
                if ($rootScope.igdocument.profile.profileComponentLibrary.children[i].id === $rootScope.profileComponent.id) {
                    $rootScope.igdocument.profile.profileComponentLibrary.children[i].name = $rootScope.profileComponent.name;
                }

                $rootScope.profileComponent = result;

            }
            $scope.changes = false;
            $scope.clearDirty();
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
        $scope.setDirty();
    };
    $scope.editMinCard = function(field) {
        field.oldMinCard = field.min;
    };
    $scope.setMinCard = function(field) {
        field.newMinCard = field.min;
    };
    $scope.cancelMinCard = function(field) {
        field.attributes.min = null;
        $scope.setDirty();

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
        $scope.setDirty();

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
        $scope.setDirty();

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
        $scope.setDirty();

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
        $scope.setDirty();

        //field.newconfLength = null;
    };
    $scope.cancelDefText = function(field) {
        field.attributes.text = null;
        $scope.setDirty();
    };
    $scope.cancelTables = function(field) {
        field.attributes.tables = null;
        $scope.setDirty();
    };
    $scope.cancelDatatype = function(field) {
        field.attributes.datatype = null;
        $scope.editableDT = '';
        $scope.setDirty();
    };
    $scope.cancelComment = function(field) {
        field.attributes.comment = null;
        $scope.setDirty();
    };
    $scope.editableDT = '';

    $scope.selectDT = function(field, datatype) {
        console.log("datatype");

        console.log(datatype);
        if (datatype && datatype !== "Others") {
            $scope.DTselected = true;

            $scope.editableDT = '';
            // field.attributes.datatype = {
            //     id: datatype.id,
            //     name: datatype.name,
            //     ext: datatype.ext,
            //     label: datatype.label
            // };
            field.attributes.datatype = {};
            field.attributes.datatype.ext = JSON.parse(datatype).ext;
            field.attributes.datatype.id = JSON.parse(datatype).id;
            field.attributes.datatype.label = JSON.parse(datatype).label;
            field.attributes.datatype.name = JSON.parse(datatype).name;


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
        modalInstance.result.then(function(field) {
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
        console.log($scope.segFlavors);
    };
    $scope.selectFlavor = function(field, flavor) {
        console.log(flavor);
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

        $scope.loadLibrariesByFlavorName = function() {
            console.log(field);
            var delay = $q.defer();
            $scope.ext = null;
            $scope.datatypes = [];
            if (field.attributes.datatype) {
                $scope.datatypes = $scope.datatypes.concat(filterFlavors($rootScope.igdocument.profile.datatypeLibrary, field.attributes.datatype.name));

            } else {
                $scope.datatypes = $scope.datatypes.concat(filterFlavors($rootScope.igdocument.profile.datatypeLibrary, field.attributes.oldDatatype.name));

            }
            $scope.datatypes = _.uniq($scope.datatypes, function(item, key, a) {
                return item.id;
            });
            return delay.promise;
        };


        var filterFlavors = function(library, name) {
            var results = [];
            _.each(library.children, function(link) {
                if (link.name === name) {
                    link.libraryName = library.metaData.name;
                    link.hl7Version = $rootScope.datatypesMap[link.id].hl7Version;
                    //link.hl7Version = library.metaData.hl7Version;
                    results.push(link);
                }
            });
            return results;
        };




        $scope.loadLibrariesByFlavorName().then(function(done) {
            console.log($scope.results);
            // $scope.selection.selected = $scope.currentDatatype.id;
            // $scope.showSelectedDetails($scope.currentDatatype);
        });
    };


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
    function($scope, $rootScope, $modalInstance, pc, PcService, messages, $http, SegmentLibrarySvc) {
        if (pc.appliedTo === null) {
            pc.appliedTo = [];
        }



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
        $scope.apply = function() {
            // pc.appliedTo.push({
            //     id: $scope.applyTo.id,
            //     name: $scope.applyTo.name
            // });
            $rootScope.profileComponent.appliedTo.push({
                id: $scope.applyTo.id,
                name: $scope.applyTo.name
            });
            console.log("$rootScope.messages");
            console.log($rootScope.messages);
            for (var i = 0; i < $rootScope.messages.children.length; i++) {
                if ($rootScope.messages.children[i].id === $scope.applyTo.id) {
                    if (!$rootScope.messages.children[i].appliedPc) {
                        $rootScope.messages.children[i].appliedPc = [];
                    }
                    console.log($rootScope.messages.children[i]);
                    $rootScope.messages.children[i].appliedPc.push(pc.id);
                }
            }

            $modalInstance.close();
            // PcService.save(pc).then(function(result) {
            //     console.log(result);

            //     $modalInstance.close();

            // });


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

                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    attributes: {
                        ref: {
                            id: $rootScope.segmentsMap[pc.ref.id].id,
                            name: $rootScope.segmentsMap[pc.ref.id].name,
                            ext: $rootScope.segmentsMap[pc.ref.id].ext,
                            label: $rootScope.segmentsMap[pc.ref.id].label,

                        }
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
                    attributes: {},
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
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables
                        },
                        appliedTo: [],
                        version: ""
                    };
                } else if (parent.type === 'segmentRef') {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.parent + '.' + $rootScope.segmentsMap[parent.ref.id].label + '.' + pc.position,
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables
                        },
                        appliedTo: [],
                        version: ""
                    };
                }

            } else if (pc.type === 'component') {
                if (parent.type === "datatype") {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.label + '.' + pc.position,
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables
                        },
                        appliedTo: [],
                        version: ""
                    };

                } else {
                    var newPc = {
                        id: new ObjectId().toString(),
                        name: pc.name,
                        type: pc.type,
                        path: parent.parent + '.' + parent.position + '.' + pc.position,
                        attributes: {
                            oldDatatype: pc.datatype,
                            oldTables: pc.tables
                        },
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
                    path: $rootScope.segmentsMap[pc.id].label,
                    attributes: {
                        ref: {
                            id: $rootScope.segmentsMap[pc.id].id,

                            name: $rootScope.segmentsMap[pc.id].name,
                            ext: $rootScope.segmentsMap[pc.id].ex,
                            label: $rootScope.segmentsMap[pc.id].label,

                        }
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
                            for (var i = 0; i < parent.fields.length; i++) {
                                console.log(parent.fields[i]);
                                if (parent.type === "segment") {
                                    parent.fields[i].parent = parent.label;
                                }
                                if (parent.type === "field" || parent.type === "component") {
                                    parent.fields[i].parent = parent.parent + '.' + parent.position;
                                }
                                if (parent.fields[i].datatype) {
                                    parent.fields[i].fields = datatypesMap[parent.fields[i].datatype.id].components;
                                }
                            }
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
            console.log($rootScope.profileComponent);
            for (var i = 0; i < $scope.selectedPC.length; i++) {
                $rootScope.profileComponent.children.push($scope.selectedPC[i]);
            }
            $modalInstance.close();

            // PcService.addPCs(currentPc.id, $scope.selectedPC).then(function(profileC) {
            //     $rootScope.profileComponent = profileC;
            //     
            // });
        };


        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });