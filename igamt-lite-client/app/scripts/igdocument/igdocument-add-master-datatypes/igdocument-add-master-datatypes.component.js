/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddMasterDatatypes',
  function ($scope, $rootScope, $modalInstance, datatypes, DatatypeLibrarySvc, DatatypeService) {
    $scope.version = $rootScope.igdocument.profile.metaData.hl7Version;
    $scope.scopes = ["MASTER"];
    $scope.masterDatatypes = [];
    $scope.newDts = [];
    DatatypeService.getPublishedMaster().then(function (result) {
      $scope.masterDatatypes = result;

    });


    $scope.ok = function () {
      var newLink = angular.fromJson({
        id: $scope.newDatatype.id,
        name: $scope.newDatatype.name
      });

      DatatypeLibrarySvc.addChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function (link) {
        $rootScope.igdocument.profile.datatypeLibrary.children.splice(0, 0, newLink);
        $rootScope.datatypes.splice(0, 0, $scope.newDatatype);
        $rootScope.datatype = $scope.newDatatype;
        $rootScope.datatypesMap[$scope.newDatatype.id] = $scope.newDatatype;
        $rootScope.processElement($scope.newDatatype);
        $rootScope.filteredDatatypesList.push($scope.newDatatype);
        $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
        $rootScope.$broadcast('event:openDatatype', $scope.newDatatype);
        $rootScope.msg().text = "datatypeAdded";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        $mdDialog.hide(datatypes);
      }, function (error) {
        $rootScope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    };

    $scope.cancel = function () {
      $modalInstance.dismiss('cancel');
    };
  });
