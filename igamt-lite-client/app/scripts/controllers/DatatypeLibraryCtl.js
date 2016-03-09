/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, DatatypeLibrarySvc, userInfoService) {

			$scope.publishSelections = [];
			
            $rootScope.$on('event:initDatatypeLibrary', function (event) {
                $scope.initDatatypeLibrary();
            });
			
			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				$scope.datatypeLibrary = DatatypeLibrarySvc.getDataTypeLibrary("MASTER");
			};
			
			$scope.trackSelections = function(bool, selection) {
				if (bool) {
					$scope.publishSelections.push(selection);
				} else {
					for (var i = 0; i < $scope.publishSelections.length; i++) {
						if ($scope.publishSelections[i].id === id) {
							$scope.publishSelections.splice(i, 1);
						}
					}
				}
			};
		});

angular.module('igl').controller('StandardDatatypeLibraryDlgCtl',
		function($scope, $rootScope, $filter, $http, $modal, $httpBackend, DatatypeLibrarySvc, userInfoService) {

			$scope.standardSelections = [];
	
			$scope.openStandardDataypes = function() {
				
				var standardDatatypesInstance = $modal.open({
					templateUrl : 'standardDatatypeDlg.html',
					controller : 'StandardDatatypeLibraryInstanceDlgCtl',
					resolve : {
						datatypeLibrary : DatatypeLibrarySvc.getDataTypeLibrary("STANDARD")
					}
				});
				
				standardDatatypesInstance.result.then(function(result) {
					var datatypeLibrary = angular.copy(DatatypeLibrarySvc.datatypeLibrary);
					datatypeLibrary.id = undefined;
					datatypeLibrary.children = result;
					DatatypeLibrarySvc.save(datatypeLibrary).then(function()  {
						$rootScope.$broadcast('event:initDatatypeLibrary');	
					});
			});
			};
		});

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $rootScope, $filter, $http, $modalInstance, $httpBackend, datatypeLibrary, DatatypeLibrarySvc, userInfoService) {
			
			$scope.okDisabled = true;
			$scope.datatypeLibrary = datatypeLibrary;		
			$scope.standardSelections = [];
			
			$scope.trackSelections = function(bool, event) {
				if (bool) {
					$scope.standardSelections.push(event);
				} else {
					for (var i = 0; i < $scope.standardSelections.length; i++) {
						if ($scope.standardSelections[i].id === event.id) {
							$scope.standardSelections.splice(i, 1);
						}
					}
				}
				$scope.okDisabled = $scope.standardSelections.length === 0;
			};

			$scope.ok = function() {
				$modalInstance.close($scope.standardSelections);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
			
		});
