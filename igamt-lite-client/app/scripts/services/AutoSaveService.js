/**
 * Created by haffo on 3/18/16.
 */
angular.module('igl').factory('AutoSaveService',
    function ($interval, IgDocumentService,$rootScope,StorageService) {
        var AutoSaveService = {
            value: undefined,
            interval: "60000", // every 60s
            start: function () {
                if (angular.isDefined(this.value)) {
                    this.stop();
                }
                this.value = $interval(this.saveDoc, this.interval);
            },
            stop: function () {
                if (angular.isDefined(this.value)) {
                    $interval.cancel(this.value);
                    this.value = undefined;
                }
            },
            saveDoc: function () {
                if ($rootScope.igdocument != null && $rootScope.hasChanges()) {
                    $rootScope.autoSaving = true;
                    $rootScope.saved = false;
                    $rootScope.clearChanges();
                    IgDocumentService.save($rootScope.igdocument).then(function(){
                        $rootScope.autoSaving = false;
                        $rootScope.saved = true;
                        StorageService.setIgDocument($rootScope.igdocument);
                        $rootScope.msg().text = null;
                        $rootScope.msg().type =null;
                        $rootScope.msg().show = false;
                    },function(){
                        $rootScope.autoSaving = false;
                        $rootScope.saved = false;
                        $rootScope.msg().text = null;
                        $rootScope.msg().type =null;
                        $rootScope.msg().show = false;
                    });
                }
            }
        };
        return AutoSaveService;
    });

