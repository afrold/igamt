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
            $scope.error.text = error;
            $scope.error.show =true;
        });
    };
}]);

