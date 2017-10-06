/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('IGDocumentExportConfigService',  function($rootScope, $http, $q) {
  var IGDocumentExportConfigService = {

    save: function(igdocumentId, exportConfig) {
      var delay = $q.defer();
      $http.post('api/igdocuments/'+ igdocumentId + '/exportConfig', exportConfig).then(function(response) {
         var saved = angular.fromJson(response.data);
        delay.resolve(saved);
        return saved;
      }, function(error) {
        delay.reject(error);
      });
      return delay.promise;
    }
  };
  return IGDocumentExportConfigService;
});
