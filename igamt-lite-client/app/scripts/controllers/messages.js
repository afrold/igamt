/**
 * Created by haffo on 2/13/15.
 */




angular.module('igl')
    .controller('MessageListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.tmpMessages =[].concat($rootScope.messages);
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent && parent!= null ? parent.children : $rootScope.message != null ? $rootScope.message.children : [];
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
            waitingDialog.show('Loading Message...', {dialogSize: 'sm', progressType: 'danger'});
            $scope.loadingSelection = true;
            $rootScope.message = $rootScope.messagesMap[messageId];
            $scope.params.refresh();
            $scope.loadingSelection = false;
            waitingDialog.hide();
        };

        $scope.close = function(){
            $rootScope.message = null;
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.goToSegment = function (segmentId) {
            $rootScope.segment = $rootScope.segmentsMap[segmentId];
            $rootScope.notifySegTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(2);
        };

        $scope.hasChildren = function (node) {
            return node && node != null && node.type !== 'segmentRef' && node.children && node.children.length > 0;
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

