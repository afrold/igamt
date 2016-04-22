'use strict';
angular
		.module('igl')
		.factory(
				'FilteringSvc',
        ['$rootScope', function($rootScope) {
/*              var svc = this;

              var filtermsgmodels = [];
//               var svc = {
              svc.getMessages = function(igdocument){
                  if (igdocument !== null && igdocument !== undefined){
                      var msgHolder = [];
                      _.each(igdocument.profile.messages.children, function(msg) {
                         msgHolder.push({"label": msg.name, "id": msg.id});
                    });
                    return msgHolder;
                  }
                  return [];
                  }

//         }
					return svc;
*/
            var svc = {};

            svc.filtermsgmodel = [];

            svc.getMessages = function(igdocument){
                  if (igdocument !== null && igdocument !== undefined){
                      var msgHolder = [];
                      _.each(igdocument.profile.messages.children, function(msg) {
                         msgHolder.push({"label": msg.name, "id": msg.id});
                    });
                    return msgHolder;
                  }
                  return [];
                  }

            svc.getFiltermsgmodel = function(){
                return this.filtermsgmodel;
            }

					return svc;
}]);
