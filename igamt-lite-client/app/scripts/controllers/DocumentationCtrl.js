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
			$rootScope.decision=null;
			for(i=0; i<$rootScope.decisions.length;i++){
				if(decision.id==$rootScope.decisions[i].id){
					 $rootScope.decisions.splice(i, 1);
				}
			}

			$rootScope.clearChanges();
            $rootScope.msg().text = "DecisionDeleteSuccess";
            $rootScope.msg().type = "success";
            $rootScope.msg().show = true;
        }, function(error) {
            $rootScope.msg().text = "DecsionDeleteFaild";
            $rootScope.msg().type = "danger";
            $rootScope.msg().show = true;
        }

		);
	}
	
	
	$scope.edit=function(){
		
		
		$scope.editMode=true;
		
	}
	
	

	$scope.processAddDecision= function(){
		
		var newId=new ObjectId().toString();
		$rootScope.decisionToAdd={
			
				id: newId,
				title:"New Decision",
				type:"decision"
				
		}
		$scope.editMode=true;
		$scope.activeId=newId;
		$scope.newOne=true;
		$rootScope.decisions.push($rootScope.decisionToAdd);
		$rootScope.decisionsMap[$rootScope.decisionToAdd.id]=$rootScope.decisionToAdd;
		//$scope.editDecision($rootScope.decisionToAdd);
		$rootScope.decision=angular.copy($rootScope.decisionToAdd);
		$rootScope.currentData=$rootScope.decision;
		//$rootScope.$emit("event:initEditArea");
	    
	    
	}
	$scope.processEditDecision = function(decision){
		$scope.activeId=decision.id;
		//$rootScope.$emit("event:initEditArea");
		console.log(decision);
		$rootScope.decision=angular.copy(decision);
		$rootScope.currentData=$rootScope.decision;

		$scope.editMode=false;
		$scope.newOne=false;
		

	}
	
	
	
	
	$scope.editDecision=function(decision){
		if ($rootScope.hasChanges()) {

	        $rootScope.openConfirmLeaveDlg().result.then(function() {
	        	$scope.processEditDecision(decision);
	        });
	    } else {

	    	$scope.processEditDecision(decision);

	    }
	};
	
	
	$scope.addDecision=function(decision){
		if ($rootScope.hasChanges()) {

	        $rootScope.openConfirmLeaveDlg().result.then(function() {
	        $scope.processAddDecision(decision);
	        });
	    } else {

	    	$scope.processAddDecision(decision);

	    }
	};
	
	
	$scope.saveDecision=function(decision){
		DecisionService.save(decision).then(function(saved){
			
			console.log("befor");
			$rootScope.decision=saved.data;

			console.log($rootScope.decisionsMap[decision.id]);
			console.log($rootScope.decisions);
			$rootScope.decisionsMap[decision.id]= saved.data;
			
			console.log("After")
			console.log($rootScope.decisionsMap[decision.id]);
			console.log($rootScope.decisions);
			console.log(saved);
			
			angular.forEach($rootScope.decisions, function(d){
				if(d.id==$rootScope.decision.id){
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
			$rootScope.clearChanges();
			 $rootScope.msg().text = "DecisionSaveSuccess";
	            $rootScope.msg().type = "success";
	            $rootScope.msg().show = true;
	        }, function(error) {
	            $rootScope.msg().text = "DecsionSaveFaild";
	            $rootScope.msg().type = "danger";
	            $rootScope.msg().show = true;
	        }
		);
	}
	$scope.resetDecision=function(d){
		console.log(d);
		console.log($rootScope.decisionsMap);
		$rootScope.decision= angular.copy($rootScope.decisionsMap[d.id]);
        if($scope.editForm) {
            $scope.editForm.$setPristine();
            $scope.editForm.$dirty = false;
        }
		

	};
	
	
	
 $scope.confirmDeleteDecision = function(decision) {
 var modalInstance = $modal.open({
     templateUrl: 'confirmDecisionDeleteCtrl.html',
     controller: 'confirmDecisionDeleteCtrl',
      resolve: {
    	  decisionToDelete: function() {
              return decision;
         }
    }
 });
 modalInstance.result.then(function(decisiontoDelete) {
	
	 $scope.deleteDecision(decision);
});
 
 
 }
 });

angular.module('igl').controller('confirmDecisionDeleteCtrl', function($scope, $rootScope, $http, $modalInstance, decisionToDelete,DecisionService) {

	$scope.decisiontoDelete=decisionToDelete;
    $scope.ok = function() {
    	
    	$modalInstance.close($scope.decisiontoDelete);
        
    };
    $scope.cancel = function () {
		$modalInstance.dismiss('cancel');
	};
});