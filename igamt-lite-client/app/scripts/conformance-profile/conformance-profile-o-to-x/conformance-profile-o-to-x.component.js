/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('OtoXCtrl', function($scope, $mdDialog, message, $rootScope, blockUI) {
  console.log(message);
  $scope.message = message;
  $scope.loading = false;

  $scope.confirm = function(message) {
    $scope.loading = true;
    blockUI.start();
    if ($scope.message.type === 'message') {
      for (var node = 0; node < message.children.length; node++) {
        if (message.children[node].usage === "O") {
          message.children[node].usage = "X";
          message.children[node].max = 0;
          message.children[node].min = 0;

        }
        if (message.children[node].type === "group") {

          $scope.confirm(message.children[node])
        }

      }
    } else if ($scope.message.type === 'segment') {
      for (var node = 0; node < message.fields.length; node++) {
        if (message.fields[node].usage === "O") {
          message.fields[node].usage = "X";
          message.fields[node].max = 0;
          message.fields[node].min = 0;

        }




      }
    } else if ($scope.message.type === 'datatype') {
      for (var node = 0; node < message.components.length; node++) {
        if (message.components[node].usage === "O") {
          message.components[node].usage = "X";
          message.components[node].max = 0;
          message.components[node].min = 0;

        }




      }
    }

    $rootScope.msg().text = "OtoXSuccess";
    $rootScope.msg().type = "success";
    $rootScope.msg().show = true;
    $rootScope.manualHandle = true;
    $scope.loading = false;
    $rootScope.messageTree = null;
    $rootScope.processMessageTree($rootScope.message);
    blockUI.stop();
    $mdDialog.hide($scope.message);
  };


  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };


});
