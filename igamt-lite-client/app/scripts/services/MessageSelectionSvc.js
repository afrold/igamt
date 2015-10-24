'use strict';

angular.module('igl').factory('MessageSelectionSvc', function () {
	
	var state = {};
	
	state.messageIds = [];
	
	state.messages = [];
	
	return state;
});