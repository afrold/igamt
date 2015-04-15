'use strict';

//This directive is used to check password to make sure they meet the minimum requirements
angular.module('igl').directive('igPasswordValidate', [
	function () {
	    return {
	        require: 'ngModel',
	        link: function(scope, elm, attrs, ctrl) {
	            ctrl.$parsers.unshift(function(viewValue) {

	                scope.pwdValidLength = (viewValue && viewValue.length >= 7 ? 'valid' : undefined);
	                scope.pwdHasLowerCaseLetter = (viewValue && /[a-z]/.test(viewValue)) ? 'valid' : undefined;
	                scope.pwdHasUpperCaseLetter = (viewValue && /[A-Z]/.test(viewValue)) ? 'valid' : undefined;
	                scope.pwdHasNumber = (viewValue && /\d/.test(viewValue)) ? 'valid' : undefined;

	                if(scope.pwdValidLength && scope.pwdHasLowerCaseLetter && scope.pwdHasUpperCaseLetter && scope.pwdHasNumber) {
	                    ctrl.$setValidity('pwd', true);
	                    return viewValue;
	                } else {
	                    ctrl.$setValidity('pwd', false);
	                    return undefined;
	                }
	            });
	        }
	    };
	}
]);
