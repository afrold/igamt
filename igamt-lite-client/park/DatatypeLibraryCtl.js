/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('DatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, ViewSettings, userInfoService) {

      $scope.datatypeLibsStruct = [];
      $scope.datatypeLibStruct = null;
      $scope.datatypesBrevis = []; 
			$scope.datatypeStruct = null;
			$scope.loadingSelection = true;
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];
	    $scope.viewSettings = ViewSettings;
      $scope.metaDataView = null;
      $scope.datatypeListView = null;
 
	    $scope.tableWidth = null;
      $scope.datatypeLibrary = "";
      $scope.hl7Version = null;
      $scope.datatypeView = null;
      $scope.scopes = [];
      $scope.datatypeLibrariesConfig = {};
      $scope.datatypeLibrariesConfig.selectedType
	  $scope.admin = userInfoService.isAdmin();
      
      $scope.datatypeLibraryTypes = [
                                { name: "Browse Master data type libraries", type: 'MASTER', visible :  $scope.admin,
                                },
                                { name: "Access My data type libraries", type: 'USER', visible :  true
                                }
                            ];

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
			};

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

			$scope.saveLibrary = function() {
				angular.forEach($scope.datatypeLibStruct.children, function(dt) {
					delete dt.new;
				});
				DatatypeLibrarySvc.save($scope.datatypeLibStruct);
			};
			
			$scope.cancel = function() {
				console.log("canceled");
			};

			$scope.editLibrary = function(datatypeLibrary) {
				$scope.loadingSelection = true;
				$timeout(
					function () {
            $scope.hl7Version = datatypeLibrary.metaData.hl7Version;
                  $scope.datatypeLibStruct = datatypeLibrary;
                  DatatypeLibrarySvc.getDatatypesByLibrary($scope.datatypeLibStruct.id).then(function(response){
                    $scope.datatypesBrevis = response;
                  });
                  console.log("$scope.edit.datatypeLibStruct=" + JSON.stringify($scope.datatypeLibStruct.children.length));
                  $scope.metaDataView = "LibraryMetaData.html";
                  $scope.datatypeListView = "DatatypeList.html";
                 $timeout(
          function () {
            $scope.loadingSelection = false;
          }, 100);
			            $scope.loadingSelection = false;
				}, 100);
			};
			
			$scope.deleteLibrary = function(datatypeLibrary) {
				DatatypeLibrarySvc.delete_($scope.datatypeLibStruct.id);
			}

	$scope.saveDatatype = function(datatype) {
		console.log("save datatype=" + JSON.stringify(datatype.label));
		DatatypeService.save(datatype).then(function(result){
			console.log("saved datatype=" + JSON.stringify(datatype));
		});
	};
	
    $scope.editDatatype = function(datatype) {
      $scope.datatypeView = "EditDatatypeLibraryDatatype.html";
		console.log("edit datatype=" + JSON.stringify(datatype.label));
      if (datatype && datatype != null) {
        $scope.loadingSelection = true;
        
        var queryId = null;
        if (datatype.oldId) {
        	queryId = datatype.oldId;
        } else {
        	queryId = datatype.id;
        }

        DatatypeService.getOne(queryId).then(function (result) {
          $scope.datatypeStruct = result;
          if (datatype.oldId) {
        	  $scope.datatypeStruct.id = null;
        	  delete datatype.oldId;
          }
          $scope.datatype["type"] = "datatype";
          $scope.tableWidth = null;
          $scope.scrollbarWidth = $scope.getScrollbarWidth();
          $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.datatypesParams = new ngTreetableParams({
              getNodes: function (parent) {
                  return DatatypeService.getNodes(parent, $scope.datatypeStruct);
              },
              getTemplate: function (node) {
                  return DatatypeService.getTemplate(node, $scope.datatypeStruct);
              }
           });
          $scope.loadingSelection = false;
          $scope.datatypeLibrariesConfig.selectedType = 'USER';
          }, function (error) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }
    };

  $scope.copyDatatype = function(datatype) {
		console.log("copy datatype=" + JSON.stringify(datatype.label));
		var newDatatype = angular.copy(result);
		newDatatype.oldId = newDatatype.id;
		newDatatype.id = new ObjectId().toString();
		newDatatype.ext = newDatatype.ext + "-" + (Math.floor(Math.random() * 10000000) + 1);
		$scope.datatypeLibStruct.children.push(newDatatype);
  	}
  
 // $scope.copyDatatype = function(datatype) {
// console.log("copy datatype=" + JSON.stringify(datatype.label));
// DatatypeService.getOne(datatype.id).then(function (result) {
// var newDatatype = angular.copy(result);
// newDatatype.id = new ObjectId().toString();
// newDatatype.label = getLabel(newDatatype.name, datatypeLibStruct.ext) + "-" +
// (Math.floor(Math.random() * 10000000) + 1);
// if (newDatatype.components != undefined && newDatatype.components != null &&
// newDatatype.components.length != 0) {
// for (var i = 0; i < newDatatype.components.length; i++) {
// newDatatype.components[i].id = new ObjectId().toString();
// }
// }
// var predicates = newDatatype['predicates'];
// if (predicates != undefined && predicates != null && predicates.length != 0)
// {
// angular.forEach(predicates, function (predicate) {
// predicate.id = new ObjectId().toString();
// });
// }
// var conformanceStatements = newDatatype['conformanceStatements'];
// if (conformanceStatements != undefined && conformanceStatements != null &&
// conformanceStatements.length != 0) {
// angular.forEach(conformanceStatements, function (conformanceStatement) {
// conformanceStatement.id = new ObjectId().toString();
// });
// }
// $scope.datatypeLibStruct.children.push(newDatatype);
// $scope.loadingSelection = false;
// if ($scope.datatypesParams)
// $scope.datatypesParams.refresh();
// }, function (error) {
// $scope.loadingSelection = false;
// $rootScope.msg().text = error.data.text;
// $rootScope.msg().type = error.data.type;
// $rootScope.msg().show = true;
// });
// };

    $scope.deleteDatatype = function(datatype) {
		console.log("delete datatype=" + JSON.stringify(datatype.label));
 				var idx = _.findIndex($scope.datatypeLibStruct.children, function (
						child) {
					return datatype.id === child.id;
				});

				$scope.datatypeLibStruct.children.splice(idx, 1);
				DatatypeService.delete_(datatype);
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
             ids.push(result.id);
          });

        	DatatypeLibrarySvc.bindDatatypes(ids, $scope.datatypeLibStruct.id, $scope.datatypeLibStruct.metaData.ext).then(function(results) {
        	console.log("$scope.openDataypeList.bindDatatypes results=" + results.length);
          angular.forEach(results, function(result){
            result.new = true;
            $scope.datatypeLibStruct.children.push(result);
           });
        });

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $rootScope, $modalInstance, $timeout, hl7Versions, DatatypeLibrarySvc, DatatypeService) {

			$scope.okDisabled = true;

      $scope.scope = "HL7STANDARD";
      $scope.hl7Versions = hl7Versions;
      console.log("StandardDatatypeLibraryInstanceDlgCtl hl7Versions" + JSON.stringify(hl7Versions));
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
