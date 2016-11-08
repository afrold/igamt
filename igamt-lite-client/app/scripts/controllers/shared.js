angular
    .module('igl')
    .controller(
    'shared', ['$scope',
        '$rootScope',
        '$http',
        'SectionSvc',
        'CloneDeleteSvc',
        'FilteringSvc',
        '$cookies',
        'DatatypeLibrarySvc',
        '$modal',
        'DatatypeService',

        function ($scope, $rootScope, $http, SectionSvc, CloneDeleteSvc, FilteringSvc, $cookies,DatatypeLibrarySvc,$modal,DatatypeService) {

            $scope.selectedTab==0;
            $scope.sharedElementView='sharedElementView';
            $scope.SharedDataTypeTree=[]
            $scope.typeOfSharing="Pending";

            $scope.setTypeOfSharing=function(type){
                $scope.typeOfSharing=type;
                if(type==='datatype'){
                    console.log("TOCs")
                    $scope.SharedtocView='sharedtocView.html';
                }
            }
            $scope.init=function(){
                $scope.createDummyLibrary();

                $scope.SharedDataTypeTree.push( $scope.library);
            }
            
            $scope.createDummyLibrary=function(){
                console.log("coalled")
                DatatypeService.getSharedDatatypes().then(function(result){
                    console.log("result");
                    console.log(result);
                    $scope.datatypes=result;
                });

              //  $scope.datatypes=[{id:1, name:"dummy",description:"dummy"},{id:2, name:"dummy",description:"dummy"}];
                $scope.tables=[{id:3, name:"dummy",description:"dummy"},{id:4, name:"dummy",description:"dummy"}];
                $scope.library={name:"bla"};
                

            }









        }]);
          