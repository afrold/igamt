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

	it("Do we have metadata?", function () {
		expect(ToCSvc).toBeDefined();
		var rval = ToCSvc.getMetadata(profile.metaData);
		expect(rval).toBeDefined();
//		console.log(rval);
//		expect(_.contains(rval.children, "hl7Version")).toBeTruthy();
//		console.log(entries);
//		var rval = _.find(entries, function(entry){
//			entry.title == "Segments";
//		});
//		console.log(JSON.stringify(rval));
//		expect(rval.children.length).toBe(166);
	});
	
	it("Do we get a valid entry?", function() {
		var entry = ToCSvc.createEntry("AUT", "AUT");
		expect(entry).toBeDefined();
		expect(_.has(entry, 'label')).toBeTruthy();
		expect(_.property('label')(entry)).toBe("AUT");
		expect(_.has(entry, 'drag')).toBeTruthy();
		expect(_.property('drag')(entry)).toBe("AUT");
		expect(_.has(entry, 'drop')).toBeFalsy();
		expect(_.has(entry, 'children')).toBeFalsy();
	});
	
	it("Do we get valid entries?", function() {
		var entries = ToCSvc.createEntries("Messages", profile.messages.children);
		expect(entries).toBeDefined();
//		console.log("entries=" + JSON.stringify(entries));
	});
	
	it("Do we have valid datatypes?", function() {
		var label = "Datatypes";
		var rval = ToCSvc.getTopEntry(label, profile.datatypes);
//		console.log(JSON.stringify(rval));
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "drag")).toBeFalsy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBe(label);
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
//		console.log(JSON.stringify(children));
//		console.log(JSON.stringify(rval));
		expect(rval.children.length).toBeGreaterThan(0);
	});
	
	it("Do we have valid segments?", function() {
		var label = "Segments";
		var rval = ToCSvc.getTopEntry(label, profile.segments);
//		console.log(JSON.stringify(rval));
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "drag")).toBeFalsy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBe(label);
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
//		console.log(JSON.stringify(children));
//		console.log(JSON.stringify(rval));
		expect(rval.children.length).toBeGreaterThan(0);
	});
	
	it("Do we have valid messages?", function() {
		var label = "Messages";
		var rval = ToCSvc.getTopEntry(label, profile.messages);

		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "drag")).toBeFalsy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBe(label);
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
//		console.log(JSON.stringify(children));
//		console.log(JSON.stringify(rval));
		expect(rval.children.length).toBeGreaterThan(0);
	});
	
	it("Do we have valid valuesets?", function() {
		var label = "Value Sets";
		var rval = ToCSvc.getTopEntry(label, profile.tables);
//		console.log(JSON.stringify(rval));
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "drag")).toBeFalsy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBe(label);
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
//		console.log(JSON.stringify(children));
//		console.log(JSON.stringify(rval));
		expect(rval.children.length).toBeGreaterThan(0);
	});
	
	it("Do we have a ToC?", function() {
		var rval = ToCSvc.getToC(profile);
		expect(rval).toBeDefined();
//		console.log("ToC=" + JSON.stringify(rval));
	});
});
