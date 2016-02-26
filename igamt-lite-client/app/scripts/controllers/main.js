'use strict';

angular.module('igl').controller('MainCtrl', ['$scope', '$rootScope', 'i18n', '$location', 'userInfoService', '$modal','Restangular','$filter','base64','$http','Idle',
    function ($scope, $rootScope, i18n, $location, userInfoService, $modal,Restangular,$filter,base64,$http,Idle) {
        //This line fetches the info from the server if the user is currently logged in.
        //If success, the app is updated according to the role.
        userInfoService.loadFromServer();
        $rootScope.loginDialog = null;

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
            if ($rootScope.igdocument && $rootScope.igdocument != null && $rootScope.hasChanges()) {
                var modalInstance = $modal.open({
                    templateUrl: 'ConfirmLogout.html',
                    controller: 'ConfirmLogoutCtrl'
                });
                modalInstance.result.then(function () {
                    $scope.execLogout();
                }, function () {
                });
            }else{
                $scope.execLogout();
            }
        };

        $scope.execLogout = function () {
            userInfoService.setCurrentUser(null);
            $scope.username = $scope.password = null;
            $scope.$emit('event:logoutRequest');
            $rootScope.initMaps();
            $rootScope.igdocument = null;
            $location.url('/home');
        };

        $scope.cancel = function () {
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

        $scope.getRoleAsString = function() {
            if ( $scope.isAuthor() === true ) { return 'author'; }
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
            if( $scope.isAuthenticated) {
                $scope.logout();
            }
            $rootScope.timedout = $modal.open({
                templateUrl: 'timedout-dialog.html',
                windowClass: 'modal-danger'
            });
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
        $rootScope.config= null;
        $rootScope.messagesData = [];
        $rootScope.messages = [];// list of messages
        $rootScope.customIgs=[];
        $rootScope.preloadedIgs = [];
        $rootScope.changes = {};
        $rootScope.generalInfo = {type: null, 'message': null};
        $rootScope.references =[]; // collection of element referencing a datatype to delete
        $rootScope.section = {};
        $rootScope.parentsMap = {};
        $rootScope.igChanged = false;

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

        $scope.getScrollbarWidth = function() {
            if($scope.scrollbarWidth == 0) {
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

        $rootScope.hasChanges = function(){
            //return Object.getOwnPropertyNames($rootScope.changes).length !== 0;
            return $rootScope.igChanged;
        };

        $rootScope.recordChanged = function(){
            $rootScope.igChanged = true;
        };

        $rootScope.recordChange = function(object,changeType) {
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


        $rootScope.recordChange2 = function(type,id,attr,value) {
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

        $rootScope.recordChangeForEdit = function(object,changeType) {
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

        $rootScope.recordChangeForEdit2 = function(type,command,id,valueType,value) {
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

        $rootScope.recordDelete = function(type,command,id) {
            if(id < 0){ // new object
                $rootScope.removeObjectFromChanges(type, "add", id);
            }else{
                $rootScope.removeObjectFromChanges(type, "edit",id);
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



        $rootScope.findObjectInChanges = function(type, command, id){
            if($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
                for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
                    var tmp = $rootScope.changes[type][command][i];
                    if (tmp.id === id) {
                        return tmp;
                    }
                }
            }
            return undefined;
        };


        $rootScope.isNewObject = function(type, command, id){
            if($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
                for (var i = 0; i < $rootScope.changes[type][command].length; i++) {
                    var tmp = $rootScope.changes[type][command][i];
                    if (tmp.id === id) {
                        return true;
                    }
                }
            }
            return false;
        };


        $rootScope.removeObjectFromChanges = function(type, command, id){
            if($rootScope.changes[type] !== undefined && $rootScope.changes[type][command] !== undefined) {
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


        $rootScope.apply = function(label){ //FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.isFlavor = function(label){ //FIXME. weak check
            return label != undefined && label != null && (label.indexOf('_') !== -1 || label.indexOf('-') !== -1);
        };

        $rootScope.getDatatype = function(id){
            return $rootScope.datatypesMap && $rootScope.datatypesMap[id];
        };


        $rootScope.processElement = function (element, parent) {
            try {
                if (element.type === "group" && element.children) {
                    $rootScope.parentsMap[element.id] = parent;
//            element["parent"] = parent;
                    element.children = $filter('orderBy')(element.children, 'position');
                    angular.forEach(element.children, function (segmentRefOrGroup) {
                        $rootScope.processElement(segmentRefOrGroup, element);
                    });
                } else if (element.type === "segmentRef") {
                    if (parent) {
                        $rootScope.parentsMap[element.id] = parent;
                    }
                    var ref = $rootScope.segmentsMap[element.ref];
                    //element.ref["path"] = ref.name;
                    $rootScope.processElement(ref, element);
                } else if (element.type === "segment") {
                    if ($rootScope.segments.indexOf(element) === -1) {
                        element["path"] = element["name"];
                        $rootScope.segments.push(element);
                        for (var i = 0; i < element.predicates.length; i++) {
                            if ($rootScope.segmentPredicates.indexOf(element.predicates[i]) === -1)
                                $rootScope.segmentPredicates.push(element.predicates[i]);
                        }

                        for (var i = 0; i < element.conformanceStatements.length; i++) {
                            if ($rootScope.segmentConformanceStatements.indexOf(element.conformanceStatements[i]) === -1)
                                $rootScope.segmentConformanceStatements.push(element.conformanceStatements[i]);
                        }
                        element.fields = $filter('orderBy')(element.fields, 'position');
                        angular.forEach(element.fields, function (field) {
                            $rootScope.processElement(field, element);
                        });
                    }
                } else if (element.type === "field") {
                    $rootScope.parentsMap[element.id] = parent;
//            element["datatype"] = $rootScope.datatypesMap[element.datatype.id];
                    element["path"] = parent.path + "." + element.position;
//            if(element.type === "component") {
//                element['sub'] = parent.type === 'component';
//            }
//            if (angular.isDefined(element.table) && element.table != null) {
//                var table = $rootScope.tablesMap[element.table];
//                if ($rootScope.tables.indexOf(table) === -1) {
//                    $rootScope.tables.push(table);
//                }
//            }
                    $rootScope.processElement($rootScope.datatypesMap[element.datatype], element);
                } else if (element.type === "component") {
                    $rootScope.parentsMap[element.id] = parent;
//              element["datatype"] = $rootScope.datatypesMap[element.datatype.id];
                    element["path"] = parent.path + "." + element.position;
//              if(element.type === "component") {
//                  element['sub'] = parent.type === 'component';
//              }
//              if (angular.isDefined(element.table) && element.table != null) {
//                  var table = $rootScope.tablesMap[element.table];
//                  if ($rootScope.tables.indexOf(table) === -1) {
//                      $rootScope.tables.push(table);
//                  }
//              }
                    $rootScope.processElement($rootScope.datatypesMap[element.datatype], element);
                } else if (element.type === "datatype") {
//            if ($rootScope.datatypes.indexOf(element) === -1) {
//                $rootScope.datatypes.push(element);
                    for (var i = 0; i < element.predicates.length; i++) {
                        if ($rootScope.datatypePredicates.indexOf(element.predicates[i]) === -1)
                            $rootScope.datatypePredicates.push(element.predicates[i]);
                    }

                    for (var i = 0; i < element.conformanceStatements.length; i++) {
                        if ($rootScope.datatypeConformanceStatements.indexOf(element.conformanceStatements[i]) === -1)
                            $rootScope.datatypeConformanceStatements.push(element.conformanceStatements[i]);
                    }


                    element.components = $filter('orderBy')(element.components, 'position');
                    angular.forEach(element.components, function (component) {
                        $rootScope.processElement(component, element);
                    });
//            }
                }
            }catch (e){
                throw e;
            }
        };

        $rootScope.createNewFlavorName = function(label){
            if( $rootScope.igdocument != null) {
                return label + "_" + $rootScope.igdocument.metaData["ext"] + "_" + (Math.floor(Math.random() * 10000000) + 1);
            }else{
                return null;
            }
        };


        $rootScope.isSubComponent = function(node){
            node.type === 'component' &&  $rootScope.parentsMap[node.id] && $rootScope.parentsMap[node.id].type === 'component';
        };

        $rootScope.findDatatypeRefs = function (datatype, obj) {
            if(angular.equals(obj.type,'field') || angular.equals(obj.type,'component')){
                if($rootScope.datatypesMap[obj.datatype] === datatype && $rootScope.references.indexOf(obj) === -1) {
                    $rootScope.references.push(obj);
                }
                $rootScope.findDatatypeRefs(datatype,$rootScope.datatypesMap[obj.datatype]);
            }else if(angular.equals(obj.type,'segment')){
                angular.forEach( $rootScope.segments, function (segment) {
                    angular.forEach(segment.fields, function (field) {
                        $rootScope.findDatatypeRefs(datatype,field);
                    });
                });
            } else if(angular.equals(obj.type,'datatype')){
                if(obj.components != undefined && obj.components != null && obj.components.length > 0){
                    angular.forEach(obj.components, function (component) {
                        $rootScope.findDatatypeRefs(datatype,component);
                    });
                }
            }
        };

        $rootScope.findTableRefs = function (table, obj) {
            if(angular.equals(obj.type,'field') || angular.equals(obj.type,'component')){
                if(obj.table != undefined){
                    if(obj.table === table.id && $rootScope.references.indexOf(obj) === -1) {
                        $rootScope.references.push(obj);
                    }
                }
                $rootScope.findTableRefs(table,$rootScope.datatypesMap[obj.datatype]);
            }else if(angular.equals(obj.type,'segment')){
                angular.forEach( $rootScope.segments, function (segment) {
                    angular.forEach(segment.fields, function (field) {
                        $rootScope.findTableRefs(table,field);
                    });
                });
            } else if(angular.equals(obj.type,'datatype')){
                if(obj.components != undefined && obj.components != null && obj.components.length > 0){
                    angular.forEach(obj.components, function (component) {
                        $rootScope.findTableRefs(table,component);
                    });
                }
            }
        };

        $rootScope.genRegex = function (format){
            if(format === 'YYYY'){
                return '(([0-9]{4})|(([0-9]{4})((0[1-9])|(1[0-2])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMM'){
                return '((([0-9]{4})((0[1-9])|(1[0-2])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMMDD'){
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMMDDhh'){
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMMDDhhmm'){
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMMDDhhmmss'){
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]))|(([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYYMMDDhhmmss.sss'){
                return '((([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]))';
            } else if(format === 'YYYY+-ZZZZ'){
                return '([0-9]{4}).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMM+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2])).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMMDD+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1])).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMMDDhh+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3])).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMMDDhhmm+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9]).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMMDDhhmmss+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9]).*((\\+|\\-)[0-9]{4})';
            } else if(format === 'YYYYMMDDhhmmss.sss+-ZZZZ'){
                return '([0-9]{4})((0[1-9])|(1[0-2]))((0[1-9])|([1-2][0-9])|(3[0-1]))(([0-1][0-9])|(2[0-3]))([0-5][0-9])([0-5][0-9])\\.[0-9][0-9][0-9][0-9]((\\+|\\-)[0-9]{4})';
            } else if(format === 'ISO-compliant OID'){
                return '[0-2](\\.(0|[1-9][0-9]*))*';
            } else if(format === 'Alphanumeric'){
                return '^[a-zA-Z0-9]*$';
            }

            return format;
        };

        $rootScope.isAvailableDTForTable = function (dt) {
            if(dt != undefined){
                if(dt.name === 'IS' ||  dt.name === 'ID' ||dt.name === 'CWE' ||dt.name === 'CNE' ||dt.name === 'CE') return true;

                if(dt.components != undefined && dt.components.length > 0) return true;

            }
            return false;
        };

        $rootScope.validateNumber = function(event) {
            var key = window.event ? event.keyCode : event.which;
            if (event.keyCode == 8 || event.keyCode == 46
                || event.keyCode == 37 || event.keyCode == 39) {
                return true;
            }
            else if ( key < 48 || key > 57 ) {
                return false;
            }
            else return true;
        };


        //We check for IE when the user load the main page.
        //TODO: Check only once.
//    $scope.checkForIE();


        $rootScope.openRichTextDlg = function(obj, key, title, disabled){
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
                            key:key,
                            obj:obj,
                            disabled:disabled,
                            title:title
                        };
                    }
                }
            });
        };

        $rootScope.openInputTextDlg = function(obj, key,title, disabled){
            var modalInstance = $modal.open({
                templateUrl: 'InputTextCtrl.html',
                controller: 'InputTextCtrl',
                backdrop: true,
                keyboard: true,
                size: 'lg',
                backdropClick: false,
                resolve: {
                    editorTarget: function () {
                        return {
                            key:key,
                            obj:obj,
                            disabled:disabled,
                            title:title
                        };
                    }
                }
            });
        };


        $rootScope.isDuplicated = function (obj, context, list) {
            if(obj == null || obj == undefined) return false;

            return _.find(_.without(list, obj), function(item) {
                return item[context] == obj[context];
            });
        };

        $scope.init = function(){
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


angular.module('igl').controller('RichTextCtrl', ['$scope', '$modalInstance','editorTarget', function($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
        $modalInstance.close($scope.editorTarget);
    };
}]);



angular.module('igl').controller('InputTextCtrl', ['$scope', '$modalInstance','editorTarget', function($scope, $modalInstance, editorTarget) {
    $scope.editorTarget = editorTarget;

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };

    $scope.close = function() {
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


