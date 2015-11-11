/**
 * Created by haffo on 5/4/15.
 */

(function (angular) {
    'use strict';
    var mod = angular.module('table-settings', []);

//    mod.directive('table-column-toggle', [
//        function () {
//            return {
//                restrict: 'A',
//                scope: {
//                    type: '@',
//                    message: '=',
//                    dqa: '=',
//                    tree: '=',
//                    editor: '=',
//                    cursor: '=',
//                    format: '='
//                },
//                templateUrl: 'directives/table-column-toggle/table-column-toggle.html',
//                replace: false,
//                controller: 'TableColumnToggleCtrl'
//            };
//        }
//    ]);
//
//    mod
//        .controller('TableColumnToggleCtrl', ['$scope', '$filter', '$modal', '$rootScope', 'TableColumnSettings', function ($scope, $filter, $modal, $rootScope, TableColumnSettings) {
//
//        }]);


    mod.factory('ColumnSettings',
        ['StorageService', function (StorageService) {
            var options = [
                { id: "usage", label: "Usage"},
                { id: "cardinality", label: "Cardinality"},
                { id: "length", label: "Length"},
                { id: "confLength", label: "Conf. Length"},
                { id: "datatype", label: "Datatype"},
                { id: "valueSet", label: "Value Set"},
                { id: "predicate", label: "Predicate"},
                { id: "confStatement", label: "Conf. Statement"},
                { id: "defText", label: "Def. Text"},
                { id: "comment", label: "Comment"}
            ];

            var visibleColumns = StorageService.get(StorageService.TABLE_COLUMN_SETTINGS_KEY) == null ?  angular.copy(options) : angular.fromJson(StorageService.get(StorageService.TABLE_COLUMN_SETTINGS_KEY));

            var ColumnSettings = {
                options: options,
                visibleColumns: visibleColumns,
                extra: {displayProp: 'id', buttonClasses: 'btn btn-xs btn-info', buttonDefaultText: 'Columns', showCheckAll:false, showUncheckAll:false,
                    smartButtonTextConverter: function(itemText, originalItem) {
                        return 'Columns';
                    }
                },
                events:{
                    onItemSelect: function(item){
                        ColumnSettings.save();
                    },
                    onItemDeselect: function(item){
                        ColumnSettings.save();
                    }
                },
                set: function (visibleColumns) {
                    ColumnSettings.visibleColumns = visibleColumns;
                    StorageService.set(StorageService.TABLE_COLUMN_SETTINGS_KEY, angular.toJson(visibleColumns));
                },
                save: function () {
                    StorageService.set(StorageService.TABLE_COLUMN_SETTINGS_KEY, angular.toJson(ColumnSettings.visibleColumns));
                },
                isVisibleColumn: function (column) {
                    for(var i=0; i < ColumnSettings.visibleColumns.length;i++){
                        if(ColumnSettings.visibleColumns[i].id === column){
                            return true;
                        }
                    }
                    return false;
                }
            };
            return ColumnSettings;
        }]);


})(angular);