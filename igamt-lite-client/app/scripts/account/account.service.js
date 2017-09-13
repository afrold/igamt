/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('Account', ['$resource',
  function ($resource) {
    return $resource('api/accounts/:id', {id: '@id'});
  }
]);
