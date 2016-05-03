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
      if (svc.mastermap[c.id, "component"]["usage"] === undefined){
        svc.mastermap[c.id, "component"]["usage"] = c["usage"];
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
      //           console.log("Processing table " + tableId);

      if (tableId !== undefined && tableId !== "") {
        var table = svc.getTableLibrary()[tableId];
        if (table != undefined){
          svc.createMMElement(table.id, "table");
          svc.addParentsId(table.id, "table", parent);

          _.each(table.codes, function(c) {
            svc.addCodes(c, parent.concat([[table.id, 'table']]));
          });
        } else {
          svc.createMMElement(tableId, "table");
          svc.addParentsId(tableId, "table", parent);
          console.log("!!! => table " + tableId + " not found in library");
        }
      }
    }

    svc.addCodes = function(code, parent) {
      //           console.log("Processing code " + code.id);
      svc.createMMElement(code.id, "code");
      svc.addParentsId(code.id, "code", parent);
    }


    svc.addDatatype = function(datatype, parent) {
      //           console.log("Processing datatype ");

      if (datatype !== undefined && datatype !== "") {

        var dt = svc.getDatatypeLibrary()[datatype];
        if (dt !== undefined){
          svc.createMMElement(dt.id, "datatype");
          svc.addParentsId(dt.id, "datatype", parent);

          dt.components.forEach( function(c) {
            svc.addComponent(c, parent.concat([[dt.id, "datatype"]]));
          });
        } else {
          svc.createMMElement(datatype, "datatype");
          svc.addParentsId(datatype, "datatype", parent);
          console.log("!!! => datatype " + datatype + " not found in library");
        }
      }
    }

    svc.addField = function (field, parent) {
      //           console.log("Processing field " + field.id);

      svc.createMMElement(field.id, "field");
      svc.addParentsId(field.id, "field", parent);
      svc.mastermap[field.id, "usage"] = field["usage"];

      svc.addValueSet(field.table, parent.concat([[field.id, "field"]]));
      svc.addDatatype(field.datatype, parent.concat([[field.id, "field"]]));
    }


    svc.addSegment = function (segmentId, parent) {
      //           console.log("Processing segment " + segmentId + "");
      var segment = svc.getSegmentLibrary()[segmentId];
      if (segment !== undefined){
        svc.createMMElement(segment.id, "segment");
        svc.addParentsId(segment.id, "segment", parent);

        _.each(segment.fields, function(f) {
          svc.addField(f, parent.concat([[segment.id, "segment"]]));
        });
      } else {
        console.log("!!! => segment id " + segmentId + " not found");
      }
    }

    svc.addMessage = function (message, parent) {
      //           console.log("processing message id : " + message.id);

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
      svc.mastermap[segRefId, "segmentRef"]["usage"] = segmentRef["usage"];
      svc.addParentsId(segRefId, "segmentRef", parent);

      svc.addSegment(segRef, parent.concat([[segRefId, "segmentRef"]]));
    }

    svc.addGroup = function (group, parent) {
      //           console.log("Processing group " + group.id);

      svc.createMMElement(group.id, "group");
      svc.mastermap[group.id, "group"]["usage"] = group["usage"];
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
      /*           svc.setSegmentLibrary(svc.createSegmentLibrary(igdocument));
            svc.setDatatypeLibrary(svc.createDatatypeLibrary(igdocument));
            svc.setTableLibrary(svc.createTableLibrary(igdocument));
   */

      svc.createMMElement(profile.id, "ig");
      svc.mastermap[profile.id, "ig"].concat(profile.id);

      _.each(profile.messages.children, function(m) {
        var parentsList = [[profile.id, "ig"]];
        svc.createMMElement(profile.id, "ig", parentsList);

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
        //Add parents reference in element
        svc.mastermap[elementId, elementType][parentType] = svc.mastermap[elementId, elementType][parentType].concat(parentId);
        // Add element reference in parents
        svc.mastermap[parentId, parentType][elementType] = svc.mastermap[parentId, parentType].concat(elementId);
      });
    }

    svc.createMMElement = function (id, type) {
      if (svc.mastermap[id, type] === undefined) {
        var eltColl = [];
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

        svc.mastermap[id, type] = eltColl;
      }
    }

    svc.createSegmentLibrary = function (igdocument){
      //             console.log("Creating segment library");
      igdocument.profile.segments.children.forEach(function(n){
        svc.segmentLibrary[n.id] = n;
      });
    }


    svc.createTableLibrary = function (igdocument){
      //             console.log("Creating table library");
      igdocument.profile.tables.children.forEach(function(n){
        svc.tableLibrary[n.id] = n;
      });
    }


    svc.createDatatypeLibrary = function (igdocument){
      //             console.log("Creating datatype library");
      igdocument.profile.datatypes.children.forEach(function(n){
        svc.datatypeLibrary[n.id] = n;
      });

    }

    svc.getAllIndexes = function(arr, val){
      var indexes = [], i;
      for (var i = 0; i < arr.length; i++){
        if (arr[i] === val){
          indexes.push(i);
        }
      }
      return indexes;
    }

    svc.getElement = function(id, type){
      var rst = svc.mastermap[id, type];
      if (rst === undefined){
        console.log(id, type, " not found")
        //         console.log(id.concat(" ", type, " => not found"));
      }
      return rst;
    }

    svc.getUsage = function (id, type){
      var elt = svc.getElement(id, type);
      if (elt !== undefined){
        if (type === "message"){
          //TBD
        }
        if (type === "field"){
          return svc.getElement(id, type)["usage"];
        }
        if (type === "segment"){
          var sgt = svc.getElement(id, type);
          var rst = [];
          sgt["group"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "group")["usage"]) === -1)
              rst.push(svc.getElement(g, "group")["usage"]);
          });
          sgt["segmentRef"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "segmentRef")["usage"]) === -1)
              rst.push(svc.getElement(g, "segmentRef")["usage"]);
          });
          return rst;
        }
        if (type === "segmentRef"){
          return svc.getElement(id, type)["usage"];
        }
        if (type === "group"){
          return svc.getElement(id, type)["usage"];
        }
        if (type === "table"){
          // => check sgt and datatypes
          var tbl = svc.getElement(id, type);
          var rst = [];
          tbl["segment"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "segment")["usage"]) === -1){
              rst.push(svc.getUsage(g, "segment"));
            }
          });
          tbl["datatype"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "datatype")["usage"]) === -1)
              rst.push(svc.getUsage(g, "datatype"));
          });
          return rst;
        }
        if (type === "datatype"){
          // => check sgt et dt
          var dt = svc.getElement(id, type);
          var rst = [];
          dt["segment"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "segment")["usage"]) === -1){
              rst.push(svc.getUsage(g, "segment"));
            }
          });
          dt["datatype"].forEach(function(g){
            if (rst.indexOf(svc.getElement(g, "datatype")["usage"]) === -1)
              rst.push(svc.getUsage(g, "datatype"));
          });
          return rst;
        }
        if (type === "component"){
          console.log();
          console.log(svc.getElement(id, type));
          return svc.getElement(id, "component")["usage"];
        }
        if (type === "code"){
          //TBD

        }
      }

    }

    return svc;

  });
