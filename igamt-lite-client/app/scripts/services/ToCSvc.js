angular.module('igl').factory ('ToCSvc', function() {
	
	var svc = this;
	
	svc.getToC = function(profile) {
		toc = [];
		toc.push(svc.getTopEntry("Introduction"));
		toc.push(svc.getTopEntry("UseCase"));
		toc.push(svc.getTopEntry("ConformanceToThisGuide"));
		
		toc.push(svc.getTopEntry("Datatypes", profile.datatypes));
		toc.push(svc.getTopEntry("ConformanceProfile", profile.messages));
		toc.push(svc.getTopEntry("CodeSystems"));
		toc.push(svc.getTopEntry("LaboratoryResultMessageDevelopmentResources"));
		toc.push(svc.getTopEntry("AdditionalImplementationGuidance"));
		toc.push(svc.getTopEntry("ComponentAndProfileOIDS"));

		toc.push(svc.getTopEntry("Segments", profile.segments));
		toc.push(svc.getTopEntry("ValueSets", profile.tables));
		return toc;
	}
	
	svc.getIntroduction = function() {
		var rval = {
				
		}
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
		var children = [];
		var rval = {
			"label" : label,
			"drop" : [label],
		}
		if (fromProfile !== undefined) {
			rval["reference"] = fromProfile;
			rval["children"] = svc.createEntries(label, fromProfile.children);
		}
		return rval;
	}
	
	// Returns a second level set entries, These are draggable.  "drag" indicates
	// where one of these entries can be dropped.  
	svc.createEntries = function(drag, children) {
		var rval = [];
		var entry = {};
		_.each(children, function(child){
			if (drag === "Datatypes") {
				entry = svc.createEntry(child, child.name + " - " + child.description, drag);
			} else if (drag === "ValueSets") {
				entry = svc.createEntry(child, child.name + " - " + child.description, drag);
			} else {
				entry = svc.createEntry(child, child.label, drag);
			}
			rval.push(entry);
		});
		return rval;
	}
	
	var assembleDatatypeLabel = function(child) {
		return child.name + " - " + child.description;
	}
	
	svc.createEntry = function(reference, label, drag, drop, children) {
		var rval = {
			"label": label,
		};
		if (reference !== undefined) {
			rval["reference"] = reference;
		}
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