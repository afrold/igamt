angular
    .module('igl')
    .controller(
    'shared', ['$scope', '$http', '$rootScope', '$q', '$modal','$timeout','ngTreetableParams', 'DatatypeLibraryDocumentSvc', 'TableLibrarySvc', 'DatatypeService', 'DatatypeLibrarySvc','IGDocumentSvc', 'TableService', 'ViewSettings', 'userInfoService',
    'blockUI','CompareService','VersionAndUseService', 'TableService',

        function ($scope, $http, $rootScope, $q, $modal, $timeout,ngTreetableParams, DatatypeLibraryDocumentSvc, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc,IGDocumentSvc, TableService, ViewSettings, userInfoService, blockUI,CompareService,VersionAndUseService,TableService) {

            $scope.selectedTab==0;
            $scope.sharedElementView='sharedElementView';
            $scope.SharedDataTypeTree=[]
            $scope.typeOfSharing="Pending";
            $rootScope.datatype={};
            $rootScope.datatypesMap={};
            $rootScope.TablesMap={};
            $scope.datatypesParams = new ngTreetableParams({
              getNodes: function(parent) {
                  return DatatypeService.getNodes(parent, $rootScope.datatype);
              },
              getTemplate: function(node) {
                  return DatatypeService.getTemplate(node, $rootScope.datatype);
              }
            });
            $scope.datatypes = [];
            $scope.pendingDatatypes = [];
            $scope.pendingTables = [];

            $scope.setTypeOfSharing=function(type){
                $scope.typeOfSharing=type;
                if(type==='datatype'){
                    $scope.SharedtocView='sharedtocView.html';
                } else if(type==='table') {
                    $scope.SharedtocView='sharedTabletocView.html';
                    $scope.getSharedTables();
                }
            }
         $scope.$on('event:openDatatypeonShare', function(event, datatype) {

            $scope.selectDatatype(datatype); // Should we open in a dialog ??
        });

            $scope.init=function(){
                $rootScope.datatype=[];
                $rootScope.tables=[];
                $scope.getSharedDatatypes();
                $scope.typeOfSharing='datatype';
                $scope.datatypeTab = { active: true};
                $scope.SharedtocView='sharedtocView.html';
                $scope.Sharedsubview = "datatypePending.html";
                $scope.SharedDataTypeTree=[$scope.library];
                $scope.SharedTableTree=[$scope.library];
            }

            $scope.showPending = function() {
              $scope.Sharedsubview = "datatypePending.html";
            }

            $scope.getSharedDatatypes = function(){
                DatatypeService.getSharedDatatypes().then(function(result){
                    $scope.datatypes = result;
                });

                DatatypeService.getPendingSharedDatatypes().then(function(result){
                    $scope.pendingDatatypes = result;
                });

              //  $scope.datatypes=[{id:1, name:"dummy",description:"dummy"},{id:2, name:"dummy",description:"dummy"}];
                // $scope.tables=[{id:3, name:"dummy",description:"dummy"},{id:4, name:"dummy",description:"dummy"}];
                // $scope.library={name:"bla"};


            }

            $scope.getSharedTables = function(){
                TableService.getSharedTables().then(function(result){
                    $scope.tables = result;
                });

                TableService.getPendingSharedTables().then(function(result){
                    $scope.pendingTables = result;
                });
            }

          function processEditDataType(data) {
            console.log("dialog not opened");
            //$rootScope.datatype=data;
            $rootScope.datatype = angular.copy(data);
            //$rootScope.datatype =result;
            $rootScope.currentData =$rootScope.datatype;
            $scope.$emit('event:openDatatypeonShare',$rootScope.datatype);
        };

        $scope.editDatatype = function(data) {
                processEditDataType(data);

        };

        $scope.confirmShareDocument = function(datatype) {
                $http.get('api/shareDtconfimation/' + datatype.id).then(function(response) {
                    $rootScope.msg().text = "dtSharedConfirmationSuccessful";
                    $rootScope.msg().type ="success";
                    $rootScope.msg().show = true;
                    $scope.getSharedDatatypes();
                }, function(error) {
                    $rootScope.msg().text = "dtSharedConfirmationFailed";
                    $rootScope.msg().type ="danger";
                    $rootScope.msg().show = true;
                    console.log(error);
                });
            };

            $scope.rejectShareDocument = function(datatype) {
                $http.get('api/shareDtreject/' + datatype.id).then(function(response) {
                    $rootScope.msg().text = "dtSharedRejectedSuccessfully";
                    $rootScope.msg().type ="success";
                    $rootScope.msg().show = true;
                    $scope.getSharedDatatypes();
                }, function(error) {
                    $rootScope.msg().text = "dtSharedRejectFailed";
                    $rootScope.msg().type ="danger";
                    $rootScope.msg().show = true;
                    console.log(error);
                });
            };

            $scope.confirmShareTable = function(table) {
                    $http.get('api/shareTableconfimation/' + table.id).then(function(response) {
                        $rootScope.msg().text = "vsSharedConfirmationSuccessful";
                        $rootScope.msg().type ="success";
                        $rootScope.msg().show = true;
                        $scope.getSharedTables();
                    }, function(error) {
                        $rootScope.msg().text = "vsSharedConfirmationFailed";
                        $rootScope.msg().type ="danger";
                        $rootScope.msg().show = true;
                        console.log(error);
                    });
                };

                $scope.rejectShareTable = function(table) {
                    $http.get('api/shareTablereject/' + table.id).then(function(response) {
                        $rootScope.msg().text = "vsSharedRejectedSuccessfully";
                        $rootScope.msg().type ="success";
                        $rootScope.msg().show = true;
                        $scope.getSharedTables();
                    }, function(error) {
                        $rootScope.msg().text = "vsSharedRejectFailed";
                        $rootScope.msg().type ="danger";
                        $rootScope.msg().show = true;
                        console.log(error);
                    });
                };

            $scope.selectDatatype = function (datatype) {
            $rootScope.datatype = angular.copy(datatype);
            $rootScope.Activate(datatype.id);
            $scope.Sharedsubview = "EditDatatypes.html";
            if (datatype && datatype != null) {
                $scope.loadingSelection = true;
                blockUI.start();
                $timeout(
                    function () {


                                $rootScope.$emit("event:initDatatype");

                                $rootScope.currentData = datatype;

                                $scope.loadingSelection = false;
                                $rootScope.datatype["type"] = "datatype";
                                $rootScope.tableWidth = null;
                                $rootScope.scrollbarWidth = $rootScope.getScrollbarWidth();
                                $rootScope.csWidth = $rootScope.getDynamicWidth(1, 3, 890);
                                $rootScope.predWidth = $rootScope.getDynamicWidth(1, 3, 890);
                                $rootScope.commentWidth = $rootScope.getDynamicWidth(1, 3, 890);
                                $scope.loadingSelection = false;
                                try {
                                    if ($scope.datatypesParams)
                                        $scope.datatypesParams.refresh();
                                } catch (e) {

                                }
                                $rootScope.references = [];
                                $rootScope.tmpReferences = [].concat($rootScope.references);
                                $rootScope.tmpReferences = [].concat($rootScope.references);

                                $rootScope.$emit("event:initEditArea");

                                blockUI.stop();

                        }
                    , 100);

                setTimeout(function () {
                    $scope.$broadcast('reCalcViewDimensions');
                    console.log("refreshed Slider!!");
                }, 1000);
            }
        };




        }]);
