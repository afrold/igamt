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
            form.target = "_blank";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
            blockUI.stop();
        },

        exportAsWithLayout: function(igDocument, format, layout) {
            blockUI.start();
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + igDocument.id + '/export/' + format + '/' +layout);
            form.method = "POST";
            form.target = "_blank";
            var layoutParameter = document.createElement("input");
            layoutParameter.type = "text";
            layoutParameter.name = "layout";
            layoutParameter.value = layout;
            form.appendChild(layoutParameter);
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
            blockUI.stop();
        },

       copyMessage: function(igId, childId) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igId + '/copyMessage', childId).then(function(response) {
                var link = angular.fromJson(response.data);
                delay.resolve(link);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        addMessages: function(igId, childrenIds) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igId + '/addMessages', childrenIds).then(function(response) {
                //var link = angular.fromJson(response.data);
                delay.resolve(true);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        findAndAddMessages: function(igId, event) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igId + '/findAndAddMessages', angular.toJson(event)).then(function(response) {
                var msgs = angular.fromJson(response.data);
                delay.resolve(msgs);
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
        share: function(igDocId, shareParticipantIds) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igDocId + '/share', shareParticipantIds).then(function(response) {
                delay.resolve(response.data);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        unshare: function(igDocId, participantId) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igDocId + '/unshare', participantId).then(function(response) {
                delay.resolve(response.data);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        saveProfileComponent: function(id, pc) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + id + '/profile/profilecomponent/save', pc).then(function(response) {
                var saveResponse = angular.fromJson(response.data);
                delay.resolve(saveResponse);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;

        },
        updateDate: function(igdocument) {
            var delay = $q.defer();
            $http.post('api/igdocuments/' + igdocument.id + '/updateDate').then(function(response) {
                var resu = response.data;
                igdocument.dateUpdated = resu;
                delay.resolve(resu);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        orderIgDocument:function(igList){
        var delay = $q.defer();
            $http.post('api/igdocuments/reorderIgs',igList).then(function(response) {
                var resu = response.data;
                delay.resolve(resu);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;

        },

        saveConformanceProfileSection:function(id,textContent, config){
            var delay = $q.defer();
             var wrapper={
                 sectionContents : textContent,
                 config: config
             };
            $http.post('api/igdocuments/'+id+'/profile/messages/section',wrapper).then(function(response) {
                var resu = response.data;
                delay.resolve(resu);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;

        }

    };
    return IgDocumentService;
});
