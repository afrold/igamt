angular.module('igl').factory ('ProfileAccessSvc', function($rootScope) {

	var svc = this;

	svc.Version = function() {
		return $rootScope.igdocument.profile.metaData.hl7Version;
	}

	svc.Messages = function() {
	
		var msgs = this;
	
		msgs.messages = function() {
			return $rootScope.igdocument.profile.messages.children;
		};
		
		msgs.findById = function(id) {
			return _.find(msgs.messages(), function(message) {
				return message.id === id;
			});
		}
		
		msgs.getMessageIds = function() {

			var rval = [];

			_.each($rootScope.igdocument.profile.messages.children, function(message) {
				rval.push(message.id);
			});

			return rval;
		}
		
		msgs.getAllSegmentRefs = function(messages) {

			var segRefs = [];
			
			_.each(messages, function(message) {
				var refs = msgs.getSegmentRefs(message);
				_.each(refs, function(ref){
					segRefs.push(ref);
				});
			});
			
			return _.uniq(segRefs);
		}
	
		msgs.getSegmentRefs = function(message) {
			
			var segRefs = [];
			
			_.each(message.children, function(groupORsegment) {
				var refs = fetchSegmentRefs(groupORsegment);
				_.each(refs, function(ref){
					segRefs.push(ref);
				});
			});
			
		  return _.uniq(segRefs);
		}
		
		msgs.getGroups = function(message) {
			
			var groups = [];
			
			_.each(message.children, function(groupORsegment) {
				console.log("Was a what? groupORsegment.type="
						+ groupORsegment.type + " name=" + message.name);
				var grps = fetchGroups(groupORsegment);
				_.each(grps, function(grp){
					groups.push(grp);
				});
			});
			
		  return groups;
		}
		
		function fetchGroups(groupORsegment) {

			var groups = [];
			
			if (groupORsegment.type === "group") {
				console.log("Was a group groupORsegment.type="
						+ groupORsegment.type);
				groups.push(groupORsegment);
				_.each(groupORsegment.children, function(groupORsegment1) {
					var grps = fetchGroups(groupORsegment1);
					_.each(grps, function(grp){
						groups.push(grp);
					});
				});
			} else {
				console.log("Was a segmentRef groupORsegment.type="
								+ groupORsegment.type);
			}
			
			return groups;
		}
	
		function fetchSegmentRefs(groupORsegment) {

			var segRefs = [];
			
			if (groupORsegment.type === "group") {
				_.each(groupORsegment.children, function(groupORsegment1) {
					var refs = fetchSegmentRefs(groupORsegment1);
					_.each(refs, function(ref){
						segRefs.push(ref);
					});
				});
			} else if (groupORsegment.type === "segmentRef") {
				segRefs.push(groupORsegment.ref);
			} else {
				console.log("Was neither group nor segmentRef groupORsegment.type="
								+ groupORsegment.type);
			}
			
			return segRefs;
		}
	
		return msgs;
	}

	svc.Segments = function() {
	
		var segs = this;
	
		segs.segments = function() {
			return $rootScope.igdocument.profile.segments.children;
		}
		
		segs.truncate = function() {
			segs.segments().length = 0;
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
		
		segs.findById = function(segId) {
//			console.log("segIds=" + segs.getAllSegmentIds());
			var segments = segs.segments();
			
			var segment = _.find(segments, function(segment1) {
				return segment1.id === segId;
			});
			
			if (!segment) {
				console.log("segs.findById: segment not found, segId=" + segId);
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
		
			_.each(ensureArray(segIds), function(id) {
				i = _.findIndex(segments, { 'id' : id });
				if (i > -1) {
					segments.splice(i, 1);
				}
			});
			
			return segments.length;
		}
		
		segs.getParentalDependencies = function(segment) {
			var messages = svc.Messages().messages();
			var rval = _.filter(messages, function(message) {
				var segRefs= svc.Messages().getSegmentRefs(message);
				return _.indexOf(segRefs, segment.id) >= 0;
			});
			return rval;
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

		return segs;
	}

	svc.Datatypes = function() {
	
		var dts = this;
	
		dts.datatypes = function() {
			return $rootScope.igdocument.profile.datatypes.children;
		}
		
		dts.truncate = function() {
			dts.datatypes().length = 0;
		}
		
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
			var i = 0;
			
			_.each(ensureArray(dtIds), function(id) {
				i = _.findIndex(datatypes, { 'id' : id });
				if (i > -1) {
					datatypes.splice(i, 1);
				}
			});
			
			return datatypes.length;
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
	
	svc.ValueSets = function() {
		
		var vss = this;
		
		vss.valueSets = function() {
			return $rootScope.igdocument.profile.tables.children;
		};
				
		vss.truncate = function() {
			vss.valueSets().length = 0;
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
				console.log("vss.findById:: Did not find vs for vsId=" + dtId);
			}
			return valueSet;
		}
		
		vss.findDead = function(idsDead, idsLive) {
			var vsIds = _.difference(idsDead, idsLive);
			return vsIds;
		}
		
		vss.removeDead = function(vsIds) {			
			var valueSets = vss.valueSets();
//			console.log("b vss.removeDead=" + valueSets.length);
			
			_.each(ensureArray(vsIds), function(vsId) {
				var i = 0;
				_.each(valueSets, function(valueSet) {
					i = _.findIndex(valueSets, { 'id' : vsId });
					if (i > -1) {
						valueSets.splice(i, 1);
					}
				});
			});
			
			return valueSets.length;
		}
		
		return vss;
	}
	
	function ensureArray(possibleArray) {
		if(angular.isArray(possibleArray)) {
			return possibleArray;
		} else {
			console.log("Array ensured.");
			return [possibleArray];
		}
	}
	
	return svc;
});