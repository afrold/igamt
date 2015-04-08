'use strict';

angular.module('igl').controller('MainCtrl', ['$scope', '$rootScope', 'i18n', '$location', 'userInfoService', '$modal',
function ($scope, $rootScope, i18n, $location, userInfoService, $modal) {
    //This line fetches the info from the server if the user is currently logged in.
    //If success, the app is updated according to the role.
    userInfoService.loadFromServer();

    $scope.language = function () {
        return i18n.language;
    };

    $scope.setLanguage = function (lang) {
        i18n.setLanguage(lang);
    };

    $scope.activeWhen = function (value) {
        return value ? 'active' : '';
    };

    $scope.activeIfInList = function(value, pathsList) {
        var found = false;
        if ( angular.isArray(pathsList) === false ) {
            return '';
        }
        var i = 0;
        while ( (i < pathsList.length) && (found === false)) {
            if ( pathsList[i] === value ) {
                return 'active';
            }
            i++;
        }
        return '';
    };

    $scope.path = function () {
        return $location.url();
    };

    $scope.login = function () {
//        console.log("in login");
        $scope.$emit('event:loginRequest', $scope.username, $scope.password);
    };
    
    $scope.loginReq = function () {
//        console.log("in loginReq");
        if ($rootScope.loginMessage()){
            $rootScope.loginMessage().text="";
            $rootScope.loginMessage().show=false;
        }
        $scope.$emit('event:loginRequired');
    };

    $scope.logout = function () {
        userInfoService.setCurrentUser(null);
        $scope.username = $scope.password = null;
        $scope.$emit('event:logoutRequest');
        $location.url('/home');
    };

    $scope.cancel = function () {
        $scope.$emit('event:loginCancel');
    };

    $scope.isAuthenticated = function() {
        return userInfoService.isAuthenticated();
    };

    $scope.isSupervisor = function() {
        return userInfoService.isSupervisor();
    };

    $scope.isVendor = function() {
        return userInfoService.isAuthorizedVendor();
    };

    $scope.isProvider = function() {
        return userInfoService.isProvider();
    };

    $scope.isCustomer = function() {
        return userInfoService.isCustomer();
    };

    $scope.isAdmin = function() {
        return userInfoService.isAdmin();
    };

    $scope.getRoleAsString = function() {
        if ( $scope.isVendor() === true ) { return 'Authorized Vendor'; }
        if ( $scope.isProvider() === true ) { return 'Provider'; }
        if ( $scope.isSupervisor() === true ) { return 'Supervisor'; }
        if ( $scope.isAdmin() === true ) { return 'Admin'; }
        return 'undefined';
    };

    $scope.getUsername = function() {
        if ( userInfoService.isAuthenticated() === true ) {
            return userInfoService.getUsername();
        }
        return '';
    };

    $rootScope.showLoginDialog = function(username, password) {
        if ( $rootScope.loginDialogShown === false ) {
//            var loginDialogOptions = {
//                backdrop: true,
//                keyboard: true,
//                backdropClick: false,
//                controller: 'LoginCtrl',
//                templateUrl: 'views/account/login.html'
//            };

            $rootScope.loginDialogShown = true;

            var dlg = $modal.open({
                backdrop: true,
                keyboard: true,
                backdropClick: false,
                controller: 'LoginCtrl',
                size:'lg',
                templateUrl: 'views/account/login.html',
                resolve: {
                    user: function() {return {username:$scope.username, password:$scope.password};}
                }
            });

            dlg.result.then(function (result) {
                $rootScope.loginDialogShown = false;
                if(result) {
                    $scope.username = result.username;
                    $scope.password = result.password;
                    $scope.login();
                } else {
                    $scope.cancel();
                }
            });


//            $dialog.dialog(angular.extend(loginDialogOptions, {
//                resolve: {
//                    user: function() {return {username:$scope.username, password:$scope.password};}
//                }
//            }))
//            .open()
//            .then(function(result) {
//                $rootScope.loginDialogShown = false;
//                if(result) {
//                    $scope.username = result.username;
//                    $scope.password = result.password;
//                    $scope.login();
//                } else {
//                    $scope.cancel();
//                }
//            });
        }
    };

    $scope.checkForIE = function() {
        var BrowserDetect = {
            init: function () {
                this.browser = this.searchString(this.dataBrowser) || 'An unknown browser';
                this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || 'an unknown version';
                this.OS = this.searchString(this.dataOS) || 'an unknown OS';
            },
            searchString: function (data) {
                for (var i=0;i<data.length;i++) {
                    var dataString = data[i].string;
                    var dataProp = data[i].prop;
                    this.versionSearchString = data[i].versionSearch || data[i].identity;
                    if (dataString) {
                        if (dataString.indexOf(data[i].subString) !== -1) {
                            return data[i].identity;
                        }
                    }
                    else if (dataProp) {
                        return data[i].identity;
                    }
                }
            },
            searchVersion: function (dataString) {
                var index = dataString.indexOf(this.versionSearchString);
                if (index === -1) { return; }
                return parseFloat(dataString.substring(index+this.versionSearchString.length+1));
            },
            dataBrowser: [
                {
                    string: navigator.userAgent,
                    subString: 'Chrome',
                    identity: 'Chrome'
                },
                {   string: navigator.userAgent,
                    subString: 'OmniWeb',
                    versionSearch: 'OmniWeb/',
                    identity: 'OmniWeb'
                },
                {
                    string: navigator.vendor,
                    subString: 'Apple',
                    identity: 'Safari',
                    versionSearch: 'Version'
                },
                {
                    prop: window.opera,
                    identity: 'Opera',
                    versionSearch: 'Version'
                },
                {
                    string: navigator.vendor,
                    subString: 'iCab',
                    identity: 'iCab'
                },
                {
                    string: navigator.vendor,
                    subString: 'KDE',
                    identity: 'Konqueror'
                },
                {
                    string: navigator.userAgent,
                    subString: 'Firefox',
                    identity: 'Firefox'
                },
                {
                    string: navigator.vendor,
                    subString: 'Camino',
                    identity: 'Camino'
                },
                {       // for newer Netscapes (6+)
                    string: navigator.userAgent,
                    subString: 'Netscape',
                    identity: 'Netscape'
                },
                {
                    string: navigator.userAgent,
                    subString: 'MSIE',
                    identity: 'Explorer',
                    versionSearch: 'MSIE'
                },
                {
                    string: navigator.userAgent,
                    subString: 'Gecko',
                    identity: 'Mozilla',
                    versionSearch: 'rv'
                },
                {       // for older Netscapes (4-)
                    string: navigator.userAgent,
                    subString: 'Mozilla',
                    identity: 'Netscape',
                    versionSearch: 'Mozilla'
                }
            ],
            dataOS : [
                {
                    string: navigator.platform,
                    subString: 'Win',
                    identity: 'Windows'
                },
                {
                    string: navigator.platform,
                    subString: 'Mac',
                    identity: 'Mac'
                },
                {
                    string: navigator.userAgent,
                    subString: 'iPhone',
                    identity: 'iPhone/iPod'
                },
                {
                    string: navigator.platform,
                    subString: 'Linux',
                    identity: 'Linux'
                }
            ]

        };
        BrowserDetect.init();

        if ( BrowserDetect.browser === 'Explorer' ) {
            var title = 'You are using Internet Explorer';
            var msg = 'This site is not yet optimized with Internet Explorer. For the best user experience, please use Chrome, Firefox or Safari. Thank you for your patience.';
            var btns = [{result:'ok', label: 'OK', cssClass: 'btn'}];

            //$dialog.messageBox(title, msg, btns).open();



        }
    };

    //We check for IE when the user load the main page.
    //TODO: Check only once.
//    $scope.checkForIE();

}]);

angular.module('igl').controller('LoginCtrl', ['$scope', '$modalInstance', 'user', function($scope, $modalInstance, user) {
    $scope.user = user;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.login = function() {
//        console.log("logging in...");
        $modalInstance.close($scope.user);
    };
}]);
