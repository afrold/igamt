/**
 * Created by haffo on 1/12/15.
 */


angular.module('igl')
    .controller('ProfileListCtrl', function ($scope, $rootScope, Restangular, $http,$filter) {
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
                    $rootScope.message.children = $filter('orderBy')($rootScope.message.children, 'position');
                    angular.forEach($rootScope.message.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup);
                    });
                }

                angular.forEach($rootScope.profile.messages.children, function (message) {
                    var segRefOrGroups = [];
                    var segments = [];
                    var datatypes = [];
                    $scope.collectData(message, segRefOrGroups, segments,datatypes);
//                    $scope.loadSegments(message, segments);
//                    angular.forEach(segments, function (segment) {
//                        $scope.loadDatatypes(segment,datatypes);
//                    });
                    $rootScope.messagesData.push({message: message, segRefOrGroups: segRefOrGroups, segments:segments, datatypes:datatypes});


                });

            }, function (error) {
                $scope.error = error;
            });
        };


        $scope.collectData = function (node, segRefOrGroups, segments, datatypes) {
            if(node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.collectData(segmentRefOrGroup,segRefOrGroups,segments, datatypes);
                    });
                } else if (node.type === 'group') {
                    segRefOrGroups.push(node);
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.collectData(segmentRefOrGroup,segRefOrGroups,segments, datatypes);
                        });
                    }
                    segRefOrGroups.push({ name: node.name, "type": "end-group"});
                } else if (node.type === 'segment') {
                    segRefOrGroups.push(node);
                    if(segments.indexOf(node) === -1) {
                        segments.push(node.ref);
                    }
                    angular.forEach(node.ref.fields, function (field) {
                         $scope.collectData(field,segRefOrGroups,segments, datatypes);
                    });
                }else if(node.type === 'component' || node.type === 'subcomponent' || node.type === 'field'){
                    $scope.collectData(node.datatype,segRefOrGroups,segments,datatypes);
                }else if(node.type === 'datatype'){
                    if(datatypes.indexOf(node) === -1) {
                        datatypes.push(node);
                    }
                    if(node.children) {
                        angular.forEach(node.children, function (component) {
                            $scope.collectData(component,segRefOrGroups,segments,datatypes);
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




        $scope.delete = function (profile) {
            profile.remove().then(function () {
                var index = $scope.custom.indexOf(profile);
                if (index > -1) $scope.custom.splice(index, 1);
            }, function (error) {
                $scope.error = error;
            });
        };
    });


angular.module('igl')
    .controller('EditProfileCtrl', function ($scope, $rootScope, Restangular) {

        $scope.error = null;

        /**
         * init the controller
         */
        $scope.init = function () {
        };

        $scope.reset = function () {
            $rootScope.context.page = $rootScope.pages[0];
            //TODO: FIX ME
            $rootScope.changes = {};
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

        $scope.save = function () {


        };
    });

