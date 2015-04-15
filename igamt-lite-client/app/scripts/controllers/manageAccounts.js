'use strict';

angular.module('igl')
.controller('ManageAccountsCtrl', ['$scope', 'accountType', 'accountList', 'Account', '$modal', '$resource',
    function ($scope, accountType, accountList, Account, $modal, $resource) {
        $scope.accountType = accountType;
        $scope.accountList = accountList;
        $scope.tmpAccountList = [].concat($scope.accountList);

        var InviteSender = $resource('api/accounts/:id/resendregistrationinvite', {id:'@id'});

//        //Paging options for the ngGrid
//        $scope.pagingOptions = {
//            pageSizes: [10, 50, 100],
//            pageSize: 10,
//            totalServerItems: 0,
//            currentPage: 1
//        };
//
//        //Filter options for the ngGrid
//        $scope.filterOptions = {
//            filterText: '',
//            useExternalFilter: false
//        };
//
//        $scope.cellTemplate = {
////            authorizedVendor: '<button type="button" class="btn btn-small" ng-click="deleteAccount(row)"><i class="icon-minus-sign"></i>Disable</button><button type="button" class="btn btn-small" ng-click="resendInvite(row)">Resend Invite</button>',
//            supervisor: '<button type="button" class="btn btn-small" ng-click="deleteAccount(row)"><i class="icon-minus-sign"></i>Disable</button><button type="button" class="btn btn-small" ng-click="resendInvite(row)">Resend Invite</button>',
//            author: '<button type="button" class="btn btn-small" ng-click="deleteAccount(row)"><i class="icon-minus-sign"></i>Disable</button>'
//        };
//
//        $scope.columnDefs= [
//            {
//                field:'id',
//                visible:false
//            },
//            {
//                field:'employer',
//                displayName:'Employer',
//                enableSorting:true
//            },
//            {
//                field:'firstname',
//                displayName:'First Name',
//                enableSorting:true
//            },
//            {
//                field:'lastname',
//                displayName:'Last Name',
//                enableSorting:true
//            },
//            {
//                field:'email',
//                displayName:'Email Address',
//                enableSorting:true
//            },
//            {
//                displayName:'Action',
//                cellTemplate:$scope.cellTemplate[$scope.accountType],
//                width:185
//            }
//        ];
//
//        $scope.gridOptions = {
//            data: 'accountList',
//            columnDefs: $scope.columnDefs,
//            showColumnMenu:true,
//            //enablePaging:true,
//            enableColumnResize: true,
//            enableRowSelection: false,
//            //pagingOptions: $scope.pagingOptions,
//            //plugins: [new ngGridCsvExportPlugin()],
//            //showFooter: true,
//            //showFilter: true,
//            showGroupPanel: true,
//            filterOptions: $scope.filterOptions
//        };

        $scope.resendInvite = function(row) {
            //TODO
//            console.log('Resend for', $scope.accountList[row.rowIndex]);
            InviteSender.save({id:row.id},
                function() {},
                function() {
//                    console.log('There was an error resending the invitation');
                }
            );
        };

        $scope.deleteAccount = function(row) {
//            var title = $.i18n.prop('manage.account.deleteModal.title');
//            var msg = $.i18n.prop('manage.account.deleteModal.msg');
//            var okButton = $.i18n.prop('manage.account.deleteModal.okButton');
//            var cancelButton = $.i18n.prop('manage.account.deleteModal.cancelButton');
//            var btns = [{result:'cancel', label: cancelButton}, {result:'ok', label: okButton, cssClass: 'btn'}];
            $scope.confirmDelete = function (accountToDelete) {
                var modalInstance = $modal.open({
                    templateUrl: 'ConfirmAccountDeleteCtrl.html',
                    controller: 'ConfirmAccountDeleteCtrl',
                    resolve: {
                        accountToDelete: function () {
                            return accountToDelete;
                        },
                        accountList: function () {
                            return $scope.accountList;
                        }
                    }
                });
                modalInstance.result.then(function (accountToDelete,accountList ) {
                    $scope.accountToDelete = accountToDelete;
                    $scope.accountList = accountList;
                }, function () {
                });
            };
        };
    }
]);



angular.module('igl').controller('ConfirmAccountDeleteCtrl', function ($scope, $modalInstance, accountToDelete,accountList,Account) {

    $scope.accountToDelete = accountToDelete;
    $scope.accountList = accountList;
    $scope.delete = function () {
         //console.log('Delete for', $scope.accountList[rowIndex]);
        Account.remove({id:accountToDelete.id},
            function() {
                var rowIndex = $scope.accountList.indexOf(accountToDelete);
                if(index !== -1){
                    $scope.accountList.splice(rowIndex,1);
                }
                $modalInstance.close($scope.accountToDelete);
            },
            function() {
//                            console.log('There was an error deleting the account');
            }
        );
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
