/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('Supervisors', ['$resource',
  function ($resource) {
    return $resource('api/shortaccounts', {filter:'accountType::supervisor'});
  }
]);
