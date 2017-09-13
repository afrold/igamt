/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('ConfigurationController', function ($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout, ConfigurationService) {


  $scope.activeId="content";
  $scope.felxwidth=50;
  $scope.includePC=false;
  $scope.init = function () {

    $scope.tabActivity=[true,false,false,false];

    console.log("INIT called");

    $scope.configMap={};
    $scope.changed=false;
    $scope.activeId="content";
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

          ConfigurationService.findCurrent("Datatype Library").then(function (response) {
            var copy =response;
            $scope.datatypeLibraryExportConfigCopy=copy;
            $scope.datatypeLibraryExportConfig = angular.copy(response);
            $scope.configMap[response.id] = response;
          });

        });

      });

    });

  }

  $scope.initExportFont = function (){
    ConfigurationService.findFonts().then(function (response) {
      $scope.fonts=response;
      $scope.resetUserExportFontConfig();
      $scope.changed=false;
    });

  }

  $scope.saveUserExportFontConfig = function(userExportFontConfig){
    ConfigurationService.saveUserExportFontConfig(userExportFontConfig).then(function (response) {
      $scope.resetChanged();
      $rootScope.msg().text = "ConfigurationSaved";
      $rootScope.msg().type = "success";
      $rootScope.msg().show = true;
    }, function(error) {
      $rootScope.msg().text = "ConfigurationSavedFaild";
      $rootScope.msg().type = "danger";
      $rootScope.msg().show = true;
    });
  }

  $scope.resetUserExportFontConfig = function(){
    ConfigurationService.getUserExportFontConfig().then(function (response) {
      $scope.userExportFontConfig = response;
      $scope.updateUserFontRadio();
      $scope.resetChanged();
    });

  }

  $scope.restoreDefaultExportFontConfig = function(){
    ConfigurationService.restoreDefaultExportFontConfig().then(function(response){
      $scope.resetChanged();
      $scope.userExportFontConfig = response;
      $scope.updateUserFontRadio();
      $scope.resetChanged();
    });
  }

  $scope.updateUserFontRadio = function(){
    $scope.userExportFontConfig.exportFont = $scope.fonts.find($scope.isFontEqualToUsers);
  }

  $scope.isFontEqualToUsers = function(font){
    return font.name === $scope.userExportFontConfig.exportFont.name;
  }

  $scope.isActive=function(str){
    return str==$scope.activeId;
  }
  $scope.setActive=function(str){
    $scope.activeId=str;
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
        }else if($scope.exportConfig.type&& $scope.exportConfig.type==="Datatype Library"){
          $scope.datatypeLibraryExportConfig=$scope.exportConfig;
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
    );
  };
  $scope.setConfigType=function(type){
    console.log(type);
    if(type==='IG'){
      $scope.exportConfig=$scope.IGStyleConfig;
    }else if (type==='Profile'){
      $scope.exportConfig=$scope.profileStyleConfig;
    }else if(type==="DatatypeLibrary"){
      $scope.exportConfig = $scope.datatypeLibraryExportConfig;
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
    }else if(conf.type&& conf.type==="Datatype Library"){
      $scope.datatypeLibraryExportConfig= angular.copy($scope.datatypeLibraryExportConfigCopy);
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

  $scope.updateWidth=function(felxwidth){
    if(felxwidth){
      $scope.felxwidth=100/3;

    }else{
      $scope.felxwidth=100/2;
    }

  }

});
