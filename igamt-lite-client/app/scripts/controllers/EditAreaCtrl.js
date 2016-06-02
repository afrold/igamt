angular.module('igl')
    .controller('EditAreaCtrl', function ($scope, $rootScope, CloneDeleteSvc,SectionSvc,ElementUtils) {

        $scope.init = function(){
            if($scope.editForm) {
                $rootScope.clearChanges();
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
                console.log("=====> set $dirty to false")
            }
        };
        

        
        $rootScope.$on("event:initEditArea", function (event) {
            $scope.init();
        });

        $scope.$watch(
            function(){
              return $scope.editForm != undefined && $scope.editForm.$dirty;
            },
            function handleFormState( newValue) {
                if(newValue){
                    $rootScope.recordChanged();
                }else{
                    $rootScope.clearChanges();
                }
            }
        );

        $scope.setDirty = function () {
            $scope.editForm.$dirty = true;
        };

        $scope.setUsage = function (node) {
            ElementUtils.setUsage(node);
            $scope.setDirty();
        };






    });