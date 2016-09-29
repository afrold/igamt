/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('SegmentService', ['$rootScope', 'ViewSettings', 'ElementUtils', '$q', '$http', 'FilteringSvc', 'userInfoService', function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, userInfoService) {
    var SegmentService = {
        getNodes: function(parent, root) {
            var children = [];

            if (parent && parent.type && parent.type === 'case') {
                children = $rootScope.datatypesMap[parent.datatype].components;
            } else {
                children = parent ? parent.fields ? parent.fields : parent.datatype ? $rootScope.datatypesMap[parent.datatype.id].components : parent.children : root != null ? root.fields : [];

                if (parent && parent.datatype && $rootScope.datatypesMap[parent.datatype.id].name === 'varies') {
                    var mapping = _.find($rootScope.segment.dynamicMapping.mappings, function(mapping) {
                        return mapping.position == parent.position;
                    });
                    if (mapping) children = mapping.cases;
                }
            }

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
            var template = node.type === 'segment' ? 'SegmentReadTree.html' : node.type === 'field' ? 'SegmentFieldReadTree.html' : node.type === 'case' ? 'SegmentCaseReadTree.html' : 'SegmentComponentReadTree.html';
            return template;
        },

        getEditTemplate: function(node, root) {
            var template = node.type === 'segment' ? 'SegmentEditTree.html' : node.type === 'field' ? 'SegmentFieldEditTree.html' : node.type === 'case' ? 'SegmentCaseReadTree.html' : 'SegmentComponentEditTree.html';
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
        }
    };
    return SegmentService;
}]);