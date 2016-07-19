
'use strict';
angular.module('igl').factory('CompareSvc',
    function($rootScope, ViewSettings, $q, $http, FilteringSvc) {
        var CompareSvc = {
            
            getNodes: function(parent, root) {
                if (!parent || parent == null) {
                    return root.children;
                } else {
                    return parent.children;
                }
            },
            getTemplate: function(node, root) {
                // if (ViewSettings.tableReadonly) {
                //     if (node.obj.type === 'segmentRef') {
                //         return 'MessageSegmentRefReadTree.html';
                //     } else if (node.obj.type === 'group') {
                //         return 'MessageGroupReadTree.html';
                //     } else if (node.obj.type === 'field') {
                //         return 'MessageFieldViewTree.html';
                //     } else if (node.obj.type === 'component') {
                //         return 'MessageComponentViewTree.html';
                //     } else {
                //         return 'MessageReadTree.html';
                //     }
                // } else {

                    if (node.obj.type === 'segmentRef') {
                        return 'CompareTree.html';
                    } else if (node.obj.type === 'group') {
                        return 'MessageGroupEditTree.html';
                    } else if (node.obj.type === 'field') {
                        return 'MessageFieldViewTree.html';
                    } else if (node.obj.type === 'component') {
                        return 'MessageComponentViewTree.html';
                    } else {
                        return 'MessageEditTree.html';
                    }
                //}
            },
        };
        return CompareSvc;
    });
