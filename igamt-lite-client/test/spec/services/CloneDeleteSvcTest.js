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
			 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/igDocuments/';
			 	var jsonFixture = getJSONFixture('igDocument-2.7-HL7STD.json');
//			 	var jsonFixture = getJSONFixture('igdocument-2.7.5-USER-1.0.json');
	    			igdocumentAsString = JSON.stringify(jsonFixture);
			 	expect($rootScope).toBeDefined();
				$rootScope.jsonFixture = jsonFixture;
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

//		expect(bCount).toBe(aCount -1);
    });
    	
    it('Can we clone a value set', function () {
	 	expect(igdocument).toBeDefined();
    		var valueSets = igdocument.profile.tables.children;
    		var bCount = igdocument.profile.tables.children.length;
    		var SUT = valueSets[4];
        CloneDeleteSvc.cloneTableFlavor(SUT);
		var aCount = igdocument.profile.tables.children.length;
//		expect(bCount).toBe(aCount -1);
    });
    
	it("Can we clone a message?", function() {
		// A clone duplicates a message then splices it into two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// We take the profile and record the length of its messages.
		var bCount = igdocument.profile.messages.children.length;
		
		// We do the clone.
		var msg = igdocument.profile.messages.children[4];
		var newMsg = CloneDeleteSvc.cloneMessage(msg);
		
		// We check our counts and undefineds
		expect(igdocument.profile.messages.children.length).toBe(bCount +1);
		expect(newMsg).toBeDefined();
		expect(newMsg.id).toBeDefined();
		expect(msg.id).not.toBe(newMsg.id)
		var grps = ProfileAccessSvc.Messages().getGroups(msg);
		var newGrps = ProfileAccessSvc.Messages().getGroups(newMsg);
		var found = false;
		_.each(newGrps, function(newGrp){
			found = (found || _includes(grps, newGrp.id));
		});
		expect(found).toBe(false);
	});
	
	it("Can we delete a message?", function () {
		// A delete removes a message by splicing it out of two arrays: (1) profile and (2) toc.
		// Here were going to compare the lengths of these arrays before and after.
		
		// First we take the profile and record the length of its messages.
		var bMsgCount = igdocument.profile.messages.children.length;
		var bSegCount = ProfileAccessSvc.Messages().getAllSegmentRefs(igdocument.profile.messages.children);

		// Second we do the delete.
		CloneDeleteSvc.deleteMessage(igdocument.profile.messages.children[4]);
		
		// Third we re-take the profile and record the length of its messages.
		var aMsgCount = igdocument.profile.messages.children.length;
		var aSegCount = ProfileAccessSvc.Messages().getAllSegmentRefs(igdocument.profile.messages.children);
		
		// Fourth we check our counts.
		expect(bMsgCount).toBe(aMsgCount +1);
	});
	
	it("If we delete all messages will we also delete all segs, dts, and vss?", function() {
		var bDtCount =  ProfileAccessSvc.Datatypes().datatypes().length;
		var bVsCount =  ProfileAccessSvc.ValueSets().valueSets().length;
		
//		console.log("svc.deleteMessage: bMsgs=" + ProfileAccessSvc.Messages().messages().length);
//		console.log("svc.deleteMessage: bSegs=" + ProfileAccessSvc.Segments().segments().length);
//		console.log("svc.deleteMessage: bDts=" + ProfileAccessSvc.Datatypes().datatypes().length);
//		console.log("svc.deleteMessage: bVss=" + ProfileAccessSvc.ValueSets().valueSets().length);
		var i = 0;
		_.eachRight(igdocument.profile.messages.children, function(message) {
//			console.log("If we delete all messages will we also delete all segs, dts, and vss? = " + (i++) + " msgId=" + message.id + " name=" + message.name + " - " + message.description);
			CloneDeleteSvc.deleteMessage(message);
		});

		var aMsgCount =  ProfileAccessSvc.Messages().messages().length;
		var aSegCount =  ProfileAccessSvc.Segments().segments().length;
		var aDtCount =  ProfileAccessSvc.Datatypes().datatypes().length;
		var aVsCount =  ProfileAccessSvc.ValueSets().valueSets().length;
		
		expect(aMsgCount).toBe(0);
		expect(aSegCount).toBe(0);
		expect(aDtCount).toBe(0);
		expect(aVsCount).toBe(0);
	});

	it("Can we delete a segment?", function() {
		var segments = ProfileAccessSvc.Segments().segments();
		expect(segments).toBeDefined();
		var segment = segments[4];
		var bCount = ProfileAccessSvc.Segments().segments().length;
		CloneDeleteSvc.deleteSegment(segment);
		var aCount = ProfileAccessSvc.Segments().segments().length;
		expect(bCount).toBe(aCount +1);
	});

	it("Can we delete a datatype?", function() {
		var datatypes = ProfileAccessSvc.Datatypes().datatypes();
		expect(datatypes).toBeDefined();
		var datatype = datatypes[4];
		var bCount = ProfileAccessSvc.Datatypes().datatypes().length;
		CloneDeleteSvc.deleteDatatype(datatype);
		var aCount = ProfileAccessSvc.Datatypes().datatypes().length;
		expect(bCount).toBe(aCount +1);
	});

	it("Can we delete a valueSet?", function() {
		var valueSets = ProfileAccessSvc.ValueSets().valueSets();
		expect(valueSets).toBeDefined();
		var valueSet = valueSets[4];
		var bCount = ProfileAccessSvc.ValueSets().valueSets().length;
		CloneDeleteSvc.deleteValueSet(valueSet);
		var aCount = ProfileAccessSvc.ValueSets().valueSets().length;
		expect(bCount).toBe(aCount +1);
	});
});
