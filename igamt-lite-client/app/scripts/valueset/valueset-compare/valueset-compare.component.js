/**
 * Created by haffo on 9/12/17.
 */

angular.module('igl').controller('cmpTableCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService, TableService) {
  var ctrl = this;
  this.tableId = -1;
  $scope.vsChanged = false;
  $scope.variable = false;
  $scope.isDeltaCalled = false;

  $scope.setDeltaToF = function() {
    console.log("HEEEEEERREEEEE");
    $scope.isDeltaCalled = false;
  }



  $scope.scopes = [{
    name: "USER",
    alias: "My IG"
  }, {
    name: "HL7STANDARD",
    alias: "Base HL7"
  }];
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
      //$scope.setIG2($scope.ig2);
      $scope.version2 = angular.copy($scope.version1);
      //$scope.status.isFirstOpen = true;
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

  //$scope.initt();

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
  $scope.setScope2 = function(scope) {

    $scope.scope2 = scope;
  };

  $scope.$watchGroup(['table1', 'table2'], function() {
    $scope.vsChanged = true;
    //$scope.segment1 = angular.copy($rootScope.activeSegment);


  }, true);
  $scope.$watchGroup(['version2', 'scope2'], function() {
    $scope.igList2 = [];
    $scope.tables2 = [];
    $scope.ig2 = "";
    if ($scope.scope2 && $scope.version2) {
      IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
        if (result) {
          if ($scope.scope2 === "HL7STANDARD") {
            console.log("====");
            $scope.ig2 = {
              id: result[0].id,
              title: result[0].metaData.title
            };
            console.log($scope.ig2);

            $scope.igList2.push($scope.ig2);

            $scope.setIG2($scope.ig2);
            $scope.igDisabled2 = true;
          } else {
            for (var i = 0; i < result.length; i++) {
              $scope.igList2.push({
                id: result[i].id,
                title: result[i].metaData.title,
              });
            }
            $scope.igDisabled2 = false;


          }
        }
      });

    }

  }, true);
  $scope.setTable2 = function(table) {
    if (table === -1) {
      $scope.table2 = {};
    } else {
      $scope.table2 = $scope.tables2[table];

    }
  };
  $scope.setIG2 = function(ig) {
    if (ig) {
      IgDocumentService.getOne(ig.id).then(function(igDoc) {
        SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
          DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
            TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
              $scope.tables2 = [];
              this.tableId = -1;

              if (igDoc) {
                //$scope.segList2 = angular.copy(segments);
                //$scope.segList2 = orderByFilter($scope.segList2, 'name');
                //$scope.dtList2 = angular.copy(datatypes);
                $scope.tableList2 = angular.copy(tables);
                //$scope.messages2 = orderByFilter(igDoc.profile.messages.children, 'name');
                //$scope.segments2 = orderByFilter(segments, 'name');
                //$scope.datatypes2 = orderByFilter(datatypes, 'name');
                $scope.tables2 = orderByFilter(tables, 'bindingIdentifier');
              }
            });
          });
        });

      });

      //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

    }

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
      if ($scope.dynamicVs_params) {
        console.log($scope.dataList);
        $scope.showDelta = true;
        $scope.status.isSecondOpen = true;
        $scope.dynamicVs_params.refresh();
      }
      $scope.deltaTabStatus.active = 1;
    });


  };


});
