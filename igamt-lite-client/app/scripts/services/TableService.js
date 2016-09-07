/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('TableService', ['$rootScope', 'ViewSettings', 'ElementUtils', '$http', '$q', 'FilteringSvc', 'userInfoService', function($rootScope, ViewSettings, ElementUtils, $http, $q, FilteringSvc, userInfoService) {
    var TableService = {

        save: function(table) {
            var delay = $q.defer();
            table.accountId = userInfoService.getAccountID();
            $http.post('api/tables/save', table).then(function(response) {
                console.log(table);
                var saved = angular.fromJson(response.data);
                delay.resolve(saved);
                return saved;
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        getOne: function(id) {
            var delay = $q.defer();
//            if ($rootScope.tablesMap[id] === undefined || $rootScope.tablesMap[id] === undefined) {
//                console.log("getOne==>");
                $http.get('api/tables/' + id).then(function(response) {
                    var table = angular.fromJson(response.data);
                    delay.resolve(table);
                }, function(error) {
                    delay.reject(error);
                });
//            } else {
//                delay.resolve($rootScope.tablesMap[id]);
//            }
            return delay.promise;
        },
        get: function(ids) {
            var delay = $q.defer();
            $http.post('api/tables/findAllByIds', ids).then(function(response) {
                var tables = angular.fromJson(response.data);
                delay.resolve(tables);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        getBindingIdentifiers: function(ids) {

        },
        merge: function(to, from) {
            to = angular.extend(to, from);
            //                to.libIds = from.libIds;
            //            	to.name = from.name;
            //            	to.description = from.description;
            //            	to.version = from.version;
            //            	to.oid = from.oid;
            //            	to.bindingIdentifier = from.bindingIdentifier;
            //            	to.stability = from.stability;
            //            	to.extensibility = from.extensibility;
            //            	to.contentDefinition = from.contentDefinition;
            //            	to.group = from.group;
            //            	to.order = from.order;
            //            	to.codes = from.codes;
            //            	to.status = from.status;
            //            	to.accountId = from.accountId;
            //            	to.date = from.date;
            //            	to.scope = from.scope;

            return to;
        },
        delete: function(table) {
            return $http.post('api/tables/' + table.id + '/delete');
        },

        getTableLink: function(table) {
            return { id: table.id, bindingIdentifier: table.bindingIdentifier };
        },
        findAllByIds: function(tableIds) {
            var delay = $q.defer();
            $http.post('api/tables/findShortAllByIds', tableIds).then(function(response) {
                delay.resolve(angular.fromJson(response.data));
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        }

    };
    return TableService;
}]);