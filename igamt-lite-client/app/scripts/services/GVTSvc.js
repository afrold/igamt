/**
 * Created by haffo on 5/4/15.
 */

angular.module('igl').factory('GVTSvc',
    ['$q','$modal', '$rootScope', 'StorageService','base64','$http',function ($q,$modal,$rootScope,StorageService,base64,$http) {

        var svc = this;
        svc.url = 'http://localhost:8081/gvt/';
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
            httpHeaders['Authorization'] = 'Basic ' + base64.encode(username + ':' + password);
            $http.get(svc.url+ 'api/accounts/login', {headers:httpHeaders}).then(function (re) {
                 delay.resolve(httpHeaders.common['Authorization']);
            }, function(er){
                delay.reject(er);
            });
            return delay.promise;
        };


        return svc;
    }]);

