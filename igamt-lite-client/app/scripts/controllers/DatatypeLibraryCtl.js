/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, $modal, $q, DatatypeLibrarySvc, userInfoService) {

			$scope.datatypeStruct = {"children" : []};
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
			
			$scope.getDisplayLabel = function(label, extension) {
				return (extension) ? label + "_" + extension : label;
			} 
			
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
			
			$scope.save = function() {
				DatatypeLibrarySvc.save($scope.datatypeLibrary);
			};
			
			$scope.openStandardDataypes = function() {
				DatatypeLibrarySvc.getDataTypeLibrary("HL7STANDARD").then(function(data) {
				    var datatypeLibrary = DatatypeLibrarySvc.assembleDatatypeLibrary(data);
				    // Assemble a datatypeLibrary and pass it into the dialog.
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
						resolve : {
							"datatypeLibrary" : datatypeLibrary
						}
					}).result.then(function(standardSelections) {
						console.log("standardSelections=" + standardSelections.length);
					    // Decorate the user selections.
						var decoratedSelections = decorateLabels(standardSelections);
					    // Push them on to the scope.
						angular.forEach(decoratedSelections, function(child) {
							$scope.datatypeStruct.children.push(child);
						});
					    // Assemble a datatypeLibrary and keep it in this scope.
						$scope.datatypeLibrary = DatatypeLibrarySvc.assembleDatatypeLibrary($scope.datatypeStruct);
						$scope.datatypeLibrary.refresh();
						console.log("$scope.datatypeStruct.children=" + $scope.datatypeStruct.children.length);
						console.log("$scope.datatypeLibrary=" + JSON.stringify($scope.datatypeLibrary));
					});
				}).catch( function (error) {
				console.log(error);
			});
		};
		
		function decorateLabels(datatypes) {
			angular.forEach(datatypes.children, function(child) {
				child.ext = Math.floor(Math.random() * 100);
			});
			return datatypes;
		};
});

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $rootScope, $filter, $http, $modalInstance, $httpBackend, datatypeLibrary, DatatypeLibrarySvc, userInfoService) {
			
			$scope.okDisabled = true;

			$scope.datatypeLibrary = datatypeLibrary;		
			$scope.standardSelections = [];
			
			$scope.getDisplayLabel = function(label, extension) {
				return label;
			} 
			
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
