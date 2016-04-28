/**
 * Created by gcr
 */
angular.module('igl').run(function ($httpBackend, $q, $http) {

      $httpBackend.whenGET(/^api\/datatypes\?).*/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../../test/fixtures/datatypes/datatype-AD-HL7STANDARD-2.5.1', false);
        request.send(null);
        var datatype = angular.fromJson(request.response);
        return [request.status, datatype, {}];
    });

});
