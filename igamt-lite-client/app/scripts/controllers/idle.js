'use strict';

/* "newcap": false */

angular.module('igl')
    .controller('IdleCtrl', function($scope, Idle, Keepalive, $modal){
        $scope.started = false;

        function closeModals() {
            if ($scope.warning) {
                $scope.warning.close();
                $scope.warning = null;
            }

            if ($scope.timedout) {
                $scope.timedout.close();
                $scope.timedout = null;
            }
        }

        $scope.$on('IdleStart', function() {
            closeModals();

            $scope.warning = $modal.open({
                templateUrl: 'warning-dialog.html',
                windowClass: 'modal-danger'
            });
        });

        $scope.$on('IdleEnd', function() {
            closeModals();
        });

        $scope.$on('IdleTimeout', function() {
            closeModals();
            $scope.timedout = $modal.open({
                templateUrl: 'timedout-dialog.html',
                windowClass: 'modal-danger'
            });
        });

        $scope.start = function() {
            closeModals();
            Idle.watch();
            $scope.started = true;
        };

        $scope.stop = function() {
            closeModals();
            Idle.unwatch();
            $scope.started = false;

        };
    });

