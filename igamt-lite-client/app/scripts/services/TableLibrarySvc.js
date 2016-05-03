/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('TableLibrarySvc', function($http, $httpBackend, userInfoService) {

	var svc = this;

	var tableLibStruct = function(scope, children) {
		this.id = null;
		this.scope = scope;
	    this.sectionDescription = null;
	    this.sectionContents = null;
		this.children = children;
	};

  svc.getHL7Versions = function() {
		return $http.get(
				'api/table-library/findHl7Versions')
				.then(function(response) {
//					console.log("response" + JSON.stringify(response));
					return angular.fromJson(response.data);
				});
  };

	svc.getDataTypeLibraryByScopes = function(scopes) {
		console.log("table-library/findByScopes scopes=" + scopes);
        return $http.post(
            'api/table-library/findByScopes', angular.toJson(scopes))
            .then(function(response) {
    					console.log("getDataTypeLibraryByScopes response=" + response.data.length);
              return angular.fromJson(response.data);
            });
	};

	svc.getDataTypeLibraryByScopesAndVersion = function(scopes, hl7Version) {
		console.log("table-library/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
        var scopesAndVersion = {
          "scopes" : scopes,
          "hl7Version" : hl7Version
        };
        return $http.post(
            'api/table-library/findByScopesAndVersion', angular.toJson(scopesAndVersion))
            .then(function(response) {
     					console.log("getDataTypeLibraryByScopesAndVersion response size=" + response.data.length);
//   					  console.log("getDataTypeLibraryByScopesAndVersion response=" + JSON.stringify(response.data));
              return angular.fromJson(response.data);
            });
	};

  svc.getTablesByLibrary = function(tableLibId) {
        return $http.get(
            'api/table-library/' + tableLibId + '/tables')
            .then(function(response) {
    //					console.log("response" + JSON.stringify(response));
              return angular.fromJson(response.data);
            });
  }

	svc.append = function(fromchildren, toChildren) {
		angular.foreach(fromchildren, function(child) {
			toChildren.push(child);
		});
		return svc.tableLibrary;
	};

	svc.createUpdate = function(scope, children) {
		var tablelrw = new tableLibStruct(scope, sortedChildren);
	};

	svc.create = function(hl7Version, scope, name, ext) {
    var tablelcw = { "hl7Version" : hl7Version,
                  "scope" : scope,
                  "name" : name,
                  "ext" : ext,
                  "accountId" : userInfoService.getAccountID()};
		return $http.post(
			'api/table-library/create', tablelcw).then(function(response) {
			return angular.fromJson(response.data)});
	};

	svc.save = function(tableLibrary) {
		return $http.post(
			'api/table-library/save', angular.toJson(tableLibrary)).then(function(response) {
			return angular.fromJson(response.data)});
	};

	return svc;
});
