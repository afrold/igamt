angular
  .module('igl')
  .controller(
      'FilteringCtl',
          function ($scope, $rootScope, FilteringSvc) {
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

            $rootScope.$on('event:loadFilter', function (event, igdocument) {
                FilteringSvc.setMsgdata(FilteringSvc.getMessages(igdocument));
                FilteringSvc.setMsgmodel(FilteringSvc.getMessages(igdocument));
                FilteringSvc.setMsgsettings(FilteringSvc.getSettings());
                FilteringSvc.setMsgtexts(FilteringSvc.getTexts("Conf. profiles"));

                FilteringSvc.setUsagesdata(FilteringSvc.getUsages());
                FilteringSvc.setUsagesmodel(FilteringSvc.getUsages());
                FilteringSvc.setUsagessettings(FilteringSvc.getSettings());
                FilteringSvc.setUsagestexts(FilteringSvc.getTexts("Usages"));
            });

  }
);
