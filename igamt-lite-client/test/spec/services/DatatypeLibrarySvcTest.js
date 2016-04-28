use strict';

describe("datatype library service", function () {
	// gcr This test is not ready for IGDocument.
	var DatatypeLibrarySvc;
	var datatypesAsString;
	var library;

	beforeEach(function() {
		module('igl');
		inject(function (_DatatypeLibrarySvc_, $injector, $rootScope, $controller, $httpBackend,         ,
        ngMockE2E) {
			DatatypeLibrarySvc = _DatatypeLibrarySvc_;

// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.
		 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/datatypes/';
		 	expect($rootScope).toBeDefined();
		});
		// We want a pristine document for each test so state changes from one test don't pollute
		// the others.
		library = JSON.parse(datatypesAsString);
	});

	it("Do we have an Introduction?", function () {
		$http
	};
}
