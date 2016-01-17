'use strict';

describe("delete message service", function () {
	
	var CloneDeleteMessageSvc;
	var ProfileAccessSvc;
	var ToCSvc;
	var $httpBackend;
	var profileAsString;
	var profile;
	
	beforeEach(function() {
		module('igl');
		inject(function (_CloneDeleteMessageSvc_, _ProfileAccessSvc_, _ToCSvc_, $injector, $rootScope, $controller) {
			CloneDeleteMessageSvc = _CloneDeleteMessageSvc_;
			ProfileAccessSvc = _ProfileAccessSvc_;
			ToCSvc = _ToCSvc_;
			$httpBackend = $injector.get('$httpBackend');
			 
// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.			 
			 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/profiles/';

			 	// Apparently, the URL that whenGet normally requires is not needed at this time.
			 	// We test here with version 2.7.
			 	// The following only loads our file once and not before each test.
			    $httpBackend.whenGET().respond(
			    	profileAsString = JSON.stringify(getJSONFixture('profile-2.7-HL7STANDARD-.json'))
//			    	profileAsString = JSON.stringify(getJSONFixture('profile-2.7.5.json'))
			    );
			    expect(profileAsString).toBeDefined();
		});
		// We want a pristine profile for each test so state changes from one test don't pollute
		// the others.
		profile = JSON.parse(profileAsString);
	});
	
	it("Can we clone a message?", function() {
		// A clone duplicates a message then splices it into two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bCount = profile.messages.children.length;
		
		// Second we extract the toc and record the length of its messages.
		var toc = ToCSvc.getToC(profile);
		var msgs = CloneDeleteMessageSvc.getMessages(toc);
		
		// Third we do the clone.
		var newMsg = CloneDeleteMessageSvc.cloneMessage(profile, msgs.children[4]);
		
		// Fourth we check our counts and undefineds
		expect(profile.messages.children.length).toBe(bCount +1);
		expect(newMsg).toBeDefined();
		expect(newMsg.reference.id).toBeDefined();
		expect(msg.id).not.toBe(newMsg.reference.id)
	});
	
	it("Can we delete a message?", function () {
		// A delete removes a message by splicing it out of two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bMsgCount = profile.messages.children.length;
		var bSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);

		// Second we do the delete.
		CloneDeleteMessageSvc.deleteMessage(profile, profile.messages.children[4]);
		
		// Third we re-take the profile and record the length of its messages.
		var aMsgCount = profile.messages.children.length;
		var aSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);
		
		// Fourth we check our counts.
		expect(bMsgCount).toBe(aMsgCount +1);
	});
	
	it("If we delete all messages will we also delete all segs, dts, and vss?", function() {
		var bMsgCount = ProfileAccessSvc.Messages(profile).messages().length;
		var bSegCount = ProfileAccessSvc.Segments(profile).segments().length;
		var bDtCount =  ProfileAccessSvc.Datatypes(profile).datatypes().length;
		var bVsCount =  ProfileAccessSvc.ValueSets(profile).valueSets().length;
		
		var i = 0;
		_.eachRight(profile.messages.children, function(message) {
			console.log("If we delete all messages will we also delete all segs, dts, and vss? = " + (i++) + " msgId=" + message.id + " name=" + message.name + " - " + message.description);
			CloneDeleteMessageSvc.deleteMessage(profile, message);
		});

		var aMsgCount =  ProfileAccessSvc.Messages(profile).messages().length;
		var aSegCount =  ProfileAccessSvc.Segments(profile).segments().length;
		var aDtCount =  ProfileAccessSvc.Datatypes(profile).datatypes().length;
		var aVsCount =  ProfileAccessSvc.ValueSets(profile).valueSets().length;
		
		expect(aMsgCount).toBe(0);
		expect(aSegCount).toBe(0);
		expect(aDtCount).toBe(0);
		expect(aVsCount).toBe(0);

//		var msgCount = profile.messages.children.length;
//		var segCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children).length;
//		var dtCount = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds().length;
//		var vsCount = ProfileAccessSvc.ValueSets(profile).getAllValueSetIds().length;
//		
//		expect(msgCount).toBe(0);
//		expect(segCount).toBe(0);
//		expect(dtCount).toBe(0);
//		expect(vsCount).toBe(0);
	});

});
