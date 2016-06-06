/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('ElementUtils',
    ['$rootScope', 'ViewSettings', function ($rootScope, ViewSettings) {
        var ElementUtils = {
            filterConstraints: function (node, constraints) {
                if (constraints) {
                    return $filter('filter')(constraints, {constraintTarget: node.position + '[1]'}, true);
                }
                return null;
            },
            isRelevant: function (node, predicates) {
                if (predicates && predicates != null && predicates.length > 0) {
                    return  predicates[0].trueUsage === "R" || predicates[0].trueUsage === "RE" || predicates[0].falseUsage === "R" || predicates[0].falseUsage === "RE";
                } else {
                    return node.usage == null || !node.usage || node.usage === "R" || node.usage === "RE" || node.usage === "C";
                }
            },
            setUsage: function (node) {
                if (node.usage && node.min) {
                    if (node.usage === "R" && (node.min == 0 || node.min === "0")) {
                        node.min = 1;
                    }
                    if (node.usage === "O") {
                        node.min = 0;
                    }
                }
            },
            getNewTableLink: function (obj) {
                var link = {};
                link['id'] = obj.id;
                link['bindingIdentifier'] = obj.bindingIdentifier;
                link['bindingStrength'] = obj.bindingStrength;
                link['bindingLocation'] = obj.bindingLocation;
                return link;
            },
            getNewDatatypeLink: function (obj) {
                var link = {};
                link['id'] = obj.id;
                link['name'] = obj.name;
                link['ext'] = obj.ext;
                return link;
            },
            indexIn: function (id, collection) {
                for (var i = 0; i < collection.length; i++) {
                    if (collection[i].id === id) {
                        return i;
                    }
                }
                return -1;
            },
            getNewDatatypeLinks: function () {
                var links = [];
                if ($rootScope.addedDatatypes != null && $rootScope.addedDatatypes.length > 0) {
                    _.each($rootScope.addedDatatypes, function (datatype) {
                        if (ElementUtils.indexIn(datatype.id, $rootScope.igdocument.profile.datatypeLibrary.children) < 0) {
                            var link = ElementUtils.getNewDatatypeLink(datatype);
                            links.push(link);
                        }
                    });
                }
                return links;
            },

            getNewTableLinks: function () {
                var links = [];
                if ($rootScope.addedTables != null && $rootScope.addedTables.length > 0) {
                    _.each($rootScope.addedTables, function (table) {
                        if (ElementUtils.indexIn(table.id, $rootScope.igdocument.profile.tableLibrary.children) < 0) {
                            var link = ElementUtils.getNewTableLink(table);
                            links.push(link);
                        }
                    });
                }
                return links;
            },
            getNewSegmentLinks: function () {
                var links = [];
                if ($rootScope.addedSegments != null && $rootScope.addedSegments.length > 0) {
                    _.each($rootScope.addedSegments, function (datatype) {
                        if (ElementUtils.indexIn(datatype.id, $rootScope.igdocument.profile.segmentLibrary.children) < 0) {
                            var link = ElementUtils.getNewSegmentLink(datatype);
                            links.push(link);
                        }
                    });
                }
                return links;
            }





        };
        return ElementUtils;
    }]);
