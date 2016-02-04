angular.module('igl').factory(
		'CloneDeleteSvc',
		function($rootScope, ProfileAccessSvc) {

			var svc = this;
			
			svc.cloneSectionFlavor = function(section) {
				var newSection = angular.copy(section);
				newSection.id = new ObjectId();
				var rand = Math.floor(Math.random() * 100);
				if (!$rootScope.igdocument.profile.metaData.ext) {
					$rootScope.igdocument.profile.metaData.ext = "";
				}
				newSection.sectionTitle = section.sectionTitle + "-"
				+ $rootScope.igdocument.profile.metaData.ext + "-"
				+ rand;
				newSection.label = newSection.sectionTitle;
				$rootScope.igdocument.childSections.splice(0, 0, newSection);
				$rootScope.$broadcast('event:SetToC');	
				$rootScope.$broadcast('event:openSection', newSection);	
			}
			
			svc.cloneSegmentFlavor = function(segment) {

		          var newSegment = angular.copy(segment);
		            newSegment.id = new ObjectId().toString();
		            newSegment.label = $rootScope.createNewFlavorName(segment.label);
		            if (newSegment.fields != undefined && newSegment.fields != null && newSegment.fields.length != 0) {
		                for (var i = 0; i < newSegment.fields.length; i++) {
		                    newSegment.fields[i].id = new ObjectId().toString();
		                }
		            }
		            var dynamicMappings = newSegment['dynamicMappings'];
		            if (dynamicMappings != undefined && dynamicMappings != null && dynamicMappings.length != 0) {
		                angular.forEach(dynamicMappings, function (dynamicMapping) {
		                	dynamicMapping.id = new ObjectId().toString();
		                		angular.forEach(dynamicMapping.mappings, function (mapping) {
		                			mapping.id = new ObjectId().toString();
//			                		angular.forEach(mapping.cases, function (case) {
//			                			case.id = new ObjectId().toString();
//			                		});
		                		});
		                });
		            }
//		            $rootScope.segments.splice(0, 0, newSegment);
		            $rootScope.igdocument.profile.segments.children.splice(0, 0, newSegment);
		            $rootScope.segment = newSegment;
		            $rootScope.segment[newSegment.id] = newSegment;
		            $rootScope.recordChanged();
					$rootScope.$broadcast('event:SetToC');	
					$rootScope.$broadcast('event:openSegment', newSegment);	
			}
			
			svc.cloneDatatypeFlavor = function(datatype) {

		          var newDatatype = angular.copy(datatype);
		            newDatatype.id = new ObjectId().toString();
		            newDatatype.label = $rootScope.createNewFlavorName(datatype.label);
		            if (newDatatype.components != undefined && newDatatype.components != null && newDatatype.components.length != 0) {
		                for (var i = 0; i < newDatatype.components.length; i++) {
		                    newDatatype.components[i].id = new ObjectId().toString();
		                }
		            }
		            var predicates = newDatatype['predicates'];
		            if (predicates != undefined && predicates != null && predicates.length != 0) {
		                angular.forEach(predicates, function (predicate) {
		                    predicate.id = new ObjectId().toString();
		                });
		            }
		            var conformanceStatements = newDatatype['conformanceStatements'];
		            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
		                angular.forEach(conformanceStatements, function (conformanceStatement) {
		                    conformanceStatement.id = new ObjectId().toString();
		                });
		            }
//		            $rootScope.datatypes.splice(0, 0, newDatatype);
		            $rootScope.igdocument.profile.datatypes.children.splice(0, 0, newDatatype);
		            $rootScope.datatype = newDatatype;
		            $rootScope.datatypesMap[newDatatype.id] = newDatatype;
		            $rootScope.recordChanged();
					$rootScope.$broadcast('event:SetToC');	
					$rootScope.$broadcast('event:openDatatype', newDatatype);	
			}

			svc.cloneTableFlavor = function(table) {

	          var newTable = angular.copy(table);
	          newTable.id = new ObjectId().toString();
		        newTable.bindingIdentifier = table.bindingIdentifier + $rootScope.createNewFlavorName(table.bindingIdentifier);
//		        $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
//		        var newTable = angular.fromJson({
//		            id:new ObjectId().toString(),
//		            type: '',
//		            bindingIdentifier: '',
//		            name: '',
//		            version: '',
//		            oid: '',
//		            tableType: '',
//		            stability: '',
//		            extensibility: '',
//		            codes: []
//		        });
//		        newTable.type = 'table';
//		        newTable.bindingIdentifier = table.bindingIdentifier + $rootScope.createNewFlavorName(table.bindingIdentifier);
//		        newTable.name = table.name + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
//		        newTable.version = table.version;
//		        newTable.oid = table.oid;
//		        newTable.tableType = table.tableType;
//		        newTable.stability = table.stability;
//		        newTable.extensibility = table.extensibility;

		        for (var i = 0, len1 = table.codes.length; i < len1; i++) {
		            $rootScope.newValueFakeId = $rootScope.newValueFakeId - 1;
		            var newValue = {
		                    id: new ObjectId().toString(),
		                    type: 'value',
		                    value: table.codes[i].value,
		                    label: table.codes[i].label,
		                    codeSystem: table.codes[i].codeSystem,
		                    codeUsage: table.codes[i].codeUsage
		                };
		            
		            newTable.codes.push(newValue);
		        }

//		        $rootScope.tables.push(newTable);
		        $rootScope.table = newTable;
		        $rootScope.tablesMap[newTable.id] = newTable;
		        
		        $rootScope.codeSystems = [];
		        
		        for (var i = 0; i < $rootScope.table.codes.length; i++) {
		        	if($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
		        		if($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== ''){
		        			$rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
		        		}
					}
		    		}
		     
		        $rootScope.igdocument.profile.tables.children.splice(0, 0, newTable);
	            $rootScope.recordChanged();
				$rootScope.$broadcast('event:SetToC');	
				$rootScope.$broadcast('event:openTable', newTable);	
			}
			
			svc.cloneMessage = function(igdocument, message) {
				// TODO gcr: Need to include the user identifier in the
				// new label.
				// $rootScope.igdocument.metaData.ext should be just that,
				// but is currently
				// unpopulated in the profile.
				var newMessage = angular.copy(message);
				newMessage.id = new ObjectId().toString();
				var groups = ProfileAccessSvc.Messages(igdocument.profile).getGroups(newMessage);
				angular.forEach(groups, function(group) {
					group.id = new ObjectId().toString();
				});
				newMessage.name = message.name + $rootScope.createNewFlavorName(message.name);
				igdocument.profile.messages.children.splice(0, 0, newMessage);
				
				return newMessage;
			}
						
			svc.deleteValueSet = function(igdocument, valueSet) {
				return deleteValueSets(igdocument, [valueSet.id]);
			}
			
			function deleteValueSets(igdocument, vssIdsSincerelyDead) {
//				console.log("deleteValueSets: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);
				return ProfileAccessSvc.ValueSets(igdocument.profile).removeDead(vssIdsSincerelyDead);		
			}
						
			svc.deleteDatatype = function(igdocument, datatype) {
				var dtIdsLive = ProfileAccessSvc.Datatypes(igdocument.profile).getAllDatatypeIds();
				var idxP = _.findIndex(dtIdsLive, function (
						child) {
					return child.id === datatype.id;
				});
				dtIdsLive.splice(idxP, 1);
				
				return deleteDatatypes(igdocument, dtIdsLive, [datatype.id])
			}
			
			function deleteDatatypes(igdocument, dtIdsLive, dtsIdsSincerelyDead) {

				// Get all value sets that are contained in the sincerely dead datatypes.
				var vssIdsMerelyDead = ProfileAccessSvc.Datatypes(igdocument.profile).findValueSetsFromDatatypeIds(dtsIdsSincerelyDead);
				// then all value sets that are contained in the live datatypes.
				var vssIdsLive = ProfileAccessSvc.Datatypes(igdocument.profile).findValueSetsFromDatatypeIds(dtIdsLive);
				var vssIdsSincerelyDead = ProfileAccessSvc.ValueSets(igdocument.profile).findDead(vssIdsMerelyDead, vssIdsLive);		
				deleteValueSets(igdocument, vssIdsSincerelyDead);
				
				var rval = ProfileAccessSvc.Datatypes(igdocument.profile).removeDead(dtsIdsSincerelyDead);		

//				console.log("deleteDatatypes: vssIdsMerelyDead=" + vssIdsMerelyDead.length);
//				console.log("deleteDatatypes: vssIdsLive=" + vssIdsLive.length);
//				console.log("deleteDatatypes: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);
				
				return rval;
			}

			svc.deleteSegment = function(igdocument, segment) {

				// Get all datatypes that are contained in the sincerely dead segments.
				var segmentRefsLive = ProfileAccessSvc.Segments(igdocument.profile).getAllSegmentIds();
				var idxP = _.findIndex(segmentRefsLive, function (
						child) {
					return child.id === segment.id;
				});
				segmentRefsLive.splice(idxP, 1);

				var rval = deleteSegments(igdocument, segmentRefsLive, [segment.id])
				
				return rval;
			}
			
			function deleteSegments(igdocument, segmentRefsLive, segmentRefsSincerelyDead) {

				// Get all datatypes that are contained in the sincerely dead segments.
				var dtIdsMerelyDead = ProfileAccessSvc.Segments(igdocument.profile).findDatatypesFromSegmentRefs(segmentRefsSincerelyDead);

				// then all datatypes that are contained in the live segments.				
				var dtIdsLive = ProfileAccessSvc.Segments(igdocument.profile).findDatatypesFromSegmentRefs(segmentRefsLive);
				var dtsIdsSincerelyDead = ProfileAccessSvc.Datatypes(igdocument.profile).findDead(dtIdsMerelyDead, dtIdsLive);
				deleteDatatypes(igdocument, dtIdsLive, dtsIdsSincerelyDead);
				
				var rval = ProfileAccessSvc.Segments(igdocument.profile).removeDead(segmentRefsSincerelyDead);				

//				console.log("deleteSegments: dtIdsMerelyDead=" + dtIdsMerelyDead.length);
//				console.log("deleteSegments: dtIdsLive=" + dtIdsLive.length);
//				console.log("deleteSegments: dtsIdsSincerelyDead=" + dtsIdsSincerelyDead.length);

				return rval;
			}

			svc.deleteMessage = function(igdocument, message) {
				// We do the delete in pairs: dead and live.  dead = things we are deleting and live = things we are keeping. 
				
				// We are deleting the message so it's dead.
				// The message there is from the ToC so what we need is its reference,
				// and it must be an array of one.
				var msgDead = [message];
				// We are keeping the children so their live.
				var msgLive = ProfileAccessSvc.Messages(igdocument.profile).messages();
				
				// We remove the dead message from the living.
				var idxP = _.findIndex(msgLive, function (
						child) {
					return child.id === msgDead[0].id;
				});
				msgLive.splice(idxP, 1);
				if (0 === ProfileAccessSvc.Messages(igdocument.profile).messages().length) {
					ProfileAccessSvc.ValueSets(igdocument.profile).truncate();
					ProfileAccessSvc.Datatypes(igdocument.profile).truncate();
					ProfileAccessSvc.Segments(igdocument.profile).truncate();
					return;
				}
				// We get all segment refs that are contained in the dead message.
				var segmentRefsMerelyDead = ProfileAccessSvc.Messages(igdocument.profile)
						.getAllSegmentRefs(msgDead);
				// We get all segment refs that are contained in the live messages.
				var segmentRefsLive = ProfileAccessSvc.Messages(igdocument.profile)
				.getAllSegmentRefs(msgLive);
				// Until now, dead meant mearly dead.  We now remove those that are most sincerely dead.
				var segmentRefsSincerelyDead = ProfileAccessSvc.Segments(igdocument.profile).findDead(segmentRefsMerelyDead, segmentRefsLive);
				if (segmentRefsSincerelyDead.length === 0) {
//					console.log("Zero dead==>");			
					return;
				}
				
				var rval = deleteSegments(igdocument, segmentRefsLive, segmentRefsSincerelyDead);
				
//				console.log("svc.deleteMessage: segmentRefsMerelyDead=" + segmentRefsMerelyDead.length);
//				console.log("svc.deleteMessage: segmentRefsLive=" + segmentRefsLive.length);
//				console.log("svc.deleteMessage: segmentRefsSincerelyDead=" + segmentRefsSincerelyDead.length);
//
//				console.log("svc.deleteMessage: aMsgs=" + ProfileAccessSvc.Messages(igdocument.profile).messages().length);
//				console.log("svc.deleteMessage: aSegs=" + ProfileAccessSvc.Segments(igdocument.profile).segments().length);
//				console.log("svc.deleteMessage: aDts=" + ProfileAccessSvc.Datatypes(igdocument.profile).datatypes().length);
//				console.log("svc.deleteMessage: aVss=" + ProfileAccessSvc.ValueSets(igdocument.profile).valueSets().length);
				
				return rval;
			}
			
			svc.deleteSection = function(igdocument, secDead) {

				// We are keeping the children so their live.
				var secLive = igdocument.childSections;
				
				// We remove the dead message from the living.
				var idxP = _.findIndex(secLive, function (
						child) {
					return child.id === secDead.id;
				});
				secLive.splice(idxP, 1);
			}

//			svc.getMessages = function(toc) {
//				var ConformanceProfile = _.find(toc, function(child) {
//					return child.id === "3";
//				});
//			
//				var messages = _.find(ConformanceProfile.children, function(
//						child) {
//					return child.id === "3.1";
//				});
//				return messages;
//			}

			svc.findMessageIndex = function(messages, id) {
				var idxT = _.findIndex(messages.children, function(child) {
					return child.reference.id === id;
				})
				return idxT;
			}

			return svc;
		});