/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('IgDocumentService', function($rootScope, ViewSettings, $q, userInfoService, $http, StorageService, $cookies, blockUI) {
    var IgDocumentService = {
        save: function(igDocument) {
            $rootScope.saved = false;
            var delay = $q.defer();
            var changes = angular.toJson([]);
            igDocument.accountId = userInfoService.getAccountID();
            var data = angular.fromJson({ "changes": changes, "igDocument": igDocument });
            $http.post('api/igdocuments/save', data).then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                igDocument.metaData.date = saveResponse.date;
                igDocument.metaData.version = saveResponse.version;
                $rootScope.saved = true;
                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
                $rootScope.saved = false;
            });
            return delay.promise;
        },
        exportAs: function(igDocument, format) {
            blockUI.start();
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + igDocument.id + '/export/' + format);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
            blockUI.stop();
        },

        addMessage: function(igId, child) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igId + '/addMessage', child).then(function(response) {
                var link = angular.fromJson(response.data);
                delay.resolve(link);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        deleteMessage: function(igId, messageId) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igId + '/deleteMessage/' + messageId).then(function(response) {
                var res = angular.fromJson(response.data);
                delay.resolve(res);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        saveMetadata: function(id, metaData) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + id + '/metadata/save', metaData).then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        getIgDocumentsByScopesAndVersion: function(scopes, hl7Version) {
            console.log("igdocuments/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
            var scopesAndVersion = {
                "scopes": scopes,
                "hl7Version": hl7Version
            };
            return $http.post(
                    'api/igdocuments/findByScopesAndVersion', angular.toJson(scopesAndVersion))
                .then(function(response) {
                    console.log("getIgDocumentsByScopesAndVersion response size=" + response.data.length);
                    return angular.fromJson(response.data);
                });
        },
        getOne: function(id) {
            var delay = $q.defer();
            $http.get('api/igdocuments/' + id).then(function(response) {
                var igDocument = angular.fromJson(response.data);
                delay.resolve(igDocument);
            }, function(error) {
                delay.reject(error);
            });

            return delay.promise;
        },
    };
    return IgDocumentService;
});
