/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('DatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, IGDocumentSvc, TableService, ViewSettings, userInfoService) {

      $scope.datatypeLibsStruct = [];
      $scope.toShow==="";
      $scope.datatypeLibStruct = null;
      $scope.datatypeLibMetaDataCopy = null;
      $scope.datatypeStruct = null;
      $scope.datatypeCopy = null;
			$scope.loadingSelection = true;
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];
	        $scope.selectedChildren = [];
	        $scope.datatypesMap = [];
	        $scope.viewSettings = ViewSettings;
      $scope.editView = null;
      $scope.datatypeListView = null;
      $scope.added = [];
      $scope.accordi = {metaData: false, definition: true, dtList: true, dtDetails: false};
      $scope.forms = {};
//      $scope.forms.datatypeForm = {};
      $scope.tableWidth = null;
  // $scope.datatypeLibrary = "";
      $scope.hl7Version = null;
      $scope.scopes = [];
      $scope.datatypeLibrariesConfig = {};
      $scope.datatypeLibrariesConfig.selectedType
	  $scope.admin = true; // userInfoService.isAdmin();
      $scope.toggle = function(param){
    	  $scope.toShow = param;
    	
      }
      
 $scope.datatypesParams = new ngTreetableParams({
 getNodes: function (parent) {
 return $scope.getNodes(parent, $scope.datatypeCopy);
 },
 getTemplate: function (node) {
 return $scope.getEditTemplate(node, $scope.datatypeCopy);
 }
 });

      $scope.datatypeLibraryTypes = [
                                { name: "Browse Master data type libraries", type: 'MASTER', visible :  $scope.admin,
                                }
                            ];

	$scope.initDatatypeLibrary = function() {
		IGDocumentSvc.loadIgDocumentMetaData();
		$scope.start = false;
	};
	

    $scope.$watch(
        function(){
          return $scope.forms.editForm != undefined && $scope.forms.editForm.$dirty;
        },
        function handleFormState( newValue) {
            if(newValue){
                $rootScope.recordChanged();
            }else{
                $rootScope.clearChanges();
            }
        }
    );
    
    $scope.seq = function(idx) {
		return idx + 1;
	};

	        $scope.selectDTLibraryType = function (selectedType) {
		        $scope.datatypeLibrariesConfig.selectedType = selectedType;
	            getDataTypeLibraryByScope(selectedType);
	        };

      $scope.getDatatypes = function(datatypeLibrary) {
        $scope.datatypeListView = "DatatypeList.html";
        $scope.loadingSelection = true;
        $timeout(
          function () {
            $scope.loadingSelection = false;
          }, 100);
      };

			function getDataTypeLibraryByScope(scope) {
				DatatypeLibrarySvc.getDataTypeLibraryByScope(scope).then(function(data) {
					$scope.datatypeLibsStruct = [];
          angular.forEach(data, function(lib){
						$scope.datatypeLibsStruct.push(lib);
          });
          	$scope.accordi.dtDetails = false;
			$rootScope.isEditing = false;
			$scope.DataTypeTree=[];
			$scope.datatypeLibCopy = {};
           console.log("$scope.datatypeLibsStruct size=" + $scope.datatypeLibsStruct.length);
				}).catch( function (error) {
					console.log(error);
				});
			};

			function getDataTypeLibraryByScopesAndVersion(scopes, hl7Version) {
				DatatypeLibrarySvc.getDataTypeLibraryByScopesAndVersion(scopes, hl7Version).then(function(data) {
						$scope.datatypeStruct =  data;
						if (!$scope.datatypeStruct.scope) {
							$scope.datatypeStruct.scope = scope;
						}
						console.log("$scope.datatypeStruct.id=" + $scope.datatypeStruct.id + " $scope.datatypeStruct.scope=" + $scope.datatypeStruct.scope);
				}).catch( function (error) {
					console.log(error);
				});
			};

			$scope.saveMetaData = function() {
				$scope.datatypeLibStruct.metaData = angular.copy($scope.datatypeLibMetaDataCopy);
				DatatypeLibrarySvc.saveMetaData($scope.datatypeLibStruct.id, $scope.datatypeLibMetaDataCopy);
	    		$scope.forms.editForm.$setPristine();
			};
			
			$scope.resetMetaData = function() {
				console.log("b=" + $scope.datatypeLibMetaDataCopy.name);
				console.log("b=" + JSON.stringify($scope.datatypeLibStruct.metaData.name));
				$scope.datatypeLibMetaDataCopy = angular.copy($scope.datatypeLibStruct.metaData);
				$scope.DataTypeTree[0].metaData = $scope.datatypeLibMetaDataCopy;
				console.log("a=" + $scope.datatypeLibMetaDataCopy.name);
				console.log("a=" + JSON.stringify($scope.datatypeLibStruct.metaData.name));
	    		$scope.forms.editForm.$setPristine();
			};			
			
			$scope.editMetadata= function(){		
				$scope.datatypeLibMetaDataCopy = angular.copy($scope.DataTypeTree[0].metaData);
	            $scope.editView = "LibraryMetaData.html";			
			}
		
			$scope.editLibrary = function(datatypeLibrary) {
		        $scope.datatypeListView = "DatatypeList.html";
					$scope.loadingSelection = true;
			        $scope.datatypeLibStruct = datatypeLibrary;    
			        DatatypeLibrarySvc.getDatatypesByLibrary(datatypeLibrary.id).then(function(datatypes){
					console.log("datatypes=" + datatypes.length);
						$rootScope.isEditing = true;
						$scope.accordi.dtDetails = true;
				           $scope.hl7Version = datatypeLibrary.metaData.hl7Version;
				            $scope.datatypeLibMetaDataCopy = angular.copy(datatypeLibrary.metaData);
							$scope.loadingSelection = false;
							$scope.DataTypeTree=[];
							$scope.datatypeLibCopy = angular.copy($scope.datatypeLibStruct);
							$scope.datatypeLibCopy.children = [];
							angular.forEach(datatypes, function(datatype){
								$scope.datatypeLibCopy.children.push(datatype);
							});
							$scope.DataTypeTree.push($scope.datatypeLibCopy);
				   });
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
			
	        $scope.confirmLibraryDelete = function (datatypeLibrary) {
	            var modalInstance = $modal.open({
	                templateUrl: 'ConfirmDatatypeLibraryDeleteCtrl.html',
	                controller: 'ConfirmDatatypeLibraryDeleteCtrl',
	                resolve: {
	                	datatypeLibraryToDelete: function () {
	                        return datatypeLibrary;
	                    }
	                }
	            });
	            modalInstance.result.then(function (datatypeLibrary) {
					DatatypeLibrarySvc.delete(datatypeLibrary.id).then(function(result){
	                var idxP = _.findIndex($scope.datatypeLibsStruct, function (child) {
	                    return child.id === datatypeLibrary.id;
	                });
	                $scope.datatypeLibsStruct.splice(idxP, 1);
					$scope.DataTypeTree=[];
					$scope.datatypeLibCopy = {};
	                $scope.datatypeLibMetaDataCopy = {};
	              	$scope.accordi.dtDetails = false;
	    			$rootScope.isEditing = false;
					});
            });
	        };

	        $scope.toggleStatus = function(status) {
	        	$scope.datatypeCopy.status = $scope.datatypeCopy.status === 'PUBLISHED' ? 'UNPUBLISHED' : 'PUBLISHED';
	        };

	$scope.saveDatatype = function(datatypeCopy) {
		console.log("save datatypeForm=" + $scope.forms.editForm);
		$scope.datatypeLibStruct = angular.copy(datatypeCopy);
		DatatypeService.save($scope.datatypeLibStruct).then(function(result){
            $rootScope.msg().text = "datatypeSaved";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
    		$scope.forms.editForm.$setPristine();
		});
	};

	$scope.resetDatatype = function(datatypeCopy) {
		$scope.forms.editForm.$setPristine();
		$scope.datatypeCopy = angular.copy($scope.datatypeLibStruct);
        $rootScope.clearChanges();
        if ($scope.datatypesParams) {
            $scope.datatypesParams.refresh();
        }
		$scope.forms.editForm.$setPristine();
	};	

   $scope.editDatatype = function(datatype) {
      $scope.editView = "EditDatatypeLibraryDatatype.html";
         $scope.loadingSelection = true;
         $scope.added = [];
         $scope.datatypeCopy = datatype;
            $scope.tableWidth = null;
            $scope.scrollbarWidth = $scope.getScrollbarWidth();
            $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
            $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
            $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
            $scope.datatypesParams.refresh();
            $scope.loadingSelection = false;
            if ($scope.datatypeStruct) {
          	  $scope.datatypeLibrariesConfig.selectedType = 'USER';
            }        
//        });
    };
    
    $scope.copyDatatype = function(datatypeCopy) {
  		var newDatatype = angular.copy(datatypeCopy);
  		newDatatype.ext = newDatatype.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
      	newDatatype.id = null;
      	newDatatype.status = 'UNPUBLISHED';
         	DatatypeService.save(newDatatype).then(function(savedDatatype){
         		newDatatype = savedDatatype;
      		$scope.datatypeLibStruct.children.push(createLink(newDatatype));
      		$scope.DataTypeTree[0].children.push(newDatatype);
        	});
        DatatypeLibrarySvc.save($scope.datatypeLibStruct);
		};
    	
    	function createLink(datatype) {
    		return { "id" : datatype.id,
    			"name" : datatype.name,
    			"ext" : datatype.ext
    			};
    		};

      $scope.deleteDatatype = function(datatypeCopy) {
				$scope.confirmDelete(datatypeCopy);
       };
		
        $scope.confirmDelete = function (datatypeCopy) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeDeleteCtl.html',
                controller: 'ConfirmDatatypeDeleteCtl',
                resolve: {
                	datatypeToDelete: function () {
                        return datatypeCopy;
                    }
                }
            });
            modalInstance.result.then(function (datatypeCopy) {
          		console.log("delete datatype=" + JSON.stringify(datatypeCopy.label));
  				var idx = _.findIndex($scope.datatypeLibStruct.children, function (child) {
  					return datatypeCopy.id === child.id;
  				});
  				if(idx > -1) {
  					$scope.datatypeLibStruct.children.splice(idx, 1);
  				}
  				var idx1 = _.findIndex($scope.DataTypeTree[0].children, function (child) {
  					return datatypeCopy.id === child.id;
  				});
  				if(idx1 > -1) {
  					$scope.DataTypeTree[0].children.splice(idx1, 1);
  				}
  				var idx2 = _.indexOf(datatypeCopy.libIds, $scope.datatypeLibStruct.id);
  				if(idx2 > -1) {
  					datatypeCopy.libIds.splice(idx2, 1);
  	 				DatatypeService.save(datatypeCopy);
  					console.log("deleted=" + $scope.datatypeLibStruct.id);
  				}
  				if (datatypeCopy.libIds.length === 0) {
  					DatatypeService.delete(datatypeCopy.id);
  				}
           });
        };

          $scope.getTableWidth = function () {
              if ($scope.tableWidth === null || $scope.tableWidth == 0) {
                  $scope.tableWidth = $("#nodeDetailsPanel").width();
              }
              return $scope.tableWidth;
          };

          $scope.getDynamicWidth = function (a, b, otherColumsWidth) {
              var tableWidth = $scope.getTableWidth();
              if (tableWidth > 0) {
                  var left = tableWidth - otherColumsWidth;
                  return {"width": a * parseInt(left / b) + "px"};
              }
              return "";
          };    

          $scope.isVisible = function (node) {
              var isVis = DatatypeService.isVisible(node);
              return isVis;
          };

          $scope.hasChildren = function (node) {
        	  console.log("hasChildren getDatatype=" + $scope.getDatatype(node.datatype.id));
              return node && node != null && node.datatype && $scope.getDatatype(node.datatype.id) != undefined && $scope.getDatatype(node.datatype.id).components != null && $scope.getDatatype(node.datatype.id).components.length > 0;
          };
          
          $scope.isChildSelected = function (component) {
              return  $scope.selectedChildren.indexOf(component) >= 0;
          };

          $scope.isChildNew = function (component) {
              return component && component != null && component.status === 'DRAFT';
          };
          
          $scope.recordDatatypeChange = function (type, command, id, valueType, value) {
              var datatypeFromChanges = $rootScope.findObjectInChanges("datatype", "add", $Scope.datatype.id);
              if (datatypeFromChanges === undefined) {
                  $rootScope.recordChangeForEdit2(type, command, id, valueType, value);
              }
          };
          
          $scope.countPredicate = function (position) {
              if ($rootScope.datatype != null)
                  for (var i = 0, len1 = $rootScope.datatype.predicates.length; i < len1; i++) {
                      if ($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
                          return 1;
                  }

              return 0;
          };
          
          $scope.showSelectDatatypeFlavorDlg = function (component) {
              var modalInstance = $modal.open({
                  templateUrl: 'SelectDatatypeFlavor.html',
                  controller: 'SelectDatatypeFlavorCtrl',
                  windowClass: 'app-modal-window',
                  resolve: {
                      currentDatatype: function () {
                          return $rootScope.datatypesMap[component.datatype.id];
                      },
                      hl7Version: function () {
                          return $rootScope.igdocument.metaData.hl7Version;
                      },
                      datatypeLibrary: function () {
                          return $rootScope.igdocument.profile.datatypeLibrary;
                      }
                  }
              });
              modalInstance.result.then(function (datatype, ext) {
                  component.datatype.id = datatype.id;
//                  MastermapSvc.addDatatypeObject(datatype, [component.id, component.type]);
                  if ($scope.datatypesParams)
                      $scope.datatypesParams.refresh();
              });

          };


          $scope.getDatatype = function (id) {
              return $scope.datatypesMap && $scope.datatypesMap[id];
          };

         $scope.getNodes = function (parent, root) {
              var children = [];
             if (parent && parent != null) {
                  if (parent.datatype) {
                      var dt = $scope.datatypesMap[parent.datatype.id];
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
              return children;
          };

    $scope.getEditTemplate = function (node, root) {
        return node.type === 'datatype' ? 'DatatypeLibraryEditTree.html' : node.type === 'component' && !DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeLibraryComponentEditTree.html' : node.type === 'component' && DatatypeService.isDatatypeSubDT(node,root) ? 'DatatypeLibrarySubComponentEditTree.html' : '';
    };
    
    $scope.isVisible = function (node) {
        return DatatypeService.isVisible(node);
    };
    
    $scope.sort = {
    		label : function(dt) {
    			return $rootScope.getLabel(dt.name, $scope.datatypeLibStruct.metaData.ext)
    		}
    };

			$scope.openStandardDataypes = function(scope) {
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
		                windowClass: 'app-modal-window',
						resolve : {
              hl7Versions : function() {
                return DatatypeLibrarySvc.getHL7Versions();
              }
						}
					}).result.then(function(standard) {
						console.log( "hl7Version=" + standard.hl7Version + " name=" + standard.name + " ext=" + standard.ext);
            $scope.hl7Version = standard.hl7Version;
            DatatypeLibrarySvc.create(standard.hl7Version, scope, standard.name, standard.ext).then(function(result) {
            	getDataTypeLibraryByScope(scope);
            	angular.forEach($scope.datatypeLibrariesConfig, function(lib) {
                    if (lib.type === scope) {
                    	$scope.datatypeLibrariesConfig.selectedType = lib;
                     }
             	});
           });
			console.log("$scope.datatypeLibsStruct=" + $scope.datatypeLibsStruct.length);
          });
		};

    $scope.openDataypeList = function(hl7Version) {
     var scopes = ['HL7STANDARD'];
      if ($scope.datatypeLibrariesConfig.selectedType === 'MASTER') {
            scopes.push('MASTER');
          } else {
              scopes.push('USER');
          }
      console.log("openDataypeList scopes=" + scopes.length);
      var datatypesListInstance = $modal.open({
						templateUrl : 'datatypeListDlg.html',
						controller : 'DatatypeListInstanceDlgCtl',
		                windowClass: 'app-modal-window',
						resolve : {
              hl7Version : function() {
                return $scope.hl7Version;
              },
              datatypeLibsStruct : function() {
                return DatatypeLibrarySvc.getDataTypeLibraryByScopesAndVersion(scopes, $scope.hl7Version);
              }
            }
      }).result.then(function(results) {
       var ids = [];
       angular.forEach(results, function(result){
           ids.push(result.id);
        });

        	DatatypeLibrarySvc.bindDatatypes(ids, $scope.datatypeLibStruct.id, $scope.datatypeLibStruct.metaData.ext).then(function(datatypeLinks) {
         	var ids = [];
        	angular.forEach(datatypeLinks, function(datatypeLink){
        	  $scope.datatypeLibStruct.children.push(datatypeLink);
        	  ids.push(datatypeLink.id);
            });
    	  DatatypeService.get(ids).then(function(datatypes) {
          	angular.forEach(datatypes, function(datatype){
          	datatype.status = "UNPUBLISHED";
          	$scope.datatypeLibCopy.children.push(datatype);
            DatatypeService.collectDatatypes(datatype.id).then(function (datatypes) {
                angular.forEach(datatypes, function (dt) {
                     if (!_.includes(dt.libIds, $scope.datatypeLibStruct.id)) {
                     	dt.libIds.push($scope.datatypeLibStruct.id);
                     }
                 	if ($scope.datatypesMap[dt.id] === null || $scope.datatypesMap[dt.id] === undefined) {
                         $scope.datatypesMap[dt.id] = dt;
                         $scope.added.push(dt.id);
                 	};
                     var exists2 = _.find($scope.DataTypeTree[0].children, 'id', dt.id);
                          if (exists2 === undefined) {
                    		$scope.DataTypeTree[0].children.push(dt);
                     }
             });
// 		        console.log("$scope.DataTypeTree=" + JSON.stringify($scope.DataTypeTree, null, 2));
                DatatypeService.saveAll(datatypes);
         });    	  });

        });
    });
  });   
};
$scope.confirmPublish = function (datatypeCopy) {
    var modalInstance = $modal.open({
        templateUrl: 'ConfirmDatatypePublishCtl.html',
        controller: 'ConfirmDatatypePublishCtl',
        resolve: {
        	datatypeToPublish: function () {
                return datatypeCopy;
            }
        }
    });
    modalInstance.result.then(function (datatypeCopy) {
  		console.log("publsih datatype=" + JSON.stringify(datatypeCopy.label));
  		$scope.datatype
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

angular.module('igl').controller('ConfirmDatatypeLibraryDeleteCtrl', function ($scope, $rootScope, $http, $modalInstance, datatypeLibraryToDelete) {
	
    $scope.datatypeLibraryToDelete = datatypeLibraryToDelete;
    $scope.loading = false;
    
    $scope.delete = function () {
		$modalInstance.close($scope.datatypeLibraryToDelete);
        };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ConfirmDatatypeDeleteCtl', function ($scope, $rootScope, $http, $modalInstance, datatypeToDelete) {
	
    $scope.datatypeToDelete = datatypeToDelete;
    $scope.loading = false;
    
    $scope.delete = function () {
		$modalInstance.close($scope.datatypeToDelete);
        };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});

angular.module('igl').controller('ConfirmDatatypePublishCtl', function ($scope, $rootScope, $http, $modalInstance, datatypeToPublish) {
	
    $scope.datatypeToPublish = datatypeToPublish;
    $scope.loading = false;
    
    $scope.delete = function () {
		$modalInstance.close($scope.datatypeToPublish);
        };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
