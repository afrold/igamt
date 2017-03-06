angular.module('igl').controller('ListCompositeProfileCtrl', function($scope, $rootScope, $http, CompositeProfileService) {




    
    var cleanState = function() {

        $scope.clearDirty();
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        $rootScope.clearChanges();
        if ($scope.compositeMessageParams) {
            $scope.compositeMessageParams.refresh();
        }
    };

});