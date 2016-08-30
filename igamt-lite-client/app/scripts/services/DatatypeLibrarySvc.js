/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('DatatypeLibrarySvc', function ($q, $http, $httpBackend, userInfoService) {

    var svc = this;

    var dtLibStruct = function (scope, children) {
        this.id = null;
        this.scope = scope;
        this.sectionDescription = null;
        this.sectionContents = null;
        this.children = children;
    };

    svc.getHL7Versions = function () {
        return $http.get(
            'api/datatype-library/findHl7Versions')
            .then(function (response) {
//					console.log("response" + JSON.stringify(response));
                return angular.fromJson(response.data);
            });
    };

    svc.getDataTypeLibraryByScope = function (scope) {
        console.log("datatype-library/findByScope scope=" + scope);
        return $http.post(
            'api/datatype-library/findByScope', scope)
            .then(function (response) {
                console.log("getDataTypeLibraryByScope response=" + response.data.length);
                return angular.fromJson(response.data);
            });
    };

    svc.getDataTypeLibraryByScopesAndVersion = function (scopes, hl7Version) {
        console.log("datatype-library/findByScopesAndVersion scopes=" + scopes + " hl7Version=" + hl7Version);
        var scopesAndVersion = {
            "scopes": scopes,
            "hl7Version": hl7Version
        };
        return $http.post(
            'api/datatype-library/findByScopesAndVersion', angular.toJson(scopesAndVersion))
            .then(function (response) {
                console.log("getDataTypeLibraryByScopesAndVersion response size=" + response.data.length);
                return angular.fromJson(response.data);
            });
    };
   

    svc.getDatatypesByLibrary = function (dtLibId) {
        return $http.get(
                'api/datatype-library/' + dtLibId + '/datatypes')
            .then(function (response) {
                //					console.log("response" + JSON.stringify(response));
                return angular.fromJson(response.data);
            });
    };

    svc.append = function (fromchildren, toChildren) {
        angular.foreach(fromchildren, function (child) {
            toChildren.push(child);
        });
        return svc.datatypeLibrary;
    };

    svc.createUpdate = function (scope, children) {
        var dtlrw = new dtLibStruct(scope, sortedChildren);
    };

    svc.create = function (hl7Version, scope, name, ext) {
        var dtlcw = { "hl7Version": hl7Version,
            "scope": scope,
            "name": name,
            "ext": ext,
            "accountId": userInfoService.getAccountID()};
        return $http.post(
            'api/datatype-library/create', dtlcw).then(function (response) {
                return angular.fromJson(response.data)
            });
    };

    svc.saveMetaData = function (datatypeLibraryId, datatypeLibraryMetaData) {
    	console.log("datatypeLibraryMetaData=" + JSON.stringify(datatypeLibraryMetaData));
        return $http.post(
            'api/datatype-library/' + datatypeLibraryId + '/saveMetaData', datatypeLibraryMetaData).then(function (response) {
                return angular.fromJson(response.data)
            });
    };

    svc.save = function (datatypeLibrary) {
    	
        return $http.post(
            'api/datatype-library/save', angular.toJson(datatypeLibrary)).then(function (response) {
                return angular.fromJson(response.data)
            });
    };
    
   svc.delete = function (datatypeLibraryId) {
    	
        return $http.get(
            'api/datatype-library/' + datatypeLibraryId + '/delete').then(function (response) {
                return angular.fromJson(response.data)
            });
    };

    svc.bindDatatypes = function (ids, dtLibId, dtLibExt) {
        var binding = {
            "datatypeIds": ids,
            "datatypeLibraryId": dtLibId,
            "datatypeLibraryExt": dtLibExt,
            "accountId": userInfoService.getAccountID()
        };
        var delay = $q.defer();
        $http.post('api/datatype-library/bindDatatypes', binding).then(function (response) {
            var datatypes = angular.fromJson(response.data);
            delay.resolve(datatypes);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };


    svc.findOneChild = function (id, list) {
        if (list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].id === id) {
                    return list[i];
                }
            }
        }
        return null;
    };

    svc.createEmptyLink = function () {
        return {id:null, ext:null, name:null};
    };


    svc.addChild = function (libId, datatypeLink) {
        var delay = $q.defer();
        $http.post('api/datatype-library/'+ libId+ '/addChild', datatypeLink).then(function (response) {
            var link = angular.fromJson(response.data);
            delay.resolve(link);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.updateChild = function (libId, datatypeLink) {
        var delay = $q.defer();
        $http.post('api/datatype-library/'+ libId+ '/updateChild', datatypeLink).then(function (response) {
            var link = angular.fromJson(response.data);
            delay.resolve(link);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.deleteChild = function (libId, id) {
        var delay = $q.defer();
        $http.post('api/datatype-library/'+ libId+ '/deleteChild/' + id).then(function (response) {
            var link = angular.fromJson(response.data);
            delay.resolve(link);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.findFlavors = function (name, scope, hl7Version, libId) {
        var delay = $q.defer();
        $http.get('api/datatype-library/'+ libId + '/findFlavors', {params: {"name": name, "scope": scope, "hl7Version": hl7Version}}).then(function (response) {
            var datatypes = angular.fromJson(response.data);
            delay.resolve(datatypes);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.findLibrariesByFlavorName = function (flavorName, flavorScope, flavorHl7Version) {
        var delay = $q.defer();
        $http.get('api/datatype-library/findLibrariesByFlavorName', {params: {"name": flavorName, "scope": flavorScope, "hl7Version": flavorHl7Version}}).then(function (response) {
            var libraries = angular.fromJson(response.data);
            delay.resolve(libraries);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.findDatatypeFlavorsByName = function (flavorName, flavorScope, flavorHl7Version) {
        var delay = $q.defer();
        $http.get('api/datatype-library/findDatatypeFlavorsByName', {params: {"name": flavorName, "scope": flavorScope, "hl7Version": flavorHl7Version}}).then(function (response) {
            var libraries = angular.fromJson(response.data);
            delay.resolve(libraries);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.addChildren = function (libId, datatypeLinks) {
        var delay = $q.defer();
        $http.post('api/datatype-library/'+ libId+ '/addChildren', datatypeLinks).then(function (response) {
            var res = angular.fromJson(response.data);
            delay.resolve(res);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };



    return svc;
});
