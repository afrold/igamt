'use strict';
angular
		.module('igl')
		.factory(
				'FilteringSvc',
        ['$rootScope', function($rootScope) {

            var svc = {};

            svc.filtermsgmodel = [];
            svc.filtermsgdata = [];

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

            svc.getMsgmodel = function(){
                return this.filtermsgmodel;
            };

            svc.setMsgmodel = function(msgmodel){
                this.filtermsgmodel = msgmodel;
            };

            svc.getMsgdata = function(){
                return this.filtermsgdata;
            };

            svc.setMsgdata = function(msgdata){
                this.filtermsgdata = msgdata;
            };

					return svc;
}]);
