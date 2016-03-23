/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('DatatypeLibrarySvc', function($http, ngTreetableParams, userInfoService) {
	
	var svc = this;
	
	var dtLibStruct = function(scope, children) {
		this.id = null;
		this.scope = scope;
	    this.sectionDescription = null;
	    this.sectionContents = null;
		this.children = children;
	};
	
	svc.getDataTypeLibrary = function(scope) {
		console.log("datatype-library/getDataTypeLibraryByScope scope=" + JSON.stringify(scope));
		var param = angular.toJson(scope);
		return $http.post(
				'api/datatype-library/getDataTypeLibraryByScope', scope)
				.then(function(response) {
					console.log("response" + JSON.stringify(response));
					var datatypes = angular.fromJson(response.data);
					var sortedChildren = _.sortBy(datatypes.children, function(child) { return child.name; });
					datatypes.children = sortedChildren;
					return new dtLibStruct(scope, sortedChildren);
				});
	};
	
	svc.assembleDatatypeLibrary = function(datatypeStruct) {
		return new ngTreetableParams({
			getNodes : function(parent) {
				return datatypeStruct.children;
			},
	        getTemplate : function(node) {
	            return 'dataTypeNode.html';
	        }
		});
	};
	
	svc.append = function(fromchildren, toChildren) {
		angular.foreach(fromchildren, function(child) {
			toChildren.push(child);
		});
		return svc.datatypeLibrary;
	};

	svc.createUpdate = function(scope, children) {
		var dtlrw = new dtLibStruct(scope, sortedChildren);
	};
	
	svc.save = function(datatypeLibrary) {
		return $http.post(
			'api/datatype-library/save', datatypeLibrary).then(function(response) {
			return angular.fromJson(response.data.children)});
	};
	
	return svc;
});