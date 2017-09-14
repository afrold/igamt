/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('ProfileSvc', function($http, $q,userInfoService) {
	var svc = this;
    svc.saveMetaData = function (id, metaData) {
        var delay = $q.defer();
        $http.post('api/igdocuments/'+ id+ '/profile/metadata/save', metaData).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            delay.resolve(saveResponse);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    return svc;
});
