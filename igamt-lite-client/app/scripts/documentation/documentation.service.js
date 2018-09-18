angular.module('igl').factory('DocumentationService',
    function($rootScope, $http, $q) {
	
	var DocumentationService={

			save:function(documentation){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();

                $http.post('api/documentations/save', documentation).then(function(response) {
                	console.log("resopense");
                	console.log(response);
                    delay.resolve(response);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			},
			delete:function(documentation){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();

                $http.post('api/documentations/delete', documentation).then(function(response) {
                	var dateUpdated = angular.fromJson(response);
                	delay.resolve(dateUpdated);
                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
				return delay.promise;
			},
			
			findAll:function(){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/documentations/findAll').then(function(response) {
                    var documentations = angular.fromJson(response.data);
                    delay.resolve(documentations);

                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			},

        reorder:function(orderList){
            var delay = $q.defer();
            $http.post('api/documentations/reorder',orderList).then(function(response) {
                var result = response.data;
                delay.resolve(result);
            }, function(error) {
                delay.reject(error);
            });
            return delay.promise;

        },

            findUserNotes:function(){
                var delay = $q.defer();
                //datatype.accountId = userInfoService.getAccountID();
                $http.post('api/documentations/findUserNotes').then(function(response) {
                    var documentations = angular.fromJson(response.data);
                    delay.resolve(documentations);

                }, function(error) {
                    //console.log("DatatypeService.save error=" + error);
                    delay.reject(error);
                });
                return delay.promise;
				
			}
			
	};
	
	
	
	return DocumentationService;
});