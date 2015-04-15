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
            id: $rootScope.newTableFakeId,
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
        $rootScope.table = newTable;

        $rootScope.listToBeAddedTables.push(newTable);
        $rootScope.recordChange2('table', "add", null, $rootScope.listToBeAddedTables);

    };

    $scope.addCode = function () {
        $rootScope.newCodeFakeId = $rootScope.newCodeFakeId - 1;
        var newCode = {
            id: $rootScope.newCodeFakeId,
            type: 'code',
            code: '',
            label: '',
            codesys: '',
            source: '',
            codeUsage: ''
        };


        $rootScope.table.codes.unshift(newCode);
        if (!$scope.isNewTable($rootScope.table.id)) {
            $rootScope.listToBeAddedCodes.push({tableId: $rootScope.table.id, code: newCode});
            $rootScope.recordChange2('code', "add", null, $rootScope.listToBeAddedCodes);
        }


    };

    $scope.deleteTable = function (table) {
        if (!$scope.isNewTableThenDelete(table.id)) {
            $rootScope.listToBeDeletedTables.push({id: table.id});
            $rootScope.recordChange2('table', "delete", null, $rootScope.listToBeDeletedTables);
        }
        $rootScope.tables.splice($rootScope.tables.indexOf(table), 1);
        $scope.close();
    };

    $scope.deleteCode = function (code) {
        if (!$scope.isNewCodeThenDelete(code.id)) {
            $rootScope.listToBeDeletedCodes.push({id: code.id});
            $rootScope.recordChange2('code', "delete", null, $rootScope.listToBeDeletedCodes);
        }
        $rootScope.table.codes.splice($rootScope.table.codes.indexOf(code), 1);
    };

    $scope.isNewCodeThenDelete = function (id) {
        for (var i = 0, len = $rootScope.listToBeAddedCodes.length; i < len; i++) {
            if ($rootScope.listToBeAddedCodes[i].code.id === id) {
                $rootScope.listToBeAddedCodes.splice(i, 1);
                return true;
            }
        }
        return false;
    };

    $scope.isNewTableThenDelete = function (id) {
        for (var i = 0, len = $rootScope.listToBeAddedTables.length; i < len; i++) {
            if ($rootScope.listToBeAddedTables[i].id === id) {
                $rootScope.listToBeAddedTables.splice(i, 1);
                return true;
            }
        }
        return false;
    };

    $scope.isNewCode = function (id) {
        for (var i = 0, len = $rootScope.listToBeAddedCodes.length; i < len; i++) {
            if ($rootScope.listToBeAddedCodes[i].code.id === id) {
                return true;
            }
        }
        return false;
    };

    $scope.isNewTable = function (id) {
        for (var i = 0, len = $rootScope.listToBeAddedTables.length; i < len; i++) {
            if ($rootScope.listToBeAddedTables[i].id === id) {
                return true;
            }
        }
        return false;
    };

    $scope.close = function () {
        $rootScope.table = null;
    };

    $scope.cloneTable = function (table) {
        $rootScope.newTableFakeId = $rootScope.newTableFakeId - 1;
        var newTable = angular.fromJson({
            id: $rootScope.newTableFakeId,
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
            newTable.codes.push({
                id: $rootScope.newCodeFakeId,
                type: 'code',
                code: table.codes[i].code,
                label: table.codes[i].label,
                codesys: table.codes[i].codesys,
                source: table.codes[i].source,
                codeUsage: table.codes[i].codeUsage
            });
        }

        $rootScope.tables.push(newTable);
        $rootScope.table = newTable;

        $rootScope.listToBeAddedTables.push(newTable);
        $rootScope.recordChange2('table', "add", null, $rootScope.listToBeAddedTables);
    };

    $scope.recordChangeCode = function (code, type, tableId) {
        if (!$scope.isNewTable(tableId)) {
            if (!$scope.isNewCode(code.id)) {
                $rootScope.recordChangeForEdit(code, type);
            }
        }
    };

    $scope.recordChangeTable = function (table, type) {
        if (!$scope.isNewTable(table.id)) {
            $rootScope.recordChangeForEdit(table, type);
        }
    };

    $scope.setAllCodeUsage = function (table, usage) {
        for (var i = 0, len = table.codes.length; i < len; i++) {
            if (table.codes[i].codeUsage !== usage) {
                table.codes[i].codeUsage = usage;
                if (!$scope.isNewTable(table.id) && !$scope.isNewCode(table.codes[i].id)) {
                    $rootScope.recordChangeForEdit(table.codes[i], 'codeUsage');
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
            $rootScope.listToBeDeletedTables.push({id: tableToDelete.id});
            $rootScope.recordChange2('table', "delete", null, $rootScope.listToBeDeletedTables);
        }
        $rootScope.tables.splice($rootScope.tables.indexOf(tableToDelete), 1);

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
        for (var i = 0, len = $rootScope.listToBeAddedTables.length; i < len; i++) {
            if ($rootScope.listToBeAddedTables[i].table.id === id) {
                $rootScope.listToBeAddedTables.splice(i, 1);
                return true;
            }
        }
        return false;
    }
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