/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('userInfo', ['$resource',
  function ($resource) {
    return $resource('api/accounts/cuser');
  }
]);
