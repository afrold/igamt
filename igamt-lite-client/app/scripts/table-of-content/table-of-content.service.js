angular
		.module('igl')
		.factory(
				'ToCSvc',
				function(FilteringSvc, MastermapSvc,$rootScope) {

					var svc = this;

					function entry(id, label, position, type, parent, reference) {
						this.id = id;
						this.label = label;
						this.selected = false;
						this.position = position;
						this.type = type;
						this.parent = parent;
						this.reference = reference;
					}
					;

					svc.currentLeaf = {
						selected : false
					};

					svc.findEntryFromRefId = function(refId, entries) {
						var rval = undefined;
						if (angular.isArray(entries)) {
							_.each(entries, function(entry) {
								if (entry.reference && entry.reference.id) {
									if (entry.reference.id === refId) {
										rval = entry;
									} else {
										if (rval) {
											return rval;
										}
										rval = svc.findEntryFromRefId(refId,
												entry.children);
									}
								}
							});
						}
						return rval;
					};

					svc.getToC = function(igdocument) {
//						console.log("Getting toc... version="
//								+ igdocument.profile.metaData.hl7Version + " "
//								+ igdocument.id);
						toc = [];

//						console.log("childSections=" +
//						igdocument.childSections.length);
						var documentMetadata = getMetadata(igdocument,
								"documentMetadata");
						toc.push(documentMetadata);
						var sections = getSections(igdocument.childSections,
								igdocument.type, igdocument);
						_.each(sections, function(section) {
							toc.push(section);
						});
						var conformanceProfile = getMessageInfrastructure(igdocument);
						toc.push(conformanceProfile);
//             console.log("toc=" + toc);
						return toc;
					};

					function getMetadata(parent, type) {
						var rval = new entry(type, "Metadata", 0, type, parent,
								parent.metaData);
						return rval;
					}
					;

					function getSections(childSections, parentType, parent) {

						var rval = [];

						_.each(childSections, function(childSection) {
							var section = new entry(childSection.id,
									childSection.sectionTitle,
									childSection.sectionPosition,
									childSection.type, parent, childSection);
							rval.push(section);
							var sections1 = getSections(
									childSection.childSections,
									childSection.type, childSection);
							_.each(sections1, function(section1) {
								if (!section.children) {
									section.children = [];
								}
								section.children.push(section1);
							});
						});
						var section2 = _.sortBy(rval, "position");
						rval = section2;
						return rval;
					}
					;

					function getMessageInfrastructure(igdocument) {
						var rval = new entry(igdocument.profile.id,
								igdocument.profile.sectionTitle,
								igdocument.profile.sectionPosition,
								igdocument.profile.type, 0, igdocument.profile);

                        var datatypes = angular.copy(igdocument.profile.datatypeLibrary);
                        datatypes.children = $rootScope.datatypes;
                        var segments = angular.copy(igdocument.profile.segmentLibrary);
                        segments.children = $rootScope.segments;
                        var tables = angular.copy(igdocument.profile.tableLibrary);
                        tables.children = $rootScope.tables;

                        var children = [];
						children.push(getMetadata(igdocument.profile,
								"profileMetadata"));
						children.push(getTopEntry(igdocument.profile.messages,
								igdocument.profile));
						children.push(getTopEntry(segments,
								igdocument.profile));
						children.push(getTopEntry(datatypes,
								igdocument.profile));
						children.push(getTopEntry(tables,
								igdocument.profile));
						rval.children = children;
						return rval;
					}
					;

					// Returns a top level entry. It can be dropped on, but
					// cannot be
					// dragged.
					function getTopEntry(child, parent) {
						// console.log("getTopEntry sectionTitle=" +
						// child.sectionTitle);
						// console.log("getTopEntry type=" + child.type);
						var children = [];
						var rval = new entry(child.id, child.sectionTitle,
								child.sectionPosition, child.type, parent,
								child);
						if (child) {
							rval["reference"] = child;
							if (angular.isArray(child.children)
									&& child.children.length > 0) {
								rval["children"] = createEntries(
										child.children[0].type, child,
										child.children);
							}
						}
						return rval;
					}
					;

					// Returns a second level set entries, These are draggable.
					// "drag"
					function createEntries(parentType, parent, children) {
						var rval = [];
						var entry = {};
						_
								.each(
										children,
										function(child) {
											if (parentType === "message") {
												entry = createEntry(child,
														child.name, parent);
//												console
//														.log("createEntries entry.reference.id="
//																+ entry.reference.id
//																+ " entry.reference.name="
//																+ entry.reference.name
//																+ "entry.parent="
//																+ rval.parent);
											} else if (parentType === "table") {
												entry = createEntry(
														child,
														child.bindingIdentifier,
														parent);
											} else if (parentType === "datatype") {
												var label = $rootScope.getDatatypeLabel(child);
//												console.log("ToC datatype label=" + label);
												entry = createEntry(child,
														label, parent);
											}  else {
												entry = createEntry(child,
														child.label, parent);
											}
											rval.push(entry);
										});
						return rval;
//						return _.sortBy(rval, "label");
					}
					;

					function createEntry(child, label, parent) {

						var rval = new entry(child.id, label,
								child.sectionPosition, child.type, parent,
								child);
						return rval;
					};

          return svc;
				})
