/**
 * Created by gcr
 */
angular.module('igl').run(function ($httpBackend, $q, $http) {


    $httpBackend.whenPOST('api/datatype-library/getDataTypeLibraryByScope').respond(function (method, url, data, headers) {
         var request = new XMLHttpRequest();
         console.log('api/datatype-library/getDataTypeLibraryByScope begin');
         var scope = data;
         var d = null;
         if (scope === 'MASTER') {
        	 	request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
//        	 	request.open('GET', 'test/fixtures/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
//        	 	request.open('GET', '../../resources/datatypes/datatypes-MASTER.json', false);
         	request.send(null);
        		d = angular.fromJson(request.response);
         } else {
             request.open('GET', '../../resources/datatypes/dtLib-2.5.1-HL7STANDARD.json', false);
             request.send(null);
             d = angular.fromJson(request.response);
         }
        console.log('api/datatype-library/getDataTypeLibraryByScope end');
        return [request.status, d, {}];
    });

  $httpBackend.whenGET(/^api\/datatypes\?).*/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../../test/fixtures/datatypes/datatype-AD-HL7STANDARD-2.5.1', false);
        request.send(null);
        var datatype = angular.fromJson(request.response);
        return [request.status, datatype, {}];
    });

});
