'use strict';

/* "newcap": false */

angular.module('igl')
.controller('UserProfileCtrl', ['$scope', '$resource', 'AccountLoader', 'Account', 'userInfoService', '$location',
    function ($scope, $resource, AccountLoader, Account, userInfoService, $location) {
        var PasswordChange = $resource('api/accounts/:id/passwordchange', {id:'@id'});

        $scope.accountpwd = {};

        $scope.initModel = function(data) {
            $scope.account = data;
            $scope.accountOrig = angular.copy($scope.account);
        };

        $scope.updateAccount = function() {
            //not sure it is very clean...
            //TODO: Add call back?
            new Account($scope.account).$save();

            $scope.accountOrig = angular.copy($scope.account);
        };

        $scope.resetForm = function() {
            $scope.account = angular.copy($scope.accountOrig);
        };

        //TODO: Change that: formData is only supported on modern browsers
        $scope.isUnchanged = function(formData) {
            return angular.equals(formData, $scope.accountOrig);
        };


        $scope.changePassword = function() {
            var user = new PasswordChange();
            user.username = $scope.account.username;
            user.password = $scope.accountpwd.currentPassword;
            user.newPassword = $scope.accountpwd.newPassword;
            user.id = $scope.account.id;
            //TODO: Check return value???
            user.$save().then(function(result){
                $scope.msg = angular.fromJson(result);
            });
        };

        $scope.deleteAccount = function () {
            var tmpAcct = new Account();
            tmpAcct.id = $scope.account.id;

            tmpAcct.$remove(function() {
                //console.log("Account removed");
                //TODO: Add a real check?
                userInfoService.setCurrentUser(null);
                $scope.$emit('event:logoutRequest');
                $location.url('/home');
            });
        };

        /*jshint newcap:false */
        AccountLoader(userInfoService.getAccountID()).then(
            function(data) {
                $scope.initModel(data);
                if (!$scope.$$phase) {
                    $scope.$apply();
                }
            },
            function() {
//                console.log('Error fetching account information');
            }
        );
    }
]);


angular.module('igl')
    .controller('UserAccountCtrl', ['$scope', '$resource', 'AccountLoader', 'Account', 'userInfoService', '$location', '$rootScope',
        function ($scope, $resource, AccountLoader, Account, userInfoService, $location,$rootScope) {


            $scope.accordi = { account : true, accounts:false};
            $scope.setSubActive = function (id) {
                if(id && id != null) {
                    $rootScope.setSubActive(id);
                    $('.accountMgt').hide();
                    $('#' + id).show();
                }
            };
            $scope.initAccount = function(){
                if($rootScope.subActivePath == null){
                    $rootScope.subActivePath = "account";
                }
                $scope.setSubActive($rootScope.subActivePath);
            };


        }
    ]);

'use strict';

angular.module('igl')
    .controller('AccountsListCtrl', ['$scope', 'MultiAuthorsLoader', 'MultiSupervisorsLoader','Account', '$modal', '$resource','AccountLoader','userInfoService','$location',
        function ($scope, MultiAuthorsLoader, MultiSupervisorsLoader, Account, $modal, $resource, AccountLoader, userInfoService, $location) {

            //$scope.accountTypes = [{ 'name':'Author', 'type':'author'}, {name:'Supervisor', type:'supervisor'}];
            //$scope.accountType = $scope.accountTypes[0];
            $scope.tmpAccountList = [].concat($scope.accountList);
            $scope.account = null;
            $scope.accountOrig = null;
            $scope.accountType = "author";
            $scope.scrollbarWidth = $scope.getScrollbarWidth();

//        var PasswordChange = $resource('api/accounts/:id/passwordchange', {id:'@id'});
            var PasswordChange = $resource('api/accounts/:id/userpasswordchange', {id:'@id'});
            var ApproveAccount = $resource('api/accounts/:id/approveaccount', {id:'@id'});
            var SuspendAccount = $resource('api/accounts/:id/suspendaccount', {id:'@id'});
            $scope.msg = null;

            $scope.accountpwd = {};

            $scope.updateAccount = function() {
                //not sure it is very clean...
                //TODO: Add call back?
                new Account($scope.account).$save();
                $scope.accountOrig = angular.copy($scope.account);
            };

            $scope.resetForm = function() {
                $scope.account = angular.copy($scope.accountOrig);
            };

            //TODO: Change that: formData is only supported on modern browsers
            $scope.isUnchanged = function(formData) {
                return angular.equals(formData, $scope.accountOrig);
            };

            $scope.changePassword = function() {
                var user = new PasswordChange();
                user.username = $scope.account.username;
                user.password = $scope.accountpwd.currentPassword;
                user.newPassword = $scope.accountpwd.newPassword;
                user.id = $scope.account.id;
                //TODO: Check return value???
                user.$save().then(function(result){
                    $scope.msg = angular.fromJson(result);
                });
            };

            $scope.loadAccounts = function(){
                if (userInfoService.isAuthenticated() && userInfoService.isAdmin()) {
                    $scope.msg = null;
                    new MultiAuthorsLoader().then(function (response) {
                        $scope.accountList = response;
                        $scope.tmpAccountList = [].concat($scope.accountList);
                    });
                }
            };

            $scope.initManageAccounts = function(){
                $scope.loadAccounts();
            };

            $scope.selectAccount = function(row) {
                $scope.accountpwd = {};
                $scope.account = row;
                $scope.accountOrig = angular.copy($scope.account);
            };

            $scope.deleteAccount = function() {
                $scope.confirmDelete($scope.account);
            };

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

            $scope.approveAccount = function() {
                var user = new ApproveAccount();
                user.username = $scope.account.username;
                user.id = $scope.account.id;
                user.$save().then(function(result){
                    $scope.account.pending = false;
                    $scope.msg = angular.fromJson(result);
                });
            };

            $scope.suspendAccount = function(){
                var user = new SuspendAccount();
                user.username = $scope.account.username;
                user.id = $scope.account.id;
                user.$save().then(function(result){
                    $scope.account.pending = true;
                    $scope.msg = angular.fromJson(result);
                });
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





