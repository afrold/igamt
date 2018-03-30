/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SelectMessagesForExportCtrl', function ($scope, igdocumentToSelect, $rootScope, $http, $cookies, ExportSvc, GVTSvc, $modal, $timeout, $window, $mdDialog, toGVT, StorageService) {
    $scope.igdocumentToSelect = igdocumentToSelect;
    $scope.toGVT = toGVT;
    $scope.exportStep = 0;
    $scope.xmlFormat = 'Validation';
    $scope.selectedMessagesIDs = [];
    $scope.loading = false;
    $scope.info = {text: undefined, show: false, type: null, details: null};
    $scope.redirectUrl = null;
    $scope.user = {username: StorageService.getGvtUsername(), password: StorageService.getGvtPassword()};
    $scope.appInfo = $rootScope.appInfo;
    $scope.selected = false;
    $scope.targetApps = $rootScope.appInfo.connectApps;
    $scope.targetDomains = null;

    $scope.target = {
        url: null, domain: null
    };


    // init selection to false
    for (var i in $scope.igdocumentToSelect.profile.messages.children) {
        var message = $scope.igdocumentToSelect.profile.messages.children[i];
        $scope.selected = false;
        message.selected = false;
    }




    $scope.selectTargetUrl = function () {
        if ($scope.target.url != null) {
            StorageService.set("EXT_TARGET_URL", $scope.target.url);
            $scope.loadingDomains = true;
            $scope.targetDomains = null;
            $scope.target.domain = null;
            GVTSvc.getDomains($scope.target.url).then(function (result) {
                $scope.targetDomains = result;
                var savedTargetDomain = StorageService.get("EXT_TARGET_DOMAIN");
                if(savedTargetDomain != null){
                    for (var targetDomain in $scope.targetDomains) {
                        if (targetDomain.value === savedTargetDomain) {
                            $scope.target.domain = savedTargetDomain;
                            break;
                        }
                    }
                }else{
                    if($scope.targetDomains != null && $scope.targetDomains.length == 1){
                        $scope.target.domain = $scope.targetDomains[0].value;
                    }
                }
                $scope.selectTargetDomain();
                $scope.loadingDomains = false;
            }, function (error) {
                $scope.loadingDomains = false;
                alert(error);
            });
        }
    };

    $scope.selectTargetDomain = function () {
        if ($scope.target.domain != null) {
            StorageService.set("EXT_TARGET_DOMAIN", $scope.target.domain);
        }
    };


    $scope.trackSelections = function () {
        $scope.selected = false;
        for (var i in $scope.igdocumentToSelect.profile.messages.children) {
            var message = $scope.igdocumentToSelect.profile.messages.children[i];
            if (message.selected) $scope.selected = true;
        }
    };

    $scope.selectionAll = function (bool) {
        for (var i in $scope.igdocumentToSelect.profile.messages.children) {
            var message = $scope.igdocumentToSelect.profile.messages.children[i];
            message.selected = bool;
        }
        $scope.selected = bool;
    };

    $scope.generatedSelectedMessagesIDs = function () {
        $scope.selectedMessagesIDs = [];
        for (var i in $scope.igdocumentToSelect.profile.messages.children) {
            var message = $scope.igdocumentToSelect.profile.messages.children[i];
            if (message.selected) {
                $scope.selectedMessagesIDs.push(message.id);
            }
        }
    };


    $scope.goBack = function () {
        $scope.exportStep = $scope.exportStep != 0 ? $scope.exportStep - 1 : 0;
    };

    $scope.goNext = function () {
        $scope.exportStep = $scope.exportStep != 2 ? $scope.exportStep + 1 : 2;
    };

    $scope.exportAsZIPforSelectedMessages = function () {
        $scope.loading = true;
        $scope.generatedSelectedMessagesIDs();
        ExportSvc.exportAsXMLByMessageIds($scope.igdocumentToSelect.id, $scope.selectedMessagesIDs, $scope.xmlFormat);
        $scope.loading = false;
    };

    $scope.cancel = function () {
        $mdDialog.hide();
    };


    $scope.showErrors = function (errorDetails) {
        $scope.exportStep = 2;
        $scope.errorDetails = errorDetails;
        $scope.tmpProfileErrors = errorDetails != null ? [].concat($scope.errorDetails.profileErrors) : [];
        $scope.tmpConstraintErrors = errorDetails != null ? [].concat($scope.errorDetails.constraintsErrors) : [];
        $scope.tmpValueSetErrors = errorDetails != null ? [].concat($scope.errorDetails.vsErrors) : [];
    };

    $scope.exportAsZIPToGVT = function () {
        $scope.loading = true;
        $scope.info.text = null;
        $scope.info.show = false;
        $scope.info.type = 'danger';
        $scope.info['details'] = null;
        $scope.generatedSelectedMessagesIDs();
        GVTSvc.login($scope.user.username, $scope.user.password, $scope.target.url).then(function (auth) {
            StorageService.setGvtUsername($scope.user.username);
            StorageService.setGvtPassword($scope.user.password);
            GVTSvc.exportToGVT($scope.igdocumentToSelect.id, $scope.selectedMessagesIDs, auth, $scope.target.url, $scope.target.domain).then(function (map) {
                var response = angular.fromJson(map.data);
                if (response.success === false) {
                    $scope.info.text = "gvtExportFailed";
                    $scope.info['details'] = response;
                    $scope.showErrors($scope.info.details);
                    $scope.info.show = true;
                    $scope.info.type = 'danger';
                    $scope.loading = false;
                } else {
                    var token = response.token;
                    $scope.exportStep = 2;
                    $scope.info.text = 'gvtRedirectInProgress';
                    $scope.info.show = true;
                    $scope.info.type = 'info';
                    $scope.redirectUrl = $scope.target.url + $rootScope.appInfo.gvtUploadTokenContext + "?x=" + encodeURIComponent(token) + "&y=" + encodeURIComponent(auth) + "&d=" + encodeURIComponent($scope.target.domain);
                    $timeout(function () {
                        $scope.loading = false;
                        $window.open($scope.redirectUrl, "_target", "", false);
                    }, 1000);
                }
            }, function (error) {
                $scope.info.text = "gvtExportFailed";
                $scope.info.show = true;
                $scope.info.type = 'danger';
                $scope.loading = false;
            });
        }, function (error) {
            $scope.info.text = error.data.text;
            $scope.info.show = true;
            $scope.info.type = 'danger';
            $scope.loading = false;
        });
    };

    if($scope.targetApps != null) {
        var savedTargetUrl = StorageService.get("EXT_TARGET_URL");
        if (savedTargetUrl && savedTargetUrl != null) {
            for (var targetApp in $scope.targetApps) {
                if (targetApp.url === savedTargetUrl) {
                    $scope.target.url = targetApp.url;
                    break;
                }
            }
        }else if($scope.targetApps.length == 1){
            $scope.target.url = $scope.targetApps[0].url;
        }
        $scope.selectTargetUrl();
    }

});

