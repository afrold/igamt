/**
 * Created by Jungyub on 4/01/15.
 */

angular.module('igl').controller('ConstraintsListCtrl',function($scope, $rootScope, Restangular, $filter) {
	$scope.loading = false;
	$scope.tmpSegmentPredicates = [].concat($rootScope.segmentPredicates);
	$scope.tmpSegmentConformanceStatements = [].concat($rootScope.segmentConformanceStatements);
	$scope.tmpDatatypePredicates = [].concat($rootScope.datatypePredicates);
	$scope.tmpDatatypeConformanceStatements = [].concat($rootScope.datatypeConformanceStatements);
	 
	
	
	$scope.init = function() {
	};
	
});