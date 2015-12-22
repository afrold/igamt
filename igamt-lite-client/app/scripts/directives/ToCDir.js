angular
		.module('igl')
		.directive(
				'trunk',
				function() {
					console.log("trunk");

					var template = "<ul class='trunk'><branch ng-repeat='branch in trunk track by $index' branch='branch'></branch></ul>";

					return {
						restrict : "E",
						replace : true,
						scope : {
							trunk : '='
						},
						template : template,
					}
				})
		.directive(
				'drop',
				function() {
					console.log("drop");

					var template = "<ul dnd-list='drop'>"
							+ "<branch ng-repeat='branch in drop track by $index' index='$index' branch='branch' drop='drop'></branch>"
							+ "</ul>";

					return {
						restrict : "E",
						replace : true,
						scope : {
							drop : '='
						},
						template : template,
					}
				})
		.directive(
				"branch",
				function($compile) {
					var branchTemplate = "<li class='branch'>"
							+ "<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected } \">"
							+ "{{branch.label}}"
							+ "</label><input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
							+ "<trunk trunk='branch.children'></trunk>"
							+ "</li>";
					var branchMessageTemplate = "<li class='branch'"
							+ " context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='messageHeadContextDiv.html'>"
							+ "<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected } \">"
							+ "{{branch.label}}"
							+ "</label><input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
							+ "<drop drop='branch.children'></drop>"
							+ "</li>";
					var leafTemplate = "<leaf leaf='branch' drop='drop' index='index'></leaf>";

					var linker = function(scope, element, attrs) {
						if (angular.isArray(scope.branch.children)) {
							// console.log("branch id=" + scope.branch.id + "
							// label=" + scope.branch.label + " chidren=" +
							// scope.branch.children.length);
							if (scope.branch.id === "3.1") {
								element.append(branchMessageTemplate);
							} else {
								element.append(branchTemplate);
							}
							$compile(element.contents())(scope);

						} else {
							// console.log("leaf=" + scope.branch.label);
							element.append(leafTemplate).show();
							$compile(element.contents())(scope);
						}
					}

					return {
						restrict : "E",
						replace : true,
						controller : "ToCCtl",
						scope : {
							index : '=',
							drop : '=',
							branch : '='
						},
						link : linker
					}
				})
		.directive(
				"leaf",
				function($compile) {

					var leafMessage = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
			            + " dnd-draggable='leaf'"
			            + " dnd-effect-allowed='move'"
			            + " dnd-moved='moved(index, leaf, drop)'"
			            + " dnd-selected='models.selected = leaf'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='messageContextDiv.html' ng-click='tocSelection(leaf)'>"
						+ "{{leaf.label}}" 
						+ "</li>";

					var leafDefault = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected} \" ng-click='tocSelection(leaf)'>{{leaf.label}}</li>";

					var linker = function(scope, element, attrs) {
						if (scope.leaf.parent === "3.1") {
							element.html(leafMessage).show();
						} else {
							element.html(leafDefault).show();
						}
						$compile(element.contents())(scope);
					}

					return {
						restrict : "E",
						replace : true,
						controller : "ToCCtl",
						scope : {
							index : '=',
							leaf : '=',
							drop : '='
						},
						link : linker
					}
				})
