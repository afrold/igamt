'use strict';

/**
 * @ngdoc function
 * @description
 * 
 * This service enables the MessageEvents structure to be accessed from both the
 * controllers of the Create IG Dialog.
 */

angular.module('igl').factory('DatatypeLibrarySvc', function($http) {
	
	var svc = this;
	
	svc.messagesByVersion = {};
	
	svc.getDatatypeLibrary = function(scope) {
		return $http.get('api/datatype-library', angular.fromJson({
					"scope" : scope
				})).then(function(response) {
				return angular.fromJson(response.data)});
			};
	};

	svc.saveDatatypeLibrary = function(library) {
		return $http.post('api/datatype-library/save', angular.fromJson({
			"library" : library
		})).then(function(response) {
		return angular.fromJson(response.data)});
	};

	return svc;
});