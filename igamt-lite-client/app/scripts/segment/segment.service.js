/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('SegmentService', ['$rootScope', 'ViewSettings', 'ElementUtils', '$q', '$http', 'FilteringSvc', 'userInfoService','$filter', 'TableService', function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, userInfoService,$filter, TableService) {
    var SegmentService = {
        getNodes: function(parent, root) {
            var children = [];
            // if (parent && parent.type && parent.type === 'case') {
            //     var dt = $rootScope.datatypesMap[parent.datatype];
            //     children = angular.copy(dt.components);
            //     for (var i = 0, len = children.length; i < len; i++) {
            //         children[i].path = parent.path + "." + children[i].position;
            //         if(children[i].path.split(".").length - 1 == 1){
            //             children[i].fieldDT = parent.datatype.id;
            //         }else if(children[i].path.split(".").length - 1 == 2){
            //             children[i].componentDT = parent.datatype.id;
            //         }
            //     }
            // } else {
                // children = parent ? parent.fields ? parent.fields : parent.datatype ? $rootScope.datatypesMap[parent.datatype.id].components : parent.children : root != null ? root.fields : [];

                if(parent){
                    if(parent.fields){
                        children = parent.fields;
                        for (var i = 0, len = children.length; i < len; i++) {
                            children[i].path = children[i].position;
                            if($rootScope.segment){
                                children[i].sev = _.find($rootScope.segment.singleElementValues, function(sev){ return sev.location  ==  children[i].path; });
                                if(children[i].sev) children[i].sev.from = 'segment';
                            }
                        }
                    }else {
                        if(parent.datatype){
                            var dt = $rootScope.datatypesMap[parent.datatype.id];
                            children = angular.copy(dt.components);
                            for (var i = 0, len = children.length; i < len; i++) {
                                children[i].path = parent.path + "." + children[i].position;
                                if(children[i].path.split(".").length - 1 == 1){
                                    children[i].fieldDT = parent.datatype.id;
                                    if($rootScope.segment){
                                        children[i].sev = _.find($rootScope.segment.singleElementValues, function(sev){ return sev.location  ==  children[i].path; });
                                        if(children[i].sev) {
                                            children[i].sev.from = 'segment';
                                        }else {
                                            var fieldPath = children[i].path.substr(children[i].path.indexOf('.') + 1);
                                            children[i].sev = _.find($rootScope.datatypesMap[children[i].fieldDT].singleElementValues, function(sev){ return sev.location  ==  fieldPath; });
                                            if(children[i].sev) {
                                                children[i].sev.from = 'field';
                                            }
                                        }
                                    }
                                }else if(children[i].path.split(".").length - 1 == 2){
                                    children[i].fieldDT = parent.fieldDT;
                                    children[i].componentDT = parent.datatype.id;
                                    if($rootScope.segment){
                                        children[i].sev = _.find($rootScope.segment.singleElementValues, function(sev){ return sev.location  ==  children[i].path; });
                                        if(children[i].sev) {
                                            children[i].sev.from = 'segment';
                                        }else {
                                            var fieldPath = children[i].path.substr(children[i].path.indexOf('.') + 1);
                                            children[i].sev = _.find($rootScope.datatypesMap[children[i].fieldDT].singleElementValues, function(sev){ return sev.location  ==  fieldPath; });
                                            if(children[i].sev) {
                                                children[i].sev.from = 'field';
                                            }else {
                                                var componentPath = children[i].path.substr(children[i].path.split('.', 2).join('.').length + 1);
                                                children[i].sev = _.find($rootScope.datatypesMap[children[i].componentDT].singleElementValues, function(sev){ return sev.location  ==  componentPath; });
                                                if(children[i].sev) {
                                                    children[i].sev.from = 'component';
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }else {
                            children = parent.children;
                        }
                    }
                }else {
                    if(root != null){
                        children = root.fields;
                        for (var i = 0, len = children.length; i < len; i++) {
                            children[i].path = children[i].position;
                            if($rootScope.segment){
                                children[i].sev = _.find($rootScope.segment.singleElementValues, function(sev){ return sev.location  ==  children[i].path; });
                                if(children[i].sev) children[i].sev.from = 'segment';
                            }
                        }
                    }else {
                        children = [];
                    }
                }


                // if (parent && parent.datatype && $rootScope.datatypesMap[parent.datatype.id].name === 'varies') {
                //     var mapping = _.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
                //         return mapping.position == parent.position;
                //     });
                //     if (mapping) {
                //         children = mapping.cases;
                //     }
                // }
            // }

            return children;
        },
        getParent: function(child) {
            var parent = $rootScope.parentsMap && $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
            return parent;
        },
        getTemplate: function(node, root) {
            var template = null;
            if (ViewSettings.tableReadonly || (root != null && root.scope === 'HL7STANDARD') || root.scope === null) {
                return SegmentService.getReadTemplate(node, root);
            } else {
                return SegmentService.getEditTemplate(node, root);
            }
            return template;
        },

        getReadTemplate: function(node, root) {
            // var template = node.type === 'segment' ? 'SegmentReadTree.html' : node.type === 'field' ? 'SegmentFieldReadTree.html' : node.type === 'case' ? 'SegmentCaseReadTree.html' : 'SegmentComponentReadTree.html';
            var template = node.type === 'segment' ? 'SegmentReadTree.html' : node.type === 'field' ? 'SegmentFieldReadTree.html' : 'SegmentComponentReadTree.html';
            return template;
        },

        getEditTemplate: function(node, root) {
            // var template = node.type === 'segment' ? 'SegmentEditTree.html' : node.type === 'field' ? 'SegmentFieldEditTree.html' : node.type === 'case' ? 'SegmentCaseReadTree.html' : 'SegmentComponentEditTree.html';
            var template = node.type === 'segment' ? 'SegmentEditTree.html' : node.type === 'field' ? 'SegmentFieldEditTree.html' : 'SegmentComponentEditTree.html';
            return template;
        },

        getSegmentLevelConfStatements: function(element) {
            var parent = SegmentService.getParent(element.id);
            var conformanceStatements = [];
            if (parent && parent != null && parent.conformanceStatements.length > 0) {
                return ElementUtils.filterConstraints(element, parent.conformanceStatements);
            }
            return conformanceStatements;
        },

        getSegmentLevelPredicates: function(element) {
            var parent = SegmentService.getParent(element.id);
            var predicates = [];
            if (parent && parent != null && parent.predicates.length > 0) {
                return ElementUtils.filterConstraints(element, parent.predicates);
            }
            return predicates;
        },

        isBranch: function(node) {
            var children = SegmentService.getNodes(node);
            return children != null && children.length > 0;
        },
        isVisible: function(node) {
            //return FilteringSvc.show(node);
            return true;

            //                 return  node ? SegmentService.isRelevant(node) ? SegmentService.isVisible(SegmentService.getParent(node)) : false : true;
        },

        isRelevant: function(node) {
            if (node === undefined || !ViewSettings.tableRelevance)
                return true;
            if (node.hide == undefined || !node.hide || node.hide === false) {
                var predicates = SegmentService.getSegmentLevelPredicates(node);
                return ElementUtils.isRelevant(node, predicates);
            } else {
                return false;
            }
        },

        save: function(segment) {
            var delay = $q.defer();
            segment.accountId = userInfoService.getAccountID();
            $http.post('api/segments/save', segment).then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                segment.date = saveResponse.date;
                segment.version = saveResponse.version;
                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        crossRef: function(segmentId, igDocumentId) {
            var delay = $q.defer();
            var wrapper = {};
            wrapper.segmentId = segmentId;
            wrapper.igDocumentId = igDocumentId;
            $http.post('api/crossRefs/segment', wrapper).then(function(response) {
                var ref = angular.fromJson(response.data);
                delay.resolve(ref);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        saves: function(segments) {
            var delay = $q.defer();
            for (var i = 0; i < segments.length; i++) {
                segments[i].accountId = userInfoService.getAccountID();
            }

            $http.post('api/segments/saveSegs', segments).then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                for (var i = 0; i < segments.length; i++) {
                    segments[i].date = saveResponse[i].date;
                    segments[i].version = saveResponse[i].version;
                }

                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        get: function(id) {
            var delay = $q.defer();
            if ($rootScope.segmentsMap[id] === undefined || $rootScope.segmentsMap[id] === undefined) {
                $http.get('api/segments/' + id).then(function(response) {
                    var segment = angular.fromJson(response.data);
                    delay.resolve(segment);
                }, function(error) {
                    delay.reject(error);
                });
            } else {
                delay.resolve($rootScope.segmentsMap[id]);
            }
            return delay.promise;
        },
        getSegmentsByScopesAndVersion: function(scopes, hl7Version) {
            console.log("segments/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
            var scopesAndVersion = {
                "scopes": scopes,
                "hl7Version": hl7Version
            };
            return $http.post(
                    'api/segments/findByScopesAndVersion', angular.toJson(scopesAndVersion))
                .then(function(response) {
                    console.log("getSegmentsByScopesAndVersion response size=" + response.data.length);
                    return angular.fromJson(response.data);
                });
        },

        merge: function(to, from) {
            console.log("to");
            console.log(to);
            to = angular.extend(to, from);
            //            to.name = from.name;
            //            to.ext = from.ext;
            //            to.label = from.label;
            //            to.description = from.description;
            //            to.status = from.status;
            //            to.comment = from.comment;
            //            to.usageNote = from.usageNote;
            //            to.scope = from.scope;
            //            to.hl7Version = from.hl7Version;
            //            to.accountId = from.accountId;
            //            to.participants = from.participants;
            //            to.libIds = from.libIds;
            //            to.predicates = from.predicates;
            //            to.conformanceStatements = from.conformanceStatements;
            //            to.sectionPosition = from.sectionPosition;
            //            to.fields = from.fields;
            //            to.version = from.version;
            //            to.date = from.date;
            //            to.purposeAndUse = from.purposeAndUse;
            //            to.coConstraints = to.coConstraints;
            return to;
        },

        delete: function(segment) {
            var delay = $q.defer();
            $http.post('api/segments/' + segment.id + '/delete').then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        getSegmentLink: function(segment) {
            return { id: segment.id, ext: segment.ext, name: segment.name };
        },

        findByIds: function(ids) {
            var delay = $q.defer();
            $http.post('api/segments/findByIds', ids).then(function(response) {
                var datatypes = angular.fromJson(response.data);
                delay.resolve(datatypes);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        collectDatatypes: function(id) {
            var delay = $q.defer();
            $http.get('api/segments/' + id + '/datatypes').then(function(response) {
                var datatypes = angular.fromJson(response.data);
                delay.resolve(datatypes);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        saveNewElements: function() {
            var delay = $q.defer();
            var datatypeLinks = ElementUtils.getNewDatatypeLinks();
            if (datatypeLinks.length > 0) {
                DatatypeLibrarySvc.addChildren($rootScope.igdocument.profile.datatypeLibrary.id, datatypeLinks).then(function() {
                    $rootScope.igdocument.profile.datatypeLibrary.children = $rootScope.igdocument.profile.datatypeLibrary.children.concat(datatypeLinks);
                    _.each($rootScope.addedDatatypes, function(datatype) {
                        if (ElementUtils.indexIn(datatype.id, $rootScope.datatypes) < 0) {
                            $rootScope.datatypes.push(datatype);
                        }
                    });
                    var tableLinks = ElementUtils.getNewTableLinks();
                    if (tableLinks.length > 0) {
                        TableLibrarySvc.addChildren($rootScope.igdocument.profile.tableLibrary.id, tableLinks).then(function() {
                            $rootScope.igdocument.profile.tableLibrary.children = $rootScope.igdocument.profile.tableLibrary.children.concat(tableLinks);
                            _.each($rootScope.addedTables, function(table) {
                                if (ElementUtils.indexIn(table.id, $rootScope.tables) < 0) {
                                    $rootScope.tables.push(table);
                                }
                            });
                            SegmentService.completeSave();
                            delay.resolve(true);
                        }, function(error) {
                            delay.reject(error);
                        });
                    } else {
                        SegmentService.completeSave();
                        delay.resolve(true);
                    }
                }, function(error) {
                    delay.reject(error);
                });
            } else {
                SegmentService.completeSave();
                delay.resolve(true);
            }
            return delay.promise;
        },

        completeSave: function() {
            $rootScope.addedDatatypes = [];
            $rootScope.addedTables = [];
            $rootScope.clearChanges();
            $rootScope.msg().text = "segmentSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        },

        reset: function() {
            if ($rootScope.addedDatatypes != null && $rootScope.addedDatatypes.length > 0) {
                _.each($rootScope.addedDatatypes, function(id) {
                    delete $rootScope.datatypesMap[id];
                });
            }
            if ($rootScope.addedTables != null && $rootScope.addedTables.length > 0) {
                _.each($rootScope.addedTables, function(id) {
                    delete $rootScope.tablesMap[id];
                });
            }
            $rootScope.segment = angular.copy($rootScope.segmentsMap[$rootScope.segment.id]);
            $rootScope.segment.fields = $filter('orderBy')($rootScope.segment.fields, 'position');

            SegmentService.initCoConstraintsTable();
        },

        updateTableBinding: function(segmentUpdateParameterList) {
            var delay = $q.defer();
            $http.post('api/segments/updateTableBinding/', segmentUpdateParameterList).then(function(response) {
                delay.resolve(true);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        updateDatatypeBinding: function(segmentUpdateParameterList) {
            var delay = $q.defer();
            $http.post('api/segments/updateDatatypeBinding/', segmentUpdateParameterList).then(function(response) {
                delay.resolve(true);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        updateDynamicMappingInfo: function () {
            var delay = $q.defer();
            $rootScope.isDynamicMappingSegment = false;
            var mappingStructure = _.find($rootScope.config.variesMapItems, function (item) {
                return item.hl7Version == $rootScope.segment.hl7Version && item.segmentName == $rootScope.segment.name;
            });

            if (mappingStructure) {
                $rootScope.isDynamicMappingSegment = true;
                console.log("=========This is DM segment!!=========");

                if ($rootScope.segment.dynamicMappingDefinition && $rootScope.segment.dynamicMappingDefinition.mappingStructure) {
                    console.log("=========Found mapping structure!!=========");
                    mappingStructure = $rootScope.segment.dynamicMappingDefinition.mappingStructure;
                } else {
                    console.log("=========Not Found mapping structure and Default setting will be used!!=========");
                    $rootScope.segment.dynamicMappingDefinition = {};
                    $rootScope.segment.dynamicMappingDefinition.mappingStructure = mappingStructure;
                }

                var valueSetBinding = _.find($rootScope.segment.valueSetBindings, function (vsb) {
                    return vsb.location == mappingStructure.referenceLocation;
                });

                if (valueSetBinding) {
                    TableService.getOne(valueSetBinding.tableId).then(function (tbl) {
                        delay.resolve(tbl);
                    }, function () {
                        delay.resolve(null);
                    });
                } else {
                    delay.resolve(null);
                }
            } else {
                delay.resolve(null);
            }

            return delay.promise;
        },
        initCoConstraintsTable: function (segment) {
            var time0 = new Date();
            console.log("initCoConstraintsTable Start: " + time0.getHours() + ":" + time0.getMinutes() + ":" + time0.getSeconds() + ":" + time0.getMilliseconds());
            console.log(segment);
            var delay = $q.defer();
            if (segment && segment.coConstraintsTable) {
                if (segment.scope === 'USER' && segment.name === 'OBX') {
                    if (!segment.coConstraintsTable.ifColumnDefinition || !segment.coConstraintsTable.thenColumnDefinitionList || segment.coConstraintsTable.thenColumnDefinitionList.length === 0) {
                        var field2 = null;
                        var field3 = null;
                        var field5 = null;

                        angular.forEach(segment.fields, function (field) {
                            if (field.position === 2) {
                                field2 = field;
                            } else if (field.position === 3) {
                                field3 = field;
                            } else if (field.position === 5) {
                                field5 = field;
                            }
                        });

                        var ifColumnDefinition = {
                            id: new ObjectId().toString(),
                            path: "3",
                            constraintPath: "3[1]",
                            type: "field",
                            constraintType: "value",
                            name: field3.name,
                            usage: field3.usage,
                            dtId: field3.datatype.id,
                            primitive: false,
                            dMReference: false
                        };

                        var field2ColumnDefinition = {
                            id: new ObjectId().toString(),
                            path: "2",
                            constraintPath: "2[1]",
                            type: "field",
                            constraintType: "dmr",
                            name: field2.name,
                            usage: field2.usage,
                            dtId: field2.datatype.id,
                            primitive: true,
                            dMReference: true
                        };

                        var field5ColumnDefinition = {
                            id: new ObjectId().toString(),
                            path: "5",
                            constraintPath: "5[1]",
                            type: "field",
                            constraintType: "valueset",
                            name: field5.name,
                            usage: field5.usage,
                            dtId: field5.datatype.id,
                            primitive: true,
                            dMReference: false
                        };
                        var thenColumnDefinitionList = [];
                        thenColumnDefinitionList.push(field2ColumnDefinition);
                        thenColumnDefinitionList.push(field5ColumnDefinition);

                        var userColumnDefinitionList = [];
                        var userColumnDefinition = {
                            id: new ObjectId().toString(),
                            title: "Comments"
                        };
                        userColumnDefinitionList.push(userColumnDefinition);

                        segment.coConstraintsTable.ifColumnDefinition = ifColumnDefinition;
                        segment.coConstraintsTable.thenColumnDefinitionList = thenColumnDefinitionList;
                        segment.coConstraintsTable.userColumnDefinitionList = userColumnDefinitionList;

                        if (!segment.coConstraintsTable.ifColumnData) segment.coConstraintsTable.ifColumnData = [];
                        if (!segment.coConstraintsTable.thenMapData) segment.coConstraintsTable.thenMapData = {};
                        if (!segment.coConstraintsTable.userMapData) segment.coConstraintsTable.userMapData = {};
                        if (!segment.coConstraintsTable.rowSize) segment.coConstraintsTable.rowSize = 0;

                        /*
                        var isAdded = false;
                        if (segment.coConstraintsTable.ifColumnDefinition) {
                            var newIFData = {};
                            newIFData.valueData = {};
                            newIFData.bindingLocation = null;

                            segment.coConstraintsTable.ifColumnData.push(newIFData);
                            isAdded = true;
                        }

                        if (segment.coConstraintsTable.thenColumnDefinitionList) {
                            for (var i = 0, len1 = segment.coConstraintsTable.thenColumnDefinitionList.length; i < len1; i++) {
                                var thenColumnDefinition = segment.coConstraintsTable.thenColumnDefinitionList[i];

                                var newTHENData = {};
                                newTHENData.valueData = {};
                                newTHENData.valueSets = [];

                                if (!segment.coConstraintsTable.thenMapData[thenColumnDefinition.id]) segment.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

                                segment.coConstraintsTable.thenMapData[thenColumnDefinition.id].push(newTHENData);
                                isAdded = true;
                            }
                            ;
                        }

                        if (segment.coConstraintsTable.userColumnDefinitionList) {
                            for (var i = 0, len1 = segment.coConstraintsTable.userColumnDefinitionList.length; i < len1; i++) {
                                var userColumnDefinition = segment.coConstraintsTable.userColumnDefinitionList[i];

                                var newUSERData = {};
                                newUSERData.text = "";

                                if (!segment.coConstraintsTable.userMapData[userColumnDefinition.id]) segment.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

                                segment.coConstraintsTable.userMapData[userColumnDefinition.id].push(newUSERData);
                                isAdded = true;
                            }
                        }

                        if (isAdded) {
                            segment.coConstraintsTable.rowSize = segment.coConstraintsTable.rowSize + 1;
                        }
                        */
                    }
                    if (segment.coConstraintsTable.thenColumnDefinitionList) {

                        segment.coConstraintsTable.thenColumnDefinitionListForDisplay = [];
                        for (var i in segment.coConstraintsTable.thenColumnDefinitionList) {
                            var def = segment.coConstraintsTable.thenColumnDefinitionList[i];

                            if (def.constraintType === 'dmr') {
                                segment.coConstraintsTable.thenColumnDefinitionListForDisplay.push(def);
                                var clone = angular.copy(def);
                                clone.constraintType = 'dmf';
                                segment.coConstraintsTable.thenColumnDefinitionListForDisplay.push(clone);
                            } else {
                                segment.coConstraintsTable.thenColumnDefinitionListForDisplay.push(def);
                            }
                        }
                    }
                    console.log(segment.coConstraintsTable);
                    delay.resolve(segment.coConstraintsTable);
                }else {
                    delay.resolve(null);
                }
            }else {
                delay.resolve(null);
            }
            var time1 = new Date();
            console.log("initCoConstraintsTable End: " + time1.getHours() + ":" + time1.getMinutes() + ":" + time1.getSeconds() + ":" + time1.getMilliseconds());
            return delay.promise;
        },
        initRowIndexForCocon: function(coConstraintsTable) {
            var delay = $q.defer();
            if(coConstraintsTable){
                var coConRowIndexList = [];
                for (var i = 0, len1 = coConstraintsTable.rowSize; i < len1; i++) {
                    var rowIndexObj = {};
                    rowIndexObj.rowIndex = i;
                    rowIndexObj.id = new ObjectId().toString();
                    coConRowIndexList.push(rowIndexObj);
                }
                delay.resolve(coConRowIndexList);
            }else {
                delay.resolve([]);
            }
            return delay.promise;
        }
    };
    return SegmentService;
}]);