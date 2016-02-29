angular
		.module('igl')
		.directive(
				'trunk',
				function() {
					console.log("trunk");

					var template = "<ul class='trunk'><branch ng-repeat='branch in trunk track by trackBy()' branch='branch'></branch></ul>";

					return {
						restrict : "E",
						replace : true,
						controller : "ToCCtl",
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
					var branchTemplate = "<li class='branch' prevent-right-click>"
							+ "<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" ng-click='tocSelection(branch)'>"
							+ "{{branch.label}}"
							+ "</label>"
							+ "<input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
							+ "<trunk trunk='branch.children'></trunk>"
							+ "</li>";
					var branchMessageTemplate = "<li class='branch'"
							+ " context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='messageHeadContextDiv.html'>"
							+ "<label for='{{branch.id}}' class='fa' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" ng-click='tocSelection(branch)'>"
							+ "{{branch.label}}"
							+ "</label>"
							+ "<input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
							+ "<drop drop='branch.children'></drop>"
							+ "</li>";
					var leafTemplate = "<leaf leaf='branch' index='index'></leaf>";

					var linker = function(scope, element, attrs) {
						console.log("<=label=" + scope.branch.label);
						if (angular.isArray(scope.branch.children)) {
							 console.log("branch id=" + scope.branch.id + " branch type=" + scope.branch.type +
							 " label=" + scope.branch.label + " children=" +
							 scope.branch.children.length);
							if (scope.branch.id === "messages") {
								element.append(branchMessageTemplate);
							} else {
								element.append(branchTemplate);
							}
							$compile(element.contents())(scope);

						} else {
							console.log("leaf id=" + scope.branch.id + " leaf type="  + scope.branch.type + " leaf label="  + scope.branch.label + " parent=" + scope.branch.parent.type);
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

					var leafMetadata = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
						+ "  prevent-right-click ng-click='tocSelection(leaf)'> "
						+ "{{leaf.label}}" 
						+ "</li>";
					var leafMessage = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
			            + " dnd-draggable='leaf'"
			            + " dnd-effect-allowed='move'"
			            + " dnd-moved='moved(index, leaf)'"
			            + " dnd-selected='models.selected = leaf'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='leafContextDiv.html' ng-click='tocSelection(leaf, branch)'> "
						+ "{{leaf.reference.name}} - {{leaf.reference.description}}" 
						+ "</li>";

					var leafValueSet = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='leafContextDiv.html' ng-click='tocSelection(leaf)'> "
						+ "{{leaf.reference.bindingIdentifier}} - {{leaf.reference.description}}" 
						+ "</li>";

					var leafSection = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='leafContextDiv.html' ng-click='tocSelection(leaf)'> "
						+ "{{leaf.reference.sectionTitle}}"

					var leafDefault = "<li class='point leaf' ng-class=\" {'toc-selected' : leaf.selected, 'selected': models.selected === leaf} \" "
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='leafContextDiv.html' ng-click='tocSelection(leaf)'> "
						+ "{{leaf.reference.label}} - {{leaf.reference.description}}"
						+ "</li>";

					var linker = function(scope, element, attrs) {
						if (scope.leaf.type === "documentMetadata" || scope.leaf.type === "profileMetadata") {
							element.html(leafMetadata).show();
//							console.log("leafMeta=" + scope.leaf.label + " parent=" + scope.leaf.parent);
						} else if (scope.leaf.type === "section") {
							element.html(leafSection).show();
							console.log("leafSection=" + scope.leaf.label + " parent=" + scope.leaf.parent);
						} else if (scope.leaf.type === "message") {
							element.html(leafMessage).show();
//							console.log("leafMessage=" + scope.leaf.label + " parent=" + scope.leaf.parent + " leaf.reference.name=" + scope.leaf.reference.name);
						} else if (scope.leaf.type === "table") {
								element.html(leafValueSet).show();
//								console.log("leafTable=" + scope.leaf.label + " parent=" + scope.leaf.parent);
						} else {
							element.html(leafDefault).show();
//							console.log("leafDefault=" + scope.leaf.label + " parent=" + scope.leaf.parent);
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
