/**
 * Created by haffo on 2/13/15.
 */




angular.module('igl')
    .controller('MessageListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams) {
        $scope.loading = false;
        $scope.loadingSelection = false;

        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.children : $rootScope.message != null ? $rootScope.message.children: [];
                },
                getTemplate: function (node) {
                    return 'MessageEditTree.html';
                }
            });


            $scope.loading = false;
        };

        $scope.select = function (messageId) {
            $rootScope.segments = [];
            $rootScope.datatypes = [];
            $rootScope.tables = [];

            $scope.loadingSelection = true;
            if (messageId != null) {
                $rootScope.message = $rootScope.messagesMap[messageId];
                angular.forEach($rootScope.message.children, function (segmentRefOrGroup) {
                    $scope.processElement(segmentRefOrGroup);
                });
            }
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.processElement = function (element, parent) {
            if (element.type === "group" && element.children) {
                angular.forEach(element.children, function (segmentRefOrGroup) {
                    $scope.processElement(segmentRefOrGroup,element);
                });
            } else if (element.type === "segment") {
                element.ref = $rootScope.segmentsMap[element.ref.id];
                element.ref.path =  element.ref.name;
                if ($rootScope.segments.indexOf(element.ref) === -1) {
                    $rootScope.segments.push(element.ref);
                    angular.forEach(element.ref.fields, function (field) {
                        $scope.processElement(field,element.ref);
                    });
                }
            } else if (element.type === "field" || element.type === "component") {
                element["datatype"] = $rootScope.datatypesMap[element["datatypeLabel"]];
                element["path"] = parent.path+"."+element.position;
                if (angular.isDefined(element.table)) {
                    element["table"] = $rootScope.tablesMap[element.table.id];
                    if ($rootScope.tables.indexOf(element.table) === -1) {
                        $rootScope.tables.push(element.table);
                    }
                }
                $scope.processElement(element.datatype,element);
            } else if (element.type === "datatype") {
                if ($rootScope.datatypes.indexOf(element) === -1) {
                    $rootScope.datatypes.push(element);
                    angular.forEach(element.children, function (component) {
                        $scope.processElement(component,parent);
                    });
                }
            }
        };

        $scope.goToSegment = function (segmentId) {
        };

    });



