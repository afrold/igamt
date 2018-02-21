/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('otherDTCtrl', function($scope, $mdDialog, datatypes, field, $rootScope, SegmentService, blockUI) {

  $scope.dtChanged = false;
  $scope.field = angular.copy(field);
  $scope.searchText="";
  $scope.newDt = null;
  var oldDt = angular.copy(field.datatype);
  $scope.datatypes = [];
  if($rootScope.currentData.type==='datatype'){
    angular.forEach(datatypes ,function(dt){
      if(dt.name!==$rootScope.datatype.name){
        $scope.datatypes.push(dt);
      }
    });
  }else{
    $scope.datatypes=datatypes;

  }

  $scope.querySearch=function (query) {
    return query? $scope.datatypes.filter(createFilterFor(query) ):$scope.datatypes;
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

  //$scope.vs = angular.copy(field.tables);
  //$scope.tableList = angular.copy(field.tables);;

  $scope.isInDts = function(datatype) {
    if(datatype=='none'){
      return false;
    }

    if (datatype && $scope.datatypes.indexOf(datatype) === -1) {
      return false;
    } else {
      return true;
    }

  };
  $scope.getDtLabel = function(element) {
    if (element) {
      if (element.ext !== null && element.ext !=='') {
        return element.name + "_" + element.ext;
      } else {
        return element.name;
      }
    }
    return "";
  };
  $scope.addDT = function() {
    blockUI.start();

    $scope.dtChanged = false;

    field.datatype = {
      id: $scope.newDt.id,
      name: $scope.newDt.name,
      ext: $scope.newDt.ext,
      label:$scope.newDt.label
    };

    blockUI.stop();

    $mdDialog.hide(field);


  };


  $scope.cancel = function() {
    field.datatype = oldDt;
    $mdDialog.hide('cancel');
  };


});
