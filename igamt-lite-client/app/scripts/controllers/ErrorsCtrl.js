/**
 * Created by haffo on 3/3/16.
 */
angular.module('igl').controller('ErrorDetailsCtrl', function ($scope, $modalInstance, error) {
    $scope.error = error;
    $scope.ok = function () {
        $modalInstance.close($scope.error);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ErrorCtrl', [ '$scope', '$modalInstance', 'StorageService', '$window',
    function ($scope, $modalInstance, StorageService, $window) {
        $scope.refresh = function () {
            $modalInstance.close($window.location.reload());
        };
    }
]);

angular.module('igl').controller('FailureCtrl', [ '$scope', '$modalInstance', 'StorageService', '$window', 'error',
    function ($scope, $modalInstance, StorageService, $window, error) {
        $scope.error = error;
        $scope.close = function () {
            $modalInstance.close();
        };
    }
]);
