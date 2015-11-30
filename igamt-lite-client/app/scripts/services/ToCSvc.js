angular.module('igl').factory ('ToCSvc', function() {
	
	var svc = this;
	
	svc.getToC = function(profile) {
		toc = [];
		toc.push(svc.getTopEntry("Datatypes", profile.datatypes));
		toc.push(svc.getTopEntry("Segments", profile.segments));
		toc.push(svc.getTopEntry("Messages", profile.messages));
		toc.push(svc.getTopEntry("Value Sets", profile.tables));
		return toc;
	}
	
	svc.getMetadata = function(metaData) {
		var rval = {};
		var label = "Metadata";
		var keys = _.keys(metaData);
//		var children = [];
//		_.each(keys, function(key) {
//			child = {};
//			child["label"] = 
//			children.push("label" : key);
//		});
//		
//		return {
//			"label" : label,
//			"drop" : label,
//			"children" : children
//		};
//		
//		
//		rval["Metadata"] = metaData;
		return keys;
	}
	
	// Returns a top level entry. It can be dropped on, but cannot be dragged.
	// It will accept a drop where the drag value matches its label.
	svc.getTopEntry = function(label, fromProfile) {
		var children = svc.createEntries(label, fromProfile.children);
		var segments = {
			"label" : label,
			"drop" : label,
			"children" : children
		}
		return segments;
	}
	
	// Returns a second level set entries, These are draggable.  "drag" indicates
	// where one of these entries can be dropped.  
	svc.createEntries = function(drag, children) {
		var rval = [];
		var labels = _.pluck(children, "label");
		if (drag === "Messages") {
			labels = _.pluck(children, "messageType");
		}
		if (drag === "Value Sets") {
			labels = _.pluck(children, "name");
		}
		_.each(labels, function(label){
			var entry = svc.createEntry(label, drag);
			rval.push(entry);
		});
		return rval;
	}
	
	svc.createEntry = function(label, drag, drop, children) {
		var rval = {
			"label": label,
		};
		if (drag !== undefined) {
			rval["drag"] = drag;
		}
		if (drop !== undefined) {
			rval["drop"] = drop;
		}
		if (children !== undefined) {
			rval["children"] = children;
		}
		return rval;
	}
		
	return svc;
})