/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller(
  'HL7VersionsDlgCtrl',
  function($scope, $rootScope, $mdDialog, $log, $http, $httpBackend, userInfoService) {

    $rootScope.clickSource = {};
    $scope.selectedHL7Version = "";

    $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();

    $scope.create = function(clickSource) {
      $rootScope.clickSource = clickSource;
      if ($rootScope.hasChanges()) {
        $rootScope.openConfirmLeaveDlg().then(function() {
          $rootScope.clearChanges();
          $rootScope.closeIGDocument();
          $rootScope.hl7Versions = [];
          $scope.hl7VersionsInstance();
        });
      } else {
        if (clickSource === 'btn' && $rootScope.igdocument != null) {
          $rootScope.clearChanges();
          $rootScope.closeIGDocument();
        }
        $rootScope.hl7Versions = [];
        $scope.hl7VersionsInstance();
      }
    };

    $scope.confirmOpen = function(igdocument) {
      return $mdDialog.show({
        templateUrl: 'ConfirmIGDocumentOpenCtrl.html',
        controller: 'ConfirmIGDocumentOpenCtrl',
        resolve: {
          igdocumentToOpen: function() {
            return igdocument;
          }
        }
      }).then(function(igdocument) {

        $rootScope.clearChanges();

        $scope.hl7VersionsInstance();
      }, function() {
        console.log("Changes discarded.");
      });
    };

     $scope.compareWith=function (str1, str2) {
         var n = str1.localeCompare(str2);
         return n>-1;


     };
    $scope.hl7VersionsInstance = function() {
      $scope.listHL7Versions().then(function(response) {
        var hl7Versions = [];
        var length = response.data.length;
        for (var i = 0; i < length; i++) {
          hl7Versions.push(response.data[i]);
        }
        return $mdDialog.show({
          templateUrl: 'hl7VersionsDlgMD.html',
          controller: 'HL7VersionsInstanceDlgCtrl',
          scope: $scope,
          preserveScope: true,
          resolve: {
            hl7Versions: function() {
              return hl7Versions;
            },
            hl7Version: function() {
              console.log("$rootScope.clickSource=" + $rootScope.clickSource);
              if ($rootScope.clickSource === "ctx") {
                return null;
              } else {
                return null;
              }
            }
          }
        }).then(function(result) {
        });
      }, function(response) {
        $rootScope.msg().text = "Cannot load the versions. Please try again";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
      });


    };

    $scope.listHL7Versions = function() {
      return $http.get('api/igdocuments/findVersions', {
        timeout: 60000
      });
    };


    $scope.closedCtxMenu = function(node, $index) {
      console.log("closedCtxMenu");
    };

  });
