/**
 * Created by haffo on 1/12/15.
 */

angular.module('igl')
    .controller('IGDocumentListCtrl', function(TableService, $scope, $rootScope, $templateCache, Restangular, $http, $filter, $modal, $cookies, $timeout, userInfoService, ToCSvc, ContextMenuSvc, ProfileAccessSvc, ngTreetableParams, $interval, ViewSettings, StorageService, $q, Notification, DatatypeService, SegmentService, IgDocumentService, ElementUtils, AutoSaveService, DatatypeLibrarySvc, SegmentLibrarySvc, TableLibrarySvc, MastermapSvc, MessageService, FilteringSvc, blockUI, PcService) {

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
        }, {
            name: "Shared Implementation Guides",
            type: 'SHARED'
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
                $scope.selectIGDocumentType('USER');
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
            $rootScope.tocView = null;
            $rootScope.subview = null;

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


            $rootScope.$on('event:updateIgDate', function(event, dateUpdated) {
                if(!dateUpdated || dateUpdated === null) {
                    IgDocumentService.updateDate($rootScope.igdocument);
                }else{
                    $rootScope.igdocument.dateUpdated = dateUpdated;
                }
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
        $scope.getTemplateRow = function(row) {
            $rootScope.row = row;
            return 'templateRow.html';

        }
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
            console.log(igdocument);
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
                                // Find share participants
                                if ($rootScope.igdocument.shareParticipantIds && $rootScope.igdocument.shareParticipantIds.length > 0) {
                                    $rootScope.igdocument.shareParticipants = [];
                                    $rootScope.igdocument.shareParticipantIds.forEach(function(participant) {
                                        $http.get('api/shareparticipant', { params: { id: participant.accountId } })
                                            .then(
                                                function(response) {
                                                    response.data.pendingApproval = participant.pendingApproval;
                                                    response.data.permission = participant.permission;
                                                    $rootScope.igdocument.shareParticipants.push(response.data);
                                                },
                                                function(error) {
                                                    console.log(error);
                                                }
                                            );
                                    });
                                }
                                $scope.loadPc().then(function() {}, function() {});
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


        $scope.loadPc = function() {
            var delay = $q.defer();
            PcService.findAll().then(function(children) {

                $rootScope.pcs = children;

                $rootScope.pcs.push({ name: "TEST1", type: "message" });
                delay.resolve(true);
            }, function(error) {
                $rootScope.msg().text = "ProfileComplonentLoadFail";
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

            var addSegmentInstance = $modal.open({
                templateUrl: 'AddSegmentDlg.html',
                controller: 'AddSegmentDlgCtl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    hl7Version: function() {
                        return $scope.hl7Version;
                    }

                }
            }).result.then(function(results) {
                var ids = [];
                angular.forEach(results, function(result) {
                    ids.push(result.id);
                });

            });

        };

        $rootScope.addCSVTables = function(selectedTableLibary) {
            var modalInstance = $modal.open({
                templateUrl: 'AddCSVTableOpenCtrl.html',
                controller: 'AddCSVTableOpenCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    selectedTableLibary: function() {
                        return selectedTableLibary;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $rootScope.addPHINVADSTables = function(selectedTableLibary) {
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

        $rootScope.addHL7Table = function(selectedTableLibary, hl7Version) {
            var modalInstance = $modal.open({
                templateUrl: 'AddHL7TableOpenCtrl.html',
                controller: 'AddHL7TableOpenCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    selectedTableLibary: function() {
                        return selectedTableLibary;
                    },
                    hl7Version: function() {
                        return hl7Version;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $scope.addDatatypes = function(hl7Version) {
            var scopes = ['HL7STANDARD'];

            DatatypeService.getDataTypesByScopesAndVersion(scopes, $scope.hl7Version).then(function(datatypes) {
                DatatypeLibrarySvc.getDataTypeLibraryByScope('MASTER').then(function(masterLib) {
                    DatatypeLibrarySvc.getDataTypeLibraryByScope('USER').then(function(userDtLib) {


                        console.log("userDtLib");
                        console.log(userDtLib);

                        console.log("addDatatype scopes=" + scopes.length);
                        var addDatatypeInstance = $modal.open({
                            templateUrl: 'AddDatatypeDlg.html',
                            controller: 'AddDatatypeDlgCtl',
                            size: 'lg',
                            windowClass: 'conformance-profiles-modal',
                            resolve: {
                                hl7Version: function() {
                                    return $scope.hl7Version;
                                },
                                datatypes: function() {

                                    return datatypes;
                                },
                                masterLib: function() {

                                    return masterLib;
                                },
                                userDtLib: function() {
                                    return userDtLib;
                                }

                            }
                        }).result.then(function(results) {
                            var ids = [];
                            angular.forEach(results, function(result) {
                                ids.push(result.id);
                            });
                        });
                    });
                });
            });
        };

        $scope.addMasterDatatype = function() {
            console.log("=========version=======");
            var scopes = ['MASTER'];

            DatatypeService.getPublishedMaster($rootScope.igdocument.profile.metaData.hl7Version).then(function(result) {
                var addDatatypeInstance = $modal.open({
                    templateUrl: 'AddDatatypeDlg.html',
                    controller: 'AddDatatypeDlgCtl',
                    size: 'lg',
                    windowClass: 'flavor-modal-window',
                    resolve: {
                        hl7Version: function() {
                            return $rootScope.igdocument.profile.metaData.hl7Version;
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
                        try {
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
                                    $rootScope.findSegmentRefs($rootScope.segment, message, '', '', message);
                                });
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
                        } catch (e) {
                            $scope.loadingSelection = false;
                            $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                            $rootScope.msg().type = "danger";
                            $rootScope.msg().show = true;
                            blockUI.stop();
                        }
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
            console.log(metaData);
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
                        try {
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
                                        $rootScope.findDatatypeRefs($rootScope.datatype, segment, $rootScope.getSegmentLabel(segment), segment);
                                    }
                                });
                                angular.forEach($rootScope.datatypes, function(dt) {
                                    if (dt && dt != null && dt.id !== $rootScope.datatype.id) $rootScope.findDatatypeRefs(datatype, dt, $rootScope.getDatatypeLabel(dt), dt);
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
                        } catch (e) {
                            $scope.loadingSelection = false;
                            $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                            $rootScope.msg().type = "danger";
                            $rootScope.msg().show = true;
                            blockUI.stop();
                        }
                    }, 100);

                setTimeout(function() {
                    $scope.$broadcast('reCalcViewDimensions');
                    console.log("refreshed Slider!!");
                }, 1000);
            }
        };

        $scope.selectMessage = function(message) {
            $rootScope.Activate(message.id);
            $rootScope.subview = "EditMessages.html";
            $scope.loadingSelection = true;
            blockUI.start();
            $timeout(
                function() {
                    try {
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
                    } catch (e) {
                        $scope.loadingSelection = false;
                        $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                        blockUI.stop();
                    }
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
            try {
                TableService.getOne(table.id).then(function(tbl) {
                    $rootScope.table = tbl;
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
                        $rootScope.findTableRefs($rootScope.table, segment, $rootScope.getSegmentLabel(segment), segment);
                    });
                    angular.forEach($rootScope.datatypes, function(dt) {
                        $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt), dt);
                    });
                    $scope.loadingSelection = false;
                    $rootScope.$emit("event:initEditArea");
                    blockUI.stop();
                }, function(errr) {
                    $scope.loadingSelection = false;
                    $rootScope.msg().text = errr.data.text;
                    $rootScope.msg().type = errr.data.type;
                    $rootScope.msg().show = true;
                    blockUI.stop();
                });
            } catch (e) {
                $scope.loadingSelection = false;
                $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                blockUI.stop();
            }

            //            $timeout(
            //                function() {
            //                    $rootScope.table = table;
            //                    $rootScope.$emit("event:initTable");
            //                    $rootScope.currentData = $rootScope.table;
            //                    $rootScope.codeSystems = [];
            //                    for (var i = 0; i < $rootScope.table.codes.length; i++) {
            //                        if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
            //                            if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
            //                                $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
            //                            }
            //                        }
            //                    }
            //                    $rootScope.references = [];
            //                    angular.forEach($rootScope.segments, function(segment) {
            //                        $rootScope.findTableRefs($rootScope.table, segment, $rootScope.getSegmentLabel(segment));
            //                    });
            //                    angular.forEach($rootScope.datatypes, function(dt) {
            //                        $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt));
            //                    });
            //                    $rootScope.tmpReferences = [].concat($rootScope.references);
            //                    $scope.loadingSelection = false;
            //                    $rootScope.$emit("event:initEditArea");
            //                    blockUI.stop();
            //                }, 100);


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
                    try {
                        $rootScope.section = angular.copy(section);
                        $rootScope.currentData = $rootScope.section;
                        $rootScope.originalSection = section;
                        $scope.loadingSelection = false;
                        $rootScope.$emit("event:initEditArea");
                        blockUI.stop();
                    } catch (e) {
                        $scope.loadingSelection = false;
                        $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                        blockUI.stop();
                    }
                }, 100);
        };

        $scope.getFullName = function() {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getFullName();
            }
            return '';
        };


		$scope.shareModal = function (igdocument) {
			$http.get('api/usernames').then(function (response) {
				var userList = response.data;
				var filteredUserList = userList.filter(function(user) {
					var isPresent = false;
					if(igdocument.shareParticipants) {
						for(var i = 0; i < igdocument.shareParticipants.length; i++) {
							if(igdocument.shareParticipants[i].id == user.id) {
								isPresent = true;
							}
						}
					}
					if(!isPresent) return user;
				});
				var modalInstance = $modal.open({
					templateUrl: 'ShareIGDocumentModal.html'
					, controller: 'ShareIGDocumentCtrl',
                        size:'lg'
					, resolve: {
						igdocumentSelected: function () {
							return igdocument;
						}
						, userList: function () {
                            return _.filter(filteredUserList, function(user){
                                    return user.id != igdocument.accountId && igdocument.shareParticipantIds && igdocument.shareParticipantIds != null && igdocument.shareParticipantIds.indexOf(user.id) == -1 ;
                                });

 						}
					}
				});
			}, function (error) {
				console.log(error);
			});
		};


        $scope.unshareModal = function (igdocument, shareParticipant) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmIGDocumentUnshareCtrl.html'
                , controller: 'UnShareIGDocumentCtrl'
                , resolve: {
                    igdocumentSelected: function () {
                        return igdocument;
                    },
                    shareParticipant: function(){
                        return shareParticipant;
                    }
                }
            });
        };

        $scope.confirmShareDocument = function(igdocument) {
            $http.get('api/shareconfimation/' + igdocument.id).then(function(response) {
                $rootScope.msg().text = "igSharedConfirmationSuccessful";
                $rootScope.msg().type ="success";
                $rootScope.msg().show = true;
                $scope.loadIGDocuments();
            }, function(error) {
                $rootScope.msg().text = "igSharedConfirmationFailed";
                $rootScope.msg().type ="danger";
                $rootScope.msg().show = true;
                console.log(error);
            });
        };

        $scope.rejectShareDocument = function(igdocument) {
            $http.get('api/sharereject/' + igdocument.id).then(function(response) {
                $rootScope.msg().text = "igSharedRejectedSuccessfully";
                $rootScope.msg().type ="success";
                $rootScope.msg().show = true;
                $scope.loadIGDocuments();
            }, function(error) {
                $rootScope.msg().text = "igSharedRejectFailed";
                $rootScope.msg().type ="danger";
                $rootScope.msg().show = true;
                console.log(error);
            });
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
            IgDocumentService.saveMetadata($rootScope.igdocument.id, $rootScope.metaData).then(function(dateUpdated) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.igdocument.metaData = angular.copy($rootScope.metaData);
                $rootScope.igdocument.dateUpdated = dateUpdated;
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
            ProfileSvc.saveMetaData($rootScope.igdocument.id, $rootScope.metaData).then(function(dateUpdated) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.igdocument.profile.metaData = angular.copy($rootScope.metaData);
                $rootScope.igdocument.dateUpdated = dateUpdated;
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

angular.module('igl').controller('AddHL7TableOpenCtrl', function($scope, $modalInstance, selectedTableLibary, hl7Version, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
    $scope.loading = false;
    $scope.selectedTableLibary = selectedTableLibary;
    $scope.selectedHL7Version = hl7Version;
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
            $scope.hl7Tables = [];
            angular.forEach(response.data, function(table) {
                if (!$scope.isAlreadyIn(table)) {
                    $scope.hl7Tables.push(table);
                }
            });
            $scope.loading = false;
        });
    };



    $scope.isAlreadyIn = function(table) {
        if ($rootScope.tablesMap[table.id] == null) return false;
        return true;
    };

    $scope.addTable = function(table) {
        $scope.selectedTables.push(table);
    };

    $scope.deleteTable = function(table) {
        var index = $scope.selectedTables.indexOf(table);
        if (index > -1) $scope.selectedTables.splice(index, 1);
    };


    $scope.save = function() {
        var childrenLinks = [];
        for (var i = 0; i < $scope.selectedTables.length; i++) {
            var newLink = angular.fromJson({
                id: $scope.selectedTables[i].id,
                bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
            });
            $scope.selectedTableLibary.children.push(newLink);
            childrenLinks.push(newLink);
            var addedTable = $scope.selectedTables[i];
            $rootScope.tables.splice(0, 0, addedTable);
            $rootScope.tablesMap[addedTable.id] = addedTable;
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

    $scope.loadTablesByVersion($scope.selectedHL7Version);
});

angular.module('igl').controller('AddCSVTableOpenCtrl', function($scope, $modalInstance, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService, IgDocumentService) {
    $scope.loading = false;
    $scope.selectedTableLibary = selectedTableLibary;
    $scope.importedTable = null;
    $scope.selectedFileName = null;
    $scope.data = null;
    $scope.isInValild = false;
    $scope.erorrMessages = [];
    $scope.validateForSelectedFile = function(files) {
        $scope.isInValild = false;
        var f = document.getElementById('csvValueSetFile').files[0];
        var reader = new FileReader();
        reader.onloadend = function(e) {
            $scope.data = Papa.parse(e.target.result);

            if ($scope.data.errors.length > 0) {
                $scope.isInValild = true;
                angular.forEach($scope.data.errors, function(e) {
                    $scope.erorrMessages.push(e.message);
                });
            }

            var index = 0;
            $scope.importedTable = {};
            $scope.importedTable.scope = 'USER';
            $scope.importedTable.codes = [];
            $scope.importedTable.libIds = [];
            angular.forEach($scope.data.data, function(row) {
                index = index + 1;

                if (index > 1 && index < 11) {
                    if (row[1] != '') {
                        switch (row[0]) {
                            case 'Mapping Identifier':
                                $scope.importedTable.bindingIdentifier = row[1];
                                break;
                            case 'Name':
                                $scope.importedTable.name = row[1];
                                break;
                            case 'Description':
                                $scope.importedTable.description = row[1];
                                break;
                            case 'OID':
                                $scope.importedTable.oid = row[1];
                                break;
                            case 'Version':
                                $scope.importedTable.version = row[1];
                                break;
                            case 'Extensibility':
                                $scope.importedTable.extensibility = row[1];
                                break;
                            case 'Stability':
                                $scope.importedTable.stability = row[1];
                                break;
                            case 'Content Definition':
                                $scope.importedTable.contentDefinition = row[1];
                                break;
                            case 'Comment':
                                $scope.importedTable.comment = row[1];
                        }
                    }
                } else if (index > 13) {

                    var code = {};
                    code.value = row[0];
                    code.label = row[1];
                    code.codeSystem = row[2];
                    code.codeUsage = row[3];
                    code.comments = row[4];

                    if (code.value != null && code.value != "") $scope.importedTable.codes.push(code);
                }
            });

            if ($scope.importedTable.bindingIdentifier == null || $scope.importedTable.bindingIdentifier == '') {
                $scope.isInValild = true;
                $scope.erorrMessages.push('No Binding Identifier');
            }

            if ($scope.importedTable.name == null || $scope.importedTable.name == '') {
                $scope.isInValild = true;
                $scope.erorrMessages.push('No Name');
            }

            var errorElm = $("#errorMessageForCSV");
            var csvSaveButton = $("#csvSaveButton");
            errorElm.empty();

            if ($scope.isInValild) {
                errorElm.append('<span>' + files[0].name + ' is invalid!</span>');
                angular.forEach($scope.erorrMessages, function(e) {
                    errorElm.append("<li>" + e + "</li>");
                    csvSaveButton.prop('disabled', true);
                });
            } else {
                errorElm.append('<span>' + files[0].name + ' is valid!</span>');
                csvSaveButton.prop('disabled', false);
            }

        };

        reader.readAsBinaryString(f);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };


    $scope.save = function() {
        $scope.importedTable.bindingIdentifier = $rootScope.createNewFlavorName($scope.importedTable.bindingIdentifier);
        $scope.importedTable.libIds.push($scope.selectedTableLibary.id);
        $scope.importedTable.newTable = true;

        TableService.save($scope.importedTable).then(function(result) {
            var newTable = result;
            var newLink = {};
            newLink.bindingIdentifier = newTable.bindingIdentifier;
            newLink.id = newTable.id;

            TableLibrarySvc.addChild($scope.selectedTableLibary.id, newLink).then(function(link) {
                $scope.selectedTableLibary.children.splice(0, 0, newLink);
                $rootScope.tables.splice(0, 0, newTable);
                $rootScope.table = newTable;
                $rootScope.tablesMap[newTable.id] = newTable;

                $rootScope.codeSystems = [];

                if ($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
                    $rootScope.filteredTablesList.push(newTable);
                    $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                }
                $rootScope.$broadcast('event:openTable', newTable);
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        }, function(error) {
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

angular.module('igl').controller('AddPHINVADSTableOpenCtrl', function($scope, $modalInstance, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
    $scope.loading = false;
    $scope.selectedTableLibary = selectedTableLibary;
    $scope.searchText = '';
    $scope.hl7Tables = null;
    $scope.preloadedPhinvadsTables = [];
    $scope.phinvadsTables = [];
    $scope.selectedTables = [];

    $scope.loadPhinvads = function() {
        $scope.loading = true;
        return $http.get('api/igdocuments/PHINVADS/tables', {
            timeout: 600000
        }).then(function(response) {
            $scope.preloadedPhinvadsTables = response.data;
            $scope.loading = false;
        });
    };

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
    };

    $scope.isAlreadyIn = function(table) {
        if ($rootScope.tablesMap[table.id] == null) return false;
        return true;
    };

    $scope.isAlreadySelected = function(table) {
        var index = _.findIndex($scope.selectedTables, function(child) {
            return child.id === table.id;
        });
        if (index == -1) return false;
        return true;
    };

    $scope.addTable = function(table) {
        $scope.selectedTables.push(table);
    };

    $scope.deleteTable = function(table) {
        var index = $scope.selectedTables.indexOf(table);
        if (index > -1) $scope.selectedTables.splice(index, 1);
    };

    $scope.save = function() {
        var childrenLinks = [];
        for (var i = 0; i < $scope.selectedTables.length; i++) {
            $http.get('api/tables/' + $scope.selectedTables[i].id, {
                timeout: 600000
            }).then(function(response) {
                var addedTable = response.data;
                $rootScope.tables.splice(0, 0, addedTable);
                $rootScope.tablesMap[addedTable.id] = addedTable;
            });

            var newLink = angular.fromJson({
                id: $scope.selectedTables[i].id,
                bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
            });
            $scope.selectedTableLibary.children.push(newLink);
            childrenLinks.push(newLink);
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


    $scope.loadPhinvads();
});


angular.module('igl').controller('AddDatatypeDlgCtl',
    function($scope, $rootScope, $modalInstance, hl7Version, datatypes, masterLib, userDtLib, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http) {

        //$scope.hl7Version = hl7Version;
        //$scope.hl7Datatypes = datatypes;        

        $scope.newDts = [];
        $scope.checkedExt = true;
        $scope.NocheckedExt = true;
        $scope.masterLib = [];
        $scope.userDtLib = userDtLib;
        $scope.masterLib = masterLib;
        $scope.selectedDatatypes = [];
        // for (var i = 0; i < $scope.masterDts.length; i++) {
        //     if (!$rootScope.datatypesMap[$scope.masterDts[i].id]) {
        //         $scope.masterDatatypes.push($scope.masterDts[i]);
        //     }
        // }
        $scope.selectUserDtLib = function(usrLib) {
            console.log(usrLib);
            DatatypeLibrarySvc.getDatatypesByLibrary(usrLib.id).then(function(datatypes) {
                $scope.userDatatypes = datatypes;
                console.log($scope.userDatatypes);
            });
        };
        $scope.selectMasterDtLib = function(masLib) {
            console.log(masLib);
            DatatypeLibrarySvc.getDatatypesByLibrary(masLib.id).then(function(datatypes) {
                $scope.masterDatatypes = datatypes;
                console.log($scope.masterDatatypes);
            });
        };
        var listHL7Versions = function() {
            return $http.get('api/igdocuments/findVersions', {
                timeout: 60000
            }).then(function(response) {
                var hl7Versions = [];
                var length = response.data.length;
                for (var i = 0; i < length; i++) {
                    hl7Versions.push(response.data[i]);
                }
                console.log(hl7Versions);
                return hl7Versions;
            });
        };

        var init = function() {
            listHL7Versions().then(function(versions) {
                //$scope.versions = versions;
                var v = [];
                for (var i = 0; i < versions.length; i++) {
                    if (versions.indexOf(hl7Version) <= i) {
                        v.push(versions[i]);
                    }
                }

                $scope.version1 = hl7Version;
                $scope.versions = v;
                var scopes = ['HL7STANDARD'];
                DatatypeService.getDataTypesByScopesAndVersion(scopes, hl7Version).then(function(result) {
                    console.log("result");
                    console.log(result);
                    $scope.hl7Datatypes = result;

                    // $scope.hl7Segments = result.filter(function(current) {
                    //     return $rootScope.segments.filter(function(current_b) {
                    //         return current_b.id == current.id;
                    //     }).length == 0
                    // });




                    console.log("addSegment scopes=" + scopes.length);


                });
            });

        };
        init();
        $scope.setVersion = function(version) {
            $scope.version1 = version;
            var scopes = ['HL7STANDARD'];
            DatatypeService.getDataTypesByScopesAndVersion(scopes, version).then(function(result) {
                console.log("result");
                console.log(result);
                $scope.hl7Datatypes = result;

                // $scope.hl7Segments = result.filter(function(current) {
                //     return $rootScope.segments.filter(function(current_b) {
                //         return current_b.id == current.id;
                //     }).length == 0
                // });






            });
        }





        $scope.addDt = function(datatype) {
            console.log(datatype);
            $scope.selectedDatatypes.push(datatype);

        };
        $scope.checkExist = function(datatype) {

            for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                if ($scope.selectedDatatypes[i].id === datatype.id) {
                    return true;
                }
            }
            for (var i = 0; i < $rootScope.datatypes.length; i++) {
                if ($rootScope.datatypes[i].id === datatype.id) {
                    return true;
                }
            }
            return false;
        }
        $scope.checkExt = function(datatype) {
            $scope.checkedExt = true;
            $scope.NocheckedExt = true;
            if (datatype.ext === "") {
                $scope.NocheckedExt = false;
                return $scope.NocheckedExt;
            }
            for (var i = 0; i < $rootScope.datatypes.length; i++) {
                if ($rootScope.datatypes[i].name === datatype.name && $rootScope.datatypes[i].ext === datatype.ext) {
                    $scope.checkedExt = false;
                    return $scope.checkedExt;
                }
            }
            console.log($scope.selectedDatatypes.indexOf(datatype));
            for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                if ($scope.selectedDatatypes.indexOf(datatype) !== i) {
                    if ($scope.selectedDatatypes[i].name === datatype.name && $scope.selectedDatatypes[i].ext === datatype.ext) {
                        $scope.checkedExt = false;
                        return $scope.checkedExt;
                    }
                }

            }

            return $scope.checkedExt;
        };
        $scope.addDtFlv = function(datatype) {
            var newDatatype = angular.copy(datatype);

            newDatatype.ext = $rootScope.createNewExtension(newDatatype.ext);
            newDatatype.scope = 'USER';
            newDatatype.participants = [];
            newDatatype.id = new ObjectId().toString();;
            newDatatype.libIds = [];
            newDatatype.libIds.push($rootScope.igdocument.profile.datatypeLibrary.id);
            if (datatype.scope === 'MASTER') {
                //newDatatype.hl7versions=[$rootScope.igdocument.profile.metaData.hl7Version];
                var temp = [];
                temp.push($rootScope.igdocument.profile.metaData.hl7Version);
                newDatatype.hl7versions = temp;
                newDatatype.hl7Version = $rootScope.igdocument.profile.metaData.hl7Version;

            }


            if (newDatatype.components != undefined && newDatatype.components != null && newDatatype.components.length != 0) {
                for (var i = 0; i < newDatatype.components.length; i++) {
                    newDatatype.components[i].id = new ObjectId().toString();
                }
            }

            var predicates = newDatatype['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function(predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }

            var conformanceStatements = newDatatype['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function(conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }
            $scope.selectedDatatypes.push(newDatatype);
            console.log($scope.selectedDatatypes)
        }
        $scope.deleteDt = function(datatype) {
            var index = $scope.selectedDatatypes.indexOf(datatype);
            if (index > -1) $scope.selectedDatatypes.splice(index, 1);
        };








        var secretEmptyKey = '[$empty$]'

        $scope.hl7Datatypes = datatypes.filter(function(current) {
            return $rootScope.datatypes.filter(function(current_b) {
                return current_b.id == current.id;
            }).length == 0
        });


        $scope.dtComparator = function(datatype, viewValue) {
            if (datatype) {
                console.log(datatype.name);
                console.log(datatype);
            }
            return viewValue === secretEmptyKey || (datatype && ('' + datatype.name).toLowerCase().indexOf(('' + viewValue).toLowerCase()) > -1);
        };


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
            console.log($scope.selectedDatatypes);
            $scope.selectFlv = [];
            var newLinks = [];
            for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                if ($scope.selectedDatatypes[i].scope === 'USER') {
                    $scope.selectFlv.push($scope.selectedDatatypes[i]);
                } else {
                    newLinks.push({
                        id: $scope.selectedDatatypes[i].id,
                        name: $scope.selectedDatatypes[i].name
                    })
                }
            }
            $rootScope.usedDtLink = [];
            $rootScope.usedVsLink = [];
            for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                $rootScope.fillMaps($scope.selectedDatatypes[i]);
            }
            DatatypeService.saves($scope.selectFlv).then(function(result) {
                for (var i = 0; i < result.length; i++) {
                    newLinks.push({
                        id: result[i].id,
                        name: result[i].name,
                        ext: result[i].ext
                    })
                }
                DatatypeLibrarySvc.addChildren($rootScope.igdocument.profile.datatypeLibrary.id, newLinks).then(function(link) {
                    for (var i = 0; i < newLinks.length; i++) {
                        $rootScope.igdocument.profile.datatypeLibrary.children.splice(0, 0, newLinks[i]);
                    }
                    for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                        $rootScope.datatypes.splice(0, 0, $scope.selectedDatatypes[i]);
                    }
                    for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                        $rootScope.datatypesMap[$scope.selectedDatatypes[i].id] = $scope.selectedDatatypes[i];
                    }
                    var usedDtId1 = _.map($rootScope.usedDtLink, function(num, key) {
                        return num.id;
                    });

                    DatatypeService.get(usedDtId1).then(function(datatypes) {
                        for (var j = 0; j < datatypes.length; j++) {
                            if (!$rootScope.datatypesMap[datatypes[j].id]) {

                                $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                                $rootScope.datatypes.push(datatypes[j]);
                                $rootScope.processElement(datatypes[j]);
                            }
                        }

                        var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                            return num.id;
                        });
                        console.log("$rootScope.usedVsLink");

                        console.log($rootScope.usedVsLink);
                        var newTablesLink = _.difference($rootScope.usedVsLink, $rootScope.igdocument.profile.tableLibrary.children);
                        console.log(newTablesLink);

                        TableLibrarySvc.addChildren($rootScope.igdocument.profile.tableLibrary.id, newTablesLink).then(function() {
                            $rootScope.igdocument.profile.tableLibrary.children = _.union(newTablesLink, $rootScope.igdocument.profile.tableLibrary.children);

                            TableService.get(usedVsId).then(function(tables) {
                                for (var j = 0; j < tables.length; j++) {
                                    if (!$rootScope.tablesMap[tables[j].id]) {
                                        $rootScope.tablesMap[tables[j].id] = tables[j];
                                        $rootScope.tables.push(tables[j]);
                                        $rootScope.processElement(tables[j]);

                                    }
                                }

                                for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
                                    $rootScope.processElement($scope.selectedDatatypes[i]);
                                }
                                //$rootScope.processElement($scope.newSegment);

                            });
                        });


                    });


                    //$rootScope.processElement($scope.newDatatype);
                    // $rootScope.filteredDatatypesList.push($scope.newDatatype);
                    // $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                    // $rootScope.$broadcast('event:openDatatype', $scope.newDatatype);
                    $rootScope.msg().text = "datatypeAdded";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;
                    $modalInstance.close(datatypes);
                });

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

angular.module('igl').controller('AddMasterDatatypes',
    function($scope, $rootScope, $modalInstance, datatypes, DatatypeLibrarySvc, DatatypeService) {
        $scope.version = $rootScope.igdocument.profile.metaData.hl7Version;
        $scope.scopes = ["MASTER"];
        $scope.masterDatatypes = [];
        $scope.newDts = [];
        DatatypeService.getPublishedMaster().then(function(result) {
            $scope.masterDatatypes = result;

        });



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
    function($scope, $rootScope, $modalInstance, hl7Version, $http, SegmentService, SegmentLibrarySvc, DatatypeService, DatatypeLibrarySvc, TableService, TableLibrarySvc, IgDocumentService) {

        $scope.selectedSegments = [];
        $scope.checkedExt = true;
        $scope.NocheckedExt = true;

        $scope.addseg = function(segment) {
            $scope.selectedSegments.push(segment);
            console.log($scope.selectedSegments);

        };
        $scope.checkExist = function(segment) {
            // if ($scope.selectedSegments.indexOf(segment) !== -1) {
            //     return true;
            // }
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
                if ($scope.selectedSegments[i].id === segment.id) {
                    return true;
                }
            }
            return false;
        }
        $scope.checkExt = function(segment) {
            console.log(segment);
            $scope.checkedExt = true;
            $scope.NocheckedExt = true;
            if (segment.ext === "") {
                $scope.NocheckedExt = false;
                return $scope.NocheckedExt;
            }
            for (var i = 0; i < $rootScope.segments.length; i++) {
                if ($rootScope.segments[i].name === segment.name && $rootScope.segments[i].ext === segment.ext) {
                    $scope.checkedExt = false;
                    return $scope.checkedExt;
                }
            }
            console.log($scope.selectedSegments.indexOf(segment));
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
                if ($scope.selectedSegments.indexOf(segment) !== i) {
                    if ($scope.selectedSegments[i].name === segment.name && $scope.selectedSegments[i].ext === segment.ext) {
                        $scope.checkedExt = false;
                        return $scope.checkedExt;
                    }
                }

            }

            return $scope.checkedExt;
        };
        $scope.addsegFlv = function(segment) {
            var newSegment = angular.copy(segment);
            newSegment.participants = [];
            newSegment.scope = 'USER';
            newSegment.id = new ObjectId().toString();
            newSegment.libIds = [];
            newSegment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
            newSegment.ext = $rootScope.createNewExtension(newSegment.ext);

            if (newSegment.fields != undefined && newSegment.fields != null && newSegment.fields.length != 0) {
                for (var i = 0; i < newSegment.fields.length; i++) {
                    newSegment.fields[i].id = new ObjectId().toString();
                }
            }


            var dynamicMappings = newSegment['dynamicMappings'];
            if (dynamicMappings != undefined && dynamicMappings != null && dynamicMappings.length != 0) {
                angular.forEach(dynamicMappings, function(dynamicMapping) {
                    dynamicMapping.id = new ObjectId().toString();
                    angular.forEach(dynamicMapping.mappings, function(mapping) {
                        mapping.id = new ObjectId().toString();
                    });
                });
            }
            $scope.selectedSegments.push(newSegment);
        }
        $scope.deleteSeg = function(segment) {
            var index = $scope.selectedSegments.indexOf(segment);
            if (index > -1) $scope.selectedSegments.splice(index, 1);
        };

        var listHL7Versions = function() {
            return $http.get('api/igdocuments/findVersions', {
                timeout: 60000
            }).then(function(response) {
                var hl7Versions = [];
                var length = response.data.length;
                for (var i = 0; i < length; i++) {
                    hl7Versions.push(response.data[i]);
                }
                console.log(hl7Versions);
                return hl7Versions;
            });
        };


        var init = function() {
            listHL7Versions().then(function(versions) {
                //$scope.versions = versions;
                var v = [];
                for (var i = 0; i < versions.length; i++) {
                    if (versions.indexOf(hl7Version) <= i) {
                        v.push(versions[i]);
                    }
                }

                $scope.version1 = hl7Version;
                $scope.versions = v;
                var scopes = ['HL7STANDARD'];
                SegmentService.getSegmentsByScopesAndVersion(scopes, hl7Version).then(function(result) {
                    console.log("result");
                    console.log(result);

                    $scope.hl7Segments = result.filter(function(current) {
                        return $rootScope.segments.filter(function(current_b) {
                            return current_b.id == current.id;
                        }).length == 0
                    });




                    console.log("addSegment scopes=" + scopes.length);


                });
            });

        };
        init();
        var secretEmptyKey = '[$empty$]'
        $scope.segComparator = function(seg, viewValue) {

            return viewValue === secretEmptyKey || ('' + seg).toLowerCase().indexOf(('' + viewValue).toLowerCase()) > -1;
        };



        $scope.setVersion = function(version) {
            console.log($scope.selectedSegments);
            $scope.version1 = version;
            var scopes = ['HL7STANDARD'];
            SegmentService.getSegmentsByScopesAndVersion(scopes, version).then(function(result) {
                console.log("result");
                console.log(result);

                $scope.hl7Segments = result.filter(function(current) {
                    return $rootScope.segments.filter(function(current_b) {
                        return current_b.id == current.id;
                    }).length == 0
                });




                console.log("addSegment scopes=" + scopes.length);


            });
        }

        console.log("=----");
        console.log($scope.hl7Segments);
        $scope.isInSegs = function(segment) {

            if (segment && $scope.hl7Segments.indexOf(segment) === -1) {
                return false;
            } else {
                return true;
            }

        };
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
            // var newLink = angular.fromJson({
            //     id: $scope.newSegment.id,
            //     name: $scope.newSegment.name
            // });

            $scope.selectFlv = [];
            var newLinks = [];
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
                if ($scope.selectedSegments[i].scope === 'USER') {
                    $scope.selectFlv.push($scope.selectedSegments[i]);
                } else {
                    newLinks.push({
                        id: $scope.selectedSegments[i].id,
                        name: $scope.selectedSegments[i].name
                    })
                }
            }

            // for (var i = 0; i < $scope.selectedSegments.length; i++) {
            //     newLinks.push({
            //         id: $scope.selectedSegments[i].id,
            //         name: $scope.selectedSegments[i].name
            //     })
            // }


            console.log("newLinks");
            console.log(newLinks);
            $rootScope.usedDtLink = [];
            $rootScope.usedVsLink = [];
            for (var i = 0; i < $scope.selectedSegments.length; i++) {
                $rootScope.fillMaps($scope.selectedSegments[i]);
            }
            SegmentService.saves($scope.selectFlv).then(function(result) {
                    for (var i = 0; i < result.length; i++) {
                        newLinks.push({
                            id: result[i].id,
                            name: result[i].name,
                            ext: result[i].ext
                        })
                    }
                    console.log("result");
                    console.log(result);
                    SegmentLibrarySvc.addChildren($rootScope.igdocument.profile.segmentLibrary.id, newLinks).then(function(link) {
                        // $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLinks);
                        for (var i = 0; i < newLinks.length; i++) {
                            $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLinks[i]);
                        }
                        //$rootScope.segments.splice(0, 0, $scope.selectedSegments);
                        for (var i = 0; i < $scope.selectedSegments.length; i++) {
                            $rootScope.segments.splice(0, 0, $scope.selectedSegments[i]);
                        }
                        //$rootScope.segment = $scope.newSegment;
                        //$rootScope.segmentsMap[$scope.newSegment.id] = $scope.newSegment;
                        for (var i = 0; i < $scope.selectedSegments.length; i++) {
                            $rootScope.segmentsMap[$scope.selectedSegments[i].id] = $scope.selectedSegments[i];
                        }
                        //TODO MasterMap need to add Segment

                        //                  MastermapSvc.addSegmentObject(newSegment, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
                        // $rootScope.filteredSegmentsList.push($scope.newSegment);
                        // $rootScope.filteredSegmentsList = _.uniq($rootScope.filteredSegmentsList);
                        // $rootScope.$broadcast('event:openSegment', $scope.newSegment);
                        $rootScope.msg().text = "segmentAdded";
                        $rootScope.msg().type = "success";
                        $rootScope.msg().show = true;
                        $modalInstance.close();
                        var usedDtId = _.map($rootScope.usedDtLink, function(num, key) {
                            return num.id;
                        });
                        DatatypeService.get(usedDtId).then(function(datatypes) {
                            for (var j = 0; j < datatypes.length; j++) {

                                $rootScope.fillMaps(datatypes[j]);

                            }
                            var usedDtId1 = _.map($rootScope.usedDtLink, function(num, key) {
                                return num.id;
                            });
                            var newDatatypesLink = _.difference($rootScope.usedDtLink, $rootScope.igdocument.profile.datatypeLibrary.children);
                            DatatypeLibrarySvc.addChildren($rootScope.igdocument.profile.datatypeLibrary.id, newDatatypesLink).then(function() {
                                $rootScope.igdocument.profile.datatypeLibrary.children = _.union(newDatatypesLink, $rootScope.igdocument.profile.datatypeLibrary.children);

                                DatatypeService.get(usedDtId1).then(function(datatypes) {
                                    for (var j = 0; j < datatypes.length; j++) {
                                        if (!$rootScope.datatypesMap[datatypes[j].id]) {

                                            $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                                            $rootScope.datatypes.push(datatypes[j]);
                                            $rootScope.processElement(datatypes[j]);
                                        }
                                    }

                                    var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                                        return num.id;
                                    });
                                    console.log("$rootScope.usedVsLink");

                                    console.log($rootScope.usedVsLink);
                                    var newTablesLink = _.difference($rootScope.usedVsLink, $rootScope.igdocument.profile.tableLibrary.children);
                                    console.log(newTablesLink);

                                    TableLibrarySvc.addChildren($rootScope.igdocument.profile.tableLibrary.id, newTablesLink).then(function() {
                                        $rootScope.igdocument.profile.tableLibrary.children = _.union(newTablesLink, $rootScope.igdocument.profile.tableLibrary.children);

                                        TableService.get(usedVsId).then(function(tables) {
                                            for (var j = 0; j < tables.length; j++) {
                                                if (!$rootScope.tablesMap[tables[j].id]) {
                                                    $rootScope.tablesMap[tables[j].id] = tables[j];
                                                    $rootScope.tables.push(tables[j]);
                                                    $rootScope.processElement(tables[j]);

                                                }
                                            }


                                            $rootScope.processElement($scope.newSegment);

                                        });
                                    });


                                });
                            });
                        });

                    })
                },
                function(error) {
                    $scope.saving = false;
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                })

        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });

angular.module('igl').controller('ShareIGDocumentCtrl', function ($scope, $modalInstance, $http, igdocumentSelected, userList,IgDocumentService,$rootScope) {

    $scope.igdocumentSelected = igdocumentSelected;
	$scope.userList =  userList;
	$scope.error = "";
	$scope.ok = function () {
		var idsTab = $scope.tags.map(function(user) {
			return user.id;
		});
        IgDocumentService.share($scope.igdocumentSelected.id,idsTab).then(function(result){
            // Add participants for direct view
            $scope.igdocumentSelected.shareParticipants = $scope.igdocumentSelected.shareParticipants || [];
            $scope.tags.forEach(function(tag) {
                tag.permission = $scope.selectedItem.selected;
                tag.pendingApproval = true;
                $scope.igdocumentSelected.shareParticipants.push(tag);
            });
            $rootScope.msg().text = "igSharedSuccessfully";
            $rootScope.msg().type ="success";
            $rootScope.msg().show = true;
            $modalInstance.close();
        }, function(error){
            $scope.error = error.data;
            console.log(error);
        });
	};
	$scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
	$scope.tags = [];
	$scope.selectedItem = {
		selected: "VIEW"
	};
	$scope.itemArray = ["VIEW"];
	
	$scope.tags = [];
	$scope.loadUsernames = function ($query) {
		return userList.filter(function (user) {
			return user.username.toLowerCase().indexOf($query.toLowerCase()) != -1;
		});
	};

    $scope.unshare = function (shareParticipant) {
        $scope.loading = false;
        IgDocumentService.unshare($scope.igdocumentSelected.id,shareParticipant.id).then(function(res){
            var indexOfId = $scope.igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.id);
            if (indexOfId > -1) {
                $scope.igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
            }
            var participantIndex = -1;
            for(var i=0; i <  $scope.igdocumentSelected.shareParticipants.length; i++){
                if($scope.igdocumentSelected.shareParticipants[i].id === shareParticipant.id){
                    participantIndex =i;
                    $scope.userList.push($scope.igdocumentSelected.shareParticipants[i]);
                    break;
                }
            }
            if (participantIndex > -1) {
                $scope.igdocumentSelected.shareParticipants.splice(participantIndex, 1);
            }
            $scope.loading = false;
            $rootScope.msg().text = "igUnSharedSuccessfully";
            $rootScope.msg().type ="success";
            $rootScope.msg().show = true;
         }, function(error){
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
            $scope.loading = false;
        });
    };


});

angular.module('igl').controller('UnShareIGDocumentCtrl', function ($scope, $modalInstance, $http, igdocumentSelected, shareParticipant,IgDocumentService,$rootScope) {
    $scope.igdocumentSelected = igdocumentSelected;
    $scope.shareParticipant = shareParticipant;
    $scope.error = "";
    $scope.loading = false;
    $scope.ok = function () {
        $scope.loading = true;
        IgDocumentService.unshare(igdocumentSelected.id,shareParticipant.id).then(function(res){
            var indexOfId = igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.id);
            if (indexOfId > -1) {
                igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
            }
            var participantIndex = -1;
            for(var i=0; i <  igdocumentSelected.shareParticipants.length; i++){
                if(igdocumentSelected.shareParticipants[i].id === shareParticipant.id){
                    participantIndex =i;
                    break;
                }
            }
            if (participantIndex > -1) {
                igdocumentSelected.shareParticipants.splice(participantIndex, 1);
            }
            $scope.loading = false;
            $rootScope.msg().text = "igUnSharedSuccessfully";
            $rootScope.msg().type ="success";
            $rootScope.msg().show = true;
            $modalInstance.close();
        }, function(error){
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
            $scope.loading = false;
        });
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
