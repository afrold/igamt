/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl').controller('TableListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.tmpTables =[].concat($rootScope.tables);
        $scope.tableCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                 	if(parent){
                		if(parent.codes){
                			return parent.codes;
                		}else{
                			return [];
                		}
                	}else{
                		return [$rootScope.table];
                	}
                 	
                },
                getTemplate: function (node) {
                    return 'TableEditTree.html';
                }
            });


            $scope.$watch(function () {
                return $rootScope.notifyTableTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                    
                }
            });
            $scope.loading = false;
        };

        $scope.select = function (table) {
            $rootScope.table = table;
            $scope.tableCopy = {};
            $scope.tableCopy = angular.copy(table,$scope.tableCopy);
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.reset = function () {
            $scope.loadingSelection = true;
            $scope.message = "Table " + $scope.tableCopy.name + " reset successfully";
            angular.extend($rootScope.table, $scope.tableCopy);
            $scope.loadingSelection = false;
        };

        $scope.hasChildren = function(node){
            if(node.type=="table") return true;
            else return false;
        };

        $scope.onTableChange = function(node){
            $scope.refreshTree();
            node.tableLabel = null;
            $rootScope.recordChange(node,'table');
        };

        $scope.refreshTree = function(){
            if ($scope.params)
                $scope.params.refresh();
        };

        $scope.goToTable = function(table){

        };
    });
