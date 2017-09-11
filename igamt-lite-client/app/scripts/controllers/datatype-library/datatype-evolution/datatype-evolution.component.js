/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('evolution',
  function($scope, $rootScope, $modalInstance, $timeout,DatatypeLibraryDocumentSvc) {

    $scope.vrs=["#","2.1","2.2","2.3","2.3.1","2.4","2.5","2.5.1","2.6","2.7","2.7.1","2.8","2.8.1","2.8.2"];
    $scope.adjusted=["21","22","23","231","24","25","251","26","27","271","28","281","282"];


    DatatypeLibraryDocumentSvc.getMatrix().then(function(result){
      $scope.matrix= result;
    });

    $scope.getColor= function(index){
      if(index===undefined){
        return "";
      }else if(index===0){
        return "#008B8B";
      }else if (index ===1){
        return "#B8860B";
      }else if (index ===2){
        return "#6495ED";
      }else if (index ===3){
        return "#9932CC";
      }else if (index ===4){
        return "#8FBC8F";
      }else if (index ===5){
        return "#2F4F4F";
      }else if (index ===6){
        return "#FF1493";
      }else if (index ===7){
        return "#FFD700";
      }else if (index ===8){
        return "#4B0082";
      }else if (index ===9){
        return "#FFB6C1";
      }else if (index ===10){
        return "#778899";
      }

    }




    $scope.ok = function() {
      $modalInstance.close();
    };

    $scope.cancel = function() {
      $modalInstance.dismiss('cancel');
    };

  });
