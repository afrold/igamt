angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http, $httpBackend,
				userInfoService) {

			$rootScope.clickSource = {};

            $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
		
			$scope.hl7Versions = function(clickSource) {
				console.log("$scope.hl7Versions  clickSource=" + clickSource);
				$rootScope.clickSource = clickSource;
				if ($rootScope.hasChanges()) {
					$scope.confirmOpen($rootScope.igdocument);
				} else {
					$rootScope.hl7Versions = false;
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
			
			$scope.hl7VersionsInstance = function() {
				return $modal.open({
					templateUrl : 'hl7VersionsDlg.html',
					controller : 'HL7VersionsInstanceDlgCtrl',
                    windowClass: 'hl7-versions-modal',
					resolve : {
						hl7Versions : function() {
							return $scope.listHL7Versions();
						}
					}
				}).result.then(function(result) {
					switch ($rootScope.clickSource) {
					case "btn": {
						$scope.createIGDocument($rootScope.hl7Version, result);
						break;
					}
					case "ctx": {
						$scope.updateIGDocument(result);
						break;
					}
					}
				});			
			};

			$scope.listHL7Versions = function() {
				return $http.get('api/igdocuments/findVersions', {
					timeout : 60000
				}).then(function(response) {
					var hl7Versions = [];
					if ($rootScope.clickSource !== "ctx") {
						$rootScope.hl7Version = $scope.hl7Version = false;
						$rootScope.igdocument = false;
					}
					var length = response.data.length;
					for (var i = 0; i < length; i++) {
						hl7Versions.push(response.data[i]);
					}
					return hl7Versions;
				});
			};

			/**
			 * TODO: Handle error from server
			 * 
			 * @param msgIds
			 */
			$scope.createIGDocument = function(hl7Version, msgEvts) {
				console.log("Creating IGDocument...");
				console.log("msgEvts=" + msgEvts);
				var iprw = {
					"hl7Version" : hl7Version,
					"msgEvts" : msgEvts,
					"accountID" : userInfoService.getAccountID(), 
					"timeout" : 60000
				};
				$http.post('api/igdocuments/createIntegrationProfile', iprw)
						.then(
								function(response) {
									var igdocument = angular
											.fromJson(response.data);
									$rootScope
											.$broadcast(
													'event:openIGDocumentRequest',
													igdocument);
									$rootScope.$broadcast('event:IgsPushed',
											igdocument);
								});
				return $rootScope.igdocument;
			};

			/**
			 * TODO: Handle error from server
			 * 
			 * @param msgIds
			 */
			$scope.updateIGDocument = function(msgEvts) {
				console.log("Updating igdocument...");
				console.log("$scope.updateIGDocumentmsgEvts=" + JSON.stringify(msgEvts));
				var iprw = {
					"igdocument" : $rootScope.igdocument,
					"msgEvts" : msgEvts,
					"timeout" : 60000
				};
				$http.post('api/igdocuments/updateIntegrationProfile', iprw)
						.then(
								function(response) {
									var igdocument = angular
											.fromJson(response.data);
									$rootScope
											.$broadcast(
													'event:openIGDocumentRequest',
													igdocument);
								});
			};

			$scope.closedCtxMenu = function(node, $index) {
				console.log("closedCtxMenu");
			};

		});

angular.module('igl').controller(
		'HL7VersionsInstanceDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, hl7Versions,
				ProfileAccessSvc, MessageEventsSvc, $timeout) {

			$scope.hl7Versions = hl7Versions;
			$scope.hl7Version = $rootScope.hl7Version;
			$scope.okDisabled = true;
			$scope.messageIds = [];
			$scope.messageEvents = [];
            $scope.loading = false;
			var messageEvents = [];
            $scope.messageEventsParams = null;
            $scope.scrollbarWidth = $rootScope.getScrollbarWidth();

            $scope.loadIGDocumentsByVersion = function() {
                $scope.loading = true;
                $timeout(function() {
                    $rootScope.hl7Version = $scope.hl7Version;
                    $scope.messageEventsParams = MessageEventsSvc.getMessageEvents($rootScope.hl7Version);
                    $scope.loading = false;
                });
			};
			
			$scope.isBranch = function(node) {
				var rval = false;
				if (node.type === "message") {
					rval = true;
					MessageEventsSvc.putState(node);
				}
				return rval;
			};
			
			$scope.trackSelections = function(bool, event) {
				if (bool) {
					messageEvents.push({ "id" : event.id, "children" : [{"name" : event.name}]});
				} else {
					for (var i = 0; i < messageEvents.length; i++) {
						if (messageEvents[i].id == event.id) {
							messageEvents.splice(i, 1);
						}
					}
				}
				$scope.okDisabled = messageEvents.length === 0;
			};


			$scope.$watch(function() {
				return $rootScope.igdocument.id;
			}, function(newValue, oldValue) {
				if ($rootScope.clickSource === "ctx") {
					$scope.hl7Version = $rootScope.hl7Version;
					$scope.messageIds = ProfileAccessSvc.Messages().getMessageIds();
					$scope.loadIGDocumentsByVersion();
				}
			});

			$scope.ok = function() {
				$scope.messageEvents = messageEvents;
				$modalInstance.close(messageEvents);
			};
			
			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
