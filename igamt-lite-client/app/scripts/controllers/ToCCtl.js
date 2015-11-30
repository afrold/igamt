angular.module('igl')
  .controller('ToCCtl', ['$scope', '$q', 'ToCSvc', 'ContextMenuSvc', function ($scope, $q, ToCSvc, ContextMenuSvc) {
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
      
      $scope.toggleToCContents = function (node) {
          console.log("b collapsed[" + node + "]=" + $scope.collapsed[node]);
          $scope.collapsed[node] = !$scope.collapsed[node];
          console.log("a collapse[" + node + "]=" + $scope.collapsed[node]);
      };
      
      $scope.closedCtxMenu = function (node, $index) {
          var item = ContextMenuSvc.get();
          switch (item) {
              case "Add":
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
              	ProfileAccessSvc.
              	break;
              default:
                  console.log("Context menu defaulted with " + item + " Should be Add or Delete.");
          }
      };

      $scope.closedCtxSubMenu = function (node, $index) {
          var item = ContextMenuSvc.get();
          switch (item) {
              case "Add":
              {
                  // not to be implemented at this time.
              }
              case "Clone":
              {
                  var newNode = (JSON.parse(JSON.stringify(node)));
                  newNode.id = null;

                  // Nodes must have unique names so we timestamp when we duplicate.
                  if (newNode.type === 'message') {
                      newNode.messageType = newNode.messageType + "-" + $rootScope.profile.metaData.ext + "-" + timeStamp();
                  }
                  for (var i in $rootScope.profile.messages.children) {
                      console.log($rootScope.profile.messages.children[i].messageType);
                  }
                  $rootScope.profile.messages.children.splice(2, 0, newNode);
                  for (var i in $rootScope.profile.messages.children) {
                      console.log($rootScope.profile.messages.children[i].messageType);
                  }
                  break;
              }
              case "Delete":
              	DeleteMessageSvc.deleteMessage($rootScope.profile, node);
              	break;
              default:
                  console.log("Context menu defaulted with " + item + " Should be Add or Delete.");
          }
      };

 }])