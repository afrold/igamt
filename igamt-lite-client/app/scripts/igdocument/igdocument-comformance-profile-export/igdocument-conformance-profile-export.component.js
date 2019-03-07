/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SelectMessagesForExportCtrl', function ($scope, igdocumentToSelect, $rootScope, $http, $cookies, ExportSvc, GVTSvc, $modal, $timeout, $window, $mdDialog, toGVT, dynamicMessages,StorageService) {
    $scope.igdocumentToSelect = igdocumentToSelect;
    $scope.toGVT = toGVT;
    $scope.exportStep = 'MESSAGE_STEP';
    $scope.xmlFormat = 'Validation';
    $scope.selectedMessages = [];
    $scope.dynamicMessages=dynamicMessages;

    $scope.loading = false;
    $scope.info = {text: undefined, show: false, type: null, details: null};
    $scope.redirectUrl = null;
    $scope.user = {username: StorageService.getGvtUsername(), password: StorageService.getGvtPassword()};
    $scope.appInfo = $rootScope.appInfo;
    $scope.selected = false;
    $scope.targetApps = _.sortBy($rootScope.appInfo.connectApps,'position');
    $scope.app=  $scope.targetApps.length&&$scope.targetApps.length? $scope.targetApps[0]:null;
    $scope.targetDomains = null;
    $scope.error = null;

    var url = $scope.targetApps !=null&&$scope.targetApps.length>0 ? $scope.targetApps[0].url: null;


    $scope.target = {
        url: url, domain: null
    };

    $scope.selectMessage=function (message) {


    };

    $scope.newDomain = null;


    // init selection to false
    for (var i in $scope.dynamicMessages) {
        var message = $scope.dynamicMessages[i];
        $scope.selected = false;
        message.selected = false;
    }


    $scope.selectTargetUrl = function () {
        console.log($scope.app);
        StorageService.set("EXT_TARGET_URL", $scope.app.url);
        $scope.loadingDomains = false;
        $scope.targetDomains = null;
        $scope.target.domain = null;
        $scope.newDomain = null;
        $scope.error = null;
    };




    $scope.selectTargetDomain = function () {
        $scope.newDomain = null;
        if ($scope.target.domain != null) {
            StorageService.set($scope.app.url + "/EXT_TARGET_DOMAIN", $scope.target.domain);
        }
    };


    $scope.trackSelections = function () {
        $scope.selected = false;
        for (var i in $scope.dynamicMessages) {
            var message = $scope.dynamicMessages[i];
            if (message.selected) $scope.selected = true;
        }
    };




    $scope.selectionAll = function (bool) {
        for (var i in $scope.dynamicMessages) {
            var message = $scope.dynamicMessages[i];
            message.selected = bool;
        }
        $scope.selected = bool;
    };

    $scope.generatedSelectedMessagesIDs = function () {
        $scope.selectedMessages = [];
        for (var i in $scope.dynamicMessages) {
            var message = $scope.dynamicMessages[i];
            if (message.selected) {
                $scope.selectedMessages.push(message);
            }
        }
    };


    $scope.goBack = function () {
        $scope.error = null;
        if ($scope.exportStep === 'LOGIN_STEP') {
            $scope.exportStep = 'MESSAGE_STEP';
        } else if ($scope.exportStep === 'DOMAIN_STEP') {
            $scope.exportStep = 'LOGIN_STEP';
        } else if ($scope.exportStep === 'ERROR_STEP') {
            $scope.loadDomains();
         }
    };

    $scope.login = function () {
        GVTSvc.login($scope.user.username, $scope.user.password, $scope.app.url).then(function (auth) {
            StorageService.setGvtUsername($scope.user.username);
            StorageService.setGvtPassword($scope.user.password);
            StorageService.setGVTBasicAuth(auth);
            $scope.loadDomains();
        }, function (error) {
            $scope.error = "Invalid credentials";
        });
    };

    $scope.loadDomains = function () {
        $scope.targetDomains = [];
        $scope.target.domain = null;
        if($scope.app.url != null) {
            GVTSvc.getDomains($scope.app.url, StorageService.getGVTBasicAuth()).then(function (result) {
                $scope.targetDomains = result;
                var savedTargetDomain = StorageService.get($scope.app.url + "/EXT_TARGET_DOMAIN");
                if (savedTargetDomain != null) {
                    for (var targetDomain in $scope.targetDomains) {
                        if (targetDomain.domain === savedTargetDomain) {
                            $scope.target.domain = savedTargetDomain;
                            break;
                        }
                    }
                } else {
                    if ($scope.targetDomains != null && $scope.targetDomains.length == 1) {
                        $scope.target.domain = $scope.targetDomains[0].domain;
                    }
                }
                $scope.exportStep = 'DOMAIN_STEP';
                $scope.selectTargetDomain();
                $scope.loadingDomains = false;
            }, function (error) {
                $scope.loadingDomains = false;
             });
        }
    };


    $scope.goNext = function () {
        $scope.error = null;
        if ($scope.exportStep === 'LOGIN_STEP') {
            $scope.login();
        } else if ($scope.exportStep === 'DOMAIN_STEP') {
            $scope.exportToGVT();
        } else if ($scope.exportStep === 'ERROR_STEP') {
            $scope.loadDomains();
        } else if ($scope.exportStep === 'MESSAGE_STEP') {
            $scope.generatedSelectedMessagesIDs();
            $scope.exportStep = 'LOGIN_STEP';
        }
    };

    $scope.createNewDomain = function () {
        $scope.newDomain = {name: null, key: null, homeTitle: null};
        $scope.error = null;
        $scope.target.domain = null;
    };


    $scope.exportAsZIPforSelectedMessages = function () {
        $scope.loading = true;
        $scope.generatedSelectedMessagesIDs();
        ExportSvc.exportAsXMLByMessageIds($scope.igdocumentToSelect.id, $scope.selectedMessages, $scope.xmlFormat);
        $scope.loading = false;
    };

    $scope.cancel = function () {
        $mdDialog.hide();
    };


    $scope.showErrors = function (errorDetails) {
        $scope.exportStep = 'ERROR_STEP';
        // $scope.info['details'] = errorDetails;
        // $scope.errorDetails = errorDetails;
        // $scope.tmpProfileErrors = errorDetails != null ? [].concat($scope.errorDetails.profileErrors) : [];
        // $scope.tmpConstraintErrors = errorDetails != null ? [].concat($scope.errorDetails.constraintsErrors) : [];
        // $scope.tmpValueSetErrors = errorDetails != null ? [].concat($scope.errorDetails.vsErrors) : [];
    };

    $scope.exportToGVT = function () {
        $scope.info.text = null;
        $scope.info.show = false;
        $scope.info.type = 'danger';
        $scope.info['details'] = null;
        var auth = StorageService.getGVTBasicAuth();
        if ($scope.app.url != null && $scope.target.domain != null && auth != null) {
            $scope.loading = true;
            GVTSvc.exportToGVT($scope.igdocumentToSelect.id, $scope.selectedMessages, auth, $scope.app.url, $scope.target.domain).then(function (map) {
                $scope.loading = false;
                var response = angular.fromJson(map.data);
                if (response.success === false) {
                    $scope.info.text = "gvtExportFailed";
                    $scope.info['details'] = response.report;
                    $scope.showErrors($scope.info.details);
                    $scope.info.show = true;
                    $scope.info.type = 'danger';
                } else {
                    var token = response.token;
                    $scope.exportStep = 'ERROR_STEP';
                    $scope.info.text = 'gvtRedirectInProgress';
                    $scope.info.show = true;
                    $scope.info.type = 'info';
                    $scope.redirectUrl = $scope.app.url + $rootScope.appInfo.connectUploadTokenContext + "?x=" + encodeURIComponent(token) + "&y=" + encodeURIComponent(auth) + "&d=" + encodeURIComponent($scope.target.domain);
                    $timeout(function () {
                        $scope.loading = false;
                        $window.open($scope.redirectUrl, "_blank");
                    }, 1000);
                }
            }, function (error) {
                $scope.info.text = "gvtExportFailed";
                $scope.info['details'] = "Sorry, we couldn't push your profiles. Please contact the administrator for more information";
                $scope.info.show = true;
                $scope.info.type = 'danger';
                $scope.loading = false;
                $scope.exportStep = 'ERROR_STEP';
            });
        }
    };


    $scope.exportAsZIPToGVT = function () {
        $scope.loading = true;
        $scope.error = null;
        if ($scope.newDomain != null) {
            $scope.newDomain.key = $scope.newDomain.name.replace(/\s+/g, '-').toLowerCase();
            GVTSvc.createDomain(StorageService.getGVTBasicAuth(), $scope.app.url, $scope.newDomain.key, $scope.newDomain.name, $scope.newDomain.homeTitle).then(function (domain) {
                $scope.loading = false;
                $scope.target.domain = $scope.newDomain.key;
                $scope.exportToGVT();
            }, function (error) {
                $scope.loading = false;
                $scope.error = error.text;
            });
        } else if ($scope.app.url != null && $scope.target.domain != null) {
            $scope.exportToGVT();
        }
    };


    if ($scope.targetApps != null) {
        var savedTargetUrl = StorageService.get("EXT_TARGET_URL");
        if (savedTargetUrl && savedTargetUrl != null) {
            for (var targetApp in $scope.targetApps) {
                if (targetApp.url === savedTargetUrl) {
                    $scope.app.url = targetApp.url;
                    break;
                }
            }
        } else if ($scope.targetApps.length == 1) {
            $scope.app.url = $scope.targetApps[0].url;
        }
        $scope.selectTargetUrl();
    }

});

