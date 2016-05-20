/**
 * Created by haffo on 5/20/16.
 */

angular.module('igl').directive('confirmOnLeave', function() {
    return {
        link: function($scope, elem, attrs) {
            window.onbeforeunload = function(){
                if ($scope.editForm.$dirty) {
                    return "You have unsaved changes, Do you want to stay on the page?";
                }
            };
            $scope.$on('$locationChangeStart', function(event, next, current) {
                if ($scope.editForm.$dirty) {
                    if(!confirm("You have unsaved changes, Do you want to stay on the page?")) {
                        event.preventDefault();
                    }
                }
            });
        }
    };
});