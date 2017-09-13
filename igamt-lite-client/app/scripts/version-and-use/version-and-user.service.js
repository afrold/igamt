angular.module('igl').factory('VersionAndUseService',
    function($rootScope, $http, $q) {
	
	var VersionAndUseService={
			findAll:function(){
				
			},
			findById:function(id){
				console.log("loooking for "+id);
				 var delay = $q.defer();
	                    $http.get('api/versionAndUse/' + id).then(function(response) {
	                        var info = angular.fromJson(response.data);
	                        delay.resolve(info);
	                    }, function(error) {
	                        delay.reject(error);
	                    });
	                
	                return delay.promise;
				
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
                console.log("Saving");
                console.log(info);
                $http.post('api/versionAndUse/save', info).then(function(response) {
                	console.log("resopense");
                	console.log(response);
                    delay.resolve(info);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			},
			findAll:function(){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/versionAndUse/findAll').then(function(response) {
                    var versions = angular.fromJson(response.data);
                    delay.resolve(versions);

                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			}
			
	};
	
	
	
	return VersionAndUseService;
});