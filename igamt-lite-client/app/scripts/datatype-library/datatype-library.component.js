/**
 * Created by haffo on 9/11/17.
 */
angular.module('igl').controller('DatatypeLibraryCtl',
  function($scope, $http, $rootScope, $q, $modal, $timeout, TableService, ngTreetableParams, DatatypeLibraryDocumentSvc, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc,IGDocumentSvc, TableService, ViewSettings, userInfoService, blockUI,CompareService,VersionAndUseService,$mdDialog) {
    //  $scope.initLibrary();
    $rootScope.filteringModeON = false;
    $scope.searchObject={};
    $rootScope.showToc=true;
    $scope.ttype="USER";
    // $rootScope.config = { "usages": ["R", "B", "RE", "C", "W", "X", "O"], "codeUsages": ["P", "R", "E"], "codeSources": ["HL7", "Local", "Redefined", "SDO"], "tableStabilities": ["Dynamic", "Static"], "tableExtensibilities": ["Close", "Open"], "constraintVerbs": ["SHALL be", "SHALL NOT be", "is", "is not"], "constraintTypes": ["valued", "one of list values", "formatted value", "a literal value", "identical to the another node"], "predefinedFormats": ["YYYYMMDDhhmmss.sss", "ISO-compliant OID", "YYYYMMDDhhmm+-ZZZZ", "YYYYMMDDhh", "YYYY+-ZZZZ", "YYYY", "YYYYMMDDhhmm", "YYYYMM", "YYYYMMDDhhmmss+-ZZZZ", "Alphanumeric", "YYYYMM+-ZZZZ", "YYYYMMDDhhmmss", "YYYYMMDD+-ZZZZ", "YYYYMMDDhh+-ZZZZ", "YYYYMMDDhhmmss.sss+-ZZZZ", "YYYYMMDD"], "statuses": ["Draft", "Active", "Withdrawn", "Superceded"], "domainVersions": ["2.0", "2.1", "2.2", "2.3", "2.4", "2.5", "2.5.1", "2.6", "2.7", "2.3.1", "2.8"], "schemaVersions": ["1.0", "2.0", "1.5", "2.5"] }
    $rootScope.igdocument = null; // current igdocument
    $rootScope.message = null; // current message
    $rootScope.datatype = null; // current datatype
    $scope.accountId = userInfoService.getAccountID().toString();
    $rootScope.pages = ['list', 'edit', 'read'];
    $rootScope.context = { page: $rootScope.pages[0] };
    $rootScope.messagesMap = {}; // Map for Message;key:id, value:object
    $rootScope.segmentsMap = {}; // Map for Segment;key:id, value:object
    $rootScope.datatypesMap = {}; // Map for Datatype; key:id, value:object
    $rootScope.tablesMap = {}; // Map for tables; key:id, value:object
    $rootScope.segments = []; // list of segments of the selected messages
    $rootScope.datatypes = []; // list of datatypes of the selected messages
    $rootScope.segmentPredicates = [];
    $scope.linksForData = [];
    $rootScope.linksForTables = [];
    $scope.AllUnchanged=[];
    $rootScope.interMediates=[];
    $scope.tabs = [{active: true}, {active: false}, {active: false}];
    $scope.dtList = [{active: true}, {active: false}, {active: false}];

    // list of segment level predicates of
    $rootScope.segmentConformanceStatements = []; // list of segment level

    $rootScope.datatypePredicates = []; // list of segment level predicates of
    $rootScope.datatypeConformanceStatements = []; // list of segment level
    // Conformance Statements of
    // the selected messages
    $rootScope.tables = [];
    $rootScope.postfixCloneTable = 'CA';
    $rootScope.newCodeFakeId = 0;
    $rootScope.newTableFakeId = 0;
    $rootScope.newPredicateFakeId = 0;
    $rootScope.newConformanceStatementFakeId = 0;
    $rootScope.segment = null;
    $rootScope.messagesData = [];
    $rootScope.messages = []; // list of messages
    $rootScope.customIgs = [];
    $rootScope.preloadedIgs = [];
    $rootScope.changes = {};
    $rootScope.generalInfo = { type: null, 'message': null };
    $rootScope.references = []; // collection of element referencing a datatype
    $rootScope.tmpReferences = [];
    $rootScope.section = {};
    $rootScope.conformanceStatementIdList = [];
    $rootScope.parentsMap = {};
    $rootScope.igChanged = false;
    $scope.selectedDT = null;
    $rootScope.messageTree = null;
    $scope.scrollbarWidth = 0;
    $scope.datatypeLibsStruct = [];
    $scope.editableDTInLib = '';
    $scope.editableVS = '';
    $scope.derivedDatatypes = [];
    $rootScope.tables = [];
    $scope.toShow === "";
    $rootScope.datatypesMap = {};
    $scope.tablesMap = {};
    $scope.tablesIds = [];
    $rootScope.datatypeLibrary = null;
    $scope.datatypeLibMetaDataCopy = null;
    $scope.datatypeStruct = null;
    $rootScope.datatype=null;
    $scope.loadingSelection = true;
    $scope.publishSelections = [];
    $scope.datatypeDisplay = [];
    $scope.selectedChildren = [];
    $scope.viewSettings = ViewSettings;
    $rootScope.subview = null;
    $scope.datatypeListView = null;
    $scope.added = [];
    $scope.accordi = { metaData: false, definition: true, dtList: true, dtDetails: false };
    $rootScope.DTLibList = true;
    $rootScope.DTLibDetails = false;
    //      $scope.forms.datatypeForm = {};
    $scope.tableWidth = null;
    // $rootScope.datatypeLibrary = "";
    $scope.hl7Version = null;
    $scope.scopes = [];
    $scope.tableCollapsed = false;
    $scope.datatypeLibrariesConfig = {};
    $scope.AllUnchanged=[];

    //$scope.accountId =userInfoService.accountId;

    $rootScope.readOnly = false;
    $scope.$on('event:openDatatypeInLib', function(event, datatype) {

      $scope.selectDatatype(datatype); // Should we open in a dialog ??
    });

    $scope.$on('event:openTable', function(event, table) {
      $scope.selectTable(table); // Should we open in a dialog ??
    });

    $scope.addingView = 'addingView.html';

    $scope.toggle = function(param) {
      $scope.toShow = param;

    }

    $scope.vrs=["#","2.3.1","2.4","2.5","2.5.1","2.6","2.7","2.7.1","2.8","2.8.1","2.8.2"];
    $scope.adjusted=["231","24","25","251","26","27","271","28","281","282"];

    $scope.initMatrix=function(){

      blockUI.start();
      DatatypeLibraryDocumentSvc.getMatrix().then(function(result){

        $scope.matrix= result;
        blockUI.stop();
      });
    };
    $scope.make_active = function(x) {
      for(i=0; i<$scope.tabs.length;i++){
        if(i==x){
          $scope.tabs[i].active = true;
        }else{
          $scope.tabs[i].active=false;
        }
      }
    };

    $scope.make_active_dtList = function(x) {
      console.log(x);

      console.log($scope.dtList);

      for(i=0; i<$scope.dtList.length;i++){
        if(i==x){
          $scope.dtList[i].active = true;
        }else{
          $scope.dtList[i].active=false;
        }
      }

    };
    $scope.getColor= function(index){
      if(index===undefined){
        return "";
      }else if(index===0){
        return "#008B8B";
      }else if (index ===1){
        return "#B8860B";
      }else if (index ===2){
        return "#6495ED";
      }else if (index ===3){
        return "#9932CC";
      }else if (index ===4){
        return "#8FBC8F";
      }else if (index ===5){
        return "#2F4F4F";
      }else if (index ===6){
        return "#FF1493";
      }else if (index ===7){
        return "#FFD700";
      }else if (index ===8){
        return "#4B0082";
      }else if (index ===9){
        return "#FFB6C1";
      }else if (index ===10){
        return "#778899";
      }
    }
    $scope.isSelected1=function(dt,version){
      return $scope.selectedRow===dt&& $scope.selectedVersion1===version ;

    }
    $scope.isSelected2=function(dt, version){
      return $scope.selectedRow===dt&& $scope.selectedVersion2===version ;
    }

    $scope.selectedVersion1=null;
    $scope.selectedVersion2=null;
    $scope.selectCell=function(dt,version){
      var vr=version.split('.').join("");

      console.log(version);
      console.log(vr);
      console.log(dt.links);
      if(dt.links[vr]!==undefined){
        $scope.selectCellAfterCheck(dt,version);
      }else{
        console.log("no version");
      }

    }
    $scope.expandAll=function(){
      $('#deltaTable').treetable('expandAll');
      $timeout( function(){
        $('#deltaTable').treetable('expandAll');
      }, 100 );
    };


    $scope.resetFilter=function () {
        $scope.searchObject={};
    }
    $scope.searchFliter=function (obj) {
        if($scope.searchObject.status && obj.status !==$scope.searchObject.status){
            return false;

        }else {
            return true;
        }
    };
    $scope.selectCellAfterCheck=function(dt,version){
      if($scope.selectedRow && dt===$scope.selectedRow){
        if($scope.selectedVersion1!==null){
          if($scope.selectedVersion1&&$scope.selectedVersion1=== version){
            $scope.selectedVersion1=null;
          }else if($scope.selectedVersion2!==null &&$scope.selectedVersion2=== version){
            $scope.selectedVersion2=null;
          }else{
            $scope.selectedVersion2=version;
          }
        }else{
          $scope.selectedVersion1=version;
        }

      }else if($scope.selectedRow && dt!==$scope.selectedRow ||!$scope.selectedRow){
        $scope.selectedRow=dt;
        $scope.selectedVersion2=null;
        $scope.selectedVersion1=version;
      }

      if($scope.selectedRow !==null &&$scope.selectedVersion2!==null &&$scope.selectedVersion1!==null ){

        var versions1=[];
        versions1.push($scope.selectedVersion1);
        var versions2=[];
        versions2.push($scope.selectedVersion2);

        DatatypeService.getOneStandard($scope.selectedRow.name, $scope.selectedVersion1,versions1).then(function(result1) {
          $scope.cmp1=result1;
          console.log("Found the first One")
          console.log($scope.cmp1);
          DatatypeService.getOneStandard($scope.selectedRow.name, $scope.selectedVersion2,versions2).then(function(result2) {
            $scope.cmp2=result2;
            console.log("Found the secont One")
            console.log($scope.cmp2);

            $scope.loadingSelection = true;
            $scope.dtChanged = false;
            $scope.vsTemplate = false;
            $scope.dataList = CompareService.cmpDatatype($scope.cmp1, $scope.cmp2, [], [], [], []);


            $scope.loadingSelection = false;
            if ($scope.dynamicDt_params) {
              console.log($scope.dataList);

              $scope.showDelta = true;
              $scope.dynamicDt_params.refresh();
              $scope.expandAll();
            }

          });
        });


      }
    }

    $scope.datatypeSource = null;
    $scope.tableSource = null;

    $scope.collapseTables = function() {
      $scope.tableCollapsed = !$scope.tableCollapsed;

    }
    $scope.selectDTLibTab = function(value) {
      if (value === 1) {
        $rootScope.DTLibList = false;
        $rootScope.DTLibDetails = true;
        $scope.evolution=false;
      } else if(value===0) {
        $rootScope.DTLibList = true;
        $rootScope.DTLibDetails = false;
        $scope.evolution=false;
      }else{
        $scope.evolution=true;
        $rootScope.DTLibList = false;
        $rootScope.DTLibDetails = false;
      }
    };

    $scope.seq = function(idx) {
      return idx + 1;
    };
    $scope.confirmClose = function() {
      if ($rootScope.hasChanges()) {
        $rootScope.openConfirmLeaveDlg().then(function() {
          $rootScope.closeLibrary();
        });
      } else {
        $rootScope.closeLibrary();
      }
    };

    $rootScope.closeLibrary=function () {
      $rootScope.datatypesMap={};
      $rootScope.libraryDoc= null;

      $scope.dtList = [{active: true}, {active: false}, {active: false}];


      $rootScope.tables = [];
      $scope.tablesIds = [];
      $rootScope.datatypeLibrary = null;
    }

    $scope.getDatatypes = function(datatypeLibrary) {
      $scope.datatypeListView = "DatatypeList.html";
      $scope.loadingSelection = true;
      $timeout(
        function() {
          $scope.loadingSelection = false;
        }, 100);
    };

    $scope.getDataTypeLibraryByScope = function(scope) {
      $scope.ttype=scope;
      $scope.datatypeLibsStruct = [];
      DatatypeLibraryDocumentSvc.getDataTypeLibraryDocumentByScope(scope).then(function(data) {
        $scope.datatypeLibsStruct = [];
        angular.forEach(data.data, function(lib) {
          $scope.datatypeLibsStruct.push(lib);
        });
        $rootScope.isEditing = false;
      }).catch(function(error) {
      });
    };


    $scope.saveMetaDataOfLibrary = function() {
      $rootScope.datatypeLibrary.metaData = angular.copy($scope.datatypeLibMetaDataCopy);

      DatatypeLibrarySvc.saveMetaData($rootScope.datatypeLibrary.id, $scope.datatypeLibMetaDataCopy).then(function(metaData){

        $scope.editForm.$dirty = false;

        $rootScope.clearChanges();
        console.log("called");
      });
      //$scope.clearDirty();


    };
    $scope.resetMetaDataOfLibrary = function() {

      $scope.datatypeLibMetaDataCopy = angular.copy($rootScope.datatypeLibrary.metaData);

      $scope.DataTypeTree[0].metaData = $scope.datatypeLibMetaDataCopy;
      $rootScope.clearChanges();
    };

    $scope.editMetadata = function() {

      $scope.datatypeLibMetaDataCopy = $scope.DataTypeTree[0].metaData;
      $rootScope.currentData= $scope.datatypeLibMetaDataCopy;
      console.log($scope.datatypeLibMetaDataCopy);
      $rootScope.subview = "LibraryMetaData.html";

    }

    $scope.exportAs = function(dataTypeLibraryDocumentId,format){
      if ($rootScope.hasChanges()) {

        $rootScope.openConfirmLeaveDlg().then(function () {
          if ($scope.editForm) {
            console.log("Cleeaning");
            $scope.editForm.$dirty = false;
            $scope.editForm.$invalid = false;

          }
          $rootScope.clearChanges();

          DatatypeLibraryDocumentSvc.exportAs(dataTypeLibraryDocumentId,format);

        });
      }else{
        DatatypeLibraryDocumentSvc.exportAs(dataTypeLibraryDocumentId,format);
      }

    }



    $scope.selectList = function() {

      $scope.getDataTypeLibraryByScope('USER');
      $scope.make_active(0);

    };

    $scope.selectMatrix = function() {


      $scope.initMatrix();
      $scope.make_active(2);


    };

    // $scope.selectEdit = function() {
    //
    //     if ($rootScope.hasChanges()) {
    //         $rootScope.openConfirmLeaveDlg().then(function() {
    //         });
    //     } else {
    //     }
    // };




    $scope.editLibrary = function(datatypeLibraryDocument, readOnly) {

      if ($rootScope.hasChanges()) {
        $rootScope.openConfirmLeaveDlg().then(function() {
          $rootScope.processEditLibrary(datatypeLibraryDocument, readOnly);
        });
      } else {
        $rootScope.processEditLibrary(datatypeLibraryDocument, readOnly);
      }
    };






    $rootScope.processEditLibrary=function(datatypeLibraryDocument, readOnly){
      $rootScope.Activate(datatypeLibraryDocument.id);
      blockUI.start();
      $timeout(function () {
        $scope.initParams();
        $rootScope.libraryDoc= datatypeLibraryDocument;
        $rootScope.accountId=datatypeLibraryDocument.accountId;
        $scope.viewSettings.setTableReadonly(readOnly);
        $rootScope.ext=datatypeLibraryDocument.metaData.ext;

        $rootScope.tables = [];
        $scope.tablesIds = [];
        $rootScope.datatypeLibrary = datatypeLibraryDocument.datatypeLibrary;
        // $rootScope.datatypeLibrary = datatypeLibraryDocument;
        $scope.datatypesIds = [];
        angular.forEach($rootScope.datatypeLibrary.children, function(datatypeLink) {
          $scope.datatypesIds.push(datatypeLink.id);
        });
        $scope.addedDatatypes = [];
        $scope.datataypestoAdd = [];
        $rootScope.libEXT = $rootScope.datatypeLibrary.metaData.ext;

        var scopes = ['HL7STANDARD'];

        $scope.loadingSelection = true;
        //$rootScope.isEditing = true;
        // $scope.hl7Version = $rootScope.datatypeLibrary.metaData.hl7Version;
        $rootScope.datatypeLibraryId = $rootScope.datatypeLibrary.id;
        $scope.datatypeLibMetaDataCopy = angular.copy(datatypeLibraryDocument.datatypeLibrary.metaData);
        $rootScope.currentData= $scope.datatypeLibMetaDataCopy;
        $scope.loadingSelection = false;
        $scope.DataTypeTree = [];
        $scope.datatypeLibCopy = angular.copy($rootScope.datatypeLibrary);
        $scope.datatypeLibCopy.children = [];
        $rootScope.readOnly = readOnly;

        if (!readOnly) {
          $scope.datatypeListView = "DatatypeList.html";
        } else {
          $scope.datatypeListView = "DatatypeListReadOnly.html";
        }
        $rootScope.datatype=null;
        $rootScope.filteringModeON = false;
        $rootScope.initMaps();
        $scope.make_active_dtList(1);
        //DTLibDetails=true;
        $rootScope.datatypes = [];
        $rootScope.datatypesMap = {};
        $rootScope.tablesMap = {};
        $rootScope.igdocument = null;
        $scope.DataTypeTree.push($scope.datatypeLibCopy);
        $rootScope.subview = "LibraryMetaData.html";
        $rootScope.$emit("event:initEditArea");


        DatatypeLibraryDocumentSvc.getAllDatatypesNames().then(function(res) {
          $scope.AllUnchanged = res;
          $scope.loadVersionAndUseInfo().then(function(){
            $scope.loadDatatypes().then(function() {
              $scope.make_active(1);
              blockUI.stop();
            }, function() {});
          })
        });
      }, function () {

      });


    };


    $rootScope.getDerived = function(element) {
      try {
        if (element && element.type && element.type === "datatype") {

          angular.forEach(element.components, function(component) {
            $rootScope.getDerived(component);
          });
        } else if (element && element.type && element.type === "component") {

          if (element.tables&&element.tables != null) {
            angular.forEach(element.tables, function(table){
              $rootScope.linksForTables.push(table);

            });


          }
          if (element.datatype !== null || element.datatype !== undefined) {
            var newLink = angular.fromJson({
              id: element.datatype.id,
              name: element.datatype.name,
              ext: element.datatype.ext
            });

            $scope.getDatatypeById(element.datatype.id).then(function(result) {

              DatatypeLibrarySvc.addChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
                if (!$rootScope.datatypesMap[element.datatype.id] || $rootScope.datatypesMap[element.datatype.id] === undefined) {
                  $rootScope.datatypes.push(result);
                  $rootScope.datatypesMap[element.datatype.id] = result;
                  $rootScope.getDerived(result);


                }

              }, function(error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
              });

            });
          }

        }

      } catch (e) {
        throw e;
      }

    };

    $scope.hasUnpublishedCopy= function(dt){
      var res= false;

      angular.forEach($rootScope.datatypes, function(datatype){
        if(datatype.versionInfo&&datatype.versionInfo.sourceId&&datatype.versionInfo.sourceId&&datatype.versionInfo.sourceId===dt.id){
          return true;
        }
      });
      return res;
    }
    $scope.addDatatypeForUser = function(hl7Version) {
      var scopes = ['HL7STANDARD'];

      DatatypeService.getDataTypesByScopesAndVersion(scopes, $scope.hl7Version).then(function(datatypes) {
        DatatypeLibrarySvc.getDataTypeLibraryByScope('MASTER').then(function(masterLib) {
          DatatypeLibrarySvc.getDataTypeLibraryByScope('USER').then(function(userDtLib) {
            var dtlibs=[];
            angular.forEach(userDtLib, function(dtLib){
              if(dtLib.id!==$rootScope.datatypeLibrary.id){
                dtlibs.push(dtLib);
              }
            });
            console.log(dtlibs);
            console.log("userDtLib");
            console.log(userDtLib);

            console.log("addDatatype scopes=" + scopes.length);
            var addDatatypeInstance = $modal.open({
              templateUrl: 'AddHL7Datatype.html',
              controller: 'AddDatatypeCtrl',
              size: 'lg',
              windowClass: 'addDatatype',
              resolve: {
                hl7Version: function() {
                  return $scope.hl7Version;
                },
                datatypes: function() {

                  return datatypes;
                },
                masterLib: function() {

                  return masterLib;
                },
                userDtLib: function() {
                  return dtlibs;
                },
                datatypeLibrary: function(){
                  return $rootScope.datatypeLibrary;
                },
                tableLibrary :function(){
                  return $rootScope.tableLibrary;
                },
                versionAndUseMap:function(){
                  return $rootScope.versionAndUseMap;
                }

              }
            }).result.then(function(results) {
              var ids = [];
              angular.forEach(results, function(result) {
                ids.push(result.id);
              });
            });
          });
        });
      });
    };

    $scope.addDatatypeTemplate = function() {
      var scopes = ['HL7STANDARD'];
      var addDatatypeInstance = $mdDialog.show({
        templateUrl: 'createDatatype.html',
        controller: 'AddDatatypeTemplate',
        size: 'lg',
        scope: $scope,        // use parent scope in template
        preserveScope: true,
        locals: {
          datatypes:  $rootScope.datatypes,

          AllUnchanged:  $scope.AllUnchanged,

          datatypeLibrary: $rootScope.datatypeLibrary,

          tableLibrary :$scope.tableLibrary
        }

      }).then(function(results) {
        DatatypeLibrarySvc.addChildrenFromDatatypes($rootScope.datatypeLibrary.id, results).then(function(result){


          angular.forEach(result, function(dt){
            console.log(dt);
            //$scope.processAddedDT(dt);
            if(dt.scope=="INTERMASTER"){
              $rootScope.interMediates.push(dt);
            }else{
              $rootScope.datatypes.push(dt);
            }


            $rootScope.datatypesMap[dt.id]=dt;
            $rootScope.datatypeLibrary.children.push({name:dt.name,ext:dt.ext,id:dt.id});

          });
        });
      });
    };



    $scope.dynamicDt_params = new ngTreetableParams({
      getNodes: function(parent) {
        if ($scope.dataList !== undefined) {

          //return parent ? parent.fields : $scope.test;
          if (parent) {
            if (parent.fields) {
              return parent.fields;
            } else if (parent.components) {
              return parent.components;
            } else if (parent.segments) {
              return parent.segments;
            } else if (parent.codes) {
              return parent.codes;
            }

          } else {
            return $scope.dataList;
          }

        }
      },
      getTemplate: function(node) {
        return 'tree_node';
      }
    });
    $scope.cmpDatatype = function(datatype1, datatype2) {

      $scope.loadingSelection = true;
      $scope.dtChanged = false;
      $scope.vsTemplate = false;
      $scope.dataList = CompareService.cmpDatatype(datatype1, datatype2, $scope.dtList1, $scope.dtList2, $scope.segList1, $scope.segList2);
      console.log($scope.dataList);
      $scope.loadingSelection = false;
      if ($scope.dynamicDt_params) {
        console.log($scope.dataList);
        $scope.showDelta = true;
        $scope.status.isSecondOpen = true;
        $scope.dynamicDt_params.refresh();
      }

    };

    $scope.selectOneToAdd = function(data) {
      if (data.clone) {
        $scope.addedItem = angular.copy(data);
        $scope.addedItem.libIds = [];
        $scope.addedItem.libIds.push($rootScope.datatypeLibrary.id);
        $scope.addedDatatypes.push($scope.addedItem);
        data.clone = false;
      }
      if (data.flavor) {
        $scope.addedItem = angular.copy(data);
        var randext = $scope.datatypeLibMetaDataCopy.ext + Math.floor(Math.random() * 100);
        $scope.addedItem.id = new ObjectId().toString();
        $scope.addedItem.ext = randext;
        $scope.addedItem.scope = $rootScope.datatypeLibrary.scope;
        $scope.addedItem.status = 'UNPUBLISHED';
        $scope.addedItem.libIds = [];
        $scope.addedItem.libIds.push($rootScope.datatypeLibrary.id);
        $scope.addedDatatypes.push($scope.addedItem);
        data.flavor = false;
      }
    };

    $scope.AddAsFlavor = function(data) {


      $scope.addedItem = angular.copy(data);
      var randext = $scope.datatypeLibMetaDataCopy.ext + Math.floor(Math.random() * 100);
      $scope.addedItem.id = new ObjectId().toString();
      $scope.addedItem.ext = randext;
      $scope.addedItem.scope = $rootScope.datatypeLibrary.scope;
      $scope.addedItem.status = 'UNPUBLISHED';
      $scope.addedItem.libIds = [];
      $scope.addedItem.libIds.push($rootScope.datatypeLibrary.id);
      $scope.addedDatatypes.push($scope.addedItem);


    };

    $scope.AddAsIs = function(data) {
      $scope.miniDTMap[data.id] = data;
      $scope.addedItem = data;
      $scope.addedItem.libIds = [];
      $scope.addedItem.libIds.push($rootScope.datatypeLibrary.id);
      $scope.addedDatatypes.push($scope.addedItem);

    };

    $scope.submit = function(addedDatatype) {
      $scope.addedDatatypes=addedDatatype;
      $rootScope.clearChanges();
      $scope.linksForData = [];
      $rootScope.linksForTables = [];
      angular.forEach($scope.addedDatatypes, function(data) {
        $scope.LinksForSubmit(data);
        if (data.status !== "PUBLISHED") {
          DatatypeService.save(data).then(function(res) {
          });
        }
      });

      angular.forEach($scope.addedDatatypes, function(datatype) {
        if(!$rootScope.datatypesMap[datatype.id]){
          var newLink = {};
          newLink = angular.fromJson({
            id: datatype.id,
            name: datatype.name,
            ext: datatype.ext
          });


          $scope.linksForData.push(newLink);
        }
      });
      $rootScope.datatypeLibrary.children = _.union($rootScope.datatypeLibrary.children, $scope.linksForData);
      //$rootScope.tableLibrary.children = _.union($scope.tableLibrary.children, $rootScope.linksForTables);
      $rootScope.tablesMap = {};
      $rootScope.datatypesMap = {};
      $scope.addedDatatypes=[];


      DatatypeLibrarySvc.addChildren($rootScope.datatypeLibrary.id, $scope.linksForData).then(function(results) {
        // TableLibrarySvc.addChildren($rootScope.tableLibrary.id, $rootScope.linksForTables).then(function(tables) {
        $scope.loadDatatypes().then(function() {
          //
          // $scope.loadTables().then(function() {
          //
          // }, function() {});


        }, function() {});
      });
      // });
      //
    };



    $scope.submitAdded = function() {
      var delay = $q.defer();
      var promises = [];
      angular.forEach($scope.addedDatatypes, function(datatype) {
        promises.push($scope.submitAsynch(datatype));
      });
      console.log("promises");
      console.log(promises);
      console.log("My datatypeLoibrary ");
      console.log($rootScope.datatypeLibrary);
      $scope.addedDatatypes = [];
      $q.all(promises).then(function(fields) {
        delay.resolve(fields);
      });
      return delay.promise;

    };


    $scope.submit1 = function() {
      $scope.submitAdded().then(function(result) {
        console.log("result");
        console.log(result);
        $scope.miniDTMap = [];
      });

    };




    $scope.LinksForSubmit = function(element) {

      if (element && element.type && element.type === "datatype") {

        angular.forEach(element.components, function(component) {
          $scope.LinksForSubmit(component);
        });


      } else if (element && element.type && element.type === "component") {

        if (element.tables&& element.tables != null) {
          angular.forEach(element.tables, function(table){
            var tmp = [];
            tmp.push(table);

            $rootScope.linksForTables = _.union($rootScope.linksForTables, tmp);
          });

        }
        if (element.datatype !== null || element.datatype !== undefined) {


          var index = $scope.linksForData.indexOf(element.datatype);
          if (index < 0) {
            $scope.linksForData.push(element.datatype);

            $scope.getDatatypeById(element.datatype.id).then(function(result) {

              $scope.LinksForSubmit(result);

            });

          }


        }

      }
    };

    $scope.publishDatatype = function(datatype) {

      $scope.containUnpublished = false;
      $scope.unpublishedTables = [];
      $scope.unpublishedDatatypes = [];
      $scope.ContainUnpublished(datatype);

      if ($scope.containUnpublished) {
        $scope.abortPublish(datatype);
        datatype.status = "UNPUBLISHED";
      } else {
        $scope.confirmPublish(datatype);

      }
    };


    $scope.getFilteredTables = function(element) {

      try {
        if (element && element.type && element.type === "datatype") {

          angular.forEach(element.components, function(component) {
            $scope.getFilteredTables(component);
          });
        } else if (element && element.type && element.type === "component") {

          if (element.tables&&element.tables != null) {
            angular.forEach(element.tables, function(table){

              var index = $rootScope.tables.indexOf($rootScope.tablesMap[table.id]);
              if (index < 0) {

                console.log("Adding the table");
                console.log(element);
                console.log($rootScope.tablesMap[table.id]);
                $rootScope.tables.push($rootScope.tablesMap[table.id]);
              }

            });
          }
          if (element.datatype !== null || element.datatype !== undefined) {

            $scope.getFilteredTables($rootScope.datatypesMap[element.datatype.id]);

          }

        }

      } catch (e) {
        throw e;
      }

    };

    $rootScope.getDerived = function(element) {
      try {
        if (element && element.type && element.type === "datatype") {

          angular.forEach(element.components, function(component) {
            $rootScope.getDerived(component);
          });
        } else if (element && element.type && element.type === "component") {

          if (element.tables&&element.tables != null) {
            angular.forEach(element.tables, function(table){
              $rootScope.linksForTables.push(table);

            });


          }
          if (element.datatype !== null || element.datatype !== undefined) {
            var newLink = angular.fromJson({
              id: element.datatype.id,
              name: element.datatype.name,
              ext: element.datatype.ext
            });

            $rootScope.getDatatypeById(element.datatype.id).then(function(result) {

              DatatypeLibrarySvc.addChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
                if (!$rootScope.datatypesMap[element.datatype.id] || $rootScope.datatypesMap[element.datatype.id] === undefined) {
                  $rootScope.datatypes.push(result);
                  $rootScope.datatypesMap[element.datatype.id] = result;
                  $rootScope.getDerived(result);


                }

              }, function(error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
              });

            });
          }

        }

      } catch (e) {
        throw e;
      }

    };

    $scope.filterByDatatype = function(datatype) {
      $rootScope.filteringModeON = true;
      $rootScope.tables = [];
      $scope.getFilteredTables(datatype);
    };

    $scope.deleteValueSetINLIB = function(table) {
      $rootScope.references = [];
      console.log($rootScope.datatypes);
      angular.forEach($rootScope.datatypes, function(dt) {
        $rootScope.findTableRefsINLIB(table, dt, $rootScope.getDatatypeLabel(dt));
      });
      console.log($rootScope.references);
      if ($rootScope.references != null && $rootScope.references.length > 0) {
        abortValueSetDelete(table);
      } else {
        confirmValueSetDelete(table);
      }
    };

    function abortValueSetDelete(table) {
      var modalInstance = $modal.open({
        templateUrl: 'ValueSetReferencesCtrl.html',
        controller: 'ValueSetReferencesCtrl',
        resolve: {
          tableToDelete: function() {
            return table;
          }
        }
      });
      modalInstance.result.then(function(table) {
        // $rootScope.tableToDelete = table;
      }, function() {});
    };

    function confirmValueSetDelete(table) {
      var modalInstance = $modal.open({
        templateUrl: 'ConfirmValueSetDeleteCtrl.html',
        controller: 'ConfirmTablesDeleteCtl',
        resolve: {
          tableToDelete: function() {
            return table;
          }
        }
      });
      modalInstance.result.then(function(table) {
        var newLink = {};
        newLink.bindingIdentifier = table.bindingIdentifier;
        newLink.id = table.id;
        if ($rootScope.tables && $rootScope.tables != null) {
          var index = $rootScope.tables.indexOf(table);
          if (index >= 0)
            $rootScope.tables.splice(index, 1);
        }
        TableLibrarySvc.deleteChild($scope.tableLibrary.id, newLink.id).then(function(link) {
        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }, function() {});
    };

    $scope.addDatatypetoLibrary = function(datatype) {
      var newLink = angular.fromJson({
        id: datatype.id,
        name: datatype.name,
        ext: datatype.ext
      });
      $rootScope.datatypeLibrary.children.push(newLink);
      DatatypeService.save(datatype).then(function(result) {
        DatatypeLibrarySvc.addChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {}, function(error) {
          $rootScope.saving = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }, function(error) {
        $rootScope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    };

    $scope.addHL7DatatypetoLibrary = function(datatype) {
      var newLink = angular.fromJson({
        id: datatype.id,
        name: datatype.name,
        ext: datatype.ext
      });
      $rootScope.datatypeLibrary.children.push(newLink);

      DatatypeLibrarySvc.addChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {}, function(error) {
        $rootScope.saving = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    }

    $rootScope.findTableRefsINLIB = function(table, obj, path) {
      if (obj && angular.equals(obj.type, 'component')) {

        if (obj.tables != undefined &&obj.tables!==null) {
          angular.forEach(obj.tables,function(table1){
            if (table1.id === table.id) {
              var found = angular.copy(obj);
              console.log(found);
              found.path = path;
              $rootScope.references.push(found);
            }
          });
        }
      } else if (obj && angular.equals(obj.type, 'datatype')) {
        if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
          angular.forEach(obj.components, function(component) {
            $rootScope.findTableRefsINLIB(table, component, path + "." + component.position);
          });
        }
      }
    };

//
//        $scope.loadDatatypes = function() {
//            var delay = $q.defer();
//            $rootScope.datatypeLibrary.type = "datatypes";
//            var dtIds = [];
//            for (var i = 0; i < $rootScope.datatypeLibrary.children.length; i++) {
//                dtIds.push($rootScope.datatypeLibrary.children[i].id);
//                //console.log(0)
//            }
//            DatatypeService.get(dtIds).then(function(result) {
//                console.log("==========Adding Datatypes from their IDS============");
//                $rootScope.datatypes = result;
//                console.log(result);
//                angular.forEach(result, function(datatype) {
//                    $rootScope.datatypesMap[datatype.id] = datatype;
//                });
//                delay.resolve(true);
//
//            }, function(error) {
//                $rootScope.msg().text = "DatatypesLoadFailed";
//                $rootScope.msg().type = "danger";
//                $rootScope.msg().show = true;
//                delay.reject(false);
//
//            });
//            return delay.promise;
//        };

    $scope.loadDatatypes = function () {
      var delay = $q.defer();
      $rootScope.datatypeLibrary.type = "datatypes";
      DatatypeLibrarySvc.getDatatypesByLibrary($rootScope.datatypeLibrary.id).then(function (children) {
        $rootScope.datatypes = [];
        $rootScope.interMediates=[];
        $rootScope.datatypesMap = {};
        angular.forEach(children, function (child) {

          if(child.scope=="INTERMASTER"){
            $rootScope.interMediates.push(child);
          }else{
            $rootScope.datatypes.push(child);
          }
          this[child.id] = child;
        }, $rootScope.datatypesMap);
        delay.resolve(true);
      }, function (error) {
        $rootScope.msg().text = "DatatypesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);

      });
      return delay.promise;
    };
    $scope.loadVersionAndUseInfo = function() {
      var delay = $q.defer();
      var dtIds = [];
      for (var i = 0; i < $rootScope.datatypeLibrary.children.length; i++) {
        dtIds.push($rootScope.datatypeLibrary.children[i].id);
        //console.log(0)
      }
      VersionAndUseService.findAll().then(function(result) {

        console.log(result);
        angular.forEach(result, function(info) {
          $rootScope.versionAndUseMap[info.id] = info;
        });
        delay.resolve(true);

      }, function(error) {
        $rootScope.msg().text = "DatatypesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);

      });
      return delay.promise;
    };
//        $scope.loadTables = function() {
//            var delay = $q.defer();
//            //$scope.tableLibrary.type = "tables";
//            var tableIds = [];
//            console.log($scope.tableLibrary);
//            for (var i = 0; i < $scope.tableLibrary.children.length; i++) {
//                tableIds.push($scope.tableLibrary.children[i].id);
//            }
//
//
//            TableService.findAllByIds(tableIds).then(function(tables) {
//                $rootScope.tables = tables;
//                angular.forEach(tables, function(table) {
//                    $rootScope.tablesMap[table.id] = table;
//                });
//                console.log($rootScope.tablesMap);
//
//            }, function(error) {
//                $rootScope.msg().text = "TablesLoadFailed";
//                $rootScope.msg().type = "danger";
//                $rootScope.msg().show = true;
//                delay.reject(false);
//            });
//
//            return delay.promise;
//        };

    $scope.loadTables = function () {
      var delay = $q.defer();
      $rootScope.tableLibrary.type = "tables";




      TableLibrarySvc.getTablesByLibrary($rootScope.tableLibrary.id).then(function (children) {
        $rootScope.tables = children;
        $rootScope.tablesMap = {};
        angular.forEach(children, function (child) {
          this[child.id] = child;
        }, $rootScope.tablesMap);
        delay.resolve(true);
      }, function (error) {
        $rootScope.msg().text = "TablesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);
      });
      return delay.promise;

    };

    $scope.openRichTextDlg = function(obj, key, title, disabled) {
      var modalInstance = $modal.open({
        templateUrl: 'RichTextCtrlLIB.html',
        controller: 'RichTextCtrlLIB',
        windowClass: 'app-modal-window',
        backdrop: true,
        keyboard: true,
        backdropClick: false,
        resolve: {
          editorTarget: function() {
            return {
              key: key,
              obj: obj,
              disabled: disabled,
              title: title
            };
          }
        }
      });
    };
    $scope.addAllTables = function() {
      console.log($scope.tablesIds);
      var delay = $q.defer();
      TableService.findAllByIds($scope.tablesIds).then(function(tables) {
        angular.forEach(tables, function(child) {
          this[child.id] = child;
        }, $scope.tablesMap);

        console.log($rootScope.tables)
        $rootScope.tables = tables;
        $scope.initialTables = angular.copy(tables);
        $rootScope.tables.forEach(function(table, i) {
          var newLink = {};
          newLink.bindingIdentifier = table.bindingIdentifier;
          newLink.id = table.id;

          TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function(link) {
            $scope.tableLibrary.children.splice(0, 0, newLink);

          }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });
        });

      }, function(error) {
        console.log(error);
        $rootScope.msg().text = "TablesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);
      });
      return delay.promise;
    };

    $scope.addTables = function(igdocument) {
      var modalInstance = $modal.open({
        templateUrl: 'AddTableOpenCtrlLIB.html',
        controller: 'AddTableOpenCtrlLIB',
        windowClass: 'conformance-profiles-modal',
        resolve: {
          tableLibrary: function() {
            return $scope.tableLibrary;
          },
          derivedTables: function() {
            return $rootScope.tables;
          }
        }
      });
      modalInstance.result.then(function() {}, function() {});
    };


    $scope.addTable = function(tableLink) {
      var delay = $q.defer();

      var tableId = [];
      tableId.push(tableLink.id);
      TableService.getOne(tableLink.id).then(function(table) {


        if (!$rootScope.tablesMap[table.id] || $rootScope.tablesMap[table.id] === undefined) {

          $rootScope.tables.push(table);
          $rootScope.tablesMap[table.id] = table;
          var newLink = {};
          newLink.bindingIdentifier = table.bindingIdentifier;
          newLink.id = table.id;

          TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function(link) {
            $scope.tableLibrary.children.splice(0, 0, newLink);
          }, function(error) {
            $rootScope.msg().text = error.data.text;
            $rootScope.msg().type = error.data.type;
            $rootScope.msg().show = true;
          });

        }

      }, function(error) {
        console.log(error);
        $rootScope.msg().text = "TablesLoadFailed";
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        delay.reject(false);
      });
      return delay.promise;
    };

    $scope.createNewExtension = function(ext) {
      if ($rootScope.datatypeLibrary != null) {
        var rand = (Math.floor(Math.random() * 10000000) + 1);
        if ($rootScope.datatypeLibrary.metaData.ext === null) {
          return ext != null && ext != "" ? ext + "_" + rand : rand;
        } else {
          return ext != null && ext != "" ? ext + "_" + $rootScope.datatypeLibrary.metaData.ext + "_" + rand + 1 : rand + 1;
        }
      } else {
        return null;
      }
    };
    $scope.copyTableINLIB = function(table) {
      var newTable = angular.copy(table);
      newTable.participants = [];
      newTable.scope = $scope.tableLibrary.scope;
      newTable.status = "UNPUBLISHED";
      newTable.id = null;
      newTable.libIds = [];
      newTable.bindingIdentifier = $scope.createNewExtension(table.bindingIdentifier);


      if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
        for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
          newTable.codes[i].id = new ObjectId().toString();
        }
      }

      TableService.save(newTable).then(function(result) {
        newTable = result;
        $scope.table = newTable;
        $scope.tablesMap[newTable.id] = newTable;
        console.log(result);
        $scope.tablesIds.push(result.id);
        var newLink = angular.copy(TableLibrarySvc.findOneChild(table.id, $scope.tableLibrary.children));
        newLink.bindingIdentifier = newTable.bindingIdentifier;
        newLink.id = newTable.id;

        TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function(link) {
          $scope.tableLibrary.children.splice(0, 0, newLink);
          $rootScope.tables.splice(0, 0, newTable);
          $scope.table = newTable;
          $scope.tablesMap[newTable.id] = newTable;

          $scope.codeSystems = [];

          for (var i = 0; i < $scope.table.codes.length; i++) {
            if ($scope.codeSystems.indexOf($scope.table.codes[i].codeSystem) < 0) {
              if ($scope.table.codes[i].codeSystem && $scope.table.codes[i].codeSystem !== '') {
                //$scope.s.push($scope.table.codes[i].codeSystem);
              }
            }
          }


        }, function(error) {
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });


      }, function(error) {
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });
    };

    $scope.redirectDTLIB = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'ConfirmRedirect.html',
        controller: 'ConfirmRedirect',
        resolve: {
          datatypeTo: function() {
            return datatype;
          }
        }
      });
      modalInstance.result.then(function(datatype) {
        DatatypeService.getOne(datatype.id).then(function(datatype) {
          $rootScope.datatype = datatype;
          $rootScope.editDatatype($scope.datatype);
          $rootScope.ActiveModel=datatype;

        });
      });
    };
    $rootScope.getDatatypeById = function(id) {
      var delay = $q.defer();
      if ($rootScope.datatypesMap[id] === undefined || $rootScope.datatypesMap[id] === null) {
        $http.get('api/datatypes/' + id).then(function(response) {
          var datatype = angular.fromJson(response.data);
          //$rootScope.datatypesMap[id] = datatype;
          delay.resolve(datatype);
        }, function(error) {
          delay.reject(error);
        });
      } else {
        delay.resolve($rootScope.datatypesMap[id]);
      }
      return delay.promise;
    }



    $scope.addTablesInLibrary = function() {
      var modalInstance = $modal.open({
        templateUrl: 'AddTableOpenCtrl.html',
        controller: 'AddTableOpenCtrlLIB',
        windowClass: 'conformance-profiles-modal',
        resolve: {
          igdocumentToSelect: function() {
            return igdocument;
          }
        }
      });
      modalInstance.result.then(function() {}, function() {});

    }
    $scope.backDT = function() {
      $scope.editableDTInLib = '';
    };

    $scope.copyLibrary = function(datatypeLibrary) {
      var newDatatypeLibrary = angular.copy(datatypeLibrary.datatypeLibrary);
      newDatatypeLibrary.id = new ObjectId().toString();
      //newDatatypeLibrary.metaData.ext = newDatatypeLibrary.metaData.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
      newDatatypeLibrary.accountId = userInfoService.getAccountID();
      var newTableLibrary = angular.copy(datatypeLibrary.tableLibrary);
      newTableLibrary.id = new ObjectId().toString();
      //newTableLibrary.metaData.ext = newDatatypeLibrary.metaData.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
      newTableLibrary.accountId = userInfoService.getAccountID();

      var newDatatypeLibraryDocument = angular.copy(datatypeLibrary);
      newDatatypeLibraryDocument.id = null;
      newDatatypeLibraryDocument.datatypeLibrary = newDatatypeLibrary;
      newDatatypeLibraryDocument.tableLibrary = newTableLibrary;
      newDatatypeLibraryDocument.accountId = userInfoService.getAccountID();
      $scope.datatypeLibsStruct.push(newDatatypeLibrary);
      DatatypeLibrarySvc.save(newDatatypeLibrary).then(function(response) {

        //newDatatypeLibraryDocument.datatypeLibrary=response;
        TableLibrarySvc.save(newTableLibrary).then(function(response) {
          //newDatatypeLibraryDocument.tableLibrary=response;
          DatatypeLibraryDocumentSvc.save(newDatatypeLibraryDocument).then(function(response) {

            $scope.editLibrary(response);

          });
        });
      });

    };

    $scope.deleteLibrary = function(datatypeLibrary) {
      $scope.confirmLibraryDelete(datatypeLibrary);
    };

    $scope.confirmLibraryDelete = function(datatypeLibrary) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'ConfirmDatatypeLibraryDeleteCtrl.html',
        controller: 'ConfirmDatatypeLibraryDeleteCtrl',
        scope:$scope,
        preserveScope:true,
        locals: {
          datatypeLibraryToDelete: datatypeLibrary

        }

      });
      modalInstance.then(function(datatypeLibraryDocument) {
        if(datatypeLibraryDocument){
          DatatypeLibraryDocumentSvc.delete(datatypeLibraryDocument.id).then(function(result) {
            $rootScope.datatypeLibrary=null;
            $rootScope.msg().text = "LibraryDeleteSuccess";
            var idxP = _.findIndex($scope.datatypeLibsStruct, function(child) {
              return child.id === datatypeLibrary.id;
            });
            $scope.datatypeLibsStruct.splice(idxP, 1);
            $scope.DataTypeTree = [];
            $scope.datatypeLibCopy = {};
            $scope.datatypeLibMetaDataCopy = {};
            $scope.accordi.dtDetails = false;
            $rootScope.isEditing = false;
          });
        }

      });
    };

    $scope.toggleStatus = function(status) {
      $scope.datatype.status = $scope.datatype.status === 'PUBLISHED' ? 'UNPUBLISHED' : 'PUBLISHED';
    };

    $scope.saveDatatype = function(datatypeCopy) {
      //console.log("save datatypeForm=" + $scope.forms.editForm);
      $rootScope.datatypeLibrary = angular.copy(datatypeCopy);
      DatatypeService.save($rootScope.datatypeLibrary).then(function(result) {
        //$scope.selectedDT=$scope.datatype;
        $rootScope.msg().text = "datatypeSaved";
        $rootScope.msg().type = "success";
        $rootScope.msg().show = true;
        //$scope.editForm.$setPristine();
        cleanState();
      });
    };

    $scope.resetDatatype = function() {
      blockUI.start();
      $rootScope.datatype=angular.copy($rootScope.datatypesMap[$scope.datatype.id]);
      cleanState();
      blockUI.stop();
    };

    var cleanState = function() {
      if ($scope.editForm) {
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
      }
    };
//        $scope.datatypesParams = new ngTreetableParams({
//            getNodes: function(parent) {
//                return DatatypeService.getDatatypeNodesInLib(parent, $rootScope.datatype);
//            },
//            getTemplate: function(node) {
//                return DatatypeService.getTemplateINLIB(node, $rootScope.datatype);
//            }
//        });
    $scope.datatypesParams = new ngTreetableParams({
      getNodes: function(parent) {
        return DatatypeService.getNodes(parent, $rootScope.datatype);
      },
      getTemplate: function(node) {
        return DatatypeService.getTemplate(node, $rootScope.datatype);
      }
    });
    $scope.initParams=function(){
      $scope.datatypesParams = new ngTreetableParams({
        getNodes: function(parent) {
          return DatatypeService.getNodes(parent, $rootScope.datatype);
        },
        getTemplate: function(node) {
          return DatatypeService.getTemplate(node, $rootScope.datatype);
        }
      });
    }

    function processEditDataType(data) {
      console.log("dialog not opened");
      //$rootScope.datatype=data;
      $rootScope.datatype = angular.copy(data);
      //$rootScope.datatype =result;
      $rootScope.currentData =$rootScope.datatype;
      $scope.$emit('event:openDatatypeInLib',$rootScope.datatype);
    };

    $scope.editDatatype = function(data) {
      console.log(data);
      // Find share participants
      if (data.shareParticipantIds && data.shareParticipantIds.length > 0) {

        console.log(data.shareParticipantIds);
        var listOfIds= _.map(data.shareParticipantIds, function(element){
          if(element.id){
            return element.id;}
          else if(element.accountId){
            return element.accountId;
          }



        });
        console.log(listOfIds);
        $http.get('api/shareparticipants', { params: { ids: listOfIds} })
          .then(
            function (response) {
              data.shareParticipantIds=angular.fromJson(response.data);
              //Proceed with next
              $scope.editDatatypeNext(data);
            },
            function (error) {
              console.log(error);
            }
          );

      } else {
        $scope.editDatatypeNext(data);
      }

    };

    $scope.editDatatypeNext = function(data) {
      if ($rootScope.hasChanges()) {
        console.log("found changes");

        $rootScope.openConfirmLeaveDlg().then(function() {
          console.log("dialog opened");
          processEditDataType(data);
        });
      } else {
        processEditDataType(data);
      }
    };


      $scope.displayNullView=function () {
          $rootScope.subview= 'Blank.html';

      };


    $scope.selectDatatype = function(datatype) {
      $rootScope.Activate(datatype.id);
      $rootScope.datatype = datatype;
      //$rootScope.datatype =result;
      $rootScope.currentData =$rootScope.datatype;
      $rootScope.subview = "EditDatatypes.html";
      $scope.loadingSelection = true;
      $rootScope.datatype["type"] = "datatype";


      try {
        if ($scope.datatypesParams)
          $scope.datatypesParams.refresh();
        $scope.loadingSelection = false;
      } catch (e) {

      }

      DatatypeService.crossRefInLibrary($rootScope.datatype.id,$rootScope.datatypeLibrary.id).then(function (result) {
        $rootScope.crossRef = result;

      }, function (error) {
        $scope.loadingSelection = false;
        $rootScope.msg().text = error.data.text;
        $rootScope.msg().type = error.data.type;
        $rootScope.msg().show = true;
      });

      $rootScope.$emit("event:initEditArea");


    };



    $scope.copyDatatype = function(datatypeCopy) {

      //$scope.lastExt="1";
      //$scope.lastExt
      var newDatatype = angular.copy(datatypeCopy);

      $scope.getLastExtesion(datatypeCopy);
      newDatatype.ext= $scope.lastExt;
      newDatatype.id = null;
      newDatatype.status = 'UNPUBLISHED';
      newDatatype.scope = $rootScope.datatypeLibrary.scope;
      newDatatype.ext=datatypeCopy.ext+"_"+(Math.floor(Math.random() * 10000000) + 1);
      DatatypeService.save(newDatatype).then(function(savedDatatype) {
        newDatatype = savedDatatype;
        $rootScope.datatypesMap[savedDatatype.id] = savedDatatype;
        $scope.editDatatype(savedDatatype);
        $rootScope.activeModel=savedDatatype.id;
        $rootScope.datatypeLibrary.children.push(createLink(newDatatype));
        $rootScope.datatypes.push(newDatatype);
        $rootScope.datatypes = _.uniq($rootScope.datatypes);
        DatatypeLibrarySvc.save($rootScope.datatypeLibrary);

      });
    };

    function createLink(datatype) {
      return {
        "id": datatype.id,
        "name": datatype.name,
        "ext": datatype.ext
      };
    };

    $scope.deleteDatatype = function(datatype) {
      if (datatype.status === 'PUBLISHED') {
        $scope.preventDeletePublished(datatype);
        console.log("Published");

      } else {
        $scope.confirmDelete(datatype);
      }

    };


    $scope.isAvailableDTForTables = function(dt) {
      if (dt != undefined) {
        if (dt.name === 'IS' || dt.name === 'ID' || dt.name === 'CWE' || dt.name === 'CNE' || dt.name === 'CE') return true;

        if (dt.components != undefined && dt.components.length > 0) return true;

      }
      return false;
    };
    $scope.preventDeletePublished = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'PreventDatatypeDeleteCtl.html',
        controller: 'PreventDatatypeDeleteCtl',
        resolve: {
          datatypeToDelete: function() {
            return datatype;
          }
        }
      });
      modalInstance.result.then(function(datatype) {

      });
    };

    $scope.confirmDelete = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'ConfirmDatatypeDeleteCtl.html',
        controller: 'ConfirmDatatypeDeleteCtl',
        resolve: {
          datatypeToDelete: function() {
            return datatype;
          }
        }
      });
      modalInstance.result.then(function(datatype) {

        console.log(datatype);
        var newLink = angular.fromJson({
          id: datatype.id,
          name: datatype.name,
          ext: datatype.ext
        });
        //$rootScope.datatypeLibrary.children.push(newLink);
        var index = $rootScope.datatypes.indexOf(datatype);
        if (index > -1) {
          $rootScope.datatypes.splice(index, 1);
        }


        DatatypeLibrarySvc.deleteChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
            if(datatype.id==$rootScope.datatype.id){
                $scope.displayNullView();
            }
          if(datatype.status=='PUBLISHED'){
            DatatypeService.delete(datatype);
          }
        });

      });
    };

    $scope.getTableWidth = function() {
      if ($scope.tableWidth === null || $scope.tableWidth == 0) {
        $scope.tableWidth = $("#nodeDetailsPanel").width();
      }
      return $scope.tableWidth;
    };

    $scope.getDynamicWidth = function(a, b, otherColumsWidth) {
      var tableWidth = $scope.getTableWidth();
      if (tableWidth > 0) {
        var left = tableWidth - otherColumsWidth;
        return { "width": a * parseInt(left / b) + "px" };
      }
      return "";
    };

    $scope.isVisible = function(node) {
      var isVis = DatatypeService.isVisible(node);
      return isVis;
    };

    $scope.hasChildren = function(node) {
      //console.log("hasChildren getDatatype=" + $scope.getDatatype(node.datatype.id));
      console.log("node");
      console.log(node);
      return node && node != null && node.datatype && $scope.getDatatype(node.datatype.id) != undefined && $scope.getDatatype(node.datatype.id).components != null && $scope.getDatatype(node.datatype.id).components.length > 0;
    };

    $scope.isChildSelected = function(component) {
      return $scope.selectedChildren.indexOf(component) >= 0;
    };

    $scope.isChildNew = function(component) {
      return component && component != null && component.status === 'DRAFT';
    };

    $scope.recordDatatypeChange = function(type, command, id, valueType, value) {
      var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $rootScope.datatype.id);
      if (datatypeFromChanges === undefined) {
        $rootScope.recordChangeForEdit2(type, command, id, valueType, value);
      }
    };

    $scope.countPredicate = function(position) {
      if (selectedDatatype != null)
        for (var i = 0, len1 = selectedDatatype.predicates.length; i < len1; i++) {
          if (selectedDatatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
            return 1;
        }

      return 0;
    };


    // create a data Library by scope

    $scope.createDatatypeLibrary = function(scope) {
      $rootScope.creatingScope=scope;
      var standardDatatypesInstance = $modal.open({
        templateUrl: 'standardDatatypeDlg.html',
        controller: 'StandardDatatypeLibraryInstanceDlgCtl',
        size:'md',
        resolve: {
          hl7Versions: function() {
            return DatatypeLibrarySvc.getHL7Versions();
          }
        }
      }).result.then(function(standard) {
        $scope.hl7Version = standard.hl7Version;
        DatatypeLibraryDocumentSvc.create(standard.hl7Version, scope, standard.name, standard.ext).then(function(result) {
          console.log(result.data);

          //angular.forEach($scope.datatypeLibrariesConfig, function(lib) {});
          $scope.editLibrary(result.data, false);


        });
      });
    };


    $scope.showEvolution = function(scope) {
      var standardDatatypesInstance = $modal.open({
        templateUrl: 'evolution.html',
        controller: 'evolution',
        size:'lg'

      }).result.then(function() {

      });
    };

    $scope.getDatatype = function(id) {
      return $rootScope.datatypesMap && $rootScope.datatypesMap[id];
    };

    $scope.getNodes = function(parent, root) {
      console.log(root);
      var children = [];
      if (parent && parent != null) {
        if (parent.datatype) {
          var dt = $rootScope.datatypesMap[parent.datatype.id];
          children = dt.components;
        } else {
          children = parent.components;
        }
      } else {
        if (root != null) {
          children = root.components;
        } else {
          children = [];
        }
      }
      console.log(children);

      return children;
    };

    $scope.getEditTemplate = function(node, root) {
      return node.type === 'datatype' ? 'DatatypeLibraryEditTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeLibraryComponentEditTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node, root) ? 'DatatypeLibrarySubComponentEditTree.html' : '';
    };

    $scope.isVisible = function(node) {
      return DatatypeService.isVisible(node);
    };

    $scope.sort = {
      label: function(dt) {
        return $rootScope.getLabel(dt.name, $rootScope.datatypeLibrary.metaData.ext)
      }
    };

    $scope.openStandardDataypes = function(scope) {
      $rootScope.creatingScope=scope;
      var standardDatatypesInstance = $modal.open({
        templateUrl: 'standardDatatypeDlg.html',
        controller: 'StandardDatatypeLibraryInstanceDlgCtl',
        windowClass: 'addDatatype',
        size:'md',
        resolve: {
          hl7Versions: function() {
            return DatatypeLibrarySvc.getHL7Versions();
          }
        }
      }).result.then(function(standard) {
        $scope.hl7Version = standard.hl7Version;
        DatatypeLibraryDocumentSvc.create(standard.hl7Version, scope, standard.name, standard.ext,standard.description,standard.orgName).then(function(result) {
          $scope.datatypeLibsStruct.push(result.data);
          angular.forEach($scope.datatypeLibrariesConfig, function(lib) {
            if (lib.type === scope) {
              $scope.datatypeLibrariesConfig.selectedType = lib;
            }
          });
          $rootScope.subview = "LibraryMetaData.html";
          $scope.editLibrary(result.data, false);
        });
      });
    };
    $scope.preventNullDesc=function(metaData){
      if(!metaData.description){
        metaData.description="";
      }
    }


    $scope.createGeneralDatatypeLibrary = function() {
      var accountId=userInfoService.getAccountID();
      var admin=userInfoService.isAdmin();
      var metaData={
      }
      var datatypeLibrary={};
      if(admin){
        metaData.scope="MASTER"

      }else{
        metaData.scope="USER"
      }
      metaData.name="";
      metaData.description="";
      metaData.orgName="NIST";
      datatypeLibrary.metaData=metaData;
      datatypeLibrary.children=[];
      datatypeLibrary.id=new ObjectId().toString();
      $rootScope.AllUnchanged=[];

      // DatatypeLibraryDocumentSvc.getAllDatatypesNames().then(function(res) {
      //     $rootScope.AllUnchanged = res;
      //
      //     }, function() {});


      DatatypeService.findByScope("INTERMASTER").then(function(response){
        console.log("ALL intermediate");
        $scope.datatypeTemplates=response;
        // $rootScope.AllUnchanged

      });
      $mdDialog.show({

        templateUrl:'createDatatypeLibrary.html',
        scope: $scope,        // use parent scope in template
        preserveScope: true,
        locals: {

          admin:admin,
          datatypeTemplates:$scope.datatypeTemplates,
          metaData:metaData
        },
        controller: CreateDatatypeLibrary
      });
      function CreateDatatypeLibrary($scope, $http, $rootScope, $q, $mdDialog, $timeout, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc,metaData,datatypeTemplates,DatatypeLibraryDocumentSvc,admin) {
        $scope.metaData=metaData;
        $scope.datatypeTemplates=datatypeTemplates;
        $scope.addedDatatypes=[];
        $scope.admin=admin;

        $scope.cancel=function(){
          $mdDialog.hide();
        }

        $scope.tabs_dlg = [{active: true}, {active: false}];
        $scope.make_active_dlg = function(x) {

          for(i=0; i<$scope.tabs_dlg.length;i++){
            if(i==x){
              $scope.tabs_dlg[i].active = true;
            }else{
              $scope.tabs_dlg[i].active=false;
            }
          }
        };

        $scope.addedDatatypes=[];

        $scope.getDatatypeFromUnchanged= function(data1){
          var data= angular.copy(data1);
          var versions= data.versions;
          var version=0;
          console.log("versions ===== data ");
          console.log(versions);
          if(versions.length&&versions.length>0){
            version=versions[versions.length-1];
          }
          var name= data.name;


        };




        $scope.getLastExtesion= function(masterDt){
          var ext=1;
          if(masterDt.hl7versions){
            var version=masterDt.hl7versions[masterDt.hl7versions.length-1];
          }
          DatatypeService.getOneStandard(masterDt.name,version,masterDt.hl7versions).then(function(result) {

            $scope.lastExt=result.ext;
            console.log($scope.lastExt);
          });

        }
        $scope.setVersion=function(v){
          $scope.version1=v;
        }
        $scope.containsCurrentVersion=function(data){
          return data.versions.indexOf($scope.version1) !== -1;
        }
        $scope.AddDatatype = function(datatype) {
          var dataToAdd = angular.copy(datatype);
          dataToAdd.scope=$scope.metaData.scope;
          // dataToAdd.hl7version=null;
          dataToAdd.id = new ObjectId().toString();
          dataToAdd.status = 'UNPUBLISHED';
          $scope.addedDatatypes.push(dataToAdd);


        };

        $scope.existingExtension=function(d,addedDatatypes){
          var version1= d.hl7versions.toString();
          console.log(addedDatatypes);
          $scope.exist=false;
          angular.forEach($scope.addedDatatypes,function(dt){
            var version2= dt.hl7versions.toString();

            console.log(dt.hl7versions);
            console.log(d.hl7versions);

            if(dt.id!==d.id && d.name===dt.name && dt.ext===d.ext && version1==version2){

              console.log("+++++++ found")
              $scope.exist=true;
            }
          });
          return $scope.exist;
        };

        $scope.createLibrary=function(){

          DatatypeLibraryDocumentSvc.create("*", $scope.metaData.scope, $scope.metaData.name, "",$scope.metaData.description,$scope.metaData.orgName).then(function(result) {
            $scope.datatypeLibsStruct.push(result.data);

            angular.forEach($scope.datatypeLibrariesConfig, function(lib) {
              if (lib.type === scope) {
                $scope.datatypeLibrariesConfig.selectedType = lib;
              }
            });
            $rootScope.subview = "LibraryMetaData.html";
            //$scope.datatypeLibsStruct.push(result.data);
            $rootScope.datatypeLibrary=result.data.datatypeLibrary;
            console.log(result.data)
            $scope.datatypeLibMetaDataCopy=angular.copy(result.data.metaData);


            DatatypeLibrarySvc.addChildrenFromDatatypes($rootScope.datatypeLibrary.id, $scope.addedDatatypes).then(function(result){

              console.log(result);
              angular.forEach(result, function(dt){
                console.log(dt);
                //$scope.processAddedDT(dt);
                if(dt.scope=="INTERMASTER"){
                  $rootScope.interMediates.push(dt);
                }else{
                  $rootScope.datatypes.push(dt);
                }


                $rootScope.datatypesMap[dt.id]=dt;
                $rootScope.datatypeLibrary.children.push({name:dt.name,ext:dt.ext,id:dt.id});

              });
            });
            $mdDialog.hide();
            $scope.editLibrary(result.data, false);

            console.log(result.data);

          });
        }


        // DatatypeLibrarySvc.addChildrenFromDatatypes($rootScope.datatypeLibrary.id, results).then(function(result){
        //
        //
        //     angular.forEach(result, function(dt){
        //         console.log(dt);
        //         //$scope.processAddedDT(dt);
        //         if(dt.scope=="INTERMASTER"){
        //             $rootScope.interMediates.push(dt);
        //         }else{
        //             $rootScope.datatypes.push(dt);
        //         }
        //
        //
        //         $rootScope.datatypesMap[dt.id]=dt;
        //         $rootScope.datatypeLibrary.children.push({name:dt.name,ext:dt.ext,id:dt.id});
        //
        //     });
        // });



      }
    }


    $scope.addingToc = [];

    $scope.addDatatypesFromTree = function() {
      $scope.miniDTMap = [];
      $scope.datatypeLibList = [];
      $rootScope.datatype=null;
      $rootScope.subview = "addingViewForMaster.html";

    };

    $scope.containDatatypeWithname = function(datatype) {
      var temp = false;
      if ($scope.addedDatatypes.length > 0) {

        angular.forEach($scope.addedDatatypes, function(flavor) {

          if (flavor.name === datatype.name) {
            temp = true;
          }
        });
      }
      return temp;

    };

    $scope.displayVersion= function(element){

      if(element.scope!=="MASTER"){
        return element.hl7Version;
      }
    };
    $scope.setLibrary = function(library) {
      DatatypeLibrarySvc.getDatatypesByLibrary(JSON.parse(library).id).then(function(result) {
        var dts = [];
        for (var i = 0; i < result.length; i++) {
          if (result[i].status === "PUBLISHED") {
            dts.push(result[i]);
          }
        }
        $scope.datataypestoAdd = dts;
      });
    };
    $scope.openDataypeList = function(hl7Version) {

      var scopes = ['HL7STANDARD'];
      if ($scope.datatypeLibrariesConfig.selectedType === 'MASTER') {
        scopes.push('MASTER');
      } else {
        scopes.push('USER');
      }
      //console.log("openDataypeList scopes=" + scopes.length);
      var datatypesListInstance = $modal.open({
        templateUrl: 'datatypeListDlg.html',
        controller: 'DatatypeListInstanceDlgCtl',
        windowClass: 'addDatatype',
        resolve: {
          hl7Version: function() {
            return $scope.hl7Version;
          },
          datatypeLibsStruct: function() {
            return DatatypeLibrarySvc.getDataTypeLibraryByScopesAndVersion(scopes, $scope.hl7Version);
          }
        }
      }).result.then(function(results) {
        var ids = [];
        angular.forEach(results, function(result) {
          ids.push(result.id);
        });

        DatatypeLibrarySvc.bindDatatypes(ids, $rootScope.datatypeLibrary.id, $rootScope.datatypeLibrary.metaData.ext).then(function(datatypeLinks) {
          var ids = [];
          angular.forEach(datatypeLinks, function(datatypeLink) {
            $rootScope.datatypeLibrary.children.push(datatypeLink);
            ids.push(datatypeLink.id);
          });
          DatatypeService.get(ids).then(function(datatypes) {
            angular.forEach(datatypes, function(datatype) {
              datatype.status = "UNPUBLISHED";
              $scope.datatypeLibCopy.children.push(datatype);
              DatatypeService.collectDatatypes(datatype.id).then(function(datatypes) {
                angular.forEach(datatypes, function(dt) {
                  if (!_.includes(dt.libIds, $rootScope.datatypeLibrary.id)) {
                    dt.libIds.push($rootScope.datatypeLibrary.id);
                  }
                  if ($rootScope.datatypesMap[dt.id] === null || $rootScope.datatypesMap[dt.id] === undefined) {
                    $rootScope.datatypesMap[dt.id] = dt;
                    $scope.added.push(dt.id);
                  };
                  var exists2 = _.find($scope.DataTypeTree[0].children, 'id', dt.id);
                  if (exists2 === undefined) {
                    $scope.DataTypeTree[0].children.push(dt);
                  }
                });
                //            //console.log("$scope.DataTypeTree=" + JSON.stringify($scope.DataTypeTree, null, 2));
                DatatypeService.saveAll(datatypes);
              });
            });

          });
        });
      });
    };



    $scope.abortPublish = function(datatype) {
      var modalInstance = $modal.open({
        templateUrl: 'AbortPublishCtl.html',
        controller: 'AbortPublishCtl',
        resolve: {
          datatypeToPublish: function() {
            return datatype;
          },
          unpublishedDatatypes: function() {
            return $scope.unpublishedDatatypes;
          },
          unpublishedTables: function() {
            return $scope.unpublishedTables;
          }

        }
      });

    };

    $scope.confirmPublish = function(datatypeCopy) {
      var modalInstance = $mdDialog.show({
        templateUrl: 'ConfirmDatatypePublishCtlMd.html',
        controller: 'ConfirmDatatypePublishCtlMd',
        locals: {
          datatypeToPublish: datatypeCopy
        }

      });
      modalInstance.then(function(datatypetoPublish) {
        console.log("Saving");
        console.log($rootScope.datatype);
        console.log("IN LIBRARY");
        console.log($rootScope.datatypeLibrary);

        var ext = $rootScope.datatype.ext;

        DatatypeService.publish($rootScope.datatype).then(function(result) {

          var oldLink = DatatypeLibrarySvc.findOneChild(result.id, $rootScope.datatypeLibrary.children);
          var newLink = DatatypeService.getDatatypeLink(result);
          newLink.ext = ext;
          DatatypeLibrarySvc.updateChild($rootScope.datatypeLibrary.id, newLink).then(function(link) {
            DatatypeService.merge($rootScope.datatypesMap[result.id], result);
            $rootScope.datatypesMap[result.id].status="PUBLISHED";
            $rootScope.datatype.status="PUBLISHED";

            if ($scope.editForm) {
              console.log("Cleeaning");
              $scope.editForm.$setPristine();
              $scope.editForm.$dirty = false;
              $scope.editForm.$invalid = false;

            }
            $rootScope.clearChanges();
            DatatypeService.merge($rootScope.datatype, result);
              if ($scope.datatypesParams){
              $scope.datatypesParams.refresh();
            }
            VersionAndUseService.findById(result.id).then(function(inf){
              $rootScope.versionAndUseMap[inf.id]=inf;
              if($rootScope.versionAndUseMap[inf.sourceId]){
                $rootScope.versionAndUseMap[inf.sourceId].deprecated=true;
              }

            });
            oldLink.ext = newLink.ext;
            oldLink.name = newLink.name;
            $scope.saving = false;
            //$scope.cleanState();
          }, function(error) {
            $scope.saving = false;
            $rootScope.msg().text = "Sorry an error occured. Please try again";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
          });
        }, function(error) {
          $scope.saving = false;
          $rootScope.msg().text = "Sorry an error occured. Please try again";
          $rootScope.msg().type = "danger";
          $rootScope.msg().show = true;
        });
      });
    };

    $scope.selectTable = function(t) {
      $rootScope.Activate(t.id);
      var table = angular.copy(t);
      if ($scope.viewSettings.tableReadonly || table.status == 'PUBLISHED') {
        $rootScope.subview = "ReadValueSets.html";
      } else {
        $rootScope.subview = "EditValueSets.html";
      }
      $scope.loadingSelection = true;
      blockUI.start();
      $rootScope.references = [];
      angular.forEach($rootScope.datatypes, function(dt) {
        console.log(dt);
        console.log($rootScope.getDatatypeLabel(dt));
        $rootScope.findTableRefsINLIB(table, dt, $rootScope.getDatatypeLabel(dt));
      });

      try {
        TableService.getOne(table.id).then(function(tbl) {
          $rootScope.table = tbl;
          // $rootScope.table.smallCodes = $rootScope.table.codes.slice(0,1000);
          $rootScope.currentData = $rootScope.table;
          // $rootScope.codeSystems = [];
          // for (var i = 0; i < $rootScope.table.codes.length; i++) {
          //   if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
          //     if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
          //       $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
          //     }
          //   }
          // }
          $rootScope.references = [];
          angular.forEach($rootScope.datatypes, function(dt) {
            $rootScope.findTableRefs($rootScope.table, dt, $rootScope.getDatatypeLabel(dt), dt);
          });
          $scope.loadingSelection = false;
          $rootScope.$emit("event:initEditArea");
          blockUI.stop();
        }, function(errr) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = errr.data.text;
          $rootScope.msg().type = errr.data.type;
          $rootScope.msg().show = true;
          blockUI.stop();
        });
      } catch (e) {
        $scope.loadingSelection = false;
        $rootScope.msg().text = "An error occured. DEBUG: \n" + e;
        $rootScope.msg().type = "danger";
        $rootScope.msg().show = true;
        blockUI.stop();
      }
    };


    $scope.navToLibrary = function(library) {
      if(library.accountId.toString()===$scope.accountId){
        $scope.editLibrary(library,false);
      }else{
        $scope.editLibrary(library,true);
      }
    };

  });
