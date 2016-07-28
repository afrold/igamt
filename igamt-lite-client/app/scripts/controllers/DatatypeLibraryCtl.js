/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('DatatypeLibraryCtl',
    function($scope,$http, $rootScope, $q, $modal, $timeout, TableService, ngTreetableParams,DatatypeLibraryDocumentSvc, TableLibrarySvc, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, IGDocumentSvc, TableService, ViewSettings, userInfoService) {
	
		$scope.datatypeLibsStruct = [];
        $scope.editableDTInLib = '';
        $scope.editableVS = '';
        $scope.derivedDatatypes = [];
        $scope.derivedTables = [];
        $scope.toShow === "";
        $rootScope.datatypesMap = {};
        $scope.tablesMap = {};
        $scope.tablesIds = [];
        $scope.datatypeLibrary = null;
        $scope.datatypeLibMetaDataCopy = null;
        $scope.datatypeStruct = null;
        $scope.datatype = null;
        $scope.loadingSelection = true;
        $scope.publishSelections = [];
        $scope.datatypeDisplay = [];
        $scope.selectedChildren = [];
        $scope.viewSettings = ViewSettings;
        $scope.editView = null;
        $scope.datatypeListView = null;
        $scope.added = [];
        $scope.accordi = { metaData: false, definition: true, dtList: true, dtDetails: false };
        $scope.forms = {};
        //      $scope.forms.datatypeForm = {};
        $scope.tableWidth = null;
        // $scope.datatypeLibrary = "";
        $scope.hl7Version = null;
        $scope.scopes = [];
        $scope.tableCollapsed=false;
        $scope.datatypeLibrariesConfig = {};
        //$scope.datatypeLibrariesConfig.selectedType
        $scope.admin = true; // userInfoService.isAdmin();
        $scope.toggle = function(param) {
            $scope.toShow = param;

        }
        $scope.datatypeSource=null;
        $scope.tableSource=null;

        // $scope.datatypesParams = new ngTreetableParams({
        //     getNodes: function(parent) {
        //         return $scope.getNodes(parent, $scope.datatype);
        //     },
        //     getTemplate: function(node) {
        //         return $scope.getEditTemplate(node, $scope.datatype);
        //     }
        // });

        $scope.collapseTables= function(){
        	$scope.tableCollapsed = !$scope.tableCollapsed;
        	
        }
        $scope.datatypesParams = new ngTreetableParams({
            getNodes: function(parent) {
                return DatatypeService. getDatatypeNodesInLib(parent, $scope.datatype);
            },
            getTemplate: function(node) {
                return DatatypeService.getTemplateINLIB(node, $scope.datatype);
            }
        });

        $scope.datatypeLibraryTypes = [{
            name: "Browse Master data type libraries",
            type: 'MASTER',
            visible: $scope.admin,
        }];

        $scope.initDatatypeLibrary = function() {
            IGDocumentSvc.loadIgDocumentMetaData();
            $scope.start = false;
        };


        $scope.$watch(
            function() {
                return $scope.forms.editForm != undefined && $scope.forms.editForm.$dirty;
            },
            function handleFormState(newValue) {
                if (newValue) {
                    $rootScope.recordChanged();
                } else {
                    $rootScope.clearChanges();
                }
            }
        );

        $scope.seq = function(idx) {
            return idx + 1;
        };

        $scope.selectDTLibraryType = function(selectedType) {
            $scope.datatypeLibrariesConfig.selectedType = selectedType;
            getDataTypeLibraryByScope(selectedType);
        };

        $scope.getDatatypes = function(datatypeLibrary) {
            $scope.datatypeListView = "DatatypeList.html";
            $scope.loadingSelection = true;
            $timeout(
                function() {
                    $scope.loadingSelection = false;
                }, 100);
        };

        function getDataTypeLibraryByScope(scope) {
        	$scope.datatypeLibsStruct=[];
            DatatypeLibraryDocumentSvc.getDataTypeLibraryDocumentByScope(scope).then(function(data) {
            	$scope.datatypeLibsStruct=[];
            	angular.forEach(data.data, function(lib) {
                 $scope.datatypeLibsStruct.push(lib);
            });
                //$scope.datatypeLibsStruct=data.data;
                console.log($scope.datatypeLibsStruct);
                $scope.accordi.dtDetails = false;
                $rootScope.isEditing = false;
                $scope.DataTypeTree = [];
                $scope.datatypeLibCopy = {};
                //console.log("$scope.datatypeLibsStruct size=" + $scope.datatypeLibsStruct.length);
            }).catch(function(error) {
                //console.log(error);
            });
        };

        function getDataTypeLibraryByScopesAndVersion(scopes, hl7Version) {
            DatatypeLibraryDocumentSvc.getDataTypeLibraryDocumentByScopesAndVersion(scopes, hl7Version).then(function(data) {
                $scope.datatypeStruct = data;
                if (!$scope.datatypeStruct.scope) {
                    $scope.datatypeStruct.scope = scope;
                }
                //console.log("$scope.datatypeStruct.id=" + $scope.datatypeStruct.id + " $scope.datatypeStruct.scope=" + $scope.datatypeStruct.scope);
            }).catch(function(error) {
                //console.log(error);
            });
        };

        $scope.saveMetaData = function() {
            $scope.datatypeLibrary.metaData = angular.copy($scope.datatypeLibMetaDataCopy);
            DatatypeLibrarySvc.saveMetaData($scope.datatypeLibrary.id, $scope.datatypeLibMetaDataCopy);
            $scope.forms.editForm.$setPristine();
        };

        $scope.resetMetaData = function() {
            //console.log("b=" + $scope.datatypeLibMetaDataCopy.name);
            //console.log("b=" + JSON.stringify($scope.datatypeLibrary.metaData.name));
            $scope.datatypeLibMetaDataCopy = angular.copy($scope.datatypeLibrary.metaData);

            $scope.DataTypeTree[0].metaData = $scope.datatypeLibMetaDataCopy;
            //console.log("a=" + $scope.datatypeLibMetaDataCopy.name);
            //console.log("a=" + JSON.stringify($scope.datatypeLibrary.metaData.name));
            $scope.forms.editForm.$setPristine();
        };

        $scope.editMetadata = function() {
            $scope.datatypeLibMetaDataCopy = $scope.DataTypeTree[0].metaData;
            $scope.editView = "LibraryMetaData.html";
        }

        $scope.editLibrary = function(datatypeLibraryDocument) {
        	$scope.datatypes=[];
        	$rootScope.datatypesMap={};
        	$scope.derivedTables=[];
        	console.log(datatypeLibraryDocument);
        	$scope.tablesIds=[];
        	$scope.datatypeLibrary=datatypeLibraryDocument.datatypeLibrary;
        	$scope.datatypesIds=[];
            angular.forEach($scope.datatypeLibrary.children, function(datatypeLink) {
            	$scope.datatypesIds.push(datatypeLink.id);
            });
        	
            DatatypeService.get($scope.datatypesIds).then(function(result){
            	console.log("==========Adding Datatypes from their IDS============");
            	$scope.datatypes=result;
            	console.log(result);
                angular.forEach(result, function(datatype) {
                	$rootScope.datatypesMap[datatype.id]=datatype;
                });
            }); 
            
        	//$scope.getDatatypesFromIds();
        	
        	$scope.tableLibrary=datatypeLibraryDocument.tableLibrary;
            angular.forEach($scope.tableLibrary.children, function(table) {
            	$scope.tablesIds.push(table.id);
            });
 
            $scope.editView = "LibraryMetaData.html";
            $scope.addedDatatypes = [];
            $scope.datataypestoAdd = [];
            $scope.hl7Version = datatypeLibraryDocument.metaData.hl7Version;
            var scopes = ['HL7STANDARD'];
            
            DatatypeService.getDataTypesByScopesAndVersion(scopes, $scope.hl7Version).then(function(result) {
                $scope.datataypestoAdd = result;
            });
            
            $scope.datatypeListView = "DatatypeList.html";
            $scope.loadingSelection = true;
                $rootScope.isEditing = true;
                $scope.accordi.dtDetails = true;
                $scope.hl7Version = $scope.datatypeLibrary.metaData.hl7Version;
                $scope.datatypeLibraryId=$scope.datatypeLibrary.id;
                $rootScope.currentLibVersion = $scope.hl7Version;
                $scope.datatypeLibMetaDataCopy = angular.copy($scope.datatypeLibrary.metaData);
                $scope.loadingSelection = false;
                $scope.DataTypeTree = [];
                $scope.datatypeLibCopy = angular.copy($scope.datatypeLibrary);
                $scope.datatypeLibCopy.children = [];
                $rootScope.datatypesMap = {};
                angular.forEach($scope.datatypes, function(child) {
                    this[child.id] = child;
                    $scope.getDerived(child);
                }, $rootScope.datatypesMap);

                $scope.datatypes=_.uniq($scope.datatypes);
                $scope.addAllTables().then(function(result){
                }); 
                $scope.DataTypeTree.push($scope.datatypeLibCopy);
                console.log("========================Datatypes+++++++++++++++++++++++++++++++++++")
                console.log($scope.datatypes);
        };
        
        $scope.getDatatypesFromIds= function(){
        	$scope.datatypesIds=[];
            angular.forEach($scope.datatypeLibrary.children, function(datatypeLink) {
            	$scope.datatypesIds.push(datatypeLink.id);
            });
        	
            DatatypeService.get($scope.datatypesIds).then(function(result){
            	console.log("==========Adding Datatypes from their IDS============");
            	$scope.datatypes=result;
            	console.log(result);
                angular.forEach(result, function(datatype) {
                	$rootScope.datatypesMap[datatype.id]=datatype;
                });
            }); 
        }
        
        
        
        $scope.startCallback = function(event, ui, title) {
            console.log('You started draggin:');
            console.log(title);
            $scope.draged=title;
        };


        $scope.dropCallback = function(event, ui) {
            var index = $scope.addedDatatypes.indexOf($scope.draged);
            if (index > -1) {
               $scope.addedDatatypes.splice(index, 1);
            }
            $scope.addedItem =angular.copy($scope.draged);
            var randext = $scope.datatypeLibMetaDataCopy.ext+Math.floor(Math.random() * 100);
            $scope.addedItem.id = new ObjectId().toString();
            $scope.addedItem.ext=randext;
            $scope.addedItem.scope='MASTER';
            $scope.addedItem.status='UNPUBLISHED';
            $scope.addedItem.libIds=[$scope.datatypeLibrary.id];
            $scope.addedDatatypes.push($scope.addedItem);
        };

        $scope.submitAdded= function(){

          angular.forEach($scope.addedDatatypes, function(datatype) {
             	$rootScope.datatypesMap[datatype.id]=datatype;
             	if($rootScope.datatypesMap[datatype.id] !==undefined){
            	$scope.datatypes.push(datatype);
            	$scope.datatypes=_.uniq($scope.datatypes);
            	$scope.getDerived(datatype);
                var newLink = angular.fromJson({
                    id: datatype.id,
                    name:datatype.name,
                    ext:datatype.ext
                });
                DatatypeService.save(datatype).then(function (result){

                    DatatypeLibrarySvc.addChild($scope.datatypeLibrary.id, newLink).then(function (link) {

                          }, function (error) {
                          $rootScope.saving = false;
                          $rootScope.msg().text = error.data.text;
                          $rootScope.msg().type = error.data.type;
                          $rootScope.msg().show = true;
                       });
                  }, function (error) {
                      $rootScope.saving = false;
                      $rootScope.msg().text = error.data.text;
                      $rootScope.msg().type = error.data.type;
                      $rootScope.msg().show = true;
                   });
             }  
         });
        
          console.log("My datatypeLoibrary ");
          console.log($scope.datatypeLibrary);
          $scope.addedDatatypes=[];
        };

        
        $scope.getDerived = function(element) {
            try {
                if (element && element.type && element.type === "datatype") {

                    angular.forEach(element.components, function(component) {
                        $scope.getDerived(component);
                    });
                } else if (element && element.type && element.type === "component") {

                    if (element.table != null) {
                    	//add child to librar
                        $scope.tablesIds.push(element.table.id);
                        $scope.tablesIds=_.uniq($scope.tablesIds);
                        $scope.addTable(element.table).then(function(result){
                            });

                    }
                    if (element.datatype != null || element.datatype != undefined) {
                        var newLink = angular.fromJson({
                            id: element.datatype.id,
                            name:element.datatype.name,
                            ext:element.datatype.ext
                        });

                        if ($rootScope.datatypesMap[element.datatype.id] === undefined) {
                        	console.log("======adding datatype======");
                        	console.log(element.datatype);
                        	$scope.getDatatypeById(element.datatype.id).then(function(result){
                        		$scope.datatypes.push(result);
                                $scope.datatypes.push(result);
                                $scope.datatypes=_.uniq($scope.datatypes);
                                $rootScope.datatypesMap[element.datatype.id]=result;
                                $scope.getDerived(result);
                                    DatatypeLibrarySvc.addChild($scope.datatypeLibrary.id, newLink).then(function (link) {

                                    }, function (error) {
                                    $rootScope.saving = false;
                                    $rootScope.msg().text = error.data.text;
                                    $rootScope.msg().type = error.data.type;
                                    $rootScope.msg().show = true;
                                 });
                                  
                        	});
                            
                        }else{
                        }

                    }

                }

            } catch (e) {
                throw e;
            }
        };
        
        
        
        $scope.filterByDatatype = function(datatype){
        	$scope.tablesIds=[];
        	$scope.getDerived(datatype);
        	$scope.addAllTables();
        	
        }
        
        $scope.deleteValueSetINLIB = function (table) {
            $rootScope.references = [];
            console.log($scope.datatypes);
           angular.forEach($scope.datatypes, function (dt) {
        	   console.log("=======================dddddd")
        	   console.log(dt);
        	   console.log($rootScope.getDatatypeLabel(dt));
        	   $rootScope.findTableRefsINLIB(table, dt, $rootScope.getDatatypeLabel(dt));
           });
           console.log($rootScope.references);
           if ($rootScope.references != null && $rootScope.references.length > 0) {
               abortValueSetDelete(table);
           } else {
               confirmValueSetDelete(table);
           }
    }
        
        function abortValueSetDelete(table) {
            var modalInstance = $modal.open({
                templateUrl: 'ValueSetReferencesCtrl.html',
                controller: 'ValueSetReferencesCtrl',
                resolve: {
                    tableToDelete: function () {
                        return table;
                    }
                }
            });
            modalInstance.result.then(function (table) {
                // $rootScope.tableToDelete = table;
            }, function () {
            });
        };

        function confirmValueSetDelete(table) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmValueSetDeleteCtrl.html',
                controller: 'ConfirmTablesDeleteCtl',
                resolve: {
                    tableToDelete: function () {
                        return table;
                    }
                }
            });
            modalInstance.result.then(function (table) {
//                tableToDelete = table;
                var newLink = {};
                newLink.bindingIdentifier = table.bindingIdentifier;
                newLink.id = table.id;
                if($scope.derivedTables && $scope.derivedTables != null) {
                	console.log("dddddddddddddddddddddd");
                	console.log(table);
                    var index = $scope.derivedTables.indexOf(table);
                    if(index >= 0)
                    $scope.derivedTables.splice(index, 1);
                }
            	
                TableLibrarySvc.deleteChild($scope.tableLibrary.id, newLink.id).then(function (link) {
                	console.log("table deleted");

                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });

      

            }, function () {
            });
        };
        
        
        $scope.addDatatypetoLibrary= function(datatype){
            var newLink = angular.fromJson({
                id: datatype.id,
                name:datatype.name,
                ext:datatype.ext
            });
            $scope.datatypeLibrary.children.push(newLink);
            DatatypeService.save(datatype).then(function (result){
              DatatypeLibrarySvc.addChild($scope.datatypeLibrary.id, newLink).then(function (link) {
                    }, function (error) {
                    $rootScope.saving = false;
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                 });
            }, function (error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
             });
        }
        
        $scope.addHL7DatatypetoLibrary= function(datatype){
            var newLink = angular.fromJson({
                id: datatype.id,
                name:datatype.name,
                ext:datatype.ext
            });
            $scope.datatypeLibrary.children.push(newLink);

              DatatypeLibrarySvc.addChild($scope.datatypeLibrary.id, newLink).then(function (link) {
                    }, function (error) {
                    $rootScope.saving = false;
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                 });
        }

        $rootScope.findTableRefsINLIB = function (table, obj, path) {
            if (angular.equals(obj.type, 'field') || angular.equals(obj.type, 'component')) {
                if (obj.table != undefined) {
                    if (obj.table.id === table.id) {
                    	
                        var found = angular.copy(obj);
                        console.log(found);
                        found.path = path;
                        $rootScope.references.push(found);
                    }
                }
                if(obj.datatype!==null){
                	console.log($rootScope.datatypesMap);
                	console.log(" I AM HERE ")
                	console.log(obj.datatype.id);
                	console.log($rootScope.datatypesMap[obj.datatype.id]);
                	$rootScope.findTableRefsINLIB(table, $rootScope.datatypesMap[obj.datatype.id], path);
                }
                //
            } else if (angular.equals(obj.type, 'datatype')) {
                if (obj.components != undefined && obj.components != null && obj.components.length > 0) {
                    angular.forEach(obj.components, function (component) {
                    	 $rootScope.findTableRefsINLIB(table, component, path + "." + component.position);
                    });
                }
            }
        };
        
        $scope.getReferencesInLIB = function(table,element,path) {
            try {
                if (element && element.type && element.type === "datatype") {
                	console.log()
                    angular.forEach(element.components, function(component) {
                    	 $scope.getReferencesInLIB(table,component,path);
                    });
                } else if (element && element.type && element.type === "component") {
                    if (element.table === table.id) {
                    	console.log("===================")
                        var found = angular.copy(element);
                        found.path = path;
                        $scope.references.push(found);
                    }
                    if (element.datatype != null || element.datatype != undefined) {
                        $scope.derivedDatatypes.push($rootScope.datatypesMap[element.datatype.id]); 
                        $scope.getDerived($rootScope.datatypesMap[element.datatype.id]);
                        if ($rootScope.datatypesMap[element.datatype.id] === undefined) {
                        }
                    }
                }

            } catch (e) {
                throw e;
            }
        };
        
        $scope.loadDatatypes = function(libID) {
            var delay = $q.defer();
            DatatypeLibrarySvc.getDatatypesByLibrary(libID).then(function(children) {
            	console.log(children);
                $rootScope.datatypesMap = {};
                angular.forEach(children, function(child) {
                	
                    this[child.id] = child;
                }, $rootScope.datatypesMap);
                delay.resolve(true);
            }, function(error) {
                $rootScope.msg().text = "DatatypesLoadFailed";
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                delay.reject(false);

            });
            return delay.promise;
        };

        $scope.addAllTables = function() {
            console.log($scope.tablesIds);
            var delay = $q.defer();
            TableService.findAllByIds($scope.tablesIds).then(function(tables) {
                angular.forEach(tables, function(child) {
                    this[child.id] = child;
                }, $scope.tablesMap);
                
                
                $scope.derivedTables= tables;
                    $scope.derivedTables.forEach(function(table, i) {
                        var newLink = {};
                        newLink.bindingIdentifier = table.bindingIdentifier;
                        newLink.id = table.id;

                        TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function (link) {
                            $scope.tableLibrary.children.splice(0, 0, newLink);
                        }, function (error) {
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
        
        
        $scope.addTable = function(tableLink) {
            var tableId=[];
            tableId.push(tableLink.id);
            var delay = $q.defer();
            TableService.findAllByIds(tableId).then(function(tables) {
                angular.forEach(tables, function(child) {
                    this[child.id] = child;
                    $scope.derivedTables.push(child);
                }, $scope.tablesMap);
                
                
                $scope.derivedTables=_.unique($scope.derivedTables);
                    $scope.derivedTables.forEach(function(table, i) {
                        var newLink = {};
                        newLink.bindingIdentifier = table.bindingIdentifier;
                        newLink.id = table.id;

                        TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function (link) {
                            $scope.tableLibrary.children.splice(0, 0, newLink);
                        }, function (error) {
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
        
        
        $scope.editTableINLIB= function(table){
        	$rootScope.table = table;
        	$scope.editView="editValueSetINLIB.html";
   
        }
        
        $scope.createNewExtension = function (ext) {
            if ($scope.datatypeLibrary != null) {
                var rand = (Math.floor(Math.random() * 10000000) + 1);
                if ($scope.datatypeLibrary.metaData["ext"] === null) {
                    return ext != null && ext != "" ? ext + "_" + rand : rand;
                } else {
                    return  ext != null && ext != "" ? ext + "_" + $scope.datatypeLibrary.metaData["ext"] + "_" + rand + 1 : rand + 1;
                }
            } else {
                return null;
            }
        };
        $scope.copyTableINLIB = function (table) {
            var newTable = angular.copy(table);
            newTable.participants = [];
            newTable.scope = 'MASTER';
            newTable.id = null;
            newTable.libIds = [];
            newTable.libIds.push($scope.tableLibrary.id);
            newTable.bindingIdentifier = $scope.createNewExtension(table.bindingIdentifier);
           

            if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
                for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
                    newTable.codes[i].id = new ObjectId().toString();
                }
            }

            TableService.save(newTable).then(function (result) {
                newTable = result;
                console.log(result);
                $scope.tablesIds.push(result.id);
                var newLink = angular.copy(TableLibrarySvc.findOneChild(table.id, $scope.tableLibrary.children));
                newLink.bindingIdentifier = newTable.bindingIdentifier;
                newLink.id = newTable.id;

                TableLibrarySvc.addChild($scope.tableLibrary.id, newLink).then(function (link) {
                    $scope.tableLibrary.children.splice(0, 0, newLink);
                    $scope.derivedTables.splice(0, 0, newTable);
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


                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });


            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };


        $scope.selectDT = function() {
            $scope.DTselected = true;


        };
        $scope.applyDTLIB = function(field, datatype) {
            field.datatype.ext = JSON.parse(datatype).ext;
            field.datatype.id = JSON.parse(datatype).id;
            field.datatype.label = JSON.parse(datatype).label;
            field.datatype.name = JSON.parse(datatype).name;
            //$scope.setDirty();
            // $rootScope.processElement(field);

            if ($scope.datatypesParams)
                $scope.datatypesParams.refresh();
            $scope.editableDTInLib = '';
            $scope.DTselected = false;

        };

         $scope.redirectDTLIB  = function(datatype) {
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
                    $scope.datatype=datatype;
                    $scope.editDatatype($scope.datatype);
                    $scope.Activate($scope.datatype.id);

                });
            });
        };


        $scope.getDatatypeById=function(id) {
            var delay = $q.defer();
            if ($rootScope.datatypesMap[id] === undefined || $rootScope.datatypesMap[id] === null) {
                $http.get('api/datatypes/' + id).then(function(response) {
                    var datatype = angular.fromJson(response.data);
                    $rootScope.datatypesMap[id]=datatype;
                    delay.resolve(datatype);
                }, function(error) {
                    delay.reject(error);
                });
            } else {
                delay.resolve($rootScope.datatypesMap[id]);
            }
            return delay.promise;
        }
        





        $scope.editDTLIB = function(field) {
            console.log("looking for flavor");
            //$scope.editableDTInLib = field.id;

            $scope.loadLibrariesByFlavorName = function() {
                var delay = $q.defer();
                $scope.ext = null;
                $scope.results = [];
                $scope.tmpResults = [];
                $scope.results = $scope.results.concat(filterFlavors($scope.datatypeLibrary, field.datatype.name));
                $scope.tmpResults = [].concat($scope.results);
                DatatypeLibrarySvc.findLibrariesByFlavorName(field.datatype.name, 'HL7STANDARD', $scope.hl7Version).then(function(libraries) {
                    if (libraries != null) {
                        _.each(libraries, function(library) {
                            $scope.results = $scope.results.concat(filterFlavors(library, field.datatype.name));
                        });
                    }

                    $scope.results = _.uniq($scope.results, function(item, key, a) {
                        return item.id;
                    });
                    $scope.tmpResults = [].concat($scope.results);

                    delay.resolve(true);
                }, function(error) {
                    $rootScope.msg().text = "Sorry could not load the data types";
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                    delay.reject(error);
                });
                return delay.promise;
            };


            var filterFlavors = function(library, name) {
                var results = [];
                _.each(library.children, function(link) {
                    if (link.name === name) {
                        link.libraryName = library.metaData.name;
                        link.hl7Version = library.metaData.hl7Version;
                        results.push(link);
                    }
                });
                return results;
            };




            $scope.loadLibrariesByFlavorName().then(function(done) {
                console.log($scope.results);
            });
        };
        

        $scope.backDT = function() {
            $scope.editableDTInLib = '';
        };


        $scope.addPredicate = function(node) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateDatatypeLibraryCtrl.html',
                controller: 'PredicateDatatypeLibraryCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function() {
                        return node;
                    },
                    selectedDatatype: function() {
                        return $scope.datatype;
                    }
                }
            });
            modalInstance.result.then(function(node) {
                $scope.selectedNode = node;
            }, function() {});
        };
        $scope.copyLibrary = function(datatypeLibrary) {
            var newDatatypeLibrary = angular.copy(datatypeLibrary);
            newDatatypeLibrary.id = null;
            newDatatypeLibrary.metaData.ext = newDatatypeLibrary.metaData.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
            newDatatypeLibrary.accountId = userInfoService.getAccountID();
            DatatypeLibrarySvc.save(newDatatypeLibrary).then(function(response) {
                $scope.datatypeLibsStruct.push(newDatatypeLibrary);
            });
        };

        $scope.deleteLibrary = function(datatypeLibrary) {
            $scope.confirmLibraryDelete(datatypeLibrary);
        };

        $scope.confirmLibraryDelete = function(datatypeLibrary) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeLibraryDeleteCtrl.html',
                controller: 'ConfirmDatatypeLibraryDeleteCtrl',
                resolve: {
                    datatypeLibraryToDelete: function() {
                        return datatypeLibrary;
                    }
                }
            });
            modalInstance.result.then(function(datatypeLibraryDocument) {
                DatatypeLibraryDocumentSvc.delete(datatypeLibraryDocument.id).then(function(result) {
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
            });
        };

        $scope.toggleStatus = function(status) {
            $scope.datatype.status = $scope.datatype.status === 'PUBLISHED' ? 'UNPUBLISHED' : 'PUBLISHED';
        };

        $scope.saveDatatype = function(datatypeCopy) {
            //console.log("save datatypeForm=" + $scope.forms.editForm);
            $scope.datatypeLibrary = angular.copy(datatypeCopy);
            DatatypeService.save($scope.datatypeLibrary).then(function(result) {
                $rootScope.msg().text = "datatypeSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                $scope.forms.editForm.$setPristine();
            });
        };

        $scope.resetDatatype = function(datatypeCopy) {
            $scope.forms.editForm.$setPristine();
            $scope.datatype = angular.copy($scope.datatypeLibrary);
            $rootScope.clearChanges();
            if ($scope.datatypesParams) {
                $scope.datatypesParams.refresh();
            }
            $scope.forms.editForm.$setPristine();
        };

        $scope.editDatatype = function(datatype) {

            $scope.datatype = datatype;
            console.log("$scope.datatype");
            console.log($scope.datatype);
            $scope.editView = "EditDatatypeLibraryDatatype.html";
            $scope.loadingSelection = true;
            $scope.added = [];
            
            if ($scope.datatypesParams) {
                $scope.datatypesParams.refresh();
            }



            console.log($scope.datatype.components);
            $scope.tableWidth = null;
            $scope.scrollbarWidth = $scope.getScrollbarWidth();
            $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
            $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
            $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
            console.log($scope.datatypesParams);
            console.log($scope.datatype.components);



            $scope.loadingSelection = false;
            if ($scope.datatypeStruct) {
                $scope.datatypeLibrariesConfig.selectedType = 'USER';
            }
        };
       
        $scope.copyDatatype = function(datatypeCopy) {
            var newDatatype = angular.copy(datatypeCopy);
            newDatatype.ext = newDatatype.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
            newDatatype.id = null;
            newDatatype.status = 'UNPUBLISHED';
            newDatatype.scope='MASTER';
            DatatypeService.save(newDatatype).then(function(savedDatatype) {
                newDatatype = savedDatatype;
                $scope.datatypeLibrary.children.push(createLink(newDatatype));
                $scope.datatypes.push(newDatatype);
                $scope.datatypes=_.uniq($scope.datatypes);
            });
            DatatypeLibrarySvc.save($scope.datatypeLibrary);
        };

        function createLink(datatype) {
            return {
                "id": datatype.id,
                "name": datatype.name,
                "ext": datatype.ext
            };
        };

        $scope.deleteDatatype = function(datatype) {
        	if(datatype.status === 'PUBLISHED'){
    			$scope.preventDeletePublished(datatype);
    			console.log("Published");
    			
    		}else{
            	$scope.confirmDelete(datatype);
    		}

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
                var newLink = angular.fromJson({
                    id: datatype.id,
                    name:datatype.name,
                    ext:datatype.ext
                });
                //$scope.datatypeLibrary.children.push(newLink);
                var index = $scope.datatypes.indexOf(datatype);
                if (index > -1) {
                	$scope.datatypes.splice(index, 1);
                }
                	DatatypeService.delete(datatype);
                	DatatypeLibrarySvc.deleteChild($scope.datatypeLibrary.id, newLink).then(function (link) {});
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
            var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $Scope.datatype.id);
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

        $scope.showSelectDatatypeFlavorDlg = function(component) {
            var modalInstance = $modal.open({
                templateUrl: 'SelectDatatypeFlavor.html',
                controller: 'SelectDatatypeFlavorCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    currentDatatype: function() {
                        return selectedDatatypesMap[component.datatype.id];
                    },
                    hl7Version: function() {
                        return $rootScope.igdocument.metaData.hl7Version;
                    },
                    datatypeLibrary: function() {
                        return $rootScope.igdocument.profile.datatypeLibrary;
                    }
                }
            });
            modalInstance.result.then(function(datatype, ext) {
                component.datatype.id = datatype.id;
                MastermapSvc.addDatatypeObject(datatype, [component.id, component.type]);
                if ($scope.datatypesParams)
                    $scope.datatypesParams.refresh();
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
                return $rootScope.getLabel(dt.name, $scope.datatypeLibrary.metaData.ext)
            }
        };

        $scope.openStandardDataypes = function(scope) {
            var standardDatatypesInstance = $modal.open({
                templateUrl: 'standardDatatypeDlg.html',
                controller: 'StandardDatatypeLibraryInstanceDlgCtl',
                windowClass: 'app-modal-window',
                resolve: {
                    hl7Versions: function() {
                        return DatatypeLibrarySvc.getHL7Versions();
                    }
                }
            }).result.then(function(standard) {
                //console.log("hl7Version=" + standard.hl7Version + " name=" + standard.name + " ext=" + standard.ext);
                $scope.hl7Version = standard.hl7Version;
                DatatypeLibraryDocumentSvc.create(standard.hl7Version, scope, standard.name, standard.ext).then(function(result) {
                    getDataTypeLibraryByScope(scope);
                    angular.forEach($scope.datatypeLibrariesConfig, function(lib) {
                        if (lib.type === scope) {
                            $scope.datatypeLibrariesConfig.selectedType = lib;
                        }
                    });
                });
                //console.log("$scope.datatypeLibsStruct=" + $scope.datatypeLibsStruct.length);
            });
        };


        $scope.addingToc = [];
        $scope.addDatatypesFromTree = function() {

            $scope.editView = 'addingView.html';


        }
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
                windowClass: 'app-modal-window',
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

                DatatypeLibrarySvc.bindDatatypes(ids, $scope.datatypeLibrary.id, $scope.datatypeLibrary.metaData.ext).then(function(datatypeLinks) {
                    var ids = [];
                    angular.forEach(datatypeLinks, function(datatypeLink) {
                        $scope.datatypeLibrary.children.push(datatypeLink);
                        ids.push(datatypeLink.id);
                    });
                    DatatypeService.get(ids).then(function(datatypes) {
                        angular.forEach(datatypes, function(datatype) {
                            datatype.status = "UNPUBLISHED";
                            $scope.datatypeLibCopy.children.push(datatype);
                            DatatypeService.collectDatatypes(datatype.id).then(function(datatypes) {
                                angular.forEach(datatypes, function(dt) {
                                    if (!_.includes(dt.libIds, $scope.datatypeLibrary.id)) {
                                        dt.libIds.push($scope.datatypeLibrary.id);
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
        $scope.confirmPublish = function(datatypeCopy) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypePublishCtl.html',
                controller: 'ConfirmDatatypePublishCtl',
                resolve: {
                    datatypeToPublish: function() {
                        return datatypeCopy;
                    }
                }
            });
            modalInstance.result.then(function(datatypeCopy) {
                DatatypeService.save(datatypeCopy);
            });
        };
    });
angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
    function($scope, $rootScope, $modalInstance, $timeout, hl7Versions, DatatypeLibrarySvc, DatatypeService) {

        $scope.okDisabled = true;

        $scope.scope = "HL7STANDARD";
        $scope.hl7Versions = hl7Versions;
        $scope.standard = {};
        $scope.standard.hl7Version = null;
        $scope.name = null;
        $scope.standard.ext = null;

        $scope.getDisplayLabel = function(dt) {
            if (dt) {
                return dt.label;
            }
        }

        $scope.ok = function() {
            $modalInstance.close($scope.standard);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };

    });

angular.module('igl').controller('DatatypeListInstanceDlgCtl',
    function($scope, $rootScope, $modalInstance, hl7Version, datatypeLibsStruct, DatatypeLibrarySvc, DatatypeService) {

        $scope.hl7Version = hl7Version;
        $scope.datatypesLibStruct = datatypeLibsStruct;
        $scope.selectedLib;
        $scope.dtSelections = [];

        $scope.trackSelections = function(bool, event) {
            if (bool) {
                $scope.dtSelections.push(event);
            } else {
                for (var i = 0; i < $scope.dtSelections.length; i++) {
                    if ($scope.dtSelections[i].id === event.id) {
                        $scope.dtSelections.splice(i, 1);
                    }
                }
            }
            $scope.okDisabled = $scope.dtSelections.length === 0;
        };

        $scope.libSelected = function(datatypeLib) {
            $scope.selectedLib = datatypeLib;
        };

        $scope.ok = function() {
            $modalInstance.close($scope.dtSelections);
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });

angular.module('igl').controller('ConfirmDatatypeLibraryDeleteCtrl', function($scope, $rootScope, $http, $modalInstance, datatypeLibraryToDelete) {

    $scope.datatypeLibraryToDelete = datatypeLibraryToDelete;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.datatypeLibraryToDelete);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ConfirmDatatypeDeleteCtl', function($scope, $rootScope, $http, $modalInstance, datatypeToDelete) {

    $scope.datatypeToDelete = datatypeToDelete;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.datatypeToDelete);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmTablesDeleteCtl', function($scope, $rootScope, $http, $modalInstance, tableToDelete) {

    $scope.tableToDelete = tableToDelete;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.tableToDelete);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('PreventDatatypeDeleteCtl', function($scope, $rootScope, $http, $modalInstance, datatypeToDelete) {

    $scope.datatypeToDelete = datatypeToDelete;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.datatypeToDelete);
    };

    $scope.cancel = function() {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ConfirmDatatypePublishCtl', function($scope, $rootScope, $http, $modalInstance, datatypeToPublish) {

    $scope.datatypeToPublish = datatypeToPublish;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.datatypeToPublish);
    };

    $scope.cancel = function() {
        $scope.datatypeToPublish.status = "'UNPUBLISHED'";
        $modalInstance.dismiss('cancel');
    };
});



angular.module('igl').controller('ConfirmRedirect', function($scope, $rootScope, $http, $modalInstance, datatypeTo) {

    $scope.datatypeTo = datatypeTo;
    $scope.loading = false;

    $scope.delete = function() {
        $modalInstance.close($scope.datatypeTo);
    };

    $scope.cancel = function() {
        //$scope.datatypeToPublish.status = "'UNPUBLISHED'";
        $modalInstance.dismiss('cancel');
    };
});



angular.module('igl').controller('PredicateDatatypeLibraryCtrl', function($scope, $modalInstance, selectedNode, selectedDatatype, $rootScope) {
    $scope.constraintType = 'Plain';
    $scope.selectedNode = selectedNode;

    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;

    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy(selectedDatatype.predicates, $scope.tempPredicates);


    $scope.setChanged = function() {
        $scope.changed = true;
    }

    $scope.initPredicate = function() {
        $scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            datatype: '',
            component_1: null,
            subComponent_1: null,
            component_2: null,
            subComponent_2: null,
            verb: null,
            contraintType: null,
            value: null,
            value2: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });
        $scope.newConstraint.datatype = selectedDatatype.name;
    }

    $scope.initComplexPredicate = function() {
        $scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.complexConstraintTrueUsage = null;
        $scope.complexConstraintFalseUsage = null;
    }

    $scope.initPredicate();


    $scope.deletePredicate = function(predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
        $scope.changed = true;
    };

    $scope.updateComponent_1 = function() {
        $scope.newConstraint.subComponent_1 = null;
    };

    $scope.updateComponent_2 = function() {
        $scope.newConstraint.subComponent_2 = null;
    };


    $scope.genLocation = function(datatype, component, subComponent) {
        var location = null;
        if (component != null && subComponent == null) {
            location = datatype + '.' + component.position + "(" + component.name + ")";
        } else if (component != null && subComponent != null) {
            location = datatype + '.' + component.position + '.' + subComponent.position + "(" + subComponent.name + ")";
        }

        return location;
    };

    $scope.genPosition = function(component, subComponent) {
        var position = null;
        if (component != null && subComponent == null) {
            position = component.position + '[1]';
        } else if (component != null && subComponent != null) {
            position = component.position + '[1]' + '.' + subComponent.position + '[1]';
        }

        return position;
    };


    $scope.deletePredicateByTarget = function() {
        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget.indexOf($scope.selectedNode.position + '[') === 0) {
                $scope.deletePredicate($scope.tempPredicates[i]);
                return true;
            }
        }
        return false;
    };

    $scope.addComplexPredicate = function() {
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;

        if ($scope.selectedNode === null) {
            $scope.complexConstraint.constraintId = '.';
        } else {
            $scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
        }

        $scope.tempPredicates.push($scope.complexConstraint);
        $scope.initComplexPredicate();
        $scope.changed = true;
    };

    $scope.addPredicate = function() {

        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

        $scope.newConstraint.position_1 = $scope.genPosition($scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.position_2 = $scope.genPosition($scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
        $scope.newConstraint.location_1 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
        $scope.newConstraint.location_2 = $scope.genLocation($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);

        if ($scope.newConstraint.position_1 != null) {
            var cp = null;
            if ($scope.selectedNode === null) {
                var cp = $rootScope.generatePredicate(".", $scope.newConstraint);
            } else {
                var cp = $rootScope.generatePredicate($scope.selectedNode.position + '[1]', $scope.newConstraint);
            }

            $scope.tempPredicates.push(cp);
            $scope.changed = true;
        }
        $scope.initPredicate();
    };

    $scope.ok = function() {
        $modalInstance.close($scope.selectedNode);
    };

    $scope.saveclose = function() {
        angular.copy($scope.tempPredicates, selectedDatatype.predicates);
        $rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
});