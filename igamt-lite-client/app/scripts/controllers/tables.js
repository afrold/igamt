/**
 * Created by Jungyub on 4/01/15.
 */

angular.module('igl').controller('TableListCtrl', function ($scope, $rootScope, Restangular, $filter, $http, $modal) {
    $scope.loading = false;
    $scope.loadingSelection = false;
    $scope.tmpTables = [].concat($rootScope.tables);
    $scope.readonly = false;
    $scope.saved = false;
    $scope.message = false;
    $scope.params = null;
    $scope.init = function () {
    };

    $scope.select = function (table) {
        $scope.loadingSelection = true;
        $rootScope.table = table;
        if ($scope.params)
            $scope.params.refresh();
        $scope.loadingSelection = false;
    };

    $scope.addTable = function () {
        $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
        var newTable = angular.fromJson({
            id: new ObjectId().toString(),
            type: 'table',
            mappingAlternateId: '',
            mappingId: '',
            name: '',
            version: '',
            codesys: '',
            oid: '',
            tableType: '',
            stability: '',
            extensibility: '',
            codes: []
        });
        $rootScope.tables.push(newTable);
        
        $rootScope.tablesMap[newTable.id] = newTable;
        
        $rootScope.table = newTable;

        $rootScope.recordChangeForEdit2('table', "add", newTable.id,'table', newTable);

    };

    $scope.addCode = function () {
        $rootScope.newCodeFakeId = $rootScope.newCodeFakeId - 1;
        var newCode = {
            id: new ObjectId().toString(),
            type: 'code',
            code: '',
            label: '',
            codesys: '',
            source: '',
            codeUsage: ''
        };


        $rootScope.table.codes.unshift(newCode);
        var newCodeBlock = {targetType:'table', targetId:$rootScope.table.id, obj:newCode};
        if(!$scope.isNewObject('table', 'addd', $rootScope.table.id)){
        	$rootScope.recordChangeForEdit2('code', "add", null,'code', newCodeBlock);
        }
    };

    $scope.deleteCode = function (code) {
        if (!$scope.isNewCodeThenDelete(code.id)) {
            $rootScope.recordChangeForEdit2('code', "delete", code.id,'id', code.id);
        }
        $rootScope.table.codes.splice($rootScope.table.codes.indexOf(code), 1);
    };

    $scope.isNewCodeThenDelete = function (id) {
    	if($rootScope.isNewObject('code', 'add',id)){
    		if($rootScope.changes['code'] !== undefined && $rootScope.changes['code']['add'] !== undefined) {
    			for (var i = 0; i < $rootScope.changes['code']['add'].length; i++) {
        			var tmp = $rootScope.changes['code']['add'][i];
        			if (tmp.obj.id === id) {
                        $rootScope.changes['code']['add'].splice(i, 1);
                        if ($rootScope.changes["code"]["add"] && $rootScope.changes["code"]["add"].length === 0) {
                            delete  $rootScope.changes["code"]["add"];
                        }

                        if ($rootScope.changes["code"] && Object.getOwnPropertyNames($rootScope.changes["code"]).length === 0) {
                            delete  $rootScope.changes["code"];
                        }
                        return true;
                   }
        		}
    		}
    		return true;
    	}
    	if($rootScope.changes['code'] !== undefined && $rootScope.changes['code']['edit'] !== undefined) {
    		for (var i = 0; i < $rootScope.changes['code']['edit'].length; i++) {
    			var tmp = $rootScope.changes['code']['edit'][i];
    			if (tmp.id === id) {
                    $rootScope.changes['code']['edit'].splice(i, 1);
                    if ($rootScope.changes["code"]["edit"] && $rootScope.changes["code"]["edit"].length === 0) {
                        delete  $rootScope.changes["code"]["edit"];
                    }

                    if ($rootScope.changes["code"] && Object.getOwnPropertyNames($rootScope.changes["code"]).length === 0) {
                        delete  $rootScope.changes["code"];
                    }
                    return false;
               }
    		}
    		return false;
    	}
        return false;
    };

    $scope.isNewCode = function (id) {
//        if (id < 0) return true;
//        else return false;
//
        return $scope.isNewObject('code', 'addd', id);
    };

    $scope.isNewTable = function (id) {
//    	if (id < 0) return true;
//        else return false;
        return $scope.isNewObject('table', 'addd',id);
    };

    $scope.close = function () {
        $rootScope.table = null;
    };

    $scope.cloneTable = function (table) {
        $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
        var newTable = angular.fromJson({
            id:new ObjectId().toString(),
            type: '',
            mappingAlternateId: '',
            mappingId: '',
            name: '',
            version: '',
            codesys: '',
            oid: '',
            tableType: '',
            stability: '',
            extensibility: '',
            codes: []
        });
        newTable.type = 'table';
        newTable.mappingId = table.mappingId + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
        newTable.mappingAlternateId = table.mappingAlternateId + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
        newTable.name = table.name + '_' + $rootScope.postfixCloneTable + $rootScope.newTableFakeId;
        newTable.version = table.version;
        newTable.oid = table.oid;
        newTable.tableType = table.tableType;
        newTable.stability = table.stability;
        newTable.extensibility = table.extensibility;

        for (var i = 0, len1 = table.codes.length; i < len1; i++) {
            $rootScope.newCodeFakeId = $rootScope.newCodeFakeId - 1;
            var newCode = {
                    id: new ObjectId().toString(),
                    type: 'code',
                    code: table.codes[i].code,
                    label: table.codes[i].label,
                    codesys: table.codes[i].codesys,
                    source: table.codes[i].source,
                    codeUsage: table.codes[i].codeUsage
                };
            
            newTable.codes.push(newCode);
        }

        $rootScope.tables.push(newTable);
        $rootScope.table = newTable;
        $rootScope.tablesMap[newTable.id] = newTable;
        $rootScope.recordChangeForEdit2('table', "add", newTable.id,'table', newTable);
    };

    $scope.recordChangeCode = function (code, valueType, value ,tableId) {
        if (!$scope.isNewTable(tableId)) {
            if (!$scope.isNewCode(code.id)) {
            	$rootScope.recordChangeForEdit2('code', 'edit',code.id,valueType,value);  
            }
        }
    };

    $scope.recordChangeTable = function (table, valueType, value) {
        if (!$scope.isNewTable(table.id)) {
            $rootScope.recordChangeForEdit2('table', 'edit',table.id,valueType,value);            
        }
    };

    $scope.setAllCodeUsage = function (table, usage) {
        for (var i = 0, len = table.codes.length; i < len; i++) {
            if (table.codes[i].codeUsage !== usage) {
                table.codes[i].codeUsage = usage;
                if (!$scope.isNewTable(table.id) && !$scope.isNewCode(table.codes[i].id)) {
                    $rootScope.recordChangeForEdit2('code','edit',table.codes[i].id,'codeUsage',usage);  
                }
            }
        }
    };

    $scope.delete = function (table) {
        $rootScope.references = [];
        angular.forEach($rootScope.segments, function (segment) {
            $rootScope.findTableRefs(table, segment);
        });
        if ($rootScope.references != null && $rootScope.references.length > 0) {
            $scope.abortDelete(table);
        } else {
            $scope.confirmDelete(table);
        }
    };

    $scope.abortDelete = function (table) {
        var modalInstance = $modal.open({
            templateUrl: 'ValueSetReferencesCtrl.html',
            controller: 'ValueSetReferencesCtrl',
            resolve: {
                tableToDelete: function () {
                    return table;
                }
            }
        });
        modalInstance.result.then(function (table) {
            $scope.tableToDelete = table;
        }, function () {
        });
    };

    $scope.confirmDelete = function (table) {
        var modalInstance = $modal.open({
            templateUrl: 'ConfirmValueSetDeleteCtrl.html',
            controller: 'ConfirmValueSetDeleteCtrl',
            resolve: {
                tableToDelete: function () {
                    return table;
                }
            }
        });
        modalInstance.result.then(function (table) {
            $scope.tableToDelete = table;
        }, function () {
        });
    };
});

angular.module('igl').controller('TableModalCtrl', function ($scope) {
    $scope.showModal = false;
    $scope.toggleModal = function () {
        $scope.showModal = !$scope.showModal;
    };
});

angular.module('igl').controller('ConfirmValueSetDeleteCtrl', function ($scope, $modalInstance, tableToDelete, $rootScope) {
    $scope.tableToDelete = tableToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;

        if (!$scope.isNewTableThenDelete(tableToDelete.id)) {
        	$rootScope.recordChangeForEdit2('table', "delete", tableToDelete.id,'id', tableToDelete.id);
        }
        $rootScope.tables.splice($rootScope.tables.indexOf(tableToDelete), 1);
        $rootScope.tablesMap[tableToDelete.id] = undefined;
        
        
        $rootScope.generalInfo.type = 'info';
        $rootScope.generalInfo.message = "Table " + $scope.tableToDelete.mappingId + " deleted successfully";

        if ($rootScope.table === $scope.tableToDelete) {
            $rootScope.table = null;
        }

        $rootScope.references = [];
        $modalInstance.close($scope.tableToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };




    $scope.isNewTableThenDelete = function (id) {
    	if($rootScope.isNewObject('table', 'add', id)){
    		if($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['add'] !== undefined) {
    			for (var i = 0; i < $rootScope.changes['table']['add'].length; i++) {
        			var tmp = $rootScope.changes['table']['add'][i];
        			if (tmp.id == id) {
                        $rootScope.changes['table']['add'].splice(i, 1);
                        if ($rootScope.changes["table"]["add"] && $rootScope.changes["table"]["add"].length === 0) {
                            delete  $rootScope.changes["table"]["add"];
                        }

                        if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
                            delete  $rootScope.changes["table"];
                        }
                        return true;
                   }
        		}
    		}
    		return true;
    	}
    	if($rootScope.changes['table'] !== undefined && $rootScope.changes['table']['edit'] !== undefined) {
    		for (var i = 0; i < $rootScope.changes['table']['edit'].length; i++) {
    			var tmp = $rootScope.changes['table']['edit'][i];
    			if (tmp.id === id) {
                    $rootScope.changes['table']['edit'].splice(i, 1);
                    if ($rootScope.changes["table"]["edit"] && $rootScope.changes["table"]["edit"].length === 0) {
                        delete  $rootScope.changes["table"]["edit"];
                    }

                    if ($rootScope.changes["table"] && Object.getOwnPropertyNames($rootScope.changes["table"]).length === 0) {
                        delete  $rootScope.changes["table"];
                    }
                    return false;
               }
    		}
    		return false;
    	}
        return false;
    };
});

angular.module('igl').controller('ValueSetReferencesCtrl', function ($scope, $modalInstance, tableToDelete) {

    $scope.tableToDelete = tableToDelete;

    $scope.ok = function () {
        $modalInstance.close($scope.tableToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});