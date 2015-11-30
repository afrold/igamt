angular.module('igl').factory('DeleteMessageSvc', function (ProfileAccessSvc) {

	var svc = this;

	svc.deleteMessage = function(profile, message) {
		var segmentRefs = ProfileAccessSvc.Messages(profile).getSegmentRefsSansOne(message);
		ProfileAccessSvc.Segments(profile).removeDead(segmentRefs);
		var id = message.id;
		var messages = profile.messages.children;
		var idx = _.findIndex(messages, function(child){
			return child.id === id;
		})
		messages.splice(idx, 1);
	}
	
	return svc;
});