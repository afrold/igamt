/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmAccountDeleteCtrl', function ($scope, $modalInstance, accountToDelete,accountList,Account,Notification) {

  $scope.accountToDelete = accountToDelete;
  $scope.accountList = accountList;
  $scope.delete = function () {
    Account.remove({id:accountToDelete.id},
      function() {
        $modalInstance.close($scope.accountToDelete);
      },
      function() {
      }
    );
  };

  $scope.cancel = function () {
    $modalInstance.dismiss('cancel');
  };
});
