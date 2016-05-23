angular
  .module('igl')
  .controller(
      'FilteringCtl',
          function ($scope, $rootScope, MastermapSvc, FilteringSvc) {
            var ctl = this;

            $scope.filtermsgmodel = function(){
                return FilteringSvc.getMsgmodel();
            };

            $scope.filtermsgdata = function(){
                return FilteringSvc.getMsgdata();
            };

            $scope.filtermsgsettings = function(){
                return FilteringSvc.getMsgsettings();
            };

            $scope.filtermsgsettings = function(){
                return FilteringSvc.getMsgsettings();
            };

            $scope.filterusagesmodel = function(){
                return FilteringSvc.getUsagesmodel();
            };

            $scope.filterusagesdata = function(){
                return FilteringSvc.getUsagesdata();
            };

            $scope.filterusagessettings = function(){
                return FilteringSvc.getUsagessettings();
            };

            $scope.filterusagetexts = function(){
                return FilteringSvc.getUsagestexts();
            };
//
//            $rootScope.$on('event:loadFilter', function (event, igdocument) {
//                FilteringSvc.loadMessages(igdocument);
//                FilteringSvc.loadUsages();
//            });
//
//            $rootScope.$on('event:loadMastermap', function (event, igdocument) {
//              MastermapSvc.parseIg(igdocument);
//            });
  }
);
