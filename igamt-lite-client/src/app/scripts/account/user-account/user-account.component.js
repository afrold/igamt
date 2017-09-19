/**
 * Created by haffo on 9/11/17.
 */



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
