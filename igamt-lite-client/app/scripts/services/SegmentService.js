/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('SegmentService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', function ($rootScope, ViewSettings,ElementUtils) {
        var SegmentService = {
            getNodes: function (parent) {
                return parent ? parent.fields ? parent.fields : parent.datatype ? $rootScope.datatypesMap[parent.datatype].components : parent.children : $rootScope.segment != null ? $rootScope.segment.fields : [];
            },
            getParent: function (child) {
                return $rootScope.parentsMap && $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
            },
            getTemplate: function (node) {
                if (ViewSettings.tableReadonly) {
                    return node.type === 'segment' ? 'SegmentReadTree.html' : node.type === 'field' ? 'SegmentFieldReadTree.html' : 'SegmentComponentReadTree.html';
                } else {
                    return node.type === 'segment' ? 'SegmentEditTree.html' : node.type === 'field' ? 'SegmentFieldEditTree.html' : 'SegmentComponentEditTree.html';
                }
            },
            getSegmentLevelConfStatements: function (element) {
                var parent = SegmentService.getParent(element.id);
                var conformanceStatements = [];
                if (parent && parent != null && parent.conformanceStatements.length > 0) {
                    return ElementUtils.filterConstraints(element, parent.conformanceStatements);
                }
                return conformanceStatements;
            },

            getSegmentLevelPredicates: function (element) {
                var parent = SegmentService.getParent(element.id);
                var predicates = [];
                if (parent && parent != null && parent.predicates.length > 0) {
                    return ElementUtils.filterConstraints(element, parent.predicates);
                }
                return predicates;
            },

            isBranch: function (node) {
                var children = SegmentService.getNodes(node);
                return children != null && children.length > 0;
            },
            isVisible: function (node) {
                return  node ? SegmentService.isRelevant(node) ? SegmentService.isVisible(SegmentService.getParent(node)) : false : true;
            },
            isRelevant: function (node) {
                if (node === undefined || !ViewSettings.tableRelevance)
                    return true;
                if (node.hide == undefined || !node.hide || node.hide === false) {
                    var predicates = SegmentService.getSegmentLevelPredicates(node);
                    return ElementUtils.isRelevant(node,predicates);
                } else {
                    return false;
                }
            }

        };
        return SegmentService;
    }]);
