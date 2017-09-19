/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('MessagesSvc', function($http, userInfoService, $q) {

    var svc = this;

    var messagesStruct = function(scope, children) {
        this.id = null;
        this.scope = scope;
        this.sectionDescription = null;
        this.sectionContents = null;
        this.children = children;
    };

    svc.findOneChild = function(id, list) {
        if (list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].id === id) {
                    return list[i];
                }
            }
        }
        return null;
    };

    svc.delete = function(message) {
        return $http.post('api/messages/' + message.id + '/delete');
    };
    svc.findMessageById = function(id) {

    };
    svc.findByIds = function(ids) {
        var delay = $q.defer();
        $http.post('api/messages/findByIds', ids).then(function(response) {
            var messages = angular.fromJson(response.data);
            delay.resolve(messages);
        }, function(error) {
            delay.reject(error);
        });
        return delay.promise;
    };
    svc.findByNamesAndScopeAndVersion = function(names, scope, hl7Version) {
        var namesAndscopeAndVersion = {
            "names": names,
            "scope": scope,
            "hl7Version": hl7Version
        };
        return $http.post(
                'api/messages/findByNamesScopeAndVersion', angular.toJson(namesAndscopeAndVersion))
            .then(function(response) {
                return angular.fromJson(response.data);
            });
    };


    return svc;
});