angular.module('igl').controller('ShareIGDocumentCtrl', function ($scope, $location, $mdDialog, $http, igdocumentSelected, userList, IgDocumentService, $rootScope, clipboard, SearchService) {

  $scope.igdocumentSelected = igdocumentSelected;
  $scope.userList = userList;
  $scope.error = "";
  angular.forEach($scope.userList, function (user) {
    if (user.email) {
      user.image = $rootScope.generateHash(user.email);

    }
  });
  $scope.clipboardSupported = clipboard.supported;
  $scope.shareUrlCopiedToClipboard = false;
  var basePath = $scope.url = $location.absUrl().substring(0,$location.absUrl().length - ($location.url().length+1));
  $scope.shareURL = basePath + SearchService.getExportUrl(igdocumentSelected,'html');
  $scope.copyUrlToClipboard = function(){
    clipboard.copyText($scope.shareURL);
    $scope.shareUrlCopiedToClipboard = true;
  };
  console.log($scope.igdocumentSelected);
  $scope.ok = function () {
    var idsTab = $scope.tags.map(function (user) {
      return user.id;
    });
    IgDocumentService.share($scope.igdocumentSelected.id, idsTab).then(function (result) {

      // Add participants for direct view
      $scope.igdocumentSelected.shareParticipants = $scope.igdocumentSelected.shareParticipants || [];
      $scope.tags.forEach(function (tag) {
        tag.permission = $scope.selectedItem.selected;
        tag.pendingApproval = true;
        $scope.igdocumentSelected.shareParticipants.push(tag);
        $scope.igdocumentSelected.realUsers.push(tag);
      });
      $rootScope.msg().text = "igSharedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
      $mdDialog.hide();
    }, function (error) {
      $scope.error = error.data;
      console.log(error);
    });
  };
  $scope.cancel = function () {
    $mdDialog.hide();
  };
  $scope.tags = [];
  $scope.selectedItem = {
    selected: "VIEW"
  };
  $scope.itemArray = ["VIEW"];

  $scope.tags = [];
  $scope.loadUsernames = function ($query) {
    return userList.filter(function (user) {
      return user.username.toLowerCase().indexOf($query.toLowerCase()) != -1;
    });
  };


  $scope.unshare = function (shareParticipant) {
    $scope.loading = false;
    IgDocumentService.unshare($scope.igdocumentSelected.id, shareParticipant.id).then(function (res) {

      var indexOfId = $scope.igdocumentSelected.shareParticipantIds.indexOf(shareParticipant.id);
      if (indexOfId > -1) {
        $scope.igdocumentSelected.shareParticipantIds.splice(indexOfId, 1);
      }
      for (i = 0; i < $scope.igdocumentSelected.realUsers.length; i++) {
        if ($scope.igdocumentSelected.realUsers[i].id == shareParticipant.id) {
          $scope.igdocumentSelected.realUsers.splice(i, 1);
        }
      }
      var participantIndex = -1;
      for (var i = 0; i < $scope.igdocumentSelected.shareParticipants.length; i++) {
        if ($scope.igdocumentSelected.shareParticipants[i].id === shareParticipant.id) {
          participantIndex = i;
          $scope.userList.push($scope.igdocumentSelected.shareParticipants[i]);
          break;
        }
      }
      if (participantIndex > -1) {
        $scope.igdocumentSelected.shareParticipants.splice(participantIndex, 1);
      }
      $scope.loading = false;
      $rootScope.msg().text = "igUnSharedSuccessfully";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
    }, function (error) {

      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
      $scope.loading = false;
    });
  };


});
