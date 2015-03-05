/**
 * Created by haffo on 2/2/15.
 */

angular.module('igl').run(function($httpBackend,$q,$http) {


    $httpBackend.whenGET('/api/profiles?userId=2').respond(function(method, url, data, headers) {
//         var profiles = CustomDataModel.findAllProfiles();
//        return [200, profiles, {}];
        var delay = $q.defer();
        $http.get('../../resources/userProfiles.json').then(
            function (object) {
                delay.resolve(angular.fromJson(object.data));
            },
            function (response) {
                delay.reject(response.data);
            }
        );
        return delay.promise;


    });

    // clone and set id to 3
    $httpBackend.whenPOST('/api/profiles').respond(function(method, url, d, headers) {
        var delay = $q.defer();
        $http.get('../../resources/profile.json').then(
            function (object) {
                var profile =  angular.fromJson(object.data);
                profile.id = 3;
                profile.preloaded = false;
                profile.metaData.name= " Cloned "+ profile.metaData.name;
                delay.resolve(profile);
            },
            function (response) {
                delay.reject(response.data);
            }
        );
        return delay.promise;
    });

    $httpBackend.whenGET('/api/profiles/preloaded').respond(function(method, url, data, headers) {
        var delay = $q.defer();
        $http.get('../../resources/preloadedProfiles.json').then(
            function (object) {
                delay.resolve(angular.fromJson(object.data));
            },
            function (response) {
                delay.reject(response.data);
            }
        );
        return delay.promise;
    });


    $httpBackend.whenGET('/api/profiles/2').respond(function(method, url, data, headers) {
        var delay = $q.defer();
        $http.get('../../resources/profile.json').then(
            function (object) {
                var profile = angular.fromJson(object.data);
                profile.id = 2;
                delay.resolve(profile);
            },
            function (response) {
                delay.reject(response.data);
            }
        );
        return delay.promise;
    });

    $httpBackend.whenDELETE('/api/profiles/2').respond(function(method, url, data, headers) {
         return [200,{}, {}];
    });

    $httpBackend.whenGET('/api/profiles/3').respond(function(method, url, data, headers) {
        var delay = $q.defer();
        $http.get('../../resources/profile.json').then(
            function (object) {
                var profile = angular.fromJson(object.data);
                profile.id = 3;
                delay.resolve(profile);
            },
            function (response) {
                delay.reject(response.data);
            }
        );
        return delay.promise;
    });


    $httpBackend.whenDELETE('/api/profiles/3').respond(function(method, url, data, headers) {
        return [200,{}, {}];
    });





    $httpBackend.whenGET(/views\//).passThrough();

    $httpBackend.whenGET(/resources\//).passThrough();


});

