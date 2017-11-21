/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller(
  'HL7VersionsInstanceDlgCtrl',
  function($scope, $rootScope, $mdDialog, $http, hl7Versions, ProfileAccessSvc, MessageEventsSvc, SegmentService, DatatypeService, TableService, TableLibrarySvc, SegmentLibrarySvc, DatatypeLibrarySvc, IgDocumentService, $timeout, ngTreetableParams, userInfoService, hl7Version, MessagesSvc) {

    $scope.hl7Versions = hl7Versions;
    $scope.hl7Version = hl7Version;
    $scope.messageEventsParams=null;
    // $scope.hl7VersionsDlgForm={
    // }
    // $scope.hl7VersionsDlgForm.metaData={};
    $scope.selectedHL7Version = hl7Version;
    $scope.okDisabled = true;
    $scope.messageIds = [];
    $scope.messageEvents = [];
    $scope.loading = false;
    var messageEvents = [];
    $scope.messageEventsParams = null;
    $scope.scrollbarWidth = $rootScope.getScrollbarWidth();
    $scope.status = {
      isCustomHeaderOpen: false,
      isFirstOpen: true,
      isSecondOpen: true,
      isFirstDisabled: false
    };
    $scope.tabs = [{active: true}, {active: false}];
    $scope.make_active = function(x) {

      for(i=0; i<$scope.tabs.length;i++){
        if(i==x){
          $scope.tabs[i].active = true;
        }else{
          $scope.tabs[i].active=false;
        }
      }

    };

    //
    // $scope.getMessageEvents=function(){
    //    var messageParm = new ngTreetableParams({
    //         getNodes: function(parent) {
    //             return parent && parent != null ? parent.children : $scope.hl7Version != null ? MessageEventsSvc.getMessageEvents($scope.hl7Version) : [];
    //         },
    //         getTemplate: function(node) {
    //             return 'MessageEventsNode.html';
    //         }
    //     });
    //     return messageParm;
    // }



    $scope.loadIGDocumentsByVersion = function(hl7Version) {
      console.log($scope.messageEventsParams);
      $scope.loading = true;
      $scope.eventList = [];
      $scope.selectedHL7Version = hl7Version;
      $scope.hl7Version = hl7Version;

      messageEvents = [];
      if($scope.messageEventsParams){
        $timeout(function() {
          if ($scope.messageEventsParams)
            $scope.messageEventsParams.refresh();
          $scope.loading = false;
        });
      }else {
        console.log("creating"+$scope.hl7Version);

        $scope.messageEventsParams= new ngTreetableParams({
          getNodes: function (parent) {
            return parent && parent != null ? parent.children : $scope.hl7Version != null ? MessageEventsSvc.getMessageEvents($scope.hl7Version) : [];
          },
          getTemplate: function (node) {
            return 'MessageEventsNode.html';
          }
        });
        $scope.loading = false;
      }

    };


    $scope.isBranch = function(node) {
      var rval = false;
      if (node.type === "message") {
        rval = true;
        MessageEventsSvc.putState(node);
      }
      return rval;
    };

    $scope.eventList = [];

    $scope.trackSelections = function(bool, event) {
      // console.log("event");
      // console.log(event);
      console.log(bool);



      if (bool) {
        $scope.eventList.push(event);
        messageEvents.push({
          "id": event.id,
          "children": [{
            "name": event.name.trim(),
            "parentStructId": event.parentStructId
          }]
        });
      } else {
        console.log(messageEvents);
        for (var i = 0; i < messageEvents.length; i++) {

          if (messageEvents[i].children[0].name == event.name.trim() && messageEvents[i].children[0].parentStructId == event.parentStructId) {
            messageEvents.splice(i, 1);
          }
        }
        for (var i = 0; i < $scope.eventList.length; i++) {
          if ($scope.eventList[i].name == event.name && $scope.eventList[i].parentStructId == event.parentStructId) {
            $scope.eventList.splice(i, 1);
          }
        }
      }
      $scope.okDisabled = messageEvents.length === 0;
    };
    $scope.isChecked = function(node) {
      if ($scope.eventList.indexOf(node) !== -1) {
        return true;
      } else {
        return false;
      }
    };


    //        $scope.$watch(function () {
    //            return $rootScope.igdocument.id;
    //        }, function (newValue, oldValue) {
    //            if ($rootScope.clickSource === "ctx") {
    //                $scope.hl7Version = $rootScope.hl7Version;
    //                $scope.messageIds = ProfileAccessSvc.Messages().getMessageIds();
    //                $scope.loadIGDocumentsByVersion();
    //            }
    //        });

    $scope.ok = function() {
      // create new ig doc submitted.
      $scope.messageEvents = messageEvents;
      switch ($rootScope.clickSource) {
        case "btn":
        {
          createIGDocument($scope.hl7Version, messageEvents);


          break;
        }
        case "ctx":
        {
          updateIGDocument(messageEvents);
          break;
        }
      }
    };

    var createIGDocument = function(hl7Version, msgEvts) {
      console.log("create Ig called");
      console.log($scope.hl7VersionsDlgForm);
      var iprw = {
        "hl7Version": hl7Version,
        "msgEvts": msgEvts,
        "metaData": $scope.hl7VersionsDlgForm.metaData,
        "accountID": userInfoService.getAccountID(),
        "timeout": 60000
      };
      $scope.okDisabled = true;
      $http.post('api/igdocuments/createIntegrationProfile', iprw)
        .then(
          function(response) {
            var igdocument = angular
              .fromJson(response.data);
            $mdDialog.hide(igdocument);
          },
          function(response) {
            $rootScope.msg().text = response.data;
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.okDisabled = false;
          });
    };

    /**
     * TODO: Handle error from server
     *
     * @param msgIds
     */
    var updateIGDocument = function(msgEvts) {
      $rootScope.usedSegsLink = [];
      $rootScope.usedDtLink = [];
      $rootScope.usedVsLink = [];


      var events = [];

      var scope = "HL7STANDARD";
      var version = $scope.hl7Version;
      console.log($rootScope.igdocument);


      console.log("update Ig called");
      console.log(msgEvts);
      for (var i = 0; i < msgEvts.length; i++) {
        events.push({
          name: msgEvts[i].children[0].name,
          parentStructId: msgEvts[i].children[0].parentStructId,
          scope: "HL7STANDARD",
          hl7Version: version

        });
      }
      console.log("namesAndSctruct");
      console.log(events);

      IgDocumentService.findAndAddMessages($rootScope.igdocument.id, events).then(function(result) {
        console.log(events);

        var msgsId = [];
        for (var i = 0; i < result.length; i++) {
          if (result[i].id) {
            msgsId.push(result[i].id);
          }

        }
        for (var i = 0; i < result.length; i++) {
          console.log("result[i]==========");
          console.log(result[i]);
          //result[i].id = new ObjectId().toString();
          $rootScope.igdocument.profile.messages.children.push(result[i]);
          $rootScope.messagesMap[result[i].id] = result[i];
          $rootScope.fillMaps(result[i]);
          console.log($rootScope.usedSegsLink);
          console.log($rootScope.usedDtLink);
          console.log($rootScope.usedVsLink);
        }
        var usedSegsId = _.map($rootScope.usedSegsLink, function(num, key) {
          return num.id;
        });
        var newSegmentsLink = _.difference($rootScope.usedSegsLink, $rootScope.igdocument.profile.segmentLibrary.children);

        SegmentLibrarySvc.addChildren($rootScope.igdocument.profile.segmentLibrary.id, newSegmentsLink).then(function() {

          SegmentService.findByIds(usedSegsId).then(function(segments) {
            for (var j = 0; j < segments.length; j++) {
              if (!$rootScope.segmentsMap[segments[j].id]) {
                $rootScope.fillMaps(segments[j]);
                $rootScope.segmentsMap[segments[j].id] = segments[j];
                $rootScope.segments.push(segments[j]);
              }
            }
            var usedDtId = _.map($rootScope.usedDtLink, function(num, key) {
              return num.id;
            });
            DatatypeService.get(usedDtId).then(function(datatypes) {
              for (var j = 0; j < datatypes.length; j++) {

                $rootScope.fillMaps(datatypes[j]);

              }
              var usedDtId1 = _.map($rootScope.usedDtLink, function(num, key) {
                return num.id;
              });
              var newDatatypesLink = _.difference($rootScope.usedDtLink, $rootScope.igdocument.profile.datatypeLibrary.children);

              DatatypeLibrarySvc.addChildren($rootScope.igdocument.profile.datatypeLibrary.id, newDatatypesLink).then(function() {
                DatatypeService.get(usedDtId1).then(function(datatypes) {
                  for (var j = 0; j < datatypes.length; j++) {
                    if (!$rootScope.datatypesMap[datatypes[j].id]) {
                      $rootScope.datatypesMap[datatypes[j].id] = datatypes[j];
                      $rootScope.datatypes.push(datatypes[j]);
                      console.log($rootScope.datatypesMap[datatypes[j].id]);
                    }
                  }

                  var usedVsId = _.map($rootScope.usedVsLink, function(num, key) {
                    return num.id;
                  });
                  var newTablesLink = _.difference($rootScope.usedVsLink, $rootScope.igdocument.profile.tableLibrary.children);
                  TableLibrarySvc.addChildren($rootScope.igdocument.profile.tableLibrary.id, newTablesLink).then(function() {
                    TableService.get(usedVsId).then(function(tables) {
                      for (var j = 0; j < tables.length; j++) {
                        if (!$rootScope.tablesMap[tables[j].id]) {
                          $rootScope.tablesMap[tables[j].id] = tables[j];
                          $rootScope.tables.push(tables[j]);
                        }
                      }


                      for (var i = 0; i < result.length; i++) {
                        console.log("=+++++result");
                        console.log(result);
                        $rootScope.processMessageTree(result[i]);

                        $mdDialog.hide($rootScope.igdocument);
                      }

                    });
                  });


                });
              });
            });




          });
        });
        console.log($rootScope.igdocument.profile.messages);
        console.log("$rootScope.segmentsMap");



      }, function(response) {
        $rootScope.msg().text = response.data;
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        $scope.okDisabled = false;
      });
      $scope.okDisabled = true;
      var iprw = {
        "igdocument": $rootScope.igdocument,
        "msgEvts": msgEvts,
        "timeout": 60000
      };
      // $http.post('api/igdocuments/updateIntegrationProfile', iprw)
      //     .then(
      //     function (response) {
      //         var igdocument = angular
      //             .fromJson(response.data);
      //         $mdDialog.hide(igdocument);
      //     }, function (response) {
      //         $rootScope.msg().text = response.data;
      //         $rootScope.msg().type = "danger";
      //         $rootScope.msg().show = true;
      //         $scope.okDisabled = false;
      //     });
    };

    if ($scope.hl7Version != null) {
      $scope.loadIGDocumentsByVersion($scope.hl7Version);
    }

    $scope.cancel = function() {
      console.log("Hiding ")
      $mdDialog.hide();
    };




  });
