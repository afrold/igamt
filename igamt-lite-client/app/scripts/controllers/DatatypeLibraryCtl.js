/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, ViewSettings) {

			$scope.datatypeStruct = false;
			$scope.loadingSelection = true;
			$scope.publishSelections = [];
			$scope.datatypeDisplay	= [];
	        $scope.viewSettings = ViewSettings;

	        $scope.tableWidth = null;

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
				getDataTypeLibrary("MASTER");
/*         $http.get('api/datatypes/12345').then(function(response){
          console.log(JSONStingify(response));
        }); */
			};

			function getDataTypeLibrary(scope) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope).then(function(data) {
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
			}

			$scope.trackSelections = function(bool, selection) {
				if (bool) {
					selection.status = "PUBLISHED";
				} else {
					selection.status = "UNPUBLISHED";
				}
			};

			$scope.save = function() {
				angular.forEach($scope.datatypeStruct.children, function(dt) {
					delete dt.new;
				});
				DatatypeLibrarySvc.save($scope.datatypeStruct);
			};

			$scope.edit = function(datatype) {
				$scope.loadingSelection = true;
				$timeout(
					function () {
			            $scope.subview = FormsSelectSvc.selectDatatype(datatype);
			            $scope.loadingSelection = false;
		                if ($scope.datatypesParams)
		                		$scope.datatypesParams.refresh();
				}, 100);
			};

	        $scope.datatypesParams = new ngTreetableParams({
	            getNodes: function (parent) {
	                return DatatypeService.getNodes(parent);
	            },
	            getTemplate: function (node) {
	                return DatatypeService.getTemplate(node);
	            }
	        });

//	        $scope.selectDatatype = function (datatype) {
//	            $scope.subview = "EditDatatypes.html";
//	            if (datatype && datatype != null) {
//	                $scope.loadingSelection = true;
//	                $rootScope.datatype = datatype;
//	                $rootScope.datatype["type"] = "datatype";
//	                $timeout(
//	                    function () {
//	                        $scope.tableWidth = null;
//	                        $scope.scrollbarWidth = $scope.getScrollbarWidth();
//	                        $scope.csWidth = $scope.getDynamicWidth(1, 3, 890);
//	                        $scope.predWidth = $scope.getDynamicWidth(1, 3, 890);
//	                        $scope.commentWidth = $scope.getDynamicWidth(1, 3, 890);
//	                        $scope.loadingSelection = false;
//	                        if ($scope.datatypesParams)
//	                            $scope.datatypesParams.refresh();
//	                    }, 100);
//	            }
//	        };

//	        $scope.getTableWidth = function () {
//	            if ($scope.tableWidth === null || $scope.tableWidth == 0) {
//	                $scope.tableWidth = $("#nodeDetailsPanel").width();
//	            }
//	            return $scope.tableWidth;
//	        };

//	        $scope.getDynamicWidth = function (a, b, otherColumsWidth) {
//	            var tableWidth = $scope.getTableWidth();
//	            if (tableWidth > 0) {
//	                var left = tableWidth - otherColumsWidth;
//	                return {"width": a * parseInt(left / b) + "px"};
//	            }
//	            return "";
//	        };

			$scope.openStandardDataypes = function() {
				DatatypeLibrarySvc.getDataTypeLibrary("HL7STANDARD").then(function(data) {
					var datatypeStruct = data;
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
						resolve : {
							"datatypeStruct" : datatypeStruct
						}
					}).result.then(function(standardSelections) {
						console.log("standardSelections=" + standardSelections.length);
					    // Decorate the user selections.
						var decoratedSelections1 = decoratedSelections(standardSelections);
					    // Push them on to the scope.
						angular.forEach(decoratedSelections1, function(child) {
							child.new = true;
							$scope.datatypeStruct.children.push(child);
						});
						console.log("$scope.datatypeStruct.children=" + $scope.datatypeStruct.children.length);
					});
				}).catch( function (error) {
				console.log(error);
			});
		};

		function decoratedSelections(datatypes) {
			angular.forEach(datatypes, function(child) {
				child.ext = Math.floor(Math.random() * 100);
				child.scope = "MASTER";
				child.hl7Version = $scope.datatypeStruct.hl7Version;
			});
			return datatypes;
		};
});

angular.module('igl').controller('StandardDatatypeLibraryInstanceDlgCtl',
		function($scope, $modalInstance, datatypeStruct, DatatypeLibrarySvc) {

			$scope.okDisabled = true;

			$scope.datatypeStruct = datatypeStruct;
			$scope.standardSelections = [];

			$scope.getDisplayLabel = function(dt) {
				if (dt) {
					return dt.label;
				}
			}

			$scope.trackSelections = function(bool, event) {
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

			$scope.ok = function() {
				$modalInstance.close($scope.standardSelections);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};

		});
