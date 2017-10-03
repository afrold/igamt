/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DeleteMessagePredicateCtrl', function($scope, $mdDialog, position, message, $rootScope) {
  $scope.selectedMessage = message;
  $scope.position = position;
  $scope.delete = function() {
    $scope.deleteExistingPredicate($scope.selectedMessage);

    $mdDialog.hide('ok');
  };

  $scope.deleteExistingPredicate = function(current) {
    if (current.predicates && current.predicates.length > 0) {
      var toBeDeletePredicate = null;
      for (var i in current.predicates) {
        var positionPath = null;
        if (current.positionPath == null || current.positionPath == '') {
          var positionPath = current.predicates[i].constraintTarget;
        } else {
          var positionPath = current.positionPath + '.' + current.predicates[i].constraintTarget;
        }
        if (positionPath == $scope.position) {
          toBeDeletePredicate = i;
        }
      }
      if (toBeDeletePredicate != null) current.predicates.splice(toBeDeletePredicate, 1);
    }
    if (current.type == 'message' || current.type == 'group') {
      for (var i in current.children) {
        $scope.deleteExistingPredicate(current.children[i]);
      }
    }
  };

  $scope.cancel = function() {
    $mdDialog.hide('cancel');
  };
});
