/**
 * Created by haffo on 2/13/15.
 */




angular.module('igl')
    .controller('MessageListCtrl', function ($scope, $rootScope, Restangular) {
        $scope.customs = [];
        $scope.messages = [];
        $scope.tmpMessages = [].concat($scope.messages);

        $scope.init = function(){
            $scope.messages = $rootScope.profile.messages.messages;
            $scope.tmpMessages = [].concat($scope.messages);
        };

        $scope.select = function(row){
            $rootScope.message = row;
        };

    });


angular.module('igl')
    .controller('MessageEditCtrl', ['$scope', '$rootScope','Restangular','ngTreetableParams',function ($scope, $rootScope, Restangular,ngTreetableParams) {

        $scope.nodeData = [];
        $scope.loading = false;

        $scope.init = function () {

            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.children : [];
                },
                getTemplate: function (node) {
                    return 'MessageEditTree.html';
                }
            });

            $scope.$watch(function () {
                return $rootScope.message.id;
            }, function (messageId) {
                if (messageId != null) {
                    $scope.loading = true;
                    $scope.nodeData = $rootScope.message.children;
                    if( $scope.params)
                    $scope.params.refresh();
                    $scope.loading = false;
                } else {
                    $scope.nodeData = [];
                    if( $scope.params)
                    $scope.params.refresh();
                    $scope.loading = false;
                }
            }, true);


        };
    }]);
