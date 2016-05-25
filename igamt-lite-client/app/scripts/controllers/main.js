'use strict';

angular.module('igl').controller('MainCtrl', ['$scope', '$rootScope', 'i18n', '$location', 'userInfoService', '$modal', 'Restangular', '$filter', 'base64', '$http', 'Idle', 'notifications', 'IdleService', 'AutoSaveService', 'StorageService', 'ViewSettings', 'DatatypeService', 'ElementUtils',
    function ($scope, $rootScope, i18n, $location, userInfoService, $modal, Restangular, $filter, base64, $http, Idle, notifications, IdleService, AutoSaveService, StorageService, ViewSettings, DatatypeService, ElementUtils) {
        //This line fetches the info from the server if the user is currently logged in.
        //If success, the app is updated according to the role.
        userInfoService.loadFromServer();
        $rootScope.loginDialog = null;

        $rootScope.csWidth = null;
        $rootScope.predWidth = null;
        $rootScope.tableWidth = null;
        $rootScope.commentWidth = null;
        $scope.viewSettings = ViewSettings;

        $scope.language = function () {
            return i18n.language;
        };

        $scope.setLanguage = function (lang) {
            i18n.setLanguage(lang);
        };

        $scope.activeWhen = function (value) {
            return value ? 'active' : '';
        };

        $scope.activeIfInList = function (value, pathsList) {
            var found = false;
            if (angular.isArray(pathsList) === false) {
                return '';
            }
            var i = 0;
            while ((i < pathsList.length) && (found === false)) {
                if (pathsList[i] === value) {
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
            if ($rootScope.loginMessage()) {
                $rootScope.loginMessage().text = "";
                $rootScope.loginMessage().show = false;
            }
            $scope.$emit('event:loginRequired');
        };

        $scope.logout = function () {
            if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
                var modalInstance = $modal.open({
                    templateUrl: 'ConfirmLogout.html',
                    controller: 'ConfirmLogoutCtrl'
                });
                modalInstance.result.then(function () {
                    $scope.execLogout();
                }, function () {
                });
            } else {
                $scope.execLogout();
            }
        };

        $scope.execLogout = function () {
            userInfoService.setCurrentUser(null);
            $scope.username = $scope.password = null;
            $scope.$emit('event:logoutRequest');
            StorageService.remove(StorageService.IG_DOCUMENT);
            $rootScope.initMaps();
            $rootScope.igdocument = null;
            AutoSaveService.stop();
            $location.url('/ig');
        };

        $scope.cancel = function () {
            $scope.$emit('event:loginCancel');
        };

        $scope.isAuthenticated = function () {
            return userInfoService.isAuthenticated();
        };

        $scope.isPending = function () {
            return userInfoService.isPending();
        };


        $scope.isSupervisor = function () {
            return userInfoService.isSupervisor();
        };

        $scope.isVendor = function () {
            return userInfoService.isAuthorizedVendor();
        };

        $scope.isAuthor = function () {
            return userInfoService.isAuthor();
        };

        $scope.isCustomer = function () {
            return userInfoService.isCustomer();
        };

        $scope.isAdmin = function () {
            return userInfoService.isAdmin();
        };

        $scope.getRoleAsString = function () {
            if ($scope.isAuthor() === true) {
                return 'author';
            }
            if ($scope.isSupervisor() === true) {
                return 'Supervisor';
            }
            if ($scope.isAdmin() === true) {
                return 'Admin';
            }
            return 'undefined';
        };

        $scope.getUsername = function () {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getUsername();
            }
            return '';
        };

        $rootScope.showLoginDialog = function (username, password) {

            if ($rootScope.loginDialog && $rootScope.loginDialog != null && $rootScope.loginDialog.opened) {
                $rootScope.loginDialog.dismiss('cancel');
            }

            $rootScope.loginDialog = $modal.open({
                backdrop: 'static',
                keyboard: 'false',
                controller: 'LoginCtrl',
                size: 'lg',
                templateUrl: 'views/account/login.html',
                resolve: {
                    user: function () {
                        return {username: $scope.username, password: $scope.password};
                    }
                }
            });

            $rootScope.loginDialog.result.then(function (result) {
                if (result) {
                    $scope.username = result.username;
                    $scope.password = result.password;
                    $scope.login();
                } else {
                    $scope.cancel();
                }
            });
        };

        $rootScope.started = false;

        Idle.watch();

        $rootScope.$on('IdleStart', function () {
            closeModals();
            $rootScope.warning = $modal.open({
                templateUrl: 'warning-dialog.html',
                windowClass: 'modal-danger'
            });
        });

        $rootScope.$on('IdleEnd', function () {
            closeModals();
        });

        $rootScope.$on('IdleTimeout', function () {
            closeModals();
            if ($scope.isAuthenticated()) {
                if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
                    $rootScope.$emit('event:saveAndExecLogout');
                } else {
                    $rootScope.$emit('event:execLogout');
                }
            }
            $rootScope.timedout = $modal.open({
                templateUrl: 'timedout-dialog.html',
                windowClass: 'modal-danger'
            });
        });

        $scope.$on('Keepalive', function () {
            if ($scope.isAuthenticated()) {
                IdleService.keepAlive();
            }
        });

        $rootScope.$on('event:execLogout', function () {
            $scope.execLogout();
        });

        function closeModals() {
            if ($rootScope.warning) {
                $rootScope.warning.close();
                $rootScope.warning = null;
            }

            if ($rootScope.timedout) {
                $rootScope.timedout.close();
                $rootScope.timedout = null;
            }
        };

        $rootScope.start = function () {
            closeModals();
            Idle.watch();
            $rootScope.started = true;
        };

        $rootScope.stop = function () {
            closeModals();
            Idle.unwatch();
            $rootScope.started = false;

        };


        $scope.checkForIE = function () {
            var BrowserDetect = {
                init: function () {
                    this.browser = this.searchString(this.dataBrowser) || 'An unknown browser';
                    this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || 'an unknown version';
                    this.OS = this.searchString(this.dataOS) || 'an unknown OS';
                },
                searchString: function (data) {
                    for (var i = 0; i < data.length; i++) {
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
                    if (index === -1) {
                        return;
                    }
                    return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
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
                dataOS: [
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

            if (BrowserDetect.browser === 'Explorer') {
                var title = 'You are using Internet Explorer';
                var msg = 'This site is not yet optimized with Internet Explorer. For the best user experience, please use Chrome, Firefox or Safari. Thank you for your patience.';
                var btns = [
                    {result: 'ok', label: 'OK', cssClass: 'btn'}
                ];

                //$dialog.messageBox(title, msg, btns).open();


            }
        };


        $rootScope.readonly = false;
        $rootScope.igdocument = null; // current igdocument
        $rootScope.message = null; // current message
        $rootScope.datatype = null; // current datatype

        $rootScope.pages = ['list', 'edit', 'read'];
        $rootScope.context = {page: $rootScope.pages[0]};
        $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
        $rootScope.segmentsMap = {};  // Map for Segment;key:id, value:object
        $rootScope.datatypesMap = {}; // Map for Datatype; key:id, value:object
        $rootScope.tablesMap = {};// Map for tables; key:id, value:object
        $rootScope.segments = [];// list of segments of the selected messages
        $rootScope.datatypes = [];// list of datatypes of the selected messages
        $rootScope.segmentPredicates = [];// list of segment level predicates of the selected messages
        $rootScope.segmentConformanceStatements = [];// list of segment level Conformance Statements of the selected messages
        $rootScope.datatypePredicates = [];// list of segment level predicates of the selected messages
        $rootScope.datatypeConformanceStatements = [];// list of segment level Conformance Statements of the selected messages
        $rootScope.tables = [];// list of tables of the selected messages
        $rootScope.postfixCloneTable = 'CA';
        $rootScope.newCodeFakeId = 0;
        $rootScope.newTableFakeId = 0;
        $rootScope.newPredicateFakeId = 0;
        $rootScope.newConformanceStatementFakeId = 0;
        $rootScope.segment = null;
        $rootScope.config = null;
        $rootScope.messagesData = [];
        $rootScope.messages = [];// list of messages
        $rootScope.customIgs = [];
        $rootScope.preloadedIgs = [];
        $rootScope.changes = {};
        $rootScope.generalInfo = {type: null, 'message': null};
        $rootScope.references = []; // collection of element referencing a datatype to delete
        $rootScope.section = {};
        $rootScope.conformanceStatementIdList = [];
        $rootScope.parentsMap = {};
        $rootScope.igChanged = false;


        $rootScope.messageTree = null;

        $scope.scrollbarWidth = 0;


        // TODO: remove
        $rootScope.selectIGDocumentTab = function (value) {
//        $rootScope.igdocumentTabs[0] = false;
//        $rootScope.igdocumentTabs[1] = false;
//        $rootScope.igdocumentTabs[2] = false;
//        $rootScope.igdocumentTabs[3] = false;
//        $rootScope.igdocumentTabs[4] = false;
//        $rootScope.igdocumentTabs[5] = false;
//        $rootScope.igdocumentTabs[value] = true;
        };

        $scope.getScrollbarWidth = function () {
            if ($scope.scrollbarWidth == 0) {
                var outer = document.createElement("div");
                outer.style.visibility = "hidden";
                outer.style.width = "100px";
                outer.style.msOverflowStyle = "scrollbar"; // needed for WinJS apps

                document.body.appendChild(outer);

                var widthNoScroll = outer.offsetWidth;
                // force scrollbars
                outer.style.overflow = "scroll";

                // add innerdiv
                var inner = document.createElement("div");
                inner.style.width = "100%";
                outer.appendChild(inner);

                var widthWithScroll = inner.offsetWidth;

                // remove divs
                outer.parentNode.removeChild(outer);

                $scope.scrollbarWidth = widthNoScroll - widthWithScroll;
            }

            return $scope.scrollbarWidth;
        };
        $rootScope.initMaps = function () {
            $rootScope.segment = null;
            $rootScope.datatype = null;
            $rootScope.message = null;
            $rootScope.table = null;
            $rootScope.codeSystems = [];
            $rootScope.messagesMap = {};
            $rootScope.segmentsMap = {};
            $rootScope.datatypesMap = {};
            $rootScope.tablesMap = {};
            $rootScope.segments = [];
            $rootScope.tables = [];
            $rootScope.segmentPredicates = [];
            $rootScope.segmentConformanceStatements = [];
            $rootScope.datatypePredicates = [];
            $rootScope.datatypeConformanceStatements = [];
            $rootScope.datatypes = [];
            $rootScope.messages = [];
            $rootScope.messagesData = [];
            $rootScope.newCodeFakeId = 0;
            $rootScope.newTableFakeId = 0;
            $rootScope.newPredicateFakeId = 0;
            $rootScope.newConformanceStatementFakeId = 0;
            $rootScope.clearChanges();
            $rootScope.parentsMap = [];
            $rootScope.conformanceStatementIdList = [];

            $rootScope.messageTree = null;
        };

        $rootScope.$watch(function () {
            return $location.path();
        }, function (newLocation, oldLocation) {
            $rootScope.setActive(newLocation);
        });


        $rootScope.api = function (value) {
            return  value;
        };


        $rootScope.isActive = function (path) {
            return path === $rootScope.activePath;
        };

        $rootScope.setActive = function (path) {
            if (path === '' || path === '/') {
                $location.path('/home');
            } else {
                $rootScope.activePath = path;
            }
        };

        $rootScope.clearChanges = function (path) {
//        $rootScope.changes = {};
            $rootScope.igChanged = false;
        };

        $rootScope.hasChanges = function () {
            //return Object.getOwnPropertyNames($rootScope.changes).length !== 0;
            return $rootScope.igChanged;
        };

        $rootScope.recordChanged = function () {
            $rootScope.igChanged = true;
        };

        $rootScope.recordChange = function (object, changeType) {
//        var type = object.type;
//
//
//        if($rootScope.changes[type] === undefined){
//            $rootScope.changes[type] = {};
//        }
//
//        if($rootScope.changes[type][object.id] === undefined){
//            $rootScope.changes[type][object.id] = {};
//        }
//
//        if(changeType === "datatype"){
//            $rootScope.changes[type][object.id][changeType] = object[changeType].id;
//        }else{
//            $rootScope.changes[type][object.id][changeType] = object[changeType];
//        }
//
//        console.log("Change is " + $rootScope.changes[type][object.id][changeType]);
            $rootScope.recordChanged();
        };


        $rootScope.recordChange2 = function (type, id, attr, value) {
//        if($rootScope.changes[type] === undefined){
//            $rootScope.changes[type] = {};
//        }
//        if($rootScope.changes[type][id] === undefined){
//            $rootScope.changes[type][id] = {};
//        }
//        if(attr != null) {
//            $rootScope.changes[type][id][attr] = value;
//        }else {
//            $rootScope.changes[type][id] = value;
//        }
            $rootScope.recordChanged();
        };

        $rootScope.recordChangeForEdit = function (object, changeType) {
//        var type = object.type;
//
//        if($rootScope.changes[type] === undefined){
//            $rootScope.changes[type] = {};
//        }
//
//        if($rootScope.changes[type]['edit'] === undefined){
//            $rootScope.changes[type]['edit'] = {};
//        }
//
//        if($rootScope.changes[type]['edit'][object.id] === undefined){
//            $rootScope.changes[type]['edit'][object.id] = {};
//        }
//        $rootScope.changes[type]['edit'][object.id][changeType] = object[changeType];
            $rootScope.recordChanged();
        };

        $rootScope.recordChangeForEdit2 = function (type, command, id, valueType, value) {
//        var obj = $rootScope.findObjectInChanges(type, "add", id);
//        if (obj === undefined) { // not a new object
//            if ($rootScope.changes[type] === undefined) {
//                $rootScope.changes[type] = {};
//            }
//            if ($rootScope.changes[type][command] === undefined) {
//                $rootScope.changes[type][command] = [];
//            }
//            if (valueType !== type) {
//                var obj = $rootScope.findObjectInChanges(type, command, id);
//                if (obj === undefined) {
//                    obj = {id: id};
//                    $rootScope.changes[type][command].push(obj);
//                }
//                obj[valueType] = value;
//            } else {
//                $rootScope.changes[type][command].push(value);
//            }
//        }
            $rootScope.recordChanged();
        };

        $rootScope.recordDelete = function (type, command, id) {
            if (id < 0) { // new object
                $rootScope.removeObjectFromChanges(type, "add", id);
            } else {
                $rootScope.removeObjectFromChanges(type, "edit", id);
//            if ($rootScope.changes[type] === undefined) {
//                $rootScope.changes[type] = {};
//            }
//            if ($rootScope.changes[type][command] === undefined) {
//                $rootScope.changes[type][command] = [];
//            }
//
//            if ($rootScope.changes[type]["delete"] === undefined) {
//                $rootScope.changes[type]["delete"] = [];
//            }
//
//            $rootScope.changes[type]["delete"].push({id:id});
                $rootScope.recordChanged();
            }

//        if($rootScope.changes[type]) {            //clean the changes object
//            if ($rootScope.changes[type]["add"] && $rootScope.changes[type]["add"].length === 0) {
//                delete  $rootScope.changes[type]["add"];
//            }
//            if ($rootScope.changes[type]["edit"] && $rootScope.changes[type]["edit"].length === 0) {
//                delete  $rootScope.changes[type]["edit"];
//            }
//
//            if (Object.getOwnPropertyNames($rootScope.changes[type]).length === 0) {
//                delete $rootScope.changes[type];
//            }
//        }
        };


        $rootScope.findObjectInChanges = function (type, command, id) {
            if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
                for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
                    var tmp = $rootScope.changes[type][command][i];
                    if (tmp.id === id) {
                        return tmp;
                    }
                }
            }
            return undefined;
        };


        $rootScope.isNewObject = function (type, command, id) {
            if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
                for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
                    var tmp = $rootScope.changes[type][command][i];
                    if (tmp.id === id) {
                        return true;
                    }
                }
            }
            return false;
        };


        $rootScope.removeObjectFromChanges = function (type, command, id) {
            if ($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
                for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
                    var tmp = $rootScope.changes[type][command][i];
                    if (tmp.id === id) {
                        $rootScope.changes[type][command].splice(i, 1);
                    }
                }
            }
            return undefined;
        };


        Restangular.setBaseUrl('api/');
//    Restangular.setResponseExtractor(function(response, operation) {
//        return response.data;
//    });

        $rootScope.showError = function (error) {
            var modalInstance = $modal.open({
                templateUrl: 'ErrorDlgDetails.html',
                controller: 'ErrorDetailsCtrl',
                resolve: {
                    error: function () {
                        return error;
                    }
                }
            });
            modalInstance.result.then(function (error) {
                $rootScope.error = error;
            }, function () {
            });
        };


        $rootScope.apply = function (label) { //FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.isFlavor = function (label) { //FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.getDatatype = function (id) {
            return $rootScope.datatypesMap && $rootScope.datatypesMap[id];
        };

        $rootScope.calNextCSID = function () {
            if ($rootScope.igdocument.metaData.ext != null) {
                var maxIDNum = Number(0);
                angular.forEach($rootScope.conformanceStatementIdList, function (id) {
                    var tempID = parseInt(id.replace($rootScope.igdocument.metaData.ext + "-", ""));

                    if (tempID > maxIDNum) maxIDNum = tempID;
                });

                return $rootScope.igdocument.metaData.ext + "-" + (maxIDNum + 1);
            } else {
                return "";
            }
        };
        $rootScope.processElement = function (element, parent) {
            try {
                if (element != undefined && element != null) {
                    if (element.type === "message") {
                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.conformanceStatements, function (cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.children, function (segmentRefOrGroup) {
                            $rootScope.processElement(segmentRefOrGroup, element);
                        });
                    } else if (element.type === "group" && element.children) {
                        if (parent) {
                            $rootScope.parentsMap[element.id] = parent;
                        }
                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.children, function (segmentRefOrGroup) {
                            $rootScope.processElement(segmentRefOrGroup, element);
                        });
                    } else if (element.type === "segmentRef") {
                        if (parent) {
                            $rootScope.parentsMap[element.id] = parent;
                        }
                        $rootScope.processElement($rootScope.segmentsMap[element.ref.id], element);
                    } else if (element.type === "segment") {
                        element.fields = $filter('orderBy')(element.fields, 'position');
                        angular.forEach(element.conformanceStatements, function (cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.fields, function (field) {
                            $rootScope.processElement(field, element);
                        });
                    } else if (element.type === "field") {
                        $rootScope.parentsMap[element.id] = parent;
                        $rootScope.processElement($rootScope.datatypesMap[element.datatype.id], element);
                    } else if (element.type === "component") {
                        $rootScope.parentsMap[element.id] = parent;
                        $rootScope.processElement($rootScope.datatypesMap[element.datatype.id], element);
                    } else if (element.type === "datatype") {
                        element.components = $filter('orderBy')(element.components, 'position');
                        angular.forEach(element.conformanceStatements, function (cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.components, function (component) {
                            $rootScope.processElement(component, element);
                        });
                    }
                }
            } catch (e) {
                throw e;
            }
        };


        $rootScope.processMessageTree = function (element, parent) {

            try {
                if (element != undefined && element != null) {
                    if (element.type === "message") {
                        var m = new Object();
                        m.children = [];
                        $rootScope.messageTree = m;

                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.children, function (segmentRefOrGroup) {
                            $rootScope.processMessageTree(segmentRefOrGroup, m);
                        });

                    } else if (element.type === "group" && element.children) {
                        var g = new Object();
                        g.path = element.position + "[1]";
                        g.obj = element;
                        g.children = [];
                        if (parent.path) {
                            g.path = parent.path + "." + element.position + "[1]";
                        }
                        parent.children.push(g);
                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.children, function (segmentRefOrGroup) {
                            $rootScope.processMessageTree(segmentRefOrGroup, g);
                        });
                    } else if (element.type === "segmentRef") {
                        var s = new Object();
                        s.path = element.position + "[1]";
                        s.obj = element;
                        s.children = [];
                        if (parent.path) {
                            s.path = parent.path + "." + element.position + "[1]";
                        }
                        parent.children.push(s);

                        var ref = $rootScope.segmentsMap[element.ref.id];
                        $rootScope.processMessageTree(ref, s);

                    } else if (element.type === "segment") {
                        element.fields = $filter('orderBy')(element.fields, 'position');
                        angular.forEach(element.fields, function (field) {
                            $rootScope.processMessageTree(field, parent);
                        });
                    } else if (element.type === "field") {
                        var f = new Object();
                        f.obj = element;
                        f.path = parent.path + "." + element.position + "[1]";
                        f.children = [];
                        parent.children.push(f);
                        $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], f);
                    } else if (element.type === "component") {
                        var c = new Object();
                        c.obj = element;
                        c.path = parent.path + "." + element.position + "[1]";
                        c.children = [];
                        parent.children.push(c);
                        $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], c);
                    } else if (element.type === "datatype") {
                        element.components = $filter('orderBy')(element.components, 'position');
                        angular.forEach(element.components, function (component) {
                            $rootScope.processMessageTree(component, parent);
                        });
                    }
                }
            } catch (e) {
                throw e;
            }
        };

        $rootScope.createNewFlavorName = function (label) {
            if ($rootScope.igdocument != null) {
                if ($rootScope.igdocument.metaData["ext"] === null) {
                    return label + "_" + (Math.floor(Math.random() * 10000000) + 1);
                } else {
                    return label + "_" + $rootScope.igdocument.metaData["ext"] + "_" + (Math.floor(Math.random() * 10000000) + 1);
                }
            } else {
                return null;
            }
        };

        $rootScope.createNewExtension = function (ext) {
            if ($rootScope.igdocument != null) {
                var rand = (Math.floor(Math.random() * 10000000) + 1);
                if ($rootScope.igdocument.metaData["ext"] === null) {
                    return ext != null && ext != "" ? ext + "_" + rand : rand;
                } else {
                    return  ext != null && ext != "" ? ext + "_" + $rootScope.igdocument.metaData["ext"] + "_" + rand + 1 : rand + 1;
                }
            } else {
                return null;
            }
        };

        $rootScope.isSubComponent = function (node) {
            node.type === 'component' && $rootScope.parentsMap[node.id] && $rootScope.parentsMap[node.id].type === 'component';
        };

        $rootScope.findDatatypeRefs = function (datatype, obj, path) {
            if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
                if (obj.datatype.id === datatype.id) {
                    var found = angular.copy(obj);
                    found.path = path;
                    $rootScope.references.push(found);
                }
                $rootScope.findDatatypeRefs(datatype, $rootScope.datatypesMap[obj.datatype.id], path);
            } else if (angular.equals(obj.type, 'segment')) {
                angular.forEach(obj.fields, function (field) {
                    $rootScope.findDatatypeRefs(datatype, field, path + "-" + field.position);
                });
            } else if (angular.equals(obj.type, 'datatype')) {
                if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
                    angular.forEach(obj.components, function (component) {
                        $rootScope.findDatatypeRefs(datatype, component, path + "." + component.position);
                    });
                }
            }
        };

        $rootScope.findSegmentRefs = function (segment, obj, path) {
            if (angular.equals(obj.type, 'message') || angular.equals(obj.type, 'group')) {
                angular.forEach(obj.children, function (child) {
                    $rootScope.findSegmentRefs(segment, child, path + "." + child.position);
                });
            } else if (angular.equals(obj.type, 'segmentRef')) {
                if (obj.ref.id === segment.id) {
                    var found = angular.copy(obj);
                    found.path = path;
                    $rootScope.references.push(found);
                }
            }
        };

        $rootScope.findTableRefs = function (table, obj, path) {
            if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
                if (obj.table != undefined) {
                    if (obj.table.id === table.id) {
                        var found = angular.copy(obj);
                        found.path = path;
                        $rootScope.references.push(found);
                    }
                }
                $rootScope.findTableRefs(table, $rootScope.datatypesMap[obj.datatype.id], path);
            } else if (angular.equals(obj.type, 'segment')) {
                angular.forEach(obj.fields, function (field) {
                    $rootScope.findTableRefs(table, field, path + "-" + field.position);
                });
            } else if (angular.equals(obj.type, 'datatype')) {
                if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
                    angular.forEach(obj.components, function (component) {
                        $rootScope.findTableRefs(table, component, path + "." + component.position);
                    });
                }
            }
        };

        $rootScope.genRegex = function (format) {
            if (format === 'YYYY') {
                return '(([0-9]{4})|(([0-9]{4})((0[1-9])|(1[0-2])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMM') {
                return '((([0-9]{4})((0[1-9])|(1[0-2])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMMDD') {
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMMDDhh') {
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMMDDhhmm') {
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMMDDhhmmss') {
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYYMMDDhhmmss.sss') {
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if (format === 'YYYY+-ZZZZ') {
                return '([0-9]{4}).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMM+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2])).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMMDD+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMMDDhh+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMMDDhhmm+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMMDDhhmmss+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]).*((\\+|\\-)[0-9]{4})';
            } else if (format === 'YYYYMMDDhhmmss.sss+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]((\\+|\\-)[0-9]{4})';
            } else if (format === 'ISO-compliant OID') {
                return '[0-2](\\.(0|[1-9][0-9]*))*';
            } else if (format === 'Alphanumeric') {
                return '^[a-zA-Z0-9]*$';
            }

            return format;
        };

        $rootScope.isAvailableDTForTable = function (dt) {
            if (dt != undefined) {
                if (dt.name === 'IS' || dt.name === 'ID' || dt.name === 'CWE' || dt.name === 'CNE' || dt.name === 'CE') return true;

                if (dt.components != undefined && dt.components.length > 0) return true;

            }
            return false;
        };

        $rootScope.validateNumber = function (event) {
            var key = window.event ? event.keyCode : event.which;
            if (event.keyCode == 8 || event.keyCode == 46
                || event.keyCode == 37 || event.keyCode == 39) {
                return true;
            }
            else if (key < 48 || key > 57) {
                return false;
            }
            else return true;
        };

        $rootScope.generateCompositeConformanceStatement = function (compositeType, firstConstraint, secondConstraint) {
            var firstConstraintAssertion = firstConstraint.assertion.replace("<Assertion>", "");
            firstConstraintAssertion = firstConstraintAssertion.replace("</Assertion>", "");
            var secondConstraintAssertion = secondConstraint.assertion.replace("<Assertion>", "");
            secondConstraintAssertion = secondConstraintAssertion.replace("</Assertion>", "");

            var cs = null;
            if (compositeType === 'AND') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + 'AND' + ' [' + secondConstraint.description + ']',
                    assertion: '<Assertion><AND>' + firstConstraintAssertion + secondConstraintAssertion + '</AND></Assertion>'
                };
            } else if (compositeType === 'OR') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + 'OR' + ' [' + secondConstraint.description + ']',
                    assertion: '<Assertion><OR>' + firstConstraintAssertion + secondConstraintAssertion + '</OR></Assertion>'
                };
            } else if (compositeType === 'IFTHEN') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: 'IF [' + firstConstraint.description + '] ' + 'THEN ' + ' [' + secondConstraint.description + ']',
                    assertion: '<Assertion><IMPLY>' + firstConstraintAssertion + secondConstraintAssertion + '</IMPLY></Assertion>'
                };
            }
            return cs;
        }


        $rootScope.generateCompositePredicate = function (compositeType, firstConstraint, secondConstraint) {
            var firstConstraintAssertion = firstConstraint.assertion.replace("<Condition>", "");
            firstConstraintAssertion = firstConstraintAssertion.replace("</Condition>", "");
            var secondConstraintAssertion = secondConstraint.assertion.replace("<Condition>", "");
            secondConstraintAssertion = secondConstraintAssertion.replace("</Condition>", "");

            var cp = null;
            if (compositeType === 'AND') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + 'AND' + ' [' + secondConstraint.description + ']',
                    trueUsage: '',
                    falseUsage: '',
                    assertion: '<Condition><AND>' + firstConstraintAssertion + secondConstraintAssertion + '</AND></Condition>'
                };
            } else if (compositeType === 'OR') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + 'OR' + ' [' + secondConstraint.description + ']',
                    trueUsage: '',
                    falseUsage: '',
                    assertion: '<Condition><OR>' + firstConstraintAssertion + secondConstraintAssertion + '</OR></Condition>'
                };
            } else if (compositeType === 'IFTHEN') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: 'IF [' + firstConstraint.description + '] ' + 'THEN ' + ' [' + secondConstraint.description + ']',
                    trueUsage: '',
                    falseUsage: '',
                    assertion: '<Condition><IMPLY>' + firstConstraintAssertion + secondConstraintAssertion + '</IMPLY></Condition>'
                };
            }
            return cp;
        }

        $rootScope.generateConformanceStatement = function (positionPath, newConstraint) {
            var cs = null;
            if (newConstraint.contraintType === 'valued') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + '.',
                    assertion: '<Assertion><Presence Path=\"' + newConstraint.position_1 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'a literal value') {
                if (newConstraint.value.indexOf("^") == -1) {
                    cs = {
                        id: new ObjectId().toString(),
                        constraintId: newConstraint.constraintId,
                        constraintTarget: positionPath,
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
                        assertion: '<Assertion><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase="false"/></Assertion>'
                    };
                } else {

                    var componetsList = newConstraint.value.split("^");
                    var assertionScript = "";
                    var componentPosition = 0;

                    angular.forEach(componetsList, function (componentValue) {
                        componentPosition = componentPosition + 1;
                        var script = '<PlainText Path=\"' + newConstraint.position_1 + "." + componentPosition + "[1]" + '\" Text=\"' + componentValue + '\" IgnoreCase="false"/>';
                        if (assertionScript === "") {
                            assertionScript = script;
                        } else {
                            assertionScript = "<AND>" + assertionScript + script + "</AND>";
                        }
                    });


                    cs = {
                        id: new ObjectId().toString(),
                        constraintId: newConstraint.constraintId,
                        constraintTarget: positionPath,
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
                        assertion: '<Assertion>' + assertionScript + '</Assertion>'
                    };
                }
            } else if (newConstraint.contraintType === 'one of list values') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.value + '.',
                    assertion: '<Assertion><StringList Path=\"' + newConstraint.position_1 + '\" CSV=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'one of codes in ValueSet') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.valueSetId + '.',
                    assertion: '<Assertion><ValueSet Path=\"' + newConstraint.position_1 + '\" ValueSetID=\"' + newConstraint.valueSetId + '\" BindingStrength=\"' + newConstraint.bindingStrength + '\" BindingLocation=\"' + newConstraint.bindingLocation + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'formatted value') {
                if (newConstraint.value === 'Regular expression') {
                    cs = {
                        id: new ObjectId().toString(),
                        constraintId: newConstraint.constraintId,
                        constraintTarget: positionPath,
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' valid in format: \'' + newConstraint.value2 + '\'.',
                        assertion: '<Assertion><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Assertion>'
                    };
                } else {
                    cs = {
                        id: new ObjectId().toString(),
                        constraintId: newConstraint.constraintId,
                        constraintTarget: positionPath,
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' valid in format: \'' + newConstraint.value + '\'.',
                        assertion: '<Assertion><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex(newConstraint.value) + '\"/></Assertion>'
                    };
                }
            } else if (newConstraint.contraintType === 'identical to another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' identical to the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'not-equal to another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="NE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'greater than another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GT" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to or greater than another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'less than another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LT" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to or less than another node') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than the value of ' + newConstraint.location_2 + '.',
                    assertion: '<Assertion><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LE" Path2=\"' + newConstraint.position_2 + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="EQ" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'not-equal to') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="NE" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'greater than') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GT" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to or greater than') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GE" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'less than') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LT" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === 'equal to or less than') {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than ' + newConstraint.value + '.',
                    assertion: '<Assertion><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LE" Value=\"' + newConstraint.value + '\"/></Assertion>'
                };
            } else if (newConstraint.contraintType === "valued sequentially starting with the value '1'") {
                cs = {
                    id: new ObjectId().toString(),
                    constraintId: newConstraint.constraintId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + " valued sequentially starting with the value '1'.",
                    assertion: '<Assertion><SetID Path=\"' + newConstraint.position_1 + '\"/></Assertion>'
                };
            }

            return cs;
        }

        $rootScope.generatePredicate = function (positionPath, newConstraint) {
            var cp = null;
            if (newConstraint.contraintType === 'valued') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType,
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><Presence Path=\"' + newConstraint.position_1 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'a literal value') {
                if (newConstraint.value.indexOf("^") == -1) {
                    cp = {
                        id: new ObjectId().toString(),
                        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                        constraintTarget: positionPath,
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
                        trueUsage: newConstraint.trueUsage,
                        falseUsage: newConstraint.falseUsage,
                        assertion: '<Condition><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase="false"/></Condition>'
                    };
                } else {
                    var componetsList = newConstraint.value.split("^");
                    var assertionScript = "";
                    var componentPosition = 0;

                    angular.forEach(componetsList, function (componentValue) {
                        componentPosition = componentPosition + 1;
                        var script = '<PlainText Path=\"' + newConstraint.position_1 + "." + componentPosition + "[1]" + '\" Text=\"' + componentValue + '\" IgnoreCase="false"/>';
                        if (assertionScript === "") {
                            assertionScript = script;
                        } else {
                            assertionScript = "<AND>" + assertionScript + script + "</AND>";
                        }
                    });
                    cp = {
                        id: new ObjectId().toString(),
                        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                        constraintTarget: positionPath,
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'' + newConstraint.value + '\'.',
                        trueUsage: newConstraint.trueUsage,
                        falseUsage: newConstraint.falseUsage,
                        assertion: '<Condition>' + assertionScript + '</Condition>'
                    };
                }
            } else if (newConstraint.contraintType === 'one of list values') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><StringList Path=\"' + newConstraint.position_1 + '\" CSV=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'one of codes in ValueSet') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' ' + newConstraint.contraintType + ': ' + newConstraint.valueSetId + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><ValueSet Path=\"' + newConstraint.position_1 + '\" ValueSetID=\"' + newConstraint.valueSetId + '\" BindingStrength=\"' + newConstraint.bindingStrength + '\" BindingLocation=\"' + newConstraint.bindingLocation + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'formatted value') {
                if (newConstraint.value === 'Regular expression') {
                    cp = {
                        id: new ObjectId().toString(),
                        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                        constraintTarget: positionPath,
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' valid in format: \'' + newConstraint.value2 + '\'.',
                        trueUsage: newConstraint.trueUsage,
                        falseUsage: newConstraint.falseUsage,
                        assertion: '<Condition><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Condition>'
                    };
                } else {
                    cp = {
                        id: new ObjectId().toString(),
                        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                        constraintTarget: positionPath,
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' valid in format: \'' + newConstraint.value + '\'.',
                        trueUsage: newConstraint.trueUsage,
                        falseUsage: newConstraint.falseUsage,
                        assertion: '<Condition><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex(newConstraint.value) + '\"/></Condition>'
                    };
                }
            } else if (newConstraint.contraintType === 'identical to another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' identical to the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'not-equal to another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="NE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'greater than another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GT" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to or greater than another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="GE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'less than another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LT" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to or less than another node') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than the value of ' + newConstraint.location_2 + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><PathValue Path1=\"' + newConstraint.position_1 + '\" Operator="LE" Path2=\"' + newConstraint.position_2 + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="EQ" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'not-equal to') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' different with ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="NE" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'greater than') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' greater than ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GT" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to or greater than') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or greater than ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="GE" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'less than') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' less than ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LT" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === 'equal to or less than') {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' equal to or less than ' + newConstraint.value + '.',
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SimpleValue Path=\"' + newConstraint.position_1 + '\" Operator="LE" Value=\"' + newConstraint.value + '\"/></Condition>'
                };
            } else if (newConstraint.contraintType === "valued sequentially starting with the value '1'") {
                cp = {
                    id: new ObjectId().toString(),
                    constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                    constraintTarget: positionPath,
                    description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + " valued sequentially starting with the value '1'.",
                    trueUsage: newConstraint.trueUsage,
                    falseUsage: newConstraint.falseUsage,
                    assertion: '<Condition><SetID Path=\"' + newConstraint.position_1 + '\"/></Condition>'
                };
            }

            return cp;
        };


        $rootScope.erorrForComplexConfStatement = function (newComplexConstraintId, targetComplexId, compositeType, firstConstraint, secondConstraint) {
            if ($rootScope.isEmptyComplexConstraintID(newComplexConstraintId)) return true;
            if ($rootScope.isDuplicatedComplexConstraintID(newComplexConstraintId, targetComplexId))  return true;
            if ($rootScope.isEmptyCompositeType(compositeType))  return true;
            if (firstConstraint == null) return true;
            if (secondConstraint == null) return true;
            return false;
        };

        $rootScope.erorrForComplexPredicate = function (compositeType, firstConstraint, secondConstraint, complexConstraintTrueUsage, complexConstraintFalseUsage) {
            if ($rootScope.isEmptyCompositeType(compositeType)) return true;
            if (firstConstraint == null) return true;
            if (secondConstraint == null) return true;
            if (complexConstraintTrueUsage == null) return true;
            if (complexConstraintFalseUsage == null) return true;
            return false;
        };

        $rootScope.erorrForPredicate = function (newConstraint, type) {
            if ($rootScope.isEmptyConstraintNode(newConstraint, type)) return true;
            if ($rootScope.isEmptyConstraintVerb(newConstraint)) return true;
            if ($rootScope.isEmptyConstraintPattern(newConstraint)) return true;
            if (newConstraint.contraintType == 'a literal value' ||
                newConstraint.contraintType == 'equal to' ||
                newConstraint.contraintType == 'not-equal to' ||
                newConstraint.contraintType == 'greater than' ||
                newConstraint.contraintType == 'equal to or greater than' ||
                newConstraint.contraintType == 'less than' ||
                newConstraint.contraintType == 'equal to or less than' ||
                newConstraint.contraintType == 'one of list values' ||
                newConstraint.contraintType == 'formatted value') {
                if ($rootScope.isEmptyConstraintValue(newConstraint)) return true;
                if (newConstraint.value == 'Regular expression') {
                    if ($rootScope.isEmptyConstraintValue2(newConstraint)) return true;
                }
            } else if (newConstraint.contraintType == 'identical to another node' ||
                newConstraint.contraintType == 'equal to another node' ||
                newConstraint.contraintType == 'not-equal to another node' ||
                newConstraint.contraintType == 'greater than another node' ||
                newConstraint.contraintType == 'equal to or greater than another node' ||
                newConstraint.contraintType == 'less than another node' ||
                newConstraint.contraintType == 'equal to or less than another node') {
                if ($rootScope.isEmptyConstraintAnotherNode(newConstraint)) return true;
            } else if (newConstraint.contraintType == 'one of codes in ValueSet') {
                if ($rootScope.isEmptyConstraintValueSet(newConstraint, type)) return true;
            }
            if (newConstraint.trueUsage == null) return true;
            if (newConstraint.falseUsage == null) return true;

            return false;
        }


        $rootScope.erorrForConfStatement = function (newConstraint, targetId, type) {
            if ($rootScope.isEmptyConstraintID(newConstraint)) return true;
            if ($rootScope.isDuplicatedConstraintID(newConstraint, targetId)) return true;
            if ($rootScope.isEmptyConstraintNode(newConstraint, type)) return true;
            if ($rootScope.isEmptyConstraintVerb(newConstraint)) return true;
            if ($rootScope.isEmptyConstraintPattern(newConstraint)) return true;
            if (newConstraint.contraintType == 'a literal value' ||
                newConstraint.contraintType == 'equal to' ||
                newConstraint.contraintType == 'not-equal to' ||
                newConstraint.contraintType == 'greater than' ||
                newConstraint.contraintType == 'equal to or greater than' ||
                newConstraint.contraintType == 'less than' ||
                newConstraint.contraintType == 'equal to or less than' ||
                newConstraint.contraintType == 'one of list values' ||
                newConstraint.contraintType == 'formatted value') {
                if ($rootScope.isEmptyConstraintValue(newConstraint)) return true;
                if (newConstraint.value == 'Regular expression') {
                    if ($rootScope.isEmptyConstraintValue2(newConstraint)) return true;
                }
            } else if (newConstraint.contraintType == 'identical to another node' ||
                newConstraint.contraintType == 'equal to another node' ||
                newConstraint.contraintType == 'not-equal to another node' ||
                newConstraint.contraintType == 'greater than another node' ||
                newConstraint.contraintType == 'equal to or greater than another node' ||
                newConstraint.contraintType == 'less than another node' ||
                newConstraint.contraintType == 'equal to or less than another node') {
                if ($rootScope.isEmptyConstraintAnotherNode(newConstraint)) return true;
            } else if (newConstraint.contraintType == 'one of codes in ValueSet') {
                if ($rootScope.isEmptyConstraintValueSet(newConstraint, type)) return true;
            }
            return false;
        };

        $rootScope.isEmptyConstraintID = function (newConstraint) {
            if (newConstraint.constraintId === null) return true;
            if (newConstraint.constraintId === '') return true;

            return false;
        }

        $rootScope.isEmptyComplexConstraintID = function (id) {
            if (id === null) return true;
            if (id === '') return true;

            return false;
        }

        $rootScope.isDuplicatedConstraintID = function (newConstraint, targetId) {
            if ($rootScope.conformanceStatementIdList.indexOf(newConstraint.constraintId) != -1 && targetId == newConstraint.constraintId) return true;

            return false;
        }

        $rootScope.isDuplicatedComplexConstraintID = function (newComplexConstraintId, targetComplexId) {
            if ($rootScope.conformanceStatementIdList.indexOf(newComplexConstraintId) != -1 && targetComplexId == newComplexConstraintId) return true;

            return false;
        }

        $rootScope.isEmptyConstraintNode = function (newConstraint, type) {
            if (type == 'datatype') {
                if (newConstraint.component_1 === null) return true;
            } else if (type == 'segment') {
                if (newConstraint.field_1 === null) return true;
            } else if (type == 'message') {
                if (newConstraint.position_1 === null) return true;
            }

            return false;
        }

        $rootScope.isEmptyConstraintVerb = function (newConstraint) {
            if (newConstraint.verb === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintPattern = function (newConstraint) {
            if (newConstraint.contraintType === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintValue = function (newConstraint) {
            if (newConstraint.value === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintValue2 = function (newConstraint) {
            if (newConstraint.value2 === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintAnotherNode = function (newConstraint, type) {
            if (type == 'datatype') {
                if (newConstraint.component_2 === null) return true;
            } else if (type == 'segment') {
                if (newConstraint.field_2 === null) return true;
            } else if (type == 'message') {
                if (newConstraint.position_2 === null) return true;
            }

            return false;
        }

        $rootScope.isEmptyConstraintValueSet = function (newConstraint) {
            if (newConstraint.valueSetId === null) return true;

            return false;
        }

        $rootScope.isEmptyCompositeType = function (compositeType) {
            if (compositeType === null) return true;

            return false;
        }


        //We check for IE when the user load the main page.
        //TODO: Check only once.
//    $scope.checkForIE();


        $rootScope.openRichTextDlg = function (obj, key, title, disabled) {
            var modalInstance = $modal.open({
                templateUrl: 'RichTextCtrl.html',
                controller: 'RichTextCtrl',
                windowClass: 'app-modal-window',
                backdrop: true,
                keyboard: true,
                backdropClick: false,
                resolve: {
                    editorTarget: function () {
                        return {
                            key: key,
                            obj: obj,
                            disabled: disabled,
                            title: title
                        };
                    }
                }
            });
        };

        $rootScope.openInputTextDlg = function (obj, key, title, disabled) {
            var modalInstance = $modal.open({
                templateUrl: 'InputTextCtrl.html',
                controller: 'InputTextCtrl',
                backdrop: true,
                keyboard: true,
                windowClass: 'app-modal-window',
                backdropClick: false,
                resolve: {
                    editorTarget: function () {
                        return {
                            key: key,
                            obj: obj,
                            disabled: disabled,
                            title: title
                        };
                    }
                }
            });
        };


        $rootScope.isDuplicated = function (obj, context, list) {
            if (obj == null || obj == undefined || obj[context] == null) return false;
            return _.find(_.without(list, obj), function (item) {
                return item[context] == obj[context] && item.id != obj.id;
            });
        };

//        $rootScope.validateExtension = function (obj, context, list) {
////            if (obj == null || obj == undefined) return false;
//            if(obj[context] == null) return false;
//            return _.find(_.without(list, obj), function (item) {
//                return item[context] == obj[context];
//            });
//
//
//        };


        $rootScope.isDuplicatedTwoContexts = function (obj, context1, context2, list) {
            if (obj == null || obj == undefined) return false;

            return _.find(_.without(list, obj), function (item) {
                if (item[context1] == obj[context1]) {
                    return item[context2] == obj[context2] && item.id != obj.id;
                } else {
                    return false;
                }
            });
        };

        $scope.init = function () {
//        $http.get('api/igdocuments/config', {timeout: 60000}).then(function (response) {
//            $rootScope.config = angular.fromJson(response.data);
//        }, function (error) {
//        });
        };

        $scope.getFullName = function () {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getFullName();
            }
            return '';
        };

        $rootScope.getLabel = function (name, ext) {
            var label = name;
            if (ext && ext !== null && ext !== "") {
                label = label + "_" + ext;
            }
            return label;
        };

        $rootScope.getDynamicWidth = function (a, b, otherColumsWidth) {
            var tableWidth = $rootScope.getTableWidth();
            if (tableWidth > 0) {
                var left = tableWidth - otherColumsWidth;
                return {"width": a * parseInt(left / b) + "px"};
            }
            return "";
        };


        $rootScope.getTableWidth = function () {
            if ($rootScope.tableWidth === null || $scope.tableWidth == 0) {
                $rootScope.tableWidth = $("#nodeDetailsPanel").width();
            }
            return $rootScope.tableWidth;
        };


        $rootScope.getConstraintAsString = function (constraint) {
            return constraint.constraintId + " - " + constraint.description;
        };

        $rootScope.getConformanceStatementAsString = function (constraint) {
            return "[" + constraint.constraintId + "]" + constraint.description;
        };

        $rootScope.getPredicateAsString = function (constraint) {
            return constraint.description;
        };

        $rootScope.getConstraintsAsString = function (constraints) {
            var str = '';
            for (var index in constraints) {
                str = str + "<p style=\"text-align: left\">" + constraints[index].id + " - " + constraints[index].description + "</p>";
            }
            return str;
        };

        $rootScope.getPredicatesAsMultipleLinesString = function (node) {
            var html = "";
            angular.forEach(node.predicates, function (predicate) {
                html = html + "<p>" + predicate.description + "</p>";
            });
            return html;
        };

        $rootScope.getPredicatesAsOneLineString = function (node) {
            var html = "";
            angular.forEach(node.predicates, function (predicate) {
                html = html + predicate.description;
            });
            return $sce.trustAsHtml(html);
        };


        $rootScope.getConfStatementsAsMultipleLinesString = function (node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function (conStatement) {
                html = html + "<p>" + conStatement.id + " : " + conStatement.description + "</p>";
            });
            return html;
        };

        $rootScope.getConfStatementsAsOneLineString = function (node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function (conStatement) {
                html = html + conStatement.id + " : " + conStatement.description;
            });
            return $sce.trustAsHtml(html);
        };

        $rootScope.getSegmentRefNodeName = function (node) {
            var seg = $rootScope.segmentsMap[node.ref.id];
            return node.position + "." + $rootScope.getSegmentLabel(seg) + ":" + seg.description;
        };

        $rootScope.getSegmentLabel = function (seg) {
//            var ext = $rootScope.getSegmentExtension(seg);
            return $rootScope.getLabel(seg.name, seg.ext);
        };

        $rootScope.getSegmentExtension = function (seg) {
            return $rootScope.getExtensionInLibrary(seg.id, $rootScope.igdocument.profile.segmentLibrary, "ext");
        };

        $rootScope.getDatatypeExtension = function (datatype) {
            return $rootScope.getExtensionInLibrary(datatype.id, $rootScope.igdocument.profile.datatypeLibrary, "ext");
        };

        $rootScope.getTableBindingIdentifier = function (table) {
            return $rootScope.getExtensionInLibrary(table.id, $rootScope.igdocument.profile.tableLibrary, "bindingIdentifier");
        };


        $rootScope.getDatatypeLabel = function (datatype) {
            if (datatype && datatype != null) {
//                var ext = $rootScope.getDatatypeExtension(datatype);
                return $rootScope.getLabel(datatype.name, datatype.ext);
            }
            return "";
        };

        $rootScope.getTableLabel = function (table) {
            if (table && table != null) {
                return $rootScope.getTableBindingIdentifier(table);
            }
            return "";
        };

        $rootScope.getExtensionInLibrary = function (id, library, propertyType) {
//            console.log("main Here id=" + id);
            if (propertyType && library.children) {
                for (var i = 0; i < library.children.length; i++) {
                    if (library.children[i].id === id) {
                        return library.children[i][propertyType];
                    }
                }
            }
            return "";
        };


        $rootScope.getGroupNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $rootScope.getFieldNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $rootScope.getComponentNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $rootScope.getDatatypeNodeName = function (node) {
            return node.position + "." + node.name;
        };

        $rootScope.onColumnToggle = function (item) {
            $rootScope.viewSettings.save();
        };

        $rootScope.getDatatypeLevelConfStatements = function (element) {
            return DatatypeService.getDatatypeLevelConfStatements(element);
        };

        $rootScope.getDatatypeLevelPredicates = function (element) {
            return DatatypeService.getDatatypeLevelPredicates(element);
        };

        $rootScope.isDatatypeSubDT = function (component) {
            return DatatypeService.isDatatypeSubDT(component, $rootScope.datatype);
        };


        $rootScope.setUsage = function (node) {
            ElementUtils.setUsage(node);
            $scope.recordChanged();
        };


        $rootScope.findDatatypeInLibrary = function (datatypeId, datatypeLibary) {
            if (datatypeLibary.children) {
                for (var i = 0; i < datatypeLibary.children.length; i++) {
                    if (datatypeLibary.children[i].id === id) {
                        return datatypeLibary.children[i];
                    }
                }
            }
            return null;
        };


        $rootScope.openConfirmLeaveDlg = function () {
            if($rootScope.modalInstance || $rootScope.modalInstance.opened){
                $rootScope.modalInstance.close();
            }
            $rootScope.modalInstance = $modal.open({
                templateUrl: 'ConfirmLeaveDlg.html',
                controller: 'ConfirmLeaveDlgCtrl',
                'size': 'md'
            });
            return $rootScope.modalInstance;
        };

        $rootScope.getOpenConfirmLeaveDlg =function(){
            return  $rootScope.modalInstance;
        }


    }]);

angular.module('igl').controller('LoginCtrl', ['$scope', '$modalInstance', 'user', function ($scope, $modalInstance, user) {
    $scope.user = user;

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.login = function () {
//        console.log("logging in...");
        $modalInstance.close($scope.user);
    };
}]);


angular.module('igl').controller('RichTextCtrl', ['$scope', '$modalInstance', 'editorTarget', function ($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function () {
        $modalInstance.close($scope.editorTarget);
    };
}]);


angular.module('igl').controller('InputTextCtrl', ['$scope', '$modalInstance', 'editorTarget', function ($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function () {
        $modalInstance.close($scope.editorTarget);
    };
}]);

angular.module('igl').controller('ConfirmLogoutCtrl', ["$scope", "$modalInstance", "$rootScope", "$http", function ($scope, $modalInstance, $rootScope, $http) {
    $scope.logout = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);


angular.module('igl').controller('ConfirmLeaveDlgCtrl', ["$scope", "$modalInstance", "$rootScope", "$http", function ($scope, $modalInstance, $rootScope, $http) {
    $scope.continue = function () {
        $modalInstance.close();
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
}]);
