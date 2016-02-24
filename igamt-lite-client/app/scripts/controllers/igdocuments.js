/**
 * Created by haffo on 1/12/15.
 */

angular.module('igl')
    .controller('IGDocumentListCtrl', function ($scope, $rootScope, Restangular, $http, $filter, $modal, $cookies, $timeout, userInfoService, ToCSvc, ContextMenuSvc, ProfileAccessSvc, ngTreetableParams, $interval, ColumnSettings, StorageService) {
        $scope.loading = false;
        $scope.uiGrid = {};
        $rootScope.igs = [];
        $scope.tmpIgs = [].concat($rootScope.igs);
        $scope.error = null;
        $scope.loading = false;
        $scope.columnSettings = ColumnSettings;
//        $scope.visibleColumns = angular.copy(ColumnSettings.visibleColumns);

        $scope.igDocumentMsg = {};
        $scope.igDocumentConfig = {
            selectedType: null
        };


        $scope.options = {
            'readonly': false
        };

        $scope.igDocumentTypes = [
            {
                name: "Predefined Implementation Guides", type: 'PRELOADED'
            },
            {
                name: "User Implementation Guides", type: 'USER'
            }
        ];
        $scope.loadingIGDocument = false;
        $scope.toEditIGDocumentId = null;
        $scope.verificationResult = null;
        $scope.verificationError = null;
        $scope.csWidth = null;
        $scope.predWidth = null;
        $scope.tableWidth = null;
        $scope.commentWidth = null;
        $scope.loadingSelection = false;
        $scope.accordi = {metaData: false, definition: true, igList: true, igDetails: false};

        $scope.selectIgTab = function (value) {
            if (value === 1) {
                $scope.accordi.igList = false;
                $scope.accordi.igDetails = true;
            } else {
                $scope.accordi.igList = true;
                $scope.accordi.igDetails = false;
            }
        };

        $scope.segmentsParams = new ngTreetableParams({
            getNodes: function (parent) {
                return parent ? parent.fields ? parent.fields : parent.datatype ? $rootScope.datatypesMap[parent.datatype].components : parent.children : $rootScope.segment != null ? $rootScope.segment.fields : [];
            },
            getTemplate: function (node) {
                if ($scope.options.readonly) {
                    return node.type === 'segment' ? 'SegmentReadTree.html' : node.type === 'field' ? 'SegmentFieldReadTree.html' : 'SegmentComponentReadTree.html';
                } else {
                    return node.type === 'segment' ? 'SegmentEditTree.html' : node.type === 'field' ? 'SegmentFieldEditTree.html' : 'SegmentComponentEditTree.html';
                }
            }
        });

        $scope.datatypesParams = new ngTreetableParams({
            getNodes: function (parent) {
                if (parent && parent != null) {

                    if (parent.datatype) {
                        var dt = $rootScope.datatypesMap[parent.datatype];
                        return dt.components;

                    } else {
                        return parent.components;
                    }
                } else {
                    if ($rootScope.datatype != null) {
                        return $rootScope.datatype.components;
                    } else {
                        return [];
                    }
                }
            },
            getTemplate: function (node) {
                if ($scope.options.readonly) {
                    return node.type === 'Datatype' ? 'DatatypeReadTree.html' : node.type === 'component' && !$scope.isDatatypeSubDT(node) ? 'DatatypeComponentReadTree.html' : node.type === 'component' && $scope.isDatatypeSubDT(node) ? 'DatatypeSubComponentReadTree.html' : '';
                } else {
                    return node.type === 'Datatype' ? 'DatatypeEditTree.html' : node.type === 'component' && !$scope.isDatatypeSubDT(node) ? 'DatatypeComponentEditTree.html' : node.type === 'component' && $scope.isDatatypeSubDT(node) ? 'DatatypeSubComponentEditTree.html' : '';
                }
            }
        });


        $scope.isDatatypeSubDT = function (component) {
            if ($rootScope.datatype != null) {
                for (var i = 0, len = $rootScope.datatype.components.length; i < len; i++) {
                    if ($rootScope.datatype.components[i].id === component.id)
                        return false;
                }
            }
            return true;
        };

        $rootScope.closeIGDocument = function () {
            $rootScope.igdocument = null;
            $rootScope.isEditing = false;
            $scope.selectIgTab(0);
            $rootScope.initMaps();
            $rootScope.clearChanges();
        };

        $scope.messagesParams = new ngTreetableParams({
            getNodes: function (parent) {
                if (!parent || parent == null) {
                    if ($rootScope.message != null) {
                        return $rootScope.message.children;
                    } else {
                        return [];
                    }
                } else if (parent.type === 'segmentRef') {
                    return $rootScope.segmentsMap[parent.ref].fields;
                } else if (parent.type === 'field') {
                    return $rootScope.datatypesMap[parent.datatype].components;
                } else if (parent.type === 'component') {
                    return $rootScope.datatypesMap[parent.datatype].components;
                } else if (parent.type === 'group') {
                    return parent.children;
                } else {
                    return [];
                }

            },
            getTemplate: function (node) {
                if ($scope.options.readonly) {

                    if (node.type === 'segmentRef') {
                        return 'MessageSegmentRefReadTree.html';
                    } else if (node.type === 'group') {
                        return 'MessageGroupReadTree.html';
                    } else if (node.type === 'field') {
                        return 'MessageFieldViewTree.html';
                    } else if (node.type === 'component') {
                        return 'MessageComponentViewTree.html';
                    } else {
                        return 'MessageReadTree.html';
                    }
                } else {
                    if (node.type === 'segmentRef') {
                        return 'MessageSegmentRefEditTree.html';
                    } else if (node.type === 'group') {
                        return 'MessageGroupEditTree.html';
                    } else if (node.type === 'field') {
                        return 'MessageFieldViewTree.html';
                    } else if (node.type === 'component') {
                        return 'MessageComponentViewTree.html';
                    } else {
                        return 'MessageEditTree.html';
                    }
                }
            }
//            options: {
//                initialState: 'expanded'
//            }
        });

        /**
         * init the controller
         */
        $scope.initIGDocuments = function () {
            $scope.igDocumentConfig.selectedType = StorageService.get("SelectedIgDocumentType") != null ? StorageService.get("SelectedIgDocumentType") : 'USER';
            $scope.loadIGDocuments();
            $scope.getScrollbarWidth();
            /**
             * On 'event:loginConfirmed', resend all the 401 requests.
             */
            $scope.$on('event:loginConfirmed', function (event) {
                $scope.igDocumentConfig.selectedType = StorageService.get("SelectedIgDocumentType") != null ? StorageService.get("SelectedIgDocumentType") : 'USER';
                $scope.loadIGDocuments();
            });

            $rootScope.$on('event:openIGDocumentRequest', function (event, igdocument) {
                $rootScope.igdocument = igdocument;
                $scope.openIGDocument(igdocument);
            });

            $scope.$on('event:openDatatype', function (event, datatype) {
                $scope.selectDatatype(datatype); // Should we open in a dialog ??
            });

            $scope.$on('event:openSegment', function (event, segment) {
                $scope.selectSegment(segment); // Should we open in a dialog ??
            });

            $scope.$on('event:openMessage', function (event, message) {
                console.log("event:openMessage=" + message);
                $scope.selectMessage(message); // Should we open in a dialog ??
            });

            $scope.$on('event:openTable', function (event, table) {
                $scope.selectTable(table); // Should we open in a dialog ??
            });

            $scope.$on('event:openSection', function (event, section) {
                $scope.selectSection(section); // Should we open in a dialog ??
            });

            $scope.$on('event:openDocumentMetadata', function (event, metaData) {
                $scope.selectDocumentMetaData(metaData); // Should we open in a dialog ??
            });

            $scope.$on('event:openProfileMetadata', function (event, metaData) {
                $scope.selectProfileMetaData(metaData); // Should we open in a dialog ??
            });

            $rootScope.$on('event:SetToC', function (event) {
                $rootScope.tocData = ToCSvc.getToC($rootScope.igdocument);
            });

            $rootScope.$on('event:IgsPushed', function (event, igdocument) {
                if ($scope.igDocumentConfig.selectedType === 'USER') {
                    $rootScope.igs.push(igdocument);
                } else {
                    $scope.igDocumentConfig.selectedType = 'USER';
                    $scope.loadIGDocuments();
                }
            });
        };

        $scope.selectIGDocumentType = function (selectedType) {
            $scope.igDocumentConfig.selectedType = selectedType;
            StorageService.set("SelectedIgDocumentType", selectedType);
            $scope.loadIGDocuments();
        };

        $scope.loadIGDocuments = function () {
            $scope.error = null;
            $rootScope.igs = [];
            $scope.tmpIgs = [].concat($rootScope.igs);
            if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
                waitingDialog.show('Loading IG Documents...', {dialogSize: 'sm', progressType: 'info'});
                $scope.loading = true;
                StorageService.set("SelectedIgDocumentType", $scope.igDocumentConfig.selectedType);
                $http.get('api/igdocuments', {params: {"type": $scope.igDocumentConfig.selectedType}}).then(function (response) {
                    $rootScope.igs = angular.fromJson(response.data);
                    $scope.tmpIgs = [].concat($rootScope.igs);
                    $scope.loading = false;
                    waitingDialog.hide();
                }, function (error) {
                    $scope.loading = false;
                    $scope.error = error.data;
                    waitingDialog.hide();
                });
            }
        };

        $scope.clone = function (igdocument) {
            $scope.toEditIGDocumentId = igdocument.id;
            waitingDialog.show('Copying IG Document...', {dialogSize: 'sm', progressType: 'info'});
            $http.post('api/igdocuments/' + igdocument.id + '/clone').then(function (response) {
                $scope.toEditIGDocumentId = null;
                if ($scope.igDocumentConfig.selectedType === 'USER') {
                    $rootScope.igs.push(angular.fromJson(response.data));
                } else {
                    $scope.igDocumentConfig.selectedType = 'USER';
                    $scope.loadIGDocuments();
                }
                waitingDialog.hide();
            }, function (error) {
                $scope.toEditIGDocumentId = null;
                waitingDialog.hide();
            });
        };

        $scope.findOne = function (id) {
            for (var i = 0; i < $rootScope.igs.length; i++) {
                if ($rootScope.igs[i].id === id) {
                    return  $rootScope.igs[i];
                }
            }
            return null;
        };

        $scope.show = function (igdocument) {
            $scope.toEditIGDocumentId = igdocument.id;
            try {
                if ($rootScope.igdocument != null && $rootScope.igdocument === igdocument) {
                    $scope.openIGDocument(igdocument);
                } else if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
                    $scope.confirmOpen(igdocument);
                    $scope.toEditIGDocumentId = null;
                } else {
                    $scope.openIGDocument(igdocument);
                }
            } catch (e) {
                $rootScope.msg().text = "igInitFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                $scope.loadingIGDocument = false;
                $scope.toEditIGDocumentId = null;
            }
        };

        $scope.edit = function (igdocument) {
            $scope.options.readonly = false;
            $scope.show(igdocument);
        };

        $scope.view = function (igdocument) {
            $scope.options.readonly = true;
            $scope.show(igdocument);
        };

        $scope.openIGDocument = function (igdocument) {
            if (igdocument != null) {
                waitingDialog.show('Opening IG Document...', {dialogSize: 'sm', progressType: 'info'});
                $scope.selectIgTab(1);
                $timeout(function () {
                    $scope.loadingIGDocument = true;
                    $rootScope.isEditing = true;
                    $rootScope.igdocument = igdocument;
                    $rootScope.igdocument.profile.messages.children = $filter('orderBy')($rootScope.igdocument.profile.messages.children, 'label');
                    $rootScope.igdocument.profile.segments.children = $filter('orderBy')($rootScope.igdocument.profile.segments.children, 'label');
                    $rootScope.igdocument.profile.datatypes.children = $filter('orderBy')($rootScope.igdocument.profile.datatypes.children, 'label');
                    $rootScope.igdocument.profile.tables.children = $filter('orderBy')($rootScope.igdocument.profile.tables.children, 'label');
                    $rootScope.tocData = ToCSvc.getToC($rootScope.igdocument);
                    $rootScope.initMaps();
                    $rootScope.messages = $rootScope.igdocument.profile.messages.children;
                    angular.forEach($rootScope.igdocument.profile.datatypes.children, function (child) {
                        this[child.id] = child;
                        if (child.displayName) { // TODO: Change displayName to label
                            child.label = child.displayName;
                        }
                    }, $rootScope.datatypesMap);
                    angular.forEach($rootScope.igdocument.profile.segments.children, function (child) {
                        this[child.id] = child;
                        if (child.displayName) { // TODO: Change displayName to label
                            child.label = child.displayName;
                        }
                    }, $rootScope.segmentsMap);

                    angular.forEach($rootScope.igdocument.profile.tables.children, function (child) {
                        this[child.id] = child;
                        if (child.displayName) { // TODO: Change displayName to label
                            child.label = child.displayName;
                        }
                        angular.forEach(child.codes, function (code) {
                            if (code.displayName) { // TODO: Change displayName to label
                                code.label = code.displayName;
                            }
                        });
                    }, $rootScope.tablesMap);

                    $rootScope.segments = [];
                    $rootScope.tables = $rootScope.igdocument.profile.tables.children;
                    $rootScope.datatypes = $rootScope.igdocument.profile.datatypes.children;

                    angular.forEach($rootScope.igdocument.profile.messages.children, function (child) {
                        this[child.id] = child;
                        var cnt = 0;
                        angular.forEach(child.children, function (segmentRefOrGroup) {
                            $rootScope.processElement(segmentRefOrGroup);
                        });
                    }, $rootScope.messagesMap);

                    if (!$rootScope.config || $rootScope.config === null) {
                        $http.get('api/igdocuments/config').then(function (response) {
                            $rootScope.config = angular.fromJson(response.data);
                            $scope.loadingIGDocument = false;
                            $scope.toEditIGDocumentId = null;
                            $scope.selectDocumentMetaData();
                        }, function (error) {
                            $scope.loadingIGDocument = false;
                            $scope.toEditIGDocumentId = null;
                        });
                    } else {
                        $scope.loadingIGDocument = false;
                        $scope.toEditIGDocumentId = null;
                        $scope.selectDocumentMetaData();
                    }
                    waitingDialog.hide();
                }, 100);
            }
        };

        $scope.collectData = function (node, segRefOrGroups, segments, datatypes) {
            if (node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'group') {
                    segRefOrGroups.push(node);
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                        });
                    }
                    segRefOrGroups.push({ name: node.name, "type": "end-group"});
                } else if (node.type === 'segment') {
                    if (segments.indexOf(node) === -1) {
                        segments.push(node);
                    }
                    angular.forEach(node.fields, function (field) {
                        $scope.collectData(field, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'segmentRef') {
                    segRefOrGroups.push(node);
                    $scope.collectData($rootScope.segmentsMap[node.ref], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'component' || node.type === 'subcomponent' || node.type === 'field') {
                    $scope.collectData($rootScope.datatypesMap[node.datatype], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'datatype') {
                    if (datatypes.indexOf(node) === -1) {
                        datatypes.push(node);
                    }
                    if (node.components) {
                        angular.forEach(node.children, function (component) {
                            $scope.collectData(component, segRefOrGroups, segments, datatypes);
                        });
                    }
                }
            }
        };

        $scope.confirmDelete = function (igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentDeleteCtrl.html',
                controller: 'ConfirmIGDocumentDeleteCtrl',
                resolve: {
                    igdocumentToDelete: function () {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function (igdocument) {
                $scope.igdocumentToDelete = igdocument;
                var idxP = _.findIndex($scope.igs, function (child) {
                    return child.id === igdocument.id;
                });
                $scope.igs.splice(idxP, 1);
            });
        };

        $scope.confirmClose = function () {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentCloseCtrl.html',
                controller: 'ConfirmIGDocumentCloseCtrl'
            });
            modalInstance.result.then(function () {
            }, function () {
            });
        };

        $scope.confirmOpen = function (igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
                controller: 'ConfirmIGDocumentOpenCtrl',
                resolve: {
                    igdocumentToOpen: function () {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function (igdocument) {
                $scope.openIGDocument(igdocument);
            }, function () {
            });
        };
        
        
        $scope.selectMessages = function (igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectMessagesOpenCtrl.html',
                controller: 'SelectMessagesOpenCtrl',
                resolve: {
                    igdocumentToSelect: function () {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function () {
            }, function () {
            });
        };
        
        
        $scope.exportAsMessages = function (id, mids) {
        	var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + id + '/export/pdf/' + mids);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        }
        
        $scope.exportAs = function (id, format) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + id + '/export/' + format);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        };

        $scope.exportDelta = function (id, format) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + id + '/delta/' + format);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        };

        $scope.close = function () {
            if ($rootScope.hasChanges()) {
                $scope.confirmClose();
            } else {
                waitingDialog.show('Closing igdocument...', {dialogSize: 'sm', progressType: 'info'});
                $rootScope.closeIGDocument();
                waitingDialog.hide();
            }
        };

        $scope.gotoSection = function (obj, type) {
            $rootScope.section['data'] = obj;
            $rootScope.section['type'] = type;
        };

        $scope.save = function () {
            $scope.igDocumentMsg = {};
            waitingDialog.show('Saving changes...', {dialogSize: 'sm', progressType: 'success'});
            var changes = angular.toJson($rootScope.changes);
            $rootScope.igdocument.accountId = userInfoService.getAccountID();
            var data = angular.fromJson(
                    {
                        "changes": changes, "igDocument": $rootScope.igdocument
                    }
            );
            $http.post('api/igdocuments/save', data).then(function (response) {
                var saveResponse = angular.fromJson(response.data);
                $rootScope.igdocument.metaData.date = saveResponse.date;
                $rootScope.igdocument.metaData.version = saveResponse.version;
                var found = $scope.findOne($rootScope.igdocument.id);
                if (found != null) {
                    var index = $rootScope.igs.indexOf(found);
                    if (index > 0) {
                        $rootScope.igs [index] = $rootScope.igdocument;
                    }
                }
                $scope.igDocumentMsg.text = saveResponse.text;
                $scope.igDocumentMsg.type = saveResponse.type;
                $scope.igDocumentMsg.show = true;
                $rootScope.clearChanges();
                waitingDialog.hide();
            }, function (error) {
                $scope.igDocumentMsg.text = error.data.text;
                $scope.igDocumentMsg.type = error.data.type;
                $scope.igDocumentMsg.show = true;
                waitingDialog.hide();
            });
        };

        $scope.exportChanges = function () {
            var form = document.createElement("form");
            form.action = 'api/igdocuments/export/changes';
            form.method = "POST";
            form.target = "_target";
            var input = document.createElement("textarea");
            input.name = "content";
            input.value = angular.fromJson($rootScope.changes);
            form.appendChild(input);
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        };

        $scope.viewChanges = function (changes) {
            var modalInstance = $modal.open({
                templateUrl: 'ViewIGChangesCtrl.html',
                controller: 'ViewIGChangesCtrl',
                resolve: {
                    changes: function () {
                        return changes;
                    }
                }
            });
            modalInstance.result.then(function (changes) {
                $scope.changes = changes;
            }, function () {
            });
        };


        $scope.reset = function () {
            $rootScope.changes = {};
            $rootScope.closeIGDocument();
        };


        $scope.initIGDocument = function () {
            $scope.loading = true;
            if ($rootScope.igdocument != null && $rootScope.igdocument != undefined)
                $scope.gotoSection($rootScope.igdocument.metaData, 'metaData');
            $scope.loading = false;

        };

        $scope.createGuide = function () {
            $scope.isVersionSelect = true;
        };

        $scope.listHL7Versions = function () {
            var hl7Versions = [];
            $http.get('api/igdocuments/hl7/findVersions', {
                timeout: 60000
            }).then(
                function (response) {
                    var len = response.data.length;
                    for (var i = 0; i < len; i++) {
                        hl7Versions.push(response.data[i]);
                    }
                });
            return hl7Versions;
        };

        $scope.showSelected = function (node) {
            $scope.selectedNode = node;
        };

        $scope.selectSegment = function (segment) {
            $scope.subview = "EditSegments.html";
            if (segment && segment != null) {
                $scope.loadingSelection = true;
                $rootScope.segment = segment;
                $rootScope.segment["type"] = "segment";
                $timeout(
                    function () {
                        $scope.tableWidth = null;
                        $scope.scrollbarWidth = $scope.getScrollbarWidth();
                        $scope.csWidth = $scope.getDynamicWidth(1, 3, 990);
                        $scope.predWidth = $scope.getDynamicWidth(1, 3, 990);
                        $scope.commentWidth = $scope.getDynamicWidth(1, 3, 990);
                        if ($scope.segmentsParams)
                            $scope.segmentsParams.refresh();
                        $scope.loadingSelection = false;
                    }, 100);
            }
        };

        $scope.selectDocumentMetaData = function () {
            $scope.subview = "EditDocumentMetadata.html";
            $scope.loadingSelection = true;
            $timeout(
                function () {
                    $scope.loadingSelection = false;
                }, 100);
        };

        $scope.selectProfileMetaData = function () {
            $scope.subview = "EditProfileMetadata.html";
            $scope.loadingSelection = true;
            $timeout(
                function () {
                    $scope.loadingSelection = false;
                }, 100);
        };

        $scope.selectDatatype = function (datatype) {
            $scope.subview = "EditDatatypes.html";
            if (datatype && datatype != null) {
                $scope.loadingSelection = true;
                $rootScope.datatype = datatype;
                $rootScope.datatype["type"] = "datatype";
                $timeout(
                    function () {
                        $scope.tableWidth = null;
                        $scope.scrollbarWidth = $scope.getScrollbarWidth();
                        $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
                        $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
                        $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
                        if ($scope.datatypesParams)
                            $scope.datatypesParams.refresh();
                        $scope.loadingSelection = false;
                    }, 100);
            }
        };

        $scope.selectMessage = function (message) {
            $scope.subview = "EditMessages.html";
            $scope.loadingSelection = true;
            $rootScope.message = message;
            $timeout(
                function () {
                    $scope.tableWidth = null;
                    $scope.scrollbarWidth = $scope.getScrollbarWidth();
                    $scope.csWidth = $scope.getDynamicWidth(1, 3, 630);
                    $scope.predWidth = $scope.getDynamicWidth(1, 3, 630);
                    $scope.commentWidth = $scope.getDynamicWidth(1, 3, 630);
                    if ($scope.messagesParams)
                        $scope.messagesParams.refresh();
                    $scope.loadingSelection = false;
                }, 100);
        };

        $scope.selectTable = function (table) {
            $scope.subview = "EditValueSets.html";
            $scope.loadingSelection = true;
            $timeout(
                function () {
                    $rootScope.table = table;
                    $rootScope.codeSystems = [];

                    for (var i = 0; i < $rootScope.table.codes.length; i++) {
                        if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
                            if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                                $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
                            }
                        }
                    }
                    $scope.loadingSelection = false;
                }, 100);
        };

        $scope.selectSection = function (section) {
            $scope.subview = "EditSections.html";
            $scope.loadingSelection = true;
            $timeout(
                function () {
                    $rootScope.section = section;
                    $scope.loadingSelection = false;
                }, 100);
        };


        $scope.getTableWidth = function () {
            if ($scope.tableWidth === null || $scope.tableWidth == 0) {
                $scope.tableWidth = $("#nodeDetailsPanel").width();
            }
            return $scope.tableWidth;
        };

        $scope.getDynamicWidth = function (a, b, otherColumsWidth) {
            var tableWidth = $scope.getTableWidth();
            if (tableWidth > 0) {
                var left = tableWidth - otherColumsWidth;
                return {"width": a * parseInt(left / b) + "px"};
            }
            return "";
        };


        $scope.getConstraintAsString = function (constraint) {
            return constraint.constraintId + " - " + constraint.description;
        };

        $scope.getConformanceStatementAsString = function (constraint) {
            return "[" + constraint.constraintId + "]" + constraint.description;
        };

        $scope.getPredicateAsString = function (constraint) {
            return constraint.description;
        };

        $scope.getConstraintsAsString = function (constraints) {
            var str = '';
            for (var index in constraints) {
                str = str + "<p style=\"text-align: left\">" + constraints[index].id + " - " + constraints[index].description + "</p>";
            }
            return str;
        };

        $scope.getPredicatesAsMultipleLinesString = function (node) {
            var html = "";
            angular.forEach(node.predicates, function (predicate) {
                html = html + "<p>" + predicate.description + "</p>";
            });
            return html;
        };

        $scope.getPredicatesAsOneLineString = function (node) {
            var html = "";
            angular.forEach(node.predicates, function (predicate) {
                html = html + predicate.description;
            });
            return $sce.trustAsHtml(html);
        };


        $scope.getConfStatementsAsMultipleLinesString = function (node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function (conStatement) {
                html = html + "<p>" + conStatement.id + " : " + conStatement.description + "</p>";
            });
            return html;
        };

        $scope.getConfStatementsAsOneLineString = function (node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function (conStatement) {
                html = html + conStatement.id + " : " + conStatement.description;
            });
            return $sce.trustAsHtml(html);
        };

        $scope.getSegmentRefNodeName = function (node) {
            return node.position + "." + $rootScope.segmentsMap[node.ref].name + ":" + $rootScope.segmentsMap[node.ref].description;
        };

        $scope.getGroupNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $scope.getFieldNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $scope.getComponentNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $scope.getDatatypeNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $scope.onColumnToggle = function (item) {
            $scope.columnSettings.save();
        };

        $scope.getFullName = function () {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getFullName();
            }
            return '';
        };
    });

angular.module('igl').controller('ViewIGChangesCtrl', function ($scope, $modalInstance, changes, $rootScope, $http) {
    $scope.changes = changes;
    $scope.loading = false;
    $scope.exportChanges = function () {
        $scope.loading = true;
        waitingDialog.show('Exporting changes...', {dialogSize: 'sm', progressType: 'success'});
        var form = document.createElement("form");
        form.action = 'api/igdocuments/export/changes';
        form.method = "POST";
        form.target = "_target";
        form.style.display = 'none';
        form.params = document.body.appendChild(form);
        form.submit();
        waitingDialog.hide();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmIGDocumentDeleteCtrl', function ($scope, $modalInstance, igdocumentToDelete, $rootScope, $http) {
    $scope.igdocumentToDelete = igdocumentToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        $http.post($rootScope.api('api/igdocuments/' + $scope.igdocumentToDelete.id + '/delete')).then(function (response) {
            var index = $rootScope.igs.indexOf($scope.igdocumentToDelete);
            if (index > -1) $rootScope.igs.splice(index, 1);
            $rootScope.backUp = null;
            if ($scope.igdocumentToDelete === $rootScope.igdocument) {
                $rootScope.closeIGDocument();
            }
            $rootScope.msg().text = "igDeleteSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            $rootScope.manualHandle = true;
            $scope.igdocumentToDelete = null;
            $scope.loading = false;

            $modalInstance.close($scope.igdocumentToDelete);

        }, function (error) {
            $scope.error = error;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
            $rootScope.msg().text = "igDeleteFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

// waitingDialog.hide();
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmIGDocumentCloseCtrl', function ($scope, $modalInstance, $rootScope, $http) {
    $scope.loading = false;
    $scope.discardChangesAndClose = function () {
        $scope.loading = true;
        $http.get('api/igdocuments/' + $rootScope.igdocument.id, {timeout: 60000}).then(function (response) {
            var index = $rootScope.igs.indexOf($rootScope.igdocument);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.clear = function () {
        $rootScope.closeIGDocument();
        $modalInstance.close();
    };

    $scope.saveChangesAndClose = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "igdocument": $rootScope.igdocument};
        $http.post('api/igdocuments/' + $rootScope.igdocument.id + '/save', data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.igdocument.metaData.date = saveResponse.date;
            $rootScope.igdocument.metaData.version = saveResponse.version;
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmIGDocumentOpenCtrl', function ($scope, $modalInstance, igdocumentToOpen, $rootScope, $http) {
    $scope.igdocumentToOpen = igdocumentToOpen;
    $scope.loading = false;
    $scope.discardChangesAndOpen = function () {
        $scope.loading = true;
        $http.get('api/igdocuments/' + $rootScope.igdocument.id, {timeout: 60000}).then(function (response) {
            var index = $rootScope.igs.indexOf($rootScope.igdocument);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $modalInstance.close($scope.igdocumentToOpen);
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.saveChangesAndOpen = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "igdocument": $rootScope.igdocument};
        $http.post('api/igdocuments/' + $rootScope.igdocument.id + '/save', data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.igdocument.metaData.date = saveResponse.date;
            $rootScope.igdocument.metaData.version = saveResponse.version;
            $scope.loading = false;
            $modalInstance.close($scope.igdocumentToOpen);
        }, function (error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('SelectMessagesOpenCtrl', function ($scope, $modalInstance, igdocumentToSelect, $rootScope, $http, $cookies) {
    $scope.igdocumentToSelect = igdocumentToSelect;
    $scope.selectedMessagesIDs = [];
    $scope.loading = false;


    $scope.trackSelections = function(bool, id) {
		if (bool) {
			$scope.selectedMessagesIDs.push(id);
		} else {
			for (var i = 0; i < $scope.selectedMessagesIDs.length; i++) {
				if ($scope.selectedMessagesIDs[i].id == id) {
					$scope.selectedMessagesIDs.splice(i, 1);
				}
			}
		}
	};
	
	 $scope.exportAsMessages = function (id, mids) {
     	var form = document.createElement("form");
     	console.log("ID: " + id);
     	console.log("Message IDs: " + mids);
     	form.action = $rootScope.api('api/igdocuments/' + id + '/export/zip/' + mids);
     	form.method = "POST";
     	form.target = "_target";
     	var csrfInput = document.createElement("input");
     	csrfInput.name = "X-XSRF-TOKEN";
     	csrfInput.value = $cookies['XSRF-TOKEN'];
     	form.appendChild(csrfInput);
     	form.style.display = 'none';
     	document.body.appendChild(form);
     	form.submit();
     }
	 
	
	$scope.exportAsZIPforSelectedMessages = function () {
		$scope.loading = true;
		$scope.exportAsMessages($scope.igdocumentToSelect.id,$scope.selectedMessagesIDs);
        $scope.loading = false;
    };
    
    $scope.exportAsZIPDisplayforSelectedMessages = function () {
		$scope.loading = true;
		$scope.exportAsMessages($scope.igdocumentToSelect.id,$scope.selectedMessagesIDs);
        $scope.loading = false;
    };
    

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
