angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {


    $scope.activeId = "content";
    $scope.includePC = false;
    $scope.init = function () {

        $scope.configMap = {};
        $scope.changed = false;
        $scope.activeId = "content";
        ConfigurationService.getUserExportConfig().then(function (response) {
            var copy = response;
            $scope.config = angular.copy(response);
            $scope.configCopy = copy;
            $scope.configMap[response.id] = response;
        });

    }

    $scope.initExportFont = function () {
        ConfigurationService.findFonts().then(function (response) {
            $scope.fonts = response;
            $scope.resetUserExportFontConfig();
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
            $rootScope.msg().text = "ConfigurationSavedFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    }

    $scope.resetUserExportFontConfig = function () {
        ConfigurationService.getUserExportFontConfig().then(function (response) {
            $scope.userExportFontConfig = response;
            $scope.updateUserFontRadio();
            $scope.resetChanged();
        });
    }

    $scope.restoreDefaultExportFontConfig = function () {
        ConfigurationService.restoreDefaultExportFontConfig().then(function (response) {
            $scope.resetChanged();
            $scope.userExportFontConfig = response;
            $scope.updateUserFontRadio();
            $scope.resetChanged();
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
        var configuration = $scope.exportConfig;
        configuration.defaultType = false;
        $scope.resetChanged();

        ConfigurationService.saveExportConfig(configuration).then(function (response) {

            $scope.config = response;

            $scope.resetChanged();
            $rootScope.msg().text = "ConfigurationSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "ConfigurationSavedFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        })
    }

    $scope.restoreDefaultExportConfig = function () {
        ConfigurationService.restoreDefault($scope.config).then(function (response) {
            console.log(response);
            $scope.config = response;
            $scope.resetChanged();

            $rootScope.msg().text = "DefaultRestored";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function (error) {
            $rootScope.msg().text = "DefaultRestoredFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        });
    };

    $scope.resetExportConfig = function () {
        $scope.config = angular.copy($scope.configCopy);
        $scope.resetChanged();
    }

    $scope.setChanged = function () {
        $scope.changed = true;
    }
    $scope.resetChanged = function () {
        $scope.changed = false;
    }

    $scope.getStyle = function (bool) {
        var beige = {
            'background': '#ffcc00'
        };
        var gray = {
            'background': 'gainsboro'
        };

        if (bool) {
            return beige;
        } else {
            return gray;
        }
    }

    $scope.updateWidth = function (felxwidth) {
        if (felxwidth) {
            $scope.felxwidth = 100 / 3;

        } else {
            $scope.felxwidth = 100 / 2;
        }

    }

});
