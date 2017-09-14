/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('AddDatatypeCtrlFromUserLib',
  function($scope, $rootScope, $modalInstance, hl7Version, userDtLib, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http, datatypeLibrary, tableLibrary, versionAndUseMap) {
    $scope.versionAndUseMap = versionAndUseMap;
    $scope.DTlinksToAdd=[];
    $scope.newDts = [];
    $scope.checkedExt = true;
    $scope.NocheckedExt = true;
    $scope.userDtLib = userDtLib;
    $scope.selectedDatatypes = [];
    $scope.selectUserDtLib = function(usrLib) {
      console.log(usrLib);
      DatatypeLibrarySvc.getDatatypesByLibrary(usrLib.id).then(function(datatypes) {

        $scope.userDatatypes = datatypes;

        console.log($scope.userDatatypes);
      });
    };
    var listHL7Versions = function() {
      return $http.get('api/igdocuments/findVersions', {
        timeout: 60000
      }).then(function(response) {
        var hl7Versions = [];
        var length = response.data.length;
        for (var i = 0; i < length; i++) {
          hl7Versions.push(response.data[i]);
        }
        console.log(hl7Versions);
        return hl7Versions;
      });
    };

    var init = function() {
      listHL7Versions().then(function(versions) {
        $scope.hl7Datatypes = [];
        $scope.version1 = "";
        if($rootScope.igdocument){
          angular.forEach(versions, function(version){
            if(version>=$rootScope.igdocument.profile.hl7Versions){
              $scope.version.push(version);
            }
          });
        }
        else{
          $scope.versions = versions;
        }

        var scopes = ['HL7STANDARD'];
      });

    };
    init();
    $scope.setVersion = function(version) {
      $scope.version1 = version;
      var scopes = ['HL7STANDARD'];
      DatatypeService.getDataTypesByScopesAndVersion(scopes, version).then(function(result) {
        console.log("result");
        console.log(result);

        $scope.hl7Datatypes = result;

      });
    }
    $scope.addDt = function(datatype) {
      console.log(datatype);
      $scope.selectedDatatypes.push(datatype);
      console.log("chowing map");

      console.log($rootScope.versionAndUseMap[datatype.id]);
    };
    $scope.checkExist = function(datatype) {

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
    $scope.checkExt = function(datatype) {
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
    $scope.addDtFlv = function(datatype) {
      $scope.newDatatype = angular.copy(datatype);

      $scope.newDatatype.ext = Math.floor(Math.random() * 1000);

      console.log($scope.newDatatype.ext);
      $scope.newDatatype.scope = datatypeLibrary.scope;
      $scope.newDatatype.status = "UNPUBLISHED";
      $scope.newDatatype.publicationVersion = 0;
      $scope.newDatatype.participants = [];
      $scope.newDatatype.id = new ObjectId().toString();
      $scope.newDatatype.libIds = [];
      $scope.selectedDatatypes.push($scope.newDatatype);
      console.log($scope.selectedDatatypes)
      console.log($scope.newDatatype.ext);
      $scope.newDatatype.scope = datatypeLibrary.scope;
      $scope.newDatatype.status = "UNPUBLISHED";
      $scope.newDatatype.participants = [];
      $scope.newDatatype.id = new ObjectId().toString();;
      $scope.newDatatype.libIds = [];
      if ($scope.newDatatype.components != undefined && $scope.newDatatype.components != null && $scope.newDatatype.components.length != 0) {
        for (var i = 0; i < $scope.newDatatype.components.length; i++) {
          $scope.newDatatype.components[i].id = new ObjectId().toString();
        }
      }

      var predicates = $scope.newDatatype['predicates'];
      if (predicates != undefined && predicates != null && predicates.length != 0) {
        angular.forEach(predicates, function(predicate) {
          predicate.id = new ObjectId().toString();
        });
      }

      var conformanceStatements = $scope.newDatatype['conformanceStatements'];
      if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
        angular.forEach(conformanceStatements, function(conformanceStatement) {
          conformanceStatement.id = new ObjectId().toString();
        });
      }
      //  $scope.selectedDatatypes.push($scope.newDatatype);
      console.log($scope.selectedDatatypes)
    }
    $scope.deleteDt = function(datatype) {
      var index = $scope.selectedDatatypes.indexOf(datatype);
      if (index > -1) $scope.selectedDatatypes.splice(index, 1);
    };
    var secretEmptyKey = '[$empty$]';

    $scope.dtComparator = function(datatype, viewValue) {
      if (datatype) {
        console.log(datatype.name);
        console.log(datatype);
      }
      return viewValue === secretEmptyKey || (datatype && ('' + datatype.name).toLowerCase().indexOf(('' + viewValue).toLowerCase()) > -1);
    };


    $scope.isInDts = function(datatype) {

      if ($scope.hl7Datatypes.indexOf(datatype) === -1) {
        return false;
      } else {
        return true;
      }

    }


    $scope.selectDT = function(datatype) {
      console.log(datatype);
      $scope.newDatatype = datatype;
    };
    $scope.selected = function() {
      return ($scope.newDatatype !== undefined);
    };
    $scope.unselect = function() {
      $scope.newDatatype = undefined;
    };
    $scope.isActive = function(id) {
      if ($scope.newDatatype) {
        return $scope.newDatatype.id === id;
      } else {
        return false;
      }
    };

    $scope.ok = function() {
      console.log($scope.selectedDatatypes);
      $scope.selectFlv = [];
      var newLinks = [];
      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
        if ($scope.selectedDatatypes[i].scope === datatypeLibrary.scope) {
          $scope.selectFlv.push($scope.selectedDatatypes[i]);
        } else {
          newLinks.push({
            id: $scope.selectedDatatypes[i].id,
            name: $scope.selectedDatatypes[i].name
          })
        }
      }
      $rootScope.usedDtLink = [];
      $rootScope.usedVsLink = [];
      for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
        $rootScope.fillMaps($scope.selectedDatatypes[i]);
      }
      DatatypeService.saves($scope.selectFlv).then(function(result) {
        for (var i = 0; i < result.length; i++) {
          newLinks.push({
            id: result[i].id,
            name: result[i].name,
            ext: result[i].ext
          })
        }
        DatatypeLibrarySvc.addChildren(datatypeLibrary.id, newLinks).then(function(link) {
          for (var i = 0; i < newLinks.length; i++) {
            datatypeLibrary.children.splice(0, 0, newLinks[i]);
          }
          for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
            $rootScope.datatypes.splice(0, 0, $scope.selectedDatatypes[i]);
          }
          for (var i = 0; i < $scope.selectedDatatypes.length; i++) {
            $rootScope.datatypesMap[$scope.selectedDatatypes[i].id] = $scope.selectedDatatypes[i];
          }
          var usedDtId1 = _.map($rootScope.usedDtLink, function(num, key) {
            return num.id;
          });

          DatatypeService.get(usedDtId1).then(function(datatypes) {
            for (var j = 0; j < datatypes.length; j++) {
              if (!$rootScope.datatypesMap[datatypes[j].id]) {

                $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                $rootScope.datatypes.push(datatypes[j]);
                // $rootScope.getDerived(datatypes[j]);
              }
            }

            var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
              return num.id;
            });
            console.log("$rootScope.usedVsLink");

            console.log($rootScope.usedVsLink);
            var newTablesLink = _.difference($rootScope.usedVsLink, tableLibrary.children);
            console.log(newTablesLink);

            TableLibrarySvc.addChildren(tableLibrary.id, newTablesLink).then(function() {
              tableLibrary.children = _.union(newTablesLink, tableLibrary.children);

              TableService.get(usedVsId).then(function(tables) {
                for (var j = 0; j < tables.length; j++) {
                  if (!$rootScope.tablesMap[tables[j].id]) {
                    $rootScope.tablesMap[tables[j].id] = tables[j];
                    $rootScope.tables.push(tables[j]);

                  }
                }


                $modalInstance.close(datatypes);

              });
            });
          });
          $rootScope.msg().text = "datatypeAdded";
          $rootScope.msg().type = "success";
          $rootScope.msg().show = true;

        });

      }, function(error) {
        $rootScope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });


    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };
  });
