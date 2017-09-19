/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('cmpMessageCtrl', function($scope, $modal, ObjectDiff, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc, CompareService) {

  $scope.msgChanged = false;
  var ctrl = this;
  this.messageId = -1;

  $scope.isDeltaCalled = false;
  $scope.setDeltaToF = function() {
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

  var init = function() {
    $scope.isDeltaCalled = true;
    $rootScope.deltaMsgList = [];
    ctrl.messageId = -1;
    $scope.message1 = angular.copy($rootScope.message);
    $scope.version1 = angular.copy($rootScope.igdocument.profile.metaData.hl7Version);

    $scope.scope1 = "USER";
    $scope.ig1 = angular.copy($rootScope.igdocument.metaData.title);
    $scope.segList1 = angular.copy($rootScope.segments);
    $scope.dtList1 = angular.copy($rootScope.datatypes);
    $scope.version2 = angular.copy($scope.version1);
    console.log($scope.scopes);
    console.log($scope.scopes[1]);
    $scope.scope2 = "HL7STANDARD";
    listHL7Versions().then(function(versions) {

      $scope.versions = versions;

      if ($scope.dynamicMsg_params) {
        $scope.showDelta = true;
        $scope.status.isFirstOpen = true;
        $scope.dynamicMsg_params.refresh();
      }

    });
  };

  $scope.$on('event:loginConfirmed', function(event) {
    init();
  });
  $rootScope.$on('event:initMessage', function(event) {
    $scope.findAllGlobalConstraints();
    if ($scope.isDeltaCalled) {
      init();
    }
  });
  $rootScope.$on('event:openMsgDelta', function(event) {
    init();
  });

  //init();


  $scope.status = {
    isCustomHeaderOpen: false,
    isFirstOpen: true,
    isSecondOpen: true,
    isFirstDisabled: false
  };


  $scope.setVersion2 = function(vr) {
    $scope.version2 = vr;

  };
  $scope.setScope2 = function(scope) {

    $scope.scope2 = scope;
  };

  $scope.$watchGroup(['message1', 'message2'], function() {
    $scope.msgChanged = true;


  }, true);
  $scope.$watchGroup(['version2', 'scope2'], function() {
    $scope.igList2 = [];
    $scope.messages2 = [];
    $scope.ig2 = "";
    console.log("==============");
    if ($scope.scope2 && $scope.version2) {
      IgDocumentService.getIgDocumentsByScopesAndVersion([$scope.scope2], $scope.version2).then(function(result) {
        if (result) {
          if ($scope.scope2 === "HL7STANDARD") {
            $scope.igDisabled2 = true;
            $scope.ig2 = {
              id: result[0].id,
              title: result[0].metaData.title
            };
            console.log($scope.ig2);
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

  $scope.setMsg2 = function(msg) {

    if (msg === -1) {
      $scope.message2 = {};
    } else {
      $scope.message2 = $scope.messages2[msg];
      console.log($scope.message2);

    }
    //$scope.segment2 = segment;
  };
  $scope.setIG2 = function(ig) {
    if (ig) {
      IgDocumentService.getOne(ig.id).then(function(igDoc) {
        SegmentLibrarySvc.getSegmentsByLibrary(igDoc.profile.segmentLibrary.id).then(function(segments) {
          DatatypeLibrarySvc.getDatatypesByLibrary(igDoc.profile.datatypeLibrary.id).then(function(datatypes) {
            TableLibrarySvc.getTablesByLibrary(igDoc.profile.tableLibrary.id).then(function(tables) {
              $scope.messages2 = [];
              $scope.msg2 = "";
              if (igDoc) {
                $scope.segList2 = angular.copy(segments);
                //$scope.segList2 = orderByFilter($scope.segList2, 'name');
                $scope.dtList2 = angular.copy(datatypes);
                $scope.tableList2 = angular.copy(tables);
                $scope.messages2 = orderByFilter(igDoc.profile.messages.children, 'name');
                $scope.segments2 = orderByFilter(segments, 'name');
                $scope.datatypes2 = orderByFilter(datatypes, 'name');
                $scope.tables2 = orderByFilter(tables, 'bindingIdentifier');
              }
            });
          });
        });

      });

      //$scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages.children;

    }

  };

  $scope.hideMsg = function(msg1, msg2) {

    if (msg2) {
      return !(msg1.structID === msg2.structID);
    } else {
      return false;
    }
  };
  $scope.disableMsg = function(msg1, msg2) {

    if (msg2) {
      return (msg1.id === msg2.id);
    } else {
      return false;
    }
  };




  $scope.dynamicMsg_params = new ngTreetableParams({
    getNodes: function(parent) {
      if ($rootScope.deltaMsgList !== undefined) {

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
          return $rootScope.deltaMsgList;
        }

      }
    },
    getTemplate: function(node) {
      return 'tree_node';
    }
  });

  $scope.cmpMessage = function(msg1, msg2) {
    $rootScope.deltaMap = {};
    $scope.loadingSelection = true;
    $scope.msgChanged = false;
    $scope.vsTemplate = false;
    $scope.loadingSelection = false;
    $rootScope.deltaMsgList = CompareService.cmpMessage(msg1, msg2, $scope.dtList1, $scope.dtList2, $scope.segList1, $scope.segList2);
    //$scope.dataList = result;
    console.log($rootScope.deltaMsgList);

    if ($scope.dynamicMsg_params) {
      console.log($rootScope.deltaMsgList);
      $scope.showDelta = true;
      $scope.status.isSecondOpen = true;
      $scope.dynamicMsg_params.refresh();
    }
    $scope.deltaTabStatus.active = 1;
  };
});
