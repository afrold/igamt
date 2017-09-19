/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('addComponentsCtrl', function($scope, $rootScope, $mdDialog, messages, segments, segmentsMap, datatypesMap, currentPc, PcLibraryService, datatypes, ngTreetableParams, $http, SegmentLibrarySvc, PcService, orderByFilter) {
  $scope.selectedPC = [];
  $scope.findingBindings = function(node) {
    var result = [];

    if (node && (node.type === "field" || node.type === "component")) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      result = _.filter(node.parentValueSetBindings, function(binding) { return binding.location == path; });
      for (var i = 0; i < result.length; i++) {
        result[i].bindingFrom = 'segment';
      }

      if (result && result.length > 0) {
        return result;
      }
    }
    return result;
  };
  $scope.findingComments = function(node) {
    var result = [];

    if (node) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      result = _.filter(node.parentComments, function(binding) { return binding.location == path; });
      for (var i = 0; i < result.length; i++) {
        result[i].from = 'segment';
      }

      if (result && result.length > 0) {
        return result;
      }
    }
    return result;
  };
  $scope.findingSingleElement = function(node) {
    var result = null;

    if (node && (node.type === "field" || node.type === "component")) {
      var index = node.path.indexOf(".");
      var path = node.path.substr(index + 1);
      result = _.find(node.parentSingleElementValues, function(binding) { return binding.location == path; });
      if (result) {
        result.from = 'segment';
        return result;
      }
    }
    return result;
  };
  $scope.MsgProfileComponentParams = new ngTreetableParams({
    getNodes: function(parent) {
      if (messages !== undefined) {
        if (parent) {
          if (parent.children) {
            for (var i = 0; i < parent.children.length; i++) {
              if (parent.type === 'group') {
                parent.children[i].parent = parent.parent + '.' + parent.position;
                parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                parent.children[i].parentComments = parent.parentComments;
                parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                parent.children[i].source = parent.source;
                if (parent.children[i].type === 'segmentRef') {
                  parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                  parent.children[i].source = parent.source;
                  parent.children[i].from = "message";
                }
              } else if (parent.type === 'message') {
                parent.children[i].parent = parent.structID;
                parent.children[i].parentValueSetBindings = parent.valueSetBindings;
                parent.children[i].parentComments = parent.comments;
                parent.children[i].parentSingleElementValues = parent.singleElementValues;
                parent.children[i].source = {};
                parent.children[i].source.messageId = parent.id;
                if (parent.children[i].type === 'segmentRef') {
                  parent.children[i].children = segmentsMap[parent.children[i].ref.id].fields;
                  parent.children[i].from = "message";
                }
              } else if (parent.type === 'segmentRef') {
                parent.children[i].parent = parent.parent + '.' + parent.position;
                parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                parent.children[i].parentComments = parent.parentComments;
                parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                parent.children[i].source = parent.source;
                parent.children[i].source.segmentId = parent.ref.id;
                parent.children[i].from = "message";
              } else if (parent.type === 'field' || parent.type === 'component') {
                parent.children[i].parent = parent.parent + '.' + parent.position;
                parent.children[i].children = datatypesMap[parent.children[i].datatype.id].components;
                parent.children[i].parentValueSetBindings = parent.parentValueSetBindings;
                parent.children[i].parentComments = parent.parentComments;
                parent.children[i].parentSingleElementValues = parent.parentSingleElementValues;
                parent.children[i].source = parent.source;
                if (parent.type === "field") {
                  parent.children[i].source.fieldDt = parent.datatype.id;
                } else if (parent.type === "component") {
                  parent.children[i].source.componentDt = parent.datatype.id;
                }
                parent.children[i].from = "message";
              }
            }
            return parent.children;
          }
        } else {

          return messages;
        }
      }
    },
    getTemplate: function(node) {
      return 'MsgProfileComponentTable';
    }
  });
  $scope.removeSelectedComp = function(pc) {
    var index = $scope.selectedPC.indexOf(pc);
    if (index > -1) $scope.selectedPC.splice(index, 1);
  };
  $scope.addElementPc = function(node, event) {
    var currentScope = angular.element(event.target).scope();
    var pc = currentScope.node;
    var parent = currentScope.parentNode;
    if (pc.type === 'message') {
      var newPc = {
        id: new ObjectId().toString(),
        obj : pc,
        type: pc.type,
        name: pc.name,
        path: pc.structID,
        source: {
          messageId: pc.id,
        },
        oldConformanceStatements: pc.conformanceStatements,
        from: "message",
        attributes: {
          oldConformanceStatements: pc.conformanceStatements,
          conformanceStatements: null,
        },
        appliedTo: [],
        version: ""
      };
    } else if (pc.type === 'segmentRef') {
      var newPc = {
        id: new ObjectId().toString(),
        obj : pc,
        type: pc.type,
        path: pc.parent + '.' + pc.position,
        itemId: pc.id,
        oldValueSetBindings: $rootScope.segmentsMap[pc.ref.id].valueSetBindings,
        source: pc.source,
        from: "message",
        attributes: {
          oldRef: {
            id: $rootScope.segmentsMap[pc.ref.id].id,
            name: $rootScope.segmentsMap[pc.ref.id].name,
            ext: $rootScope.segmentsMap[pc.ref.id].ext,
            label: $rootScope.segmentsMap[pc.ref.id].label,

          },
          ref: {
            id: $rootScope.segmentsMap[pc.ref.id].id,
            name: $rootScope.segmentsMap[pc.ref.id].name,
            ext: $rootScope.segmentsMap[pc.ref.id].ext,
            label: $rootScope.segmentsMap[pc.ref.id].label,

          },
          oldDynamicMappingDefinition: $rootScope.segmentsMap[pc.ref.id].dynamicMappingDefinition,
          oldCoConstraintsTable: $rootScope.segmentsMap[pc.ref.id].coConstraintsTable,
          oldUsage: pc.usage,
          usage: null,
          oldMin: pc.min,
          min: null,
          oldMax: pc.max,
          max: null,
          oldComment: pc.comment,
          comment: null,
          oldConformanceStatements: $rootScope.segmentsMap[pc.ref.id].conformanceStatements,
          conformanceStatements: null,
        },
        appliedTo: [],
        version: ""
      };
    } else if (pc.type === 'group') {
      var newPc = {
        id: new ObjectId().toString(),
        name: pc.name,
        type: pc.type,
        path: pc.parent + '.' + pc.position,
        itemId: pc.id,
        source: pc.source,
        from: "message",
        attributes: {
          oldUsage: pc.usage,
          usage: null,
          oldMin: pc.min,
          min: null,
          oldMax: pc.max,
          max: null,
          oldComment: pc.comment,
          comment: null,
          oldConformanceStatements: pc.conformanceStatements,
          conformanceStatements: null,
        },
        appliedTo: [],
        version: ""
      };
    } else if (pc.type === 'field') {
      if (parent.type === 'segment') {
        var newPc = {
          id: new ObjectId().toString(),
          name: pc.name,
          type: pc.type,
          path: parent.label + '.' + pc.position,
          pathExp: parent.label + '.' + pc.position,
          itemId: pc.id,
          parentValueSetBindings: pc.parentValueSetBindings,
          parentComments: pc.parentComments,
          parentSingleElementValues: pc.parentSingleElementValues,
          source: pc.source,
          from: "segment",
          attributes: {
            oldDatatype: pc.datatype,
            oldTables: pc.tables,
            oldUsage: pc.usage,
            usage: null,
            oldMin: pc.min,
            min: null,
            oldMax: pc.max,
            max: null,
            oldMinLength: pc.minLength,
            minLength: null,
            oldMaxLength: pc.maxLength,
            maxLength: null,
            oldConfLength: pc.confLength,
            confLength: null,
            oldComment: pc.comment,
            comment: null,
            text: pc.text
          },
          appliedTo: [],
          version: ""
        };
      } else if (parent.type === 'segmentRef') {
        var newPc = {
          id: new ObjectId().toString(),
          name: pc.name,
          type: pc.type,
          path: parent.parent + '.' + parent.position + '.' + pc.position,
          itemId: pc.id,
          from: "message",
          parentValueSetBindings: pc.parentValueSetBindings,
          parentComments: pc.parentComments,
          parentSingleElementValues: pc.parentSingleElementValues,
          source: pc.source,
          attributes: {
            oldDatatype: pc.datatype,
            oldTables: pc.tables,
            oldUsage: pc.usage,
            usage: null,
            oldMin: pc.min,
            min: null,
            oldMax: pc.max,
            max: null,
            oldMinLength: pc.minLength,
            minLength: null,
            oldMaxLength: pc.maxLength,
            maxLength: null,
            oldConfLength: pc.confLength,
            confLength: null,
            oldComment: pc.comment,
            comment: null,
            text: pc.text
          },
          appliedTo: [],
          version: ""
        };
      }
    } else if (pc.type === 'component') {
      var newPc = {
        id: new ObjectId().toString(),
        name: pc.name,
        type: pc.type,
        path: parent.parent + '.' + parent.position + '.' + pc.position,
        parentValueSetBindings: pc.parentValueSetBindings,
        parentSingleElementValues: pc.parentSingleElementValues,
        parentComments: pc.parentComments,
        source: pc.source,
        itemId: pc.id,
        from: parent.from,
        attributes: {
          oldDatatype: pc.datatype,
          oldTables: pc.tables,
          oldUsage: pc.usage,
          usage: null,
          oldMin: pc.min,
          min: null,
          oldMax: pc.max,
          max: null,
          oldMinLength: pc.minLength,
          minLength: null,
          oldMaxLength: pc.maxLength,
          maxLength: null,
          oldConfLength: pc.confLength,
          confLength: null,
          oldComment: pc.comment,
          comment: null,
          text: pc.text
        },
        appliedTo: [],
        version: ""
      };



    } else if (pc.type === 'segment') {
      var newPc = {
        id: new ObjectId().toString(),
        name: $rootScope.segmentsMap[pc.id].name,
        ext: $rootScope.segmentsMap[pc.id].ext,
        type: "segmentRef",
        path: $rootScope.segmentsMap[pc.id].label,
        pathExp: $rootScope.segmentsMap[pc.id].label,
        oldValueSetBindings: pc.valueSetBindings,
        valueSetBindings:[],
        source: pc.source,
        from: "segment",
        itemId: pc.id,
        attributes: {
          ref: {
            id: $rootScope.segmentsMap[pc.id].id,
            name: $rootScope.segmentsMap[pc.id].name,
            ext: $rootScope.segmentsMap[pc.id].ext,
            label: $rootScope.segmentsMap[pc.id].label,

          },
          oldRef: {
            id: $rootScope.segmentsMap[pc.id].id,
            name: $rootScope.segmentsMap[pc.id].name,
            ext: $rootScope.segmentsMap[pc.id].ext,
            label: $rootScope.segmentsMap[pc.id].label,

          },
          oldDynamicMappingDefinition: pc.dynamicMappingDefinition,
          oldConformanceStatements: pc.conformanceStatements,
          conformanceStatements: null,
          oldCoConstraintsTable: pc.coConstraintsTable,
        },
        appliedTo: [],
        version: ""
      };
      if(!newPc.source) newPc.source = {};
      newPc.source.segmentId = pc.id;
    } else if (pc.type === 'datatype') {
      var newPc = {
        id: new ObjectId().toString(),
        name: $rootScope.datatypesMap[pc.id].label,
        ext: $rootScope.datatypesMap[pc.id].ext,
        type: pc.type,
        path: $rootScope.datatypesMap[pc.id].label,
        itemId: pc.id,
        attributes: {},
        appliedTo: [],
        version: ""
      };
    };
    if (newPc.type !== "segmentRef") {
      newPc.oldValueSetBindings = $scope.findingBindings(newPc);
    }
    if(newPc.oldValueSetBindings&&newPc.oldValueSetBindings.length!==0){
      newPc.valueSetBindings=[];
    }
    newPc.oldComments = $scope.findingComments(newPc);
    newPc.oldPredicate = $rootScope.findPredicateForPC(newPc);
    $scope.selectedPC.push(newPc);
  };
  $scope.SegProfileComponentParams = new ngTreetableParams({
    getNodes: function(parent) {
      if (segments !== undefined) {
        if (parent) {
          if (parent.fields) {
            for (var i = 0; i < parent.fields.length; i++) {
              if (parent.type === "segment") {
                parent.fields[i].parent = parent.label;
                parent.fields[i].parentValueSetBindings = parent.valueSetBindings;
                parent.fields[i].parentSingleElementValues = parent.singleElementValues;
                parent.fields[i].parentComments = parent.comments;
                parent.fields[i].source = {};
                parent.fields[i].source.segmentId = parent.id;
                //parent.fields[i].sourceId = parent.id;

                parent.fields[i].from = "segment";
              }
              if (parent.type === "field" || parent.type === "component") {
                parent.fields[i].parent = parent.parent + '.' + parent.position;
                parent.fields[i].parentValueSetBindings = parent.parentValueSetBindings;
                parent.fields[i].parentSingleElementValues = parent.parentSingleElementValues;
                parent.fields[i].parentComments = parent.parentComments;
                parent.fields[i].source = {};
                parent.fields[i].source.segmentId = parent.source.segmentId;
                if (parent.type === "field") {
                  parent.fields[i].source.fieldDt = parent.datatype.id;
                } else if (parent.type === "component") {
                  parent.fields[i].source.fieldDt = parent.source.fieldDt;
                  parent.fields[i].source.componentDt = parent.datatype.id;
                }
                parent.fields[i].from = "segment";
              }
              if (parent.fields[i].datatype) {
                parent.fields[i].fields = datatypesMap[parent.fields[i].datatype.id].components;
              }
            }
            return parent.fields;
          }

        } else {
          return orderByFilter(segments, 'name');;
        }

      }
    },
    getTemplate: function(node) {
      return 'SegProfileComponentTable';
    }
  });
  $scope.DTProfileComponentParams = new ngTreetableParams({
    getNodes: function(parent) {
      if (datatypes !== undefined) {
        if (parent) {
          if (parent.components) {
            for (var i = 0; i < parent.components.length; i++) {
              if (parent.type === "datatype") {
                parent.components[i].parent = parent.label;
              }
              if (parent.type === "component") {
                parent.components[i].parent = parent.parent + '.' + parent.position;
              }
              if (parent.components[i].datatype) {
                parent.components[i].components = datatypesMap[parent.components[i].datatype.id].components;
              }
            }
            return parent.components;
          }

        } else {
          return datatypes;
        }

      }
    },
    getTemplate: function(node) {
      return 'DTProfileComponentTable';
    }
  });
  $scope.add = function() {
    // PcLibraryService.addComponentsToLib($rootScope.igdocument.id, $scope.selectedPC).then(function(ig) {
    //     $rootScope.igdocument.profile.profileComponentLibrary.children = ig.profile.profileComponentLibrary.children;
    //     if ($scope.profileComponentParams) {
    //         $scope.profileComponentParams.refresh();
    //     }
    //     $modalInstance.close();
    // });
    var position = currentPc.children.length + 1;
    for (var i = 0; i < $scope.selectedPC.length; i++) {
      $scope.selectedPC[i].position = position;
      position++;
      $rootScope.profileComponent.children.push($scope.selectedPC[i]);
    }
    console.log($rootScope.profileComponent);
    $mdDialog.hide();

    // PcService.addPCs(currentPc.id, $scope.selectedPC).then(function(profileC) {
    //     $rootScope.profileComponent = profileC;
    //
    // });
  };
  $scope.cancel = function() {
    $mdDialog.hide();
  };
});
