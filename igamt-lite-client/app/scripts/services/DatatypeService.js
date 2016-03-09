/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('DatatypeService',
    ['$rootScope', 'ViewSettings', function ($rootScope, ViewSettings) {
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
            }
        };
        return DatatypeService;
    }]);
