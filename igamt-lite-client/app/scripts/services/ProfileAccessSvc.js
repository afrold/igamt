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
	
		dts.getAllDatatypeIds = function() {
			
			var dtIds = [];
			
			_.each(dts.datatypes, function(datatype) {
				dtIds.push(datatype.id);
			});
			
			return dtIds;
		}
		
		dts.findById = function(id) {
			return _.find(dts.datatypes, function(datatype) {
				return datatype.id === id;
			});
		}						
		
		dts.removeDead = function(idsDead, idsLive) {
			var dtIds = _.difference(idsDead, idsLive);
			var i = 0;
			_.each(dtIds, function(id) {
				i = _.indexOf(dts.datatypes, id, i);
				dts.datatypes.splice(i, 1);
			});
			
			return dts.datatypes;
		}
		
		dts.findValueSetsFromDatatypeIds = function(dtIds) {
			vsIds = [];
			_.each(dtIds, function(dtId) {
				var datatype = dts.findById(dtId);
				if (datatype) {
					var rval = dts.findValueSetsFromDatatype(datatype);
					vsIds.push(rval);
				}
			});
			console.log("vsIds=" + vsIds.length + " dtIds=" + dtIds.length)
			return _.uniq(_.flatten(vsIds));
		}
		
		dts.findValueSetsFromDatatype = function(datatype) {
			vsIds = [];
			_.each(datatype.components, function(component) {
				if (component.table.trim()) {
					vsIds.push(component.table);
				}
			});
			return _.uniq(vsIds);
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
		
		segs.removeDead = function(idsDead, idsLive) {
			var segIds = _.difference(idsDead, idsLive);
			var segments = segs.segments();
			var i = 0;
			_.each(segIds, function(id) {
				i = _.indexOf(svc.Datatypes(profile).datatypes, id, i);
				if (i > -1) {
					segments.splice(i, 1);
				}
			});
			
			return segments;
		}
		
		segs.findDatatypesFromSegmentRefs  = function(segRefs) {
			var dtIds = [];
			_.each(segRefs, function(segRef) {
				var segment = segs.findById(segRef);
				if (segment) {
					dtIds.push(segs.findDatatypesFromSegment(segment));
				} else {
//					console.log("segment=" + segment);
				}
			});
			
			return _.uniq(_.flatten(dtIds));
		}
		
		segs.findDatatypesFromSegment = function(segment) {
			var dtIds = [];
			_.each(segment.fields, function(field) {
				dtIds.push(field.datatype);
			});
			
			return _.uniq(dtIds);
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
//			console.log("rval=" + rval.length);
			return rval;
		}
		
		msgs.findOtherMessages = function(message) {
			var msgIds = _.pluck
		}
		
		msgs.getAllSegmentRefs = function(messages) {

			var segRefs = [];
			
			_.each(messages, function(message) {
				var refs = msgs.getSegmentRefs(message);
				segRefs.push(refs);
			});
			
			return _.uniq(_.flatten(segRefs));
		}
	
		msgs.getSegmentRefs = function(message) {
			
			var segRefs = [];
			
			_.each(message.children, function(groupORsegment) {
				segRefs.push(msgs.fetchSegmentRefs(groupORsegment, segRefs));					
			});
		  return _.flatten(segRefs);
		}
		
		msgs.fetchSegmentRefs = function(groupORsegment) {

			var segRefs = [];
			
			if (groupORsegment.type === "group") {
				_.each(groupORsegment.children, function(groupORsegment1) {
					segRefs.push(msgs.fetchSegmentRefs(groupORsegment1, segRefs));					
				});
			} else if (groupORsegment.type === "segmentRef") {
				segRefs.push(groupORsegment.id);
			} else {
				console.log("Was neither group nor segmetnRef groupORsegment.type="
								+ groupORsegment.type);
			}
			return _.flatten(segRefs);
		}
	
		return msgs;
	}
	
	svc.ValueSets = function(profile) {
		
		var vss = this;
		
		vss.valueSets = (function() {
			return _.sortBy(profile.tables.children, "id");
		})();
		
		vss.getAllValueSetIds = function() {
			
			var vsIds = [];
			
			var i = 0;
			_.each(vss.valueSets, function(valueSet) {
				if (valueSet) {
					vsIds.push(valueSet.id);
				}
			});
			
			return vsIds;
		}
		
		vss.findById = function(id) {
			return _.find(vss.valueSets, function(vs) {
				return vs.id === id;
			});
		}
		
		vss.removeDead = function(idsDead, idsLive) {
			var vsIds = _.difference(idsDead, idsLive);
			var i = 0;
			_.each(vsIds, function(id) {
				i = _.indexOf(vss.valueSets, id, i);
				vss.valueSets.splice(i, 1);
			});
			
			return vss.valueSets;
		}
		
		return vss;
	}
	
	return svc;
});