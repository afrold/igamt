/**
 * http://usejsdoc.org/
 */
angular.module('igl').factory('afterDrop', function($http, $q,userInfoService) {
	var svc = this;
	
	
	
    svc.addSectionToIg = function (id, section) {
        var delay = $q.defer();
        $http.post('api/igdocuments/'+ id+ '/section/save', section).then(function (response) {
            var saveResponse = angular.fromJson(response.data);
            delay.resolve(saveResponse);
        }, function (error) {
            delay.reject(error);
        });
        return delay.promise;
    };
    
    
    
    $scope.reOrderMessages = function(id,children) {
        var childrenMap = [];

        for (var i = 0; i <= children.length - 1; i++) {
            var childMap = {};
            childMap.id = children[i].id;
            childMap.position = children[i].position;
            messagesMap.push(childMap);
        }
        var req = {
            method: 'POST',
            url: "api/igdocuments/" + id + "/updateOrder",
            headers: {
                'Content-Type': "application/json"
            },
            data: childrenMap
        }


        var promise = $http(req)
            .success(function(data, status, headers, config) {

                return data;
            })
            .error(function(data, status, headers, config) {
                if (status === 404) {
                    console.log("Could not reach the server");
                } else if (status === 403) {
                    console.log("limited access");
                }
            });
        return promise;
    }
    
    
    
    
    
    
    



    return svc;
});
