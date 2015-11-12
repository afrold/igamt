'use strict';

describe("profile access service", function () {
	
	var ProfileAccessSvc;
	var $httpBackend;
	var profileAsString;
	var profile;
	
	beforeEach(function() {
		module('igl');
		inject(function (_ProfileAccessSvc_, $injector, $rootScope, $controller) {
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

	it("Check version", function () {
		expect(ProfileAccessSvc.Version(profile)).toBe("2.7");
	});

	it("Are we getting all Segments?", function () {
		expect(ProfileAccessSvc.Segments(profile).segments().length).toBe(166);
	});

	it("Can we find a segment?", function () {
		var id = ProfileAccessSvc.Segments(profile).segments()[122].id;
		expect(id).toBe(ProfileAccessSvc.Segments(profile).findById(id).id);
	});

	it("Can we find a segment from a message's segmentRefs?", function () {
		var msg = profile.messages.children[4];
		expect(msg).toBeDefined();
		expect(msg.children).toBeDefined();
		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
		var segIds = _.pluck(ProfileAccessSvc.Segments(profile).segments(), "id");
		expect(segIds.length).toBe(166);
		_.each(msgSegRefs, function(id) {
			expect(_.contains(segIds, id)).toBe(true);
		});
	});
	
	it("Can we find segments from a collection of segmentRefs?", function () {
		var ids =  [];
		var segments = ProfileAccessSvc.Segments(profile).segments();
		ids.push(segments[0].id);
		ids.push(segments[122].id);
		ids.push(segments[165].id);
		expect(ProfileAccessSvc.Segments(profile).findByIds(ids).length).toBe(3);
	});
	
	it("Can we find segments from a collection of a message's segmentRefs?", function () {
		var msg = profile.messages.children[4];
		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
		var segments = ProfileAccessSvc.Segments(profile).findByIds(msgSegRefs);
		expect(segments.length).toBe(msgSegRefs.length);
		expect(segments[0]).toBeDefined();
	});
	
	it("Can we find dead segments?", function () {
		var ids =  [];
		var segments = ProfileAccessSvc.Segments(profile).segments();
		ids.push(segments[0].id);
		ids.push(segments[122].id);
		ids.push(segments[165].id);
		var dead = ProfileAccessSvc.Segments(profile).findDead(ids);
		expect(dead.length).toBe(163);
	});

	it("Can we remove the dead segments?", function () {
		var ids =  [];
		var segments = ProfileAccessSvc.Segments(profile).segments();
		ids.push(segments[0].id);
		ids.push(segments[122].id);
		ids.push(segments[165].id);
		var live = ProfileAccessSvc.Segments(profile).removeDead(ids);
		expect(live.length).toBe(3);
	});

	it("Are we getting all Message Segments?", function () {
		var segments = ProfileAccessSvc.Messages(profile).getAllSegmentRefs();
		expect(segments.length).toBe(3507);
	});
	
	it("Can we find a datatype by name?", function () {
		var name = ProfileAccessSvc.Datatypes(profile).datatypes[67].name;
		expect(name).toBe(ProfileAccessSvc.Datatypes(profile).findByName(name).name);
	});

	it("Can we find dead datatypes?", function () {
		var names =  [];
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[0].name);
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[31].name);
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[85].name);
		expect(ProfileAccessSvc.Datatypes(profile).findDead(names).length).toBe(83);
	});

	it("Can we remove dead datatypes?", function () {
		var names =  [];
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[0].name);
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[31].name);
		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[85].name);
		expect(ProfileAccessSvc.Datatypes(profile).removeDead(names).length).toBe(3);
	});

	it("Can we find fields from segmentRefs?", function () {
		var segs =  [];
		var segments = ProfileAccessSvc.Segments(profile).segments();
		expect(segments.length).toBe(166);
		segs.push(segments[0]);
		segs.push(segments[122]);
		segs.push(segments[165]);
		expect(segs[0]).toBeDefined();
		expect(segs[0].fields).toBeDefined();
		var flds = ProfileAccessSvc.Segments(profile).findFields(segs[0]).length;
		expect(flds).toBe(33);
		expect(segs[1]).toBeDefined();
		expect(segs[1].fields).toBeDefined();
		flds = ProfileAccessSvc.Segments(profile).findFields(segs[1]).length;
		expect(flds).toBe(12);
		expect(segs[2]).toBeDefined();
		expect(segs[2].fields).toBeDefined();
		flds = ProfileAccessSvc.Segments(profile).findFields(segs[2]).length;
		expect(flds).toBe(14);
	});	
	
	it("Do our message counts balance?", function () {
		var msg = profile.messages.children[4];
		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
		var sansSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefsSansOne(msg);
		var allSegRefs = ProfileAccessSvc.Messages(profile).getAllSegmentRefs();
		expect(msgSegRefs.length).toBe(33);
		expect(sansSegRefs.length).toBe(3474);
		expect(allSegRefs.length).toBe(3507);
		expect(sansSegRefs.length).toBe(allSegRefs.length - msgSegRefs.length);
	});
});