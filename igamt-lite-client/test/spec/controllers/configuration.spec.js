'use strict';

describe('Controller: ConfigurationController', function () {

  var ctrl, scope;

      beforeEach(module('IGL'));

      // include previous module containing mocked service which will override actual service, because it's declared later
      beforeEach(module('mock.config'));

      beforeEach(inject(function($controller, $rootScope, _UserService_) { // inject mocked service
          scope = $rootScope.$new();

          ctrl = $controller('MyController', {
              $scope: scope,
              UserService: _UserService_
          });

      }));

});
