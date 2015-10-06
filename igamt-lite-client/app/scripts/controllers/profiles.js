/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, $rootScope, Restangular, $http, $filter, userInfoService, $modal, $cookies, $timeout) {
        $scope.loading = false;
        $rootScope.igs = [];
        $scope.igContext = {
            type: 'USER'
        };

        $scope.tmpIgs = [].concat($rootScope.igs);
        $scope.error = null;
        $scope.loading = false;

        $scope.igTypes = [
            {
                name: "Predefined Implementation Guides", type: 'PRELOADED'
            },
            {
                name: "User Implementation Guides", type: 'USER'
            }
        ];
        $scope.loadingProfile = false;
        $scope.toEditProfileId = null;
        $scope.verificationResult = null;
        $scope.verificationError = null;

        /**
         * init the controller
         */
        $scope.init = function () {
            $scope.igContext.igType = $scope.igTypes[1];
            $scope.loadProfiles();
            /**
             * On 'event:loginConfirmed', resend all the 401 requests.
             */
            $scope.$on('event:loginConfirmed', function (event) {
                $scope.igContext.igType = $scope.igTypes[1];
                $scope.loadProfiles();
            });


            $rootScope.$on('event:openProfileRequest', function (event, profile) {
                $scope.openProfile(profile);
            });
           
        };

        $rootScope.$on('event:IgsPushed', function(event, profile) {
        	if($scope.igContext.igType.type === 'USER'){
                $rootScope.igs.push(profile);
             } else {
                $scope.igContext.igType = $scope.igTypes[1];
                $scope.loadProfiles();
                profile = $scope.findOne(profile.id);
            }
          	$scope.edit(profile);
          });

        $scope.loadProfiles = function () {
            $scope.error = null;
            if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
                $rootScope.selectIgTab(0);
                $scope.loading = true;
                if ($scope.igContext.igType.type === 'PRELOADED') {
                    $http.get('api/profiles', {timeout: 60000}).then(function (response) {
                        $rootScope.igs = angular.fromJson(response.data);
                        $scope.tmpIgs = [].concat($rootScope.igs);
                        $scope.loading = false;
                    }, function (error) {
                        $scope.loading = false;
                        $scope.error = "Failed to load the profiles";
                    });
                } else if ($scope.igContext.igType.type === 'USER') {
                    $http.get('api/profiles/cuser', {timeout: 60000}).then(function (response) {
                        $rootScope.igs = angular.fromJson(response.data);
                        $scope.tmpIgs = [].concat($rootScope.igs);
                        $scope.loading = false;
                    }, function (error) {
                        $scope.loading = false;
                        $scope.error = "Failed to load the profiles";
                    });
                }

            }
        };

        $scope.clone = function (profile) {
            $scope.toEditProfileId = profile.id;
            waitingDialog.show('Cloning profile...', {dialogSize: 'sm', progressType: 'info'});
            $http.post('api/profiles/' + profile.id + '/clone', {timeout: 60000}).then(function (response) {
                $scope.toEditProfileId = null;
                if($scope.igContext.igType.type === 'USER'){
                    $rootScope.igs.push(angular.fromJson(response.data));
                 }else {
                    $scope.igContext.igType = $scope.igTypes[1];
                    $scope.loadProfiles();
                }
                waitingDialog.hide();
            }, function (error) {
                $scope.toEditProfileId = null;
                waitingDialog.hide();
            });
        };

        $scope.findOne = function (id) {
            for (var i = 0; i < $rootScope.igs.length; i++) {
                if ($rootScope.igs[i].id === id) {
                    return  $rootScope.igs[i];
                }
            }
            return null;
        };

        $scope.edit = function (profile) {
            $scope.toEditProfileId = profile.id;
            try {
                if ($rootScope.profile != null && $rootScope.profile === profile) {
                    $rootScope.selectIgTab(1);
                    $scope.toEditProfileId = null;
                } else if ($rootScope.profile && $rootScope.profile != null && $rootScope.hasChanges()) {
                    $scope.confirmOpen(profile);
                    $scope.toEditProfileId = null;
                } else {
                    $timeout(
                        function () {
                            $scope.openProfile(profile);
                        }, 1000);
                }
            } catch (e) {
                $rootScope.msg().text = "igInitFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                $scope.loadingProfile = false;
                $scope.toEditProfileId = null;
            }
        };

        $scope.openProfile = function (profile) {
            $scope.loadingProfile = true;
            $rootScope.selectIgTab(1);
            if (profile != null) {
                $rootScope.initMaps();
                $rootScope.profile = profile;
                $rootScope.messages = $rootScope.profile.messages.children;
                angular.forEach($rootScope.profile.datatypes.children, function (child) {
                    this[child.id] = child;
                }, $rootScope.datatypesMap);
                angular.forEach($rootScope.profile.segments.children, function (child) {
                    this[child.id] = child;
                }, $rootScope.segmentsMap);

                angular.forEach($rootScope.profile.tables.children, function (child) {
                    this[child.id] = child;
                }, $rootScope.tablesMap);


                $rootScope.segments = [];
                $rootScope.tables = $rootScope.profile.tables.children;
                $rootScope.datatypes = $rootScope.profile.datatypes.children;

                angular.forEach($rootScope.profile.messages.children, function (child) {
                    this[child.id] = child;
                    angular.forEach(child.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                }, $rootScope.messagesMap);


                if ($rootScope.messages.length === 1) {
                    var message = $rootScope.messages[0];
                    message.children = $filter('orderBy')(message.children, 'position');
                    angular.forEach(message.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                    $rootScope.$emit('event:openMessage', message.id);


                }

//                angular.forEach($rootScope.profile.messages.children, function (message) {
//                    var segRefOrGroups = [];
//                    var segments = [];
//                    var datatypes = [];
//                    $scope.collectData(message, segRefOrGroups, segments, datatypes);
//                    $rootScope.messagesData.push({message: message, segRefOrGroups: segRefOrGroups, segments: segments, datatypes: datatypes});
//                });
                $scope.gotoSection($rootScope.profile.metaData, 'metaData');
                $scope.loadingProfile = false;
                $scope.toEditProfileId = null;

            }
        };


        $scope.collectData = function (node, segRefOrGroups, segments, datatypes) {
            if (node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'group') {
                    segRefOrGroups.push(node);
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.collectData(segmentRefOrGroup, segRefOrGroups, segments, datatypes);
                        });
                    }
                    segRefOrGroups.push({ name: node.name, "type": "end-group"});
                } else if (node.type === 'segment') {
                    if (segments.indexOf(node) === -1) {
                        segments.push(node);
                    }
                    angular.forEach(node.fields, function (field) {
                        $scope.collectData(field, segRefOrGroups, segments, datatypes);
                    });
                } else if (node.type === 'segmentRef') {
                    segRefOrGroups.push(node);
                    $scope.collectData($rootScope.segmentsMap[node.ref], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'component' || node.type === 'subcomponent' || node.type === 'field') {
                    $scope.collectData($rootScope.datatypesMap[node.datatype], segRefOrGroups, segments, datatypes);
                } else if (node.type === 'datatype') {
                    if (datatypes.indexOf(node) === -1) {
                        datatypes.push(node);
                    }
                    if (node.components) {
                        angular.forEach(node.children, function (component) {
                            $scope.collectData(component, segRefOrGroups, segments, datatypes);
                        });
                    }
                }
            }
        };

        $scope.confirmDelete = function (profile) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmProfileDeleteCtrl.html',
                controller: 'ConfirmProfileDeleteCtrl',
                resolve: {
                    profileToDelete: function () {
                        return profile;
                    }
                }
            });
            modalInstance.result.then(function (profile) {
                $scope.profileToDelete = profile;
            }, function () {
            });
        };


        $scope.confirmClose = function () {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmProfileCloseCtrl.html',
                controller: 'ConfirmProfileCloseCtrl'
            });
            modalInstance.result.then(function () {
            }, function () {
            });
        };


        $scope.confirmOpen = function (profile) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmProfileOpenCtrl.html',
                controller: 'ConfirmProfileOpenCtrl',
                resolve: {
                    profileToOpen: function () {
                        return profile;
                    }
                }
            });
            modalInstance.result.then(function (profile) {
                $scope.openProfile(profile);
            }, function () {
            });
        };


        $scope.exportAs = function (id, format) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/profiles/' + id + '/export/' + format + '/true');
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
//            console.log("CSRF=" + csrfInput.value);
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
//            httpHeaders.common['X-XSRF-TOKEN'] = $cookies['XSRF-TOKEN'];
//            httpHeaders.common['JSSESSIONID'] = $cookies['JSSESSIONID'];
            form.submit();
        };

        $scope.exportDelta = function (id, format) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/profiles/' + id + '/delta/' + format);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
//            console.log("CSRF=" + csrfInput.value);
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
//            httpHeaders.common['X-XSRF-TOKEN'] = $cookies['XSRF-TOKEN'];
//            httpHeaders.common['JSSESSIONID'] = $cookies['JSSESSIONID'];
            form.submit();
        };

//
//        $scope.verify = function (id) {
//            waitingDialog.show('Verifying changes...', {dialogSize: 'sm', progressType: 'info'});
//            $scope.verificationError = null;
//            $scope.loading = true;
//            $http.get('api/profiles/' + id + '/verify', {timeout: 60000}).then(function (response) {
//                $rootScope.verificationResult = angular.fromJson(response.data);
//                $scope.loading = false;
//                waitingDialog.hide();
//            }, function (error) {
//                $scope.loading = false;
//                $scope.verificationError = "Verification Failed";
//                waitingDialog.hide();
//            });
//        };

        $scope.close = function () {
            if ($rootScope.hasChanges()) {
                $scope.confirmClose();
            } else {
                waitingDialog.show('Closing profile...', {dialogSize: 'sm', progressType: 'info'});
                $rootScope.profile = null;
                $rootScope.selectIgTab(0);
                $rootScope.initMaps();
                waitingDialog.hide();
            }
        };


        $scope.gotoSection = function (obj, type) {
            $rootScope.section['data'] = obj;
            $rootScope.section['type'] = type;
        };


        $scope.save = function () {
            waitingDialog.show('Saving changes...', {dialogSize: 'sm', progressType: 'success'});
            var changes = angular.toJson($rootScope.changes);
            var data = {"changes": changes, "profile": $rootScope.profile};
            $http.post('api/profiles/' + $rootScope.profile.id + '/save', data, {timeout: 60000}).then(function (response) {
                var saveResponse = angular.fromJson(response.data);
                $rootScope.profile.metaData.date = saveResponse.date;
                $rootScope.profile.metaData.version = saveResponse.version;
                var found = $scope.findOne($rootScope.profile.id);
                if(found != null){
                    var index = $rootScope.igs.indexOf(found);
                    if(index > 0){
                        $rootScope.igs [index] = $rootScope.profile;
                    }
                }
                $rootScope.msg().text = "igSaveSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $rootScope.clearChanges();
                waitingDialog.hide();
            }, function (error) {
                $scope.error = error;
                $rootScope.msg().text = "igSaveFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                waitingDialog.hide();
            });
        };

        $scope.exportChanges = function () {
            var form = document.createElement("form");
            form.action = 'api/profiles/export/changes';
            form.method = "POST";
            form.target = "_target";
            var input = document.createElement("textarea");
            input.name = "content";
            input.value = angular.fromJson($rootScope.changes);
            form.appendChild(input);
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        };

        $scope.viewChanges = function (changes) {
            var modalInstance = $modal.open({
                templateUrl: 'ViewIGChangesCtrl.html',
                controller: 'ViewIGChangesCtrl',
                resolve: {
                    changes: function () {
                        return changes;
                    }
                }
            });
            modalInstance.result.then(function (changes) {
                $scope.changes = changes;
            }, function () {
            });
        };


        $scope.reset = function () {
//            $rootScope.context.page = $rootScope.pages[0];
            $rootScope.selectIgTab(0);
            $rootScope.changes = {};
            $rootScope.profile = null;
        };


        $scope.initProfile = function () {
            $scope.loading = true;
            if ($rootScope.profile != null && $rootScope.profile != undefined)
                $scope.gotoSection($rootScope.profile.metaData, 'metaData');
            $scope.loading = false;

        };


    });


angular.module('igl').controller('ViewIGChangesCtrl', function ($scope, $modalInstance, changes, $rootScope, $http) {
    $scope.changes = changes;
    $scope.loading = false;
    $scope.exportChanges = function () {
        $scope.loading = true;
        waitingDialog.show('Exporting changes...', {dialogSize: 'sm', progressType: 'success'});
        var form = document.createElement("form");
        form.action = 'api/profiles/export/changes';
        form.method = "POST";
        form.target = "_target";
        form.style.display = 'none';
        form.params = document.body.appendChild(form);
        form.submit();
        waitingDialog.hide();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmProfileDeleteCtrl', function ($scope, $modalInstance, profileToDelete, $rootScope, $http) {
    $scope.profileToDelete = profileToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        $http.post($rootScope.api('api/profiles/' + $scope.profileToDelete.id + '/delete'), {timeout: 60000}).then(function (response) {
            var index = $rootScope.igs.indexOf($scope.profileToDelete);
            if (index > -1) $rootScope.igs.splice(index, 1);
            $rootScope.backUp = null;
            if ($scope.profileToDelete === $rootScope.profile) {
                $rootScope.initMaps();
                $rootScope.profile = null;
                $rootScope.selectIgTab(0);
            }
            $rootScope.msg().text = "igDeleteSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            $rootScope.manualHandle = true;
            $scope.profileToDelete = null;
            $scope.loading = false;

            $modalInstance.close($scope.profileToDelete);

        }, function (error) {
            $scope.error = error;
            $scope.loading = false;
            $modalInstance.close($scope.profileToDelete);
            $rootScope.msg().text = "igDeleteFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

//            waitingDialog.hide();
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmProfileCloseCtrl', function ($scope, $modalInstance, $rootScope, $http) {
    $scope.loading = false;
    $scope.discardChangesAndClose = function () {
        $scope.loading = true;
        $http.get('api/profiles/' + $rootScope.profile.id, {timeout: 60000}).then(function (response) {
            var index = $rootScope.igs.indexOf($rootScope.profile);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.clear = function () {
        $rootScope.changes = {};
        $rootScope.profile = null;
        $rootScope.selectIgTab(0);
        $rootScope.initMaps();
        $modalInstance.close();
    };


    $scope.saveChangesAndClose = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "profile": $rootScope.profile};
        $http.post('api/profiles/' + $rootScope.profile.id + '/save', data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.profile.metaData.date = saveResponse.date;
            $rootScope.profile.metaData.version = saveResponse.version;
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmProfileOpenCtrl', function ($scope, $modalInstance, profileToOpen, $rootScope, $http) {
    $scope.profileToOpen = profileToOpen;
    $scope.loading = false;
    $scope.discardChangesAndOpen = function () {
        $scope.loading = true;
        $http.get('api/profiles/' + $rootScope.profile.id, {timeout: 60000}).then(function (response) {
            var index = $rootScope.igs.indexOf($rootScope.profile);
            $rootScope.igs[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $modalInstance.close($scope.profileToOpen);
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "igResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $modalInstance.dismiss('cancel');
        });
    };


    $scope.saveChangesAndOpen = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "profile": $rootScope.profile};
        $http.post('api/profiles/' + $rootScope.profile.id + '/save', data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $rootScope.profile.metaData.date = saveResponse.date;
            $rootScope.profile.metaData.version = saveResponse.version;
            $scope.loading = false;
            $modalInstance.close($scope.profileToOpen);
        }, function (error) {
            $rootScope.msg().text = "igSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };


});




