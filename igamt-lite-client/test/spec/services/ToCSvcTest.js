'use strict';

describe("toc service", function () {
	// gcr This test is not ready for IGDocument.
	var ToCSvc;
	var igdocumentAsString;
	var document;
	
	beforeEach(function() {
		module('igl');
		inject(function (_ToCSvc_, $injector, $rootScope, $controller) {
			ToCSvc = _ToCSvc_;
 
// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.			 
		 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/igDocuments/';
		 	var jsonFixture = getJSONFixture('igDocument-2.7-HL7STD.json');
    			igdocumentAsString = JSON.stringify(jsonFixture);
		 	expect($rootScope).toBeDefined();
		 	expect(igdocumentAsString).toBeDefined();
		});
		// We want a pristine document for each test so state changes from one test don't pollute
		// the others.
		document = JSON.parse(igdocumentAsString);
	});

	it("Do we have an Introduction?", function () {
		expect(ToCSvc).toBeDefined();
		var rval = ToCSvc.getSections();
		expect(rval).toBeDefined();
		var hasIt = _.find(rval, function(section){
			return section.lable === "Introduction";
		});
		expect(hasIt).toBe(true);
		
//		console.log(JSON.stringify(rval, null, 2));
	});
	
	it("Do we have a Use Case?", function () {
		expect(ToCSvc).toBeDefined();
		var rval = ToCSvc.getSections();
		expect(rval).toBeDefined();
		var hasIt = _.find(rval, function(section){
			return section.lable === "Use Case";
		});
		expect(hasIt).toBe(true);
		
//		console.log(JSON.stringify(rval, null, 2));
	});
	
	it("Do we get a valid entry?", function() {
		var rval = ToCSvc.createEntry("AUT", "2.1", "AUT", "2", []);
		expect(rval).toBeDefined();
		expect(_.has(rval, 'id')).toBeTruthy();
		expect(_.property('id')(rval)).toBe("2.1");
		expect(_.has(rval, 'label')).toBeTruthy();
		expect(_.property('label')(rval)).toBe("AUT");
		expect(_.has(rval, 'selected')).toBeTruthy();
		expect(_.property('selected')(rval)).toBeFalsy();
		expect(_.has(rval, 'parent')).toBeTruthy();
		expect(_.property('parent')(rval)).toBe("2");
		expect(_.has(rval, 'drop')).toBeTruthy();
		expect(_.has(rval, 'children')).toBeFalsy();
	});
	
	it("Do we get valid entries?", function() {
		var rval = ToCSvc.createEntries("Messages", document.profile.messages.children);
		expect(rval).toBeDefined();
//		console.log("rval=" + JSON.stringify(rval, null, 2));
	});
		
	it("Do we have valid messages?", function() {
		var label = "Conformance Profiles";
		var rval = ToCSvc.getTopEntry("3.1", "3", label, document.profile.segments);		

		expect(_.has(rval, 'id')).toBeTruthy();
		expect(_.property('id')(rval)).toBe("3.1");
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "parent")).toBeTruthy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBeDefined();
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
		expect(rval.children.length).toBeGreaterThan(0);

//		console.log(JSON.stringify(rval));
//		console.log(JSON.stringify(children, null, 2));
//		console.log(JSON.stringify(rval, null, 2));
	});	
	
	it("Do we have valid segments?", function() {
		var label = "Segments and Field Descriptions";
		var rval = ToCSvc.getTopEntry("3.2", "3", label, document.profile.segments);		
		expect(_.has(rval, 'id')).toBeTruthy();
		expect(_.property('id')(rval)).toBe("3.2");
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "parent")).toBeTruthy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBeDefined();
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
		expect(rval.children.length).toBeGreaterThan(0);
		
//		console.log(JSON.stringify(children, null, 2));
//		console.log(JSON.stringify(rval, null, 2));
	});
	
	it("Do we have valid datatypes?", function() {
		var label = "Datatypes";
		var rval = ToCSvc.getTopEntry("5", label, document.profile.datatypes);
		var rval = ToCSvc.getTopEntry("3.3", "3", label, document.profile.datatypes);
		expect(_.has(rval, 'id')).toBeTruthy();
		expect(_.property('id')(rval)).toBe("3.3");
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "parent")).toBeTruthy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBeDefined();
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
		expect(rval.children.length).toBeGreaterThan(0);

//		console.log(JSON.stringify(rval));
//		console.log(JSON.stringify(children, null, 2));
//		console.log(JSON.stringify(rval, null, 2));
	});
	
	it("Do we have valid valuesets?", function() {
		var label = "Value Sets";
		var rval = ToCSvc.getTopEntry("3.4", "3", label, document.profile.tables);
		expect(_.has(rval, 'id')).toBeTruthy();
		expect(_.property('id')(rval)).toBe("3.4");
		expect(_.has(rval, "label")).toBeTruthy();
		expect(_.property("label")(rval)).toBe(label);
		expect(_.has(rval, "parent")).toBeTruthy();
		expect(_.has(rval, "drop")).toBeTruthy();
		var drops = _.property("drop")(rval);
		expect(drops).toBeDefined();
		expect(_.has(rval, "children")).toBeTruthy();
		var children  = _.property("children")(rval);
		expect(children.length).toBeGreaterThan(0);
		expect(rval.children.length).toBeGreaterThan(0);


//		console.log(JSON.stringify(rval));
//		console.log(JSON.stringify(children, null, 2));
//		console.log(JSON.stringify(rval, null, 2));
	});
	
	it("Do we have MessageInfrstucture with the right nestings in the right order?", function() {
		var rval = ToCSvc.getMessageInfrastructure(document.profile);
		expect(rval.children.length).toBe(4);
		expect(rval.children[0].id).toBe("3.1");
		expect(rval.children[1].id).toBe("3.2");
		expect(rval.children[2].id).toBe("3.3");
		expect(rval.children[3].id).toBe("3.4");
	});
	
	it("Do we have a ToC?", function() {
		var rval = ToCSvc.getToC(document);
		expect(rval).toBeDefined();
		expect(rval.length).toBe(3);

//		console.log("ToC=" + JSON.stringify(rval, null, 2));
	});
});
