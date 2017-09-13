/**
 * Created by haffo on 9/12/17.
 */


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
