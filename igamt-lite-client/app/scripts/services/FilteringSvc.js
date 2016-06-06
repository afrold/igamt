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
      return [{"label":"R" , "id":0},{"label":"RE" , "id":1},{"label":"O" , "id":2},{"label":"C" , "id":3},{"label":"X" , "id":4},{"label":"B" , "id":5}];
    };

    svc.getUsageById = ["R", "RE", "O", "C", "X", "B"];

    svc.getSettings = function(){
      return {
        scrollableHeight: '200px',
        scrollable: true,
        enableSearch: true,
        buttonClasses: 'btn btn-xs btn-primary',
        displayProp: 'label'
      };
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
        };
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
        //if (svc.searchById(svc.getMsgmodel(), msgId !== undefined) {
            if (_.find(svc.getMsgmodel(), function(usg) {
                    return usg.id === msgId;
                }).length !== 0) {
                    svc.getMsgmodel().push({"label": name, "id": msgId});
                }
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

    svc.showToC = function(node){
        var rst1 = false;
        _.each(svc.getMsgmodel(), function(filterElt){
            rst1 = rst1 || svc.filterByMsg(node, filterElt);
        });
        return rst1;
    };

    svc.show = function(leaf){
      if (leaf === undefined) {
        return true;
      }
      var rst1 = false;
      _.each(svc.getMsgmodel(), function(filterElt){
        rst1 = rst1 || svc.filterByMsg(leaf, filterElt);
      });

      var rst2 = false;
      _.each(svc.getUsagesmodel(), function(filterElt){
        rst2 = rst || svc.filterByUsage(leaf, filterElt);
        });

       var rst = rst1 && rst2;
       if (rst === undefined){
        rst = true;
      }
      return rst;
    };

    svc.filterByMsg = function(leaf, filterElt){
        if (leaf.id !== undefined && leaf.type !== undefined){
            if (leaf.type === "message" && leaf.id === filterElt.id) {
                return true;
            }
            if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined) {
                return (MastermapSvc.getElementByKey(leaf.id, leaf.type, "message").indexOf(filterElt.id) !== -1);
            }
        }
        return false;
    }

    svc.filterByUsage = function(leaf, filterElt){
        if (MastermapSvc.getElement(leaf.id, leaf.type) !== undefined){
            if (MastermapSvc.getUsage(leaf.id, leaf.type) !== undefined){
                if (MastermapSvc.getElementByKey(leaf.id, leaf.type, "usage").length === 0) {
                    return true;
                }
                if (leaf.type === "message"){
                    return (MastermapSvc.getUsage(leaf.id, leaf.type).indexOf(svc.getUsageById[filterElt.id]) !== -1);
                }

                if (leaf.type === "table"){
                    return true;
                }
                if (leaf.type === "subcomponent"){
                    return (MastermapSvc.getUsage(leaf.id, "component").indexOf(filterElt) !== -1);
                } else {
                    return (MastermapSvc.getUsage(leaf.id, leaf.type).indexOf(filterElt) !== -1);
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
    }

    svc.isUnused = function(node){
        return false;
//        if (MastermapSvc.getElement(node.id, node.type) !== undefined){
//            if (node.type == "datatype") {
//                return (MastermapSvc.getElementByKey(node.id, node.type, "field").length === 0) &&
//                (MastermapSvc.getElementByKey(node.id, node.type, "datatype").length === 0);
//            } else if (node.type == "table") {
//                return (MastermapSvc.getElementByKey(node.id, node.type, "field").length === 0) &&
//                (MastermapSvc.getElementByKey(node.id, node.type, "datatype").length === 0);
//            } else if (node.type == "component") {
//                return (MastermapSvc.getElementByKey(node.id, node.type, "datatype").length === 0);
//            } else if (node.type == "field") {
//                return (MastermapSvc.getElementByKey(node.id, node.type, "segment").length === 0);
//            } else if (node.type == "segment") {
//                return (MastermapSvc.getElementByKey(node.id, node.type, "segmentRef").length === 0);
//            } else if (node.type == "group" | node.type == "segmentRef") {
//                 return (MastermapSvc.getElementByKey(node.id, node.type, "field").length === 0) &&
//                 (MastermapSvc.getElementByKey(node.id, node.type, "datatype").length === 0);
//            } else if (node.type == "message") {
//                 return (MastermapSvc.getElementByKey(node.id, node.type, "profile").length === 0);
//            } else if (node.type == "profile") {
//                 return (MastermapSvc.getElementByKey(node.id, node.type, "ig").length === 0);
//            }
//        } else {
//            return false;
//        }
    }

    return svc;
  });
