'use strict';

angular.module('igl').factory('Authors', ['$resource',
    function ($resource) {
        return $resource('api/shortaccounts', {filter:'accountType::author'});
    }
]);

angular.module('igl').factory('Supervisors', ['$resource',
    function ($resource) {
        return $resource('api/shortaccounts', {filter:'accountType::supervisor'});
    }
]);


angular.module('igl').factory('MultiAuthorsLoader', ['Authors', '$q',
    function (Authors, $q) {
        return function() {
            var delay = $q.defer();
            Authors.query(
                function(auth) {
                    delay.resolve(auth);
                },
                function() {
                    delay.reject('Unable to fetch list of authors');
                }
            );
            return delay.promise;
        };
    }
]);

angular.module('igl').factory('MultiSupervisorsLoader', ['Supervisors', '$q',
    function (Supervisors, $q) {
        return function() {
            var delay = $q.defer();
            Supervisors.query(
                function(res) {
                    delay.resolve(res);
                },
                function() {
                    delay.reject('Unable to fetch list of supervisors');
                }
            );
            return delay.promise;
        };
    }
]);
