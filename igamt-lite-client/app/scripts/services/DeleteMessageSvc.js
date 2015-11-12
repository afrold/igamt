angular.module('igl').factory('DeleteMessageSvc', function () {

	var del = this;

	del.deleteMessage = function(profile, message) {
		var segmentRefs = ProfileAccessSvc.Message(profile).getSegmentRefsSansOne(message);
		ProfileAccessSvc.Segments(profile).removeDead(segmentsRefs);
	}
	
	return del;
});