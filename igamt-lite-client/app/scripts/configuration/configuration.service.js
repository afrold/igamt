angular.module('igl').factory('ConfigurationService',
    function ($rootScope, $http, $q) {

        var ConfigurationService = {

            saveExportConfig: function (configuration) {
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();

                $http.post('api/exportConfiguration/saveExportConfig', configuration).then(function (response) {
                    var conf = angular.fromJson(response.data);
                    delay.resolve(conf);

                }, function (error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;

            },
            restoreDefaultExportConfig: function (configuration) {
                var delay = $q.defer();
                $http.post('api/exportConfiguration/restoreDefaultExportConfig', configuration).then(function (response) {
                    console.log("resopense");
                    console.log(response);
                    var conf = angular.fromJson(response.data);
                    delay.resolve(conf);
                }, function (error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
            },

            getUserExportConfig: function () {
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.get('api/exportConfiguration/getUserExportConfig').then(function (response) {
                    console.log(response);
                    var conf = angular.fromJson(response.data);
                    console.log(response)
                    delay.resolve(conf);
                }, function (error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;

            },

            findFonts: function () {
                var delay = $q.defer();
                $http.get('api/exportConfiguration/findFonts').then(function (response) {
                    var fonts = angular.fromJson(response.data);
                    delay.resolve(fonts);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            getUserExportFontConfig: function () {
                var delay = $q.defer();
                $http.get('api/exportConfiguration/getUserExportFontConfig').then(function (response) {
                    var userExportFontConfig = angular.fromJson(response.data);
                    delay.resolve(userExportFontConfig);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            saveUserExportFontConfig: function (userExportFontConfig) {
                var delay = $q.defer();
                $http.post('api/exportConfiguration/saveExportFontConfig', userExportFontConfig).then(function (response) {
                    var conf = angular.fromJson(response.data);
                    delay.resolve(conf);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            restoreDefaultExportFontConfig: function () {
                var delay = $q.defer();
                $http.post('api/exportConfiguration/restoreDefaultExportFontConfig').then(function (response) {
                    console.log("response");
                    console.log(response);
                    var conf = angular.fromJson(response.data);
                    delay.resolve(conf);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            }

        };



        return ConfigurationService;
    });
