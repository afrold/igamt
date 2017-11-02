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
      var index = _.findIndex($rootScope.tables, function (child) {
          return child.id === table.id;
      });
      if (index == -1) return false;
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
      newTable.bindingIdentifier = table.bindingIdentifier+(Math.floor(Math.random() * 100) + 1);

      newTable.id = new ObjectId().toString();
      newTable.createdFrom =table.id;
      newTable.libIds = [];
      newTable.referenceUrl= $rootScope.getPhinvadsURL(table);

      newTable.libIds.push($rootScope.tableLibrary.id);

      newTable.sourceType="INTERNAL";

      if(newTable.numberOfCodes && newTable.numberOfCodes>500){

          $scope.codesPresence[newTable.id]=false;

      }else{
          $scope.codesPresence[newTable.id]=true;
      }

      $scope.selectedTables.push(newTable);

  };

    $scope.getAllTables=function () {
        return _.union($rootScope.tables,$scope.selectedTables);

    };

  $scope.AsIs=function (table){
      $scope.selectedTables.push(table);
      if(table.numberOfCodes&&table.numberOfCodes>500){

          $scope.codesPresence[table.id]=false;

      }else{
          $scope.codesPresence[table.id]=true;
      }
  };

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
    
    $scope.saveDisabled=function () {
        var allTables=$scope.getAllTables();
        for(i=0; i<$scope.selectedTables.length; i++) {
            if ($scope.duplicated($scope.selectedTables[i])) {
                return true;
            }
        }
        return false;
    };
    $scope.duplicated=function(table){
        var allTables=$scope.getAllTables();
            for(i=0; i<allTables.length; i++) {

                if(allTables[i].id!==table.id&&allTables[i].bindingIdentifier==table.bindingIdentifier&&allTables[i].scope==table.scope){
                    return true;
                }

            }

        return false;
    };
});


