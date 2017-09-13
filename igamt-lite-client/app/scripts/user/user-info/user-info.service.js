/**
 * Created by haffo on 9/12/17.
 */



angular.module('igl').factory('userLoaderService', ['userInfo', '$q',
  function (userInfo, $q) {
    var load = function() {
      var delay = $q.defer();
      userInfo.get({},
        function(theUserInfo) {
          delay.resolve(theUserInfo);
        },
        function() {
          delay.reject('Unable to fetch user info');
        }
      );
      return delay.promise;
    };
    return {
      load: load
    };
  }
]);
