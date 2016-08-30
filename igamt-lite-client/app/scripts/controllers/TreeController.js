angular
    .module('igl')
    .controller(
        'TreeCtrl', ['$scope',
            '$rootScope',
            '$http',
            'SectionSvc',
            'CloneDeleteSvc',
            'FilteringSvc',
            'SectionSvc',

            function($scope, $rootScope, $http, SectionSvc, CloneDeleteSvc, FilteringSvc, SectionSvc) {

                $scope.collapsedata = false;
                $scope.collapsePcs=true;
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

                $rootScope.switcherDatatypeLibrary = function() {

                    $scope.dataTypeLibraryCollapsed = !$scope.dataTypeLibraryCollapsed;

                };
                $rootScope.openMetadata = function() {

                    $scope.openMetadata = !$scope.openMetadata;

                };

                $rootScope.switcherprofile = function() {
                    $scope.profilecollapsed = !$scope.profilecollapsed;

                };
                $rootScope.switcherpcs=function(){
                      $scope.collapsePcs=!$scope.collapsePcs;
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
                        //event.source.nodeScope.$modelValue.sectionPosition = sortAfter + 1;

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
                                    return "";
                                });
                                SectionSvc.update($rootScope.igdocument.id, parentDest).then(function() {
                                    return "";
                                });
                                return "";
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

                $scope.cloneSection=function(section){

                    var clone= angular.copy(section);
                    clone.id=new ObjectId().toString();
                    clone.childSections=[];
                    clone.sectionTitle = section.sectionTitle + Math.floor((Math.random() * 50000) + 1);
                    if(section.childSections&& section.childSections.length>0){
                        angular.forEach(section.childSections,function(sect){
                            clone.childSections.push($scope.cloneInside(sect));

                        });
                    }
                    return clone;


                };

                $scope.cloneInside= function(sectionInside){
                    var clone= angular.copy(sectionInside);
                    clone.id=new ObjectId().toString();
                    clone.childSections=[];
                    if(sectionInside.childSections&& sectionInside.childSections.length>0){

                        angular.forEach(sectionInside.childSections,function(sect){
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

                    ['add Section',
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
                                    SectionSvc.save($rootScope.igdocument.id, cloneModel);

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

                            var section = $itemScope.section;
                            var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue.indexOf($itemScope.$nodeScope.$modelValue);
                            if (index > -1) {
                                $itemScope.$nodeScope.$parentNodesScope.$modelValue
                                    .splice(index, 1);
                            }
                            $scope.updatePositions($itemScope.$nodeScope.$parentNodesScope.$modelValue);


                            SectionSvc.delete($rootScope.igdocument.id, $itemScope.section.id).then(function() {
                                if ($itemScope.section.id === $rootScope.activeModel) {
                                    $scope.displayNullView();
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

                    ['add Section',
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

                    ['Create a Flavor',
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
                    null, ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteSegment($itemScope.segment);
                        }
                    ]

                ];

                $scope.DataTypeOptions = [

                    ['Create a Flavor',
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

                $scope.ValueSetOptions = [

                    ['Create a Flavor',
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
                    null, ['Delete',
                        function($itemScope) {
                            CloneDeleteSvc.deleteValueSet($itemScope.table);

                        }
                    ]

                ];

                $scope.ValueSetOptionsINLIB = [


                                          ['Copy',
                                              function($itemScope) {
                                  
                                                      $scope.copyTableINLIB($itemScope.table,$scope.tableLibrary.id);
                                                  
                                              }
                                          ],
                                          null, ['Delete',
                                              function($itemScope) {
                                                 $scope.deleteValueSetINLIB($itemScope.table);

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

                    ['Add', function($itemScope) {
                        $scope.hl7Versions('ctx');
                        //$scope.addMessageToIg($rootScope.igdocument.profile.metaData.hl7Version);
                    }],
                    null, ['Export', function($itemScope) {
                        $scope.selectMessages($rootScope.igdocument);
                    }]
                ];

                $scope.ValueSetRootOptions = [
                    ['Add Value Sets', function($itemScope) {
                        $scope.addTable($rootScope.igdocument);
                    }]
                ];

                $scope.DataTypeOptionsInLib = [
                    ['Create a Copy',
                        function($itemScope) {
                            console.log("create a copy=" + $itemScope);
                            console.log($itemScope.data);
                            $scope.copyDatatype($itemScope.data);

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
                    ['Add Segment',
                        function($itemScope) {
                            $scope.addSegments($rootScope.igdocument.profile.metaData.hl7Version);
                        }
                    ]
                ];

                $scope.addDatatype = [
                    ['Add Datatype',
                        function($itemScope) {
                            $scope.addDatatypes($rootScope.igdocument.profile.metaData.hl7Version);
                        }
                    ]
                ];

                $scope.DataTypeLibraryOptions = [
                    ['Add Datatypes',
                        function($itemScope) {
                            $scope.addDatatypesFromTree();
                            //$scope.openDataypeList($scope.datatypeLibStruct.metaData.hl7Version);
                        }
                    ]
                ];



                $scope.ValueSetAddOptionsINLIB = [
                    ['Add Value Sets ',
                        function($itemScope) {
                            //$scope.addDatatypesFromTree();
                            //$scope.openDataypeList($scope.datatypeLibStruct.metaData.hl7Version);
                        }
                    ]
                ];

                $scope.addValueSets = [

                    ['Add Value Sets',
                        function($itemScope) {
                            CloneDeleteSvc.createNewTable('USER', $rootScope.igdocument.profile.tableLibrary);
                        }
                    ], null,
                    ['Import HL7 Value Sets',
                        function($itemScope) {
                            $scope.addHL7Table($rootScope.igdocument.profile.tableLibrary);
                        }
                    ], null,
                    ['Import PHINVADs Value Sets',
                        function($itemScope) {
                            $scope.addPHINVADSTables($rootScope.igdocument.profile.tableLibrary);
                        }
                    ], null,
                    ['Import CSV Tables',
                        function($itemScope) {
                            $scope.addCSVTables($rootScope.igdocument.profile.tableLibrary);
                        }
                    ]

                ];


                $scope.addValueSetsInTableLibrary = [
                    ['Add Value Sets',
                        function($itemScope) {
                            $scope.addTablesInLibrary();
                        }
                    ]
                ];

                function processEditSeg(seg) {
                    $scope.Activate(seg.id);
                    //$rootScope.activeSegment = seg;
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
                    //$rootScope.currentData=section;

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

                $rootScope.editDataType = function(data) {

                    console.log("editDataType");

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
                    if (leaf.scope === 'HL7STANDARD') {
                        return 'HL7';
                    } else if (leaf.scope === 'USER') {
                        return 'USR';

                    } else if (leaf.scope === 'MASTER') {
                        return 'MAS';
                    } else {
                        return "";

                    }
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

                $scope.getSegmentsFromgroup = function(group) {
                    //_.union($rootScope.selectedSegments,temp);
                    for (var i = 0; i < group.children.length; i++) {
                        if (group.children[i].type === "segmentRef") {
                            console.log("IN IF ");
                            var segment = $rootScope.segmentsMap[group.children[i].ref.id];
                            var temp2 = [];
                            temp2.push(segment);
                            $rootScope.FilteredSegments.push(segment);
                            //$rootScope.FilteredSegments=_.union($rootScope.FilteredSegments,temp2);


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
                            //console.log(datatype);
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
                    $scope.filteringModeON = false;

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
            }
        ]);