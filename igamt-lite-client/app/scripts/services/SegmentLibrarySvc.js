/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('SegmentLibrarySvc', function($http, userInfoService) {

	var svc = this;

	var segLibStruct = function(scope, children) {
		this.id = null;
		this.scope = scope;
	    this.sectionDescription = null;
	    this.sectionContents = null;
		this.children = children;
	};

  svc.getHL7Versions = function() {
		return $http.get(
				'api/segment-library/findHl7Versions')
				.then(function(response) {
//					console.log("response" + JSON.stringify(response));
					return angular.fromJson(response.data);
				});
  };

	svc.getSegmentLibraryByScopes = function(scopes) {
		console.log("segment-library/findByScopes scopes=" + scopes);
        return $http.post(
            'api/segment-library/findByScopes', angular.toJson(scopes))
            .then(function(response) {
    					console.log("getSegmentLibraryByScopes response=" + response.data.length);
              return angular.fromJson(response.data);
            });
	};

	svc.getSegmentLibraryByScopesAndVersion = function(scopes, hl7Version) {
		console.log("segment-library/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
        var scopesAndVersion = {
          "scopes" : scopes,
          "hl7Version" : hl7Version
        };
        return $http.post(
            'api/segment-library/findByScopesAndVersion', angular.toJson(scopesAndVersion))
            .then(function(response) {
     					console.log("getSegmentLibraryByScopesAndVersion response size=" + response.data.length);
//   					  console.log("getSegmentLibraryByScopesAndVersion response=" + JSON.stringify(response.data));
              return angular.fromJson(response.data);
            });
	};

  svc.getSegmentsByLibrary = function(dtLibId) {
        return $http.get(
            'api/segment-library/' + dtLibId + '/segments')
            .then(function(response) {
    //					console.log("response" + JSON.stringify(response));
              return angular.fromJson(response.data);
            });
  }

	svc.append = function(fromchildren, toChildren) {
		angular.forEach(fromchildren, function(child) {
			toChildren.push(child);
		});
		return svc.segmentLibrary;
	};

	svc.createUpdate = function(scope, children) {
		var dtlrw = new segLibStruct(scope, children);
	};

	svc.create = function(hl7Version, scope, name, ext) {
    var dtlcw = { "hl7Version" : hl7Version,
                  "scope" : scope,
                  "name" : name,
                  "ext" : ext,
                  "accountId" : userInfoService.getAccountID()};
		return $http.post(
			'api/segment-library/create', dtlcw).then(function(response) {
			return angular.fromJson(response.data)});
	};

	svc.save = function(segmentLibrary) {
		return $http.post(
			'api/segment-library/save', angular.toJson(segmentLibrary)).then(function(response) {
			return angular.fromJson(response.data)});
	};

	return svc;
});
