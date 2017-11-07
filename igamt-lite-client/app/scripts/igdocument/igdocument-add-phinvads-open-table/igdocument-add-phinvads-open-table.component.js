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
      newTable.bindingIdentifier = table.bindingIdentifier+"_"+(Math.floor(Math.random() * 1000) + 1);
      newTable.id = new ObjectId().toString();
      newTable.createdFrom =table.id;
      newTable.libIds = [];
      newTable.referenceUrl= $rootScope.getPhinvadsURL(table);
      newTable.libIds.push($rootScope.tableLibrary.id);
      newTable.sourceType=null;
      $scope.selectedTables.push(newTable);

        if(newTable.numberOfCodes && newTable.numberOfCodes>500){
            newTable.sourceType="EXTERNAL";

        }else{
            newTable.sourceType="INTERNAL";
        }

    };
    $scope.getAllTables=function(){
        return _.union($rootScope.tables,$scope.selectedTables);
    };
    $scope.toggleFlavor=function (table) {
        if(table.sourceType=="INTERNAL"){
            table.sourceType="EXTERNAL";
        }else{
            table.sourceType="INTERNAL";
        }

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
        var reducedMap={};
        for(i=0;i<$scope.selectedTables.length; i++){
            if($scope.codesPresence[$scope.selectedTables[i].id]==false){
                reducedMap[$scope.selectedTables[i].id]=false;
            }
        }

        var wrapper={tables:$scope.selectedTables,codesPresence:reducedMap};

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

    };
    
    $scope.saveDisabled=function () {
        for(i=0; i<$scope.selectedTables.length; i++) {
            var table=$scope.selectedTables[i];
            if ($scope.duplicated(table)||!table.sourceType||table.bindingIdentifier==="") {
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


