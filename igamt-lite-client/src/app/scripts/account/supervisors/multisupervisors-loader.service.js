/**
 * Created by haffo on 9/12/17.
 */


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
