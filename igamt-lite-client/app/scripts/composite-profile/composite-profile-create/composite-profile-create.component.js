/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('createCompositeProfileCtrl',
  function ($scope, $rootScope, $mdDialog, $http, $filter, PcService, IgDocumentService, CompositeProfileService) {
    $scope.compositeMetaData = true;
    $scope.tabStatus = {
      active: 0
    };
    $scope.next = function () {
      $scope.tabStatus.active++;
    };
    $scope.back = function () {
      $scope.tabStatus.active--;
    };
    $scope.pcList = [];
    $scope.baseProfiles = $rootScope.messages.children;
    $scope.pcs = $rootScope.profileComponents;
    $scope.position = 1;

    $scope.checkCompositeExt = function (ext) {
      for (var i = 0; i < $rootScope.compositeProfiles.length; i++) {

        if (ext === $rootScope.compositeProfilesStructureMap[$rootScope.compositeProfiles[i].id].ext) {
          return true;
        }

      }
      return false;
    }


    $scope.changePage = function () {
      $scope.compositeMetaData = !$scope.compositeMetaData;
    };

    $scope.selectBaseProfile = function (baseP) {
      $scope.baseP = angular.copy(baseP);
    };
    $scope.checkExist = function (pc) {
      for (var i = 0; i < $scope.pcList.length; i++) {
        if ($scope.pcList[i].id === pc.id) {
          return true;
        }
      }
      return false;
    };
    $scope.removePc = function (pc) {
      var positionToRemove = pc.position;
      var index = $scope.pcList.indexOf(pc);
      if (index > -1) $scope.pcList.splice(index, 1);
      for (var i = 0; i < $scope.pcList.length; i++) {
        if ($scope.pcList[i].position >= positionToRemove) {
          $scope.pcList[i].position = $scope.pcList[i].position - 1;
        }
      }
      $scope.position = $scope.position - 1;

    };
    $scope.selectPC = function (pc) {
      var interPc = {
        id: pc.id,
        name: pc.name,
        description: pc.description,
        comment: pc.comment,
        pcDate: new Date(),
        position: angular.copy($scope.position)
      }
      console.log(interPc);
      $scope.pcList.push(interPc);
      $scope.position = $scope.position + 1;
    };
    $scope.create = function () {
      console.log("=========");
      var compositeProfileStructure = {
        id: new ObjectId().toString(),
        coreProfileId: $scope.baseP.id,
        profileComponentsInfo: $scope.pcList,
        name: $scope.name,
        ext: $scope.ext,
        description: $scope.description,
        comment: $scope.comment
      };
      CompositeProfileService.create(compositeProfileStructure, $rootScope.igdocument.id).then(function (cpStructure) {
        console.log(cpStructure);
        CompositeProfileService.build(cpStructure).then(function (result) {
          console.log("composite");
          console.log(result);
          console.log(cpStructure);
          $rootScope.igdocument.profile.compositeProfiles.children.push(cpStructure);
          for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
            if ($rootScope.igdocument.profile.messages.children[i].id === cpStructure.coreProfileId) {
              if ($rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList === null) {
                $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList = [];
              }
              $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList.push(cpStructure.id);
              $rootScope.messagesMap[$rootScope.igdocument.profile.messages.children[i].id] = $rootScope.igdocument.profile.messages.children[i];
            }
          }
          for (var i = 0; i < cpStructure.profileComponentIds.length; i++) {
            for (var j = 0; j < $rootScope.profileComponents.length; j++) {
              if (cpStructure.profileComponentIds[i] === $rootScope.profileComponents[j].id) {
                if ($rootScope.profileComponents[j].compositeProfileStructureList === null) {
                  $rootScope.profileComponents[j].compositeProfileStructureList = [];
                }
                $rootScope.profileComponents[j].compositeProfileStructureList.push(cpStructure.id);
                $rootScope.profileComponentsMap[$rootScope.profileComponents[j].id] = $rootScope.profileComponents[j];
              }
            }
          }
          $rootScope.compositeProfilesStructureMap[cpStructure.id] = cpStructure;
          console.log($rootScope.igdocument);

          $mdDialog.hide(cpStructure);


        });

      });

    };

    $scope.cancel = function () {
      $mdDialog.hide();
    };


  });
