/**
 * Created by haffo on 9/11/17.
 */

angular.module('igl')
  .controller('EditAreaCtrl', function ($scope, $rootScope, CloneDeleteSvc,SectionSvc,ElementUtils) {

    $scope.initArea = function() {
      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        console.log("=====> set $dirty to false")
        $rootScope.clearChanges();

      }
    };
    $scope.printForm=function () {
      console.log("Print form ")
      console.log($scope.editForm);
    }

    $scope.confLengthPattern= '[1-9]\\d*[#=]{0,1}\|^NA$';

    $rootScope.$on("event:initEditArea", function (event) {

      $scope.initArea();
    });

    $scope.$watch(
      function() {
        return $scope.editForm&&$scope.modified();
      },
      function handleFormState(newValue) {
        // console.log($scope.editForm);

        if (newValue) {
          $rootScope.recordChanged();
        } else {
          $rootScope.clearChanges();
        }
      }
    );
    $scope.$watch(
      function() {
        return $rootScope.igChanged;
      },
      function handleFormState(newValue) {
        // console.log($scope.editForm);
        if (newValue) {
          $scope.setDirty();
        } else {
          $scope.clearDirty();
        }
      }
    );


    $scope.setDirty = function() {
      $scope.editForm.$dirty = true;
      $scope.editForm.$pristine = false;
    };
    $scope.modified=function () {
      return $scope.editForm.$dirty&&!$scope.editForm.$pristine;
    }

    $scope.setUsage = function(node) {
      ElementUtils.setUsage(node);
      $scope.setDirty();
    };
    $scope.clearDirty = function() {
      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
      }
    };


    $scope.openRichTextDlg = function (obj, key, title, disabled) {
      var original = obj[key];
      var modalInstance = $rootScope.openRichTextDlg(obj, key, title, disabled);
      modalInstance.then(function () {
        if(!disabled && original !== obj[key]){
          $rootScope.recordChanged();
        }
      }, function () {
        if(!disabled && original !== obj[key]){
          $rootScope.recordChanged();
        }
      });
    };

    $scope.openInputTextDlg = function (obj, key, title, disabled) {
      var original = obj[key];
      var modalInstance = $rootScope.openInputTextDlg(obj, key, title, disabled);
      modalInstance.then(function () {
        if(!disabled && original !== obj[key]){
          $rootScope.recordChanged();
        }
      }, function () {
        if(!disabled && original !== obj[key]){
          $rootScope.recordChanged();
        }
      });
    };

    $scope.clearComments = function (obj, key) {
      obj[key] = '';
      $scope.setDirty();
    };

    $scope.editLength = function(node){
      node.confLength = "NA";
      node.minLength = "";
      node.maxLength = "";
      $scope.setDirty();
    };

    $scope.editConfLength = function(node){
      node.confLength = "";
      node.minLength = "NA";
      node.maxLength = "NA";
      $scope.setDirty();
    };

    $scope.clearConfLength = function(node){
      node.confLength = "NA";
      $scope.setDirty();
    };

    $scope.clearLength = function(node){
      node.minLength = "NA";
      node.maxLength = "NA";
      $scope.setDirty();
    };



  });
