/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl')
  .controller('AccountsListCtrl', ['$scope', 'MultiAuthorsLoader', 'MultiSupervisorsLoader','Account', '$mdDialog', '$resource','AccountLoader','userInfoService','$location',
    function ($scope, MultiAuthorsLoader, MultiSupervisorsLoader, Account, $mdDialog, $resource, AccountLoader, userInfoService, $location) {

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
        var modalInstance = $mdDialog.show({
            templateUrl: 'ConfirmAccountDeleteCtrl.html',
            controller: 'ConfirmAccountDeleteCtrl',
            preserveScope:true,
            locals:{

                accountToDelete: accountToDelete,
                accountList:$scope.accountList

            }

        });
        modalInstance.then(function (accountToDelete) {
          $scope.accountToDelete = accountToDelete;
          var rowIndex = $scope.accountList.indexOf(accountToDelete);
          if(rowIndex !== -1){
            $scope.accountList.splice(rowIndex,1);
          }
          $scope.tmpAccountList = [].concat($scope.accountList);
          $scope.account =null;
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

