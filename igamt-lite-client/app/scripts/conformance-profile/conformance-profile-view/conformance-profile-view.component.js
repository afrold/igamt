/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('MessageViewCtrl', function($scope, $rootScope, Restangular) {
  $scope.loading = false;
  $scope.msg = null;
  $scope.messageData = [];
  $scope.setData = function(node) {
    if (node) {
      if (node.type === 'message') {
        angular.forEach(node.children, function(segmentRefOrGroup) {
          $scope.setData(segmentRefOrGroup);
        });
      } else if (node.type === 'group') {
        $scope.messageData.push({ name: "-- " + node.name + " begin" });
        if (node.children) {
          angular.forEach(node.children, function(segmentRefOrGroup) {
            $scope.setData(segmentRefOrGroup);
          });
        }
        $scope.messageData.push({ name: "-- " + node.name + " end" });
      } else if (node.type === 'segment') {
        $scope.messageData.push + (node);
      }
    }
  };


  $scope.init = function(message) {
    $scope.loading = true;
    $scope.msg = message;
    console.log(message.id);
    $scope.setData($scope.msg);
    $scope.loading = false;
  };

  //        $scope.hasChildren = function (node) {
  //            return node && node != null && node.type !== 'segment' && node.children && node.children.length > 0;
  //        };

});
