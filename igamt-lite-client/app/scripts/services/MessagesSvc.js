/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('MessagesSvc', function($http, userInfoService) {

	var svc = this;

	var messagesStruct = function(scope, children) {
		this.id = null;
		this.scope = scope;
	    this.sectionDescription = null;
	    this.sectionContents = null;
		this.children = children;
	};

    svc.findOneChild = function (id, list) {
        if (list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].id === id) {
                    return list[i];
                }
            }
        }
        return null;
    };

    svc.delete = function(message) {
        return $http.post('api/messages/'+ message.id+ '/delete');
    };



    return svc;
});
