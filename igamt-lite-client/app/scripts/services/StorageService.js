'use strict';
angular.module('igl').factory('StorageService',
    ['localStorageService', function (localStorageService) {
        var service = {
            TABLE_COLUMN_SETTINGS_KEY: 'SETTINGS_KEY',
            SELECTED_IG_DOCUMENT_TYPE:'SelectedIgDocumentType',
            SELECTED_IG_DOCUMENT_ID:'SelectedIgDocumentId',
            APP_VERSION:'APP_VERSION',
            TABLE_CONCISE_SETTINGS:'TABLE_CONCISE_SETTINGS',
            TABLE_RELEVANCE_SETTINGS:'TABLE_RELEVANCE_SETTINGS',
            TABLE_COLLAPSE_SETTINGS:'TABLE_COLLAPSE_SETTINGS',
            TABLE_READONLY_SETTINGS:'TABLE_READONLY_SETTINGS',
            IG_DOCUMENT:'IG_DOCUMENT',
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
            getSelectedIgDocumentType: function () {
                return this.get(this.SELECTED_IG_DOCUMENT_TYPE);
            },
            setAppVersion: function (version) {
                this.set(this.APP_VERSION,version);
            },
            getAppVersion: function () {
                return this.get(this.APP_VERSION);
            },
            getIgDocument: function () {
                return this.get(this.IG_DOCUMENT) != null ? angular.fromJson(this.get(this.IG_DOCUMENT)):null;
            },
            setIgDocument: function (igDocument) {
                this.set(this.IG_DOCUMENT,igDocument != null ?  angular.toJson(igDocument):null);
            }
        };
        return service;
    }]
);
