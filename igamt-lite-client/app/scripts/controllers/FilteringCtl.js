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

            $scope.filtermsgsettings = {
                scrollableHeight: '200px',
                scrollable: true,
                enableSearch: true,
                buttonClasses: 'btn btn-xs',
                displayProp: 'label'
            };


            $scope.texts = {
                checkAll: 'Check All',
                uncheckAll: 'Uncheck All',
                selectionCount: 'checked',
                selectionOf: '/',
                searchPlaceholder: 'Search...',
                buttonDefaultText: 'Conf. profile',
                dynamicButtonTextSuffix: 'checked'
            };
            angular.extend($scope.texts, $scope.translationTexts);

            $rootScope.$on('event:loadFilter', function (event, igdocument) {
                FilteringSvc.setMsgdata(FilteringSvc.getMessages(igdocument));
                FilteringSvc.setMsgmodel(FilteringSvc.getMessages(igdocument));
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
