'use strict';

describe("delete message service", function () {
	
	var CloneDeleteSvc;
	var ProfileAccessSvc;
	var ToCSvc;
	var i18n;
	var userInfoService;
	var scope;
	var rootScope;
	var httpBackend;
	var location;
	var filter;
	var base64;
	var http;
	var httpBackend;
	var modal;
	var Restangular;
	var controller;
	var igdocumentAsString;
	var igdocument;
	var ctrl;
	var Idle;
	
	beforeEach(function() {
		module('igl');
		inject(function (_CloneDeleteSvc_, _ProfileAccessSvc_, _ToCSvc_, _i18n_, _userInfoService_, $modal, _Restangular_, $filter, _base64_, $http, $httpBackend, _Idle_, $location, $rootScope, $controller) {
			CloneDeleteSvc = _CloneDeleteSvc_;
			ProfileAccessSvc = _ProfileAccessSvc_;
			ToCSvc = _ToCSvc_;
			i18n = _i18n_;
			userInfoService = _userInfoService_;
			scope = $rootScope.$new();
			rootScope = $rootScope;
			location = $location;
			filter = $filter;
			base64 = _base64_;
			http = $http;
			modal = $modal;
			Restangular = _Restangular_;
			httpBackend = $httpBackend;
			controller = $controller;
			Idle = _Idle_;
			 
// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.			 
			 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/igdocument/';
			 	var jsonFixture = getJSONFixture('igdocument-2.7.json');
	    			igdocumentAsString = JSON.stringify(jsonFixture);
			 	expect($rootScope).toBeDefined();
			 	expect(igdocumentAsString).toBeDefined();
		});
		ctrl = controller('MainCtrl', {
			$scope : scope, 
			$rootScope : rootScope, 
			i18n : i18n, 
			$location : location, 
			userInfoService : userInfoService, 
			$modal : modal,
			Restangular : Restangular,
			$filter : filter,
			base64 : base64,
			$http : http,
			Idle : Idle
		});
	 	expect(ctrl).toBeDefined();
		// We want a pristine profile for each test so state changes from one test don't pollute
		// the others.
		igdocument = JSON.parse(igdocumentAsString);
	 	expect(igdocument).toBeDefined();
		rootScope.igdocument = igdocument;
	});
	
    it('Can we clone a datatype', function () {
	 	expect(igdocument).toBeDefined();
    		var datatypes = igdocument.profile.datatypes.children;
    		var bCount = igdocument.profile.datatypes.children.length;
    		var SUT = datatypes[4];
        CloneDeleteSvc.cloneDatatypeFlavor(SUT);
		var aCount = igdocument.profile.datatypes.children.length;
		expect(bCount).toBe(aCount -1);
    });
    	
    it('Can we clone a value set', function () {
	 	expect(igdocument).toBeDefined();
    		var valueSets = igdocument.profile.tables.children;
    		var bCount = igdocument.profile.tables.children.length;
    		var SUT = valueSets[4];
        CloneDeleteSvc.cloneTableFlavor(SUT);
		var aCount = igdocument.profile.tables.children.length;
		expect(bCount).toBe(aCount -1);
    });
    
	it("Can we clone a message?", function() {
		// A clone duplicates a message then splices it into two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bCount = igdocument.profile.messages.children.length;
		
		// Second we extract the toc and record the length of its messages.
		var toc = ToCSvc.getToC(igdocument);
		var msgs = CloneDeleteSvc.getMessages(toc);
		
		// Third we do the clone.
		var newMsg = CloneDeleteSvc.cloneMessage(igdocument, msgs.children[4]);
		
		// Fourth we check our counts and undefineds
		expect(igdocument.profile.messages.children.length).toBe(bCount +1);
		expect(newMsg).toBeDefined();
		expect(newMsg.reference.id).toBeDefined();
		expect(msg.id).not.toBe(newMsg.reference.id)
	});
	
	it("Can we delete a message?", function () {
		// A delete removes a message by splicing it out of two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bMsgCount = igdocument.profile.messages.children.length;
		var bSegCount = ProfileAccessSvc.Messages(igdocument.profile).getAllSegmentRefs(igdocument.profile.messages.children);

		// Second we do the delete.
		CloneDeleteSvc.deleteMessage(igdocument, igdocument.profile.messages.children[4]);
		
		// Third we re-take the profile and record the length of its messages.
		var aMsgCount = igdocument.profile.messages.children.length;
		var aSegCount = ProfileAccessSvc.Messages(igdocument.profile).getAllSegmentRefs(igdocument.profile.messages.children);
		
		// Fourth we check our counts.
		expect(bMsgCount).toBe(aMsgCount +1);
	});
	
	it("If we delete all messages will we also delete all segs, dts, and vss?", function() {
		var bDtCount =  ProfileAccessSvc.Datatypes(igdocument.profile).datatypes().length;
		var bVsCount =  ProfileAccessSvc.ValueSets(igdocument.profile).valueSets().length;
		
		var i = 0;
		_.eachRight(igdocument.profile.messages.children, function(message) {
//			console.log("If we delete all messages will we also delete all segs, dts, and vss? = " + (i++) + " msgId=" + message.id + " name=" + message.name + " - " + message.description);
			CloneDeleteSvc.deleteMessage(igdocument, message);
		});

		var aMsgCount =  ProfileAccessSvc.Messages(igdocument.profile).messages().length;
		var aSegCount =  ProfileAccessSvc.Segments(igdocument.profile).segments().length;
		var aDtCount =  ProfileAccessSvc.Datatypes(igdocument.profile).datatypes().length;
		var aVsCount =  ProfileAccessSvc.ValueSets(igdocument.profile).valueSets().length;
		
		expect(aMsgCount).toBe(0);
		expect(aSegCount).toBe(0);
		expect(aDtCount < bDtCount).toBe(true);
		expect(aVsCount < bVsCount).toBe(true);
	});

});
