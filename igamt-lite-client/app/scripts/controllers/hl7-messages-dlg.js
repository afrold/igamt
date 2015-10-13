angular.module('igl').controller('HL7VMessagesDlgCtrl',
		function($scope, $rootScope, $modalInstance, $http, hl7Version) {

			$scope.selected = {
				item : hl7Version
			};

			var profileVersions = [];

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
