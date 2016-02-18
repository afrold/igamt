/**
 * Created by haffo on 2/13/15.
 */

angular.module('igl')
    .controller('MessageListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams, $filter, $http, $modal, $timeout, CloneDeleteSvc) {
        $scope.init = function () {
        };

        $scope.copy = function(message) {
        		CloneDeleteSvc.copyMessage(message);
    			$rootScope.$broadcast('event:SetToC');
        }
        
        $scope.close = function () {
            $rootScope.message = null;
            if ($scope.messagesParams)
                $scope.messagesParams.refresh();
        };

        $scope.delete = function() {
    			CloneDeleteSvc.deleteMessage(message);
    			$rootScope.$broadcast('event:SetToC');
        }
        
        $scope.goToSegment = function (segmentId) {
            $scope.$emit('event:openSegment', $rootScope.segmentsMap[segmentId]);
        };
        
        $scope.goToDatatype = function (datatype) {
            $scope.$emit('event:openDatatype', datatype);
        };
        
        $scope.goToTable = function (table) {
            $scope.$emit('event:openTable', table);
        };

        $scope.hasChildren = function (node) {
          if(node && node != null){
          	if(node.type === 'group'){
          		return node.children && node.children.length > 0;
          	}else if(node.type === 'segmentRef'){
          		return $rootScope.segmentsMap[node.ref].fields && $rootScope.segmentsMap[node.ref].fields.length > 0;
          	}else if(node.type === 'field' || node.type === 'component'){
          		return $rootScope.datatypesMap[node.datatype].components && $rootScope.datatypesMap[node.datatype].components.length > 0;
          	}
          	
          	
          	return false;
          }else {
          	return false;
          }
          
        };
        
        $scope.isSub = function (component) {
            return $scope.isSubDT(component);
        };

        $scope.isSubDT = function (component) {
            return component.type === 'component' && $rootScope.parentsMap && $rootScope.parentsMap[component.id] && $rootScope.parentsMap[component.id].type === 'component';
        };

        $scope.countConformanceStatements = function (node) {
            if (node != null && node.conformanceStatements) {
                return node.conformanceStatements.length;
            }
            return 0;
        };

        $scope.countPredicates = function (node) {
            if (node != null && node.predicates) {
                return node.predicates.length;
            }
            return 0;
        };

        $scope.manageConformanceStatement = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'ConformanceStatementMessageCtrl.html',
                controller: 'ConformanceStatementMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function () {
                        return node;
                    }
                }
            });
            modalInstance.result.then(function (node) {
                $scope.selectedNode = node;
            }, function () {
            });
        };

        $scope.managePredicate = function (node) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateMessageCtrl.html',
                controller: 'PredicateMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedNode: function () {
                        return node;
                    }
                }
            });
            modalInstance.result.then(function (node) {
                $scope.selectedNode = node;
            }, function () {
            });
        };
    });


angular.module('igl')
    .controller('MessageRowCtrl', function ($scope, $filter) {
        $scope.formName = "form_" + new Date().getTime();
    });


angular.module('igl')
    .controller('MessageViewCtrl', function ($scope, $rootScope, Restangular) {
        $scope.loading = false;
        $scope.msg = null;
        $scope.messageData = [];
        $scope.setData = function (node) {
            if (node) {
                if (node.type === 'message') {
                    angular.forEach(node.children, function (segmentRefOrGroup) {
                        $scope.setData(segmentRefOrGroup);
                    });
                } else if (node.type === 'group') {
                    $scope.messageData.push({ name: "-- " + node.name + " begin"});
                    if (node.children) {
                        angular.forEach(node.children, function (segmentRefOrGroup) {
                            $scope.setData(segmentRefOrGroup);
                        });
                    }
                    $scope.messageData.push({ name: "-- " + node.name + " end"});
                } else if (node.type === 'segment') {
                    $scope.messageData.push + (node);
                }
            }
        };


        $scope.init = function (message) {
            $scope.loading = true;
            $scope.msg = message;
            console.log(message.id);
            $scope.setData($scope.msg);
            $scope.loading = false;
        };

//        $scope.hasChildren = function (node) {
//            return node && node != null && node.type !== 'segment' && node.children && node.children.length > 0;
//        };

    });

angular.module('igl').controller('PredicateMessageCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];
    $scope.newConstraint = angular.fromJson({
        position_T: null,
        position_1: null,
        position_2: null,
        location_T: null,
        location_1: null,
        location_2: null,
        currentNode_T: null,
        currentNode_1: null,
        currentNode_2: null,
        childNodes_T: [],
        childNodes_1: [],
        childNodes_2: [],
        verb: null,
        contraintType: null,
        value: null,
        trueUsage: null,
        falseUsage: null,
        valueSetId: null,
        bindingStrength: 'R',
        bindingLocation: '1',
        constraintClassification: 'E'
    });
    $scope.newConstraint.location_T = $scope.selectedNode.name;
    $scope.newConstraint.location_1 = $scope.selectedNode.name;
    $scope.newConstraint.location_2 = $scope.selectedNode.name;

    for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
        if ($scope.selectedNode.children[i].type === 'group') {
            var groupModel = {
                name: $scope.selectedNode.children[i].name,
                position: $scope.selectedNode.children[i].position,
                type: 'group',
                node: $scope.selectedNode.children[i]
            };
            $scope.newConstraint.childNodes_T.push(groupModel);
            $scope.newConstraint.childNodes_1.push(groupModel);
            $scope.newConstraint.childNodes_2.push(groupModel);
        } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
            var segmentModel = {
                name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                position: $scope.selectedNode.children[i].position,
                type: 'segment',
                node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
            };
            $scope.newConstraint.childNodes_T.push(segmentModel);
            $scope.newConstraint.childNodes_1.push(segmentModel);
            $scope.newConstraint.childNodes_2.push(segmentModel);
        }
    }
    
    $scope.initPredicate = function(){
    	$scope.newConstraint = angular.fromJson({
            position_T: null,
            position_1: null,
            position_2: null,
            location_T: null,
            location_1: null,
            location_2: null,
            currentNode_T: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_T: [],
            childNodes_1: [],
            childNodes_2: [],
            verb: null,
            contraintType: null,
            value: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.location_T = $scope.selectedNode.name;
        $scope.newConstraint.location_1 = $scope.selectedNode.name;
        $scope.newConstraint.location_2 = $scope.selectedNode.name;

        for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
            if ($scope.selectedNode.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedNode.children[i].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'group',
                    node: $scope.selectedNode.children[i]
                };
                $scope.newConstraint.childNodes_T.push(groupModel);
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
                };
                $scope.newConstraint.childNodes_T.push(segmentModel);
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
    }

    $scope.deletePredicate = function (predicate) {
        $scope.selectedNode.predicates.splice($scope.selectedNode.predicates.indexOf(predicate), 1);
        if (!$scope.isNewCP(predicate.id)) {
            $rootScope.recordChanged();
        }
    };
    
    $scope.deletePredicateForComplex = function (predicate) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(predicate), 1);
    };

    $scope.isNewCP = function (id) {
        if ($rootScope.isNewObject("predicate", "add", id)) {
            if ($rootScope.changes['predicate'] !== undefined && $rootScope.changes['predicate']['add'] !== undefined) {
                for (var i = 0; i < $rootScope.changes['predicate']['add'].length; i++) {
                    var tmp = $rootScope.changes['predicate']['add'][i];
                    if (tmp.obj.id === id) {
                        $rootScope.changes['predicate']['add'].splice(i, 1);

                        if ($rootScope.changes["predicate"]["add"] && $rootScope.changes["predicate"]["add"].length === 0) {
                            delete  $rootScope.changes["predicate"]["add"];
                        }

                        if ($rootScope.changes["predicate"] && Object.getOwnPropertyNames($rootScope.changes["predicate"]).length === 0) {
                            delete  $rootScope.changes["predicate"];
                        }


                        return true;
                    }
                }
            }
            return true;
        }
        return false;
    };

    $scope.updateLocationT = function () {
        $scope.newConstraint.location_T = $scope.newConstraint.currentNode_T.name;
        if ($scope.newConstraint.position_T != null) {
            $scope.newConstraint.position_T = $scope.newConstraint.position_T + '.' + $scope.newConstraint.currentNode_T.position + '[1]';
        } else {
            $scope.newConstraint.position_T = $scope.newConstraint.currentNode_T.position + '[1]';
        }

        $scope.newConstraint.childNodes_T = [];

        if ($scope.newConstraint.currentNode_T.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_T.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_T.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_T.node.children[i].name,
                        position: $scope.newConstraint.currentNode_T.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_T.node.children[i]
                    };
                    $scope.newConstraint.childNodes_T.push(groupModel);
                } else if ($scope.newConstraint.currentNode_T.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_T + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_T.node.children[i].ref].name,
                        position: $scope.newConstraint.currentNode_T.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_T.node.children[i].ref]
                    };
                    $scope.newConstraint.childNodes_T.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_T.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_T.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_T + '.' + $scope.newConstraint.currentNode_T.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_T.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_T.node.fields[i].datatype]
                };
                $scope.newConstraint.childNodes_T.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_T.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_T.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_T + '.' + $scope.newConstraint.currentNode_T.node.components[i].position,
                    position: $scope.newConstraint.currentNode_T.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_T.node.components[i].datatype]
                };
                $scope.newConstraint.childNodes_T.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_T.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_T.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_T + '.' + $scope.newConstraint.currentNode_T.node.components[i].position,
                    position: $scope.newConstraint.currentNode_T.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_T.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_T = null;

    };

    $scope.updateLocation1 = function () {
        $scope.newConstraint.location_1 = $scope.newConstraint.currentNode_1.name;
        if ($scope.newConstraint.position_1 != null) {
            $scope.newConstraint.position_1 = $scope.newConstraint.position_1 + '.' + $scope.newConstraint.currentNode_1.position + '[1]';
        } else {
            $scope.newConstraint.position_1 = $scope.newConstraint.currentNode_1.position + '[1]';
        }

        $scope.newConstraint.childNodes_1 = [];

        if ($scope.newConstraint.currentNode_1.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_1.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_1.node.children[i].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_1.node.children[i]
                    };
                    $scope.newConstraint.childNodes_1.push(groupModel);
                } else if ($scope.newConstraint.currentNode_1.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_1 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref]
                    };
                    $scope.newConstraint.childNodes_1.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_1.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.fields[i].datatype]
                };
                $scope.newConstraint.childNodes_1.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.components[i].datatype]
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_1 = null;

    };

    $scope.updateLocation2 = function () {
        $scope.newConstraint.location_2 = $scope.newConstraint.currentNode_2.name;
        if ($scope.newConstraint.position_2 != null) {
            $scope.newConstraint.position_2 = $scope.newConstraint.position_2 + '.' + $scope.newConstraint.currentNode_2.position + '[1]';
        } else {
            $scope.newConstraint.position_2 = $scope.newConstraint.currentNode_2.position + '[1]';
        }

        $scope.newConstraint.childNodes_2 = [];

        if ($scope.newConstraint.currentNode_2.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_2.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_2.node.children[i].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_2.node.children[i]
                    };
                    $scope.newConstraint.childNodes_2.push(groupModel);
                } else if ($scope.newConstraint.currentNode_2.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_2 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref]
                    };
                    $scope.newConstraint.childNodes_2.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_2.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.fields[i].datatype]
                };
                $scope.newConstraint.childNodes_2.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.components[i].datatype]
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_2 = null;

    };
    
    $scope.changeConstraintType = function () {
    	$scope.newConstraint = angular.fromJson({
            position_T: null,
            position_1: null,
            position_2: null,
            location_T: null,
            location_1: null,
            location_2: null,
            currentNode_T: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_T: [],
            childNodes_1: [],
            childNodes_2: [],
            verb: null,
            contraintType: null,
            value: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1',
            constraintClassification: 'E'
        });
        $scope.newConstraint.location_T = $scope.selectedNode.name;
        $scope.newConstraint.location_1 = $scope.selectedNode.name;
        $scope.newConstraint.location_2 = $scope.selectedNode.name;

        for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
            if ($scope.selectedNode.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedNode.children[i].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'group',
                    node: $scope.selectedNode.children[i]
                };
                $scope.newConstraint.childNodes_T.push(groupModel);
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
                };
                $scope.newConstraint.childNodes_T.push(segmentModel);
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
        
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }
    
    $scope.addComplexConformanceStatement = function(){
        $scope.complexConstraint.constraintId = $scope.complexConstraint.constraintTarget;
        $scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
        $scope.selectedNode.predicates.push($scope.complexConstraint);
        var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: $scope.complexConstraint};
        $rootScope.recordChanged();
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        
        $scope.complexConstraint = null;
        $scope.newComplexConstraintClassification = 'E';
    };
    
    $scope.compositeConformanceStatements = function(){
    	if($scope.compositeType === 'AND'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + $scope.firstConstraint.constraintId  + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.firstConstraint.constraintTarget,
                    description: '['+ $scope.firstConstraint.description + '] ' + 'AND' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<AND>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</AND>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'OR'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.firstConstraint.constraintTarget,
                    description: '['+ $scope.firstConstraint.description + '] ' + 'OR' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<OR>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</OR>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'IFTHEN'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: $scope.firstConstraint.constraintTarget,
                    description: 'IF ['+ $scope.firstConstraint.description + '] ' + 'THEN ' + ' [' + $scope.secondConstraint.description + ']',
                    trueUsage: $scope.firstConstraint.trueUsage,
                    falseUsage: $scope.firstConstraint.falseUsage,
                    assertion: '<IMPLY>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</IMPLY>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}
    	
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };
    

    $scope.addPredicate = function () {
        $rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;

        if ($scope.newConstraint.position_1 != null) {
            if ($scope.newConstraint.contraintType === 'valued') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Presence Path=\"' + $scope.newConstraint.position_1 + '\"/>'
                    };
                    $scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType,
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Presence Path=\"' + $scope.newConstraint.position_1 + '\"/>'
                    };
                	$scope.newComplexConstraint.push(cp);
            	}

            } else if ($scope.newConstraint.contraintType === 'a literal value') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PlainText Path=\"' + $scope.newConstraint.position_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                    };
                    $scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PlainText Path=\"' + $scope.newConstraint.position_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                    };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<StringList Path=\"' + $scope.newConstraint.position_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                        };
                    $scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<StringList Path=\"' + $scope.newConstraint.position_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'one of codes in ValueSet') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<ValueSet Path=\"' + $scope.newConstraint.position_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<ValueSet Path=\"' + $scope.newConstraint.position_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'formatted value') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Format Path=\"' + $scope.newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                        };
                    $scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<Format Path=\"' + $scope.newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + $scope.newConstraint.location_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
                        $scope.selectedNode.predicates.push(cp);
                        var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                        $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
                            id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + $scope.newConstraint.location_2 + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.newComplexConstraint.push(cp);
            	}
            }
            
            
            
            else if ($scope.newConstraint.contraintType === 'equal to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'not-equal to the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'greater than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'less than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or less than the another node') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + $scope.newConstraint.location_2  + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'not-equal to') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'greater than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'less than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            } else if ($scope.newConstraint.contraintType === 'equal to or less than') {
            	if($scope.constraintType === 'Plain'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: $scope.newConstraint.location_T,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		$scope.selectedNode.predicates.push(cp);
                    var newCPBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cp};
                    $rootScope.recordChanged();
            	}else if ($scope.constraintType === 'Complex'){
            		var cp = {
            				id: new ObjectId().toString(),
                            constraintId: 'CP' + $rootScope.newPredicateFakeId,
                            constraintTarget: $scope.newConstraint.position_T,
                            constraintClassification: $scope.newConstraint.constraintClassification,
                            description: 'If the value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                            trueUsage: $scope.newConstraint.trueUsage,
                            falseUsage: $scope.newConstraint.falseUsage,
                            assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                        };
            		
            		$scope.newComplexConstraint.push(cp);
            	}
            }
        }
        
        $scope.initPredicate();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});


angular.module('igl').controller('ConformanceStatementMessageCtrl', function ($scope, $modalInstance, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.constraintType = 'Plain';
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = '';
    $scope.newComplexConstraintClassification = 'E';
    $scope.newComplexConstraint = [];
    
    $scope.newConstraint = angular.fromJson({
        position_1: null,
        position_2: null,
        location_1: null,
        location_2: null,
        currentNode_1: null,
        currentNode_2: null,
        childNodes_1: [],
        childNodes_2: [],
        verb: null,
        constraintId: null,
        contraintType: null,
        value: null,
        valueSetId: null,
        bindingStrength: 'R',
        bindingLocation: '1',
        constraintClassification: 'E'
    });
    $scope.newConstraint.location_1 = $scope.selectedNode.name;
    $scope.newConstraint.location_2 = $scope.selectedNode.name;

    for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
        if ($scope.selectedNode.children[i].type === 'group') {
            var groupModel = {
                name: $scope.selectedNode.children[i].name,
                position: $scope.selectedNode.children[i].position,
                type: 'group',
                node: $scope.selectedNode.children[i]
            };
            $scope.newConstraint.childNodes_1.push(groupModel);
            $scope.newConstraint.childNodes_2.push(groupModel);
        } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
            var segmentModel = {
                name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                position: $scope.selectedNode.children[i].position,
                type: 'segment',
                node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
            };
            $scope.newConstraint.childNodes_1.push(segmentModel);
            $scope.newConstraint.childNodes_2.push(segmentModel);
        }
    }
    
    
    $scope.initConformanceStatement = function (){
    	$scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_1: [],
            childNodes_2: [],
            verb: null,
            constraintId: null,
            contraintType: null,
            value: null,
	        valueSetId: null,
	        bindingStrength: 'R',
	        bindingLocation: '1',
	        constraintClassification: 'E'
        });
        $scope.newConstraint.location_1 = $scope.selectedNode.name;
        $scope.newConstraint.location_2 = $scope.selectedNode.name;

        for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
            if ($scope.selectedNode.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedNode.children[i].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'group',
                    node: $scope.selectedNode.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
                };
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
    }

    $scope.updateLocation1 = function () {
        $scope.newConstraint.location_1 = $scope.newConstraint.currentNode_1.name;
        if ($scope.newConstraint.position_1 != null) {
            $scope.newConstraint.position_1 = $scope.newConstraint.position_1 + '.' + $scope.newConstraint.currentNode_1.position + '[1]';
        } else {
            $scope.newConstraint.position_1 = $scope.newConstraint.currentNode_1.position + '[1]';
        }

        $scope.newConstraint.childNodes_1 = [];

        if ($scope.newConstraint.currentNode_1.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_1.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_1.node.children[i].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_1.node.children[i]
                    };
                    $scope.newConstraint.childNodes_1.push(groupModel);
                } else if ($scope.newConstraint.currentNode_1.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_1 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref].name,
                        position: $scope.newConstraint.currentNode_1.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_1.node.children[i].ref]
                    };
                    $scope.newConstraint.childNodes_1.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_1.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.fields[i].datatype]
                };
                $scope.newConstraint.childNodes_1.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_1.node.components[i].datatype]
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_1.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_1.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_1 + '.' + $scope.newConstraint.currentNode_1.node.components[i].position,
                    position: $scope.newConstraint.currentNode_1.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_1.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_1 = null;

    };

    $scope.updateLocation2 = function () {
        $scope.newConstraint.location_2 = $scope.newConstraint.currentNode_2.name;
        if ($scope.newConstraint.position_2 != null) {
            $scope.newConstraint.position_2 = $scope.newConstraint.position_2 + '.' + $scope.newConstraint.currentNode_2.position + '[1]';
        } else {
            $scope.newConstraint.position_2 = $scope.newConstraint.currentNode_2.position + '[1]';
        }

        $scope.newConstraint.childNodes_2 = [];

        if ($scope.newConstraint.currentNode_2.type === 'group') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.children.length; i < len1; i++) {
                if ($scope.newConstraint.currentNode_2.node.children[i].type === 'group') {
                    var groupModel = {
                        name: $scope.newConstraint.currentNode_2.node.children[i].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'group',
                        node: $scope.newConstraint.currentNode_2.node.children[i]
                    };
                    $scope.newConstraint.childNodes_2.push(groupModel);
                } else if ($scope.newConstraint.currentNode_2.node.children[i].type === 'segmentRef') {
                    var segmentModel = {
                        name: $scope.newConstraint.location_2 + '.' + $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref].name,
                        position: $scope.newConstraint.currentNode_2.node.children[i].position,
                        type: 'segment',
                        node: $rootScope.segmentsMap[$scope.newConstraint.currentNode_2.node.children[i].ref]
                    };
                    $scope.newConstraint.childNodes_2.push(segmentModel);
                }
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'segment') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.fields.length; i < len1; i++) {
                var fieldModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.fields[i].position,
                    position: $scope.newConstraint.currentNode_2.node.fields[i].position,
                    type: 'field',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.fields[i].datatype]
                };
                $scope.newConstraint.childNodes_2.push(fieldModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'field') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: $rootScope.datatypesMap[$scope.newConstraint.currentNode_2.node.components[i].datatype]
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        } else if ($scope.newConstraint.currentNode_2.type === 'subComponent') {
            for (var i = 0, len1 = $scope.newConstraint.currentNode_2.node.components.length; i < len1; i++) {
                var componentModel = {
                    name: $scope.newConstraint.location_2 + '.' + $scope.newConstraint.currentNode_2.node.components[i].position,
                    position: $scope.newConstraint.currentNode_2.node.components[i].position,
                    type: 'subComponent',
                    node: null
                };
                $scope.newConstraint.childNodes_2.push(componentModel);
            }
        }

        $scope.newConstraint.currentNode_2 = null;

    };

    $scope.deleteConformanceStatement = function (conformanceStatement) {
        $scope.selectedNode.conformanceStatements.splice($scope.selectedNode.conformanceStatements.indexOf(conformanceStatement), 1);
        if (!$scope.isNewCS(conformanceStatement.id)) {
            $rootScope.recordChanged();
        }
    };
    
    $scope.deleteConformanceStatementForComplex = function (conformanceStatement) {
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf(conformanceStatement), 1);
    };


    $scope.isNewCS = function (id) {
        if ($rootScope.isNewObject("conformanceStatement", "add", id)) {
            if ($rootScope.changes['conformanceStatement'] !== undefined && $rootScope.changes['conformanceStatement']['add'] !== undefined) {
                for (var i = 0; i < $rootScope.changes['conformanceStatement']['add'].length; i++) {
                    var tmp = $rootScope.changes['conformanceStatement']['add'][i];
                    if (tmp.obj.id === id) {
                        $rootScope.changes['conformanceStatement']['add'].splice(i, 1);
                        if ($rootScope.changes["conformanceStatement"]["add"] && $rootScope.changes["conformanceStatement"]["add"].length === 0) {
                            delete  $rootScope.changes["conformanceStatement"]["add"];
                        }

                        if ($rootScope.changes["conformanceStatement"] && Object.getOwnPropertyNames($rootScope.changes["conformanceStatement"]).length === 0) {
                            delete  $rootScope.changes["conformanceStatement"];
                        }
                        return true;
                    }

                }
            }
            return true;
        }
        return false;
    };
    
    $scope.changeConstraintType = function () {
    	$scope.newConstraint = angular.fromJson({
            position_1: null,
            position_2: null,
            location_1: null,
            location_2: null,
            currentNode_1: null,
            currentNode_2: null,
            childNodes_1: [],
            childNodes_2: [],
            verb: null,
            constraintId: null,
            contraintType: null,
            value: null,
	        valueSetId: null,
	        bindingStrength: 'R',
	        bindingLocation: '1',
	        constraintClassification: 'E'
        });
        $scope.newConstraint.location_1 = $scope.selectedNode.name;
        $scope.newConstraint.location_2 = $scope.selectedNode.name;

        for (var i = 0, len1 = $scope.selectedNode.children.length; i < len1; i++) {
            if ($scope.selectedNode.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedNode.children[i].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'group',
                    node: $scope.selectedNode.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedNode.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $scope.selectedNode.name + '.' + $rootScope.segmentsMap[$scope.selectedNode.children[i].ref].name,
                    position: $scope.selectedNode.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedNode.children[i].ref]
                };
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
		
    	if($scope.constraintType === 'Complex'){
    		$scope.newComplexConstraint = [];
    		$scope.newComplexConstraintId = '';
    		$scope.newComplexConstraintClassification = 'E';
    	}
    }
    
    $scope.addComplexConformanceStatement = function(){
    	$scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    	$scope.complexConstraint.constraintClassification = $scope.newComplexConstraintClassification;
    	
    	$scope.selectedNode.conformanceStatements.push($scope.complexConstraint);
        var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: $scope.complexConstraint};
        $rootScope.recordChanged();
        
        $scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.complexConstraint), 1);
        
        $scope.complexConstraint = null;
        $scope.newComplexConstraintId = '';
        $scope.newComplexConstraintClassification = 'E';
    };
    
    $scope.compositeConformanceStatements = function(){
    	if($scope.compositeType === 'AND'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'AND(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: '.',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'AND' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<AND>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</AND>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'OR'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'OR(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: '.',
                    description: '['+ $scope.firstConstraint.description + '] ' + 'OR' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<OR>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</OR>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}else if($scope.compositeType === 'IFTHEN'){
    		var cs = {
                    id: new ObjectId().toString(),
                    constraintId: 'IFTHEN(' + $scope.firstConstraint.constraintId + ',' + $scope.secondConstraint.constraintId + ')',
                    constraintTarget: '.',
                    description: 'IF ['+ $scope.firstConstraint.description + '] ' + 'THEN ' + ' [' + $scope.secondConstraint.description + ']',
                    assertion: '<IMPLY>' + $scope.firstConstraint.assertion + $scope.secondConstraint.assertion + '</IMPLY>'
            };
    		$scope.newComplexConstraint.push(cs);
    	}
    	
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.firstConstraint), 1);
    	$scope.newComplexConstraint.splice($scope.newComplexConstraint.indexOf($scope.secondConstraint), 1);
    	
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
    };
    

    $scope.addConformanceStatement = function () {
        $rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;

        if ($scope.newConstraint.position_1 != null) {
            if ($scope.newConstraint.contraintType === 'valued') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + '.',
                    assertion: '<Presence Path=\"' + $scope.newConstraint.position_1 + '\"/>'
                };
                
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'a literal value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<PlainText Path=\"' + $scope.newConstraint.position_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'one of list values') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
                    assertion: '<StringList Path=\"' + $scope.newConstraint.position_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'one of codes in ValueSet') {
                var cs = {
                        id: new ObjectId().toString(),
                        constraintId: $scope.newConstraint.constraintId,
                        constraintTarget: '.',
                        constraintClassification: $scope.newConstraint.constraintClassification,
                        description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' ' + $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.valueSetId + '.',
                        assertion: '<ValueSet Path=\"' + $scope.newConstraint.position_1 + '\" ValueSetID=\"' + $scope.newConstraint.valueSetId + '\" BindingStrength=\"' + $scope.newConstraint.bindingStrength + '\" BindingLocation=\"' + $scope.newConstraint.bindingLocation +'\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            }  else if ($scope.newConstraint.contraintType === 'formatted value') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
                    assertion: '<Format Path=\"' + $scope.newConstraint.position_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'identical to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' identical to the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'not-equal to the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'greater than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
               }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'less than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or less than the another node') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than the value of ' + $scope.newConstraint.location_2 + '.',
                    assertion: '<PathValue Path1=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Path2=\"' + $scope.newConstraint.position_2 + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="EQ" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'not-equal to') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' different with ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="NE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'greater than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' greater than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GT" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or greater than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or greater than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="GE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'less than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' less than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LT" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            } else if ($scope.newConstraint.contraintType === 'equal to or less than') {
                var cs = {
                    id: new ObjectId().toString(),
                    constraintId: $scope.newConstraint.constraintId,
                    constraintTarget: '.',
                    constraintClassification: $scope.newConstraint.constraintClassification,
                    description: 'The value of ' + $scope.newConstraint.location_1 + ' ' + $scope.newConstraint.verb + ' equal to or less than ' + $scope.newConstraint.value + '.',
                    assertion: '<SimpleValue Path=\"' + $scope.newConstraint.position_1 + '\" Operator="LE" Value=\"' + $scope.newConstraint.value + '\"/>'
                };
                if($scope.constraintType === 'Plain'){
                	$scope.selectedNode.conformanceStatements.push(cs);
                    var newCSBlock = {targetType: 'group', targetId: $scope.selectedNode.id, obj: cs};
                    $rootScope.recordChanged();
                }else if ($scope.constraintType === 'Complex'){
                	$scope.newComplexConstraint.push(cs);
                }
            }
        }
        
        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };

});



