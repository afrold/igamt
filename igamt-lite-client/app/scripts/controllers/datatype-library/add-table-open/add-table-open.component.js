/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('AddTableOpenCtrlLIB', function($scope, $modalInstance, tableLibrary, derivedTables, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
  $scope.loading = false;
  //$scope.igdocumentToSelect = igdocumentToSelect;
  $scope.source = '';
  $scope.selectedHL7Version = '';
  $scope.searchText = '';
  $scope.hl7Versions = [];
  $scope.hl7Tables = null;
  $scope.phinvadsTables = null;
  $scope.selectedTables = [];

  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };

  $scope.listHL7Versions = function() {
    return $http.get('api/igdocuments/findVersions', {
      timeout: 60000
    }).then(function(response) {
      var hl7Versions = [];
      var length = response.data.length;
      for (var i = 0; i < length; i++) {
        hl7Versions.push(response.data[i]);
      }
      $scope.hl7Versions = hl7Versions;
    });
  };

  $scope.loadTablesByVersion = function(hl7Version) {
    $scope.loading = true;
    $scope.selectedHL7Version = hl7Version;
    return $http.get('api/igdocuments/' + hl7Version + "/tables", {
      timeout: 60000
    }).then(function(response) {
      $scope.hl7Tables = response.data;
      $scope.loading = false;
    });
  };

  $scope.searchPhinvads = function(searchText) {
    $scope.loading = true;
    $scope.searchText = searchText;
    if($scope.searchText!==''){
      return $http.get('api/igdocuments/' + searchText + "/PHINVADS/tables", {
        timeout: 600000
      }).then(function(response) {
        $scope.phinvadsTables = response.data;
        $scope.loading = false;
      });
    }

  }
  $scope.createNewExtension = function(ext) {
    if (tableLibrary != null) {
      var rand = (Math.floor(Math.random() * 10000000) + 1);
      if (tableLibrary.metaData.ext === null) {
        return ext != null && ext != "" ? ext + "_" + rand : rand;
      } else {
        return ext != null && ext != "" ? ext + "_" + tableLibrary.metaData.ext + "_" + rand + 1 : rand + 1;
      }
    } else {
      return null;
    }
  };

  $scope.addTable = function(table) {
    var newTable = angular.copy(table);
    newTable.participants = [];
    newTable.bindingIdentifier = $scope.createNewExtension(table.bindingIdentifier);
    newTable.scope = $rootScope.datatypeLibrary.scope;

    if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
      for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
        newTable.codes[i].id = new ObjectId().toString();
      }
    }
    console.log(JSON.stringify(newTable));
    $scope.selectedTables.push(newTable);
  };

  $scope.deleteTable = function(table) {
    var index = $scope.selectedTables.indexOf(table);
    if (index > -1) $scope.selectedTables.splice(index, 1);
  };


  function positionElements(chidren) {
    var sorted = _.sortBy(chidren, "sectionPosition");
    var start = sorted[0].sectionPosition;
    _.each(sorted, function(sortee) {
      sortee.sectionPosition = start++;
    });
    return sorted;
  }
});
