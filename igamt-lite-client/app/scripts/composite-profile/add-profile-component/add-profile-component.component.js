/**
 * Created by haffo on 9/11/17.
 */



angular.module('igl').controller('addMorePcsToCompositeProfileCtrl',
  function ($scope, $rootScope, $mdDialog, $http, $filter, compositeProfileStructure, PcService, IgDocumentService, CompositeProfileService) {
    console.log(compositeProfileStructure);
    $scope.compositeProfileStructure = compositeProfileStructure;
    $rootScope.coreMessageMetaData = {
      name: $rootScope.messagesMap[$scope.compositeProfileStructure.coreProfileId].name,
      identifier: $rootScope.messagesMap[$scope.compositeProfileStructure.coreProfileId].identifier,
      description: $rootScope.messagesMap[$scope.compositeProfileStructure.coreProfileId].description,
      comment: $rootScope.messagesMap[$scope.compositeProfileStructure.coreProfileId].comment
    };
    var usedPcs = [];
    for (var i = 0; i < $rootScope.profileComponents.length; i++) {
      if ($rootScope.profileComponents[i].compositeProfileStructureList) {
        for (var j = 0; j < $rootScope.profileComponents[i].compositeProfileStructureList.length; j++) {
          if ($rootScope.profileComponents[i].compositeProfileStructureList[j] === $scope.compositeProfileStructure.id) {
            usedPcs.push($rootScope.profileComponents[i]);
          }
        }
      }

    }
    console.log("pcs");
    console.log($rootScope.profileComponents);
    console.log($scope.compositeProfileStructure);
    $scope.pcs = _.difference($rootScope.profileComponents, usedPcs);
    console.log($scope.pcs);
    $scope.position = $scope.compositeProfileStructure.profileComponentsInfo.length + 1;
    $scope.pcList = [];
    $scope.existingPcList = usedPcs;


    $scope.checkExist = function (pc) {
      for (var i = 0; i < $scope.pcList.length; i++) {
        if ($scope.pcList[i].id === pc.id) {
          return true;
        }
      }
      for (var i = 0; i < $scope.existingPcList.length; i++) {
        if ($scope.existingPcList[i].id === pc.id) {
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
        authorNotes: pc.authorNotes,
        pcDate: pc.dateUpdated,
        position: angular.copy($scope.position)
      }
      console.log(interPc);
      $scope.pcList.push(interPc);
      $scope.position = $scope.position + 1;
    };
    $scope.update = function () {

      for (var i = 0; i < $scope.pcList.length; i++) {
        $scope.compositeProfileStructure.profileComponentsInfo.push($scope.pcList[i]);
      }

      CompositeProfileService.addPcs($scope.pcList, $scope.compositeProfileStructure.id).then(function (cpStructure) {
        //new tp update pcs and messages in the front end

        CompositeProfileService.build(cpStructure).then(function (result) {

          for (var i = 0; i < $rootScope.igdocument.profile.compositeProfiles.children.length; i++) {
            if ($rootScope.igdocument.profile.compositeProfiles.children[i].id === cpStructure.id) {
              $rootScope.igdocument.profile.compositeProfiles.children[i] = cpStructure;
              $rootScope.compositeProfilesStructureMap[cpStructure.id] = cpStructure;
            }
          }
          // for (var i = 0; i < $rootScope.igdocument.profile.messages.children.length; i++) {
          //     if ($rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList === null) {
          //         $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList = [];
          //     }
          //     for (var j = 0; j < $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList.length; j++) {
          //         if ($rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList[j] === cpStructure.id) {
          //             $rootScope.igdocument.profile.messages.children[i].compositeProfileStructureList[j] = cpStructure;
          //             $rootScope.messagesMap[$rootScope.igdocument.profile.messages.children[i].id] = $rootScope.igdocument.profile.messages.children[i];
          //         }
          //     }
          // }
          console.log($rootScope.profileComponents);
          for (var i = 0; i < $rootScope.profileComponents.length; i++) {
            if ($rootScope.profileComponents[i].compositeProfileStructureList === null) {
              $rootScope.profileComponents[i].compositeProfileStructureList = [];
            }
            if (!_.find($rootScope.profileComponents[i].compositeProfileStructureList, function (cp) {
                return cp === cpStructure.id;
              })) {
              $rootScope.profileComponents[i].compositeProfileStructureList.push(cpStructure.id);
            }
            $rootScope.profileComponentsMap[$rootScope.profileComponents[i].id] = $rootScope.profileComponents[i];
            // for (var j = 0; j < $rootScope.profileComponents[i].compositeProfileStructureList.length; j++) {

            //     if ($rootScope.profileComponents[i].compositeProfileStructureList[j].id === cpStructure.id) {
            //         //$rootScope.profileComponents[i].compositeProfileStructureList[j] = cpStructure;

            //     }
            // }
          }

          console.log($rootScope.igdocument);
          $mdDialog.hide(cpStructure);

        });

      });

    };

    $scope.cancel = function () {
      $mdDialog.hide('cancel');
    };


  });
