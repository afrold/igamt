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
			    	profileAsString = JSON.stringify(getJSONFixture('profile-2.7.json'))
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
	
//	it("Can we get messages?", function () {
//		var toc = ToCSvc.getToC(profile);
//		var msgs = CloneDeleteMessageSvc.getMessages(toc);
//		expect(msgs).toBeDefined();
// 		expect(msgs.children.length).toBe(193);
// 		var msg = msgs.children[0];
//		expect(msg).toBeDefined();
//		expect(msg.reference.type).toBe("message");
//	});
	
	it("Can we delete a message?", function () {
		// A delete removes a message by splicing it out of two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bMsgCount = profile.messages.children.length;
		var bSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);

		// Second we do the delete.
		CloneDeleteMessageSvc.deleteMessage(profile, profile.messages.children[6]);
		
		// Third we re-take the profile and record the length of its messages.
		var aMsgCount = profile.messages.children.length;
		var aSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);
		
		// Fourth we check our counts.
		expect(bMsgCount).toBe(aMsgCount +1);
	});
	
	it("If we delete all messages will we also delete all segments?", function() {
		var bMsgCount = profile.messages.children.length;
		var bSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);
		var bSegIds = ProfileAccessSvc.Segments(profile).getAllSegmentIds();
		
//		_.eachRight(profile.messages.children, function(message) {
//			CloneDeleteMessageSvc.deleteMessage(profile, message);
//		});

		var aMsgCount = profile.messages.children.length;
		var aSegCount = ProfileAccessSvc.Messages(profile).getAllSegmentRefs(profile.messages.children);
		expect(aMsgCount).toBe(0);
		expect(aSegCount).toBe(0);
	});
	
//	it("Can we find an index?", function() {
//		var toc = ToCSvc.getToC(profile);
//		var msgs = CloneDeleteMessageSvc.getMessages(toc);
//		
//		// if this index cannot be found, it might be because the test data got regenerated resulting in fresh ids.
//		var id = "5665cee2d4c613e7b531be4e";
//		var idx = CloneDeleteMessageSvc.findMessageIndex(msgs, id);		
//		expect(idx).toBeDefined();
//	});
});
