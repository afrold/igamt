/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('MessageService',
    function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, SegmentLibrarySvc, TableLibrarySvc, DatatypeLibrarySvc) {
        var MessageService = {
            save: function(message) {
                var delay = $q.defer();
                $http.post('api/messages/save', message, {
                    headers: { 'Content-Type': 'application/json' }
                }).then(function(response) {
                    var saved = angular.fromJson(response.data);
                    delay.resolve(saved);
                    return saved;
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            getNodes: function(parent, root) {
                if (!parent || parent == null) {
                    return root.children;
                } else {
                    return parent.children;
                }
            },
            getTemplate: function(node, root) {
                if (ViewSettings.tableReadonly) {
                    if (node.obj.type === 'segmentRef') {
                        return 'MessageSegmentRefReadTree.html';
                    } else if (node.obj.type === 'group') {
                        return 'MessageGroupReadTree.html';
                    } else if (node.obj.type === 'field') {
                        return 'MessageFieldViewTree.html';
                    } else if (node.obj.type === 'component') {
                        return 'MessageComponentViewTree.html';
                    } else {
                        return 'MessageReadTree.html';
                    }
                } else {

                    if (node.obj.type === 'segmentRef') {
                        return 'MessageSegmentRefEditTree.html';
                    } else if (node.obj.type === 'group') {
                        return 'MessageGroupEditTree.html';
                    } else if (node.obj.type === 'field') {
                        return 'MessageFieldViewTree.html';
                    } else if (node.obj.type === 'component') {
                        return 'MessageComponentViewTree.html';
                    } else {
                        return 'MessageEditTree.html';
                    }
                }
            },
            merge: function(to, from) {
                to = _.extend(to, from);
                //                to.accountID = from.accountID;
                //                to.children = from.children;
                //                to.comment = from.comment;
                //                to.conformanceStatements = from.conformanceStatements;
                //                to.date = from.date;
                //                to.description = from.description;
                //                to.event = from.event;
                //                to.hl7Version = from.hl7Version;
                //                to.id = from.id;
                //                to.identifier = from.identifier;
                //                to.libIds = from.libIds;
                //                to.messageID = from.messageID;
                //                to.messageType = from.messageType;
                //                to.name = from.name;
                //                to.participants = from.participants;
                //                to.position = from.position;
                //                to.predicates = from.predicates;
                //                to.scope = from.scope;
                //                to.status = from.status;
                //                to.structID = from.structID;
                //                to.type = from.type;
                //                to.usageNote = from.usageNote;
                //                to.version = from.version;
                return to;
            },

            indexIn: function(id, collection) {
                for (var i = 0; i < collection.length; i++) {
                    if (collection[i].id === id) {
                        return i;
                    }
                }
                return -1;
            },


            completeSave: function() {
                $rootScope.addedDatatypes = [];
                $rootScope.addedTables = [];
                $rootScope.addedSegments = [];
                $rootScope.msg().text = "messageSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $rootScope.processElement($rootScope.message);
                $rootScope.clearChanges();
                $rootScope.messageTree = null;
                $rootScope.processMessageTree($rootScope.message);
            },


            saveNewElements: function() {
                var delay = $q.defer();
                var links = ElementUtils.getNewSegmentLinks();
                if (links.length > 0) {
                    SegmentLibrarySvc.addChildren($rootScope.igdocument.profile.segmentLibrary.id, links).then(function() {
                        $rootScope.igdocument.profile.segmentLibrary.children = $rootScope.igdocument.profile.segmentLibrary.children.concat(links);
                        _.each($rootScope.addedSegments, function(segment) {
                            if (ElementUtils.indexIn(segment.id, $rootScope.segments) < 0) {
                                $rootScope.segments.push(segment);
                            }
                        });
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
                                        MessageService.completeSave();
                                        delay.resolve(true);
                                    }, function(error) {
                                        delay.reject(error);
                                    });
                                } else {
                                    MessageService.completeSave();
                                    delay.resolve(true);
                                }
                            }, function(error) {
                                delay.reject(error);
                            });
                        } else {
                            MessageService.completeSave();
                            delay.resolve(true);
                        }
                    }, function(error) {
                        delay.reject(error);
                    });
                } else {
                    MessageService.completeSave();
                    delay.resolve(true);
                }
                return delay.promise;
            },


            reset: function() {
                if ($rootScope.addedSegments != null && $rootScope.addedSegments.length > 0) {
                    _.each($rootScope.addedSegments, function(segment) {
                        delete $rootScope.segmentsMap[segment.id];
                    });
                }
                if ($rootScope.addedDatatypes != null && $rootScope.addedDatatypes.length > 0) {
                    _.each($rootScope.addedDatatypes, function(datatype) {
                        delete $rootScope.datatypesMap[datatype.id];
                    });
                }
                if ($rootScope.addedTables != null && $rootScope.addedTables.length > 0) {
                    _.each($rootScope.addedTables, function(table) {
                        delete $rootScope.tablesMap[table.id];
                    });
                }
                $rootScope.message = angular.copy($rootScope.messagesMap[$rootScope.message.id]);
            },

            findIndex: function(id) {
                for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
                    if ($rootScope.igdocument.profile.messages.children[i].id === id) {
                        return i;
                    }
                }
                return -1;
            },
            updatePosition: function(children, old_index, new_index) {
                if (new_index >= children.length) {

                    var k = new_index - children.length;
                    while ((k--) + 1) {
                        children.push(undefined);
                    }
                }
                children.splice(new_index, 0, children.splice(old_index, 1)[0]);

                angular.forEach(children, function(child) {
                    child.position = children.indexOf(child) + 1;

                });
            },
            addSegToPath: function(path, message, segment, oldPos, newPos) {



                if (path.length === 1) {
                    if (message.children) {
                        console.log(message);
                        (message.children[path[0] - 1]).children.push(segment);
                        console.log((message.children[path[0] - 1]).children);

                        MessageService.updatePosition((message.children[path[0] - 1]).children, oldPos, newPos);

                    }
                } else {
                    var x = angular.copy(path);
                    path.splice(0, 1);
                    //message.children[x[0] - 1]
                    MessageService.addSegToPath(path, message.children[x[0] - 1], segment, oldPos, newPos);

                }
                return message;
            },
            deleteSegFromPath: function(path, message) {
                var delay = $q.defer();

                if (path.length === 1) {
                    if (message.children) {

                        message.children.splice(path[0] - 1, 1);
                        $rootScope.parentGroup = message;
                    }
                } else {

                    var x = angular.copy(path);
                    path.splice(0, 1);
                    //message.children[x[0] - 1]
                    MessageService.deleteSegFromPath(path, message.children[x[0] - 1]);


                }
                delay.resolve(true);
                return delay.promise;

            },
            findParentByPath: function(path, message) {
                var delay = $q.defer();

                if (path.length === 1) {
                    if (message.children) {
                        $rootScope.segParent = message;


                    }
                } else {
                    var x = angular.copy(path);
                    path.splice(0, 1);
                    //message.children[x[0] - 1]
                    MessageService.findParentByPath(path, message.children[x[0] - 1]);

                }
                delay.resolve(true);
                return delay.promise;
            },

            updateSegmentBinding: function(segmentBindingUpdateParameterList) {
                var delay = $q.defer();
                $http.post('api/messages/updateSegmentBinding/', segmentBindingUpdateParameterList).then(function(response) {
                    delay.resolve(true);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            }



        };
        return MessageService;
    });