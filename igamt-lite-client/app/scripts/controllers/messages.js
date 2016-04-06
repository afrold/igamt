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

        $scope.delete = function(message) {
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

        $scope.manageConformanceStatement = function (node, message) {
            var modalInstance = $modal.open({
                templateUrl: 'ConformanceStatementMessageCtrl.html',
                controller: 'ConformanceStatementMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                    selectedMessage: function () {
                        return message;
                    },
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

        $scope.managePredicate = function (node, message) {
            var modalInstance = $modal.open({
                templateUrl: 'PredicateMessageCtrl.html',
                controller: 'PredicateMessageCtrl',
                windowClass: 'app-modal-window',
                resolve: {
                	selectedMessage: function () {
                        return message;
                    },
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
        
        $scope.countPredicate = function (position) {
            if ($rootScope.message != null) {
                for (var i = 0, len1 = $rootScope.message.predicates.length; i < len1; i++) {
                    if ($rootScope.message.predicates[i].constraintTarget.indexOf(position) === 0)
                        return 1;
                }
            }
            return 0;
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

angular.module('igl').controller('PredicateMessageCtrl', function ($scope, $modalInstance, selectedNode, selectedMessage, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.selectedMessage = selectedMessage;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.complexConstraintTrueUsage = null;
    $scope.complexConstraintFalseUsage = null;
    
    $scope.changed = false;
    $scope.tempPredicates = [];
    angular.copy($scope.selectedMessage.predicates, $scope.tempPredicates);
    
    $scope.setChanged = function () {
    	$scope.changed = true;
    }
       
    $scope.initPredicate = function(){
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
            contraintType: null,
            value: null,
            value2: null,
            trueUsage: null,
            falseUsage: null,
            valueSetId: null,
            bindingStrength: 'R',
            bindingLocation: '1'
        });

        for (var i = 0, len1 = $scope.selectedMessage.children.length; i < len1; i++) {
            if ($scope.selectedMessage.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedMessage.children[i].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'group',
                    node: $scope.selectedMessage.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedMessage.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref]
                };
                $scope.newConstraint.childNodes_1.push(segmentModel);
                $scope.newConstraint.childNodes_2.push(segmentModel);
            }
        }
    }

    $scope.deletePredicate = function (predicate) {
        $scope.tempPredicates.splice($scope.tempPredicates.indexOf(predicate), 1);
        $scope.changed = true;
    };
    
    
    $scope.deletePredicateByTarget = function () {
        for (var i = 0, len1 = $scope.tempPredicates.length; i < len1; i++) {
            if ($scope.tempPredicates[i].constraintTarget === $scope.selectedNode.path) {
                $scope.deletePredicate($scope.tempPredicates[i]);
                return true;
            }
        }
        return false;
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
                    name: $scope.newConstraint.location_1 + '-' + $scope.newConstraint.currentNode_1.node.fields[i].position,
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
                    name: $scope.newConstraint.location_2 + '-' + $scope.newConstraint.currentNode_2.node.fields[i].position,
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
    
    $scope.addComplexPredicate = function(){
        $scope.complexConstraint = $rootScope.generateCompositePredicate($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
        $scope.complexConstraint.trueUsage = $scope.complexConstraintTrueUsage;
        $scope.complexConstraint.falseUsage = $scope.complexConstraintFalseUsage;
    	$scope.complexConstraint.constraintId = $scope.newConstraint.datatype + '-' + $scope.selectedNode.position;
    	$scope.tempPredicates.push($scope.complexConstraint);
    	$scope.initComplexPredicate();
        $scope.changed = true;
    };
    

    $scope.addPredicate = function () {
        if ($scope.newConstraint.position_1 != null) {
        	$rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
        	var positionPath = selectedNode.path;
        	var cp = $rootScope.generatePredicate(positionPath, $scope.newConstraint);
            $scope.tempPredicates.push(cp);
            $scope.changed = true;
        }
        $scope.initPredicate();
    };

    $scope.ok = function () {
        $modalInstance.close($scope.selectedNode);
    };
    
    $scope.saveclose = function () {
    	angular.copy($scope.tempPredicates, $scope.selectedMessage.predicates);
    	$rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };
    
    $scope.initPredicate();

});


angular.module('igl').controller('ConformanceStatementMessageCtrl', function ($scope, $modalInstance, selectedMessage, selectedNode, $rootScope) {
    $scope.selectedNode = selectedNode;
    $scope.selectedMessage = selectedMessage;
    $scope.firstConstraint = null;
    $scope.secondConstraint = null;
    $scope.compositeType = null;
    $scope.complexConstraint = null;
    $scope.newComplexConstraintId = $rootScope.calNextCSID();
    
    $scope.changed = false;
    $scope.tempComformanceStatements = [];
    angular.copy($scope.selectedMessage.conformanceStatements, $scope.tempComformanceStatements);
    
    $scope.setChanged = function () {
    	$scope.changed = true;
    }
    
    $scope.initComplexStatement = function () {
    	$scope.firstConstraint = null;
        $scope.secondConstraint = null;
        $scope.compositeType = null;
        $scope.newComplexConstraintId = $rootScope.calNextCSID();
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
            constraintId: $rootScope.calNextCSID(),
            contraintType: null,
            value: null,
            value2: null,
	        valueSetId: null,
	        bindingStrength: 'R',
	        bindingLocation: '1'
        });

    	for (var i = 0, len1 = $scope.selectedMessage.children.length; i < len1; i++) {
            if ($scope.selectedMessage.children[i].type === 'group') {
                var groupModel = {
                    name: $scope.selectedMessage.children[i].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'group',
                    node: $scope.selectedMessage.children[i]
                };
                $scope.newConstraint.childNodes_1.push(groupModel);
                $scope.newConstraint.childNodes_2.push(groupModel);
            } else if ($scope.selectedMessage.children[i].type === 'segmentRef') {
                var segmentModel = {
                    name: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref].name,
                    position: $scope.selectedMessage.children[i].position,
                    type: 'segment',
                    node: $rootScope.segmentsMap[$scope.selectedMessage.children[i].ref]
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
                    name: $scope.newConstraint.location_1 + '-' + $scope.newConstraint.currentNode_1.node.fields[i].position,
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
                    name: $scope.newConstraint.location_2 + '-' + $scope.newConstraint.currentNode_2.node.fields[i].position,
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
    	$scope.tempComformanceStatements.splice($scope.tempComformanceStatements.indexOf(conformanceStatement), 1);
        $scope.changed = true;
    };
    
    $scope.addComplexConformanceStatement = function(){
        $scope.complexConstraint = $rootScope.generateCompositeConformanceStatement($scope.compositeType, $scope.firstConstraint, $scope.secondConstraint);
    	$scope.complexConstraint.constraintId = $scope.newComplexConstraintId;
    	if($rootScope.conformanceStatementIdList.indexOf($scope.complexConstraint.constraintId) == -1) $rootScope.conformanceStatementIdList.push($scope.complexConstraint.constraintId);
    	$scope.tempComformanceStatements.push($scope.complexConstraint);
    	$scope.initComplexStatement();
        $scope.changed = true;
    };
    
    $scope.addConformanceStatement = function () {
        if ($scope.newConstraint.position_1 != null) {
        	$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
        	var positionPath = selectedNode.path;
        	var cs = $rootScope.generateConformanceStatement(positionPath, $scope.newConstraint);
            $scope.tempComformanceStatements.push(cs);
            if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
            $scope.changed = true;
        }
        
        $scope.initConformanceStatement();
    };

    $scope.ok = function () {
    	angular.forEach($scope.tempComformanceStatements, function (cs) {
    		$rootScope.conformanceStatementIdList.splice($rootScope.conformanceStatementIdList.indexOf(cs.constraintId), 1);
    	});
    	
    	angular.forEach($scope.selectedMessage.conformanceStatements, function (cs) {
    		if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    	});
    	
        $modalInstance.close($scope.selectedNode);
    };
    
    $scope.saveclose = function () {
    	angular.forEach($scope.tempComformanceStatements, function (cs) {
    		if($rootScope.conformanceStatementIdList.indexOf(cs.constraintId) == -1) $rootScope.conformanceStatementIdList.push(cs.constraintId);
    	});
    	angular.copy($scope.tempComformanceStatements, $scope.selectedMessage.conformanceStatements);
    	$rootScope.recordChanged();
        $modalInstance.close($scope.selectedNode);
    };

    $scope.initConformanceStatement();
});



