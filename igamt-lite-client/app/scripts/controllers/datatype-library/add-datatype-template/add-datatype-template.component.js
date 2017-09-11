/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddDatatypeTemplate',
  function($scope, $rootScope, $mdDialog,datatypes, DatatypeLibrarySvc, DatatypeService, TableLibrarySvc, TableService, $http,datatypeLibrary,tableLibrary,AllUnchanged) {

    $scope.AllUnchanged=AllUnchanged;

    $scope.addedDatatypes=[];




    var init = function() {
      DatatypeService.findByScope("INTERMASTER").then(function(response){
        console.log("ALL intermediate");
        $scope.hl7Datatypes=response;

      });


    };
    init();




    $scope.getDatatypeFromUnchanged= function(data1){
      var data= angular.copy(data1);
      var versions= data.versions;
      var version=0;
      console.log("versions ===== data ");
      console.log(versions);
      if(versions.length&&versions.length>0){
        version=versions[versions.length-1];
      }
      var name= data.name;


    };





    $scope.getLastExtesion= function(masterDt){
      var ext=1;
      if(masterDt.hl7versions){
        var version=masterDt.hl7versions[masterDt.hl7versions.length-1];
      }
      DatatypeService.getOneStandard(masterDt.name,version,masterDt.hl7versions).then(function(result) {

        $scope.lastExt=result.ext;
        console.log($scope.lastExt);
      });

    }
    $scope.setVersion=function(v){
      $scope.version1=v;
    }
    $scope.containsCurrentVersion=function(data){
      return data.versions.indexOf($scope.version1) !== -1;
    }
    $scope.AddDatatype = function(datatype) {
      var dataToAdd = angular.copy(datatype);
      dataToAdd.scope=$rootScope.datatypeLibrary.scope;
      // dataToAdd.hl7version=null;
      dataToAdd.id = new ObjectId().toString();
      dataToAdd.status = 'UNPUBLISHED';
      dataToAdd.scope = $rootScope.datatypeLibrary.scope;
      $scope.addedDatatypes.push(dataToAdd);


    };

    $scope.existingExtension=function(d,addedDatatypes){
      var version1= d.hl7versions.toString();
      console.log(addedDatatypes);
      $scope.exist=false;
      angular.forEach($scope.addedDatatypes,function(dt){
        var version2= dt.hl7versions.toString();

        console.log(dt.hl7versions);
        console.log(d.hl7versions);

        if(dt.id!==d.id && d.name===dt.name && dt.ext===d.ext && version1==version2){

          console.log("+++++++ found")
          $scope.exist=true;
        }
      });
      return $scope.exist;
    };


    $scope.cancel = function() {
      $mdDialog.hide('cancel');
    };

    $scope.ok = function() {
      $mdDialog.hide($scope.addedDatatypes);
    };
  });
