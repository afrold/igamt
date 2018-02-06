/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('AddFieldCtrl', function($scope, $mdDialog, datatypes, segment, valueSets, $rootScope, $http, ngTreetableParams, SegmentService, DatatypeLibrarySvc, MessageService, blockUI) {
  $scope.valueSets = valueSets;
  $scope.datatypes = datatypes;

  $scope.newField = {
    confLength: "NA",
    datatype: {
      ext: null,
      id: "",
      label: "",
      name: ""
    },
    hide: false,
    added: true,
    id: "",
    itemNo: "",
    max: "",
    maxLength: "",
    min: "",
    minLength: "",
    name: "",
    position: "",
    text: "",
    type: "field",
    usage: ""


  };
  $scope.datatypes = datatypes;
  $scope.querySearch=function (query) {
    return query? $scope.datatypes.filter( createFilterFor(query) ):$scope.datatypes;
  }
  function createFilterFor(query) {
    var lowercaseQuery = angular.lowercase(query);

    return function filterFn(dt) {

      return $scope.getLowerCaseLabel(dt).indexOf(lowercaseQuery) === 0;
    };

  }



  $scope.getLowerCaseLabel= function(element) {
    if (!element.ext || element.ext == "") {
      return angular.lowercase(element.name);
    } else {
      return angular.lowercase(element.name + "_" + element.ext);
    }
  };

  $scope.$watch('DT', function() {
    if ($scope.DT) {
      $scope.newField.datatype.ext = $scope.DT.ext;
      $scope.newField.datatype.id = $scope.DT.id;
      $scope.newField.datatype.name = $scope.DT.name;
    }
  }, true);
  $scope.loadVS = function($query) {


    return valueSets.filter(function(table) {
      return table.bindingIdentifier.toLowerCase().indexOf($query.toLowerCase()) != -1;
    });

  };
  $scope.tableList = [];
  $scope.tagAdded = function(tag) {
    $scope.vsChanged = true;
    // $scope.tableList.push({
    //     id: tag.id,
    //     bindingIdentifier: tag.bindingIdentifier,
    //     bindingLocation: null,
    //     bindingStrength: null
    // });


    //$scope.log.push('Added: ' + tag.text);
  };

  $scope.tagRemoved = function(tag) {
    $scope.vsChanged = true;

    for (var i = 0; i < $scope.tableList.length; i++) {
      if ($scope.tableList[i].id === tag.id) {
        $scope.tableList.splice(i, 1);
      }
    };


  };

  // $scope.$watch('VS', function() {
  //     if ($scope.VS) {
  //         $scope.newField.table.bindingIdentifier = $scope.VS.bindingIdentifier;
  //         $scope.newField.table.id = $scope.VS.id;


  //     }
  // }, true);

  $scope.selectUsage = function(usage) {
    if (usage === 'X' || usage === 'W') {
      $scope.newField.max = 0;
      $scope.newField.min = 0;
      $scope.disableMin = true;
      $scope.disableMax = true;

    } else if (usage === 'R') {
      $scope.newField.min = 1;

      $scope.disableMin = true;
      $scope.disableMax = false;
    } else if (usage === 'RE' || usage === 'O') {
      $scope.newField.min = 0;

      $scope.disableMin = true;
      $scope.disableMax = false;

    } else {
      $scope.disableMin = false;
      $scope.disableMax = false;
    }
  };



  $scope.selectDT = function(datatype) {
    $scope.DT = datatype;
    //$scope.newSeg = segment;
  };
  $scope.selectedDT = function() {
    return ($scope.DT !== undefined);
    //return ($scope.newSeg !== undefined);
  };
  $scope.unselectDT = function() {
    $scope.DT = undefined;
    //$scope.newSeg = undefined;
  };
  $scope.isDTActive = function(id) {
    if ($scope.DT) {
      return $scope.DT.id === id;
    } else {
      return false;
    }

  };


  $scope.selectVS = function(valueSet) {
    $scope.VS = valueSet;
    //$scope.newSeg = segment;
  };
  $scope.selectedVS = function() {
    return ($scope.VS !== undefined);
    //return ($scope.newSeg !== undefined);
  };
  $scope.unselectVS = function() {
    $scope.VS = undefined;
    //$scope.newSeg = undefined;
  };
  $scope.isVSActive = function(id) {
    if ($scope.VS) {
      return $scope.VS.id === id;
    } else {
      return false;
    }

  };


  $scope.addField = function() {
    blockUI.start();

    $scope.newField.position = $rootScope.segment.fields.length+1;
    $scope.newField.id = new ObjectId().toString();
    $rootScope.segment.fields.push($scope.newField);
    $rootScope.recordChanged();
    $rootScope.processElement($rootScope.segment);


      if ($scope.segmentsParams) {
        $scope.segmentsParams.refresh();
      }

    blockUI.stop();
    $mdDialog.hide();

  };


  $scope.cancel = function() {
    $mdDialog.hide();
  };


});
