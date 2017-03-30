angular.module('igl').controller('ListProfileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, PcLibraryService, PcService, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, MessageService,TableService) {
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
    $scope.redirectVS = function(binding) {
        console.log("bindiiing");
        console.log(binding);
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

    $scope.findingSingleElement = function(node) {
        var result = null;

        if (node && (node.type === "field" || node.type === "component")) {
            var index = node.path.indexOf(".");
            var path = node.path.substr(index + 1);
            if (!node.singleElementValues || node.singleElementValues.length <= 0) {
                if (node.from === "message") {
                    result = _.filter(node.oldSingleElementValues, function(binding) { return binding.location == path; });
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
        var modalInstance = $modal.open({
            templateUrl: 'EditSingleElement.html',
            controller: 'EditSingleElementCtrlInPc',
            backdrop: true,
            keyboard: true,
            windowClass: 'input-text-modal-window',
            backdropClick: false,
            resolve: {
                currentNode: function() {
                    return node;
                }
            }
        });

        modalInstance.result.then(function(value) {
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
    };
    $scope.updateMinL = function(node) {
        if (parseInt(node.minLength) === node.attributes.oldMinLength) {
            node.attributes.minLength = null;
        } else {
            node.attributes.minLength = parseInt(node.minLength);
        }
    };


    $scope.cancelMinL = function(field) {
        field.attributes.minLength = null;
        field.minLength = field.attributes.oldMinLength;

        $scope.setDirty();
    };
    $scope.initMaxL = function(node) {
        if (node.attributes.maxLength) {
            node.maxLength = node.attributes.maxLength;
        } else {
            node.maxLength = node.attributes.oldMaxLength;
        }
    };
    $scope.updateMaxL = function(node) {
        if (node.maxLength === node.attributes.oldMaxLength) {
            node.attributes.maxLength = null;
        } else {
            node.attributes.maxLength = node.maxLength;
        }
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
    };
    $scope.updateConfL = function(node) {
        if (node.confLength === node.attributes.oldConfLength) {
            node.attributes.confLength = null;
        } else {
            node.attributes.confLength = node.confLength;
        }
    };

    $scope.cancelConfL = function(field) {
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
                field.attributes.datatype =field.datatype;
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
    function($scope, $rootScope, $modalInstance, messages, segments, segmentsMap, datatypesMap, currentPc, PcLibraryService, datatypes, ngTreetableParams, $http, SegmentLibrarySvc, PcService) {
        $scope.selectedPC = [];
        console.log("current pc");
        console.log(currentPc);
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
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;

                                    if (parent.children[i].type === 'segmentRef') {

                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                                        parent.children[i].from = "message";


                                    }
                                } else if (parent.type === 'message') {
                                    parent.children[i].parent = parent.structID;
                                    parent.children[i].parentValueSetBindings = parent.valueSetBindings;
                                    parent.children[i].parentComments = parent.comments;
                                    parent.children[i].parentSingleElementValues = parent.singleElementValues;

                                    if (parent.children[i].type === 'segmentRef') {
                                        parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                                        parent.children[i].from = "message";

                                    }
                                } else if (parent.type === 'segmentRef') {
                                    parent.children[i].parent = parent.parent + '.' + parent.position;
                                    parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                                    parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.children[i].parentComments = parent.parentComments;
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;

                                    parent.children[i].from = "message";


                                } else if (parent.type === 'field' || parent.type === 'component') {

                                    parent.children[i].parent = parent.parent + '.' + parent.position;
                                    parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                                    parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.children[i].parentComments = parent.parentComments;
                                    parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;

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
            if (pc.type === 'segmentRef') {
                var newPc = {
                    id: new ObjectId().toString(),
                    type: pc.type,
                    path: pc.parent + '.' + pc.position,
                    itemId: pc.id,
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
                        oldUsage: pc.usage,
                        usage: null,
                        oldMin: pc.min,
                        min: null,
                        oldMax: pc.max,
                        max: null,
                        oldComment: pc.comment,
                        comment: null,
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
                    type: pc.type,
                    path: $rootScope.segmentsMap[pc.id].label,
                    pathExp: $rootScope.segmentsMap[pc.id].label,
                    from: "segment",
                    itemId: pc.id,
                    attributes: {
                        ref: {
                            id: $rootScope.segmentsMap[pc.id].id,
                            name: $rootScope.segmentsMap[pc.id].name,
                            ext: $rootScope.segmentsMap[pc.id].ex,
                            label: $rootScope.segmentsMap[pc.id].label,

                        },
                        oldRef: {
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
                    itemId: pc.id,
                    attributes: {},
                    appliedTo: [],
                    version: ""
                };


            };
            console.log("newPc");
            console.log(newPc);
            newPc.oldValueSetBindings = $scope.findingBindings(newPc);
            newPc.oldSingleElementValues = $scope.findingSingleElement(newPc);
            newPc.oldComments = $scope.findingComments(newPc);
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

                                    parent.fields[i].from = "segment";
                                }
                                if (parent.type === "field" || parent.type === "component") {
                                    parent.fields[i].parent = parent.parent + '.' + parent.position;
                                    parent.fields[i].parentValueSetBindings = parent.parentValueSetBindings;
                                    parent.fields[i].parentSingleElementValues = parent.parentSingleElementValues;
                                    parent.fields[i].parentComments = parent.parentComments;

                                    parent.fields[i].from = "segment";

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
angular.module('igl').controller('EditSingleElementCtrlInPc', function($scope, $rootScope, $modalInstance, userInfoService, currentNode) {
    $scope.currentNode = currentNode;

    $scope.sevVale = '';
    if (currentNode.singleElementValues && currentNode.singleElementValues.value !== null && currentNode.singleElementValues.location !== null) {
        $scope.sevVale = currentNode.singleElementValues.value;

    } else if (currentNode.oldSingleElementValues && currentNode.oldSingleElementValues.value !== null && currentNode.oldSingleElementValues.location !== null) {
        $scope.sevVale = currentNode.oldSingleElementValues.value;

    }


    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        $modalInstance.close($scope.sevVale);
    };
});
angular.module('igl').controller('EditCommentCtrlInPc', function($scope, $rootScope, $modalInstance, userInfoService, currentNode, currentComment, disabled, type) {
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
        $modalInstance.dismiss('cancel');
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
            currentNode.comments.push(newComment);
        }

        $modalInstance.close($scope.currentNode);
    };
});



angular.module('igl').controller('TableBindingForMsgCtrl', function($scope, $modalInstance, currentNode, $rootScope, blockUI,TableService) {
    $scope.changed = false;
    console.log(currentNode);
    $scope.currentNode = currentNode;
    $scope.currentNode.locationPath = currentNode.path;
    $scope.isSingleValueSetAllowed = false;
    $scope.valueSetSelectedForSingleCode = null;

    $scope.singleCodeInit = function() {
        $scope.valueSetSelectedForSingleCode = null;
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
        if($scope.isSingleValueSetAllowed) $scope.selectedValueSetBindings = [];
        if($scope.selectedValueSetBindings.length > 0 && $scope.selectedValueSetBindings[0].type == 'singlecode') $scope.selectedValueSetBindings = [];
        if($scope.listOfBindingLocations){
            $scope.selectedValueSetBindings.push({ tableId: v.id, bindingStrength: "R", location: positionPath, bindingLocation: "1", usage: currentNode.usage, type: "valueset" });
        }else {
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
    $scope.selectValueSetForSingleCode = function (v){
        console.log(v);
        TableService.getOne(v.id).then(function(tbl) {
            $scope.valueSetSelectedForSingleCode = tbl;
        }, function() {
        });
    };
    $scope.isCodeSelected = function (c){
        for (var i = 0; i < $scope.selectedValueSetBindings.length; i++) {
            if($scope.selectedValueSetBindings[i].code){
                if($scope.selectedValueSetBindings[i].code.id == c.id) return true;
            }
        }
        return false;
    };
    $scope.selectCode = function (c){
        $scope.selectedValueSetBindings = [];
        $scope.selectedValueSetBindings.push({ tableId: $scope.valueSetSelectedForSingleCode.id, location: positionPath, usage: currentNode.usage, type: "singlecode", code : c});
        $scope.changed = true;
    };
     $scope.unselectCode = function(c){
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