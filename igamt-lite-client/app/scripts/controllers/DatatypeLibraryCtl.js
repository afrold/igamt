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
			};

			function getDataTypeLibrary(scope, hl7Version) {
				DatatypeLibrarySvc.getDataTypeLibrary(scope, hl7Version).then(function(data) {
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

			$scope.openStandardDataypes = function() {
					var standardDatatypesInstance = $modal.open({
						templateUrl : 'standardDatatypeDlg.html',
						controller : 'StandardDatatypeLibraryInstanceDlgCtl',
						resolve : {
              hl7Versions : function() {
                return DatatypeLibrarySvc.getHL7Versions();
              }
						}
					}).result.then(function(standardSelections) {
						console.log("standardSelections=" + standardSelections.length);
					    // Decorate the user selections.
						var decoratedSelections1 = decoratedSelections(standardSelections);
					    // Push them on to the scope.
            if (!$scope.datatypeStruct) {
              $scope.datatypeStruct = {};
            }
            if (!$scope.datatypeStruct.children) {
              $scope.datatypeStruct.children = [];
            }
						angular.forEach(decoratedSelections1, function(child) {
							child.new = true;
							$scope.datatypeStruct.children.push(child);
						});
						console.log("$scope.datatypeStruct.children=" + $scope.datatypeStruct.children.length);
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
		function($scope, $rootScope, $modalInstance, $timeout, hl7Versions, DatatypeLibrarySvc, DatatypeService) {

			$scope.okDisabled = true;

			$scope.hl7Versions = hl7Versions;
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

      $scope.loadDatatypesByVersion = function() {
        $scope.loading = true;
        var dtLib;
          DatatypeService.get4Lib(dtLib.id).then(function(response){
            $scope.datatypeStruct = response;
          });
          $scope.loading = false;
      };

			$scope.ok = function() {
				$modalInstance.close($scope.standardSelections);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};

		});
