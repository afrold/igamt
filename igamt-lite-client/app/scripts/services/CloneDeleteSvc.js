angular.module('igl').factory(
		'CloneDeleteSvc',
		function($rootScope, ProfileAccessSvc) {

			var svc = this;
			
			svc.cloneSectionFlavor = function(igdocument, entry) {
				var newEntry = angular.copy(entry);
				newEntry.reference.id = new ObjectId();
				var rand = Math.floor(Math.random() * 100);
				if (!igdocument.profile.metaData.ext) {
					igdocument.profile.metaData.ext = "";
				}
				newEntry.reference.name = newEntry.reference.name + "-"
				+ igdocument.profile.metaData.ext + "-"
				+ rand + "-"
				+ newEntry.reference.description;
				newEntry.label = newEntry.reference.name;
				igdocument.childSections.splice(0, 0, newEntry.reference);
				$rootScope.$broadcast('event:SetToC');	
				$rootScope.$broadcast('event:openSection', newEntry);	
			}
			
			svc.cloneSegmentFlavor = function(segment) {

		          var flavor = angular.copy(segment);
		            flavor.id = new ObjectId().toString();
		            flavor.label = $rootScope.createNewFlavorName(segment.label);
		            if (flavor.fields != undefined && flavor.fields != null && flavor.fields.length != 0) {
		                for (var i = 0; i < flavor.fields.length; i++) {
		                    flavor.fields[i].id = new ObjectId().toString();
		                }
		            }
		            var dynamicMappings = flavor['dynamicMappings'];
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
		            $rootScope.segments.splice(0, 0, flavor);
		            $rootScope.segment = flavor;
		            $rootScope.segment[flavor.id] = flavor;
		            $rootScope.recordChangeForEdit2('segment', "add", flavor.id, 'segment', flavor);
					$rootScope.$broadcast('event:SetToC');	
					$rootScope.$broadcast('event:openSegment', flavor);	
			}
			
			svc.cloneDatatypeFlavor = function(datatype) {

		          var flavor = angular.copy(datatype);
		            flavor.id = new ObjectId().toString();
		            flavor.label = $rootScope.createNewFlavorName(datatype.label);
		            if (flavor.components != undefined && flavor.components != null && flavor.components.length != 0) {
		                for (var i = 0; i < flavor.components.length; i++) {
		                    flavor.components[i].id = new ObjectId().toString();
		                }
		            }
		            var predicates = flavor['predicates'];
		            if (predicates != undefined && predicates != null && predicates.length != 0) {
		                angular.forEach(predicates, function (predicate) {
		                    predicate.id = new ObjectId().toString();
		                });
		            }
		            var conformanceStatements = flavor['conformanceStatements'];
		            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
		                angular.forEach(conformanceStatements, function (conformanceStatement) {
		                    conformanceStatement.id = new ObjectId().toString();
		                });
		            }
		            $rootScope.datatypes.splice(0, 0, flavor);
		            $rootScope.datatype = flavor;
		            $rootScope.datatypesMap[flavor.id] = flavor;
		            $rootScope.recordChangeForEdit2('datatype', "add", flavor.id, 'datatype', flavor);
					$rootScope.$broadcast('event:SetToC');	
					$rootScope.$broadcast('event:openDatatype', flavor);	
			}

			svc.cloneTableFlavor = function(table) {

		        $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
		        var newTable = angular.fromJson({
		            id:new ObjectId().toString(),
		            type: '',
		            bindingIdentifier: '',
		            name: '',
		            version: '',
		            oid: '',
		            tableType: '',
		            stability: '',
		            extensibility: '',
		            codes: []
		        });
		        newTable.type = 'table';
		        newTable.bindingIdentifier = table.bindingIdentifier + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
		        newTable.name = table.name + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
		        newTable.version = table.version;
		        newTable.oid = table.oid;
		        newTable.tableType = table.tableType;
		        newTable.stability = table.stability;
		        newTable.extensibility = table.extensibility;

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

		        $rootScope.tables.push(newTable);
		        $rootScope.table = newTable;
		        $rootScope.tablesMap[newTable.id] = newTable;
		        
		        $rootScope.codeSystems = [];
		        
		        for (var i = 0; i < $rootScope.table.codes.length; i++) {
		        	if($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0){
		        		if($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== ''){
		        			$rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
		        		}
					}
		    	}
		     
		        $rootScope.recordChangeForEdit2('table', "add", newTable.id,'table', newTable);
				$rootScope.$broadcast('event:SetToC');	
				$rootScope.$broadcast('event:openTable', newTable);	
			}
			
			svc.cloneMessage = function(igdocument, entry) {
				// TODO gcr: Need to include the user identifier in the
				// new label.
				// $rootScope.igdocument.metaData.ext should be just that,
				// but is currently
				// unpopulated in the profile.
				var newEntry = angular.copy(entry);
				newEntry.reference.id = new ObjectId();
				var rand = Math.floor(Math.random() * 100);
				if (!igdocument.profile.metaData.ext) {
					igdocument.profile.metaData.ext = "";
				}
				// Nodes must have unique names so we append a random number when we
				// duplicate.
				if (newEntry.reference.type === 'message') {
					newEntry.reference.name = newEntry.reference.name + "-"
							+ igdocument.profile.metaData.ext + "-"
							+ rand + "-"
							+ newEntry.reference.description;
					newEntry.label = newEntry.reference.name;
					igdocument.profile.messages.children.splice(0, 0, newEntry.reference);
				}

				return newEntry;
			}
						
			svc.deleteValueSet = function(igdocument, valueSet) {
				return deleteValueSets(igdocument, [valueSet.id]);
			}
			
			function deleteValueSets(igdocument, vssIdsSincerelyDead) {
				console.log("deleteValueSets: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);
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

				console.log("deleteDatatypes: vssIdsMerelyDead=" + vssIdsMerelyDead.length);
				console.log("deleteDatatypes: vssIdsLive=" + vssIdsLive.length);
				console.log("deleteDatatypes: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);
				
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

				console.log("deleteSegments: dtIdsMerelyDead=" + dtIdsMerelyDead.length);
				console.log("deleteSegments: dtIdsLive=" + dtIdsLive.length);
				console.log("deleteSegments: dtsIdsSincerelyDead=" + dtsIdsSincerelyDead.length);

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
					console.log("Zero dead==>");			
					return;
				}
				
				var rval = deleteSegments(igdocument, segmentRefsLive, segmentRefsSincerelyDead);
				
				console.log("svc.deleteMessage: segmentRefsMerelyDead=" + segmentRefsMerelyDead.length);
				console.log("svc.deleteMessage: segmentRefsLive=" + segmentRefsLive.length);
				console.log("svc.deleteMessage: segmentRefsSincerelyDead=" + segmentRefsSincerelyDead.length);

				console.log("svc.deleteMessage: aMsgs=" + ProfileAccessSvc.Messages(igdocument.profile).messages().length);
				console.log("svc.deleteMessage: aSegs=" + ProfileAccessSvc.Segments(igdocument.profile).segments().length);
				console.log("svc.deleteMessage: aDts=" + ProfileAccessSvc.Datatypes(igdocument.profile).datatypes().length);
				console.log("svc.deleteMessage: aVss=" + ProfileAccessSvc.ValueSets(igdocument.profile).valueSets().length);
				
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

			svc.getMessages = function(toc) {
				var ConformanceProfile = _.find(toc, function(child) {
					return child.id === "3";
				});
			
				var messages = _.find(ConformanceProfile.children, function(
						child) {
					return child.id === "3.1";
				});
				return messages;
			}

			svc.findMessageIndex = function(messages, id) {
				var idxT = _.findIndex(messages.children, function(child) {
					return child.reference.id === id;
				})
				return idxT;
			}

			return svc;
		});