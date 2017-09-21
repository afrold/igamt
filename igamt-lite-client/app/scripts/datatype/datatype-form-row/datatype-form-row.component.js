/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('FormRowCtrl', function($scope, $filter) {
  $scope.init = function(node) {
    $scope.node = node;
  }

  $scope.formName = "form_" + new Date().getTime();
});

