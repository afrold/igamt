/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('SectionSvc', function($http, $q,userInfoService, $rootScope) {
	var svc = this;
    svc.save = function (id, section) {
        var delay = $q.defer();
        $http.post('api/igdocuments/'+ id+ '/section/save', section).then(function (response) {
            var dateUpdated = angular.fromJson(response.data);
            section.dateUpdated = dateUpdated;
            $rootScope.$emit("event:updateIgDate",dateUpdated);
            delay.resolve(dateUpdated);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };
    svc.update = function (id, section) {
        var delay = $q.defer();
        console.log("section");
        console.log(section);
        $http.post('api/igdocuments/'+ id+ '/section/update', section).then(function (response) {
            var dateUpdated = angular.fromJson(response.data);
            section.dateUpdated = dateUpdated;
            $rootScope.$emit("event:updateIgDate",dateUpdated);
            delay.resolve(dateUpdated);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.delete = function (id, sectionId) {
        var delay = $q.defer();
        $http.post('api/igdocuments/'+ id+ '/section/'+sectionId+'/delete').then(function (response) {
            var dateUpdated = angular.fromJson(response.data);
            $rootScope.$emit("event:updateIgDate",dateUpdated);
            delay.resolve(dateUpdated);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };

    svc.merge = function(oldValue, newValue){
        angular.extend(oldValue, newValue);
//        oldValue.id =  newValue.id;
//        oldValue.sectionTitle =  newValue.sectionTitle;
//        oldValue.sectionDescription =  newValue.sectionDescription;
//        oldValue.sectionPosition =  newValue.sectionPosition;
//        oldValue.sectionContents =  newValue.sectionContents;
//        oldValue.type =  newValue.type;
    };

    return svc;
});
