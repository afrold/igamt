angular.module('igl')
 .directive('trunk', function () {
	  console.log("trunk");
// gcr: Parked temporarily.
// template: "<ul><branch ng-repeat='branch in trunk' branch='branch'></branch></ul>",

	  var template = "<div><branch ng-repeat='branch in trunk' branch='branch'></branch></div>";

  return {
      restrict: "E",
      replace: true,
      scope: {
          trunk: '='
      },
      template: template,
  }
})
.directive("branch", function ($compile) {
	  console.log("branch");
	  
	  var trunkTemplate = "<trunk trunk='branch.children'></trunk>";
//	  var branchTemplate ="<div ng-class=\"{'yesyes':yesDrop, 'nono':noDrop}\"><li drop-container accepts='{{branch.drop}}' on-drag-enter='onEnter(branch)' on-drag-leave='onLeave(branch)' on-drop='onDrop(branch)'>{{branch.label}}</li></div>";
//	  var leafTemplate = "<li drag-container accepts='{{branch.drop}}' on-drag-start='onStart(branch)' on-drag-stop='onStop(branch)' on-drag-enter='onEnter(branch)' on-drop='onDrop(branch)' mime-type='{{branch.drag}}'>{{branch.label}}</li>";
	  var branchTemplate = "<div class='txt' ng-class=\"{'yesyes':yesDrop, 'nono':noDrop}\"" + 
	  						"context-menu context-menu-close='closedCtxMenu(branch.label, $index)' data-target='headContextDiv.html'" + 
								"drop-container accepts='{{branch.drop}}'" + 
								"on-drag-enter='onEnter(branch)' on-drag-leave='onLeave(branch)' on-drop='onDrop(branch)'>" +
								"<a class='point' ng-show='branch.children && branch.children.length > 0'" +
								"ng-click='collapsed[branch.label] = !collapsed[branch.label]'>" +
								"<i class='fa' ng-class=\"{'fa-caret-right': collapsed[branch.label],'fa-caret-down': !collapsed[branch.label]}\"></i>" +
								"<b>{{branch.label}}</b>" + 
								"</a>" +
								"<div collapse='collapsed[branch.label]' ng-show='!collapsed[branch.label]' class='panel-body'><leaf ng-repeat='leaf in branch.children' leaf='leaf'></leaf></div>" +
								"</div>";
//	  var leafMetadata = "<li drag-container accepts='{{branch.drop}}' on-drag-start='onStart(branch)' on-drag-stop='onStop(branch)' on-drag-enter='onEnter(branch)' on-drop='onDrop(branch)' mime-type='{{branch.drag}}'>{{branch.label}}</li>";
//
//	  var leafMessage = "<div class='sub-txt' context-menu context-menu-close='closedCtxSubMenu(branch, $index)'>" +
//						    "<a class='point txt'>" +
//							"<span class='fa' ng-class=\"{'': !collapsed[branch.label]}\">{{branch.label}}</span>" +
//	      				    "</a>" +
//	  					"</div>";
// 
//	  var leafDefault = "<div class='sub-txt'>" +
//						    "<a class='point txt'>" +
//							"<span class='fa' ng-class=\"{'': !collapsed[branch.label]}\">{{branch.label}}</span>" +
//	      				    "</a>" +
//	  					"</div>";
//	  
	  var linker = function(scope, element, attrs) {
		  if (angular.isArray(scope.branch.children)) {
              // We must add the branchTemplate before we add the trunkTemplate.
              element.append(branchTemplate).show();
              $compile(element.contents())(scope);
              
              element.append(trunkTemplate);
              $compile(element.contents())(scope); 
		  }
//		  } else {
//	        if (scope.branch.drag === "Messages") {
//		        element.html(leafMessage).show();	        	
//	        } else {
//		        element.html(leafDefault).show();
//	        }
//	        $compile(element.contents())(scope);
//		  }
	    }
	  
  return {
      restrict: "E",
      replace: true,
      controller: "ToCCtl",
      scope: {
          branch: '='
      	},
      link: linker
      }
})
.directive("leaf", function($compile) {
	  var leafMetadata = "<li drag-container on-drag-start='onStart(leaf)' on-drag-stop='onStop(leaf)' on-drag-enter='onEnter(leaf)' on-drop='onDrop(leaf)' mime-type='{{leaf.drag}}'>{{leaf.label}}</li>";

	  var leafMessage = "<div class='sub-txt'" +
	  						"context-menu context-menu-close='closedCtxSubMenu(leaf, $index)' data-target='messageContextDiv.html'" + 
	  						"drag-container mime-type='{{leaf.drag}}'" + 
	  						"on-drag-start='onStart(branch)' on-drag-stop='onStop(branch)' on-drag-enter='onEnter(branch)' on-drop='onDrop(branch)'>" +
						    "<a class='point txt'>" +
							"<span class='fa' ng-class=\"{'': !collapsed[leaf.label]}\">{{leaf.label}}</span>" +
	      				    "</a>" +
	  					"</div>";
 
	  var leafDefault = "<div class='sub-txt'" + 
	  						"drag-container mime-type='{{leaf.drag}}'" + 
	  						"on-drag-start='onStart(branch)' on-drag-stop='onStop(branch)' on-drag-enter='onEnter(branch)' on-drop='onDrop(branch)'>" +
						    "<a class='point txt'>" +
							"<span class='fa' ng-class=\"{'': !collapsed[leaf.label]}\">{{leaf.label}}</span>" +
	      				    "</a>" +
	  					"</div>";
	  
	  var linker = function(scope, element, attrs) {
	        if (scope.leaf.drag === "Messages") {
		        element.html(leafMessage).show();	        	
	        } else {
		        element.html(leafDefault).show();
	        }
	        $compile(element.contents())(scope);
	    }
	  
	  return {
	      restrict: "E",
	      replace: true,
	      controller: "ToCCtl",
	      scope: {
	          leaf: '='
	      	},
	      link: linker
	      }
})

