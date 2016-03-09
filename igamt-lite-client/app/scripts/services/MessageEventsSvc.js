'use strict';

/**
 * @ngdoc function
 * @description
 * 
 * This service enables the MessageEvents structure to be accessed from both the
 * controllers of the Create IG Dialog.
 */

angular.module('igl').factory('MessageEventsSvc', function($http, ngTreetableParams) {
	
	var svc = this;
	
	svc.getMessageEvents = function(hl7Version) {
		return new ngTreetableParams({
			getNodes : function(parent) {
				return parent ? parent.children : mes(hl7Version);
			},
	        getTemplate : function(node) {
	            return 'MessageEventsNode.html';
	        },
	        options : {
	            onNodeExpand: function() {
	                console.log('A node was expanded!');
	            }
	        }
		});
	};
	
function mes(hl7Version) {
	console.log("hl7Version=" + JSON.stringify(hl7Version));
	return $http.post(
			'api/igdocuments/messageListByVersion', hl7Version).then(function(response) {
			return angular.fromJson(response.data)});
		};

	return svc;
});