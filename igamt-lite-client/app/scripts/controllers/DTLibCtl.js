/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('DatatypeLibraryCtl',
		function($scope, $rootScope, $filter, $http) {

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				$http.get('api/datatype-library', {
					timeout : 60000
				}).then(function(response) {
					$scope.datatypeLibrary = angular.fromJson(response.data);
				});
			};
		});
