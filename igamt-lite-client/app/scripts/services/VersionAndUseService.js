angular.module('igl').factory('VersionAndUseService',
    function($rootScope, $http, $q) {
	
	var VersionAndUseService={
			findAll:function(){
				
			},
			findById:function(id){
				
			},
			findAllByIds:function(ids){

	                var delay = $q.defer();
	                $http.post('api/versionAndUse/findByIds', ids).then(function(response) {
	                    var datatypes = angular.fromJson(response.data);
	                    delay.resolve(datatypes);
	                }, function(error) {
	                    delay.reject(error);
	                });
	                return delay.promise;

				
			},
			update:function(info){
				
			},
			save:function(info){
            	console.log(info);
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/versionAndUse/save', info).then(function(response) {
                    delay.resolve(info);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			}
			
	};
	
	
	
	return VersionAndUseService;
});