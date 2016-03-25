angular.module('igl').directive('windowExit', function($window, $templateCache,$http, $rootScope,StorageService,IgDocumentService,ViewSettings,AutoSaveService) {
    return {
        restrict: 'AE',
        //performance will be improved in compile
        compile: function(element, attrs){
            var myEvent = $window.attachEvent || $window.addEventListener,
                chkevent = $window.attachEvent ? 'onbeforeunload' : 'beforeunload'; /// make IE7, IE8 compatable
            myEvent(chkevent, function (e) { // For >=IE7, Chrome, Firefox
                AutoSaveService.stop();
                if($rootScope.igdocument != null) {
                    if (!ViewSettings.tableReadonly) {
                        IgDocumentService.save($rootScope.igdocument).then(function (result) {
                            StorageService.setIgDocument($rootScope.igdocument);
                        });
                    } else {
                        StorageService.setIgDocument($rootScope.igdocument);
                    }
                }
                $templateCache.removeAll();
            });
        }
    };
});