/**
 * Created by haffo on 2/2/15.
 */

angular.module('igl').run(function($httpBackend,CustomProfileDataModel,PredefinedProfileDataModel) {

    $httpBackend.whenGET('/api/profiles?userId=2').respond(function(method, url, data, headers) {
         var profiles = CustomProfileDataModel.findAll();
        return [200, profiles, {}];
    });

    $httpBackend.whenPOST('/api/profiles').respond(function(method, url, data, headers) {
        var profiles =  CustomProfileDataModel.findAll();
        var profile = profiles[0];
        profile.id = 3;
        profile.metaData.name= " Cloned from "+ profile.metaData.name;
        return [200, profile, {}];
    });

    $httpBackend.whenGET('/api/profiles/preloaded').respond(function(method, url, data, headers) {
        var profiles =  PredefinedProfileDataModel.findAll();
        return [200, profiles, {}];
    });


    $httpBackend.whenGET('/api/profiles/2').respond(function(method, url, data, headers) {
        var profiles = CustomProfileDataModel.findAll();
        return [200, profiles[0], {}];
    });

    $httpBackend.whenDELETE('/api/profiles/2').respond(function(method, url, data, headers) {
         return [200,{}, {}];
    });

    $httpBackend.whenGET('/api/profiles/3').respond(function(method, url, data, headers) {
        var profiles = CustomProfileDataModel.findAll();
        return [200, profiles[0], {}];
    });


    $httpBackend.whenDELETE('/api/profiles/3').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });





    $httpBackend.whenGET(/views\//).passThrough();


});

