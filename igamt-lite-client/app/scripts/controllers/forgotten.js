'use strict';

angular.module('igl')
.controller('ForgottenCtrl', ['$scope', '$resource',
    function ($scope, $resource) {
        var ForgottenRequest = $resource('api/sooa/accounts/passwordreset', {username:'@username'});

        $scope.requestResetPassword =  function() {
            var resetReq = new ForgottenRequest();
            resetReq.username = $scope.username;
            resetReq.$save(function() {
                if ( resetReq.text === 'resetRequestProcessed' ) {
                    $scope.username = '';
                }
            });
        };
    }
]);
