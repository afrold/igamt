'use strict';

angular.module('igl').directive('igCheckUsername', [ '$resource',
	function ($resource) {
	    return {
	        restrict: 'AC',
	        require: 'ngModel',
	        link: function (scope, element, attrs, ctrl) {
	            var Username = $resource('api/sooa/usernames/:username', {username: '@username'});

	            element.on('keyup', function() {
	                if ( element.val().length >= 4 ) {
	                    var usernameToCheck = new Username({username:element.val()});
	                    //var delay = $q.defer();
	                    usernameToCheck.$get(function() {
	                        scope.usernameValidLength  = (element.val() && element.val().length >= 4 && element.val().length <= 20 ? 'valid' : undefined);
	                        scope.usernameUnique  = ((usernameToCheck.text === 'usernameNotFound') ? 'valid' : undefined);

	                        if(scope.usernameValidLength && scope.usernameUnique ) {
	                            ctrl.$setValidity('username', true);
	                        } else {
	                            ctrl.$setValidity('username', false);
	                        }

	                    }, function() {
	                        //console.log("FAILURE", usernameToCheck);
	                    });
	                }
	                else {
	                    scope.usernameValidLength = undefined;
	                    scope.usernameUnique = undefined;
	                    ctrl.$setValidity('username', false);
	                }
	            });
	        }
	    };
	}
]);
