angular.module('igl').controller('compareCtrl', function($scope, $modal, ObjectDiff, $rootScope, $q, $interval, uiGridTreeViewConstants, $http, StorageService, userInfoService) {

    $scope.igDocumentConfig = {
        selectedType: null
    };
    $scope.igList = [];


    $scope.gridOptions = {
        enableSorting: true,
        enableFiltering: true,
        showTreeExpandNoChildren: true,
        columnDefs: [
            { name: 'key', width: '30%' },
            { name: 'msg1', field: 'removed', width: '30%' },
            { name: 'msg2', field: 'added', width: '30%' },

        ],
        onRegisterApi: function(gridApi) {
            $scope.gridApi = gridApi;
            $scope.loadIGDocuments().then(function() {

                getPage();

                $scope.gridApi.treeBase.on.rowExpanded($scope, function(row) {
                    console.log(row);
                    // if (row.entity.$$hashKey === $scope.gridOptions.data[1].$$hashKey && !$scope.nodeLoaded) {
                    //     $interval(function() {
                    //         $scope.gridOptions.data.splice(51, 0, { name: 'description', gender: 'female', age: 53, company: 'Griddable grids', balance: 38000, $$treeLevel: 1 }, { name: 'Dynamic 2', gender: 'male', age: 18, company: 'Griddable grids', balance: 29000, $$treeLevel: 1 });
                    //         $scope.nodeLoaded = true;
                    //     }, 2000, 1);
                    // }


                });

                // for (i = 0; i < $rootScope.igs[0].profile.messages.children.length; i++) {
                //     $rootScope.igs[0].profile.messages.children[i].subGridOptions = {
                //         columnDefs: [{ name: "id", field: "id" }, { name: "name", field: "name" }],
                //         data: $rootScope.igs[0].profile.messages.children[i]
                //     }
                // }
                // $scope.gridOptions.data = $rootScope.igs;
            });

        }
    };



    var getPage = function() {
        var objToArray = function(object) {
            var result = [];
            $.map(object, function(value, index) {

                result.push(value);
            });
            return result;

        };
        var getSegName = function(diffItem) {


        };
        var formatChange = function(key, diffItem) {

            var property;
            switch (diffItem.changed) {
                case 'equal':

                    property = {
                        key : diffItem.value.label
                        //removed: diffItem.value,
                        //value:formatChangesToXMLString(diffItem),
                    };

                    break;

                case 'removed':

                    property = {
                        key: key,
                        removed: diffItem.value,
                        //value:formatChangesToXMLString(diffItem),
                    };
                    break;

                case 'added':
                    // console.log(diffItem);
                    property = {
                        key: key,
                        added: diffItem.value,
                        //value:formatChangesToXMLString(diffItem),
                    };
                    break;

                case 'primitive change':
                    property = {
                        key: key,
                        added: diffItem.added,
                        removed: diffItem.removed
                    };

                    break;

                case 'object change':

                    var objChanges = [];
                    if (diffItem.value.label) {
                        console.log("labeeeel");
                        key = diffItem.value.label;
                        added = diffItem.value.label.added;
                        removed = diffItem.value.label.removed;
                    } else if (diffItem.value[0]) {
                        key = '';
                        added = '';
                        removed = '';
                        console.log("not labeeel");
                    }

                    // for (var i = 0; i < objToArray(diffItem.value).length; i++) {
                    //     //console.log(objToArray(diffItem.value)[i]);
                    //   //  objChanges.push(formatChangesToXMLString(objToArray(diffItem.value)[i]));


                    // }


                    property = {
                        //key: key,
                        value: formatChangesToXMLString(diffItem),
                        added: added,
                        removed: removed
                    };


                    break;

            };
            //console.log(property);

            return property;
        };

        var formatChangesToXMLString = function(changes) {

            var properties = [];
            // if (changes[0] === 'equal') {
            //     $scope.gridOptions.data = [];
            // } else {


            // }



            if (changes[0] === 'equal') {
                $scope.gridOptions.data = [];
            } else {
                if ($.isArray(changes)) {
                    for (var key in changes[1]) {

                        var changed = changes[1][key].changed;
                        if (changed !== 'equal') {
                            properties.push(formatChange(key, changes[1][key]));
                        }
                    }
                } else {
                    for (var key in changes.value) {


                        var changed = changes.value[key].changed;
                        if (changed !== 'equal') {
                            properties.push(formatChange(key, changes.value[key]));
                        }
                    }
                }
            }
            return properties;
        };


        var writeoutNode = function(childArray, currentLevel, dataArray) {
            childArray.forEach(function(childNode) {
                if (childNode.value) {
                    childNode.$$treeLevel = currentLevel;
                    dataArray.push(childNode);
                    writeoutNode(childNode.value, currentLevel + 1, dataArray);
                } else {
                    dataArray.push(childNode);
                }

                // 
                //  writeoutNode(childNode.value, currentLevel + 1, dataArray);
            });
        };
        if ($scope.diff) {
            var array = objToArray($scope.diff);
            //formatChangesToXMLString(array);
            $scope.gridOptions.data = [];

            writeoutNode(formatChangesToXMLString(array[1].segments), 0, $scope.gridOptions.data);

        } else {
            $scope.gridOptions.data = [];
        }





















        // if (changes.changed == 'equal') {
        //     return '';
        // } else {
        //     var diff = changes.value;

        //     for (var key in diff) {
        //         var changed = diff[key].changed;
        //         if (changed !== 'equal')
        //             properties.push(formatChange(key, diff[key], shallow, true));
        //     }

        // }


        // var formatChange = function(key, diffItem) {
        //     var changed = diffItem.changed;
        //     var property;
        //     switch (changed) {
        //         case 'equal':
        //             property = (stringifyObjectKey(escapeHTML(key)) + '<span>: </span>' + inspect(diffItem.value));
        //             break;

        //         case 'removed':
        //             property = ('<del class="diff">' + stringifyObjectKey(escapeHTML(key)) + '<span>: </span>' + inspect(diffItem.value) + '</del>');
        //             break;

        //         case 'added':
        //             property = ('<ins class="diff">' + stringifyObjectKey(escapeHTML(key)) + '<span>: </span>' + inspect(diffItem.value) + '</ins>');
        //             break;

        //         case 'primitive change':
        //             var prefix = stringifyObjectKey(escapeHTML(key)) + '<span>: </span>';
        //             property = (
        //                 '<del class="diff diff-key">' + prefix + inspect(diffItem.removed) + '</del><span>,</span>\n' +
        //                 '<ins class="diff diff-key">' + prefix + inspect(diffItem.added) + '</ins>');
        //             break;

        //         case 'object change':
        //             property = shallow ? '' : (stringifyObjectKey(key) + '<span>: </span>' + (diffOnly ? formatChangesToXMLString(diffItem) : formatToJsonXMLString(diffItem)));
        //             break;
        //     }

        //     return property;
        // }
















        // $rootScope.igs[0].profile.messages.children[0].$$treeLevel = 0;

        // $rootScope.igs[0].profile.messages.children[1].$$treeLevel = 1;







    };

    $scope.loadIGDocuments = function() {
        var delay = $q.defer();
        $scope.igDocumentConfig.selectedType = StorageService.getSelectedIgDocumentType() != null ? StorageService.getSelectedIgDocumentType() : 'USER';
        $scope.error = null;
        $rootScope.igs = [];
        $scope.tmpIgs = [].concat($rootScope.igs);
        if (userInfoService.isAuthenticated() && !userInfoService.isPending()) {
            $scope.loading = true;
            StorageService.setSelectedIgDocumentType($scope.igDocumentConfig.selectedType);
            $http.get('api/igdocuments', { params: { "type": $scope.igDocumentConfig.selectedType } }).then(function(response) {
                $rootScope.igs = angular.fromJson(response.data);
                $scope.tmpIgs = [].concat($rootScope.igs);
                $scope.loading = false;
                delay.resolve(true);
            }, function(error) {
                $scope.loading = false;
                $scope.error = error.data;
                delay.reject(false);
            });
        } else {
            delay.reject(false);
        }
        return delay.promise;
    };
    $scope.loadIGDocuments().then(function() {

        angular.forEach($rootScope.igs, function(ig) {


            var element = {
                id: ig.id,
                title: ig.metaData.title
            };
            $scope.igList.push(element);

        });

    });

    $scope.findIGbyID = function(id) {
        var selectedIG = [];

        angular.forEach($rootScope.igs, function(ig) {
            if (ig.id === id) {

                selectedIG = ig;
            }
        });
        return selectedIG;


    };
    $scope.setIG1 = function(ig) {
        if (ig) {
            $scope.messages1 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages
        }
        //$scope.gridOptions.data = $scope.yourObjectOne;


    };
    $scope.setIG2 = function(ig) {
        if (ig) {
            $scope.messages2 = ($scope.findIGbyID(JSON.parse(ig).id)).profile.messages
        }
    };
    $scope.setMsg1 = function(msg) {
        $scope.msg1 = msg;

    };
    $scope.setMsg2 = function(msg) {
        $scope.msg2 = msg;


    };
    $scope.formatGrp = function(grp) {
        var group = {
            name: grp.name,
            type: grp.type,
            minCard: grp.min,
            maxCard: grp.max,
            usage: grp.usage,
            position: grp.position,
            segments: $scope.formatSeg(grp.children)
        };
        return group;

    };
    $scope.formatSeg = function(segments) {
        var result = [];
        for (var i = 0; i < segments.length; i++) {
            if (segments[i].type === 'segmentRef') {
                var segment = {};

                segment = {
                    name: segments[i].ref.name,
                    label: segments[i].ref.label,
                    type: segments[i].type,
                    minCard: segments[i].min,
                    maxCard: segments[i].max,
                    usage: segments[i].usage,
                    position: segments[i].position
                };
                result.push(segment);

            } else {
                result.push($scope.formatGrp(segments[i]));
            }
        };
        return result;


    };
    $scope.formatMsg = function(msg) {

        var message = {
            name: msg.name,
            event: msg.event,
            structID: msg.structID,
            position: msg.position,
            segments: $scope.formatSeg(msg.children)
        }
        console.log(message);
        return message;

    };


    $scope.compare = function() {

        $scope.diff = ObjectDiff.diffOwnProperties($scope.formatMsg(JSON.parse($scope.msg1)), $scope.formatMsg(JSON.parse($scope.msg2)));

        // you can directly diff your objects including prototype properties and inherited properties using `diff` method
        $scope.diffAll = ObjectDiff.diff(JSON.parse($scope.msg1), JSON.parse($scope.msg2));

        // gives a full object view with Diff highlighted
        $scope.diffValue = ObjectDiff.toJsonView($scope.diffAll);

        // gives object view with onlys Diff highlighted
        $scope.diffValueChanges = ObjectDiff.toJsonDiffView($scope.diff);

        console.log($scope.diff);
        console.log($scope.diffAll);
        getPage();


    };


    // $scope.$watch('ig2', function() {
    //     console.log("rgkdrfgkdfkdfodf");
    //     console.log($scope.ig1);
    //     if ($scope.ig2) {
    //         $scope.yourObjectTwo = ($scope.findIGbyID(JSON.parse($scope.ig2).id)).profile.messages;
    //     }
    //     console.log("$scope.yourObjectOne");
    //     console.log($scope.yourObjectOne);
    //     console.log("$scope.yourObjectTwo");
    //     console.log($scope.yourObjectTwo);

    //     // This is required only if you want to show a JSON formatted view of your object without using a filter

    // }, true);





});
