angular.module('igl').factory ('ToCSvc', function() {
	
	var svc = this;
	
	svc.getToC = function(profile) {
		toc = [];
		toc.push(svc.getTopEntry("Introduction", svc.getIntroduction()));
		toc.push(svc.getTopEntry("Use Case", svc.getUseCase()));
		toc.push(svc.getTopEntry("Messages", profile.messages));
		toc.push(svc.getTopEntry("Segments", profile.segments));		
		toc.push(svc.getTopEntry("Datatypes", profile.datatypes));
		toc.push(svc.getTopEntry("Value Sets", profile.tables));
		return toc;
	}
	
	svc.getIntroduction = function() {
		var rval = {
			"children" : [
			{
				"label" : "Purpose",
				"drag" : "Introduction",
				"reference" : ""
			}, {
				"label" : "Audience",
				"drag" : "Introduction",
				"reference" : ""
			}, {
				"label" : "Organisation of this guide",
				"drag" : "Introduction",
				"reference" : ""
			}, {
				"label" : "Referenced profiles - antecedents",
				"drag" : "Introduction",
				"reference" : ""
			}, {
				"label" : "Scope",
				"drag" : "Introduction",
				"reference" : ""
			}, {
				"label" : "Key technical decisions [conventions]",
				"drag" : "Introduction",
				"reference" : ""
			}
			]
		};
		return rval;
	}
	
	svc.getUseCase = function() {
		return {
			"children" : [
			{
				"label" : "Actors",
				"drag" : "Use Case",
				"reference" : ""
			}, {
				"label" : "Actors",
				"drag" : "Use case assumptions",
				"reference" : ""
			}, {
				"label" : "User story",
				"drag" : "Use Case",
				"reference" : ""
			}, {
				"label" : "Sequence diagram",
				"drag" : "Use Case",
				"reference" : ""
			}
			]
		};
		return rval;
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
			if (drag === "Messages") {
				entry = svc.createEntry(child, child.name + " - " + child.description, drag);
			} else if (drag === "Datatypes") {
				entry = svc.createEntry(child, child.name + " - " + child.description, drag);
			} else if (drag === "Value Sets") {
				entry = svc.createEntry(child, child.bindingIdentifier + " - " + child.description, drag);
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