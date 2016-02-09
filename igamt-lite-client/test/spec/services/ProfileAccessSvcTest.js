'use strict';

describe("profile access service", function () {
	
	var ProfileAccessSvc;
	var igdocumentAsString;
	var profile;
	var $rootScope;
	
	beforeEach(function() {
		module('igl');
		inject(function (_ProfileAccessSvc_, $injector, _$rootScope_, $controller) {
			ProfileAccessSvc = _ProfileAccessSvc_;
			 
// Don't ask me why, but the following fixtures path MUST have "base/" prepended or it won't work.
// Also, see the "pattern" thing, which is the last element of the files array in test/karma.conf.js.			 
			 	jasmine.getJSONFixtures().fixturesPath='base/test/fixtures/igDocuments/';
			 	var jsonFixture = getJSONFixture('igdocument-2.7-HL7STANDARD-.json');
	    			igdocumentAsString = JSON.stringify(jsonFixture);
	    			$rootScope = _$rootScope_;
			 	expect($rootScope).toBeDefined();
			 	expect(igdocumentAsString).toBeDefined();
		});
		// We want a pristine profile for each test so state changes from one test don't pollute
		// the others.
		var igdocument = JSON.parse(igdocumentAsString);
		$rootScope.igdocument = igdocument;
		profile = igdocument.profile;
		expect(profile).toBeDefined();
	});
	
	it ("Can we determine a dependency?", function() {
		var message = _.find(profile.messages.children, function(message){
		return message.name === "SRR^S01^SRR_S01";
		});
		var segIds = ProfileAccessSvc.Messages().getSegmentRefs(message);
		var SUT = ProfileAccessSvc.Segments().findById(segIds[0]);
		var msgs = ProfileAccessSvc.Segments().getParentalDependencies(SUT);
		expect(msgs.length > 0).toBe(true);
		msgs = ProfileAccessSvc.Segments().getParentalDependencies("X");
		expect(msgs.length > 0).toBe(false);
	});
	
//	it("Do we get a collection of groups?", function() {
//		var message = _.find(profile.messages.children, function(message){
//			return message.name === "SRR^S01^SRR_S01";
//		});
//		console.log("message=" + message.name);
//		var groups = ProfileAccessSvc.Messages().getGroups(message);
//		expect(groups).toBeDefined();
//		expect(groups.length).toBe(1);
//		var groups = ProfileAccessSvc.Messages().getGroups(message1);
//		expect(groups).toBeDefined();
//		expect(groups.length > 0).toBe(true);
//	});
//	
//	it("Do we get all message ids?", function() {
//		var cntMessages = profile.messages.children.length;
//		var cntIds = ProfileAccessSvc.Messages().getMessageIds().length;
//		expect(cntMessages).toBe(cntIds);
//	});
//	
//	it("Can we find a message given its id?", function(){
//		var messages = ProfileAccessSvc.Messages().messages();
//		_.each(messages, function(message){
//			var msg = ProfileAccessSvc.Messages().findById(message.id);
//			expect(msg).toBeDefined();
//			expect(msg.id).toBe(message.id);
//		})
//	});
	
	it("Can we find a segment given its id?", function(){
		var segments = ProfileAccessSvc.Segments().segments();
		_.each(segments, function(segment){
			var seg = ProfileAccessSvc.Segments().findById(segment.id);
			expect(seg).toBeDefined();
			expect(seg.id).toBe(segment.id);
		})
	});
	
//	it("Can we find a datatype given its id?", function(){
//		var datatypes = ProfileAccessSvc.Datatypes(profile).datatypes();
//		_.each(datatypes, function(datatype){
//			var dt = ProfileAccessSvc.Datatypes(profile).findById(datatype.id);
//			expect(dt).toBeDefined();
//			expect(dt.id).toBe(datatype.id);
//		})
//	});
//	
//	it("Can we find a valueSet given its id?", function(){
//		var valueSets = ProfileAccessSvc.ValueSets(profile).valueSets();
//		_.each(valueSets, function(valueSet){
//			var vs = ProfileAccessSvc.ValueSets(profile).findById(valueSet.id);
//			expect(vs).toBeDefined();
//			expect(vs.id).toBe(valueSet.id);
//		})
//	});
//	
//
//	it("Check version", function () {
//		expect(ProfileAccessSvc.Version(profile)).toBe("2.7");
//	});
	
	it("Are fetched segmentRefs valid?", function () {

		var messages = ProfileAccessSvc.Messages().messages();
		expect(messages).toBeDefined();
		var msgSegRefs = ProfileAccessSvc.Messages().getAllSegmentRefs(messages);
		expect(msgSegRefs).toBeDefined();
		expect(_.isArray(msgSegRefs)).toBe(true);
		var segIds = ProfileAccessSvc.Segments().getAllSegmentIds();
		expect(msgSegRefs.length).toBe(segIds.length);
	});
	
//	it("Can we find the living and the dead?", function() {
//		var msgLive = ProfileAccessSvc.Messages().messages();
//		var msgDead = [];
//		msgDead.push(ProfileAccessSvc.Messages().messages()[6]);
//		
//		var bLiveSize = msgLive.length;
//		var idxP = _.findIndex(msgLive, {'id': msgDead[0].id});
//		msgLive.splice(idxP, 1);
//		var aIdxP = _.findIndex(msgLive, {'id' : msgDead[0].id});
//
//		var aLiveSize = msgLive.length;
//		expect(bLiveSize).toBe(aLiveSize + 1);
//		var msgLiveIds = _.pluck(msgLive, "id");
//		var msgDeadIds = _.pluck(msgDead, "id");
//
//		expect(_.indexOf(msgLiveIds, msgDeadIds[0])).toBe(-1)
//		expect(_.intersection(msgLiveIds, msgDeadIds).length).toBe(0);
//
//		var segmentRefsLive = ProfileAccessSvc.Messages().getAllSegmentRefs(msgLive);
//		_.each(segmentRefsLive, function(segmentRefLive){
//			var seg = ProfileAccessSvc.Segments().findById(segmentRefLive);
//			expect(seg).toBeDefined();
//			expect(seg.id).toBe(segmentRefLive);
//		})
//		var segmentRefsMerelyDead = ProfileAccessSvc.Messages().getAllSegmentRefs(msgDead);
//		_.each(segmentRefsMerelyDead, function(segmentRefMerelyDead){
//			var seg = ProfileAccessSvc.Segments().findById(segmentRefMerelyDead);
//			expect(seg).toBeDefined();
//			expect(seg.id).toBe(segmentRefMerelyDead);
//		})
//		
//		var segmentRefsSincerelyDead = ProfileAccessSvc.Segments().findDead(segmentRefsMerelyDead, segmentRefsLive);
//		segmentRefsSincerelyDead = ProfileAccessSvc.Segments().removeDead(segmentRefsSincerelyDead);
//		var segmentRefsLeft = ProfileAccessSvc.Messages().getAllSegmentRefs(msgLive);
//		expect(_.difference(segmentRefsMerelyDead, segmentRefsLeft).length).toBe(0);
////		expect(segmentRefsSincerelyDead.length).toBe(0);		
////		expect(_.intersection(segmentRefsLive, segmentRefsSincerelyDead).length).toBe(0);
//		
////		var dtsLive =  ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segmentRefsLive);
////		var dtsDead =  ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segmentRefsReallyDead);
////		var dtsReallyDead = ProfileAccessSvc.Datatypes(profile).removeDead(dtsDead, dtsLive);
////		expect(_.difference(dtsDead, dtsLive).length).toBe(0);
////		expect(_.intersection(dtsLive, dtsReallyDead).length).toBe(0);
////			
////		var vssLive = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtsLive);
////		var vssDead = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtsReallyDead);
////		var vssReallyDead = ProfileAccessSvc.ValueSets(profile).removeDead(vssDead, vssLive);
////		expect(_.difference(vssDead, vssLive).length).toBe(0);
////		expect(_.intersection(vssLive, vssReallyDead).length).toBe(0);
//		
////		console.log("Can we find the living and the dead?segmentRefsLive=" + segmentRefsLive.length);
////		console.log("Can we find the living and the dead?segmentRefsMerelyDead=" + segmentRefsMerelyDead.length);
////		console.log("Can we find the living and the dead?segmentRefsSincerelyDead=" + segmentRefsSincerelyDead.length);
////		console.log("Can we find the living and the dead?segmentRefsLeft=" + segmentRefsLeft.length);
////		console.log("Can we find the living and the dead?dtsLive=" + dtsLive.length);
////		console.log("Can we find the living and the dead?dtsDead=" + dtsDead.length);
////		console.log("Can we find the living and the dead?dtsReallyDead=" + dtsReallyDead.length);
////		console.log("Can we find the living and the dead?vssLive=" + vssLive.length);
////		console.log("Can we find the living and the dead?vssDead=" + vssDead.length);
////		console.log("Can we find the living and the dead?vssReallyDead=" + vssReallyDead.length);
//	});
//	
//	it("Can we find all datatypes in a segment(s)?", function() {
//		var segments = ProfileAccessSvc.Segments().segments();
//		expect(segments.length).toBe(166);
//		
//		var dts0 = ProfileAccessSvc.Segments().findDatatypesFromSegment(segments[0]);
//		expect(dts0.length > 0).toBe(true);
//		
//		var dts3 = ProfileAccessSvc.Segments().findDatatypesFromSegment(segments[3]);
//		expect(dts3.length > 0).toBe(true);
//		
//		var segRefs = [segments[0].id, segments[3].id];
//		var dts03 = ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segRefs);
//		expect(dts03.length > 0).toBe(true);
//		
//		var segRefs = ProfileAccessSvc.Segments().getAllSegmentIds();
//		var dtsAll = ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segRefs);
//		var dtsAll1 = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
//
//		expect(_.difference(dtsAll, dtsAll1).length).toBe(0);
//		
//		expect(dtsAll1.length).toBe(ProfileAccessSvc.Datatypes(profile).datatypes().length);
//	});
//	
//	it("Can we remove datatypes?", function() {
//		var bDtIds = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
//		var deadDtIds = _.take(bDtIds, 3);
//		var liveDtIds = _.slice(bDtIds, 3);
//		var deadDeadDtIds = ProfileAccessSvc.Datatypes(profile).removeDead(deadDtIds, liveDtIds);
//		var aDtIds = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
//		
//		expect(bDtIds.length).toBe(aDtIds.length + 3);
//		
////		console.log("Can we remove datatypes?bDtIds=" + bDtIds.length);
////		console.log("Can we remove datatypes?aDtIds=" + aDtIds.length);
////		console.log("Can we remove datatypes?deadDtIds=" + deadDtIds.length);
////		console.log("Can we remove datatypes?deadDtIds=" + deadDtIds);
////		console.log("Can we remove datatypes?liveDtIds=" + liveDtIds.length);
//	});
//	
//	it("Can we remove valueSets?", function() {
//		var bVsIds = ProfileAccessSvc.ValueSets(profile).getAllValueSetIds();
//		var deadVsIds = _.take(bVsIds, 3);
//		var liveVsIds = _.slice(bVsIds, 3);
//		var deadDeadVsIds = ProfileAccessSvc.ValueSets(profile).removeDead(deadVsIds, liveVsIds);
//		var aVsIds = ProfileAccessSvc.Datatypes(profile).getAllValueSetIds();
//		expect(bVsIds.length).toBe(aVsIds.length + 3);
//		
////		console.log("Can we remove valueSets?diff="  + _.difference(deadVsIds, liveVsIds));
////		console.log("Can we remove valueSets?bVsIds=" + bVsIds.length);
////		console.log("Can we remove valueSets?aVsIds=" + aVsIds.length);
////		console.log("Can we remove valueSets?deadVsIds=" + deadVsIds.length);
////		console.log("Can we remove valueSets?deadVsIds=" + deadVsIds);
////		console.log("Can we remove valueSets?deadDeadVsIds=" + deadDeadVsIds.length);
////		console.log("Can we remove valueSets?deadDeadVsIds=" + deadDeadVsIds);
////		console.log("Can we remove valueSets?liveVsIds=" + liveVsIds.length);
//	});
//	
//	it("Can we find all value sets in a datatype(s)?", function() {
//		var dtIds = ProfileAccessSvc.Datatypes(profile).getAllDatatypeIds();
//		var datatypes = ProfileAccessSvc.Datatypes(profile).datatypes();
//		expect(dtIds.length).toBe(datatypes.length);
//		var vsIds = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtIds);
//		var vsIds1 = ProfileAccessSvc.ValueSets(profile).getAllValueSetIds();
//		_.each(vsIds, function(vsId){
//			var valueSet = ProfileAccessSvc.ValueSets(profile).findById(vsId);
//			expect(vsId).toBe(valueSet.id);
//		});
//	});
//	
//	it("Can we find a segment using its id?", function() {
//		var segRefs = ProfileAccessSvc.Messages().getAllSegmentRefs(profile.messages.children);
//		var segIds = ProfileAccessSvc.Segments().getAllSegmentIds();
//		_.each(segRefs, function(segRef){
//			expect(_.indexOf(segIds, segRef) > -1).toBe(true);
//		});
//		
////		console.log("Can we find a segment using its id?segRefs=" + segRefs.length);
////		console.log("Can we find a segment using its id?segIds=" + segIds.length);
//	});
});