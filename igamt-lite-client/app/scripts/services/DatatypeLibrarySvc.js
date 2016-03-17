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
		console.log("datatypeLibrary scope=" + JSON.stringify(scope));
		return $http.post(
				'api/datatype-library/getDatatypeLibraryByScope', scope)
				.then(function(response) {
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
	        },
	        options : {
	            onNodeExpand: function() {
	                console.log('A node was expanded!');
	            }
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