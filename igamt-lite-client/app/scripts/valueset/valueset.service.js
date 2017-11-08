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

                $http.get('api/tables/' + id).then(function(response) {
                    var table = angular.fromJson(response.data);
                    delay.resolve(table);
                }, function(error) {
                    delay.reject(error);
                });

            return delay.promise;
        },

        getOneInLibrary: function(id, libId) {
            var delay = $q.defer();

            $http.get('api/tables/'+libId+'/'+ id).then(function(response) {
                var table = angular.fromJson(response.data);
                delay.resolve(table);
            }, function(error) {
                delay.reject(error);
            });

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
        },
        findShortByScope: function(scope) {
            var delay = $q.defer();
            $http.post('api/tables/findShortByScope', scope).then(function(response) {
                delay.resolve(angular.fromJson(response.data));
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        findShortById: function(id) {
            var delay = $q.defer();
            $http.post('api/tables/findShortById', id).then(function(response) {
                delay.resolve(angular.fromJson(response.data));
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
         publish: function(table) {
                var delay = $q.defer();
                table.accountId = userInfoService.getAccountID();
                $http.post('api/tables/publish', table).then(function(response) {
                    var saveResponse = angular.fromJson(response.data);
                   
                    delay.resolve(saveResponse);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
        },
        share:function(tableId,shareParticipantIds, accountId){
            var delay = $q.defer();
            $http.post('api/tables/' + tableId + '/share', {'accountId': accountId, 'participantsList': shareParticipantIds}).then(function (response) {
                delay.resolve(response.data);
            }, function (error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        unshare: function(tableId, participantId){
            var delay = $q.defer();
            $http.post('api/tables/' + tableId + '/unshare', participantId).then(function (response) {
                delay.resolve(response.data);
             }, function (error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        getSharedTables: function(){
            var delay = $q.defer();
            $http.get('api/tables/findShared').then(function (response) {
                delay.resolve(response.data);
             }, function (error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        getPendingSharedTables: function(){
            var delay = $q.defer();
            $http.get('api/tables/findPendingShared').then(function (response) {
                delay.resolve(response.data);
             }, function (error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        crossRef: function(table, igDocumentId) {
            var delay = $q.defer();
            var wrapper = {};
            wrapper.tableId= table.id;
            wrapper.igDocumentId= igDocumentId;
            var value= $rootScope.getUpdatedBindingIdentifier(table);
            wrapper.assertionId="ValueSetID="+"\""+value+"\"";
            $http.post('api/crossRefs/table', wrapper).then(function(response) {
                var ref = angular.fromJson(response.data);
                delay.resolve(ref);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        findAllPhinvads: function() {
            var delay = $q.defer();
            $http.get('api/igdocuments/PHINVADS/tables').then(function(response) {
                var tables = angular.fromJson(response.data);
                delay.resolve(tables);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },

        savePhinvads: function(libId,tables) {
        var delay = $q.defer();
            $http.post('api/igdocuments/'+libId+'/addPhinvads', tables).then(function(response) {
            var tables = angular.fromJson(response.data);
            delay.resolve(tables);
            }, function(error) {
            delay.reject(error);
            });
            return delay.promise;
        },
        searchForDelta: function(scope,version,bindingIdentifier) {

            var wrapper={
                scope:scope,version:version,bindingIdentifier:bindingIdentifier
            };
            console.log(wrapper);
            var delay = $q.defer();
            $http.post('api/tables/searchForDelta',wrapper).then(function(response) {
                var tables = angular.fromJson(response.data);
                delay.resolve(tables);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        }

    };
    return TableService;
}]);
