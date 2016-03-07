'use strict';
angular.module('igl').factory('StorageService',
    ['localStorageService', function (localStorageService) {
        var service = {
            TABLE_COLUMN_SETTINGS_KEY: 'SETTINGS_KEY',
            SELECTED_IG_DOCUMENT_TYPE:'SelectedIgDocumentType',
            SELECTED_IG_DOCUMENT_ID:'SelectedIgDocumentId',
            APP_VERSION:'APP_VERSION',

            remove: function (key) {
                return localStorageService.remove(key);
            },

            removeList: function removeItems(key1, key2, key3) {
                return localStorageService.remove(key1, key2, key3);
            },

            clearAll: function () {
                return localStorageService.clearAll();
            },
            set: function (key, val) {
                return localStorageService.set(key, val);
            },
            get: function (key) {
                return localStorageService.get(key);
            },
            setSelectedIgDocumentType: function (val) {
                this.set(this.SELECTED_IG_DOCUMENT_TYPE,val);
            },
            getSelectedIgDocumentType: function (key) {
                return this.get(this.SELECTED_IG_DOCUMENT_TYPE);
            },
            setSelectedIgDocumentId: function (id) {
                this.set(this.SELECTED_IG_DOCUMENT_ID,id);
            },
            getSelectedIgDocumentId: function () {
                return this.get(this.SELECTED_IG_DOCUMENT_ID);
            },
            setAppVersion: function (version) {
                this.set(this.APP_VERSION,version);
            },
            getAppVersion: function () {
                return this.get(this.APP_VERSION);
            }
        };
        return service;
    }]
);
