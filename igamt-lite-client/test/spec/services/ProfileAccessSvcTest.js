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
		expect(profile).toBeDefined();
	});
	
	it("Do we get all message ids?", function() {
		var cntMessages = profile.messages.children.length;
		var cntIds = ProfileAccessSvc.Messages(profile).getMessageIds().length;
		expect(cntMessages).toBe(cntIds);
	});

	it("Check version", function () {
		expect(ProfileAccessSvc.Version(profile)).toBe("2.7");
	});

	it("Are we fetching segments?", function() {
		var messages = profile.messages.children
//		console.log("messages=" + messages.length);
		
		// To test our function we must have input of known state.
		var message = messages[6];
//		console.log("message.children=" + message.children.length);

		// Find anything at the top level that is a group.
		var groups = _.where(message.children, {'type' : 'group'});
//		console.log("groups=" + groups.length);
		// Find anything at the top level that is a segmentRef.
		var segments = _.where(message.children, {'type' : 'segmentRef'});
//		console.log("segmentRefs=" + segmentRefs.length);
		
		// We know there's only one group and it should contain 9 segmentRefs.
		var rvalG = ProfileAccessSvc.Messages(profile).fetchSegmentRefs(groups[0]);
//		console.log("rvalG=" + rvalG.length);
		expect(rvalG.length).toBe(9);

		// If we pass in 1 segmentRef we should only get 1 back and it should be the same one.
		var rvalS = ProfileAccessSvc.Messages(profile).fetchSegmentRefs(segments[0]);
		console.log("segmentId=" + segments[0].id);
		console.log("rvalS=" + rvalS);
		expect(rvalS.length).toBe(1);
		expect(rvalS[0]).toBe(segments[0].id);
		// It should also be findable in the segments collection.
		var segments1 = ProfileAccessSvc.Segments(profile).findByIds(rvalS);
		expect(segments1).toBeDefined();
		var segments2 = ProfileAccessSvc.Segments(profile).findByIds(rvalG);
		expect(segments1).toBeDefined();
		
		// Let's get all segmentRefs in the message.
		var rvalA = ProfileAccessSvc.Messages(profile).getSegmentRefs(message);
		expect(rvalA.length).toBe(15);
		
		// Lets get all segmentRefs in all messages in the profile.
		var rvalAA = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(messages);
		expect(rvalAA.length).toBe(3507);
//		console.log("rvalAA=" + rvalAA.length);
	});
	
	it("Are the segmentRefs fetched valid?", function () {
		var message = profile.messages.children[6];
		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
		var segments = ProfileAccessSvc.Segments(profile).findByIds(msgSegRefs);
		expect(segments).toBeDefined();
	});
	
	it("Can we find the living and the dead?", function() {
		var msgDead = [];
		msgDead.push(profile.messages.children[6]);
		var msgLive = profile.messages.children;
		
		var bLiveSize = msgLive.length;
		var idxP = _.findIndex(msgLive, function(
				child) {
			return child.id === msgDead.id;
		})
		msgLive.splice(idxP, 1);
		var aLiveSize = msgLive.length;
		expect(bLiveSize).toBe(aLiveSize + 1);

		var segmentRefsLive = ProfileAccessSvc.Messages(profile)
		.getAllSegmentRefs(msgLive);

		var segmentRefsDead = ProfileAccessSvc.Messages(profile)
		.getAllSegmentRefs(msgDead);
		
		var segments = ProfileAccessSvc.Segments(profile).segments();

		var segmentRefsReallyDead = ProfileAccessSvc.Segments(profile).removeDead(segmentRefsLive, segmentRefsDead);
		expect(segmentRefsReallyDead.length).toBe(166);
		
		segmentRefsReallyDead = ProfileAccessSvc.Segments(profile).removeDead(segmentRefsLive, segmentRefsDead);
		expect(segmentRefsReallyDead.length).toBe(166);
			
//		console.log("segments=" + segments.length);
//		console.log("msgLive=" + msgLive.length);
//		console.log("msgDead=" + msgDead.length);
//		console.log("segmentRefsLive=" + segmentRefsLive.length);
//		console.log("segmentRefsDead=" + segmentRefsDead.length);
//		console.log("segmentRefsReallyDead=" + segmentRefsReallyDead.length);
//		console.log("_.intersection(segmentRefsLive, segmentRefsDead)=" + _.intersection(segmentRefsLive, segmentRefsDead).length);
//		console.log("_.difference(segmentRefsLive, segmentRefsDead)=" + _.difference(segmentRefsDead, segmentRefsLive).length);

		segmentRefsDead.push('aaa');
//		console.log("segmentRefsDead=" + segmentRefsDead.length);
//		console.log("_.difference(segmentRefsLive, segmentRefsDead)=" + _.difference(segmentRefsDead, segmentRefsLive).length);
		segmentRefsReallyDead = ProfileAccessSvc.Segments(profile).removeDead(segmentRefsLive, segmentRefsDead);
		expect(segmentRefsReallyDead.length).toBe(166);
	});
	
	it("Can we find all datatypes in a segment(s)?", function() {
		var segments = ProfileAccessSvc.Segments(profile).segments();
		expect(segments.length).toBe(166);
		var dts0 = ProfileAccessSvc.Segments(profile).findDatatypesFromSegment(segments[0]);
		expect(dts0.length).toBe(4);
		
		var dts3 = ProfileAccessSvc.Segments(profile).findDatatypesFromSegment(segments[3]);
		expect(dts3.length).toBe(15);
		expect(_.intersection(dts0, dts3).length).toBe(dts0.length);
		
		var segRefs = [segments[0].id, segments[3].id];
		var dts03 = ProfileAccessSvc.Segments(profile).findDatatypesFromSegmentRefs(segRefs);
		expect(dts03.length).toBe(15);
		
		var segRefs = ProfileAccessSvc.Segments(profile).getAllSegmentIds();
		var dtsAll = ProfileAccessSvc.Segments(profile).findDatatypesFromSegmentRefs(segRefs);
		var dtsAll1 = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
		expect(_.difference(dtsAll1, dtsAll).length).toBe(dtsAll1.length - dtsAll.length);
		
		expect(dtsAll1.length).toBe(ProfileAccessSvc.Datatypes(profile).datatypes.length);
	});
	
	it("Can we find all value sets in a datatype(s)?", function() {
		var dtIds = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
		var datatypes = ProfileAccessSvc.Datatypes(profile).datatypes;
		expect(dtIds.length).toBe(datatypes.length);
		var vsIds = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtIds);
		var vsIds1 = ProfileAccessSvc.ValueSets(profile).getAllValueSetIds();
//		console.log("vsIds=" + JSON.stringify(vsIds, null, 2));
		expect(_.difference(vsIds1, vsIds).length).toBe(vsIds1.length - vsIds.length);
	});

	it("Are we getting all Message Segments?", function () {
		var segRefs = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);
		expect(segRefs).toBeDefined();
		expect(segRefs.length).toBe(3507);
	});
	
//	it("Can we find segments from a collection of a message's segmentRefs?", function () {
//		var msg = profile.messages.children[4];
//		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
//		var segments = ProfileAccessSvc.Segments(profile).findByIds(msgSegRefs);
//		expect(segments.length).toBe(msgSegRefs.length);
//		expect(segments[0]).toBeDefined();
//	});
	
//	it("Can we find a segment from its id?", function() {
//		var segRefs = ProfileAccessSvc.Messages(profile).getAllSegmentRefs([profile.messages.children[6]]);
//		var segIds = ProfileAccessSvc.Segments(profile).getAllSegmentIds();
//		expect(segRefs).toBeDefined();
//		expect(segIds).toBeDefined();
//		console.log(segRefs.length);
//		console.log(segIds.length);
//		console.log(segRefs.sort());
//		console.log(segIds.sort());
//		expect(_.intersection(segRefs, segIds).length).toBe(segRefs.length);
//		var segment = ProfileAccessSvc.Segments(profile).findById(segRefs[0]);
//		expect(segment).toBeDefined();
//	});
	
//	it("Are we getting all Segments?", function () {
//		expect(ProfileAccessSvc.Segments(profile).segments().length).toBe(166);
//	});
//
//	it("Can we find a segment?", function () {
//		var id = ProfileAccessSvc.Segments(profile).segments()[122].id;
//		expect(id).toBe(ProfileAccessSvc.Segments(profile).findById(id).id);
//	});
//
//	it("Can we find a segment from a message's segmentRefs?", function () {
//		var msg = profile.messages.children[4];
//		expect(msg).toBeDefined();
//		expect(msg.children).toBeDefined();
//		var msgSegRefs = ProfileAccessSvc.Messages(profile).getSegmentRefs(msg);
//		var segIds = _.pluck(ProfileAccessSvc.Segments(profile).segments(), "id");
//		expect(segIds.length).toBe(166);
//		_.each(msgSegRefs, function(id) {
//			expect(_.contains(segIds, id)).toBe(true);
//		});
//	});
//	
//	it("Can we find segments from a collection of segmentRefs?", function () {
//		var ids =  [];
//		var segments = ProfileAccessSvc.Segments(profile).segments();
//		ids.push(segments[0].id);
//		ids.push(segments[122].id);
//		ids.push(segments[165].id);
//		expect(ProfileAccessSvc.Segments(profile).findByIds(ids).length).toBe(3);
//	});
//	
//	
//	it("Can we find dead segments?", function () {
//		var ids =  [];
//		var segments = ProfileAccessSvc.Segments(profile).segments();
//		ids.push(segments[0].id);
//		ids.push(segments[122].id);
//		ids.push(segments[165].id);
//		var dead = ProfileAccessSvc.Segments(profile).findDead(ids);
//		expect(dead.length).toBe(163);
//	});
//
//	it("Can we remove the dead segments?", function () {
//		var ids =  [];
//		var segments = ProfileAccessSvc.Segments(profile).segments();
//		ids.push(segments[0].id);
//		ids.push(segments[122].id);
//		ids.push(segments[165].id);
//		var live = ProfileAccessSvc.Segments(profile).removeDead(ids);
//		expect(live.length).toBe(3);
//	});
//	
//	it("Can we find a datatype by name?", function () {
//		var name = ProfileAccessSvc.Datatypes(profile).datatypes[67].name;
//		expect(name).toBe(ProfileAccessSvc.Datatypes(profile).findByName(name).name);
//	});
//
//	it("Can we find dead datatypes?", function () {
//		var names =  [];
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[0].name);
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[31].name);
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[85].name);
//		expect(ProfileAccessSvc.Datatypes(profile).findDead(names).length).toBe(83);
//	});
//
//	it("Can we remove dead datatypes?", function () {
//		var names =  [];
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[0].name);
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[31].name);
//		names.push(ProfileAccessSvc.Datatypes(profile).datatypes[85].name);
//		expect(ProfileAccessSvc.Datatypes(profile).removeDead(names).length).toBe(3);
//	});
});