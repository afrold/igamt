/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, ViewSettings, userInfoService) {

      $scope.datatypeLibsStruct = [];
      $scope.datatypeLibStruct = null;
			$scope.datatypeStruct = null;
			$scope.loadingSelection = true;
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];
	    $scope.viewSettings = ViewSettings;
      $scope.metaDataView = null;
      $scope.datatypeListView = null;
      $scope.igDocumentConfig = {};
      $scope.igDocumentConfig.selectedType

	    $scope.tableWidth = null;
      $scope.datatypeLibrary = "";
      $scope.hl7Version = null;
      $scope.datatypeView = null;
      $scope.scopes = [];

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
        $scope.scopes = ["USER"];
        if (true) { //($scope.isAuthenticated() && $scope.isAdmin()) {
          $scope.scopes.push("MASTER");
        }
				getDataTypeLibraryByScopes($scope.scopes);
			};

//      $scope.getMetaData = function(datatypeLibrary) {
//        $scope.metaDataView = "LibraryMetaData.html";
//        $scope.loadingSelection = true;
//        $timeout(
//          function () {
//            $scope.loadingSelection = false;
//          }, 100);
//      };

      $scope.getDatatypes = function(datatypeLibrary) {
        $scope.datatypeListView = "DatatypeList.html";
        $scope.loadingSelection = true;
        $timeout(
          function () {
            $scope.loadingSelection = false;
          }, 100);
      };

			function getDataTypeLibraryByScopes(scopes) {
				DatatypeLibrarySvc.getDataTypeLibraryByScopes(scopes).then(function(data) {
          angular.forEach(data, function(datum){
						$scope.datatypeLibsStruct.push(datum);
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

			$scope.getDisplayLabel = function(dt) {
				if(dt) {
					return (dt.ext) ? dt.label + "_" + dt.ext : dt.label;
				}
			};

			$scope.trackSelections = function(bool, selection) {
				if (bool) {
					selection.status = "PUBLISHED";
				} else {
					selection.status = "UNPUBLISHED";
				}
			};

			$scope.saveLib = function() {
				angular.forEach($scope.datatypeLibStruct.children, function(dt) {
					delete dt.new;
				});
				DatatypeLibrarySvc.save($scope.datatypeLibStruct);
			};
			
			$scope.save = function() {
				DatatypeService.save($scope.datatype);
			};
			
			$scope.cancel = function() {
				console.log("canceled");
			};

			$scope.edit = function(datatypeLibrary) {
				$scope.loadingSelection = true;
				$timeout(
					function () {
            $scope.hl7Version = datatypeLibrary.metaData.hl7Version;
                  $scope.datatypeLibStruct = datatypeLibrary;
                  DatatypeLibrarySvc.getDatatypesByLibrary($scope.datatypeLibStruct.id).then(function(response){
                    $scope.datatypeLibStruct.children = response;
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

	$scope.saveDatatype = function(datatype) {
		DatatypeService.save(datatype).then(function(result){
			console.log("saved datatype=" + JSONStringify(datatype));
		});
	};
	
    $scope.editDatatype = function(datatype) {
      $scope.datatypeView = "EditDatatypes.html";
      if (datatype && datatype != null) {
        $scope.loadingSelection = true;
        DatatypeService.getOne(datatype.id).then(function (result) {
          $rootScope.datatype = result;
          $scope.loadingSelection = false;
          $rootScope.datatype["type"] = "datatype";
          $scope.tableWidth = null;
          $scope.scrollbarWidth = $scope.getScrollbarWidth();
          $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
          $scope.loadingSelection = false;
          if ($scope.datatypesParams) {
            $scope.datatypesParams.refresh();
          }
          $scope.igDocumentConfig.selectedType = 'USER';
          }, function (error) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
      }
    };

    $scope.copyDatatype = function(datatype) {
        DatatypeService.getOne(datatype.id).then(function (result) {
          var newDatatype = angular.copy(result);
          newDatatype.id = new ObjectId().toString();
          newDatatype.label = newDatatype.label + "_" + (Math.floor(Math.random() * 10000000) + 1);
          if (newDatatype.components != undefined && newDatatype.components != null && newDatatype.components.length != 0) {
            for (var i = 0; i < newDatatype.components.length; i++) {
              newDatatype.components[i].id = new ObjectId().toString();
            }
          }
          var predicates = newDatatype['predicates'];
          if (predicates != undefined && predicates != null && predicates.length != 0) {
            angular.forEach(predicates, function (predicate) {
              predicate.id = new ObjectId().toString();
            });
          }
          var conformanceStatements = newDatatype['conformanceStatements'];
          if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
            angular.forEach(conformanceStatements, function (conformanceStatement) {
              conformanceStatement.id = new ObjectId().toString();
            });
          }
          $scope.datatypeLibStruct.children.push(newDatatype);
         $scope.loadingSelection = false;
          if ($scope.datatypesParams)
            $scope.datatypesParams.refresh();
        }, function (error) {
          $scope.loadingSelection = false;
          $rootScope.msg().text = error.data.text;
          $rootScope.msg().type = error.data.type;
          $rootScope.msg().show = true;
        });
    };

    $scope.deleteDatatype = function(datatype) {
 				var idx = _.findIndex($scope.datatypeLibStruct.children, function (
						child) {
					return datatype.id === child.id;
				});

				$scope.datatypeLibStruct.children.splice(idx, 1);
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


			$scope.openStandardDataypes = function() {
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
            DatatypeLibrarySvc.create(standard.hl7Version, "MASTER", standard.name, standard.ext).then(function(result){
             $scope.datatypeLibsStruct.push(result);
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

        	DatatypeLibrarySvc.bindDatatypes(ids, $scope.datatypeLibStruct.id, $scope.datatypeLibStruct.metaData.ext).then(function(results) {
        	console.log("$scope.openDataypeList.bindDatatypes results=" + results.length);
          angular.forEach(results, function(result){
            result.new = true;
            $scope.datatypeLibStruct.children.push(result);
           });
        });
      });

    }
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

/* 			$scope.trackSelections = function(bool, event) {
				if (bool) {
					$scope.standardSelections.push(event);
				} else {
					for (var i = 0; i < $scope.standardSelections.length; i++) {
						if ($scope.standardSelections[i].id === event.id) {
							$scope.standardSelections.splice(i, 1);
						}
					}
				}
				$scope.okDisabled = $scope.standardSelections.length === 0;
			};
 */
/*       $scope.loadDatatypesByScopeAndVersion = function() {
        $scope.loading = true;
            DatatypeLibrarySvc.getDatatypesByScopeAndVersion($scope.scope, hl7Version).then(function(response){
            $scope.datatypeStruct = response;
          });
          $scope.loading = false;
      }; */

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
