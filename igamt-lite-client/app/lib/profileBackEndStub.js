///**
// * Created by haffo on 2/2/15.
// */

angular.module('igl').run(function ($httpBackend, $q, $http) {

    $httpBackend.whenGET('api/profiles').respond(function (method, url, data, headers) {
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
    $httpBackend.whenPOST('api/profiles/552014603004d0a9f09caf16/clone').respond(function (method, url, d, headers) {
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
    $httpBackend.whenPOST('api/profiles/552014603004d0a9f09caf11/clone').respond(function (method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf17";
        profile.preloaded = false;
        profile.metaData.name = " Cloned " + profile.metaData.name;
        return [request.status, profile, {}];

    });

    $httpBackend.whenGET('api/profiles/cuser').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profiles = angular.fromJson(request.response);
        return [request.status, profiles, {}];
    });

    $httpBackend.whenGET('api/profiles/2').respond(function (method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile4.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 2;
        return [request.status, profile, {}];
    });

    $httpBackend.whenPOST('api/profiles/save').respond(function (method, url, d, headers) {
        console.log("Changes received:" + d.changes);
        return [200, {}, {}];
    });


    $httpBackend.whenDELETE('api/profiles/2').respond(function (method, url, data, headers) {
        return [200, {}, {}];
    });

    $httpBackend.whenPOST('api/profiles/1/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });

    $httpBackend.whenPOST('api/profiles/2/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });

    $httpBackend.whenPOST('api/profiles/3/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenPOST('api/profiles/4/delete').respond(function (method, url, d, headers) {
        return [200, {}, {}];
    });
    $httpBackend.whenGET('api/profiles/3').respond(function (method, url, data, headers) {
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

    $httpBackend.whenDELETE('api/profiles/3').respond(function (method, url, data, headers) {
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

    $httpBackend.whenGET('api/profiles/hl7/findVersions').respond(function (method, url, data, headers) {
    	console.log('api/profiles/hl7/findVersions');
        return [200, ["2.3","2.31","2.4","2.5","2.51","2.6","2.7"], {}];
    });

    $httpBackend.whenGET('api/profiles/hl7/messageListByVersion/2.7/').respond(function (method, url, data, headers) {
        var msgList = [["P11", "P11", "", "DFT", "Detail financial transactions"],
         ["O07", "O07", "", "OMN", "Non-stock requisition order message"],
         ["I08", "I08", "", "RQA", "Request patient authorization"]]
        return [200, msgList, {}];
    });

    $httpBackend.whenPOST('api/profiles/hl7/createIntegrationProfile').respond(function (method, url, data, headers) {
    	console.log('api/profiles/hl7/createIntegrationProfile start');
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profiles.3/profile-2.7.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        console.log('api/profiles/hl7/createIntegrationProfile end');
        return [request.status, profile, {}];
    });

    $httpBackend.whenPOST('api/profiles/hl7/updateIntegrationProfile').respond(function (method, url, data, headers) {
    	console.log('api/profiles/hl7/updateIntegrationProfile start');
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profiles.3.3/profile-2.7.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        console.log('api/profiles/hl7/createIntegrationProfile end');
        return [request.status, profile, {}];
    });

    $httpBackend.whenGET('api/profiles/config').respond(function (method, url, data, headers) {
         var request = new XMLHttpRequest();
        request.open('GET', '../../resources/config.json', false);
        request.send(null);
        var d = angular.fromJson(request.response);
        console.log('api/profiles/config end');
        return [request.status, d, {}];
    });

});

