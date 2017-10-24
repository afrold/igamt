/**
 * Created by haffo on 9/11/17.
 */
angular
  .module('igl')
  .controller(
    'shared', ['$scope', '$http', '$rootScope', '$q', '$modal','$timeout','ngTreetableParams', 'DatatypeLibraryDocumentSvc', 'TableLibrarySvc', 'DatatypeService', 'DatatypeLibrarySvc','IGDocumentSvc', 'TableService', 'ViewSettings', 'userInfoService',
      'blockUI','CompareService','VersionAndUseService', 'TableService',

      function ($scope, $http, $rootScope, $q, $modal, $timeout,ngTreetableParams, DatatypeLibraryDocumentSvc, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc,IGDocumentSvc, TableService, ViewSettings, userInfoService, blockUI,CompareService,VersionAndUseService,TableService) {

        $scope.selectedTab==0;
        $rootScope.SharingScope=true;
        $rootScope.datatypeLibrary=null;
        $scope.sharedElementView='sharedElementView';
        $scope.sharedElementViewForTables='sharedElementViewForTables.html';
        $scope.SharedtocViewForTables='';
        $scope.SharedDataTypeTree=[]
        $scope.typeOfSharing="Pending";
        $rootScope.datatype={};
        $rootScope.datatypesMap={};
        $rootScope.TablesMap={};
        $rootScope.igdocument=null;
        $scope.tableTab=false;
        $scope.$on('event:openTableForShare', function (event, table) {
          $scope.selectTable(table); // Should we open in a dialog ??
        });

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
            $scope.getSharedDatatypes();
          } else if(type==='table') {
            $scope.SharedtocViewForTables='sharedTabletocView.html';
            $scope.SharedsubviewForTable="datatypePending.html";
            $scope.getSharedTables();
          }
        };

        $scope.$on('event:openDatatypeonShare', function(event, datatype) {

          $scope.selectDatatype(datatype); // Should we open in a dialog ??
        });

        $scope.editTable=function(table){
          $rootScope.table=table;


        };

        $scope.getOwnerName = function(element) {
          if(!element.accountId) {
            return null;
          }
          return $http.get('api/shareparticipant', { params: { id: element.accountId } })
            .then(
              function(response) {

                console.log("Response is Here")
                console.log(response.data)

                element.owner = response.data;

              },
              function(error) {
                console.log(error);
              });
        };

        $scope.init=function(){
          $scope.pending=true;
          $scope.getSharedDatatypes();
          $scope.setTypeOfSharing('datatype');
          $scope.datatypeTab = { active: true};
          $scope.SharedtocView='sharedtocView.html';
          $scope.Sharedsubview = "datatypePending.html";
          $scope.SharedDataTypeTree=[$scope.library];
          $scope.SharedTableTree=[$scope.library];
        };

        $scope.showPending = function() {
          $scope.pending=true;
          $scope.Sharedsubview = "datatypePending.html";
          $scope.SharedsubviewForTable="datatypePending.html";
        };

        $scope.getSharedDatatypes = function(){
          blockUI.start();

          // Reset has pending
          $scope.hasPending = false;

          DatatypeService.getSharedDatatypes().then(function(result){

            $scope.datatypes = result;

            $scope.processListOfshared($scope.datatypes);


            angular.forEach($scope.datatypes, function(datatype){
              $scope.getOwnerName(datatype);


              //$rootScope.datatypesMap[datatype.id]=datatype;
              //$scope.processDatatype(datatype);

            });
            blockUI.stop();
          });

          DatatypeService.getPendingSharedDatatypes().then(function(result){
            $scope.pendingDatatypes=result;
            $scope.processListOfshared($scope.pendingDatatypes);


            angular.forEach($scope.pendingDatatypes, function(datatype){
              $scope.getOwnerName(datatype);


              //$rootScope.datatypesMap[datatype.id]=datatype;
              //$scope.processDatatype(datatype);

            });
            blockUI.stop();

            if($scope.pendingDatatypes.length > 0) {
              $scope.hasPending = true;
            }
          });

          //  $scope.datatypes=[{id:1, name:"dummy",description:"dummy"},{id:2, name:"dummy",description:"dummy"}];
          // $scope.tables=[{id:3, name:"dummy",description:"dummy"},{id:4, name:"dummy",description:"dummy"}];
          // $scope.library={name:"bla"};


        };

        $scope.getSharedTables = function(){

          // Reset has pending
          $scope.hasPending = false;

          TableService.getSharedTables().then(function(result){
            $scope.tables = result;
            angular.forEach($scope.tables, function(table){
              $scope.getOwnerName(table);
              $rootScope.tablesMap[table.id]=table;
            });
          });

          TableService.getPendingSharedTables().then(function(result){
            $scope.pendingTables = result;
            angular.forEach($scope.pendingTables, function(table){
              $scope.getOwnerName(table);
              $rootScope.tablesMap[table.id]=table;
            });

            if($scope.pendingTables.length > 0) {
              $scope.hasPending = true;
            }
          });
        };

        function processEditDataType(data) {
          console.log("dialog not opened");
          //$rootScope.datatype=data;
          $rootScope.datatype = angular.copy(data);
          //$rootScope.datatype =result;
          $rootScope.currentData =$rootScope.datatype;
          $scope.$emit('event:openDatatypeonShare',$rootScope.datatype);
        };

        $scope.editDatatype = function(data) {
          $scope.pending=false;
          processEditDataType(data);

        };
        $scope.editTable = function(table) {
          $scope.pending=false;
          $scope.tableTab=true;
          $scope.$emit('event:openTableForShare',table);

        };

        $scope.processListOfshared= function(datatypes){
          $scope.datatypesIds=[];

          angular.forEach(datatypes, function (datatype) {
            $scope.processDatatype(datatype);



          })
          $scope.datatypesIds=_.uniq($scope.datatypesIds);
          DatatypeService.get($scope.datatypesIds).then(function (result) {
            console.log("Found")
            angular.forEach(result,function (dt) {
              $rootScope.datatypesMap[dt.id]=dt;
            });


          });

        };

        $scope.processDatatype=function (datatype) {
          $scope.datatypesIds.push(datatype.id);
          if(datatype.components&&datatype.components.length>0){

            angular.forEach(datatype.components, function(component){
              if(component.datatype && component.datatype.id) {
                DatatypeService.getOne(component.datatype.id).then(
                  function (result) {
                    $rootScope.datatypesMap[result.id]=result;
                    $scope.processDatatype(result);
                  }
                );

              }
            });



          }

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
                angular.forEach($scope.datatypes, function (dt) {
                  if (dt && dt != null && dt.id !== $rootScope.datatype.id) $rootScope.findDatatypeRefs(datatype, dt, $rootScope.getDatatypeLabel(dt), dt);
                });

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

        $scope.selectTable = function (t) {
          $rootScope.Activate(t.id);
          $scope.SharedsubviewForTable = "ReadValueSets.html";

          $scope.loadingSelection = true;
          blockUI.start();

          $rootScope.table = t;
          $rootScope.table.codes = $rootScope.table.codes.slice(0,1000);
          $rootScope.$emit("event:initTable");
          $rootScope.codeSystems = [];
          for (var i = 0; i < $rootScope.table.codes.length; i++) {
            if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
              if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
              }
            }
          }
          $rootScope.references = [];

          angular.forEach($scope.datatypes, function (dt) {
            $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt), dt);
          });

          $scope.loadingSelection = false;
          $rootScope.$emit("event:initEditArea");
          blockUI.stop();
        };



        $scope.unshareDatatype=[
          ['Remove',
            function ($itemScope) {
              $rootScope.refsForDelete=[];
              angular.forEach($scope.datatypes, function (dt) {
                if (dt && dt != null && dt.id !== $itemScope.data.id) $rootScope.findTempDatatypeRefs($itemScope.data, dt, $rootScope.getDatatypeLabel(dt),dt);
              });
              if($rootScope.refsForDelete.length>0){
                $scope.abortUnshare($itemScope.data);
              }else{
                $scope.confirmUnshare($itemScope.data);
              }
            }

          ]
        ];
        $scope.pendingContext=[
          ['Confirm Share',
            function ($itemScope) {
              $scope.confirmShareDocument($itemScope.data);

            }

          ],
          ['Reject Share',
            function ($itemScope) {
              $scope.rejectShareDocument($itemScope.data);
            }

          ]

        ];

        $scope.unshareTable=[
          ['Remove',
            function ($itemScope) {
              $rootScope.refsForDelete=[];
              angular.forEach($scope.datatypes, function (dt) {
                if (dt && dt != null && dt.id !== $itemScope.table.id) $rootScope.findTableRefsForDelete($itemScope.table, dt,$rootScope.getDatatypeLabel(dt),dt);
              });
              if($rootScope.refsForDelete.length>0){
                $scope.abortUnshare($itemScope.table);
              }else{
                $scope.confirmUnshareTable($itemScope.table);
              }
            }

          ]
        ];



        $scope.removeDatatype=function(datatype){
          var accountId=userInfoService.getAccountID();
          var accountId=userInfoService.getAccountID();
          console.log(accountId);
          DatatypeService.unshare(datatype.id,accountId).then(function(res){
            console.log("unshared");

            var index = $scope.datatypes.indexOf(datatype);
            console.log(index);
            if (index > -1){

              $scope.datatypes.splice(index, 1);
            }
          });

        }

        $scope.removeTable=function(table){
          var accountId=userInfoService.getAccountID();
          var accountId=userInfoService.getAccountID();
          console.log(accountId);
          TableService.unshare(table.id,accountId).then(function(res){
            console.log("unshared");

            var index = $scope.tables.indexOf(table);
            console.log(index);
            if (index > -1){

              $scope.tables.splice(index, 1);
            }
          });

        }

        $rootScope.confirmUnshare = function(datatype) {
          var modalInstance = $modal.open({
            templateUrl: 'confirmUnshare.html',
            controller: 'confirmUnshare',
            resolve: {
              datatypeTo: function() {
                return datatype;
              }

            }
          });
          modalInstance.result.then(function(datatype) {
            $scope.removeDatatype(datatype);

          });
        };

        $rootScope.confirmUnshareTable = function(table) {
          var modalInstance = $modal.open({
            templateUrl: 'confirmUnshare.html',
            controller: 'confirmUnshare',
            resolve: {
              datatypeTo: function() {
                return table;
              }

            }
          });
          modalInstance.result.then(function(table) {
            $scope.removeTable(table);

          });
        };

        $rootScope.abortUnshare = function(datatype) {
          var modalInstance = $modal.open({
            templateUrl: 'abortDeleteDatatype.html',
            controller: 'abortUnshare',
            resolve: {
              datatypeTo: function() {
                return datatype;
              }


            }
          });
          modalInstance.result.then(function() {

          });
        };



      }]);
