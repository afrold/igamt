angular
    .module('igl')
    .controller(
    'TreeCtrl', ['$scope',
        '$rootScope',
        '$http',
        'SectionSvc',
        'CloneDeleteSvc',
        'FilteringSvc',

        function ($scope, $rootScope, $http, SectionSvc, CloneDeleteSvc,FilteringSvc) {


            $scope.collapsedata = false;
            $scope.collapsemessage = false;
            $scope.collapsesegment = false;
            $scope.collapsetable = false;
            $scope.collapsevalueSet=false;
            $scope.profilecollapsed = false;
            $scope.activeModel = "";
            $scope.Activate = function (param) {
                $scope.activeModel = param;
            }
      

            $rootScope.switcherprofile = function () {
                $scope.profilecollapsed = !$scope.profilecollapsed;

            };

            $rootScope.switchervalueSet = function () {
                $scope.collapsevalueSet = !$scope.collapsevalueSet;
            };
            $rootScope.switchertable = function () {
                $scope.collapsetable = !$scope.collapsetable;

            };

            $rootScope.switcherseg = function () {
                $scope.collapsesegment = !$scope.collapsesegment;

            };

            $rootScope.switchermsg = function () {
                $scope.collapsemessage = !$scope.collapsemessage;
            };

            $rootScope.switcherdata = function () {
                $scope.collapsedata = !$scope.collapsedata;

            };
            

            $scope.treeOptions = {

                accept: function (sourceNodeScope, destNodesScope, destIndex) {
                    var dataTypeSource = sourceNodeScope.$element
                        .attr('data-type');
                    var dataTypeDest = destNodesScope.$element
                        .attr('data-type');


                    if (!dataTypeDest) {
                        return false;
                    }
                    else if (dataTypeSource === "sections" && dataTypeDest === "sections") {
                        return true;
                    }
//							else if (dataTypeDest == sourceNodeScope.$parentNodeScope.$modelValue.sectionTitle){
//
//								return true;
//								}
                    else if (dataTypeDest === dataTypeSource + "s") {
                        return true;

                    } else
                        return false;
                },
                dropped: function (event) {

                    var sourceNode = event.source.nodeScope;
                    var destNodes = event.dest.nodesScope;
                    var sortBefore = event.source.index;
                    var sortAfter = event.dest.index;
                    var source = sourceNode.$parentNodeScope.$modelValue;
                    var dest = destNodes.$parent.$modelValue;

                    var dataType = destNodes.$element.attr('data-type');
                    event.source.nodeScope.$modelValue.sectionPosition = sortAfter + 1;
                    $scope.updatePositions(event.dest.nodesScope.$modelValue);
                    $scope.updatePositions(event.source.nodesScope.$modelValue);
                    console.log(sourceNode);
                    if (dest.type === "document" || source.type === "document") {
                        $scope.updateChildeSections($rootScope.igdocument.childSections);
                    }
                    else if (dest.type === "section" && source.type === "section") {
                        $scope.updateAfterDrop(source, dest);
                    }

                }
            };


            $scope.updatePositions = function (arr) {
                if (arr !== undefined) {
                    for (var i = arr.length - 1; i >= 0; i--) {
                        arr[i].sectionPosition = i + 1;
                    }
                }
                return "";
            };
            

            $scope.updateMessagePositions = function (arr) {
                if (arr !== undefined) {
                    for (var i = arr.length - 1; i >= 0; i--) {
                        arr[i].position = i + 1;
                    }
                }
                return "";
            };

            $scope.getLastPosition = function (arr) {
                var position = arr.length;
                for (var i = arr.length - 1; i >= 0; i--) {
                    var position = arr.length;

                    if (arr[i].sectionPosition && arr[i].sectionPosition >= position) {
                        return arr[i].sectionPosition + 1;
                    }
                    else return position;
                }

            };

            $scope.getLastcloneIndex = function (arr, name) {
                var index = 0;
                var cutIndex = name.length - 1;
                for (var i = arr.length - 1; i >= 0; i--) {
                    if (arr[i].sectionTitle.substring(0, cutIndex) === name && arr[i].sectionTitle.length > cutIndex + 1) {
                        index = parseInt(arr[i].sectionTitle.substring(cutIndex + 1)) + 1;

                        return index;
                    }
                    else return 1;
                }

            };


            $scope.getLabel = function ($itemScope) {
                if ($itemScope.$parentNodeScope) {
                    var p = $scope.getLabel($itemScope.$parentNodeScope);
                    if (p === "")
                        return $itemScope.$modelValue.sectionPosition;
                    else
                        return p + "." + $itemScope.$modelValue.sectionPosition;
                }
                else {
                    return "";
                }
            };


            $scope.cloneSectionTree = function (section) {
                var clone = {};
                if (section.childSections === undefined) {
                    clone.id = new ObjectId().toString();
                    clone.type = section.type;
                    clone.sectionContents = section.sectionContents;
                    clone.sectionDescription = section.sectionDescription;
                    clone.sectionTitle = section.sectionTitle + Math.floor((Math.random() * 50000) + 1);
                    console.log(clone);
                    return clone;
                }
                else {
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

            $scope.debug= function(leaf){
            	console.log("DEBUG FNCT");
            	console.log(leaf.$nodeScope.$modelValue.description);
            }
            $scope.sectionOption = [

                ['clone',
                    function ($itemScope) {
                        var cloneModel = $scope.cloneSectionTree($itemScope.$nodeScope.$modelValue);
                        cloneModel.sectionPosition = $scope.getLastPosition($itemScope.$nodeScope.$parentNodesScope.$modelValue);
                        $itemScope.$nodeScope.$parentNodesScope.$modelValue.push(cloneModel);
                        $scope.editSection(cloneModel);
                        $scope.activeModel = cloneModel.id;
                        if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === "document") {
                            $scope.updateChildeSections($rootScope.igdocument.childSections);
                        }
                        else if ($itemScope.$nodeScope.$parentNodeScope.$modelValue.type === "section") {
                            console.log(SectionSvc);
                            SectionSvc.save($itemScope.$nodeScope.$parentNodeScope.$modelValue.id, $itemScope.$nodeScope.$parentNodeScope.$modelValue)
                            //updateSection($itemScope.$nodeScope.$parentNodeScope.$modelValue);
                        }

                    } ],
                null,
                [
                    'delete',
                    function ($itemScope) {
                        $itemScope.$nodeScope.remove();
                        SectionSvc.delete($itemScope.$nodeScope.$parentNodesScope.$modelValue.id, $itemScope.$nodeScope.$parentNodesScope.$modelValue);
                        var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue
                            .indexOf($itemScope.$nodeScope.$modelValue);
                        if (index > -1) {
                            $itemScope.$nodeScope.$parentNodesScope.$modelValue
                                .splice(index, 1);
                        }
                        $scope.updatePositions($itemScope.$nodeScope.$parentNodesScope.$modelValue);
                    }
                ]

            ];

            $scope.SegmentOptions = [

                ['clone',
                    function ($itemScope) {
                        CloneDeleteSvc.copySegment($itemScope.segment);

                    } ],
                null,
                ['delete',
                    function ($itemScope) {
                        CloneDeleteSvc.deleteSegment($itemScope.segment);
                    } ]

            ];

            
            
            
            $scope.DataTypeOptions = [

                ['clone',
                    function ($itemScope) {
                	
                	//  console.log("******"+$itemScope.$nodeScope.$modelValue.name+"******");
                        CloneDeleteSvc.copyDatatype($itemScope.data); 

                      
                    } ],
                null,
                ['delete',
                    function ($itemScope) {
                        CloneDeleteSvc.deleteDatatype($itemScope.data);
                    } ]
                    
                   
            ];

            $scope.ValueSetOptions = [

                ['clone',
                    function ($itemScope) {
                        CloneDeleteSvc.copyTable($itemScope.table);

                    } ],
                null,
                ['delete',
                    function ($itemScope) {
                        CloneDeleteSvc.deleteValueSet($itemScope.table);
                    } ]

            ];

            $scope.MessagesOption = [

                [
                    'clone',
                    function ($itemScope) {
                        CloneDeleteSvc.copyMessage($itemScope.msg);

                    } ],
                null,
                [
                    'delete',
                    function ($itemScope) {
                        CloneDeleteSvc.deleteMessage($itemScope.msg);


                    } ]

            ];


            $scope.MessagesRootOption = [

                [ 'export', function ($itemScope) {
                    $scope.selectMessages($rootScope.igdocument);
                } ]

            ];


            $scope.ValueSetOptions = [

                [
                    'clone',
                    function ($itemScope) {
                        CloneDeleteSvc.copyTable($itemScope.table);


                    } ],
                null,
                [
                    'delete',
                    function ($itemScope) {
                        CloneDeleteSvc.deleteValueSet($itemScope.table);
                    } ]


            ];


            $scope.ValueSetRootOptions = [
                [ 'add Table', function ($itemScope) {
                    $scope.addTable($rootScope.igdocument);
                } ]

            ];


            $scope.editSeg = function (seg) {
                //console.log("EditSeg")

                $scope.$emit('event:openSegment', seg);

            }

            $scope.editIg = function (ig) {
                $rootScope.igdocument = ig;
                $scope.$emit('event:openDocumentMetadata',
                    $rootScope.igdocument);
            }

            $scope.editSection = function (section) {
                $rootScope.section = section;
                $scope.$emit('event:openSection', $rootScope.section);
            }


            $scope.editRoutSection = function (param) {

                $scope.$emit('event:openSection', $scope.getRoutSectionByname(param));
            }


            $scope.getRoutSectionByname = function (name) {
                var section = {};
                $scope.Activate(name);
                if (name.toLowerCase() === 'conformance profiles') {
                	
                    section.sectionContents = $rootScope.igdocument.profile.messages.sectionContents;
                    section.sectionTitle = $rootScope.igdocument.profile.messages.sectionTitle;
                    section.sectionPosition = $rootScope.igdocument.profile.messages.sectionPosition;
                    section.sectionType = $rootScope.igdocument.profile.messages.sectionType;
                    section.sectionDescription = $rootScope.igdocument.profile.messages.Description;

                } else if (name.toLowerCase() === 'segments and field descriptions') {
            
                    section.sectionContents = $rootScope.igdocument.profile.segmentLibrary.sectionContents;
                    section.sectionTitle = $rootScope.igdocument.profile.segmentLibrary.sectionTitle;
                    section.sectionPosition = $rootScope.igdocument.profile.segmentLibrary.sectionPosition;
                    section.sectionDescription = $rootScope.igdocument.profile.segmentLibrary.Description;
                } else if (name.toLowerCase() === 'value sets') {
                    section.sectionContents = $rootScope.igdocument.profile.tableLibrary.sectionContents;
                    section.sectionTitle = $rootScope.igdocument.profile.tableLibrary.sectionTitle;
                    section.sectionPosition = $rootScope.igdocument.profile.tableLibrary.sectionPosition;
                    section.sectionType = $rootScope.igdocument.profile.tableLibrary.sectionType;
                    section.sectionDescription = $rootScope.igdocument.profile.tableLibrary.Description;
                } else if (name.toLowerCase() === 'datatypes') {
                    section.sectionContents = $rootScope.igdocument.profile.datatypeLibrary.sectionContents;
                    section.sectionTitle = $rootScope.igdocument.profile.datatypeLibrary.sectionTitle;
                    section.sectionPosition = $rootScope.igdocument.profile.datatypeLibrary.sectionPosition;
                    section.sectionType = $rootScope.igdocument.profile.datatypeLibrary.sectionType;
                    section.sectionDescription = $rootScope.igdocument.profile.datatypeLibrary.Description;
                }
                //console.log(section);
                return section;
            }
            $scope.editDataType = function (data) {
                $rootScope.datatype = data;
                $scope.$emit('event:openDatatype', $rootScope.datatype);
            }

            $scope.editTable = function (table) {
                $rootScope.table = table;
                $scope.$emit('event:openTable', $rootScope.table);
            }

            $scope.editMessage = function (message) {
                $rootScope.message = message;
                $scope.$emit('event:openMessage', message);
            }
            $scope.editProfile = function () {
            	 $scope.Activate("Message Infrastructure");
                $scope.$emit('event:openProfileMetadata',
                    $rootScope.igdocument);
            }


            $scope.updateAfterDrop = function (source, dest) {

                var id = $rootScope.igdocument.id;
                //console.log(JSON.stringify(source));
                var req = {
                    method: 'POST',
                    url: "api/igdocuments/" + id + "/dropped",
                    headers: {
                        'Content-Type': "application/json"
                    },
                    data: {source: source, dest: dest}
                }


                var promise = $http(req)
                    .success(function (data, status, headers, config) {
                        ////console.log(data);
                        return data;
                    })
                    .error(function (data, status, headers, config) {
                        if (status === 404) {
                            console.log("Could not reach the server");
                        }
                        else if (status === 403) {
                            console.log("limited access");
                        }
                    });
                return promise;
            }
            

            $scope.updateChildeSections = function (childSections) {

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
                    .success(function (data, status, headers, config) {
                        ////console.log(data);
                        return data;
                    })
                    .error(function (data, status, headers, config) {
                        if (status === 404) {
                            console.log("Could not reach the server");
                        } else if (status === 403) {
                            console.log("limited access");
                        }
                    });
                return promise;
            }
           
            $scope.isUnused = function (node) {
                return FilteringSvc.isUnused(node);
            };


            $scope.showToC = function (leaf) {
                return FilteringSvc.showToC(leaf);
            };
            
            $scope.getScopeLabel = function (leaf) {
            if (leaf.scope==='HL7STANDARD'){
            	return 'HL7';
            }
            else  if(leaf.scope==='USER') {
            	return 'USE';
            	
            }
            
            else  if(leaf.scope==='MASTER') {
            	return 'MAS';
            	
            }
            
            else {
            	return "";
            	
            }
            };
            

            
            



        }]);





