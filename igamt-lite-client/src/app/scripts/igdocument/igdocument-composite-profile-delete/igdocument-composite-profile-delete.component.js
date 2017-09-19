/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DeleteCompositeProfileCtrl', function ($scope, $mdDialog, compositeMessageToDelete, $rootScope, $http, CompositeProfileService, PcService) {

  $scope.compositeMessageToDelete = compositeMessageToDelete;
  var pcsToChange = [];


  $scope.loading = false;
  $scope.delete = function () {
    $scope.loading = true;
    console.log("$rootScope.igdocument.profile.messages.children");
    console.log($rootScope.igdocument.profile.messages.children);
    console.log("$scope.compositeMessageToDelete");
    console.log($scope.compositeMessageToDelete);
    console.log("$rootScope.profileComponents");
    console.log($rootScope.profileComponents);
    CompositeProfileService.delete($scope.compositeMessageToDelete.id, $rootScope.igdocument.id).then(function () {
      var index = $rootScope.compositeProfiles.indexOf($scope.compositeMessageToDelete);
      if (index > -1) {
        $rootScope.compositeProfiles.splice(index, 1);
      }
      var index1 = $rootScope.igdocument.profile.compositeProfiles.children.indexOf($scope.compositeMessageToDelete);
      if (index1 > -1) {
        $rootScope.igdocument.profile.compositeProfiles.children.splice(index1, 1);
      }
      $rootScope.compositeProfilesStructureMap[$scope.compositeMessageToDelete.id] = null;

      for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
        if ($rootScope.igdocument.profile.messages.children[i].id === $scope.compositeMessageToDelete.coreProfileId) {

          var index = $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList.indexOf($scope.compositeMessageToDelete.id);
          if (index > -1) {
            $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList.splice(index, 1);
          }
          $rootScope.messagesMap[$rootScope.igdocument.profile.messages.children[i].id] = $rootScope.igdocument.profile.messages.children[i];
        }
      }

      for (var i = 0; i < $scope.compositeMessageToDelete.profileComponentIds.length; i++) {
        for (var j = 0; j < $rootScope.profileComponents.length; j++) {
          if ($rootScope.profileComponents[j].id === $scope.compositeMessageToDelete.profileComponentIds[i]) {
            var index = $rootScope.profileComponents[j].compositeProfileStructureList.indexOf($scope.compositeMessageToDelete.id);
            if (index > -1) {
              $rootScope.profileComponents[j].compositeProfileStructureList.splice(index, 1);
            }
            $rootScope.profileComponentsMap[$rootScope.profileComponents[j].id] = $rootScope.profileComponents[j];
          }
        }
      }
      if ($rootScope.compositeProfileStructure != null && $rootScope.compositeProfileStructure.id === $scope.compositeMessageToDelete.id) {
        $rootScope.subview = null;
        $rootScope.compositeProfileStructure = null;
      }
      $rootScope.msg().text = "compositeProfileDeleteSuccess";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $rootScope.manualHandle = true;
      $scope.loading = false;
      $mdDialog.hide($scope.compositeMessageToDelete);
    });


  };

  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };
});
