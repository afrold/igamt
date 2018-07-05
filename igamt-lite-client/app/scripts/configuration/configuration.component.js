angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {


    $scope.activeId = "content";
    $scope.includePC = false;
    $scope.init = function () {
        $scope.changed = false;
        $scope.activeId = "content";
        ConfigurationService.getUserExportConfig().then(function (response) {
            var copy = response;
            $scope.config = angular.copy(response);
            $scope.configCopy = copy;
        });

    }

    $scope.initExportFont = function () {
        ConfigurationService.findFonts().then(function (response) {
            $scope.fonts = response;
            // $scope.resetUserExportFontConfig();
            $scope.changed = false;
        });

    }

    $scope.saveUserExportFontConfig = function (userExportFontConfig) {
        ConfigurationService.saveUserExportFontConfig(userExportFontConfig).then(function (response) {
            $scope.resetChanged();
            $rootScope.msg().text = "ConfigurationSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "ConfigurationSavedFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    }

    $scope.resetUserExportFontConfig = function () {
        ConfigurationService.getUserExportFontConfig().then(function (response) {
            $scope.userExportFontConfig = response;
            $scope.updateUserFontRadio();
            $scope.resetChanged();
            $rootScope.msg().text = "ConfigurationResetSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "ConfigurationResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    }

    $scope.restoreDefaultExportFontConfig = function () {
        ConfigurationService.restoreDefaultExportFontConfig().then(function (response) {
            $scope.resetChanged();
            $scope.userExportFontConfig = response;
            $scope.updateUserFontRadio();
            $scope.resetChanged();
            $rootScope.msg().text = "DefaultRestored";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "DefaultRestoredFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    }

    $scope.updateUserFontRadio = function () {
        $scope.userExportFontConfig.exportFont = $scope.fonts.find($scope.isFontEqualToUsers);
    }

    $scope.isFontEqualToUsers = function (font) {
        return font.name === $scope.userExportFontConfig.exportFont.name;
    }

    $scope.isActive = function (str) {
        return str == $scope.activeId;
    }
    $scope.setActive = function (str) {
        $scope.activeId = str;
    }
    $scope.saveExportConfig = function () {
        var configuration = $scope.config;
        configuration.defaultType = false;
        $scope.resetChanged();

        ConfigurationService.saveExportConfig(configuration).then(function (response) {

            $scope.config = response;

            $scope.resetChanged();
            $rootScope.msg().text = "ConfigurationSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "ConfigurationSavedFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        })
    }

    $scope.restoreDefaultExportConfig = function () {
        ConfigurationService.restoreDefaultExportConfig($scope.config).then(function (response) {
            console.log(response);
            $scope.config = response;
            $scope.resetChanged();
            $rootScope.msg().text = "DefaultRestored";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "DefaultRestoredFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    };

    $scope.resetExportConfig = function () {
        ConfigurationService.getUserExportConfig().then(function (response) {
            $scope.resetChanged();
            var copy = response;
            $scope.config = angular.copy(response);
            $scope.configCopy = copy;
            $rootScope.msg().text = "ConfigurationResetSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "ConfigurationResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    }

    $scope.setChanged = function () {
        $scope.changed = true;
    }
    $scope.resetChanged = function () {
        $scope.changed = false;
    }

});
