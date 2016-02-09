angular
		.module('igl')
		.controller(
				'ToCCtl',
				[
						'$scope',
						'$rootScope',
						'$q',
						'ToCSvc',
						'ContextMenuSvc',
						'CloneDeleteSvc',
						function($scope, $rootScope, $q, ToCSvc,
								ContextMenuSvc, CloneDeleteSvc) {
							var ctl = this;
							$scope.collapsed = [];
							$scope.yesDrop = false;
							$scope.noDrop = true;
							$scope.$watch('tocData', function(newValue,
									oldValue) {
								if (!oldValue && newValue) {
									_.each($scope.tocData, function(head) {
										$scope.collapsed[head] = false;
									});
								}
							});
							$scope.moved = function (index, leaf, branch) {
								var idx = _.findLastIndex(branch, function(leaf1) {
									return leaf.id === leaf1.id;
								});
							
								if (index === idx) {
									branch.splice(index + 1, 1);
								} else {
									branch.splice(index, 1);
								}
							}
							$scope.calcOffset = function(level) {
								return "margin-left : " + level + "em";
							}

							$scope.tocSelection = function(entry) {
								// TODO gcr: See about refactoring this to
								// eliminate the switch.
								// One could use entry.reference.type to assemble
								// the $emit string.
								// Doing so would require maintaining a sync
								// with the ProfileListController.
								entry.selected = true;
								ToCSvc.currentLeaf.selected = false;
								ToCSvc.currentLeaf = entry;
								console.log("entry.parent=" + entry.parent);
								switch (entry.parent) {
								case "documentMetadata": {
									$scope.$emit('event:openDocumentMetadata',
											entry.reference);
									break;
								}
								case "profileMetadata": {
									$scope.$emit('event:openProfileMetadata',
											entry.reference);
									break;
								}
								case "message": {
									$scope.$emit('event:openMessage',
											entry.reference);
									break;
								}
								case "segment": {
									$scope.$emit('event:openSegment',
											entry.reference);
									break;
								}
								case "datatype": {
									$scope.$emit('event:openDatatype',
											entry.reference);
									break;
								}
								case "table": {
									$scope.$emit('event:openTable',
											entry.reference);
									break;
								}
								default: {
									$scope.$emit('event:openSection',
											entry.reference);
									break;
								}
								}
								return $scope.subview;
							};
							
							$scope.closedCtxSubMenu = function(leaf, $index) {
								var ctxMenuSelection = ContextMenuSvc.get();
								switch (ctxMenuSelection) {
								case "Copy":
									console.log("Copy==> node=" + leaf);
									if (leaf.reference.type === 'section') {
					        				CloneDeleteSvc.copySection(leaf.reference);
									} else if (leaf.reference.type === 'segment') {
						        			CloneDeleteSvc.copySegment(leaf.reference);
									}  else if (leaf.reference.type === 'datatype') {
						        			CloneDeleteSvc.copyDatatype(leaf.reference);
									} else if (leaf.reference.type === 'table') {
										CloneDeleteSvc.copyTable(leaf.reference);
									} else if (leaf.reference.type === 'message') {
										CloneDeleteSvc.copyMessage(leaf.reference);
									}
									break;
//								case "Copy":
//									console.log("Clone==> node=" + leaf);
//									CloneDeleteSvc.cloneMessage(
//											$rootScope.igdocument, leaf.reference);
//									$rootScope.$broadcast('event:SetToC');
//									break;
								case "Delete":
									console.log("Copy==> node=" + leaf);
									if (leaf.reference.type === 'section') {
					        				CloneDeleteSvc.deleteSection(leaf.reference);
									} else if (leaf.reference.type === 'segment') {
						        			CloneDeleteSvc.deleteSegment(leaf.reference);
									}  else if (leaf.reference.type === 'datatype') {
						        			CloneDeleteSvc.deleteDatatype(leaf.reference);
									} else if (leaf.reference.type === 'table') {
										CloneDeleteSvc.deleteValueSet(leaf.reference);
									} else if (leaf.reference.type === 'message') {
										CloneDeleteSvc.deleteMessage(leaf.reference);
									}
									break;
								default:
									console
											.log("Context menu defaulted with "
													+ ctxMenuSelection
													+ " Should be Add, clone, or Delete.");
								}
								$rootScope.$broadcast('event:SetToC');
							};

						} ])