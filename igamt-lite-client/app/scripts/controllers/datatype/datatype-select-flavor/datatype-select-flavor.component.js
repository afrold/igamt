/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('SelectDatatypeFlavorCtrl', function($scope, $filter, $modalInstance, $rootScope, $http, currentDatatype, DatatypeService, $rootScope, hl7Version, ngTreetableParams, ViewSettings, DatatypeLibrarySvc, $q, datatypeLibrary, TableService) {
  $scope.resultsError = null;
  $scope.viewSettings = ViewSettings;
  $scope.resultsLoading = null;
  $scope.results = [];
  $scope.tmpResults = [].concat($scope.results);
  $scope.datatypeLibrary = datatypeLibrary;

  $scope.currentDatatype = angular.copy(currentDatatype);
  $scope.selection = { library: null, scope: null, hl7Version: hl7Version, datatype: null, name: $scope.currentDatatype != null && $scope.currentDatatype ? $scope.currentDatatype.name : null, selected: null };
  $scope.dataypesMap = {};
  $scope.datatypeFlavorParams = new ngTreetableParams({
    getNodes: function(parent) {
      return DatatypeService.getNodes(parent, $scope.selection.datatype);
    },
    getTemplate: function(node) {
      return DatatypeService.getReadTemplate(node, $scope.selection.datatype);
    }
  });

  $scope.isRelevant = function(node) {
    var rel = DatatypeService.isRelevant(node);
    return rel;
  };

  $scope.isBranch = function(node) {
    var isBran = DatatypeService.isBranch(node);
    return isBran;
  };


  $scope.isVisible = function(node) {
    var isVis = DatatypeService.isVisible(node);
    return isVis;
  };

  $scope.children = function(node) {
    var chil = DatatypeService.getNodes(node);
    return chil;
  };

  $scope.getParent = function(node) {
    var par = DatatypeService.getParent(node);
    return par;
  };

  $scope.isChildSelected = function(component) {
    return $scope.selectedChildren.indexOf(component) >= 0;
  };

  $scope.isChildNew = function(component) {
    return component && component != null && component.status === 'DRAFT';
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

  $scope.loadLibrariesByFlavorName = function() {
    var delay = $q.defer();
    $scope.selection.datatype = null;
    $scope.selection.selected = null;
    $scope.ext = null;
    $scope.results = [];
    $scope.tmpResults = [];
    $scope.results = $scope.results.concat(filterFlavors(datatypeLibrary, $scope.selection.name));
    $scope.tmpResults = [].concat($scope.results);
    DatatypeLibrarySvc.findLibrariesByFlavorName($scope.selection.name, 'HL7STANDARD', $scope.selection.hl7Version).then(function(libraries) {
      if (libraries != null) {
        _.each(libraries, function(library) {
          $scope.results = $scope.results.concat(filterFlavors(library, $scope.selection.name));
        });
      }

      $scope.results = _.uniq($scope.results, function(item, key, a) {
        return item.id;
      });
      $scope.tmpResults = [].concat($scope.results);

      delay.resolve(true);
    }, function(error) {
      $rootScope.msg().text = "Sorry could not load the data types";
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
      delay.reject(error);
    });
    return delay.promise;
  };

  var filterFlavors = function(library, name) {
    var results = [];
    _.each(library.children, function(link) {
      if (link.name === name) {
        link.libraryName = library.metaData.name;
        link.hl7Version = library.metaData.hl7Version;
        results.push(link);
      }
    });
    return results;
  };

  $scope.isDatatypeSubDT = function(component) {
    return DatatypeService.isDatatypeSubDT(component, $scope.selection.datatype);
  };

  $scope.isSelectedDatatype = function(datatype) {
    return $scope.selection.selected != null && datatype != null && $scope.selection.selected == datatype.id;
  };

  $scope.isSelectedLibrary = function(library) {
    return $scope.selection.library != null && library != null && $scope.selection.library.id == library.id;
  };

  $scope.showSelectedDetails = function(datatype) {
    if (datatype && datatype != null) {
      $scope.selection.datatype = datatype;
      $scope.selection.datatype["type"] = "datatype";
    }
  };

  var indexIn = function(id, collection) {
    for (var i = 0; i < collection.length; i++) {
      if (collection[i].id === id) {
        return i;
      }
    }
    return -1;
  };


  var collectNewDatatypesAndTables = function(root, datatypes) {
    $rootScope.datatypesMap[root.id] = root;
    if (indexIn(root.id, $rootScope.addedDatatypes) < 0) {
      $rootScope.addedDatatypes.push(root);
    }
    var tmpTables = [];
    angular.forEach(datatypes, function(child) {
      $rootScope.datatypesMap[child.id] = child;
      if (indexIn(child.id, $rootScope.addedDatatypes) < 0) {
        $rootScope.addedDatatypes.push(child);
        $rootScope.filteredDatatypesList.push(child);
      }

      if (indexIn(child.table.id, $rootScope.addedTables) < 0) {
        tmpTables.push(child.table.id);

      }
    });

    if (tmpTables.length > 0) {
      TableService.findAllByIds(tmpTables).then(function(tables) {
        $rootScope.addedTables = $rootScope.addedTables.concat(tables);
        angular.forEach(tables, function(table) {
          $rootScope.tablesMap[table.id] = table;
        });
        $modalInstance.close($scope.selection.datatype);
      }, function(error) {
        $rootScope.msg().text = "Sorry an error occured. Please try again";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });
    } else {
      $modalInstance.close($scope.selection.datatype);
    }
  };


  $scope.submit = function() {
    var indexFromLibrary = indexIn($scope.selection.datatype.id, $scope.datatypeLibrary.children);
    var indexFromCollection = indexIn($scope.selection.datatype.id, $rootScope.datatypes);
    var indexFromMap = $rootScope.datatypesMap[$scope.selection.datatype.id] != undefined && $rootScope.datatypesMap[$scope.selection.datatype.id] != null ? 100 : -1;

    if (indexFromLibrary < 0 | indexFromCollection < 0 | indexFromMap < 0) {
      DatatypeService.getOne($scope.selection.datatype.id).then(function(full) {
        DatatypeService.collectDatatypes(full.id).then(function(datatypes) {
          $rootScope.processSegmentsTree($rootScope.segment, null);
          $scope.ext = full.ext;
          $scope.selection.datatype = full;
          $scope.selection.datatype["type"] = "datatype";
          collectNewDatatypesAndTables($scope.selection.datatype, datatypes);
        }, function(error) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = "Sorry could not load the data type";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
        });
      }, function(error) {
        $scope.resultsLoading = false;
        $rootScope.msg().text = "Sorry could not load the data type";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });
    } else {
      $modalInstance.close($scope.selection.datatype);
    }
  };


  $scope.cancel = function() {
    $scope.resetMap();
    $modalInstance.dismiss('cancel');
  };

  $scope.getLocalDatatypeLabel = function(link) {
    return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
  };


  $scope.resetMap = function() {
    if ($rootScope.addedDatatypes = null) {
      angular.forEach($rootScope.addedDatatypes, function(child) {
        var dt = $rootScope.datatypesMap[child];
        if (dt.id !== $scope.currentDatatype.id) {
          delete $rootScope.datatypesMap[child];
        }
      });
    }
  };

  $scope.loadLibrariesByFlavorName().then(function(done) {
    $scope.selection.selected = $scope.currentDatatype.id;
    $scope.showSelectedDetails($scope.currentDatatype);
  });

});
