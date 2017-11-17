/**
 * Created by haffo on 9/11/17.
 */


angular.module('igl').controller('AddSegmentCtrl', function($scope, $mdDialog, segments, place, $rootScope, $http, ngTreetableParams, SegmentService, MessageService, blockUI) {
  $scope.segmentParent = place;
  //console.log(place);


  // $scope.segmentss = result.filter(function(current) {
  //     return segments.filter(function(current_b) {
  //         return current_b.id == current.id;
  //     }).length == 0
  // });
  $scope.searchText="";
  $scope.segments = segments;
  $scope.querySearch=function (query) {
    return query? $scope.segments.filter( createFilterFor(query) ):$scope.segments;
  }
  function createFilterFor(query) {
    var lowercaseQuery = angular.lowercase(query);

    return function filterFn(seg) {

      return $scope.getLowerCaseLabel(seg).indexOf(lowercaseQuery) === 0;
    };

  }



  $scope.getLowerCaseLabel= function(element) {
    if (!element.ext || element.ext == "") {
      return angular.lowercase(element.name);
    } else {
      return angular.lowercase(element.name + "_" + element.ext);
    }
  };

  $scope.newSegment = {
    accountId: null,
    comment: "",
    conformanceStatements: [],
    date: null,
    hl7Version: null,
    id: "",
    libIds: [],
    max: "",
    min: "",
    participants: [],
    position: place.children.length+1,
    predicates: [],
    ref: {
      ext: null,
      id: "",
      label: "",
      name: ""
    },
    scope: null,
    status: null,
    type: "segmentRef",
    usage: "",
    version: null

  };
  $scope.$watch('newSeg', function() {
    if ($scope.newSeg) {
      $scope.newSegment.id = new ObjectId().toString();
      $scope.newSegment.ref.ext = $scope.newSeg.ext;
      $scope.newSegment.ref.id = $scope.newSeg.id;
      $scope.newSegment.ref.name = $scope.newSeg.name;
    }

  }, true);
  $scope.isInSegs = function(segment) {
    console.log(segment);
    console.log(segments.indexOf(segment));
    if (segment && segments.indexOf(segment) === -1) {
      return false;
    } else {
      return true;
    }

  };
  $scope.selectUsage = function(usage) {
    console.log(usage);
    if (usage === 'X' || usage === 'W') {
      $scope.newSegment.max = 0;
      $scope.newSegment.min = 0;
      $scope.disableMin = true;
      $scope.disableMax = true;

    } else if (usage === 'R') {
      $scope.newSegment.min = 1;

      $scope.disableMin = true;
      $scope.disableMax = false;
    } else if (usage === 'RE' || usage === 'O') {
      $scope.newSegment.min = 0;

      $scope.disableMin = true;
      $scope.disableMax = false;

    } else {
      $scope.disableMin = false;
      $scope.disableMax = false;

    }

  };
  $scope.selectSeg = function(segment) {
    $scope.newSeg = segment;
  };
  $scope.selected = function() {
    return ($scope.newSeg !== undefined);
  };
  $scope.unselect = function() {
    $scope.newSeg = undefined;
  };
  $scope.isActive = function(id) {
    if ($scope.newSeg) {
      return $scope.newSeg.id === id;
    } else {
      return false;
    }
  };


  $scope.addSegment = function() {
    blockUI.start();
    if (place.type === "message") {
      // $rootScope.message.children.push($scope.newSegment);
      $rootScope.message.children.splice($scope.newSegment.position - 1,0, $scope.newSegment);
      for(i=0;i<$rootScope.message.children.length; i++){
        $rootScope.message.children[i].position=i+1;
      }
    } else if (place.obj && place.obj.type === "group") {

        var path = place.path.replace(/\[[0-9]+\]/g, '');
        path = path.split(".");
        $scope.insertInPath(path, $rootScope.message, $scope.newSegment);
    }
    $rootScope.messageTree = null;
    $rootScope.processMessageTree($rootScope.message);
    blockUI.stop();
    $mdDialog.hide();


  };
  $scope.insertInPath=function(path,messageOrGroup,segment){

    if(path.length===1){
      var list=messageOrGroup.children[path[0]-1].children;
      var position=segment.position;
      var element= segment;
      $scope.insertInList(position,list, element);
    }
    else{
      var oldPAth=angular.copy(path);
      var newPath=path.splice(0,1);


      $scope.insertInPath(newPath,messageOrGroup.children[oldPath[0]-1],segment);

    }
  };


  $scope.insertInList=function(position,list, element){
    list.splice(position-1,0, element);
    for(i=0;i<list.length; i++){
      list[i].position=i+1;
    }

  };

  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
