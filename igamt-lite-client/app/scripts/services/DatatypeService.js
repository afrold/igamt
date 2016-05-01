/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('DatatypeService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', '$http', '$q', function ($rootScope, ViewSettings, ElementUtils, $http, $q) {
        var DatatypeService = {
            getNodes: function (parent) {
                if (parent && parent != null) {
                    if (parent.datatype) {
                        var dt = $rootScope.datatypesMap[parent.datatype];
                        return dt.components;
                    } else {
                        return parent.components;
                    }
                } else {
                    if ($rootScope.datatype != null) {
                        return $rootScope.datatype.components;
                    } else {
                        return [];
                    }
                }
            },
            getParent: function (child) {
                return $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
            },
            getTemplate: function (node) {
                if (ViewSettings.tableReadonly || $rootScope.datatype != null && $rootScope.datatype.scope === 'HL7STANDARD' || $rootScope.datatype.scope === null) {
                    return node.type === 'Datatype' ? 'DatatypeReadTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node) ? 'DatatypeComponentReadTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node) ? 'DatatypeSubComponentReadTree.html' : '';
                } else {
                    return node.type === 'Datatype' ? 'DatatypeEditTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node) ? 'DatatypeComponentEditTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node) ? 'DatatypeSubComponentEditTree.html' : '';
                }
            },
            isDatatypeSubDT: function (component) {
                if ($rootScope.datatype != null) {
                    for (var i = 0, len = $rootScope.datatype.components.length; i < len; i++) {
                        if ($rootScope.datatype.components[i].id === component.id)
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
                    delay.resolve(saveResponse);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            getOne: function (id) {
                var delay = $q.defer();
                $http.get('api/datatypes/' + id).then(function (response) {
                    var datatype = angular.fromJson(response.data);
                    delay.resolve(datatype);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            get: function (ids) {
                var delay = $q.defer();
                $http.post('api/datatype/findByIds', ids).then(function (response) {
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
                to.participants =  from.participants;
                to.libId = from.libId;
                to.predicates = from.predicates;
                to.conformanceStatements = from.conformanceStatements;
                to.sectionPosition = from.sectionPosition;
                to.components = from.components;
                to.version = from.version;
                to.date = from.date;
                return to;
            },
            searchByNameVersionAndScope: function(searchName, searchScope,searchHl7Version){
                var delay = $q.defer();
                $http.get('api/datatypes/search', {params:{"searchName": searchName,"searchScope": searchScope,"searchHl7Version":searchHl7Version}}).then(function (response) {
                    var datatypes = angular.fromJson(response.data);
                    delay.resolve(datatypes);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            }
        };
        return DatatypeService;
    }])
;
