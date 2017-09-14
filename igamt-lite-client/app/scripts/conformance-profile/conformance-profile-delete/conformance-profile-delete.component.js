/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfirmMessageDeleteCtrl', function($scope, $mdDialog, messageToDelete, $rootScope, MessagesSvc, IgDocumentService, CloneDeleteSvc) {
  $scope.messageToDelete = messageToDelete;
  $scope.loading = false;
  $scope.delete = function() {
    $scope.loading = true;
    IgDocumentService.deleteMessage($rootScope.igdocument.id, $scope.messageToDelete.id).then(function(res) {
      MessagesSvc.delete($scope.messageToDelete).then(function(result) {
        // We must delete from two collections.
        //CloneDeleteSvc.execDeleteMessage($scope.messageToDelete);
        if ($rootScope.messages.children) {
          var index = MessagesSvc.findOneChild($scope.messageToDelete.id, $rootScope.messages.children);
          if (index >= 0) {
            $rootScope.messages.children.splice(index, 1);
          }
        }

        var tmp = MessagesSvc.findOneChild($scope.messageToDelete.id, $rootScope.igdocument.profile.messages.children);
        if (tmp != null) {
          var index = $rootScope.igdocument.profile.messages.children.indexOf(tmp);
          if (index >= 0) {
            $rootScope.igdocument.profile.messages.children.splice(index, 1);
          }
        }

        $rootScope.messagesMap[$scope.messageToDelete.id] = null;
        $rootScope.references = [];
        if ($rootScope.message != null && $rootScope.message.id === $scope.messageToDelete.id) {
          $rootScope.message = null;
        }
        $rootScope.msg().text = "messageDeleteSuccess";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
        $scope.loading = false;
        $mdDialog.hide($scope.messageToDelete);
      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
        $scope.loading = false;
      });
    }, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
      $rootScope.manualHandle = true;
      $scope.loading = false;
    });
  };


  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };


});
