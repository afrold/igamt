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
			
			for(var j=0, len2 = $rootScope.segments[i].conformanceStatements.length; j < len2; j++){
				var cs = angular.fromJson({
					id : $rootScope.segments[i].conformanceStatements[j].constraintId,
					position : $rootScope.segments[i].name + '-' + $rootScope.segments[i].conformanceStatements[j].constraintTarget,
					description : $rootScope.segments[i].conformanceStatements[j].description
				});
				
				$scope.segmentConformanceStatements.push(cs);
			}
		}
		
		for(var i=0, len1 = $rootScope.datatypes.length; i < len1; i++){
			for(var j=0, len2 = $rootScope.datatypes[i].predicates.length; j < len2; j++){
				var cp = angular.fromJson({
					position : $rootScope.datatypes[i].name + '-' + $rootScope.datatypes[i].predicates[j].constraintTarget,
					description : $rootScope.datatypes[i].predicates[j].description,
					trueUsage : $rootScope.datatypes[i].predicates[j].trueUsage,
					falseUsage : $rootScope.datatypes[i].predicates[j].falseUsage
				});
				
				$scope.datatypePredicates.push(cp);
			}
			
			for(var j=0, len2 = $rootScope.datatypes[i].conformanceStatements.length; j < len2; j++){
				var cs = angular.fromJson({
					id : $rootScope.datatypes[i].conformanceStatements[j].constraintId,
					position : $rootScope.datatypes[i].name + '-' + $rootScope.datatypes[i].conformanceStatements[j].constraintTarget,
					description : $rootScope.datatypes[i].conformanceStatements[j].description
				});
				
				$scope.datatypeConformanceStatements.push(cs);
			}
		}
		
		$scope.tmpSegmentPredicates = [].concat($scope.segmentPredicates);
		$scope.tmpSegmentConformanceStatements = [].concat($scope.segmentConformanceStatements);
		$scope.tmpDatatypePredicates = [].concat($scope.datatypePredicates);
		$scope.tmpDatatypeConformanceStatements = [].concat($scope.datatypeConformanceStatements);
		
		
		$scope.loading = false;
	};
});