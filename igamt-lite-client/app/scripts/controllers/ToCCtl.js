angular.module('igl')
  .controller('ToCCtl', ['$scope', '$rootScope', '$q', 'ToCSvc', 'ContextMenuSvc', function ($scope, $rootScope, $q, ToCSvc, ContextMenuSvc) {
	console.log("ToCCtl");
	var ctl = this;
	var drag = {};
    $scope.collapsed = [];
    $scope.$watch('tocData', function(newValue, oldValue) {
    	if (!oldValue && newValue) {
      	  _.each($scope.tocData, function(head) {
    		  $scope.collapsed[head] = false;
      	  });
    	}
    });
    var accept = function() {
		
	};
	$scope.yesDrop = false;
	$scope.noDrop = false;
	$scope.onStart = function(member) {
		drag = member.drag;
      	console.log("onStart=" + JSON.stringify(member));
      };
  	$scope.onStop = function(member) {
      	console.log("onStop=" + JSON.stringify(member));
      };
	$scope.onEnter = function(member) {
		$scope.yesDrop = _.contains(member.drop, drag);
		$scope.noDrop = !noDrop
      	console.log("onEnter=" + "member=" + JSON.stringify(member) + " noDrop=" + $scope.noDrop);
      };
  	$scope.onLeave = function(member) {
  		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onLeave" + JSON.stringify(member) + " noDrop=" + $scope.noDrop);
      };
	$scope.onDrop = function(member) {
		drag = {};
		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onDrop=" + JSON.stringify(member));
      };
// FIXME gcr: Moving to expression on element.       
//      $scope.toggleToCContents = function (node) {
//          console.log("b collapsed[" + node + "]=" + $scope.collapsed[node]);
//          $scope.collapsed[node] = !$scope.collapsed[node];
//          console.log("a collapse[" + node + "]=" + $scope.collapsed[node]);
//      };
      
    $scope.tocSelection = function (leaf) {
		// TODO gcr: See about refactoring this to eliminate the switch.
		// One could use leaf.reference.type to assemble the $emit string.
    	// Doing so would require maintaining a sync with the ProfileListController.
	    switch (leaf.drag) {
	        case "Metadata":
	        {
	            $scope.selectMetaData();
	            break;
	        }
	        case "Datatypes":
	        {
	        	$scope.$emit('event:openDatatype', leaf.reference);
	            break;
	        }
	        case "Segments":
	        {
	        	$scope.$emit('event:openSegment', leaf.reference);
	            break;
	        }
	        case "Messages":
	        {
	        	$scope.$emit('event:openMessage', leaf.reference);
	            break;
	        }
	        case "ValueSets":
	        {
	        	$scope.$emit('event:openTable', leaf.reference);
	            break;
	        }
	        default:
	        {
	            $scope.subview = "nts.html";
	        }
	    }
	    return $scope.subview;
    };      

	$scope.closedCtxMenu = function (node, $index) {
          var item = ContextMenuSvc.get();
          switch (item) {
              case "Add":
               	  console.log("Add==>");
//				if (node === "Messages") {
//					var hl7VersionsInstance;
//					hl7VersionsInstance = $modal.open({
//						templateUrl : 'hl7MessagesDlg.html',
//						controller : 'HL7VersionsInstanceDlgCtrl',
//						resolve : {
//							hl7Versions : function() {
//								return $scope.listHL7Versions();
//							}
//						}
//					});
//					
//					hl7VersionsInstance.result.then(function(result) {
//						console.log(result);
//						$scope.updateProfile(result);
//					});
//				} else {
//					alert("Was not Messages. Was:" + node);
//				}
                  break;
              case "Delete":
            	  console.log("Delete==>");
              	break;
              default:
                  console.log("Context menu defaulted with " + item + " Should be Add or Delete.");
          }
      };

      $scope.closedCtxSubMenu = function (leaf, $index) {
          var ctxMenuSelection = ContextMenuSvc.get();
          switch (ctxMenuSelection) {
              case "Add":
              {
               	  console.log("Add==> node=" + leaf);
                 // not to be implemented at this time.
              }
              case "Clone":
              {
               	  console.log("Clone==> node=" + leaf);
               	  CloneDeleteMessageSvc.cloneMessage(leaf, $index);
// FIXME gcr: Moving to CloneDeleteMessageSvc.          	  
//               	var newNode = (JSON.parse(JSON.stringify(node)));
//                  newNode.id = null;
//
//                  // Nodes must have unique names so we timestamp when we duplicate.
//                  if (newNode.type === 'message') {
//                      newNode.messageType = newNode.messageType + "-" + $rootScope.profile.metaData.ext + "-" + timeStamp();
//                  }
//                  for (var i in $rootScope.profile.messages.children) {
//                      console.log($rootScope.profile.messages.children[i].messageType);
//                  }
//                  $rootScope.profile.messages.children.splice(2, 0, newNode);
//                  for (var i in $rootScope.profile.messages.children) {
//                      console.log($rootScope.profile.messages.children[i].messageType);
//                  }
                  break;
              }
              case "Delete":
            	  console.log("Delete==> node=" + leaf);
              	CloneDeleteMessageSvc.deleteMessage(leaf, $index);
              	break;
              default:
            	  console.log("Context menu defaulted with " + ctxMenuSelection + " Should be Add, clone, or Delete.");
          }
      };

 }])