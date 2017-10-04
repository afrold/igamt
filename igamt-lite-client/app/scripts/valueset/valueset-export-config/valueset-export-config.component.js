/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ValueSetExportConfigCtrl', function ($scope, $rootScope, IGDocumentExportConfigService, TableLibrarySvc, blockUI) {

  $rootScope.allTables = [];
  $rootScope.selectedTables = [];
  $scope.toAdd = [];
  $scope.toRemove = [];

  $rootScope.$on("event:initValueSetExportConfig", function (event) {
    $scope.initExportConfig();
  });

  $scope.initExportConfig = function () {
    $rootScope.allTables = [];
    $rootScope.selectedTables = [];
    $scope.toAdd = [];
    $scope.toRemove = [];
    $rootScope.allTables = angular.copy($rootScope.tables);
    var valueSetsToExport = angular.copy($rootScope.igdocument.exportConfig).valueSetsToExport;
    if (valueSetsToExport && valueSetsToExport.length > 0) {
      for (var i = 0; i < $rootScope.allTables.length; i++) {
        var table = $rootScope.allTables[i];
        if (valueSetsToExport.indexOf(table.id) >= 0) {
          $scope.addValueSet(table);
        }
      }
    }
  };

  $scope.selectToAdd = function (table) {
    var index = $scope.toAdd.indexOf(table);
    if (index < 0) {
      $scope.toAdd.push(table);
    } else {
      $scope.toAdd.splice(index, 1);
    }
  };

  $scope.selectToRemove = function (table) {
    var index = $scope.toRemove.indexOf(table);
    if (index < 0) {
      $scope.toRemove.push(table);
    } else {
      $scope.toRemove.splice(index, 1);
    }
  };


  $scope.addSelectedValueSets = function () {
    if ($scope.toAdd && $scope.toAdd.length > 0) {
      for (var i = 0; i < $scope.toAdd.length; i++) {
        $scope.addValueSet($scope.toAdd[i]);
      }
      $scope.toAdd = [];
    }
  };

  $scope.removeSelectedValueSets = function () {
    if ($scope.toRemove && $scope.toRemove.length > 0) {
      for (var i = 0; i < $scope.toRemove.length; i++) {
        $scope.removeValueSet($scope.toRemove[i]);
      }
      $scope.toRemove = [];
    }
  };

  $scope.isSelectedToAdd = function (table) {
    $scope.toAdd.indexOf(table) > 0;
  };

  $scope.isSelectedToRemove = function (table) {
    $scope.toRemove.indexOf(table) > 0;
  };


  $scope.addValueSet = function (table) {
    var index = $rootScope.selectedTables.indexOf(table);
    if (index < 0) {
      $rootScope.selectedTables.unshift(table);
    }

    index = $rootScope.allTables.indexOf(table);
    if (index >= 0) {
      $rootScope.allTables.splice(index, 1);
    }
  };

  $scope.removeAllValueSet = function () {
    $scope.toRemove= [];
    $scope.toAdd= [];
    for (var i = 0; i < $rootScope.selectedTables.length; i++) {
      var table = $rootScope.selectedTables[i];
      $scope.removeValueSet(table);
    }
    $rootScope.selectedTables = [];
  };

  $scope.addAllValueSet = function () {
    $scope.toRemove= [];
    $scope.toAdd= [];
    for (var i = 0; i < $rootScope.allTables.length; i++) {
      var table = $rootScope.allTables[i];
      $scope.addValueSet(table);
    }
    $rootScope.allTables = [];
  };


  $scope.removeValueSet = function (table) {
    var index = $rootScope.selectedTables.indexOf(table);
    if (index >= 0) {
      $rootScope.selectedTables.splice(index, 1);
    }
    // index =  $rootScope.selectedTables.indexOf(table);
    // if (index > -1) {
    //   $rootScope.selectedTables.splice(index, 1);
    // }
    //
    index = $rootScope.allTables.indexOf(table);
    if (index < 0) {
      $rootScope.allTables.unshift(table);
    } else {
      $rootScope.allTables.splice(0, 0, $rootScope.allTables.splice(index, 1)[0]);
    }
  };


  $scope.saveConfig = function () {
    if ($rootScope.igdocument != null) {
      var exportConfig = angular.copy($rootScope.igdocument.exportConfig);
      exportConfig.valueSetsToExport = [];
      for(var i=0; i < $rootScope.selectedTables.length; i++){
        exportConfig.valueSetsToExport.push($rootScope.selectedTables[i].id);
      }
      IGDocumentExportConfigService.save($rootScope.igdocument.id,exportConfig).then(function (result) {
        $scope.saving = false;
        $scope.saved = true;
        $rootScope.igdocument['exportConfig'] = exportConfig;
        if ($scope.editForm) {
          $scope.editForm.$setPristine();
          $scope.editForm.$dirty = false;
        }
        $rootScope.clearChanges();
        $rootScope.msg().text = "sectionSaved";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
      }, function (error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
        $scope.saved = false;
        $scope.saving = false;
      });
    }
  };

  $scope.resetConfig = function () {
    $scope.editForm.$setPristine();
    $scope.editForm.$dirty = false;
    $rootScope.clearChanges();
    $scope.initExportConfig();
  };


});
