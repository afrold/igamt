/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('AddDatatypeDlgCtl',
  function ($scope, $rootScope, $mdDialog, hl7Version, datatypes, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http) {

    //$scope.hl7Version = hl7Version;
    //$scope.hl7Datatypes = datatypes;
    $scope.TablesIds=[];
    $scope.newDts = [];
    $scope.checkedExt = true;
    $scope.DTlinksToAdd=[];
    $scope.newDts = [];
    $scope.checkedExt = true;
    $scope.NocheckedExt = true;
    $scope.masterLib = [];
    $scope.selectedDatatypes = [];
    // for (var i = 0; i < $scope.masterDts.length; i++) {
    //     if (!$rootScope.datatypesMap[$scope.masterDts[i].id]) {
    //         $scope.masterDatatypes.push($scope.masterDts[i]);
    //     }
    // }
    var listHL7Versions = function () {
      return $http.get('api/igdocuments/findVersions', {
        timeout: 60000
      }).then(function (response) {
        var hl7Versions = [];
        var length = response.data.length;
        for (var i = 0; i < length; i++) {
          hl7Versions.push(response.data[i]);
        }
        console.log(hl7Versions);
        return hl7Versions;
      });
    };

    var init = function () {
      listHL7Versions().then(function (versions) {
        //$scope.versions = versions;
        var v = [];
        for (var i = 0; i < versions.length; i++) {
          if (versions.indexOf(hl7Version) <= i) {
            v.push(versions[i]);
          }
        }

        $scope.version1 = hl7Version;
        $scope.versions = v;
        var scopes = ['HL7STANDARD'];
        DatatypeService.getDataTypesByScopesAndVersion(scopes, hl7Version).then(function (result) {
          console.log("result");
          console.log(result);
          $scope.hl7Datatypes = result;

          // $scope.hl7Segments = result.filter(function(current) {
          //     return $rootScope.segments.filter(function(current_b) {
          //         return current_b.id == current.id;
          //     }).length == 0
          // });


          console.log("addSegment scopes=" + scopes.length);


        });
      });

    };
    init();
    $scope.setVersion = function (version) {
      $scope.version1 = version;
      var scopes = ['HL7STANDARD'];
      DatatypeService.getDataTypesByScopesAndVersion(scopes, version).then(function (result) {
        console.log("result");
        console.log(result);
        $scope.hl7Datatypes = result;

        // $scope.hl7Segments = result.filter(function(current) {
        //     return $rootScope.segments.filter(function(current_b) {
        //         return current_b.id == current.id;
        //     }).length == 0
        // });


      });
    }


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
      newDatatype.libIds.push($rootScope.igdocument.profile.datatypeLibrary.id);

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
      console.log($scope.selectedDatatypes)
    }
    $scope.deleteDt = function (datatype) {
      var index = $scope.selectedDatatypes.indexOf(datatype);
      if (index > -1) $scope.selectedDatatypes.splice(index, 1);
    };
    var secretEmptyKey = '[$empty$]'

    $scope.hl7Datatypes = datatypes.filter(function (current) {
      return $rootScope.datatypes.filter(function (current_b) {
          return current_b.id == current.id;
        }).length == 0
    });


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


    $scope.processAddedDT=function(datatype){
      if(!$rootScope.datatypesMap[datatype.id]){
        $scope.DTlinksToAdd.push({
          id: datatype.id,
          name: datatype.name,
          ext:datatype.ext
        });
      }
      if(datatype.components&&datatype.components.length!=0){
        angular.forEach(datatype.components, function(component){
          if(component.datatype){

            $scope.processAddedDT(component.datatype);
          }

        });
      }
      console.log("DEBUG");
      console.log(datatype);
      if(datatype.valueSetBindings&&datatype.valueSetBindings.length>0){
        angular.forEach(datatype.valueSetBindings,function(binding){
          if(binding.tableId&&!$rootScope.tablesMap[binding.tableId]){
            var temp=[];
            temp.push(binding.tableId);
            $scope.TablesIds=_.union($scope.TablesIds,temp);
            console.log($scope.TablesIds);
          }

        })
      }
    };

    $scope.ok = function() {


      DatatypeLibrarySvc.addChildrenFromDatatypes($rootScope.datatypeLibrary.id, $scope.selectedDatatypes).then(function(result){


        angular.forEach(result, function(dt){
          console.log(dt);
          $scope.processAddedDT(dt);

          console.log(dt.scope);


          // if(dt.scope==="'INTERMASTER'"){
          //
          //     $rootScope.interMediates.push(dt);
          // }else{
          //     $rootScope.datatypes.push(dt);
          // }
          console.log("processing")
          $rootScope.processElement(dt);

          if(!$rootScope.datatypesMap[dt.id]){
            $rootScope.datatypesMap[dt.id]=dt;
            $rootScope.datatypes.push(dt);


          }
          if(dt.parentVersion){
            var objectMap=dt.parentVersion+"VV"+dt.hl7Version;
            $rootScope.usingVersionMap[objectMap]=dt;
          }
          // $rootScope.datatypesMap[dt.id]=dt;

          $rootScope.datatypeLibrary.children.push({name:dt.name,ext:dt.ext,id:dt.id});



        });

        TableLibrarySvc.addChildrenByIds($rootScope.tableLibrary.id, $scope.TablesIds).then(function(result) {
          console.log(result);
          angular.forEach(result, function(table){

            if(!$rootScope.tablesMap[table.id]){
              $rootScope.tables.push(table);
              $rootScope.tablesMap[table.id]=table;
              $rootScope.tableLibrary.children.push({id:table.id, bindingIdentifier:table.bindingIdentifier});

            }



          });
          $mdDialog.hide();


        });

      });



      // $scope.processList();
    };

    $scope.cancel = function () {
      $mdDialog.hide();
    };
  });
