'use strict';

/**
 * @ngdoc function
 * @description
 *
 * This service is used to tranfer the state of a context menu selection between controllers.  
 * The state can be accessed but once.  It is left in its inital state. 
 */

angular.module('igl').factory('ContextMenuSvc', function () {
	
	var svc = {};
    
    svc.item = null;
    
    svc.ext = null;
    
    svc.get = function() {
    	var tmp = svc.item;
    	svc.item = null;
    	return tmp;
    };
    
    svc.put = function(item) {
    	svc.item = item;
    };
    
    return svc;
});




