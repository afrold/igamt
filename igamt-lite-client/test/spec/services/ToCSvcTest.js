'use strict';

describe("toc service", function () {
	
	var ToCSvc;
	var $httpBackend;
	var profileAsString;
	var profile;
	
	beforeEach(function() {
		module('igl');
		inject(function (_ToCSvc_, $injector, $rootScope, $controller) {
			ToCSvc = _ToCSvc_;
			$httpBackend = $injector.get('$httpBackend');
			 
// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.			 
			 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/profiles/';

			 	// Apparently, the URL that whenGet normally requires is not needed at this time.
			 	// We test here with version 2.7.
			 	// The following only loads our file once and not before each test.
			    $httpBackend.whenGET().respond(
			    	profileAsString = JSON.stringify(getJSONFixture('profile-2.7.json'))
			    );
			    expect(profileAsString).toBeDefined();
		});
		// We want a pristine profile for each test so state changes from one test don't pollute
		// the others.
		profile = JSON.parse(profileAsString);
	});

	it("Do we have well formed entries?", function () {
		var entries = ToCSvc.getEntries(profile);
		expect(entries).toBeDefined();
		console.log(entries);
		expect(entries.length).toBe(4);
//		var segs = _.find(entries, function(entry){
//			entry.title == "Segments";
//		});
//		console.log(JSON.stringify(segs));
//		expect(segs.children.length).toBe(166);
	});
});
