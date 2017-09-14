/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('addDefTextCtrl',
  function($scope, $rootScope, $mdDialog, field, PcService, $http, SegmentLibrarySvc) {
    $scope.field = field;
    $scope.close = function() {
      //$scope.field.attributes.comment = $scope.comment;
      $mdDialog.hide();
    };
  });
