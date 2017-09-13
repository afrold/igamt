/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('ShareTableCtrl', function($scope, $modalInstance, $http, igdocumentSelected, userList, TableService, $rootScope) {

  $scope.igdocumentSelected = igdocumentSelected;

  // Add participants username and fullname
  // Find share participants
  if ($scope.igdocumentSelected.shareParticipantIds && $scope.igdocumentSelected.shareParticipantIds.length > 0) {
    $scope.igdocumentSelected.shareParticipantIds.forEach(function(participant) {
      $http.get('api/shareparticipant', { params: { id: participant.accountId } })
        .then(
          function(response) {
            participant.username = response.data.username;
            participant.fullname = response.data.fullname;
          },
          function(error) {
            console.log(error);
          }
        );
    });
  }
  $scope.userList = userList;
  $scope.error = "";
  $scope.tags = [];
  $scope.ok = function() {
    var idsTab = $scope.tags.map(function(user) {
      return user.accountId;
    });

    TableService.share($scope.igdocumentSelected.id, idsTab, $rootScope.accountId).then(function(result) {
      // Add participants for direct view
      $scope.igdocumentSelected.shareParticipantIds = $scope.igdocumentSelected.shareParticipantIds || [];
      $scope.tags.forEach(function(tag) {
        tag.permission = $scope.selectedItem.selected;
        tag.pendingApproval = true;
        $scope.igdocumentSelected.shareParticipantIds.push(tag);
      });
      $rootScope.msg().text = "vsSharedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $modalInstance.close();
    }, function(error) {
      $scope.error = error.data;
      console.log(error);
    });
  };
  $scope.cancel = function() {
    $modalInstance.dismiss('cancel');
  };

  $scope.selectedItem = {
    selected: "VIEW"
  };
  $scope.itemArray = ["VIEW"];

  $scope.loadUsernames = function($query) {
    return userList.filter(function(user) {
      return user.username.toLowerCase().indexOf($query.toLowerCase()) != -1;
    });
  };

  $scope.unshare = function(shareParticipant) {
    $scope.loading = false;
    TableService.unshare($scope.igdocumentSelected.id, shareParticipant.accountId).then(function(res) {
      var indexOfId = $scope.igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.accountId);
      if (indexOfId > -1) {
        $scope.igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
      }
      var participantIndex = -1;
      for (var i = 0; i < $scope.igdocumentSelected.shareParticipantIds.length; i++) {
        if ($scope.igdocumentSelected.shareParticipantIds[i].accountId === shareParticipant.accountId) {
          participantIndex = i;
          $scope.userList.push($scope.igdocumentSelected.shareParticipantIds[i]);
          break;
        }
      }
      if (participantIndex > -1) {
        $scope.igdocumentSelected.shareParticipantIds.splice(participantIndex, 1);
      }
      $scope.loading = false;
      $rootScope.msg().text = "vsUnSharedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
    }, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
      $scope.loading = false;
    });
  };


});
