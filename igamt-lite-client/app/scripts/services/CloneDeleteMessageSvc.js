angular.module('igl').factory(
		'CloneDeleteMessageSvc',
		function(ProfileAccessSvc) {

			var svc = this;

			svc.cloneMessage = function(profile, toc, message) {
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
				var ConformanceProfile = _.find(toc, function(child) {
					return child.id === "3";
				});

				var messages = _.find(ConformanceProfile.children, function(
						child) {
					return child.id === "3.1";
				});

				var id = message.reference.id;
				var idx = _.findIndex(messages.children, function(child) {
					return child.reference.id === id;
				})
				messages.children.splice(idx, 0, newMessage);
				return newMessage;
			}

			svc.deleteMessage = function(profile, toc, message) {
				var segmentRefs = ProfileAccessSvc.Messages(profile)
						.getSegmentRefsSansOne(message.reference);
				ProfileAccessSvc.Segments(profile).removeDead(segmentRefs);
				var id = message.reference.id;
				var idxP = _.findIndex(profile.messages.children, function(
						child) {
					return child.id === id;
				})
				profile.messages.children.splice(idxP, 1);
				var messages = svc.getMessages(toc);
				var idxT = svc.findMessageIndex(messages);
				messages.children.splice(idxT, 1);
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