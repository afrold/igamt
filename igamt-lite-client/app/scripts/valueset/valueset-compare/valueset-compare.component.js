/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('cmpTableCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService, TableService) {
  var ctrl = this;
  this.tableId = -1;
  $scope.vsChanged = false;
  $scope.variable = false;
  $scope.isDeltaCalled = false;
  $scope.from="Current";
  $scope.searchTerm={bindingIdentifier:''};

  $scope.scope=$rootScope.table.scope;
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
    $scope.isDeltaCalled = true;
    $scope.dataList = [];
    listHL7Versions().then(function(versions) {
      $scope.versions = versions;
      $scope.version1 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);
      $scope.scope1 = "USER";
      $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);
      $scope.table1 = angular.copy($rootScope.table);
      this.tableId = -1;
      $scope.variable = !$scope.variable;
      $scope.tables = null;
      $scope.version2 = angular.copy($scope.version1);
      $scope.scope2 = "HL7STANDARD";
      if ($scope.dynamicVs_params) {
        $scope.showDelta = false;
        $scope.status.isFirstOpen = true;
        $scope.dynamicVs_params.refresh();
      }
    });

  };

  $scope.$on('event:loginConfirmed', function(event) {
    $scope.initt();
  });
  $rootScope.$on('event:initTable', function(event) {
    if ($scope.isDeltaCalled) {
      $scope.initt();
    }
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
            $scope.tablesToCompare=$rootScope.tables;
            $scope.scope='';
            $scope.version='';
            $scope.bindingIdentifier='';
            $scope.selectedTable=null;
            _.without($rootScope.tables, $rootScope.table);
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

  $scope.dynamicVs_params = new ngTreetableParams({
    getNodes: function(parent) {
      if ($scope.dataList !== undefined) {
        if (parent) {
          if (parent.codes) {
            return parent.codes;
          }
        } else {
          return $scope.dataList;
        }
      }
    },
    getTemplate: function(node) {
      $scope.vsTemplate = true;
      return 'valueSet_node';
    }
  });
  $scope.cmpTable = function(table1, table2) {

    $scope.loadingSelection = true;
    $scope.vsChanged = false;
    $scope.vsTemplate = false;
    TableService.getOne(table2.id).then(function(vs2) {

      $scope.dataList = CompareService.cmpValueSet(table1, vs2);
      $scope.loadingSelection = false;
      if($scope.dataList){
          if ($scope.dynamicVs_params) {
              $scope.showDelta = true;
              $scope.status.isSecondOpen = true;
              $scope.dynamicVs_params.refresh();
              $scope.deltaTabStatus.active = 1;

          }
      }else{

       // $scope.comparDifferentType(table1, vs2);
      }


    });

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
