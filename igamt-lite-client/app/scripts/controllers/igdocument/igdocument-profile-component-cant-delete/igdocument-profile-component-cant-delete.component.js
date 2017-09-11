/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('CantDeletePcCtrl', function ($scope, $mdDialog, ngTreetableParams, profileComponent, $rootScope, $http, PcService) {
  $scope.profileComponent = profileComponent;
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

  $scope.cantDeletePcParams = new ngTreetableParams({
    getNodes: function (parent) {
      if ($scope.profileComponent.compositeProfileStructureList && $scope.profileComponent.compositeProfileStructureList.length > 0) {

        return getAppliedCompositeProfilesByIds($scope.profileComponent.compositeProfileStructureList);


      }
    },
    getTemplate: function (node) {
      return 'applyPcToTable';
    }
  });

  $scope.cancel = function () {
    $mdDialog.hide();
  };
});

