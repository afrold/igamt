'use strict';

/**
 * @ngdoc function
 * @name clientApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the clientApp
 */
angular.module('igl')
  .controller('AboutCtrl', function ($scope, $rootScope) {

   $scope.releaseNotes = [
        {
            "version":$rootScope.appInfo.version,
            "date":$rootScope.appInfo.date,
            updates:[
                'Update License for Editor',' Update Select IG Document Types Buttons','Update Create IG Document dialog',' Update Export as Dialog'
            ]
        },
        {
            "version":'1.0.0-beta-2',
            "date":'03/07/2016',
            updates:[
                'Display the left side table of contents for HTML export','Add Image (pfg,gif,jpeg,jpg) upload feature',' Add File (word, html,pdf) Upload feature','Export IG Document as Word Document',
                'Display the list message events by version for the creation of a new IG Document','Handle the message level predicates and conformance statements','Added Issue and About Tabs'
            ]
        }
    ];



  });
