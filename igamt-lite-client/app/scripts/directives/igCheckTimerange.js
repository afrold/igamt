'use strict';

//This directive is used to make sure the start hour of a timerange is < of the end hour 
angular.module('igl').directive('igCheckTimerange', [
    function () {
        return {
            replace: true,
            link: function (scope, elem, attrs, ctrl) {
                //elem is a div element containing all the select input
                //each one of them has a class for easy selection
                var myElem = elem.children();
                var sh = myElem.find('.shour');
                var sm = myElem.find('.sminute');
                var eh = myElem.find('.ehour');
                var em = myElem.find('.eminute');

                var ctrlSH, ctrlSM, ctrlEH, ctrlEM;
                ctrlSH = sh.inheritedData().$ngModelController;
                ctrlSM = sm.inheritedData().$ngModelController;
                ctrlEH = eh.inheritedData().$ngModelController;
                ctrlEM = em.inheritedData().$ngModelController;
               
                var newnew = true;

                var checkTimeRange = function() {
                    if ( newnew ) {
                        //We only do that once to set the $pristine field to false
                        //Because if $pristine==true, and $valid=false, the visual feedback 
                        //are not displayed
                        ctrlSH.$setViewValue(ctrlSH.$modelValue);
                        ctrlSM.$setViewValue(ctrlSM.$modelValue);
                        ctrlEH.$setViewValue(ctrlEH.$modelValue);
                        ctrlEM.$setViewValue(ctrlEM.$modelValue);
                        newnew = false;
                    }
                    //Getting a date object
                    var tmpDate = new Date();
                    //init the start time with the dummy date
                    var startTime = angular.copy(tmpDate);
                    //init the end time with the same dummy date
                    var endTime =  angular.copy(tmpDate);

                    startTime.setHours(sh.val());
                    startTime.setMinutes(sm.val());
                    endTime.setHours(eh.val());
                    endTime.setMinutes(em.val());
                    
                    if ( startTime < endTime ) {
                        //console.log("Excellent!");
                        ctrlSH.$setValidity('poaOK', true);
                        ctrlSM.$setValidity('poaOK', true);
                        ctrlEH.$setValidity('poaOK', true);
                        ctrlEM.$setValidity('poaOK', true);
                    }
                    else {
                        //console.log("Bad... :(");
                        ctrlSH.$setValidity('poaOK', false);
                        ctrlSM.$setValidity('poaOK', false);
                        ctrlEH.$setValidity('poaOK', false);
                        ctrlEM.$setValidity('poaOK', false);
                    }
                };

                sh.on('change', checkTimeRange);
                sm.on('change', checkTimeRange);
                eh.on('change', checkTimeRange);
                em.on('change', checkTimeRange);
            }
        };
    }
]);