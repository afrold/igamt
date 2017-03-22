angular.module('igl').controller('ListCompositeProfileCtrl', function($scope, $rootScope, $http, CompositeProfileService) {



    $scope.save = function() {
        console.log($rootScope.compositeProfileStructure);
        $scope.saving = true;
        CompositeProfileService.save($rootScope.compositeProfileStructure).then(function(result) {

                console.log("in BUild");
                $rootScope.compositeProfileStructure = result;
                $rootScope.$emit("event:updateIgDate");
                for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
                    if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === result.id) {
                        $rootScope.igdocument.profile.compositeProfiles.children[i] = result;
                    }
                }
                $rootScope.compositeProfilesStructureMap[result.id] = result;
                console.log(result);
                $rootScope.editCM(result);
                cleanState();

            },
            function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
    };
    var updatePcPosition = function(pcList) {
        for (var i = 0; i < pcList.length; i++) {
            pcList[i].position = i + 1;
        }
    };
    $scope.changePosition = function() {
        console.log($rootScope.compositeProfile);
        updatePcPosition($rootScope.compositeProfile.appliedProfileComponents);
        for (var i = 0; i < $rootScope.compositeProfileStructure.profileComponentsInfo.length; i++) {
            for (var j = 0; j < $rootScope.compositeProfile.appliedProfileComponents.length; j++) {
                if ($rootScope.compositeProfileStructure.profileComponentsInfo[i].id === $rootScope.compositeProfile.appliedProfileComponents[j].pc.id) {
                    $rootScope.compositeProfileStructure.profileComponentsInfo[i].position = $rootScope.compositeProfile.appliedProfileComponents[j].position;
                }
            }
        }
        $scope.save();
        console.log($rootScope.compositeProfileStructure);
    };
    $scope.removePc = function(pcId) {
        for (var i = 0; i < $rootScope.compositeProfileStructure.profileComponentsInfo.length; i++) {

            if ($rootScope.compositeProfileStructure.profileComponentsInfo[i].id === pcId) {
                $rootScope.compositeProfileStructure.profileComponentsInfo.splice(i, 1);
            }
        }
        updatePcPosition($rootScope.compositeProfileStructure.profileComponentsInfo);
        console.log("------");
        console.log($rootScope.compositeProfileStructure);
        console.log("------");

        CompositeProfileService.removePc($rootScope.compositeProfileStructure, pcId).then(function(result) {
            CompositeProfileService.build(result).then(function(profile) {
                $rootScope.compositeProfileStructure = result;
                $rootScope.compositeProfilesStructureMap[result.id] = result;
                for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
                    if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === $rootScope.compositeProfileStructure.id) {
                        $rootScope.igdocument.profile.compositeProfiles.children[i] = $rootScope.compositeProfileStructure;
                    }
                }
                for (var i = 0; i < $rootScope.profileComponents.length; i++) {
                    if ($rootScope.profileComponents[i].id === pcId) {
                        for (var j = 0; j < $rootScope.profileComponents[i].compositeProfileStructureList.length; j++) {
                            if ($rootScope.profileComponents[i].compositeProfileStructureList[j] === result.id) {
                                $rootScope.profileComponents[i].compositeProfileStructureList.splice(j, 1);
                                $rootScope.profileComponentsMap[$rootScope.profileComponents[i].id] = $rootScope.profileComponents[i];
                            }
                        }
                    }
                }
                $rootScope.editCM(result);
            });

        }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
        });


    };

    var cleanState = function() {

        $scope.clearDirty();
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        $rootScope.clearChanges();
        if ($scope.compositeMessageParams) {
            $scope.compositeMessageParams.refresh();
        }
    };

});