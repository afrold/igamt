/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, DataTypeLibrarySvc, userInfoService) {

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				$scope.datatypeLibrary = DataTypeLibrarySvc.getDTLib("MASTER");
			};
		});
	};

angular.module('igl').controller('StandardDatatypeLibraryDlgCtl',
		function($scope, $rootScope, $filter, $http, $modal, $httpBackend, DataTypeLibrarySvc, userInfoService) {

	$scope.datatypeLibrary = [];
	
			$scope.openStandardDataypes = function() {
				
				var standardDatatypesInstance = $modal.open({
					templateUrl : 'standardDataTypeDlg.html',
					controller : 'StandardDatatypeLibraryInstanceDlgCtl',
					resolve : {
						hl7Versions : function() {
							return $scope.listHL7Versions();
						}
					}
				});
				
				standardDatatypesInstance.result.then(function(result) {
					$scope.datatypeLibrary = result;
				}
			});
		});

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $rootScope, $filter, $http, $modal, $httpBackend, DataTypeLibrarySvc, userInfoService) {
			
			$scope.datatypeLibrary = [];
			
			$scope.loadStandardDatatypes = function() {
				$scope.datatypeLibrary = DataTypeLibrarySvc.getDTLib("STANDARD");
			}
			
			$scope.trackSelections = function(bool, event) {
				if (bool) {
					$scope.datatypeLibrary.push(event);
				} else {
					for (var i = 0; i < $scope.datatypeLibrary.length; i++) {
						if ($scope.datatypeLibrary[i].id == id) {
							$scope.datatypeLibrary.splice(i, 1);
						}
					}
				}
			};

			$scope.ok = function() {
				$modalInstance.close($scope.datatypeLibrary);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
			
		});
