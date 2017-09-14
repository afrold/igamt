/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('CantDeleteMsgCtrl', function ($scope, ngTreetableParams, $mdDialog, msg, $rootScope) {
  $scope.msg = msg;
  $scope.loading = false;
  var getAppliedProfileComponentsById = function (cp) {
    var result = [];
    for (var i = 0; i < cp.profileComponentIds.length; i++) {
      result.push($rootScope.profileComponentsMap[cp.profileComponentIds[i]]);
    }
    return result;
  }
  var getAppliedCompositeProfilesByIds = function (cpIds) {
    var result = [];

    for (var i = 0; i < cpIds.length; i++) {
      result.push({
        compositeName: $rootScope.compositeProfilesStructureMap[cpIds[i]].name,
        pcs: getAppliedProfileComponentsById($rootScope.compositeProfilesStructureMap[cpIds[i]]),
        coreMessageName: $rootScope.messagesMap[$rootScope.compositeProfilesStructureMap[cpIds[i]].coreProfileId].name
      });
    }
    return result;

  }

  $scope.cantDeleteMsgParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($scope.msg.compositeProfileStructureList && $scope.msg.compositeProfileStructureList.length > 0) {

        return getAppliedCompositeProfilesByIds($scope.msg.compositeProfileStructureList);


      }
    },
    getTemplate: function (node) {
      return 'applyPcToTable';
    }
  });

  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };
});
