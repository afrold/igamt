angular.module('igl').factory('ConfigurationService',
    function($rootScope, $http, $q) {
	
	var ConfigurationService={

			override:function(configuration){
                 var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();

                $http.post('api/ExportConfiguration/override', configuration).then(function(response) {
                         var conf= angular.fromJson(response.data);
                   delay.resolve(conf);

                 }, function(error) {
                     //console.log("DatatypeService.save error=" + error);
                     delay.reject(error);
                 });
                 return delay.promise;
				
			},
			restoreDefault:function(configuration){
                var delay = $q.defer();
            $http.post('api/ExportConfiguration/restoreDefault', configuration).then(function(response) {
                	console.log("resopense");
                 	console.log(response);
                     var conf= angular.fromJson(response.data);
                   delay.resolve(conf);
                 }, function(error) {
                     //console.log("DatatypeService.save error=" + error);
                     delay.reject(error);
                 });
                 return delay.promise;
			},
			
			findCurrent:function(type){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/ExportConfiguration/findCurrent',type).then(function(response) {
                    console.log(response);
                    var conf= angular.fromJson(response.data);
                    console.log(response)
                    delay.resolve(conf);

                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			}

			findFonts:function(){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/ExportConfiguration/findFonts').then(function(response) {
                    var fonts= angular.fromJson(response.data);
                    delay.resolve(conf);
                }, function(error) {
                    delay.reject(error);
                });
                return delay.promise;
			}
			
	};
	
	
	
	return ConfigurationService;
});
