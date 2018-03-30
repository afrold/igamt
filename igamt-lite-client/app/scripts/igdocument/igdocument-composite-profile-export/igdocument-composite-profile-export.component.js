/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SelectCompositeProfilesForExportCtrl', function ($scope, $mdDialog, igdocumentToSelect, $rootScope, $http, $cookies, ExportSvc, GVTSvc, $timeout, $window, toGVT, StorageService) {
    $scope.igdocumentToSelect = igdocumentToSelect;
    $scope.toGVT = toGVT;
    $scope.exportStep = 0;
    $scope.xmlFormat = 'Validation';
    $scope.selectedCompositeProfileIDs = [];
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
    for (var i in $scope.igdocumentToSelect.profile.compositeProfiles.children) {
        var message = $scope.igdocumentToSelect.profile.compositeProfiles.children[i];
        message.selected = $scope.selected = false;
    }


    if ($scope.targetApps != null) {
        var savedTargetUrl = StorageService.get("EXT_TARGET_URL");
        if (savedTargetUrl && savedTargetUrl != null) {
            for (var targetApp in $scope.targetApps) {
                if (targetApp.url === savedTargetUrl) {
                    $scope.target.url = targetApp.url;
                    break;
                }
            }
            $scope.selectTargetUrl();
        }
    }


    $scope.selectTargetUrl = function () {
        if ($scope.target.url != null) {
            StorageService.set("EXT_TARGET_URL", $scope.target.url);
            $scope.loadingDomains = true;
            $scope.targetDomains = null;
            $scope.target.domain = null;
            GVTSvc.getDomains($scope.target.url).then(function (domains) {
                $scope.targetDomains = domains;
                var savedTargetDomain = StorageService.get("EXT_TARGET_DOMAIN");
                if(savedTargetDomain != null){
                    for (var targetDomain in $scope.targetDomains) {
                        if (targetDomain.value === savedTargetDomain) {
                            $scope.target.domain = savedTargetDomain;
                            break;
                        }
                    }
                    $scope.selectTargetDomain();
                }

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
        for (var i in $scope.igdocumentToSelect.profile.compositeProfiles.children) {
            var message = $scope.igdocumentToSelect.profile.compositeProfiles.children[i];
            if (message.selected) $scope.selected = true;
        }
    };

    $scope.selectionAll = function (bool) {
        for (var i in $scope.igdocumentToSelect.profile.compositeProfiles.children) {
            var message = $scope.igdocumentToSelect.profile.compositeProfiles.children[i];
            message.selected = bool;
        }
        $scope.selected = bool;
    };

    $scope.generatedSelectedMessagesIDs = function () {
        $scope.selectedCompositeProfileIDs = [];
        for (var i in $scope.igdocumentToSelect.profile.compositeProfiles.children) {
            var message = $scope.igdocumentToSelect.profile.compositeProfiles.children[i];
            if (message.selected) {
                $scope.selectedCompositeProfileIDs.push(message.id);
            }
        }
    };

    $scope.goBack = function () {
        $scope.exportStep = $scope.exportStep != 0 ? $scope.exportStep - 1 : 0;
    };

    $scope.goNext = function () {
        $scope.exportStep = $scope.exportStep != 2 ? $scope.exportStep + 1 : 2;
    };

    $scope.exportAsZIPforSelectedCompositeProfiles = function () {
        $scope.loading = true;
        $scope.generatedSelectedMessagesIDs();
        ExportSvc.exportAsXMLByCompositeProfileIds($scope.igdocumentToSelect.id, $scope.selectedCompositeProfileIDs, $scope.xmlFormat);
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
        GVTSvc.login($scope.user.username, $scope.user.password).then(function (auth) {
            StorageService.setGvtUsername($scope.user.username);
            StorageService.setGvtPassword($scope.user.password);
            GVTSvc.exportToGVTForCompositeProfile($scope.igdocumentToSelect.id, $scope.selectedCompositeProfileIDs, auth, $scope.target.url, $scope.target.domain).then(function (map) {
                var response = angular.fromJson(map.data);
                if (response.success === false) {
                    $scope.info.text = "gvtExportFailed";
                    $scope.info['details'] = response;
                    $scope.showErrors($scope.info.details);
                    $scope.info.show = true;
                    $scope.info.type = 'danger';
                    $scope.loading = false;
                } else {
                    $scope.exportStep = 2;
                    var token = response.token;
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


});
