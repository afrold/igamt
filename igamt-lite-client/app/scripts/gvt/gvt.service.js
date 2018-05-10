/**
 * Created by haffo on 5/4/15.
 */

angular.module('igl').factory('GVTSvc',
    ['$q','$modal', '$rootScope', 'StorageService','base64','$http',function ($q,$modal,$rootScope,StorageService,base64,$http) {

        var svc = this;

 //        var Email = $resource(svc.url+ 'api/sooa/emails/:email', {email: '@email'});
//
//        svc.userExists = function(email) {
//            var delay = $q.defer();
//            var emailToCheck = new Email({email:email});
//            emailToCheck.$get(function() {
//                delay.resolve(emailToCheck.text);
//            }, function(error) {
//                delay.reject(error.data);
//             });
//            return delay.promise;
//        };

        svc.login = function(username, password,targetUrl) {
            var delay = $q.defer();
            var httpHeaders = {};
            httpHeaders['Accept'] = 'application/json';
            var auth =  base64.encode(username + ':' + password);
            httpHeaders['target-auth'] = 'Basic ' + auth;
            httpHeaders['target-url'] = targetUrl;
            $http.get('api/connect/login', {headers:httpHeaders}).then(function (res) {
                delay.resolve(auth);
            }, function(er){
                delay.reject(er);
            });
            return delay.promise;
        };

        svc.createDomain = function(username, password,targetUrl,key, name) {
            var delay = $q.defer();
            var httpHeaders = {};
            httpHeaders['Accept'] = 'application/json';
            var auth =  base64.encode(username + ':' + password);
            httpHeaders['target-auth'] = 'Basic ' + auth;
            httpHeaders['target-url'] = targetUrl;
            $http.post('api/connect/createDomain', {headers:httpHeaders,params:{'key':key,'name':name}}).then(function (res) {
                delay.resolve(auth);
            }, function(er){
                delay.reject(er);
            });
            return delay.promise;
        };


        svc.exportToGVT = function(id,mids, auth,targetUrl,targetDomain) {
             var httpHeaders = {};
            httpHeaders['target-auth'] = auth;
            httpHeaders['target-url'] = targetUrl;
            httpHeaders['target-domain'] = targetDomain;
            return $http.post('api/igdocuments/' + id + '/connect/messages',mids,{headers:httpHeaders});
        };

        svc.exportToGVTForCompositeProfile = function(id, cids, auth,targetUrl,targetDomain) {
            var httpHeaders = {};
            httpHeaders['target-auth'] = auth;
            httpHeaders['target-url'] = targetUrl;
            httpHeaders['target-domain'] = targetDomain;
            return $http.post('api/igdocuments/' + id + '/connect/composites',cids,{headers:httpHeaders});
        };

        svc.getDomains = function(targetUrl) {
            var delay = $q.defer();
            var httpHeaders = {};
            httpHeaders['target-url'] = targetUrl;
            $http.get("api/connect/domains",{headers:httpHeaders}).then(function (result) {
                var data = angular.fromJson(result.data);
                delay.resolve(data);
            }, function(er){
                delay.reject(er);
            });
            return delay.promise;
        };


        return svc;
    }]);

