'use strict';

angular.module('igl').controller('IssueCtrl', ['$scope', '$resource',
    function ($scope, $resource) {
        var Issue = $resource('api/sooa/issues/:id');

        $scope.clearIssue = function() {
            $scope.issue.title = '';
            $scope.issue.description = '';
            $scope.issue.email = '';
        };

        $scope.submitIssue = function() {
            var issueToReport = new Issue($scope.issue);
            issueToReport.$save(function() {
                if ( issueToReport.text === '') {
                    $scope.clearIssue();
                }
            });
        };
    }
]);
