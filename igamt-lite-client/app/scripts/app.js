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
        'restangular',
        'ngMockE2E'
    ]);

app.config(function ($routeProvider, RestangularProvider) {
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

});

app.run(function ($rootScope, $location, Restangular) {

    $rootScope.profile = {};
    $rootScope.settings = {step : 0};
    $rootScope.statuses = ['Draft', 'Active', 'Superceded', 'Withdrawn'];
    $rootScope.hl7Versions = ['2.0', '2.1', '2.2', '2.3','2.3.1', '2.4','2.5','2.5.1','2.6','2.7','2.8'];
    $rootScope.schemaVersions = ['1.0', '1.5', '2.0', '2.5'];


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

    Restangular.setBaseUrl('/api/');
//    Restangular.setResponseExtractor(function(response, operation) {
//        return response.data;
//    });


});

//
//angular.module('ui.bootstrap.carousel', ['ui.bootstrap.transition'])
//    .controller('CarouselController', ['$scope', '$timeout', '$transition', '$q', function ($scope, $timeout, $transition, $q) {
//    }]).directive('carousel', [function () {
//        return {
//
//        }
//    }]);
