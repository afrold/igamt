angular.module('igl').factory('SearchService',function($http, $q) {
    var SearchService={

        generateParameters:function(fields){
            var getParameters = '?';
            var isFirst = true;
            if(fields != undefined){
                fields.forEach(function(field){
                    if(field != undefined && field.value != undefined){
                        if(!isFirst){
                            getParameters += '&';
                        } else {
                            isFirst = false;
                        }
                        getParameters += field.param+"="+field.value;
                    }
                });
            }
            if(getParameters == '?'){
                return '';
            }
            return getParameters;
        },

        search:function(searchParameters,callback){
            var getParameters = this.generateParameters(searchParameters.fields);
            console.log('params: '+getParameters);
            $http({
                method: 'GET',
                url: 'api/export/'+searchParameters.value+'/html'+getParameters
            }).success(function(data){
                callback(data);
            });
        },

        listHL7Versions : function(callback) {
            var hl7Versions = [];
            $http.get('api/igdocuments/hl7/findVersions', {
                timeout: 60000
            }).then(
                function(response) {
                    var len = response.data.length;
                    for (var i = 0; i < len; i++) {
                        hl7Versions.push(response.data[i]);
                    }
                    callback(hl7Versions);
                }
            );
        },

        getContent:function(entity, callback){
            var url = "api/export/";
            switch(entity.type){
                case 'datatype':
                    url+="datatype";
                    break;
                case 'message':
                    url+="message";
                    break;
                case 'segment':
                    url+="segment";
                    break;
                case 'table':
                    url+="valueSet";
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
