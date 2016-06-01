/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('MessageService',
    ['$rootScope', 'ViewSettings', 'ElementUtils','$q', '$http', 'FilteringSvc', function ($rootScope, ViewSettings,ElementUtils,$q,$http, FilteringSvc) {
        var MessageService = {
        	save: function (message) {
        		var delay = $q.defer();
                $http.post('api/messages/save', message, {
                    headers:{'Content-Type':'application/json'}
                }).then(function (response) {
                	var saved = angular.fromJson(response.data);
                	delay.resolve(saved);
                	return saved;
                }, function (error) {
                	delay.reject(error);
                });
                return delay.promise;
        	},
        	getNodes: function (parent,root) {
                if (!parent || parent == null) {
                    return root.children;
                } else {
                    return parent.children;
                }
            },
            getTemplate: function (node,root) {
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
            merge: function (to, from) {
                to.accountID = from.accountID;
                to.children = from.children;
                to.comment = from.comment;
                to.conformanceStatements = from.conformanceStatements;
                to.date = from.date;
                to.description = from.description;
                to.event = from.event;
                to.hl7Version= from.hl7Version;
                to.id = from.id;
                to.identifier = from.identifier;
                to.libIds = from.libIds;
                to.messageID = from.messageID;
                to.messageType = from.messageType;
                to.name = from.name;
                to.participants = from.participants;
                to.position = from.position;
                to.predicates = from.predicates;
                to.scope = from.scope;
                to.status = from.status;
                to.structID = from.structID;
                to.type = from.type;
                to.usageNote = from.usageNote;
                to.version = from.version;
                return to;
            },
        };
        return MessageService;
    }]);
