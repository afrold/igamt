/**
 * Created by haffo on 3/9/16.
 */
'use strict';
angular.module('igl').factory('FieldService',
    ['$rootScope', 'ViewSettings', 'ElementUtils', '$filter', function ($rootScope, ViewSettings, ElementUtils,$filter) {
        var ComponentService = {
            create: function (position) {
                return {
                    id: new ObjectId().toString(),
                    type: 'field',
                    min: 0,
                    max: '0',
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
                    status:"DRAFT",
                    itemNo:null
                }
            },

            deleteOne: function(field, segment){
                if( segment.fields != null &&  segment.fields) {
                    var index = segment.fields.indexOf(field);
                    if (index > -1) segment.fields.splice(index, 1);
                }
                this.computePositions(segment);
            },
            deleteList: function(fields, segment){
                var that = this;
                angular.forEach(fields, function (child) {
                    that.deleteOne(child,segment);
                });
                this.computePositions(segment);
            },
            computePositions: function(segment){
                segment.fields = $filter('orderBy')(segment.fields, 'position');
                for(var i=0; i < segment.fields.length;i++){
                    segment.fields[i].position = i+1;
                }
            }
        };
        return ComponentService;
    }]);
