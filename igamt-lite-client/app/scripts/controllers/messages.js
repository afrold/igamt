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
                    return parent ? parent.children : $rootScope.message != null ? [$rootScope.message] : [];
                },
                getTemplate: function (node) {
                    return 'MessageEditTree.html';
                },
                options: {
                    initialState: 'expanded'
                }
            });


            $scope.$watch(function () {
                return $rootScope.notifyMsgTreeUpdate;
            }, function (messageId) {
                if (messageId != 0) {
                    $scope.params.refresh();
                }
            });

            $scope.loading = false;
        };

        $scope.select = function (messageId) {
//            $rootScope.segments = [];
//            $rootScope.datatypes = [];
//            $rootScope.tables = [];
            $scope.loadingSelection = true;
            if (messageId != null) {
                $rootScope.message = $rootScope.messagesMap[messageId];
//                angular.forEach($rootScope.message.children, function (segmentRefOrGroup) {
//                    $rootScope.processElement(segmentRefOrGroup);
//                });
            }
            $scope.params.refresh();
            $scope.loadingSelection = false;
        };


        $scope.goToSegment = function (segmentId) {
            $rootScope.segment = $rootScope.segmentsMap[segmentId];
            $rootScope.notifySegTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(2);
        };

        $scope.hasChildren = function (node) {
            return node && node != null && node.type !== 'segment' && node.children && node.children.length > 0;
        };


    });


angular.module('igl')
    .controller('MessageRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });


angular.module('igl')
    .controller('MessageViewCtrl', function ($scope, $rootScope, Restangular) {
        $scope.loading = false;
        $scope.msg = null;
        $scope.messageData = [];
        $scope.setData = function (node) {
            if(node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.setData(segmentRefOrGroup);
                    });
                } else if (node.type === 'group') {
                    $scope.messageData.push({ name: "-- " + node.name + " begin"});
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.setData(segmentRefOrGroup);
                        });
                    }
                    $scope.messageData.push({ name: "-- " + node.name + " end"});
                } else if (node.type === 'segment') {
                    $scope.messageData.push + (node);
                }
            }
        };


        $scope.init = function (message) {
            $scope.loading = true;
            $scope.msg = message;
            console.log(message.id);
            $scope.setData($scope.msg);
            $scope.loading = false;
        };

//        $scope.hasChildren = function (node) {
//            return node && node != null && node.type !== 'segment' && node.children && node.children.length > 0;
//        };

    });

