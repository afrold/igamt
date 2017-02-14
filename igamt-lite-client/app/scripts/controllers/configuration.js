angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {




    $scope.init = function () {
        $scope.tabActivity=[true,false,false,false];

        console.log("INIT called");

        $scope.configMap={};
        $scope.changed=false;
        ConfigurationService.findCurrent("IG Style").then(function (response) {
            var copy=response;
            $scope.IGStyleExportConfig = angular.copy(response);
            $scope.IGStyleExportConfigCopy=copy;
             $scope.configMap[response.id] = response;

            ConfigurationService.findCurrent("Profile Style").then(function (response) {
                var copy=response;
                $scope.profileStyleExportConfigCopy=copy;
                $scope.profileStyleExportConfig = angular.copy(response);

                 $scope.configMap[response.id]=response;
                ConfigurationService.findCurrent("Table Style").then(function (response) {
                    var copy =response;
                    $scope.tableStyleExportConfigCopy=copy;
                     $scope.tableStyleExportConfig = angular.copy(response);
                     $scope.configMap[response.id] = response;

                    //$scope.exportConfig=$scope.IGStyleExportConfig;
         


                });
            });

        });



    }

    $scope.override=function(config, type){
        config.defaultType=false;
        config.type = type;
        $scope.resetChanged();

        ConfigurationService.override(config).then(function(response){

            $scope.exportConfig=response;

            $scope.resetChanged();
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

      $scope.restoreDefault=function(config,type){
        config.defaultType=false;
        config.type = type;
        ConfigurationService.restoreDefault(config).then(function(response){
            console.log(response);
            $scope.exportConfig=response;
            if($scope.exportConfig.type&& $scope.exportConfig.type==="IG Style"){
                $scope.IGStyleExportConfig =$scope.exportConfig;
            }else if($scope.exportConfig.type&& $scope.exportConfig.type==="Profile Style"){
                 $scope.profileStyleExportConfig=$scope.exportConfig;
            }else{
                $scope.tableStyleExportConfig=$scope.exportConfig;
            }
            $scope.resetChanged();

            $rootScope.msg().text = "DefaultRestored";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function(error) {
            $rootScope.msg().text = "DefaultRestoredFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        }
        )
    };
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
    $scope.reset=function(conf, type){
        conf.type = type;
        console.log(conf);
        console.log($scope.configMap[conf.id]);
            if(conf.type&& conf.type==="IG Style"){
                $scope.IGStyleExportConfig = angular.copy($scope.IGStyleExportConfigCopy);
            }else if(conf.type&& conf.type==="Profile Style"){
                 $scope.profileStyleExportConfig= angular.copy($scope.profileStyleExportConfigCopy);
            }else{
                $scope.tableStyleExportConfig=angular.copy($scope.tableStyleExportConfig);
            }
        console.log(conf);
        //console.log(configMap);
        $scope.resetChanged();
    }

    $scope.setChanged=function(){
        $scope.changed=true;
    }
    $scope.resetChanged=function(){
        $scope.changed=false;
    }

     $scope.getStyle=function(bool){
         var beige={'background':'#ffcc00'};
         var gray={'background':'gainsboro' };

         if(bool){
             return beige;
         }else{
             return gray;
         }
     }

});
