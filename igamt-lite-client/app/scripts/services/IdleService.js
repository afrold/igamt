/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('IdleService',
    function ($http) {
        var IdleService = {
            keepAlive: function () {
                 return $http.get(
                    'api/session/keepAlive');
            }
        };
        return IdleService;
    });
