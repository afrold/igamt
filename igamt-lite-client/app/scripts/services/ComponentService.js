/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('ComponentService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', '$filter', function ($rootScope, ViewSettings, ElementUtils,$filter) {
        var ComponentService = {
            create: function (position) {
                return {
                    id: new ObjectId().toString(),
                    type: 'component',
                    name: '',
                    usage: null,
                    minLength: 0,
                    maxLength: '0',
                    confLength: '1',
                    table: '',
                    bindingStrength: '',
                    bindingLocation: '',
                    datatype: null,
                    position: position,
                    comment: null,
                    text: null,
                    hide: false,
                    status:"DRAFT"
                }
            },

            deleteOne: function(component, datatype){
                if( datatype.components != null &&  datatype.components) {
                    var index = datatype.components.indexOf(component);
                    if (index > -1) datatype.components.splice(index, 1);
                }
                this.computePositions(datatype);
            },
            deleteList: function(components, datatype){
                var that = this;
                angular.forEach(components, function (child) {
                    that.deleteOne(child,datatype);
                });
                this.computePositions(datatype);
            },
            computePositions: function(datatype){
                datatype.components = $filter('orderBy')(datatype.components, 'position');
                for(var i=0; i < datatype.components.length;i++){
                    datatype.components[i].position = i+1;
                }
            }
        };
        return ComponentService;
    }]);
