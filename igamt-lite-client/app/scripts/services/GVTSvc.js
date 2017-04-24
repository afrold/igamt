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

        svc.login = function(username, password) {
            var delay = $q.defer();
            var httpHeaders = {};
            httpHeaders['Accept'] = 'application/json';
            var auth =  base64.encode(username + ':' + password);
            httpHeaders['gvt-auth'] = 'Basic ' + auth;
            $http.get('api/gvt/login', {headers:httpHeaders}).then(function (res) {
                delay.resolve(auth);
            }, function(er){
                delay.reject(er);
            });
            return delay.promise;
        };

        svc.exportToGVT = function(id,mids, auth) {
             var httpHeaders = {};
            httpHeaders['gvt-auth'] = auth;
            return $http.post('api/igdocuments/' + id + '/export/gvt',mids,{headers:httpHeaders});
        };

        svc.exportToGVTForCompositeProfile = function(id, cids, auth) {
            var httpHeaders = {};
            httpHeaders['gvt-auth'] = auth;
            return $http.post('api/igdocuments/' + id + '/export/gvt/composite',cids,{headers:httpHeaders});
        };

        return svc;
    }]);

