'use strict';

angular.module('igl').directive('igCheckPoaDate', [
    function () {
        return {
            replace: true,
            link: function (scope, elem, attrs, ctrl) {
                var startElem = elem.find('#inputStartDate');
                var endElem = elem.find('#inputEndDate');

                var ctrlStart = startElem.inheritedData().$ngModelController;
                var ctrlEnd = endElem.inheritedData().$ngModelController;

                var checkDates = function() {
                    var sDate = new Date(startElem.val());
                    var eDate = new Date(endElem.val());
                    if ( sDate < eDate ) {
                        //console.log("Good!");
                        ctrlStart.$setValidity('datesOK', true);
                        ctrlEnd.$setValidity('datesOK', true);
                    }
                    else {
                        //console.log(":(");
                        ctrlStart.$setValidity('datesOK', false);
                        ctrlEnd.$setValidity('datesOK', false);
                    }
                };

                startElem.on('change', checkDates);
                endElem.on('change', checkDates);
            }
        };
    }
]);