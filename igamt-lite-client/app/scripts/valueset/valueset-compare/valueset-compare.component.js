/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('cmpTableCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService, TableService) {
  var ctrl = this;
  this.tableId = -1;
  $scope.vsChanged = false;
  $scope.variable = false;
  $scope.isDeltaCalled = false;

  $scope.showResult=false;
  $scope.from="Current";
  $scope.searchTerm={bindingIdentifier:''};

  $scope.scope=$rootScope.table.scope;
    $scope.compareCodeResult=[];
  $scope.version=$rootScope.table.hl7Version;
  $scope.bindingIdentifier=$rootScope.table.bindingIdentifier;
  $scope.tablesToCompare=_.without($rootScope.tables, $rootScope.table);
  $scope.selectedTable=null;
  $scope.setDeltaToF = function() {
    $scope.isDeltaCalled = false;
  };
  $scope.scopes = [{
    name: "USER",
    alias: "USER"
  }, {
    name: "HL7STANDARD",
    alias: "Base HL7"
  },
      {
          name: "PHINVADS",
          alias: "PHINVADS"
      }
  ];
  var listHL7Versions = function() {
    return $http.get('api/igdocuments/findVersions', {
      timeout: 60000
    }).then(function(response) {
      var hl7Versions = [];
      var length = response.data.length;
      for (var i = 0; i < length; i++) {
        hl7Versions.push(response.data[i]);
      }
      return hl7Versions;
    });
  };

  $scope.status = {
    isCustomHeaderOpen: false,
    isFirstOpen: true,
    isSecondOpen: true,
    isFirstDisabled: false
  };

  $scope.initt = function() {
      $scope.vsChanged = false;
      $scope.variable = false;
      $scope.isDeltaCalled = false;

      $scope.showResult=false;
      $scope.from="Current";
      $scope.filter={bindingIdentifier:''};

      $scope.compareCodeResult=[];
      $scope.bindingIdentifier=$rootScope.table.bindingIdentifier;
      $scope.tablesToCompare =_.filter($rootScope.tables, function(table){
          return $rootScope.table.id!=table.id;
      });
      $scope.selectedTable=null;
    listHL7Versions().then(function(versions) {
      $scope.versions = versions;
    });

  };

  $scope.$on('event:loginConfirmed', function(event) {
    $scope.initt();
  });
  $rootScope.$on('event:initTable', function(event) {
       $scope.initt();

      // if ($scope.isDeltaCalled) {
    // }
  });

  $rootScope.$on('event:openVSDelta', function(event) {
    $scope.initt();
  });

  $scope.setVersion2 = function(vr) {

    $scope.version2 = vr;



  };
  $scope.$watch('scope',function (newValue,oldValue) {

     if($scope.scope=="PHINVADS") {
         $scope.version = "NV";
     }

  });

  $scope.$watch('from',function (newValue,oldValue) {
        if($scope.from=="Current"){
            $scope.scope='';
            $scope.version='';
            $scope.bindingIdentifier='';
            $scope.selectedTable=null;
            $scope.tablesToCompare =_.filter($rootScope.tables, function(table){
                return $rootScope.table.id!=table.id;
            });
        }else{
            $scope.clearSearch();
        }
  });

  $scope.clearSearch=function () {
      $scope.scope='';
      $scope.version='';
      $scope.bindingIdentifier='';
      $scope.selectedTable=null;
      $scope.tablesToCompare=[];
  };

  $scope.hideVS = function(vs1, vs2) {
    if (vs2) {
      return !(vs1.name === vs2.name);
    } else {
      return false;
    }
  };
  $scope.disableVS = function(vs1, vs2) {

    if (vs2) {
      return (vs1.id === vs2.id);
    } else {
      return false;
    }
  };

  // $scope.dynamicVs_params = new ngTreetableParams({
  //   getNodes: function(parent) {
  //     if ($scope.dataList !== undefined) {
  //       if (parent) {
  //         if (parent.codes) {
  //           return parent.codes;
  //         }
  //       } else {
  //         return $scope.dataList;
  //       }
  //     }
  //   },
  //   getTemplate: function(node) {
  //     $scope.vsTemplate = true;
  //     return 'valueSet_node';
  //   }
  // });
  $scope.cmpTable = function(table1, table2) {

    $scope.loadingSelection = true;
    $scope.vsChanged = false;
    $scope.vsTemplate = false;
    TableService.getOne(table2.id).then(function(vs2) {
      $scope.selectedTable=vs2;

      if(table1.sourceType!=="EXTERNAL"&&$scope.selectedTable.sourceType!=='EXTERNAL'){
          $scope.compareCodes(table1.codes,$scope.selectedTable.codes);
          $scope.loadingSelection = false;
          $scope.showDelta = true;
          $scope.status.isSecondOpen = true;
          $scope.deltaTabStatus.active = 1;
      }else{
          $scope.showResult=true;
          $scope.loadingSelection = false;
          $scope.showDelta = true;
          $scope.status.isSecondOpen = true;
          $scope.deltaTabStatus.active = 1;
      }


    });

  };

    $scope.sanitize=function (attr) {
        if(attr=='Undefined'){
            return "Not Defined";
        }else if(!attr){
            return "Not Applicable";
        }else{
            return attr;
        }
    };
    $scope.compareCodes=function (list1, list2) {
      var startList=[];
      $scope.compareCodeResult=[];

        startList=_.union(list1, list2);
        console.log(startList);
        var allValues=_.map(startList, function(code){
          if(code.value) return {value:code.value, codeSystem:code.codeSystem};
        });

        console.log(allValues);
        angular.forEach(allValues,function (v) {

          var value1=$scope.getCodeByValue(v.value,v.codeSystem,list1);

          var value2=$scope.getCodeByValue(v.value,v.codeSystem,list2);
          console.log(value1);
          console.log(value2);

          // if($scope.isDifferent(value1,value2)){
              $scope.compareCodeResult.push({c1:value1,c2:value2})
          // }
        });
        $scope.showResult=true;

    };

    $scope.getCodeByValue=function(value,codeSystem,codes){

      var mapped =_.filter(codes, function(code){
        return code.value&&code.value == value&&code.codeSystem&&code.codeSystem==codeSystem;
      });

      if(mapped.length>0){
        return mapped[0]; //it should be 1 element.

      }else{
        return {
            value:value,
            label:'-',
            codeSystem:'-',
            codeUsage:'-',
            absent:true

        }
      }
    };

    $scope.isDifferent=function(c1,c2){
      if(c1.value!==c2.value){
        return true;
      }else if(c1.label!==c2.label){
        return true;
      }else if (c1.codeUsage!==c2.codeUsage){
        return true;
      }else{
        return false;
      }
    };

    $scope.selectTableForDelta=function (row) {
        if($scope.selectedTable&&$scope.selectedTable.id==row.id){
            $scope.selectedTable=null;
        }
        else{
            $scope.selectedTable=row;

        }
        $scope.showResult=false;
    };
    $scope.isSelectedForDelta=function (row) {
        return $scope.selectedTable&&row.id===$scope.selectedTable.id;
    };

    $scope.searchForDelta=function () {
        $scope.loading=true;
        $scope.selectedTable=null;


        TableService.searchForDelta($scope.scope,$scope.version,$scope.bindingIdentifier).then(function (result) {
          console.log($scope.scope);

          $scope.tablesToCompare=result;
          $scope.loading=false;
        });
    };


});
