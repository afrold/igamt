/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('cmpSegmentCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService) {
  $scope.segChanged = false;
  $scope.isDeltaCalled = false;
  var ctrl = this;
  this.segmentId = -1;

  $scope.setDeltaToF = function() {
    console.log("setDeltaToF called");
    console.log($rootScope.segment);

    $scope.isDeltaCalled = false;
  }


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
    return "";
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
    isSecondOpen: false,
    isFirstDisabled: false
  };
  $scope.variable = false;

  $scope.initt = function() {
    $scope.isDeltaCalled = true;
    $scope.dataList = [];
    listHL7Versions().then(function(versions) {
      $scope.versions = versions;
      $scope.segment1 = angular.copy($rootScope.segment);
      $scope.version1 = angular.copy($scope.segment1.hl7Version);
      ctrl.segmentId = -1;
      //$scope.setIG2($scope.ig2);
      $scope.variable = !$scope.variable;
      $scope.segList1 = angular.copy($rootScope.segments);
      $scope.dtList1 = angular.copy($rootScope.datatypes);
      $scope.version2 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);
      //$scope.status.isFirstOpen = true;
      $scope.scope2 = "HL7STANDARD";
      if ($scope.dynamicSeg_params) {
        $scope.showDelta = true;
        $scope.status.isFirstOpen = true;
        $scope.dynamicSeg_params.refresh();
      }

    });


  };

  $scope.$on('event:loginConfirmed', function(event) {
    $scope.initt();
  });

  //$scope.initt();

  $rootScope.$on('event:initSegment', function(event) {
    if ($scope.isDeltaCalled) {
      $scope.initt();
    }
    // $scope.initt();
  });

  $rootScope.$on('event:openSegDelta', function(event) {
    $scope.initt();
  });


  // $rootScope.$on('event:saveSegForDelta', function(event) {
  //     $scope.dataList = [];
  //     $scope.initt();
  // });

  $scope.scope1 = "USER";
  $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);

  $scope.setVersion2 = function(vr) {
    $scope.version2 = vr;

  };
  $scope.setScope2 = function(scope) {

    $scope.scope2 = scope;
  };

  $scope.$watchGroup(['segment1', 'segment2'], function() {
    $scope.segChanged = true;
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
  $scope.setSegment2 = function(segment) {

    if (segment === -1) {
      $scope.segment2 = {};
    } else {
      $scope.segment2 = $scope.segments2[segment];
    }
    //$scope.segment2 = segment;
  };
  $scope.setIG2 = function(ig) {
    if (ig) {
      IgDocumentService.getOne(ig.id).then(function(igDoc) {
        SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
          DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
            TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
              $scope.segments2 = [];
              if (igDoc) {
                $scope.segList2 = angular.copy(segments);
                //$scope.segList2 = orderByFilter($scope.segList2, 'name');
                $scope.dtList2 = angular.copy(datatypes);
                $scope.tableList2 = angular.copy(tables);
                //$scope.messages2 = orderByFilter(igDoc.profile.messages.children, 'name');
                $scope.segments2 = orderByFilter(segments, 'name');

              }
            });
          });
        });

      });

      //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

    }

  };

  $scope.hideSeg = function(seg1, seg2) {

    if (seg2) {
      return !(seg1.name === seg2.name);
    } else {
      return false;
    }
  };
  $scope.disableSeg = function(seg1, seg2) {

    if (seg2) {
      return (seg1.id === seg2.id);
    } else {
      return false;
    }
  };




  $scope.dynamicSeg_params = new ngTreetableParams({
    getNodes: function(parent) {
      if ($scope.dataList !== undefined) {

        //return parent ? parent.fields : $scope.test;
        if (parent) {
          if (parent.fields) {
            return parent.fields;
          } else if (parent.components) {
            return parent.components;
          } else if (parent.segments) {
            return parent.segments;
          } else if (parent.codes) {
            return parent.codes;
          }

        } else {
          return $scope.dataList;
        }

      }
    },
    getTemplate: function(node) {
      return 'tree_node';
    }
  });
  $scope.cmpSegment = function(segment1, segment2) {

    $scope.loadingSelection = true;
    $scope.segChanged = false;
    $scope.vsTemplate = false;
    $scope.dataList = CompareService.cmpSegment(segment1, segment2, $scope.dtList1, $scope.dtList2, $scope.segList1, $scope.segList2);

    $scope.loadingSelection = false;
    if ($scope.dynamicSeg_params) {
      $scope.showDelta = true;
      $scope.status.isSecondOpen = true;
      $scope.dynamicSeg_params.refresh();
    }
    $scope.deltaTabStatus.active = 1;

  };


});
