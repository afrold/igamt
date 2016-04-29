/**
 * http://usejsdoc.org/
 */
angular.module('igl').controller('MasterDatatypeLibraryCtl',
		function($scope, $rootScope, $modal, $timeout, ngTreetableParams, DatatypeService, DatatypeLibrarySvc, FormsSelectSvc, ViewSettings) {

      $scope.datatypeLibsStruct = [];
      $scope.datatypeLibStruct = null;
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

			$scope.initDatatypeLibrary = function() {
				$scope.start = false;
        var scopes = ["USER"];
        if (true) { //($scope.isAuthenticated() && $scope.isAdmin()) {
          scopes.push("MASTER");
        }
				getDataTypeLibraryByScopes(scopes);
			};

      $scope.getMetaData = function(datatypeLibrary) {
        $scope.metaDataView = "EditDocumentMetadata.html";
        $scope.loadingSelection = true;
        $timeout(
          function () {
            $scope.loadingSelection = false;
          }, 100);
      };

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
						$scope.datatypeLibsStruct =  data;
				}).catch( function (error) {
					console.log(error);
				});
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

			$scope.edit = function(datatypeLibrary) {
				$scope.loadingSelection = true;
				$timeout(
					function () {
                  $scope.datatypeLibStruct = datatypeLibrary;
			            $scope.metaDataView = $scope.datatypeLibStruct.metaData;
                  DatatypeLibrarySvc.getDatatypesByLibrary(datatypeLibrary.id).then(function(response){
                    $scope.datatypeLibStruct.children = response;
                  });
                  $scope.metaDataView = "LibraryMetaData.html";
                  $scope.datatypeListView = "DatatypeList.html";
                 $timeout(
          function () {
            $scope.loadingSelection = false;
          }, 100);
			            $scope.loadingSelection = false;
				}, 100);
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
					}).result.then(function(hl7Version, name, ext) {
//						console.log( "hl7Version=" + hl7Version + " name=" + name + " ext=" ext);
            $scope.hl7Version = hl7Version;
					    // Decorate the user selections.
						var decoratedSelections1 = decoratedSelections(standardSelections);
					    // Push them on to the scope.
            var datatypeLibrary = DatatypeLibrarySvc.create(hl7Version, scope, name, ext);
            $scope.datatypeLibsStruct.push(datatypeLibrary);
						console.log("$scope.datatypeStruct.children=" + $scope.datatypeStruct.children.length);
          });
		};

    $scope.openDataypeList = function(hl7Version) {
      var datatypesListInstance = $modal.open({
						templateUrl : 'datatypeListDlg.html',
						controller : 'DatatypeListInstanceDlgCtl',
						resolve : {
              hl7Version : $scope.hl7Version,
              datatypeLibsStruct : DatatypeLibrarySvc.getDataTypeLibraryByScopesAndVersion
              }
      }).result.then(function() {

      });

    }

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

      $scope.scope = "HL7STANDARD";
      $scope.hl7Versions = hl7Versions;
      $scope.name = null;
      $scope.ext = null;
      $scope.hl7Version = null;
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

      $scope.loadDatatypesByScopeAndVersion = function() {
        $scope.loading = true;
            DatatypeLibrarySvc.getDatatypesByScopeAndVersion($scope.scope, hl7Version).then(function(response){
            $scope.datatypeStruct = response;
          });
          $scope.loading = false;
      };

			$scope.ok = function() {
				$modalInstance.close($scope.hl7Version, $scope.name, $scope.ext);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};

		});

angular.module('igl').controller('DatatypeListInstanceDlgCtl',
		function($scope, $rootScope, $modalInstance, hl7Version, datatypeLibsStruct, DatatypeLibrarySvc, DatatypeService) {

      $scope.hl7Version = hl7Version;
      $scope.datatypeLibsStruct = datatypeLibsStruct;

			$scope.ok = function() {
				$modalInstance.close($scope.standardSelections);
			};

			$scope.cancel = function() {
				$modalInstance.dismiss('cancel');
			};
});
