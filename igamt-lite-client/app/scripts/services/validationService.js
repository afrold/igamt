angular.module('igl').factory('ValidationService', function($http, $q, userInfoService) {

    var svc = this;

   
        svc.validatedatatype = function(dt,igHl7Version) {
            var delay = $q.defer();

            $http.post('api/validation/validateDatatype/'+igHl7Version, dt).then(function(response) {

                console.log(response);
                var saved = angular.fromJson(response.data);
                delay.resolve(saved);
                return saved;
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        svc.validateSegment = function(seg,igHl7Version) {
            var delay = $q.defer();

            $http.post('api/validation/validateSegment/'+igHl7Version, seg).then(function(response) {

                console.log(response);
                var saved = angular.fromJson(response.data);
                delay.resolve(saved);
                return saved;
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        svc.validateMessage = function(msg,hl7Version) {
            var delay = $q.defer();
            msg.hl7Version = hl7Version;

            $http.post('api/validation/validateMessage',msg).then(function(response) {

                console.log(response);
                var saved = angular.fromJson(response.data);
                delay.resolve(saved);
                return saved;
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;
        },
        svc.validateIg = function(ig) {
            var delay = $q.defer();

            $http.post('api/validation/validateIg',ig).then(function(response) {

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