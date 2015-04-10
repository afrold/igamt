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
                    return parent ? parent.fields ? parent.fields: parent.datatype ? parent.datatype.components:parent.children : $rootScope.segment != null ? [$rootScope.segment]:[];
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
//            waitingDialog.show('Loading Segment ' + segment.name + "...", {dialogSize: 'sm', progressType: 'info'});
            $rootScope.segment = segment;
            $rootScope.segment["type"] = "segment";
//             $scope.segmentCopy = {};
//            $scope.segmentCopy = angular.copy(segment,$scope.segmentCopy);
            if ($scope.params)
                $scope.params.refresh();
            $scope.loadingSelection = false;
//            waitingDialog.hide();
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
            return node && node != null && ((node.fields && node.fields.length >0 ) || (node.datatype && node.datatype.components && node.datatype.components.length > 0));
        };

        $scope.validateLabel = function (label, name) {
            if(label && !label.startsWith(name)){
                return false;
            }
            return true;
        };

        $scope.onDatatypeChange = function(node){
            $scope.refreshTree();
            node.datatypeLabel = null;
            $rootScope.recordChange(node,'datatype');
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
        	$rootScope.recordChangeForEdit2('field','edit',node.id,'table',null);
        }
        
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
	        
		}
		
		$scope.mappingTable = function(){
			$scope.selectedNode.table = $scope.selectedTable;
			$rootScope.recordChangeForEdit2('field','edit',$scope.selectedNode.id,'table',$scope.selectedNode.table.id);
			$scope.showTableMapModal = false;
		}
		
		$scope.findDTByComponentId = function(componentId){
			for(var i=0, len1 = $rootScope.datatypes.length; i < len1; i ++){
				for(var j=0, len2 = $rootScope.datatypes[i].components.length; j<len2;j++ ){
					if($rootScope.datatypes[i].components[j].id == componentId)
						return $rootScope.datatypes[i];
				}
			}
			return null;
		}
		
		$scope.findDTByLabel = function(label){
			for(var i=0, len1 = $rootScope.datatypes.length; i < len1; i ++){
				if($rootScope.datatypes[i].label == label)
					return $rootScope.datatypes[i];
			}
			return null;
		}
		
		$scope.showPredicateManagerModal = false;
		$scope.showConformanceStatementManagerModal = false;
		$scope.selectedPosition = null;
		$scope.selectedDTofField_1 = null;
		$scope.selectedDTofComponent_1 = null;
		$scope.selectedDTofSubComponent_1 = null;
		$scope.newConstraint = null;
		
		$scope.managePredicate = function(position){
			$scope.selectedPosition = position;
			$scope.showPredicateManagerModal = !$scope.showPredicateManagerModal;	
			$scope.newConstraint = angular.fromJson({
				segment : '',
				field_1 : null,
				component_1 : null,
				subComponent_1 : null,
				verb : null,
				contraintType : null,
				trueUsage : null,
				falseUsage : null
			});
			$scope.newConstraint.segment = $rootScope.segment.name;
			$scope.selectedDTofField_1 = null;
			$scope.selectedDTofComponent_1 = null;
			$scope.selectedDTofSubComponent_1 = null;
		}
		
		$scope.manageConformanceStatement = function(position){
			$scope.selectedPosition = position;
			$scope.showConformanceStatementManagerModal = !$scope.showConformanceStatementManagerModal;	
			$scope.newConstraint = angular.fromJson({
				segment : '',
				field_1 : null,
				component_1 : null,
				subComponent_1 : null,
				verb : null,
				constraintId : null,
				contraintType : null,
				trueUsage : null,
				falseUsage : null
			});
			$scope.newConstraint.segment = $rootScope.segment.name;
			$scope.selectedDTofField_1 = null;
			$scope.selectedDTofComponent_1 = null;
			$scope.selectedDTofSubComponent_1 = null;
		}
		
		$scope.deletePredicate = function(predicate){
			$rootScope.segment.predicates.splice($rootScope.segment.predicates.indexOf(predicate),1);
			if(!$scope.isNewCP(predicate.id)){
				$rootScope.listToBeDeletedPredicates.push({id: predicate.id});
				$rootScope.recordChange2('predicates',"delete",null,$rootScope.listToBeDeletedPredicates);
			}
		}
		
		$scope.deleteConformanceStatement = function(conformanceStatement){
			$rootScope.segment.conformanceStatements.splice($rootScope.segment.conformanceStatements.indexOf(conformanceStatement),1);
			
			if(!$scope.isNewCS(conformanceStatement.id)){
				$rootScope.listToBeDeletedConformanceStatements.push({id: conformanceStatement.id});
				$rootScope.recordChange2('conformanceStatements',"delete",null,$rootScope.listToBeDeletedConformanceStatements);
			}
		}
		
		
		$scope.isNewCS = function(id){
			for(var i=0, len = $rootScope.listToBeAddedConformanceStatements.length; i < len; i ++){
				if($rootScope.listToBeAddedConformanceStatements[i].constraint.id === id){
					$rootScope.listToBeAddedConformanceStatements.splice(i, 1);
					return true;
				}
			}
			return false;
		}
		
		$scope.isNewCP = function(id){
			for(var i=0, len = $rootScope.listToBeAddedPredicates.length; i < len; i ++){
				if($rootScope.listToBeAddedPredicates[i].constraint.id === id){
					$rootScope.listToBeAddedPredicates.splice(i, 1);
					return true;
				}
			}
			return false;
		}
		
		$scope.updateField_1 = function(){
			$scope.selectedDTofField_1 = $scope.findDTByLabel($scope.newConstraint.field_1.datatypeLabel);
		}
		
		$scope.updateComponent_1 = function(){
			$scope.selectedDTofComponent_1 = $scope.findDTByLabel($scope.newConstraint.component_1.datatypeLabel);
		}
		
		$scope.updateSubComponent_1 = function(){
			$scope.selectedDTofSubComponent_1 = $scope.findDTByLabel($scope.newConstraint.subComponent_1.datatypeLabel);
		}
		
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
		}
		
		$scope.updatePredicate = function() {
			$rootScope.newPredicateFakeId = $rootScope.newPredicateFakeId - 1;
			$scope.deletePredicateByTarget()
			
			if($scope.newConstraint.contraintType === 'valued'){
				var position_1 = '';
			
				if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 == null && $scope.newConstraint.subComponent_1 == null){
					position_1 = $scope.newConstraint.segment + '-' + $scope.newConstraint.field_1.position;
				}else if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 != null && $scope.newConstraint.subComponent_1 == null){
					position_1 = $scope.newConstraint.segment + '-' + $scope.newConstraint.field_1.position + '.' + $scope.newConstraint.component_1.position;
				}else if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 != null && $scope.newConstraint.subComponent_1 != null){
					position_1 = $scope.newConstraint.segment + '-' + $scope.newConstraint.field_1.position + '.' + $scope.newConstraint.component_1.position + '.' + $scope.newConstraint.subComponent_1.position;
				}
				
				var cp = {
						id : $rootScope.newPredicateFakeId,
						constraintId : $rootScope.segment.name+ '.' + $scope.selectedPosition,
						constraintTarget : $scope.selectedPosition + '[1]',
						description : 'If ' + position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType,
						assertion : null,
						trueUsage : $scope.newConstraint.trueUsage,
						falseUsage : $scope.newConstraint.falseUsage
					}
				$rootScope.segment.predicates.push(cp);
				$rootScope.listToBeAddedPredicates.push({segmentId: $rootScope.segment.id , constraint: cp});
			}
			
			$rootScope.recordChange2('predicates',"add",null,$rootScope.listToBeAddedPredicates);
		};


        $scope.show = function(segment){
            return true;
        };
		
		$scope.addConformanceStatement = function() {
			$rootScope.newConformanceStatementFakeId = $rootScope.newConformanceStatementFakeId - 1;
 			if($scope.newConstraint.contraintType === 'valued'){
				var position_1 = '';
			
				if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 == null && $scope.newConstraint.subComponent_1 == null){
					position_1 = $scope.newConstraint.segment + '.' + $scope.newConstraint.field_1.position;
				}else if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 != null && $scope.newConstraint.subComponent_1 == null){
					position_1 = $scope.newConstraint.segment + '.' + $scope.newConstraint.field_1.position + '.' + $scope.newConstraint.component_1.position;
				}else if($scope.newConstraint.field_1 != null && $scope.newConstraint.component_1 != null && $scope.newConstraint.subComponent_1 != null){
					position_1 = $scope.newConstraint.segment + '.' + $scope.newConstraint.field_1.position + '.' + $scope.newConstraint.component_1.position + '.' + $scope.newConstraint.subComponent_1.position;
				}
				
				var cs = {
						id : $rootScope.newConformanceStatementFakeId,
						constraintId : $scope.newConstraint.constraintId,
						constraintTarget : $scope.selectedPosition + '[1]',
						description : position_1 + ' ' +  $scope.newConstraint.verb + ' ' +  $scope.newConstraint.contraintType,
						assertion : null
					};
				$rootScope.segment.conformanceStatements.push(cs);
				$rootScope.listToBeAddedConformanceStatements.push({segmentId: $rootScope.segment.id , constraint: cs});
			}
 			
 			$rootScope.recordChange2('conformanceStatements',"add",null,$rootScope.listToBeAddedConformanceStatements);
		}
		
		$scope.countConformanceStatements = function(position){
			var count = 0;
			for(var i=0, len1 = $rootScope.segment.conformanceStatements.length; i < len1; i ++){
				if($rootScope.segment.conformanceStatements[i].constraintTarget.indexOf(position + '[') === 0)
					count = count + 1;
			}
			
			return count;
		}
		
		$scope.countPredicate = function(position){
			for(var i=0, len1 = $rootScope.segment.predicates.length; i < len1; i ++){
				if($rootScope.segment.predicates[i].constraintTarget.indexOf(position + '[') === 0)
					return 1;
			}
			
			return 0;
		}
     });

angular.module('igl')
    .controller('SegmentRowCtrl', function ($scope,$filter) {
        $scope.formName = "form_"+ new Date().getTime();
    });