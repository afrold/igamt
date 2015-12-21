angular.module('igl').factory ('ToCSvc', function() {
	
	var svc = this;
	
	svc.currentLeaf = { selected : false };
	
	svc.getToC = function(profile) {
		console.log("Getting toc...");
		var toc = [];
		toc.push(svc.getIntroduction());
		toc.push(svc.getUseCase());
		toc.push(svc.getMessageInfrastructure(profile));
//		svc.setLevels(toc);
		return toc;
	}
	
	svc.levels = [];
	svc.cnt = 0;
	svc.setLevels = function(entries) {
		
		svc.levels.push(svc.cnt++);
		_.each(entries, function(entry){
			entry.level = svc.cnt;
			svc.setLevels(entry.children);
		});
		svc.cnt = svc.levels.pop();
	} 
	
	svc.getIntroduction = function() {
		var rval = {
			"id" : "1",
			"label" : "Introduction",
			"selected" : false,
			"parent" : "0",
			"drop" : [],
			"reference" : "",
			"children" : [
			{
				"id" : "1.1",
				"label" : "Purpose",
				"selected" : false,
				"parent" : "1",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "1.2",
				"label" : "Audience",
				"selected" : false,
				"parent" : "1",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "1.3",
				"label" : "Organization of this guide",
				"selected" : false,
				"parent" : "1",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "1.4",
				"label" : "Referenced profiles - antecedents",
				"selected" : false,
				"parent" : "1",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "1.5",
				"label" : "Scope",
				"selected" : false,
				"parent" : "1",
				"drop" : "Scope",
				"reference" : "",
				"children" : [
					{"label" : "In Scope",
					"selected" : false,
					"parent" : "1.5",
					"drop" : [],
					"reference" : ""
					}, {
					"label" : "Out of Scope",
					"selected" : false,
					"parent" : "1.5",
					"drop" : [],
					"reference" : ""
					}
				 ]
			}, {
				"label" : "Key technical decisions [conventions]",
				"id" : "1.6",
				"selected" : false,
				"parent" : "1",
				"drop" : [],
				"reference" : ""
			}
			]
		};
		return rval;
	}
	
	svc.getUseCase = function() {
		return {
			"id" : "2",
			"label" : "Use Case",
			"selected" : false,
			"parent" : "0",
			"drop" : [],
			"reference" : "",
			"children" : [
			{
				"id" : "2.1",
				"label" : "Actors",
				"selected" : false,
				"parent" : "2",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "2.2",
				"label" : "Use case assumptions",
				"selected" : false,
				"parent" : "2",
				"drop" : [],
				"reference" : "",
				"children" : [
					{
						"id" : "2.3.1",
						"label" : "Pre Conditions",
						"selected" : false,
						"parent" : "2",
						"drop" : [],
						"reference" : ""
					},{
						"id" : "2.3.2",
						"label" : "Post Condition",
						"selected" : false,
						"parent" : "2",
						"drop" : [],
						"reference" : ""
					},{
						"id" : "2.3.3",
						"label" : "Functional Requirements",
						"selected" : false,
						"parent" : "2",
						"drop" : [],
						"reference" : ""
					}
				 ]
			}, {
				"id" : "2.3",
				"label" : "User story",
				"selected" : false,
				"parent" : "2",
				"drop" : [],
				"reference" : ""
			}, {
				"id" : "2.4",
				"label" : "Sequence diagram",
				"selected" : false,
				"parent" : "2",
				"drop" : [],
				"reference" : "",
				"children" : [
					{
						"id" : "2.4.1",
						"label" : "Acknolegements",
						"selected" : false,
						"parent" : "2",
						"drop" : [],
						"reference" : ""
					},{
						"id" : "2.4.2",
						"label" : "Error Handling",
						"selected" : false,
						"parent" : "2",
						"drop" : [],
						"reference" : ""
					}
				]
			}
			]
		};
		return rval;
	}
	
	svc.getMessageInfrastructure = function(profile) {
		var rval = {
				"id" : "3",
				"label" : "Message Infrastructure",
				"selected" : false,
				"parent" : "0",
				"drop" : [],
				"reference" : "",
				"children" : []
		}
		rval.children.push(svc.getTopEntry("3.1", "3", "Conformance Profiles", profile.messages));
		rval.children.push(svc.getTopEntry("3.2", "3", "Segments and Field Descriptions", profile.segments));		
		rval.children.push(svc.getTopEntry("3.3", "3", "Datatypes", profile.datatypes));
		rval.children.push(svc.getTopEntry("3.4", "3", "Value Sets", profile.tables));
		return rval;
	}
	
	// Returns a top level entry. It can be dropped on, but cannot be dragged.
	// It will accept a drop where the drag value matches its label.
	svc.getTopEntry = function(id, parent, label, fromProfile) {
		var children = [];
		var rval = {
			"id" : id,
			"label" : label,
			"selected" : false,
			"parent" : parent,
			"drop" : [id],
			"selected" : false,
		}
		if (fromProfile !== undefined) {
			rval["reference"] = fromProfile;
			rval["children"] = svc.createEntries(id, fromProfile.children);
		}
		return rval;
	}
	
	// Returns a second level set entries, These are draggable.  "drag" indicates
	// where one of these entries can be dropped.  
	svc.createEntries = function(parent, children) {
		var rval = [];
		var entry = {};
		_.each(children, function(child){
			if (parent === "3.1") {
				entry = svc.createEntry(child, child.id, child.name + " - " + child.description, parent, parent);
			} else if (parent === "3.2") {
				entry = svc.createEntry(child, child.id, child.name + " - " + child.description, parent, child.drop);
			} else if (parent === "3.3") {
				entry = svc.createEntry(child, child.id, child.name + " - " + child.description, parent, child.drop);
			} else if (parent === "3.4") {
				entry = svc.createEntry(child, child.id, child.bindingIdentifier + " - " + child.description, parent, child.drop);
			} else {
				entry = svc.createEntry(child, child.id, child.label, parent, child.drop, child.children);
			}
			rval.push(entry);
		});
//		return _.sortBy(rval, 'label');
		return rval;
	}
	
	svc.createEntry = function(reference, id, label, parent, drop, children) {
		var rval = {
			"id" : id,
			"label": label,
			"selected" : false,
		};
		if (reference !== undefined) {
			rval["reference"] = reference;
		}
		if (parent !== undefined) {
			rval["parent"] = parent;
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