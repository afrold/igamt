angular
  .module('igl')
  .controller(
      'MastermapCtl',
          function ($scope, $rootScope, MastermapSvc) {
            var ctl = this;

            $scope.mastermap = function(){
              return MastermapSvc.getMastermap();
            }

            $rootScope.$on('event:loadMastermap', function (event, igdocument) {
              MastermapSvc.parseIg(igdocument);
            });


  }
);
