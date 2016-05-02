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


    svc.addValueSet = function(tableId, parent) {
      //           console.log("Processing table " + tableId);

      if (tableId !== undefined && tableId !== "") {
        var table = svc.getTableLibrary()[tableId];
        svc.createMMElement(table.id);
        svc.addParentsId(table.id, "table", parent);
        svc.mastermap[table.id]["type"] = "table";

        _.each(table.codes, function(c) {
          svc.addCodes(c, parent.concat([[table.id, 'table']]));
        });
      }
    }

    svc.addCodes = function(code, parent) {
      //           console.log("Processing code " + code.id);

      svc.createMMElement(code.id);
      svc.addParentsId(code.id, "code", parent);
      svc.mastermap[code.id]["type"] = "code";
    }


    svc.addDatatype = function(datatypeId, parent) {
      //           console.log("Processing datatype ");

      if (datatypeId !== undefined && datatypeId !== "") {

        var dt = svc.getDatatypeLibrary()[datatypeId];
        if (dt !== undefined){
          svc.createMMElement(dt.id);
          svc.addParentsId(dt.id, "datatype", parent);
          svc.mastermap[dt.id]["type"] = "datatype";

          dt.components.forEach( function(c) {
            svc.createMMElement(c.id);
            svc.createMMElement(c.id, "component", parent.concat([[dt.id, "datatype"]]));
            svc.mastermap[c.id]["usage"] = dt["usage"];
            // svc.addDatatype(c.datatype, parent.concat(new  Array([dt.id, 'datatype']))); ?? nedd to process subcomponent without infinite loop
          });
        } else {
          console.log("!!! => datatype id " + datatypeId + " not found");
        }
      }
    }

    svc.addField = function (field, parent) {
      //           console.log("Processing field " + field.id);

      svc.createMMElement(field.id);
      svc.addParentsId(field.id, "field", parent);
      svc.mastermap[field.id]["type"] = "field";
      svc.mastermap[field.id]["usage"] = field["usage"];

      svc.addValueSet(field.table, parent.concat([[field.id, 'table']]));
      svc.addDatatype(field.datatype, parent.concat([[field.id, 'field']]));
    }


    svc.addSegment = function (segmentId, parent) {
      //           console.log("Processing segment " + segmentId + "");

      var segment = svc.getSegmentLibrary()[segmentId];
      if (segment !== undefined){
        svc.createMMElement(segment.id);
        svc.addParentsId(segment.id, "segment", parent);
        svc.mastermap[segment.id]["type"] = "segment";

        _.each(segment.fields, function(f) {
          svc.addField(f, parent.concat([[segment.id, "segment"]]));
        });
      } else {
        console.log("!!! => segment id " + segmentId + " not found");
      }
    }

    svc.addMessage = function (message, parent) {
      //           console.log("processing message id : " + message.id);

      svc.createMMElement(message.id);
      svc.addParentsId(message.id, "message", parent);
      svc.mastermap[message.id]["type"] = "message";

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

      svc.createMMElement(segRefId);
      svc.mastermap[segRefId]["usage"] = segmentRef["usage"];
      svc.mastermap[segRefId]["type"] = "segmentRef";
      svc.addParentsId(segRefId, "segmentRef", parent);

      svc.addSegment(segRef, parent.concat([[segRefId + "", 'segmentRef']]));
    }

    svc.addGroup = function (group, parent) {
      //           console.log("Processing group " + group.id);

      svc.createMMElement(group.id);
      svc.mastermap[group.id]["usage"] = group["usage"];
      svc.mastermap[group.id]["type"] = "group";
      svc.createMMElement(group.id, "group", parent);

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

      svc.createMMElement(profile.id);
      svc.mastermap[profile.id]["ig"].concat(profile.id);

      _.each(profile.messages.children, function(m) {
        var parentsList = [[profile.id, "ig"]];
        svc.createMMElement(profile.id, "ig", parentsList);
        svc.mastermap[profile.id]["type"] = "ig";

        svc.addMessage(m, parentsList);
      });

    }

    svc.addParentsId = function (elementId, elementType, parentsList) {
      //             console.log(elementId + " -> " + parentsList)
      //Element refers to self
      svc.mastermap[elementId][elementType] = svc.mastermap[elementId][elementType].concat(elementId);
      //Add parents reference in element
      _.each(parentsList, function(parent) {
        var parentId = parent[0];
        var parentType = parent[1];
        svc.mastermap[elementId][parentType] = svc.mastermap[elementId][parentType].concat(parentId);
      });
      // Add element reference in parents
      _.each(parentsList, function(parent) {
        var parentId = parent[0];
        var parentType = parent[1];
        svc.mastermap[parentId][elementType] = svc.mastermap[elementId][parentType].concat(elementId);
      });
    }

    svc.createMMElement = function (elementId) {
      //           if (!(elementId in Object.keys(svc.mastermap))) {
      if (svc.mastermap.indexOf(elementId) === -1) {
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
        eltColl["type"] = "";
        eltColl["id"] = elementId;

        svc.mastermap[elementId] = eltColl;
      }
    }

    svc.createSegmentLibrary = function (igdocument){
      //             console.log("Creating segment library");
      /*             var segdict = [];
             igdocument.profile.segments.children.forEach(function(n){
              segdict[n.id] = n;
            });
            return segdict; */
      igdocument.profile.segments.children.forEach(function(n){
        svc.segmentLibrary[n.id] = n;
      });
    }


    svc.createTableLibrary = function (igdocument){
      //             console.log("Creating table library");
      /*             var tabledict = [];
            igdocument.profile.tables.children.forEach(function(n){
              tabledict[n.id] = n;
            });
            return tabledict; */
      igdocument.profile.tables.children.forEach(function(n){
        svc.tableLibrary[n.id] = n;
      });
    }


    svc.createDatatypeLibrary = function (igdocument){
      //             console.log("Creating datatype library");
      /*             var dtdict = [];
            igdocument.profile.datatypes.children.forEach(function(n){
              dtdict[n.id] = n;
            });
            return dtdict; */
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
/*        svc.getMastermap().forEach(function(n){
        if (n["id"] === id)
          if (n["type"] === type)
            return n;
        }); */
       for (var elt in svc.mastermap){
        if (svc.mastermap[elt]["id"] === id && svc.mastermap[elt]["type"] === type){
            return svc.mastermap[elt];}
        };

      return undefined;
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
        }
        if (type === "component"){
          return svc.getElement(id, type)["usage"];
        }
        if (type === "code"){
          //TBD
        }
      }

    }

    return svc;

  });
