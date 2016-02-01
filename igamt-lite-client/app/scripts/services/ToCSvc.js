angular.module('igl').factory(
		'ToCSvc',
		function() {

			var svc = this;

			svc.currentLeaf = {
				selected : false
			};

			svc.getToC = function(igdocument) {
				console.log("Getting toc...");
				toc = [];
				console.log("childSections=" + igdocument.childSections.length);
				var sections = svc.getSections(igdocument.childSections);
				var conformanceProfile = svc.getMessageInfrastructure(igdocument.profile);
				toc.push(sections);
				toc.push(conformanceProfile);
				return _.flatten(toc);
			}
			
			svc.getSections = function(childSections, parent) {
				
				var rval = [];

 				_.each(childSections, function(childSection) {
					var section = { "id" : childSection.id,
					"label" : childSection.sectionTitle,
					"selected" : false,
					"position" : childSection.sectionPosition,
					"parent" : parent,
					"reference" : childSection
					}
					var sections1 = svc.getSections(childSection.childSections, childSection._id);
					_.each(sections1, function(section1) {
						if (!section.children) {
							section.children = [];
						}
						section.children.push(section1);						
					})
					rval.push(section);
				});

				return rval;
			}
			
			svc.getMessageInfrastructure = function(profile) {
				var rval = {
					"id" : "3",
					"label" : profile.sectionTitle,
					"selected" : false,
					"position" : profile.sectionPosition,
					"parent" : "0",
					"reference" : "",
					"children" : []
				}
				rval.children.push(svc.getTopEntry("3.1", "3",
						profile.messages.sectionTitle, profile.messages));
				rval.children.push(svc.getTopEntry("3.2", "3",
						profile.segments.sectionTitle, profile.segments));
				rval.children.push(svc.getTopEntry("3.3", "3", profile.datatypes.sectionTitle,
						profile.datatypes));
				rval.children.push(svc.getTopEntry("3.4", "3", profile.tables.sectionTitle,
						profile.tables));
				return rval;
			}

			// Returns a top level entry. It can be dropped on, but cannot be
			// dragged.
			// It will accept a drop where the drag value matches its label.
			svc.getTopEntry = function(id, parent, label, fromProfile) {
				var children = [];
				var rval = {
					"id" : id,
					"label" : label,
					"selected" : false,
					"parent" : parent,
					"selected" : false,
				}
				if (fromProfile !== undefined) {
					rval["reference"] = fromProfile;
					rval["children"] = svc.createEntries(id,
							fromProfile.children);
				}
				return rval;
			}

			// Returns a second level set entries, These are draggable. "drag"
			// indicates
			// where one of these entries can be dropped.
			svc.createEntries = function(parent, children) {
				var rval = [];
				var entry = {};
				_.each(children, function(child) {
					if (parent === "3.1") {
						entry = svc.createEntry(child, child.id, child.name
								+ " - " + child.sectionTitle, parent, parent);
					} else if (parent === "3.2") {
						entry = svc
								.createEntry(child, child.id, child.label
										+ " - " + child.sectionTitle, parent);
					} else if (parent === "3.3") {
						entry = svc
								.createEntry(child, child.id, child.label
										+ " - " + child.sectionTitle, parent);
					} else if (parent === "3.4") {
						entry = svc
								.createEntry(child, child.id,
										child.bindingIdentifier + " - "
												+ child.sectionTitle, parent);
					} else {
						entry = svc.createEntry(child, child.id, child.label,
								parent, child.children);
					}
					rval.push(entry);
				});
				if (parent === "3.1") {
					return rval;
				} else {
					return _.sortBy(rval, 'label');
				}
			}

			svc.createEntry = function(reference, id, label, parent,
					children) {
				var rval = {
					"id" : id,
					"label" : label,
					"selected" : false,
				};
				if (reference !== undefined) {
					rval["reference"] = reference;
				}
				if (parent !== undefined) {
					rval["parent"] = parent;
				}
				if (children !== undefined) {
					if (children.length > 0) {
						rval["children"] = children;
					}
				}
				return rval;
			}

			return svc;
		})