'use strict';

/**
 * @ngdoc overview
 * @name clientApp
 * @description
 * # clientApp
 *
 * Main module of the application.
 */
var app = angular
    .module('igl', [
        'ngAnimate',
        'ngCookies',
        'ngMessages',
        'ngResource',
        'ngRoute',
        'ngSanitize',
        'ngTouch',
        'ui.bootstrap',
        'smart-table',
        'ngTreetable',
        'restangular'
//        ,
//        'ngMockE2E'
     ]);

var
//the HTTP headers to be used by all requests
    httpHeaders,

//the message to show on the login popup page
    loginMessage,

//the spinner used to show when we are still waiting for a server answer
    spinner,

//The list of messages we don't want to displat
    mToHide = ['usernameNotFound', 'emailNotFound', 'usernameFound', 'emailFound', 'loginSuccess', 'userAdded'];

//the message to be shown to the user
var msg = {};

app.config(function ($routeProvider, RestangularProvider, $httpProvider) {


    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html'
        })
        .when('/home', {
            templateUrl: 'views/home.html'
        })
        .when('/ig', {
            templateUrl: 'views/ig.html'
        })
        .when('/doc', {
            templateUrl: 'views/doc.html'
        })
        .when('/setting', {
            templateUrl: 'views/setting.html'
        })
        .when('/about', {
            templateUrl: 'views/about.html'
        })
        .when('/contact', {
            templateUrl: 'views/contact.html'
        })
        .when('/forgotten', {
            templateUrl: 'views/account/forgotten.html',
            controller: 'ForgottenCtrl'
        })
        .when('/issue', {
            templateUrl: 'views/issue.html',
            controller: 'IssueCtrl'
        })
        .when('/registration', {
            templateUrl: 'views/account/registration.html',
            controller: 'RegistrationCtrl'
        })
        .when('/account', {
            templateUrl: 'views/account/account.html',
            controller: 'AccountCtrl',
            resolve: {
                login: ['LoginService', function(LoginService){
                    return LoginService();
                }]
            }
        })
        .when('/registerResetPassword', {
            templateUrl: 'views/account/registerResetPassword.html',
            controller: 'RegisterResetPasswordCtrl',
            resolve: {
                isFirstSetup: function() {
                    return true;
                }
            }
        })
        .when('/resetPassword', {
            templateUrl: 'views/account/registerResetPassword.html',
            controller: 'RegisterResetPasswordCtrl',
            resolve: {
                isFirstSetup: function() {
                    return false;
                }
            }
        })
        .when('/authors', {
            templateUrl: 'views/account/manageAccounts.html',
            controller: 'ManageAccountsCtrl',
            resolve: {
                accountType: function() {
                    return 'author';
                },
                accountList:  ['MultiAuthorsLoader', function(MultiAuthorsLoader) {
                    return MultiAuthorsLoader();
                }]
            }
        }) .when('/supervisors', {
            templateUrl: 'views/account/manageAccounts.html',
            controller: 'ManageAccountsCtrl',
            resolve: {
                accountType: function() {
                    return 'supervisor';
                },
                accountList:  ['MultiSupervisorsLoader', function(MultiSupervisorsLoader) {
                    return MultiSupervisorsLoader();
                }]
            }
        }).when('/registrationSubmitted', {
            templateUrl: 'views/account/registrationSubmitted.html'
        })
        .otherwise({
            redirectTo: '/'
        });

//    $http.defaults.headers.post['X-CSRFToken'] = $cookies['csrftoken'];

    $httpProvider.interceptors.push(function ($q) {
        return {
            request: function (config) {
//                return "http://localhost:8080/igl-api"+ value;
                if(config.url.startsWith("api")){
//                    config.url = "http://localhost:8080/igl-api/"+  config.url;
                 }
                return config || $q.when(config);
            }
        }
    });



//    $httpProvider.interceptors.push('503Interceptor');
//    $httpProvider.interceptors.push('sessionTimeoutInterceptor');
    $httpProvider.interceptors.push(function ($rootScope, $q) {
        var setMessage = function (response) {
            //if the response has a text and a type property, it is a message to be shown
            if (response.data && response.data.text && response.data.type) {

//                    console.log("received message of some type");
//                    console.log("response.status"+response.status);
//                    console.log("response.config.url"+response.config.url);
//                    console.log("response.data.type"+response.data.type);
//                    console.log("response.data.text"+response.data.text);

                if (response.status === 401 ) {
//                        console.log("setting login message");
                    loginMessage = {
                        text: response.data.text,
                        type: response.data.type,
                        show: true,
                        manualHandle: response.data.manualHandle
                    };
//                        console.log("loginMessage.text"+loginMessage.text);
//                        console.log("loginMessage.show"+loginMessage.show);
                } else {
//                        console.log("setting message");
                    msg = {
                        text: response.data.text,
                        type: response.data.type,
                        show: true,
                        manualHandle: response.data.manualHandle
                    };
                    var found = false;
                    var i = 0;
                    while ( i < mToHide.length && !found ) {
                        if ( msg.text === mToHide[i] ) {
                            found = true;
                        }
                        i++;
                    }
                    if ( found === true) {
                        msg.show = false;
                    } else {
                        //hide the msg in 5 seconds
                        //                        setTimeout(
                        //                            function() {
                        //                                msg.show = false;
                        //                                //tell angular to refresh
                        //                                $rootScope.$apply();
                        //                            },
                        //                            10000
                        //                        );
                    }
                }
            }
        };

        return {
            response: function (response) {
                setMessage(response);
                return response || $q.when(response);
            },

            responseError: function (response) {
                setMessage(response);
                return $q.reject(response);
            }
        };


//        return function (promise) {
//            return promise.then(
//                //this is called after each successful server request
//                function (response) {
//                    setMessage(response);
//                    return response;
//                },
//                //this is called after each unsuccessful server request
//                function (response) {
//                    setMessage(response);
//                    return $q.reject(response);
//                }
//            );
//        };
    });

    //configure $http to show a login dialog whenever a 401 unauthorized response arrives
    $httpProvider.interceptors.push(function ($rootScope, $q) {


        return {
            response: function (response) {
                return response   || $q.when(response);
            },
            responseError: function (response) {
                if (response.status === 401) {
                    //We catch everything but this one. So public users are not bothered
                    //with a login windows when browsing home.
                    if ( response.config.url !== 'api/accounts/cuser') {
                        //We don't intercept this request
                        var deferred = $q.defer(),
                            req = {
                                config: response.config,
                                deferred: deferred
                            };
                        $rootScope.requests401.push(req);
                        $rootScope.$broadcast('event:loginRequired');
//                        return deferred.promise;

                        return  $q.when(response);
                    }
                }
                return $q.reject(response);
            }
        };



//
//        return function (promise) {
//            return promise.then(
//                //success -> don't intercept
//                function (response) {
//                    return response;
//                },
//                //error -> if 401 save the request and broadcast an event
//                function (response) {
//                    if (response.status === 401) {
//                        //We catch everything but this one. So public users are not bothered
//                        //with a login windows when browsing home.
//                        if ( response.config.url !== 'api/accounts/cuser') {
//                            //We don't intercept this request
//                            var deferred = $q.defer(),
//                                req = {
//                                    config: response.config,
//                                    deferred: deferred
//                                };
//                            $rootScope.requests401.push(req);
//                            $rootScope.$broadcast('event:loginRequired');
//                            return deferred.promise;
//                        }
//                    }
//                    return $q.reject(response);
//                }
//            );
//        };
    });

    //intercepts ALL angular ajax http calls
    $httpProvider.interceptors.push(function ($q) {
//        return function (promise) {
//            return promise.then(
//                function (response) {
//                    //hide the spinner
//                    spinner = false;
//                    return response;
//                },
//                function (response) {
//                    //hide the spinner
//                    spinner = false;
//                    return $q.reject(response);
//                }
//            );
//        };

        return {
            response: function (response) {
                //hide the spinner
                spinner = false;
                return response   || $q.when(response);
            },

            responseError: function (response) {
                //hide the spinner
                spinner = false;
                return $q.reject(response);
            }
        };


    });

    var spinnerStarter = function (data, headersGetter) {
        spinner = true;
        return data;
    };
    $httpProvider.defaults.transformRequest.push(spinnerStarter);

    httpHeaders = $httpProvider.defaults.headers;


});

app.run(function ($rootScope, $location, Restangular, $modal,$filter,base64,userInfoService,$http) {
    $rootScope.readonly = false;
    $rootScope.profile = null; // current profile
    $rootScope.message = null; // current message
    $rootScope.datatype = null; // current datatype
    $rootScope.statuses = ['Draft', 'Active', 'Superceded', 'Withdrawn'];
    $rootScope.hl7Versions = ['2.0', '2.1', '2.2', '2.3', '2.3.1', '2.4', '2.5', '2.5.1', '2.6', '2.7', '2.8'];
    $rootScope.schemaVersions = ['1.0', '1.5', '2.0', '2.5'];
    $rootScope.pages = ['list', 'edit', 'read'];
    $rootScope.context = {page: $rootScope.pages[0]};
    $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
    $rootScope.segmentsMap = {};  // Map for Segment;key:id, value:object
    $rootScope.datatypesMap = {}; // Map for Datatype; key:id, value:object
    $rootScope.tablesMap = {};// Map for tables; key:id, value:object
    $rootScope.segments = [];// list of segments of the selected messages
    $rootScope.datatypes = [];// list of datatypes of the selected messages
    $rootScope.tables = [];// list of tables of the selected messages
    $rootScope.usages = ['R', 'RE', 'O', 'C', "CE","X", "B", "W"];
    $rootScope.codeUsages = ['R', 'P', 'E'];
    $rootScope.codeSources = ['HL7', 'Local', 'Redefined', 'SDO'];
    $rootScope.tableStabilities = ['Static', 'Dynamic'];
    $rootScope.tableExtensibilities = ['Open', 'Close'];
    $rootScope.constraintVerbs = ['SHALL be', 'SHALL NOT be', 'is', 'is not'];
    $rootScope.contraintTypes = ['valued', 'a literal value', 'one of list values', 'formatted value', 'identical to the another node'];
    $rootScope.predefinedFormats = ['ISO-compliant OID', 'Alphanumeric', 'YYYY', 'YYYYMM', 'YYYYMMDD', 'YYYYMMDDhh', 'YYYYMMDDhhmm', 'YYYYMMDDhhmmss', 'YYYYMMDDhhmmss.sss', 'YYYY+-ZZZZ', 'YYYYMM+-ZZZZ', 'YYYYMMDD+-ZZZZ', 'YYYYMMDDhh+-ZZZZ', 'YYYYMMDDhhmm+-ZZZZ', 'YYYYMMDDhhmmss+-ZZZZ', 'YYYYMMDDhhmmss.sss+-ZZZZ'];
    $rootScope.postfixCloneTable = 'CA';
    $rootScope.newCodeFakeId = 0;
    $rootScope.newTableFakeId = 0;
    $rootScope.newPredicateFakeId = 0;
    $rootScope.newConformanceStatementFakeId = 0;
    $rootScope.segment = null;
    $rootScope.profileTabs = new Array();
    $rootScope.igTabs = new Array();
    $rootScope.notifyMsgTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifyMsgTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifyDtTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifyTableTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifySegTreeUpdate = '0'; // TODO: FIXME
    $rootScope.messagesData = []; 
    $rootScope.messages = [];// list of messages
    $rootScope.customIgs=[];
    $rootScope.preloadedIgs = [];
    $rootScope.changes = {};
    $rootScope.generalInfo = {type: null, 'message': null};
    $rootScope.references =[]; // collection of element referencing a datatype to delete
    $rootScope.section = {};
    $rootScope.parentsMap = {};

    $rootScope.selectProfileTab = function (value) {
        $rootScope.profileTabs[0] = false;
        $rootScope.profileTabs[1] = false;
        $rootScope.profileTabs[2] = false;
        $rootScope.profileTabs[3] = false;
        $rootScope.profileTabs[4] = false;
        $rootScope.profileTabs[5] = false;
        $rootScope.profileTabs[value] = true;
    };

    $rootScope.selectIgTab = function (value) {
        $rootScope.igTabs[0] = false;
        $rootScope.igTabs[1] = false;
        $rootScope.igTabs[value] = true;
    };

    $rootScope.initMaps = function () {
        $rootScope.segment = null;
        $rootScope.datatype = null;
        $rootScope.message = null;
        $rootScope.table = null;
        $rootScope.messagesMap = {};
        $rootScope.segmentsMap = {};
        $rootScope.datatypesMap = {};
        $rootScope.tablesMap = {};
        $rootScope.segments = [];
        $rootScope.tables = [];
        $rootScope.datatypes = [];
        $rootScope.messages = [];
        $rootScope.messagesData = [];

        $rootScope.newCodeFakeId = 0;
        $rootScope.newTableFakeId = 0;
        $rootScope.newPredicateFakeId = 0;
        $rootScope.newConformanceStatementFakeId = 0;
        
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
        $rootScope.changes = {};
    };

    $rootScope.hasChanges = function(){
        return Object.getOwnPropertyNames($rootScope.changes).length !== 0;
    };

    $rootScope.recordChange = function(object,changeType) {
        var type = object.type;

        if($rootScope.changes[type] === undefined){
            $rootScope.changes[type] = {};
        }

        if($rootScope.changes[type][object.id] === undefined){
            $rootScope.changes[type][object.id] = {};
        }

        if(changeType === "datatype"){
            $rootScope.changes[type][object.id][changeType] = object[changeType].id;
        }else{
            $rootScope.changes[type][object.id][changeType] = object[changeType];
        }

        console.log("Change is " + $rootScope.changes[type][object.id][changeType]);
    };


    $rootScope.recordChange2 = function(type,id,attr,value) {
        if($rootScope.changes[type] === undefined){
            $rootScope.changes[type] = {};
        }
        if($rootScope.changes[type][id] === undefined){
            $rootScope.changes[type][id] = {};
        }
        if(attr != null) {
            $rootScope.changes[type][id][attr] = value;
        }else {
            $rootScope.changes[type][id] = value;
        }
    };

    $rootScope.recordChangeForEdit = function(object,changeType) {
        var type = object.type;

        if($rootScope.changes[type] === undefined){
            $rootScope.changes[type] = {};
        }

        if($rootScope.changes[type]['edit'] === undefined){
            $rootScope.changes[type]['edit'] = {};
        }
        
        if($rootScope.changes[type]['edit'][object.id] === undefined){
            $rootScope.changes[type]['edit'][object.id] = {};
        }


        $rootScope.changes[type]['edit'][object.id][changeType] = object[changeType];
        

        console.log("Change is " + $rootScope.changes[type]['edit'][object.id][changeType]);
    };
    
    $rootScope.recordChangeForEdit2 = function(type,command,id,valueType,value) {
        var obj = $rootScope.findObjectInChanges(type, "add", id);
        if (obj === undefined) { // not a new object
            if ($rootScope.changes[type] === undefined) {
                $rootScope.changes[type] = {};
            }
            if ($rootScope.changes[type][command] === undefined) {
                $rootScope.changes[type][command] = [];
            }
            if (valueType !== type) {
                var obj = $rootScope.findObjectInChanges(type, command, id);
                if (obj === undefined) {
                    obj = {id: id};
                    $rootScope.changes[type][command].push(obj);
                }
                obj[valueType] = value;
            } else {
                $rootScope.changes[type][command].push(value);
            }
        }
    };

    $rootScope.recordDelete = function(type,command,id) {
        if(id < 0){ // new object
            $rootScope.removeObjectFromChanges(type, "add", id);
        }else{
            $rootScope.removeObjectFromChanges(type, "edit",id);
            if ($rootScope.changes[type] === undefined) {
                $rootScope.changes[type] = {};
            }
            if ($rootScope.changes[type][command] === undefined) {
                $rootScope.changes[type][command] = [];
            }

            if ($rootScope.changes[type]["delete"] === undefined) {
                $rootScope.changes[type]["delete"] = [];
            }

            $rootScope.changes[type]["delete"].push({id:id});
        }

        if($rootScope.changes[type]) {            //clean the changes object
            if ($rootScope.changes[type]["add"] && $rootScope.changes[type]["add"].length === 0) {
                delete  $rootScope.changes[type]["add"];
            }
            if ($rootScope.changes[type]["edit"] && $rootScope.changes[type]["edit"].length === 0) {
                delete  $rootScope.changes[type]["edit"];
            }

            if (Object.getOwnPropertyNames($rootScope.changes[type]).length === 0) {
                delete $rootScope.changes[type];
            }
        }
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

    $rootScope.processElement = function (element, parent) {
        if (element.type === "group" && element.children) {
            $rootScope.parentsMap[element.id] = parent;
//            element["parent"] = parent;
            element.children = $filter('orderBy')(element.children, 'position');
            angular.forEach(element.children, function (segmentRefOrGroup) {
                $rootScope.processElement(segmentRefOrGroup,element);
            });
        } else if (element.type === "segmentRef") {
            if(parent) {
                $rootScope.parentsMap[element.id] = parent;
            }
            var ref = $rootScope.segmentsMap[element.ref.id];
            element.ref["path"] =  ref.name;
            $rootScope.processElement(ref,element);
        }  else if (element.type === "segment") {
            if ($rootScope.segments.indexOf(element) === -1) {
                element["path"] = element["name"];
                $rootScope.segments.push(element);
                element.fields = $filter('orderBy')(element.fields, 'position');
                angular.forEach(element.fields, function (field) {
                    $rootScope.processElement(field,element);
                });
            }
        } else if (element.type === "field" || element.type === "component") {
            $rootScope.parentsMap[element.id] = parent;
//            element["datatype"] = $rootScope.datatypesMap[element.datatype.id];
            element["path"] = parent.path+"."+element.position;
//            if(element.type === "component") {
//                element['sub'] = parent.type === 'component';
//            }
            if (angular.isDefined(element.table) && element.table != null) {
                var table = $rootScope.tablesMap[element.table.id];
                if ($rootScope.tables.indexOf(table) === -1) {
                    $rootScope.tables.push(table);
                }
            }
            $rootScope.processElement($rootScope.datatypesMap[element.datatype.id],element);
        } else if (element.type === "datatype") {
            if ($rootScope.datatypes.indexOf(element) === -1) {
                $rootScope.datatypes.push(element);
                element.components = $filter('orderBy')(element.components, 'position');
                angular.forEach(element.components, function (component) {
                    $rootScope.processElement(component,parent);
                });
            }
        }
    };


    $rootScope.isSubComponent = function(node){
        node.type === 'component' &&  $rootScope.parentsMap[node.id] && $rootScope.parentsMap[node.id].type === 'component';
    };

    $rootScope.findDatatypeRefs = function (datatype, obj) {
        if(angular.equals(obj.type,'field') || angular.equals(obj.type,'component')){
            if($rootScope.datatypesMap[obj.datatype.id] === datatype && $rootScope.references.indexOf(obj) === -1) {
                $rootScope.references.push(obj);
             }
            $rootScope.findDatatypeRefs(datatype,$rootScope.datatypesMap[obj.datatype.id]);
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
        		if(obj.table.id === table.id && $rootScope.references.indexOf(obj) === -1) {
                    $rootScope.references.push(obj);
                 }	
        	}
            $rootScope.findTableRefs(table,$rootScope.datatypesMap[obj.datatype.id]);
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

    //Check if the login dialog is already displayed.
    $rootScope.loginDialogShown = false;

    //make current message accessible to root scope and therefore all scopes
    $rootScope.msg = function () {
        return msg;
    };

    //make current loginMessage accessible to root scope and therefore all scopes
    $rootScope.loginMessage = function () {
//            console.log("calling loginMessage()");
        return loginMessage;
    };

    //showSpinner can be referenced from the view
    $rootScope.showSpinner = function() {
        return spinner;
    };

    /**
     * Holds all the requests which failed due to 401 response.
     */
    $rootScope.requests401 = [];

    $rootScope.$on('event:loginRequired', function () {
//            console.log("in loginRequired event");
        $rootScope.showLoginDialog();
    });

    /**
     * On 'event:loginConfirmed', resend all the 401 requests.
     */
    $rootScope.$on('event:loginConfirmed', function () {
        var i,
            requests = $rootScope.requests401,
            retry = function (req) {
                $http(req.config).then(function (response) {
                    req.deferred.resolve(response);
                });
            };

        for (i = 0; i < requests.length; i += 1) {
            retry(requests[i]);
        }
        $rootScope.requests401 = [];
    });

    /*jshint sub: true */
    /**
     * On 'event:loginRequest' send credentials to the server.
     */
    $rootScope.$on('event:loginRequest', function (event, username, password) {
        httpHeaders.common['Accept'] = 'application/json';
        httpHeaders.common['Authorization'] = 'Basic ' + base64.encode(username + ':' + password);
//        httpHeaders.common['withCredentials']=true;
//        httpHeaders.common['Origin']="http://localhost:9000";
        $http.get('api/accounts/login').success(function() {
            //If we are here in this callback, login was successfull
            //Let's get user info now
            httpHeaders.common['Authorization'] = null;
            $http.get('api/accounts/cuser').success(function (data) {
                userInfoService.setCurrentUser(data);
                $rootScope.$broadcast('event:loginConfirmed');
            });
        });
    });

    /**
     * On 'logoutRequest' invoke logout on the server.
     */
    $rootScope.$on('event:logoutRequest', function () {
        httpHeaders.common['Authorization'] = null;
        userInfoService.setCurrentUser(null);
        $http.get('j_spring_security_logout');
    });

    /**
     * On 'loginCancel' clears the Authentication header
     */
    $rootScope.$on('event:loginCancel',function (){
        httpHeaders.common['Authorization'] = null;
    });

    $rootScope.$on('$routeChangeStart', function(next, current) {
//            console.log('route changing');
        // If there is a message while change Route the stop showing the message
        if (msg && msg.manualHandle === 'false'){
//                console.log('detected msg with text: ' + msg.text);
            msg.show = false;
        }
    });

    $rootScope.loadUserFromCookie = function() {
        if ( userInfoService.hasCookieInfo() === true ) {
            //console.log("found cookie!")
            userInfoService.loadFromCookie();
            httpHeaders.common['Authorization'] = userInfoService.getHthd();
        }
        else {
            //console.log("cookie not found");
        }
    };


});

//
//app.factory('503Interceptor', function ($injector, $q, $rootScope) {
//    return function (responsePromise) {
//        return responsePromise.then(null, function (errResponse) {
//            if (errResponse.status === 503) {
//                $rootScope.showError(errResponse);
//            } else {
//                return $q.reject(errResponse);
//            }
//        });
//    };
//}).factory('sessionTimeoutInterceptor', function ($injector, $q, $rootScope) {
//    return function (responsePromise) {
//        return responsePromise.then(null, function (errResponse) {
//            if (errResponse.reason === "The session has expired") {
//                $rootScope.showError(errResponse);
//            } else {
//                return $q.reject(errResponse);
//            }
//        });
//    };
//});
//

app.controller('ErrorDetailsCtrl', function ($scope, $modalInstance, error) {
    $scope.error = error;
    $scope.ok = function () {
        $modalInstance.close($scope.error);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


//app.filter('flavors', function() {
//    return function(input, name) {
//
//    };
//});

app.filter('flavors',function(){
    return function(inputArray,name){
        return inputArray.filter(function(item){
            return item.name === name || angular.equals(item.name,name);
        });
    };
});


//
//angular.module('ui.bootstrap.carousel', ['ui.bootstrap.transition'])
//    .controller('CarouselController', ['$scope', '$timeout', '$transition', '$q', function ($scope, $timeout, $transition, $q) {
//    }]).directive('carousel', [function () {
//        return {
//
//        }
//    }]);
