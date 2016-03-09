/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('DataTypeLibrarySvc', function($http, ngTreetableParams, userInfoService) {
	
	var svc = this;
	
	var dtLib = {};
	
	svc.getDTLib = function(scope) {
		return new ngTreetableParams({
			getNodes : function(parent) {
				return dtLib(scope);
			},
	        getTemplate : function(node) {
	            return 'dataTypeNode.html';
	        },
	        options : {
	            onNodeExpand: function() {
	                console.log('A node was expanded!');
	            }
	        }
		});
	};
	
	function dtLib(scope) {
		console.log("dtLib scope=" + JSON.stringify(scope));
		var dtlrw = {
			"scope" : scope,
			"accountId" : userInfoService.getAccountID(),
			"dtLib" : svc.dtLib
		}
		return $http.post(
				'api/datatype-library', dtlrw)
				.then(function(response) {
					svc.dtLib = response.data;
				return angular.fromJson(dtLib.children)});
	};

	svc.save = function(dtLib) {
		return $http.post(
				'api/datatype-library', dtLib).then(function(response) {
				return angular.fromJson(response.data.children)});
	};
	
	return svc;
});