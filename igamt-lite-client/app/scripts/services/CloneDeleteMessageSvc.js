angular.module('igl').factory('CloneDeleteMessageSvc', function (ProfileAccessSvc) {

	var svc = this;

	svc.cloneMessage = function(profile, message) {
	// TODO gcr: Need to include the user identifier in the new label. 
	// $rootScope.profile.metaData.ext should be just that, but is currently 
	// unpopulated in the profile.
	var newMessage = (JSON.parse(JSON.stringify(message)));
	newMessage.reference.id = null;
		
		// Nodes must have unique names so we timestamp when we duplicate.
		if (newNode.type === 'message') {
			newNode.reference.messageType = newNode.reference.messageType + "-" + $rootScope.profile.metaData.ext + "-" + timeStamp();
			newNode.label = newNode.reference.messageType;
		}
		for (var i in $rootScope.profile.messages.children) {
			console.log($rootScope.profile.messages.children[i].messageType);
		}
		$rootScope.profile.messages.children.splice(2, 0, newNode);
		for (var i in $rootScope.profile.messages.children) {
			console.log($rootScope.profile.messages.children[i].messageType);
		}		
	}
	
	svc.deleteMessage = function(message) {
		var segmentRefs = ProfileAccessSvc.Messages($rootScope.profile).getSegmentRefsSansOne(message);
		ProfileAccessSvc.Segments($rootScope.profile).removeDead(segmentRefs);
		var id = message.id;
		var messages = $rootScope.profile.messages.children;
		var idx = _.findIndex(messages, function(child){
			return child.id === id;
		})
		messages.splice(idx, 1);
	}
	
  function timeStamp() {
	  // Create a date object with the current time
	  var now = new Date();
	
	  // Create an array with the current month, day and time
	  var date = [ now.getMonth() + 1, now.getDate(), now.getFullYear() ];
	
	  // Create an array with the current hour, minute and second
	  var time = [ now.getHours(), now.getMinutes(), now.getSeconds() ];
	
	  // Determine AM or PM suffix based on the hour
	  var suffix = ( time[0] < 12 ) ? "AM" : "PM";
	
	  // Convert hour from military time
	  time[0] = ( time[0] < 12 ) ? time[0] : time[0] - 12;
	
	  // If hour is 0, set it to 12
	  time[0] = time[0] || 12;
	
	  // If seconds and minutes are less than 10, add a zero
	  for (var i = 1; i < 3; i++) {
	      if (time[i] < 10) {
	          time[i] = "0" + time[i];
	      }
	  }
	
	  // Return the formatted string
	  return date.join("/") + " " + time.join(":") + " " + suffix;
	};
	
	return svc;
});