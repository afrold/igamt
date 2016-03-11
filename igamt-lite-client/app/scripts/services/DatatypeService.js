/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('DatatypeService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', function ($rootScope, ViewSettings, ElementUtils) {
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
                if (ViewSettings.tableReadonly) {
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
            }
        };
        return DatatypeService;
    }]);
