/**
 * Created by haffo on 2/13/17.
 */



angular.module('igl').controller('GVTLoginCtrl', ['$scope', '$modalInstance', 'user', 'GVTSvc', function($scope, $modalInstance, user,GVTSvc) {
    $scope.user = user;
    $scope.error = {text: undefined, show:false};
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.login = function() {
        $scope.error = {text: undefined, show:false};
        GVTSvc.login($scope.user.username, $scope.user.password).then(function(auth){
            $modalInstance.close(auth);
        }, function(error){
            $scope.error.text =  error.data != null ? error.data : "ERROR: Cannot access server.";
            $scope.error.show =true;
        });
    };
}])

angular.module('igl').controller('GVTErrorsCtrl', ['$scope', '$modalInstance', 'errorDetails', function($scope, $modalInstance, errorDetails) {
    $scope.errorDetails = errorDetails;
    $scope.tmpProfileErrors = [].concat($scope.errorDetails.profileErrors);
    $scope.tmpConstraintErrors = [].concat($scope.errorDetails.constraintsErrors);
    $scope.tmpValueSetErrors = [].concat($scope.errorDetails.vsErrors);

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
    $scope.close = function() {
        $modalInstance.close();
    };
}]);


