angular.module('igl')
  .controller('ToCCtl', ['$scope', '$rootScope', '$q', 'ToCSvc', 'ContextMenuSvc', 'DnDSvc', 'CloneDeleteMessageSvc', function ($scope, $rootScope, $q, ToCSvc, ContextMenuSvc, DnDSvc, CloneDeleteMessageSvc) {
	console.log("ToCCtl");
	var ctl = this;
    $scope.collapsed = [];
    $scope.$watch('tocData', function(newValue, oldValue) {
    	if (!oldValue && newValue) {
      	  _.each($scope.tocData, function(head) {
    		  $scope.collapsed[head] = false;
      	  });
    	}
    });
    
// FIXME gcr: dead code.    
//    var accept = function() {
//		
//	};
	$scope.yesDrop = false;
	$scope.noDrop = false;
	$scope.onBranchStart = function(tocEntry) {
		DnDSvc.drag = tocEntry.drag;
      	console.log("onBranchStart=" + tocEntry.label);
      };
  	$scope.onBranchStop = function(tocEntry) {
      	console.log("onBranchStop=" + tocEntry.label);
      };
	$scope.onBranchEnter = function(tocEntry) {
		$scope.yesDrop = _.contains(tocEntry.drop, DnDSvc.drag);
		$scope.noDrop = !$scope.noDrop
      	console.log("onBranchEnter=" + "member=" + tocEntry.label + " drag=" + DnDSvc.drag  + " yesDrop=" + $scope.yesDrop + " noDrop=" + $scope.noDrop);
      };
  	$scope.onBranchLeave = function(tocEntry) {
  		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onBranchLeave=" + tocEntry.label + " noDrop=" + $scope.noDrop);
      };
	$scope.onBranchDrop = function(tocEntry) {
		DnDSvc.drag = {};
		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onBranchDrop=" + tocEntry.label);
      };

	$scope.onLeafStart = function(tocEntry) {
		DnDSvc.drag = tocEntry.drag;
      	console.log("onLeafStart=" + tocEntry.label + " drag=" + DnDSvc.drag);
      };
  	$scope.onLeafStop = function(tocEntry) {
      	console.log("onLeafStop=" + tocEntry.label);
      };
	$scope.onLeafEnter = function(tocEntry) {
		$scope.yesDrop = _.contains(tocEntry.drop, drag);
		$scope.noDrop = !noDrop
      	console.log("onLeafEnter=" + "member=" + tocEntry.label + " noDrop=" + $scope.noDrop);
      };
  	$scope.onLeafLeave = function(tocEntry) {
  		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onLeafLeave=" + tocEntry.label + " noDrop=" + $scope.noDrop);
      };
	$scope.onLeafDrop = function(tocEntry) {
		DnDSvc.drag = undefined;
		$scope.yesDrop = false;
		$scope.noDrop = false;
      	console.log("onLeafDrop=" + tocEntry.label);
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
	        case "ConformanceProfile":
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
               	CloneDeleteMessageSvc.addEntry(leaf);
              }
              case "Clone":
              {
               	  console.log("Clone==> node=" + leaf);
               	  CloneDeleteMessageSvc.cloneMessage($rootScope.profile, $rootScope.tocData, leaf);
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
              	CloneDeleteMessageSvc.deleteMessage($rootScope.profile, $rootScope.tocData, leaf);
              	break;
              default:
            	  console.log("Context menu defaulted with " + ctxMenuSelection + " Should be Add, clone, or Delete.");
          }
      };

 }])