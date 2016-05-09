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

    svc.findOneChild = function (id, library) {
        if (library.children) {
            for (var i = 0; i < library.children.length; i++) {
                if (library.children[i].id === id) {
                    return library.children[i];
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
