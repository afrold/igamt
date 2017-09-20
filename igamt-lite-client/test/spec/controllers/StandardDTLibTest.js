'use strict';
// gcr Not yet working 
describe('StandardDTLibCtl', function () {

  // load the controller's module
  beforeEach(module('igl'));

  var StandardDTLibCtl;
  var scope; 
  var modalInstance;
  var mockMasterDTLibSvc;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope, MasterDTLibSvc) {
    scope = $rootScope.$new();
    modalInstance = { 
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
              then: jasmine.createSpy('modalInstance.result.then')
            }
    	};
    mockMasterDTLibSvc = MasterDTLibSvc;
    spyOn(mockProfileAccessSvc, 'Version');

   HL7VersionsInstanceDlgCtrl = $controller('StandardDTLibCtl', {
      $scope: scope,
      $modalInstance: modalInstance,
      MasterDTLibSvc : mockMasterDTLibSvc
  });
  })
  );

  it('Are we sorting as expected?', function () {
	  expect(scope).toBeDefined();
  });
});
