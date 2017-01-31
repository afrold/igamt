angular.module('igl').factory('DecisionService',
    function($rootScope, $http, $q) {
	
	var DecisionService={

			save:function(decision){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();

                $http.post('api/decisions/save', decision).then(function(response) {
                	console.log("resopense");
                	console.log(response);
                    delay.resolve(response);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			},
			findAll:function(){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/decisions/findAll').then(function(response) {
                    var decisions = angular.fromJson(response.data);
                    delay.resolve(decisions);

                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			}
			
	};
	
	
	
	return DecisionService;
});