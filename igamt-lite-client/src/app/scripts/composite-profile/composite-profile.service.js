/**
 * Created by haffo on 9/12/17.
 */
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
        $http.post('api/composite-profile/save', compositeProfileStructure, {
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
      addPcs: function(pcs, cpId) {
        var delay = $q.defer();
        $http.post('api/composite-profile/addPcs/' + cpId, pcs, {
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
      removePc: function(cps, pcId) {
        var delay = $q.defer();
        $http.post('api/composite-profile/removePc/' + pcId, cps, {
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
      delete: function(cpID, igId) {
        var delay = $q.defer();
        $http.get('api/composite-profile/delete/' + cpID + '/' + igId).then(function() {
          delay.resolve(true);
        }, function(error) {
          delay.reject(error);
        });
        return delay.promise;
      },

      build: function(compositeProfileStructure) {
        var delay = $q.defer();
        $http.post('api/composite-profile/build', compositeProfileStructure, {
          headers: { 'Content-Type': 'application/json' }
        }).then(function(response) {
          var saved = angular.fromJson(response.data);
          console.log("angular.copy(saved)");

          console.log(angular.copy(saved));
          delay.resolve(saved);
          return saved;
        }, function(error) {
          delay.reject(error);
        });
        return delay.promise;
      },





    };
    return CompositeProfileService;
  });
