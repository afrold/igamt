'use strict';
angular
		.module('igl')
		.factory(
				'FilteringSvc',
        ['$rootScope', function($rootScope) {

            var svc = {};

            svc.filtermsgmodel = [];

            svc.getmodel = function(){
              return svc.filtermsgmodel;
            };

            svc.getMessages = function(igdocument){
                  if (igdocument !== null && igdocument !== undefined){
                      var msgHolder = [];
                    svc.addCodes(123);
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

            svc.addCodes = function(code) {
              console.log("Processing code " + code);
              _.each(svc.filtermsgmodel, function(tt){
                console.log(tt)
              })
        }

					return svc;
}]);
