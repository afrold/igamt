/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('DatatypeService',
    function($rootScope, ViewSettings, ElementUtils, $http, $q, FilteringSvc, userInfoService, TableLibrarySvc, DatatypeLibrarySvc) {
        var DatatypeService = {
            getNodes: function(parent, root) {
                var children = [];
                if (parent && parent != null) {
                    if (parent.datatype) {
                        var dt = $rootScope.datatypesMap[parent.datatype.id];
                        children = dt.components;
                    } else {
                        children = parent.components;
                    }
                } else {
                    if (root != null) {
                        children = root.components;
                    } else {
                        children = [];
                    }
                }
                return children;
            },
            getDatatypeNodesInLib: function(parent, root) {
                console.log(root);
                var children = [];
                if (parent && parent != null) {
                    if (parent.datatype) {
                        var dt = $rootScope.datatypesMap[parent.datatype.id];
                        children = dt.components;
                    } else {
                        children = parent.components;
                    }
                } else {
                    if (root != null) {
                        children = root.components;
                    } else {
                        children = [];
                    }
                }
                console.log(children);
                return children;
            },

            // body...

            getParent: function(child) {
                var template = $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
                return template;
            },
            getTemplate: function(node, root) {
                if (ViewSettings.tableReadonly || root != null && root.scope === 'HL7STANDARD' || root.scope === 'MASTER' || root.scope === null) {
                    return DatatypeService.getReadTemplate(node, root);
                } else {
                    //console.log("INTO THE NODES ")
                    //console.log(node);
                    //console.log(root);
                    return DatatypeService.getEditTemplate(node, root);
                }
            },

            getReadTemplate: function(node, root) {
                return node.type === 'Datatype' ? 'DatatypeReadTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeComponentReadTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeSubComponentReadTree.html' : '';
            },

            getEditTemplate: function(node, root) {
                return node.type === 'Datatype' ? 'DatatypeEditTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeComponentEditTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeSubComponentEditTree.html' : '';
            },


            getTemplateINLIB: function(node, root) {
                if ($rootScope.readOnly || root != null && root.scope === 'HL7STANDARD' || root.scope === null || root != null && root.status === 'PUBLISHED') {
                    return DatatypeService.getReadTemplateINLIB(node, root);
                } else {
                    //console.log("INTO THE NODES ")
                    //console.log(node);
                    //console.log(root);
                    return DatatypeService.getEditTemplateINLIB(node, root);
                }
            },

            getReadTemplateINLIB: function(node, root) {
                return node.type === 'Datatype' ? 'DatatypeReadTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeComponentReadTreeINLIB2.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeSubComponentReadTreeINLIB2.html' : '';
            },

            getEditTemplateINLIB: function(node, root) {
                return node.type === 'Datatype' ? 'DatatypeEditTreeINLIB2.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeComponentEditTreeINLIB2.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeSubComponentEditTreeINLIB2.html' : '';
            },



            isDatatypeSubDT: function(component, root) {
                if (root != null) {
                    for (var i = 0, len = root.components.length; i < len; i++) {
                        if (root.components[i].id === component.id)
                            return false;
                    }
                }
                return true;
            },
            isBranch: function(node) {
                var children = DatatypeService.getNodes(node);
                return children != null && children.length > 0;
            },
            isVisible: function(node) {
                //                return FilteringSvc.show(node);
                return true;
                //                return  node ? DatatypeService.isRelevant(node) ? DatatypeService.isVisible(DatatypeService.getParent(node)) : false : true;
            },
            isRelevant: function(node) {
                if (node === undefined || !ViewSettings.tableRelevance)
                    return true;
                if (node.hide == undefined || !node.hide || node.hide === false) {
                    var predicates = DatatypeService.getDatatypeLevelPredicates(node);
                    return ElementUtils.isRelevant(node, predicates);
                } else {
                    return false;
                }
            },
            getDatatypeLevelConfStatements: function(element) {
                var datatype = DatatypeService.getParent(element);
                var confStatements = [];
                if (datatype && datatype != null && datatype.conformanceStatements.length > 0) {
                    return ElementUtils.filterConstraints(element, datatype.conformanceStatements);
                }
                return confStatements;
            },

            getDatatypeLevelPredicates: function(element) {
                var datatype = DatatypeService.getParent(element);
                var predicates = [];
                if (datatype && datatype != null && datatype.predicates.length > 0) {
                    return ElementUtils.filterConstraints(element, datatype.predicates);
                }
                return predicates;
            },
            save: function(datatype) {
            	console.log(datatype);
                var delay = $q.defer();
                datatype.accountId = userInfoService.getAccountID();
                $http.post('api/datatypes/save', datatype).then(function(response) {
                    var saveResponse = angular.fromJson(response.data);
                     datatype.dateUpdated = saveResponse.dateUpdated;
                    datatype.version = saveResponse.version;
                    datatype.id = saveResponse.id;
                    delay.resolve(saveResponse);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
            },
            saves: function(datatypes) {
                var delay = $q.defer();
                for (var i = 0; i < datatypes.length; i++) {
                    datatypes[i].accountId = userInfoService.getAccountID();
                }

                $http.post('api/datatypes/saveDts', datatypes).then(function(response) {
                    var saveResponse = angular.fromJson(response.data);
                    for (var i = 0; i < datatypes.length; i++) {
                        datatypes[i].dateUpdated = saveResponse[i].dateUpdated;
                        datatypes[i].version = saveResponse[i].version;
                    }

                    delay.resolve(saveResponse);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            saveAll: function(datatypes) {
                var delay = $q.defer();
                $http.post('api/datatypes/saveAll', datatypes).then(function(response) {
                    //                    var saveResponse = angular.fromJson(response.data);
                    //                    datatype.date = saveResponse.date;
                    //                    datatype.version = saveResponse.version;
                    //                    datatype.id = saveResponse.id;
                    //                    delay.resolve(datatypes);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
            },
            getOne: function(id) {
                var delay = $q.defer();
                if ($rootScope.datatypesMap[id] === undefined || $rootScope.datatypesMap[id] === null) {
                    $http.get('api/datatypes/' + id).then(function(response) {
                        var datatype = angular.fromJson(response.data);
                        delay.resolve(datatype);
                    }, function(error) {
                        delay.reject(error);
                    });
                } else {
                    delay.resolve($rootScope.datatypesMap[id]);
                }
                return delay.promise;
            },

            getOneDatatype: function(id) {
                var delay = $q.defer();

                $http.get('api/datatypes/' + id).then(function(response) {
                    var datatype = angular.fromJson(response.data);
                    delay.resolve(datatype);
                }, function(error) {
                    delay.reject(error);
                });

                return delay.promise;
            },


            get: function(ids) {
                var delay = $q.defer();
                $http.post('api/datatypes/findByIds', ids).then(function(response) {
                    var datatypes = angular.fromJson(response.data);
                    delay.resolve(datatypes);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            getOneStandard: function(name, version, versions) {
                var wrapper = {
                    name: name,
                    hl7Version: version,
                    scope: "HL7STANDARD",
                    versions: versions
                }
                var delay = $q.defer();
                $http.post('api/datatypes/findOneStrandard', angular.toJson(wrapper)).then(function(response) {
                    console.log(response);
                    var datatype = angular.fromJson(response.data);
                    delay.resolve(datatype);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            getPublishedMaster: function(hl7Version) {
                var delay = $q.defer();

                $http.post('api/datatypes/findPublished', hl7Version).then(function(response) {
                    console.log(response);
                    var datatype = angular.fromJson(response.data);
                    delay.resolve(datatype);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            getDataTypesByScopesAndVersion: function(scopes, hl7Version) {
                //console.log("datatypes/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
                var scopesAndVersion = {
                    "scopes": scopes,
                    "hl7Version": hl7Version
                };
                return $http.post(
                        'api/datatypes/findByScopesAndVersion', angular.toJson(scopesAndVersion))
                    .then(function(response) {
                        //console.log("getDataTypesByScopesAndVersion response size=" + response.data.length);
                        return angular.fromJson(response.data);
                    });
            },
            merge: function(to, from) {
                to = angular.extend(to, from);
                //                to.name = from.name;
                //                to.ext = from.ext;
                //                to.label = from.label;
                //                to.description = from.description;
                //                to.status = from.status;
                //                to.comment = from.comment;
                //                to.usageNote = from.usageNote;
                //                to.scope = from.scope;
                //                to.hl7Version = from.hl7Version;
                //                to.accountId = from.accountId;
                //                to.participants = from.participants;
                //                to.libId = from.libId;
                //                to.predicates = from.predicates;
                //                to.conformanceStatements = from.conformanceStatements;
                //                to.sectionPosition = from.sectionPosition;
                //                to.components = from.components;
                //                to.version = from.version;
                //                to.date = from.date;
                //                to.purposeAndUse = from.purposeAndUse;
                return to;
            },
            findFlavors: function(name, scope, hl7Version) {
                var delay = $q.defer();
                $http.get('api/datatypes/findFlavors', { params: { "name": name, "scope": scope, "hl7Version": hl7Version } }).then(function(response) {
                    var datatypes = angular.fromJson(response.data);
                    delay.resolve(datatypes);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            delete: function(datatype) {
                return $http.get('api/datatypes/' + datatype.id + '/delete');
            },

            getDatatypeLink: function(datatype) {
                return { id: datatype.id, ext: datatype.ext, name: datatype.name };
            },
            collectDatatypes: function(id) {
                var delay = $q.defer();
                $http.get('api/datatypes/' + id + '/datatypes').then(function(response) {
                    delay.resolve(angular.fromJson(response.data));
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
                                DatatypeService.completeSave();
                                delay.resolve(true);
                            }, function(error) {
                                delay.reject(error);
                            });
                        } else {
                            DatatypeService.completeSave();
                            delay.resolve(true);
                        }
                    }, function(error) {
                        delay.reject(error);
                    });
                } else {
                    DatatypeService.completeSave();
                    delay.resolve(true);
                }
                return delay.promise;
            },

            completeSave: function() {
                $rootScope.addedDatatypes = [];
                $rootScope.addedTables = [];
                $rootScope.clearChanges();
                $rootScope.msg().text = "datatypeSaved";
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
                $rootScope.datatype = angular.copy($rootScope.datatypesMap[$rootScope.datatype.id]);
            },
            resetLib: function() {
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
                $rootScope.datatype = angular.copy($rootScope.datatypesMap[$rootScope.datatype.id]);
            },

            updateTableBinding: function(datatypeUpdateParameterList) {
                var delay = $q.defer();
                $http.post('api/datatypes/updateTableBinding/', datatypeUpdateParameterList).then(function(response) {
                    delay.resolve(true);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            updateDatatypeBinding: function(datatypeUpdateParameterList) {
                var delay = $q.defer();
                $http.post('api/datatypes/updateDatatypeBinding/', datatypeUpdateParameterList).then(function(response) {
                    delay.resolve(true);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            }

        };
        return DatatypeService;
    });