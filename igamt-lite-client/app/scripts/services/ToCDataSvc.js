angular.module('igl').factory ('ToCDataSvc', ['$http', function($http) {

	var svc = this;
	
	svc.getToCData = function() {
		var rval;
	    $http.get('api/profiles/toc', {timeout: 60000}).then(function (response) {
	    	rval = angular.fromJson(response.data);
	        $scope.loading = false;
	    }, function (error) {
	        $scope.loading = false;
	        $scope.error = "Failed to load the profiles";
	    });
	    return rval;
	};
	
	return svc;
}])