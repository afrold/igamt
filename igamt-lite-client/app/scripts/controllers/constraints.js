/**
 * Created by Jungyub on 4/01/15.
 */

angular.module('igl').controller('ConstraintsListCtrl',function($scope, $rootScope, Restangular, $filter) {
	$scope.loading = false;
	$scope.segmentPredicates = [];
	$scope.tmpSegmentPredicates = [];
	$scope.segmentConformanceStatements = [];
	$scope.tmpSegmentConformanceStatements = [];
	$scope.datatypePredicates = [];
	$scope.tmpDatatypePredicates = [];
	$scope.datatypeConformanceStatements = [];
	$scope.tmpDatatypeConformanceStatements = [];
	$scope.init = function() {
		$scope.loading = true;
		$scope.loading = false;
	};
	
	$scope.getSegmentPredicates = function() {
		for(var i=0, len1 = $rootScope.segments.length; i < len1; i++){
			for(var j=0, len2 = $rootScope.segments[i].predicates.length; j < len2; j++){
				var cp = angular.fromJson({
					position : $rootScope.segments[i].name + '-' + $rootScope.segments[i].predicates[j].constraintTarget,
					description : $rootScope.segments[i].predicates[j].description,
					trueUsage : $rootScope.segments[i].predicates[j].trueUsage,
					falseUsage : $rootScope.segments[i].predicates[j].falseUsage
				});
				
				$scope.segmentPredicates.push(cp);
			}
		}
		$scope.tmpSegmentPredicates = [].concat($scope.segmentPredicates);
		return $scope.segmentPredicates;
	}
	
});