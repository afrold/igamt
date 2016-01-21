angular.module('igl').factory(
		'CloneDeleteMessageSvc',
		function(ProfileAccessSvc) {

			var svc = this;

			svc.cloneMessage = function(igdocument, entry) {
				// TODO gcr: Need to include the user identifier in the
				// new label.
				// $rootScope.igdocument.metaData.ext should be just that,
				// but is currently
				// unpopulated in the profile.
				var newEntry = (JSON.parse(JSON.stringify(entry)));
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
				} else if (newEntry.reference.type === 'datatype') {
					newEntry.reference.label = newEntry.reference.label + "-"
					+ igdocument.profile.metaData.ext + "-"
					+ rand + "- flavor";
					newEntry.label = newEntry.reference.label;
					igdocument.profile.datatypes.children.splice(0, 0, newEntry.reference);
				} else if (newEntry.reference.type === 'table') {
					newEntry.reference.bindingIdentifier = newEntry.reference.bindingIdentifier + "-"
					+ igdocument.profile.metaData.ext + "-"
					+ rand + "- flavor";
					newEntry.label = newEntry.reference.bindingIdentifier;
					igdocument.profile.tables.children.splice(0, 0, newEntry.reference);
				}

				return newEntry;
			}

			svc.deleteMessage = function(igdocument, message) {
				// We do the delete in pairs: dead and live.  dead = things we are deleting and live = things we are keeping. 
				
				// We are deleting the message so it's dead.
				// The message there is from the ToC so what we need is its reference,
				// and it must be an array of one.
				var msgDead = [message];
				// We are keeping the children so their live.
				var msgLive = ProfileAccessSvc.Messages(igdocument.profile).messages();
				
				// First we remove the dead message from the living.
				var idxP = _.findIndex(msgLive, function (
						child) {
					return child.id === msgDead[0].id;
				});
				msgLive.splice(idxP, 1);
				
				// Second we get all segment refs that are contained in the dead message.
				var segmentRefsMerelyDead = ProfileAccessSvc.Messages(igdocument.profile)
						.getAllSegmentRefs(msgDead);
				// then we get all segment refs that are contained in the live messages.
				var segmentRefsLive = ProfileAccessSvc.Messages(igdocument.profile)
				.getAllSegmentRefs(msgLive);
				// Until now, dead meant mearly dead.  We now remove those that really are sincerely dead.
				var segmentRefsSincerelyDead = ProfileAccessSvc.Segments(igdocument.profile).findDead(segmentRefsMerelyDead, segmentRefsLive);
				if (segmentRefsSincerelyDead.length === 0) {
					return;
				}
				
				// Third we
				// get all datatypes that are contained in the sincerely dead segments.
				var dtIdsMerelyDead = ProfileAccessSvc.Segments(igdocument.profile).findDatatypesFromSegmentRefs(segmentRefsSincerelyDead);

				// then all datatypes that are contained in the live segments.				
				var dtIdsLive = ProfileAccessSvc.Segments(igdocument.profile).findDatatypesFromSegmentRefs(segmentRefsLive);
				var dtsIdsSincerelyDead = ProfileAccessSvc.Datatypes(igdocument.profile).findDead(dtIdsMerelyDead, dtIdsLive);

				// Fourth we 
				// get all value sets that are contained in the sincerely dead datatypes.
				var vssIdsMerelyDead = ProfileAccessSvc.Datatypes(igdocument.profile).findValueSetsFromDatatypeIds(dtsIdsSincerelyDead);
				// then all value sets that are contained in the live datatypes.
				var vssIdsLive = ProfileAccessSvc.Datatypes(igdocument.profile).findValueSetsFromDatatypeIds(dtIdsLive);
				var vssIdsSincerelyDead = ProfileAccessSvc.ValueSets(igdocument.profile).findDead(vssIdsMerelyDead, vssIdsLive);		

				ProfileAccessSvc.ValueSets(igdocument.profile).removeDead(vssIdsSincerelyDead);		
				ProfileAccessSvc.Datatypes(igdocument.profile).removeDead(dtsIdsSincerelyDead);
				ProfileAccessSvc.Segments(igdocument.profile).removeDead(segmentRefsSincerelyDead);
				
				console.log("svc.deleteMessage: segmentRefsMerelyDead=" + segmentRefsMerelyDead.length);
				console.log("svc.deleteMessage: segmentRefsLive=" + segmentRefsLive.length);
				console.log("svc.deleteMessage: segmentRefsSincerelyDead=" + segmentRefsSincerelyDead.length);

				console.log("svc.deleteMessage: dtIdsMerelyDead=" + dtIdsMerelyDead.length);
				console.log("svc.deleteMessage: dtIdsLive=" + dtIdsLive.length);
				console.log("svc.deleteMessage: dtsIdsSincerelyDead=" + dtsIdsSincerelyDead.length);

				console.log("svc.deleteMessage: vssIdsMerelyDead=" + vssIdsMerelyDead.length);
				console.log("svc.deleteMessage: vssIdsLive=" + vssIdsLive.length);
				console.log("svc.deleteMessage: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);

				console.log("svc.deleteMessage: aMsgs=" + ProfileAccessSvc.Messages(igdocument.profile).getMessageIds().length);
				console.log("svc.deleteMessage: aSegs=" + ProfileAccessSvc.Segments(igdocument.profile).segments().length);
				console.log("svc.deleteMessage: aDts=" + ProfileAccessSvc.Datatypes(igdocument.profile).datatypes().length);
				console.log("svc.deleteMessage: aVss=" + ProfileAccessSvc.ValueSets(igdocument.profile).valueSets().length);
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