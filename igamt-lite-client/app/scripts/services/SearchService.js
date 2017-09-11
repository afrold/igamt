angular.module('igl').factory('SearchService',function($http, $q,$sce) {
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

        search:function(searchParameters,successCallback,errorCallback){
            var getParameters = this.generateParameters(searchParameters.fields);
            console.log('params: '+getParameters);
            $http({
                method: 'GET',
                url: 'api/export/'+searchParameters.value+getParameters
            }).then(function (success){
                if(success.data.html != ''){
                    success.data.html = $sce.trustAsHtml(success.data.html);
                }
                successCallback(success.data);
             },function (error){
                errorCallback();
             });
        },

        listHL7Versions : function(callback) {
            var hl7Versions = [];
            $http.get('api/search/listHl7Versions', {
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

        getExportUrl:function(entity,type){
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
                case 'document':
                    url+="igDocument"
            }
            return url += "/"+entity.id+"/"+type;
        },

        getContent:function(entity, callback){
            $http({
                method: 'GET',
                url: getExportUrl(entity,'html')
            }).success(function(data){
                callback(data);
            });
        }
    };

    return SearchService;
});
