/**
 * Created by haffo on 9/12/17.
 */
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
