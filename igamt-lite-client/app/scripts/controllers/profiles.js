/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, $rootScope, Restangular, $http, $filter) {
        $scope.loading = false;
        $scope.tmpPreloadeds = [];
        $scope.tmpCustoms = [];
        $scope.error = null;
        $scope.user = {id: 2};
        $scope.preloadedLoading = false;
        $scope.customLoading = false;
        $scope.preLoadedError = null;
        $scope.customError = null;
        // step: 0; list of profile
        // step 1: edit profile


        /**
         * init the controller
         */
        $scope.init = function () {
            $rootScope.context.page = $rootScope.pages[0];
            $scope.preloadedError = null;
            $scope.customError = null;

            if($scope.customIgs.length === 0) {
                $scope.customLoading = true;
                $http.get($rootScope.api('/api/profiles/preloaded'), {timeout: 60000}).then(function (response) {
                    $rootScope.preloadedIgs = angular.fromJson(response.data);
                    $scope.preloadedLoading = false;
                }, function (error) {
                    $scope.preLoadedError = error;
                    $scope.preloadedLoading = false;
                });
            }
            if($scope.preloadedIgs.length === 0) {
                $scope.preloadedLoading = true;
                $http.get($rootScope.api('/api/profiles/custom'), {timeout: 60000}).then(function (response) {
                    $rootScope.customIgs = angular.fromJson(response.data);
                    $scope.customLoading = false;
                }, function (error) {
                    $scope.customError = error;
                    $scope.customLoading = false;
                });
            }
        };

        $scope.clone = function (id) {
            waitingDialog.show('Cloning profile...', {dialogSize: 'sm', progressType: 'info'});
            $http.post($rootScope.api('/api/profiles/' + id + '/clone')).then(function (response) {
                $rootScope.customIgs.push(angular.fromJson(response.data));
                waitingDialog.hide();
            }, function (error) {
                $scope.error = error;
                waitingDialog.hide();
            });
        };

        $scope.findOne = function (id) {
            for (var i = 0; i < $rootScope.customIgs.length; i++) {
                if ($rootScope.customIgs[i].id === id) {
                    return  $rootScope.customIgs[i];
                }
            }
            return null;
        };


        $scope.goToSection = function(id){
            $scope.section = id;
        };

        $scope.edit = function (id) {
            waitingDialog.show('Loading profile...', {dialogSize: 'sm', progressType: 'info'});
//             $http.get($rootScope.api('/api/profiles/'+ row.id)).then(function (profile) {
            $scope.loading = true;
            $rootScope.generalInfo = {type: null, 'message': null};
            var profile = $scope.findOne(id);
            if (profile != null) {
                $rootScope.initMaps();

                $rootScope.context.page = $rootScope.pages[1];
                $rootScope.profile = profile;
                $rootScope.messages = $rootScope.profile.messages.children;
//                $rootScope.backUp = Restangular.copy($rootScope.profile);

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

                if ($rootScope.profile.messages.children.length === 1) {
                    $rootScope.segments = [];
                    $rootScope.tables = [];
                    $rootScope.datatypes = [];

                    $rootScope.message =$rootScope.messages[0];
                    $rootScope.message.children = $filter('orderBy')($rootScope.message.children, 'position');
                    angular.forEach($rootScope.message.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                    $rootScope.notifyMsgTreeUpdate = new Date().getTime();
                }

                angular.forEach($rootScope.profile.messages.children, function (message) {
                    var segRefOrGroups = [];
                    var segments = [];
                    var datatypes = [];
                    $scope.collectData(message, segRefOrGroups, segments, datatypes);
//                    $scope.loadSegments(message, segments);
//                    angular.forEach(segments, function (segment) {
//                        $scope.loadDatatypes(segment,datatypes);
//                    });
                    $rootScope.messagesData.push({message: message, segRefOrGroups: segRefOrGroups, segments: segments, datatypes: datatypes});


                });

                $scope.loading = false;

            }

            waitingDialog.hide();
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
                    $scope.collectData(node.ref, segRefOrGroups, segments, datatypes);
                }else if (node.type === 'component' || node.type === 'subcomponent' || node.type === 'field') {
                    $scope.collectData(node.datatype, segRefOrGroups, segments, datatypes);
                } else if (node.type === 'datatype') {
                    if (datatypes.indexOf(node) === -1) {
                        datatypes.push(node);
                    }
                    if (node.children) {
                        angular.forEach(node.children, function (component) {
                            $scope.collectData(component, segRefOrGroups, segments, datatypes);
                        });
                    }
                }
            }
        };


//        $scope.loadSegments = function (node, segments) {
//            if(node) {
//                if (node.type === 'message') {
//                    angular.forEach(node.children, function (segmentRefOrGroup) {
//                        $scope.loadSegments(segmentRefOrGroup,segments);
//                    });
//                } else if (node.type === 'group') {
//                    if (node.children) {
//                        angular.forEach(node.children, function (segmentRefOrGroup) {
//                            $scope.loadSegments(segmentRefOrGroup,segments);
//                        });
//                    }
//                } else if (node.type === 'segment') {
//                    segments.push(node.ref);
//                }
//            }
//        };
//
//        $scope.loadDatatypes = function (node, datatypes) {
//            if(node) {
//                if (node.type === 'segment') {
//                    angular.forEach(node.fields, function (field) {
//                        $scope.loadDatatypes(field,datatypes);
//                    });
//                }else if(node.type === 'datatype'){
//                    if(datatypes.indexOf(node) === -1) {
//                        datatypes.push(node);
//                    }
//                    if(node.children) {
//                        angular.forEach(node.children, function (component) {
//                            $scope.loadDatatypes(component, datatypes);
//                        });
//                    }
//                }else if(node.type === 'component' || node.type === 'subcomponent' || node.type === 'field'){
//                    $scope.loadDatatypes(node.datatype,datatypes);
//                }
//            }
//        };


        $scope.delete = function (id) {
            waitingDialog.show('Deleting profile...', {dialogSize: 'sm', progressType: 'danger'});
            var profile = $scope.findOne(id);
            if (profile != null) {
                $http.post($rootScope.api('/api/profiles/' + id + '/delete'), {timeout: 60000}).then(function (response) {
                    var index = $rootScope.customIgs.indexOf(profile);
                    if (index > -1) $rootScope.customIgs.splice(index, 1);
                    waitingDialog.hide();

                }, function (error) {
                    $scope.error = error;
                    waitingDialog.hide();

                });
            }
        };

        $scope.exportAs = function (id,format) {
            waitingDialog.show('Exporting profile...', {dialogSize: 'sm', progressType: 'success'});
            var form = document.createElement("form");
            form.action = $rootScope.api('/api/profiles/'+ id+ '/export/'+ format);
            form.method = "POST";
            form.target = "_target";
            form.style.display = 'none';
            form.params =
            document.body.appendChild(form);
            form.submit();
            $rootScope.changes = {};
            waitingDialog.hide();
//             $http.post($rootScope.api('/api/profiles/'+ id+ '/export'), {params:{'exportType':format},timeout: 60000}).then(function (response) {
//                waitingDialog.hide();
//                $rootScope.changes = {};
//            }, function (error) {
//                $scope.error = error;
//                waitingDialog.hide();
//            });
        };



    });

angular.module('igl')
    .controller('EditProfileCtrl', function ($scope, $rootScope, Restangular,$http) {

        $scope.error = null;

        /**
         * init the controller
         */
        $scope.init = function () {
        };

        $scope.reset = function () {
            $rootScope.context.page = $rootScope.pages[0];
            $rootScope.changes = {};
            $rootScope.profile = null;
        };

        $scope.delete = function () {
            waitingDialog.show('Deleting profile...', {dialogSize: 'sm', progressType: 'danger'});
            $http.post($rootScope.api('/api/profiles/'+ $rootScope.profile.id + '/delete'), {timeout: 60000}).then(function (response) {
                var index = $rootScope.customIgs.indexOf($rootScope.profile);
                if (index > -1) $rootScope.customIgs.splice(index, 1);
                $rootScope.backUp = null;
                waitingDialog.hide();
                $rootScope.context.page = $rootScope.pages[0];
                $rootScope.profile = null;
                $rootScope.generalInfo.message = "Implementation Guide deleted successfully !";
                $rootScope.generalInfo.type = 'success';
            }, function (error) {
                $scope.error = error;
                waitingDialog.hide();
            });
        };

        $scope.save = function () {
            waitingDialog.show('Saving changes...', {dialogSize: 'sm', progressType: 'success'});
            var changes = angular.toJson($rootScope.changes);
            var data = {"value": changes};
            $http.post($rootScope.api('/api/profiles/save'), data ,{timeout: 60000}).then(function (response) {
                $rootScope.generalInfo.message = "Implementation Guide saved successfully !";
                $rootScope.generalInfo.type = 'success';
                waitingDialog.hide();
                $rootScope.changes = {};
            }, function (error) {
                $scope.error = error;
                waitingDialog.hide();
            });
        };


        $scope.exportAs = function (format) {
            waitingDialog.show('Exporting profile...', {dialogSize: 'sm', progressType: 'success'});
//            var data = angular.toJson($rootScope.changes);

            var form = document.createElement("form");
            form.action = $rootScope.api('/api/profiles/'+ $rootScope.profile.id+ '/export/'+ format);
            form.method = "POST";
            form.target = "_target";
            form.style.display = 'none';
            form.params =
                document.body.appendChild(form);
            form.submit();
             waitingDialog.hide();




//            $http.post($rootScope.api('/api/profiles/'+ $rootScope.profile.id+ '/export'), data, {params: {'exportType': format}, timeout: 60000}).then(function (response) {
//                waitingDialog.hide();
//                $rootScope.changes = {};
//            }, function (error) {
//                $scope.error = error;
//                waitingDialog.hide();
//            });
        };


        $scope.close = function () {
            $rootScope.changes = {}; // FIXME
            $rootScope.profile = null;
            $rootScope.context.page = $rootScope.pages[0];
        };

    });

