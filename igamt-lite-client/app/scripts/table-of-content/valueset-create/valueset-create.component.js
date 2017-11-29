/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('CreateValueSet', ['$rootScope', '$scope', '$mdDialog','selectedTableLibary','TableService','TableLibrarySvc', function ($rootScope, $scope, $mdDialog,selectedTableLibary,TableService,TableLibrarySvc) {

  $scope.newTable={};
  $scope.selectedTableLibary=selectedTableLibary;
  $scope.newTable.shareParticipantIds = [];
  $scope.newTable.sourceType=null;
  $scope.newTable.codes=[];
  $scope.newTable.scope = selectedTableLibary.scope;
  $scope.newTable.id = null;
  $scope.newTable.libIds = [];
  $scope.newTable.codes = [];
  $scope.newTable.newTable = true;
  $scope.newTable.authorNotes="";
  $scope.newTable.referenceUrl="";
  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };

    $scope.duplicated=function(table){
        var allTables=$rootScope.tables;
        for(i=0; i<allTables.length; i++) {

            if(allTables[i].id!==table.id&&allTables[i].bindingIdentifier==table.bindingIdentifier&&allTables[i].scope==table.scope){
                return true;
            }

        }

        return false;
    };


  $scope.add = function () {
    $scope.newTable.extensibility="Undefined";
    $scope.newTable.stability="Undefined";
    $scope.newTable.contentDefinition="Undefined";


    TableService.save($scope.newTable).then(function (result) {
      console.log($scope.newTable);
      console.log(result);

      var newTable = result;
      var newLink = {};
      newLink.bindingIdentifier = newTable.bindingIdentifier;
      newLink.id = newTable.id;

      TableLibrarySvc.addChild($scope.selectedTableLibary.id, newLink).then(function (link) {
        $scope.selectedTableLibary.children.splice(0, 0, newLink);
        $rootScope.tables.splice(0, 0, newTable);
        $rootScope.table = newTable;
        $rootScope.tablesMap[newTable.id] = newTable;
        $mdDialog.hide(result);
        $rootScope.$broadcast('event:openTable', newTable);
      }, function (error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

    }, function (error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });

  };
}]);
