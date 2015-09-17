angular.module('igl').controller(
		'HL7VersionsDlgCtrl',
		function($scope, $rootScope, $modal, $log, $http) {

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
					var profile = $scope.createProfile(result);
					$rootScope.$broadcast('event:IgsPushed', profile);
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
				 $http.get('/api/profiles/createIntegrationProfile/' + hl7Version + '/' + msgIds, {timeout:
				 60000}).then(function
				 (response) {
					 profile = angular.fromJson(response.data);
				 });
				 return profile;
//				var request = new XMLHttpRequest();
//				request
//						.open('GET', '../../resources/profile-2.5.1.json',
//								false);
//				request.send(null);
//				var profile = angular.fromJson(request.response);
//				return profile;
			}
		});

angular.module('igl').controller('HL7VersionsInstanceDlgCtrl',
		function($scope, $modalInstance, $http, hl7Versions) {

			$scope.selected = {
				item : hl7Versions[0]
			};

			var profileVersions = [];

			$scope.loadProfilesByVersion = function() {
				 console.log("==>0");
				 $http.get('/api/profiles/messageListByVersion/' + $scope.hl7Version, {
					 	timeout : 60000
					 }).then(function (response) {
						 console.log("==>1");
					 $scope.profilesByVersion = angular.fromJson(response.data);
					 console.log("response.data=" + response.data);
					 console.log("$scope.profilesByVersion=" +
					 $scope.profilesByVersion);
				 });
//				$scope.profilesByVersion = [ {
//					"Id" : "aId",
//					"StructID" : "aStructID",
//					"Version" : "aVersion",
//					"Type" : "aType",
//					"Event" : "aEvent",
//					"Description" : "aDescription"
//				}, {
//					"Id" : "bId",
//					"StructID" : "bStructID",
//					"Version" : "bVersion",
//					"Type" : "bType",
//					"Event" : "bEvent",
//					"Description" : "bDescription"
//				}, {
//					"Id" : "cId",
//					"StructID" : "cStructID",
//					"Version" : "cVersion",
//					"Type" : "cType",
//					"Event" : "cEvent",
//					"Description" : "cDescription"
//				} ]
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
