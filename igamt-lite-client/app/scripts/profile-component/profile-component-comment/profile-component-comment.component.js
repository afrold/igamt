/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('EditCommentCtrlInPc', function($scope, $rootScope, $mdDialog, userInfoService, currentNode, currentComment, disabled, type) {
  $scope.currentNode = currentNode;
  $scope.currentComment = currentComment;
  var currentPath = null;
  var index = currentNode.path.indexOf(".");
  currentPath = currentNode.path.substr(index + 1);

  $scope.dialogStep = 0;
  console.log($scope.dialogStep);
  $scope.disabled = disabled;
  $scope.title = '';


  $scope.title = 'Comment of ' + $scope.currentNode.path;

  $scope.descriptionText = '';

  if ($scope.currentComment) $scope.descriptionText = $scope.currentComment.description;

  $scope.cancel = function() {
    $mdDialog.hide();
  };

  $scope.goNext = function() {
    $scope.dialogStep = $scope.dialogStep + 1;
  };

  $scope.goBack = function () {
    $scope.dialogStep = $scope.dialogStep - 1;
  };
  $scope.close = function() {
    if ($scope.currentComment) {
      $scope.currentComment.description = $scope.descriptionText;
      $scope.currentComment.lastUpdatedDate = new Date();
    } else {
      var newComment = {};

      newComment.description = $scope.descriptionText;
      newComment.location = currentPath;
      newComment.lastUpdatedDate = new Date();
      if (!currentNode.comments) {
        currentNode.comments = [];
      }
      currentNode.comments.push(newComment);
    }

    $mdDialog.hide($scope.currentNode);
  };
});
