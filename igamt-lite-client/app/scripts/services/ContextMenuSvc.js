'use strict';

/**
 * @ngdoc function
 * @description
 *
 * This service is used to tranfer the state of a context menu selection between controllers.  
 * The state can be accessed but once.  It is left in its inital state. 
 */

angular.module('igl').factory('ContextMenuSvc', function () {
	
	var state = {};
    
    state.item = null;
    
    state.ext = null;
    
    state.get = function() {
    	var tmp = state.item;
    	state.item = null;
    	return tmp;
    };
    
    state.put = function(item) {
    	state.item = item;
    };
    
    return state;
});




