angular.module('igl')
 .directive('trunk', function () {
	  console.log("trunk");

	  var template = "<ul class='trunk'><branch ng-repeat='branch in trunk track by $index' branch='branch'></branch></ul>";

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

	  var trunkTemplate = "<trunk trunk='branch.children'></trunk>";
	  var branchTemplate = "<li class='branch'>" +
	  	"<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected } \">" +
	  	"{{branch.label}}" +
	  	"</label><input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>" +
	  	"<trunk trunk='branch.children'></trunk>" +
		"</li>";
	  var branchMessageTemplate = "<li class='branch' context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='messageHeadContextDiv.html'>" +
	  	"<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected } \">" +
	  	"{{branch.label}}" +
	  	"</label><input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>" +
		"<trunk trunk='branch.children'></trunk>" +
		"</li>";
	  var leafTemplate = "<leaf leaf='branch'></leaf>";
		  
	  var linker = function(scope, element, attrs) {
		  if (angular.isArray(scope.branch.children)) {
//			  console.log("branch id=" + scope.branch.id + " label=" + scope.branch.label + " chidren=" + scope.branch.children.length);
			  if (scope.branch.id === "3.1") {
				  element.append(branchMessageTemplate);
			  } else {				  
				  element.append(branchTemplate);
			  }
              $compile(element.contents())(scope); 

		  } else {
//			  console.log("leaf=" + scope.branch.label);
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

	  var leafMessage = "<li class='point branch' ng-class=\" {'toc-selected' : leaf.selected} \" context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='messageContextDiv.html' ng-click='tocSelection(leaf)'>{{leaf.label}}</li>";
 
	  var leafDefault = "<li class='point branch' ng-class=\" {'toc-selected' : leaf.selected} \" ng-click='tocSelection(leaf)'>{{leaf.label}}</li>";
	  
	  var linker = function(scope, element, attrs) {
	        if (scope.leaf.parent === "3.1") {
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

