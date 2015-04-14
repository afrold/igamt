/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('DatatypeListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter, $http,$modal) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.tmpDatatypes =[].concat($rootScope.datatypes);
        $scope.datatypeCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.datatype ? parent.datatype.components: parent.components : $rootScope.datatype != null ? $rootScope.datatype.components:[];
                },
                getTemplate: function (node) {
                    return 'DatatypeEditTree.html';
                }
                ,
                options: {
                    initialState: 'expanded'
                }
            });

            $scope.$watch(function () {
                return $rootScope.notifyDtTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                }
            });

            $scope.loading = false;
        };

        $scope.select = function (datatype) {
            $rootScope.datatype = datatype;
            $rootScope.datatype["type"] = "datatype";
            if ($scope.params)
                $scope.params.refresh();
//            $rootScope.go('/profiles#datatypeDef');
            $scope.loadingSelection = false;
         };

        $scope.flavor = function (datatype) {
            var flavor = angular.copy(datatype);
            var id = (Math.floor(Math.random()*10000000) + 1);
            flavor.id = - 1 * id;
            flavor.label = datatype.label + "_"+id;
            if(flavor.components != undefined && flavor.components != null && flavor.components.length != 0){
                for(var i=0; i < flavor.components.length; i++){
                    flavor.components[i].id = -1* (Math.floor(Math.random()*10000000) + 1);
                    flavor.components[i].datatype = datatype.components[i].datatype;
                }
            }
            $rootScope.datatypes.splice(0, 0, flavor);
            $rootScope.datatype = flavor;
            var tmp = angular.copy(flavor);
            if(tmp.components != undefined && tmp.components != null && tmp.components.length != 0){
                angular.forEach(tmp.components, function (component) {
                    component.datatype = component.datatype.id;
                    if(component.table != undefined) {
                        component.table =component.table.id;
                    }
                });
            }
            var predicates = tmp['predicates'];
            if( predicates!= undefined && predicates != null && predicates.length != 0){
                angular.forEach(predicates, function (predicate) {
                    predicate.id = -1 * (Math.floor(Math.random()*10000000) + 1);
                });
            }
            var conformanceStatements = tmp['conformanceStatements'];
            if(conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0){
                angular.forEach(conformanceStatements, function (conformanceStatement) {
                    conformanceStatement.id = -1 * (Math.floor(Math.random()*10000000) + 1);
                });
            }
//            $rootScope.recordChange2('datatype',tmp.id,null,tmp);
            $rootScope.recordChangeForEdit2('datatype', "add", flavor.id,'datatype', tmp);

//            $rootScope.recordChangeForEdit2('datatype', "add", null,null, tmp);
//            type,command,id,valueType,value


            $scope.select(flavor);

        };

        $scope.close = function(){
            $rootScope.datatype = null;
             if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.delete = function (datatype) {
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                $rootScope.findDatatypeRefs(datatype,segment);
            });
            if( $rootScope.references != null &&  $rootScope.references.length > 0){
                $scope.abortDelete(datatype);
            }else {
                $scope.confirmDelete(datatype);
            }
        };

        $scope.abortDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'DatatypeReferencesCtrl.html',
                controller: 'DatatypeReferencesCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
            });
        };

        $scope.confirmDelete = function (datatype) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeDeleteCtrl.html',
                controller: 'ConfirmDatatypeDeleteCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                $scope.dtToDelete = datatype;
            }, function () {
             });
        };


        $scope.hasChildren = function(node){
            return node && node != null && node.datatype && node.datatype.components != null && node.datatype.components.length >0;
        };


        $scope.validateLabel = function (label, name) {
           if(label && !label.startsWith(name)){
               return false;
           }
           return true;
        };

        $scope.onDatatypeChange = function(node){
            $rootScope.recordChangeForEdit2('component','edit',node.id,'datatype',node.id);
            $scope.refreshTree(); // TODO: Refresh only the node
         };

        $scope.refreshTree = function(){
            if ($scope.params)
                $scope.params.refresh();
        };
		
		$scope.goToTable = function(table){
        	$rootScope.table = table;
            $rootScope.notifyTableTreeUpdate = new Date().getTime();
            $rootScope.selectProfileTab(4);
        };
        
        $scope.deleteTable = function (node) {
        	node.table = null;
        	$rootScope.recordChange(node,'table');
        };
        
        $scope.showTableMapModal = false;
        
        $scope.displayedTableCollection = [].concat($scope.tables);
        $scope.displayedCodeCollection = [];
        
        
        $scope.selectedNode = null;
		$scope.mapTable = function(node) {
			$scope.selectedNode = node;
			$scope.selectedTable = node.table;
			if($scope.selectedTable != undefined)
				$scope.displayedCodeCollection = [].concat($scope.selectedTable.codes);
			$scope.showTableMapModal = !$scope.showTableMapModal;
		};
		
		$scope.selectedTable = null;
		$scope.selectTable = function(table){
			$scope.selectedTable = table;
			$scope.displayedCodeCollection = [].concat($scope.selectedTable.codes);
	        
		};
		
		$scope.mappingTable = function(){
			$scope.selectedNode.table = $scope.selectedTable;
			$rootScope.recordChange($scope.selectedNode,'table');
			$scope.showTableMapModal = false;
		};
		
		$scope.countPredicate = function(position){
			for(var i=0, len = $rootScope.datatype.predicates.length; i < len; i ++){
				if($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
					return 1;
			}
			
			return 0;
		};
		
		$scope.showPredicateManagerModal = false;
		$scope.showConformanceStatementManagerModal = false;
		$scope.selectedPosition = null;
		$scope.newConstraint = null;
		
		$scope.managePredicate = function(position){
			$scope.selectedPosition = position;
			$scope.showPredicateManagerModal = !$scope.showPredicateManagerModal;	
			$scope.newConstraint = angular.fromJson({
				datatype : '',
				component_1 : null,
				subComponent_1 : null,
				component_2 : null,
				subComponent_2 : null,
				verb : null,
				contraintType : null,
				value : null,
				trueUsage : null,
				falseUsage : null
			});
			$scope.newConstraint.datatype = $rootScope.datatype.name;
		};
		
		$scope.deletePredicate = function(predicate){
			$rootScope.datatype.predicates.splice($rootScope.datatype.predicates.indexOf(predicate),1);
			if(!$scope.isNewCP(predicate.id)){
				$rootScope.listToBeDeletedPredicates.push({id: predicate.id});
				$rootScope.recordChange2('predicates',"delete",null,$rootScope.listToBeDeletedPredicates);
			}
		};
		
		$scope.deleteConformanceStatement = function(conformanceStatement){
			$rootScope.datatype.conformanceStatements.splice($rootScope.datatype.conformanceStatements.indexOf(conformanceStatement),1);
			
			if(!$scope.isNewCS(conformanceStatement.id)){
				$rootScope.listToBeDeletedConformanceStatements.push({id: conformanceStatement.id});
				$rootScope.recordChange2('conformanceStatements',"delete",null,$rootScope.listToBeDeletedConformanceStatements);
			}
		};
		
		
		$scope.isNewCS = function(id){
			for(var i=0, len = $rootScope.listToBeAddedConformanceStatements.length; i < len; i ++){
				if($rootScope.listToBeAddedConformanceStatements[i].constraint.id === id){
					$rootScope.listToBeAddedConformanceStatements.splice(i, 1);
					return true;
				}
			}
			return false;
		};
		
		$scope.isNewCP = function(id){
			for(var i=0, len = $rootScope.listToBeAddedPredicates.length; i < len; i ++){
				if($rootScope.listToBeAddedPredicates[i].constraint.id === id){
					$rootScope.listToBeAddedPredicates.splice(i, 1);
					return true;
				}
			}
			return false;
		};
		
		$scope.updateComponent_1 = function(){
			$scope.newConstraint.subComponent_1 = null;
		};
		
		$scope.updateComponent_2 = function(){
			$scope.newConstraint.subComponent_2 = null;
		};
		
		
		$scope.manageConformanceStatement = function(position){
			$scope.selectedPosition = position;
			$scope.showConformanceStatementManagerModal = !$scope.showConformanceStatementManagerModal;	
			$scope.newConstraint = angular.fromJson({
				datatype : '',
				component_1 : null,
				subComponent_1 : null,
				component_2 : null,
				subComponent_2 : null,
				verb : null,
				constraintId : null,
				contraintType : null,
				value: null
			});
			$scope.newConstraint.datatype = $rootScope.datatype.name;
		};
		
		$scope.isSubDT = function(componentId){
			for(var i=0, len = $rootScope.datatype.components.length; i < len; i ++){
				if($rootScope.datatype.components[i].id === componentId)
					return false;
			}
			
			return true;
		};
		
		$scope.findDTByComponentId = function(componentId){
//			for(var i=0, len1 = $rootScope.datatypes.length; i < len1; i ++){
//				for(var j=0, len2 = $rootScope.datatypes[i].components.length; j<len2;j++ ){
//					if($rootScope.datatypes[i].components[j].id == componentId)
//						return $rootScope.datatypes[i];
//				}
//			}
            return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId].datatype: null;
 		};
		
		
		
		$scope.deletePredicateByTarget = function(){
			for(var i=0, len1 = $rootScope.datatype.predicates.length; i < len1; i ++){
					if($rootScope.datatype.predicates[i].constraintTarget.indexOf($scope.selectedPosition + '[') === 0){
						if(!$scope.isNewCP($rootScope.datatype.predicates[i].id)){
							$rootScope.listToBeDeletedPredicates.push({id: $rootScope.datatype.predicates[i].id});
							$rootScope.recordChange2('predicates',"delete",null,$rootScope.listToBeDeletedPredicates);
						}
						$rootScope.datatype.predicates.splice(i, 1);
						return true;
					}
				
			}
			return false;
		};
		
		$scope.updatePredicate = function() {
			$rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
			$scope.deletePredicateByTarget();
			
			var position_1 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var position_2 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
			var location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
			
			if(position_1 != null){
				if($scope.newConstraint.contraintType === 'valued'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.datatype + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType,
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<Presence Path=\"' + location_1 + '\"/>'
						};
					$rootScope.datatype.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({datatypeId: $rootScope.datatype.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'a literal value'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.datatype + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
						};
					$rootScope.datatype.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({datatypeId: $rootScope.datatype.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'one of list values'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.datatype + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
						};
					$rootScope.datatype.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({datatypeId: $rootScope.datatype.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'formatted value'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.datatype + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<Format Path=\"'+ location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
						};
					$rootScope.datatype.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({datatypeId: $rootScope.datatype.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'identical to the another node'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.datatype + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
						};
					$rootScope.datatype.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({datatypeId: $rootScope.datatype.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}
			}
		};

        $scope.genPosition = function(datatype, component, subComponent){
        	var position = null;
        	if(component != null && subComponent == null){
				position = datatype + '.' + component.position;
			}else if(component != null && subComponent != null){
				position = datatype + '.' + component.position + '.' + subComponent.position;
			}
        	
        	return position;
        };
        
        $scope.genLocation = function(segment, field, component, subComponent){
        	var location = null;
        	if(field != null && component == null && subComponent == null){
				location = field.position + '[1]';
			}else if(field != null && component != null && subComponent == null){
				location = field.position + '[1].' + component.position + '[1]';
			}else if(field != null && component != null && subComponent != null){
				location = field.position + '[1].' + component.position + '[1].' + subComponent.position + '[1]';
			}
        	
        	return location;
        };
		
		$scope.addConformanceStatement = function() {
			$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
			
			var position_1 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var position_2 = $scope.genPosition($scope.newConstraint.datatype, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
			var location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);
			
			if(position_1 != null){
				if($scope.newConstraint.contraintType === 'valued'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType,
							assertion : '<Presence Path=\"' + location_1 + '\"/>'
						};
					$rootScope.datatype.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({datatypeId: $rootScope.datatype.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'a literal value'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
							assertion : '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
						};
					$rootScope.datatype.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({datatypeId: $rootScope.datatype.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'one of list values'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
							assertion : '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
						};
					$rootScope.datatype.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({datatypeId: $rootScope.datatype.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'formatted value'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
							assertion : '<Format Path=\"'+ location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
						};
					$rootScope.datatype.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({datatypeId: $rootScope.datatype.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'identical to the another node'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
							assertion : '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
						};
					$rootScope.datatype.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({datatypeId: $rootScope.datatype.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}
			}
		};
		
		$scope.countConformanceStatements = function(position){
			var count = 0;
			if($rootScope.datatype != null)
            for(var i=0, len1 = $rootScope.datatype.conformanceStatements.length; i < len1; i ++){
				if($rootScope.datatype.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
					count = count + 1;
			}
			
			return count;
		};
		
		$scope.countPredicate = function(position){
            if($rootScope.datatype != null)
			for(var i=0, len1 = $rootScope.datatype.predicates.length; i < len1; i ++){
				if($rootScope.datatype.predicates[i].constraintTarget.indexOf(position + '[') === 0)
					return 1;
			}
			
			return 0;
		};
    });




angular.module('igl')
    .controller('DatatypeRowCtrl', function ($scope,$filter) {
         $scope.formName = "form_"+ new Date().getTime();
    });



angular.module('igl').controller('ConfirmDatatypeDeleteCtrl', function ($scope, $modalInstance, dtToDelete,$rootScope) {
    $scope.dtToDelete = dtToDelete;
    $scope.loading = false;
    $scope.delete = function () {
        $scope.loading = true;
        // remove any change made to components
//        if($scope.dtToDelete.id < 0){ // new object
//          $rootScope.removeObjectFromChanges("datatype", "add", id);
//        }else{
//
//            $rootScope.changes['datatype']['delete'].push({id:$scope.dtToDelete.id});
//        }

//        if($scope.dtToDelete.components != undefined && $scope.dtToDelete.components != null && $scope.dtToDelete.components.length > 0){
//            angular.forEach($scope.dtToDelete.components, function (component) {
//                if($rootScope.changes['component'] && $rootScope.changes['component'][component.id] && $rootScope.changes['component'][component.id]){
//                    delete $rootScope.changes['component'][component.id];
//                }
//            });
//        }
//        if( $rootScope.changes['component'] && Object.getOwnPropertyNames($rootScope.changes['component']).length === 0){
//            delete $rootScope.changes['component'];
//        }
//        // remove any change made to datatype
//        if($rootScope.changes['datatype'] != undefined  && $rootScope.changes['datatype'][$scope.dtToDelete.id] != undefined){
//            if($scope.dtToDelete.id < 0){
//                delete $rootScope.changes['datatype'][$scope.dtToDelete.id];
//                if( Object.getOwnPropertyNames($rootScope.changes['datatype']).length === 0){
//                    delete $rootScope.changes['datatype'];
//                }
//            }else{
//                $rootScope.changes['datatype'][$scope.dtToDelete.id] = null;
//            }
//        }

        var index = $rootScope.datatypes.indexOf($scope.dtToDelete);
        if (index > -1) $rootScope.datatypes.splice(index, 1);
        if($rootScope.datatype === $scope.dtToDelete){
            $rootScope.datatype = null;
        }
        $rootScope.references = [];

        $rootScope.recordDelete("datatype", "edit",  $scope.dtToDelete.id );
        if($scope.dtToDelete.components != undefined && $scope.dtToDelete.components != null && $scope.dtToDelete.components.length > 0){

            //clear components changes
            angular.forEach($scope.dtToDelete.components, function (component) {
                $rootScope.recordDelete("component", "edit",  component.id);
                $rootScope.removeObjectFromChanges("component", "delete", component.id);
            });
            if ($rootScope.changes["component"]["delete"] && $rootScope.changes["component"]["delete"].length === 0) {
                delete  $rootScope.changes["component"]["delete"];
            }

            if ($rootScope.changes["component"]&& $rootScope.changes["component"].length === 0) {
                delete  $rootScope.changes["component"];
            }

        }

        if($scope.dtToDelete.predicates != undefined && $scope.dtToDelete.predicates != null && $scope.dtToDelete.predicates.length > 0) {
            //clear predicates changes
            angular.forEach($scope.dtToDelete.predicates, function (predicate) {
                $rootScope.recordDelete("predicate", "edit", predicate.id);
                $rootScope.removeObjectFromChanges("predicate", "delete", predicate.id);
            });
            if ($rootScope.changes["predicate"]["delete"] && $rootScope.changes["predicate"]["delete"].length === 0) {
                delete  $rootScope.changes["predicate"]["delete"];
            }

            if ($rootScope.changes["predicate"]&& $rootScope.changes["predicate"].length === 0) {
                delete  $rootScope.changes["predicate"];
            }

        }

        if($scope.dtToDelete.conformanceStatements != undefined && $scope.dtToDelete.conformanceStatements != null && $scope.dtToDelete.conformanceStatements.length > 0) {
            //clear conforamance statement changes
            angular.forEach($scope.dtToDelete.conformanceStatements, function (confStatement) {
                $rootScope.recordDelete("conformanceStatement", "edit", confStatement.id);
                $rootScope.removeObjectFromChanges("conformanceStatement", "delete", confStatement.id);
            });
            if ($rootScope.changes["conformanceStatement"]["delete"] && $rootScope.changes["conformanceStatement"]["delete"].length === 0) {
                delete  $rootScope.changes["conformanceStatement"]["delete"];
            }

            if ($rootScope.changes["conformanceStatement"]&& $rootScope.changes["conformanceStatement"].length === 0) {
                delete  $rootScope.changes["conformanceStatement"];
            }
        }


        $rootScope.msg().text =  "dtDeleteSuccess";
        $rootScope.msg().type="success";
        $rootScope.msg().show = true;
        $rootScope.manualHandle = true;
        $modalInstance.close($scope.dtToDelete);

    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});


angular.module('igl').controller('DatatypeReferencesCtrl', function ($scope, $modalInstance, dtToDelete) {

    $scope.dtToDelete = dtToDelete;

    $scope.ok = function () {
        $modalInstance.close($scope.dtToDelete);
    };

    $scope.cancel = function () {
        $modalInstance.dismiss('cancel');
    };
});
