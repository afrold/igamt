/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('addSharedDts',
  function($scope, $rootScope, hl7Version, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http, datatypeLibrary, tableLibrary, versionAndUseMap,$mdDialog, datatypes) {
    $scope.versionAndUseMap = versionAndUseMap;

    $scope.getOwnerName = function(element) {
      if(!element.accountId) {
        return null;
      }
      return $http.get('api/shareparticipant', { params: { id: element.accountId } })
        .then(
          function(response) {
            console.log(response.data)

            element.owner = response.data;

          },
          function(error) {
            console.log(error);
          });
    };



    angular.forEach(datatypes,function(datatype){
      $scope.getOwnerName(datatype);

    });
    $scope.hl7Datatypes=datatypes;

    $scope.addDtFlvForLib = function (datatype) {
      var newDatatype = angular.copy(datatype);
      newDatatype.publicationVersion = 0;
      newDatatype.shareParticipantIds=[];

      newDatatype.ext = $rootScope.createNewExtension(newDatatype.ext);
      newDatatype.scope = $rootScope.datatypeLibrary.scope;
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
    };

    $scope.TablesIds=[];
    $scope.newDts = [];
    $scope.checkedExt = true;
    $scope.DTlinksToAdd=[];
    $scope.miniUsingVersionMap={};
    $scope.NocheckedExt = true;
    $scope.masterLib = [];
    $scope.selectedDatatypes = [];
    // $scope.selectMasterDtLib = function(masLib) {
    //     console.log(masLib);
    //     DatatypeLibrarySvc.getPublishedDatatypesByLibrary(masLib.id,$rootScope.igdocument.profile.metaData.hl7Version).then(function(datatypes) {
    //         console.log(datatypes);
    //         $scope.scope=$rootScope.datatypeLibrary.scope;
    //         $scope.masterDatatypes = datatypes
    //
    //         angular.forEach($scope.masterDatatypes,function (dt) {
    //             dt.hl7Version=$rootScope.igdocument.profile.metaData.hl7Version;
    //         })
    //         console.log($scope.masterDatatypes);
    //     });
    // };
    $scope.containsVersion = function(versions, v) {
      angular.forEach(versions, function(version) {
        if (v === version) {
          return true;
        }
      });
      return false;
    }
    // var listHL7Versions = function() {
    //     return $http.get('api/igdocuments/findVersions', {
    //         timeout: 60000
    //     }).then(function(response) {
    //         var hl7Versions = [];
    //         var length = response.data.length;
    //         for (var i = 0; i < length; i++) {
    //             hl7Versions.push(response.data[i]);
    //         }
    //         console.log(hl7Versions);
    //         return hl7Versions;
    //     });
    // };




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



    $scope.checkImported = function(datatype,version) {

      var objectMap=datatype.id+"VV"+version;

      if($rootScope.usingVersionMap[objectMap]||$scope.miniUsingVersionMap[objectMap]){
        // console.log($rootScope.usingVersionMap[objectMap])
        return true;
      }else{
        return false;
      }

    };


    $scope.addDtFlv = function(datatype) {


      $scope.DatatypeToAdd = angular.copy(datatype);
      // $scope.DatatypeToAdd.publicationVersion=0;
      $scope.DatatypeToAdd.parentVersion=datatype.id;
      $scope.DatatypeToAdd.shareParticipantIds=[];
      var objectMap=datatype.id+"VV"+datatype.hl7Version;

      $scope.miniUsingVersionMap[objectMap]=true;
      $rootScope.usingVersionMap[objectMap]=true;

      $scope.DatatypeToAdd.valueSetBindings=[];
      DatatypeService.getMergedMaster($scope.DatatypeToAdd).then(function(standard) {
        $scope.DatatypeToAdd=standard;
        $scope.DatatypeToAdd.parentVersion=datatype.id;


        $scope.DatatypeToAdd.participants = [];
        $scope.DatatypeToAdd.id = new ObjectId().toString();
        $scope.DatatypeToAdd.libIds = [];
        $scope.selectedDatatypes.push($scope.DatatypeToAdd);
        $scope.DatatypeToAdd.participants = [];
        $scope.DatatypeToAdd.libIds = [];
        if ( $scope.DatatypeToAdd.components != undefined && $scope.DatatypeToAdd.components != null && $scope.DatatypeToAdd.components.length != 0) {
          for (var i = 0; i < $scope.DatatypeToAdd.components.length; i++) {
            $scope.DatatypeToAdd.components[i].id = new ObjectId().toString();
          }
        }

        var predicates = $scope.DatatypeToAdd['predicates'];
        if (predicates != undefined && predicates != null && predicates.length != 0) {
          angular.forEach(predicates, function(predicate) {
            predicate.id = new ObjectId().toString();
          });
        }

        var conformanceStatements = $scope.DatatypeToAdd['conformanceStatements'];
        if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
          angular.forEach(conformanceStatements, function(conformanceStatement) {
            conformanceStatement.id = new ObjectId().toString();
          });
        }
        //  $scope.selectedDatatypes.push($scope.newDatatype);
        console.log($scope.selectedDatatypes);
        console.log($scope.DatatypeToAdd);
        if($scope.DatatypeToAdd.valueSetBindings&&$scope.DatatypeToAdd.valueSetBindings.length!==0) {
          angular.forEach($scope.DatatypeToAdd.valueSetBindings, function (binding) {
            if (binding.tableId && !$rootScope.tablesMap[binding.tableId]) {
              var temp = [];
              temp.push(binding.tableId);
              $scope.TablesIds = _.union($scope.TablesIds, temp);
              console.log($scope.TablesIds);
            }

          })
        }
        console.log("DEBUG");
        console.log($scope.TablesIds);
      });
    }
    $scope.deleteDt = function(datatype) {
      var index = $scope.selectedDatatypes.indexOf(datatype);
      if (index > -1) $scope.selectedDatatypes.splice(index, 1);
    };
    var secretEmptyKey = '[$empty$]'
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
      $scope.DatatypeToAdd = datatype;
    };
    $scope.selected = function() {
      return ($scope.DatatypeToAdd!== undefined);
    };
    $scope.unselect = function() {
      $scope.DatatypeToAdd = undefined;
    };
    $scope.isActive = function(id) {
      if ($scope.DatatypeToAdd) {
        return $scope.DatatypeToAdd.id === id;
      } else {
        return false;
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
        if($rootScope.igdocument){
          TableLibrarySvc.addChildrenByIds(tableLibrary.id, $scope.TablesIds).then(function(result) {
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

        }else{
          $mdDialog.hide();
        }

        // TableLibrarySvc.addChildrenByIds(tableLibrary.id, $scope.TablesIds).then(function(result) {
        //     console.log(result);
        //     angular.forEach(result, function(table){
        //
        //         if(!$rootScope.tablesMap[table.id]){
        //             $rootScope.tables.push(table);
        //             $rootScope.tablesMap[table.id]=table;
        //             $rootScope.tableLibrary.children.push({id:table.id, bindingIdentifier:table.bindingIdentifier});
        //
        //         }
        //
        //
        //
        //     });
        //     $mdDialog.hide();
        //
        //
        // });

      });



      // $scope.processList();
    };


    $scope.processList=function(){


      angular.forEach($scope.selectedDatatypes, function(dt){
        $scope.processAddedDT(dt);



      });

      DatatypeService.saves($scope.selectedDatatypes).then(function(result) {




        for (var i = 0; i < result.length; i++) {
          if(!$rootScope.datatypesMap[result[i].id]){
            $rootScope.datatypesMap[result[i].id]=result[i];
            $rootScope.datatypes.push(result[i]);
          }
        }

        DatatypeLibrarySvc.addChildren(datatypeLibrary.id, $scope.DTlinksToAdd).then(function(link) {
          $rootScope.datatypeLibrary.children.push(link);
          var usedDtId1 = _.map($scope.DTlinksToAdd, function(num, key) {
            return num.id;
          });

          DatatypeService.get(usedDtId1).then(function(datatypes) {
            angular.forEach(datatypes, function(datatype){
              if(!$rootScope.datatypesMap[datatype.id]){
                $rootScope.datatypesMap[datatype.id]=datatype;
                $rootScope.datatypes.push(datatype);
                if(datatype.parentVersion){
                  console.log("DEBUUUG");

                  var objectMap=datatype.parentVersion+"VV"+datatype.hl7Version;
                  $rootScope.usingVersionMap[objectMap]=datatype;
                  console.log($rootScope.usingVersionMap[objectMap]);


                }


              }
            })
            TableLibrarySvc.addChildrenByIds(tableLibrary.id, $scope.TablesIds).then(function(result) {
              console.log(result);
              angular.forEach(result, function(table){

                if(!$rootScope.tablesMap[table.id]){
                  $rootScope.tables.push(table);
                  $rootScope.tablesMap[table.id]=table;

                }



              });
              $mdDialog.hide();

            });
          });
        });
      });
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
    }


    $scope.processDatatype= function (datatype) {
      if(datatype.scope=="HL7STANDARD"){
        $scope.processStandardDT(datatype);
      }else if(datatype.scope=="MASTER"){
        $scope.processMasterDT(datatype);

      }
    }



    $scope.cancel = function() {
      $mdDialog.hide();
    };
  });

