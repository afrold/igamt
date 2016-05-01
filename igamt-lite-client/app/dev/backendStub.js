/**
 * Created by gcr
 */
angular.module('igl').run(function ($httpBackend, $q, $http) {

    $httpBackend.whenGET('api/datatype-library/findHl7Versions').respond(function (method, url, data, headers) {
    	console.log('api/igdocuments/hl7/findHl7Versions');
        return [200, ["2.1","2.2","2.3","2.3.1","2.4","2.5.1","2.6","2.7"], {}];
    });

   $httpBackend.whenPOST('api/datatype-library/findByScopes').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
         console.log('api/findByScopes begin=' + data);
        request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
        request.send(null);
        var datatype = angular.fromJson(request.response);
        return [request.status, datatype, {}];
    });

   $httpBackend.whenPOST('api/datatype-library/findByScopesAndVersion').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        var datatypeLibs = [];
         console.log('api/findByScopesAndVersion begin=' + data);
        request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
        request.send(null);
         datatypeLibs.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypeLibraries/dtLib-HL7STANDARD-2.5.1.json', false);
        request.send(null);
        datatypeLibs.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypeLibraries/dtLib-USER-2.5.1.json', false);
        request.send(null);
        datatypeLibs.push(angular.fromJson(request.response));
         return [request.status, datatypeLibs, {}];
    });

    $httpBackend.whenGET(/^api\/datatype-library\/.*\/datatypes/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
         console.log('api\\/.*\\/ url=' + url);
        request.open('GET', '../../resources/datatypes/datatype-AD-HL7STANDARD-2.5.1.json', false);
        request.send(null);
        var datatype = [];
            datatype.push(angular.fromJson(request.response));
        return [request.status, datatype, {}];
    });

   $httpBackend.whenPOST('api/datatype-library/save').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
         console.log('api/save begin=' + data);
        var response = angular.fromJson(request.response);
        return [request.status, response, {}];
    });

   $httpBackend.whenPOST('api/datatype/findByIds').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/datatype/findByIds begin=' + data);
        var datatypes = [];
        request.open('GET', '../../resources/datatypes/datatype-AUI-HL7STANDARD-2.5.1.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypes/datatype-CCD-HL7STANDARD-2.5.1.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypes/datatype-CD-HL7STANDARD-2.5.1.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        return [request.status, datatypes, {}];
    });

});
