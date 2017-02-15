angular
    .module('igl')
    .controller(
        'TreeCtrl', ['$scope',
            '$rootScope',
            '$http',
            'SectionSvc',
            'CloneDeleteSvc',
            'FilteringSvc',
            '$cookies',
            'DatatypeLibrarySvc',
            '$modal',
            'CompositeMessageService',
            'PcService',

            function($scope, $rootScope, $http, SectionSvc, CloneDeleteSvc, FilteringSvc, $cookies, DatatypeLibrarySvc, $modal, CompositeMessageService, PcService) {

                $scope.collapsedata = false;
                $scope.collapsePcs = true;
                $scope.collapseprofilecomponent = false;
                $scope.collapsemessage = false;
                $scope.collapsesegment = false;
                $scope.collapsetable = false;
                $scope.collapsevalueSet = false;
                $scope.profilecollapsed = false;
                $scope.openMetadata = false;
                $scope.ordredMessages = [];
                $scope.dataTypeLibraryCollapsed = false;
                $rootScope.activeModel = "";
                $scope.segmentsChecked = false;
                $rootScope.filteringMode = false;
                $rootScope.loadingSegments = false;
                $rootScope.loadingDataTypes = false;
                $rootScope.loadingTables = false;
                $scope.Activate = function(param) {
                    $rootScope.activeModel = param;
                }
                $scope.getDeleteLabl = function() {
                    // if($rootScope.references.length===0){
                    return "Delete"
                        // }else return "Show References"
                };

                $scope.exportCSVForTable = function(table) {
                    console.log(table);

                    var form = document.createElement("form");

                    form.action = $rootScope.api('api/tables/exportCSV/' + table.id);
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

                $rootScope.switcherDatatypeLibrary = function() {

                    $scope.dataTypeLibraryCollapsed = !$scope.dataTypeLibraryCollapsed;

                };
                $rootScope.openMetadata = function() {

                    $scope.openMetadata = !$scope.openMetadata;

                };

                $rootScope.switcherprofile = function() {
                    $scope.profilecollapsed = !$scope.profilecollapsed;

                };
                $rootScope.switcherpcs = function() {
                    $scope.collapsePcs = !$scope.collapsePcs;
                }


                $rootScope.updateSectionContent = function(section) {
                    if (section.childSections) {
                        section.childSections = section.childSections;
                    }

                };
                $rootScope.switchervalueSet = function() {
                    $scope.collapsevalueSet = !$scope.collapsevalueSet;
                };
                $rootScope.switchertable = function() {
                    $scope.collapsetable = !$scope.collapsetable;

                };

                $rootScope.switcherseg = function() {
                    $scope.collapsesegment = !$scope.collapsesegment;

                };

                $rootScope.switchermsg = function() {
                    $scope.collapsemessage = !$scope.collapsemessage;
                };
                $rootScope.switcherpc = function() {
                    $scope.collapseprofilecomponent = !$scope.collapseprofilecomponent;
                };
                $rootScope.switchercm = function() {
                    $scope.collapsecompositemessage = !$scope.collapsecompositemessage;
                };

                $rootScope.switcherdata = function() {
                    $scope.collapsedata = !$scope.collapsedata;

                };
                $scope.DatattypeTreeOption = {
                    accept: function(sourceNodeScope, destNodesScope, destIndex) {
                        var dataTypeSource = sourceNodeScope.$element
                            .attr('data-type');
                        var dataTypeDest = destNodesScope.$element
                            .attr('data-type');

                        return true;


                    },


                    dropped: function(event) {

                        var sourceNode = event.source.nodeScope;
                        var destNodes = event.dest.nodesScope;
                        var sortBefore = event.source.index;
                        var sortAfter = event.dest.index;
                        var source = sourceNode.$parentNodeScope.$modelValue;
                        var dest = destNodes.$parent.$modelValue;
                        var dataTypeDest = destNodes.$element.attr('data-type');
                        var dataTypeSource = sourceNode.$element.attr('data-type');
                        event.source.nodeScope.$modelValue.sectionPosition = sortAfter + 1;

                        var parentSource = sourceNode.$parentNodeScope.$modelValue;
                        var parentDest = event.dest.nodesScope.$nodeScope.$modelValue;


                    }
                };
                $scope.isValidated = function(data) {
                    if (data && $rootScope.validationResult.targetId === data.id) {
                        return true;
                    } else {
                        return false;
                    }
                };
                $scope.hasErrorInTree = function(data) {
                    // if ($rootScope.validationResult) {
                    //     if (data &&  $rootScope.validationResult.targetId===data.id) {
                    //         return true;
                    //     } else {
                    //         return false;
                    //     }
                    // }
                    if ($rootScope.validationResult && $rootScope.validationResult.errorCount>0) {
                        return true;
                    } else {
                        return false;
                    }



                    // if ($rootScope.datatypeValidationResult) {
                    //     var x = $rootScope.datatypeValidationResult.items.find(function(item) {
                    //         if (item.targetId === data.id) {
                    //             return (item.targetId === data.id)
                    //         } else if (item.parentId === data.id) {
                    //             return (item.parentId === data.id);
                    //         }
                    //     });
                    //     if (x) {
                    //         return true;
                    //     } else {
                    //         return false;
                    //     }
                    // } else {
                    //     return false;
                    // }
                };


                $scope.treeOptions = {

                    accept: function(sourceNodeScope, destNodesScope, destIndex) {
                        var dataTypeSource = sourceNodeScope.$element
                            .attr('data-type');
                        var dataTypeDest = destNodesScope.$element
                            .attr('data-type');


                        if (!dataTypeDest) {

                            return false;
                        } else if (dataTypeSource === "sections" && dataTypeDest === "sections") {

                            return true;


                        } else if (dataTypeDest === dataTypeSource + "s") {
                            return true;

                        } else
                            return false;
                    },


                    dragStart: function(event) {
                        $rootScope.childSections = angular.copy($rootScope.igdocument.childSections);
                        //                	var sourceNode = event.source.nodeScope;
                        //        			var destNodes = event.dest.nodesScope;
                        //        			
                        //        			$scope.sourceDrag=angular.copy(sourceNode.$modelValue);
                        //        			//$scope.destDrag=angular.copy(sourceNode.$parent.$nodeScope.$modelValue);
                        //        			$scope.parentDrag=sourceNode.$parentNodeScope.$modelValue;
                        //        			console.log($scope.parentDrag);



                        if ($rootScope.hasChanges()) {

                            $rootScope.openConfirmLeaveDlg().result.then(function() {



                            });
                        }

                    },


                    dropped: function(event) {

                        var sourceNode = event.source.nodeScope;
                        var destNodes = event.dest.nodesScope;
                        var sortBefore = event.source.index;
                        var sortAfter = event.dest.index;
                        var source = sourceNode.$parentNodeScope.$modelValue;
                        var dest = destNodes.$parent.$modelValue;
                        var dataTypeDest = destNodes.$element.attr('data-type');
                        var dataTypeSource = sourceNode.$element.attr('data-type');
                        // event.source.nodeScope.$modelValue.sectionPosition =
                        // sortAfter + 1;

                        var parentSource = sourceNode.$parentNodeScope.$modelValue;
                        var parentDest = event.dest.nodesScope.$nodeScope.$modelValue;


                        if (dataTypeDest === "messages") {
                            console.log("========ordering messages");
                            $scope.updateMessagePositions($rootScope.igdocument.profile.messages.children);
                            $scope.reOrderMessages();
                            return "";
                        } else if (parentSource.type === "document" && parentDest.type === "section") {
                            console.log("putting root into child");
                            $scope.updatePositions($rootScope.igdocument.childSections);
                            $scope.updatePositions(parentDest.childSections);
                            $scope.updateChildeSections($rootScope.igdocument.childSections);
                            return "";
                        } else if (parentSource.type === "document" && parentDest.type === "document") {
                            console.log("========updating childSection of ig");
                            $scope.updatePositions($rootScope.igdocument.childSections);
                            $scope.reOrderChildSections();
                            return "";

                        } else if (parentSource.type === "section" && parentDest.type === "document") {
                            console.log($rootScope.igdocument.childSections);
                            $scope.updatePositions($rootScope.igdocument.childSections);
                            $scope.updatePositions(parentSource.childSections);
                            $scope.updateChildeSections($rootScope.igdocument.childSections);

                            return "";

                        } else if (dataTypeDest && dataTypeDest === "sections" && dataTypeSource === "sections") {

                            if (parentDest.id === parentSource.id) {
                                $scope.updatePositions(parentSource.childSections);
                                console.log("=========ordering the same section");
                                console.log(parentSource);
                                SectionSvc.update($rootScope.igdocument.id, parentSource).then(function() {
                                    return "";
                                });
                            } else {
                                console.log(" ordering 2 sections ");
                                $scope.updatePositions(parentSource.childSections);
                                $scope.updatePositions(parentDest.childSections);
                                SectionSvc.update($rootScope.igdocument.id, parentSource).then(function() {
                                    SectionSvc.update($rootScope.igdocument.id, parentDest).then(function() {
                                        return "";
                                    });
                                });

                            }

                        }
                    }


                };

                $scope.updatePositions = function(arr) {
                    if (arr !== undefined) {
                        for (var i = 0; i <= arr.length - 1; i++) {
                            arr[i].sectionPosition = i + 1;

                        }
                    }
                    return "";
                };


                $scope.updateMessagePositions = function(arr) {


                    if (arr !== undefined && arr != null) {
                        for (var i = 0; i <= arr.length - 1; i++) {
                            if (arr[i] != null) // wierd but happened
                                arr[i].position = i + 1;
                        }
                    }
                    return "";
                };


                $scope.getLastPosition = function(arr) {
                    var position = arr.length;
                    for (var i = arr.length - 1; i >= 0; i--) {
                        var position = arr.length;

                        if (arr[i].sectionPosition && arr[i].sectionPosition >= position) {
                            return arr[i].sectionPosition + 1;
                        } else return position;
                    }

                };

                $scope.getLastcloneIndex = function(arr, name) {
                    var index = 0;
                    var cutIndex = name.length - 1;
                    for (var i = arr.length - 1; i >= 0; i--) {
                        if (arr[i].sectionTitle.substring(0, cutIndex) === name && arr[i].sectionTitle.length > cutIndex + 1) {
                            index = parseInt(arr[i].sectionTitle.substring(cutIndex + 1)) + 1;

                            return index;
                        } else return 1;
                    }

                };


                $scope.getLabel = function($itemScope) {
                    if ($itemScope.$parentNodeScope) {
                        var p = $scope.getLabel($itemScope.$parentNodeScope);
                        if (p === "")
                            return $itemScope.$modelValue.sectionPosition;
                        else
                            return p + "." + $itemScope.$modelValue.sectionPosition;
                    } else {
                        return "";
                    }
                };


                $scope.cloneSectionTree = function(section) {
                    var clone = {};
                    if (section.childSections === undefined) {
                        clone.id = new ObjectId().toString();
                        clone.type = section.type;
                        clone.sectionContents = section.sectionContents;
                        clone.sectionDescription = section.sectionDescription;
                        clone.sectionTitle = section.sectionTitle + Math.floor((Math.random() * 50000) + 1);
                        console.log(clone);
                        return clone;
                    } else {
                        clone.type = section.type;
                        clone.childSections = [];
                        clone.sectionTitle = section.sectionTitle + Math.floor((Math.random() * 50000) + 1);

                        clone.id = new ObjectId().toString();
                        clone.sectionContents = section.sectionContents;
                        clone.sectionDescription = section.sectionDescription;
                        for (var i = 0; i <= section.childSections.length - 1; i++) {
                            var child = $scope.cloneSectionTree(section.childSections[i]);
                            child.sectionTitle = section.childSections[i].sectionTitle;
                            child.sectionPosition = section.childSections[i].sectionPosition;
                            clone.childSections.push(child);
                        }
                        return clone;
                    }
                };

                $scope.cloneSection = function(section) {

                    var clone = angular.copy(section);
                    clone.id = new ObjectId().toString();
                    clone.childSections = [];
                    clone.sectionTitle = section.sectionTitle + Math.floor((Math.random() * 50000) + 1);
                    if (section.childSections && section.childSections.length > 0) {
                        angular.forEach(section.childSections, function(sect) {
                            clone.childSections.push($scope.cloneInside(sect));

                        });
                    }
                    return clone;


                };

                $scope.cloneInside = function(sectionInside) {
                    var clone = angular.copy(sectionInside);
                    clone.id = new ObjectId().toString();
                    clone.childSections = [];
                    if (sectionInside.childSections && sectionInside.childSections.length > 0) {

                        angular.forEach(sectionInside.childSections, function(sect) {
                            clone.childSections.push($scope.cloneInside(sect));

                        });

                    }
                    return clone;
                }


                $scope.debug = function(childSections) {
                    console.log("DEBUG FNCT");
                    console.log(childSections);
                }

                $scope.recharge = false;

                $scope.sectionOption = [

                    ['Add Section',
                        function($itemScope) {
                            var newSection = {};
                            newSection.type = "section";
                            newSection.id = new ObjectId().toString();
                            newSection.childSections = [];
                            newSection.sectionContents = "";
                            newSection.sectionDescription = "";
                            newSection.sectionTitle = "new Section" + Math.floor((Math.random() * 50000) + 1);


                            if (!$itemScope.section.childSections.length) {
                                newSection.sectionPosition = 1;
                                $itemScope.section.childSections = [];
                                $itemScope.section.childSections.push(newSection);

                            } else {
                                $itemScope.section.childSections.push(newSection);
                                newSection.sectionPosition = $itemScope.section.childSections.length;
                            }
                            console.log($itemScope.section);
                            SectionSvc.update($rootScope.igdocument.id, $itemScope.section);
                            $scope.editSection(newSection);
                            $rootScope.activeModel = newSection.id;


                        }
                    ],
                    null,


                    ['Copy',
                        function($itemScope) {

                            function process() {
                                var cloneModel = $scope.cloneSection($itemScope.$nodeScope.$modelValue);
                                cloneModel.sectionPosition = $scope.getLastPosition($itemScope.$nodeScope.$parentNodesScope.$modelValue);
                                $itemScope.$nodeScope.$parentNodesScope.$modelValue.push(cloneModel);
                                $scope.editSection(cloneModel);
                                if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === "document") {
                                    $scope.updateChildeSections($rootScope.igdocument.childSections);
                                } else if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === "section") {
                                    SectionSvc.update($rootScope.igdocument.id, $itemScope.$nodeScope.$parentNodeScope.$modelValue);

                                }
                            };

                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    process();
                                });
                            } else {
                                process();
                            }


                        }
                    ],
                    null, [
                        'Delete',
                        function($itemScope) {
                            console.log($itemScope.section);
                            var section = $itemScope.section;
                            var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue.indexOf($itemScope.$nodeScope.$modelValue);

                            if (index > -1) {
                                $itemScope.$nodeScope.$parentNodesScope.$modelValue
                                    .splice(index, 1);
                            }
                            $scope.updatePositions($itemScope.$nodeScope.$parentNodesScope.$modelValue);


                            SectionSvc.delete($rootScope.igdocument.id, $itemScope.section.id).then(function() {

                                $scope.closeChildren($itemScope.section);
                                if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === 'section') {
                                    SectionSvc.update($rootScope.igdocument.id, $itemScope.$nodeScope.$parentNodeScope.$modelValue).then(function() {

                                    });
                                } else if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === 'document') {
                                    $scope.closeChildren($itemScope.section);
                                    $scope.updateChildeSections($rootScope.igdocument.childSections);


                                }
                            });





                        }
                    ]

                ];

                function processAddSection() {
                    var newSection = {};
                    newSection.id = new ObjectId().toString();
                    newSection.childSections = [];

                    var rand = Math.floor(Math.random() * 100);
                    if (!$rootScope.igdocument.profile.metaData.ext) {
                        $rootScope.igdocument.profile.metaData.ext = "";
                    }
                    newSection.sectionTitle = "New Section" + "-" +
                        $rootScope.igdocument.profile.metaData.ext + "-" +
                        rand;
                    newSection.label = newSection.sectionTitle;
                    $rootScope.igdocument.childSections.push(newSection);

                    newSection.sectionPosition = $rootScope.igdocument.childSections.length;
                    $scope.updateChildeSections($rootScope.igdocument.childSections);
                    $scope.Activate(newSection.id);
                };

                $scope.igOptions = [

                    ['Add Section',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    processAddSection();
                                });
                            } else {
                                processAddSection();
                            }


                        }
                    ]

                ];


                $scope.SegmentOptions = [

                    ['Create Flavor',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copySegment($itemScope.segment);
                                });
                            } else {
                                console.log($itemScope.segment);
                                CloneDeleteSvc.copySegment($itemScope.segment);
                            }

                        }
                    ],
                    null, [$scope.getDeleteLabl(),
                        function($itemScope) {
                            CloneDeleteSvc.deleteSegment($itemScope.segment);
                        }
                    ]

                ];

                $scope.DataTypeOptions = [

                    ['Create Flavor',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copyDatatype($itemScope.data);
                                });
                            } else {
                                CloneDeleteSvc.copyDatatype($itemScope.data);
                            }
                        }
                    ],
                    null, ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteDatatype($itemScope.data);

                        }
                    ]
                ];



                $scope.DataTypeOptionsForPublished = [
                    ['Create New Version',
                        function($itemScope) {
                            console.log($rootScope.versionAndUseMap[$itemScope.data.id]);
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    if ($rootScope.readyForNewVersion($itemScope.data)) {
                                        CloneDeleteSvc.upgradeDatatype($itemScope.data);
                                    } else {
                                        $scope.showCannotPublish($itemScope.data);
                                    }

                                });
                            } else {

                                if ($rootScope.readyForNewVersion($rootScope.versionAndUseMap[$itemScope.data.id])) {
                                    CloneDeleteSvc.upgradeDatatype($itemScope.data);
                                } else {

                                    $scope.showCannotPublish($itemScope.data);
                                }
                            }
                        }
                    ],
                    ['Create Flavor',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copyDatatype($itemScope.data);
                                });
                            } else {
                                CloneDeleteSvc.copyDatatype($itemScope.data);
                            }
                        }
                    ],
                    ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteDatatype($itemScope.data);

                        }
                    ]
                ];


                $scope.TableOptionsForPublished = [
                    ['Create New Version',
                        function($itemScope) {
                            console.log($rootScope.versionAndUseMap[$itemScope.table.id]);
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    if ($rootScope.readyForNewVersion($rootScope.versionAndUseMap[$itemScope.table.id])) {
                                        CloneDeleteSvc.upgradeTable($itemScope.table);
                                    } else {
                                        $scope.showCannotPublish($itemScope.table);
                                    }

                                });
                            } else {

                                if ($rootScope.readyForNewVersion($rootScope.versionAndUseMap[$itemScope.table.id])) {
                                    CloneDeleteSvc.upgradeTable($itemScope.table);
                                } else {

                                    $scope.showCannotPublish($itemScope.table);
                                }
                            }
                        }
                    ],

                    ['Export CSV',
                        function($itemScope) {
                            $scope.exportCSVForTable($itemScope.table);

                        }
                    ],


                    ['Create Flavor',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copyTable($itemScope.table);
                                });
                            } else {
                                CloneDeleteSvc.copyTable($itemScope.table);
                            }
                        }
                    ],
                    ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteValueSet($itemScope.table);

                        }
                    ]
                ];




                $scope.closeChildren = function(section) {
                    console.log("Calling close Children")
                    if (section.id === $rootScope.activeModel) {
                        console.log(section);
                        $scope.displayNullView();
                    } else {
                        angular.forEach(section.childSections, function(s) {

                            $scope.closeChildren(s);
                        });
                    }
                }



                $rootScope.readyForNewVersion = function(element) {
                    var ready = true;
                    var obj = $rootScope.versionAndUseMap[element.id];
                    if (obj.derived && obj.derived.length > 0) {
                        angular.forEach(obj.derived, function(derived) {

                            console.log(derived);
                            console.log($rootScope.datatypesMap[derived]);
                            if (element.type === 'datatype') {
                                if ($rootScope.datatypesMap[derived].status !== "PUBLISHED") {
                                    console.log("here");
                                    ready = false;
                                }
                            } else {
                                if (element.type === 'table') {
                                    if ($rootScope.tablesMap[derived].status !== "PUBLISHED") {
                                        console.log("here");
                                        ready = false;
                                    }
                                }
                            }
                        });
                    }
                    return ready;
                };

                $scope.ValueSetOptions = [

                    ['Create Flavor',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {
                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copyTable($itemScope.table);
                                });
                            } else {
                                CloneDeleteSvc.copyTable($itemScope.table);
                            }
                        }
                    ],
                    null, ['Export CSV',
                        function($itemScope) {
                            $scope.exportCSVForTable($itemScope.table);

                        }
                    ],
                    null, ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteValueSet($itemScope.table);

                        }
                    ]

                ];

                $scope.ValueSetOptionsINLIB = [

                    ['Create Flavor',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {
                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    console.log($scope.tableLibrary);
                                    console.log("table in lib");
                                    CloneDeleteSvc.copyTableINLIB($itemScope.table, $rootScope.tableLibrary);
                                });
                            } else {
                                console.log("table in lib");
                                CloneDeleteSvc.copyTableINLIB($itemScope.table, $rootScope.tableLibrary);
                            }
                        }
                    ],
                    null, ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteValueSet($itemScope.table);

                        }
                    ]

                ];


                $scope.MessagesOption = [

                    [
                        'Copy',
                        function($itemScope) {

                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.copyMessage($itemScope.msg);
                                });
                            } else {
                                CloneDeleteSvc.copyMessage($itemScope.msg);
                            }
                        }
                    ],
                    null, [
                        'Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteMessage($itemScope.msg);
                        }
                    ]
                ];


                $scope.MessagesRootOption = [

                    ['Add Profile', function($itemScope) {
                        if ($rootScope.hasChanges()) {

                            $rootScope.openConfirmLeaveDlg().result.then(function() {
                                $scope.hl7Versions('ctx');
                            });
                        } else {
                            $scope.hl7Versions('ctx');
                        }

                    }],
                    null, ['Export Profile', function($itemScope) {
                        $scope.selectMessagesForExport($rootScope.igdocument);

                    }]
                ];

                $scope.ProfileComponentsRootOption = [

                    ['Create Profile Component', function($itemScope) {
                        if ($rootScope.hasChanges()) {

                            $rootScope.openConfirmLeaveDlg().result.then(function() {
                                $scope.createProfileComponent();
                            });
                        } else {
                            $scope.createProfileComponent();
                        }

                    }]
                ];

                $scope.CompositeMessagesRootOption = [
                    ['Create Composite Profile', function($itemScope) {
                        if ($rootScope.hasChanges()) {

                            $rootScope.openConfirmLeaveDlg().result.then(function() {
                                $scope.createCompositeMessage();
                            });
                        } else {
                            $scope.createCompositeMessage();
                        }

                    }]
                ];

                $scope.ProfileComponentsOption = [

                    ['Delete', function($itemScope) {

                        PcService.getPc($itemScope.pc.id).then(function(profileComponent) {
                            console.log(profileComponent);
                            if (profileComponent.appliedTo === null || (profileComponent.appliedTo && profileComponent.appliedTo.length === 0)) {
                                if ($rootScope.hasChanges()) {
                                    $rootScope.openConfirmLeaveDlg().result.then(function() {
                                        $rootScope.deleteProfileComponent($rootScope.igdocument.profile.profileComponentLibrary.id, profileComponent);
                                    });
                                } else {
                                    $rootScope.deleteProfileComponent($rootScope.igdocument.profile.profileComponentLibrary.id, profileComponent);
                                }
                            } else {
                                //can't delete because it's used
                                console.log("notHEEEEEEEREEEE");
                                $rootScope.cantDeletePc(profileComponent);

                            }

                        });



                    }]
                ];
                $scope.compositeMessagesOption = [

                    ['Delete', function($itemScope) {
                        console.log($itemScope.cm);
                        if ($rootScope.hasChanges()) {
                            $rootScope.openConfirmLeaveDlg().result.then(function() {
                                $rootScope.deleteCompositeMessage($itemScope.cm);
                            });
                        } else {
                            $rootScope.deleteCompositeMessage($itemScope.cm);
                        }






                    }]
                ];


                $scope.ValueSetRootOptions = [
                    ['Add Value Sets', function($itemScope) {
                        if ($rootScope.hasChanges()) {

                            $rootScope.openConfirmLeaveDlg().result.then(function() {
                                $scope.addTable($rootScope.igdocument);
                            });
                        } else {
                            $scope.addTable($rootScope.igdocument);

                        }


                    }]
                ];

                $scope.DataTypeOptionsInLib = [
                    ['Copy',
                        function($itemScope) {

                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $scope.copyDatatype($itemScope.data);
                                });
                            } else {
                                $scope.copyDatatype($itemScope.data);

                            }
                        }
                    ],
                    null, ['Delete',
                        function($itemScope) {
                            console.log("delete=" + $itemScope);
                            console.log($itemScope.data);
                            $scope.deleteDatatype($itemScope.data);
                        }
                    ]
                ];

                $scope.addSegment = [
                    ['Add HL7 Segment',
                        function($itemScope) {

                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $scope.addSegments($rootScope.igdocument.profile.metaData.hl7Version);
                                });
                            } else {

                                $scope.addSegments($rootScope.igdocument.profile.metaData.hl7Version);
                            }

                        }
                    ]
                ];

                $scope.addDatatype = [
                    ['Add HL7 Data Type',
                        function($itemScope) {

                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $scope.addDatatypes($rootScope.igdocument.profile.metaData.hl7Version);

                                });
                            } else {
                                $scope.addDatatypes($rootScope.igdocument.profile.metaData.hl7Version);
                            }
                        }
                    ],
                    ['Add USER Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addDatatypesFromUserLib("2.1");



                                });

                            } else {

                                $rootScope.addDatatypesFromUserLib("2.1");

                            }
                        }
                    ],
                    ['Add Master Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addDatatypeFromMasterLib("2.1");



                                });

                            } else {

                                $rootScope.addDatatypeFromMasterLib("2.1");

                            }
                        }
                    ],
                    ['Add Shared Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addSharedDatatypes();



                                });

                            } else {

                                $rootScope.addSharedDatatypes();

                            }
                        }
                    ]
                ];

                $scope.MasterDataTypeLibraryOptions = [
                    [' Add Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $scope.addMasterDt();



                                });

                            } else {
                                $scope.addMasterDt();

                            }
                        }
                    ]
                ];

                $scope.UserDataTypeLibraryOptions = [
                    ['Add HL7 Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $scope.addDatatypeForUser("2.1");



                                });

                            } else {

                                $scope.addDatatypeForUser("2.1");

                            }
                        }
                    ],
                    ['Add USER Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addDatatypesFromUserLib("2.1");



                                });

                            } else {

                                $rootScope.addDatatypesFromUserLib("2.1");

                            }
                        }
                    ],
                    ['Add Master Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addDatatypeFromMasterLib("2.1");



                                });

                            } else {

                                $rootScope.addDatatypeFromMasterLib("2.1");

                            }
                        }
                    ],
                    ['Add Shared Datatypes',
                        function($itemScope) {
                            console.log("adding datatype");
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {

                                    $rootScope.addSharedDatatypes();



                                });

                            } else {

                                $rootScope.addSharedDatatypes();

                            }
                        }
                    ]








                ];


                $scope.addValueSets = [


                    ['Add New Value Set',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.createNewTable('USER', $rootScope.igdocument.profile.tableLibrary);

                                });
                            } else {

                                CloneDeleteSvc.createNewTable('USER', $rootScope.igdocument.profile.tableLibrary);

                            }

                        }
                    ],
                    ['Add HL7 Value Sets',

                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addHL7Table($rootScope.igdocument.profile.tableLibrary, $rootScope.igdocument.metaData.hl7Version);

                                });
                            } else {

                                $rootScope.addHL7Table($rootScope.igdocument.profile.tableLibrary, $rootScope.igdocument.metaData.hl7Version);

                            }

                        }
                    ],

                    ['Add from PHINVADs',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addPHINVADSTables($rootScope.igdocument.profile.tableLibrary);

                                });
                            } else {

                                $rootScope.addPHINVADSTables($rootScope.igdocument.profile.tableLibrary);

                            }
                        }
                    ],

                    ['Add from CSV file',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addCSVTables($rootScope.igdocument.profile.tableLibrary);

                                });
                            } else {

                                $rootScope.addCSVTables($rootScope.igdocument.profile.tableLibrary);

                            }

                        }
                    ]

                ];


                $scope.addValueSetsInTableLibrary = [

                    ['Import New Value Set',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    CloneDeleteSvc.createNewTable($rootScope.tableLibrary.scope, $scope.tableLibrary);
                                    $scope.editTableINLIB($rootScope.table);
                                });
                            } else {

                                CloneDeleteSvc.createNewTable($rootScope.tableLibrary.scope, $scope.tableLibrary);
                                $scope.editTableINLIB($rootScope.table);
                            }

                        }
                    ],
                    ['Import HL7 Value Sets',

                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addHL7Table($rootScope.tableLibrary, "2.1");
                                });
                            } else {

                                $rootScope.addHL7Table($rootScope.tableLibrary, "2.1");
                            }



                        }
                    ],

                    ['Import from PHINVADs',
                        function($itemScope) {


                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addPHINVADSTables($rootScope.tableLibrary);
                                });
                            } else {

                                $rootScope.addPHINVADSTables($rootScope.tableLibrary);
                            }
                        }
                    ],

                    ['Import CSV file',
                        function($itemScope) {
                            if ($rootScope.hasChanges()) {

                                $rootScope.openConfirmLeaveDlg().result.then(function() {
                                    $rootScope.addCSVTables($$rootScope.tableLibrary);
                                });
                            } else {

                                $rootScope.addCSVTables($rootScope.tableLibrary);
                            }
                        }
                    ]

                ];

                function processEditSeg(seg) {
                    $scope.Activate(seg.id);
                    // $rootScope.activeSegment = seg;
                    $scope.$emit('event:openSegment', seg);
                };

                $rootScope.editSeg = function(seg) {

                    if ($rootScope.hasChanges()) {

                        $rootScope.openConfirmLeaveDlg().result.then(function() {

                            processEditSeg(seg);
                        });
                    } else {
                        processEditSeg(seg);
                    }


                };

                function processEditIg(ig) {
                    $scope.Activate(ig.id);
                    $rootScope.igdocument = ig;
                    $scope.$emit('event:openDocumentMetadata',
                        $rootScope.igdocument);
                };


                $scope.editIg = function(ig) {

                    if ($rootScope.hasChanges()) {

                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditIg(ig);
                        });
                    } else {
                        processEditIg(ig);
                    }


                };

                function processEditSection(section) {
                    $scope.Activate(section.id);
                    $rootScope.section = section;
                    $scope.$emit('event:openSection', $rootScope.section);
                };


                $scope.editSection = function(section) {
                    if (section.sectionContents === null) {
                        section.sectionContents = "";
                    }
                    if ($rootScope.hasChanges()) {

                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditSection(section);
                        });
                    } else {
                        processEditSection(section);
                    }

                }

                function processEditRoutSection(param) {

                    $scope.Activate(param.id);
                    $rootScope.section = $scope.getRoutSectionByname(param);
                    // $rootScope.currentData=section;

                    if ($rootScope.section.sectionContents === null) {
                        $rootScope.section.sectionContents = "";
                    }
                    $scope.$emit('event:openSection', $rootScope.section);
                };

                $scope.editRoutSection = function(param) {
                    if ($rootScope.hasChanges()) {

                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditRoutSection(param);
                        });
                    } else {
                        processEditRoutSection(param);
                    }
                };

                $scope.getRoutSectionByname = function(name) {
                    $rootScope.currentData = {};
                    $scope.Activate(name);
                    if (name.toLowerCase() === 'conformance profiles') {
                        $rootScope.currentData = $rootScope.igdocument.profile.messages;

                    } else if (name.toLowerCase() === 'segments and field descriptions') {
                        $rootScope.currentData = $rootScope.igdocument.profile.segmentLibrary;

                    } else if (name.toLowerCase() === 'value sets') {
                        $rootScope.currentData = $rootScope.igdocument.profile.tableLibrary;
                    } else if (name.toLowerCase() === 'datatypes') {
                        $rootScope.currentData = $rootScope.igdocument.profile.datatypeLibrary;
                    }
                    if ($rootScope.currentData.sectionContents === null || $rootScope.currentData.sectionContents === undefined) {
                        $rootScope.currentData.sectionContents = "";
                    }
                    return $rootScope.currentData;
                };

                function processEditDataType(data) {

                    console.log("dialog not opened");
                    $scope.Activate(data.id);
                    $rootScope.datatype = data;
                    $scope.$emit('event:openDatatype', $rootScope.datatype);
                };

                $rootScope.editDatatype = function(data) {
                    console.log(data);
                    // Find share participants
                    if (data.shareParticipantIds && data.shareParticipantIds.length > 0) {

                        console.log(data.shareParticipantIds);
                        var listOfIds = _.map(data.shareParticipantIds, function(element) {
                            if (element.id) {
                                return element.id;
                            } else if (element.accountId) {
                                return element.accountId;
                            }



                        });
                        console.log(listOfIds);
                        $http.get('api/shareparticipants', { params: { ids: listOfIds } })
                            .then(
                                function(response) {
                                    data.shareParticipantIds = angular.fromJson(response.data);
                                    //Proceed with next
                                    editDatatypeNext(data);
                                },
                                function(error) {
                                    console.log(error);
                                }
                            );

                    } else {
                        editDatatypeNext(data);
                    }

                };

                function editDatatypeNext(data) {
                    if ($rootScope.hasChanges()) {
                        console.log("found changes");

                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            console.log("dialog opened");
                            processEditDataType(data);
                        });
                    } else {
                        processEditDataType(data);
                    }
                };

                function processEditTable(table) {
                    $scope.Activate(table.id);
                    $rootScope.table = table;
                    $scope.$emit('event:openTable', $rootScope.table);
                };

                $rootScope.editTable = function(table) {
                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditTable(table);
                        });
                    } else {
                        processEditTable(table);
                    }
                };


                function processEditTableInLib(table) {
                    $scope.Activate(table.id);
                    $rootScope.table = table;
                    $scope.$emit('event:openTable', $rootScope.table);
                };

                $scope.editTableINLIB = function(table) {
                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditTableInLib(table);
                        });
                    } else {
                        processEditTableInLib(table);
                    }
                };



                function processEditMessage(message) {
                    $scope.Activate(message.id);
                    $rootScope.message = message;
                    console.log("three");
                    console.log(message);
                    $scope.$emit('event:openMessage', message);
                };


                $scope.editMessage = function(message) {

                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditMessage(message);
                        });
                    } else {
                        processEditMessage(message);
                    }

                };

                function processEditPC(pc) {
                    console.log("================================");
                    console.log(pc);
                    PcService.getPc(pc.id).then(function(profileC) {
                        console.log("HEEEERE");
                        console.log(profileC);
                        $scope.Activate(pc.id);
                        $rootScope.profileComponent = profileC;
                        $scope.$emit('event:openPc');
                    });

                };

                function processEditCM(cm) {
                    console.log("================================");
                    console.log(cm);
                    CompositeMessageService.getCm(cm.id).then(function(compositeM) {
                        console.log("HEEEERE");
                        console.log(compositeM);
                        $scope.Activate(cm.id);
                        $rootScope.compositeMessage = compositeM;
                        $scope.$emit('event:openCm');
                    });

                };
                $rootScope.editPC = function(pc) {

                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditPC(pc);
                        });
                    } else {
                        processEditPC(pc);
                    }

                };
                $rootScope.editCM = function(cm) {

                    if ($rootScope.hasChanges()) {
                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditCM(cm);
                        });
                    } else {
                        processEditCM(cm);
                    }

                };


                function processEditProfile() {
                    $scope.Activate("Message Infrastructure");
                    $scope.$emit('event:openProfileMetadata',
                        $rootScope.igdocument);
                };

                $scope.editProfile = function() {

                    if ($rootScope.hasChanges()) {

                        $rootScope.openConfirmLeaveDlg().result.then(function() {
                            processEditProfile();
                        });
                    } else {
                        processEditProfile();
                    }

                };

                $scope.updateChildeSections = function(childSections) {

                    var id = $rootScope.igdocument.id;
                    var req = {
                        method: 'POST',
                        url: "api/igdocuments/" + id + "/updateChildSections",
                        headers: {
                            'Content-Type': "application/json"
                        },
                        data: childSections
                    }

                    var promise = $http(req)
                        .success(function(data, status, headers, config) {
                            // //console.log(data);
                            return data;
                        })
                        .error(function(data, status, headers, config) {
                            if (status === 404) {
                                console.log("Could not reach the server");
                            } else if (status === 403) {
                                console.log("limited access");
                            }
                        });
                    return promise;
                }

                $scope.reOrderChildSections = function() {
                    var childSections = $rootScope.igdocument.childSections;
                    var sections = [];
                    for (var i = 0; i <= childSections.length - 1; i++) {
                        var sectionMap = {};
                        sectionMap.id = childSections[i].id;
                        sectionMap.sectionPosition = childSections[i].sectionPosition;
                        sections.push(sectionMap);
                    }
                    var id = $rootScope.igdocument.id;
                    var req = {
                        method: 'POST',
                        url: "api/igdocuments/" + id + "/reorderChildSections",
                        headers: {
                            'Content-Type': "application/json"
                        },
                        data: sections
                    }

                    var childSections = $rootScope.igdocument.childSections;
                    var sections = [];
                    for (var i = 0; i <= childSections.length - 1; i++) {
                        var sectionMap = {};
                        sectionMap.id = childSections[i].id;
                        sectionMap.sectionPosition = childSections[i].sectionPosition;
                        sections.push(sectionMap);
                    }
                    var id = $rootScope.igdocument.id;
                    var req = {
                        method: 'POST',
                        url: "api/igdocuments/" + id + "/reorderChildSections",
                        headers: {
                            'Content-Type': "application/json"
                        },
                        data: sections
                    }


                    var promise = $http(req)
                        .success(function(data, status, headers, config) {

                            return data;
                        })
                        .error(function(data, status, headers, config) {
                            if (status === 404) {
                                console.log("Could not reach the server");
                            } else if (status === 403) {
                                console.log("limited access");
                            }
                        });
                    return promise;
                };

                $scope.reOrderMessages = function() {
                    var messagesMap = [];

                    var messages = $rootScope.igdocument.profile.messages.children;
                    for (var i = 0; i <= messages.length - 1; i++) {
                        var messageMap = {};
                        messageMap.id = messages[i].id;
                        messageMap.position = messages[i].position;
                        messagesMap.push(messageMap);
                    }
                    console.log(messagesMap);
                    var id = $rootScope.igdocument.id;
                    var req = {
                        method: 'POST',
                        url: "api/igdocuments/" + id + "/reorderMessages",
                        headers: {
                            'Content-Type': "application/json"
                        },
                        data: messagesMap
                    }
                    var promise = $http(req)
                        .success(function(data, status, headers, config) {

                            return data;
                        })
                        .error(function(data, status, headers, config) {
                            if (status === 404) {
                                console.log("Could not reach the server");
                            } else if (status === 403) {
                                console.log("limited access");
                            }
                        });
                    return promise;
                };

                $scope.showUnused = function(node) {
                    if (node.id === null) {
                        return true;
                    }
                    return FilteringSvc.isUnused(node);
                };

                $scope.showToC = function(leaf) {
                    return leaf.id === null || FilteringSvc.isUnused(leaf) || FilteringSvc.showToC(leaf);
                };

                $scope.getScopeLabel = function(leaf) {
                    if (leaf) {


                        if (leaf.scope === 'HL7STANDARD') {
                            return 'HL7';
                        } else if (leaf.scope === 'USER') {
                            return 'USR';

                        } else if (leaf.scope === 'MASTER') {
                            return 'MAS';
                        } else if (leaf.scope === 'PRELOADED') {
                            return 'PRL';
                        } else if (leaf.scope === 'PHINVADS') {
                            return 'PVS';
                        } else {
                            return "";

                        }
                    }
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

                $rootScope.getLabelOfData = function(name, ext) {

                    var label = "";


                    if (ext && ext !== null && ext !== "") {
                        label = name + "_" + ext;

                    } else {
                        label = name;
                    }
                    return label;
                };

                $scope.getDatatypeLabel = function(datatype) {
                    if (datatype && datatype != null) {
                        // var ext = $rootScope.getDatatypeExtension(datatype);
                        return $rootScope.getLabel(datatype.name, datatype.ext);
                    }
                    return "";
                };


                $scope.getSegmentsFromgroup = function(group) {
                    // _.union($rootScope.selectedSegments,temp);
                    for (var i = 0; i < group.children.length; i++) {
                        if (group.children[i].type === "segmentRef") {
                            console.log("IN IF ");
                            var segment = $rootScope.segmentsMap[group.children[i].ref.id];
                            var temp2 = [];
                            temp2.push(segment);
                            $rootScope.FilteredSegments.push(segment);
                            // $rootScope.FilteredSegments=_.union($rootScope.FilteredSegments,temp2);


                        } else if (group.children[i].type === "group") {
                            console.log("group case ");
                            $scope.getSegmentsFromgroup(group.children[i]);
                        }
                    }

                }


                $scope.getDatatypeFromDatatype = function(datatype) {
                    var data = [];
                    if (datatype.components.length === 0) {
                        $scope.getTablesFromDatatype(datatype);
                        return 0;
                    } else {

                        for (var i = 0; i < datatype.components.length; i++) {
                            var temp = [];
                            temp.push($rootScope.datatypesMap[datatype.components[i].datatype.id]);
                            $scope.getTablesFromDatatype($rootScope.datatypesMap[datatype.components[i].datatype.id]);
                            data = _.union(data, temp);
                        }
                    }
                    return data;
                }

                $scope.FilterbyConformance = function(msg) {

                    $rootScope.FilteredSegments = [];
                    $rootScope.filteredDatatypes = [];
                    $rootScope.filterdTables = [];
                    for (var i = msg.children.length - 1; i >= 0; i--) {


                        if (msg.children[i].type === "segmentRef") {
                            var seg = $rootScope.segmentsMap[msg.children[i].ref.id];
                            console.log(seg);
                            $rootScope.FilteredSegments.push(seg);
                            var temp = [];
                            temp.push(seg);
                            $rootScope.filteredDatatype = _.union($rootScope.selectedDataTypes, $scope.getDatataypeFromSegment(segment));
                            $scope.getTablesFromSegment(segment);

                        } else if (msg.children[i].type === "group") {
                            $scope.getSegmentsFromgroup(msg.children[i]);
                        }
                    }


                }

                $scope.getDatataypeFromSegment = function(seg) {
                    var data = [];
                    for (var i = 0; i < seg.fields.length; i++) {
                        console.log(seg.fields[i].datatype.id);
                        var datatype = $rootScope.datatypesMap[seg.fields[i].datatype.id];
                        console.log(datatype);
                        $scope.getTablesFromDatatype(datatype);
                        var temp = [];
                        temp.push(datatype);
                        temp = _.union(temp, $scope.getDatatypeFromDatatype(datatype));
                        data = _.union(data, temp);
                    }
                    return data;
                }

                $scope.getTablesFromSegment = function(seg) {
                    var tables = [];
                    for (var i = 0; i < seg.fields.length; i++) {
                        if (seg.fields[i].table != null) {
                            var table = $rootScope.tablesMap[seg.fields[i].table.id];
                            // console.log(datatype);
                            var temp = [];
                            temp.push(table);
                            tables = _.union(tables, temp);
                            $rootScope.filtererTables = _.union($rootScope.selectedTables, tables);
                        }
                    }

                }
                $scope.resetSegments = function() {
                    $rootScope.selectedSegment = null;
                    console.log("called");
                    $rootScope.filteredDatatypesList = angular.copy($rootScope.datatypes);
                    $rootScope.filteredTablesList = angular.copy($rootScope.tables);
                    if ($rootScope.selectedMessage != null) {
                        $rootScope.processMessageTree($rootScope.selectedMessage, null);
                    }
                    $rootScope.filteredSegmentsList.forEach(function(segment, i) {
                        segment.checked = false;
                    });
                }

                $scope.resetLibFilter = function() {
                    console.log("called");
                    $rootScope.filteringModeON = false;

                    $scope.datatypes.forEach(function(data, i) {

                        data.checked = false;
                    });
                    $scope.loadTables().then(function() {});
                };

                $scope.resetDatatypes = function() {
                    console.log("called");
                    if ($rootScope.selectedSegment != null) {
                        $rootScope.processSegmentsTree($rootScope.selectedSegment, null);
                    } else if ($rootScope.selectedMessage != null) {
                        $rootScope.processMessageTree($rootScope.selectedMessage, null);
                    }
                    $rootScope.filteredDatatypesList.forEach(function(data, i) {
                        data.checked = false;
                    });
                }


                $scope.resetMessages = function() {
                    $rootScope.selectedMessage = null;
                    $rootScope.filteredDatatypesList = angular.copy($rootScope.datatypes);
                    $rootScope.filteredSegmentsList = angular.copy($rootScope.segments);
                    $rootScope.filteredTablesList = angular.copy($rootScope.tables);

                    $rootScope.igdocument.profile.messages.children.forEach(function(msg, i) {
                        msg.checked = false;
                    });
                }

                $scope.showCannotPublish = function(datatype) {

                    $scope.unpublishedChild = [];
                    angular.forEach($rootScope.versionAndUseMap[datatype.id].derived, function(derived) {
                        if ($rootScope.datatypesMap[derived].status == "UNPUBLISHED") {
                            $scope.unpublishedChild.push($rootScope.datatypesMap[derived]);

                        }

                    });
                    var addDatatypeInstance = $modal.open({
                        templateUrl: 'cannotPublish.html',
                        controller: 'cannotPublish',
                        size: 'lg',
                        windowClass: 'conformance-profiles-modal',
                        resolve: {

                            datatype: function() {

                                return datatype;
                            },
                            derived: function() {


                                return $scope.unpublishedChild;
                            }

                        }
                    }).result.then(function(results) {

                    });
                };
                $rootScope.addDatatypesFromUserLib = function() {
                    var scopes = ['HL7STANDARD'];


                    DatatypeLibrarySvc.getDataTypeLibraryByScope('USER').then(function(userDtLib) {
                        var dtlibs = [];
                        angular.forEach(userDtLib, function(dtLib) {
                            if (dtLib.id !== $rootScope.datatypeLibrary.id) {
                                dtlibs.push(dtLib);
                            }
                        });
                        console.log(dtlibs);
                        console.log("userDtLib");
                        console.log(userDtLib);

                        console.log("addDatatype scopes=" + scopes.length);
                        var addDatatypeInstance = $modal.open({
                            templateUrl: 'AddUserDatatype.html',
                            controller: 'AddDatatypeCtrlFromUserLib',
                            size: 'lg',
                            windowClass: 'addDatatype',
                            resolve: {
                                hl7Version: function() {
                                    return $scope.hl7Version;
                                },
                                userDtLib: function() {
                                    return dtlibs;
                                },
                                datatypeLibrary: function() {
                                    return $rootScope.datatypeLibrary;
                                },
                                tableLibrary: function() {
                                    return $rootScope.tableLibrary;
                                },
                                versionAndUseMap: function() {
                                    return $rootScope.versionAndUseMap;
                                }
                            }
                        }).result.then(function(results) {
                            var ids = [];
                            angular.forEach(results, function(result) {
                                ids.push(result.id);
                            });
                        });

                    });

                }

                $rootScope.addSharedDatatypes = function() {
                    var scopes = ['HL7STANDARD'];


                    var addDatatypeInstance = $modal.open({
                        templateUrl: 'AddSharedDatatype.html',
                        controller: 'AddSharedDatatype',
                        size: 'lg',
                        windowClass: 'addDatatype',
                        resolve: {
                            hl7Version: function() {
                                return $scope.hl7Version;
                            },

                            datatypeLibrary: function() {
                                return $rootScope.datatypeLibrary;
                            },
                            tableLibrary: function() {
                                return $rootScope.tableLibrary;
                            },
                            versionAndUseMap: function() {
                                return $rootScope.versionAndUseMap;
                            }
                        }
                    }).result.then(function(results) {


                    });

                }

                $rootScope.addDatatypeFromMasterLib = function() {

                    DatatypeLibrarySvc.getDataTypeLibraryByScope('MASTER').then(function(masterLib) {
                        var dtlibs = [];
                        angular.forEach(masterLib, function(dtLib) {
                            if (dtLib.id !== $rootScope.datatypeLibrary.id) {
                                dtlibs.push(dtLib);
                            }
                        });

                        var addDatatypeInstance = $modal.open({
                            templateUrl: 'AddMasterDatatypeIntoUser.html',
                            controller: 'addMAsterInLibrary',
                            size: 'lg',
                            windowClass: 'addDatatype',
                            resolve: {
                                hl7Version: function() {
                                    return $scope.hl7Version;
                                },
                                masterLib: function() {
                                    return dtlibs;
                                },
                                datatypeLibrary: function() {
                                    return $rootScope.datatypeLibrary;
                                },
                                tableLibrary: function() {
                                    return $rootScope.tableLibrary;
                                },
                                versionAndUseMap: function() {
                                    return $rootScope.versionAndUseMap;
                                }
                            }
                        }).result.then(function(results) {
                            var ids = [];
                            angular.forEach(results, function(result) {
                                ids.push(result.id);
                            });
                        });

                    });

                }
            }
        ]);

angular.module('igl').controller('AddDatatypeCtrlFromUserLib',
    function($scope, $rootScope, $modalInstance, hl7Version, userDtLib, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http, datatypeLibrary, tableLibrary, versionAndUseMap) {
        $scope.versionAndUseMap = versionAndUseMap;
        $scope.newDts = [];
        $scope.checkedExt = true;
        $scope.NocheckedExt = true;
        $scope.userDtLib = userDtLib;
        $scope.selectedDatatypes = [];
        $scope.selectUserDtLib = function(usrLib) {
            console.log(usrLib);
            DatatypeLibrarySvc.getDatatypesByLibrary(usrLib.id).then(function(datatypes) {

                $scope.userDatatypes = datatypes;

                console.log($scope.userDatatypes);
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
                $scope.hl7Datatypes = [];
                $scope.version1 = "";
                $scope.versions = versions;
                var scopes = ['HL7STANDARD'];
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

            });
        }
        $scope.addDt = function(datatype) {
            console.log(datatype);
            $scope.selectedDatatypes.push(datatype);
            console.log("chowing map");

            console.log($rootScope.versionAndUseMap[datatype.id]);
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
            $scope.newDatatype = angular.copy(datatype);

            $scope.newDatatype.ext = Math.floor(Math.random() * 1000);

            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";
            $scope.newDatatype.publicationVersion=0;
            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();
            $scope.newDatatype.libIds = [];
            $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";
            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();;
            $scope.newDatatype.libIds = [];
            if ($scope.newDatatype.components != undefined && $scope.newDatatype.components != null && $scope.newDatatype.components.length != 0) {
                for (var i = 0; i < $scope.newDatatype.components.length; i++) {
                    $scope.newDatatype.components[i].id = new ObjectId().toString();
                }
            }

            var predicates = $scope.newDatatype['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function(predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }

            var conformanceStatements = $scope.newDatatype['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function(conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }
            //  $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
        }
        $scope.deleteDt = function(datatype) {
            var index = $scope.selectedDatatypes.indexOf(datatype);
            if (index > -1) $scope.selectedDatatypes.splice(index, 1);
        };
        var secretEmptyKey = '[$empty$]';

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
                if ($scope.selectedDatatypes[i].scope === datatypeLibrary.scope) {
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
                DatatypeLibrarySvc.addChildren(datatypeLibrary.id, newLinks).then(function(link) {
                    for (var i = 0; i < newLinks.length; i++) {
                        datatypeLibrary.children.splice(0, 0, newLinks[i]);
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
                                // $rootScope.getDerived(datatypes[j]);
                            }
                        }

                        var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                            return num.id;
                        });
                        console.log("$rootScope.usedVsLink");

                        console.log($rootScope.usedVsLink);
                        var newTablesLink = _.difference($rootScope.usedVsLink, tableLibrary.children);
                        console.log(newTablesLink);

                        TableLibrarySvc.addChildren(tableLibrary.id, newTablesLink).then(function() {
                            tableLibrary.children = _.union(newTablesLink, tableLibrary.children);

                            TableService.get(usedVsId).then(function(tables) {
                                for (var j = 0; j < tables.length; j++) {
                                    if (!$rootScope.tablesMap[tables[j].id]) {
                                        $rootScope.tablesMap[tables[j].id] = tables[j];
                                        $rootScope.tables.push(tables[j]);

                                    }
                                }


                                $modalInstance.close(datatypes);

                            });
                        });
                    });
                    $rootScope.msg().text = "datatypeAdded";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;

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


angular.module('igl').controller('addMAsterInLibrary',
    function($scope, $rootScope, $modalInstance, hl7Version, masterLib, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http, datatypeLibrary, tableLibrary, versionAndUseMap) {
        $scope.versionAndUseMap = versionAndUseMap;
        $scope.newDts = [];
        $scope.checkedExt = true;
        $scope.NocheckedExt = true;
        $scope.masterLib = [];
        $scope.masterLib = masterLib;
        $scope.selectedDatatypes = [];
        $scope.selectMasterDtLib = function(masLib) {
            console.log(masLib);
            DatatypeLibrarySvc.getDatatypesByLibrary(masLib.id).then(function(datatypes) {
                console.log(datatypes);

                $scope.masterDatatypes = _.where(datatypes, { scope: "MASTER", status: "PUBLISHED" });
                // if($rootScope.igdocument){
                //     var temp=[];
                //     angular.forEach($scope.masterDatatypes, function(datatype){
                //         if($scope.containsVersion(datatype.hl7versions, $rootScope.igdocument.profile.hl7Version)){
                //             temp.push(datatype);
                //         }
                //     });
                //     $scope.masterDatatypes=temp;
                // }
                console.log($scope.masterDatatypes);
            });
        };
        $scope.containsVersion=function(versions, v){
            angular.forEach(versions, function(version){
                if(v===version){
                    return true;
                }
            });
            return false;
        }
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
                $scope.hl7Datatypes = [];
                $scope.version1 = "";
                $scope.versions = versions;
                var scopes = ['HL7STANDARD'];
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
            });
        }
        $scope.addDt = function(datatype) {
            console.log(datatype);
            $scope.selectedDatatypes.push(datatype);
            console.log("chowing map");

            console.log($rootScope.versionAndUseMap[datatype.id]);
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

            $scope.newDatatype = angular.copy(datatype);
            $scope.newDatatype.publicationVersion=0;

            if ($rootScope.igdocument) {

                console.log("merging");
                //newDatatype.hl7versions=[$rootScope.igdocument.profile.metaData.hl7Version];
                var temp = [];
                temp.push($rootScope.igdocument.profile.metaData.hl7Version);
                $scope.newDatatype.hl7versions = temp;
                DatatypeService.getOneStandard(datatype.name, $scope.newDatatype.hl7Version, $scope.newDatatype.hl7versions).then(function(standard) {
                    $rootScope.mergeEmptyProperty($scope.newDatatype, standard);
                    console.log("MERGING");
                    $scope.newDatatype.hl7Version = $rootScope.igdocument.profile.metaData.hl7Version;
                    console.log($scope.newDatatype);
                });
            }

            if ($rootScope.libraryDoc) {
                console.log("HERRRRE");
                var temp = [];
                temp.push(datatype.hl7Version);
                $scope.newDatatype.hl7versions = temp;
                $scope.newDatatype.hl7Version = datatype.hl7Version;
                DatatypeService.getOneStandard(datatype.name, datatype.hl7Version, datatype.hl7versions).then(function(standard) {
                    $rootScope.mergeEmptyProperty($scope.newDatatype, standard);
                    console.log("MERGING");
                });

            }


            $scope.newDatatype.ext = Math.floor(Math.random() * 1000);

            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";

            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();
            $scope.newDatatype.libIds = [];
            $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";
            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();
            $scope.newDatatype.libIds = [];
            if ($scope.newDatatype.components != undefined && $scope.newDatatype.components != null && $scope.newDatatype.components.length != 0) {
                for (var i = 0; i < $scope.newDatatype.components.length; i++) {
                    $scope.newDatatype.components[i].id = new ObjectId().toString();
                }
            }

            var predicates = $scope.newDatatype['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function(predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }

            var conformanceStatements = $scope.newDatatype['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function(conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }
            //  $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
        }
        $scope.deleteDt = function(datatype) {
            var index = $scope.selectedDatatypes.indexOf(datatype);
            if (index > -1) $scope.selectedDatatypes.splice(index, 1);
        };
        var secretEmptyKey = '[$empty$]'
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
                if ($scope.selectedDatatypes[i].scope === datatypeLibrary.scope) {
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
                DatatypeLibrarySvc.addChildren(datatypeLibrary.id, newLinks).then(function(link) {
                    for (var i = 0; i < newLinks.length; i++) {
                        datatypeLibrary.children.splice(0, 0, newLinks[i]);
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
                                //$rootScope.getDerived(datatypes[j]);
                            }
                        }

                        var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                            return num.id;
                        });
                        console.log("$rootScope.usedVsLink");

                        console.log($rootScope.usedVsLink);
                        var newTablesLink = _.difference($rootScope.usedVsLink, tableLibrary.children);
                        console.log(newTablesLink);

                        TableLibrarySvc.addChildren(tableLibrary.id, newTablesLink).then(function() {
                            tableLibrary.children = _.union(newTablesLink, tableLibrary.children);

                            TableService.get(usedVsId).then(function(tables) {
                                for (var j = 0; j < tables.length; j++) {
                                    if (!$rootScope.tablesMap[tables[j].id]) {
                                        $rootScope.tablesMap[tables[j].id] = tables[j];
                                        $rootScope.tables.push(tables[j]);

                                    }
                                }
                                $modalInstance.close(datatypes);
                            });

                        });
                    });
                    $rootScope.msg().text = "datatypeAdded";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;
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

angular.module('igl').controller('cannotPublish', function($scope, $rootScope, $http, $modalInstance, datatype, derived) {

    $scope.datatypeTo = datatype;
    $scope.delete = function() {
        $modalInstance.close($scope.datatypeTo);
    };

    $scope.cancel = function() {
        console.log("sssss");
        $modalInstance.dismiss('cancel');
    };
});
angular.module('igl').controller('ConfirmTablePublishCtl', function($scope, $rootScope, $http, $modalInstance, tableToPublish) {

    $scope.tableToPublish = tableToPublish;
    $scope.loading = false;

    $scope.confirm = function() {
        console.log("confirming")

        $modalInstance.close($scope.tableToPublish);
    };

    $scope.cancel = function() {
        $scope.tableToPublish.status = "UNPUBLISHED";
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('AddSharedDatatype',
    function($scope, $rootScope, $modalInstance, hl7Version, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http, datatypeLibrary, tableLibrary, versionAndUseMap) {
        $scope.versionAndUseMap = versionAndUseMap;
        $scope.newDts = [];
        $scope.checkedExt = true;
        $scope.NocheckedExt = true;
        $scope.selectedDatatypes = [];


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
                $scope.hl7Datatypes = [];
                $scope.version1 = "";
                $scope.versions = versions;
                DatatypeService.getSharedDatatypes().then(function(result) {
                    console.log("result");
                    console.log(result);

                    $scope.hl7Datatypes = result;

                });
                var scopes = ['HL7STANDARD'];
            });

        };
        init();
        $scope.setVersion = function(version) {
            $scope.version1 = version;
        }
        $scope.addDt = function(datatype) {
            console.log(datatype);
            $scope.selectedDatatypes.push(datatype);
            console.log("chowing map");

            console.log($rootScope.versionAndUseMap[datatype.id]);
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
            $scope.newDatatype = angular.copy(datatype);
            $scope.newDatatype.publicationVersion=0;


            $scope.newDatatype.ext = Math.floor(Math.random() * 1000);

            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";

            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();
            $scope.newDatatype.libIds = [];
            $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
            console.log($scope.newDatatype.ext);
            $scope.newDatatype.scope = datatypeLibrary.scope;
            $scope.newDatatype.status = "UNPUBLISHED";
            $scope.newDatatype.participants = [];
            $scope.newDatatype.id = new ObjectId().toString();;
            $scope.newDatatype.libIds = [];
            if ($scope.newDatatype.components != undefined && $scope.newDatatype.components != null && $scope.newDatatype.components.length != 0) {
                for (var i = 0; i < $scope.newDatatype.components.length; i++) {
                    $scope.newDatatype.components[i].id = new ObjectId().toString();
                }
            }

            var predicates = $scope.newDatatype['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function(predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }

            var conformanceStatements = $scope.newDatatype['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function(conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }
            //  $scope.selectedDatatypes.push($scope.newDatatype);
            console.log($scope.selectedDatatypes)
        }
        $scope.deleteDt = function(datatype) {
            var index = $scope.selectedDatatypes.indexOf(datatype);
            if (index > -1) $scope.selectedDatatypes.splice(index, 1);
        };
        var secretEmptyKey = '[$empty$]';

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
                if ($scope.selectedDatatypes[i].scope === datatypeLibrary.scope) {
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
                DatatypeLibrarySvc.addChildren(datatypeLibrary.id, newLinks).then(function(link) {
                    for (var i = 0; i < newLinks.length; i++) {
                        datatypeLibrary.children.splice(0, 0, newLinks[i]);
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
                                // $rootScope.getDerived(datatypes[j]);
                            }
                        }

                        var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                            return num.id;
                        });
                        console.log("$rootScope.usedVsLink");

                        console.log($rootScope.usedVsLink);
                        var newTablesLink = _.difference($rootScope.usedVsLink, tableLibrary.children);
                        console.log(newTablesLink);

                        TableLibrarySvc.addChildren(tableLibrary.id, newTablesLink).then(function() {
                            tableLibrary.children = _.union(newTablesLink, tableLibrary.children);

                            TableService.get(usedVsId).then(function(tables) {
                                for (var j = 0; j < tables.length; j++) {
                                    if (!$rootScope.tablesMap[tables[j].id]) {
                                        $rootScope.tablesMap[tables[j].id] = tables[j];
                                        $rootScope.tables.push(tables[j]);

                                    }
                                }


                                $modalInstance.close(datatypes);

                            });
                        });
                    });
                    $rootScope.msg().text = "datatypeAdded";
                    $rootScope.msg().type = "success";
                    $rootScope.msg().show = true;

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