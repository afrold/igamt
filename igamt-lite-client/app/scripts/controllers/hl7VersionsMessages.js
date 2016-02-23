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
			$scope.createIGDocument = function(hl7Version, msgIds) {
				console.log("Creating igdocument...");
				var iprw = {
					"hl7Version" : hl7Version,
					"msgIds" : msgIds,
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
				ProfileAccessSvc, ngTreetableParams) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			$scope.igdocumentVersions = [];
			var igdocumentVersions = [];
			var mes = 					[ {
				  "messageStructureId" : "ORR_O02",
				  "type" : "structure",
				  "events" : [ {
				    "type" : "event",
				    "event" : "O02"
				  } ],
				  "description" : "General order response message response to any ORM"
				}, {
				  "messageStructureId" : "BAR_P02",
				  "type" : "structure",
				  "events" : [ {
				    "type" : "event",
				    "event" : "P02"
				  } ],
				  "description" : "Add/change billing account"
				}, {
				  "messageStructureId" : "RRD_O14",
				  "type" : "structure",
				  "events" : [ {
				    "type" : "event",
				    "event" : "O14"
				  } ],
				  "description" : "Pharmacy/treatment dispense acknowledgment message"
				}];
			
			$scope.loadIGDocumentsByVersion = function() {
				$rootScope.hl7Version = $scope.hl7Version;
				$scope.messageEventsParams = new ngTreetableParams( {
					getNodes: function(parent) {
						console.log("parent ? parent.children : mes;");
						return parent ? parent.children : mes;
					},
			        getTemplate: function(node) {
			            return 'MessageEventsNode.html';
			        },
			        options: {
			            onNodeExpand: function() {
			                console.log('A node was expanded!');
			            }
			        }
				});
			};

//				$scope.messageEventsParams = new ngTreetableParams( {
//					getNodes: function(parent) {
//				        var deferred = $q.defer();
//				        $http.post('api/igdocuments/messageListByVersion', angular.fromJson({
//							"hl7Version" : $scope.hl7Version,
//							"messageIds" : $scope.igdocumentVersions
//						})).success(function(data) {
//				            deferred.resolve(data);
//						});
//				        return deferred.promise;
//					},
//			        getTemplate: function(node) {
//			            return 'MessageEventsNode.html';
//			        }
//				});
			
			$scope.trackSelections = function(bool, id) {
				if (bool) {
					igdocumentVersions.push(id);
				} else {
					for (var i = 0; i < igdocumentVersions.length; i++) {
						if (igdocumentVersions[i].id == id) {
							igdocumentVersions.splice(i, 1);
						}
					}
				}
			};

			$scope.$watch(function() {
				return $rootScope.igdocument
			}, function(newValue, oldValue) {
				if ($rootScope.clickSource === "ctx") {
					$scope.hl7Version = newValue.metaData.hl7Version;
					$scope.igdocumentVersions = ProfileAccessSvc.Messages().getMessageIds();
					$scope.loadIGDocumentsByVersion();
				}
			});

//			$scope.getHL7Version = function() {
//				return ProfileAccessSvc.Version();
//			};

			$scope.hl7Versions = hl7Versions;
			$scope.ok = function() {
				$scope.igdocumentVersions = igdocumentVersions;
				$modalInstance.close(igdocumentVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
