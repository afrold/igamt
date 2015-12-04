angular.module('igl').factory('CloneDeleteMessageSvc', function (ProfileAccessSvc) {

	var svc = this;
	
	svc.addEntry = function(profile, toc, entry) {
		console.log("Add entry.");
	}

	svc.cloneMessage = function(profile, toc, message) {
	// TODO gcr: Need to include the user identifier in the new label. 
	// $rootScope.profile.metaData.ext should be just that, but is currently 
	// unpopulated in the profile.
	var newMessage = (JSON.parse(JSON.stringify(message)));
	newMessage.reference.id = new ObjectId();
		
		// Nodes must have unique names so we timestamp when we duplicate.
		if (newMessage.reference.type === 'message') {
			newMessage.reference.name = newMessage.reference.name + "-" + profile.metaData.ext + "-" + Math.floor(Math.random()*100);
			newMessage.label = newMessage.reference.name;
		}

		profile.messages.children.splice(0, 0, newMessage.reference);
     	  var messages = _.find(toc, function(child){
       		  return child.label === "Messages";
       	  }); 
  		var id = message.reference.id;
  		var idx = _.findIndex(messages.children, function(child){
			return child.reference.id === id;
		})
		messages.children.splice(idx, 0, newMessage);
  		return newMessage;
	}
	
	svc.deleteMessage = function(profile, toc, message) {
		var segmentRefs = ProfileAccessSvc.Messages(profile).getSegmentRefsSansOne(message.reference);
		ProfileAccessSvc.Segments(profile).removeDead(segmentRefs);
		var id = message.reference.id;
		var idxP = _.findIndex(profile.messages.children, function(child){
			return child.id === id;
		})
		profile.messages.children.splice(idxP, 1);
	   	  var tocMessages = _.find(toc, function(child){
	   		  return child.label === "Messages";
	   	  }); 
		var idxT = _.findIndex(tocMessages.children, function(child){
			return child.reference.id === id;
		})
		tocMessages.children.splice(idxT, 1);
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