'use strict';

describe("delete message service", function () {
	
	var DeleteMessageSvc;
	var ProfileAccessSvc;
	var $httpBackend;
	var profileAsString;
	var profile;
	
	beforeEach(function() {
		module('igl');
		inject(function (_DeleteMessageSvc_, _ProfileAccessSvc_, $injector, $rootScope, $controller) {
			DeleteMessageSvc = _DeleteMessageSvc_;
			ProfileAccessSvc = _ProfileAccessSvc_;
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
	
	it("Can we delete a message?", function () {
		var msg = profile.messages.children[4];
		var segRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
		var segments = ProfileAccessSvc.Segments(profile).findByIds(segRefs);
		var datatypes = [];
		_.each(segments, function(segment) {
				_.each(ProfileAccessSvc.Segments(profile).findFields(segment), function(field) {
				datatypes.push(field.datatype);				
			});
		});
		DeleteMessageSvc().deleteMessage(profile, msg);
		expect(profile.messages.children.length).toBe(profile.messages.children.length -1);
		expect(segRefs.length).toBe(profile.messages.children.length -1);
	});

});
