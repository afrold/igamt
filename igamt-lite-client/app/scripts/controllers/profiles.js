/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, $rootScope, Restangular,$http) {
        $scope.customSummaries = [];
        $scope.preloadedSummarries = [];
        $scope.tmpPreloadeds = [];
        $scope.tmpCustoms = [];
        $scope.error = null;
        $scope.user = {id: 2};
         // step: 0; list of profile
        // step 1: edit profile


        /**
         * init the controller
         */
        $scope.init = function () {
            $rootScope.context.page = $rootScope.pages[0];
            $http.get('/api/profiles/preloaded').then(function(preloadedSummaries){
                for(var i=0; i < preloadedSummaries.length; i++){
                    var s =   preloadedSummaries[i];
                    var summary = {};
                    summary["id"] = s[0];
                    summary["metaData"] = s[1];
                    $scope.preloadedSummarries.push(summary);
                }
            });

            $http.get('/api/profiles?userId='+$scope.user.id).then(function(customs){
                 for(var i=0; i < customs.length; i++){
                     var s =   customs[i];
                     var summary = {};
                     summary["id"] = s[0];
                     summary["metaData"] = s[1];
                     $scope.customs.push(summary);
                 }
             });

         };

        $scope.clone = function (profile) {
            Restangular.all('profiles').post({targetId: profile.id, preloaded: profile.preloaded}).then(function(res) {
                $scope.customs.push(res);
            },function(error){
                $scope.error = error;
            });
        };

        $scope.edit = function (profile) {
            Restangular.one('profiles',profile.id).get().then(function(profile) {
                $rootScope.context.page = $rootScope.pages[1];
                $rootScope.profile = profile;
                //$rootScope.backUp = Restangular.copy($rootScope.profile);
            },function(error){
                $scope.error = error;
            });
        };


        $scope.delete = function (profile) {
            profile.remove().then(function () {
                var index = $scope.customs.indexOf(profile);
                if (index > -1) $scope.customs.splice(index, 1);
            }, function(error){
                $scope.error = error;
            });
        };

    });


angular.module('igl')
    .controller('EditProfileCtrl', function ($scope, $rootScope, Restangular) {

        $scope.changes = [];
        $scope.error = null;

        /**
         * init the controller
         */
        $scope.init = function () {
        };

        $scope.cancel = function(){
            $rootScope.context.page = $rootScope.pages[0];
            $scope.changes = [];
            $rootScope.profile =null;
        };

        $scope.delete = function () {
            $rootScope.profile.remove().then(function () {
                $rootScope.context.page = $rootScope.pages[0];
                var index = $scope.customs.indexOf( $rootScope.profile);
                if (index > -1) $scope.customs.splice(index, 1);
                $rootScope.backUp = null;
            }, function(error){
                $scope.error = error;
            });
        };

        $scope.applyChanges = function () {

        };
    });

