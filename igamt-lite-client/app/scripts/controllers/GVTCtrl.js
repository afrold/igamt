/**
 * Created by haffo on 2/13/17.
 */



angular.module('igl').controller('GVTLoginCtrl', ['$scope', 'user', 'GVTSvc', '$mdDialog', function($scope, user, GVTSvc, $mdDialog) {
    $scope.user = user;
    $scope.error = { text: undefined, show: false };
    $scope.cancel = function() {
        $mdDialog.hide();
    };

    $scope.login = function() {
        $scope.error = { text: undefined, show: false };
        GVTSvc.login($scope.user.username, $scope.user.password).then(function(auth) {
            $mdDialog.hide(auth);
        }, function(error) {
            $scope.error.text = error.data.text;
            $scope.error.show = true;
            $scope.error.type = 'danger';

        });
    };
}])

angular.module('igl').controller('GVTErrorsCtrl', ['$scope', '$modalInstance', 'errorDetails', function($scope, $modalInstance, errorDetails) {
    $scope.errorDetails = errorDetails;
    $scope.tmpProfileErrors = errorDetails != null ? [].concat($scope.errorDetails.profileErrors) : [];
    $scope.tmpConstraintErrors = errorDetails != null ? [].concat($scope.errorDetails.constraintsErrors) : [];
    $scope.tmpValueSetErrors = errorDetails != null ? [].concat($scope.errorDetails.vsErrors) : [];

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.close = function() {
        $modalInstance.close();
    };
}]);