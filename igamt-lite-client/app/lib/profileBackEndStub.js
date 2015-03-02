/**
 * Created by haffo on 2/2/15.
 */

angular.module('igl').run(function($httpBackend,CustomDataModel,PredefinedDataModel,$q) {


    $httpBackend.whenGET('/api/profiles?userId=2').respond(function(method, url, data, headers) {
         var profiles = CustomDataModel.findAllProfiles();
        return [200, profiles, {}];
    });

    // clone and set id to 3
    $httpBackend.whenPOST('/api/profiles').respond(function(method, url, d, headers) {
        var data = angular.fromJson(d);
        var pId = data.targetId;
        var preloaded = data.preloaded;
        var profile = null;
        if(!preloaded){
           profile = CustomDataModel.findOneProfile(pId);
        }else{
           profile = PredefinedDataModel.findOneProfile(pId);
        }
        var res = angular.copy(profile);
        res.id = 3;
        res.preloaded = false;
        res.metaData.name= " Cloned "+ profile.metaData.name;
        return [200, res, {}];
    });

    $httpBackend.whenGET('/api/profiles/preloaded').respond(function(method, url, data, headers) {
        var profiles =  PredefinedDataModel.findAllProfiles();
        return [200, profiles, {}];
    });


    $httpBackend.whenGET('/api/profiles/2').respond(function(method, url, data, headers) {
        var profile =  CustomDataModel.findOneProfile(2);
        return [200, profile, {}];
    });

    $httpBackend.whenDELETE('/api/profiles/2').respond(function(method, url, data, headers) {
         return [200,{}, {}];
    });

    $httpBackend.whenGET('/api/profiles/3').respond(function(method, url, data, headers) {
        var delay = $q.defer();
        CustomDataModel.findOneFullProfile(3).then(function(response){
            delay.resolve([200, response, {}]);
        },function(error){
            delay.reject(error);
        });
        return delay;
    });


    $httpBackend.whenDELETE('/api/profiles/3').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });





    $httpBackend.whenGET(/views\//).passThrough();

    $httpBackend.whenGET('../../resources/profile.json').passThrough();


});

