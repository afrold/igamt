/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ValueSetExportConfigCtrl', function ($scope, $rootScope, IGDocumentExportConfigService, $timeout, blockUI) {

  $scope.allTables = [];
  $scope.selectedTables = [];
  $scope.checkedAll = {value: false};
  $scope.tokenPromise = undefined;
  $scope.countInternals = 0;
  $scope.filterCriteria = {
    export: "*",
    sourceType: "*",
    bindingIdentifier: "",
    name: ""
  };

  $rootScope.$on("event:initValueSetExportConfig", function (event) {
    $scope.initExportConfig();
  });

  $scope.itemsByPage = 15;

  $scope.initExportConfig = function () {
    $scope.allTables = [];
    $scope.allTables = angular.copy($rootScope.tables);
    $scope.displayed = [].concat($scope.allTables);
    var valueSetsToExport = angular.copy($rootScope.igdocument.exportConfig).valueSetsToExport;
    valueSetsToExport = valueSetsToExport == undefined || valueSetsToExport == null ? [] : valueSetsToExport;
    for (var i = 0; i < $scope.displayed.length; i++) {
      var table = $scope.displayed[i];
      setExport(table, valueSetsToExport.indexOf(table.id) > -1 ? 'Exported' : 'Not Exported');
      $scope.countInternals = table.sourceType == 'INTERNAL' ? $scope.countInternals + 1 : $scope.countInternals;
    }
  };

  $scope.clearFilters = function () {
    $scope.filterCriteria = {
      export: "*",
      sourceType: "*",
      bindingIdentifier: "",
      name: ""
    };
    $scope.filter();
  };

  $scope.filter = function () {
    $timeout(function () {
      if ($scope.tokenPromise) {
        $timeout.cancel($scope.tokenPromise);
        $scope.tokenPromise = undefined;
      }
      $scope.tokenPromise = $timeout(function () {
        $scope.displayed = _.filter($scope.allTables, function (table) {
          var res = ($scope.filterCriteria.sourceType == "*" || table.sourceType == $scope.filterCriteria.sourceType)
            && ($scope.filterCriteria.export == "*" || table.export == $scope.filterCriteria.export)
            && ($scope.filterCriteria.name == "" || table.name.indexOf($scope.filterCriteria.name) > -1)
            && ($scope.filterCriteria.bindingIdentifier == "" || table.bindingIdentifier.indexOf($scope.filterCriteria.bindingIdentifier) > -1);
          return res;
        });
      });
    });
  };

  $scope.checkValueSet = function (table) {
    setExport(table, table['export'] == 'Exported' ? 'Not Exported' : 'Exported');
  };

  var setExport = function (table, value) {
    table['export'] = value;
    var index = $scope.selectedTables.indexOf(table.id);
    if (value == 'Exported') {
      if (index <= -1) {
        $scope.selectedTables.push(table.id);
      }
    } else {
      if (index > -1) {
        $scope.selectedTables.splice(index, 1);
      }
    }
  };


  $scope.isChecked = function (table) {
    return $scope.selectedTables.indexOf(table.id) > -1;
  };

  $scope.isIndeterminate = function () {
    return ($scope.selectedTables.length !== 0 &&
      $scope.selectedTables.length !== $scope.displayed.length);
  };

  $scope.isCheckedAll = function () {
    return ($scope.selectedTables.length !== 0 &&
      $scope.selectedTables.length == $scope.displayed.length);
  };

  $scope.checkAllValueSets = function () {
    var res = $scope.checkedAll.value ? 'Exported' : 'Not Exported';
    for (var i = 0; i < $scope.displayed.length; i++) {
      setExport($scope.displayed[i], res);
    }
  };


  $scope.itemsByPage = 15;

  $scope.saveConfig = function () {
    if ($rootScope.igdocument != null) {
      var exportConfig = angular.copy($rootScope.igdocument.exportConfig);
      exportConfig.valueSetsToExport = [];
      for (var i = 0; i < $scope.selectedTables.length; i++) {
        exportConfig.valueSetsToExport.push($scope.selectedTables[i]);
      }
      IGDocumentExportConfigService.save($rootScope.igdocument.id, exportConfig).then(function (result) {
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
