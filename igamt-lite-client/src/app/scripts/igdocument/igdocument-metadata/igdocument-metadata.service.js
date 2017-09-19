/**
 * Created by haffo on 9/12/17.
 */


angular.module('igl').factory('IGDocumentSvc', function($http, $q, $rootScope) {
  var IGDocumentSvc = {
    loadIgDocumentMetaData : function() {
      var delay = $q.defer();
      if ($rootScope.config || $rootScope.config === null) {
        $http.get('api/igdocuments/config').then(function(response) {
          $rootScope.config = angular.fromJson(response.data);
          delay.resolve($rootScope.config);
        }, function(error) {
          delay.reject(error);
        });
      } else {
        delay.resolve($rootScope.config);
      }
      return delay.promise;
    }
  }

  return IGDocumentSvc;
});
