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
				
//				console.log("childSections=" + igdocument.childSections.length);
				var documentMetadata = getMetadata(igdocument.metaData, "documentMetadata");
				toc.push(documentMetadata);
				var sections = getSections(igdocument.childSections, igdocument.type);
				_.each(sections, function(section){
					toc.push(section);
				});
				var conformanceProfile = getMessageInfrastructure(igdocument.profile);
				toc.push(conformanceProfile);
				return toc;
			}
			
			function getMetadata(metaData, parent) {
				var rval = new entry(parent, "Metadata", 0, parent, metaData);
				return rval;
			}
			
			function getSections(childSections, parent) {

				var rval = [];
				
 				_.each(childSections, function(childSection) {
 					var section = new entry(parent, childSection.sectionTitle, childSection.sectionPosition, childSection.type, childSection);
 					rval.push(section);	
					var sections1 = getSections(childSection.childSections, childSection.type);
					_.each(sections1, function(section1) {
						if (!section.childSections) {
							section.children = [];
						}
						section.children.push(section1);						
					});
				});
				var section2 = _.sortBy(rval, "position");
				rval = section2;
				return rval;
			}
			
			function getMessageInfrastructure(profile) {
				var rval = new entry(profile.type, profile.sectionTitle, profile.sectionPosition, 0, profile);
				var children = [];
				children.push(getMetadata(profile.metaData, "profileMetadata"));
				children.push(getTopEntry(profile.messages));
				children.push(getTopEntry(profile.segments));
				children.push(getTopEntry(profile.datatypes));
				children.push(getTopEntry(profile.tables));
				rval.children = children;
				return rval;
			}
			
			// Returns a top level entry. It can be dropped on, but cannot be
			// dragged.
			// It will accept a drop where the drag value matches its label.
			function getTopEntry(profile) {
				var children = [];
				var rval = new entry(profile.type, profile.sectionTitle, profile.sectionPosition, 0, profile);
				if (profile) {
					rval["reference"] = profile;
					if(angular.isArray(profile.children)) {
						rval["children"] = createEntries(profile.children[0].type, profile.children);
					}
				}
				return rval;
			}

			// Returns a second level set entries, These are draggable. "drag"
			function createEntries(parent, children) {
				var rval = [];
				var entry = {};
				_.each(children, function(child) {
					if(parent === "message") {
						entry = createEntry(child, child.name, parent);
//						console.log("createEntries entry.reference.name=" + entry.reference.name + " entry.parent=" + rval.parent);
					} else if (parent === "table") {
						entry = createEntry(child, child.bindingIdentifier, parent);
					} else {
						entry = createEntry(child, child.label, parent);
					}
					rval.push(entry);
				});
				if (parent === "message") {
					return rval;
				} else {
					return _.sortBy(rval, "label");
				}
			}

			function createEntry(child, label, parent) {
				
				var rval = new entry(child.id, label, child.sectionPosition, child.type, child);
				return rval;
			}

			return svc;
		})