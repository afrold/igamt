/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('SelectSegmentFlavorCtrl', function($scope, $filter, $q, $modalInstance, $rootScope, $http, segmentLibrary, SegmentService, $rootScope, hl7Version, ngTreetableParams, ViewSettings, SegmentLibrarySvc, datatypeLibrary, DatatypeLibrarySvc, currentSegment, TableService) {
  $scope.segmentLibrary = segmentLibrary;
  $scope.datatypeLibrary = datatypeLibrary;
  $scope.resultsError = null;
  $scope.viewSettings = ViewSettings;
  $scope.resultsLoading = null;
  $scope.results = [];
  $scope.tmpResults = [].concat($scope.results);
  $scope.currentSegment = currentSegment;
  $scope.selection = { library: null, scope: null, hl7Version: hl7Version, segment: null, name: $scope.currentSegment != null && $scope.currentSegment ? $scope.currentSegment.name : null, selected: null };


  $scope.segmentFlavorParams = new ngTreetableParams({
    getNodes: function(parent) {
      return SegmentService.getNodes(parent, $scope.selection.segment);
    },
    getTemplate: function(node) {
      return SegmentService.getReadTemplate(node, $scope.selection.segment);
    }
  });

  $scope.loadLibrariesByFlavorName = function() {
    var delay = $q.defer();
    $scope.selection.segment = null;
    $scope.selection.selected = null;
    $scope.resetMap();
    $scope.ext = null;
    $scope.results = [];
    $scope.tmpResults = [];
    $scope.results = $scope.results.concat(filterFlavors($scope.segmentLibrary, $scope.selection.name));
    $scope.tmpResults = [].concat($scope.results);
    SegmentLibrarySvc.findLibrariesByFlavorName($scope.selection.name, 'HL7STANDARD', /*$scope.selection.hl7Version*/ $rootScope.igdocument.profile.metaData.hl7Version).then(function(libraries) {
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
      $rootScope.msg().text = "Sorry could not load the segments";
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


  $scope.selectSegment = function(segment) {
    if (segment && segment != null) {
      $scope.loadingSelection = true;
      $scope.selection.segment = segment;
      $scope.selection.segment["type"] = "segment";
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

  var collectNewSegmentAndDatatypesAndTables = function(segment, datatypes) {
    $rootScope.segmentsMap[segment.id] = segment;
    if (indexIn(segment.id, $rootScope.addedSegments) < 0) {
      $rootScope.addedSegments.push(segment);
    }
    var tmpTables = [];
    angular.forEach(datatypes, function(child) {
      if (indexIn(child.id, $rootScope.datatypes) < 0) {
        $rootScope.datatypesMap[child.id] = child;
      }
      if (indexIn(child.id, $rootScope.addedDatatypes) < 0) {
        $rootScope.addedDatatypes.push(child);
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
        $modalInstance.close($scope.selection.segment);
      }, function(error) {
        $rootScope.msg().text = "Sorry an error occured. Please try again";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });
    } else {
      $modalInstance.close($scope.selection.segment);
    }
  };

  $scope.submit = function() {
    console.log($scope.selection);
    var indexFromLibrary = indexIn($scope.selection.segment.id, $scope.segmentLibrary.children);
    var indexFromCollection = indexIn($scope.selection.segment.id, $rootScope.segments);
    var indexFromMap = $rootScope.segmentsMap[$scope.selection.segment.id] != undefined && $rootScope.segmentsMap[$scope.selection.segment.id] != null ? 100 : -1;
    if (indexFromLibrary < 0 | indexFromCollection < 0 | indexFromMap < 0) {
      SegmentService.get($scope.selection.segment.id).then(function(full) {
        $scope.ext = $scope.selection.segment.ext;
        $scope.selection.segment = full;
        $scope.selection.segment["type"] = "segment";
        SegmentService.collectDatatypes(full.id).then(function(datatypes) {
          collectNewSegmentAndDatatypesAndTables($scope.selection.segment, datatypes);
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
      $modalInstance.close($scope.selection.segment);
    }
  };
  $scope.cancel = function() {
    $scope.resetMap();
    $modalInstance.dismiss('cancel');
  };


  $scope.validateLabel = function(label, name) {
    if (label && !label.startsWith(name)) {
      return false;
    }
    return true;
  };

  $scope.findDTByComponentId = function(componentId) {
    return $rootScope.parentsMap && $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId] : null;
  };

  $scope.isSub = function(component) {
    return $scope.isSubDT(component);
  };

  $scope.isSubDT = function(component) {
    return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
  };

  $scope.hasChildren = function(node) {
    return node && node != null && ((node.fields && node.fields.length > 0) || (node.datatype && $rootScope.getDatatype(node.datatype.id) && $rootScope.getDatatype(node.datatype.id).components && $rootScope.getDatatype(node.datatype.id).components.length > 0));
  };


  $scope.validateLabel = function(label, name) {
    if (label && !label.startsWith(name)) {
      return false;
    }
    return true;
  };


  $scope.isRelevant = function(node) {
    return SegmentService.isRelevant(node);
  };

  $scope.isBranch = function(node) {
    SegmentService.isBranch(node);
  };

  $scope.isVisible = function(node) {
    return SegmentService.isVisible(node);
  };

  $scope.children = function(node) {
    return SegmentService.getNodes(node);
  };

  $scope.getParent = function(node) {
    return SegmentService.getParent(node);
  };

  $scope.getSegmentLevelConfStatements = function(element) {
    return SegmentService.getSegmentLevelConfStatements(element);
  };

  $scope.getSegmentLevelPredicates = function(element) {
    return SegmentService.getSegmentLevelPredicates(element);
  };


  $scope.isChildSelected = function(component) {
    return $scope.selectedChildren.indexOf(component) >= 0;
  };

  $scope.isChildNew = function(component) {
    return component && component != null && component.status === 'DRAFT';
  };

  var containsId = function(id, library) {
    for (var i = 0; i < library.children.length; i++) {
      if (library.children[i].id === id) {
        return true;
      }
    }
  };

  $scope.resetMap = function() {
    if ($rootScope.addedDatatypes = null) {
      angular.forEach($rootScope.addedDatatypes, function(child) {
        delete $rootScope.datatypesMap[child];
      });
    }
  };

  $scope.getLocalDatatypeLabel = function(link) {
    return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
  };

  $scope.getLocalSegmentLabel = function(link) {
    return link != null ? $rootScope.getLabel(link.name, link.ext) : null;
  };

  $scope.loadLibrariesByFlavorName().then(function(done) {
    $scope.selection.selected = $scope.currentSegment.id;
    $scope.selectSegment($scope.currentSegment);
  });

});
