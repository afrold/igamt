/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('ConfirmAccountDeleteCtrl', function ($scope, $mdDialog, accountToDelete,accountList,Account,Notification) {

  $scope.accountToDelete = accountToDelete;
  $scope.accountList = accountList;
  $scope.delete = function () {
    Account.remove({id:accountToDelete.id},
      function() {
        $mdDialog.hide($scope.accountToDelete);
      },
      function() {
      }
    );
  };

  $scope.cancel = function () {
    $mdDialog.hide('cancel');
  };
});
