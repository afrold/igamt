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
				var documentMetadata = getDocumentMetadata(igdocument);
				toc.push(documentMetadata);
				var sections = getSections(igdocument.childSections);
				toc.push(sections);
				var conformanceProfile = getMessageInfrastructure(igdocument.profile);
				toc.push(conformanceProfile);
				return _.flatten(toc);
			}
			
			function getDocumentMetadata(igdocument) {
				var metaData = {
				"label" : "Metadata",
				"selected" : false,
				"position" : 0,
				"parent" : parent,
				"reference" : igdocument.metaData
			}				
				return metaData;
			}
			
			function getSections(childSections, parent) {
				
				var rval = [];

 				_.each(childSections, function(childSection) {
					var section = { "id" : childSection.id,
					"label" : childSection.sectionTitle,
					"selected" : false,
					"position" : childSection.sectionPosition,
					"parent" : parent,
					"reference" : childSection
					}
					var sections1 = getSections(childSection.childSections, childSection._id);
					_.each(sections1, function(section1) {
						if (!section.children) {
							section.children = [];
						}
						section.children.push(section1);						
					})
					var section2 = _.sortBy(sections1, function(section1) { return section1.position; });
					rval.push(section2);
				});

				return rval;
			}
			
			function getMessageInfrastructure(profile) {
				var rval = {
					"id" : "3",
					"label" : profile.sectionTitle,
					"selected" : false,
					"position" : profile.sectionPosition,
					"parent" : "0",
					"reference" : "",
					"children" : []
				}
				rval.children.push(getProfileMetadata(profile));
				rval.children.push(getTopEntry("3.1", "3",
						profile.messages.sectionTitle, profile.messages));
				rval.children.push(getTopEntry("3.2", "3",
						profile.segments.sectionTitle, profile.segments));
				rval.children.push(getTopEntry("3.3", "3", profile.datatypes.sectionTitle,
						profile.datatypes));
				rval.children.push(getTopEntry("3.4", "3", profile.tables.sectionTitle,
						profile.tables));
				return rval;
			}
			
			function getProfileMetadata(profile) {
				var metaData = {
				"label" : "Metadata",
				"selected" : false,
				"position" : 0,
				"parent" : parent,
				"reference" : profile.metaData
				}				
				return metaData;
			}
			
			// Returns a top level entry. It can be dropped on, but cannot be
			// dragged.
			// It will accept a drop where the drag value matches its label.
			function getTopEntry(id, parent, label, fromProfile) {
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
					rval["children"] = createEntries(id,
							fromProfile.children);
				}
				return rval;
			}

			// Returns a second level set entries, These are draggable. "drag"
			// indicates
			// where one of these entries can be dropped.
			function createEntries(parent, children) {
				var rval = [];
				var entry = {};
				_.each(children, function(child) {
					if (parent === "3.1") {
						entry = createEntry(child, parent);
					} else if (parent === "3.2") {
						entry = createEntry(child, parent);
					} else if (parent === "3.3") {
						entry = createEntry(child, parent);
					} else if (parent === "3.4") {
						entry = createEntry(child, parent);
					} else {
						entry = createEntry(child, parent);
					}
					rval.push(entry);
				});
				if (parent === "3.1") {
					return rval;
				} else {
					return _.sortBy(rval, 'label');
				}
			}

			function createEntry(child, parent) {
				var rval = {
					"id" : id,
					"label" : child.label,
					"description" : child.description,
					"selected" : false,
				};
				if (reference !== undefined) {
					rval["reference"] = child;
				}
				if (parent !== undefined) {
					rval["parent"] = parent;
				}
				if (child.children !== undefined) {
					if (child.children.length > 0) {
						rval["children"] = child.children;
					}
				}
				return rval;
			}

			return svc;
		})