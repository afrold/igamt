/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('IgDocumentService',
    ['$rootScope', 'ViewSettings', '$q', 'userInfoService', '$http', 'StorageService', function ($rootScope, ViewSettings, $q, userInfoService, $http,StorageService) {
        var IgDocumentService = {
            save: function (igDocument) {
                $rootScope.saved = false;
                var delay = $q.defer();
                var changes = angular.toJson([]);
                igDocument.accountId = userInfoService.getAccountID();
                var data = angular.fromJson({"changes": changes, "igDocument": igDocument});
                $http.post('api/igdocuments/save', data).then(function (response) {
                    var saveResponse = angular.fromJson(response.data);
                    igDocument.metaData.date = saveResponse.date;
                    igDocument.metaData.version = saveResponse.version;
                    $rootScope.saved = true;
                    delay.resolve(saveResponse);
                }, function (error) {
                    delay.reject(error);
                    $rootScope.saved = false;
                });
                return delay.promise;
            },
            exportAs: function(igDocument,format){
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
            }
        };
        return IgDocumentService;
    }]);
