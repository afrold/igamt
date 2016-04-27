'use strict';
angular
		.module('igl')
		.factory(
				'FilteringSvc',
        ['$rootScope', function($rootScope) {

            var svc = {};

            svc.filtermsgmodel = [];
            svc.filtermsgdata = [];
            svc.filtermsgsettings = [];
            svc.filtermsgtexts = [];

            svc.filterusagesmodel = [];
            svc.filterusagesdata = [];
            svc.filterusagessettings = [];
            svc.filterusagestexts = [];

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

            svc.getMsgsettings = function(){
                return this.filtermsgsettings;
            };

            svc.setMsgsettings = function(msgsettings){
                this.filtermsgsettings = msgsettings;
            };

            svc.getMsgtexts = function(){
                return this.filtermsgtexts;
            };

            svc.setMsgtexts = function(msgtexts){
                this.filtermsgtexts = msgtexts;
            };

            svc.getUsagesmodel = function(){
                return this.filterusagesmodel;
            };

            svc.setUsagesmodel = function(usagesmodel){
                this.filterusagesmodel = usagesmodel;
            };

            svc.getUsagesdata = function(){
                return this.filterusagesdata;
            };

            svc.setUsagesdata = function(usagesdata){
                this.filterusagesdata = usagesdata;
            };

            svc.getUsagessettings = function(){
                return this.filterusagessettings;
            };

            svc.setUsagessettings = function(usagessettings){
                this.filterusagessettings = usagessettings;
            };

            svc.getUsagestexts = function(){
                return this.filterusagestexts;
            };

            svc.setUsagestexts = function(usagestexts){
                this.filterusagestexts = usagestexts;
            };

            svc.getMessages = function(igdocument){
                  if (igdocument !== null && igdocument !== undefined){
                      var msgHolder = [];
                      _.each(igdocument.profile.messages.children, function(msg) {
                         msgHolder.push({"label": msg.name, "id": msg.id});
                    });
                    return msgHolder;
                  }
                  return [];
            };

            svc.getUsages = function(){
              return [{"label":"R" , "id":1},{"label":"RE" , "id":2},{"label":"O" , "id":3},{"label":"X" , "id":4}]
            };

            svc.getSettings = function(){
              return {
                scrollableHeight: '200px',
                scrollable: true,
                enableSearch: true,
                buttonClasses: 'btn btn-xs',
                displayProp: 'label'
            }
            };

            svc.getTexts = function(text){
              return {
                checkAll: 'Check All',
                uncheckAll: 'Uncheck All',
                selectionCount: 'checked',
                selectionOf: '/',
                searchPlaceholder: 'Search...',
                buttonDefaultText: text,
                dynamicButtonTextSuffix: 'checked'
            }
            };

					return svc;
}]);
