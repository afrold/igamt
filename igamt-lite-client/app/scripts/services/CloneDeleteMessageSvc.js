angular.module('igl').factory(
		'CloneDeleteMessageSvc',
		function(ProfileAccessSvc) {

			var svc = this;

			svc.cloneMessage = function(profile, message) {
				// TODO gcr: Need to include the user identifier in the
				// new label.
				// $rootScope.profile.metaData.ext should be just that,
				// but is currently
				// unpopulated in the profile.
				var newMessage = (JSON.parse(JSON.stringify(message)));
				newMessage.reference.id = new ObjectId();

				// Nodes must have unique names so we append a random number when we
				// duplicate.
				if (newMessage.reference.type === 'message') {
					newMessage.reference.name = newMessage.reference.name + "-"
							+ profile.metaData.ext + "-"
							+ Math.floor(Math.random() * 100) + "-"
							+ newMessage.reference.description;
					newMessage.label = newMessage.reference.name;
				}

				profile.messages.children.splice(0, 0, newMessage.reference);
				return newMessage;
			}

			svc.deleteMessage = function(profile, message) {
				// We do the delete in pairs: dead and live.  dead = things we are deleting and live = things we are keeping. 
				
				// We are deleting the message so it's dead.
				// The message there is from the ToC so what we need is its reference,
				// and it must be an array of one.
				var msgDead = [message];
				// We are keeping the children so their live.
				var msgLive = ProfileAccessSvc.Messages(profile).messages();
				
				// First we remove the dead message from the living.
				var idxP = _.findIndex(msgLive, function (
						child) {
					return child.id === msgDead[0].id;
				});
				msgLive.splice(idxP, 1);
				
				// Second we get all segment refs that are contained in the dead message.
				var segmentRefsMerelyDead = ProfileAccessSvc.Messages(profile)
						.getAllSegmentRefs(msgDead);
				// then we get all segment refs that are contained in the live messages.
				var segmentRefsLive = ProfileAccessSvc.Messages(profile)
				.getAllSegmentRefs(msgLive);
				// Until now, dead meant mearly dead.  We now remove those that really are sincerely dead.
				var segmentRefsSincerelyDead = ProfileAccessSvc.Segments(profile).findDead(segmentRefsMerelyDead, segmentRefsLive);
				if (segmentRefsSincerelyDead.length === 0) {
					return;
				}
				
				// Third we
				// get all datatypes that are contained in the sincerely dead segments.
				var dtIdsMerelyDead = ProfileAccessSvc.Segments(profile).findDatatypesFromSegmentRefs(segmentRefsSincerelyDead);

				// then all datatypes that are contained in the live segments.				
				var dtIdsLive = ProfileAccessSvc.Segments(profile).findDatatypesFromSegmentRefs(segmentRefsLive);
				var dtsIdsSincerelyDead = ProfileAccessSvc.Datatypes(profile).findDead(dtIdsMerelyDead, dtIdsLive);

				// Fourth we 
				// get all value sets that are contained in the sincerely dead datatypes.
				var vssIdsMerelyDead = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtsIdsSincerelyDead);
				// then all value sets that are contained in the live datatypes.
				var vssIdsLive = ProfileAccessSvc.Datatypes(profile).findValueSetsFromDatatypeIds(dtIdsLive);
				var vssIdsSincerelyDead = ProfileAccessSvc.ValueSets(profile).findDead(vssIdsMerelyDead, vssIdsLive);		

				ProfileAccessSvc.ValueSets(profile).removeDead(vssIdsSincerelyDead);		
				ProfileAccessSvc.Datatypes(profile).removeDead(dtsIdsSincerelyDead);
				ProfileAccessSvc.Segments(profile).removeDead(segmentRefsSincerelyDead);
				
				console.log("svc.deleteMessage: segmentRefsMerelyDead=" + segmentRefsMerelyDead.length);
				console.log("svc.deleteMessage: segmentRefsLive=" + segmentRefsLive.length);
				console.log("svc.deleteMessage: segmentRefsSincerelyDead=" + segmentRefsSincerelyDead.length);

				console.log("svc.deleteMessage: dtIdsMerelyDead=" + dtIdsMerelyDead.length);
				console.log("svc.deleteMessage: dtIdsLive=" + dtIdsLive.length);
				console.log("svc.deleteMessage: dtsIdsSincerelyDead=" + dtsIdsSincerelyDead.length);

				console.log("svc.deleteMessage: vssIdsMerelyDead=" + vssIdsMerelyDead.length);
				console.log("svc.deleteMessage: vssIdsLive=" + vssIdsLive.length);
				console.log("svc.deleteMessage: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);

				console.log("svc.deleteMessage: aMsgs=" + ProfileAccessSvc.Messages(profile).getMessageIds().length);
				console.log("svc.deleteMessage: aSegs=" + ProfileAccessSvc.Segments(profile).segments().length);
				console.log("svc.deleteMessage: aDts=" + ProfileAccessSvc.Datatypes(profile).datatypes().length);
				console.log("svc.deleteMessage: aVss=" + ProfileAccessSvc.ValueSets(profile).valueSets().length);
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