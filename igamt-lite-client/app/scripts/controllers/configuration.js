angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {




    $scope.init = function () {
        console.log("INIT called");
        $scope.configMap={};
        ConfigurationService.findCurrent("IG Style").then(function (response) {
            $scope.IGStyleExportConfig = response;
             $scope.configMap[response.id] = angular.copy(response);

            ConfigurationService.findCurrent("Profile Style").then(function (response) {
                $scope.profileStyleExportConfig = response;
                 $scope.configMap[response.id]=angular.copy(response);
                ConfigurationService.findCurrent("Table Style").then(function (response) {
                    $scope.tableStyleExportConfig = response;
                     $scope.configMap[response.id] = angular.copy(response);

                    $scope.exportConfig=$scope.IGStyleExportConfig;
                    $rootScope.$emit("event:initEditArea");


                });
            });

        });



    }

    $scope.override=function(config){
        ConfigurationService.override(config).then(function(response){
            $scope.exportConfig=response;
        	$rootScope.clearChanges();
            $rootScope.msg().text = "ConfigurationSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function(error) {
            $rootScope.msg().text = "ConfigurationSavedFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        }
        )
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
    $scope.reset=function(conf){
        conf= $scope.configMap[conf.id];
    }



});
