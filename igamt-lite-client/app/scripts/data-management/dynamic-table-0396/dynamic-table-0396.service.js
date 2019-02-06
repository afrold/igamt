/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('DynamicTable0396Service',
    ['$rootScope', '$filter','$http', function ($rootScope, $filter,$http) {
        var DynamicTable0396Service = {
            get: function () {
                return $http.get('api/data-management/dynamic-table-0396');
            },

            fetch: function () {
                return $http.post('api/data-management/dynamic-table-0396/fetch-updates');
            },
            version: function () {
                return $http.get('api/data-management/dynamic-table-0396/version');
            }
        };
        return DynamicTable0396Service;
    }]);
