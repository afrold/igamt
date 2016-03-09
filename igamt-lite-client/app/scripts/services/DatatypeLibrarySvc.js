/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('DatatypeLibrarySvc', function($http, ngTreetableParams, userInfoService) {
	
	var svc = this;
	
	svc.datatypeLibrary = {};
	
	svc.getDataTypeLibrary = function(scope) {
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
		console.log("datatypeLibrary scope=" + JSON.stringify(scope));
			var dtlrw = {
				"scope" : scope,
				"accountId" : userInfoService.getAccountID(),
				"dtLib" : svc.datatypeLibrary
			}
			return $http.post(
					'api/datatype-library', dtlrw)
					.then(function(response) {
						var datatypeLibrary = angular.fromJson(response.data)
						var sortedChildren = _.sortBy(datatypeLibrary.children, function(child) { return child.name; });
						svc.datatypeLibrary.children = sortedChildren;
						return svc.datatypeLibrary.children;
					});
	};

	svc.save = function(datatypeLibrary) {
		return $http.post(
			'api/datatype-library/save', datatypeLibrary).then(function(response) {
			return angular.fromJson(response.data.children)});
	};
	
	return svc;
});