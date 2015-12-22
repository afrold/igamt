angular.module('igl').factory ('ProfileAccessSvc', function() {

	var svc = this;

	var sortById = function(a, b) {
		if (a.id < b.id)
			return -1;
		if (a.id > b.id)
			return 1;
		return 0;
	}

	svc.Version = function(profile) {
		return profile.metaData.hl7Version;
	}


	svc.Datatypes = function(profile) {
	
		var dts = this;
	
		dts.datatypes = (function() {
			return _.sortBy(profile.datatypes.children, "id");
		})();
	
		dts.findById = function(id) {
			return _.find(dts.datatypes, function(datatype) {
				return datatype.id === id;
			});
		}						
		
		dts.findByNames = function(names) {
			var datatypes = [];
			_.each(names, function(name){
				datatypes.push(dts.findByName(name));
			});
			return datatypes;
		}	
		
		dts.findByName = function(name) {
			return _.find(dts.datatypes, function(datatype) {
				return datatype.name === name;
			});
		}	
		
		dts.findDead = function(names) {
			dtNames = _.pluck(dts.datatypes, "name");
			return _.difference(dtNames, names);
		}
	
		dts.removeDead = function(names) {
			dtNames = dts.findDead(names);
			var i = 0;
			_.each(dtNames, function(id) {
				i = _.indexOf(dts.datatypes, id, i);
				dts.datatypes.splice(i, 1);
			});
			
			return dts.datatypes;
		}
		
		return dts;
	}

	svc.Segments = function(profile) {
	
		var segs = this;
	
		segs.segments = function() {
			return profile.segments.children;
		}
		
		segs.getAllSegmentIds = function() {
			var rval = [];
			_.each(segs.segments(), function(seg){
				rval.push(seg.id);
			});
			return rval;
		}
		
		segs.findByIds = function(ids) {
			var segments = [];
			_.each(ids, function(id){
				var segment = segs.findById(id);
				if (segment) {
					segments.push(segment);
				}
			});
			return segments;
		}
		
		segs.findById = function(id) {
			var segment = _.find(segs.segments(), function(segment) {
				return segment.id === id;
			});
			
			return segment;
		}
	
		// We pass in segRefs as a collection of ids for segments that are
		// alive.
		// All the rest are dead.
		segs.findDead = function(segRefs) {
			var segIds = segs.getAllSegmentIds();
			return _.difference(segIds, segRefs);
		}
	
		// We pass in segRefs as a collection of ids for segments that are
		// alive.
		// All the rest are dead.
		segs.removeDead = function(segRefs) {
			var segIds = segs.findDead(segRefs);
			var segments = segs.segments();
			var i = 0;
			_.each(segIds, function(id) {
				i = _.indexOf(segments, id, i);
				segments.splice(i, 1);
			});
			
			return segments;
		}
		
		segs.findFields = function(segment) {
			var fields = [];
			_.each(segment.fields, function(field) {
				fields.push(field);					
			});
			
			return fields;
		}
		
		segs.findDatatypeNames = function(fields) {
			var datatypeNames = [];
			_.each(fields, function(field){
				datatypeNames.push(field.datatype);
			});
			
			return datatypeNames;
		}

		return segs;
	}

	svc.Messages = function(profile) {
	
		var msgs = this;
	
		msgs.findById = function(id) {
			return _.find(msgs.messages, function(message) {
				return message.id === id;
			});
		}
		
		msgs.getMessageIds = function() {
			var rval = [];
			_.each(profile.messages.children, function(message) {
				rval.push(message.id);
			});
			console.log("rval=" + rval.length);
			return rval;
		}
		
		msgs.getAllSegmentRefs = function() {

			var segmentRefs = [];
			
			_.each(profile.messages.children, function(message) {
				var refs = msgs.getSegmentRefs(message);
				_.each(refs, function(ref) {
					segmentRefs.push(ref);
				});
			});
			
			return segmentRefs;
		}
	
		msgs.getSegmentRefsSansOne = function(message) {

			var segmentRefs = [];
			var messageId = message.id;
			
			_.each(profile.messages.children, function(message) {
				if (message.id !== messageId) {
					var refs = msgs.getSegmentRefs(message);
					_.each(refs, function(ref) {
						segmentRefs.push(ref);
					});
				}
			});
			
			return segmentRefs;
		}
		
		msgs.getSegmentRefs = function(message) {
			
			var segmentRefs = [];
			
			_.each(message.children, function(groupORsegment) {
				msgs.fetchSegments(groupORsegment, segmentRefs);
			});
		  return segmentRefs;
		}
		
		msgs.fetchSegments = function(groupORsegment, segmentRefs) {

			if (groupORsegment.type === "group") {
				_.each(groupORsegment.children, function(groupORsegment1) {
					msgs.fetchSegments(groupORsegment1, segmentRefs);					
				});
			} else if (groupORsegment.type === "segmentRef") {
				segmentRefs.push(groupORsegment.ref);
			} else {
				console.log("Was neither group nor segmetnRef groupORsegment.type="
								+ groupORsegment.type);
			}
		}
	
		return msgs;
	}
	
	return svc;
});