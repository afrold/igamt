angular.module('igl').controller(
    'HL7VersionsDlgCtrl',
    function ($scope, $rootScope, $modal, $log, $http, $httpBackend, ProfileAccessSvc) {

        $rootScope.clickSource = {};

        $scope.hl7Versions = function (clickSource) {
            $rootScope.clickSource = clickSource;
            var hl7VersionsInstance = $modal.open({
                templateUrl: 'hl7VersionsDlg.html',
                controller: 'HL7VersionsInstanceDlgCtrl',
                resolve: {
                    hl7Versions: function () {
                        return $scope.listHL7Versions();
                    }
                }
            });

            hl7VersionsInstance.result.then(function (result) {
                var hl7Version = $rootScope.hl7Version;
                switch ($rootScope.clickSource) {
                    case "btn":
                    {
                        $scope.createProfile(hl7Version, result);
//						$rootScope.selectIgTab(1);
                        $rootScope.hl7Version = null;
                        break;
                    }
                    case "ctx":
                    {
                        $scope.updateProfile(result);
                        break;
                    }
                }
            });
        };

        $scope.listHL7Versions = function () {
            var hl7Versions = [];
            $http.get('api/profiles/hl7/findVersions', {
                timeout: 60000
            }).then(
                function (response) {
                    var len = response.data.length;
                    for (var i = 0; i < len; i++) {
                        hl7Versions.push(response.data[i]);
                    }
                });
            return hl7Versions;
        };

        /**
         * TODO: Handle error from server
         * @param msgIds
         */
        $scope.createProfile = function (hl7Version, msgIds) {
            var iprw = {
                "hl7Version": hl7Version,
                "msgIds": msgIds,
                "timeout": 60000
            };
            $http.post('api/profiles/hl7/createIntegrationProfile', iprw).then(function
                (response) {
                var profile = angular.fromJson(response.data);
                $rootScope.$broadcast('event:openProfileRequest', profile);
                $rootScope.$broadcast('event:IgsPushed', profile);
            });
            return $scope.profile;
        };

        /**
         * TODO: Handle error from server
         * @param msgIds
         */
        $scope.updateProfile = function (msgIds) {
            var iprw = {
                "profile": $rootScope.profile,
                "msgIds": msgIds,
                "timeout": 60000
            };
            $http.post('api/profiles/hl7/updateIntegrationProfile', iprw).then(function
                (response) {
                var profile = angular.fromJson(response.data);
                $rootScope.$broadcast('event:openProfileRequest', profile);
            });
        };

        $scope.getLeveledProfile = function (profile) {
            $rootScope.leveledProfile = [
                {title: "Metadata", children: []},
                {title: "Datatypes", children: profile.datatypes.children},
                {title: "Segments", children: profile.segments.children},
                {title: "Messages", children: profile.messages.children},
                {title: "ValueSets", children: profile.tables.children}
            ];
        };

        $scope.closedCtxMenu = function (node, $index) {
            console.log("closedCtxMenu");
        };

    });

angular.module('igl').controller('HL7VersionsInstanceDlgCtrl',
    function ($scope, $rootScope, $modalInstance, $http, hl7Versions, ProfileAccessSvc) {

        $scope.selected = {
            item: hl7Versions[0]
        };

        $scope.profileVersions = [];
        var profileVersions = [];

        $scope.loadProfilesByVersion = function () {
// FIXME gcr: Not right; hence the comment. We are getting the message list here not passing it in.  
//        	$http.post('api/profiles/hl7/messageListByVersion/' + $scope.hl7Version, angular.fromJson({"messageIds":$scope.profileVersions})).then(function (response) {
            $http.get('api/profiles/hl7/messageListByVersion/' + $scope.hl7Version).then(function (response) {
                $scope.messagesByVersion = angular.fromJson(response.data);
            });
        };

        $scope.trackSelections = function (bool, id) {
            if (bool) {
                profileVersions.push(id);
            } else {
                for (var i = 0; i < profileVersions.length; i++) {
                    if (profileVersions[i].id == id) {
                        profileVersions.splice(i, 1);
                    }
                }
            }
        };

        $scope.$watch(function () {
            return $rootScope.profile
        }, function (newValue, oldValue) {
            if ($rootScope.clickSource === "ctx") {
                $scope.hl7Version = newValue.metaData.hl7Version;
                $scope.loadProfilesByVersion();
            }
        });

        $scope.getHL7Version = function () {
            return ProfileAccessSvc.getVersion($rootScope.profile);
        };

        $scope.hl7Versions = hl7Versions;
        $scope.ok = function () {
            $scope.profileVersions = profileVersions;
            $modalInstance.close(profileVersions);
        };

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });
