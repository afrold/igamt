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
                 	return parent && parent.codes ? parent.codes : $rootScope.table != null ? [$rootScope.table]:[];             	
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
        	$scope.loadingSelection = true;
            $rootScope.table = table;
//            $scope.tableCopy = {};
//            $scope.tableCopy = angular.copy(table);
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };
        
        
        /*
    "type": "code",
    "id": 1502,
    "code": "U", 
    "label": "Unknown",
    "codesys": "0214",
    "source": "Local",
    "codeUsage": "R"
         */
        $scope.addCode = function () {
        	$scope.loadingSelection = true;
        	$rootScope.table.codes.push({id:-1, type: 'code', code:'', label:'', codesys:'', source:'', codeUsage:'R'});
        	if ($scope.params) $scope.params.refresh();
        	$scope.loadingSelection = false;
        };
        
        $scope.deleteCode = function (code) {
        	$scope.loadingSelection = true;
        	$rootScope.table.codes.splice($rootScope.table.codes.indexOf(code),1);
        	if ($scope.params) $scope.params.refresh();
        	$scope.loadingSelection = false;
        };
       
        $scope.reset = function () {
            $scope.loadingSelection = true;
            $scope.message = "Table " + $scope.tableCopy.name + " reset successfully";
            angular.extend($rootScope.table, $scope.tableCopy);
            $scope.loadingSelection = false;
        };

        $scope.hasChildren = function(node){
            if(node && node.type=="table") return true;
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
    });
