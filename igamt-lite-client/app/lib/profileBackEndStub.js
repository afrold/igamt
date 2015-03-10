/**
 * Created by haffo on 2/2/15.
 */

angular.module('igl').run(function($httpBackend,$q,$http) {


    $httpBackend.whenGET('/api/profiles?userId=2').respond(function(method, url, data, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/userProfiles.json', false);
        request.send(null);
        var profile =  angular.fromJson(request.response);
        return [request.status, profile, {}];

    });

    // clone and set id to 3
    $httpBackend.whenPOST('/api/profiles').respond(function(method, url, d, headers) {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile.json', false);
        request.send(null);
        var profile =  angular.fromJson(request.response);
        profile.id = 3;
        profile.preloaded = false;
        profile.metaData.name= " Cloned "+ profile.metaData.name;
        return [request.status, profile, {}];

     });

    $httpBackend.whenGET('/api/profiles/preloaded').respond(function(method, url, data, headers) {
         var request = new XMLHttpRequest();
        request.open('GET', '../../resources/preloadedProfiles.json', false);
        request.send(null);
        var profiles = angular.fromJson(request.response);
        return [request.status, profiles, {}];
     });

    $httpBackend.whenGET('/api/profiles/2').respond(function(method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile1.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 2;
        return [request.status, profile, {}];

     });

    $httpBackend.whenDELETE('/api/profiles/2').respond(function(method, url, data, headers) {
         return [200,{}, {}];
    });

    $httpBackend.whenGET('/api/profiles/3').respond(function(method, url, data, headers) {
        var profile = null;
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/profile.json', false);
        request.send(null);
        var profile = angular.fromJson(request.response);
        profile.id = 3;
        return [request.status, profile, {}];
      });


    $httpBackend.whenDELETE('/api/profiles/3').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });





    $httpBackend.whenGET(/views\//).passThrough();

    $httpBackend.whenGET(/resources\//).passThrough();


});

