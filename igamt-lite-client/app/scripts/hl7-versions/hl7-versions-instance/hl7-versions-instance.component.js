/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller(
  'HL7VersionsInstanceDlgCtrl',
  function($scope, $rootScope, $mdDialog, $http, hl7Versions, ProfileAccessSvc, MessageEventsSvc, SegmentService, DatatypeService, TableService, TableLibrarySvc, SegmentLibrarySvc, DatatypeLibrarySvc, IgDocumentService, $timeout, ngTreetableParams, userInfoService, hl7Version, MessagesSvc) {

    $scope.hl7Versions = hl7Versions;
    $scope.hl7Version = hl7Version;
    $scope.messageEventsParams=null;
    $scope.usedSegsLink = [];
    $scope.usedDtLink = [];
    $scope.usedVsLink = [];
    $scope.usedValueSetsId=[];
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

    $scope.loadIGDocumentsByVersion = function(hl7Version) {
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
              $rootScope.$emit(
                  'event:openIGDocumentRequest',
                  igdocument);
              $rootScope.$broadcast('event:IgsPushed',
                  igdocument);
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

        var events = [];
        var version = $scope.hl7Version;
      for (var i = 0; i < msgEvts.length; i++) {
        events.push({
          name: msgEvts[i].children[0].name,
          parentStructId: msgEvts[i].children[0].parentStructId,
          scope: "HL7STANDARD",
          hl7Version: version

        });
      }
      IgDocumentService.findAndAddMessages($rootScope.igdocument.id, events).then(function(result) {

          angular.forEach(result.datatypes, function (datatype) {
              $rootScope.datatypes.push(datatype);
              $rootScope.datatypesMap[datatype.id]=datatype;
              $rootScope.igdocument.profile.datatypeLibrary.children.push({name: datatype.name, id : datatype.id});

          });
          angular.forEach(result.tables, function (table) {
              $rootScope.tables.push(table);
              $rootScope.tablesMap[table.id]=table;
              $rootScope.igdocument.profile.tableLibrary.children.push({name: table.bindingIdentifier, id : table.id});

          });

          angular.forEach(result.segments, function (segment) {
              $rootScope.segments.push(segment);
              $rootScope.segmentsMap[segment.id]=segment;
              $rootScope.igdocument.profile.segmentLibrary.children.push({name: segment.bindingIdentifier, id : segment.id});

          });

          angular.forEach(result.msgsToadd, function (msg) {

              $rootScope.igdocument.profile.messages.children.push(msg);
              if($rootScope.section&& $rootScope.type=='messages'){
                  console.log($rootScope.section);
                  $rootScope.section.children.push(msg);
              }
              $rootScope.messagesMap[msg.id]=msg;
          });

          $mdDialog.hide($rootScope.igdocument);
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
          $scope.okDisabled = false;

      },function (error) {
              $scope.okDisabled = true;

          }
      );
    };

    $scope.cancel = function() {
        $mdDialog.hide();
    };






  });
