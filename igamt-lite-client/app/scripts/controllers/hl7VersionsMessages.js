angular.module('igl').controller(
    'HL7VersionsDlgCtrl',
    function ($scope, $rootScope, $modal, $log, $http, $httpBackend, userInfoService) {

        $rootScope.clickSource = {};

        $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();

        $scope.hl7Versions = function (clickSource) {
            $rootScope.clickSource = clickSource;
            if ($rootScope.hasChanges()) {
                $rootScope.openConfirmLeaveDlg().result.then(function () {
                    $rootScope.clearChanges();
                    $scope.hl7VersionsInstance();
                });
            } else if (clickSource === 'btn' && $rootScope.igdocument != null) {
                return $modal.open({
                    templateUrl: 'CreateNewIGAlert.html',
                    size: 'md',
                    controller: 'CreateNewIGAlertCtrl'
                });
            } else {
                $rootScope.hl7Versions = [];
                $scope.hl7VersionsInstance();
            }
        };

        $scope.confirmOpen = function (igdocument) {
            return $modal.open({
                templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
                controller: 'ConfirmIGDocumentOpenCtrl',
                resolve: {
                    igdocumentToOpen: function () {
                        return igdocument;
                    }
                }
            }).result.then(function (igdocument) {
                    $rootScope.clearChanges();
                    $scope.hl7VersionsInstance();
                }, function () {
                    console.log("Changes discarded.");
                });
        };

        $scope.hl7VersionsInstance = function () {
            $scope.listHL7Versions().then(function(response){
                var hl7Versions = [];
                var length = response.data.length;
                for (var i = 0; i < length; i++) {
                    hl7Versions.push(response.data[i]);
                }
                return $modal.open({
                    templateUrl: 'hl7VersionsDlg.html',
                    controller: 'HL7VersionsInstanceDlgCtrl',
                    windowClass: 'hl7-versions-modal',
                    resolve: {
                        hl7Versions: function () {
                            return hl7Versions;
                        },
                        hl7Version: function () {
                            console.log("$rootScope.clickSource=" + $rootScope.clickSource);
                            if ($rootScope.clickSource === "ctx") {
                                console.log("hl7Version=" + $rootScope.igdocument.profile.metaData.hl7Version);
                                return $rootScope.igdocument.profile.metaData.hl7Version;
                            } else {
                                return null;
                            }
                        }
                    }
                }).result.then(function (igdocument) {
                        $rootScope
                            .$emit(
                            'event:openIGDocumentRequest',
                            igdocument);
                        $rootScope.$broadcast('event:IgsPushed',
                            igdocument);
                    });
            }, function(response){
                $rootScope.msg().text = "Cannot load the versions. Please try again";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
             });


        };

        $scope.listHL7Versions = function () {
            return $http.get('api/igdocuments/findVersions', {
                timeout: 60000
            });
        };


        $scope.closedCtxMenu = function (node, $index) {
            console.log("closedCtxMenu");
        };

    });

angular.module('igl').controller(
    'HL7VersionsInstanceDlgCtrl',
    function ($scope, $rootScope, $modalInstance, $http, hl7Versions, ProfileAccessSvc, MessageEventsSvc, $timeout, ngTreetableParams, userInfoService,hl7Version) {

        $scope.hl7Versions = hl7Versions;
        $scope.hl7Version = hl7Version;
        $scope.okDisabled = true;
        $scope.messageIds = [];
        $scope.messageEvents = [];
        $scope.loading = false;
        var messageEvents = [];
        $scope.messageEventsParams = null;
        $scope.scrollbarWidth = $rootScope.getScrollbarWidth();


        $scope.messageEventsParams = new ngTreetableParams({
            getNodes: function (parent) {
                return parent && parent != null ? parent.children : $scope.hl7Version != null ? MessageEventsSvc.getMessageEvents($scope.hl7Version) : [];
            },
            getTemplate: function (node) {
                return 'MessageEventsNode.html';
            }
        });


        $scope.loadIGDocumentsByVersion = function () {
            $scope.loading = true;
            $timeout(function () {
                 if ($scope.messageEventsParams)
                    $scope.messageEventsParams.refresh();
                $scope.loading = false;
            });
        };

        $scope.isBranch = function (node) {
            var rval = false;
            if (node.type === "message") {
                rval = true;
                MessageEventsSvc.putState(node);
            }
            return rval;
        };

        $scope.trackSelections = function (bool, event) {
            if (bool) {
                messageEvents.push({ "id": event.id, "children": [
                    {"name": event.name}
                ]});
            } else {
                for (var i = 0; i < messageEvents.length; i++) {
                    if (messageEvents[i].id == event.id) {
                        messageEvents.splice(i, 1);
                    }
                }
            }
            $scope.okDisabled = messageEvents.length === 0;
        };


//        $scope.$watch(function () {
//            return $rootScope.igdocument.id;
//        }, function (newValue, oldValue) {
//            if ($rootScope.clickSource === "ctx") {
//                $scope.hl7Version = $rootScope.hl7Version;
//                $scope.messageIds = ProfileAccessSvc.Messages().getMessageIds();
//                $scope.loadIGDocumentsByVersion();
//            }
//        });

        $scope.ok = function () {
            // create new ig doc submitted.
            $scope.messageEvents = messageEvents;
            switch ($rootScope.clickSource) {
                case "btn":
                {
                    createIGDocument($scope.hl7Version, messageEvents);
                    break;
                }
                case "ctx":
                {
                    updateIGDocument(messageEvents);
                    break;
                }
            }
        };

        var createIGDocument = function (hl7Version, msgEvts) {
            console.log("create Ig called");
            var iprw = {
                "hl7Version": hl7Version,
                "msgEvts": msgEvts,
                "accountID": userInfoService.getAccountID(),
                "timeout": 60000
            };
            $scope.okDisabled = true;
            $http.post('api/igdocuments/createIntegrationProfile', iprw)
                .then(
                function (response) {
                    var igdocument = angular
                        .fromJson(response.data);
                    $modalInstance.close(igdocument);
                }, function (response) {
                    $rootScope.msg().text = response.data;
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    $scope.okDisabled = false;
                });
         };

        /**
         * TODO: Handle error from server
         *
         * @param msgIds
         */
        var updateIGDocument = function (msgEvts) {
            console.log("update Ig called");
            $scope.okDisabled = true;
            var iprw = {
                "igdocument": $rootScope.igdocument,
                "msgEvts": msgEvts,
                "timeout": 60000
            };
            $http.post('api/igdocuments/updateIntegrationProfile', iprw)
                .then(
                function (response) {
                    var igdocument = angular
                        .fromJson(response.data);
                    $modalInstance.close(igdocument);
                }, function (response) {
                    $rootScope.msg().text = response.data;
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                    $scope.okDisabled = false;
                });
        };

        if($scope.hl7Version != null){
            $scope.loadIGDocumentsByVersion();
        }

        $scope.cancel = function () {
            $modalInstance.dismiss('cancel');
        };
    });
