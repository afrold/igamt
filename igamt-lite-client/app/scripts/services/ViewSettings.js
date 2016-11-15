/**
 * Created by haffo on 5/4/15.
 */

angular.module('igl').factory('ViewSettings',
    ['StorageService', function (StorageService) {
        var columnOptions = [
            { id: "datatype", label: "Datatype"},
            { id: "valueSet", label: "Value Set"},
            { id: "predicate", label: "Predicate"},
            { id: "confStatement", label: "Conf. Statement"},
            { id: "defText", label: "Defin. Text"},
            { id: "comment", label: "Comment"}
        ];
        var selectedColumns =  {
            "datatype": true,
            "valueSet": true,
            "predicate": true,
            "confStatement": true,
            "defText": true,
            "comment": true
        };

//        var visibleColumns = StorageService.get(StorageService.TABLE_COLUMN_SETTINGS_KEY) == null ? angular.copy(columnOptions) : angular.fromJson(StorageService.get(StorageService.TABLE_COLUMN_SETTINGS_KEY));
        var visibleColumns = angular.copy(columnOptions);
        var ViewSettings = {
            columnOptions: columnOptions,
            visibleColumns: visibleColumns,
            selectedColumns: selectedColumns,
            translations: {buttonDefaultText: 'Visible Columns'},
            extra: {displayProp: 'label', buttonClasses: 'btn btn-xs btn-primary', showCheckAll: false, showUncheckAll: false, scrollable: false},
            tableRelevance:StorageService.get(StorageService.TABLE_RELEVANCE_SETTINGS) == null ? false : StorageService.get(StorageService.TABLE_RELEVANCE_SETTINGS),
            tableConcise:StorageService.get(StorageService.TABLE_CONCISE_SETTINGS) == null ? false : StorageService.get(StorageService.TABLE_CONCISE_SETTINGS),
            tableCollapse:StorageService.get(StorageService.TABLE_COLLAPSE_SETTINGS) == null ? true : StorageService.get(StorageService.TABLE_COLLAPSE_SETTINGS),
            tableReadonly:StorageService.get(StorageService.TABLE_READONLY_SETTINGS) == null ? false : StorageService.get(StorageService.TABLE_READONLY_SETTINGS),
            events: {
                onItemSelect: function (item) {
                    console.log("selected " + item);
                    ViewSettings.setVisibleColumns();
                    ViewSettings.selectedColumns[item.id] = true;
                },
                onItemDeselect: function (item) {
                    console.log("deselected " + item);
                    ViewSettings.setVisibleColumns();
                    ViewSettings.selectedColumns[item.id] = false;
                }
            },
            setVisibleColumns: function () {
                StorageService.set(StorageService.TABLE_COLUMN_SETTINGS_KEY, angular.toJson(ViewSettings.visibleColumns));
            },
            setTableConcise: function (concise) {
                ViewSettings.tableConcise = concise;
                StorageService.set(StorageService.TABLE_CONCISE_SETTINGS, ViewSettings.tableConcise);
            },
            setTableRelevance: function (relevance) {
                ViewSettings.tableRelevance = relevance;
                StorageService.set(StorageService.TABLE_RELEVANCE_SETTINGS, ViewSettings.tableRelevance);
            },
            setTableCollapse: function (collapse) {
                ViewSettings.tableCollapse = collapse;
                StorageService.set(StorageService.TABLE_COLLAPSE_SETTINGS, ViewSettings.tableCollapse);
            },
            setTableReadonly: function (value) {
                ViewSettings.tableReadonly = value;
                StorageService.set(StorageService.TABLE_READONLY_SETTINGS, ViewSettings.tableReadonly);
            },
            isVisibleColumn: function (column) {
                return ViewSettings.selectedColumns[column];
            }
        };
        return ViewSettings;
    }]);

