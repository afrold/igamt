/**
 * Created by gcr
 */
angular.module('igl').run(function ($httpBackend, $q, $http,$rootScope) {


    function getDatatypes() {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/datatypes/datatypes-USER-2.5.1.json', false);
        request.send(null);
        var datatypes = [].concat(angular.fromJson(request.response));
        return fixProperties(datatypes);
    };

    function getSegments() {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/segments/segments-USER-2.5.1.json', false);
        request.send(null);
        var segments = [].concat(angular.fromJson(request.response));
        return fixProperties(segments);
    };

    function getTables() {
        var request = new XMLHttpRequest();
        request.open('GET', '../../resources/tables/tables-USER-2.5.1.json', false);
        request.send(null);
        var tables = [].concat(angular.fromJson(request.response));
        return tables;
    };

    function find(id, collection) {
        for (var i = 0; i < collection.length; i++) {
            if (collection[i].id === id) {
                return collection[i];
            }
        }
        return null;
    };


    function fixProperties(collection) {
        for (var i = 0; i < collection.length; i++) {
            var item = collection[i];
            var index = item.label.indexOf("_");
            item.ext = index != -1 ? item.label.substring(index + 1, item.label.length) : "";
            item.scope = item.ext != null && item.ext != "" ? "USER" : "HL7STANDARD";
        }
        return collection;
    };

    function findDatatypeFlavors(name, hl7Version, scope) {
        var datatypes = getDatatypes();
        var flavors = [];
        angular.forEach(datatypes, function (datatype) {
            if(datatype.name === name && datatype.scope === scope) {
                flavors.push(datatype);
            }
        });
        return flavors;
    };

    function findSegmentFlavors(name, hl7Version, scope) {
        var segments = getSegments();
        var flavors = [];
        angular.forEach(segments, function (segment) {
            if(segment.name === name && segment.scope === scope) {
                flavors.push(segment);
            }
        });
        return flavors;
    };


    function findSegment(id) {
        var segment = find(id, getSegments());
        if (segment != null) {
            var index = segment.label.indexOf("_");
            segment.ext = index != -1 ? segment.label.substring(0, index) : segment.label;
            segment.scope = segment.ext != null && segment.ext != "" ? "USER" : "HL7STANDARD";
        }
        return segment;
    };

    function findTable(id) {
        return find(id, getTables());
    };

    function findDatatype(id) {
        var datatype = find(id, getDatatypes());
        if (datatype != null) {
            var index = datatype.label.indexOf("_");
            datatype.ext = index != -1 ? datatype.label.substring(0, index) : datatype.label;
            datatype.scope = datatype.ext != null && datatype.ext != "" ? "USER" : "HL7STANDARD";
        }
        return datatype;
    };

    function parseKeyValue(keyValue) {
        var obj = {}, key_value, key;
        angular.forEach((keyValue || "").split('&'), function(keyValue){
            if (keyValue) {
                key_value = keyValue.split('=');
                key = decodeURIComponent(key_value[0]);
                obj[key] = key_value[1];
            }
        });
        return obj;
    }

    $httpBackend.whenPOST('api/updateSections').respond(function (data, status, headers, config) {
        console.log('updating Sections');
        
        return null;
    });
    
    $httpBackend.whenGET('api/datatype-library/findHl7Versions').respond(function (method, url, data, headers) {
        console.log('api/igdocuments/hl7/findHl7Versions');
        return [200, ["2.1", "2.2", "2.3", "2.3.1", "2.4", "2.5.1", "2.6", "2.7"], {}];
    });

    $httpBackend.whenPOST('api/datatype-library/findByScope').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/findByScope begin=' + data);
        if ("MASTER" === data) {
            request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.7.json', false);
        } else {
            request.open('GET', '../../resources/datatypeLibraries/dtLib-USER-2.7.json', false);
        }
      request.send(null);
        var datatypeLib = [angular.fromJson(request.response)];
        return [200, datatypeLib, {}];
    });

    $httpBackend.whenPOST('api/datatype-library/findByScopeAndVersion').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        var datatypeLibs = [];
        console.log('api/findByScopesAndVersion begin=' + data);
        request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.7.json', false);
        request.send(null);
        datatypeLibs.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypeLibraries/dtLib-HL7STANDARD-2.7.json', false);
        request.send(null);
        datatypeLibs.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypeLibraries/dtLib-USER-2.7.json', false);
        request.send(null);
        datatypeLibs.push(angular.fromJson(request.response));
        return [200, datatypeLibs, {}];
    });

    $httpBackend.whenGET(/^api\/datatype-library\/.*\/datatypes/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
         console.log('api\\/.*\\/ url=' + url);
         var datatypes = [];
         request.open('GET', '../../resources/datatypes/datatype-AD-HL7STANDARD-2.7.json', false);
         request.send(null);
         datatypes.push(angular.fromJson(request.response));
         request.open('GET', '../../resources/datatypes/datatype-AUI-HL7STANDARD-2.7.json', false);
         request.send(null);
         datatypes.push(angular.fromJson(request.response));
         request.open('GET', '../../resources/datatypes/datatype-CCP-HL7STANDARD-2.7.json', false);
         request.send(null);
         datatypes.push(angular.fromJson(request.response));
        return [request.status, datatypes, {}];
    });

    $httpBackend.whenPOST('api/datatype-library/create').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/findByScopes begin=' + data);
        request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.7.json', false);
        request.send(null);
        var datatypeLib = angular.fromJson(request.response);
        return [200, datatypeLib, {}];
    });

    $httpBackend.whenPOST('api/datatype-library/save').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/save begin=' + data);
        var response = "response";
        return [200, response, {}];
    });

    $httpBackend.whenGET(/^api\/segment-library\/.*/).respond(function (method, url, data, headers, params) {
        return [200, getSegments(), {}];
    });

     $httpBackend.whenPOST(/^api\/datatype-library\/.*/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        var libId = url.split('/')[2];
        var action = url.split('/')[3];
        if(action === 'updateChild'){
            return [200, data, {}];
        }else if(action === 'deleteChild'){
            return [200, true, {}];
        }else{
            return [200, {}, {}];
        }
    });

//    $httpBackend.whenPOST('api/datatype-library/findByScopesAndVersion').respond(function (method, url, data, headers) {
//        var request = new XMLHttpRequest();
//        console.log('api/findByScopesAndVersion begin=' + data);
//        var datatypeLibs = [];
//        request.open('GET', '../../resources/datatypeLibraries/dtLib-MASTER-2.7.json', false);
//        request.send(null);
//        datatypeLibs.push(angular.fromJson(request.response));
//        request.open('GET', '../../resources/datatypeLibraries/dtLib-HL7STANDARD-2.7.json', false);
//        request.send(null);
//        datatypeLibs.push(angular.fromJson(request.response));
//        request.open('GET', '../../resources/datatypeLibraries/dtLib-USER-2.7.json', false);
//        request.send(null);
//        datatypeLibs.push(angular.fromJson(request.response));
//       return [200, datatypeLibs, {}];
//    });

    
    $httpBackend.whenPOST('api/datatype-library/bindDatatypes').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        var datatypes = [];
        request.open('GET', '../../resources/datatypes/datatype-AD-HL7STANDARD-2.7.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypes/datatype-AUI-HL7STANDARD-2.7.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        request.open('GET', '../../resources/datatypes/datatype-CCD-HL7STANDARD-2.7.json', false);
        request.send(null);
        datatypes.push(angular.fromJson(request.response));
        return [200, datatypes, {}];
    });

    $httpBackend.whenGET(/^api\/table-library\/.*/).respond(function (method, url, data, headers, params) {
        return [200, getTables(), {}];
    });

    $httpBackend.whenGET(/^api\/datatypes\/.*/).respond(function (method, url, data, headers,params) {
        var path = url.split('/')[2];
        if(path.startsWith('findFlavors')){
            var url_parts = url.split('?');
            var params = parseKeyValue( url_parts[1]);
            return [200, findDatatypeFlavors(params.name,params.hl7Version,params.scope), {}];
        } else if(path.indexOf('delete') >= 0){
            return [200, true, {}];
        }else{
            return [200, findDatatype(path), {}];
         }
     });

    $httpBackend.whenPOST('api/datatypes/save').respond(function (method, url, datatype, headers) {
        var request = new XMLHttpRequest();
        console.log('api/save begin=' + datatype);
        var response = angular.fromJson(datatype);
        return [200, response, {}];
    });
    
  $httpBackend.whenPOST('api/datatypes/saveAll').respond(function (method, url, datatype, headers) {
  var request = new XMLHttpRequest();
  console.log('api/saveAll begin=' + datatype);
  var response = angular.fromJson(datatype);
  return [200, response, {}];
});
  
$httpBackend.whenPOST('api/datatypes/findByIds').respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        console.log('api/findByIds begin=' + data);
            request.open('GET', '../../resources/datatypes/datatype-WVI-HL7STANDARD-2.3.1.json', false);
        
      request.send(null);
        var datatypeLib = [angular.fromJson(request.response)];
        return [200, datatypeLib[0], {}];
    });





    $httpBackend.whenGET(/^api\/segments\/.*/).respond(function (method, url, data, headers) {
        var id = url.split('/')[2];
        return [200, findSegment(id), {}];
    });

    $httpBackend.whenGET(/^api\/tables\/.*/).respond(function (method, url, data, headers) {
        var id = url.split('/')[2];
        return [200, findTable(id), {}];
    });

    $httpBackend.whenPOST(/^api\/segment-library\/.*/).respond(function (method, url, data, headers) {
        var request = new XMLHttpRequest();
        var libId = url.split('/')[2];
        var action = url.split('/')[3];
        if(action === 'updateChild'){
            return [200, data, {}];
        }else if(action === 'deleteChild'){
            return [200, true, {}];
        }else{
            return [200, {}, {}];
        }
    });



    $httpBackend.whenPOST('api/segments/save').respond(function (method, url, datatype, headers) {
        var request = new XMLHttpRequest();
        console.log('api/save begin=' + datatype);
        var response = angular.fromJson(datatype);
        return [200, response, {}];
    });
    $httpBackend.whenPOST('api/messages/save').respond(function (method, url, message, headers) {
        var request = new XMLHttpRequest();
        console.log('api/save begin=' + message);
        var response = angular.fromJson(message);
        return [200, response, {}];
    });

});