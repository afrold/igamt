'use strict';

describe('Controller: AboutCtrl', function () {

  // load the controller's module
  beforeEach(module('igl'));

  var $controller,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function(_$controller_){
    // The injector unwraps the underscores (_) from around the parameter names when matching
    $controller = _$controller_;
  }));


  describe('$scope.releaseNotes', function() {
    it("No more release notes", function() {
      var $scope = {};
      var controller = $controller('AboutCtrl', { $scope: $scope });
      expect($scope.releaseNotes.length).toBe(0);
    });
  });

});
