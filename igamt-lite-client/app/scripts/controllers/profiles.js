/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, Restangular, Profile) {
        $scope.page = null;
        $scope.customs = [];
        $scope.predefineds = [];
        $scope.tmpPredefineds = [];
        $scope.tmpCustoms = [];
        $scope.profile = {};
        $scope.error = null;

        $scope.settings = {step : 0};
        // step: 0; list of profile
        // step 1: edit profile



        /**
         * init the controller
         */
        $scope.init = function(){
            $scope.customs = Restangular.all('customProfiles').getList().$object;
            $scope.tmpCustoms = $scope.customs;

            $scope.predefineds = Restangular.all('predefinedProfiles').getList().$object;
            $scope.tmpPredefineds = $scope.predefineds;
        };


        $scope.create = function(profile){
            $scope.settings.step = 0;
            var clone = Restangular.copy(profile);
            clone.id = null;
            clone.metaData.name = "";
            $scope.customs.push(clone);
        };

        $scope.edit = function(profile){
            $scope.settings.step = 0;
            var copy = CloneProfile.getOne(profile.id).then(function(profile){
                $scope.profile = profile;
                $scope.customs.push($scope.profile);
            }, function(error){
                $scope.error = error.data;
            });
        };


        $scope.delete = function(profile){

        };


    });

