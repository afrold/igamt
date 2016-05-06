'use strict';
angular
.module('igl')
.service (
  'MastermapSvc',
  function($rootScope) {

    var svc = {};

    svc.mastermap = [];

    svc.segmentLibrary = {};
    svc.datatypeLibrary = {};
    svc.tableLibrary = {};

    svc.parseIg = function(igdocument){
      if (igdocument !== null && igdocument !== undefined){
        svc.addIG(igdocument);
        return svc.getMastermap();
      }
      return [];
    }

    svc.getMastermap = function(){
      return this.mastermap;
    }

    svc.getSegmentLibrary = function(){
      return this.segmentLibrary;
    }
    svc.setSegmentLibrary = function(segmentLibrary){
      this.segmentLibrary = segmentLibrary;
    }
    svc.getDatatypeLibrary = function(){
      return this.datatypeLibrary;
    }
    svc.setDatatypeLibrary = function(datatypeLibrary){
      this.datatypeLibrary = datatypeLibrary
    }
    svc.getTableLibrary = function(){
      return this.tableLibrary;
    }
    svc.setTableLibrary = function(tableLibrary){
      this.tableLibrary = tableLibrary;
    }


    svc.addComponent = function(c, parent){
      svc.createMMElement(c.id, "component");
      svc.addParentsId(c.id, "component", parent);
      if (svc.getElementByKey(c.id, "component", "usage") === undefined){
        svc.setElement(c.id, "component", "usage", c["usage"]);
      }
      if (c.datatype !== undefined){
        svc.addDatatype(c.datatype, parent.concat([[c.id, "component"]]));
      }
      if (c.table !== undefined){
        svc.addValueSet(c.table, parent.concat([[c.id, "component"]]));
      }

      // svc.addDatatype(c.datatype, parent.concat(new  Array([dt.id, 'datatype']))); ?? nedd to process subcomponent without infinite loop
    }


    svc.addValueSet = function(tableId, parent) {
      if (tableId !== undefined && tableId !== "") {
        var table = svc.getTableLibrary()[tableId];
        if (table !== undefined){
          svc.createMMElement(table.id, "table");
          svc.addParentsId(table.id, "table", parent);

          _.each(table.codes, function(c) {
            svc.addCodes(c, parent.concat([[table.id, 'table']]));
          });
        } else {
          svc.createMMElement(tableId, "table");
          svc.addParentsId(tableId, "table", parent);
          //           console.log("!!! => table " + tableId + " not found in library");
        }
      }
    }


    svc.addCodes = function(code, parent) {

      svc.createMMElement(code.id, "code");
      svc.addParentsId(code.id, "code", parent);
    }


    svc.addDatatype = function(datatype, parent) {

      if (datatype !== undefined && datatype !== "") {
        var dt = svc.getDatatypeLibrary()[datatype];
        if (dt !== undefined){
          svc.createMMElement(dt.id, "datatype");
          svc.addParentsId(dt.id, "datatype", parent);
//           console.log(svc.mastermap[dt.id, "datatype"])
          dt.components.forEach( function(c) {
            svc.addComponent(c, parent.concat([[dt.id, "datatype"]]));
          });
        } else {
          svc.createMMElement(datatype, "datatype");
          svc.addParentsId(datatype, "datatype", parent);
          //           console.log("!!! => datatype " + datatype + " not found in library");
        }
      }
    }


    svc.addField = function (field, parent) {

      svc.createMMElement(field.id, "field");
      svc.addParentsId(field.id, "field", parent);
      svc.setElement(field.id, "field", "usage", field["usage"]);

      svc.addValueSet(field.table, parent.concat([[field.id, "field"]]));
      svc.addDatatype(field.datatype, parent.concat([[field.id, "field"]]));
    }


    svc.addSegment = function (segmentId, parent) {

      svc.createMMElement(segmentId, "segment");
      svc.addParentsId(segmentId, "segment", parent);

      var segment = svc.getSegmentLibrary()[segmentId];
      if (segment !== undefined){
        _.each(segment.fields, function(f) {
          svc.addField(f, parent.concat([[segment.id, "segment"]]));
        });
      } else {
        console.log("!!! => segment id " + segmentId + " not found");
      }
    }

    svc.addMessage = function (message, parent) {

      svc.createMMElement(message.id, "message");
      svc.addParentsId(message.id, "message", parent);

      _.each(message.children, function(n) {
        if (n.type === "segmentRef"){
          svc.addSegmentRef(n, parent.concat([[message.id, 'message']]));
        } else {
          svc.addGroup(n, parent.concat([[message.id, 'message']]));
        }
      });
    }


    svc.addSegmentRef = function (segmentRef, parent){
      //           console.log("Processing segment ref " + segmentRef.id);

      var segRefId = segmentRef.id;
      var segRef = segmentRef.ref;

      svc.createMMElement(segRefId, "segmentRef");
      svc.setElement(segRefId, "segmentRef", "usage", segmentRef["usage"]);
      svc.addParentsId(segRefId, "segmentRef", parent);

      svc.addSegment(segRef, parent.concat([[segRefId, "segmentRef"]]));
    }


    svc.addGroup = function (group, parent) {
      //           console.log("Processing group " + group.id);

      svc.createMMElement(group.id, "group");
      svc.setElement(group.id, "group", "usage", group["usage"]);
      if (group["usage"] === undefined){
        console.log("undefined usage for group!!")}
      svc.addParentsId(group.id, "group", parent);

      _.each(group.children, function(n) {
        if (n.type === "segmentRef"){
          svc.addSegmentRef(n, parent.concat([[group.id, 'group']]));
        } else {
          svc.addGroup(n, parent.concat([[group.id, 'group']]));
        }
      });
    }


    svc.addIG = function (igdocument) {
      console.log("Creating mastermap\nprocessing IG : " + igdocument.profile.id);

      var profile = igdocument.profile;

      svc.createSegmentLibrary(igdocument);
      svc.createDatatypeLibrary(igdocument);
      svc.createTableLibrary(igdocument);

      svc.createMMElement(profile.id, "ig");

      _.each(profile.messages.children, function(m) {
        var parentsList = [[profile.id, "ig"]];
        svc.addMessage(m, parentsList);
      });

    }

    svc.addParentsId = function (elementId, elementType, parentsList) {
      //             console.log(elementId + " -> " + parentsList)
      //  //Element refers to self
      //   svc.mastermap[elementId][elementType] = svc.mastermap[elementId][elementType].concat(elementId);

      _.each(parentsList, function(parent) {
        var parentId = parent[0];
        var parentType = parent[1];
        //Add parents reference in element if not present
        if (svc.getElementByKey(elementId, elementType, parentType).indexOf(parentId) === -1){
          svc.setElement(elementId, elementType, parentType, svc.getElementByKey(elementId, elementType, parentType).concat(parentId));
        }
        // Add element reference in parents if not already present
        if (svc.getElementByKey(parentId, parentType, elementType).indexOf(elementId) === -1){
          svc.setElement(parentId, parentType, elementType, svc.getElementByKey(parentId, parentType, elementType).concat(elementId));
        }
      });
    }

    svc.createMMElement = function (id, type) {
      if (svc.getElement(id, type) === undefined) {
        var eltColl = new Array();
        eltColl["ig"] =[];
        eltColl["message"] =[];
        eltColl["field"] =[];
        eltColl["segment"] =[];
        eltColl["segmentRef"] =[];
        eltColl["group"] =[];
        eltColl["table"] =[];
        eltColl["datatype"] =[];
        eltColl["component"] =[];
        eltColl["code"] =[];
        eltColl["usage"] =[];
        eltColl["type"] = type;
        eltColl["id"] = id;

        svc.mastermap[id.concat(type)] = eltColl;
      }
    }

    svc.createSegmentLibrary = function (igdocument){
      //             console.log("Creating segment library");
      igdocument.profile.segmentLibrary.children.forEach(function(n){
        svc.segmentLibrary[n.id] = n;
      });
    }


    svc.createTableLibrary = function (igdocument){
      //             console.log("Creating table library");
      igdocument.profile.tableLibrary.children.forEach(function(n){
        svc.tableLibrary[n.id] = n;
      });
    }


    svc.createDatatypeLibrary = function (igdocument){
      //             console.log("Creating datatype library");
      igdocument.profile.datatypeLibrary.children.forEach(function(n){
        svc.datatypeLibrary[n.id] = n;
      });
    }

    svc.getElement = function(id, type){
      var rst = svc.mastermap[id.concat(type)];
      return rst;
    }

    svc.getElementByKey = function(id, type, key){
      return svc.mastermap[id.concat(type)][key];
    }

    svc.setElement = function(id, type, key, value){
      svc.mastermap[id.concat(type)][key] = value;
    }

    svc.getUsage = function (id, type){
      var item = svc.getElement(id, type);
      if (item !== undefined){
        if (type === "message"){
          //TBD
          return [];
        }
        if (type === "field"){
          return [svc.getElementByKey(id, type, "usage")];
        }
        if (type === "segmentRef"){
          return [svc.getElementByKey(id, type, "usage")];
        }
        if (type === "group"){
          return [svc.getElementByKey(id, type, "usage")];
        }
        if (type === "component"){
          return [svc.getElementByKey(id, "component", "usage")];
        }
        if (type === "segment"){
          var sgt = svc.getElement(id, type);
          var rst = [];
          var usg = "";
          sgt["group"].forEach(function(elt){
            usg = svc.getElementByKey(elt, "group", "usage");
            if (rst.indexOf(usg) === -1)
              rst.push(usg);
          });
          sgt["segmentRef"].forEach(function(elt){
            usg = svc.getElementByKey(elt, "segmentRef", "usage");
            if (rst.indexOf(usg) === -1)
              rst.push(usg);
          });
          return rst;
        }
        if (type === "table"){
          // => check sgt and datatypes
          var tbl = svc.getElement(id, type);
          var rst = [];
          tbl["segment"].forEach(function(elt){
            var usgs = svc.getUsage(elt, "segment");
            usgs.forEach(function(usg){
              if (rst.indexOf(usg) === -1){
                rst.push(usg);
              }
            });
          });
/*           tbl["datatype"].forEach(function(elt){
            var usgs = svc.getUsage(elt, "datatype");
            usgs.forEach(function(usg){
              if (rst.indexOf(usg) === -1){
                rst.push(usg);
              }
            });
          });*/
          return rst;
        }
        if (type === "datatype"){
          // => check sgt and dt
          var dt = svc.getElement(id, type);
          var rst = [];
          var usg = "";
          if (dt["segment"] !== undefined){
            dt["segment"].forEach(function(elt){
              var usgs = svc.getUsage(elt, "segment");
              usgs.forEach(function(usg){
              if (rst.indexOf(usg) === -1){
                rst.push(usg);
              }
              });
            });
          }
/*           if (dt["datatype"] != undefined){
            dt["datatype"].forEach(function(elt){
              var usgs = svc.getUsage(elt, "datatype");
              usgs.forEach(function(usg){
              if (rst.indexOf(usg) === -1){
                rst.push(usg);
              }
              });
            });
          } */
          return rst;
        }
        if (type === "code"){
          //TBD
          return [];
        }
      }
    }

    return svc;

  });
