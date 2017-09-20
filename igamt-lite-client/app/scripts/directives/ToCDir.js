angular
		.module('igl')
		.directive(
				'trunk',
				function() {
//					console.log("trunk");

					var template = "<ul class='trunk'><branch ng-repeat='branch in trunk track by trackBy()' branch='branch'></branch></ul>";

					return {
						restrict : "E",
						replace : true,
						controller : "ToCCtl",
						scope : {
							trunk : '='
						},
						template : template
					}
				})
		.directive(
				'drop',
				function() {
//					console.log("drop");

					var template = "<ul dnd-list='drop'>"
							+ "<branch ng-repeat='branch in drop track by $index' index='$index' branch='branch' drop='drop'></branch>"
							+ "</ul>";

					return {
						restrict : "E",
						replace : true,
						scope : {
							drop : '='
						},
						template : template
					}
				})
		.directive(
				"branch",
				function($compile) {
					var branchNoCtxTemplate = "<li class='branch' prevent-right-click>"
						+ "<label for='{{branch.id}}' class='fa fa-lg' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" />"
						+ "</label>"
						+ "<input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
						+ "<a ng-click='tocSelection(branch)' ng-class=\" {'toc-selected' : branch.highlight, 'selected': models.selected === branch} \" >{{branch.label}}</a>"
						+ "<trunk trunk='branch.children'></trunk>"
						+ "</li>";
					var branchTemplate = "<li class='branch'"
						+ " context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='contextDiv.html''> "
						+ "<label for='{{branch.id}}' class='fa fa-lg' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" />"
						+ "</label>"
						+ "<input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
						+ "<a ng-click='tocSelection(branch)' ng-class=\" {'toc-selected' : branch.highlight, 'selected': models.selected === branch} \" >{{branch.label}}</a>"
						+ "<trunk trunk='branch.children'></trunk>"
						+ "</li>";
					var branchMessagesTemplate = "<li class='branch'"
						+ " context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='messageHeadContextDiv.html'>"
						+ "<label for='{{branch.id}}' class='fa fa-lg' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" />"
						+ "<input type='checkbox' id='{{branch.id}}'ng-model='branch.selected'/>"
						+ "<a ng-click='tocSelection(branch)' ng-class=\" {'toc-selected' : branch.highlight, 'selected': models.selected === branch} \" >{{branch.label}}</a>"
						+ "<drop drop='branch.children'></drop>"
						+ "</li>";
					var branchTablesTemplate = "<li class='branch'"
						+ " context-menu context-menu-close='closedCtxSubMenu(branch)' data-target='tableHeadContextDiv.html'>"
						+ "<label for='{{branch.id}}' class='fa fa-lg' ng-class=\" {'fa-caret-right': branch.selected,'fa-caret-down': !branch.selected} \" />"
						+ "</label>"
						+ "<input type='checkbox' id='{{branch.id}}' ng-model='branch.selected'/>"
						+ "<a ng-click='tocSelection(branch)' ng-class=\" {'toc-selected' : branch.highlight, 'selected': models.selected === branch} \" >{{branch.label}}</a>"
						+ "<drop drop='branch.children'></drop>"
						+ "</li>";
					var leafTemplate = "<leaf leaf='branch' index='index'></leaf>";

					var linker = function(scope, element, attrs) {
//						console.log("<=label=" + scope.branch.label);
						if (angular.isArray(scope.branch.children)) {
//							 console.log("branch id=" + scope.branch.id + " branch type=" + scope.branch.type +
//							 " label=" + scope.branch.label + " children=" +
//							 scope.branch.children.length);
							if ( _.indexOf(["profile", "segments", "datatypes"], scope.branch.type) > -1) {
								element.append(branchNoCtxTemplate);
							} else if (scope.branch.type === "messages") {
								element.append(branchMessagesTemplate);
							} else if (scope.branch.type === "tables") {
								element.append(branchTablesTemplate);
							} else {
								element.append(branchTemplate);
							}
							$compile(element.contents())(scope);

						} else {
//							console.log("leaf id=" + scope.branch.id + " leaf type="  + scope.branch.type + " leaf label="  + scope.branch.label + " parent=" + scope.branch.parent.type);
							element.append(leafTemplate).show();
							$compile(element.contents())(scope);
						}
					};

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

					var leafMetadata = "<li class='point leaf'"
						+ "  prevent-right-click> "
						+ "<a ng-click='tocSelection(leaf)' ng-class=\" {'toc-selected' : leaf.highlight, 'selected': models.selected === leaf} \" >{{leaf.label}}</a>"
						+ "</li>";

					var leafMessage = "<li class='point leaf'"
			            + " ng-show='show(leaf)'"
			            + " dnd-draggable='leaf'"
			            + " dnd-effect-allowed='move'"
			            + " dnd-moved='moved(index, leaf)'"
			            + " dnd-selected='models.selected = leaf'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='messageContextDiv.html'> "
						+ "<a ng-click='tocSelection(leaf)' ng-class=\" {'toc-selected' : leaf.highlight, 'selected': models.selected === leaf} \" >{{leaf.reference.name}} - {{leaf.reference.description}}</a>"
						+ "</li>";

					var leafValueSet = "<li class='point leaf'"
            + " ng-show='show(leaf)'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='contextDiv.html'> "
						+ "<a ng-click='tocSelection(leaf)' ng-class=\" {'toc-selected' : leaf.highlight, 'selected': models.selected === leaf} \" >{{leaf.reference.bindingIdentifier}} - {{leaf.reference.name}}</a>"
						+ "</li>";

					var leafSection = "<li class='point leaf'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='contextDiv.html'> "
						+ "<a ng-click='tocSelection(leaf)' ng-class=\" {'toc-selected' : leaf.highlight, 'selected': models.selected === leaf} \" >{{leaf.reference.sectionTitle}}</a>"

					var leafDefault = "<li class='point leaf'"
            + " ng-show='show(leaf)'"
						+ " context-menu context-menu-close='closedCtxSubMenu(leaf)' data-target='contextDiv.html'> "
						+ "<a ng-click='tocSelection(leaf)' ng-class=\" {'toc-selected' : leaf.highlight, 'selected': models.selected === leaf} \" >{{leaf.label}} - {{leaf.reference.description}}</a>"
						+ "</li>";

					var linker = function(scope, element, attrs) {
						if (_.indexOf(["documentMetadata", "profileMetadata"] ,scope.leaf.type) > -1) {
							element.html(leafMetadata).show();
//							console.log("leafMeta=" + scope.leaf.label + " type=" + scope.leaf.type + " parent=" + scope.leaf.parent);
						} else if (scope.leaf.type === "section") {
							element.html(leafSection).show();
//							console.log("leafSection=" + scope.leaf.label + " type=" + scope.leaf.type  + " parent=" + scope.leaf.parent);
						} else if (scope.leaf.type === "message") {
							element.html(leafMessage).show();
//							console.log("leafMessage=" + scope.leaf.label + " type=" + scope.leaf.type  + " parent=" + scope.leaf.parent + " leaf.reference.id=" + scope.leaf.reference.id + " leaf.reference.position=" + scope.leaf.reference.position);
						} else if (scope.leaf.type === "table") {
							element.html(leafValueSet).show();
//								console.log("leafTable=" + scope.leaf.label + " type=" + scope.leaf.type  + " parent=" + scope.leaf.parent);
						} else {
							element.html(leafDefault).show();
							console.log("ToCDir leafDefault label=" + scope.leaf.label + " type=" + scope.leaf.type);
//							console.log("leafDefault=" + scope.leaf.label + " parent=" + scope.leaf.parent);
						}
						$compile(element.contents())(scope);
					};

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
				});
