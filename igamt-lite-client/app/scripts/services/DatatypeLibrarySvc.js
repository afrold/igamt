/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('DatatypeLibrarySvc', function($http, $httpBackend, ngTreetableParams, userInfoService) {
	
	var svc = this;
	
	var dtLibStruct = function(scope, children) {
		this.id = null;
		this.scope = scope;
	    this.sectionDescription = null;
	    this.sectionContents = null;
		this.children = children;
	};
	
	svc.getDataTypeLibrary = function(scope) {
		console.log("datatype-library/getDataTypeLibraryByScope scope=" + scope);
		return $http.post(
				'api/datatype-library/getDataTypeLibraryByScope', scope)
				.then(function(response) {
//					console.log("response" + JSON.stringify(response));
					return angular.fromJson(response.data);
				});
	};
	
//	svc.assembleDatatypeLibrary = function(datatypeStruct) {
//		return new ngTreetableParams({
//			getNodes : function(parent) {
//				return _.sortBy(datatypeStruct.children, 'label');
//
//			},
//	        getTemplate : function(node) {
//	            return 'dataTypeNode.html';
//	        }
//		});
//	};
	
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