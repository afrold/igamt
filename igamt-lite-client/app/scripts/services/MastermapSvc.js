'use strict';
angular
  .module('igl')
  .service (
      'MastermapSvc',
      function($rootScope) {

        var svc = {};

        svc.mastermap = [];

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

        svc.addValueSet = function(tableId, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("Processing table " + tableId);

          if (tableId !== undefined && tableId !== "") {
            var table = tableLibrary[tableId];
            svc.createMMElement(table.id);
            svc.addParentsId(table.id, "table", parent);

            _.each(table.codes, function(c) {
              svc.addCodes(c, parent.concat([[table.id, 'table']]));
            });
          }
        }

        svc.addCodes = function(code, parent) {
//           console.log("Processing code " + code.id);

          svc.createMMElement(code.id);
          svc.addParentsId(code.id, "code", parent);
        }


        svc.addDatatype = function(datatypeId, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("Processing datatype ");

          if (datatypeId !== undefined && datatypeId !== "") {

            var dt = datatypeLibrary[datatypeId];
            if (dt !== undefined){
              svc.createMMElement(dt.id);
              svc.addParentsId(dt.id, "datatype",parent);

              dt.components.forEach( function(c) {
                svc.createMMElement(c.id);
                svc.createMMElement(c.id, "component", parent.concat([[dt.id, 'datatype']]));
                // svc.addDatatype(c.datatype, parent.concat(new  Array([dt.id, 'datatype']))); ?? nedd to process subcomponent without infinite loop
              });
            } else {
              console.log("!!! => datatype id " + datatypeId + " not found");
            }
          }
        }

        svc.addField = function (field, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("Processing field " + field.id);

          svc.createMMElement(field.id);
          svc.addParentsId(field.id, "field", parent);

          svc.addValueSet(field.table, parent.concat([[field.id, 'field']]), segmentLibrary, datatypeLibrary, tableLibrary);
          svc.addDatatype(field.datatype, parent.concat([[field.id, 'field']]), segmentLibrary, datatypeLibrary, tableLibrary);
        }


        svc.addSegment = function (segmentId, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("Processing segment " + segmentId + "");

          var segment = segmentLibrary[segmentId];
          if (segment !== undefined){
            svc.createMMElement(segment.id);
            svc.addParentsId(segment.id, "sgt", parent);

            _.each(segment.fields, function(f) {
              svc.addField(f, parent.concat([[segment.id, 'sgt']]), segmentLibrary, datatypeLibrary, tableLibrary);
            });
          } else {
            console.log("!!! => segment id " + segmentId + "not found");
          }
        }

        svc.addMessage = function (message, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("processing message id : " + message.id);

          svc.createMMElement(message.id);
          svc.addParentsId(message.id, "msg", parent);

          _.each(message.children, function(n) {
            if (n.type === "segmentRef"){
              svc.addSegmentRef(n, parent.concat([[message.id, 'msg']]), segmentLibrary, datatypeLibrary, tableLibrary);
            } else {
              svc.addGroup(n, parent.concat([[message.id, 'msg']]), segmentLibrary, datatypeLibrary, tableLibrary);
            }
          });
        }

        svc.addSegmentRef = function (segmentRef, parent, segmentLibrary, datatypeLibrary, tableLibrary){
//           console.log("Processing segment ref " + segmentRef.id);

          var segRefId = segmentRef.id;
          var segRef = segmentRef.ref;

          svc.createMMElement(segRefId);
          svc.addParentsId(segRefId, "segmentRef", parent);

          svc.addSegment(segRef, parent.concat([[segRefId + "", 'segmentRef']]), segmentLibrary, datatypeLibrary, tableLibrary);
        }

        svc.addGroup = function (group, parent, segmentLibrary, datatypeLibrary, tableLibrary) {
//           console.log("Processing group " + group.id);

          svc.createMMElement(group.id);
          svc.createMMElement(group.id, "group", parent);

          _.each(group.children, function(n) {
            if (n.type === "segmentRef"){
              svc.addSegmentRef(n, parent.concat([[group.id, 'group']]), segmentLibrary, datatypeLibrary, tableLibrary);
            } else {
              svc.addGroup(n, parent.concat([[group.id, 'group']]), segmentLibrary, datatypeLibrary, tableLibrary);
            }
          });
        }


        svc.addIG = function (igdocument) {
          console.log("Creating mastermap\nprocessing IG : " + igdocument.profile.id);

          var profile = igdocument.profile;

          var segmentLibrary = svc.createSegmentLibrary(igdocument);
          var datatypeLibrary = svc.createDatatypeLibrary(igdocument);
          var tableLibrary = svc.createTableLibrary(igdocument);


          svc.createMMElement(profile.id);
          svc.mastermap[profile.id]["ig"].concat(profile.id);

          _.each(profile.messages.children, function(m) {
            var parentsList = [[profile.id, 'ig']];
            svc.createMMElement(profile.id, "ig", parentsList);

            svc.addMessage(m, parentsList, segmentLibrary, datatypeLibrary, tableLibrary);
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
            eltColl["msg"] =[];
            eltColl["field"] =[];
            eltColl["sgt"] =[];
            eltColl["segmentRef"] =[];
            eltColl["group"] =[];
            eltColl["table"] =[];
            eltColl["datatype"] =[];
            eltColl["component"] =[];
            eltColl["code"] =[];
            eltColl["usage"] =[];

            svc.mastermap[elementId] = eltColl;
          }
        }

        svc.createSegmentLibrary = function (igdocument){
//             console.log("Creating segment library");
            var segdict = [];
            igdocument.profile.segments.children.forEach(function(n){
              segdict[n.id] = n;
            });
            return segdict;
          }


        svc.createTableLibrary = function (igdocument){
//             console.log("Creating table library");
            var tabledict = [];
            igdocument.profile.tables.children.forEach(function(n){
              tabledict[n.id] = n;
            });
            return tabledict;
          }


        svc.createDatatypeLibrary = function (igdocument){
//             console.log("Creating datatype library");
            var dtdict = [];
            igdocument.profile.datatypes.children.forEach(function(n){
              dtdict[n.id] = n;
            });
            return dtdict;
          }

        return svc;

});
