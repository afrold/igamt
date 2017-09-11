/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('UnShareIGDocumentCtrl', function ($scope, $modalInstance, $http, igdocumentSelected, shareParticipant, IgDocumentService, $rootScope) {

  $scope.igdocumentSelected = igdocumentSelected;
  $scope.shareParticipant = shareParticipant;
  $scope.error = "";
  $scope.loading = false;
  $scope.ok = function () {
    $scope.loading = true;
    IgDocumentService.unshare(igdocumentSelected.id, shareParticipant.id).then(function (res) {

      var indexOfId = igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.id);
      if (indexOfId > -1) {
        igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
      }
      var participantIndex = -1;
      for (var i = 0; i < igdocumentSelected.shareParticipants.length; i++) {
        if (igdocumentSelected.shareParticipants[i].id === shareParticipant.id) {
          participantIndex = i;
          break;
        }
      }
      if (participantIndex > -1) {
        igdocumentSelected.shareParticipants.splice(participantIndex, 1);
      }
      $scope.loading = false;
      $rootScope.msg().text = "igUnSharedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $mdDialog.hide();
    }, function (error) {

      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
      $scope.loading = false;
    });
  };
  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
