/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('EditCommentCtrl', function($scope, $rootScope, $mdDialog, userInfoService, currentNode, currentComment, disabled, type) {
  $scope.currentNode = currentNode;
  $scope.currentComment = currentComment;
  var currentPath = null;
  if (type == 'message') {
    currentPath = $rootScope.refinePath($scope.currentNode.path);
  } else {
    currentPath = $scope.currentNode.path;
  }

  $scope.disabled = disabled;
  var targetObj = type === 'datatype' ? $rootScope.datatype : type === 'segment' ? $rootScope.segment : $rootScope.message;
  $scope.title = '';

  if (type == 'message') {
    $scope.title = 'Comment:' + targetObj.name + '.' + $rootScope.refinePath($scope.currentNode.locationPath);
  } else {
    $scope.title = 'Comment: ' + targetObj.name + '.' + $scope.currentNode.path;
  }
  $scope.descriptionText = '';

  if ($scope.currentComment) $scope.descriptionText = $scope.currentComment.description;

  $scope.cancel = function() {
    $mdDialog.hide();
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
      targetObj.comments.push(newComment);
    }

    $mdDialog.hide($scope.currentNode);
  };
});
