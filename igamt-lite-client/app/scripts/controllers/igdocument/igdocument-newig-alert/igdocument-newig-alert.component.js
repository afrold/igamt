/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('CreateNewIGAlertCtrl', function ($scope, $rootScope, $http, $modalInstance) {
  $scope.close = function () {
    $modalInstance.dismiss('cancel');
  };
});
