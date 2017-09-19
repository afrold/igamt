/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl')
  .controller('DatatypesInLib', function($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $q, $modal, $timeout, CloneDeleteSvc, ViewSettings, DatatypeService, ComponentService, MastermapSvc, FilteringSvc, DatatypeLibrarySvc, TableLibrarySvc, MessageService, TableService, blockUI) {
    $scope.accordStatus = {
      isCustomHeaderOpen: false,
      isFirstOpen: true,
      isSecondOpen: true,
      isThirdOpen: true,
      isFirstDisabled: false
    };
    $scope.editableDT = '';
    $scope.editableVS = '';
    $scope.readonly = false;
    $scope.saved = false;
    $scope.message = false;
    $scope.datatypeCopy = null;
    $scope.viewSettings = ViewSettings;
    $scope.selectedChildren = [];
    $scope.saving = false;
    $scope.init = function() {};
    $scope.deleteComponent = function(componentToDelete, datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'DeleteComponent.html',
        controller: 'DeleteComponentCtrl',
        size: 'md',
        resolve: {
          componentToDelete: function() {
            return componentToDelete;
          },
          datatype: function() {
            return datatype;
          }


        }
      });
      modalInstance.result.then(function() {

        $scope.setDirty();
        try {
          if ($scope.datatypesParams)
            $scope.datatypesParams.refresh();
        } catch (e) {

        }
      });
    };

    $scope.deletePredicate = function(position, datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'DeleteDatatypePredicate.html',
        controller: 'DeleteDatatypePredicateCtrl',
        size: 'md',
        resolve: {
          position: function() {
            return position;
          },
          datatype: function() {
            return datatype;
          }
        }
      });
      modalInstance.result.then(function() {
        $scope.setDirty();
      });
    };

    $scope.openPredicateDialog = function(node) {
      if (node.usage == 'C') $scope.managePredicate(node);
    };

    $scope.save = function() {
      var datatype = $rootScope.datatype;

      console.log(datatype);
      var ext = datatype.ext;

      DatatypeService.save(datatype).then(function(result) {
        var oldLink = DatatypeLibrarySvc.findOneChild(result.id,$scope.datatypeLibrary.children);
        var newLink = DatatypeService.getDatatypeLink(result);
        newLink.ext = ext;
        DatatypeLibrarySvc.updateChild($scope.datatypeLibrary.id, newLink).then(function(link) {
          $scope.saving = false;
          //$rootScope.datatypesMap[result.id] = result;
          console.log("before the merge")
          console.log($rootScope.datatypesMap[result.id]);
          console.log("After the merge")
          console.log($rootScope.datatypesMap[result.id]);

          DatatypeService.merge($rootScope.datatypesMap[result.id], result);
          DatatypeService.merge($rootScope.datatypesMap[result.id], result);

          cleanState();
        }, function(error) {
          $scope.saving = false;
          $rootScope.msg().text = "Sorry an error occured. Please try again";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
        });

      }, function(error) {
        $scope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    };
    $scope.confirmPublish = function(datatypeCopy) {
      var modalInstance = $modal.open({
        templateUrl: 'ConfirmDatatypePublishCtl.html',
        controller: 'ConfirmDatatypePublishCtl',
        resolve: {
          datatypeToPublish: function() {
            return datatypeCopy;
          }
        }
      });
      modalInstance.result.then(function(datatypeCopy) {
        if ($rootScope.datatypesParams) {
          $rootScope.datatypesParams.refresh();
        }
        $scope.save();
      });
    };


    $scope.abortPublish = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'AbortPublishCtl.html',
        controller: 'AbortPublishCtl',
        resolve: {
          datatypeToPublish: function() {
            return datatype;
          },
          unpublishedDatatypes: function() {
            return $scope.unpublishedDatatypes;
          },
          unpublishedTables: function() {
            return $scope.unpublishedTables;
          }

        }
      });

    };



    $scope.OtoX = function(message) {
      console.log(message);
      var modalInstance = $modal.open({
        templateUrl: 'OtoX.html',
        controller: 'OtoXCtrl',
        size: 'md',
        resolve: {
          message: function() {
            return message;
          }
        }
      });
      modalInstance.result.then(function() {
        $scope.setDirty();
        try {
          if ($scope.datatypesParams)
            $scope.datatypesParams.refresh();
        } catch (e) {

        }
      });
    };

    $scope.editableComp = '';
    $scope.editComponent = function(component) {
      $scope.editableComp = component.id;
      $scope.compName = component.name;

    };

    $scope.backComp = function() {
      $scope.editableComp = '';
    };
    $scope.applyComp = function(datatype, component, name, position) {
      blockUI.start();
      $scope.editableComp = '';
      if (component) {
        component.name = name;


      }
      if (position) {
        MessageService.updatePosition(datatype.components, component.position - 1, position - 1);
      }
      $scope.setDirty();

      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
      $scope.Posselected = false;
      blockUI.stop();

    };
    $scope.selectPos = function() {

      $scope.Posselected = true;
    };

    $scope.dtmSliderOptions = {
      ceil: 7,
      floor: 0,
      showSelectionBar: true,
      onChange: function(id) {
        $scope.setDirty();
      },
      showTicks: true,
      getTickColor: function (value) {
        if (value < 3)
          return 'red';
        if (value < 6)
          return 'orange';
        if (value < 8)
          return 'yellow';
        return '#2AE02A';
      }
    };

    $scope.refreshSlider = function(){
      setTimeout(function(){
        $scope.$broadcast('reCalcViewDimensions');
        console.log("refreshed Slider!!");
      }, 1000);
    };





    $scope.initDatatypes = function() {
      // if($rootScope.datatypesParams!==undefined){
      //     $rootScope.datatypesParams.refresh();

      // }
      console.log("$rootScope.datatype");
      // else{
      $rootScope.datatypesParams = new ngTreetableParams({
        getNodes: function(parent) {
          return DatatypeService.getDatatypeNodesInLib(parent, $rootScope.datatype);
        },
        getTemplate: function(node) {
          return DatatypeService.getTemplateINLIB(node, $rootScope.datatype);
        }
      });
      console.log($rootScope.datatype);
      if ($rootScope.datatypesParams) {

        $rootScope.datatypesParams.refresh();
      }


      //}
    };



    $scope.selectDT = function(field, datatype) {
      $scope.DTselected = true;
      blockUI.start();
      field.datatype.ext = JSON.parse(datatype).ext;
      field.datatype.id = JSON.parse(datatype).id;
      field.datatype.label = JSON.parse(datatype).label;
      field.datatype.name = JSON.parse(datatype).name;
      console.log(field);
      $scope.setDirty();
      // $rootScope.processElement(field);

      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
      $scope.editableDT = '';
      $scope.DTselected = false;
      blockUI.stop();


    };
    // $scope.applyDT = function(field, datatype) {
    //     blockUI.start();
    //     field.datatype.ext = JSON.parse(datatype).ext;
    //     field.datatype.id = JSON.parse(datatype).id;
    //     field.datatype.label = JSON.parse(datatype).label;
    //     field.datatype.name = JSON.parse(datatype).name;
    //     console.log(field);
    //     $scope.setDirty();
    //     // $rootScope.processElement(field);

    //     if ($scope.datatypesParams)
    //         $scope.datatypesParams.refresh();
    //     $scope.editableDT = '';
    //     $scope.DTselected = false;
    //     blockUI.stop();

    // };
    $scope.redirectDT = function(datatype) {
      DatatypeService.getOne(datatype.id).then(function(datatype) {
        var modalInstance = $modal.open({
          templateUrl: 'redirectCtrl.html',
          controller: 'redirectCtrl',
          size: 'md',
          resolve: {
            destination: function() {
              return datatype;
            }
          }



        });
        modalInstance.result.then(function() {
          console.log("EDITING")
          $scope.editDataType(datatype);
        });



      });
    };


//        $scope.editDT = function(field) {
//            $scope.editableDT = field.id;
//            $scope.loadLibrariesByFlavorName = function() {
//                var delay = $q.defer();
//                $scope.ext = null;
//                $scope.results = [];
//                $scope.tmpResults = [];
//                $scope.results = $scope.results.concat(filterFlavors($scope.datatypeLibrary, field.datatype.name));
//                $scope.tmpResults = [].concat($scope.results);
//                DatatypeLibrarySvc.findLibrariesByFlavorName(field.datatype.name, 'HL7STANDARD', $scope.datatypeLibrary.metaData.hl7Version).then(function(libraries) {
//                    if (libraries != null) {
//                        _.each(libraries, function(library) {
//                            $scope.results = $scope.results.concat(filterFlavors(library, field.datatype.name));
//                        });
//                    }
//
//                    $scope.results = _.uniq($scope.results, function(item, key, a) {
//                        return item.id;
//                    });
//                    $scope.tmpResults = [].concat($scope.results);
//
//                    delay.resolve(true);
//                }, function(error) {
//                    $rootScope.msg().text = "Sorry could not load the data types";
//                    $rootScope.msg().type = error.data.type;
//                    $rootScope.msg().show = true;
//                    delay.reject(error);
//                });
//                return delay.promise;
//            };
//
//
//            var filterFlavors = function(library, name) {
//                var results = [];
//                _.each(library.children, function(link) {
//                    if (link.name === name) {
//                        link.libraryName = library.metaData.name;
//                        link.hl7Version = $rootScope.datatypesMap[link.id].hl7Version;
//                        //link.hl7Version = library.metaData.hl7Version;
//                        results.push(link);
//                    }
//                });
//                return results;
//            };
//
//
//
//
//            $scope.loadLibrariesByFlavorName().then(function(done) {
//                console.log($scope.results);
//                // $scope.selection.selected = $scope.currentDatatype.id;
//                // $scope.showSelectedDetails($scope.currentDatatype);
//            });
//        };
    $scope.results = [];
    $scope.editDT = function(field) {
      $scope.editableDT = field.id;

      $scope.results = [];
      angular.forEach($scope.datatypeLibrary.children ,function(dtLink){
        if(dtLink.name&&dtLink.name===field.datatype.name&&field.datatype.id!==dtLink.id){
          $scope.results.push(dtLink);
        }
      });
    };

    $scope.backDT = function() {
      $scope.editableDT = '';
    };



    $scope.editVSModal = function(component) {
      var modalInstance = $modal.open({
        templateUrl: 'editVSModal.html',
        controller: 'EditVSCtrlForLib',
        windowClass: 'edit-VS-modal',
        resolve: {

          valueSets: function() {
            return $rootScope.tables;
          },

          component: function() {
            return component;
          }

        }
      });
      modalInstance.result.then(function(datatype) {
        $scope.setDirty();
        if ($scope.datatypesParams) {
          $scope.datatypesParams.refresh();
        }
      });

    };

    $scope.editVS = function(field) {
      $scope.editableVS = field.id;
      if (field.table !== null) {
        $scope.VSselected = true;
        $scope.selectedValueSet = field.table;
        console.log($scope.selectedValueSet);

      } else {
        $scope.VSselected = false;

      }
    };
    $scope.backVS = function() {
      $scope.editableVS = '';
    };

    $scope.selectVS = function(field, valueSet) {
      $scope.selectedValueSet = valueSet;
      $scope.VSselected = true;
      $scope.editableVS = '';
      if (field.table === null) {
        field.table = {
          id: '',
          bindingIdentifier: ''

        };
        console.log(field);

      }

      field.table.id = $scope.selectedValueSet.id;
      field.table.bindingIdentifier = $scope.selectedValueSet.bindingIdentifier;
      $scope.setDirty();
      $scope.VSselected = false;



    };
    // $scope.applyVS = function(field) {
    //     $scope.editableVS = '';
    //     if (field.table === null) {
    //         field.table = {
    //             id: '',
    //             bindingIdentifier: ''

    //         };
    //         console.log(field);

    //     }

    //     field.table.id = $scope.selectedValueSet.id;
    //     field.table.bindingIdentifier = $scope.selectedValueSet.bindingIdentifier;
    //     $scope.setDirty();
    //     $scope.VSselected = false;

    // };

    $scope.redirectVS = function(valueSet) {
      TableService.getOne(valueSet.id).then(function(valueSet) {
        var modalInstance = $modal.open({
          templateUrl: 'redirectCtrl.html',
          controller: 'redirectCtrl',
          size: 'md',
          resolve: {
            destination: function() {
              return valueSet;
            }
          }



        });
        modalInstance.result.then(function() {
          $rootScope.editTable(valueSet);
        });



      });
    };


    $scope.selectedVS = function() {
      return ($scope.selectedValueSet !== undefined);
    };
    $scope.unselectVS = function() {
      $scope.selectedValueSet = undefined;
      $scope.VSselected = false;

      //$scope.newSeg = undefined;
    };
    $scope.isVSActive = function(id) {
      if ($scope.selectedValueSet) {
        return $scope.selectedValueSet.id === id;
      } else {
        return false;
      }

    };





    $scope.addComponentModal = function(datatype) {
      console.log(datatype);
      var modalInstance = $modal.open({
        templateUrl: 'AddComponentModal.html',
        controller: 'AddComponentCtrl',
        windowClass: 'app-modal-window',
        resolve: {

          valueSets: function() {
            return $rootScope.tables;
          },
          datatypes: function() {
            return $rootScope.datatypes;
          },
          datatype: function() {
            return datatype;
          },
          messageTree: function() {
            return $rootScope.messageTree;
          }

        }
      });
      modalInstance.result.then(function(datatype) {
        $scope.setDirty();

        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      });
    };

    $scope.copy = function(datatype) {
      CloneDeleteSvc.copyDatatype(datatype);
    };

    $scope.reset = function() {
      console.log("Called reset");
      blockUI.start();
      $rootScope.datatype = angular.copy($rootScope.datatypesMap[$scope.datatype.id]);
      cleanState();
      blockUI.stop();
    };

    $scope.recordDatatypeChange = function(type, command, id, valueType, value) {
      var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $rootScope.datatype.id);
      if (datatypeFromChanges === undefined) {
        $rootScope.recordChangeForEdit2(type, command, id, valueType, value);
      }
    };

    $scope.close = function() {
      $rootScope.datatype = null;
      $scope.refreshTree();
      $scope.loadingSelection = false;
    };

    $scope.delete = function(datatype) {
      CloneDeleteSvc.deleteDatatype(datatype);
    };

    $scope.hasChildren = function(node) {
      return node && node != null && node.datatype && $rootScope.getDatatype(node.datatype.id) != undefined && $rootScope.getDatatype(node.datatype.id).components != null && $rootScope.getDatatype(node.datatype.id).components.length > 0;
    };

    $scope.validateLabel = function(label, name) {
      if (label && !label.startsWith(name)) {
        return false;
      }
      return true;
    };

    $scope.onDatatypeChange = function(node) {
      $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'datatype', node.datatype);
      $scope.refreshTree(); // TODO: Refresh only the node
    };

    $scope.refreshTree = function() {
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
    };

    $scope.goToTable = function(table) {
      $scope.$emit('event:openTable', table);
    };

    $scope.deleteTable = function(node) {
      node.table = null;
      $rootScope.recordChangeForEdit2('component', 'edit', node.id, 'table', null);
    };

    $scope.managePredicate = function(node) {
      var modalInstance = $modal.open({
        templateUrl: 'PredicateDatatypeCtrl.html',
        controller: 'PredicateDatatypeCtrlForLib',
        windowClass: 'app-modal-window',
        resolve: {
          selectedNode: function() {
            return node;
          }
        }
      });
      modalInstance.result.then(function(node) {
        $scope.selectedNode = node;
        $scope.setDirty();
      }, function() {});
    };

    $scope.manageConformanceStatement = function(node) {
      var modalInstance = $modal.open({
        templateUrl: 'ConformanceStatementDatatypeCtrl.html',
        controller: 'ConformanceStatementDatatypeCtrlForLib',
        windowClass: 'app-modal-window',
        resolve: {
          selectedNode: function() {
            return node;
          }
        }
      });
      modalInstance.result.then(function(node) {
        $scope.selectedNode = node;
        $scope.setDirty();
      }, function() {});
    };

    $scope.isSubDT = function(component) {
      if ($rootScope.datatype != null) {
        for (var i = 0, len = $rootScope.datatype.components.length; i < len; i++) {
          if ($rootScope.datatype.components[i].id === component.id)
            return false;
        }
      }
      return true;
    };

    $scope.findDTByComponentId = function(componentId) {
      return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
    };

    $scope.countConformanceStatements = function(position) {
      var count = 0;
      if ($rootScope.datatype != null)
        for (var i = 0, len1 = $rootScope.datatype.conformanceStatements.length; i < len1; i++) {
          if ($rootScope.datatype.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
            count = count + 1;
        }

      return count;
    };

    $scope.countPredicate = function(position) {
      var count = 0;
      if ($rootScope.datatype != null)
        for (var i = 0, len1 = $rootScope.datatype.predicates.length; i < len1; i++) {
          if ($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
            count = count + 1;
        }

      return count;
    };

    $scope.countPredicateOnSubComponent = function(position, componentId) {
      var dt = $scope.findDTByComponentId(componentId);
      if (dt != null)
        for (var i = 0, len1 = dt.predicates.length; i < len1; i++) {
          if (dt.predicates[i].constraintTarget.indexOf(position + '[') === 0)
            return 1;
        }

      return 0;
    };


    $scope.isRelevant = function(node) {
      return DatatypeService.isRelevant(node);
    };

    $scope.isBranch = function(node) {
      return DatatypeService.isBranch(node);
    };


    $scope.isVisible = function(node) {
      return DatatypeService.isVisible(node);
    };

    $scope.children = function(node) {
      return DatatypeService.getNodes(node);
    };

    $scope.getParent = function(node) {
      return DatatypeService.getParent(node);
    };

    $scope.getDatatypeLevelConfStatements = function(element) {
      return DatatypeService.getDatatypeLevelConfStatements(element);
    };

    $scope.getDatatypeLevelPredicates = function(element) {
      return DatatypeService.getDatatypeLevelPredicates(element);
    };

    $scope.isChildSelected = function(component) {
      return $scope.selectedChildren.indexOf(component) >= 0;
    };

    $scope.isChildNew = function(component) {
      return component && component != null && component.status === 'DRAFT';
    };


    $scope.selectChild = function($event, child) {
      var checkbox = $event.target;
      var action = (checkbox.checked ? 'add' : 'remove');
      updateSelected(action, child);
    };


    $scope.selectAllChildren = function($event) {
      var checkbox = $event.target;
      var action = (checkbox.checked ? 'add' : 'remove');
      for (var i = 0; i < $rootScope.datatype.components.length; i++) {
        var component = $rootScope.datatype.components[i];
        updateSelected(action, component);
      }
    };

    var updateSelected = function(action, child) {
      if (action === 'add' && !$scope.isChildSelected(child)) {
        $scope.selectedChildren.push(child);
      }
      if (action === 'remove' && $scope.isChildSelected(child)) {
        $scope.selectedChildren.splice($scope.selectedChildren.indexOf(child), 1);
      }
    };

    //something extra I couldn't resist adding :)
    $scope.isSelectedAllChildren = function() {
      return $rootScope.datatype && $rootScope.datatype != null && $rootScope.datatype.components && $scope.selectedChildren.length === $rootScope.datatype.components.length;
    };


    /**
     * TODO: update master map
     */
    $scope.createNewComponent = function() {
      if ($rootScope.datatype != null) {
        if (!$rootScope.datatype.components || $rootScope.datatype.components === null)
          $rootScope.datatype.components = [];
        var child = ComponentService.create($rootScope.datatype.components.length + 1);
        $rootScope.datatype.components.push(child);
        //TODO update master map
        //MastermapSvc.addDatatypeObject($rootScope.datatype, [[$rootScope.igdocument.id, "ig"], [$scope.datatypeLibrary.id, "profile"]]);
        //TODO:remove as legacy code
        $rootScope.parentsMap[child.id] = $rootScope.datatype;
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      }
    };

    /**
     * TODO: update master map
     */
    $scope.deleteComponents = function() {
      if ($rootScope.datatype != null && $scope.selectedChildren != null && $scope.selectedChildren.length > 0) {
        ComponentService.deleteList($scope.selectedChildren, $rootScope.datatype);
        //TODO update master map
        //TODO:remove as legacy code
        angular.forEach($scope.selectedChildren, function(child) {
          delete $rootScope.parentsMap[child.id];
        });
        $scope.selectedChildren = [];
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
      }
    };


    var cleanState = function() {
      $scope.selectedChildren = [];
      $rootScope.addedDatatypes = [];
      $rootScope.addedTables = [];
      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
      }
      $rootScope.clearChanges();
      if ($scope.datatypesParams)
        $scope.datatypesParams.refresh();
    };
    $scope.callDTDelta = function() {

      $rootScope.$emit("event:openDTDelta");
    };


    $rootScope.$on('event:initDatatypeInLib', function(event) {

      $scope.initt();

    });

    $scope.initt = function() {
      if ($scope.dynamicDt_params) {
        $scope.dynamicDt_params.refresh();
      }

    };
    $scope.cancel = function() {
      //TODO: remove changes from master ma
      angular.forEach($rootScope.datatype.components, function(child) {
        if ($scope.isChildNew(child.status)) {
          delete $rootScope.parentsMap[child.id];
        }
      });
      $rootScope.datatype = null;
      $scope.selectedChildren = [];
      $rootScope.clearChanges();
    };

    var searchById = function(id) {
      var children = $scope.datatypeLibrary.children;
      for (var i = 0; i < $scope.datatypeLibrary.children; i++) {
        if (children[i].id === id) {
          return children[i];
        }
      }
      return null;
    };

    var indexIn = function(id, collection) {
      for (var i = 0; i < collection.length; i++) {
        if (collection[i].id === id) {
          return i;
        }
      }
      return -1;
    };


    $scope.showSelectDatatypeFlavorDlg = function(component) {
      var modalInstance = $modal.open({
        templateUrl: 'SelectDatatypeFlavor.html',
        controller: 'SelectDatatypeFlavorCtrl',
        windowClass: 'app-modal-window',
        resolve: {
          currentDatatype: function() {
            return $rootScope.datatypesMap[component.datatype.id];
          },

          hl7Version: function() {
            return $scope.datatypeLibrary.metaData.hl7Version;
          },
          datatypeLibrary: function() {
            return $scope.datatypeLibrary;
          }
        }
      });
      modalInstance.result.then(function(datatype, ext) {
        //                MastermapSvc.deleteElementChildren(component.datatype.id, "datatype", component.id, component.type);
        //                MastermapSvc.addDatatypeObject(datatype, [[component.id, component.type]]);
        component.datatype.id = datatype.id;
        component.datatype.name = datatype.name;
        component.datatype.ext = datatype.ext;
        $rootScope.processElement(component);
        $scope.setDirty();
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();

      });

    };

  });
