'use strict';
// gcr Not yet working 
describe('HL7VersionsInstanceDlgCtrl', function () {

  // load the controller's module
  beforeEach(module('igl'));

  var HL7VersionsInstanceDlgCtrl,
    scope;

  // Initialize the controller and a mock scope
  beforeEach(inject(function ($controller, $rootScope) {
    scope = $rootScope.$new();
    HL7VersionsInstanceDlgCtrl = $controller('HL7VersionsInstanceDlgCtrl', {
      $scope: scope
    });
  }));

  it('Are sorting as expected?', function () {
	  var messageList = [[2, "444", "DDD", "xyz"], [5, "111", "AAA", "abc"], [3, "333", "CCC", "def"]];
	  expect(scope).toBeDefined();
	  expect(scope.loadProfilesByVersion()).toBeDefined();
//	  var sorted = scope.sortMessageList(messageList);
//		expect(sorted[0] [2]).toBe("AAA");
//		expect(sorted[1] [2]).toBe("CCC");
//		expect(sorted[2] [2]).toBe("DDD");
  });
});
