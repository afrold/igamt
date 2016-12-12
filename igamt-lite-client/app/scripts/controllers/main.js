'use strict';

angular.module('igl').controller('MainCtrl', ['$document', '$scope', '$rootScope', 'i18n', '$location', 'userInfoService', '$modal', 'Restangular', '$filter', 'base64', '$http', 'Idle', 'IdleService', 'AutoSaveService', 'StorageService', 'ViewSettings', 'DatatypeService', 'SegmentService', 'MessageService', 'ElementUtils', 'SectionSvc',
    function($document, $scope, $rootScope, i18n, $location, userInfoService, $modal, Restangular, $filter, base64, $http, Idle, IdleService, AutoSaveService, StorageService, ViewSettings, DatatypeService, SegmentService, MessageService, ElementUtils, SectionSvc) {
        // This line fetches the info from the server if the user is currently
        // logged in.
        // If success, the app is updated according to the role.

        //     $(document).keydown(function(e) {
        //     var nodeName = e.target.nodeName.toLowerCase();

        //     if (e.which === 8) {
        //         if ((nodeName === 'input' && e.target.type === 'text') ||
        //             nodeName === 'textarea') {
        //             // do nothing
        //         } else {
        //             e.preventDefault();
        //         }
        //     }
        // });
        userInfoService.loadFromServer();
        $rootScope.loginDialog = null;

        $rootScope.csWidth = null;
        $rootScope.predWidth = null;
        $rootScope.tableWidth = null;
        $rootScope.commentWidth = null;
        $scope.viewSettings = ViewSettings;
        $rootScope.addedSegments = [];
        $rootScope.dateFormat= 'MM/dd/yyyy HH:mm';
        $scope.state = false;

        $scope.toggleState = function() {
            $scope.state = !$scope.state;
        };

        $scope.language = function() {
            return i18n.language;
        };

        $scope.setLanguage = function(lang) {
            i18n.setLanguage(lang);
        };

        $scope.activeWhen = function(value) {
            return value ? 'active' : '';
        };

        $scope.activeIfInList = function(value, pathsList) {
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
        $rootScope.setCardinalities = function(obj) {
            if (obj.usage === 'R') {
                obj.min = 1;
            } else if (obj.usage === 'X' || obj.usage === 'BW') {
                obj.min = 0;
                obj.max = 0;
            } else if (obj.usage === 'O') {
                obj.min = 0;

            }

        };


        $scope.path = function() {
            return $location.url();
        };

        $scope.login = function() {
            // ////console.log("in login");
            $scope.$emit('event:loginRequest', $scope.username, $scope.password);
        };

        $scope.loginReq = function() {
            // ////console.log("in loginReq");
            if ($rootScope.loginMessage()) {
                $rootScope.loginMessage().text = "";
                $rootScope.loginMessage().show = false;
            }
            $scope.$emit('event:loginRequired');
        };

        $scope.logout = function() {
            if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
                var modalInstance = $modal.open({
                    templateUrl: 'ConfirmLogout.html',
                    controller: 'ConfirmLogoutCtrl'
                });
                modalInstance.result.then(function() {
                    $scope.execLogout();
                }, function() {});
            } else {
                $scope.execLogout();
            }
        };

        $scope.execLogout = function() {
            userInfoService.setCurrentUser(null);
            $scope.username = $scope.password = null;
            $scope.$emit('event:logoutRequest');
            StorageService.remove(StorageService.IG_DOCUMENT);
            $rootScope.initMaps();
            $rootScope.igdocument = null;
            AutoSaveService.stop();
            if ($location.path() === '/compare') {
                $location.url('/compare');
            } else {
                $location.url('/ig');
            }
        };

        $scope.cancel = function() {
            $scope.$emit('event:loginCancel');
        };

        $scope.isAuthenticated = function() {
            return userInfoService.isAuthenticated();
        };

        $scope.isPending = function() {
            return userInfoService.isPending();
        };


        $scope.isSupervisor = function() {
            return userInfoService.isSupervisor();
        };

        $scope.isVendor = function() {
            return userInfoService.isAuthorizedVendor();
        };

        $scope.isAuthor = function() {
            return userInfoService.isAuthor();
        };

        $scope.isCustomer = function() {
            return userInfoService.isCustomer();
        };

        $scope.isAdmin = function() {
            return userInfoService.isAdmin();
        };


        $scope.getAccountID = function(accountId) {
            return userInfoService.getAccountID();
        };


        $scope.getRoleAsString = function() {
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

        $scope.getUsername = function() {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getUsername();
            }
            return '';
        };

        $rootScope.showLoginDialog = function(username, password) {

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
                    user: function() {
                        return { username: $scope.username, password: $scope.password };
                    }
                }
            });

            $rootScope.loginDialog.result.then(function(result) {
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

        $rootScope.$on('IdleStart', function() {
            closeModals();
            $rootScope.warning = $modal.open({
                templateUrl: 'warning-dialog.html',
                windowClass: 'modal-danger'
            });
        });

        $rootScope.$on('IdleEnd', function() {
            closeModals();
        });

        $rootScope.$on('IdleTimeout', function() {
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

        $scope.$on('Keepalive', function() {
            if ($scope.isAuthenticated()) {
                IdleService.keepAlive();
            }
        });

        $rootScope.$on('event:execLogout', function() {
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

        $rootScope.start = function() {
            closeModals();
            Idle.watch();
            $rootScope.started = true;
        };

        $rootScope.stop = function() {
            closeModals();
            Idle.unwatch();
            $rootScope.started = false;

        };
        $rootScope.setCardinalities = function(obj) {
            if (obj.usage === 'R') {
                obj.min = 1;
            } else if (obj.usage === 'X' || obj.usage === 'BW') {
                obj.min = 0;
                obj.max = 0;
            } else if (obj.usage === 'O') {
                obj.min = 0;

            }

        };


        $scope.checkForIE = function() {
            var BrowserDetect = {
                init: function() {
                    this.browser = this.searchString(this.dataBrowser) || 'An unknown browser';
                    this.version = this.searchVersion(navigator.userAgent) || this.searchVersion(navigator.appVersion) || 'an unknown version';
                    this.OS = this.searchString(this.dataOS) || 'an unknown OS';
                },
                searchString: function(data) {
                    for (var i = 0; i < data.length; i++) {
                        var dataString = data[i].string;
                        var dataProp = data[i].prop;
                        this.versionSearchString = data[i].versionSearch || data[i].identity;
                        if (dataString) {
                            if (dataString.indexOf(data[i].subString) !== -1) {
                                return data[i].identity;
                            }
                        } else if (dataProp) {
                            return data[i].identity;
                        }
                    }
                },
                searchVersion: function(dataString) {
                    var index = dataString.indexOf(this.versionSearchString);
                    if (index === -1) {
                        return;
                    }
                    return parseFloat(dataString.substring(index + this.versionSearchString.length + 1));
                },
                dataBrowser: [{
                    string: navigator.userAgent,
                    subString: 'Chrome',
                    identity: 'Chrome'
                }, {
                    string: navigator.userAgent,
                    subString: 'OmniWeb',
                    versionSearch: 'OmniWeb/',
                    identity: 'OmniWeb'
                }, {
                    string: navigator.vendor,
                    subString: 'Apple',
                    identity: 'Safari',
                    versionSearch: 'Version'
                }, {
                    prop: window.opera,
                    identity: 'Opera',
                    versionSearch: 'Version'
                }, {
                    string: navigator.vendor,
                    subString: 'iCab',
                    identity: 'iCab'
                }, {
                    string: navigator.vendor,
                    subString: 'KDE',
                    identity: 'Konqueror'
                }, {
                    string: navigator.userAgent,
                    subString: 'Firefox',
                    identity: 'Firefox'
                }, {
                    string: navigator.vendor,
                    subString: 'Camino',
                    identity: 'Camino'
                }, { // for newer Netscapes (6+)
                    string: navigator.userAgent,
                    subString: 'Netscape',
                    identity: 'Netscape'
                }, {
                    string: navigator.userAgent,
                    subString: 'MSIE',
                    identity: 'Explorer',
                    versionSearch: 'MSIE'
                }, {
                    string: navigator.userAgent,
                    subString: 'Gecko',
                    identity: 'Mozilla',
                    versionSearch: 'rv'
                }, { // for older Netscapes (4-)
                    string: navigator.userAgent,
                    subString: 'Mozilla',
                    identity: 'Netscape',
                    versionSearch: 'Mozilla'
                }],
                dataOS: [{
                    string: navigator.platform,
                    subString: 'Win',
                    identity: 'Windows'
                }, {
                    string: navigator.platform,
                    subString: 'Mac',
                    identity: 'Mac'
                }, {
                    string: navigator.userAgent,
                    subString: 'iPhone',
                    identity: 'iPhone/iPod'
                }, {
                    string: navigator.platform,
                    subString: 'Linux',
                    identity: 'Linux'
                }]

            };
            BrowserDetect.init();

            if (BrowserDetect.browser === 'Explorer') {
                var title = 'You are using Internet Explorer';
                var msg = 'This site is not yet optimized with Internet Explorer. For the best user experience, please use Chrome, Firefox or Safari. Thank you for your patience.';
                var btns = [
                    { result: 'ok', label: 'OK', cssClass: 'btn' }
                ];

                // $dialog.messageBox(title, msg, btns).open();


            }
        };


        $rootScope.readonly = false;
        $rootScope.igdocument = null; // current igdocument
        $rootScope.message = null; // current message
        $rootScope.datatype = null; // current datatype

        $rootScope.pages = ['list', 'edit', 'read'];
        $rootScope.context = { page: $rootScope.pages[0] };
        $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
        $rootScope.segmentsMap = {}; // Map for Segment;key:id, value:object
        $rootScope.datatypesMap = {}; // Map for Datatype; key:id, value:object
        $rootScope.tablesMap = {}; // Map for tables; key:id, value:object
        $rootScope.segments = []; // list of segments of the selected messages
        $rootScope.datatypes = []; // list of datatypes of the selected messages
        $rootScope.segmentPredicates = []; // list of segment level predicates of
        // the selected messages
        $rootScope.segmentConformanceStatements = []; // list of segment level
        // Conformance Statements of
        // the selected messages
        $rootScope.datatypePredicates = []; // list of segment level predicates of
        // the selected messages
        $rootScope.datatypeConformanceStatements = []; // list of segment level
        // Conformance Statements of
        // the selected messages
        $rootScope.tables = []; // list of tables of the selected messages
        $rootScope.postfixCloneTable = 'CA';
        $rootScope.newCodeFakeId = 0;
        $rootScope.newTableFakeId = 0;
        $rootScope.newPredicateFakeId = 0;
        $rootScope.newConformanceStatementFakeId = 0;
        $rootScope.segment = null;
        $rootScope.config = null;
        $rootScope.messagesData = [];
        $rootScope.messages = []; // list of messages
        $rootScope.customIgs = [];
        $rootScope.preloadedIgs = [];
        $rootScope.changes = {};
        $rootScope.generalInfo = { type: null, 'message': null };
        $rootScope.references = []; // collection of element referencing a datatype
        $rootScope.tmpReferences = [];
        // to delete
        $rootScope.section = {};
        $rootScope.conformanceStatementIdList = [];
        $rootScope.parentsMap = {};
        $rootScope.igChanged = false;


        $rootScope.messageTree = null;

        $scope.scrollbarWidth = 0;


        // TODO: remove
        $rootScope.selectIGDocumentTab = function(value) {
            // $rootScope.igdocumentTabs[0] = false;
            // $rootScope.igdocumentTabs[1] = false;
            // $rootScope.igdocumentTabs[2] = false;
            // $rootScope.igdocumentTabs[3] = false;
            // $rootScope.igdocumentTabs[4] = false;
            // $rootScope.igdocumentTabs[5] = false;
            // $rootScope.igdocumentTabs[value] = true;
        };

        $scope.getScrollbarWidth = function() {
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
        $rootScope.initMaps = function() {
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

        $rootScope.$watch(function() {
            return $location.path();
        }, function(newLocation, oldLocation) {
            $rootScope.setActive(newLocation);
        });


        $rootScope.api = function(value) {
            return value;
        };


        $rootScope.isActive = function(path) {
            return path === $rootScope.activePath;
        };

        $rootScope.setActive = function(path) {
            if (path === '' || path === '/') {
                $location.path('/home');
            } else {
                $rootScope.activePath = path;
            }
        };

        $rootScope.clearChanges = function(path) {
            // $rootScope.changes = {};
            $rootScope.igChanged = false;
        };

        $rootScope.hasChanges = function() {
            // return Object.getOwnPropertyNames($rootScope.changes).length !== 0;
            return $rootScope.igChanged;
        };

        $rootScope.recordChanged = function() {
            $rootScope.igChanged = true;
        };


        $rootScope.recordChange = function(object, changeType) {
            // var type = object.type;


            // if($rootScope.changes[type] === undefined){
            // $rootScope.changes[type] = {};
            // }

            // if($rootScope.changes[type][object.id] === undefined){
            // $rootScope.changes[type][object.id] = {};
            // }

            // if(changeType === "datatype"){
            // $rootScope.changes[type][object.id][changeType] = object[changeType].id;
            // }else{
            // $rootScope.changes[type][object.id][changeType] = object[changeType];
            // }

            // ////console.log("Change is " + $rootScope.changes[type][object.id][changeType]);
            $rootScope.recordChanged();
        };
        $rootScope.addHL7Table = function(selectedTableLibary, hl7Version) {
            var modalInstance = $modal.open({
                templateUrl: 'AddHL7TableOpenCtrl.html',
                controller: 'AddHL7TableOpenCtrl',
                windowClass: 'conformance-profiles-modal',
                resolve: {
                    selectedTableLibary: function() {
                        return selectedTableLibary;
                    },
                    hl7Version: function() {
                        return hl7Version;
                    }
                }
            });
            modalInstance.result.then(function() {}, function() {});
        };

        $rootScope.recordChange2 = function(type, id, attr, value) {
            // if($rootScope.changes[type] === undefined){
            // $rootScope.changes[type] = {};
            // }
            // if($rootScope.changes[type][id] === undefined){
            // $rootScope.changes[type][id] = {};
            // }
            // if(attr != null) {
            // $rootScope.changes[type][id][attr] = value;
            // }else {
            // $rootScope.changes[type][id] = value;
            // }
            $rootScope.recordChanged();
        };

        $rootScope.recordChangeForEdit = function(object, changeType) {
            // var type = object.type;

            // if($rootScope.changes[type] === undefined){
            // $rootScope.changes[type] = {};
            // }

            // if($rootScope.changes[type]['edit'] === undefined){
            // $rootScope.changes[type]['edit'] = {};
            // }

            // if($rootScope.changes[type]['edit'][object.id] === undefined){
            // $rootScope.changes[type]['edit'][object.id] = {};
            // }
            // $rootScope.changes[type]['edit'][object.id][changeType] = object[changeType];
            $rootScope.recordChanged();
        };

        $rootScope.recordChangeForEdit2 = function(type, command, id, valueType, value) {
            // var obj = $rootScope.findObjectInChanges(type, "add", id);
            // if (obj === undefined) { // not a new object
            // if ($rootScope.changes[type] === undefined) {
            // $rootScope.changes[type] = {};
            // }
            // if ($rootScope.changes[type][command] === undefined) {
            // $rootScope.changes[type][command] = [];
            // }
            // if (valueType !== type) {
            // var obj = $rootScope.findObjectInChanges(type, command, id);
            // if (obj === undefined) {
            // obj = {id: id};
            // $rootScope.changes[type][command].push(obj);
            // }
            // obj[valueType] = value;
            // } else {
            // $rootScope.changes[type][command].push(value);
            // }
            // }
            $rootScope.recordChanged();
        };

        $rootScope.recordDelete = function(type, command, id) {
            //            if (id < 0) { // new object
            //                $rootScope.removeObjectFromChanges(type, "add", id);
            //            } else {
            //                $rootScope.removeObjectFromChanges(type, "edit", id);
            // if ($rootScope.changes[type] === undefined) {
            // $rootScope.changes[type] = {};
            // }
            // if ($rootScope.changes[type][command] === undefined) {
            // $rootScope.changes[type][command] = [];
            // }

            // if ($rootScope.changes[type]["delete"] === undefined) {
            // $rootScope.changes[type]["delete"] = [];
            // }

            // $rootScope.changes[type]["delete"].push({id:id});
            //$rootScope.recordChanged();
            //}

            // if($rootScope.changes[type]) { //clean the changes object
            // if ($rootScope.changes[type]["add"] && $rootScope.changes[type]["add"].length
            // === 0) {
            // delete $rootScope.changes[type]["add"];
            // }
            // if ($rootScope.changes[type]["edit"] &&
            // $rootScope.changes[type]["edit"].length === 0) {
            // delete $rootScope.changes[type]["edit"];
            // }

            // if (Object.getOwnPropertyNames($rootScope.changes[type]).length === 0) {
            // delete $rootScope.changes[type];
            // }
            // }
        };


        $rootScope.findObjectInChanges = function(type, command, id) {
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


        $rootScope.isNewObject = function(type, command, id) {
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


        $rootScope.removeObjectFromChanges = function(type, command, id) {
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
        // Restangular.setResponseExtractor(function(response, operation) {
        // return response.data;
        // });

        $rootScope.showError = function(error) {
            var modalInstance = $modal.open({
                templateUrl: 'ErrorDlgDetails.html',
                controller: 'ErrorDetailsCtrl',
                resolve: {
                    error: function() {
                        return error;
                    }
                }
            });
            modalInstance.result.then(function(error) {
                $rootScope.error = error;
            }, function() {});
        };


        $rootScope.apply = function(label) { // FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.isFlavor = function(label) { // FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.getDatatype = function(id) {
            //console.log("WAAAAAAAAAAA HEREREEEEEEEEEEEE");
            return $rootScope.datatypesMap && $rootScope.datatypesMap[id];
        };

        $rootScope.calNextCSID = function(ext, flavorName) {
            var prefix = '';
            if (ext != null && ext !== '') {
                prefix = ext;
            } else if (flavorName != null && flavorName !== '') {
                prefix = flavorName;
            } else {
                prefix = 'Default';
            }

            return $rootScope.createNewFlavorName(prefix);
        };

        $rootScope.usedSegsLink = [];
        $rootScope.usedDtLink = [];
        $rootScope.usedVsLink = [];
        $rootScope.fillMaps = function(element) {
            if (element != undefined && element != null) {
                if (element.type === "message") {
                    for (var i = 0; i < element.children.length; i++) {
                        $rootScope.fillMaps(element.children[i]);
                    }
                } else if (element.type === "segmentRef") {
                    $rootScope.usedSegsLink.push(element.ref);
                } else if (element.type === "group" && element.children) {
                    for (var i = 0; i < element.children.length; i++) {
                        $rootScope.fillMaps(element.children[i]);
                    }
                } else if (element.type === "segment") {
                    for (var i = 0; i < element.fields.length; i++) {
                        $rootScope.fillMaps(element.fields[i]);
                    }
                } else if (element.type === "field") {
                    $rootScope.usedDtLink.push(element.datatype);
                    for (var i = 0; i < element.tables.length; i++) {
                        $rootScope.usedVsLink.push(element.tables[i]);
                    }

                } else if (element.type === "component") {
                    $rootScope.usedDtLink.push(element.datatype);
                    for (var i = 0; i < element.tables.length; i++) {
                        $rootScope.usedVsLink.push(element.tables[i]);
                    }
                } else if (element.type === "datatype") {
                    for (var i = 0; i < element.components.length; i++) {
                        $rootScope.fillMaps(element.components[i]);
                    }
                }
            }
        };



        $rootScope.processElement = function(element, parent) {
            try {
                if (element != undefined && element != null) {
                    if (element.type === "message") {
                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.conformanceStatements, function(cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.children, function(segmentRefOrGroup) {
                            $rootScope.processElement(segmentRefOrGroup, element);
                        });
                    } else if (element.type === "group" && element.children) {
                        if (parent) {
                            $rootScope.parentsMap[element.id] = parent;
                        }
                        element.children = $filter('orderBy')(element.children, 'position');
                        angular.forEach(element.children, function(segmentRefOrGroup) {
                            $rootScope.processElement(segmentRefOrGroup, element);
                        });
                    } else if (element.type === "segmentRef") {
                        if (parent) {
                            $rootScope.parentsMap[element.id] = parent;
                        }
                        $rootScope.processElement($rootScope.segmentsMap[element.ref.id], element);
                    } else if (element.type === "segment") {
                        element.fields = $filter('orderBy')(element.fields, 'position');
                        angular.forEach(element.conformanceStatements, function(cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.fields, function(field) {
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
                        angular.forEach(element.conformanceStatements, function(cs) {
                            if ($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
                        });
                        angular.forEach(element.components, function(component) {
                            $rootScope.processElement(component, element);
                        });
                    }
                }
            } catch (e) {
                throw e;
            }
        };


        $rootScope.filteredSegmentsList = [];
        $rootScope.filteredTablesList = [];
        $rootScope.filteredDatatypesList = [];
        $rootScope.selectedMessage = null;
        $rootScope.selectedSegment = null;

        $rootScope.processMessageTree = function(element, parent) {


            try {
                if (element != undefined && element != null) {
                    if (element.type === "message") {
                        $rootScope.selectedMessage = element;
                        $rootScope.filteredSegmentsList = [];
                        $rootScope.filteredTablesList = [];
                        $rootScope.filteredDatatypesList = [];
                        var m = {};
                        m.children = [];
                        $rootScope.messageTree = m;

                        angular.forEach(element.children, function(segmentRefOrGroup) {
                            $rootScope.processMessageTree(segmentRefOrGroup, m);
                        });

                    } else if (element.type === "group" && element.children) {
                        var g = {};
                        g.path = element.position + "[1]";
                        g.locationPath = element.name.substr(element.name.lastIndexOf('.') + 1) + '[1]';
                        g.obj = element;
                        g.children = [];
                        if (parent.path) {
                            g.path = parent.path + "." + g.path;
                            g.locationPath = parent.locationPath + "." + g.locationPath;
                        }
                        parent.children.push(g);
                        angular.forEach(element.children, function(segmentRefOrGroup) {
                            $rootScope.processMessageTree(segmentRefOrGroup, g);
                        });
                    } else if (element.type === "segmentRef") {
                        var s = {};
                        s.path = element.position + "[1]";
                        s.locationPath = $rootScope.segmentsMap[element.ref.id].name + '[1]';
                        s.obj = element;
                        s.children = [];
                        if (parent.path) {
                            s.path = parent.path + "." + element.position + "[1]";
                            s.locationPath = parent.locationPath + "." + s.locationPath;
                        }

                        if ($rootScope.segmentsMap[s.obj.ref.id] == undefined) {
                            throw new Error("Cannot find Segment[id=" + s.obj.ref.id + ", name= " + s.obj.ref.name + "]");
                        }
                        s.obj.ref.ext = $rootScope.segmentsMap[s.obj.ref.id].ext;
                        s.obj.ref.label = $rootScope.getLabel(s.obj.ref.name, s.obj.ref.ext);
                        parent.children.push(s);

                        var ref = $rootScope.segmentsMap[element.ref.id];
                        $rootScope.processMessageTree(ref, s);

                    } else if (element.type === "segment") {
                        if (!parent) {
                            var s = {};
                            s.obj = element;
                            s.path = element.name;
                            s.locationPath = element.name;
                            s.children = [];
                            parent = s;
                        }

                        $rootScope.filteredSegmentsList.push(element);
                        $rootScope.filteredSegmentsList = _.uniq($rootScope.filteredSegmentsList);

                        angular.forEach(element.fields, function(field) {
                            $rootScope.processMessageTree(field, parent);
                        });
                    } else if (element.type === "field") {
                        var f = {};
                        f.obj = element;
                        f.path = parent.path + "." + element.position + "[1]";
                        f.locationPath = parent.locationPath + "." + element.position + "[1]";
                        f.children = [];
                        var d = $rootScope.datatypesMap[f.obj.datatype.id];
                        if (d === undefined) {
                            throw new Error("Cannot find Data Type[id=" + f.obj.datatype.id + ", name= " + f.obj.datatype.name + "]");
                        }
                        f.obj.datatype.ext = $rootScope.datatypesMap[f.obj.datatype.id].ext;
                        f.obj.datatype.label = $rootScope.getLabel(f.obj.datatype.name, f.obj.datatype.ext);
                        // for (var i = 0; i < f.obj.tables.length; i++) {
                        //     if($rootScope.tablesMap[f.obj.tables[i].id]){
                        //         f.obj.tables[i].bindingIdentifier=$rootScope.tablesMap[f.obj.tables[i].id].bindingIdentifier;
                        //     }
                        // };
                        parent.children.push(f);

                        $rootScope.filteredDatatypesList.push($rootScope.datatypesMap[element.datatype.id]);
                        $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                        if (element.tables && element.tables.length > 0) {
                            angular.forEach(element.tables, function(table) {
                                $rootScope.filteredTablesList.push($rootScope.tablesMap[table.id]);
                            });
                            // $rootScope.filteredTablesList.push($rootScope.tablesMap[element.table.id]);
                        }
                        $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                        $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], f);
                    } else if (element.type === "component") {
                        var c = {};

                        c.obj = element;
                        c.path = parent.path + "." + element.position + "[1]";
                        c.locationPath = parent.locationPath + "." + element.position + "[1]";
                        c.children = [];
                        var d = $rootScope.datatypesMap[c.obj.datatype.id];
                        if (d === undefined) {
                            throw new Error("Cannot find Data Type[id=" + c.obj.datatype.id + ", name= " + c.obj.datatype.name + "]");
                        }
                        c.obj.datatype.ext = d.ext;
                        c.obj.datatype.label = $rootScope.getLabel(c.obj.datatype.name, c.obj.datatype.ext);
                        parent.children.push(c);
                        $rootScope.filteredDatatypesList.push($rootScope.datatypesMap[element.datatype.id]);
                        $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                        if (element.tables && element.tables.length > 0) {
                            angular.forEach(element.tables, function(table) {
                                $rootScope.filteredTablesList.push($rootScope.tablesMap[table.id]);
                            });
                            //$rootScope.filteredTablesList.push($rootScope.tablesMap[element.table.id]);
                        }
                        $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                        $rootScope.processMessageTree($rootScope.datatypesMap[element.datatype.id], c);
                    } else if (element.type === "datatype") {
                        if (!parent) {
                            var d = {};
                            d.obj = element;
                            d.path = element.name;
                            d.locationPath = element.name;
                            d.children = [];
                            parent = d;
                        }
                        angular.forEach(element.components, function(component) {
                            $rootScope.processMessageTree(component, parent);
                        });
                    }
                }
            } catch (e) {
                throw e;
            }
        };

        $rootScope.processSegmentsTree = function(element, parent) {
            //console.log(element);

            try {
                if (element.type === "segment") {
                    $rootScope.selectedSegment = element;
                    $rootScope.filteredTablesList = [];
                    $rootScope.filteredDatatypesList = [];

                    if (!parent) {
                        var s = {};
                        s.obj = element;
                        s.path = element.name;
                        s.children = [];
                        parent = s;
                    }
                    element.fields = $filter('orderBy')(element.fields, 'position');

                    angular.forEach(element.fields, function(field) {
                        $rootScope.processSegmentsTree(field, parent);
                    });
                } else if (element.type === "field") {
                    var f = {};
                    f.obj = element;
                    f.path = parent.path + "." + element.position + "[1]";
                    f.children = [];
                    parent.children.push(f);
                    $rootScope.filteredDatatypesList.push($rootScope.datatypesMap[element.datatype.id]);
                    $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                    if (element.tables && element.tables.length > 0) {
                        angular.forEach(element.tables, function(table) {
                            $rootScope.filteredTablesList.push($rootScope.tablesMap[table.id]);
                        });
                        //$rootScope.filteredTablesList.push($rootScope.tablesMap[element.table.id]);
                    }
                    $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                    $rootScope.processSegmentsTree($rootScope.datatypesMap[element.datatype.id], f);
                } else if (element.type === "component") {
                    var c = {};
                    c.obj = element;
                    c.path = parent.path + "." + element.position + "[1]";
                    c.children = [];
                    parent.children.push(c);
                    $rootScope.filteredDatatypesList.push($rootScope.datatypesMap[element.datatype.id]);
                    $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                    if (element.tables && element.tables.length > 0) {
                        angular.forEach(element.tables, function(table) {
                            $rootScope.filteredTablesList.push($rootScope.tablesMap[table.id]);
                        });
                        //$rootScope.filteredTablesList.push($rootScope.tablesMap[element.table.id]);
                    }
                    $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                    ////console.log($rootScope.filteredTablesList);
                    ////console.log($rootScope.filteredTablesList);

                    $rootScope.processSegmentsTree($rootScope.datatypesMap[element.datatype.id], c);
                } else if (element.type === "datatype") {

                    if (!parent) {
                        var d = {};
                        d.obj = element;
                        d.path = element.name;
                        d.children = [];
                        parent = d;
                    }

                    angular.forEach(element.components, function(component) {
                        $rootScope.processSegmentsTree(component, parent);
                    });
                }

            } catch (e) {
                throw e;
            }
        };

        $rootScope.checkedDatatype = null;

        $rootScope.rebuildTreeFromDatatype = function(data) {
            $rootScope.checkedDatatype = data;
            $rootScope.filteredTablesList = [];
            $rootScope.processDatatypeTree(data, null);
        }

        $rootScope.processDatatypeTree = function(element, parent) {

            ////console.log(element);

            try {
                if (element.type === "datatype") {
                    if (!parent) {
                        var d = {};
                        d.obj = element;
                        d.path = element.name;
                        d.children = [];
                        parent = d;
                    }
                    ////console.log("IN Data TYPE ")

                    angular.forEach(element.components, function(component) {
                        $rootScope.processDatatypeTree(component, parent);
                    });
                } else if (element.type === "component") {
                    var c = {};
                    c.obj = element;
                    c.path = parent.path + "." + element.position + "[1]";
                    c.children = [];
                    parent.children.push(c);
                    $rootScope.filteredDatatypesList.push($rootScope.datatypesMap[element.datatype.id]);
                    $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                    if (element.tables && element.tables != null && element.tables.length > 0) {
                        angular.forEach(element.tables, function(table) {
                            $rootScope.filteredTablesList.push($rootScope.tablesMap[table.id]);
                        });
                        //$rootScope.filteredTablesList.push($rootScope.tablesMap[element.table.id]);
                    }
                    $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                    $rootScope.processDatatypeTree($rootScope.datatypesMap[element.datatype.id], c);
                }

            } catch (e) {
                throw e;
            }
        };

        $rootScope.createNewFlavorName = function(label) {
            if ($rootScope.igdocument != null) {
                if ($rootScope.igdocument.metaData["ext"] === null || $rootScope.igdocument.metaData["ext"] === '') {
                    return label + "_" + (Math.floor(Math.random() * 10000000) + 1);
                } else {
                    return label + "_" + $rootScope.igdocument.metaData["ext"] + "_" + (Math.floor(Math.random() * 10000000) + 1);
                }
            } else {
                return null;
            }
        };

        $rootScope.createNewExtension = function(ext) {
            if ($rootScope.igdocument != null) {
                var rand = (Math.floor(Math.random() * 10000000) + 1);
                if ($rootScope.igdocument.metaData["ext"] === null) {
                    return ext != null && ext != "" ? ext + "_" + rand : rand;
                } else {
                    return ext != null && ext != "" ? ext + "_" + $rootScope.igdocument.metaData["ext"] + "_" + rand + 1 : rand + 1;
                }
            } else {
                return null;
            }
        };

        $rootScope.isSubComponent = function(node) {
            node.type === 'component' && $rootScope.parentsMap[node.id] && $rootScope.parentsMap[node.id].type === 'component';
        };

        $rootScope.findDatatypeRefs = function(datatype, obj, path, target) {
            if (obj != null && obj != undefined) {
                if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
                    if (obj.datatype.id === datatype.id) {
                        var found = angular.copy(obj);
                        found.path = path;
                        found.target = angular.copy(target);
                        found.datatypeLink = angular.copy(obj.datatype);
                        $rootScope.references.push(found);
                    }
                    $rootScope.findDatatypeRefs(datatype, $rootScope.datatypesMap[obj.datatype.id], path, target);
                } else if (angular.equals(obj.type, 'segment')) {
                    angular.forEach(obj.fields, function(field) {
                        $rootScope.findDatatypeRefs(datatype, field, path + "-" + field.position, target);
                    });
                } else if (angular.equals(obj.type, 'datatype')) {
                    if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
                        angular.forEach(obj.components, function(component) {
                            $rootScope.findDatatypeRefs(datatype, component, path + "." + component.position, target);
                        });
                    }
                }
            }
        };

        $rootScope.findSegmentRefs = function(segment, obj, path, positionPath, target) {
            if (obj != null && obj != undefined) {
                if (angular.equals(obj.type, 'message')) {
                    angular.forEach(obj.children, function(child) {
                        $rootScope.findSegmentRefs(segment, child, obj.name + '-' + obj.identifier, obj.name + '-' + obj.identifier, target);
                    });
                } else if (angular.equals(obj.type, 'group')){
                    angular.forEach(obj.children, function(child) {
                        var groupNames = obj.name.split(".");
                        var groupName = groupNames[groupNames.length - 1];
                        $rootScope.findSegmentRefs(segment, child, path + '.' + groupName, positionPath + '.' + obj.position, target);
                    });
                } else if (angular.equals(obj.type, 'segmentRef')) {
                    if (obj.ref.id === segment.id) {
                        var found = angular.copy(obj);
                        found.path = path + '.' + segment.name;
                        found.positionPath = positionPath + '.' + obj.position;
                        found.target = angular.copy(target);
                        found.segmentLink = angular.copy(obj.ref);
                        $rootScope.references.push(found);
                    }
                }
            }
        };

        $rootScope.findTableRefs = function(table, obj, path, target) {
            if (obj != null && obj != undefined) {
                if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
                    if (obj.tables != undefined && obj.tables.length > 0) {
                        angular.forEach(obj.tables, function(tableInside) {
                            if (tableInside.id === table.id) {
                                var found = angular.copy(obj);
                                found.path = path;
                                found.target = angular.copy(target);
                                found.tableLink = angular.copy(tableInside);
                                $rootScope.references.push(found);
                            }
                        });
                    }
                    // $rootScope.findTableRefs(table, $rootScope.datatypesMap[obj.datatype.id], path);
                } else if (angular.equals(obj.type, 'segment')) {
                    angular.forEach(obj.fields, function(field) {
                        $rootScope.findTableRefs(table, field, path + "-" + field.position, target);
                    });
                } else if (angular.equals(obj.type, 'datatype')) {
                    if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
                        angular.forEach(obj.components, function(component) {
                            $rootScope.findTableRefs(table, component, path + "." + component.position, target);
                        });
                    }
                }
            }
        };

        $rootScope.saveBindingForSegment = function() {
            var segmentBindingUpdateParameterList = [];

            for (var q = 0; q < $rootScope.references.length; q++) {
                var ref = $rootScope.references[q];
                if (ref.segmentLink.isChanged) {
                    ref.segmentLink.isNew = null;
                    ref.segmentLink.isChanged = null;
                    var segmentBindingUpdateParameter = {};
                    segmentBindingUpdateParameter.messageId = ref.target.id;
                    segmentBindingUpdateParameter.newSegmentLink = angular.copy(ref.segmentLink);
                    segmentBindingUpdateParameter.positionPath = ref.positionPath;
                    segmentBindingUpdateParameterList.push(segmentBindingUpdateParameter);

                    var message = angular.copy($rootScope.messagesMap[segmentBindingUpdateParameter.messageId]);
                    var paths = segmentBindingUpdateParameter.positionPath.split('.');
                    $rootScope.updateSegmentBinding(message.children, paths, segmentBindingUpdateParameter.newSegmentLink);

                    $rootScope.messagesMap[message.id] = message;
                    var oldMessage = _.find($rootScope.igdocument.profile.messages.children, function(msg) {
                        return msg.id == message.id;
                    });

                    var index = $rootScope.igdocument.profile.messages.children.indexOf(oldMessage);
                    if (index > -1) $rootScope.igdocument.profile.messages.children[index] = message;

                }
            }

            MessageService.updateSegmentBinding(segmentBindingUpdateParameterList).then(function(result) {}, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

            $rootScope.references = [];
            angular.forEach($rootScope.igdocument.profile.messages.children, function(message) {
                $rootScope.findSegmentRefs($rootScope.segment, message, '', '', message);
            });

        };

        $rootScope.updateSegmentBinding = function (children, paths, newSegmentLink){
            var position = parseInt(paths[1]);
            var child = $rootScope.findChildByPosition(children, position);

            if(paths.length == 2) {
                if(child.type === "segmentRef"){
                    child.ref = newSegmentLink;
                }
            }else{
                $rootScope.updateSegmentBinding(child.children, paths.slice(1), newSegmentLink);
            }
        };

        $rootScope.findChildByPosition = function (children, position){
            for (var i = 0; i < children.length; i++) {
                if(children[i].position == position) return children[i];
            }
            return null;
        };

        $rootScope.saveBindingForDatatype = function() {
            var datatypeUpdateParameterList = [];
            var segmentUpdateParameterList = [];

            for (var q = 0; q < $rootScope.references.length; q++) {
                var ref = $rootScope.references[q];
                if (ref.datatypeLink.isNew) {
                    if (ref.type == 'component') {
                        var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
                        ref.datatypeLink.isNew = null;
                        ref.datatypeLink.isChanged = null;
                        var newDatatypeLink = angular.copy(ref.datatypeLink);
                        var targetComponent = angular.copy(ref);
                        targetComponent.target = null;
                        targetComponent.path = null;
                        targetComponent.datatypeLink = null;

                        var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
                            return component.position == targetComponent.position;
                        });
                        if (toBeUpdateComponent) toBeUpdateComponent.datatype = newDatatypeLink;
                        $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
                        var oldDatatype = _.find($rootScope.datatypes, function(dt) {
                            return dt.id == targetDatatype.id;
                        });
                        var index = $rootScope.datatypes.indexOf(oldDatatype);
                        if (index > -1) $rootScope.datatypes[index] = targetDatatype;

                        var datatypeUpdateParameter = {};
                        datatypeUpdateParameter.datatypeId = targetDatatype.id;
                        datatypeUpdateParameter.componentId = targetComponent.id;
                        datatypeUpdateParameter.datatypeLink = newDatatypeLink;
                        datatypeUpdateParameterList.push(datatypeUpdateParameter);
                    } else if (ref.type == 'field') {
                        var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
                        ref.datatypeLink.isNew = null;
                        ref.datatypeLink.isChanged = null;
                        var newDatatypeLink = angular.copy(ref.datatypeLink);
                        var targetField = angular.copy(ref);
                        targetField.target = null;
                        targetField.path = null;
                        targetField.datatypeLink = null;

                        var toBeUpdateField = _.find(targetSegment.fields, function(field) {
                            return field.position == targetField.position;
                        });
                        if (toBeUpdateField) toBeUpdateField.datatype = newDatatypeLink;
                        $rootScope.segmentsMap[targetSegment.id] = targetSegment;
                        var oldSegment = _.find($rootScope.segments, function(seg) {
                            return seg.id == targetSegment.id;
                        });
                        var index = $rootScope.segments.indexOf(oldSegment);
                        if (index > -1) $rootScope.segments[index] = targetSegment;

                        var segmentUpdateParameter = {};
                        segmentUpdateParameter.segmentId = targetSegment.id;
                        segmentUpdateParameter.fieldId = targetField.id;
                        segmentUpdateParameter.datatypeLink = newDatatypeLink;
                        segmentUpdateParameterList.push(segmentUpdateParameter);
                    }
                } else if (ref.datatypeLink.isChanged) {
                    if (ref.type == 'component') {
                        var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
                        ref.datatypeLink.isNew = null;
                        ref.datatypeLink.isChanged = null;
                        var newDatatypeLink = angular.copy(ref.datatypeLink);
                        var targetComponent = angular.copy(ref);
                        targetComponent.target = null;
                        targetComponent.path = null;
                        targetComponent.datatypeLink = null;

                        var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
                            return component.position == targetComponent.position;
                        });
                        if (toBeUpdateComponent) {

                            if (toBeUpdateComponent.datatype.id == $rootScope.datatype.id) {
                                toBeUpdateComponent.datatype = newDatatypeLink;
                            }

                        }
                        $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
                        var oldDatatype = _.find($rootScope.datatypes, function(dt) {
                            return dt.id == targetDatatype.id;
                        });
                        var index = $rootScope.datatypes.indexOf(oldDatatype);
                        if (index > -1) $rootScope.datatypes[index] = targetDatatype;

                        var datatypeUpdateParameter = {};
                        datatypeUpdateParameter.datatypeId = targetDatatype.id;
                        datatypeUpdateParameter.componentId = targetComponent.id;
                        datatypeUpdateParameter.datatypeLink = newDatatypeLink;
                        datatypeUpdateParameter.key = $rootScope.table.id;
                        datatypeUpdateParameterList.push(datatypeUpdateParameter);
                    } else if (ref.type == 'field') {
                        var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
                        ref.datatypeLink.isNew = null;
                        ref.datatypeLink.isChanged = null;
                        var newDatatypeLink = angular.copy(ref.datatypeLink);
                        var targetField = angular.copy(ref);
                        targetField.target = null;
                        targetField.path = null;
                        targetField.datatypeLink = null;

                        var toBeUpdateField = _.find(targetSegment.fields, function(field) {
                            return field.position == targetField.position;
                        });
                        if (toBeUpdateField) {
                            if (toBeUpdateField.datatype.id == $rootScope.datatype.id) {
                                toBeUpdateField.datatype = newDatatypeLink;
                            }

                        }
                        $rootScope.segmentsMap[targetSegment.id] = targetSegment;
                        var oldSegment = _.find($rootScope.segments, function(seg) {
                            return seg.id == targetSegment.id;
                        });
                        var index = $rootScope.segments.indexOf(oldSegment);
                        if (index > -1) $rootScope.segments[index] = targetSegment;

                        var segmentUpdateParameter = {};
                        segmentUpdateParameter.segmentId = targetSegment.id;
                        segmentUpdateParameter.fieldId = targetField.id;
                        segmentUpdateParameter.datatypeLink = newDatatypeLink;
                        segmentUpdateParameter.key = $rootScope.datatype.id;
                        segmentUpdateParameterList.push(segmentUpdateParameter);
                    }
                }
            }

            SegmentService.updateDatatypeBinding(segmentUpdateParameterList).then(function(result) {}, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

            DatatypeService.updateDatatypeBinding(datatypeUpdateParameterList).then(function(result) {}, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

            $rootScope.references = [];
            angular.forEach($rootScope.segments, function(segment) {
                $rootScope.findDatatypeRefs($rootScope.datatype, segment, $rootScope.getSegmentLabel(segment), segment);
            });
            angular.forEach($rootScope.datatypes, function(dt) {
                $rootScope.findDatatypeRefs($rootScope.datatype, dt, $rootScope.getDatatypeLabel(dt), dt);
            });
        };


        $rootScope.saveBindingForValueSet = function() {
            var datatypeUpdateParameterList = [];
            var segmentUpdateParameterList = [];

            for (var q = 0; q < $rootScope.references.length; q++) {
                var ref = $rootScope.references[q];
                if (ref.tableLink.isNew) {
                    if (ref.type == 'component') {
                        var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
                        ref.tableLink.isNew = null;
                        ref.tableLink.isChanged = null;
                        var newTableLink = angular.copy(ref.tableLink);
                        var targetComponent = angular.copy(ref);
                        targetComponent.target = null;
                        targetComponent.path = null;
                        targetComponent.tableLink = null;

                        var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
                            return component.position == targetComponent.position;
                        });
                        if (toBeUpdateComponent) toBeUpdateComponent.tables.push(newTableLink);
                        $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
                        var oldDatatype = _.find($rootScope.datatypes, function(dt) {
                            return dt.id == targetDatatype.id;
                        });
                        var index = $rootScope.datatypes.indexOf(oldDatatype);
                        if (index > -1) $rootScope.datatypes[index] = targetDatatype;

                        var datatypeUpdateParameter = {};
                        datatypeUpdateParameter.datatypeId = targetDatatype.id;
                        datatypeUpdateParameter.componentId = targetComponent.id;
                        datatypeUpdateParameter.tableLink = newTableLink;
                        datatypeUpdateParameterList.push(datatypeUpdateParameter);
                    } else if (ref.type == 'field') {
                        var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
                        ref.tableLink.isNew = null;
                        ref.tableLink.isChanged = null;
                        var newTableLink = angular.copy(ref.tableLink);
                        var targetField = angular.copy(ref);
                        targetField.target = null;
                        targetField.path = null;
                        targetField.tableLink = null;

                        var toBeUpdateField = _.find(targetSegment.fields, function(field) {
                            return field.position == targetField.position;
                        });
                        if (toBeUpdateField) toBeUpdateField.tables.push(newTableLink);
                        $rootScope.segmentsMap[targetSegment.id] = targetSegment;
                        var oldSegment = _.find($rootScope.segments, function(seg) {
                            return seg.id == targetSegment.id;
                        });
                        var index = $rootScope.segments.indexOf(oldSegment);
                        if (index > -1) $rootScope.segments[index] = targetSegment;

                        var segmentUpdateParameter = {};
                        segmentUpdateParameter.segmentId = targetSegment.id;
                        segmentUpdateParameter.fieldId = targetField.id;
                        segmentUpdateParameter.tableLink = newTableLink;
                        segmentUpdateParameterList.push(segmentUpdateParameter);
                    }
                } else if (ref.tableLink.isChanged) {
                    if (ref.type == 'component') {
                        var targetDatatype = angular.copy($rootScope.datatypesMap[ref.target.id]);
                        ref.tableLink.isNew = null;
                        ref.tableLink.isChanged = null;
                        var newTableLink = angular.copy(ref.tableLink);
                        var targetComponent = angular.copy(ref);
                        targetComponent.target = null;
                        targetComponent.path = null;
                        targetComponent.tableLink = null;

                        var toBeUpdateComponent = _.find(targetDatatype.components, function(component) {
                            return component.position == targetComponent.position;
                        });
                        if (toBeUpdateComponent) {
                            for (var i = 0; i < toBeUpdateComponent.tables.length; i++) {
                                if (toBeUpdateComponent.tables[i].id == $rootScope.table.id) {
                                    toBeUpdateComponent.tables[i] = newTableLink;
                                }
                            }
                        }
                        $rootScope.datatypesMap[targetDatatype.id] = targetDatatype;
                        var oldDatatype = _.find($rootScope.datatypes, function(dt) {
                            return dt.id == targetDatatype.id;
                        });
                        var index = $rootScope.datatypes.indexOf(oldDatatype);
                        if (index > -1) $rootScope.datatypes[index] = targetDatatype;

                        var datatypeUpdateParameter = {};
                        datatypeUpdateParameter.datatypeId = targetDatatype.id;
                        datatypeUpdateParameter.componentId = targetComponent.id;
                        datatypeUpdateParameter.tableLink = newTableLink;
                        datatypeUpdateParameter.key = $rootScope.table.id;
                        datatypeUpdateParameterList.push(datatypeUpdateParameter);
                    } else if (ref.type == 'field') {
                        var targetSegment = angular.copy($rootScope.segmentsMap[ref.target.id]);
                        ref.tableLink.isNew = null;
                        ref.tableLink.isChanged = null;
                        var newTableLink = angular.copy(ref.tableLink);
                        var targetField = angular.copy(ref);
                        targetField.target = null;
                        targetField.path = null;
                        targetField.tableLink = null;

                        var toBeUpdateField = _.find(targetSegment.fields, function(field) {
                            return field.position == targetField.position;
                        });
                        if (toBeUpdateField) {
                            for (var i = 0; i < toBeUpdateField.tables.length; i++) {
                                if (toBeUpdateField.tables[i].id == $rootScope.table.id) {
                                    toBeUpdateField.tables[i] = newTableLink;
                                }
                            }
                        }
                        $rootScope.segmentsMap[targetSegment.id] = targetSegment;
                        var oldSegment = _.find($rootScope.segments, function(seg) {
                            return seg.id == targetSegment.id;
                        });
                        var index = $rootScope.segments.indexOf(oldSegment);
                        if (index > -1) $rootScope.segments[index] = targetSegment;

                        var segmentUpdateParameter = {};
                        segmentUpdateParameter.segmentId = targetSegment.id;
                        segmentUpdateParameter.fieldId = targetField.id;
                        segmentUpdateParameter.tableLink = newTableLink;
                        segmentUpdateParameter.key = $rootScope.table.id;
                        segmentUpdateParameterList.push(segmentUpdateParameter);
                    }
                }
            }

            SegmentService.updateTableBinding(segmentUpdateParameterList).then(function(result) {}, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

            DatatypeService.updateTableBinding(datatypeUpdateParameterList).then(function(result) {}, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

            $rootScope.references = [];
            angular.forEach($rootScope.segments, function(segment) {
                $rootScope.findTableRefs($rootScope.table, segment, $rootScope.getSegmentLabel(segment), segment);
            });
            angular.forEach($rootScope.datatypes, function(dt) {
                $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt), dt);
            });
        };

        $rootScope.genRegex = function(format) {
            if (format === 'YYYY') {
                return '([0-9]{4})(((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?)?)?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMM') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?)?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMMDD') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMMDDhh') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMMDDhhmm') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMMDDhhmmss') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])(\\.[0-9]{1,4})?((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYYMMDDhhmmss.sss') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9]{1,4}((\\+|\\-)[0-9]{4})?';
            } else if (format === 'YYYY+-ZZZZ') {
                return '([0-9]{4})(((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?)?)?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMM+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))(((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?)?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMMDD+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))((([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?)?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMMDDhh+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))(([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?)?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMMDDhhmm+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])(([0-5][0-9])(\\.[0-9]{1,4})?)?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMMDDhhmmss+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])(\\.[0-9]{1,4})?(\\+|\\-)[0-9]{4}';
            } else if (format === 'YYYYMMDDhhmmss.sss+-ZZZZ') {
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9]{1,4}(\\+|\\-)[0-9]{4}';
            } else if (format === 'ISO-compliant OID') {
                return '[0-2](\\.(0|[1-9][0-9]*))*';
            } else if (format === 'Alphanumeric') {
                return '^[a-zA-Z0-9]*$';
            } else if (format === 'Positive Integer') {
                return '^[1-9]\d*$';
            }

            return format;
        };

        $rootScope.isAvailableDTForTable = function(dt) {
            if (dt != undefined) {
                if (dt.name === 'IS' || dt.name === 'ID' || dt.name === 'CWE' || dt.name === 'CNE' || dt.name === 'CE') return true;

                if (dt.components != undefined && dt.components.length > 0) return true;

            }
            return false;
        };

        $rootScope.validateNumber = function(event) {
            var key = window.event ? event.keyCode : event.which;
            if (event.keyCode == 8 || event.keyCode == 46 || event.keyCode == 37 || event.keyCode == 39) {
                return true;
            } else if (key < 48 || key > 57) {
                return false;
            } else return true;
        };


        $rootScope.displayLocationForDatatype = function(dt, constraintTarget) {
            var position = constraintTarget.substring(0, constraintTarget.indexOf('['));
            var component = _.find(dt.components, function(c) {
                return c.position == position;
            });
            if (component) return dt.name + "." + position + " (" + component.name + ")";
            return dt.name;
        };

        $rootScope.displayLocationForSegment = function(segment, constraintTarget) {
            var position = constraintTarget.substring(0, constraintTarget.indexOf('['));
            var field = _.find(segment.fields, function(f) {
                return f.position == position;
            });
            if (field) return segment.name + "-" + position + " (" + field.name + ")";
            return segment.name;
        };

        $rootScope.generateCompositeConformanceStatement = function(compositeType, firstConstraint, secondConstraint, constraints) {
            var cs = null;
            if (compositeType === 'AND' || compositeType === 'OR' || compositeType === 'XOR') {
                var firstConstraintAssertion = firstConstraint.assertion.replace("<Assertion>", "");
                firstConstraintAssertion = firstConstraintAssertion.replace("</Assertion>", "");
                var secondConstraintAssertion = secondConstraint.assertion.replace("<Assertion>", "");
                secondConstraintAssertion = secondConstraintAssertion.replace("</Assertion>", "");

                cs = {
                    id: new ObjectId().toString(),
                    constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + compositeType + ' [' + secondConstraint.description + ']',
                    assertion: '<Assertion><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Assertion>'
                };
            } else if(compositeType === 'IFTHEN'){
                var firstConstraintAssertion = firstConstraint.assertion.replace("<Assertion>", "");
                firstConstraintAssertion = firstConstraintAssertion.replace("</Assertion>", "");
                var secondConstraintAssertion = secondConstraint.assertion.replace("<Assertion>", "");
                secondConstraintAssertion = secondConstraintAssertion.replace("</Assertion>", "");

                cs = {
                    id: new ObjectId().toString(),
                    constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: 'IF [' + firstConstraint.description + '] THEN [' + secondConstraint.description + ']',
                    assertion: '<Assertion><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Assertion>'
                };
            } else if (compositeType === 'FORALL' || compositeType === 'EXIST') {
                var forALLExistId = compositeType;
                var forALLExistAssertion = '';
                var forALLExistDescription = compositeType;
                var forALLExistConstraintTarget = '';

                angular.forEach(constraints, function(c) {
                    forALLExistAssertion = forALLExistAssertion + c.assertion.replace("<Assertion>", "").replace("</Assertion>", "");
                    forALLExistDescription = forALLExistDescription + '[' + c.description + ']';
                    forALLExistId = forALLExistId + '(' + c.constraintId + ')';
                    forALLExistConstraintTarget = c.constraintTarget;
                });

                cs = {
                    id: new ObjectId().toString(),
                    constraintId: forALLExistId,
                    constraintTarget: forALLExistConstraintTarget,
                    description: forALLExistDescription,
                    assertion: '<Assertion><' + compositeType + '>' + forALLExistAssertion + '</' + compositeType + '></Assertion>'
                };
            }
            return cs;
        };



        $rootScope.generateCompositePredicate = function(compositeType, firstConstraint, secondConstraint, constraints) {
            var cp = null;
            if (compositeType === 'AND' || compositeType === 'OR' || compositeType === 'XOR') {
                var firstConstraintAssertion = firstConstraint.assertion.replace("<Condition>", "");
                firstConstraintAssertion = firstConstraintAssertion.replace("</Condition>", "");
                var secondConstraintAssertion = secondConstraint.assertion.replace("<Condition>", "");
                secondConstraintAssertion = secondConstraintAssertion.replace("</Condition>", "");

                cp = {
                    id: new ObjectId().toString(),
                    constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: '[' + firstConstraint.description + '] ' + compositeType + ' [' + secondConstraint.description + ']',
                    trueUsage: '',
                    falseUsage: '',
                    assertion: '<Condition><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Condition>'
                };
            } else if (compositeType === 'IFTHEN') {
                var firstConstraintAssertion = firstConstraint.assertion.replace("<Condition>", "");
                firstConstraintAssertion = firstConstraintAssertion.replace("</Condition>", "");
                var secondConstraintAssertion = secondConstraint.assertion.replace("<Condition>", "");
                secondConstraintAssertion = secondConstraintAssertion.replace("</Condition>", "");

                cp = {
                    id: new ObjectId().toString(),
                    constraintId: compositeType + '(' + firstConstraint.constraintId + ',' + secondConstraint.constraintId + ')',
                    constraintTarget: firstConstraint.constraintTarget,
                    description: 'IF [' + firstConstraint.description + '] THEN [' + secondConstraint.description + ']',
                    trueUsage: '',
                    falseUsage: '',
                    assertion: '<Condition><' + compositeType + '>' + firstConstraintAssertion + secondConstraintAssertion + '</' + compositeType + '></Condition>'
                };
            } else if (compositeType === 'FORALL' || compositeType === 'EXIST') {
                var forALLExistId = compositeType;
                var forALLExistAssertion = '';
                var forALLExistDescription = compositeType;
                var forALLExistConstraintTarget = '';

                angular.forEach(constraints, function(c) {
                    forALLExistAssertion = forALLExistAssertion + c.assertion.replace("<Condition>", "").replace("</Condition>", "");
                    forALLExistDescription = forALLExistDescription + '[' + c.description + ']';
                    forALLExistId = forALLExistId + '(' + c.constraintId + ')';
                    forALLExistConstraintTarget = c.constraintTarget;
                });

                cp = {
                    id: new ObjectId().toString(),
                    constraintId: forALLExistId,
                    constraintTarget: forALLExistConstraintTarget,
                    description: forALLExistDescription,
                    assertion: '<Condition><' + compositeType + '>' + forALLExistAssertion + '</' + compositeType + '></Condition>'
                };
            }
            return cp;
        };

        $rootScope.generateFreeTextConformanceStatement = function(positionPath, newConstraint) {
            var cs = {
                id: new ObjectId().toString(),
                constraintId: newConstraint.constraintId,
                constraintTarget: positionPath,
                description: newConstraint.freeText,
                assertion: null
            };

            return cs;
        };

        $rootScope.generateConformanceStatement = function(positionPath, newConstraint) {
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
                        assertion: '<Assertion><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Assertion>'
                    };
                } else {
                    console.log(newConstraint.value);
                    if(newConstraint.value =='^~\\&'){
                        cs = {
                            id: new ObjectId().toString(),
                            constraintId: newConstraint.constraintId,
                            constraintTarget: positionPath,
                            description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' \'^~\\&amp;\'.',
                            assertion: '<Assertion><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"^~\\&amp;\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Assertion>'
                        };
                    }else {
                        var componetsList = newConstraint.value.split("^");
                        var assertionScript = "";
                        var componentPosition = 0;

                        angular.forEach(componetsList, function(componentValue) {
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
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value2 + '\'.',
                        assertion: '<Assertion><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Assertion>'
                    };
                } else {
                    cs = {
                        id: new ObjectId().toString(),
                        constraintId: newConstraint.constraintId,
                        constraintTarget: positionPath,
                        description: 'The value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value + '\'.',
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


            if (newConstraint.verb.includes('NOT') || newConstraint.verb.includes('not')) {
                cs.assertion = cs.assertion.replace("<Assertion>", "<Assertion><NOT>");
                cs.assertion = cs.assertion.replace("</Assertion>", "</NOT></Assertion>");
            }

            return cs;
        };

        $rootScope.generateFreeTextPredicate = function(positionPath, newConstraint) {
            var cp = {
                id: new ObjectId().toString(),
                constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                constraintTarget: positionPath,
                description: newConstraint.freeText,
                trueUsage: newConstraint.trueUsage,
                falseUsage: newConstraint.falseUsage,
                assertion: null
            };
            return cp;
        };

        $rootScope.generatePredicate = function(positionPath, newConstraint) {
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
                        assertion: '<Condition><PlainText Path=\"' + newConstraint.position_1 + '\" Text=\"' + newConstraint.value + '\" IgnoreCase=\"' + newConstraint.ignoreCase + '\"/></Condition>'
                    };
                } else {
                    var componetsList = newConstraint.value.split("^");
                    var assertionScript = "";
                    var componentPosition = 0;

                    angular.forEach(componetsList, function(componentValue) {
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
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value2 + '\'.',
                        trueUsage: newConstraint.trueUsage,
                        falseUsage: newConstraint.falseUsage,
                        assertion: '<Condition><Format Path=\"' + newConstraint.position_1 + '\" Regex=\"' + newConstraint.value2 + '\"/></Condition>'
                    };
                } else {
                    cp = {
                        id: new ObjectId().toString(),
                        constraintId: 'CP_' + positionPath + '_' + $rootScope.newPredicateFakeId,
                        constraintTarget: positionPath,
                        description: 'If the value of ' + newConstraint.location_1 + ' ' + newConstraint.verb + ' formatted with \'' + newConstraint.value + '\'.',
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

            if (newConstraint.verb.includes('NOT') || newConstraint.verb.includes('not')) {
                cp.assertion = cp.assertion.replace("<Condition>", "<Condition><NOT>");
                cp.assertion = cp.assertion.replace("</Condition>", "</NOT></Condition>");
            }

            return cp;
        };


        $rootScope.erorrForComplexConfStatement = function(newComplexConstraintId, targetComplexId, compositeType, firstConstraint, secondConstraint, constraints) {
            if ($rootScope.isEmptyCompositeType(compositeType)) return true;
            if ($rootScope.isEmptyComplexConstraintID(newComplexConstraintId)) return true;
            if ($rootScope.isDuplicatedComplexConstraintID(newComplexConstraintId, targetComplexId)) return true;

            if (compositeType == 'FORALL' || compositeType == 'EXIST') {
                if (constraints.length < 2) return true;
            } else {
                if (firstConstraint == null) return true;
                if (secondConstraint == null) return true;
            }

            return false;
        };

        $rootScope.erorrForComplexPredicate = function(compositeType, firstConstraint, secondConstraint, complexConstraintTrueUsage, complexConstraintFalseUsage, constraints) {
            if ($rootScope.isEmptyCompositeType(compositeType)) return true;
            if (compositeType == 'FORALL' || compositeType == 'EXIST') {
                if (constraints.length < 2) return true;
            } else {
                if (firstConstraint == null) return true;
                if (secondConstraint == null) return true;
            }
            return false;
        };


        $rootScope.erorrForPredicate = function(newConstraint, type, selectedNode) {
            if (!selectedNode) return true;
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
                if ($rootScope.isEmptyConstraintValueSet(newConstraint)) return true;
            }

            return false;
        }


        $rootScope.erorrForConfStatement = function(newConstraint, targetId, type, selectedNode) {
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
                if ($rootScope.isEmptyConstraintValueSet(newConstraint)) return true;
            }
            return false;
        };

        $rootScope.isEmptyConstraintID = function(newConstraint) {
            if (newConstraint.constraintId === null) return true;
            if (newConstraint.constraintId === '') return true;

            return false;
        }

        $rootScope.isEmptyComplexConstraintID = function(id) {
            if (id === null) return true;
            if (id === '') return true;

            return false;
        }

        $rootScope.isDuplicatedConstraintID = function(newConstraint, targetId) {
            if ($rootScope.conformanceStatementIdList.indexOf(newConstraint.constraintId) != -1 && targetId == newConstraint.constraintId) return true;

            return false;
        }

        $rootScope.isDuplicatedComplexConstraintID = function(newComplexConstraintId, targetComplexId) {
            if ($rootScope.conformanceStatementIdList.indexOf(newComplexConstraintId) != -1 && targetComplexId == newComplexConstraintId) return true;

            return false;
        }

        $rootScope.isEmptyConstraintNode = function(newConstraint, type) {
            if (type == 'datatype') {
                if (newConstraint.component_1 === null) return true;
            } else if (type == 'segment') {
                if (newConstraint.field_1 === null) return true;
            } else if (type == 'message') {
                if (newConstraint.position_1 === null) return true;
            }

            return false;
        }

        $rootScope.isEmptyConstraintVerb = function(newConstraint) {
            if (newConstraint.verb === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintPattern = function(newConstraint) {
            if (newConstraint.contraintType === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintValue = function(newConstraint) {
            if (newConstraint.value === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintValue2 = function(newConstraint) {
            if (newConstraint.value2 === null) return true;

            return false;
        }

        $rootScope.isEmptyConstraintAnotherNode = function(newConstraint, type) {
            if (type == 'datatype') {
                if (newConstraint.component_2 === null) return true;
            } else if (type == 'segment') {
                if (newConstraint.field_2 === null) return true;
            } else if (type == 'message') {
                if (newConstraint.position_2 === null) return true;
            }

            return false;
        }

        $rootScope.isEmptyConstraintValueSet = function(newConstraint) {
            if (newConstraint.valueSetId === null) return true;

            return false;
        }

        $rootScope.isEmptyCompositeType = function(compositeType) {
            if (compositeType === null) return true;

            return false;
        }


        // We check for IE when the user load the main page.
        // TODO: Check only once.
        // $scope.checkForIE();


        $rootScope.openRichTextDlg = function(obj, key, title, disabled) {
            return $modal.open({
                templateUrl: 'RichTextCtrl.html',
                controller: 'RichTextCtrl',
                windowClass: 'app-modal-window',
                backdrop: true,
                keyboard: true,
                backdropClick: false,
                resolve: {
                    editorTarget: function() {
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

        $rootScope.openInputTextDlg = function(obj, key, title, disabled) {
            return $modal.open({
                templateUrl: 'InputTextCtrl.html',
                controller: 'InputTextCtrl',
                backdrop: true,
                keyboard: true,
                windowClass: 'input-text-modal-window',
                backdropClick: false,
                resolve: {
                    editorTarget: function() {
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


        $rootScope.isDuplicated = function(obj, context, list) {
            if (obj == null || obj == undefined || obj[context] == null) return false;
            return _.find(_.without(list, obj), function(item) {
                return item[context] == obj[context] && item.id != obj.id;
            });
        };

        // $rootScope.validateExtension = function (obj, context, list) {
        // //if (obj == null || obj == undefined) return false;
        // if(obj[context] == null) return false;
        // return _.find(_.without(list, obj), function (item) {
        // return item[context] == obj[context];
        // });


        // };


        $rootScope.isDuplicatedTwoContexts = function(obj, context1, context2, list) {
            if (obj == null || obj == undefined) return false;

            return _.find(_.without(list, obj), function(item) {
                if (item[context1] == obj[context1]) {
                    return item[context2] == obj[context2] && item.id != obj.id;
                } else {
                    return false;
                }
            });
        };

        $scope.init = function() {
            $http.get('api/igdocuments/config', { timeout: 60000 }).then(function(response) {
                $rootScope.config = angular.fromJson(response.data);
            }, function(error) {});
        };

        $scope.getFullName = function() {
            if (userInfoService.isAuthenticated() === true) {
                return userInfoService.getFullName();
            }
            return '';
        };

        $rootScope.getLabel = function(name, ext) {
            var label = name;
            if (ext && ext !== null && ext !== "") {
                label = label + "_" + ext;
            }
            return label;
        };

        $rootScope.getDynamicWidth = function(a, b, otherColumsWidth) {
            var tableWidth = $rootScope.getTableWidth();
            if (tableWidth > 0) {
                var left = tableWidth - otherColumsWidth;
                return { "width": a * parseInt(left / b) + "px" };
            }
            return "";
        };


        $rootScope.getTableWidth = function() {
            if ($rootScope.tableWidth === null || $scope.tableWidth == 0) {
                $rootScope.tableWidth = $("#nodeDetailsPanel").width();
            }
            return $rootScope.tableWidth;
        };


        $rootScope.getConstraintAsString = function(constraint) {
            return constraint.constraintId + " - " + constraint.description;
        };

        $rootScope.getConformanceStatementAsString = function(constraint) {
            return "[" + constraint.constraintId + "]" + constraint.description;
        };
        $rootScope.getConstraintAsId = function(constraint) {
            return "[" + constraint.constraintId + "]";
        };

        $rootScope.getPredicateAsString = function(constraint) {
            if(constraint) return constraint.description;
            return null;
        };

        $rootScope.getTextValue = function(value) {
            return value;
        };

        $rootScope.getConstraintsAsString = function(constraints) {
            var str = '';
            for (var index in constraints) {
                str = str + "<p style=\"text-align: left\">" + constraints[index].id + " - " + constraints[index].description + "</p>";
            }
            return str;
        };

        $rootScope.getPredicatesAsMultipleLinesString = function(node) {
            var html = "";
            angular.forEach(node.predicates, function(predicate) {
                html = html + "<p>" + predicate.description + "</p>";
            });
            return html;
        };

        $rootScope.getPredicatesAsOneLineString = function(node) {
            var html = "";
            angular.forEach(node.predicates, function(predicate) {
                html = html + predicate.description;
            });
            return $sce.trustAsHtml(html);
        };


        $rootScope.getConfStatementsAsMultipleLinesString = function(node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function(conStatement) {
                html = html + "<p>" + conStatement.id + " : " + conStatement.description + "</p>";
            });
            return html;
        };

        $rootScope.getConfStatementsAsOneLineString = function(node) {
            var html = "";
            angular.forEach(node.conformanceStatements, function(conStatement) {
                html = html + conStatement.id + " : " + conStatement.description;
            });
            return $sce.trustAsHtml(html);
        };

        $rootScope.getSegmentRefNodeName = function(node) {
            var seg = $rootScope.segmentsMap[node.ref.id];
            return node.position + "." + $rootScope.getSegmentLabel(seg) + ":" + seg.description;
        };

        $rootScope.getSegmentLabel = function(seg) {
            // var ext = $rootScope.getSegmentExtension(seg);
            return seg != null ? $rootScope.getLabel(seg.name, seg.ext) : "";
        };

        $rootScope.getSegmentExtension = function(seg) {
            return $rootScope.getExtensionInLibrary(seg.id, $rootScope.igdocument.profile.segmentLibrary, "ext");
        };

        $rootScope.getDatatypeExtension = function(datatype) {
            return $rootScope.getExtensionInLibrary(datatype.id, $rootScope.igdocument.profile.datatypeLibrary, "ext");
        };

        $rootScope.getTableBindingIdentifier = function(table) {
            return $rootScope.getExtensionInLibrary(table.id, $rootScope.igdocument.profile.tableLibrary, "bindingIdentifier");
        };


        $rootScope.getDatatypeLabel = function(datatype) {
            if (datatype && datatype != null) {
                // var ext = $rootScope.getDatatypeExtension(datatype);
                return $rootScope.getLabel(datatype.name, datatype.ext);
            }
            return "";
        };

        $rootScope.hasSameVersion = function(element) {

            return element.hl7Version;

        }

        $rootScope.getTableLabel = function(table) {
            if (table && table.bindingIdentifier) {
                return $rootScope.getLabel(table.bindingIdentifier, table.ext);
            }
            return "";
        };

        $rootScope.getExtensionInLibrary = function(id, library, propertyType) {
            // ////console.log("main Here id=" + id);
            if (propertyType && library.children) {
                for (var i = 0; i < library.children.length; i++) {
                    if (library.children[i].id === id) {
                        return library.children[i][propertyType];
                    }
                }
            }
            return "";
        };


        $rootScope.getGroupNodeName = function(node) {
            return node.position + "." + node.name;
        };

        $rootScope.getFieldNodeName = function(node) {
            return node.position + "." + node.name;
        };

        $rootScope.getComponentNodeName = function(node) {
            return node.position + "." + node.name;
        };

        $rootScope.getDatatypeNodeName = function(node) {
            return node.position + "." + node.name;
        };

        $rootScope.onColumnToggle = function(item) {
            $rootScope.viewSettings.save();
        };

        $rootScope.getDatatypeLevelConfStatements = function(element) {
            return DatatypeService.getDatatypeLevelConfStatements(element);
        };

        $rootScope.getDatatypeLevelPredicates = function(element) {
            return DatatypeService.getDatatypeLevelPredicates(element);
        };

        $rootScope.isDatatypeSubDT = function(component) {
            return DatatypeService.isDatatypeSubDT(component, $rootScope.datatype);
        };



        $rootScope.setUsage = function(node) {
            ElementUtils.setUsage(node);
        };


        $rootScope.findDatatypeInLibrary = function(datatypeId, datatypeLibary) {
            if (datatypeLibary.children) {
                for (var i = 0; i < datatypeLibary.children.length; i++) {
                    if (datatypeLibary.children[i].id === id) {
                        return datatypeLibary.children[i];
                    }
                }
            }
            return null;
        };


        $rootScope.openConfirmLeaveDlg = function() {
            if ($rootScope.modalInstance != undefined && $rootScope.modalInstance != null && $rootScope.modalInstance.opened) {
                $rootScope.modalInstance.close();
            }
            $rootScope.modalInstance = $modal.open({
                templateUrl: 'ConfirmLeaveDlg.html',
                controller: 'ConfirmLeaveDlgCtrl',
                'size': 'md'
            });
            return $rootScope.modalInstance;
        };

        $rootScope.displayNullView = function() {
            //console.log("before");
            //console.log($rootScope.subview);
            $rootScope.subview = 'Blank.html';
            //console.log("after");
            //console.log($rootScope.subview);
        }

        $rootScope.Activate = function(param) {
            $rootScope.activeModel = param;
        }


        var vm = this;

        //        $scope.$on("getMenuState", function (event, data) {
        //            $scope.$apply(function () {
        //                vm.opened = data;
        //            });
        //        });
        //
        //        this.toggleNavigation = function() {
        //            $mdSidenav('navigation-drawer').toggle();
        //        };

        $scope.checkedNavigation = false;
        $scope.toggleNavigation = function() {
            $scope.checkedNavigation = !$scope.checkedNavigation;
        };


    }
]);


angular.module('igl').controller('LoginCtrl', ['$scope', '$modalInstance', 'user', function($scope, $modalInstance, user) {
    $scope.user = user;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.login = function() {
        // ////console.log("logging in...");
        $modalInstance.close($scope.user);
    };
}]);


angular.module('igl').controller('RichTextCtrl', ['$scope', '$modalInstance', 'editorTarget', function($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        $modalInstance.close($scope.editorTarget);
    };
}]);


angular.module('igl').controller('InputTextCtrl', ['$scope', '$modalInstance', 'editorTarget', function($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        $modalInstance.close($scope.editorTarget);
    };
}]);

angular.module('igl').controller('ConfirmLogoutCtrl', ["$scope", "$modalInstance", "$rootScope", "$http", function($scope, $modalInstance, $rootScope, $http) {
    $scope.logout = function() {
        $modalInstance.close();
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
}]);


angular.module('igl').controller('ConfirmLeaveDlgCtrl', function($scope, $modalInstance, $rootScope, $http, SectionSvc, FilteringSvc, MessageService, SegmentService, SegmentLibrarySvc, DatatypeLibrarySvc, DatatypeService, IgDocumentService, ProfileSvc, TableService, TableLibrarySvc) {
    $scope.continue = function() {
        $rootScope.clearChanges();
        $modalInstance.close();
    };


    $scope.discard = function() {
        var data = $rootScope.currentData;
        if (data.type && data.type === "message") {
            MessageService.reset();
        } else if (data.type && data.type === "segment") {
            SegmentService.reset();
        } else if (data.type && data.type === "datatype") {
            DatatypeService.reset();
        }
        $rootScope.addedSegments = [];
        $rootScope.addedDatatypes = [];
        $rootScope.addedTables = [];
        $scope.continue();
    };

    $scope.error = null;
    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.save = function() {
        var data = $rootScope.currentData;
        if ($rootScope.libraryDoc && $rootScope.libraryDoc != null) {
            if (data.datatypeLibId && data.date) {
                DatatypeLibrarySvc.saveMetaData($rootScope.libraryDoc.datatypeLibrary.id, data);
            }

        }
        var section = { id: data.id, sectionTitle: data.sectionTitle, sectionDescription: data.sectionDescription, sectionPosition: data.sectionPosition, sectionContents: data.sectionContents };
        ////console.log(data);

        if (data.type && data.type === "section") {
            ////console.log($rootScope.originalSection);
            ////console.log(data);

            SectionSvc.update($rootScope.igdocument.id, section).then(function(result) {
                ////console.log($rootScope.igdocument);
                SectionSvc.merge($rootScope.originalSection, section);
                $scope.continue();
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        } else if (data.type && data.type === "messages") {
            ////console.log($rootScope.originalSection);
            ////console.log(data);
            SectionSvc.update($rootScope.igdocument.id, section).then(function(result) {
                ////console.log($rootScope.igdocument);
                SectionSvc.merge($rootScope.originalSection, section);
                $scope.continue();
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        } else if (data.type && data.type === "segments") {
            ////console.log($rootScope.originalSection);
            ////console.log(data);

            SectionSvc.update($rootScope.igdocument.id, section).then(function(result) {
                ////console.log($rootScope.igdocument);
                SectionSvc.merge($rootScope.originalSection, section);
                $scope.continue();
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        } else if (data.type && data.type === "datatypes") {
            ////console.log($rootScope.originalSection);
            ////console.log(data);

            SectionSvc.update($rootScope.igdocument.id, section).then(function(result) {
                ////console.log($rootScope.igdocument);
                SectionSvc.merge($rootScope.originalSection, section);
                $scope.continue();
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        } else if (data.type && data.type === "tables") {
            ////console.log($rootScope.originalSection);
            ////console.log(data);

            SectionSvc.update($rootScope.igdocument.id, section).then(function(result) {
                ////console.log($rootScope.igdocument);
                SectionSvc.merge($rootScope.originalSection, section);
                $scope.continue();
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        } else if (data.type && data.type === "message") {
            var message = $rootScope.message;
            ////console.log($rootScope.message);
            MessageService.save(message).then(function(result) {
                var index = MessageService.findIndex(message.id);
                if (index < 0) {
                    $rootScope.igdocument.profile.messages.children.splice(0, 0, message);
                }
                MessageService.saveNewElements().then(function() {
                    MessageService.merge($rootScope.messagesMap[message.id], message);
                    $scope.continue();
                }, function(error) {
                    $rootScope.msg().text = "Sorry an error occured. Please try again";
                    $rootScope.msg().type = "danger";
                    $rootScope.msg().show = true;
                });
            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        } else if (data.type && data.type === "segment") {
            if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
                var segment = $rootScope.segment;
                var ext = segment.ext;
                if (segment.libIds === undefined) segment.libIds = [];
                if (segment.libIds.indexOf($rootScope.igdocument.profile.segmentLibrary.id) == -1) {
                    segment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
                }
                SegmentService.save($rootScope.segment).then(function(result) {
                    var oldLink = SegmentLibrarySvc.findOneChild(result.id, $rootScope.igdocument.profile.segmentLibrary.children);
                    var newLink = SegmentService.getSegmentLink(result);
                    SegmentLibrarySvc.updateChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function(link) {
                        SegmentService.saveNewElements().then(function() {
                            SegmentService.merge($rootScope.segmentsMap[result.id], result);
                            if (oldLink && oldLink != null) {
                                oldLink.ext = newLink.ext;
                                oldLink.name = newLink.name;
                            }
                            $scope.continue();
                        }, function(error) {
                            $rootScope.msg().text = "Sorry an error occured. Please try again";
                            $rootScope.msg().type = "danger";
                            $rootScope.msg().show = true;
                        });
                    }, function(error) {
                        $rootScope.msg().text = error.data.text;
                        $rootScope.msg().type = error.data.type;
                        $rootScope.msg().show = true;
                    });
                }, function(error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }else {
                $rootScope.saveBindingForSegment();
                $scope.continue();
            }


        } else if (data.type && data.type === "datatype") {
            if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
                var datatype = $rootScope.datatype;
                var ext = datatype.ext;
                var libId = "";
                var children = [];
                DatatypeService.save(datatype).then(function(result) {
                    if ($rootScope.libraryDoc && $rootScope.libraryDoc !== null) {
                        libId = $rootScope.libraryDoc.datatypeLibrary.id;
                        children = $rootScope.libraryDoc.datatypeLibrary.children;

                    } else if ($rootScope.igdocument && $rootScope.igdocument !== null) {
                        libId = $rootScope.igdocument.profile.datatypeLibrary.id;
                        children = $rootScope.igdocument.profile.datatypeLibrary.children;
                    }
                    var oldLink = DatatypeLibrarySvc.findOneChild(result.id, children);
                    var newLink = DatatypeService.getDatatypeLink(result);
                    newLink.ext = ext;
                    DatatypeLibrarySvc.updateChild(libId, newLink).then(function(link) {
                        DatatypeService.merge($rootScope.datatypesMap[result.id], result);
                        if (oldLink && oldLink != null) {
                            oldLink.ext = newLink.ext;
                            oldLink.name = newLink.name;
                        }
                        $scope.continue();

                    }, function(error) {
                        $rootScope.msg().text = "Sorry an error occured. Please try again";
                        $rootScope.msg().type = "danger";
                        $rootScope.msg().show = true;
                    });

                }, function(error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }else {
                $rootScope.saveBindingForDatatype();
                $scope.continue();
            }

        } else if (data.type && data.type === "table") {
            if (data.scope === 'USER' || (data.status && data.status === 'UNPUBLISHED')) {
                var table = $rootScope.table;
                var libId = "";
                var children = [];
                var bindingIdentifier = table.bindingIdentifier;
                if ($rootScope.libraryDoc && $rootScope.libraryDoc !== null) {
                    libId = $rootScope.libraryDoc.tableLibrary.id;
                    children = $rootScope.libraryDoc.tableLibrary.children;

                } else if ($rootScope.igdocument && $rootScope.igdocument !== null) {
                    libId = $rootScope.igdocument.profile.tableLibrary.id;
                    children = $rootScope.igdocument.profile.tableLibrary.children;
                }
                TableService.save(table).then(function(result) {
                    var oldLink = TableLibrarySvc.findOneChild(result.id, children);
                    TableService.merge($rootScope.tablesMap[result.id], result);
                    var newLink = TableService.getTableLink(result);
                    newLink.bindingIdentifier = bindingIdentifier;
                    TableLibrarySvc.updateChild(libId, newLink).then(function(link) {
                        if (oldLink && oldLink != null) oldLink.bindingIdentifier = link.bindingIdentifier;
                        $rootScope.msg().text = "tableSaved";
                        $rootScope.msg().type = "success";
                        $rootScope.msg().show = true;
                        $scope.continue();
                    }, function(error) {
                        $rootScope.msg().text = error.data.text;
                        $rootScope.msg().type = error.data.type;
                        $rootScope.msg().show = true;
                    });
                }, function(error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }else {
                $rootScope.saveBindingForValueSet();

            }


        } else if (data.type === "document") {

            IgDocumentService.saveMetadata($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
                $rootScope.igdocument.metaData = angular.copy($rootScope.metaData);
                $scope.continue();

            }, function(error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        } else if (data.type === "profile") {

            if ($rootScope.igdocument != null && $rootScope.metaData != null) {
                ProfileSvc.saveMetaData($rootScope.igdocument.id, $rootScope.metaData).then(function(result) {
                    $scope.continue();
                }, function(error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }
        }


    }
});