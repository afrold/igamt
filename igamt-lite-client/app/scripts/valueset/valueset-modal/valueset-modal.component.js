/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('TableModalCtrl', function($scope) {
  $scope.showModal = false;
  $scope.toggleModal = function() {
    $scope.showModal = !$scope.showModal;
  };
});
