// 'use strict';
angular
		.module('igl')
		.factory(
				'FilteringSvc',
        ['$rootScope', 'MastermapSvc', function($rootScope, MastermapSvc) {

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
              return [{"label":"R" , "id":1},{"label":"RE" , "id":2},{"label":"O" , "id":3},{"label":"C" , "id":4},{"label":"X" , "id":5}]
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

          svc.show = function(leaf){
            var rst = false;
            _.each(svc.getMsgmodel(), function(filterElt){
              //                   console.log("filter => " + filterElt.id);
              //                   console.log("leaf => " +leaf.id);
              rst = rst || filterByMsg(leaf, filterElt);
            });
            return rst;
          };

          filterByMsg = function(leaf, filterElt){
//             if (MastermapSvc.getMastermap()[leaf.id] !== undefined){
            if (leaf.id === filterElt.id){
              return true;
            }

            console.log(MastermapSvc.getElement(leaf.id, leaf.type));
            if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined){

              return (MastermapSvc.getElement(leaf.id, leaf.type)["message"].indexOf(filterElt.id) !== -1);
            } else {
              console.log("Unfound in mastermap: ");
              console.log(leaf);
              //               return true;
            }
          }

          filterByUsage = function(leaf){
            var mm = MastermapSvc.getMastermap()[leaf.id];
            if (mm["type"] === "segment"){
              return true; //(mm['msg'].indexOf(filterElt.id) !== -1);
            } else {
              return false;
            }
          }

					return svc;
}]);
