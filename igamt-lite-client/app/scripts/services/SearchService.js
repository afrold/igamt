angular.module('igl').factory('SearchService',function($http, $q) {
    var SearchService={

        generateParameters:function(fields){
            var getParameters = '';
            var isFirst = true;
            if(fields != undefined){
                fields.forEach(function(field){
                    if(field != undefined && field.value != undefined){
                        if(!isFirst){
                            getParameters += ',';
                        } else {
                            isFirst = false;
                        }
                        getParameters += field.param+"="+field.value;
                    }
                });
            }
            console.log('params: '+getParameters);
            return getParameters;
        },

        search:function(searchParameters,callback){
            var getParameters = this.generateParameters(searchParameters.fields);
            $http({
                method: 'GET',
                url: 'api/search/'+searchParameters.value,
                params: getParameters
            }).success(function(data){
                callback(data);
            });
        },

        getContent:function(entity, callback){
            var url = "api/export/";
            switch(entity.type){
                case 'datatype':
                    url+="datatypes";
                    break;
                case 'message':
                    url+="messages";
                    break;
                case 'segment':
                    url+="segments";
                    break;
                case 'table':
                    url+="valueSets";
                    break;
            }
            url += "/"+entity.id+"/html";
            $http({
                method: 'GET',
                url: url
            }).success(function(data){
                callback(data);
            });
        }
    };

    return SearchService;
});
