/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').factory('CompareService',
  function($rootScope, ViewSettings, ElementUtils, $q, $http, FilteringSvc, SegmentLibrarySvc, TableLibrarySvc, DatatypeLibrarySvc, ObjectDiff,$filter) {
    var CompareService = {














      orderBy: function(list, criteria) {
        if(list == null || list == undefined)
          return list;
        return $filter('orderBy')(list, criteria);
      },
      getNodes: function(parent, root) {
        if (!parent || parent == null) {
          return root.children;
        } else {
          return parent.children;
        }
      },
      getTemplate: function(node, root) {
        if (ViewSettings.tableReadonly) {
          if (node.obj.type === 'segmentRef') {
            return 'MessageSegmentRefReadTree.html';
          } else if (node.obj.type === 'group') {
            return 'MessageGroupReadTree.html';
          } else if (node.obj.type === 'field') {
            return 'MessageFieldViewTree.html';
          } else if (node.obj.type === 'component') {
            return 'MessageComponentViewTree.html';
          } else {
            return 'MessageReadTree.html';
          }
        } else {

          if (node.obj.type === 'segmentRef') {
            return 'MessageSegmentRefEditTree.html';
          } else if (node.obj.type === 'group') {
            return 'MessageGroupEditTree.html';
          } else if (node.obj.type === 'field') {
            return 'MessageFieldViewTree.html';
          } else if (node.obj.type === 'component') {
            return 'MessageComponentViewTree.html';
          } else {
            return 'MessageEditTree.html';
          }
        }
      },
      objToArray: function(object) {
        var result = [];
        $.map(object, function(value, index) {

          result.push(value);
        });
        return result;

      },
      cmpMessage: function(msg1, msg2, dtList1, dtList2, segList1, segList2) {
        msg1.children = CompareService.orderBy(msg1.children, 'position');
        msg2.children =  CompareService.orderBy(msg2.children, 'position');
        var msg1 = CompareService.fMsg(msg1, dtList1, segList1);
        var msg2 = CompareService.fMsg(msg2, dtList2, segList2);
        var diff = ObjectDiff.diffOwnProperties(msg1, msg2);
        var dataList = [];
        if (diff.changed === "object change") {
          var array = CompareService.objToArray(diff);
          var arraySeg = CompareService.objToArray(array[1].segments.value);
          for (var i = 0; i < arraySeg.length; i++) {
            CompareService.writettTable(arraySeg[i], dataList);
          }
        }
        var isEmpty = true;
        var result = [];
        for (var i = 0; i < dataList.length; i++) {
          if (!_.isEmpty(dataList[i])) {
            result.push(dataList[i]);
            isEmpty = false;
          }
        }
        if (isEmpty) {
          return null;
        } else {
            return result;
        }
      },
      cmpSegment: function(segment1, segment2, dtList1, dtList2, segList1, segList2) {

        segment1.fields = CompareService.orderBy(segment1.fields, 'position');
        segment2.fields =  CompareService.orderBy(segment2.fields, 'position');

        var seg1 = CompareService.fSegment(segment1, dtList1, segList1);
        var seg2 = CompareService.fSegment(segment2, dtList2, segList2);

          for(i=0;i<seg1.valueSetBindings.length; i++){
              if(seg1.valueSetBindings[i].location){
                  var location = parseInt(seg1.valueSetBindings[i].location);
                  if(seg1.components[location]){
                      if(seg1.components[location].tables){
                          seg1.components[location].tables.push({id:seg1.valueSetBindings[i].tableId});
                      }else{
                          seg1.components[location].tables=[];
                          seg1.components[location].tables.push({id:seg1.valueSetBindings[i].tableId});
                      }
                  }
              }
          }
          for(i=0;i<seg2.valueSetBindings.length; i++){
              if(seg2.valueSetBindings[i].location){
                  var location = parseInt(seg2.valueSetBindings[i].location);
                  if(seg2.components[location]){
                      if(seg2.components[location].tables){
                          seg2.components[location].tables.push({id:seg2.valueSetBindings[i].tableId});
                      }else{
                          seg2.components[location].tables=[];
                          seg2.components[location].tables.push({id:datatype2.valueSetBindings[i].tableId});
                      }
                  }
              }
          }

        //var diff = ObjectDiff.diffOwnProperties(seg1, seg2);
        var diff = ObjectDiff.diff(seg1, seg2);

        var dataList = [];
        if (diff.changed === "object change") {
          var array = CompareService.objToArray(diff);
          var arraySeg = CompareService.objToArray(array[1]);
          CompareService.writettTable(arraySeg[0], dataList);
        }
        return dataList;
      },
      cmpDatatype: function(datatype1, datatype2) {

          datatype1.components = CompareService.orderBy(datatype1.components, 'position');
          datatype2.components = CompareService.orderBy(datatype2.components, 'position');

          var delay = $q.defer();
          var wrapper = {};
          wrapper.d1=datatype1;
          wrapper.d2=datatype2;

          console.log(wrapper);

          $http.post('api/diff/datatype', wrapper).then(function(response) {
              var ref = angular.fromJson(response.data);
              console.log(ref);
              delay.resolve(ref);
          }, function(error) {
              delay.reject(error);
          });
          return delay.promise;


      },
      cmpValueSet: function(table1, table2) {

        if(table1.sourceType ===table2.sourceType &&table1.sourceType=='INTERNAL'){
            table1.codes =  CompareService.orderBy(table1.codes, 'value');
            table2.codes =  CompareService.orderBy(table2.codes, 'value');

            var vs1 = table1;
            var vs2 = table2;


            var diff = ObjectDiff.diffOwnProperties(vs1, vs2);
            var dataList = [];
            if (diff.changed === "object change") {
                CompareService.writettTable(diff, dataList);
            }

            return dataList
        }else return null;


      },
      fMsg: function(msg, datatypeList, segmentList) {

        var elements = []
        var message = {
          name: msg.name,
          event: msg.event,
          structID: msg.structID,
          position: msg.position,
          segments: CompareService.fElements(msg.children, datatypeList, segmentList)
        };
        // for (var i = 0; i < msg.children.length; i++) {
        //     elements.push($scope.fElement(msg.children[i]));
        // };
        // message.segments=elements;

        return message;
      },
      fSegment: function(segment, datatypeList, segmentList) {
        var elements = [];

        if (segment.type === "segment") {
          elements.push(segment);
        }

        return CompareService.fElements(elements, datatypeList, segmentList);
      },
      fDatatype: function(datatype, datatypeList, segmentList) {
        var elements = [];
        if (datatype.type === "datatype") {
          elements.push(datatype);
        }
        return CompareService.fElements(elements, datatypeList, segmentList);
      },

      fElements: function(elements, datatypeList, segmentList) {

        var result = [];
        for (var i = 0; i < elements.length; i++) {
          if (elements[i].type === 'segmentRef') {
            var segment = {};

            segment = {
              //id: elements[i].ref.id,
              name: elements[i].ref.name,
              label: elements[i].ref.label,
              type: elements[i].type,
              minCard: elements[i].min,
              maxCard: elements[i].max,
              usage: elements[i].usage,
              position: elements[i].position
            };

            for (var j = 0; j < segmentList.length; j++) {


              if (elements[i].ref.id === segmentList[j].id) {

                segment.description = segmentList[j].description;
                segment.conformanceStatements = segmentList[j].conformanceStatements;
                segment.coConstraints = segmentList[j].coConstraints;
                segment.predicates = segmentList[j].predicates;
                segment.fields = CompareService.fFields(segmentList[j].fields, datatypeList, segmentList);
              }
            };



            result.push(segment);

          } else if (elements[i].type === 'group') {
            result.push(CompareService.fGrp(elements[i], datatypeList, segmentList));
          } else if (elements[i].type === 'segment') {
            elements[i].fields = CompareService.fFields(elements[i].fields, datatypeList, segmentList);
            result.push(elements[i]);
          } else if (elements[i].type === 'datatype') {
            elements[i].components = CompareService.fFields(elements[i].components, datatypeList, segmentList);
            result.push(elements[i]);
          } else if (elements[i].type === 'component') {
            //elements[i].fields = $scope.fFields(elements[i].fields, datatypeList, segmentList);
          }
        };
        return result;
      },
      fGrp: function(grp, datatypeList, segmentList) {
        var group = {
          name: grp.name,
          type: grp.type,
          minCard: grp.min,
          maxCard: grp.max,
          usage: grp.usage,
          position: grp.position,
          segments: CompareService.fElements(grp.children, datatypeList, segmentList)
        };
        return group;
      },
      fFields: function(fields, datatypeList, segmentList) {
        // for (var j = 0; j < datatypeList.length; j++) {
        //     for (var i = 0; i < datatypeList[j].components.length; i++) {
        //         datatypeList[j].components[i].id = "";
        //         datatypeList[j].components[i].datatype.id = "";

        //     }
        // }

        for (var i = 0; i < fields.length; i++) {
          fields[i].id = "";
          fields[i].dateUpdated = null;
          for (var j = 0; j < datatypeList.length; j++) {
            if (fields[i].datatype.id === datatypeList[j].id) {
              fields[i].components = CompareService.fFields(datatypeList[j].components, datatypeList, segmentList);
            }
          };
          if (fields[i].datatype.ext === "") {
            fields[i].datatype.ext = null;
          }
          fields[i].datatype.id = "";
          for (var k = 0; k < fields[i].tables.length; k++) {
            if(fields[i].tables[k].id ==null && fields[i].tables[k].id ==""){
                fields[i].tables[k].id = "";

            }
          }
        };
          return fields;
      },
      writettTable: function(childArray, dataArray) {
        var result = {};


        if (childArray.changed === "object change") {
          if (childArray.value.position && childArray.value.position.changed === "equal") {
            result.position = {
              element: childArray.value.position.value,

            };
          }

          if (childArray.value.type.changed === "equal") {
            result.type = {
              element: childArray.value.type.value,
            };

            if (childArray.value.usage && childArray.value.usage.changed === "primitive change") {
              result.usage = {
                element1: childArray.value.usage.removed,
                element2: childArray.value.usage.added

              };
            }
            if (childArray.value.type.value === "field" || childArray.value.type.value === "component" || childArray.value.type.value === "datatype") {

              if (childArray.value.name.changed === "primitive change") {
                result.label = {
                  element1: childArray.value.name.removed,
                  element2: childArray.value.name.added

                };

              }


              if (childArray.value.name.changed === "equal") {

                result.label = {
                  element: childArray.value.name.value,
                };
              }
              if (childArray.value.ext && childArray.value.ext.changed === "primitive change") {
                result.ext = {
                  element1: childArray.value.ext.removed,
                  element2: childArray.value.name.added

                };

              }


              if (childArray.value.ext && childArray.value.ext.changed === "equal" && childArray.value.ext.value !== null) {
                result.ext = {
                  element: childArray.value.ext.value,

                };
              }
              if (childArray.value.min && childArray.value.min.changed === "primitive change") {
                result.minCard = {
                  element1: childArray.value.min.removed,
                  element2: childArray.value.min.added

                };
              } else if (childArray.value.min && childArray.value.min.changed === "removed") {} else if (childArray.value.min && childArray.value.min.changed === "added") {}
              if (childArray.value.max && childArray.value.max.changed === "primitive change") {
                result.maxCard = {
                  element1: childArray.value.max.removed,
                  element2: childArray.value.max.added

                };
              }
              if (childArray.value.minLength && childArray.value.minLength.changed === "primitive change") {
                result.minLength = {
                  element1: childArray.value.minLength.removed,
                  element2: childArray.value.minLength.added

                };
              }
              if (childArray.value.maxLength && childArray.value.maxLength.changed === "primitive change") {
                result.maxLength = {
                  element1: childArray.value.maxLength.removed,
                  element2: childArray.value.maxLength.added

                };
              }
              if (childArray.value.confLength && childArray.value.confLength.changed === "primitive change") {
                result.confLength = {
                  element1: childArray.value.confLength.removed,
                  element2: childArray.value.confLength.added

                };
              }
              if (childArray.value.datatype && childArray.value.datatype.changed === "object change") {
                result.datatype = {
                  element1: childArray.value.datatype.value.label.removed,
                  element2: childArray.value.datatype.value.label.added

                };
              }
              if (childArray.value.components && childArray.value.components.changed === "object change") {
                result.components = [];
                CompareService.objToArray(childArray.value.components.value).forEach(function(childNode) {
                  CompareService.writettTable(childNode, result.components);

                });
              }
              var objToArray = function(object) {
                var result = [];
                $.map(object, function(value, index) {

                  result.push(value);
                });
                return result;

              };
              if (childArray.value.tables && childArray.value.tables.changed === "object change") {
                result.valuesets = [];

                var tables = objToArray(childArray.value.tables.value);
                for (var i = 0; i < tables.length; i++) {

                  if (tables[i].changed === "object change") {

                    if (tables[i].value.bindingIdentifier.changed === "primitive change") {
                      result.valuesets.push({
                        element1: tables[i].value.bindingIdentifier.removed,
                        element2: tables[i].value.bindingIdentifier.added

                      });
                    }
                  } else if (tables[i].changed === "removed") {

                    result.valuesets.push({
                      element1: tables[i].value.bindingIdentifier,
                      element2: ""
                    });
                  } else if (tables[i].changed === "added") {
                    result.valuesets.push({
                      element1: "",
                      element2: tables[i].value.bindingIdentifier
                    });
                  }
                }
              }
            } else {

              if (childArray.value.minCard && childArray.value.minCard.changed === "primitive change") {
                result.minCard = {
                  element1: childArray.value.minCard.removed,
                  element2: childArray.value.minCard.added

                };
              }
              if (childArray.value.maxCard && childArray.value.maxCard.changed === "primitive change") {
                result.maxCard = {
                  element1: childArray.value.maxCard.removed,
                  element2: childArray.value.maxCard.added

                };
              }
            }

            if (childArray.value.type.value === "segmentRef" || childArray.value.type.value === "segment") {
              if (childArray.value.name.changed === "primitive change") {
                result.label = {
                  element1: childArray.value.name.removed,
                  element2: childArray.value.name.added
                };
              }
              if (childArray.value.name.changed === "equal") {
                result.label = {
                  element: childArray.value.name.value,
                };
              }
              if (childArray.value.ext && childArray.value.ext.changed === "primitive change") {
                result.ext = {
                  element1: childArray.value.ext.removed,
                  element2: childArray.value.ext.added
                };
              }
              if (childArray.value.ext && childArray.value.ext.changed === "equal") {
                result.ext = {
                  element: childArray.value.ext.value,
                };
              }
              if (childArray.value.description && childArray.value.description.changed === "primitive change") {
                result.description = {
                  element1: childArray.value.description.removed,
                  element2: childArray.value.description.added
                };
              } else if (childArray.value.description && childArray.value.description.changed === "equal") {
                result.description = {
                  element: childArray.value.description.value,

                };
              }


              if (childArray.value.fields && childArray.value.fields.changed === "object change") {
                result.fields = [];
                CompareService.objToArray(childArray.value.fields.value).forEach(function(childNode) {
                  CompareService.writettTable(childNode, result.fields);

                });
              }

            } else if (childArray.value.type.value === "group") {
              if (childArray.value.name.changed === "primitive change") {
                result.label = {
                  element1: childArray.value.name.removed,
                  element2: childArray.value.name.added

                };

              }

              if (childArray.value.name.changed === "equal") {
                result.label = {
                  element: childArray.value.name.value,

                };
              }
              if (childArray.value.segments.changed === "object change") {
                result.segments = [];
                CompareService.objToArray(childArray.value.segments.value).forEach(function(childNode) {
                  CompareService.writettTable(childNode, result.segments);

                });


              }
            } else if (childArray.value.type.value === "table") {

              if (childArray.value.bindingIdentifier.changed === "primitive change") {
                result.label = {
                  element1: childArray.value.bindingIdentifier.removed,
                  element2: childArray.value.bindingIdentifier.added

                };

              }

              if (childArray.value.bindingIdentifier.changed === "equal") {
                result.label = {
                  element: childArray.value.bindingIdentifier.value,

                };
              }
              if (childArray.value.description && childArray.value.description.changed === "primitive change") {
                result.description = {
                  element1: childArray.value.description.removed,
                  element2: childArray.value.description.added
                };
              } else if (childArray.value.description && childArray.value.description.changed === "equal") {
                result.description = {
                  element: childArray.value.description.value,

                };
              }
              if (childArray.value.extensibility && childArray.value.extensibility.changed === "primitive change") {
                result.extensibility = {
                  element1: childArray.value.extensibility.removed,
                  element2: childArray.value.extensibility.added
                };
              }
                if (childArray.value.contentDefinition && childArray.value.contentDefinition.changed === "primitive change") {
                    result.contentDefinition = {
                        element1: childArray.value.contentDefinition.removed,
                        element2: childArray.value.contentDefinition.added
                    };
                }
                if (childArray.value.stability && childArray.value.stability.changed === "primitive change") {
                    result.stability = {
                        element1: childArray.value.stability.removed,
                        element2: childArray.value.stability.added
                    };
                }
              if (childArray.value.codes && childArray.value.codes.changed === "object change") {
                result.codes = [];
                CompareService.objToArray(childArray.value.codes.value).forEach(function(childNode) {
                  CompareService.writettTable(childNode, result.codes);

                });
              }
            } else if (childArray.value.type.value === "code") {

                if (childArray.value.codeSystem && childArray.value.codeSystem.changed === "primitive change") {
                    result.codeSystem = {
                        element1: childArray.value.codeSystem.removed,
                        element2: childArray.value.codeSystem.added
                    };
                }
                if (childArray.value.codeUsage && childArray.value.codeUsage.changed === "primitive change") {
                    result.codeUsage = {
                        element1: childArray.value.codeUsage.removed,
                        element2: childArray.value.codeUsage.added
                    };
                }
                if (childArray.value.label && childArray.value.label.changed === "primitive change") {
                    result.description = {
                        element1: childArray.value.label.removed,
                        element2: childArray.value.label.added
                    };
                } else if (childArray.value.label && childArray.value.label.changed === "equal") {
                    result.description = {
                        element: childArray.value.label.value,

                    };
                }
                if (childArray.value.value && childArray.value.value.changed === "primitive change") {
                    result.label = {
                        element1: childArray.value.value.removed,
                        element2: childArray.value.value.added
                    };
                } else if (childArray.value.value && childArray.value.value.changed === "equal") {
                    result.label = {
                        element: childArray.value.value.value,

                    };
                }
            }
          } else if (childArray.value.type.changed === "primitive change") {
            result.label = {
              element1: childArray.value.name.removed,
              element2: childArray.value.name.added

            };
            result.type = {
              element1: childArray.value.type.removed,
              element2: childArray.value.type.added
            };
          }





        } else if (childArray.changed === "added") {
          if (childArray.value.type === "field" || childArray.value.type === "component") {
            var empty = "";


            result.position = {
              element: childArray.value.position,

            };
            result.type = {
              element2: childArray.value.type,

            };
            result.label = {
              element1: empty,
              element2: childArray.value.name
            };
            result.usage = {
              element1: empty,
              element2: childArray.value.usage
            };
            result.minCard = {
              element1: empty,
              element2: childArray.value.min
            };
            result.maxCard = {
              element1: empty,
              element2: childArray.value.max
            };
            result.minLength = {
              element1: empty,
              element2: childArray.value.minLength
            };
            result.maxLength = {
              element1: empty,
              element2: childArray.value.maxLength
            };
            result.confLength = {
              element1: empty,
              element2: childArray.value.confLength
            };
            result.datatype = {
              element1: empty,
              element2: childArray.value.datatype.label
            };
            var components = [];
            if (childArray.value.components) {
              for (var i = 0; i < childArray.value.components.length; i++) {
                var component = {};
                component.position = {
                  element: childArray.value.components[i].position,

                };
                component.type = {
                  element2: childArray.value.components[i].type,

                };
                component.label = {
                  element1: empty,
                  element2: childArray.value.components[i].name
                };
                component.usage = {
                  element1: empty,
                  element2: childArray.value.components[i].usage
                };

                component.minLength = {
                  element1: empty,
                  element2: childArray.value.components[i].minLength
                };
                component.maxLength = {
                  element1: empty,
                  element2: childArray.value.components[i].maxLength
                };
                component.confLength = {
                  element1: empty,
                  element2: childArray.value.components[i].confLength
                };
                component.datatype = {
                  element1: empty,
                  element2: childArray.value.components[i].datatype.label
                };
                components.push(component);


              }
            }

            result.components = components;


          }


        } else if (childArray.changed === "removed") {
          var empty = "";
          if (childArray.value.type === "field" || childArray.value.type === "component") {

            result.position = {
              element: childArray.value.position,

            };
            result.type = {
              element1: childArray.value.type,

            };
            result.label = {
              element2: empty,
              element1: childArray.value.name
            };
            result.usage = {
              element2: empty,
              element1: childArray.value.usage
            };
            result.minCard = {
              element2: empty,
              element1: childArray.value.min
            };
            result.maxCard = {
              element2: empty,
              element1: childArray.value.max
            };
            result.minLength = {
              element2: empty,
              element1: childArray.value.minLength
            };
            result.maxLength = {
              element2: empty,
              element1: childArray.value.maxLength
            };
            result.confLength = {
              element2: empty,
              element1: childArray.value.confLength
            };
            result.datatype = {
              element2: empty,
              element1: childArray.value.datatype.label
            };
            var components = [];
            if (childArray.value.components) {
              for (var i = 0; i < childArray.value.components.length; i++) {
                var component = {};
                component.position = {
                  element: childArray.value.components[i].position,

                };
                component.type = {
                  element1: childArray.value.components[i].type,

                };
                component.label = {
                  element2: empty,
                  element1: childArray.value.components[i].name
                };
                component.usage = {
                  element2: empty,
                  element1: childArray.value.components[i].usage
                };

                component.minLength = {
                  element2: empty,
                  element1: childArray.value.components[i].minLength
                };
                component.maxLength = {
                  element2: empty,
                  element1: childArray.value.components[i].maxLength
                };
                component.confLength = {
                  element2: empty,
                  element1: childArray.value.components[i].confLength
                };
                component.datatype = {
                  element2: empty,
                  element1: childArray.value.components[i].datatype.label
                };
                components.push(component);


              }
            }

            result.components = components;




          }


        }
        dataArray.push(result);
      },




    };

    return CompareService;

  });
