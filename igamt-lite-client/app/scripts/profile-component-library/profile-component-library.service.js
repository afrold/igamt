/**
 * Created by haffo on 9/12/17.
 */
/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('PcLibraryService', function($http, $httpBackend, $q, userInfoService) {

  var svc = this;
  svc.getProfileComponentsByLibrary = function(pcLibId) {
    return $http.get(
      'api/profilecomponent-library/' + pcLibId + '/profilecomponents')
      .then(function(response) {
        //					console.log("response" + JSON.stringify(response));
        return angular.fromJson(response.data);
      });
  },
    svc.getProfileComponentLibrary = function(pcLibId) {
      return $http.get(
        'api/profilecomponent-library/' + pcLibId)
        .then(function(response) {
          //					console.log("response" + JSON.stringify(response));
          return angular.fromJson(response.data);
        });
    },
    svc.addComponentToLib = function(igId, pc) {
      var delay = $q.defer();

      $http.post('api/profilecomponent-library/' + igId + '/add', pc).then(function(response) {

        console.log(response);
        var saved = angular.fromJson(response.data);
        delay.resolve(saved);
        return saved;
      }, function(error) {
        delay.reject(error);
      });
      return delay.promise;
    },
    svc.addComponentsToLib = function(igId, pcs) {
      var delay = $q.defer();

      $http.post('api/profilecomponent-library/' + igId + '/addMult', pcs).then(function(response) {

        console.log(response);
        var saved = angular.fromJson(response.data);
        delay.resolve(saved);
        return saved;
      }, function(error) {
        delay.reject(error);
      });
      return delay.promise;
    }




  return svc;
});
