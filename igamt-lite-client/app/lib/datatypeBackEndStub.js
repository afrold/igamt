///**
// * Created by haffo on 2/2/15.
// */
//
//angular.module('igl').run(function($httpBackend,$q,$http) {
//
//
//
//    // clone and set id to 3
//    $httpBackend.whenPOST('/api/datatypes').respond(function(method, url, d, headers) {
//        var request = new XMLHttpRequest();
//        request.open('GET', '../../resources/profile1.json', false);
//        request.send(null);
//        var profile =  angular.fromJson(request.response);
//        profile.id = 3;
//        profile.preloaded = false;
//        profile.metaData.name= " Cloned "+ profile.metaData.name;
//        return [request.status, profile, {}];
//
//     });
//
//    $httpBackend.whenGET(/views\//).passThrough();
//
//    $httpBackend.whenGET(/resources\//).passThrough();
//
//
//});
//
