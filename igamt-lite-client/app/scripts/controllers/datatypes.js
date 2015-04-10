/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter, $http,$modal) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.tmpDatatypes =[].concat($rootScope.datatypes);
        $scope.datatypeCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.datatype ? parent.datatype.components: parent.components : $rootScope.datatype != null ? [$rootScope.datatype]:[];
                },
                getTemplate: function (node) {
                    return 'DatatypeEditTree.html';
                }
                ,
                options: {
                    initialState: 'expanded'
                }
            });

            $scope.$watch(function () {
                return $rootScope.notifyDtTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                }
            });

            $scope.loading = false;
        };

        $scope.select = function (datatype) {
            $rootScope.datatype = datatype;
            $rootScope.datatype["type"] = "datatype";
            if ($scope.params)
                $scope.params.refresh();
//            $rootScope.go('/profiles#datatypeDef');
            $scope.loadingSelection = false;
         };

        $scope.flavor = function (datatype) {
            var flavor = angular.copy(datatype);
            var id = (Math.floor(Math.random()*10000000) + 1);
            flavor.id = - 1 * id;
            flavor.label = datatype.label + "_"+id;
            if(flavor.components != undefined && flavor.components != null && flavor.components.length != 0){
                for(var i=0; i < flavor.components.length; i++){
                    flavor.components[i].id = -1* (Math.floor(Math.random()*10000000) + 1);
                    flavor.components[i].datatype = datatype.components[i].datatype;
                }
            }
            $rootScope.datatypes.splice(0, 0, flavor);
            $rootScope.datatype = flavor;
            var tmp = angular.copy(flavor);
            delete tmp.type;
            if(tmp.components != undefined && tmp.components != null && tmp.components.length != 0){
                angular.forEach(tmp.components, function (component) {
                    component.datatype = {id:component.datatype.id};
                    if(component.table != undefined) {
                        component.table = {id: component.table.id};
                    }
                });
            }

            var predicates = tmp['predicates'];
            if( predicates!= undefined && predicates != null && predicates.length != 0){
                angular.forEach(predicates, function (predicate) {
                    predicate.id = -1 * (Math.floor(Math.random()*10000000) + 1);
                });
            }

            var conformanceStatements = tmp['conformanceStatements'];
            if(conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0){
                angular.forEach(conformanceStatements, function (conformanceStatement) {
                    conformanceStatement.id = -1 * (Math.floor(Math.random()*10000000) + 1);
                });
            }
            $rootScope.recordChange2('datatype',tmp.id,null,tmp);

            $scope.select(flavor);

        };

        $scope.close = function(){
            $rootScope.datatype = null;
             if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.delete = function (datatype) {
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                $rootScope.findDatatypeRefs(datatype,segment);
            });
            if( $rootScope.references != null &&  $rootScope.references.length > 0){
                $scope.abortDelete(datatype);
            }else {
                $scope.confirmDelete(datatype);
            }
        };

        $scope.abortDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'DatatypeReferencesCtrl.html',
                controller: 'DatatypeReferencesCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
            });
        };

        $scope.confirmDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeDeleteCtrl.html',
                controller: 'ConfirmDatatypeDeleteCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
             });
        };


        $scope.hasChildren = function(node){
            return node && node != null && node.datatype && node.datatype.components != null && node.datatype.components.length >0;
        };


        $scope.validateLabel = function (label, name) {
           if(label && !label.startsWith(name)){
               return false;
           }
           return true;
        };


        $scope.onDatatypeChange = function(node){
            $scope.refreshTree();
            node.datatypeLabel = null;
            $rootScope.recordChange(node,'datatype');
         };

        $scope.refreshTree = function(){
            if ($scope.params)
                $scope.params.refresh();
        };
		
		$scope.goToTable = function(table){
        	$rootScope.table = table;
            $rootScope.notifyTableTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(4);
        };
        
        $scope.deleteTable = function (node) {
        	node.table = null;
        	$rootScope.recordChange(node,'table');
        }
        
        $scope.showTableMapModal = false;
        
        $scope.displayedTableCollection = [].concat($scope.tables);
        $scope.displayedCodeCollection = [];
        
        
        $scope.selectedNode = null;
		$scope.mapTable = function(node) {
			$scope.selectedNode = node;
			$scope.selectedTable = node.table;
			if($scope.selectedTable != undefined)
				$scope.displayedCodeCollection = [].concat($scope.selectedTable.codes);
			$scope.showTableMapModal = !$scope.showTableMapModal;
		};
		
		$scope.selectedTable = null;
		$scope.selectTable = function(table){
			$scope.selectedTable = table;
			$scope.displayedCodeCollection = [].concat($scope.selectedTable.codes);
	        
		}
		
		$scope.mappingTable = function(){
			$scope.selectedNode.table = $scope.selectedTable;
			$rootScope.recordChange($scope.selectedNode,'table');
			$scope.showTableMapModal = false;
		}

    });




angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope,$filter) {
         $scope.formName = "form_"+ new Date().getTime();
    });



angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function ($scope, $modalInstance, dtToDelete,$rootScope) {
    $scope.dtToDelete = dtToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;

        // remove any change made to components
        if($scope.dtToDelete.components != undefined && $scope.dtToDelete.components != null && $scope.dtToDelete.components.length > 0){
            angular.forEach($scope.dtToDelete.components, function (component) {
                if($rootScope.changes['component'] && $rootScope.changes['component'][component.id] && $rootScope.changes['component'][component.id]){
                    delete $rootScope.changes['component'][component.id];
                }
            });
        }

        if( $rootScope.changes['component'] && Object.getOwnPropertyNames($rootScope.changes['component']).length === 0){
            delete $rootScope.changes['component'];
        }

        // remove any change made to datatype
        if($rootScope.changes['datatype'] != undefined  && $rootScope.changes['datatype'][$scope.dtToDelete.id] != undefined){
            if($scope.dtToDelete.id < 0){
                delete $rootScope.changes['datatype'][$scope.dtToDelete.id];
                if( Object.getOwnPropertyNames($rootScope.changes['datatype']).length === 0){
                    delete $rootScope.changes['datatype'];
                }
            }else{
                $rootScope.changes['datatype'][$scope.dtToDelete.id] = null;
            }
        }

        var index = $rootScope.datatypes.indexOf($scope.dtToDelete);
        if (index > -1) $rootScope.datatypes.splice(index, 1);

        $rootScope.generalInfo.type = 'info';
        $rootScope.generalInfo.message = "Datatype " + $scope.dtToDelete.label + " deleted successfully";

        if($rootScope.datatype === $scope.dtToDelete){
            $rootScope.datatype = null;
        }

        $rootScope.references = [];
        $modalInstance.close($scope.dtToDelete);



    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('DatatypeReferencesCtrl', function ($scope, $modalInstance, dtToDelete) {

    $scope.dtToDelete = dtToDelete;

    $scope.ok = function () {
        $modalInstance.close($scope.dtToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
