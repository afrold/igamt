/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('AddDatatypesFromLibtoLib',
  function ($scope, $rootScope, $mdDialog, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http,  masterLib, datatypeLibrary, versionAndUseMap) {

    //$scope.hl7Version = hl7Version;
    //$scope.hl7Datatypes = datatypes;

    $scope.newDts = [];
    $scope.checkedExt = true;
    $scope.NocheckedExt = true;
    $scope.masterLib = masterLib;
    $scope.selectedDatatypes = [];
    // for (var i = 0; i < $scope.masterDts.length; i++) {
    //     if (!$rootScope.datatypesMap[$scope.masterDts[i].id]) {
    //         $scope.masterDatatypes.push($scope.masterDts[i]);
    //     }
    $scope.masLib=null;

    $scope.selectMasterDtLib = function(masLib) {
      DatatypeLibrarySvc.getPublishedDatatypesByLibraryForLibray(masLib.id).then(function(datatypes) {
        console.log(datatypes);
        $scope.scope=$rootScope.datatypeLibrary.scope;
        $scope.hl7Datatypes = datatypes
      });


    };
    $scope.addDt = function (datatype) {
      console.log(datatype);
      $scope.selectedDatatypes.push(datatype);

    };
    $scope.checkExist = function (datatype) {

      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
        if ($scope.selectedDatatypes[i].id === datatype.id) {
          return true;
        }
      }
      for (var i = 0; i < $rootScope.datatypes.length; i++) {
        if ($rootScope.datatypes[i].id === datatype.id) {
          return true;
        }
      }
      return false;
    }
    $scope.checkExt = function (datatype) {
      $scope.checkedExt = true;
      $scope.NocheckedExt = true;
      if (datatype.ext === "") {
        $scope.NocheckedExt = false;
        return $scope.NocheckedExt;
      }
      for (var i = 0; i < $rootScope.datatypes.length; i++) {
        if ($rootScope.datatypes[i].name === datatype.name && $rootScope.datatypes[i].ext === datatype.ext) {
          $scope.checkedExt = false;
          return $scope.checkedExt;
        }
      }
      console.log($scope.selectedDatatypes.indexOf(datatype));
      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
        if ($scope.selectedDatatypes.indexOf(datatype) !== i) {
          if ($scope.selectedDatatypes[i].name === datatype.name && $scope.selectedDatatypes[i].ext === datatype.ext) {
            $scope.checkedExt = false;
            return $scope.checkedExt;
          }
        }

      }

      return $scope.checkedExt;
    };

    $scope.addDtFlv = function (datatype) {
      var newDatatype = angular.copy(datatype);
      newDatatype.publicationVersion = 0;

      newDatatype.ext = $rootScope.createNewExtension(newDatatype.ext);
      newDatatype.scope = 'USER';
      newDatatype.status = 'UNPUBLISHED';
      newDatatype.participants = [];
      newDatatype.id = new ObjectId().toString();
      newDatatype.libIds = [];

      if (newDatatype.components != undefined && newDatatype.components != null && newDatatype.components.length != 0) {
        for (var i = 0; i < newDatatype.components.length; i++) {
          newDatatype.components[i].id = new ObjectId().toString();
        }
      }

      var predicates = newDatatype['predicates'];
      if (predicates != undefined && predicates != null && predicates.length != 0) {
        angular.forEach(predicates, function (predicate) {
          predicate.id = new ObjectId().toString();
        });
      }

      var conformanceStatements = newDatatype['conformanceStatements'];
      if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
        angular.forEach(conformanceStatements, function (conformanceStatement) {
          conformanceStatement.id = new ObjectId().toString();
        });
      }
      $scope.selectedDatatypes.push(newDatatype);
      console.log($scope.selectedDatatypes);
    }
    $scope.deleteDt = function (datatype) {
      var index = $scope.selectedDatatypes.indexOf(datatype);
      if (index > -1) $scope.selectedDatatypes.splice(index, 1);
    };
    var secretEmptyKey = '[$empty$]'
    //
    // $scope.hl7Datatypes = datatypes.filter(function (current) {
    //     return $rootScope.datatypes.filter(function (current_b) {
    //             return current_b.id == current.id;
    //         }).length == 0
    // });


    $scope.dtComparator = function (datatype, viewValue) {
      if (datatype) {
        console.log(datatype.name);
        console.log(datatype);
      }
      return viewValue === secretEmptyKey || (datatype && ('' + datatype.name).toLowerCase().indexOf(('' + viewValue).toLowerCase()) > -1);
    };


    $scope.isInDts = function (datatype) {

      if ($scope.hl7Datatypes.indexOf(datatype) === -1) {
        return false;
      } else {
        return true;
      }

    }


    $scope.selectDT = function (datatype) {
      console.log(datatype);
      $scope.newDatatype = datatype;
    };
    $scope.selected = function () {
      return ($scope.newDatatype !== undefined);
    };
    $scope.unselect = function () {
      $scope.newDatatype = undefined;
    };
    $scope.isActive = function (id) {
      if ($scope.newDatatype) {
        return $scope.newDatatype.id === id;
      } else {
        return false;
      }
    };

    $scope.ok = function () {
      $mdDialog.hide($scope.selectedDatatypes);
    };

    $scope.cancel = function () {
      $mdDialog.hide('cancel');
    };
  });
