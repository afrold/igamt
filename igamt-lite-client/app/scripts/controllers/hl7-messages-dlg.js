angular.module('igl').controller('HL7VMessagesDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, HL7VersionSvc) {

			var profileVersions = [];
			
			$scope.hl7Version = HL7VersionSvc.hl7Version;
			
			 $http.get('api/profiles/hl7/messageListByVersion/' + $scope.hl7Version, {
				 	timeout : 60000
				 }).then(function (response) {
					 $scope.messagesByVersion = angular.fromJson(response.data);
			 });
			
//			$scope.getVersion = function() {
//				return HL7VersionSvc.hl7Version;
//			}
					
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

			$scope.ok = function() {
				$modalInstance.close(profileVersions);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
		});
