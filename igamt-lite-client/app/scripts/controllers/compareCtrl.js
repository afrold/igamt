angular.module('igl').controller('compareCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc) {
    $scope.igDocumentConfig = {
        selectedType: null
    };
    $scope.getLabel = function(element) {
        if (element.ext !== null) {
            return element.name + "_" + element.ext;
        } else {
            return element.name;
        }
    };

    $scope.msgSelected = false;
    $scope.igDisabled1 = false;
    $scope.igDisabled2 = false;
    $scope.msgChanged = false;
    $scope.cmpMsg = false;
    $scope.cmpSeg = false;
    $scope.cmpDT = false;
    $scope.cmpVS = false;

    //$scope.scopes = ["USER", "HL7STANDARD"];
    $scope.scopes = [{
        name: "USER",
        alias: "My IG"
    }, {
        name: "HL7STANDARD",
        alias: "Base HL7"
    }];
    $scope.toCompare = [{
        name: "message",
        alias: "Messages"
    }, {
        name: "datatype",
        alias: "Datatypes"
    }, {
        name: "valueset",
        alias: "Value Sets"
    }, {
        name: "segment",
        alias: "Segments"
    }];

    $scope.showDelta = false;
    var listHL7Versions = function() {
        return $http.get('api/igdocuments/findVersions', {
            timeout: 60000
        }).then(function(response) {
            var hl7Versions = [];
            var length = response.data.length;
            for (var i = 0; i < length; i++) {
                hl7Versions.push(response.data[i]);
            }
            return hl7Versions;
        });
    };

    var init = function() {
        listHL7Versions().then(function(versions) {
            $scope.versions = versions;
        });
    };

    $scope.$on('event:loginConfirmed', function(event) {
        init();
    });

    init();



    $scope.setCmpType = function(type) {
        if (type === "message") {
            $scope.cmpSeg = false;
            $scope.cmpVS = false;
            $scope.cmpDT = false;
            $scope.cmpMsg = true;
        } else if (type === "segment") {
            $scope.cmpMsg = false;
            $scope.cmpVS = false;
            $scope.cmpDT = false;
            $scope.cmpSeg = true;
        } else if (type === "datatype") {
            $scope.cmpSeg = false;
            $scope.cmpVS = false;
            $scope.cmpMsg = false;
            $scope.cmpDT = true;
        } else if (type === "valueset") {
            $scope.cmpSeg = false;
            $scope.cmpMsg = false;
            $scope.cmpDT = false;
            $scope.cmpVS = true;
        }
    }


    $scope.setVersion1 = function(vr) {
        $scope.version1 = vr;

    };
    $scope.setScope1 = function(scope) {

        $scope.scope1 = scope;

    }
    $scope.setVersion2 = function(vr) {
        $scope.version2 = vr;

    };
    $scope.setScope2 = function(scope) {

        $scope.scope2 = scope;
    };

    $scope.$watchGroup(['version1', 'scope1'], function() {
        $scope.igList1 = [];
        $scope.messages1 = [];
        $scope.ig1 = "";


        if ($scope.scope1 && $scope.version1) {
            IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope1], $scope.version1).then(function(result) {
                console.log(result);

                if (result) {

                    if ($scope.scope1 === "HL7STANDARD") {
                        $scope.igDisabled1 = true;


                        $scope.ig1 = {
                            id: result[0].id,
                            title: result[0].metaData.title
                        };


                        $scope.igList1.push($scope.ig1);
                        $scope.setIG1($scope.ig1);

                    } else {
                        $scope.igDisabled1 = false;

                        for (var i = 0; i < result.length; i++) {
                            $scope.igList1.push({
                                id: result[i].id,
                                title: result[i].metaData.title
                            });
                        }

                    }





                }
            });
        }

    }, true);
    $scope.$watchGroup(['version2', 'scope2'], function() {
        $scope.igList2 = [];
        $scope.messages2 = [];
        $scope.ig2 = "";
        if ($scope.scope2 && $scope.version2) {
            console.log("hereee2");
            IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {

                if (result) {
                    if ($scope.scope2 === "HL7STANDARD") {
                        $scope.igDisabled2 = true;
                        $scope.ig2 = {
                            id: result[0].id,
                            title: result[0].metaData.title
                        };
                        $scope.igList2.push($scope.ig2);

                        $scope.setIG2($scope.ig2);
                    } else {
                        $scope.igDisabled2 = false;
                        for (var i = 0; i < result.length; i++) {
                            $scope.igList2.push({
                                id: result[i].id,
                                title: result[i].metaData.title,
                            });
                        }

                    }




                }

            });
            // SegmentService.getSegmentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
            //     console.log(result);

            // });
        }

    }, true);
    $scope.$watchGroup(['msg1', 'msg2'], function() {
        $scope.msgChanged = true;


    }, true);


    $scope.findIGbyID = function(id) {
        var selectedIG = [];

        angular.forEach($rootScope.igs, function(ig) {
            if (ig.id === id) {

                selectedIG = ig;
            }
        });
        return selectedIG;


    };
    $scope.setIG1 = function(ig) {
        if (ig) {
            IgDocumentService.getOne(ig.id).then(function(igDoc) {
                console.log(ig.id);
                SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
                    DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
                        TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
                            $scope.messages1 = [];
                            $scope.msg1 = "";
                            if (igDoc) {
                                $scope.segList1 = angular.copy(segments);
                                $scope.dtList1 = angular.copy(datatypes);
                                $scope.tableList1 = angular.copy(tables);
                                $scope.messages1 = orderByFilter(igDoc.profile.messages.children, 'name');
                                $scope.segments1 = segments;
                                $scope.datatypes1 = datatypes;
                                $scope.tables1 = tables;
                            }
                        });
                    });
                });

            });

            //$scope.messages1 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

        }


    };
    $scope.setIG2 = function(ig) {
        if (ig) {
            IgDocumentService.getOne(ig.id).then(function(igDoc) {
                SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
                    DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
                        TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
                            $scope.messages2 = [];
                            $scope.msg2 = "";
                            if (igDoc) {
                                $scope.segList2 = angular.copy(segments);
                                $scope.dtList2 = angular.copy(datatypes);
                                $scope.tableList2 = angular.copy(tables);
                                $scope.messages2 = igDoc.profile.messages.children;
                                $scope.segments2 = segments;
                                $scope.datatypes2 = datatypes;
                                $scope.tables2 = tables;
                            }
                        });
                    });
                });

            });

            //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

        }

    };
    $scope.hideMsg = function(msg1, msg2) {

        if (msg2) {
            return !(msg1.structID === JSON.parse(msg2).structID);
        } else {
            return false;
        }
    };
    $scope.disableMsg = function(msg1, msg2) {

        if (msg2) {
            return (msg1.id === JSON.parse(msg2).id);
        } else {
            return false;
        }
    };

    $scope.setMsg1 = function(msg) {
        console.log(JSON.parse(msg));
        $scope.msg1 = msg;
        $scope.segmentList1 = JSON.parse(msg);
    };
    $scope.setMsg2 = function(msg) {
        $scope.msg2 = msg;
    };
    $scope.setSegment1 = function(segment) {
        console.log(JSON.parse(segment));
    };
    $scope.setDatatype1 = function(datatype) {
        console.log(JSON.parse(datatype));
    };
    $scope.setTable1 = function(table) {
        console.log(table);
    };
    $scope.setSegment2 = function(segment) {
        console.log(JSON.parse(segment));
    };
    $scope.setDatatype2 = function(datatype) {
        console.log(JSON.parse(datatype));
    };
    $scope.setTable2 = function(table) {
        console.log(table);
    };
    $scope.clearAll = function() {
        $scope.msg1 = "";
        $scope.msg2 = "";
        $scope.ig1 = "";
        $scope.ig2 = "";
        $scope.version1 = "";
        $scope.version2 = "";
        $scope.scope1 = "";
        $scope.scope2 = "";

    };
    $scope.clearIG = function() {
        $scope.ig1 = "";
        $scope.ig2 = "";
    }
    $scope.clearVersion = function() {
        $scope.version1 = "";
        $scope.version2 = "";
    }
    $scope.clearMessage = function() {
        $scope.msg1 = "";
        $scope.msg2 = "";

    }
    $scope.clearScope = function() {
        $scope.scope1 = "";
        $scope.scope2 = "";
    }
    $scope.formatGrp = function(grp) {
        var delay = $q.defer();

        var group = {
            id: grp.id,
            name: grp.name,
            type: grp.type,
            minCard: grp.min,
            maxCard: grp.max,
            usage: grp.usage,
            position: grp.position,
            //segments: $scope.formatSeg(grp.children)
        };
        $scope.formatSegments(grp.children).then(function(result) {
            //group.segments = result;
            var resultList = _.flatten(result);
            var SortedRes = [];
            SortedRes = orderByFilter(resultList, 'position');
            // for(var i=0;i<result.length;i++){
            //     if(angular.isArray(result[i])){
            //         for (var j = 0; i < result[i].length; j++) {
            //             resultList.push(result[i][j])
            //         };

            //     } 
            // }

            group.segments = SortedRes;
            delay.resolve(group);


        });
        return delay.promise;
        //return group;

    };

    $scope.formatFields = function(ids, fields) {
        var delay = $q.defer();
        DatatypeService.get(ids).then(function(dts) {
            for (var j = 0; j < dts.length; j++) {
                for (var i = 0; i < dts[j].components.length; i++) {
                    dts[j].components[i].id = "";

                }
            }
            for (var j = 0; j < fields.length; j++) {
                for (var i = 0; i < dts.length; i++) {
                    if (fields[j].datatype.id === dts[i].id && dts[i].components.length > 0) {
                        fields[j].components = dts[i].components;
                    }
                }
            }
            delay.resolve(fields);
        });
        return delay.promise;
    };


    $scope.formatComponent = function(ids, fields) {
        var delay = $q.defer();


        DatatypeService.get(ids).then(function(dts) {
            for (var j = 0; j < dts.length; j++) {
                for (var i = 0; i < dts[j].components.length; i++) {
                    dts[j].components[i].id = "";
                    dts[j].components[i].datatype.id = "";

                }
            }
            for (var j = 0; j < fields.length; j++) {
                for (var i = 0; i < dts.length; i++) {
                    if (fields[j].datatype.id === dts[i].id) {
                        fields[j].components = dts[i].components;
                    }

                }
                fields[j].datatype.id = "";
            }
            delay.resolve(fields);
        });






        // DatatypeService.getOne(field.datatype.id).then(function(dt) {
        //     for (var i = 0; i < dt.components.length; i++) {
        //         dt.components[i].id = "";
        //     }
        //     field.components = dt.components;
        //     delay.resolve(field);
        // });
        return delay.promise;



    };

    $scope.formatField = function(segment) {
        var delay = $q.defer();
        var promises = [];
        var dtId = [];
        for (var i = 0; i < segment.fields.length; i++) {
            segment.fields[i].id = "";
            //$scope.formatField(segs.fields[i]);
            dtId.push(segment.fields[i].datatype.id);

            //            promises.push($scope.formatComponent(segment.fields[i]));
        }
        promises.push($scope.formatComponent(dtId, segment.fields));

        $q.all(promises).then(function(fields) {
            delay.resolve(fields);
        });
        return delay.promise;
    };

    $scope.formatSeg = function(segment, segs) {
        var delay = $q.defer();
        if (segment.type === 'segmentRef') {
            var newSegment = {};
            newSegment = {
                id: segment.ref.id,
                name: segment.ref.name,
                label: segment.ref.label,
                type: segment.type,
                // fields: result,
                minCard: segment.min,
                maxCard: segment.max,
                usage: segment.usage,
                position: segment.position,
                description: segs.description,
                conformanceStatements: segs.conformanceStatements,
                coConstraints: segs.coConstraints,
                predicates: segs.predicates



            };
            $scope.formatField(segs).then(function(fields) {
                newSegment.fields = segs.fields;
                delay.resolve(newSegment);

            });

            // SegmentService.get(segment.ref.id).then(function(segs) {
            //     newSegment.description = segs.description;
            //     $scope.formatField(segs).then(function(fields) {
            //         newSegment.fields = segs.fields;
            //         delay.resolve(newSegment);

            //     });




            // });

            // $scope.formatFields(dtIds, newSegment.fields);


        } else {
            $scope.formatGrp(segment).then(function(grp) {
                delay.resolve(grp);
            });
        }
        return delay.promise;
    };

    $scope.segRefsFormating = function(idList, segmentRefs) {
        var delay = $q.defer();
        var promises = [];
        SegmentService.findByIds(idList).then(function(segments) {
            for (var i = 0; i < segmentRefs.length; i++) {
                for (var j = 0; j < segments.length; j++) {
                    if (segmentRefs[i].ref.id === segments[j].id) {
                        promises.push($scope.formatSeg(segmentRefs[i], segments[j]));
                    }

                }
            }
            $q.all(promises).then(function(segs) {
                delay.resolve(segs);
            });

        });

        return delay.promise;

    };




    $scope.formatSegments = function(segments) {

        var delay = $q.defer();
        var promises = [];
        var segRefList = [];
        var idList = [];
        for (var i = 0; i < segments.length; i++) {
            if (segments[i].type === "group") {

                promises.push($scope.formatGrp(segments[i]));

            } else if (segments[i].type === "segmentRef") {
                idList.push(segments[i].ref.id);
                segRefList.push(segments[i]);

                //                promises.push($scope.formatSeg(segments[i]));

            }
            // else if (segments[i].type === "field") {
            //     promises.push($scope.formatField(segments[i]));

            // }
        };
        promises.push($scope.segRefsFormating(idList, segRefList));
        $q.all(promises).then(function(segs) {
            delay.resolve(segs);
        });
        return delay.promise;


        //return result;


    };
    $scope.formatMsg = function(msg) {
        console.log(msg);
        var delay = $q.defer();

        var message = {
            name: msg.name,
            event: msg.event,
            structID: msg.structID,
            position: msg.position,
            //segments: $scope.formatSeg(msg.children)
        }
        $scope.formatSegments(msg.children).then(function(result) {
            var resultList = _.flatten(result);
            var SortedRes = [];
            SortedRes = orderByFilter(resultList, 'position');

            // for(var i=0;i<result.length;i++){
            //     if(angular.isArray(result[i])){
            //         for (var j = 0; i < result[i].length; j++) {
            //             resultList.push(result[i][j])
            //         };

            //     } 
            // }

            message.segments = SortedRes;

            delay.resolve(message);

        });


        return delay.promise;

        // console.log(message);
        // return message;

    };


    $scope.fMsg = function(msg, datatypeList, segmentList) {
        console.log("====");
        console.log(segmentList);
        var elements = []
        var message = {
            name: msg.name,
            event: msg.event,
            structID: msg.structID,
            position: msg.position,
            segments: $scope.fElements(msg.children, datatypeList, segmentList)
        };
        // for (var i = 0; i < msg.children.length; i++) {
        //     elements.push($scope.fElement(msg.children[i]));
        // };
        // message.segments=elements;

        return message;
    };
    $scope.fElements = function(elements, datatypeList, segmentList) {

        var result = [];
        for (var i = 0; i < elements.length; i++) {
            if (elements[i].type === 'segmentRef') {
                var segment = {};

                segment = {
                    //id: elements[i].ref.id,
                    name: elements[i].ref.name,
                    label: elements[i].ref.label,
                    type: elements[i].type,
                    minCard: elements[i].min,
                    maxCard: elements[i].max,
                    usage: elements[i].usage,
                    position: elements[i].position
                };

                for (var j = 0; j < segmentList.length; j++) {
                   

                    if (elements[i].ref.id === segmentList[j].id) {

                        segment.description = segmentList[j].description;
                        segment.conformanceStatements = segmentList[j].conformanceStatements;
                        segment.coConstraints = segmentList[j].coConstraints;
                        segment.predicates = segmentList[j].predicates;
                        segment.fields = $scope.fFields(segmentList[j].fields, datatypeList, segmentList);
                    }
                };



                result.push(segment);

            } else {
                result.push($scope.fGrp(elements[i], datatypeList, segmentList));
            }
        };
        return result;
    };
    $scope.fGrp = function(grp, datatypeList, segmentList) {
        var group = {
            name: grp.name,
            type: grp.type,
            minCard: grp.min,
            maxCard: grp.max,
            usage: grp.usage,
            position: grp.position,
            segments: $scope.fElements(grp.children, datatypeList, segmentList)
        };
        return group;
    };
    $scope.fFields = function(fields, datatypeList, segmentList) {
        for (var j = 0; j < datatypeList.length; j++) {
            for (var i = 0; i < datatypeList[j].components.length; i++) {
                datatypeList[j].components[i].id = "";
                datatypeList[j].components[i].datatype.id = "";

            }
        }
        for (var i = 0; i < fields.length; i++) {
            fields[i].id = "";
            for (var j = 0; j < datatypeList.length; j++) {
                if (fields[i].datatype.id === datatypeList[j].id) {
                    fields[i].components = datatypeList[j].components;

                }
            };
            fields[i].datatype.id = "";

        };
        return fields;


    };




    var objToArray = function(object) {
        var result = [];
        $.map(object, function(value, index) {

            result.push(value);
        });
        return result;

    };
    var writettTable = function(childArray, dataArray) {
        var result = {};
        console.log(childArray);


        if (childArray.changed === "object change") {
            if (childArray.value.position.changed === "equal") {
                result.position = {
                    msg: childArray.value.position.value,

                };
            }

            if (childArray.value.type.changed === "equal") {
                result.type = {
                    msg: childArray.value.type.value,
                };

                if (childArray.value.usage.changed === "primitive change") {
                    result.usage = {
                        msg1: childArray.value.usage.removed,
                        msg2: childArray.value.usage.added

                    };
                }
                if (childArray.value.type.value === "field" || childArray.value.type.value === "component") {
                    if (childArray.value.name.changed === "primitive change") {
                        result.label = {
                            msg1: childArray.value.name.removed,
                            msg2: childArray.value.name.added

                        };

                    }

                    if (childArray.value.name.changed === "equal") {
                        result.label = {
                            msg: childArray.value.name.value,

                        };
                    }
                    if (childArray.value.min && childArray.value.min.changed === "primitive change") {
                        result.minCard = {
                            msg1: childArray.value.min.removed,
                            msg2: childArray.value.min.added

                        };
                    } else if (childArray.value.min && childArray.value.min.changed === "removed") {
                        console.log(childArray.value.min);
                    } else if (childArray.value.min && childArray.value.min.changed === "added") {
                        console.log(childArray.value.min);
                    }
                    if (childArray.value.max && childArray.value.max.changed === "primitive change") {
                        result.maxCard = {
                            msg1: childArray.value.max.removed,
                            msg2: childArray.value.max.added

                        };
                    }
                    if (childArray.value.minLength.changed === "primitive change") {
                        result.minLength = {
                            msg1: childArray.value.minLength.removed,
                            msg2: childArray.value.minLength.added

                        };
                    }
                    if (childArray.value.maxLength.changed === "primitive change") {
                        result.maxLength = {
                            msg1: childArray.value.maxLength.removed,
                            msg2: childArray.value.maxLength.added

                        };
                    }
                    if (childArray.value.confLength.changed === "primitive change") {
                        result.confLength = {
                            msg1: childArray.value.confLength.removed,
                            msg2: childArray.value.confLength.added

                        };
                    }
                    if (childArray.value.datatype.changed === "object change") {
                        result.datatype = {
                            msg1: childArray.value.datatype.value.label.removed,
                            msg2: childArray.value.datatype.value.label.added

                        };
                    }
                    if (childArray.value.components && childArray.value.type.value === "field" && childArray.value.components.changed === "object change") {
                        result.components = [];
                        objToArray(childArray.value.components.value).forEach(function(childNode) {
                            writettTable(childNode, result.components);

                        });

                    }
                    if (childArray.value.table.changed === "object change" && childArray.value.table.value.bindingIdentifier.changed === "primitive change") {
                        result.valueset = {
                            msg1: childArray.value.table.value.bindingIdentifier.removed,
                            msg2: childArray.value.table.value.bindingIdentifier.added

                        };

                    }


                } else {

                    if (childArray.value.minCard.changed === "primitive change") {
                        result.minCard = {
                            msg1: childArray.value.minCard.removed,
                            msg2: childArray.value.minCard.added

                        };
                    }
                    if (childArray.value.maxCard.changed === "primitive change") {
                        result.maxCard = {
                            msg1: childArray.value.maxCard.removed,
                            msg2: childArray.value.maxCard.added

                        };
                    }
                }

                if (childArray.value.type.value === "segmentRef") {
                    if (childArray.value.label.changed === "primitive change") {
                        result.label = {
                            msg1: childArray.value.label.removed,
                            msg2: childArray.value.label.added

                        };

                    }
                    if (childArray.value.description && childArray.value.description.changed === "primitive change") {
                        result.description = {
                            msg1: childArray.value.description.removed,
                            msg2: childArray.value.description.added

                        };

                    } else if (childArray.value.description && childArray.value.description.changed === "equal") {
                        result.description = {
                            msg: childArray.value.description.value,

                        };
                    }


                    if (childArray.value.label.changed === "equal") {
                        result.label = {
                            msg: childArray.value.label.value,

                        };
                    }
                    if (childArray.value.fields && childArray.value.fields.changed === "object change") {
                        result.fields = [];
                        objToArray(childArray.value.fields.value).forEach(function(childNode) {
                            writettTable(childNode, result.fields);

                        });
                    }

                } else if (childArray.value.type.value === "group") {
                    if (childArray.value.name.changed === "primitive change") {
                        result.label = {
                            msg1: childArray.value.name.removed,
                            msg2: childArray.value.name.added

                        };

                    }

                    if (childArray.value.name.changed === "equal") {
                        result.label = {
                            msg: childArray.value.name.value,

                        };
                    }
                    if (childArray.value.segments.changed === "object change") {
                        result.segments = [];
                        objToArray(childArray.value.segments.value).forEach(function(childNode) {
                            writettTable(childNode, result.segments);

                        });


                    }
                }
            } else if (childArray.value.type.changed === "primitive change") {
                result.label = {
                    msg1: childArray.value.name.removed,
                    msg2: childArray.value.name.added

                };
                result.type = {
                    msg1: childArray.value.type.removed,
                    msg2: childArray.value.type.added
                };
            }
            dataArray.push(result);




        }

    };
    $scope.dynamic_params = new ngTreetableParams({
        getNodes: function(parent) {
            if ($scope.dataList !== undefined) {
                //return parent ? parent.fields : $scope.test;
                if (parent) {
                    if (parent.fields) {
                        return parent.fields;
                    } else if (parent.components) {
                        return parent.components;
                    } else if (parent.segments) {
                        return parent.segments;
                    }

                } else {
                    return $scope.dataList;
                }

            }
        },
        getTemplate: function(node) {
            return 'tree_node';


        }
    });

    $scope.cmp = function() {
        $scope.loadingSelection = true;
        $scope.msgChanged = false;
        var msg1 = $scope.fMsg(JSON.parse($scope.msg1), $scope.dtList1, $scope.segList1);
        var msg2 = $scope.fMsg(JSON.parse($scope.msg2), $scope.dtList2, $scope.segList2)

        console.log(msg1);
        console.log(msg2);
        // console.log($scope.dtList2);
        // console.log($scope.segList2);
        $scope.diff = ObjectDiff.diffOwnProperties(msg1, msg2);
        $scope.dataList = [];
        console.log($scope.diff);

        if ($scope.diff.changed === "object change") {
            var array = objToArray($scope.diff);
            var arraySeg = objToArray(array[1].segments.value);

            //writeTable(array[1].segments, 0, $scope.gridOptions.data);
            for (var i = 0; i < arraySeg.length; i++) {
                writettTable(arraySeg[i], $scope.dataList);
            }

        }

        console.log("Here outside");
        $scope.loadingSelection = false;


        if ($scope.dynamic_params) {
            console.log($scope.dataList);
            $scope.showDelta = true;
            $scope.dynamic_params.refresh();
        }

    }
    $scope.AdvencedCmp = function() {

    }

    $scope.compare = function() {

        $scope.loadingSelection = true;
        $scope.msgChanged = false;
        $scope.formatMsg(JSON.parse($scope.msg1)).then(function(msg1) {


            $scope.formatMsg(JSON.parse($scope.msg2)).then(function(msg2) {
                console.log(JSON.parse($scope.msg1));
                console.log(JSON.parse($scope.msg2));

                $scope.diff = ObjectDiff.diffOwnProperties(msg1, msg2);
                console.log($scope.diff);
                $scope.dataList = [];
                if ($scope.diff.changed === "object change") {
                    var array = objToArray($scope.diff);
                    var arraySeg = objToArray(array[1].segments.value);

                    //writeTable(array[1].segments, 0, $scope.gridOptions.data);
                    for (var i = 0; i < arraySeg.length; i++) {
                        writettTable(arraySeg[i], $scope.dataList);
                    }

                }

                console.log("Here outside");
                $scope.loadingSelection = false;


                if ($scope.dynamic_params) {
                    console.log($scope.dataList);
                    $scope.showDelta = true;
                    $scope.dynamic_params.refresh();
                }
            });

        });

    };








});
