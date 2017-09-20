'use strict';

angular.module('igl').factory('Account', ['$resource',
    function ($resource) {
        return $resource('api/accounts/:id', {id: '@id'});
    }
]);

angular.module('igl').factory('LoginService', ['$resource', '$q',
    function ($resource, $q) {
        return function() {
            var myRes = $resource('api/accounts/login');
            var delay = $q.defer();
            myRes.get({},
                function(res) {
                    delay.resolve(res);
                }
            );
            return delay.promise;
        };
    }
]);

angular.module('igl').factory('AccountLoader', ['Account', '$q',
    function (Account, $q) {
        return function(acctID) {
            var delay = $q.defer();
            Account.get({id: acctID},
                function(account) {
                    delay.resolve(account);
                },
                function() {
                    delay.reject('Unable to fetch account');
                }
            );
            return delay.promise;
        };
    }
]);
