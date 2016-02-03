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
				rval.children.push(getTopEntry(profile.messages));
				rval.children.push(getTopEntry(profile.segments));
				rval.children.push(getTopEntry(profile.datatypes));
				rval.children.push(getTopEntry(profile.tables));
				return rval;
			}
			
			function getProfileMetadata(profile) {
				var metaData = {
				"label" : "Metadata",
				"selected" : false,
				"position" : 0,
				"reference" : profile.metaData
				}				
				return metaData;
			}
			
			// Returns a top level entry. It can be dropped on, but cannot be
			// dragged.
			// It will accept a drop where the drag value matches its label.
			function getTopEntry(fromProfile) {
				var children = [];
				var rval = {
					"id" : fromProfile.type,
					"label" : fromProfile.sectionTitle,
					"position" : fromProfile.sectionPosition,
					"selected" : false,
				}
				if (fromProfile) {
					rval["reference"] = fromProfile;
					if(angular.isArray(fromProfile.children)) {
						rval["children"] = createEntries(fromProfile.type, fromProfile.children);
					}
				}
				return rval;
			}

			// Returns a second level set entries, These are draggable. "drag"
			function createEntries(parent, children) {
				var rval = [];
				var entry = {};
				_.each(children, function(child) {
					if(parent === "messages") {
						entry = createEntry(child, child.name, parent);
					} else if (parent === "tables") {
						entry = createEntry(child, child.bindingIdentifier, parent);
					} else {
						entry = createEntry(child, child.label, parent);
					}
					rval.push(entry);
				});
				if (parent === "messages") {
					return rval;
				} else {
					return _.sortBy(rval, "label");
				}
			}

			function createEntry(child, label, parent) {
				var rval = {
					"id" : child.type,
					"label" : label,
					"description" : child.description,
					"position" : child.sectionPosition,
					"selected" : false,
				};
				if (child) {
					rval["reference"] = child;
				}
				if (parent) {
					rval["parent"] = parent;
				}
//				if (angular.isArray(child.children)) {
//					rval["children"] = child.children;					}
//				}
				return rval;
			}

			return svc;
		})