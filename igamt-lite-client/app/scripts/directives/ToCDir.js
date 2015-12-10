angular.module('igl')
 .directive('branches', function () {
	  console.log("branches");

	  var template = "<div ng-class\"calcOffset(branch.level)\"><branch ng-repeat='branch in branches track by $index' branch='branch'></branch></div>";

  return {
      restrict: "E",
      replace: true,
      scope: {
    	  branches: '='
      },
      template: template,
  }
})
.directive("branch", function ($compile) {
	  console.log("branch");
	  
	  var branchesTemplate = "<branches branches='branch.children'></branches>";
	  var branchTemplate = "<div ng-class=\"{'yesyes':yesDrop, 'nono':noDrop}\"" + 
		" drop-container accepts='{{branch.drop}}'" + 
		" on-drag-enter='onBranchEnter(branch)' on-drag-leave='onBranchLeave(branch)' on-drop='onBranchDrop(branch)'>" +
		"<a class='point'" +
		"ng-click='collapsed[branch.label] = !collapsed[branch.label]'>" +
		"<span class='fa' ng-class=\"{'fa-caret-right': collapsed[branch.label],'fa-caret-down': !collapsed[branch.label]}\">" +
		"<span ng-class=\" {'top-level': branch.level === 1} \">{{branch.label}}</span></span>" + 
		"</a>" +
		"</div>";
	  var branchMessageTemplate = "<div ng-class=\"{'yesyes':yesDrop, 'nono':noDrop}\"" + 
		" context-menu context-menu-close='closedCtxMenu(branch)' data-target='messageHeadContextDiv.html'" + 
		" drop-container accepts='{{branch.drop}}'" + 
		" on-drag-enter='onBranchEnter(branch)' on-drag-leave='onBranchLeave(branch)' on-drop='onBranchDrop(branch)'>" +
		"<a class='point'" +
		"ng-click='collapsed[branch.label] = !collapsed[branch.label]'>" +
		"<span class='fa' ng-class=\"{'fa-caret-right': collapsed[branch.label],'fa-caret-down': !collapsed[branch.label]}\">" +
		"<b>{{branch.label}}</b></span>" + 
		"</a>" +
		"</div>";

	  var leafTemplate = "<leaf leaf='branch'></leaf>";
		  
	  var linker = function(scope, element, attrs) {
		  if (angular.isArray(scope.branch.children)) {
			  console.log("branch=" + scope.branch.children.length);
            // We must add the branchTemplate before we add the branchesTemplate.
			  if (scope.branch.id === "3") {
		          element.append(branchMessageTemplate).show();		  
			  } else {
				  element.append(branchTemplate).show();
			  }
              $compile(element.contents())(scope);
              
              element.append(branchesTemplate);
              $compile(element.contents())(scope); 
		  } else {
			  element.append(leafTemplate).show();
              $compile(element.contents())(scope);
		  }
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

	  var leafMessage = "<div" +
	  						" context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='messageContextDiv.html'" + 
	  						" drag-container mime-type='{{leaf.drag}}'" + 
	  						" on-drag-start='onLeafStart(leaf)' on-drag-stop='onLeafStop(leaf)' on-drag-enter='onLeafEnter(leaf)' on-drop='onLeafDrop(leaf)'" + 
	  						" ng-click='tocSelection(leaf)'>" +
						    "<a class='point txt'>" +
							"<span class='fa' ng-class=\" {'': !collapsed[leaf.label], 'toc-selected' : leaf.selected} \">{{leaf.label}}</span>" +
	      				    "</a>" +
	  					"</div>";
 
	  var leafDefault = "<div" + 
	  						" drag-container mime-type='{{leaf.drag}}'" + 
	  						" on-drag-start='onLeafStart(leaf)' on-drag-stop='onLeafStop(leaf)' on-drag-enter='onLeafEnter(leaf)' on-drop='onLeafDrop(leaf)'" + 
	  						" ng-click='tocSelection(leaf)'>" +
						    "<a class='point txt'>" +
							"<span class='fa' ng-class=\" {'': !collapsed[leaf.label], 'toc-selected' : leaf.selected} \">{{leaf.label}}</span>" +
	      				    "</a>" +
	  					"</div>";
	  
	  var linker = function(scope, element, attrs) {
	        if (scope.leaf.drag === "3") {
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

