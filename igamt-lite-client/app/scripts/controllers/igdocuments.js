/**
 * Created by haffo on 1/12/15.
 */

angular.module('igl')
    .controller('IGDocumentListCtrl', function($scope, $rootScope, $templateCache, Restangular, $http, $filter, $modal, $cookies, $timeout, userInfoService, ToCSvc, ContextMenuSvc, ProfileAccessSvc, ngTreetableParams, $interval, ViewSettings, StorageService, $q, Notification, DatatypeService, SegmentService, IgDocumentService, ElementUtils, AutoSaveService, DatatypeLibrarySvc, SegmentLibrarySvc, TableLibrarySvc, TableService, MastermapSvc, MessageService, FilteringSvc, blockUI) {
        $scope.loading = false;
        $scope.tocView = 'views/toc.html';
        $scope.uiGrid = {};
        $rootScope.igs = [];
        $rootScope.currentData = null;
        $scope.tmpIgs = [].concat($rootScope.igs);
        $scope.error = null;
        $scope.loadingTree = false;
        $scope.filtering = false;
        $scope.tocView = 'views/toc.html';
        $scope.print = function(param) {
            //console.log(param);
        }
        $scope.loading = true;
        $scope.viewSettings = ViewSettings;
        $scope.igDocumentMsg = {};
        $scope.igDocumentConfig = {
            selectedType: null
        };
        $rootScope.usageF = false;
        $scope.nodeReady = true;
        $scope.igDocumentTypes = [{
            name: "Browse Existing Preloaded Implementation Guides",
            type: 'PRELOADED'
        }, {
            name: "Access My implementation guides",
            type: 'USER'
        }];
        $scope.loadingIGDocument = false;
        $scope.toEditIGDocumentId = null;
        $scope.verificationResult = null;
        $scope.verificationError = null;
        $scope.loadingSelection = false;
        $scope.accordi = { metaData: false, definition: true, igList: true, igDetails: false, active: { list: true, edit: false } };
        $rootScope.autoSaving = false;
        //        AutoSaveService.stop();
        $rootScope.saved = false;

        $scope.usageFilter = function() {
            blockUI.start();
            $rootScope.usageF = true;
            $('#treeTable').treetable('collapseAll');
            blockUI.stop();
            return false;

        };

        $scope.selectIgTab = function(value) {
            if (value === 1) {
                $scope.accordi.igList = false;
                $scope.accordi.igDetails = true;
            } else {
                $scope.accordi.igList = true;
                $scope.accordi.igDetails = false;
            }
        };

        $scope.segmentsParams = new ngTreetableParams({
            getNodes: function(parent) {
                return SegmentService.getNodes(parent, $rootScope.segment);
            },
            getTemplate: function(node) {
                return SegmentService.getTemplate(node, $rootScope.segment);
            }
        });

        $scope.datatypesParams = new ngTreetableParams({
            getNodes: function(parent) {
                return DatatypeService.getNodes(parent, $rootScope.datatype);
            },
            getTemplate: function(node) {
                return DatatypeService.getTemplate(node, $rootScope.datatype);
            }
        });

        $rootScope.closeIGDocument = function() {
            $rootScope.clearChanges();
            $rootScope.igdocument = null;
            $rootScope.isEditing = false;
            $scope.selectIgTab(0);
            $rootScope.initMaps();
            StorageService.setIgDocument(null);
        };

        $scope.getMessageParams = function() {
            return new ngTreetableParams({
                getNodes: function(parent) {
                    return MessageService.getNodes(parent, $rootScope.messageTree);
                },
                getTemplate: function(node) {
                    return MessageService.getTemplate(node, $rootScope.messageTree);
                }
            });
        };

        /**
         * init the controller
         */
        $scope.initIGDocuments = function() {
            $scope.loadIGDocuments();
            $scope.getScrollbarWidth();
            /**
             * On 'event:loginConfirmed', resend all the 401 requests.
             */
            $scope.$on('event:loginConfirmed', function(event) {
                $scope.loadIGDocuments();
            });

            $rootScope.$on('event:openIGDocumentRequest', function(event, igdocument) {
                $scope.selectIGDocument(igdocument);
            });

            $scope.$on('event:openDatatype', function(event, datatype) {

                $scope.selectDatatype(datatype); // Should we open in a dialog ??
            });

            $scope.$on('event:openSegment', function(event, segment) {

                $scope.selectSegment(segment); // Should we open in a dialog ??
            });

            $scope.$on('event:openMessage', function(event, message) {
                $rootScope.messageTree = null;
                $scope.selectMessage(message); // Should we open in a dialog ??
            });

            $scope.$on('event:openTable', function(event, table) {
                $scope.selectTable(table); // Should we open in a dialog ??
            });

            $scope.$on('event:openSection', function(event, section, referencer) {
                $scope.selectSection(section, referencer); // Should we open in a dialog ??
            });

            $scope.$on('event:openDocumentMetadata', function(event, metaData) {
                $scope.selectDocumentMetaData(metaData); // Should we open in a dialog ??
            });

            $scope.$on('event:openProfileMetadata', function(event, metaData) {
                $scope.selectProfileMetaData(metaData); // Should we open in a dialog ??
            });


            $rootScope.$on('event:IgsPushed', function(event, igdocument) {
                //                console.log("event:IgsPushed=" + igdocument)
                if ($scope.igDocumentConfig.selectedType === 'USER') {
                    var idx = $rootScope.igs.findIndex(function(igd) {
                        return igd.id === igdocument.id;
                    });
                    if (idx > -1) {
                        $timeout(function() {
                            //                            _.each($rootScope.igs, function (igd) {
                            //                                console.log("b msgs=" + igd.metaData.title + " eq=" + (igd === igdocument));
                            //                            });
                            $rootScope.igs.splice(idx, 1);
                            $scope.tmpIgs = [].concat($rootScope.igs);
                            //                            _.each($scope.tmpIgs, function (igd) {
                            //                                console.log("a msgs=" + igd.metaData.title + " eq=" + (igd === igdocument));
                            //                                console.log("msgs=" + igd.metaData.title + " len=" + igd.profile.messages.children.length);
                            //                            });
                        }, 100);
                        $rootScope.igs.push(igdocument);
                    } else {
                        //                        console.log("pushed=>");
                        $rootScope.igs.push(igdocument);
                    }
                } else {
                    $scope.igDocumentConfig.selectedType = 'USER';
                    $scope.loadIGDocuments();
                }
            });

            $rootScope.$on('event:saveAndExecLogout', function(event) {
                if ($rootScope.igdocument != null) {
                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            $rootScope.$emit('event:execLogout');
                        });
                    } else {
                        $rootScope.$emit('event:execLogout');
                    }
                } else {
                    $rootScope.$emit('event:execLogout');
                }
            });
        };

        $scope.selectIGDocumentType = function(selectedType) {
            //console.log("selectIGDocumentType msgs=" + selectedType.metaData.title + " len=" + selectedType.profile.messages.children.length);
            $scope.igDocumentConfig.selectedType = selectedType;
            StorageService.setSelectedIgDocumentType(selectedType);
            $scope.loadIGDocuments();
        };


        $scope.selectIGDocument = function(igdocument) {
            $rootScope.igdocument = igdocument;
            $scope.openIGDocument(igdocument);
        };

        $scope.loadIGDocuments = function() {
            var delay = $q.defer();
            $scope.igDocumentConfig.selectedType = StorageService.getSelectedIgDocumentType() != null ? StorageService.getSelectedIgDocumentType() : 'USER';
            $scope.error = null;
            $rootScope.igs = [];
            $scope.tmpIgs = [].concat($rootScope.igs);
            if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
                $scope.loading = true;
                StorageService.setSelectedIgDocumentType($scope.igDocumentConfig.selectedType);
                $http.get('api/igdocuments', { params: { "type": $scope.igDocumentConfig.selectedType } }).then(function(response) {
                    $rootScope.igs = angular.fromJson(response.data);
                    $scope.tmpIgs = [].concat($rootScope.igs);
                    $scope.loading = false;
                    delay.resolve(true);
                }, function(error) {
                    $scope.loading = false;
                    $scope.error = error.data;
                    delay.reject(false);
                });
            } else {
                delay.reject(false);
            }
            return delay.promise;
        };

        $scope.clone = function(igdocument) {
            $scope.toEditIGDocumentId = igdocument.id;
            $http.post('api/igdocuments/' + igdocument.id + '/clone').then(function(response) {
                $scope.toEditIGDocumentId = null;
                if ($scope.igDocumentConfig.selectedType === 'USER') {
                    $rootScope.igs.push(angular.fromJson(response.data));
                } else {
                    $scope.igDocumentConfig.selectedType = 'USER';
                    $scope.loadIGDocuments();
                }
                $scope.selectIgTab(0);
                $rootScope.msg().text = "igClonedSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
            }, function(error) {
                $scope.toEditIGDocumentId = null;
                $rootScope.msg().text = "igClonedFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        $scope.findOne = function(id) {
            for (var i = 0; i < $rootScope.igs.length; i++) {
                if ($rootScope.igs[i].id === id) {
                    return $rootScope.igs[i];
                }
            }
            return null;
        };

        var preventChangesLost = function() {
            //            if ($rootScope.hasChanges()) {
            //                if(!confirm("You have unsaved changes, Do you want to stay on the page?")) {
            //                    event.preventDefault();
            //                }
            //            }
        }

        $scope.show = function(igdocument) {
            var process = function() {
                $scope.toEditIGDocumentId = igdocument.id;
                try {
                    $scope.openIGDocument(igdocument);
                } catch (e) {
                    $rootScope.msg().text = "igInitFailed";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    $scope.loadingIGDocument = false;
                    $scope.toEditIGDocumentId = null;
                }
            };

            if ($rootScope.hasChanges()) {
                $rootScope.openConfirmLeaveDlg().result.then(function() {
                    process();
                });
            } else {
                process();
            }
        };
        $scope.displayFilteredTree = function() {
            console.log("IN Filterd");
            //$rootScope.loadingTree =! $rootScope.loadingTree;
            $scope.ready = false;
            $scope.tocView = 'views/tocFilterMd.html';
        }

        $scope.ready = false;

        $scope.ready = function() {
            return $scope.ready;
        }

        $scope.finishLoading = function() {

            $scope.loadingTree = false;

            $scope.setReady(true);
        }

        $scope.setReady = function(b) {

            $scope.ready = b;
        }

        $scope.setFilter = function(b) {

            $scope.filtering = b;
        }
        $scope.getFilter = function() {

            return $scope.filtering;
        }


        $scope.toggleLoading = function() {
            $scope.loadingTree = true;
        }

        $scope.showLoading = function() {
            return $scope.loadingTree;
        }

        $scope.displayRegularTree = function() {
            blockUI.start();
            console.log("IN REGULAR")
                //$rootScope.loadingTree =! $rootScope.loadingTree;
            $scope.ready = false;
            $scope.tocView = 'views/toc.html';
            blockUI.stop();

        }

        $scope.edit = function(igdocument) {
            console.log("edit msgs=" + igdocument.metaData.title + " len=" + igdocument.profile.messages.children.length);
            $scope.viewSettings.setTableReadonly(false);
            $scope.tocView = 'views/toc.html';
            $scope.show(igdocument);
        };

        $scope.view = function(igdocument) {
            $scope.viewSettings.setTableReadonly(true);
            $scope.tocView = 'views/tocReadOnly.html';
            $scope.show(igdocument);
        };


        // switcher
        $scope.enabled = true;
        $scope.onOff = true;
        $scope.yesNo = true;
        $scope.disabled = true;

        $scope.changeCallback = function() {
            console.log('This is the state of my model ' + $scope.enabled);
        };


        $scope.orderSectionsByPosition = function(sections) {
            sections = $filter('orderBy')(sections, 'sectionPosition');
            angular.forEach(sections, function(section) {
                if (section.childSections && section.childSections != null && section.childSections.length > 0) {
                    section.childSections = $scope.orderSectionsByPosition(section.childSections);
                }
            });
            return sections;
        };

        $scope.orderMesagesByPositon = function(messages) {
            return $filter('orderBy')(messages, 'position');
        };

        $scope.openIGDocument = function(igdocument) {
            if (igdocument != null) {
                $timeout(function() {
                    $scope.selectIgTab(1);
                    $rootScope.TreeIgs = [];
                    $rootScope.TreeIgs.push(igdocument);
                    $rootScope.selectedMessagesIDS = [];
                    igdocument.childSections = $scope.orderSectionsByPosition(igdocument.childSections);
                    igdocument.profile.messages.children = $scope.orderMesagesByPositon(igdocument.profile.messages.children);
                    $rootScope.selectedMessages = angular.copy(igdocument.profile.messages.children);
                    $scope.loadingIGDocument = true;
                    $rootScope.isEditing = true;
                    $rootScope.igdocument = igdocument;
                    if (igdocument.profile.metaData.hl7Version != undefined || igdocument.profile.metaData.hl7Version != null) {
                        $rootScope.hl7Version = igdocument.profile.metaData.hl7Version;
                    }
                    $rootScope.initMaps();
                    $scope.loadSegments().then(function() {
                        $rootScope.filteredSegmentsList = angular.copy($rootScope.segments);
                        //$rootScope.filteredSegmentsList=[];
                        $scope.loadDatatypes().then(function() {

                            $rootScope.filteredDatatypesList = angular.copy($rootScope.datatypes);
                            $scope.loadTables().then(function() {
                                $scope.collectMessages();

                                $scope.messagesParams = $scope.getMessageParams();
                                $scope.loadIgDocumentMetaData();

                                $rootScope.filteredTablesList = angular.copy($rootScope.tables);
                            }, function() {});
                        }, function() {});
                    }, function() {});
                }, function() {});
            }

        };


        $rootScope.getMessagesFromIDS = function(selectedMessagesIDS, ig) {
            $rootScope.selectedMessages = []

        }

        $scope.loadIgDocumentMetaData = function() {
            if (!$rootScope.config || $rootScope.config === null) {
                $http.get('api/igdocuments/config').then(function(response) {
                    $rootScope.config = angular.fromJson(response.data);
                    $scope.loadingIGDocument = false;
                    $scope.toEditIGDocumentId = null;
                    $scope.selectDocumentMetaData();
                }, function(error) {
                    $scope.loadingIGDocument = false;
                    $scope.toEditIGDocumentId = null;
                });
            } else {
                $scope.loadingIGDocument = false;
                $scope.toEditIGDocumentId = null;
                $scope.selectDocumentMetaData();
            }
        };

        $scope.loadDatatypes = function() {
            var delay = $q.defer();
            $rootScope.igdocument.profile.datatypeLibrary.type = "datatypes";
            DatatypeLibrarySvc.getDatatypesByLibrary($rootScope.igdocument.profile.datatypeLibrary.id).then(function(children) {
                $rootScope.datatypes = children;
                $rootScope.datatypesMap = {};
                angular.forEach(children, function(child) {
                    this[child.id] = child;
                }, $rootScope.datatypesMap);
                delay.resolve(true);
            }, function(error) {
                $rootScope.msg().text = "DatatypesLoadFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                delay.reject(false);

            });
            return delay.promise;
        };

        $scope.loadSegments = function() {
            var delay = $q.defer();
            $rootScope.igdocument.profile.segmentLibrary.type = "segments";
            SegmentLibrarySvc.getSegmentsByLibrary($rootScope.igdocument.profile.segmentLibrary.id).then(function(children) {
                $rootScope.segments = children;
                $rootScope.segmentsMap = {};
                angular.forEach(children, function(child) {
                    this[child.id] = child;
                }, $rootScope.segmentsMap);
                delay.resolve(true);
            }, function(error) {
                $rootScope.msg().text = "SegmentsLoadFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                delay.reject(false);
            });
            return delay.promise;
        };


        $scope.loadTables = function() {
            var delay = $q.defer();
            $rootScope.igdocument.profile.tableLibrary.type = "tables";




            TableLibrarySvc.getTablesByLibrary($rootScope.igdocument.profile.tableLibrary.id).then(function(children) {
                $rootScope.tables = children;
                $rootScope.tablesMap = {};
                angular.forEach(children, function(child) {
                    this[child.id] = child;
                }, $rootScope.tablesMap);
                delay.resolve(true);
            }, function(error) {
                $rootScope.msg().text = "TablesLoadFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                delay.reject(false);
            });
            return delay.promise;

        };


        $scope.loadFilter = function() {
            $rootScope.$emit('event:loadFilter', $rootScope.igdocument);
        };

        $scope.loadMastermap = function() {
            //            $rootScope.$emit('event:loadMastermap', $rootScope.igdocument);
            //            MastermapSvc.parseIg($rootScope.igdocument);
        };


        $scope.collectTables = function() {
            $rootScope.tables = $rootScope.igdocument.profile.tableLibrary.children;
            $rootScope.tablesMap = {};
            angular.forEach($rootScope.igdocument.profile.tableLibrary.children, function(child) {
                this[child.id] = child;
                if (child.displayName) {
                    child.label = child.displayName;
                }
                angular.forEach(child.codes, function(code) {
                    if (code.displayName) {
                        code.label = code.displayName;
                    }
                });
            }, $rootScope.tablesMap);
        };

        $scope.collectMessages = function() {
            $rootScope.messagesMap = {};
            $rootScope.messages = $rootScope.igdocument.profile.messages;
            angular.forEach($rootScope.igdocument.profile.messages.children, function(child) {
                if (child != null) {
                    this[child.id] = child;
                    var cnt = 0;
                    angular.forEach(child.children, function(segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                }
            }, $rootScope.messagesMap);
        };

        $scope.collectData = function(node, segRefOrGroups, segments, datatypes) {
            if (node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function(segmentRefOrGroup) {
                        $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'group') {
                    segRefOrGroups.push(node);
                    if (node.children) {
                        angular.forEach(node.children, function(segmentRefOrGroup) {
                            $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                        });
                    }
                    segRefOrGroups.push({ name: node.name, "type": "end-group" });
                } else if (node.type === 'segment') {
                    if (segments.indexOf(node) === -1) {
                        segments.push(node);
                    }
                    angular.forEach(node.fields, function(field) {
                        $scope.collectData(field, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'segmentRef') {
                    segRefOrGroups.push(node);
                    $scope.collectData($rootScope.segmentsMap[node.ref.id], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'component' || node.type === 'subcomponent' || node.type === 'field') {
                    $scope.collectData($rootScope.datatypesMap[node.datatype.id], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'datatype') {
                    if (datatypes.indexOf(node) === -1) {
                        datatypes.push(node);
                    }
                    if (node.components) {
                        angular.forEach(node.children, function(component) {
                            $scope.collectData(component, segRefOrGroups, segments, datatypes);
                        });
                    }
                }
            }
        };

        $scope.confirmDelete = function(igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentDeleteCtrl.html',
                controller: 'ConfirmIGDocumentDeleteCtrl',
                resolve: {
                    igdocumentToDelete: function() {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function(igdocument) {
                $scope.igdocumentToDelete = igdocument;
                var idxP = _.findIndex($rootScope.igs, function(child) {
                    return child.id === igdocument.id;
                });
                $rootScope.igs.splice(idxP, 1);
                $scope.tmpIgs = [].concat($rootScope.igs);
            });
        };

        $scope.confirmClose = function() {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentCloseCtrl.html',
                controller: 'ConfirmIGDocumentCloseCtrl'
            });
            modalInstance.result.then(function() {
                $rootScope.clearChanges();
            }, function() {});
        };

        $scope.confirmOpen = function(igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
                controller: 'ConfirmIGDocumentOpenCtrl',
                resolve: {
                    igdocumentToOpen: function() {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function(igdocument) {
                $rootScope.clearChanges();
                $scope.openIGDocument(igdocument);
            }, function() {});
        };


        $scope.selectMessagesForExport = function(igdocument) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectMessagesForExportCtrl.html',
                controller: 'SelectMessagesForExportCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    igdocumentToSelect: function() {
                        return igdocument;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $scope.addSegments = function(hl7Version) {
            var scopes = ['HL7STANDARD'];

            SegmentService.getSegmentsByScopesAndVersion(scopes, $scope.hl7Version).then(function(result) {
                console.log("result");
                console.log(result);


                console.log("addSegment scopes=" + scopes.length);
                var addSegmentInstance = $modal.open({
                    templateUrl: 'AddSegmentDlg.html',
                    controller: 'AddSegmentDlgCtl',
                    windowClass: 'flavor-modal-window',
                    resolve: {
                        hl7Version: function() {
                            return $scope.hl7Version;
                        },
                        segments: function() {


                            return result;
                        }
                    }
                }).result.then(function(results) {
                    var ids = [];
                    angular.forEach(results, function(result) {
                        ids.push(result.id);
                    });


                });

            });

        };

        $scope.addPHINVADSTables = function(selectedTableLibary) {
            var modalInstance = $modal.open({
                templateUrl: 'AddPHINVADSTableOpenCtrl.html',
                controller: 'AddPHINVADSTableOpenCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    selectedTableLibary: function() {
                        return selectedTableLibary;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $scope.addHL7Table = function(selectedTableLibary) {
            var modalInstance = $modal.open({
                templateUrl: 'AddHL7TableOpenCtrl.html',
                controller: 'AddHL7TableOpenCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    selectedTableLibary: function() {
                        return selectedTableLibary;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $scope.addDatatypes = function(hl7Version) {
            var scopes = ['HL7STANDARD'];

            DatatypeService.getDataTypesByScopesAndVersion(scopes, $scope.hl7Version).then(function(result) {
                console.log("result");
                console.log(result);

                console.log("addDatatype scopes=" + scopes.length);
                var addDatatypeInstance = $modal.open({
                    templateUrl: 'AddDatatypeDlg.html',
                    controller: 'AddDatatypeDlgCtl',
                    size: 'lg',
                    windowClass: 'flavor-modal-window',
                    resolve: {
                        hl7Version: function() {
                            return $scope.hl7Version;
                        },
                        datatypes: function() {
                            console.log("datatypes");
                            console.log(result);

                            return result;
                        }
                    }
                }).result.then(function(results) {
                    var ids = [];
                    angular.forEach(results, function(result) {
                        ids.push(result.id);
                    });
                });
            });
        };

        $scope.exportAsMessages = function(id, mids) {
            blockUI.start();
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
            blockUI.stop();
        };

        $scope.exportAs = function(format) {
            if ($rootScope.igdocument != null) {
                IgDocumentService.exportAs($rootScope.igdocument, format);

                //                if (!ViewSettings.tableReadonly) {
                //                    IgDocumentService.save($rootScope.igdocument).then(function (result) {
                //                        IgDocumentService.exportAs($rootScope.igdocument, format);
                //                    }, function (error) {
                //                        $rootScope.msg().text = error.data.text;
                //                        $rootScope.msg().type = error.data.type;
                //                        $rootScope.msg().show = true;
                //                    });
                //                } else {
                //                    IgDocumentService.exportAs($rootScope.igdocument, format);
                //                }
            }
        };

        $scope.exportDelta = function(id, format) {
            blockUI.start();
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
            blockUI.stop();
        };

        $scope.close = function() {
            if ($rootScope.hasChanges()) {
                $rootScope.openConfirmLeaveDlg().result.then(function() {
                    $rootScope.closeIGDocument();
                });
            } else {
                $rootScope.closeIGDocument();
            }
        };

        $scope.gotoSection = function(obj, type) {
            $rootScope.section['data'] = obj;
            $rootScope.section['type'] = type;
        };

        //        $scope.save = function () {
        //            $rootScope.msg().text = null;
        //            $rootScope.msg().type = null;
        //            $rootScope.msg().show = false;
        //            var delay = $q.defer();
        //            waitingDialog.show('Saving changes...', {dialogSize: 'xs', progressType: 'success'});
        //            IgDocumentService.save($rootScope.igdocument).then(function (saveResponse) {
        //                var found = $scope.findOne($rootScope.igdocument.id);
        //                if (found != null) {
        //                    var index = $rootScope.igs.indexOf(found);
        //                    if (index > 0) {
        //                        $rootScope.igs [index] = $rootScope.igdocument;
        //                    }
        //                }
        //                $rootScope.msg().text = saveResponse.text;
        //                $rootScope.msg().type = saveResponse.type;
        //                $rootScope.msg().show = true;
        //                StorageService.setIgDocument($rootScope.igdocument);
        //                $rootScope.clearChanges();
        //                waitingDialog.hide();
        //                delay.resolve(true);
        //            }, function (error) {
        //                $rootScope.msg().text = error.data.text;
        //                $rootScope.msg().type = error.data.type;
        //                $rootScope.msg().show = true;
        //                waitingDialog.hide();
        //                delay.reject(false);
        //            });
        //            return delay.promise;
        //        };


        $scope.exportChanges = function() {
            blockUI.start();
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
            blockUI.stop();
        };

        $scope.viewChanges = function(changes) {
            var modalInstance = $modal.open({
                templateUrl: 'ViewIGChangesCtrl.html',
                controller: 'ViewIGChangesCtrl',
                resolve: {
                    changes: function() {
                        return changes;
                    }
                }
            });
            modalInstance.result.then(function(changes) {
                $scope.changes = changes;
            }, function() {});
        };


        $scope.reset = function() {
            $rootScope.changes = {};
            $rootScope.closeIGDocument();
        };


        $scope.initIGDocument = function() {
            $scope.loading = true;
            if ($rootScope.igdocument != null && $rootScope.igdocument != undefined)
                $scope.gotoSection($rootScope.igdocument.metaData, 'metaData');
            $scope.loading = false;

        };

        $scope.createGuide = function() {
            $scope.isVersionSelect = true;
        };

        $scope.listHL7Versions = function() {
            var hl7Versions = [];
            $http.get('api/igdocuments/hl7/findVersions', {
                timeout: 60000
            }).then(
                function(response) {
                    var len = response.data.length;
                    for (var i = 0; i < len; i++) {
                        hl7Versions.push(response.data[i]);
                    }
                });
            return hl7Versions;
        };

        $scope.showSelected = function(node) {
            $scope.selectedNode = node;
        };

        $scope.selectSegment = function(segment) {
            $rootScope.Activate(segment.id);
            $rootScope.subview = "EditSegments.html";
            if (segment && segment != null) {
                $scope.loadingSelection = true;
                blockUI.start();
                $timeout(
                    function() {
                        SegmentService.get(segment.id).then(function(result) {
                            $rootScope.segment = angular.copy(segment);
                            $rootScope.$emit("event:initSegment");

                            $rootScope.currentData = $rootScope.segment;
                            $rootScope.segment.ext = $rootScope.getSegmentExtension($rootScope.segment);
                            $rootScope.segment["type"] = "segment";
                            $rootScope.tableWidth = null;
                            $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                            $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 990);
                            $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 990);
                            $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 990);
                            $scope.loadingSelection = false;
                            try {
                                if ($scope.segmentsParams)
                                    $scope.segmentsParams.refresh();
                            } catch (e) {

                            }

                            $rootScope.references = [];
                            angular.forEach($rootScope.igdocument.profile.messages.children, function(message) {
                                $rootScope.findSegmentRefs($rootScope.segment, message, message.name);
                            });
                            $rootScope.tmpReferences = [].concat($rootScope.references);

                            $scope.loadingSelection = false;
                            $rootScope.$emit("event:initEditArea");
                            blockUI.stop();
                        }, function(error) {
                            $scope.loadingSelection = false;
                            $rootScope.msg().text = error.data.text;
                            $rootScope.msg().type = error.data.type;
                            $rootScope.msg().show = true;
                            blockUI.stop();
                        });
                    }, 100);
            }
        };

        $scope.selectDocumentMetaData = function() {
            $rootScope.subview = "EditDocumentMetadata.html";
            $scope.loadingSelection = true;
            blockUI.start();
            $rootScope.metaData = angular.copy($rootScope.igdocument.metaData);
            $rootScope.currentData = $rootScope.igdocument;
            $timeout(
                function() {
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, 100);
        };

        $scope.selectProfileMetaData = function() {
            $rootScope.subview = "EditProfileMetadata.html";
            $rootScope.metaData = angular.copy($rootScope.igdocument.profile.metaData);
            $rootScope.currentData = $rootScope.igdocument.profile;
            $scope.loadingSelection = true;
            blockUI.start();
            $timeout(
                function() {
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, 100);
        };

        $scope.selectDatatype = function(datatype) {
            $rootScope.Activate(datatype.id);
            $rootScope.subview = "EditDatatypes.html";
            if (datatype && datatype != null) {
                $scope.loadingSelection = true;
                blockUI.start();
                $timeout(
                    function() {
                        DatatypeService.getOne(datatype.id).then(function(result) {
                            $rootScope.datatype = angular.copy(result);
                            $rootScope.$emit("event:initDatatype");

                            $rootScope.currentData = datatype;

                            $rootScope.datatype.ext = $rootScope.getDatatypeExtension($rootScope.datatype);
                            $scope.loadingSelection = false;
                            $rootScope.datatype["type"] = "datatype";
                            $rootScope.tableWidth = null;
                            $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                            $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 890);
                            $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 890);
                            $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 890);
                            $scope.loadingSelection = false;
                            try {
                                if ($scope.datatypesParams)
                                    $scope.datatypesParams.refresh();
                            } catch (e) {

                            }
                            $rootScope.references = [];
                            $rootScope.tmpReferences = [].concat($rootScope.references);
                            angular.forEach($rootScope.segments, function(segment) {
                                if (segment && segment != null) {
                                    $rootScope.findDatatypeRefs($rootScope.datatype, segment, $rootScope.getSegmentLabel(segment));
                                }
                            });
                            angular.forEach($rootScope.datatypes, function(dt) {
                                if (dt && dt != null && dt.id !== $rootScope.datatype.id) $rootScope.findDatatypeRefs(datatype, dt, $rootScope.getDatatypeLabel(dt));
                            });

                            $rootScope.tmpReferences = [].concat($rootScope.references);

                            $rootScope.$emit("event:initEditArea");
                            blockUI.stop();
                        }, function(error) {
                            $scope.loadingSelection = false;
                            $rootScope.msg().text = error.data.text;
                            $rootScope.msg().type = error.data.type;
                            $rootScope.msg().show = true;
                            blockUI.stop();
                        });
                    }, 100);
            }
        };

        $scope.selectMessage = function(message) {
            $rootScope.Activate(message.id);
            $rootScope.subview = "EditMessages.html";
            $scope.loadingSelection = true;
            blockUI.start();
            $timeout(
                function() {
                    $rootScope.originalMessage = message;
                    $rootScope.message = angular.copy(message);
                    $rootScope.currentData = $rootScope.message;
                    $rootScope.processMessageTree($rootScope.message);
                    $rootScope.tableWidth = null;
                    $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                    $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 630);
                    $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 630);
                    $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 630);
                    $scope.loadingSelection = false;
                    try {
                        if ($scope.messagesParams)
                            $scope.messagesParams.refresh();
                    } catch (e) {

                    }
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, 100);
        };

        $scope.selectTable = function(t) {
            $rootScope.Activate(t.id);
            var table = angular.copy(t);

            if ($scope.viewSettings.tableReadonly || table.scope !== 'USER') {
                $rootScope.subview = "ReadValueSets.html";
            } else {
                $rootScope.subview = "EditValueSets.html";
            }

            $scope.loadingSelection = true;
            blockUI.start();
            $timeout(
                function() {
                    $rootScope.table = table;
                    $rootScope.$emit("event:initTable");
                    $rootScope.currentData = $rootScope.table;
                    $rootScope.codeSystems = [];
                    for (var i = 0; i < $rootScope.table.codes.length; i++) {
                        if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
                            if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                                $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
                            }
                        }
                    }
                    $rootScope.references = [];
                    angular.forEach($rootScope.segments, function(segment) {
                        $rootScope.findTableRefs($rootScope.table, segment, $rootScope.getSegmentLabel(segment));
                    });
                    angular.forEach($rootScope.datatypes, function(dt) {
                        $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt));
                    });
                    $rootScope.tmpReferences = [].concat($rootScope.references);
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, 100);
        };

        $scope.selectSection = function(section) {
            if (section.sectionContents === null || section.sectionContents === undefined) {
                section.sectionContents = "";
                console.log(section);
            }
            $rootScope.subview = "EditSections.html";
            $scope.loadingSelection = true;
            blockUI.start();

            $timeout(
                function() {
                    $rootScope.section = angular.copy(section);
                    $rootScope.currentData = $rootScope.section;
                    $rootScope.originalSection = section;
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, 100);
        };

        $scope.getFullName = function() {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getFullName();
            }
            return '';
        };


    });

angular.module('igl').controller('ViewIGChangesCtrl', function($scope, $modalInstance, changes, $rootScope, $http) {
    $scope.changes = changes;
    $scope.loading = false;
    $scope.exportChanges = function() {
        $scope.loading = true;
        var form = document.createElement("form");
        form.action = 'api/igdocuments/export/changes';
        form.method = "POST";
        form.target = "_target";
        form.style.display = 'none';
        form.params = document.body.appendChild(form);
        form.submit();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmIGDocumentDeleteCtrl', function($scope, $modalInstance, igdocumentToDelete, $rootScope, $http) {
    $scope.igdocumentToDelete = igdocumentToDelete;
    $scope.loading = false;
    $scope.delete = function() {
        $scope.loading = true;
        $http.post($rootScope.api('api/igdocuments/' + $scope.igdocumentToDelete.id + '/delete')).then(function(response) {
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

        }, function(error) {
            $scope.error = error;
            $scope.loading = false;
            $rootScope.msg().text = "igDeleteFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            //            $modalInstance.dismiss('cancel');


            // waitingDialog.hide();
        });
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmIGDocumentCloseCtrl', function($scope, $modalInstance, $rootScope, $http) {
    $scope.loading = false;
    $scope.discardChangesAndClose = function() {
        $scope.loading = true;
        $http.get('api/igdocuments/' + $rootScope.igdocument.id, { timeout: 60000 }).then(function(response) {
            var index = $rootScope.igs.indexOf($rootScope.igdocument);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $scope.clear();
        }, function(error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $modalInstance.dismiss('cancel');
        });
    };

    $scope.clear = function() {
        $rootScope.closeIGDocument();
        $modalInstance.close();
    };

    $scope.ConfirmIGDocumentOpenCtrl = function() {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = { "changes": changes, "igDocument": $rootScope.igdocument };
        $http.post('api/igdocuments/save', data, { timeout: 60000 }).then(function(response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.igdocument.metaData.date = saveResponse.date;
            $rootScope.igdocument.metaData.version = saveResponse.version;
            $scope.loading = false;
            $scope.clear();
        }, function(error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('CreateNewIGAlertCtrl', function($scope, $rootScope, $http, $modalInstance) {
    $scope.close = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ConfirmIGDocumentOpenCtrl', function($scope, $modalInstance, igdocumentToOpen, $rootScope, $http) {
    $scope.igdocumentToOpen = igdocumentToOpen;
    $scope.loading = false;

    $scope.discardChangesAndOpen = function() {
        $scope.loading = true;
        $http.get('api/igdocuments/' + $rootScope.igdocument.id, { timeout: 60000 }).then(function(response) {
            var index = $rootScope.igs.indexOf($rootScope.igdocument);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $modalInstance.close($scope.igdocumentToOpen);
        }, function(error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $modalInstance.dismiss('cancel');
        });
    };

    $scope.saveChangesAndOpen = function() {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = { "changes": changes, "igDocument": $rootScope.igdocument };
        $http.post('api/igdocuments/save', data, { timeout: 60000 }).then(function(response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.igdocument.metaData.date = saveResponse.date;
            $rootScope.igdocument.metaData.version = saveResponse.version;
            $scope.loading = false;
            $modalInstance.close($scope.igdocumentToOpen);
        }, function(error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('DocumentMetaDataCtrl', function($scope, $rootScope, $http, IgDocumentService, blockUI) {
    $scope.saving = false;
    $scope.saved = false;

    $scope.save = function() {
        $scope.saving = true;
        $scope.saved = false;
        if ($rootScope.igdocument != null && $rootScope.metaData != null) {
            IgDocumentService.saveMetadata($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.igdocument.metaData = angular.copy($rootScope.metaData);
                if ($scope.editForm) {
                    $scope.editForm.$setPristine();
                    $scope.editForm.$dirty = false;
                }
                $rootScope.clearChanges();
                $rootScope.msg().text = "documentMetaDataSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;

            }, function(error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
                $scope.saved = false;

            });
        }
    };
    $scope.reset = function() {
        blockUI.start();
        $scope.editForm.$dirty = false;
        $scope.editForm.$setPristine();
        $rootScope.clearChanges();
        $rootScope.metaData = angular.copy($rootScope.igdocument.metaData);
        blockUI.stop();
    };
});

angular.module('igl').controller('ProfileMetaDataCtrl', function($scope, $rootScope, $http, ProfileSvc, blockUI) {
    $scope.saving = false;
    $scope.saved = false;
    $scope.save = function() {
        $scope.saving = true;
        $scope.saved = false;
        if ($rootScope.igdocument != null && $rootScope.metaData != null) {
            ProfileSvc.saveMetaData($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.igdocument.profile.metaData = angular.copy($rootScope.metaData);
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
                $rootScope.clearChanges();
                $rootScope.msg().text = "messageInfrasctructureSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;

            }, function(error) {
                $scope.saving = false;
                $scope.saved = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        }
    };
    $scope.reset = function() {
        blockUI.start();
        $scope.editForm.$dirty = false;
        $scope.editForm.$setPristine();
        $rootScope.clearChanges();
        $rootScope.metaData = angular.copy($rootScope.igdocument.profile.metaData);
        blockUI.stop();

    };
});


angular.module('igl').controller('SelectMessagesForExportCtrl', function($scope, $modalInstance, igdocumentToSelect, $rootScope, $http, $cookies, ExportSvc) {
    $scope.igdocumentToSelect = igdocumentToSelect;
    $scope.xmlFormat = 'Validation';
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


    $scope.exportAsZIPforSelectedMessages = function() {
        $scope.loading = true;
        ExportSvc.exportAsXMLByMessageIds($scope.igdocumentToSelect.id, $scope.selectedMessagesIDs, $scope.xmlFormat);
        $scope.loading = false;
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('AddHL7TableOpenCtrl', function($scope, $modalInstance, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
    $scope.loading = false;
    $scope.selectedTableLibary = selectedTableLibary;
    $scope.selectedHL7Version = '';
    $scope.searchText = '';
    $scope.hl7Versions = [];
    $scope.hl7Tables = null;
    $scope.selectedTables = [];

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.listHL7Versions = function() {
        return $http.get('api/igdocuments/findVersions', {
            timeout: 60000
        }).then(function(response) {
            var hl7Versions = [];
            var length = response.data.length;
            for (var i = 0; i < length; i++) {
                hl7Versions.push(response.data[i]);
            }
            $scope.hl7Versions = hl7Versions;
        });
    };

    $scope.loadTablesByVersion = function(hl7Version) {
        $scope.loading = true;
        $scope.selectedHL7Version = hl7Version;
        return $http.get('api/igdocuments/' + hl7Version + "/tables", {
            timeout: 60000
        }).then(function(response) {
            $scope.hl7Tables = response.data;
            $scope.loading = false;
        });
    };

    $scope.addTable = function(table) {
        var newTable = angular.copy(table);
        newTable.id = new ObjectId().toString();
        newTable.participants = [];
        newTable.bindingIdentifier = $rootScope.createNewFlavorName(table.bindingIdentifier);
        newTable.scope = 'USER';

        if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
            for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
                newTable.codes[i].id = new ObjectId().toString();
            }
        }
        $scope.selectedTables.push(newTable);
    };

    $scope.deleteTable = function(table) {
        var index = $scope.selectedTables.indexOf(table);
        if (index > -1) $scope.selectedTables.splice(index, 1);
    };


    $scope.save = function() {
        var childrenLinks = [];
        for (var i = 0; i < $scope.selectedTables.length; i++) {
            $scope.selectedTables[i].libIds.push($scope.selectedTableLibary.id);
            var newLink = angular.fromJson({
                id: $scope.selectedTables[i].id,
                bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
            });
            $scope.selectedTableLibary.children.push(newLink);
            childrenLinks.push(newLink);
            var addedTable = $scope.selectedTables[i];
            $rootScope.tables.splice(0, 0, addedTable);
            $rootScope.tablesMap[addedTable.id] = addedTable;
            TableService.save(addedTable).then(function(result) {
            }, function(error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        }
        TableLibrarySvc.addChildren($scope.selectedTableLibary.id, childrenLinks).then(function(link) {

            if ($scope.editForm) {
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
            }
            $rootScope.clearChanges();
            $rootScope.msg().text = "tableSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;

        }, function(error) {
            $scope.saving = false;
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
        });


        $modalInstance.dismiss('cancel');
    };

    function positionElements(chidren) {
        var sorted = _.sortBy(chidren, "sectionPosition");
        var start = sorted[0].sectionPosition;
        _.each(sorted, function(sortee) {
            sortee.sectionPosition = start++;
        });
        return sorted;
    };

    $scope.listHL7Versions();
});

angular.module('igl').controller('AddPHINVADSTableOpenCtrl', function($scope, $modalInstance, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
    $scope.loading = false;
    $scope.selectedTableLibary = selectedTableLibary;
    $scope.searchText = '';
    $scope.hl7Tables = null;
    $scope.phinvadsTables = null;
    $scope.selectedTables = [];

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.searchPhinvads = function(searchText) {
        $scope.loading = true;
        $scope.searchText = searchText;
        return $http.get('api/igdocuments/' + searchText + "/PHINVADS/tables", {
            timeout: 600000
        }).then(function(response) {
            $scope.phinvadsTables = response.data;
            $scope.loading = false;
        });
    }

    $scope.addTable = function(table) {
        var newTable = angular.copy(table);
        newTable.id = new ObjectId().toString();
        newTable.participants = [];
        newTable.bindingIdentifier = $rootScope.createNewFlavorName(table.bindingIdentifier);
        newTable.scope = 'USER';

        if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
            for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
                newTable.codes[i].id = new ObjectId().toString();
            }
        }
        $scope.selectedTables.push(newTable);
    };

    $scope.deleteTable = function(table) {
        var index = $scope.selectedTables.indexOf(table);
        if (index > -1) $scope.selectedTables.splice(index, 1);
    };


    $scope.save = function() {
        var childrenLinks = [];
        for (var i = 0; i < $scope.selectedTables.length; i++) {
            $scope.selectedTables[i].libIds.push($scope.selectedTableLibary.id);
            var newLink = angular.fromJson({
                id: $scope.selectedTables[i].id,
                bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
            });
            $rootScope.igdocument.profile.tableLibrary.children.push(newLink);
            childrenLinks.push(newLink);
            var addedTable = $scope.selectedTables[i];
            $rootScope.tables.splice(0, 0, addedTable);
            $rootScope.tablesMap[addedTable.id] = addedTable;
            TableService.save(addedTable).then(function(result) {
            }, function(error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        }


        TableLibrarySvc.addChildren($scope.selectedTableLibary.id, childrenLinks).then(function(link) {

            if ($scope.editForm) {
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
            }
            $rootScope.clearChanges();
            $rootScope.msg().text = "tableSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;

        }, function(error) {
            $scope.saving = false;
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
        });


        $modalInstance.dismiss('cancel');
    };

    function positionElements(chidren) {
        var sorted = _.sortBy(chidren, "sectionPosition");
        var start = sorted[0].sectionPosition;
        _.each(sorted, function(sortee) {
            sortee.sectionPosition = start++;
        });
        return sorted;
    }
});


angular.module('igl').controller('AddDatatypeDlgCtl',
    function($scope, $rootScope, $modalInstance, hl7Version, datatypes, DatatypeLibrarySvc, DatatypeService) {

        //$scope.hl7Version = hl7Version;
        //$scope.hl7Datatypes = datatypes;


        $scope.hl7Datatypes = datatypes.filter(function(current) {
            return $rootScope.datatypes.filter(function(current_b) {
                return current_b.id == current.id;
            }).length == 0
        });


        $scope.isInDts = function(datatype) {

            if ($scope.hl7Datatypes.indexOf(datatype) === -1) {
                return false;
            } else {
                return true;
            }

        }


        $scope.selectDT = function(datatype) {
            console.log(datatype);
            $scope.newDatatype = datatype;
        };
        $scope.selected = function() {
            return ($scope.newDatatype !== undefined);
        };
        $scope.unselect = function() {
            $scope.newDatatype = undefined;
        };
        $scope.isActive = function(id) {
            if ($scope.newDatatype) {
                return $scope.newDatatype.id === id;
            } else {
                return false;
            }
        };


        $scope.ok = function() {
            var newLink = angular.fromJson({
                id: $scope.newDatatype.id,
                name: $scope.newDatatype.name
            });

            DatatypeLibrarySvc.addChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function(link) {
                $rootScope.igdocument.profile.datatypeLibrary.children.splice(0, 0, newLink);
                $rootScope.datatypes.splice(0, 0, $scope.newDatatype);
                $rootScope.datatype = $scope.newDatatype;
                $rootScope.datatypesMap[$scope.newDatatype.id] = $scope.newDatatype;
                $rootScope.processElement($scope.newDatatype);
                $rootScope.filteredDatatypesList.push($scope.newDatatype);
                $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                $rootScope.$broadcast('event:openDatatype', $scope.newDatatype);
                $rootScope.msg().text = "datatypeAdded";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $modalInstance.close(datatypes);
            }, function(error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });


angular.module('igl').controller('AddSegmentDlgCtl',
    function($scope, $rootScope, $modalInstance, hl7Version, segments, SegmentService, SegmentLibrarySvc, IgDocumentService) {


        $scope.hl7Segments = segments.filter(function(current) {
            return $rootScope.segments.filter(function(current_b) {
                return current_b.id == current.id;
            }).length == 0
        });
        $scope.isInSegs = function(segment) {
            console.log($scope.hl7Segments.indexOf(segment) === -1);

            if ($scope.hl7Segments.indexOf(segment) === -1) {
                return false;
            } else {
                return true;
            }

        }


        //$scope.hl7Segments = _.difference(segments, $rootScope.segments);


        $scope.selectSeg = function(segment) {
            $scope.newSegment = segment;
        };
        $scope.selected = function() {
            return ($scope.newSegment !== undefined);
        };
        $scope.unselect = function() {
            $scope.newSegment = undefined;
        };
        $scope.isActive = function(id) {
            if ($scope.newSegment) {
                return $scope.newSegment.id === id;
            } else {
                return false;
            }
        };

        $scope.ok = function() {

            //var newSegment = JSON.parse(segment);

            var newLink = angular.fromJson({
                id: $scope.newSegment.id,
                name: $scope.newSegment.name
            });

            //            $scope.newSegment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);

            // SegmentService.save($scope.newSegment).then(function(result) {
            SegmentLibrarySvc.addChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function(link) {
                $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLink);
                $rootScope.segments.splice(0, 0, $scope.newSegment);
                $rootScope.segment = $scope.newSegment;
                $rootScope.segmentsMap[$scope.newSegment.id] = $scope.newSegment;
                //TODO MasterMap need to add Segment
                $rootScope.processElement($scope.newSegment);
                //                  MastermapSvc.addSegmentObject(newSegment, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
                $rootScope.filteredSegmentsList.push($scope.newSegment);
                $rootScope.filteredSegmentsList = _.uniq($rootScope.filteredSegmentsList);
                $rootScope.$broadcast('event:openSegment', $scope.newSegment);
                $rootScope.msg().text = "segmentAdded";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $modalInstance.close(segments);
            }, function(error) {
                $scope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
            //            }, function(error) {
            //                $scope.saving = false;
            //                $rootScope.msg().text = error.data.text;
            //                $rootScope.msg().type = error.data.type;
            //                $rootScope.msg().show = true;
            //            });


        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });