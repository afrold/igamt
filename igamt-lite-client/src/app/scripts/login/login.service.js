/**
 * Created by haffo on 9/12/17.
 */

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

