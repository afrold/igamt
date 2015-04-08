'use strict';

angular.module('igl')
.controller('RegisterResetPasswordCtrl', ['$scope', '$resource', '$dialog', '$routeParams', 'isFirstSetup',
    function ($scope, $resource, $dialog, $routeParams, isFirstSetup) {
        $scope.displayForm = true;
        $scope.isFirstSetup = isFirstSetup;

        if ( !angular.isDefined($routeParams.username) ) {
            $scope.displayForm = false;
        }
        if ( $routeParams.username === '' ) {
            $scope.displayForm = false;
        }
        if ( !angular.isDefined($routeParams.token) ) {
            $scope.displayForm = false;
        }
        if ( $routeParams.token === '' ) {
            $scope.displayForm = false;
        }
        if ( !angular.isDefined($routeParams.userId) ) {
            $scope.displayForm = false;
        }
        if ( $routeParams.userId === '' ) {
            $scope.displayForm = false;
        }

        //to register an account for the first time
        var AcctInitPassword = $resource('api/sooa/accounts/register/:userId/passwordreset', {userId:'@userId', token:'@token'});
        //to reset the password  
        var AcctResetPassword = $resource('api/sooa/accounts/:id/passwordreset', {id:'@userId', token:'@token'});

        $scope.user = {};
        $scope.user.username = $routeParams.username;
        $scope.user.newUsername = $routeParams.username;
        $scope.user.userId = $routeParams.userId;
        $scope.user.token = $routeParams.token;

        var dialogOptions = {
            backdrop: true,
            keyboard: true,
            backdropClick: false,
            controller: 'AgreementCtrl',
            templateUrl: 'views/agreement.html'
        };

        $scope.confirmRegistration = function() {
            $dialog.dialog(angular.extend(dialogOptions))
            .open()
            .then(function(result) {
                if(result) {
                    var initAcctPass = new AcctInitPassword($scope.user);
                    initAcctPass.signedConfidentialityAgreement = true;
                    initAcctPass.$save(function() {
                        $scope.user.password = '';
                        $scope.user.passwordConfirm = '';
                    });
                }
                else {
                    //console.log("Agreement not accepted");
                }
            });
        };

        $scope.changePassword = function() {
            var resetAcctPass = new AcctResetPassword($scope.user);
            resetAcctPass.$save(function() {
                $scope.user.password = '';
                $scope.user.passwordConfirm = '';
            });
        };
    }
]);
