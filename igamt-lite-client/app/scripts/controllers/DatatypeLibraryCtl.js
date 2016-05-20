/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('DatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, IGDocumentSvc, TableService, ViewSettings, userInfoService) {

      $scope.datatypeLibsStruct = [];
      $scope.toShow==="";
      $scope.datatypeLibStruct = null;
      $scope.datatypeLibMetaDataCopy = null;
// $scope.datatypesJoinStruct = [];
			$scope.datatypeStruct = null;
			$scope.datatypeCopy = null;
			$scope.loadingSelection = true;
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];
	    $scope.viewSettings = ViewSettings;
      $scope.metaDataView = null;
      $scope.datatypeListView = null;
      $scope.accordi = {metaData: false, definition: true, dtList: true, dtDetails: false};

      $scope.tableWidth = null;
  // $scope.datatypeLibrary = "";
      $scope.hl7Version = null;
      $scope.metaDataView= null;
      $scope.scopes = [];
      $scope.datatypeLibrariesConfig = {};
      $scope.datatypeLibrariesConfig.selectedType
	  $scope.admin = true; // userInfoService.isAdmin();
      $scope.toggle = function(param){
    	  $scope.toShow = param;
    	
      }
// $scope.datatypesParams = new ngTreetableParams({
// getNodes: function (parent) {
// return $scope.getNodes(parent, $scope.datatypeCopy);
// },
// getTemplate: function (node) {
// return $scope.getEditTemplate(node, $scope.datatypeCopy);
// }
// });

      $scope.datatypeLibraryTypes = [
                                { name: "Browse Master data type libraries", type: 'MASTER', visible :  $scope.admin,
                                },
                                { name: "Access My data type libraries", type: 'USER', visible :  true
                                }
                            ];

	$scope.initDatatypeLibrary = function() {
		IGDocumentSvc.loadIgDocumentMetaData();
		$scope.start = false;
	};
	
	$scope.seq = function(idx) {
		return idx + 1;
	}

// $scope.getMetaData = function(datatypeLibrary) {
// $scope.metaDataView = "LibraryMetaData.html";
// $scope.loadingSelection = true;
// $timeout(
// function () {
// $scope.loadingSelection = false;
// }, 100);
// };

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

			$scope.trackSelections = function(bool, selection) {
				if (bool) {
					selection.status = "PUBLISHED";
				} else {
					selection.status = "UNPUBLISHED";
				}
			};

			$scope.saveMetaData = function() {
				$scope.datatypeLibStruct.metaData = angular.copy($scope.datatypeLibMetaDataCopy);
				DatatypeLibrarySvc.saveMetaData($scope.datatypeLibStruct.id, $scope.datatypeLibMetaDataCopy);
			};
			
			$scope.cancel = function() {
				console.log("canceled");
			};
			
			$scope.editMetadata= function(){
			
	            $scope.metaDataView = "LibraryMetaData.html";
	                  
				
			}
	
			
			$scope.editLibrary = function(datatypeLibrary) {
		        $scope.datatypeListView = "DatatypeList.html";
				   console.log("edit().datatypeLibrary=" + JSON.stringify(datatypeLibrary));
					$scope.loadingSelection = true;
				$rootScope.isEditing = true;
		           $scope.hl7Version = datatypeLibrary.metaData.hl7Version;
		           $scope.datatypeLibStruct = datatypeLibrary;    
		            $scope.datatypeLibMetaDataCopy = angular.copy(datatypeLibrary.metaData);
					$scope.loadingSelection = false;
          		  $rootScope.DataTypeTree = [];
        		  $rootScope.DataTypeTree = $scope.datatypeLibStruct.children;
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
				DatatypeLibrarySvc.delete(datatypeLibrary.id);
                var idxP = _.findIndex($scope.datatypeLibsStruct, function (child) {
                    return child.id === datatypeLibrary.id;
                });
                $scope.datatypeLibsStruct.splice(idxP, 1);
			}

	        $scope.confirmDelete = function (datatypeLibrary) {
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
					DatatypeLibrarySvc.delete(datatypeLibrary.id);
	                var idxP = _.findIndex($scope.datatypeLibsStruct, function (child) {
	                    return child.id === datatypeLibrary.id;
	                });
	                $scope.datatypeLibsStruct.splice(idxP, 1);
	            });
	        };

	        $scope.confirmClose = function () {
	            var modalInstance = $modal.open({
	                templateUrl: 'ConfirmDatatypeLibraryCloseCtrl.html',
	                controller: 'ConfirmDatatypeLibraryCloseCtrl'
	            });
	            modalInstance.result.then(function () {
	                $rootScope.clearChanges();
	            }, function () {
	            });
	        };

	        $scope.confirmOpen = function (igdocument) {
	            var modalInstance = $modal.open({
	                templateUrl: 'ConfirmDatatypeLibraryOpenCtrl.html',
	                controller: 'ConfirmDatatypeLibraryOpenCtrl',
	                resolve: {
	                    datatyepLibStructToOpen: function () {
	                        return datatypeLibrary;
	                    }
	                }
	            });
	            modalInstance.result.then(function (igdocument) {
	                $rootScope.clearChanges();
	                $scope.openDatatypeLibrary(igdocument);
	            }, function () {
	            });
	        };


	$scope.saveDatatype = function(datatype) {
		console.log("save datatype=" + JSON.stringify(datatype.label));
		DatatypeService.save(datatype).then(function(result){
			console.log("saved datatype=" + JSON.stringify(datatype));
		});
	};
	
    $scope.editDatatype = function(datatypeLink) {
      $scope.metaDataView = "EditDatatypeLibraryDatatype.html";
         $scope.loadingSelection = true;
        DatatypeService.getOne(datatypeLink.id).then(function(datatype) {
        	$scope.datatypeStruct = datatype;
            $scope.datatypeCopy  = angular.copy(datatype);
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
        });
        $scope.loadingSelection = false;
    };
    
    $scope.getNodes = function (parent, root) {
        var children = [];
        if (parent && parent != null) {
            if (parent.datatype) {
                 children = DatatypeService.getOne(parent.datatype);
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
    }

  $scope.copyDatatype = function(datatypeLink) {
		console.log("copy datatype=" + JSON.stringify(datatypeLink));
		var newDatatypeLink = angular.copy(datatypeLink);
		newDatatypeLink.ext = newDatatypeLink.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
       DatatypeService.getOne(datatypeLink.id).then(function(datatype) {
        	$scope.datatypeStruct = datatype;
        	$scope.datatypeStruct.id = null;
           	DatatypeService.save($scope.datatypeStruct).then(function(savedDatatype){
            	$scope.datatypeStruct = savedDatatype;
            	newDatatypeLink.id = savedDatatype.id;
          	});
        });
		console.log("copy datatype=" + JSON.stringify(newDatatypeLink));
		$scope.datatypeLibStruct.children.push(newDatatypeLink);
  	}

    $scope.deleteDatatype = function(datatype) {
		console.log("delete datatype=" + JSON.stringify(datatype.label));
 				var idx = _.findIndex($scope.datatypeLibStruct.children, function (child) {
					return datatype.id === child.id;
				});
 				$scope.datatypeCopy = null;
				$scope.datatypeLibStruct.children.splice(idx, 1);
				DatatypeService.delete(datatype.id);
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

			$scope.openStandardDataypes = function(scope) {
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
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
     var scopes = ['USER', 'HL7STANDARD'];
      if (userInfoService.isAdmin()) {
            scopes.push('MASTER');
          }
      console.log("openDataypeList scopes=" + scopes.length);
      var datatypesListInstance = $modal.open({
						templateUrl : 'datatypeListDlg.html',
						controller : 'DatatypeListInstanceDlgCtl',
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

        	DatatypeLibrarySvc.bindDatatypes(ids, $scope.datatypeLibStruct.id, $scope.datatypeLibStruct.metaData.ext).then(function(datatypes) {
        	console.log("$scope.openDataypeList.bindDatatypes datatypes=" + datatypes.length);
          angular.forEach(datatypes, function(datatype){
        	  $scope.datatypeLibStruct.children.push(datatype);
            });
        });
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

/*
 * $scope.trackSelections = function(bool, event) { if (bool) {
 * $scope.standardSelections.push(event); } else { for (var i = 0; i <
 * $scope.standardSelections.length; i++) { if ($scope.standardSelections[i].id
 * === event.id) { $scope.standardSelections.splice(i, 1); } } }
 * $scope.okDisabled = $scope.standardSelections.length === 0; };
 */
/*
 * $scope.loadDatatypesByScopeAndVersion = function() { $scope.loading = true;
 * DatatypeLibrarySvc.getDatatypesByScopeAndVersion($scope.scope,
 * hl7Version).then(function(response){ $scope.datatypeStruct = response; });
 * $scope.loading = false; };
 */

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

angular.module('igl').controller('ConfirmDatatypeLibraryDeleteCtrl', function ($scope, $rootScope, $http, $modalInstance, datatypeLibStructToDelete) {
	
    $scope.datatypeLibStructToDelete = datatypeLibStructToDelete;
    $scope.loading = false;
    
    $scope.delete = function () {
        $scope.loading = true;
        $http.post($rootScope.api('api/datatype-library/' + $scope.datatypeLibStructToDelete.id + '/delete')).then(function (response) {
            var index = $rootScope.datatypeLibsStruct.indexOf($scope.datatypeLibStructToDelete);
            if (index > -1) $rootScope.datatypeLibsStruct.splice(index, 1);
            $rootScope.backUp = null;
            if ($scope.datatypeLibStructToDelete === $scope.datatypeLibStruct) {
                $scope.closeDatatypeLibrary();
            }
            $rootScope.msg().text = "dtDeleteSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
            $rootScope.manualHandle = true;
            $scope.igdocumentToDelete = null;
            $scope.loading = false;
            $modalInstance.close($scope.datatypeLibStructToDelete);

        }, function (error) {
            $scope.error = error;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
            $rootScope.msg().text = "dtDeleteFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;


// waitingDialog.hide();
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmDatatypeLibraryCloseCtrl', function ($scope, $modalInstance, $rootScope, $http) {
    $scope.loading = false;
    $scope.discardChangesAndClose = function () {
        $scope.loading = true;
        $http.get('api/datatype-library/' + $scope.datatypeLibStruct.id, {timeout: 60000}).then(function (response) {
            var index = $scope.datatypeLibsStruct.indexOf($scope.datatypeLibStruct);
            $scope.datatypeLibsStruct[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "dtResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $modalInstance.dismiss('cancel');
        });
    };

    $scope.clear = function () {
        $rootScope.closeDatatypeLibrary();
        $modalInstance.close();
    };

    $scope.ConfirmDatatypeLibraryOpenCtrl = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "datatypeLibrary": $scope.datatypeLibStruct};
        $http.post('api/datatype-library/save',data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $$scope.datatypeLibStruct.metaData.date = saveResponse.date;
            $scope.datatypeLibStruct.metaData.version = saveResponse.version;
            $scope.loading = false;
            $scope.clear();
        }, function (error) {
            $rootScope.msg().text = "dtSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };
    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('ConfirmDatatypeLibraryOpenCtrl', function ($scope, $modalInstance, datatypeLibStructToOpen, $rootScope, $http) {
    $scope.datatypeLibStructToOpen = datatypeLibStructToOpen;
    $scope.loading = false;

    $scope.discardChangesAndOpen = function () {
        $scope.loading = true;
        $http.get('api/datatype-library/' + $scope.datatypeLibStruct.id, {timeout: 60000}).then(function (response) {
            var index = $scope.datatypeLibsStruct.indexOf($scope.datatypeLibStruct);
            $scope.datatypeLibsStruct[index] = angular.fromJson(response.data);
            $scope.loading = false;
            $modalInstance.close($scope.datatypeLibStructToOpen);
        }, function (error) {
            $scope.loading = false;
            $rootScope.msg().text = "dtResetFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;

            $modalInstance.dismiss('cancel');
        });
    };

    $scope.saveChangesAndOpen = function () {
        $scope.loading = true;
        var changes = angular.toJson($rootScope.changes);
        var data = {"changes": changes, "datatypeLib": $scope.datatypeLibStruct};
        $http.post('api/datatype-library/save', data, {timeout: 60000}).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            $scope.datatypeLibStruct.metaData.date = saveResponse.date;
            $scope.datatypeLibStruct.metaData.version = saveResponse.version;
            $scope.loading = false;
            $modalInstance.close($scope.datatypeLibStructToOpen);
        }, function (error) {
            $rootScope.msg().text = "dtSaveFailed";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
            $scope.loading = false;
            $modalInstance.dismiss('cancel');
        });
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
