angular.module('igl').controller('compareCtrl', function($scope, $modal, ObjectDiff, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService) {
    $scope.igDocumentConfig = {
        selectedType: null
    };





    // $scope.loadIGDocuments = function() {
    //     var delay = $q.defer();
    //     $scope.igDocumentConfig.selectedType = StorageService.getSelectedIgDocumentType() != null ? StorageService.getSelectedIgDocumentType() : 'USER';
    //     $scope.error = null;
    //     $rootScope.igs = [];
    //     $scope.tmpIgs = [].concat($rootScope.igs);
    //     if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
    //         $scope.loading = true;
    //         StorageService.setSelectedIgDocumentType($scope.igDocumentConfig.selectedType);
    //         $http.get('api/igdocuments', { params: { "type": $scope.igDocumentConfig.selectedType } }).then(function(response) {
    //             $rootScope.igs = angular.fromJson(response.data);
    //             $scope.tmpIgs = [].concat($rootScope.igs);
    //             $scope.loading = false;
    //             delay.resolve(true);
    //         }, function(error) {
    //             $scope.loading = false;
    //             $scope.error = error.data;
    //             delay.reject(false);
    //         });
    //     } else {
    //         delay.reject(false);
    //     }
    //     return delay.promise;
    // };
    // $scope.loadIGDocuments().then(function() {
    //     console.log($rootScope.igs);
    //     $scope.msg1=$rootScope.igs[0].profile.messages.children[0];
    //     $scope.msg2=$rootScope.igs[0].profile.messages.children[1];
    //     $scope.formatMsg($scope.msg1).then(function(msg1) {
    //         console.log("Here inside1");

    //         $scope.formatMsg($scope.msg2).then(function(msg2) {
    //             $scope.diff = ObjectDiff.diffOwnProperties(msg1, msg2);
    //             console.log($scope.diff);

    //             var array = objToArray($scope.diff);
    //             var arraySeg = objToArray(array[1].segments.value);

    //             //writeTable(array[1].segments, 0, $scope.gridOptions.data);
    //             $scope.dataList = [];
    //             for (var i = 0; i < arraySeg.length; i++) {
    //                 writettTable(arraySeg[i], $scope.dataList);
    //             }
    //             console.log($scope.dataList);

    //             if ($scope.dynamic_params) {
    //                 $scope.showDelta = true;
    //                 $scope.dynamic_params.refresh();
    //             }
    //         });
    //         console.log("Here outside");

    //     });


    //     // angular.forEach($rootScope.igs, function(ig) {


    //     //     var element = {
    //     //         id: ig.id,
    //     //         title: ig.metaData.title
    //     //     };
    //     //     $scope.igList.push(element);

    //     // });

    // });















    $scope.msgSelected = false;
    $scope.igDisabled1 = false;
    $scope.igDisabled2 = false;
    $scope.msgChanged=false;

    //$scope.scopes = ["USER", "HL7STANDARD"];
    $scope.scopes = [{
        name: "USER",
        alias: "My IG"
    }, {
        name: "HL7STANDARD",
        alias: "Base HL7"
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
        }

    }, true);
    $scope.$watchGroup(['msg1', 'msg2'], function() {
        $scope.msgChanged=true;


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
            IgDocumentService.getOne(ig.id).then(function(result) {
                $scope.messages1 = [];
                $scope.msg1 = "";
                if (result) {
                    $scope.messages1 = result.profile.messages.children;
                }

            });

            //$scope.messages1 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

        }


    };
    $scope.setIG2 = function(ig) {
        if (ig) {
            IgDocumentService.getOne(ig.id).then(function(result) {
                $scope.messages2 = [];
                $scope.msg2 = "";
                if (result) {
                    $scope.messages2 = result.profile.messages.children;
                }

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
        $scope.msg1 = msg;
    };
    $scope.setMsg2 = function(msg) {
        $scope.msg2 = msg;
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
            group.segments = result;
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

    $scope.formatSeg = function(segment) {
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
                position: segment.position
            };

            SegmentService.get(segment.ref.id).then(function(segs) {
                newSegment.description = segs.description;
                $scope.formatField(segs).then(function(fields) {
                    newSegment.fields = segs.fields;
                    delay.resolve(newSegment);

                });




            });
            // $scope.formatFields(dtIds, newSegment.fields);


        } else {
            $scope.formatGrp(segment).then(function(grp) {
                delay.resolve(grp);
            });
        }
        return delay.promise;
    };




    $scope.formatSegments = function(segments) {
        var delay = $q.defer();
        var promises = [];
        for (var i = 0; i < segments.length; i++) {
            if (segments[i].type === "group") {

                promises.push($scope.formatGrp(segments[i]));

            } else if (segments[i].type === "segmentRef") {
                promises.push($scope.formatSeg(segments[i]));

            }
            // else if (segments[i].type === "field") {
            //     promises.push($scope.formatField(segments[i]));

            // }
        };
        $q.all(promises).then(function(segs) {
            delay.resolve(segs);
        });
        return delay.promise;


        //return result;


    };
    $scope.formatMsg = function(msg) {
        var delay = $q.defer();

        var message = {
            name: msg.name,
            event: msg.event,
            structID: msg.structID,
            position: msg.position,
            //segments: $scope.formatSeg(msg.children)
        }
        $scope.formatSegments(msg.children).then(function(result) {
            message.segments = result;

            delay.resolve(message);

        });


        return delay.promise;

        // console.log(message);
        // return message;

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
        // console.log(childArray);


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
                    if (childArray.value.description.changed === "primitive change") {
                        result.description = {
                            msg1: childArray.value.description.removed,
                            msg2: childArray.value.description.added

                        };

                    } else if (childArray.value.description.changed === "equal") {
                        result.description = {
                            msg: childArray.value.description.value,

                        };
                    }


                    if (childArray.value.label.changed === "equal") {
                        result.label = {
                            msg: childArray.value.label.value,

                        };
                    }
                    if (childArray.value.fields.changed === "object change") {
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

    $scope.compare = function() {

        $scope.loadingSelection = true;
        $scope.msgChanged=false;
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
