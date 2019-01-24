/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl')
    .controller('DynamicTable0396Management', ['$scope','Account', '$mdDialog', 'DynamicTable0396Service','userInfoService','$location','$rootScope',
        function ($scope, Account, $mdDialog, DynamicTable0396Service, userInfoService, $location,$rootScope) {

            $scope.table =null;
            $scope.tabStatus = {
                active: 1
            };
            $scope.loading = false;
            $scope.error = null;

            $scope.fetchUpdates = function() {
                 var modalInstance = $mdDialog.show({
                    templateUrl: 'ConfirmFetchDynamicTable0396UpdatesCtrl.html',
                    controller: 'ConfirmFetchDynamicTable0396UpdatesCtrl'
                });
                modalInstance.then(function (table) {
                    $scope.updateTable(table);
                }, function () {
                    $scope.loading = false;
                });
            };

            $scope.get = function() {
                $scope.loading = true;
                DynamicTable0396Service.get().then(function(result){
                    $scope.loading = false;
                    var res = angular.fromJson(result);
                    $scope.updateTable(res.data);
                }, function(error){
                    $scope.loading = false;
                    $scope.error = error.data;
                });
            };

            $scope.updateTable= function(table) {
                $scope.table = table;
                $scope.searchObject = {};
                $rootScope.$emit("event:initEditArea");
                $rootScope.msg().text = "dataManagement.table0396.update.success";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
             };


            $scope.initDynamicTable0396 = function(){
                $scope.table =null;
                $scope.loading = false;
                $scope.error = null;
                $scope.get();
            };
        }
    ]);


angular.module('igl').controller('ConfirmFetchDynamicTable0396UpdatesCtrl', function ($scope, $mdDialog,Account,Notification,DynamicTable0396Service) {
    $scope.loading = false;
    $scope.confirm = function () {
        $scope.loading = true;
        DynamicTable0396Service.fetch().then(function(result){
            var res = angular.fromJson(result);
            $mdDialog.hide( res.data);
        }, function(error){
            $scope.loading = false;
            $scope.error = error.data;
        });
    };

    $scope.cancel = function () {
        $mdDialog.hide('cancel');
    };
});

