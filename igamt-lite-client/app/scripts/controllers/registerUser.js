'use strict';

angular.module('igl')
.controller('RegisterUserCtrl', ['$scope', '$resource', 'userInfoService',
    function ($scope, $resource, userInfoService) {
        var RegisterUser = $resource('api/accounts/register/:id');
        $scope.accountTypes = ['authorizedVendor'];
        $scope.newUser = {};
        $scope.newUser.accountType = 'authorizedVendor';
       
        $scope.isAdmin = function() {
            return userInfoService.isAdmin();
        };

        $scope.isSupervisor = function() {
            return userInfoService.isSupervisor();
        };

        if ( $scope.isAdmin() ) {
            $scope.accountTypes.push('supervisor');
            $scope.accountTypes.push('provider');
        }

        $scope.registerUser = function() {
            var newUserToRegister = new RegisterUser($scope.newUser);
            newUserToRegister.username = '';

            newUserToRegister.$save(function() {
                //TODO: Check return code to make sure things are good.
                $scope.newUser.email = '';
            });
        };
    }
]);
