/**
 * Created by haffo on 2/13/15.
 */


angular.module('igl')
    .controller('SegmentListCtrl', function ($scope, $rootScope, Restangular, ngTreetableParams,$filter) {
        $scope.loading = false;
        $scope.loadingSelection = false;
        $scope.readonly = false;
        $scope.saved = false;
        $scope.message = false;
        $scope.params = null;
        $scope.tmpSegments =[].concat($rootScope.segments);
        $scope.segmentCopy = null;
        $scope.init = function () {
            $scope.loading = true;
            $scope.params = new ngTreetableParams({
                getNodes: function (parent) {
                    return parent ? parent.fields ? parent.fields: parent.datatype ? $rootScope.datatypesMap[parent.datatype.id].components:parent.children : $rootScope.segment != null ? $rootScope.segment.fields:[];
                },
                getTemplate: function (node) {
                    return 'SegmentEditTree.html';
                }
//                ,
//                options: {
//                    initialState: 'expanded'
//                }
            });


            $scope.$watch(function () {
                return $rootScope.notifySegTreeUpdate;
            }, function (changeId) {
                if(changeId != 0) {
                    $scope.params.refresh();
                }
            });
            $scope.select($rootScope.segments[0]);
            $scope.loading = false;
        };
//
        $scope.select = function (segment) {
            if(segment) {
//            waitingDialog.show('Loading Segment ' + segment.name + "...", {dialogSize: 'sm', progressType: 'info'});
                $rootScope.segment = segment;
                $rootScope.segment["type"] = "segment";
//             $scope.segmentCopy = {};
//            $scope.segmentCopy = angular.copy(segment,$scope.segmentCopy);
                if ($scope.params)
                    $scope.params.refresh();
                $scope.loadingSelection = false;
//            waitingDialog.hide();
            }
        };

        $scope.reset = function () {
//            $scope.loadingSelection = true;
//            $scope.message = "Segment " + $scope.segmentCopy.label + " reset successfully";
//            angular.extend($rootScope.segment, $scope.segmentCopy);
//             $scope.loadingSelection = false;
        };

        $scope.close = function(){
            $rootScope.segment = null;
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
        };

        $scope.hasChildren = function(node){
            return node && node != null && ((node.fields && node.fields.length >0 ) || (node.datatype && $rootScope.datatypesMap[node.datatype.id].components && $rootScope.datatypesMap[node.datatype.id].components.length > 0));
        };

        $scope.validateLabel = function (label, name) {
            if(label && !label.startsWith(name)){
                return false;
            }
            return true;
        };

        $scope.onDatatypeChange = function(node){
//            $rootScope.recordChange(node,'datatype');
//            $rootScope.recordChangeForEdit2('field','edit',node.id,'datatype',node.id);
            $rootScope.recordChangeForEdit2('field','edit',node.id,'datatype',node.datatype);
            $scope.refreshTree();
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

        $scope.goToDatatype = function(datatype){
            $rootScope.datatype = datatype;
            $rootScope.selectProfileTab(3);
            $rootScope.notifyDtTreeUpdate = new Date().getTime();
        };
        
        $scope.deleteTable = function (node) {
        	node.table = null;
        	$rootScope.recordChangeForEdit2('field','edit',node.id,'table',null);
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
			$rootScope.recordChangeForEdit2('field','edit',$scope.selectedNode.id,'table',$scope.selectedNode.table.id);
			$scope.showTableMapModal = false;
		};

		$scope.findDTByComponentId = function(componentId){
//			for(var i=0, len1 = $rootScope.datatypes.length; i < len1; i ++){
//                if($rootScope.datatypes[i].children != undefined){
//				for(var j=0, len2 = $rootScope.datatypes[i].components.length; j<len2;j++ ){
//					if($rootScope.datatypes[i].components[j].id == componentId)
// 						return $rootScope.datatypes[i];
//				}
//				}
//			}
            return $rootScope.parentsMap[componentId] ? $rootScope.parentsMap[componentId].datatype: null;
		};

        $scope.isSub = function(component){
            return  component.type === 'component' && $rootScope.parentsMap[component.id].type === 'component';
        };




        $scope.showPredicateManagerModal = false;
		$scope.showConformanceStatementManagerModal = false;
		$scope.selectedPosition = null;
		$scope.newConstraint = null;
		
		$scope.managePredicate = function(position){
			$scope.selectedPosition = position;
			$scope.showPredicateManagerModal =true;
			$scope.newConstraint = angular.fromJson({
				segment : '',
				field_1 : null,
				component_1 : null,
				subComponent_1 : null,
				field_2 : null,
				component_2 : null,
				subComponent_2 : null,
				verb : null,
				contraintType : null,
				value : null,
				trueUsage : null,
				falseUsage : null
			});
			$scope.newConstraint.segment = $rootScope.segment.name;
		};
		
		$scope.manageConformanceStatement = function(position){
			$scope.selectedPosition = position;
			$scope.showConformanceStatementManagerModal =true;
			$scope.newConstraint = angular.fromJson({
				segment : '',
				field_1 : null,
				component_1 : null,
				subComponent_1 : null,
				field_2 : null,
				component_2 : null,
				subComponent_2 : null,
				verb : null,
				constraintId : null,
				contraintType : null,
				value: null
			});
			$scope.newConstraint.segment = $rootScope.segment.name;
		};
		
		$scope.deletePredicate = function(predicate){
			$rootScope.segment.predicates.splice($rootScope.segment.predicates.indexOf(predicate),1);
			if(!$scope.isNewCP(predicate.id)){
				$rootScope.listToBeDeletedPredicates.push({id: predicate.id});
				$rootScope.recordChange2('predicates',"delete",null,$rootScope.listToBeDeletedPredicates);
			}
		};
		
		$scope.deleteConformanceStatement = function(conformanceStatement){
			$rootScope.segment.conformanceStatements.splice($rootScope.segment.conformanceStatements.indexOf(conformanceStatement),1);
			
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
		
		$scope.updateField_1 = function(){
			$scope.newConstraint.component_1 = null;
			$scope.newConstraint.subComponent_1 = null;
		};
		
		$scope.updateComponent_1 = function(){
			$scope.newConstraint.subComponent_1 = null;
		};
		
		$scope.updateField_2 = function(){
			$scope.newConstraint.component_2 = null;
			$scope.newConstraint.subComponent_2 = null;
		};
		
		$scope.updateComponent_2 = function(){
			$scope.newConstraint.subComponent_2 = null;
		};
		
		
		$scope.deletePredicateByTarget = function(){
			for(var i=0, len1 = $rootScope.segment.predicates.length; i < len1; i ++){
					if($rootScope.segment.predicates[i].constraintTarget.indexOf($scope.selectedPosition + '[') === 0){
						if(!$scope.isNewCP($rootScope.segment.predicates[i].id)){
							$rootScope.listToBeDeletedPredicates.push({id: $rootScope.segment.predicates[i].id});
							$rootScope.recordChange2('predicates',"delete",null,$rootScope.listToBeDeletedPredicates);
						}
						$rootScope.segment.predicates.splice(i, 1);
						return true;
					}
				
			}
			return false;
		};
		
		$scope.updatePredicate = function() {
			$rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
			$scope.deletePredicateByTarget();
			
			var position_1 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var position_2 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);			
			var location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);	
			
			if(position_1 != null){
				if($scope.newConstraint.contraintType === 'valued'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.segment + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType,
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<Presence Path=\"' + location_1 + '\"/>'
						};
					$rootScope.segment.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'a literal value'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.segment + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
						};
					$rootScope.segment.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'one of list values'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.segment + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
						};
					$rootScope.segment.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'formatted value'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.segment + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<Format Path=\"'+ location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
						};
					$rootScope.segment.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}else if($scope.newConstraint.contraintType === 'identical to the another node'){
					var cp = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.segment + '-' + $scope.selectedPosition,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'If the value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
							trueUsage : $scope.newConstraint.trueUsage,
							falseUsage : $scope.newConstraint.falseUsage,
							assertion : '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
						};
					$rootScope.segment.predicates.push(cp);
					$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
					$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
				}
			}
		};


        $scope.show = function(segment){
            return true;
        };
        
        $scope.genPosition = function(segment, field, component, subComponent){
        	var position = null;
        	if(field != null && component == null && subComponent == null){
				position = segment + '-' + field.position;
			}else if(field != null && component != null && subComponent == null){
				position = segment + '-' + field.position + '.' + component.position;
			}else if(field != null && component != null && subComponent != null){
				position = segment + '-' + field.position + '.' + component.position + '.' + subComponent.position;
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
			
			var position_1 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var position_2 = $scope.genPosition($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);			
			var location_1 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_1, $scope.newConstraint.component_1, $scope.newConstraint.subComponent_1);
			var location_2 = $scope.genLocation($scope.newConstraint.segment, $scope.newConstraint.field_2, $scope.newConstraint.component_2, $scope.newConstraint.subComponent_2);	
			
			
			if(position_1 != null){
				if($scope.newConstraint.contraintType === 'valued'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType + '.',
							assertion : '<Presence Path=\"' + location_1 + '\"/>'
						};
					$rootScope.segment.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'a literal value'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' \'' + $scope.newConstraint.value + '\'.',
							assertion : '<PlainText Path=\"' + location_1 + '\" Text=\"' + $scope.newConstraint.value + '\" IgnoreCase="false"/>'
						};
					$rootScope.segment.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'one of list values'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType + ': ' + $scope.newConstraint.value + '.',
							assertion : '<StringList Path=\"' + location_1 + '\" CSV=\"' + $scope.newConstraint.value + '\"/>'
						};
					$rootScope.segment.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'formatted value'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' valid in format: \'' + $scope.newConstraint.value + '\'.',
							assertion : '<Format Path=\"'+ location_1 + '\" Regex=\"' + $rootScope.genRegex($scope.newConstraint.value) + '\"/>'
						};
					$rootScope.segment.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}else if($scope.newConstraint.contraintType === 'identical to the another node'){
					var cs = {
							id : $rootScope.newConformanceStatementFakeId,
							constraintId : $scope.newConstraint.constraintId,
							constraintTarget : $scope.selectedPosition + '[1]',
							description : 'The value of ' + position_1 + ' ' +  $scope.newConstraint.verb + ' identical to the value of ' + position_2 + '.',
							assertion : '<PathValue Path1=\"' + location_1 + '\" Operator="EQ" Path2=\"' + location_2 + '\"/>'
						};
					$rootScope.segment.conformanceStatements.push(cs);
					$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
					$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
				}
			}
		};
		
		$scope.countConformanceStatements = function(position){
			var count = 0;
			for(var i=0, len1 = $rootScope.segment.conformanceStatements.length; i < len1; i ++){
				if($rootScope.segment.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
					count = count + 1;
			}
			
			return count;
		};
		
		$scope.countPredicate = function(position){
			for(var i=0, len1 = $rootScope.segment.predicates.length; i < len1; i ++){
				if($rootScope.segment.predicates[i].constraintTarget.indexOf(position + '[') === 0)
					return 1;
			}
			
			return 0;
		};
     });

angular.module('igl')
    .controller('SegmentRowCtrl', function ($scope,$filter) {
        $scope.formName = "form_"+ new Date().getTime();
    });