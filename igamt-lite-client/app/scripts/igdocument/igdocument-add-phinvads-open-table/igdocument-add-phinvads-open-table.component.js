/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddPHINVADSTableOpenCtrl', function ($scope, $mdDialog, selectedTableLibary, $rootScope, $http, $cookies, TableLibrarySvc, TableService) {
  $scope.loading = false;
  $scope.selectedTableLibary = selectedTableLibary;
  $scope.searchText = '';
  $scope.hl7Tables = null;
  $scope.preloadedPhinvadsTables = [];
  $scope.phinvadsTables = [];
  $scope.selectedTables = [];
  $scope.codesPresence={};
  $scope.searched = false;
  // $scope.tablestoAdd=tablestoAdd;

  $scope.cancel = function () {
    $mdDialog.hide();
  };


  $scope.searchPhinvads = function (searchText) {
    $scope.loading = true;
    $scope.searchText = searchText;
    return $http.get('api/igdocuments/' + searchText + "/PHINVADS/tables", {
      timeout: 600000
    }).then(function (response) {
      $scope.phinvadsTables = response.data;
      $scope.loading = false;
      $scope.searched = true;
    });
  };
  $scope.isAlreadyIn = function (table) {
    if ($rootScope.tablesMap[table.id] == null) return false;
    return true;
  };

  $scope.isAlreadySelected = function (table) {
    var index = _.findIndex($scope.selectedTables, function (child) {
      return child.id === table.id;
    });
    if (index == -1) return false;
    return true;
  };

  $scope.addTable = function (table) {
    $scope.selectedTables.push(table);
  };

  $scope.deleteTable = function (table) {
    var index = $scope.selectedTables.indexOf(table);
    if (index > -1) $scope.selectedTables.splice(index, 1);
  };


  $scope.AsFlavor=function (table) {

      var newTable= angular.copy(table);
      newTable.shareParticipantIds = [];
      newTable.status="UNPUBLISHED";
      newTable.scope="USER";

      newTable.id = new ObjectId().toString();
      newTable.createdFrom =table.id;
      newTable.libIds = [];
      newTable.referenceUrl= $rootScope.getPhinvadsURL(table);

      newTable.libIds.push($rootScope.tableLibrary.id);

      newTable.sourceType="INTERNAL";

      if(newTable.numberOfCodes&&newTable.numberOfCodes>500){

          $scope.codesPresence[newTable.id]=false;

      }else{
          $scope.codesPresence[newTable.id]=true;
      }

      $scope.selectedTables.push(newTable);

  };

  $scope.AsIs=function (table){
      $scope.selectedTables.push(table);
      if(table.numberOfCodes&&table.numberOfCodes>500){

          $scope.codesPresence[table.id]=false;

      }else{
          $scope.codesPresence[table.id]=true;
      }
  };





  // $scope.save = function () {
  //   var childrenLinks = [];
  //
  //   // angular.forEach($scope.selectedTables, function (v) {
  //   //   if(v.sourceType=='INTERNAL'){
  //   //       $scope.selectedTables.push($scope.copy(v));
  //   //       v.sourceType=='EXTERNAL';
  //   //   }
  //   // });
  //
  //   for (var i = 0; i < $scope.selectedTables.length; i++) {
  //     $http.get('api/tables/' + $scope.selectedTables[i].id, {
  //       timeout: 600000
  //     }).then(function (response) {
  //       var addedTable = response.data;
  //       $rootScope.tables.splice(0, 0, addedTable);
  //       $rootScope.tablesMap[addedTable.id] = addedTable;
  //     });
  //
  //     var newLink = angular.fromJson({
  //       id: $scope.selectedTables[i].id,
  //       bindingIdentifier: $scope.selectedTables[i].bindingIdentifier
  //     });
  //
  //
  //     $scope.selectedTableLibary.children.push(newLink);
  //     childrenLinks.push(newLink);
  //   }
  //   TableLibrarySvc.addChildren($scope.selectedTableLibary.id, childrenLinks).then(function (link) {
  //
  //     if ($scope.editForm) {
  //       $scope.editForm.$setPristine();
  //       $scope.editForm.$dirty = false;
  //     }
  //     $rootScope.clearChanges();
  //     $mdDialog.hide();
  //     $rootScope.msg().text = "tableSaved";
  //     $rootScope.msg().type = "success";
  //     $rootScope.msg().show = true;
  //
  //   }, function (error) {
  //     $scope.saving = false;
  //     $rootScope.msg().text = error.data.text;
  //     $rootScope.msg().type = error.data.type;
  //     $rootScope.msg().show = true;
  //   });
  //
  //
  // };





    $scope.save=function () {
        var wrapper={tables:$scope.selectedTables,codesPresence:$scope.codesPresence};

        TableService.savePhinvads($scope.selectedTableLibary.id, wrapper).then(function(tables){
          angular.forEach(tables, function(t){

            $rootScope.tables.push(t);
            $scope.selectedTableLibary.codePresence[t.id]= $scope.codesPresence[t.id];
            var newLink = angular.fromJson({
                id:t.id,
                bindingIdentifier: t.bindingIdentifier
                  });

            $scope.selectedTableLibary.children.push(newLink);
                  if ($scope.editForm) {
                    $scope.editForm.$setPristine();
                    $scope.editForm.$dirty = false;
                  }
                  $rootScope.clearChanges();
                  $mdDialog.hide();
                  $rootScope.msg().text = "tableSaved";
                  $rootScope.msg().type = "success";
                  $rootScope.msg().show = true;


          });
      }, function (error) {

              $scope.saving = false;
              $rootScope.msg().text = error.data.text;
              $rootScope.msg().type = error.data.type;
              $rootScope.msg().show = true;
      });

    }
});


