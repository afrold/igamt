/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, Restangular) {
        $scope.page = null;
        $scope.userProfiles = [];
        $scope.preloadedPorfiles = [];
        $scope.tmpPreloadedPorfiles = [];
        $scope.tmpUserProfiles = [];
        $scope.profile = {};


        /**
         * init teh controller
         */
        $scope.init = function(){

        };


    });

