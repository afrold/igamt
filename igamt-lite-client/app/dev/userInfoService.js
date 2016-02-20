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
    function($cookieStore, userLoaderService) {
        var currentUser = {
        		username : "gcr46",
        		accountId : 45,
        		authorities : []
        };
       var supervisor = false,
        author = false,
        admin = false,
        id = null,
        username = '',
        fullName = '';

        //console.log("USER ID=", $cookieStore.get('userID'));
       
        var loadFromCookie = function() {
            //console.log("UserID=", $cookieStore.get('userID'));

            id = $cookieStore.get('userID');
            username = $cookieStore.get('username');
            author = $cookieStore.get('author');
            supervisor = $cookieStore.get('supervisor');
            admin = $cookieStore.get('admin');
        };

        var saveToCookie = function() {
            $cookieStore.put('accountID', id);
            $cookieStore.put('username', username);
            $cookieStore.put('author', author);
            $cookieStore.put('supervisor', supervisor);
            $cookieStore.put('admin', admin);
        };

        var clearCookie = function() {
            $cookieStore.remove('accountID');
            $cookieStore.remove('username');
            $cookieStore.remove('author');
            $cookieStore.remove('supervisor');
            $cookieStore.remove('admin');
            $cookieStore.remove('hthd');
        };

        var saveHthd = function(header) {
            $cookieStore.put('hthd', header);
        };

        var getHthd = function(header) {
            return $cookieStore.get('hthd');
        };

        var hasCookieInfo =  function() {
            if ( $cookieStore.get('username') === '' ) {
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

//        var isAuthorizedVendor = function() {
//            return authorizedVendor;
//        };
//
//        var isCustomer = function() {
//            return (author || authorizedVendor);
//        };

        var isSupervisor = function() {
            return supervisor;
        };

        var isPending = function() {
            return isAuthenticated() && currentUser != null ? currentUser.pending: false;
        };

        var isAuthenticated = function() {
//        	if ( angular.isObject(currentUser) && currentUser.authenticated === true) {
//                return true;
//            }
//            else {
//                return false;
//            }
        	return true;
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
