angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {




    $scope.init = function () {
        console.log("INIT called");
        ConfigurationService.findCurrent("IG Style").then(function (response) {
            $scope.IGStyleExportConfig = response;
            ConfigurationService.findCurrent("Profile Style").then(function (response) {
                $scope.profileStyleExportConfig = response;

                ConfigurationService.findCurrent("Table Style").then(function (response) {
                    $scope.tableStyleExportConfig = response;

                    $scope.exportConfig=$scope.IGStyleExportConfig;


                });
            });

        });



    }

    $scope.override=function(config){
        ConfigurationService.override(config).then(function(response){
            $scope.exportConfig=response;
        })
    }
    $scope.setConfigType=function(type){
        console.log(type);
        if(type==='IG'){
             $scope.exportConfig=$scope.IGStyleConfig;
        }else if (type==='Profile'){
            $scope.exportConfig=$scope.profileStyleConfig;
        }else{
            $scope.exportConfig=$scope.tableStyleConfig;
        }

    }



});
