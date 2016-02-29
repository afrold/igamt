angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http, $httpBackend,
				userInfoService) {

			$rootScope.clickSource = {};
			$scope.hl7Version = {};
			$scope.hl7Versions = function(clickSource) {
				$rootScope.clickSource = clickSource;
				if (clickSource === "btn") {
					$rootScope.hl7Version = {};
					$rootScope.igdocument = false;
				}
				var hl7VersionsInstance = $modal.open({
					templateUrl : 'hl7VersionsDlg.html',
					controller : 'HL7VersionsInstanceDlgCtrl',
					resolve : {
						hl7Versions : function() {
							return $scope.listHL7Versions();
						}
					}
				});

				hl7VersionsInstance.result.then(function(result) {
					var hl7Version = $rootScope.hl7Version;
					switch ($rootScope.clickSource) {
					case "btn": {
						$scope.createIGDocument(hl7Version, result);
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
				var hl7Versions = [];
				$http.get('api/igdocuments/findVersions', {
					timeout : 60000
				}).then(function(response) {
					var len = response.data.length;
					for (var i = 0; i < len; i++) {
						hl7Versions.push(response.data[i]);
					}
				});
				return hl7Versions;
			};

			/**
			 * TODO: Handle error from server
			 * 
			 * @param msgIds
			 */
			$scope.createIGDocument = function(hl7Version, msgEvts) {
				console.log("Creating igdocument...");
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
			$scope.updateIGDocument = function(msgIds) {
				console.log("Updating igdocument...");
				var iprw = {
					"igdocument" : $rootScope.igdocument,
					"msgIds" : msgIds,
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
				ProfileAccessSvc, MessageEventsSvc) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			$scope.messageIds = [];
			$scope.messageEvents = [];
			var messageEvents = [];
			
			$scope.loadIGDocumentsByVersion = function() {
				$rootScope.hl7Version = $scope.hl7Version;
				$scope.messageEventsParams = MessageEventsSvc.getMessageEvents($scope.hl7Version, $scope.messageIds);
			};
			
			$scope.isBranch = function(node) {
				var rval = false;
				if (node.type === "message") {
					rval = true;
					MessageEventsSvc.putState(node);
				}
				return rval;
			};
			
			$scope.getState = function() {
				return MessageEventsSvc.getState();
			}
			
			$scope.trackSelections = function(bool, event) {
				if (bool) {
					messageEvents.push({ "id" : event.id, "children" : [{"name" : event.name}]});
				} else {
					for (var i = 0; i < messageEvents.length; i++) {
						if (messageEvents[i].id == id) {
							messageEvents.splice(i, 1);
						}
					}
				}
			};

			$scope.$watch(function() {
				return $rootScope.igdocument
			}, function(newValue, oldValue) {
				if ($rootScope.clickSource === "ctx") {
					$scope.hl7Version = newValue.metaData.hl7Version;
					$scope.messageIds = ProfileAccessSvc.Messages().getMessageIds();
					$scope.loadIGDocumentsByVersion();
				}
			});

			$scope.hl7Versions = hl7Versions;
			$scope.ok = function() {
				$scope.messageEvents = messageEvents;
				$modalInstance.close(messageEvents);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
