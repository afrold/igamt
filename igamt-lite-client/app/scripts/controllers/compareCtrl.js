angular.module('igl').controller('compareCtrl', function($scope, $modal, ObjectDiff, $rootScope, $q, $interval, uiGridTreeViewConstants, ngTreetableParams, $http, StorageService, userInfoService, SegmentService) {

    $scope.igDocumentConfig = {
        selectedType: null
    };
    $scope.igList = [];


    // $scope.gridOptions = {
    //     enableSorting: true,
    //     enableFiltering: true,
    //     showTreeExpandNoChildren: true,
    //     columnDefs: [
    //         { name: 'position', width: '30%' },
    //         { name: 'key', width: '30%' },
    //         { name: 'msg1', field: 'removed', width: '30%' },
    //         { name: 'msg2', field: 'added', width: '30%' },

    //     ],
    //     onRegisterApi: function(gridApi) {
    //         $scope.gridApi = gridApi;
    //         $scope.loadIGDocuments().then(function() {

    //             getPage();

    //             $scope.gridApi.treeBase.on.rowExpanded($scope, function(row) {
    //                 console.log(row);
    //                 // if (row.entity.$$hashKey === $scope.gridOptions.data[1].$$hashKey && !$scope.nodeLoaded) {
    //                 //     $interval(function() {
    //                 //         $scope.gridOptions.data.splice(51, 0, { name: 'description', gender: 'female', age: 53, company: 'Griddable grids', balance: 38000, $$treeLevel: 1 }, { name: 'Dynamic 2', gender: 'male', age: 18, company: 'Griddable grids', balance: 29000, $$treeLevel: 1 });
    //                 //         $scope.nodeLoaded = true;
    //                 //     }, 2000, 1);
    //                 // }


    //             });

    //             // for (i = 0; i < $rootScope.igs[0].profile.messages.children.length; i++) {
    //             //     $rootScope.igs[0].profile.messages.children[i].subGridOptions = {
    //             //         columnDefs: [{ name: "id", field: "id" }, { name: "name", field: "name" }],
    //             //         data: $rootScope.igs[0].profile.messages.children[i]
    //             //     }
    //             // }
    //             // $scope.gridOptions.data = $rootScope.igs;
    //         });

    //     }
    // };



    // var getPage = function() {
    //     var objToArray = function(object) {
    //         var result = [];
    //         $.map(object, function(value, index) {

    //             result.push(value);
    //         });
    //         return result;

    //     };
    //     var getSegName = function(diffItem) {


    //     };
    //     // var formatChange = function(key, diffItem) {

    //     //     var property;
    //     //     switch (diffItem.changed) {
    //     //         case 'equal':

    //     //             property = {
    //     //                 key: diffItem.value.label
    //     //                     //removed: diffItem.value,
    //     //                     //value:formatChangesToXMLString(diffItem),
    //     //             };

    //     //             break;

    //     //         case 'removed':

    //     //             property = {
    //     //                 key: key,
    //     //                 removed: diffItem.value,
    //     //                 //value:formatChangesToXMLString(diffItem),
    //     //             };
    //     //             break;

    //     //         case 'added':
    //     //             // console.log(diffItem);
    //     //             property = {
    //     //                 key: key,
    //     //                 added: diffItem.value,
    //     //                 //value:formatChangesToXMLString(diffItem),
    //     //             };
    //     //             break;

    //     //         case 'primitive change':
    //     //             property = {
    //     //                 key: key,
    //     //                 added: diffItem.added,
    //     //                 removed: diffItem.removed
    //     //             };

    //     //             break;

    //     //         case 'object change':

    //     //             var objChanges = [];
    //     //             if (diffItem.value.label) {
    //     //                 key = diffItem.value.label;
    //     //                 added = diffItem.value.label.added;
    //     //                 removed = diffItem.value.label.removed;
    //     //             } else if (diffItem.value[0]) {
    //     //                 key = '';
    //     //                 added = '';
    //     //                 removed = '';
    //     //             }

    //     //             // for (var i = 0; i < objToArray(diffItem.value).length; i++) {
    //     //             //     //console.log(objToArray(diffItem.value)[i]);
    //     //             //   //  objChanges.push(formatChangesToXMLString(objToArray(diffItem.value)[i]));


    //     //             // }


    //     //             property = {
    //     //                 //key: key,
    //     //                 value: formatChangesToXMLString(diffItem),
    //     //                 added: added,
    //     //                 removed: removed
    //     //             };


    //     //             break;

    //     //     };
    //     //     //console.log(property);

    //     //     return property;
    //     // };

    //     // var formatChangesToXMLString = function(changes) {

    //     //     var properties = [];
    //     //     // if (changes[0] === 'equal') {
    //     //     //     $scope.gridOptions.data = [];
    //     //     // } else {


    //     //     // }
    //     //     //console.log(changes);




    //     //     if (changes[0] === 'equal') {
    //     //         $scope.gridOptions.data = [];
    //     //     } else {
    //     //         if ($.isArray(changes)) {
    //     //             for (var key in changes[1]) {

    //     //                 var changed = changes[1][key].changed;
    //     //                 if (changed !== 'equal') {
    //     //                     properties.push(formatChange(key, changes[1][key]));
    //     //                 }
    //     //             }
    //     //         } else {
    //     //             for (var key in changes.value) {


    //     //                 var changed = changes.value[key].changed;
    //     //                 if (changed !== 'equal') {
    //     //                     properties.push(formatChange(key, changes.value[key]));
    //     //                 }
    //     //             }
    //     //         }
    //     //     }
    //     //     return properties;
    //     // };


    //     // var writeoutNode = function(childArray, currentLevel, dataArray) {
    //     //     childArray.forEach(function(childNode) {
    //     //         // console.log("currentLevel");

    //     //         // console.log(currentLevel);
    //     //         // console.log(childNode);
    //     //         if (childNode.value) {
    //     //             childNode.$$treeLevel = currentLevel;
    //     //             dataArray.push(childNode);
    //     //             writeoutNode(childNode.value, currentLevel + 1, dataArray);
    //     //         } else {
    //     //             dataArray.push(childNode);
    //     //         }

    //     //         // 
    //     //         //  writeoutNode(childNode.value, currentLevel + 1, dataArray);
    //     //     });
    //     // };

    //     var writeTable = function(childArray, currentLevel, dataArray) {
    //         if (childArray.changed === "object change") {

    //             if (childArray.value.name === undefined) {

    //                 objToArray(childArray.value).forEach(function(childNode) {
    //                     if (childNode.changed === "object change") {
    //                         if (childNode.value.label && childNode.value.label.changed === "primitive change") {
    //                             console.log(childNode);

    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 //key: childNode.value.position.value,
    //                                 added: childNode.value.label.added,
    //                                 removed: childNode.value.label.removed,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);


    //                         } else if (childNode.value.type.value === "field" && childNode.value.name && childNode.value.name.changed === "primitive change") {
    //                             console.log(childNode);

    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 added: childNode.value.name.added,
    //                                 removed: childNode.value.name.removed,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);



    //                         } else if (childNode.value.type.changed === "equal" && childNode.value.type.value === "field" && childNode.value.name.changed === "equal") {

    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 added: childNode.value.name.value,
    //                                 removed: childNode.value.name.value,

    //                                 //key: childNode.value.position.value + "." + childNode.value.name.value,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);



    //                         } else if (childNode.value.type.value === "group" && childNode.value.name && childNode.value.name.changed === "primitive change") {

    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 //key: childNode.value.position.value,
    //                                 added: childNode.value.name.added,
    //                                 removed: childNode.value.name.removed,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);

    //                         } else if (childNode.value.label && childNode.value.label.changed === "equal") {

    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 added: childNode.value.label.value,
    //                                 removed: childNode.value.label.value,
    //                                 //key: childNode.value.position.value + "." + childNode.value.label.value,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);

    //                         } else if (childNode.value.type.changed === "equal" && childNode.value.type.value === "group" && childNode.value.name.changed === "equal") {
    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 added: childNode.value.name.value,
    //                                 removed: childNode.value.name.value,
    //                                 //key: childNode.value.position.value + "." + childNode.value.name.value,
    //                                 $$treeLevel: currentLevel
    //                             });
    //                             writeTable(childNode, currentLevel + 1, dataArray);


    //                             console.log("group's here");

    //                         } else if (childNode.value.type.changed !== "equal") {

    //                             console.log("Error can't compare msgs with different structures");
    //                             dataArray.push({
    //                                 position: childNode.value.position.value,
    //                                 //key: childNode.value.position.value,
    //                                 added: childNode.value.name.added,
    //                                 removed: childNode.value.name.removed,
    //                                 $$treeLevel: currentLevel


    //                             });
    //                         }


    //                     }

    //                 });
    //             } else {

    //                 if (childArray.value.type.changed === "equal") {

    //                     //childArray.$$treeLevel = currentLevel;
    //                     if (childArray.value.usage.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "Usage",
    //                             added: childArray.value.usage.added,
    //                             removed: childArray.value.usage.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "segmentRef" && childArray.value.minCard.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "Min Card",
    //                             added: childArray.value.minCard.added,
    //                             removed: childArray.value.minCard.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "segmentRef" && childArray.value.maxCard.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "Max Card",
    //                             added: childArray.value.maxCard.added,
    //                             removed: childArray.value.maxCard.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "field" && childArray.value.min.changed === "primitive change") {
    //                         console.log(childArray);
    //                         dataArray.push({
    //                             key: "Min",
    //                             added: childArray.value.min.added,
    //                             removed: childArray.value.min.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "field" && childArray.value.max.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "Max",
    //                             added: childArray.value.max.added,
    //                             removed: childArray.value.max.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "field" && childArray.value.maxLength.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "Max Length",
    //                             added: childArray.value.maxLength.added,
    //                             removed: childArray.value.maxLength.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "field" && childArray.value.minLength.changed === "primitive change") {
    //                         dataArray.push({
    //                             key: "min Length",
    //                             added: childArray.value.minLength.added,
    //                             removed: childArray.value.minLength.removed,


    //                         });
    //                     }
    //                     if (childArray.value.type.value === "group") {

    //                         writeTable(childArray.value.segments, currentLevel, dataArray);

    //                     }
    //                     if (childArray.value.type.value === "segmentRef") {
    //                         console.log("iam hreee");
    //                         writeTable(childArray.value.fields, currentLevel, dataArray);
    //                     }

    //                 } else {
    //                     dataArray.push({
    //                         key: childArray.value.position.value,
    //                         added: childArray.value.name.added,
    //                         removed: childArray.value.name.removed,
    //                         $$treeLevel: currentLevel


    //                     });
    //                     console.log("Error can't compare msgs with different structures(inside group)");

    //                 }

    //                 // } else if (childArray.value.type.changed !== "equal") {
    //                 //     console.log(childArray);
    //                 //     console.log("Error can't compare msgs with different structures");
    //                 //     dataArray.push({
    //                 //         key: childArray.value.position.value,
    //                 //         added: childArray.value.name.added,
    //                 //         removed: childArray.value.name.removed,


    //                 //     });
    //                 // } else if (childArray.value.type.changed === "equal" && childArray.value.type.value === "group") {
    //                 //     console.log("group's here");

    //                 // }
    //             }
    //         }

    //     };



    //     if ($scope.diff) {
    //         var array = objToArray($scope.diff);
    //         //console.log(array);
    //         //formatChangesToXMLString(array);
    //         $scope.gridOptions.data = [];
    //         writeTable(array[1].segments, 0, $scope.gridOptions.data);

    //         //writeoutNode(formatChangesToXMLString(array[1].segments), 0, $scope.gridOptions.data);

    //     } else {
    //         $scope.gridOptions.data = [];
    //     }


    // };

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
        var delay = $q.defer();

        var group = {
            id: grp.id,
            name: grp.name,
            type: grp.type,
            minCard: grp.min,
            maxCard: grp.max,
            usage: grp.usage,
            position: grp.position,
            //segments: $scope.formatSeg(grp.children)
        };
        $scope.formatSegments(grp.children).then(function(result) {
            group.segments = result;
            delay.resolve(group);


        });
        return delay.promise;
        //return group;

    };
    $scope.formatSeg = function(segment) {
        var delay = $q.defer();

        if (segment.type === 'segmentRef') {
            var newSegment = {};
            newSegment = {
                id: segment.ref.id,
                name: segment.ref.name,
                label: segment.ref.label,
                type: segment.type,
                // fields: result,
                minCard: segment.min,
                maxCard: segment.max,
                usage: segment.usage,
                position: segment.position
            };
            SegmentService.get(segment.ref.id).then(function(segs) {
                newSegment.fields = segs.fields;

                delay.resolve(newSegment);
            });

        } else {
            $scope.formatGrp(segment).then(function(grp) {
                delay.resolve(grp);

            });

        }
        return delay.promise;



    };




    $scope.formatSegments = function(segments) {
        var delay = $q.defer();
        var promises = [];
        for (var i = 0; i < segments.length; i++) {
            if (segments[i].type === "group") {

                promises.push($scope.formatGrp(segments[i]));

            } else {
                promises.push($scope.formatSeg(segments[i]));

            }
        };
        $q.all(promises).then(function(segs) {
            delay.resolve(segs);
        });
        return delay.promise;


        //return result;


    };
    $scope.formatMsg = function(msg) {
        var delay = $q.defer();

        var message = {
            name: msg.name,
            event: msg.event,
            structID: msg.structID,
            position: msg.position,
            //segments: $scope.formatSeg(msg.children)
        }
        $scope.formatSegments(msg.children).then(function(result) {
            message.segments = result;
            delay.resolve(message);

        });

        return delay.promise;

        // console.log(message);
        // return message;

    };


    $scope.compare = function() {

        $scope.formatMsg(JSON.parse($scope.msg1)).then(function(msg1) {
            $scope.formatMsg(JSON.parse($scope.msg2)).then(function(msg2) {
                console.log("heree");
                //$scope.compareParams = $scope.getParams();
                $scope.diff = ObjectDiff.diffOwnProperties(msg1, msg2);

                $scope.params = new ngTreetableParams({
                    getNodes: function(parent) {
                        console.log($scope.diff);
                        return [{ name: 'foo', value: 'bar' }];
                    },
                    getTemplate: function(node) {
                        return 'TreeNode.html';
                    },
                    options: {
                        onNodeExpand: function() {
                            console.log('A node was expanded!');
                        }
                    }
                });




                // getPage();


            });

        });

        //$scope.diff = ObjectDiff.diffOwnProperties($scope.formatMsg(JSON.parse($scope.msg1)), $scope.formatMsg(JSON.parse($scope.msg2)));

        // you can directly diff your objects including prototype properties and inherited properties using `diff` method
        //$scope.diffAll = ObjectDiff.diff(JSON.parse($scope.msg1), JSON.parse($scope.msg2));

        // gives a full object view with Diff highlighted
        //$scope.diffValue = ObjectDiff.toJsonView($scope.diffAll);

        // gives object view with onlys Diff highlighted
        //$scope.diffValueChanges = ObjectDiff.toJsonDiffView($scope.diff);

        // console.log($scope.diff);
        // console.log($scope.diffAll);



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
