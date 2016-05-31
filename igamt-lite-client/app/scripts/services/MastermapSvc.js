'use strict';
angular.module('igl').service('MastermapSvc', function($rootScope) {
    var svc = {};
    svc.mastermap = [];
    svc.segmentLibrary = {};
    svc.datatypeLibrary = {};
    svc.tableLibrary = {};
    svc.parseIg = function(igdocument) {
        if (igdocument !== null && igdocument !== undefined) {
            svc.addIG(igdocument);
            return svc.getMastermap();
        }
        return [];
    };
    svc.getMastermap = function() {
        return this.mastermap;
    };
    svc.getSegmentLibrary = function() {
        return this.segmentLibrary;
    };
    svc.setSegmentLibrary = function(segmentLibrary) {
        this.segmentLibrary = segmentLibrary;
    };
    svc.getDatatypeLibrary = function() {
        return this.datatypeLibrary;
    };
    svc.setDatatypeLibrary = function(datatypeLibrary) {
        this.datatypeLibrary = datatypeLibrary;
    };
    svc.getTableLibrary = function() {
        return this.tableLibrary;
    };
    svc.setTableLibrary = function(tableLibrary) {
        this.tableLibrary = tableLibrary;
    };
    svc.addComponent = function(c, parent) {
        if (c !== undefined && c !== null) {
            svc.createMMElement(c.id, "component");
            svc.addParentsId(c.id, "component", parent);
            if (svc.getElementByKey(c.id, "component", "usage") !==
                undefined) {
                svc.setElement(c.id, "component", "usage", [c[
                    "usage"]]);
            }
            if (c.datatype !== undefined && svc.getDatatypeLibrary()[
                c.datatype] !== null) {
                svc.addDatatypeObject(svc.getDatatypeLibrary()[c.datatype],
                    parent.concat([
                        [c.id, "component"]
                    ]));
            }
            if (c.table !== undefined && svc.getTableLibrary()[c.table] !==
                null) {
                svc.addValueSetObject(svc.getTableLibrary()[c.table],
                    parent.concat([
                        [c.id, "component"]
                    ]));
            }
        }
    };
    svc.deleteComponent = function(componentId) {
        svc.removeMastermapElt(componentId, "component");
    }
    svc.addValueSetId = function(tableId, parent) {
        if (tableId !== undefined && tableId !== null && tableId !==
            "") {
            svc.createMMElement(tableId, "table");
            svc.addParentsId(tableId, "table", parent);
            //            var table = svc.getTableLibrary()[tableId];
            //            if (table !== undefined){
            //                _.each(table.codes, function(c) {
            //                    svc.addCodes(c, parent.concat([[table.id, 'table']]));
            //                });
            //            } else {
            //                // Table not found
            //                // console.log("!!! => table " + tableId + " not found in library");
            //            }
        }
    };
    svc.addValueSetObject = function(table, parent) {
        if (table !== undefined && table !== null) {
            svc.createMMElement(table.id, "table");
            svc.addParentsId(table.id, "table", parent);
            //            _.each(table.codes, function(c) {
            //                svc.addCodes(c, parent.concat([[table.id, 'table']]));
            //            });
        };
    };
    svc.deleteValueset = function(tableId) {
        svc.removeMastermapElt(tableId, "table");
    }
    svc.addCodes = function(code, parent) {
        svc.createMMElement(code.id, "code");
        svc.addParentsId(code.id, "code", parent);
    }
    svc.addDatatypeId = function(datatypeId, parent) {
        // Add id then check if in library
        if (datatypeId !== undefined && datatypeId !== null &&
            datatypeId !== "") {
            svc.createMMElement(datatypeId, "datatype");
            svc.addParentsId(datatypeId, "datatype", parent);
            var dt = svc.getDatatypeLibrary()[datatypeId];
            if (dt !== undefined && dt !== null && dt !== "") {
                _.each(dt.components, function(c) {
                    svc.addComponent(c, parent.concat([
                        [dt.id, "datatype"]
                    ]));
                });
            }
        }
    }
    svc.addDatatypeObject = function(dtLink, parent) {
        if (dtLink !== undefined && dtLink !== null) {
            svc.createMMElement(dtLink.id, "datatype");
            svc.addParentsId(dtLink.id, "datatype", parent);
            var dt = svc.getDatatypeLibrary()[dtLink.id];
            if (dt !== undefined && dt !== null && dt !== "") {
                _.each(dt.components, function(c) {
                    svc.addComponent(c, parent.concat([
                        [dtLink.id, "datatype"]
                    ]));
                });
            }
        }
    }
    svc.deleteDatatype = function(datatypeId) {
        svc.removeMastermapElt(datatypeId, "datatype");
    }
    svc.deleteElementChildren = function(elementId, elementType, childrenToBeRemoved, childrenType) {
        svc.removeId(childrenToBeRemoved, svc.getElementByKey(elementId, elementType, childrenType));
    }
    svc.addFieldObject = function(field, parent) {
        if (field !== undefined && field !== null) {
            svc.createMMElement(field.id, "field");
            svc.addParentsId(field.id, "field", parent);
            svc.setElement(field.id, "field", "usage", [field[
                "usage"]]);
            svc.addValueSetObject(field.table, parent.concat([
                [field.id, "field"]
            ]));
            svc.addDatatypeObject(field.datatype, parent.concat([
                [field.id, "field"]
            ]));
        }
    }
    svc.addSegmentId = function(segmentId, parent) {
        // Add the id in mastermap when given. Then check if object can be found in segment library.
        if (segmentId !== undefined && segmentId !== null &&
            segmentId !== "") {
            svc.createMMElement(segmentId, "segment");
            svc.addParentsId(segmentId, "segment", parent);
            var segment = svc.getSegmentLibrary()[segmentId];
            if (segment !== undefined && segment !== null) {
                _.each(segment.fields, function(field) {
                    svc.addFieldObject(field, parent.concat(
                        [
                            [segmentId, "segment"]
                        ]));
                });
            }
        }
    }
    svc.addSegmentObject = function(segmentLink, parent) {
        console.log(segmentLink);
        if (segmentLink !== undefined && segmentLink !== null) {
            svc.createMMElement(segmentLink.id, "segment");
            svc.addParentsId(segmentLink.id, "segment", parent);
            var segment = svc.getSegmentLibrary()[segmentLink.id];
            if (segment !== undefined && segment !== null) {
                _.each(segment.fields, function(f) {
                    svc.addFieldObject(f, parent.concat([
                        [segmentLink.id, "segment"]
                    ]));
                });
            }
        }
    }
    svc.deleteSegment = function(segmentId) {
        svc.removeMastermapElt(segmentId, "segment");
    }
    svc.deleteMessage = function(messageId) {
        svc.removeMastermapElt(messageId, "message");
    }
    svc.addMessageObject = function(message, parent) {
        svc.createMMElement(message.id, "message");
        svc.addParentsId(message.id, "message", parent);
        var tmp = svc.getElementByKey(message.id, "message",
            "usage");
        _.each(message.children, function(segrefOrGroup) {
            if (segrefOrGroup.type === "segmentRef") {
                svc.addSegmentRef(segrefOrGroup, parent.concat(
                    [
                        [message.id, 'message']
                    ]));
            } else {
                svc.addGroup(segrefOrGroup, parent.concat([
                    [message.id, 'message']
                ]));
            }
            svc.addInArray(segrefOrGroup.usage, tmp);
        });
        svc.getElement(message.id, "message").usage = tmp;
    }
    svc.addSegmentRef = function(segmentRef, parent) {
        if (segmentRef !== undefined && segmentRef !== null) {
            if (segmentRef.id !== undefined && segmentRef.id !==
                null) {
                svc.createMMElement(segmentRef.id, "segmentRef");
                svc.addParentsId(segmentRef.id, "segmentRef",
                    parent);
                svc.setElement(segmentRef.id, "segmentRef", "usage", [
                    segmentRef["usage"]
                ]);
                if (segmentRef.ref.id !== undefined && segmentRef.ref
                    .id !== null) {
                    svc.addSegmentId(segmentRef.ref.id, parent.concat(
                        [
                            [segmentRef.id, "segmentRef"]
                        ]));
                }
            }
        }
    }
    svc.deleteSegmentRef = function(segmentRefId) {
        svc.removeMastermapElt(segmentRefId, "segmentRef");
    }
    svc.addGroup = function(group, parent) {
        if (group !== undefined && group !== null) {
            svc.createMMElement(group.id, "group");
            svc.addParentsId(group.id, "group", parent);
            if (group["usage"] !== undefined && group["usage"] !==
                null && group["usage"] !== "") {
                svc.setElement(group.id, "group", "usage", [group[
                    "usage"]]);
            }
            _.each(group.children, function(n) {
                if (n.type === "segmentRef") {
                    svc.addSegmentRef(n, parent.concat([
                        [group.id, 'group']
                    ]));
                } else {
                    svc.addGroup(n, parent.concat([
                        [group.id, 'group']
                    ]));
                }
            });
        }
    }
    svc.addIG = function(igdocument) {
        //console.log("Creating mastermap\nprocessing IG : " + igdocument.id);
        svc.mastermap = [];

        var profile = igdocument.profile;

        // Setting libraries
        svc.createSegmentLibrary($rootScope.segmentsMap);
        svc.createDatatypeLibrary($rootScope.datatypesMap);
        svc.createTableLibrary($rootScope.tablesMap);

        // Initializing mastermap
        _.each(svc.getTableLibrary(), function(tbl) {
            svc.createMMElement(tbl.id, "table");
        });
        _.each(svc.getDatatypeLibrary(), function(dt) {
            //console.log(dt);
            svc.createMMElement(dt.id, "datatype");
        });
        _.each(svc.getSegmentLibrary(), function(sgt) {

            //console.log(sgt);
            svc.createMMElement(sgt.id, "segment");
        });

        // Adding reference to IG document
        svc.createMMElement(igdocument.id, "ig");
        svc.addParentsId(igdocument.id, "ig", [
            [igdocument.id, "ig"]
        ]);

        // Adding reference to profile
        var parents = [
            [igdocument.id, "ig"]
        ];
        svc.createMMElement(profile.id, "profile");
        svc.addParentsId(profile.id, "profile", parent);

        // Adding message content in mastermap
        parents = parents.concat([
            [profile.id, "profile"]
        ]);
        _.each(profile.messages.children, function(msg) {
            svc.addMessageObject(msg, parents);
        });

        //        try {
        //            console.log("mm");
        //            console.log(svc.getMastermap());
        //        } catch (err) {
        //                console.log(err);
        //        }
    }
    svc.deleteIgdocument = function(igdocumentId) {
        svc.removeMastermapElt(igdocumentId, "ig");
    }
    svc.deleteProfile = function(profileId) {
        svc.removeMastermapElt(profileId, "profile");
    }
    svc.addParentsId = function(elementId, elementType, parentsList) {
        // Element refers to self
//        svc.setElement(elementId, elementType, elementType, svc.getElementByKey(
//            elementId, elementType, elementType).concat(
//            elementId));
        _.each(parentsList, function(parent) {
            var parentId = parent[0];
            var parentType = parent[1];
            //Add parents reference in element if not present
            if (svc.getElementByKey(elementId, elementType,
            parentType) !== undefined || svc.getElementByKey(elementId,
            elementType, parentType) !== null) {
                if (svc.getElementByKey(elementId, elementType,
                    parentType).indexOf(parentId) === -1) {
                    svc.setElement(elementId, elementType,
                        parentType, svc.getElementByKey(
                            elementId, elementType,
                            parentType).concat(parentId));
                }
            }
//            // Add element reference in parents if not already present
//            if (svc.getElementByKey(parentId, parentType,
//                elementType).indexOf(elementId) === -1) {
//                svc.setElement(parentId, parentType,
//                    elementType, svc.getElementByKey(
//                        parentId, parentType,
//                        elementType).concat(elementId));
//            }
        });
    }
    svc.createMMElement = function(id, type) {
        if (id !== null && id !== undefined && svc.getElement(id, type) === undefined) {
            var eltColl = new Object();
            eltColl["ig"] = [];
            eltColl["profile"] = [];
            eltColl["message"] = [];
            eltColl["field"] = [];
            eltColl["segment"] = [];
            eltColl["segmentRef"] = [];
            eltColl["group"] = [];
            eltColl["table"] = [];
            eltColl["datatype"] = [];
            eltColl["component"] = [];
            eltColl["code"] = [];
            eltColl["usage"] = [];
            eltColl["scope"] = [];
            eltColl["type"] = type;
            eltColl["id"] = id;
            svc.mastermap[id.concat(type)] = eltColl;
        }
        else {
        //console.log("null id")
        }
    }
    svc.removeMastermapElt = function(toBeRemovedId, toBeRemovedType) {
        // Collects elements about elements to delete
        var toBeRemovedInfo = svc.getElement(toBeRemovedId,
            toBeRemovedType);
        var areas = ["ig", "profile", "message", "field", "segment",
            "segmentRef", "group", "table", "datatype",
            "component", "code"
        ];
        // Remove element in parents
        _.each(areas, function(area) {
            if (toBeRemovedInfo[area] !== null || toBeRemovedInfo[area] !== undefined){
                _.each(toBeRemovedInfo[area], function(idParent) {
                    svc.removeId(idParent, svc.getElementByKey(
                        idParent, area,
                        toBeRemovedType));
                });
            }
        });
        // Remove element in mastermap
        svc.removeId(toBeRemovedId.concat(toBeRemovedType), svc.getMastermap());
    }
    svc.createSegmentLibrary = function(segmentsLibrary) {
        svc.segmentLibrary = segmentsLibrary;
    }
    svc.createTableLibrary = function(tablesLibrary) {
        svc.tableLibrary = tablesLibrary;
    }
    svc.createDatatypeLibrary = function(datatypesLibrary) {
        svc.datatypeLibrary = datatypesLibrary;
    }
    svc.getElement = function(id, type) {
        if (id !== null || id !== undefined) {
            return svc.mastermap[id.concat(type)];
        } else {
            //console.log(type);
            return null;
        }
    }
    svc.getElementByKey = function(id, type, key) {
        return svc.getElement(id, type)[key];
    }
    svc.setElement = function(id, type, key, value) {
        svc.mastermap[id.concat(type)][key] = value;
    }
    svc.removeId = function(idKey, myArray) {
    if (idKey !== null || idKey !== undefined || myArray !== null || myArray !== undefined) {
            var index = myArray.indexOf(idKey);
            if (index !== -1) {
                myArray.splice(index, 1);
            }
        }
    };
    svc.searchById = function(idKey, myArray) {
        for (var i = 0; i < myArray.length; i++) {
            if (myArray[i].id === idKey) {
                return i;
            }
        }
        return undefined;
    };
    svc.addInArray = function(elt, myArray) {
        if (myArray.indexOf(elt) === -1) {
            myArray.push(elt);
        }
    }
    svc.getUsage = function(id, type) {
        var item = svc.getElement(id, type);
        if (item !== undefined) {
            if (type === "message") {
                // usage is union of first level usages of segmentRefs or groups.
                // usage is union of first level usages of segmentRefs or groups.
                return svc.getElementByKey(id, type, "usage");
            }
            if (type === "field" || type === "segmentRef" || type ===
                "group" || type === "component") {
                // usage is set in element
                return svc.getElementByKey(id, type, "usage");
            }
            if (type === "segment") {
                var sgt = svc.getElement(id, type);
                var rst = [];
                var usg = "";
                _.each(sgt["segmentRef"], function(elt) {
                    usg = svc.getElementByKey(elt,
                        "segmentRef", "usage");
                    if (rst.indexOf(usg) === -1) rst.push(
                        usg);
                });
                return rst;
            }
            if (type === "table") {
                var tbl = svc.getElement(id, type);
                var rst = [];
                _.each(tbl["segment"], function(elt) {
                    var usgs = svc.getUsage(elt, "segment");
                    _.each(usgs, function(usg) {
                        if (rst.indexOf(usg) === -1) {
                            rst.push(usg);
                        }
                    });
                });
                _.each(tbl["datatype"], function(elt) {
                    var usgs = svc.getUsage(elt, "datatype");
                    _.each(usgs, function(usg) {
                        if (rst.indexOf(usg) === -1) {
                            rst.push(usg);
                        }
                    });
                });
                return rst;
            }
            if (type === "datatype") {
                var dt = svc.getElement(id, type);
                var rst = [];
                var usg = "";
                _.each(dt["segment"], function(elt) {
                    var usgs = svc.getUsage(elt, "segment");
                    _.each(usgs, function(usg) {
                        if (rst.indexOf(usg) === -1) {
                            rst.push(usg);
                        }
                    });
                });
                _.each(dt["datatype"], function(elt) {
                    var usgs = svc.getUsage(elt, "datatype");
                    _.each(usgs, function(usg) {
                        if (rst.indexOf(usg) === -1) {
                            rst.push(usg);
                        }
                    });
                });
                return rst;
            }
            if (type === "code") {
                var cd = svc.getElement(id, type);
                var rst = [];
                var usg = "";
                _.each(cd["table"], function(elt) {
                    var usgs = svc.getUsage(elt, "table");
                    _.each(usgs, function(usg) {
                        if (rst.indexOf(usg) === -1) {
                            rst.push(usg);
                        }
                    });
                });
                return rst;
            }
        }
    }
    return svc;
});