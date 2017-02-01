angular.module('igl').controller('DocumentationController', function($scope, $rootScope, Restangular, $filter, $http, $modal, $timeout,DecisionService) {


	$scope.editMode=false;
	$scope.newOne=false;
	$scope.activeId=null;
	
//	$scope.init=function(){
//		console.log("lddsdsdsddssd");
//		$scope.text="fwfwfw";
//	$rootScope.decisions=[{title: "decision One" , content:"blaadefefe"},{title: "decision One" , content:"blaadefefe"},{title: "decision One" , content:"blaadefefe"}];
//	}
	$scope.init=function(){		
		DecisionService.findAll().then(function(result){	
			$rootScope.decisionsMap={};
			$rootScope.decisions=result;
			
			angular.forEach(result, function(decision){
				
				$rootScope.decisionsMap[decision.id]=decision;
			});
			$rootScope.$emit("event:initEditArea");
		});

	};
	
	
	$scope.deleteDecision=function(decision){
		

		
		DecisionService.delete(decision).then(function(){
			$scope.decision=null;
			for(i=0; i<$rootScope.decisions.length;i++){
				if(decision.id==$rootScope.decisions[i].id){
					 $rootScope.decisions.splice(i, 1);
				}
			}

             
            $rootScope.msg().text = "DecisionDeleteSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function(error) {
            $scope.toEditIGDocumentId = null;
            $rootScope.msg().text = "DecsionDeleteFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        }

		);
	}
	
	
	$scope.edit=function(){
		
		
		$scope.editMode=true;
		
	}
	
	
	$scope.subview="editDecision.html";

	$scope.addDecision= function(){
		
		var newId=new ObjectId().toString();
		$scope.decisionToAdd={
			
				id: newId,
				title:"New Decision",
				type:"decision"
				
		}
		$scope.editMode=true;
		$scope.activeId=newId;
		$scope.newOne=true;
		$rootScope.decisions.push($scope.decisionToAdd);
		$rootScope.decisionsMap[$scope.decisionToAdd.id]=$scope.decisionToAdd;
		//$scope.editDecision($scope.decisionToAdd);
		$scope.decision=angular.copy($scope.decisionToAdd);
		$rootScope.$emit("event:initEditArea");
	    
	    
	}
	$scope.editDecision = function(decision){
		$scope.activeId=decision.id;
		$rootScope.$emit("event:initEditArea");
		console.log(decision);
		$scope.decision=angular.copy(decision);
		$scope.editMode=false;
		$scope.newOne=false;
		

	}
	
	$scope.saveDecision=function(decision){
		DecisionService.save(decision).then(function(saved){
			
			console.log("befor");
			$scope.decision=saved.data;

			console.log($rootScope.decisionsMap[decision.id]);
			console.log($rootScope.decisions);
			$rootScope.decisionsMap[decision.id]= saved.data;
			
			console.log("After")
			console.log($rootScope.decisionsMap[decision.id]);
			console.log($rootScope.decisions);
			console.log(saved);
			
			angular.forEach($rootScope.decisions, function(d){
				if(d.id==$scope.decision.id){
				d.title=$rootScope.decisionsMap[saved.data.id].title;
				d.content=$rootScope.decisionsMap[saved.data.id].content;
				d.dateUpdated=$rootScope.decisionsMap[saved.data.id].dateUpdated;
				d.username=$rootScope.decisionsMap[saved.data.id].username;
				}
			});
			
            if($scope.editForm) {
                $scope.editForm.$setPristine();
                $scope.editForm.$dirty = false;
            }
			$scope.editMode=false;
			$scope.newOne=false;
		});
	}
	$scope.resetDecision=function(d){
		console.log(d);
		console.log($rootScope.decisionsMap);
		$scope.decision= angular.copy($rootScope.decisionsMap[d.id]);
        if($scope.editForm) {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
        }
		

	}
	

});

angular.module('igl').controller('addDecisionCtrl', function($scope, $rootScope, $http, $modalInstance, decisionToAdd,DecisionService) {

	$scope.decisiontoAdd=decisionToAdd;
    $scope.ok = function() {
    	
    	DecisionService.save(decision).then(function(response){
			$scope.decision=response;
			$rootScope.decision.push(response);
			
		});
        $modalInstance.dismiss('cancel');
        
    };
    $scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
});