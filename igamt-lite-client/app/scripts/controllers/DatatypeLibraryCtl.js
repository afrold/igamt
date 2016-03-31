/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, $modal, $q, DatatypeLibrarySvc, userInfoService) {

			$scope.datatypeStruct = false;
			
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];		
			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				getDataTypeLibrary("MASTER");
			};
			
			function getDataTypeLibrary(scope) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope).then(function(data) {
						$scope.datatypeStruct =  data;
						if (!$scope.datatypeStruct.scope) {
							$scope.datatypeStruct.scope = scope;
						}
						console.log("$scope.datatypeStruct.id=" + $scope.datatypeStruct.id + " $scope.datatypeStruct.scope=" + $scope.datatypeStruct.scope);
				}).catch( function (error) {
					console.log(error);
				});
			};
			
			$scope.getDisplayLabel = function(dt) {
				if(dt) {
					return (dt.ext) ? dt.label + "_" + dt.ext : dt.label;
				}
			} 
			
			$scope.trackSelections = function(bool, selection) {
				if (bool) {
					selection.status = "PUBLISHED";
				} else {
					selection.status = "UNPUBLISHED";
				}
			};
			
			$scope.save = function() {
				angular.forEach($scope.datatypeStruct.children, function(dt) {
					delete dt.new;
				});
				DatatypeLibrarySvc.save($scope.datatypeStruct);
			};
			
			$scope.openDatatype = function(datatype) {
				console.log("$scope.openDatatype(datatype)");
			};
			
			$scope.openStandardDataypes = function() {
				DatatypeLibrarySvc.getDataTypeLibrary("HL7STANDARD").then(function(data) {
					var datatypeStruct = data;
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
						resolve : {
							"datatypeStruct" : datatypeStruct
						}
					}).result.then(function(standardSelections) {
						console.log("standardSelections=" + standardSelections.length);
					    // Decorate the user selections.
						var decoratedSelections = decoratedSelections(standardSelections);
					    // Push them on to the scope.
						angular.forEach(decoratedSelections, function(child) {
							child.new = true;
							$scope.datatypeStruct.children.push(child);
						});
						console.log("$scope.datatypeStruct.children=" + $scope.datatypeStruct.children.length);
					});
				}).catch( function (error) {
				console.log(error);
			});
		};
		
		function decoratedSelections(datatypes) {
			angular.forEach(datatypes, function(child) {
				child.ext = Math.floor(Math.random() * 100);
				child.scope = "MASTER";
				child.accountId = 
			});
			return datatypes;
		};
});

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $rootScope, $filter, $http, $modalInstance, $httpBackend, datatypeStruct, DatatypeLibrarySvc, userInfoService) {
			
			$scope.okDisabled = true;

			$scope.datatypeStruct = datatypeStruct;	
			$scope.standardSelections = [];
			
			$scope.getDisplayLabel = function(dt) {
				if (dt) {
					return dt.label;
				}
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
