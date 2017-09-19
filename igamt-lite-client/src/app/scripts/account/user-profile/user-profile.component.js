/**
 * Created by haffo on 9/11/17.
 */
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
