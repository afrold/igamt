/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').controller('ValueSetSectionCtrl', function ($scope, $rootScope, TableLibrarySvc, $timeout, blockUI) {

    $scope.allTables = [];
    $scope.selectedTables = [];
    $scope.checkedAll = {value: false};
    $scope.tokenPromise = undefined;
    $scope.countInternals = 0;
    $scope.filterCriteria = {
        export: "*",
        sourceType: "*",
        bindingIdentifier: "",
        name: "",
        scope: "*"
    };
    $scope.include = angular.copy($rootScope.igdocument.profile.tableLibrary.exportConfig.include);

    $scope.scopeCountMap = {};

    $rootScope.$on("event:initTableLibrarySection", function (event) {
        $scope.initSection();
    });

    $scope.itemsByPage = 15;

    $scope.initSection = function () {
        $scope.allTables = [];
        $scope.scopeCountMap = {};
        $scope.selectedTables = [];
        $scope.allTables = angular.copy($rootScope.tables);
        $scope.displayed = [].concat($scope.allTables);
        $scope.countInternals = 0;
        for (var i = 0; i < $scope.displayed.length; i++) {
            var table = $scope.displayed[i];
            if ($scope.isConfigEnabled()) {
                setExport(table, $scope.include.indexOf(table.id) > -1);
            }
            $scope.countInternals = table.sourceType == 'INTERNAL' ? $scope.countInternals + 1 : $scope.countInternals;
            if ($scope.scopeCountMap[table.scope] == undefined)
                $scope.scopeCountMap[table.scope] = 0;
            $scope.scopeCountMap[table.scope] = $scope.scopeCountMap[table.scope] + 1;
        }
    };

    $scope.clearFilters = function () {
        $scope.filterCriteria = {
            export: "*",
            sourceType: "*",
            bindingIdentifier: "",
            name: "",
            scope: "*"
        };
        $scope.filter();
    };

    $scope.filter = function () {
        $timeout(function () {
            if ($scope.tokenPromise) {
                $timeout.cancel($scope.tokenPromise);
                $scope.tokenPromise = undefined;
            }
            $scope.tokenPromise = $timeout(function () {
                $scope.displayed = _.filter($scope.allTables, function (table) {
                    return $scope.matches(table);
                });
            });
        });
    };


    $scope.matches = function (table) {
        return ($scope.filterCriteria.sourceType == "*" || table.sourceType == $scope.filterCriteria.sourceType)
            && ($scope.filterCriteria.export == "*" || $scope.getExported(table) == $scope.filterCriteria.export)
            && ($scope.filterCriteria.name == "" || table.name.indexOf($scope.filterCriteria.name) > -1)
            && ($scope.filterCriteria.bindingIdentifier == "" || table.bindingIdentifier.indexOf($scope.filterCriteria.bindingIdentifier) > -1)
            && ($scope.filterCriteria.scope == "*" || table.scope === $scope.filterCriteria.scope);
    };


    $scope.checkValueSet = function (table) {
        setExport(table, $scope.selectedTables.indexOf(table.id) <= -1);
        if (!$scope.matches(table)) {
            var index = $scope.displayed.indexOf(table);
            if (index > -1)
                $scope.displayed.splice(index, 1);
        }
        $scope.recordChanged();
    };

    $scope.isConfigEnabled = function () {
        return $scope.include && $scope.include != null;
    };

    $scope.enableConfig = function () {
        $scope.checkedAll = {value: false};
        $scope.tokenPromise = undefined;
        $scope.countInternals = 0;
        if ($scope.isConfigEnabled()) {
            $scope.include = null;
        } else {
            $scope.include = [];
        }
        $scope.initSection();
        $scope.recordChanged();
    };

    $scope.getExported = function (table) {
        return $scope.isChecked(table) ? 'Exported' : 'Not Exported';
    };


    var setExport = function (table, exported) {
        var index = $scope.selectedTables.indexOf(table.id);
        if (exported) {
            if (index <= -1) {
                $scope.selectedTables.push(table.id);
            }
        } else {
            if (index > -1) {
                $scope.selectedTables.splice(index, 1);
            }
        }
    };


    $scope.isChecked = function (table) {
        return $scope.selectedTables.indexOf(table.id) > -1;
    };

    $scope.isIndeterminate = function () {
        return ($scope.selectedTables.length !== 0 &&
        $scope.selectedTables.length !== $scope.displayed.length);
    };

    $scope.isCheckedAll = function () {
        return ($scope.selectedTables.length !== 0 &&
        $scope.selectedTables.length == $scope.displayed.length);
    };

    $scope.checkAllValueSets = function () {
        var checked = !$scope.checkedAll.value;
        for (var i = 0; i < $scope.displayed.length; i++) {
            setExport($scope.displayed[i], checked);
        }
        $scope.filter();
        $scope.recordChanged();
    };


    $scope.displayType = function (row) {
        return row.sourceType == 'INTERNAL' || row.sourceType == null ? 'Internally Managed' : 'Externally Managed';
    };

    $scope.itemsByPage = 15;

    $scope.saveConfig = function () {
        if ($rootScope.igdocument != null) {
            var tableLibrary = $rootScope.section;
            var exportConfig = tableLibrary.exportConfig;
            if ($scope.isConfigEnabled()) {
                exportConfig.include = [];
                if ($scope.selectedTables.length > 0) {
                    for (var i = 0; i < $scope.selectedTables.length; i++) {
                        exportConfig.include.push($scope.selectedTables[i]);
                    }
                }
            } else {
                exportConfig.include = null;
            }

            TableLibrarySvc.saveSection($rootScope.igdocument.profile.tableLibrary.id, tableLibrary).then(function (result) {
                $scope.saving = false;
                $scope.saved = true;
                $rootScope.section.dateUpdated = result.date;
                $rootScope.igdocument.dateUpdated = $rootScope.section.dateUpdated;
                $rootScope.igdocument.profile.tableLibrary['exportConfig'] = exportConfig;
                $rootScope.igdocument.profile.tableLibrary['sectionTitle'] = tableLibrary['sectionTitle'];
                $rootScope.igdocument.profile.tableLibrary['sectionContents'] = tableLibrary['sectionContents'];
                $rootScope.igdocument.profile.tableLibrary['sectionDescription'] = tableLibrary['sectionDescription'];

                if ($scope.editForm) {
                    $scope.editForm.$setPristine();
                    $scope.editForm.$dirty = false;
                }
                $rootScope.clearChanges();
                $rootScope.msg().text = "sectionSaved";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;

            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
                $scope.saved = false;
                $scope.saving = false;
            });
        }
    };

    $scope.resetSection = function () {
        $scope.include = angular.copy($rootScope.igdocument.profile.tableLibrary.exportConfig.include);
        $rootScope.section['sectionTitle'] = $rootScope.igdocument.profile.tableLibrary['sectionTitle'];
        $rootScope.section['sectionContents'] = $rootScope.igdocument.profile.tableLibrary['sectionContents'];
        $rootScope.section['sectionDescription'] = $rootScope.igdocument.profile.tableLibrary['sectionDescription'];
        $scope.initSection();
        $scope.editForm.$setPristine();
        $scope.editForm.$dirty = false;
        $rootScope.clearChanges();
    };


});
