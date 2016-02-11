///**
// * Created by haffo on 2/2/15.
// */

angular.module('igl').run(function ($httpBackend, $q, $http) {

    $httpBackend.whenGET('api/igdocuments').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        return [request.status, profile, {}];
    });

    $httpBackend.whenGET('api/shortaccounts?filter=accountType::author').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/shortaccounts.json', false);
        request.send(null);
        var profile = request.response;
        return [request.status, profile, {}];
    });

    $httpBackend.whenPOST('api/accounts/1/userpasswordchange').respond(function (method, url, data, headers) {
        return [200, {type: 'success',
            text: 'accountPasswordReset',
            resourceId: '1',
            manualHandle: "false"}, {}];
    });

    $httpBackend.whenPOST('api/accounts/2/userpasswordchange').respond(function (method, url, data, headers) {
        return [200, {type: 'success',
            text: 'invalidPassword',
            resourceId: '2',
            manualHandle: "false"}, {}];
    });


    $httpBackend.whenPOST('api/accounts/1/approveaccount').respond(function (method, url, data, headers) {
        return [200, {type: 'success',
            text: 'accountApproved',
            resourceId: '1',
            manualHandle: "false"}, {}];
    });

    $httpBackend.whenPOST('api/accounts/2/approveaccount').respond(function (method, url, data, headers) {
        return [200, {type: 'success',
            text: 'accountIsNotPending',
            resourceId: '2',
            manualHandle: "false"}, {}];
    });


    $httpBackend.whenPOST('api/accounts/1/suspendaccount').respond(function (method, url, data, headers) {
        return [200, {type: 'success',
            text: 'accountSuspended',
            resourceId: '1',
            manualHandle: "false"}, {}];
    });


    // clone and set id to 3
    $httpBackend.whenPOST('api/igdocuments/552014603004d0a9f09caf16/clone').respond(function (method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf11";
        profile.preloaded = false;
        profile.metaData.name = " Cloned " + profile.metaData.name;
        return [request.status, profile, {}];

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
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profiles = angular.fromJson(request.response);
        return [request.status, [profiles], {}];
    });

    $httpBackend.whenGET('api/igdocuments/2').respond(function (method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
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
		console.log("api/igdocuments/:type==>");
    return [200, {}, {}];
    });

    
    $httpBackend.whenGET('api/igdocuments/findVersions').respond(function (method, url, data, headers) {
    	console.log('api/igdocuments/hl7/findVersions');
        return [200, ["2.3","2.31","2.4","2.5","2.51","2.6","2.7"], {}];
    });
    
    $httpBackend.whenPOST('api/igdocuments/messageListByVersion').respond(function (method, url, data, headers) {
        var msgList = [["5665cee2d4c613e7b531be55", "P11", "DFT_P11", "Detail financial transactions"], 
         ["5665cee2d4c613e7b531b7ba", "A24", "ADT_A24", "ADT messagee"], 
         ["5665cee2d4c613e7b531be18", "I08", "RPA_I08", "Request patient authorization"],
        ["5665cee2d4c613e7b531be4e", "Q16", "QSB_Q16", "Create subscription"],
        ["5665cee2d4c613e7b531bbbb", "B08", "PMU_B08", "Add personnel record"]]
        return [200, msgList, {}];
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
        request.open('GET', '../../igDocuments/igdocument-2.7-HL7STANDARD-.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        console.log('api/igdocuments/createIntegrationProfile end');
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

});

