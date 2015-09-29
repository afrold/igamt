angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http, $httpBackend) {

			$scope.hl7Versions = function() {
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
					console.log(result);
					$scope.createProfile($rootScope.hl7Version, result);
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
						});
				return hl7Versions;
			};

			$scope.createProfile = function(hl7Version, msgIds) {
				var iprw = {
						"hl7Version" : hl7Version,
						"msgIds" : msgIds,
						"timeout" : 60000
				};
				 $http.post('api/profiles/hl7/createIntegrationProfile', iprw).then(function
				 (response) {
					 $scope.profile = angular.fromJson(response.data);
					 $rootScope.$broadcast('event:IgsPushed', $scope.profile);
				 });
				 return $scope.profile;
			}
		});

angular.module('igl').controller('HL7VersionsInstanceDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, hl7Versions) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			var profileVersions = [];

			$scope.loadProfilesByVersion = function() {
				 console.log("$scope.hl7Version=" + $scope.hl7Version);
				 $rootScope.hl7Version = $scope.hl7Version;
				 $http.get('api/profiles/hl7/messageListByVersion/' + $scope.hl7Version, {
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

			$scope.hl7Versions = hl7Versions;
			$scope.ok = function() {
				$modalInstance.close(profileVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
