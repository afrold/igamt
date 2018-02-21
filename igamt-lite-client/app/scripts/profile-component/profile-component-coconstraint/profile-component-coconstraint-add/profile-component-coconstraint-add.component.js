/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddCoConstraintCtrlInPc', function($scope, $mdDialog, node, context, $rootScope, TableService, SegmentService) {

    $scope.Cchanged=false;
    $scope.coconstrsaintChanged=function () {
     $scope.Cchanged=true;
    };
    $scope.isCocontraintChanged=function () {
        return Cchanged;
    };
  

  $scope.backToCoConTable = function(){
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };


    $scope.initRowIndexForCocon = function(){
        $scope.coConRowIndexList = [];

        for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
            var rowIndexObj = {};
            rowIndexObj.rowIndex = i;
            rowIndexObj.id = new ObjectId().toString();
            $scope.coConRowIndexList.push(rowIndexObj);
        }
    };


  $scope.initCoConstraintsTable = function() {
    $scope.coConTable = true;
      $scope.Cchanged=false;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
    if($scope.seg) {
        if ($scope.seg.name === 'OBX') {
            if (!$scope.coConstraintsTable.ifColumnDefinition || !$scope.coConstraintsTable.thenColumnDefinitionList || $scope.coConstraintsTable.thenColumnDefinitionList.length === 0) {
                var field2 = null;
                var field3 = null;
                var field5 = null;

                angular.forEach(segment.fields, function (field) {
                    if (field.position === 2) {
                        field2 = field;
                    } else if (field.position === 3) {
                        field3 = field;
                    } else if (field.position === 5) {
                        field5 = field;
                    }
                });

                var ifColumnDefinition = {
                    id: new ObjectId().toString(),
                    path: "3",
                    constraintPath: "3[1]",
                    type: "field",
                    constraintType: "value",
                    name: field3.name,
                    usage: field3.usage,
                    dtId: field3.datatype.id,
                    primitive: false,
                    dMReference: false
                };

                var field2ColumnDefinition = {
                    id: new ObjectId().toString(),
                    path: "2",
                    constraintPath: "2[1]",
                    type: "field",
                    constraintType: "dmr",
                    name: field2.name,
                    usage: field2.usage,
                    dtId: field2.datatype.id,
                    primitive: true,
                    dMReference: true
                };

                var field5ColumnDefinition = {
                    id: new ObjectId().toString(),
                    path: "5",
                    constraintPath: "5[1]",
                    type: "field",
                    constraintType: "valueset",
                    name: field5.name,
                    usage: field5.usage,
                    dtId: field5.datatype.id,
                    primitive: true,
                    dMReference: false
                };
                var thenColumnDefinitionList = [];
                thenColumnDefinitionList.push(field2ColumnDefinition);
                thenColumnDefinitionList.push(field5ColumnDefinition);

                var userColumnDefinitionList = [];
                var userColumnDefinition = {
                    id: new ObjectId().toString(),
                    title: "Comments"
                };
                userColumnDefinitionList.push(userColumnDefinition);

                $scope.coConstraintsTable.ifColumnDefinition = ifColumnDefinition;
                $scope.coConstraintsTable.thenColumnDefinitionList = thenColumnDefinitionList;
                $scope.coConstraintsTable.userColumnDefinitionList = userColumnDefinitionList;

                if (!$scope.coConstraintsTable.ifColumnData) $scope.coConstraintsTable.ifColumnData = [];
                if (!$scope.coConstraintsTable.thenMapData) $scope.coConstraintsTable.thenMapData = {};
                if (!$scope.coConstraintsTable.userMapData) $scope.coConstraintsTable.userMapData = {};
                if (!$scope.coConstraintsTable.rowSize) $scope.coConstraintsTable.rowSize = 0;
            }
            if ($scope.coConstraintsTable.thenColumnDefinitionList) {

                $scope.coConstraintsTable.thenColumnDefinitionListForDisplay = [];
                for (var i in $scope.coConstraintsTable.thenColumnDefinitionList) {
                    var def = $scope.coConstraintsTable.thenColumnDefinitionList[i];

                    if (def.constraintType === 'dmr') {
                        $scope.coConstraintsTable.thenColumnDefinitionListForDisplay.push(def);
                        var clone = angular.copy(def);
                        clone.constraintType = 'dmf';
                        $scope.coConstraintsTable.thenColumnDefinitionListForDisplay.push(clone);
                    } else {
                        $scope.coConstraintsTable.thenColumnDefinitionListForDisplay.push(def);
                    }
                }
            }
            $scope.initRowIndexForCocon();
        }
    }
  };
  $scope.node = angular.copy(node);
  $scope.seg = angular.copy($rootScope.segmentsMap[node.attributes.ref.id]);
  $scope.changed = false;

  $scope.findCoConstraints = function() {
    if ($scope.node.type === "segmentRef") {
      if ($scope.node.attributes.coConstraintsTable && $scope.node.attributes.coConstraintsTable.rowSize > 0) {
        $scope.coConstraintsTable = $scope.node.attributes.coConstraintsTable;
      } else {
        $scope.coConstraintsTable = $scope.node.attributes.oldCoConstraintsTable;
      }
    }

  };
  $scope.findCoConstraints();
  $scope.initCoConstraintsTable();
  $scope.initRowIndexForCocon();

  $scope.saveIF = function() {
    var ifColumnDefinition = {};
    ifColumnDefinition.id = new ObjectId().toString();
    ifColumnDefinition.constraintType = $scope.coConstraintType;
    ifColumnDefinition.name = $scope.targetNode.name;
    ifColumnDefinition.usage = $scope.targetNode.usage;
    ifColumnDefinition.dtId = $scope.targetNode.datatype.id;
    ifColumnDefinition.primitive = $scope.primitive;

    if ($scope.selectedFieldPosition) {
      ifColumnDefinition.path = "" + $scope.selectedFieldPosition;
      ifColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
      ifColumnDefinition.type = "field";
      if ($scope.selectedComponentPosition) {
        ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedComponentPosition;
        ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
        ifColumnDefinition.type = "component";
        if ($scope.selectedSubComponentPosition) {
          ifColumnDefinition.path = ifColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
          ifColumnDefinition.constraintPath = ifColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
          ifColumnDefinition.type = "subcomponent";
        }
      }
    }
    if (ifColumnDefinition) {
      if (!$scope.coConstraintsTable) {
        $scope.coConstraintsTable = {};
        $scope.coConstraintsTable.rowSize = 0;
      }

      if (!$scope.coConstraintsTable.ifColumnDefinition) {
        $scope.coConstraintsTable.ifColumnData = [];
        for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
          $scope.coConstraintsTable.ifColumnData.push({});
        }
      }

      $scope.coConstraintsTable.ifColumnDefinition = ifColumnDefinition;
    }
  $scope.coconstrsaintChanged();
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };
  $scope.updateFieldIF = function() {
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;

    var field = _.find($scope.seg.fields, function(f) {
      return f.position == $scope.selectedFieldPosition;
    });

    $scope.targetNode = field;

    if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
      $scope.primitive = false;
      $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

    }
  };
  $scope.updateComponentIF = function() {
    $scope.selectedSubComponentPosition = null;
    $scope.subComponents = null;
    $scope.primitive = true;

    var component = _.find($scope.components, function(c) {
      return c.position == $scope.selectedComponentPosition;
    });

    $scope.targetNode = component;
    if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
      $scope.primitive = false;
      $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
    }
  };
  $scope.updateSubComponentIF = function() {
    $scope.primitive = true;
    var subComponent = _.find($scope.subComponents, function(sc) {
      return sc.position == $scope.selectedSubComponentPosition;
    });
    $scope.targetNode = subComponent;
  };
  $scope.coConstraintIFDefinition = function() {
    $scope.coConTable = false;
    $scope.ifAddCoCon = true;
    $scope.thenAddCoCon = false;
    $scope.userAddCoCon = false;
    $scope.thenData = false;
    $scope.userData = false;
    $scope.selectedCoConstraintIFDefinition = angular.copy($scope.coConstraintsTable.ifColumnDefinition);


    if ($scope.selectedCoConstraintIFDefinition) {
      $scope.primitive = $scope.selectedCoConstraintIFDefinition.primitive;
      $scope.coConstraintType = $scope.selectedCoConstraintIFDefinition.constraintType;
      var splitLocation = $scope.selectedCoConstraintIFDefinition.path.split('.');
      if (splitLocation.length > 0) {
        $scope.selectedFieldPosition = splitLocation[0];

        var field = _.find($scope.seg.fields, function(f) {
          return f.position == splitLocation[0];
        });

        $scope.targetNode = field;

        if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
          $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

          if (splitLocation.length > 1 && $scope.components) {
            $scope.selectedComponentPosition = splitLocation[1];
            var component = _.find($scope.components, function(c) {
              return c.position == splitLocation[1];
            });
            $scope.targetNode = component;
            if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
              $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

              if (splitLocation.length > 2 && $scope.subComponents) {
                $scope.selectedSubComponentPosition = splitLocation[2];
                var subComponent = _.find($scope.subComponents, function(sc) {
                  return sc.position == splitLocation[2];
                });
                $scope.targetNode = subComponent;
              }
            }
          }
        }
      }
    };
  };
  $scope.coConstraintTHENDefinition = function(columnDefinition) {
    $scope.coConTable = false;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = true;
    $scope.userAddCoCon = false;
    $scope.thenData = false;
    $scope.userData = false;

    $scope.selectedCoConstraintTHENDefinition = angular.copy(columnDefinition);
    console.log($scope.selectedCoConstraintTHENDefinition);

    $scope.coConstraintType = 'value';
    $scope.selectedFieldPosition = null;
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    $scope.targetNode = null;


    if ($scope.selectedCoConstraintTHENDefinition) {
      $scope.primitive = $scope.selectedCoConstraintTHENDefinition.primitive;
      $scope.coConstraintType = $scope.selectedCoConstraintTHENDefinition.constraintType;
      $scope.dMReference = $scope.selectedCoConstraintTHENDefinition.dMReference;
      var splitLocation = $scope.selectedCoConstraintTHENDefinition.path.split('.');
      if (splitLocation.length > 0) {
        $scope.selectedFieldPosition = splitLocation[0];

        var field = _.find($scope.seg.fields, function(f) {
          return f.position == splitLocation[0];
        });

        $scope.targetNode = field;

        if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
          $scope.components = $rootScope.datatypesMap[field.datatype.id].components;

          if (splitLocation.length > 1 && $scope.components) {
            $scope.selectedComponentPosition = splitLocation[1];
            var component = _.find($scope.components, function(c) {
              return c.position == splitLocation[1];
            });
            $scope.targetNode = component;
            if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
              $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;

              if (splitLocation.length > 2 && $scope.subComponents) {
                $scope.selectedSubComponentPosition = splitLocation[2];
                var subComponent = _.find($scope.subComponents, function(sc) {
                  return sc.position == splitLocation[2];
                });
                $scope.targetNode = subComponent;
              }
            }
          }
        }
      }
    };
  };
  $scope.coConstraintUSERDefinitionEdit = function(coConstraintUSERDefinition) {
    $scope.coConTable = false;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = true;
    $scope.userData = false;
    $scope.coConstraintUSERDefinition = angular.copy(coConstraintUSERDefinition);
    $scope.title = null;

    if ($scope.coConstraintUSERDefinition) {
      $scope.title = $scope.coConstraintUSERDefinition.title;
    }




  };
  $scope.isDual = function (def){
    if(def) {
      if($scope.seg.name === 'OBX' && def.path + "" === "5") return true;
    }
    return false;
  };

  $scope.editValueSetForVaries = function (id, index){
    $scope.coConstraintsTable.thenMapData[id][index].valueData = {};
    $scope.editValueSetThenMapData(id,index);
  };

  $scope.editValueForVaries = function (id, index){
    $scope.coConstraintsTable.thenMapData[id][index].valueData = {};
    $scope.coConstraintsTable.thenMapData[id][index].valueData.value = "Edit Value";
    $scope.coConstraintsTable.thenMapData[id][index].valueSets = [];
  };


  $scope.editValueSetThenMapData = function(currentId, currentIndex) {
    $scope.currentId = currentId;
    $scope.currentIndex = currentIndex;
    $scope.coConTable = false;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = true;
    $scope.userAddCoCon = false;
    $scope.userData = false;
    $scope.data = angular.copy($scope.coConstraintsTable.thenMapData[currentId][currentIndex]);
    $scope.listOfBindingLocations = null;
    $scope.columnDefinition = _.find($scope.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
      return columnDefinition.id == currentId;
    });

    if ($scope.columnDefinition) {
      var dtId = $scope.columnDefinition.dtId;

      if ($rootScope.datatypesMap[dtId].name.toLowerCase() == 'varies') {
        var referenceColumnDefinition = _.find($scope.coConstraintsTable.thenColumnDefinitionList, function(columnDefinition) {
          return columnDefinition.dMReference;
        });

        if (referenceColumnDefinition) {
          dtId = $scope.coConstraintsTable.thenMapData[referenceColumnDefinition.id][currentIndex].datatypeId;
        }

        $scope.listOfBindingLocations = $scope.findOptionsVS(dtId);
      } else {
        if (!$scope.columnDefinition.primitive) {
          $scope.listOfBindingLocations = $scope.findOptionsVS(dtId);
        } else {
          $scope.listOfBindingLocations = null;
        }
      }
    } else {
      $scope.listOfBindingLocations = null;
    }

  };
  $scope.findOptionsVS = function(dtId) {
    var result = [];
    result.push('1');


    if (!dtId) return result;

    if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
        return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
      })) {
      var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

      var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

      if (bls && bls.length > 0) return bls;
    }

    return result;
  };
  $scope.isSelected = function(v) {
    if ($scope.data && $scope.data.valueSets) {
      for (var i = 0; i < $scope.data.valueSets.length; i++) {
        if ($scope.data.valueSets[i].tableId == v.id) return true;
      }
    }
    return false;
  };
  $scope.toggleVs=function (v) {
    if(!$scope.isSelected(v)){
      $scope.selectValueSet(v);
    }else{
      $scope.unselectValueSet(v);
    }
  };


  $scope.selectValueSet = function(v) {
    if (!$scope.data) $scope.data = {};
    if (!$scope.data.valueSets) $scope.data.valueSets = [];
    $scope.data.valueSets.push({ tableId: v.id, bindingStrength: "R" });
  };
  $scope.deleteValueSet = function(index) {
    if (index >= 0) {
      $scope.data.valueSets.splice(index, 1);
    }
  };
  $scope.unselectValueSet = function(v) {
    var toBeDelBinding = _.find($scope.data.valueSets, function(binding) {
      return binding.tableId == v.id;
    });
    var index = $scope.data.valueSets.indexOf(toBeDelBinding);
    if (index >= 0) {
      $scope.data.valueSets.splice(index, 1);
    }
  };
  $scope.saveValueSet = function() {
    if ($scope.data) {
      $scope.coConstraintsTable.thenMapData[$scope.currentId][$scope.currentIndex] = angular.copy($scope.data);
      $scope.coconstrsaintChanged();

    }
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };
  $scope.saveUSERData = function() {
    if ($scope.data) {
      $scope.coConstraintsTable.userMapData[$scope.currentId][$scope.currentIndex] = angular.copy($scope.data);
      $scope.coconstrsaintChanged();
    }
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };
  $scope.saveUSER = function() {
    console.log($scope.coConstraintUSERDefinition);
    if ($scope.coConstraintUSERDefinition) {
      for (i in $scope.coConstraintsTable.userColumnDefinitionList) {
        if ($scope.coConstraintsTable.userColumnDefinitionList[i].id === $scope.coConstraintUSERDefinition.id) {
          $scope.coConstraintsTable.userColumnDefinitionList[i].title = angular.copy($scope.title);
        }
      }
    } else {
      var userColumnDefinition = {};
      userColumnDefinition.title = $scope.title;
      if (userColumnDefinition) {
        userColumnDefinition.title = $scope.title;

        if (!$scope.coConstraintsTable) {
          $scope.coConstraintsTable = {};
          $scope.coConstraintsTable.rowSize = 0;
        }

        if (!$scope.coConstraintsTable.userColumnDefinitionList) {
          $scope.coConstraintsTable.userColumnDefinitionList = [];
          $scope.coConstraintsTable.userMapData = {};
        }

        if (!userColumnDefinition.id) {
          userColumnDefinition.id = new ObjectId().toString();
          $scope.coConstraintsTable.userColumnDefinitionList.push(userColumnDefinition);

          $scope.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

          for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
            $scope.coConstraintsTable.userMapData[userColumnDefinition.id].push({});
          }
        } else {

          for (var i in $scope.coConstraintsTable.userColumnDefinitionList) {
            if ($scope.coConstraintsTable.userColumnDefinitionList[i].id == userColumnDefinition.id) {
              $scope.coConstraintsTable.userColumnDefinitionList[i] = userColumnDefinition;
            }
          }
        }
      }
    }
    $scope.coconstrsaintChanged();
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };
  $scope.isVariesDT = function() {
    if ($scope.targetNode) {
      if ($rootScope.datatypesMap[$scope.targetNode.datatype.id].name.toLowerCase() == 'varies') {
        $scope.coConstraintType = 'valueset';
        return true;
      }
    }

    return false;
  };
  $scope.updateFieldTHEN = function() {
    $scope.selectedComponentPosition = null;
    $scope.selectedSubComponentPosition = null;
    $scope.components = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    var field = _.find($scope.seg.fields, function(f) {
      return f.position == $scope.selectedFieldPosition;
    });

    $scope.targetNode = field;

    if ($scope.seg.name === "OBX") {
      console.log("=========This is DM segment!!=========");
      var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
        return item.hl7Version == $scope.seg.hl7Version && item.segmentName == $scope.seg.name;
      });

      if (mappingStructure) {
        if ($scope.seg.dynamicMappingDefinition && $scope.seg.dynamicMappingDefinition.mappingStructure) {
          console.log("=========Found mapping structure!!=========");
          mappingStructure = $scope.seg.dynamicMappingDefinition.mappingStructure;
        } else {
          console.log("=========Not Found mapping structure and Default setting will be used!!=========");
        }

        var valueSetBinding = _.find($scope.seg.valueSetBindings, function(vsb) {
          return vsb.location == mappingStructure.referenceLocation;
        });

        if (valueSetBinding) {
          TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
            $rootScope.dynamicMappingTable = tbl;
          }, function() {

          });
        }

        if ($scope.selectedFieldPosition == mappingStructure.referenceLocation) {
          $scope.dMReference = true;
        }
      }
    }

    if (field && $rootScope.datatypesMap[field.datatype.id].components.length > 0) {
      $scope.primitive = false;
      $scope.components = $rootScope.datatypesMap[field.datatype.id].components;
    }
  };
  $scope.updateComponentTHEN = function() {
    $scope.selectedSubComponentPosition = null;
    $scope.subComponents = null;
    $scope.primitive = true;
    $scope.dMReference = false;

    var component = _.find($scope.components, function(c) {
      return c.position == $scope.selectedComponentPosition;
    });

    $scope.targetNode = component;
    if (component && $rootScope.datatypesMap[component.datatype.id].components.length > 0) {
      $scope.primitive = false;
      $scope.subComponents = $rootScope.datatypesMap[component.datatype.id].components;
    }
  };
  $scope.updateSubComponentTHEN = function() {
    $scope.primitive = true;
    $scope.dMReference = false;
    var subComponent = _.find($scope.subComponents, function(sc) {
      return sc.position == $scope.selectedSubComponentPosition;
    });
    $scope.targetNode = subComponent;
  };
  $scope.saveTHEN = function() {
    var thenColumnDefinition = {};
    if ($scope.selectedCoConstraintTHENDefinition) {
      thenColumnDefinition.id = $scope.selectedCoConstraintTHENDefinition.id;
    }

    thenColumnDefinition.constraintType = $scope.coConstraintType;
    thenColumnDefinition.name = $scope.targetNode.name;
    thenColumnDefinition.usage = $scope.targetNode.usage;
    thenColumnDefinition.dtId = $scope.targetNode.datatype.id;
    thenColumnDefinition.primitive = $scope.primitive;
    thenColumnDefinition.dMReference = $scope.dMReference;

    if (thenColumnDefinition.dMReference) {
      thenColumnDefinition.constraintType = 'dmr';
    }

    if ($scope.selectedFieldPosition) {
      thenColumnDefinition.path = "" + $scope.selectedFieldPosition;
      thenColumnDefinition.constraintPath = "" + $scope.selectedFieldPosition + "[1]";
      thenColumnDefinition.type = "field";
      if ($scope.selectedComponentPosition) {
        thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedComponentPosition;
        thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedComponentPosition + "[1]";
        thenColumnDefinition.type = "component";
        if ($scope.selectedSubComponentPosition) {
          thenColumnDefinition.path = thenColumnDefinition.path + "." + $scope.selectedSubComponentPosition;
          thenColumnDefinition.constraintPath = thenColumnDefinition.constraintPath + "." + $scope.selectedSubComponentPosition + "[1]";
          thenColumnDefinition.type = "subcomponent";
        }
      }
    }
    if (thenColumnDefinition) {
      if (!$scope.coConstraintsTable) {
        $scope.coConstraintsTable = {};
        $scope.coConstraintsTable.rowSize = 0;
      }

      if (!$scope.coConstraintsTable.thenColumnDefinitionList) {
        $scope.coConstraintsTable.thenColumnDefinitionList = [];
        $scope.coConstraintsTable.thenMapData = {};
      }

      if (!thenColumnDefinition.id) {
        thenColumnDefinition.id = new ObjectId().toString();
        $scope.coConstraintsTable.thenColumnDefinitionList.push(thenColumnDefinition);
        $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

        for (var i = 0, len1 = $scope.coConstraintsTable.rowSize; i < len1; i++) {
          $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id].push({});
        }
      } else {
        for (var i in $scope.coConstraintsTable.thenColumnDefinitionList) {
          if ($scope.coConstraintsTable.thenColumnDefinitionList[i].id == thenColumnDefinition.id) {
            $scope.coConstraintsTable.thenColumnDefinitionList[i] = thenColumnDefinition;
          }
        }
      }
    }
    $scope.initCoConstraintsTable();
    $scope.coconstrsaintChanged();
    $scope.coConTable = true;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.thenData = false;
    $scope.userAddCoCon = false;
    $scope.userData = false;
  };
  $scope.delCoConstraintIFDefinition = function(ifColumnDefinition) {
    $scope.coConstraintsTable.ifColumnDefinition = null;
    $scope.coConstraintsTable.ifColumnData = [];

    $scope.resetCoConstraintsTable();
    $scope.coconstrsaintChanged();
  };
  $scope.delCoConstraintTHENDefinition = function(columnDefinition) {
    var index = $scope.coConstraintsTable.thenColumnDefinitionList.indexOf(columnDefinition);

    if (index > -1) {
      $scope.coConstraintsTable.thenMapData[columnDefinition.id] = null;
      $scope.coConstraintsTable.thenColumnDefinitionList.splice(index, 1);
    };

    $scope.resetCoConstraintsTable();
    $scope.coconstrsaintChanged();
  };
  $scope.delCoConstraintUSERDefinition = function(columnDefinition) {
    console.log(columnDefinition);
    var index = $scope.coConstraintsTable.userColumnDefinitionList.indexOf(columnDefinition);
    console.log(index);
    if (index > -1) {
      $scope.coConstraintsTable.userMapData[columnDefinition.id] = null;
      $scope.coConstraintsTable.userColumnDefinitionList.splice(index, 1);
    };

    $scope.resetCoConstraintsTable();
    $scope.coconstrsaintChanged();
  };
  $scope.resetCoConstraintsTable = function() {
    if (!$scope.coConstraintsTable.ifColumnDefinition) {
      if (!$scope.coConstraintsTable.thenColumnDefinitionList || $scope.coConstraintsTable.thenColumnDefinitionList.length == 0) {
        if (!$scope.coConstraintsTable.userColumnDefinitionList || $scope.coConstraintsTable.userColumnDefinitionList.length == 0) {
          $scope.coConstraintsTable = {};
        }
      }
    }

    $scope.initCoConstraintsTable();
    $scope.initRowIndexForCocon();
  };

  $scope.findOptions = function(dtId) {
    var result = [];
    result.push('1');


    if(!dtId || !$rootScope.datatypesMap[dtId]) return result;

    if (_.find($rootScope.config.codedElementDTs, function(valueSetAllowedDT) {
        return valueSetAllowedDT == $rootScope.datatypesMap[dtId].name;
      })) {
      var hl7Version = $rootScope.datatypesMap[dtId].hl7Version;

      var bls = $rootScope.config.bindingLocationListByHL7Version[hl7Version];

      if (bls && bls.length > 0) return bls;
    }

    return result;
  };
  $scope.updateDynamicMappingInfo = function() {
    $scope.isDynamicMappingSegment = false;
    $scope.dynamicMappingTable = null;

    var mappingStructure = _.find($rootScope.config.variesMapItems, function(item) {
      return item.hl7Version == $scope.seg.hl7Version && item.segmentName == $scope.seg.name;
    });

    if (mappingStructure) {
      $scope.isDynamicMappingSegment = true;
      console.log("=========This is DM segment!!=========");

      if ($scope.seg.dynamicMappingDefinition && $scope.seg.dynamicMappingDefinition.mappingStructure) {
        console.log("=========Found mapping structure!!=========");
        mappingStructure = $scope.seg.dynamicMappingDefinition.mappingStructure;
      } else {
        console.log("=========Not Found mapping structure and Default setting will be used!!=========");
      }

      var valueSetBinding = _.find($scope.seg.valueSetBindings, function(vsb) {
        return vsb.location == mappingStructure.referenceLocation;
      });

      if (valueSetBinding) {
        TableService.getOne(valueSetBinding.tableId).then(function(tbl) {
          $scope.dynamicMappingTable = tbl;
        }, function() {

        });
      }
    }
  };
  $scope.updateDynamicMappingInfo();
  $scope.addCoConstraintRow = function() {
    var isAdded = false;
    if(!$scope.coConstraintsTable.ifColumnData) $scope.coConstraintsTable.ifColumnData = [];
    if(!$scope.coConstraintsTable.thenMapData) $scope.coConstraintsTable.thenMapData = {};
    if(!$scope.coConstraintsTable.userMapData) $scope.coConstraintsTable.userMapData = {};

    if($scope.coConstraintsTable.ifColumnDefinition){
      var newIFData = {};
      newIFData.valueData = {};
      newIFData.bindingLocation = null;
      newIFData.isNew = true;

      $scope.coConstraintsTable.ifColumnData.unshift(newIFData);
      isAdded = true;
    }

    if($scope.coConstraintsTable.thenColumnDefinitionList){
      for (var i = 0, len1 = $scope.coConstraintsTable.thenColumnDefinitionList.length; i < len1; i++) {
        var thenColumnDefinition = $scope.coConstraintsTable.thenColumnDefinitionList[i];

        var newTHENData = {};
        newTHENData.valueData = {};
        newTHENData.valueSets = [];
        newTHENData.isNew = true;

        if(!$scope.coConstraintsTable.thenMapData[thenColumnDefinition.id]) $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id] = [];

        $scope.coConstraintsTable.thenMapData[thenColumnDefinition.id].unshift(newTHENData);
        isAdded = true;
      };
    }

    if($scope.coConstraintsTable.userColumnDefinitionList){
      for (var i = 0, len1 = $scope.coConstraintsTable.userColumnDefinitionList.length; i < len1; i++) {
        var userColumnDefinition = $scope.coConstraintsTable.userColumnDefinitionList[i];

        var newUSERData = {};
        newUSERData.text = "";
        newUSERData.isNew = true;

        if(!$scope.coConstraintsTable.userMapData[userColumnDefinition.id]) $scope.coConstraintsTable.userMapData[userColumnDefinition.id] = [];

        $scope.coConstraintsTable.userMapData[userColumnDefinition.id].unshift(newUSERData);
        isAdded = true;
      };
    }

    if(isAdded) {
        $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize + 1;
        $scope.initRowIndexForCocon();
        $scope.coconstrsaintChanged();

      // $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize + 1;
      // $scope.initRowIndexForCocon();
      // $rootScope.recordChanged ();
    }
  };
  $scope.cloneCoConstraintRow = function (rowIndex){
    if($scope.coConstraintsTable.ifColumnDefinition){
      if($scope.coConstraintsTable.ifColumnData){
        var copy = angular.copy($scope.coConstraintsTable.ifColumnData[rowIndex]);
        copy.isNew = true;
        $scope.coConstraintsTable.ifColumnData.splice(rowIndex + 1, 0, copy);
      }
    }

    if($scope.coConstraintsTable.thenColumnDefinitionList && $scope.coConstraintsTable.thenColumnDefinitionList.length > 0){
      if($scope.coConstraintsTable.thenMapData){
        for(var i in $scope.coConstraintsTable.thenColumnDefinitionList){
          if($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id]){
            var copy = angular.copy($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id][rowIndex]);
            copy.isNew = true;
            $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id].splice(rowIndex + 1, 0, copy);
          }
        }
      }
    }

    if($scope.coConstraintsTable.userColumnDefinitionList && $scope.coConstraintsTable.userColumnDefinitionList.length > 0){
      if($scope.coConstraintsTable.userMapData){
        for(var i in $scope.coConstraintsTable.userColumnDefinitionList){
          if($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id]){
            var copy = angular.copy($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id][rowIndex]);
            copy.isNew = true;
            $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id].splice(rowIndex + 1, 0, copy);
          }
        }
      }
    }

    $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize + 1;
    $scope.initRowIndexForCocon();
    $scope.coconstrsaintChanged()
  };
  $scope.delCoConstraintRow = function (rowIndex){
    if($scope.coConstraintsTable.ifColumnDefinition){
      $scope.coConstraintsTable.ifColumnData.splice(rowIndex, 1);
    }

    if($scope.coConstraintsTable.thenColumnDefinitionList && $scope.coConstraintsTable.thenColumnDefinitionList.length > 0){
      if($scope.coConstraintsTable.thenMapData){
        for(var i in $scope.coConstraintsTable.thenColumnDefinitionList){
          if($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id]){
            $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id].splice(rowIndex, 1);
          }
        }
      }
    }

    if($scope.coConstraintsTable.userColumnDefinitionList && $scope.coConstraintsTable.userColumnDefinitionList.length > 0){
      if($scope.coConstraintsTable.userMapData){
        for(var i in $scope.coConstraintsTable.userColumnDefinitionList){
          if($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id]){
            $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id].splice(rowIndex, 1);
          }
        }
      }
    }

    $scope.coConstraintsTable.rowSize = $scope.coConstraintsTable.rowSize - 1;
    $scope.initRowIndexForCocon();
    $scope.coconstrsaintChanged();
  };
  $scope.cancel = function() {
      $scope.Cchanged=false;

      $mdDialog.hide();
  }
  $scope.saveclose = function() {
      $scope.Cchanged=false;

      $mdDialog.hide($scope.coConstraintsTable);

  }
  $scope.deleteVS = function (item, array){
    var index = array.indexOf(item);
    if (index >= 0) {
      array.splice(index, 1);
      $scope.coconstrsaintChanged();
    }
  };
  $scope.coConSortableOption = {
    update: function(e, ui) {
    },
    stop: function(e, ui) {
      var newIfColumnData = [];


      for(var i=0, len1=$scope.coConRowIndexList.length; i < len1; i++){
        var rowIndex = $scope.coConRowIndexList[i].rowIndex;
        newIfColumnData.push($scope.coConstraintsTable.ifColumnData[rowIndex]);
      }
      $scope.coConstraintsTable.ifColumnData = newIfColumnData;


      for(var i in $scope.coConstraintsTable.thenColumnDefinitionList) {
        if ($scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id]) {
          var oldThenMapData = $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id];
          var newThenMapData = [];

          for(var j=0, len1=$scope.coConRowIndexList.length; j < len1; j++){
            var rowIndex = $scope.coConRowIndexList[j].rowIndex;
            newThenMapData.push(oldThenMapData[rowIndex]);
          }
          $scope.coConstraintsTable.thenMapData[$scope.coConstraintsTable.thenColumnDefinitionList[i].id] = newThenMapData;
        }
      }

      for(var i in $scope.coConstraintsTable.userColumnDefinitionList) {
        if ($scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id]) {
          var oldUserMapData = $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id];
          var newUserMapData = [];

          for(var j=0, len1=$scope.coConRowIndexList.length; j < len1; j++){
            var rowIndex = $scope.coConRowIndexList[j].rowIndex;
            newUserMapData.push(oldUserMapData[rowIndex]);
          }
          $scope.coConstraintsTable.userMapData[$scope.coConstraintsTable.userColumnDefinitionList[i].id] = newUserMapData;
        }
      }
      $scope.initRowIndexForCocon();
      $scope.coconstrsaintChanged();
    }
  };
  $scope.editUserData = function (currentId, currentIndex) {
    $scope.currentId = currentId;
    $scope.currentIndex = currentIndex;
    $scope.coConTable = false;
    $scope.ifAddCoCon = false;
    $scope.thenAddCoCon = false;
    $scope.userAddCoCon = false;
    $scope.thenData = false;
    $scope.userData = true;
    if($scope.coConstraintsTable.userMapData[currentId] && $scope.coConstraintsTable.userMapData[currentId][currentIndex]) $scope.data = angular.copy($scope.coConstraintsTable.userMapData[currentId][currentIndex]);
  };
});
