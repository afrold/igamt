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
      return [{"label":"R" , "id":0},{"label":"RE" , "id":1},{"label":"O" , "id":2},{"label":"C" , "id":3},{"label":"X" , "id":4},{"label":"B" , "id":5}]
    };

    svc.getUsageById = ["R", "RE", "O", "C", "X", "B"];

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

    svc.searchById = function(idKey, myArray){
        for (var i=0; i < myArray.length; i++) {
            if (myArray[i].id === idKey) {
                return i;
            }
        }
        return undefined;
    };

    svc.removeMsgFromFilter = function(msgId){
        var elt = svc.searchById(msgId, svc.getMsgmodel());
        if (elt !== undefined){
            svc.getMsgmodel().splice(elt, 1)
            }
    };

    svc.addMsgInFilter = function(name, msgId){
        if (name != undefined && msgId !== undefined) {
            svc.getMsgmodel().push({"label": name, "id": msgId});
            }
    };

    svc.updateMsgFromFilter = function(name, msgId){
        var elt = svc.searchById(msgId, svc.getMsgmodel());
        if (elt !== undefined){
            svc.getMsgmodel()[elt].label = name;
            }
    };

    svc.loadMessages = function(igdocument){
        svc.setMsgdata(svc.getMessages(igdocument));
        svc.setMsgmodel(svc.getMessages(igdocument));
        svc.setMsgsettings(svc.getSettings());
        svc.setMsgtexts(svc.getTexts("Conf. profiles"));
}
     svc.loadUsages = function(){
         svc.setUsagesdata(svc.getUsages());
         svc.setUsagesmodel(svc.getUsages());
         svc.setUsagessettings(svc.getSettings());
         svc.setUsagestexts(svc.getTexts("Usages"));
     };

    svc.showToC = function(leaf){
      var rst1 = false;
      _.each(svc.getMsgmodel(), function(filterElt){
        rst1 = rst1 || svc.filterByMsg(leaf, filterElt);
      });
      return rst1;
    };

    svc.show = function(leaf){
      if (leaf === undefined) {
        console.log("Undefined leaf");
        return true;
      }
      var rst1 = false;
      _.each(svc.getMsgmodel(), function(filterElt){
        rst1 = rst1 || svc.filterByMsg(leaf, filterElt);
      });

      var validUsages = [];
      _.each(svc.getUsagesmodel(), function(filterElt){
        validUsages.push(svc.getUsageById[filterElt.id]);
        });
       var rst2 = svc.filterByUsage(leaf, validUsages);

       var rst = rst1 && rst2;
       if (rst === undefined){
        rst = true;
      }
      return rst;
    };

    svc.filterByMsg = function(leaf, filterElt){
      if (leaf.id === filterElt.id){
        return true;
      }
      if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined) {
        return (MastermapSvc.getElementByKey(leaf.id, leaf.type, "message").indexOf(filterElt.id) !== -1);
      } else {
      console.log("UNDEFINED ELEMENT!!");
      console.log("--> mastermap");
      console.log(MastermapSvc.getMastermap());
      console.log("--> undefined leaf");
      console.log(leaf);
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

  svc.showInnerHtml = function(node, parentNode){
          var validUsages = [];
          _.each(svc.getUsagesmodel(), function(filterElt){
            validUsages.push(svc.getUsageById[filterElt.id]);
            });

          return svc.filterByUsageWithParent(node, parentNode, validUsages);
  }

    svc.filterByUsageWithParent = function(node, parentNode, filter){
//      if (MastermapSvc.getElement(node.id, node.type) !== undefined){
//        if (MastermapSvc.getUsage(node.id, node.type) !== undefined){
//          if (node.type === "field" || node.type === "component" ){
//}
            if (node.type === "subcomponent"){
                var showElt = svc.filterByUsage({"id":node.id, "type":"component"}, filter);
            } else {
                var showElt = svc.filterByUsage(node, filter);
            }
            var showParents = svc.filterByUsage(parentNode, filter);
            if (showElt && showParents !== undefined) {
                return showElt && showParents;
            } else {
                return true;
            }
 //        }
//      }
}
    return svc;
  });
