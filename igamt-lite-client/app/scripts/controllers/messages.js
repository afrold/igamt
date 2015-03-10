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
                    return parent ? parent.children: $scope.message != null ? [$rootScope.message]:[];
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
                if(messageId != 0) {
                    $scope.params.refresh();
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
            $scope.params.refresh();
            $scope.loadingSelection = false;
        };


        $scope.goToSegment = function (segmentId) {
            $rootScope.segment = $rootScope.segmentsMap[segmentId];
            $rootScope.notifySegTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(2);
        };

        $scope.hasChildren = function(node){
            return node && node != null && node.type !==  'segment' && node.children && node.children.length >0;
        };


    });


angular.module('igl')
    .controller('MessageRowCtrl', function ($scope,$filter) {
        $scope.formName = "form_"+ new Date().getTime();
    });
