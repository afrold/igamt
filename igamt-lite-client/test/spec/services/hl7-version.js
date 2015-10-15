'use strict';

describe("hl7Version service", function () {
	
	var HL7VersionSvc;
	var httpBackend;
	
	beforeEach(function() {
		console.log("0 here==>");
		module('igl');
		inject(function (_HL7VersionSvc_, $httpBackend) {
			HL7VersionSvc = _HL7VersionSvc_;
			console.log("1 here==>");
	  		httpBackend = $httpBackend;
		  });
		console.log(HL7VersionSvc);
		console.log(httpBackend);
	});
	console.log("2 here==>");
	  
	it("should get and set", function () {
		console.log("3 here==>");
		HL7VersionSvc = "2.5.1";
		expect("2.5.1").toBe("2.5.0");
		console.log("4 here==>");
	});
	console.log("5 here==>");
});