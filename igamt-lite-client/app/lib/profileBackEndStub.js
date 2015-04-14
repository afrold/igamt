/**
* Created by haffo on 2/2/15.
*/

angular.module('igl').run(function($httpBackend,$q,$http) {


    $httpBackend.whenGET('api/profiles').respond(function(method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profiles1.json', false);
        request.send(null);
        var profile =  angular.fromJson(request.response);
        return [request.status, profile, {}];

    });

    // clone and set id to 3
    $httpBackend.whenPOST('api/profiles/552014603004d0a9f09caf16/clone').respond(function(method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile =  angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf11";
        profile.preloaded = false;
        profile.metaData.name= " Cloned "+ profile.metaData.name;
        return [request.status, profile, {}];

     });

    $httpBackend.whenGET('api/accounts/cuser').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });




    // clone and set id to 3
    $httpBackend.whenPOST('api/profiles/552014603004d0a9f09caf11/clone').respond(function(method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile =  angular.fromJson(request.response);
        profile.id = "552014603004d0a9f09caf17";
        profile.preloaded = false;
        profile.metaData.name= " Cloned "+ profile.metaData.name;
        return [request.status, profile, {}];

    });

    $httpBackend.whenGET('api/profiles/cuser').respond(function(method, url, data, headers) {
         var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profiles2.json', false);
        request.send(null);
        var profiles = angular.fromJson(request.response);
        return [request.status, profiles, {}];
     });

    $httpBackend.whenGET('api/profiles/2').respond(function(method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 2;
        return [request.status, profile, {}];
     });

    $httpBackend.whenPOST('api/profiles/save').respond(function(method, url, d, headers) {
        console.log("Changes received:" + d.changes);
        return [200,{}, {}];
    });



    $httpBackend.whenDELETE('api/profiles/2').respond(function(method, url, data, headers) {
         return [200,{}, {}];
    });

    $httpBackend.whenPOST('api/profiles/1/delete').respond(function(method, url, d, headers) {
        return [200,{}, {}];
    });

    $httpBackend.whenPOST('api/profiles/2/delete').respond(function(method, url, d, headers) {
        return [200,{}, {}];
    });

    $httpBackend.whenPOST('api/profiles/3/delete').respond(function(method, url, d, headers) {
        return [200,{}, {}];
    });
    $httpBackend.whenPOST('api/profiles/4/delete').respond(function(method, url, d, headers) {
        return [200,{}, {}];
    });
    $httpBackend.whenGET('api/profiles/3').respond(function(method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 3;
        return [request.status, profile, {}];
    });


    $httpBackend.whenPOST('api/datatypes/4/delete').respond(function(method, url, d, headers) {
        return [200,{}, {}];
    });

    $httpBackend.whenPOST('api/datatypes/4/clone').respond(function(method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/datatype.json', false);
        request.send(null);
        var datatype =  angular.fromJson(request.response);
        datatype.id = 4;
        datatype.label = datatype.label + "_Cloned";
        return [request.status, datatype, {}];
    });

    $httpBackend.whenDELETE('api/profiles/3').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });

    $httpBackend.whenGET('api/accounts/cuser').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });

    $httpBackend.whenGET('api/accounts/login').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });



    $httpBackend.whenGET(/views\//).passThrough();

    $httpBackend.whenGET(/resources\//).passThrough();


});

