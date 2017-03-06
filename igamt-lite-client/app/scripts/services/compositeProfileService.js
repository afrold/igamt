'use strict';
angular.module('igl').factory('CompositeProfileService',
    function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, SegmentLibrarySvc, TableLibrarySvc, DatatypeLibrarySvc) {
        var CompositeProfileService = {
            create: function(compositeProfileStructure, igId) {
                var delay = $q.defer();
                $http.post('api/composite-profile/create/' + igId, compositeProfileStructure, {
                    headers: { 'Content-Type': 'application/json' }
                }).then(function(response) {
                    var saved = angular.fromJson(response.data);
                    delay.resolve(saved);
                    return saved;
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            save: function(compositeProfileStructure) {
                var delay = $q.defer();
                $http.post('api/composite-profile/save', message, {
                    headers: { 'Content-Type': 'application/json' }
                }).then(function(response) {
                    var saved = angular.fromJson(response.data);
                    delay.resolve(saved);
                    return saved;
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
            delete: function(cmID) {
                var delay = $q.defer();
                $http.get('api/composite-messages/delete/' + cmID).then(function() {


                    delay.resolve(true);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
            },
           
         



        };
        return CompositeProfileService;
    });