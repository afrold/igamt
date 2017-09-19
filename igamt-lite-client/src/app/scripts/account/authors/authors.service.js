/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('Authors', ['$resource',
  function ($resource) {
    return $resource('api/shortaccounts', {filter:'accountType::author'});
  }
]);
