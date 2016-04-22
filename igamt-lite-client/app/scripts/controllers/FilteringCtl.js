angular
  .module('igl')
  .controller(
      'FilteringCtl',
          function ($scope, $rootScope, FilteringSvc) {
            var ctl = this;

            $scope.filtermsgmodel = function(){
             return FilteringSvc.getmodel();
            };

                $scope.filtermsgsettings = {
                scrollableHeight: '200px',
                scrollable: true,
                enableSearch: true,
                displayProp: 'label'
              };

            $scope.filtermsgdata = [];

            $rootScope.$on('event:loadFilter', function (event, igdocument) {
                $scope.filtermsgdata = FilteringSvc.getMessages(igdocument);
            });



/*             function isShowable(elementId, filterId, filterType){
              return mastermap.get(elementId).get(filterType).has(filterId);
            }

            function isOptional(elementId){
              return mastermap.get(elementId).get('usage').has('O');
            }

            function isRequired(elementId){
              return (mastermap.get(elementId).get('usage').has('R') || mastermap.get(elementId).get('usage').has('RE'));
            } */

  }
);
