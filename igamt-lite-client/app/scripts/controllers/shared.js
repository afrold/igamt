angular
    .module('igl')
    .controller(
    'shared', ['$scope', '$http', '$rootScope', '$q', '$modal','$timeout','ngTreetableParams', 'DatatypeLibraryDocumentSvc', 'TableLibrarySvc', 'DatatypeService', 'DatatypeLibrarySvc','IGDocumentSvc', 'TableService', 'ViewSettings', 'userInfoService',
    'blockUI','CompareService','VersionAndUseService',

        function ($scope, $http, $rootScope, $q, $modal, $timeout,ngTreetableParams, DatatypeLibraryDocumentSvc, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc,IGDocumentSvc, TableService, ViewSettings, userInfoService, blockUI,CompareService,VersionAndUseService) {

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

            $scope.setTypeOfSharing=function(type){
                $scope.typeOfSharing=type;
                if(type==='datatype'){
                    console.log("TOCs")
                    $scope.SharedtocView='sharedtocView.html';


                }
            }
         $scope.$on('event:openDatatypeonShare', function(event, datatype) {

            $scope.selectDatatype(datatype); // Should we open in a dialog ??
        });

            $scope.init=function(){
                $rootScope.datatype=null;
                $scope.getSharedDatatypes();
                $scope.SharedtocView='sharedtocView.html';
                $scope.Sharedsubview = "datatypePending.html";
                $scope.SharedDataTypeTree=[$scope.library];
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
                    $rootScope.msg().text = "igSharedConfirmationSuccessful";
                    $rootScope.msg().type ="success";
                    $rootScope.msg().show = true;
                    $scope.getSharedDatatypes();
                }, function(error) {
                    $rootScope.msg().text = "igSharedConfirmationFailed";
                    $rootScope.msg().type ="danger";
                    $rootScope.msg().show = true;
                    console.log(error);
                });
            };

            $scope.rejectShareDocument = function(datatype) {
                $http.get('api/shareDtreject/' + datatype.id).then(function(response) {
                    $rootScope.msg().text = "igSharedRejectedSuccessfully";
                    $rootScope.msg().type ="success";
                    $rootScope.msg().show = true;
                    $scope.getSharedDatatypes();
                }, function(error) {
                    $rootScope.msg().text = "igSharedRejectFailed";
                    $rootScope.msg().type ="danger";
                    $rootScope.msg().show = true;
                    console.log(error);
                });
            };

            $scope.selectDatatype = function (datatype) {
                $rootScope.datatype = angular.copy(datatype);
            console.log(datatype);
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
