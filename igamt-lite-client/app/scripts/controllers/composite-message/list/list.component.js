angular.module('igl').controller('ListCompositeMessageCtrl', function($scope, $rootScope, $http, CompositeMessageService) {




  $scope.save = function() {
    $scope.saving = true;
    var message = $rootScope.compositeMessage;
    CompositeMessageService.save(message).then(function(result) {
      $rootScope.compositeMessage.dateUpdated = result.dateUpdated;
      $rootScope.compositeMessage = result;
      $rootScope.$emit("event:updateIgDate");
      var index = findIndex(message.id);
      $rootScope.igdocument.profile.compositeMessages.children[index] = result


      if (index < 0) {
        $rootScope.igdocument.profile.compositeMessages.children.splice(0, 0, message);
      }
      cleanState();


    }, function(error) {
      $rootScope.msg().text = error.data.text;
      $rootScope.msg().type = error.data.type;
      $rootScope.msg().show = true;
    });
  };
  var findIndex = function(id) {
    for (var i = 0; i < $rootScope.igdocument.profile.compositeMessages.children.length; i++) {
      if ($rootScope.igdocument.profile.compositeMessages.children[i].id === id) {
        return i;
      }
    }
    return -1;
  };
  var cleanState = function() {

    $scope.clearDirty();
    $scope.editForm.$setPristine();
    $scope.editForm.$dirty = false;
    $rootScope.clearChanges();
    if ($scope.compositeMessageParams) {
      $scope.compositeMessageParams.refresh();
    }
  };

});
