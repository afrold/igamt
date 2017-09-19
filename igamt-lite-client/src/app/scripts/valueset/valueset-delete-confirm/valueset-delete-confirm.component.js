/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('ConfirmValueSetDeleteCtrl', function($scope, $mdDialog, tableToDelete, $rootScope, TableService, TableLibrarySvc, CloneDeleteSvc) {
  $scope.tableToDelete = tableToDelete;
  $scope.loading = false;


  $scope.delete = function() {
    $scope.loading = true;
    if ($scope.tableToDelete.scope === 'USER') {
      CloneDeleteSvc.deleteTableAndTableLink($scope.tableToDelete);
    } else {
      CloneDeleteSvc.deleteTableLink($scope.tableToDelete);
    }
    $mdDialog.hide($scope.tableToDelete);
    $scope.loading = false;
  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };


  $scope.isNewTableThenDelete = function(id) {
    if ($rootScope.isNewObject('table', 'add', id)) {
      if ($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['add'] !== undefined) {
        for (var i = 0; i < $rootScope.changes['table']['add'].length; i++) {
          var tmp = $rootScope.changes['table']['add'][i];
          if (tmp.id == id) {
            $rootScope.changes['table']['add'].splice(i, 1);
            if ($rootScope.changes["table"]["add"] && $rootScope.changes["table"]["add"].length === 0) {
              delete $rootScope.changes["table"]["add"];
            }

            if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
              delete $rootScope.changes["table"];
            }
            return true;
          }
        }
      }
      return true;
    }
    if ($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['edit'] !== undefined) {
      for (var i = 0; i < $rootScope.changes['table']['edit'].length; i++) {
        var tmp = $rootScope.changes['table']['edit'][i];
        if (tmp.id === id) {
          $rootScope.changes['table']['edit'].splice(i, 1);
          if ($rootScope.changes["table"]["edit"] && $rootScope.changes["table"]["edit"].length === 0) {
            delete $rootScope.changes["table"]["edit"];
          }

          if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
            delete $rootScope.changes["table"];
          }
          return false;
        }
      }
      return false;
    }
    return false;
  };
});
