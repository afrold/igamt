/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('PcService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', '$http', '$q', 'userInfoService', function ($rootScope, ViewSettings, ElementUtils, $http, $q,userInfoService) {
        var PcService = {

            save: function (pc) {
                var delay = $q.defer();
                // table.accountId = userInfoService.getAccountID();
                $http.post('api/profileComponent/save', pc).then(function (response) {
                    var saved = angular.fromJson(response.data);
                    delay.resolve(saved);
                    return saved;
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            },

            findAll: function () {
                var delay = $q.defer();
                $http.post('api/profileComponent/findAll').then(function (response) {
                    var res = angular.fromJson(response.data);
                    delay.resolve(res);
                }, function (error) {
                    delay.reject(error);
                });
                return delay.promise;
            }

        }
        return PcService;
    }]);
