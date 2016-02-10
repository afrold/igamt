'use strict';
// gcr Not yet working 
describe('ConfirmSegmentDeleteCtrlTest', function () {

  // load the controller's module
  beforeEach(module('igl'));

  var ConfirmSegmentDeleteCtrl;
  var scope; 
  var rootScope;
  var modalInstance;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    rootScope = $rootScope;
    
    modalInstance = { 
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
              then: jasmine.createSpy('modalInstance.result.then')
            }
    	};

    ConfirmSegmentDeleteCtrl = $controller('ConfirmSegmentDeleteCtrl', {
      $scope: scope,
      $modalInstance: modalInstance,
      segToDelete: "x",
      $rootScope: rootScope
  });
  })
  );

  it("Can we delete a segment?", function () {
	  expect(scope).toBeDefined();
	  expect(modalInstance).toBeDefined();
	  expect(ConfirmSegmentDeleteCtrl).toBeDefined();
	  expect(rootScope).toBeDefined();
	  ConfirmSegmentDeleteCtrl.delete();
  });
});
