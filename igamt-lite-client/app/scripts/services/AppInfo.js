/**
 * Created by haffo on 3/3/16.
 */

angular.module('igl').factory('AppInfo', ['$http', '$q', function ($http, $q) {
    return {
        get: function () {
            var delay = $q.defer();
            $http.get('api/appInfo').then(
                function (object) {
                    delay.resolve(angular.fromJson(object.data));
                },
                function (response) {
                    delay.reject(response.data);
                }
            );
            return delay.promise;
        }
    };
}]);