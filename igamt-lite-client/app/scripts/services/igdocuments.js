'use strict';

/**
 * @ngdoc function
 * @description # AboutCtrl Controller of the clientApp
 */
//
// // Declare factory
// angular.module('igl').factory('Profiles', function(Restangular) {
// return Restangular.service('profiles');
// });

angular.module('igl').factory('Section', function($http, $q) {
	var Section = function() {
		this.data = null;
		this.type = null;
		this.sections = [];
	};
	return Section;
});

angular.module('igl').factory('IGDocumentSvc', function($http, $q, $rootScope) {
	var IGDocumentSvc = {
		loadIgDocumentMetaData : function() {
			var delay = $q.defer();
			if ($rootScope.config || $rootScope.config === null) {
				$http.get('api/igdocuments/config').then(function(response) {
					$rootScope.config = angular.fromJson(response.data);
					delay.resolve($rootScope.config);
				}, function(error) {
					delay.reject(error);
				});
			} else {
				delay.resolve($rootScope.config);
			}
			return delay.promise;
		}
	}
	
	return IGDocumentSvc;
});
