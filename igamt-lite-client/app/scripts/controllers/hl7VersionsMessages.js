angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http, $httpBackend, HL7VersionSvc) {

			$rootScope.clickSource = {};
			
			$scope.hl7Versions = function(clickSource) {
				$rootScope.clickSource = clickSource;
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
					var hl7Version = HL7VersionSvc.hl7Version;
					switch ($rootScope.clickSource) {
					case "btn": { 
						$scope.createProfile(hl7Version, result);
						$rootScope.selectIgTab(1);
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
				}).then(
						function(response) {
							var len = response.data.length;
							for (var i = 0; i < len; i++) {
								hl7Versions.push(response.data[i]);
							}
//							$scope.setHL7Version(hl7Versions[len -1]);
						});
				return hl7Versions;
			};

			$scope.createProfile = function(hl7Version, msgIds) {
				$rootScope.isEditing = true;
				var iprw = {
						"hl7Version" : hl7Version,
						"msgIds" : msgIds,
						"timeout" : 60000
				};
				 $http.post('api/profiles/hl7/createIntegrationProfile', iprw).then(function
				 (response) {
					 $rootScope.profile = angular.fromJson(response.data);
					 $scope.getLeveledProfile($rootScope.profile);
					 $rootScope.$broadcast('event:IgsPushed', $rootScope.profile);
				 });
				 return $scope.profile;
			};

			$scope.updateProfile = function(msgIds) {
				var iprw = {
						"profile" : $rootScope.profile,
						"msgIds" : msgIds,
						"timeout" : 60000
				};
				 $http.post('api/profiles/hl7/updateIntegrationProfile', iprw).then(function
						 (response) {
					 $rootScope.profile = angular.fromJson(response.data);
					 $scope.getLeveledProfile($rootScope.profile);
				 });
			};
						
			$scope.getLeveledProfile = function(profile) {
				$rootScope.leveledProfile = [{title : "Datatypes", children : profile.datatypes.children},
				                         {title : "Segments", children : profile.segments.children},
				                         {title : "Messages", children : profile.messages.children},
				                         {title : "ValueSets", children : profile.tables.children}];
			};

			$scope.setHL7Version = function(hl7Version) {
				HL7VersionSvc.hl7Version = hl7Version;
			};
			
			$scope.closedCtxMenu = function(node, $index) {
				console.log("closedCtxMenu");
			};

		});

angular.module('igl').controller('HL7VersionsInstanceDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, hl7Versions, HL7VersionSvc) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			$scope.profileVersions = [];
			var profileVersions = [];

			$scope.loadProfilesByVersion = function() {
				 $http.get('api/profiles/hl7/messageListByVersion/' + $scope.hl7Version + "/" + $scope.profileVersions, {
					 	timeout : 60000
					 }).then(function (response) {
					 $scope.messagesByVersion = angular.fromJson(response.data);
				 });
			};

			$scope.trackSelections = function(bool, id) {
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
			
			$scope.$watch(function() {return $rootScope.profile}, function(newValue, oldValue) {
				if($rootScope.clickSource === "ctx") {
					$scope.hl7Version = newValue.metaData.hl7Version;
					$scope.loadProfilesByVersion();
				}
		    });
		    
		    $scope.getHL7Version = function() {
				return $rootScope.profile.metaData.hl7Version;
			};

			$scope.hl7Versions = hl7Versions;
			$scope.ok = function() {
				$scope.profileVersions = profileVersions;
				$modalInstance.close(profileVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
