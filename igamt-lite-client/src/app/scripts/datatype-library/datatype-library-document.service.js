/**
 * Created by haffo on 9/12/17.
 */
angular.module('igl').factory('DatatypeLibraryDocumentSvc', function ($q, $http, $httpBackend, userInfoService, blockUI, $rootScope, $cookies) {

  var svc = this;
  var dtLibStruct = function (scope, children) {
    this.id = null;
    this.scope = scope;
    this.sectionDescription = null;
    this.sectionContents = null;
    this.children = children;
  };

  svc.getDataTypeLibraryDocumentByScope= function(scope) {
    var delay = $q.defer();
    $http.post('api/datatype-library-document/findByScope',scope).then(function(response) {
      var saveResponse = angular.fromJson(response);
      delay.resolve(saveResponse);
    }, function(error) {
      //console.log("DatatypeService.save error=" + error);
      delay.reject(error);
    });
    return delay.promise;
  },



//    svc.getDataTypeLibraryDocumentByScopeForAll = function (scope) {
//        console.log("datatype-library-document/findByScopeForAll scope=" + scope);
//        return $http.post(
//            'api/datatype-library-document/findByScopeForAll', scope)
//            .then(function (response) {
//                console.log("getDataTypeLibraryByScope response=" + response.data.length);
//                return angular.fromJson(response);
//            });
//    };
//

    svc.create = function (hl7Version, scope, name, ext,description, orgName) {
      var dtlcw = { "hl7Version": hl7Version,
        "scope": scope,
        "name": name,
        "ext": ext,
        "description":description,
        "orgName":orgName,
        "accountId": userInfoService.getAccountID()};
      return $http.post(
        'api/datatype-library-document/create', dtlcw).then(function (response) {
        return angular.fromJson(response)
      });
    };

  svc.delete = function (datatypeLibraryDocumentId) {

    return $http.get(
      'api/datatype-library-document/' + datatypeLibraryDocumentId + '/delete').then(function (response) {
      return angular.fromJson(response.data)
    });
  };

  svc.save = function (datatypeLibrary) {
    blockUI.start();

    return $http.post(
      'api/datatype-library-document/save', angular.toJson(datatypeLibrary)).then(function (response) {
      blockUI.stop();
      return angular.fromJson(response.data)

    });
  };
  svc.getAllDatatypesNames = function (datatypeLibrary) {
    var delay = $q.defer();

    $http.post('api/datatype-library-document/getAllDatatypesName').then(function (response) {
      delay.resolve(angular.fromJson(response.data));

    } , function(error) {
      //console.log("DatatypeService.save error=" + error);
      delay.reject(error);
    });
    return delay.promise;
  },

    svc.getDataTypeLibraryDocumentByScope= function(scope) {
      var delay = $q.defer();
      $http.post('api/datatype-library-document/findByScope',scope).then(function(response) {
        var saveResponse = angular.fromJson(response);
        delay.resolve(saveResponse);
      }, function(error) {
        //console.log("DatatypeService.save error=" + error);
        delay.reject(error);
      });
      return delay.promise;
    },


    svc.exportAs = function(dataTypeLibraryDocumentId, format) {
      blockUI.start();

      var form = document.createElement("form");
      form.action = $rootScope.api('api/datatype-library-document/' + dataTypeLibraryDocumentId + '/export/' + format);
      form.method = "POST";
      form.target = "_blank";
      var csrfInput = document.createElement("input");
      csrfInput.name = "X-XSRF-TOKEN";
      csrfInput.value = $cookies['XSRF-TOKEN'];
      form.appendChild(csrfInput);
      form.style.display = 'none';
      document.body.appendChild(form);
      form.submit();
      blockUI.stop();
    };

  svc.getMatrix = function () {

    return $http.post(
      'api/datatype-library-document/getMatrix').then(function (response) {
      return angular.fromJson(response.data)
    });
  };

  return svc;
});
