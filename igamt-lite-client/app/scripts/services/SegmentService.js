/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('SegmentService',
    ['$rootScope', 'ViewSettings', 'ElementUtils','$q', '$http', 'FilteringSvc', function ($rootScope, ViewSettings,ElementUtils,$q,$http, FilteringSvc) {
        var SegmentService = {
            getNodes: function (parent,root) {
                return parent ? parent.fields ? parent.fields : parent.datatype ? $rootScope.datatypesMap[parent.datatype].components : parent.children : root != null ? root.fields : [];
            },
            getParent: function (child) {
                return $rootScope.parentsMap && $rootScope.parentsMap[child.id] ? $rootScope.parentsMap[child.id] : null;
            },
            getTemplate: function (node,root) {
                if (ViewSettings.tableReadonly || root != null && root.scope === 'HL7STANDARD' || root.scope === null) {
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
              return FilteringSvc.show(node);
//                 return  node ? SegmentService.isRelevant(node) ? SegmentService.isVisible(SegmentService.getParent(node)) : false : true;
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
            },
            save: function (segment) {
                var delay = $q.defer();
                $http.post('api/segments/save', segment).then(function (response) {
                    var saveResponse = angular.fromJson(response.data);
                    segment.date = saveResponse.date;
                    segment.version = saveResponse.version;
                    delay.resolve(saveResponse);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            get: function (id) {
                var delay = $q.defer();
                if($rootScope.segmentsMap[id] === undefined || $rootScope.segmentsMap[id] === undefined) {
                    $http.get('api/segments/' + id).then(function (response) {
                        var segment = angular.fromJson(response.data);
                        delay.resolve(segment);
                    }, function (error) {
                        delay.reject(error);
                    });
                }else{
                    delay.resolve($rootScope.segmentsMap[id]);
                }
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
                to.fields = from.fields;
                to.version = from.version;
                to.date = from.date;
                to.purposeAndUse = from.purposeAndUse;
                return to;
            }

        };
        return SegmentService;
    }]);
