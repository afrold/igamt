/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http, $httpBackend, $modal, $q, ngTreetableParams, DatatypeLibrarySvc, userInfoService) {

			$scope.datatypes = [];
			$scope.datatypeLibrary = false;		
			$scope.publishSelections = [];
			
            $rootScope.$on('event:initDatatypeLibrary', function (event) {
                $scope.initDatatypeLibrary();
            });
			
			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				getDataTypeLibrary("MASTER");
			};
			
			function getDataTypeLibrary(scope) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope).then(function(data) {
				    var dtLib = assembleDatatypeLibrary(data);
				    if (scope === "MASTER") {
					    $scope.datatypes = data;
				    $scope.datatypeLibrary = dtLib;
				    } else {
				    	return dtLib;
				    }
				}).catch( function (error) {
					console.log(error);
				});
			};
			
			function assembleDatatypeLibrary(datatypes) {
				return new ngTreetableParams({
					getNodes : function(parent) {
						return datatypes.children;
					},
			        getTemplate : function(node) {
			            return 'dataTypeNode.html';
			        },
			        options : {
			            onNodeExpand: function() {
			                console.log('A node was expanded!');
			            }
			        }
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
			
			$scope.openStandardDataypes = function() {
				
				var standardDatatypesInstance = $modal.open({
					templateUrl : 'standardDatatypeDlg.html',
					controller : 'StandardDatatypeLibraryInstanceDlgCtl',
					resolve : {
						datatypeLibrary : getDataTypeLibrary("HL7STANDARD")
					}
				}).result.then(function(standardSelections) {
					angular.foreach(standardSelections, function(child) {
						$scope.datatypes.value.children.push(child);
					});
					$scope.datatypes.id = undefined;
					$scope.datatypes.scope = "MASTER";
					DatatypeLibrarySvc.save($scope.datatypes).then(function()  {
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
