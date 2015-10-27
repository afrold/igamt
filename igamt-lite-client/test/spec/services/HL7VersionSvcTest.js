'use strict';

describe("hl7Version service", function () {
	
	var HL7VersionSvc;
	
	beforeEach(module('igl'));
	
	beforeEach(inject(function (_HL7VersionSvc_) {
			HL7VersionSvc = _HL7VersionSvc_;
			HL7VersionSvc.hl7Version = "2.7";
			console.log("1 hl7Version==>");
		  }));
	console.log("2 hl7Version==>");
	  
	it("should get and set", function () {
		console.log("3 hl7Version==>");
		expect(HL7VersionSvc.hl7Version).toBe("2.7");
		console.log("4 hl7Version==>");
	});
	console.log("5 hl7Version==>");
});