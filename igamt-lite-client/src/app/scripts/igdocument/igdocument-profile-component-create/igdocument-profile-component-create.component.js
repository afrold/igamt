/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('createProfileComponentCtrl',
  function ($scope, $rootScope, $mdDialog, $http, PcService, IgDocumentService) {
    $scope.create = function () {
      var newPC = {
        name: $scope.name,
        description: $scope.description,
        comment: $scope.comment,
        appliedTo: [],
        children: []
      };
      console.log(newPC);

      //add save function

      IgDocumentService.saveProfileComponent($rootScope.igdocument.id, newPC).then(function (profileC) {
        $rootScope.profileComponent = profileC;
        console.log(profileC);

        $rootScope.igdocument.profile.profileComponentLibrary.children.push(profileC);
        $rootScope.profileComponents.push(profileC);
        $rootScope.profileComponentsMap[profileC.id] = profileC;
        $rootScope.Activate(profileC.id);
        $mdDialog.hide(profileC);

      });


    };

    $scope.cancel = function () {
      $mdDialog.hide();
    };
  });
