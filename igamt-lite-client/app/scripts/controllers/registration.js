'use strict';

angular.module('igl')
.controller('RegistrationCtrl', ['$scope', '$resource', '$modal', '$location',
    function ($scope, $resource, $modal, $location) {
        $scope.account = {};
        $scope.registered = false;
        $scope.agreed = false;

        //Creating a type to check with the server if a username already exists.
        var Username = $resource('api/sooa/usernames/:username', {username: '@username'});
        var Email = $resource('api/sooa/emails/:email', {email: '@email'});

        var NewAccount = $resource('api/sooa/accounts/register');

        $scope.registerAccount = function() {
            if($scope.agreed) {
                //console.log("Creating account");
                var acctToRegister = new NewAccount();
                acctToRegister.accountType = 'author';
                acctToRegister.employer =  $scope.account.employer;
                acctToRegister.fullName =  $scope.account.fullName;
                acctToRegister.phone =  $scope.account.phone;
                acctToRegister.title =  $scope.account.title;
                acctToRegister.juridiction =  $scope.account.juridiction;
                acctToRegister.username =  $scope.account.username;
                acctToRegister.password =  $scope.account.password;
                acctToRegister.email =  $scope.account.email;
                acctToRegister.signedConfidentialityAgreement = true;
                acctToRegister.$save(
                    function() {
                        if (acctToRegister.text ===  'userAdded') {
                            $scope.account = {};
                            //should unfreeze the form
                            $scope.registered = true;
                            $location.path('/registrationSubmitted');
                        }else{
                            $scope.registered = false;
                        }
                    },
                    function() {
                        $scope.registered = false;
                    }
                );
                //should freeze the form - at least the button
                $scope.registered = true;
            }
        };

//        $scope.registerAccount = function() {
//            /* Check for username already in use
//               Verify email not already associated to an account
//               Will need to send an email if success
//               */
//            var modalInstance = $modal.open({
//                backdrop: true,
//                keyboard: true,
//                backdropClick: false,
//                controller: 'AgreementCtrl',
//                templateUrl: 'views/account/agreement.html'
//            });
//
//            modalInstance.result.then(function(result) {
//                if(result) {
//                    //console.log("Creating account");
//                    var acctToRegister = new NewAccount();
//                    acctToRegister.accountType = 'provider';
//                    acctToRegister.company =  $scope.account.company;
//                    acctToRegister.firstname =  $scope.account.firstname;
//                    acctToRegister.lastname =  $scope.account.lastname;
//                    acctToRegister.username =  $scope.account.username;
//                    acctToRegister.password =  $scope.account.password;
//                    acctToRegister.email =  $scope.account.email;
//                    acctToRegister.signedConfidentialityAgreement = true;
//
//                    acctToRegister.$save(
//                        function() {
//                            if (acctToRegister.text ===  'userAdded') {
//                                $scope.account = {};
//                                //should unfreeze the form
//                                $scope.registered = true;
//                                $location.path('/home');
//                            }
//                        },
//                        function() {
//                            $scope.registered = false;
//                        }
//                    );
//                    //should freeze the form - at least the button
//                    $scope.registered = true;
//                }
//                else {
//                    //console.log('Account not created');
//                }
//            });
//        };
    }
]);
//
//angular.module('igl').controller('AgreementCtrl', ['$scope', '$modalInstance',
//    function ($scope, $modalInstance) {
//
//        $scope.acceptAgreement =  function() {
//            var res = true;
//            $modalInstance.close(res);
//        };
//
//        $scope.doNotAcceptAgreement =  function() {
//            var res = false;
//            $modalInstance.close(res);
//        };
//    }
//]);
