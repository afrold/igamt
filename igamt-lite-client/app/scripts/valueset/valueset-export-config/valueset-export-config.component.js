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
    if (valueSetsToExport) {
      for (var i = 0; i < $scope.displayed.length; i++) {
        var table = $scope.displayed[i];
        setExport(table, valueSetsToExport.indexOf(table.id) > -1 ? 'Exported' : 'Not Exported');
        $scope.countInternals = table.sourceType == 'INTERNAL' ?  $scope.countInternals + 1:  $scope.countInternals;
      }
    }
  };

  $scope.clearFilters = function(){
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
    if(value == 'Exported'){
        if(index <= -1){
          $scope.selectedTables.push(table.id);
        }
    }else {
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


// var
//   nameList = ['Pierre', 'Pol', 'Jacques', 'Robert', 'Elisa'],
//   familyName = ['Dupont', 'Germain', 'Delcourt', 'bjip', 'Menez'],
//   nationList = ['USA', 'France', 'Germany'],
//   educationList = ['Doctorate', 'Master', 'Bachelor', 'High school'];
//
// function createRandomItem() {
//   var
//     firstName = nameList[Math.floor(Math.random() * 5)],
//     lastName = familyName[Math.floor(Math.random() * 5)],
//     nationality = nationList[Math.floor(Math.random() * 3)],
//     education = educationList[Math.floor(Math.random() * 4)];
//
//   return {
//     firstName: firstName,
//     lastName: lastName,
//     nationality: nationality,
//     education: education
//   };
// }
//
// $scope.itemsByPage = 15;
//
// $scope.collection = [];
// $scope.displayed = [].concat($scope.collection);
// for (var j = 0; j < 200; j++) {
//   $scope.collection.push(createRandomItem());
// }


// $scope.selectToAdd = function (table) {
//   var index = $scope.toAdd.indexOf(table);
//   if (index < 0) {
//     $scope.toAdd.push(table);
//   } else {
//     $scope.toAdd.splice(index, 1);
//   }
// };
//
// $scope.selectToRemove = function (table) {
//   var index = $scope.toRemove.indexOf(table);
//   if (index < 0) {
//     $scope.toRemove.push(table);
//   } else {
//     $scope.toRemove.splice(index, 1);
//   }
// };
//
//
// $scope.addSelectedValueSets = function () {
//   if ($scope.toAdd && $scope.toAdd.length > 0) {
//     for (var i = 0; i < $scope.toAdd.length; i++) {
//       $scope.addValueSet($scope.toAdd[i]);
//     }
//     $scope.toAdd = [];
//   }
// };
//
// $scope.removeSelectedValueSets = function () {
//   if ($scope.toRemove && $scope.toRemove.length > 0) {
//     for (var i = 0; i < $scope.toRemove.length; i++) {
//       $scope.removeValueSet($scope.toRemove[i]);
//     }
//     $scope.toRemove = [];
//   }
// };
//
// $scope.isSelectedToAdd = function (table) {
//   $scope.toAdd.indexOf(table) >= 0;
// };
//
// $scope.isSelectedToRemove = function (table) {
//   $scope.toRemove.indexOf(table) >= 0;
// };
//
//
// $scope.addValueSet = function (table) {
//   var index = $rootScope.selectedTables.indexOf(table);
//   if (index < 0) {
//     $rootScope.selectedTables.push(table);
//   }else {
//     console.log("table found: " + table.bindingIdentifier);
//   }
//
//   index = $rootScope.allTables.indexOf(table);
//   if (index >= 0) {
//     $rootScope.allTables.splice(index, 1);
//   }else {
//     console.log("table not found: " + table.bindingIdentifier);
//   }
// };
//
// $scope.removeAllValueSet = function () {
//   $scope.toRemove= [];
//   $scope.toAdd= [];
//   for (var i = 0; i < $rootScope.selectedTables.length; i++) {
//     var table = $rootScope.selectedTables[i];
//     index = $rootScope.allTables.indexOf(table);
//     if (index < 0) {
//       $rootScope.allTables.push(table);
//     } else {
//       $rootScope.allTables.splice(0, 0, $rootScope.allTables.splice(index, 1)[0]);
//     }
//   }
//   $rootScope.selectedTables = [];
// };
//
// $scope.addAllValueSet = function () {
//   $scope.toRemove= [];
//   $scope.toAdd= [];
//   for (var i = 0; i < $rootScope.allTables.length; i++) {
//     var table = $rootScope.allTables[i];
//     var index = $rootScope.selectedTables.indexOf(table);
//     if (index < 0) {
//       $rootScope.selectedTables.push(table);
//     }
//   }
//   $rootScope.allTables = [];
// };
//
//
// $scope.removeValueSet = function (table) {
//   var index = $rootScope.selectedTables.indexOf(table);
//   if (index >= 0) {
//     $rootScope.selectedTables.splice(index, 1);
//   }
//   // index =  $rootScope.selectedTables.indexOf(table);
//   // if (index > -1) {
//   //   $rootScope.selectedTables.splice(index, 1);
//   // }
//   //
//   index = $rootScope.allTables.indexOf(table);
//   if (index < 0) {
//     $rootScope.allTables.push(table);
//   } else {
//     $rootScope.allTables.splice(0, 0, $rootScope.allTables.splice(index, 1)[0]);
//   }
// };

  $scope.itemsByPage = 15;


  $scope.saveConfig = function () {
    if ($rootScope.igdocument != null) {
      var exportConfig = angular.copy($rootScope.igdocument.exportConfig);
      exportConfig.valueSetsToExport = [];
      for (var i = 0; i < $rootScope.selectedTables.length; i++) {
        exportConfig.valueSetsToExport.push($rootScope.selectedTables[i]);
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
