/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddHL7TableOpenCtrl', function ($scope, $mdDialog, selectedTableLibary, hl7Version, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
  $scope.loading = false;
  $scope.selectedTableLibary = selectedTableLibary;
  $scope.selectedHL7Version = hl7Version;
  $scope.searchText = '';
  $scope.hl7Versions = [];
  $scope.hl7Tables = null;
  $scope.selectedTables = [];

  $scope.cancel = function () {
    $mdDialog.hide();
  };

  $scope.listHL7Versions = function () {
    return $http.get('api/igdocuments/findVersions', {
      timeout: 60000
    }).then(function (response) {
      var hl7Versions = [];
      var length = response.data.length;
      for (var i = 0; i < length; i++) {
        hl7Versions.push(response.data[i]);
      }
      $scope.hl7Versions = hl7Versions;
    });
  };

  $scope.loadTablesByVersion = function (hl7Version) {
    $scope.loading = true;
    $scope.selectedHL7Version = hl7Version;
    return $http.get('api/igdocuments/' + hl7Version + "/tables", {
      timeout: 60000
    }).then(function (response) {
      $scope.hl7Tables = [];
      angular.forEach(response.data, function (table) {
        if (!$scope.isAlreadyIn(table)&&!table.duplicated) {
          $scope.hl7Tables.push(table);
        }
      });
      $scope.loading = false;
    });
  };


  $scope.isAlreadyIn = function (table) {
    if ($rootScope.tablesMap[table.id] == null) return false;
    return true;
  };

  $scope.addTable = function (table) {
    $scope.selectedTables.push(table);
  };

  $scope.deleteTable = function (table) {
    var index = $scope.selectedTables.indexOf(table);
    if (index > -1) $scope.selectedTables.splice(index, 1);
  };


  $scope.save = function () {
    var childrenLinks = [];
    for (var i = 0; i < $scope.selectedTables.length; i++) {
      var newLink = angular.fromJson({
        id: $scope.selectedTables[i].id,
        bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
      });
      $scope.selectedTableLibary.children.push(newLink);
      childrenLinks.push(newLink);
      var addedTable = $scope.selectedTables[i];
      $rootScope.tables.splice(0, 0, addedTable);
      $rootScope.tablesMap[addedTable.id] = addedTable;
    }
    TableLibrarySvc.addChildren($scope.selectedTableLibary.id, childrenLinks).then(function (link) {

      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
      }
      $rootScope.clearChanges();
      $rootScope.msg().text = "tableSaved";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;

      $mdDialog.hide();

    }, function (error) {
      $scope.saving = false;
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });


  };

  function positionElements(chidren) {
    var sorted = _.sortBy(chidren, "sectionPosition");
    var start = sorted[0].sectionPosition;
    _.each(sorted, function (sortee) {
      sortee.sectionPosition = start++;
    });
    return sorted;
  };

  $scope.listHL7Versions();

  $scope.loadTablesByVersion($scope.selectedHL7Version);
});
