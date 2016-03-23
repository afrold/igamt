/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, $modal, $q, DatatypeLibrarySvc, userInfoService) {

			$scope.datatypeStruct = {};
			$scope.datatypeLibrary = false;		
			$scope.publishSelections = [];
			
			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				getDataTypeLibrary("MASTER");
			};
			
			function getDataTypeLibrary(scope) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope).then(function(data) {
						$scope.datatypeStruct = data;
					    $scope.datatypeLibrary = DatatypeLibrarySvc.assembleDatatypeLibrary($scope.datatypeStruct);
					    $scope.datatypeLibrary.refresh();
					}).catch( function (error) {
					console.log(error);
				});
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
			
			$scope.openStandardDataypes = function(scope) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope).then(function(datatypeStruct) {
				    var datatypeLibrary = DatatypeLibrarySvc.assembleDatatypeLibrary(datatypeStruct);

					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
						resolve : {
							"datatypeLibrary" : datatypeLibrary
						}
					}).result.then(function(standardSelections) {
						angular.forEach(standardSelections, function(child) {
							$scope.datatypeStruct.children.push(child);
						});
						$scope.datatypeStruct.id = undefined;
						DatatypeLibrarySvc.save($scope.datatypeStruct).then(function()  {
			                $scope.getDataTypeLibrary("MASTER");
						});
					});
					
				}).catch( function (error) {
				console.log(error);
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
