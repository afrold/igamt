/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('LoginCtrl', [ '$rootScope','$scope', '$mdDialog', 'user','userInfoService','base64', '$http', function($rootScope,$scope, $mdDialog, user,userInfoService,base64, $http) {
  $scope.user = user;
  $rootScope.loginOpen=true;
  $scope.cancel = function() {

      $scope.errorLogin=null;
      $rootScope.loginOpen=false;

      $mdDialog.hide();
  };

  $scope.errorLogin=null;
  $scope.login = function() {
      // ////console.log("logging in...");


      httpHeaders.common['Accept'] = 'application/json';
      httpHeaders.common['Authorization'] = 'Basic ' + base64.encode($scope.user.username + ':' + $scope.user.password);
      $http.get('api/accounts/login').then(function (response) {
          var loginResponse = angular.fromJson(response.data);

          httpHeaders.common['Authorization'] = null;
          $http.get('api/accounts/cuser').then(function (result) {
              if (result.data && result.data != null) {
                  var rs = angular.fromJson(result.data);
                  console.log(rs);
                  userInfoService.setCurrentUser(rs);
                  $rootScope.loginOpen=false;
                  $mdDialog.hide();
                  $rootScope.$broadcast('event:loginConfirmed');
                  $scope.errorLogin = null;
              }else{
                  $rootScope.loginOpen=true;
                  $scope.errorLogin="Error: "+loginResponse.text;
              }
          });


      });
  }

}]);
