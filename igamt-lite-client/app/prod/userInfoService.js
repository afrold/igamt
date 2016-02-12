'use strict';

angular.module('igl').factory('userInfo', ['$resource',
    function ($resource) {
        return $resource('igamt/api/accounts/cuser');
    }
]);

angular.module('igl').factory('userLoaderService', ['userInfo', '$q',
    function (userInfo, $q) {
        var load = function() {
            var delay = $q.defer();
            userInfo.get({},
                function(theUserInfo) {
                    delay.resolve(theUserInfo);
                },
                function() {
                    delay.reject('Unable to fetch user info');
                }
            );
            return delay.promise;
        };
        return {
            load: load
        };
    }
]);

angular.module('igl').factory('userInfoService', ['$cookieStore', 'userLoaderService',
    function(StorageService, userLoaderService) {
        var currentUser = null;
       var supervisor = false,
        author = false,
        admin = false,
        id = null,
        username = '',
        fullName = '';

        //console.log("USER ID=", StorageService.get('userID'));
       
        var loadFromCookie = function() {
            //console.log("UserID=", StorageService.get('userID'));

            id = StorageService.get('userID');
            username = StorageService.get('username');
            author = StorageService.get('author');
            supervisor = StorageService.get('supervisor');
            admin = StorageService.get('admin');
        };

        var saveToCookie = function() {
            StorageService.set('accountID', id);
            StorageService.set('username', username);
            StorageService.set('author', author);
            StorageService.set('supervisor', supervisor);
            StorageService.set('admin', admin);
            StorageService.set('fullName', fullName);

        };

        var clearCookie = function() {
            StorageService.remove('accountID');
            StorageService.remove('username');
            StorageService.remove('author');
            StorageService.remove('supervisor');
            StorageService.remove('admin');
            StorageService.remove('hthd');
            StorageService.remove('fullName');

        };

        var saveHthd = function(header) {
            StorageService.set('hthd', header);
        };

        var getHthd = function(header) {
            return StorageService.get('hthd');
        };

        var hasCookieInfo =  function() {
            if ( StorageService.get('username') === '' ) {
                return false;
            }
            else {
                return true;
            }
        };

        var getAccountID = function() {
            if ( isAuthenticated() ) {
                return currentUser.accountId.toString();
            }
            return '0';
        };

        var isAdmin = function() {
            return admin;
        };

        var isAuthor = function() {
            return author;
        };

        var isSupervisor = function() {
            return supervisor;
        };

        var isPending = function() {
            return isAuthenticated() && currentUser != null ? currentUser.pending: false;
        };

        var isAuthenticated = function() {
        	if ( angular.isObject(currentUser) && currentUser.authenticated === true) {
                return true;
            }
            else {
                return false;
            }
//        	return true;
        };

        var loadFromServer = function() {
            if ( !isAuthenticated() ) {
                userLoaderService.load().then(setCurrentUser);
            }
        };

        var setCurrentUser = function(newUser) {
            currentUser = newUser;
            //console.log("NewUser=", newUser);
            if ( angular.isObject(currentUser) ) {
//                console.log("currentUser -> "+currentUser);
                username = currentUser.username;
                fullName = currentUser.fullName;
                id = currentUser.accountId;
                if ( angular.isArray(currentUser.authorities)) {
                    angular.forEach(currentUser.authorities, function(value, key){
                        switch(value.authority)
                        {
                        case 'user':
                            //console.log("user found");
                            break;
                        case 'admin':
                            admin = true;
                            //console.log("admin found");
                            break;
                        case 'author':
                            author = true;
                            //console.log("author found");
                            break;
//                        case 'authorizedVendor':
//                            authorizedVendor = true;
//                            //console.log("authorizedVendor found");
//                            break;
                        case 'supervisor':
                            supervisor = true;
                            //console.log("supervisor found");
                            break;
                        default:
                            //console.log("default");
                        }
                    });
                }
                //saveToCookie();
            }
            else {
                supervisor = false;
                author = false;
                admin = false;
                //clearCookie();
            }
        };

        var getUsername = function() {
            return username;
        };

        var getFullName = function() {
            return fullName;
        };

        return {
            saveHthd: saveHthd,
            getHthd: getHthd,
            hasCookieInfo: hasCookieInfo,
            loadFromCookie: loadFromCookie,
            getAccountID: getAccountID,
            isAdmin: isAdmin,
            isAuthor: isAuthor,
            isAuthenticated: isAuthenticated,
            isPending: isPending,
            isSupervisor: isSupervisor,
            setCurrentUser: setCurrentUser,
            loadFromServer: loadFromServer,
            getUsername: getUsername,
            getFullName: getFullName
       };
    }
]);
