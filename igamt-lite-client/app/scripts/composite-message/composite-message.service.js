/**
 * Created by haffo on 9/12/17.
 */
'use strict';
angular.module('igl').factory('CompositeMessageService',
  function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, SegmentLibrarySvc, TableLibrarySvc, DatatypeLibrarySvc) {
    var CompositeMessageService = {
      create: function(message, igId) {
        var delay = $q.defer();
        $http.post('api/composite-messages/create/' + igId, message, {
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
      save: function(message) {
        var delay = $q.defer();
        $http.post('api/composite-messages/save', message, {
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
      getCm: function(cmID) {
        var delay = $q.defer();
        $http.get('api/composite-messages/' + cmID).then(function(response) {
          console.log("-----------------------");
          console.log(response);
          var cm = angular.fromJson(response.data);
          console.log(cm);
          delay.resolve(cm);
        }, function(error) {
          delay.reject(error);
        });
        return delay.promise;
      },
      getSegOrGrp: function(children) {
        var delay = $q.defer();


        console.log(children);

        $http.post('api/composite-messages/getsegorgrp', children).then(function(response) {
          console.log("-----------------------");
          console.log(response);
          var cm = angular.fromJson(response.data);
          console.log(cm);
          delay.resolve(cm);
        }, function(error) {
          delay.reject(error);
        });
        return delay.promise;
      },
      SaveGroupOrSegment: function(children) {
        console.log(children);
        var delay = $q.defer();
        $http.post('api/composite-messages/savegrporseg', children).then(function(response) {
          console.log("-----------------------");
          console.log(response);
          var cm = angular.fromJson(response.data);
          console.log(cm);
          delay.resolve(cm);
        }, function(error) {
          delay.reject(error);
        });
        return delay.promise;

      }




    };
    return CompositeMessageService;
  });
