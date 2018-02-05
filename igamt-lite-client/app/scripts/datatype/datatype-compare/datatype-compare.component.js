/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('cmpDatatypeCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService) {
  var ctrl = this;
  this.datatypeId = -1;
  $scope.dtChanged = false;
  $scope.isDeltaCalled = false;
  $scope.setDeltaToF = function() {
    $scope.isDeltaCalled = false;
  }

  $scope.dataList={};


  $scope.scopes = [{
    name: "USER",
    alias: "My IG"
  }, {
    name: "HL7STANDARD",
    alias: "Base HL7"
  }];

  $scope.getLabel = function(element) {
    if (element) {
      if (element.ext !== null && element.ext !== "") {
        return element.name + "_" + element.ext;
      } else {
        return element.name;
      }
    }

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
      if ($rootScope.igdocument && $rootScope.igdocument != null) {
        $scope.datatype1 = angular.copy($rootScope.datatype);

        $scope.version1 = angular.copy($scope.datatype1.hl7Version);
        $scope.scope1 = "USER";
        $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);
        ctrl.datatypeId = -1;
        $scope.variable = !$scope.variable;


        $scope.segments2 = null;
        //$scope.setIG2($scope.ig2);
        $scope.segList1 = angular.copy($rootScope.segments);
        $scope.dtList1 = angular.copy($rootScope.datatypes);
        $scope.version2 = angular.copy($scope.version1);
      }
      //$scope.status.isFirstOpen = true;
      $scope.scope2 = "HL7STANDARD";
      if ($scope.dynamicDt_params) {
        $scope.showDelta = false;
        $scope.status.isFirstOpen = true;
        $scope.dynamicDt_params.refresh();
      }

    });



  };

  $scope.$on('event:loginConfirmed', function(event) {
    $scope.initt();
  });

  //$scope.initt();

  $rootScope.$on('event:initDatatype', function(event) {
    $scope.getAllVersionsOfDT($rootScope.datatype.parentVersion);
    if ($scope.isDeltaCalled) {
      $scope.initt();
    }
  });
  $rootScope.$on('event:openDTDelta', function(event) {
    $scope.initt();
  });



  $scope.setVersion2 = function(vr) {
    $scope.version2 = vr;

  };
  $scope.setScope2 = function(scope) {

    $scope.scope2 = scope;
  };

  $scope.$watchGroup(['datatype1', 'datatype2'], function() {
    $scope.dtChanged = true;
    //$scope.segment1 = angular.copy($rootScope.activeSegment);
  }, true);
  $scope.$watchGroup(['version2', 'scope2', 'variable'], function() {
    $scope.igList2 = [];
    $scope.segments2 = [];
    $scope.ig2 = "";
    if ($scope.scope2 && $scope.version2) {
      IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
        if (result) {
          if ($scope.scope2 === "HL7STANDARD") {
            $scope.igDisabled2 = true;
            $scope.ig2 = {
              id: result[0].id,
              title: result[0].metaData.title
            };
            $scope.igList2.push($scope.ig2);

            $scope.setIG2($scope.ig2);
          } else {
            $scope.igDisabled2 = false;
            for (var i = 0; i < result.length; i++) {
              $scope.igList2.push({
                id: result[i].id,
                title: result[i].metaData.title,
              });
            }
          }
        }
      });

    }

  }, true);
  $scope.setDatatype2 = function(datatype) {
    if (datatype === -1) {
      $scope.datatype2 = {};
    } else {
      $scope.datatype2 = $scope.datatypes2[datatype];

    }
  };
  $scope.setIG2 = function(ig) {
    if (ig) {
      IgDocumentService.getOne(ig.id).then(function(igDoc) {
        SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
          DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
            TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
              $scope.segments2 = [];
              $scope.segment2 = "";
              if (igDoc) {
                $scope.segList2 = angular.copy(segments);
                $scope.dtList2 = angular.copy(datatypes);
                $scope.tableList2 = angular.copy(tables);

                $scope.datatypes2 = orderByFilter(datatypes, 'name');
              }
            });
          });
        });

      });


    }

  };

  $scope.hideDT = function(dt1, dt2) {

    if (dt2) {
      return !(dt1.name === dt2.name);
    } else {
      return false;
    }
  };
  $scope.disableDT = function(dt1, dt2) {

    if (dt2) {
      return (dt1.id === dt2.id);
    } else {
      return false;
    }
  };




  $scope.dynamicDt_params = new ngTreetableParams({
    getNodes: function(parent) {
      if(parent){
          return parent.children;
      }
      else{
        return $scope.dataList.children;
      }




    },
    getTemplate: function(node) {
      return "deltaElement.html";
    }
  });
  $scope.cmpDatatype = function(datatype1, datatype2) {

    $scope.loadingSelection = true;
    $scope.dtChanged = false;
    $scope.vsTemplate = false;

  CompareService.cmpDatatype(datatype1, datatype2).then(function (result) {
      $scope.dataList =      result;
      $scope.loadingSelection = false;
      if ($scope.dynamicDt_params) {
          $scope.showDelta = true;
          $scope.status.isSecondOpen = true;
          $scope.dynamicDt_params.refresh();
      }
      console.log($scope.dynamicDt_params);
      $scope.deltaTabStatus.active = 1;

  });

  };
});
