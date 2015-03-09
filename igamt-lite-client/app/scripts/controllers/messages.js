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
                    $rootScope.processElement(segmentRefOrGroup);
                });
            }
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };
        $scope.goToSegment = function (segmentId) {
        };

    });



