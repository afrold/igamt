'use strict';
angular
.module('igl')
.factory(
  'FilteringSvc',
  function($rootScope, MastermapSvc) {

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
      return [{"label":"R" , "id":1},{"label":"RE" , "id":2},{"label":"O" , "id":3},{"label":"C" , "id":4},{"label":"X" , "id":5},{"label":"B" , "id":6}]
    };

    svc.getSettings = function(){
      return {
        scrollableHeight: '200px',
        scrollable: true,
        enableSearch: true,
        buttonClasses: 'btn btn-xs btn-primary',
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

    svc.showToC = function(leaf){
      var rst1 = false;
      _.each(svc.getMsgmodel(), function(filterElt){
        rst1 = rst1 || svc.filterByMsg(leaf, filterElt);
      });
//       console.log("show toc");
//       console.log(rst1);
      return rst1;
    };

    svc.show = function(leaf){
    // Attention
    return true;
      if (leaf === undefined) {
        console.log("undefined");
        return true;
      }
      var rst1 = false;
      _.each(svc.getMsgmodel(), function(filterElt){
        rst1 = rst1 || svc.filterByMsg(leaf, filterElt);
      });

      var validUsages = [];
      _.each(svc.getUsagesmodel(), function(filterElt){
        validUsages.push(filterElt.label);
        });
       var rst2 = svc.filterByUsage(leaf, validUsages);

       var rst = rst2; //!!!
//       var rst = rst1 && rst2;
       if (rst === undefined){
        rst = true;
      }
      return rst;
    };

    svc.filterByMsg = function(leaf, filterElt){
      if (leaf.id === filterElt.id){
        return true;
      }

      if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined){
        return (MastermapSvc.getElementByKey(leaf.id, leaf.type, "message").indexOf(filterElt.id) !== -1);
      }
    }

    svc.filterByUsage = function(leaf, filter){
      if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined){
        if (MastermapSvc.getUsage(leaf.id, leaf.type) !== undefined){
          if (leaf.type === "message" || leaf.type === "table"){
            return true;
          } else {
            var leafUsages = MastermapSvc.getUsage(leaf.id, leaf.type);
            var rst = false;
            _.each(leafUsages, function(usg){
              rst = rst || (filter.indexOf(usg) !== -1);
            });
            return rst;
          }
        }
      }
    }
    return svc;
  });
