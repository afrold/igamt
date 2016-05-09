/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('DatatypeService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', '$http', '$q', 'FilteringSvc', function ($rootScope, ViewSettings, ElementUtils, $http, $q, FilteringSvc) {
        var DatatypeService = {
            getNodes: function (parent, root) {
                var children = [];
                if (parent && parent != null) {
                    if (parent.datatype) {
                        var dt = $rootScope.datatypesMap[parent.datatype];
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
            getParent: function (child) {
                var template =  $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
                return template;
            },
            getTemplate: function (node, root) {
                if (ViewSettings.tableReadonly || root != null && root.scope === 'HL7STANDARD' || root.scope === null) {
                    return DatatypeService.getReadTemplate(node,root);
                } else {
                    return DatatypeService.getEditTemplate(node,root);
                }
            },

            getReadTemplate: function (node, root) {
               return node.type === 'Datatype' ? 'DatatypeReadTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeComponentReadTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeSubComponentReadTree.html' : '';
            },

            getEditTemplate: function (node, root) {
                return node.type === 'Datatype' ? 'DatatypeEditTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeComponentEditTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeSubComponentEditTree.html' : '';
            },

            isDatatypeSubDT: function (component,root) {
                if (root != null) {
                    for (var i = 0, len = root.components.length; i < len; i++) {
                        if (root.components[i].id === component.id)
                            return false;
                    }
                }
                return true;
            },
            isBranch: function (node) {
                var children = DatatypeService.getNodes(node);
                return children != null && children.length > 0;
            },
            isVisible: function (node) {
//                return FilteringSvc.show(node);
                 return  node ? DatatypeService.isRelevant(node) ? DatatypeService.isVisible(DatatypeService.getParent(node)) : false : true;
            },
            isRelevant: function (node) {
                if (node === undefined || !ViewSettings.tableRelevance)
                    return true;
                if (node.hide == undefined || !node.hide || node.hide === false) {
                    var predicates = DatatypeService.getDatatypeLevelPredicates(node);
                    return ElementUtils.isRelevant(node, predicates);
                } else {
                    return false;
                }
            },
            getDatatypeLevelConfStatements: function (element) {
                var datatype = DatatypeService.getParent(element);
                var confStatements = [];
                if (datatype && datatype != null && datatype.conformanceStatements.length > 0) {
                    return ElementUtils.filterConstraints(element, datatype.conformanceStatements);
                }
                return confStatements;
            },

            getDatatypeLevelPredicates: function (element) {
                var datatype = DatatypeService.getParent(element);
                var predicates = [];
                if (datatype && datatype != null && datatype.predicates.length > 0) {
                    return ElementUtils.filterConstraints(element, datatype.predicates);
                }
                return predicates;
            },
            save: function (datatype) {
                var delay = $q.defer();
                $http.post('api/datatypes/save', datatype).then(function (response) {
                    var saveResponse = angular.fromJson(response.data);
                    datatype.date = saveResponse.date;
                    datatype.version = saveResponse.version;
                    datatype.id = saveResponse.id;
                    delay.resolve(datatype);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            getOne: function (id) {
                var delay = $q.defer();
                if ($rootScope.datatypesMap[id] === undefined || $rootScope.datatypesMap[id] === undefined) {
                    $http.get('api/datatypes/' + id).then(function (response) {
                        var datatype = angular.fromJson(response.data);
                        delay.resolve(datatype);
                    }, function (error) {
                        delay.reject(error);
                    });
                } else {
                    delay.resolve($rootScope.datatypesMap[id]);
                }
                return delay.promise;
            },
            get: function (ids) {
                var delay = $q.defer();
                $http.post('api/datatypes/findByIds', ids).then(function (response) {
                    var datatypes = angular.fromJson(response.data);
                    delay.resolve(datatypes);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            merge: function (to, from) {
                to.name = from.name;
                to.ext = from.ext;
                to.label = from.label;
                to.description = from.description;
                to.status = from.status;
                to.comment = from.comment;
                to.usageNote = from.usageNote;
                to.scope = from.scope;
                to.hl7Version = from.hl7Version;
                to.accountId = from.accountId;
                to.participants = from.participants;
                to.libId = from.libId;
                to.predicates = from.predicates;
                to.conformanceStatements = from.conformanceStatements;
                to.sectionPosition = from.sectionPosition;
                to.components = from.components;
                to.version = from.version;
                to.date = from.date;
                to.purposeAndUse = from.purposeAndUse;
                return to;
            },
            findFlavors: function (name, scope, hl7Version) {
                var delay = $q.defer();
                $http.get('api/datatypes/findFlavors', {params: {"name": name, "scope": scope, "hl7Version": hl7Version}}).then(function (response) {
                    var datatypes = angular.fromJson(response.data);
                    delay.resolve(datatypes);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            delete_: function(datatype) {
                 return $http.post('api/datatypes/'+ datatype.id+ '/delete');
            },

            getDatatypeLink : function(datatype){
                return {id:datatype.id, ext: null, name: datatype.name};
            }

        };
        return DatatypeService;
    }])
;
