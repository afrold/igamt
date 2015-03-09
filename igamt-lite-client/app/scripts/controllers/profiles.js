/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, $rootScope, Restangular, $http) {
        $scope.custom = [];
        $scope.preloaded = [];
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
            $http.get('/api/profiles/preloaded').then(function (response) {
                $scope.preloaded = response.data;
            });

            $http.get('/api/profiles?userId=' + $scope.user.id).then(function (response) {
                $scope.custom = response.data;
            });

        };

        $scope.clone = function (profile) {
            Restangular.all('profiles').post({targetId: profile.id}).then(function (res) {
                $scope.custom.push(res);
            }, function (error) {
                $scope.error = error;
            });
        };


        $scope.edit = function (profile) {
            Restangular.one('profiles', profile.id).get().then(function (profile) {
                $rootScope.initMaps();
                $rootScope.context.page = $rootScope.pages[1];
                $rootScope.profile = profile;
                $rootScope.backUp = Restangular.copy($rootScope.profile);

                angular.forEach($rootScope.profile.datatypes.children, function (child) {
                    this[child.label] = child;
                }, $rootScope.datatypesMap);


                angular.forEach($rootScope.profile.segments.children, function (child) {
                    this[child.id] = child;
                }, $rootScope.segmentsMap);

                angular.forEach($rootScope.profile.tableLibrary.tables.children, function (child) {
                    this[child.id] = child;
                }, $rootScope.tablesMap);


                angular.forEach($rootScope.profile.messages.children, function (child) {
                    this[child.id] = child;
                    angular.forEach(child.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                }, $rootScope.messagesMap);

                if($rootScope.profile.messages.children.length === 1){
                     $rootScope.message = $rootScope.profile.messages.children[0];
                     angular.forEach($rootScope.message.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                     });
                }

            }, function (error) {
                $scope.error = error;
            });
        };

        $scope.delete = function (profile) {
            profile.remove().then(function () {
                var index = $scope.custom.indexOf(profile);
                if (index > -1) $scope.custom.splice(index, 1);
            }, function (error) {
                $scope.error = error;
            });
        };

    })
;


angular.module('igl')
    .controller('EditProfileCtrl', function ($scope, $rootScope, Restangular) {

        $scope.changes = [];
        $scope.error = null;

        /**
         * init the controller
         */
        $scope.init = function () {
        };

        $scope.cancel = function () {
            $rootScope.context.page = $rootScope.pages[0];
            $scope.changes = [];
            $rootScope.profile = null;
        };

        $scope.delete = function () {
            $rootScope.profile.remove().then(function () {
                $rootScope.context.page = $rootScope.pages[0];
                var index = $scope.custom.indexOf($rootScope.profile);
                if (index > -1) $scope.custom.splice(index, 1);
                $rootScope.backUp = null;
            }, function (error) {
                $scope.error = error;
            });
        };

        $scope.applyChanges = function () {

        };
    });

