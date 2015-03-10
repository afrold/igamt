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
        'restangular',
        'ngMockE2E'
    ]);

app.config(function ($routeProvider, RestangularProvider, $httpProvider) {
    $routeProvider
        .when('/', {
            templateUrl: 'views/home.html'
        })
        .when('/home', {
            templateUrl: 'views/home.html'
        })
        .when('/profiles', {
            templateUrl: 'views/dashboard.html'
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
        .otherwise({
            redirectTo: '/'
        });

    RestangularProvider.setBaseUrl('/api/');

    RestangularProvider.addElementTransformer('profiles', false, function (profile) {
        profile.addRestangularMethod('clone', 'post', 'clone');
        return profile;
    });


    $httpProvider.interceptors.push('503Interceptor');
    $httpProvider.interceptors.push('sessionTimeoutInterceptor');


});

app.run(function ($rootScope, $location, Restangular, CustomDataModel, $modal) {
    $rootScope.readonly = false;
    $rootScope.profile = {}; // current profile
    $rootScope.message = null; // current message
    $rootScope.datatype = null; // current datatype
    $rootScope.statuses = ['Draft', 'Active', 'Superceded', 'Withdrawn'];
    $rootScope.hl7Versions = ['2.0', '2.1', '2.2', '2.3', '2.3.1', '2.4', '2.5', '2.5.1', '2.6', '2.7', '2.8'];
    $rootScope.schemaVersions = ['1.0', '1.5', '2.0', '2.5'];
    $rootScope.pages = ['list', 'edit', 'read'];
    $rootScope.context = {page: $rootScope.pages[0]};
    $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
    $rootScope.segmentsMap = {};  // Map for Segment;key:id, value:object
    $rootScope.datatypesMap = {}; // Map for Datatype; key:label, value:object
    $rootScope.tablesMap = {};// Map for tables; key:id, value:object
    $rootScope.segments = [];// list of segments of the selected messages
    $rootScope.datatypes = [];// list of datatypes of the selected messages
    $rootScope.tables = [];// list of tables of the selected messages
    $rootScope.usages = ['R', 'RE', 'O', 'C', "CE","X", "B", "W"];
    $rootScope.segment = null;
    $rootScope.profileTabs = new Array();
    $rootScope.notifyMsgTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifyMsgTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifyDtTreeUpdate = '0'; // TODO: FIXME
    $rootScope.notifySegTreeUpdate = '0'; // TODO: FIXME
    $rootScope.changes = {
        //"segment":{}, ex.{1:[{usage:1},{min:1}],2:[]}
        //"group":{},ex.{1:[{usage:1},{min:1}],2:[]}
        //"field":{},ex.{1:[{usage:1},{min:1}],2:[]}
        //"component":{},ex.{1:[{usage:1},{min:1}],2:[]}
        //"datatype":{}ex.{1:[{usage:1},{min:1}],2:[]}
    }; // key:type, value:array of object

    $rootScope.selectProfileTab = function (value) {
        $rootScope.profileTabs[0] = false;
        $rootScope.profileTabs[1] = false;
        $rootScope.profileTabs[2] = false;
        $rootScope.profileTabs[3] = false;
        $rootScope.profileTabs[4] = false;
        $rootScope.profileTabs[5] = false;
        $rootScope.profileTabs[value] = true;
    };

    $rootScope.initMaps = function () {
        $rootScope.segment = null;
        $rootScope.datatype = null;
        $rootScope.messagesMap = {};
        $rootScope.segmentsMap = {};
        $rootScope.datatypesMap = {};
        $rootScope.tablesMap = {};
        $rootScope.segments.length = 0;
        $rootScope.tables.length = 0;
        $rootScope.datatypes.length = 0;
    };

    $rootScope.$watch(function () {
        return $location.path();
    }, function (newLocation, oldLocation) {
        $rootScope.setActive(newLocation);
    });

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

    Restangular.setBaseUrl('/api/');
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
            angular.forEach(element.children, function (segmentRefOrGroup) {
                $rootScope.processElement(segmentRefOrGroup,element);
            });
        } else if (element.type === "segment") {
            element.ref = $rootScope.segmentsMap[element.ref.id];
            element.ref.path =  element.ref.name;
            if ($rootScope.segments.indexOf(element.ref) === -1) {
                $rootScope.segments.push(element.ref);
                angular.forEach(element.ref.fields, function (field) {
                    $rootScope.processElement(field,element.ref);
                });
            }
        } else if (element.type === "field" || element.type === "component") {
            element["datatype"] = $rootScope.datatypesMap[element["datatypeLabel"]];

            element["path"] = parent.path+"."+element.position;
            if (angular.isDefined(element.table)) {
                element["table"] = $rootScope.tablesMap[element.table.id];
                if ($rootScope.tables.indexOf(element.table) === -1) {
                    $rootScope.tables.push(element.table);
                }
            }
            $rootScope.processElement(element.datatype,element);
        } else if (element.type === "datatype") {
            if ($rootScope.datatypes.indexOf(element) === -1) {
                $rootScope.datatypes.push(element);
                angular.forEach(element.children, function (component) {
                    $rootScope.processElement(component,parent);
                });
            }
        }
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



});


app.factory('503Interceptor', function ($injector, $q, $rootScope) {
    return function (responsePromise) {
        return responsePromise.then(null, function (errResponse) {
            if (errResponse.status === 503) {
                $rootScope.showError(errResponse);
            } else {
                return $q.reject(errResponse);
            }
        });
    };
}).factory('sessionTimeoutInterceptor', function ($injector, $q, $rootScope) {
    return function (responsePromise) {
        return responsePromise.then(null, function (errResponse) {
            if (errResponse.reason === "The session has expired") {
                $rootScope.showError(errResponse);
            } else {
                return $q.reject(errResponse);
            }
        });
    };
});


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
