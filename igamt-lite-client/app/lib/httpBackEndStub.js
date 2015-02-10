/**
 * Created by haffo on 2/2/15.
 */

angular.module('igl').run(function($httpBackend,CustomProfileDataModel,PredefinedProfileDataModel) {

    $httpBackend.whenGET('/api/v1/profiles/customProfiles').respond(function(method, url, data, headers) {
         var profiles = CustomProfileDataModel.findAll();
        return [200, profiles, {}];
    });


    $httpBackend.whenGET('/api/v1/profiles/predefinedProfiles').respond(function(method, url, data, headers) {
        var profiles =  PredefinedProfileDataModel.findAll();
        return [200, profiles, {}];
    });

    $httpBackend.whenGET(/views\//).passThrough();


});

