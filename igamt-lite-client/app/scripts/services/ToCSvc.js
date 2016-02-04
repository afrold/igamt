angular.module('igl').factory(
		'ToCSvc',
		function() {

			var svc = this;
			
			function entry(id, label, position, parent, reference) { 
				this.id = id;
				this.label = label;
				this.selected = false;
				this.position = position;
				this.parent = parent;
				this.reference = reference;
			};

			svc.currentLeaf = {
				selected : false
			};

			svc.getToC = function(igdocument) {
				console.log("Getting toc...");
				toc = [];
				
				console.log("childSections=" + igdocument.childSections.length);
				var documentMetadata = getMetadata(igdocument.metaData, "documentMetadata");
				toc.push(documentMetadata);
//				var sections = getSections(igdocument.childSections);
//				toc.push(sections);
				var conformanceProfile = getMessageInfrastructure(igdocument.profile);
				toc.push(conformanceProfile);
				return _.flatten(toc);
			}
			
			function getMetadata(metaData, parent) {
				var rval = new entry(parent, "Metadata", 0, parent, metaData);
				return rval;
			}
			
			function getRootSections(igdocument, toc) {
				
				var sections = getSubSections(igdocument.childSections, igdocument.type);
				_.each(sections, function(section) {
					toc.push(section);
				});
 				
 				return toc;
			}
			
			function getSubSections(childSections, parent) {

				var rval = new entry;
				
 				_.each(childSections, function(childSection) {
	 					rval.id = childSection.id;
	 					rval.label = childSection.sectionTitle;
	 					rval.selected = false;
	 					rval.position = childSection.sectionPosition;
	 					rval.reference = childSection;
 					
					var sections1 = getSubSections(childSection.childSections, childSection._id);
					_.each(sections1, function(section1) {
						rval.childSections.push(section1);						
					});
				});
				var section2 = _.sortBy(rval.childSections, function(childSection1) { return childSection1.position; });
				rval.childSections = section2;
				return rval;
			}
			
			function getMessageInfrastructure(profile) {
				var rval = {
					"id" : profile.type,
					"label" : profile.sectionTitle,
					"selected" : false,
					"position" : profile.sectionPosition,
					"parent" : "0",
					"reference" : "",
					"children" : []
				}
				rval.children.push(getMetadata(profile.metaData, "profileMetadata"));
				rval.children.push(getTopEntry(profile.messages));
				rval.children.push(getTopEntry(profile.segments));
				rval.children.push(getTopEntry(profile.datatypes));
				rval.children.push(getTopEntry(profile.tables));
				return rval;
			}
			
//			function getProfileMetadata(profile) {
//				var metaData = {
//				"label" : "Metadata",
//				"selected" : false,
//				"position" : 0,
//				"reference" : profile.metaData
//				}				
//				return metaData;
//			}
			
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