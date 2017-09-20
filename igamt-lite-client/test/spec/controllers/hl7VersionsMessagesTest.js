'use strict';
// gcr Not yet working 
describe('HL7VersionsInstanceDlgCtrl', function () {

  // load the controller's module
  beforeEach(module('igl'));

  var HL7VersionsInstanceDlgCtrl;
  var scope; 
  var modalInstance;
  var mockProfileAccessSvc;
  var mockUserInfoService;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope, ProfileAccessSvc, userInfoService) {
    scope = $rootScope.$new();
    modalInstance = { 
            close: jasmine.createSpy('modalInstance.close'),
            dismiss: jasmine.createSpy('modalInstance.dismiss'),
            result: {
              then: jasmine.createSpy('modalInstance.result.then')
            }
    	};
    mockProfileAccessSvc = ProfileAccessSvc;
    mockUserInfoService = userInfoService;
    spyOn(mockUserInfoService, 'getAccountID');
    spyOn(mockProfileAccessSvc, 'Version');

   HL7VersionsInstanceDlgCtrl = $controller('HL7VersionsDlgCtrl', {
      $scope: scope,
      $modalInstance: modalInstance,
      ProfileAccessSvc : mockProfileAccessSvc,
      userInfoService : mockUserInfoService
  });
  })
  );

  it('Are we sorting as expected?', function () {
	  expect(scope).toBeDefined();
	  expect(scope.createProfile("2.7", [])).toBeDefined();
  });
});
