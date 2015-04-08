'use strict';

angular.module('igl')
    .controller('ManageCEHRTsCtrl', ['$scope', 'Vendors', 'CEHRTModelProcessor', '$dialog',
        function ($scope, Vendors, CEHRTModelProcessor, $dialog) {
            $scope.vendors = Vendors;
            $scope.CEHRTs = [];

            $scope.createAccountList = function() {
                if ( angular.isArray($scope.vendors) === true) {
                    angular.forEach(Vendors, function(vendor, key){
                        angular.forEach(vendor.cehrTechnologies, function(cehrt, key){
                            var newSystem = angular.copy(cehrt);
                            newSystem.authorizedVendor = {
                                id: vendor.id,
                                company: vendor.company,
                                email: vendor.email,
                                firstname: vendor.firstname,
                                lastname: vendor.lastname
                            };
                            $scope.CEHRTs[$scope.CEHRTs.length] = newSystem;
                        });
                    });
                }
            };

            $scope.processResponseType = function() {
                angular.forEach($scope.CEHRTs, function(cehrt, key){
                    cehrt.respEmail = cehrt.respMDN = cehrt.respOther = false;
                    cehrt.otherResponse = '';
    
                    //console.log('cehrt=', cehrt);
                    angular.forEach(cehrt.responsesType, function(respType, key){
                        //console.log('resptype=', respType);
                        switch(respType.type) {
                        case 'email':
                            //console.log('Email found');
                            cehrt.respEmail = true;
                            break;
                        case 'MDN':
                            //console.log('MDN found');
                            cehrt.respMDN = true;
                            break;
                        default:
                            if ( respType.type !== '') {
                                //console.log('other response found');
                                cehrt.respOther = true;
                                cehrt.otherResponse = respType.type;
                            }
                        }
                    });
                });
            };

            $scope.processPOAs = function() {
                //Preprocess the model for pretty print
                angular.forEach($scope.CEHRTs, function(cehrt, key){
                    angular.forEach(cehrt.periodsOfAvailability, function(mypoa, key){
                        CEHRTModelProcessor.preProcess(mypoa);
                    });
                });
            };
    
            //Pre-processing JSON message to transform it for display
            $scope.createAccountList();
            $scope.processResponseType();
            $scope.processPOAs();


            //console.log('CEHRTs=', $scope.CEHRTs);
    
            $scope.showCEHRTDetails = false;
            $scope.cehrtIndex = -1;
            $scope.currentCehrt = {};
    
            $scope.showDetails = function(index) {
                if ($scope.cehrtIndex === index) {
                    $scope.showCEHRTDetails = !$scope.showCEHRTDetails;
                }
                else {
                    $scope.showCEHRTDetails = true;
                }
                $scope.currentCehrt = $scope.CEHRTs[index];
                $scope.cehrtIndex = index;
            };
    
            $scope.selectCEHRTNoShow  = function(index) {
                $scope.currentCehrt = $scope.CEHRTs[index];
            };

            $scope.disableCEHRT = function(index) {
                var title = $.i18n.prop('manage.cehrt.disableModal.title');
                var msg = $.i18n.prop('manage.cehrt.disableModal.msg');
                var okButton = $.i18n.prop('manage.cehrt.disableModal.okButton');
                var cancelButton = $.i18n.prop('manage.cehrt.disableModal.cancelButton');
                var btns = [{result:'cancel', label: cancelButton}, {result:'ok', label: okButton, cssClass: 'btn'}];
    
                $dialog.messageBox(title, msg, btns)
                .open()
                .then(function(result){
                    if ( result === 'ok' ) {
//                        console.log('Disabling ', $scope.CEHRTs[index]);
                    }
                });
            };
        }
]);
