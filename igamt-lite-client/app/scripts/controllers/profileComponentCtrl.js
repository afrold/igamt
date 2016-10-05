angular.module('igl').controller('profileComponentCtrl', function($scope, $modal, orderByFilter, $rootScope, $q, $interval, ngTreetableParams, $http, StorageService, userInfoService, IgDocumentService, SegmentService, DatatypeService, SegmentLibrarySvc, DatatypeLibrarySvc, TableLibrarySvc) {


    $scope.profileComponents = [];
    $scope.createProfileComponent = function() {
        var newPC = {
            id: new ObjectId().toString(),
            name: "Test",
            type: "Segment",
            children: [{
                type: "Field",
                path: "MSH.1",
                usage: "R",
                minCard: 1,

            }, {
                type: "Field",
                path: "MSA.2",
                usage: "X",
                minLength: 10,
            }]

        };
        $scope.profileComponents.push(newPC);

    };
    $scope.editPC = function(pc) {
        $scope.currentPC = pc;
        console.log($scope.currentPC);
        if ($scope.profileComponentParams) {
            console.log("HEEERE");
            $scope.profileComponentParams.refresh();
        }
    };


    $scope.profileComponentParams = new ngTreetableParams({
        getNodes: function(parent) {
            if ($scope.currentPC !== undefined) {
                if (parent) {
                    if (parent.fields) {
                        return parent.fields;
                    } else if (parent.components) {
                        return parent.components;
                    } else if (parent.segments) {
                        return parent.segments;
                    } else if (parent.codes) {
                        return parent.codes;
                    }

                } else {
                    return $scope.currentPC.children;
                }

            }
        },
        getTemplate: function(node) {
            return 'profileComponentT';
        }
    });


    $scope.createProfileComponent = function() {
        var scopes = ['HL7STANDARD'];
        console.log("addDatatype scopes=" + scopes.length);
        var addDatatypeInstance = $modal.open({
            templateUrl: 'createProfileComponent.html',
            controller: 'createProfileComponentCtrl',
            size: 'lg',
            windowClass: 'conformance-profiles-modal',
            resolve: {
                hl7Version: function() {
                    return $scope.hl7Version;
                },
                datatypes: function() {

                    return datatypes;
                },
                masterDatatypes: function() {

                    return master;
                }
            }
        }).result.then(function(results) {

        });

    };



});


angular.module('igl').controller('createProfileComponentCtrl',
    function($scope, $rootScope, $modalInstance, datatypes, DatatypeLibrarySvc, DatatypeService) {
        var listHL7Versions = function() {
            return $http.get('api/igdocuments/findVersions', {
                timeout: 60000
            }).then(function(response) {
                var hl7Versions = [];
                var length = response.data.length;
                for (var i = 0; i < length; i++) {
                    hl7Versions.push(response.data[i]);
                }
                return hl7Versions;
            });
        };

        var init = function() {
            listHL7Versions().then(function(versions) {
                $scope.versions = versions;
            });
        };

        $scope.cancel = function() {
            $modalInstance.dismiss('cancel');
        };
    });