///**
// * Created by haffo on 2/2/15.
// */
angular.module('igl').run(function ($httpBackend, $q, $http) {
    $httpBackend.whenGET('api/session/keepAlive').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/igdocuments').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        return [request.status, profile, {}];
    });
    $httpBackend.whenGET('api/igdocuments/{id}').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        return [request.status, profile, {}];
    });
    $httpBackend.whenGET('api/usernames').respond(function (method, url, data, headers) {
        return [200, [{
                "username": "woorion",
                "fullname": "Jungyub Woo",
                "id": 44
		}, {
                "username": "nist",
                "fullname": "nist",
                "id": 45
		},
            {
                "username": "test",
                "fullname": "test",
                "id": 46
		}], {}];
    });
    $httpBackend.whenGET(new RegExp('api/shareparticipants\\?ids=.*')).respond(function (method, url, data, headers) {
        return [200, [{
            "username": "woorion",
            "fullname": "Jungyub Woo",
            "id": 44
		}, {
            "username": "test",
            "fullname": "test",
            "id": 46
		}], {}];
    });
    $httpBackend.whenGET(new RegExp('api/shareparticipant\\?id=.*')).respond(function (method, url, data, headers) {
        return [200, {
            "username": "test",
            "fullname": "test",
            "id": 46
		}, {}];
    });
	$httpBackend.whenPOST(new RegExp('api/.*/share')).respond(function (method, url, data, headers) {
        return [200, [], {}];
    });
    $httpBackend.whenGET('api/shortaccounts?filter=accountType::author').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/shortaccounts.json', false);
        request.send(null);
        var profile = request.response;
        return [request.status, profile, {}];
    });
    $httpBackend.whenPOST('api/accounts/1/userpasswordchange').respond(function (method, url, data, headers) {
        return [200, {
            type: 'success',
            text: 'accountPasswordReset',
            resourceId: '1',
            manualHandle: "false"
		}, {}];
    });
    $httpBackend.whenPOST('api/accounts/2/userpasswordchange').respond(function (method, url, data, headers) {
        return [200, {
            type: 'success',
            text: 'invalidPassword',
            resourceId: '2',
            manualHandle: "false"
		}, {}];
    });
    $httpBackend.whenPOST('api/accounts/1/approveaccount').respond(function (method, url, data, headers) {
        return [200, {
            type: 'success',
            text: 'accountApproved',
            resourceId: '1',
            manualHandle: "false"
		}, {}];
    });
    $httpBackend.whenPOST('api/accounts/2/approveaccount').respond(function (method, url, data, headers) {
        return [200, {
            type: 'success',
            text: 'accountIsNotPending',
            resourceId: '2',
            manualHandle: "false"
		}, {}];
    });
    $httpBackend.whenPOST('api/accounts/1/suspendaccount').respond(function (method, url, data, headers) {
        return [200, {
            type: 'success',
            text: 'accountSuspended',
            resourceId: '1',
            manualHandle: "false"
		}, {}];
    });
    // clone and set id to 3
    $httpBackend.whenPOST('api/igdocuments/552014603004d0a9f09caf16/clone').respond(function (method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf11";
        profile.preloaded = false;
        profile.metaData.name = " Cloned " + profile.metaData.name;
        return [request.status, profile, {}];
    });
    $httpBackend.whenPOST('api/profileComponent/findAll').respond(function (method, url, data, headers) {
        return [200, [], {}];
    });
    $httpBackend.whenGET('api/accounts/cuser').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    // clone and set id to 3
    $httpBackend.whenPOST('api/igdocuments/552014603004d0a9f09caf11/clone').respond(function (method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf17";
        profile.preloaded = false;
        profile.metaData.name = " Cloned " + profile.metaData.name;
        return [request.status, profile, {}];
    });
    $httpBackend.whenGET('api/igdocuments/cuser').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var profiles = angular.fromJson(request.response);
        return [request.status, [profiles], {}];
    });
    $httpBackend.whenGET('api/igdocuments/2').respond(function (method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 2;
        return [request.status, profile, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/save').respond(function (method, url, d, headers) {
        console.log("Changes received:" + d.changes);
        return [200, {}, {}];
    });
    $httpBackend.whenDELETE('api/igdocuments/2').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/1/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/2/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/3/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/4/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/igdocuments/3').respond(function (method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 3;
        return [request.status, profile, {}];
    });
    $httpBackend.whenPOST('api/datatypes/4/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/datatypes/4/clone').respond(function (method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/datatype.json', false);
        request.send(null);
        var datatype = angular.fromJson(request.response);
        datatype.id = 4;
        datatype.label = datatype.label + "_Cloned";
        return [request.status, datatype, {}];
    });
    $httpBackend.whenDELETE('api/igdocuments/3').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/accounts/cuser').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/accounts/login').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET(/views\//).passThrough();
    $httpBackend.whenGET(/resources\//).passThrough();
    $httpBackend.whenGET('api/igdocuments?type=PRELOADED').respond(function (method, url, data, headers) {
        console.log("api/igdocuments/:type==>");
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/igdocuments?type=USER').respond(function (method, url, data, headers) {
        var userDocs = [];
        console.log('api/igdocuments?type=USER' + ' data=' + data);
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        userDocs.push(d);
        //        var request = new XMLHttpRequest();
        //        request.open('GET', '../../resources/igDocuments/igdocument-2.6.1-USER-ABC.json', false);
        //       request.send(null);
        //       var d = angular.fromJson(request.response);
        //       userDocs.push(d);
        //       var request = new XMLHttpRequest();
        //       request.open('GET', '../../resources/igDocuments/igdocument-2.6.1-USER-DEI.json', false);
        //      request.send(null);
        //      var d = angular.fromJson(request.response);
        //      userDocs.push(d);
        console.log('api/igdocuments?type=USER end');
        return [request.status, userDocs, {}];
    });
    $httpBackend.whenGET('api/igdocuments?type=SHARED').respond(function (method, url, data, headers) {
        var userDocs = [];
        console.log('api/igdocuments?type=SHARED' + ' data=' + data);
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igd-USER-2.5.1.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        userDocs.push(d);
        //        var request = new XMLHttpRequest();
        //        request.open('GET', '../../resources/igDocuments/igdocument-2.6.1-USER-ABC.json', false);
        //       request.send(null);
        //       var d = angular.fromJson(request.response);
        //       userDocs.push(d);
        //       var request = new XMLHttpRequest();
        //       request.open('GET', '../../resources/igDocuments/igdocument-2.6.1-USER-DEI.json', false);
        //      request.send(null);
        //      var d = angular.fromJson(request.response);
        //      userDocs.push(d);
        console.log('api/igdocuments?type=SHARED end');
        return [request.status, userDocs, {}];
    });
    $httpBackend.whenGET('api/igdocuments/findVersions').respond(function (method, url, data, headers) {
        console.log('api/igdocuments/hl7/findVersions');
        return [200, ["2.5.1", "2.6", "2.7"], {}];
    });
    $httpBackend.whenPOST('api/igdocuments/messageListByVersion').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/igdocuments/messageListByVersion start' + ' data=' + data);
        request.open('GET', '../../resources/igDocuments/mes-hl7Version-USER-1.0.json', false);
        //        request.open('GET', '../../resources/igDocuments/igdocument-2.7.5-USER-1.0.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        console.log('api/igdocuments/messageListByVersion end');
        return [request.status, d, {}];
        return [200, d, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/createIntegrationProfile').respond(function (method, url, data, headers) {
        console.log('api/igdocuments/hl7/createIntegrationProfile start' + ' data=' + data);
        var profile = null;
        var request = new XMLHttpRequest();
        //        request.open('GET', '../../resources/igDocuments/igdocument-2.7-HL7STANDARD-.json', false);
        request.open('GET', '../../resources/igDocuments/igdocument-2.7.5-USER-1.0.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        console.log('api/igdocuments/hl7/createIntegrationProfile end');
        return [request.status, profile, {}];
    });
    $httpBackend.whenPOST('api/igdocuments/updateIntegrationProfile').respond(function (method, url, data, headers) {
        console.log('api/igdocuments/hl7/updateIntegrationProfile start');
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igdocument-2.6.1-USER-ABC2.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        console.log('api/igdocuments/updateIntegrationProfile end');
        return [request.status, profile, {}];
    });
    $httpBackend.whenGET('api/igdocuments/config').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/config.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        console.log('api/igdocuments/config end');
        return [request.status, d, {}];
    });
    $httpBackend.whenGET('api/igdocuments/toc').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/igDocuments/igdocument-2.7-HL7STANDARD-.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        console.log('api/igdocuments/config end');
        return [request.status, d, {}];
    });
    $httpBackend.whenGET('api/appInfo').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/appInfo/appInfo.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        return [request.status, d, {}];
    });
    $httpBackend.whenGET('api/datatypes/565f3ab5d4c6e52cfd43b928').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/datatypes/datatype1.json', false);
        request.send(null);
        var datatype = angular.fromJson(request.response);
        return [request.status, datatype, {}];
    });
    //    $httpBackend.whenPOST('api/datatypes/save').respond(function (method, url, data, headers) {
    //        var request = new XMLHttpRequest();
    //        data.version = "4";
    //        return [200, data, {}];
    //    });
    //    $httpBackend.whenGET('api/segments/565f3ab5d4c6e52cfd43be67').respond(function (method, url, data, headers) {
    //        var request = new XMLHttpRequest();
    //        request.open('GET', '../../resources/segments/segment1.json', false);
    //        request.send(null);
    //        var segment1 = angular.fromJson(request.response);
    //        return [request.status, segment1, {}];
    //    });
    //
    //    $httpBackend.whenPOST('api/segments/save').respond(function (method, url, data, headers) {
    //        var request = new XMLHttpRequest();
    //        data.version = "4";
    //        return [200, data, {}];
    //    });
    $httpBackend.whenGET('api/datatypes/searchByLabel?search=X').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/datatypes/searchResults.json', false);
        request.send(null);
        var results = angular.fromJson(request.response);
        return [request.status, results, {}];
    });
    //    $httpBackend.whenGET('api/datatypes/1').respond(function (method, url, data, headers) {
    //        var request = new XMLHttpRequest();
    //        request.open('GET', '../../resources/datatypes/datatype1.json', false);
    //        request.send(null);
    //        var datatype = angular.fromJson(request.response);
    //        return [request.status, datatype, {}];
    //    });
    $httpBackend.whenPOST('api/datatype-library/getDataTypeLibraryByScope').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/datatype-library/getDataTypeLibraryByScope begin');
        var scope = data;
        var d = null;
        if (scope === 'MASTER') {
            request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
            //              request.open('GET', 'test/fixtures/datatypeLibraries/dtLib-MASTER-2.5.1.json', false);
            //              request.open('GET', '../../resources/datatypes/datatypes-MASTER.json', false);
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
});