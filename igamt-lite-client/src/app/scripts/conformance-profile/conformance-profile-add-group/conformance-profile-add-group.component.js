/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl').controller('AddGroupCtrl', function($scope, $mdDialog, segments, place, $rootScope, $http, ngTreetableParams, SegmentService, MessageService, blockUI) {
  $scope.groupParent = place;

  $scope.newGroup = {
    accountId: null,
    children: [],
    comment: "",
    conformanceStatements: [],
    date: null,
    hl7Version: null,
    id: "",
    libIds: [],
    max: "",
    min: "",
    name: "",
    participants: [],
    position: "",
    predicates: [],
    scope: null,
    status: null,
    type: "group",
    usage: "",
    version: null

  };
  $scope.selectUsage = function(usage) {
    console.log(usage);
    if (usage === 'X' || usage === 'W') {
      $scope.newGroup.max = 0;
      $scope.newGroup.min = 0;
      $scope.disableMin = true;
      $scope.disableMax = true;

    } else if (usage === 'R') {
      $scope.newGroup.min = 1;

      $scope.disableMin = true;
      $scope.disableMax = false;
    } else if (usage === 'RE' || usage === 'O') {
      $scope.newGroup.min = 0;

      $scope.disableMin = true;
      $scope.disableMax = false;

    } else {
      $scope.disableMin = false;
      $scope.disableMax = false;

    }

  };


  $scope.addGroup = function() {
    blockUI.start();

    $scope.newGroup.id = new ObjectId().toString();
    $scope.newGroup.name = $scope.grpName;
    if (place.children.length !== 0) {
      if (place.type === "message") {
        $scope.newGroup.position = place.children[place.children.length - 1].position + 1;


      } else {
        $scope.newGroup.position = place.children[place.children.length - 1].obj.position + 1;

      }

    } else {
      $scope.newGroup.position = 1;
    }


    if (place.type === "message") {
      $rootScope.message.children.push($scope.newGroup);
      MessageService.updatePosition(place.children, $scope.newGroup.position - 1, $scope.position - 1);

    } else if (place.obj && place.obj.type === "group") {
      $scope.path = place.path.replace(/\[[0-9]+\]/g, '');
      $scope.path = $scope.path.split(".");

      MessageService.addSegToPath($scope.path, $rootScope.message, $scope.newGroup, $scope.newGroup.position - 1, $scope.position - 1);

      //place.children.push($scope.newSegment);
    }


    $rootScope.messageTree = null;
    $rootScope.processMessageTree($rootScope.message);
    //console.log($rootScope.messageTree);

    if ($scope.messagesParams) {
      $scope.messagesParams.refresh();
    }
    blockUI.stop();
    $mdDialog.hide();


  };


  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
