angular.module('igl')
    .directive('ngCheckcompext', function($rootScope) {
        return {
            restrict: 'AE', //E = element, A = attribute, C = class, M = comment
            require: 'ngModel',
            scope: {
                // compositeprofiles: '=',
                property: '@'
            },


            //templateUrl: 'label.html',
            //controller: 'labelController', //Embed a custom controller in the directive
            link: function(scope, element, attrs, ctrl) {
                ctrl.$validators.checkcompext = function(model, viewValue) {
                    console.log(scope.property);
                    if (scope.property === "new") {
                        for (var i = 0; i < $rootScope.compositeProfiles.length; i++) {

                            if (viewValue === $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].ext) {
                                return false;
                            }

                        }
                        return true;

                    } else if (scope.property === "old") {
                        for (var i = 0; i < $rootScope.compositeProfiles.length; i++) {

                            if (viewValue === $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].ext && $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].id !== $rootScope.compositeProfileStructure.id) {
                                return false;
                            }

                        }
                        return true;
                    }



                }
            }
        }
    });