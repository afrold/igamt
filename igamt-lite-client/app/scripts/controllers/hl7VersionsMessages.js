angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http, $httpBackend,
				ProfileAccessSvc, userInfoService) {

			$rootScope.clickSource = {};
			$scope.hl7Version = {};

			$scope.hl7Versions = function(clickSource) {
				$rootScope.clickSource = clickSource;
				if (clickSource === "btn") {
					$rootScope.hl7Version = {};
					$rootScope.profile = false;
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
						$scope.createProfile(hl7Version, result);
						break;
					}
					case "ctx": {
						$scope.updateProfile(result);
						break;
					}
					}
				});
			};

			$scope.listHL7Versions = function() {
				var hl7Versions = [];
				$http.get('api/profiles/hl7/findVersions', {
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
			$scope.createProfile = function(hl7Version, msgIds) {
				console.log("Creating profile...");
				var iprw = {
					"hl7Version" : hl7Version,
					"msgIds" : msgIds,
					"accountID" : userInfoService.getAccountID(), 
					"timeout" : 60000
				};
				$http.post('api/profiles/hl7/createIntegrationProfile', iprw)
						.then(
								function(response) {
									var igdocument = angular
											.fromJson(response.data);
									$rootScope
											.$broadcast(
													'event:openIGDocumentRequest',
													igdocument);
									$rootScope.$broadcast('event:IgsPushed',
											igdocument.profile);
								});
				return $rootScope.igdocument;
			};

			/**
			 * TODO: Handle error from server
			 * 
			 * @param msgIds
			 */
			$scope.updateProfile = function(msgIds) {
				console.log("Updating profile...");
				var iprw = {
					"profile" : $rootScope.profile,
					"msgIds" : msgIds,
					"timeout" : 60000
				};
				$http.post('api/profiles/hl7/updateIntegrationProfile', iprw)
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
				ProfileAccessSvc) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			console.log("$scope.profileVersions init");
			$scope.profileVersions = [];
			var profileVersions = [];

			$scope.loadProfilesByVersion = function() {
				$rootScope.hl7Version = $scope.hl7Version;
				console.log("loadProfilesByVersion.hl7Version=" + $scope.hl7Version);
				console.log("loadProfilesByVersion.profileVersions=" + $scope.profileVersions);
				$http.post(
						'api/profiles/hl7/messageListByVersion', angular.fromJson({
							"hl7Version" : $scope.hl7Version,
							"messageIds" : $scope.profileVersions
						})).then(function(response) {
					$scope.messagesByVersion = angular.fromJson(response.data);
					});
				};

			$scope.trackSelections = function(bool, id) {
				console.log("trackSelections=" + id);
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

			$scope.$watch(function() {
				return $rootScope.profile
			}, function(newValue, oldValue) {
				if ($rootScope.clickSource === "ctx") {
					$scope.hl7Version = newValue.metaData.hl7Version;
					$scope.profileVersions = ProfileAccessSvc.Messages($rootScope.profile).getMessageIds();
					console.log("$watch.profileVersions=" + $scope.profileVersions);
					$scope.loadProfilesByVersion();
				}
			});

			$scope.getHL7Version = function() {
				return ProfileAccessSvc.getVersion($rootScope.profile);
			};

			$scope.hl7Versions = hl7Versions;
			$scope.ok = function() {
				console.log("ok-profileVersions=" + profileVersions);
				$scope.profileVersions = profileVersions;
				$modalInstance.close(profileVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
