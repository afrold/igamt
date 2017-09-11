/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ViewIGChangesCtrl', function ($scope, $modalInstance, changes, $rootScope, $http) {
  $scope.changes = changes;
  $scope.loading = false;
  $scope.exportChanges = function () {
    $scope.loading = true;
    var form = document.createElement("form");
    form.action = 'api/igdocuments/export/changes';
    form.method = "POST";
    form.target = "_target";
    form.style.display = 'none';
    form.params = document.body.appendChild(form);
    form.submit();
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
