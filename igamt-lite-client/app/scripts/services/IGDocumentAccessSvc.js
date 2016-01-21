angular.module('igl').factory ('IGDocumentAccessSvc', function() {

	var svc = this;

	var sortById = function(a, b) {
		if (a.id < b.id)
			return -1;
		if (a.id > b.id)
			return 1;
		return 0;
	}

	svc.Version = function(igdocument) {
		return igdocument.metaData.hl7Version;
	}


	svc.Datatypes = function(igdocument) {
	
		var dts = this;
	
<<<<<<< ours
		dts.datatypes = function() {
			return profile.datatypes.children;
		};
=======
		dts.datatypes = (function() {
			return _.sortBy(igdocument.profile.datatypes.children, "id");
		})();
>>>>>>> theirs
	
		dts.getAllDatatypeIds = function() {
			
			var dtIds = [];
			
			_.each(dts.datatypes(), function(datatype) {
				dtIds.push(datatype.id);
			});
			
			return dtIds;
		}
		
		dts.findById = function(id) {
			var datatype = _.find(dts.datatypes(), function(datatype) {
				return datatype.id === id;
			});
			if (!datatype) {
				console.log("dts.findById: datatype not found id=" + id);
			}
			return datatype;
		}						
		
		dts.findDead = function(idsDead, idsLive) {
			var dtIds = _.difference(idsDead, idsLive);
			return dtIds;
		}
				
		dts.removeDead = function(dtIds) {
			var datatypes = dts.datatypes();
			var i = -1;
			
			_.each(dtIds, function(id) {
				i = _.findIndex(datatypes, { 'id' : id });
				if (i > -1) {
					datatypes.splice(i, 1);
				} else {
					console.log("dts.removeDead: datatype not found id=" + id);
				}
			});
			
			return dtIds;
		}
		
		dts.findValueSetsFromDatatypeIds = function(dtIds) {
			
			var vsIds = [];
			
			_.each(dtIds, function(dtId) {
				var datatype = dts.findById(dtId);
				if (datatype) {
					var rvals = dts.findValueSetsFromDatatype(datatype);
					_.each(rvals, function(rval) {
						vsIds.push(rval);
					});
				} else {
					console.log("dts.findValueSetsFromDatatypeIds: Did not find dt for dtId=" + dtId);
				}
			});
			
			return _.uniq(vsIds);
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

	svc.Segments = function(igdocument) {
	
		var segs = this;
	
		segs.segments = function() {
			return igdocument.profile.segments.children;
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
			if (!segment) {
				console.log("segs.findById: segment not found id=" + id);
			}
			return segment;
		}
		
		segs.findDead = function(idsDead, idsLive) {
			var segIds = _.difference(idsDead, idsLive);
			return segIds;
		}
		
		segs.removeDead = function(segIds) {
			var segments = segs.segments();
			var i = -1;
		
			_.each(segIds, function(id) {
				i = _.findIndex(segments, { 'id' : id });
				if (i > -1) {
					segments.splice(i, 1);
				} else {
					console.log("segs.removeDead: segment not found id=" + id);
				}
			});
			
			return segIds;
		}
		
		segs.findDatatypesFromSegmentRefs  = function(segRefs) {
			
			var dtIds = [];
			
			_.each(segRefs, function(segRef) {
				var segment = segs.findById(segRef);
				if (segment) {
					dtIds.push(segs.findDatatypesFromSegment(segment));
				} else {
					console.log("segs.findDatatypesFromSegmentRefs: Did not find seg for segRef=" + segRef);
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
	
//		segs.findDatatypeNames = function(fields) {
//			
//			var datatypeNames = [];
//			
//			_.each(fields, function(field){
//				datatypeNames.push(field.datatype);
//			});
//			
//			return datatypeNames;
//		}

		return segs;
	}

	svc.Messages = function(igdocument) {
	
		var msgs = this;
	
		msgs.messages = function() {
			return profile.messages.children;
		};
		
		msgs.findById = function(id) {
			return _.find(msgs.messages(), function(message) {
				return message.id === id;
			});
		}
		
		msgs.getMessageIds = function() {

			var rval = [];
<<<<<<< ours

			_.each(profile.messages.children, function(message) {
=======
			_.each(igdocument.profile.messages.children, function(message) {
>>>>>>> theirs
				rval.push(message.id);
			});

			return rval;
		}
		
		msgs.getAllSegmentRefs = function(messages) {

			var segRefs = [];
			
<<<<<<< ours
			_.each(messages, function(message) {
=======
			_.each(igdocument.profile.messages.children, function(message) {
>>>>>>> theirs
				var refs = msgs.getSegmentRefs(message);
				_.each(refs, function(ref){
					segRefs.push(ref);
				});
			});
			
			return _.uniq(segRefs);
		}
	
<<<<<<< ours
=======
		msgs.getSegmentRefsSansOne = function(message) {

			var segmentRefs = [];
			var messageId = message.id;
			
			_.each(igdocument.profile.messages.children, function(message) {
				if (message.id !== messageId) {
					var refs = msgs.getSegmentRefs(message);
					_.each(refs, function(ref) {
						segmentRefs.push(ref);
					});
				}
			});
			
			return segmentRefs;
		}
		
>>>>>>> theirs
		msgs.getSegmentRefs = function(message) {
			
			var segRefs = [];
			
			_.each(message.children, function(groupORsegment) {
				var refs = msgs.fetchSegmentRefs(groupORsegment);
				_.each(refs, function(ref){
					segRefs.push(ref);
				});
			});
			
		  return _.uniq(segRefs);
		}
		
		msgs.fetchSegmentRefs = function(groupORsegment) {

			var segRefs = [];
			
			if (groupORsegment.type === "group") {
				_.each(groupORsegment.children, function(groupORsegment1) {
					var refs = msgs.fetchSegmentRefs(groupORsegment1);
					_.each(refs, function(ref){
						segRefs.push(ref);
					});
				});
			} else if (groupORsegment.type === "segmentRef") {
				segRefs.push(groupORsegment.ref);
			} else {
				console.log("Was neither group nor segmetnRef groupORsegment.type="
								+ groupORsegment.type);
			}
			
			return segRefs;
		}
	
		return msgs;
	}
	
	svc.ValueSets = function(profile) {
		
		var vss = this;
		
		vss.valueSets = function() {
//			return _.sortBy(profile.tables.children, "id");
			return profile.tables.children;
		};
		
		vss.getAllValueSetIds = function() {
			
			var vsIds = [];
			var valueSets = vss.valueSets();
			var i = 0;
			_.each(valueSets, function(valueSet) {
				if (valueSet) {
					vsIds.push(valueSet.id);
				}
			});
			
			return vsIds;
		}
		
		vss.findById = function(id) {
			var valueSet =  _.find(vss.valueSets(), function(vs) {
				return vs.id === id;
			});
			if (!valueSet) {
				console.log("vss.findById: valueSet not found id=" + id);
			}
			return valueSet;
		}
		
		vss.findDead = function(idsDead, idsLive) {
			var vsIds = _.difference(idsDead, idsLive);
			return vsIds;
		}
		
		vss.removeDead = function(vsIds) {			
			var valueSets = vss.valueSets();
			var i = -1;
			
			_.each(vsIds, function(id) {
				i = _.findIndex(valueSets, { 'id' : id });
				if (i > -1) {
					valueSets.splice(i, 1);
				} else {
					console.log("vss.removeDead: valueSet not found id=" + id);
				}
			});
			return vsIds;
		}
		
		return vss;
	}
	
	return svc;
});