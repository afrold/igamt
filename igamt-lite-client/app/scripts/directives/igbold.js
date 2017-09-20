'use strict';

//This directive is used to highlight the cehrt that is active
angular.module('igl').directive('ehrbold', [
    function () {
        return {
            restrict: 'C',
            link: function(scope, element, attrs) {
//                element.on('click', function() {
//                    element.siblings().removeClass('cehrtactive');
//                    element.siblings().children().removeClass('cehrtDeleteButtonActive');
//                    element.siblings().children().addClass('cehrtDeleteButtonNotActive');
//
//                    element.addClass('cehrtactive');
//                    element.children().removeClass('cehrtDeleteButtonNotActive');
//                    element.children().addClass('cehrtDeleteButtonActive');
//                });
            }
        };
    }
]);
