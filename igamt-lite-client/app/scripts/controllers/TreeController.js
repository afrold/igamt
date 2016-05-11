angular
		.module('igl')
		.controller(
				'TreeCtrl',
				function($scope, $rootScope,$http) {

					$scope.collapsedata = true;
					$scope.collapsemessage = true;
					$scope.collapsesegment = true;
					$scope.collapsetable = true;

					$scope.activeModel = "";
					$scope.Activate = function(param) {
						$scope.activeModel = param;
					}
					$scope.profilecollapsed = false;

					$rootScope.switcherprofile = function() {
						$scope.profilecollapsed = !$scope.profilecollapsed;

					};
					$rootScope.switchertable = function() {
						$scope.collapsetable = !$scope.collapsetable;

					};

					$rootScope.switcherseg = function() {
						$scope.collapsesegment = !$scope.collapsesegment;

					};

					$rootScope.switchermsg = function() {
						$scope.collapsemessage = !$scope.collapsemessage;
					};

					$rootScope.switcherdata = function() {
						$scope.collapsedata = !$scope.collapsedata;

					};

					$scope.treeOptions = {

						accept : function(sourceNodeScope, destNodesScope,
								destIndex) {
							var dataTypeSource = sourceNodeScope.$element
									.attr('data-type');
							var dataTypeDest = destNodesScope.$element
									.attr('data-type');
							
							
							
							
							if (!dataTypeDest){
								return false;
							}
							else if (dataTypeDest == sourceNodeScope.$parentNodeScope.$modelValue.sectionTitle){
								
								return true;
								}
							else if (dataTypeDest === dataTypeSource + "s") {
								return true;

							} else
								return false;
						},
						dropped : function(event) {

							 var sourceNode = event.source.nodeScope;
						        var destNodes = event.dest.nodesScope;
						        var sortBefore = event.source.index ;
						        var sortAfter = event.dest.index ;

						        var dataType = destNodes.$element.attr('data-type');
						        event.source.nodeScope.$modelValue.sectionPosition = sortAfter+1;
						        $scope.updatePositions(event.dest.nodesScope.$modelValue);
						        $scope.updatePositions(event.source.nodesScope.$modelValue);
						       
				
						        $scope.updateSections(event.dest.nodesScope.$modelValue,event.source.nodesScope.$modelValue);


						}
					};

					
					
					$scope.updatePositions= function(arr){

						for (var i = arr.length - 1; i >= 0; i--){
						    arr[i].sectionPosition=i+1;
						    }
					
					};
					
					$scope.getLastPosition= function(arr){
						var position = arr.length;
						for (var i = arr.length - 1; i >= 0; i--){
							var position = arr.length;
							
						    if(arr[i].sectionPosition&& arr[i].sectionPosition>=position){
						    	return arr[i].sectionPosition+1;
						    }
						    else return position;
						    }
					
					};
					
					$scope.getLastcloneIndex= function(arr,name){
						var index=0;
						var cutIndex=name.length-1;
						for (var i = arr.length - 1; i >= 0; i--){
						    if(arr[i].sectionTitle.substring(0,cutIndex)===name &&  arr[i].sectionTitle.length>cutIndex+1){
						    	index = parseInt(arr[i].sectionTitle.substring(cutIndex+1))+1;
						    	
						    	return index;
						    }
						    else return 1;
						    }
					
					};
					
					
					
					
					$scope.sectionOption = [

							[
									'clone',
									function($itemScope) {
										console.log($itemScope);
										var id = new ObjectId().toString();
										var cloneModel = {
											id : id,
											childSections : []
										};
										var sectionTitle = $itemScope.$nodeScope.$modelValue.sectionTitle;
										sectionTitle = sectionTitle+Math.floor((Math.random() * 50000) + 1);
										cloneModel.sectionTitle = sectionTitle;
										cloneModel.sectionPosition= $scope.getLastPosition($itemScope.$nodeScope.$parentNodesScope.$modelValue);
										cloneModel.sectionContent= $itemScope.$nodeScope.$modelValue.sectionContent;
										for (var j = 0 ;j <= $itemScope.$nodeScope.$modelValue.childSections.length - 1; j++) {
										
											var idchild = new ObjectId()
													.toString();
											var child = {
												id : idchild,
												sectionTitle : "",
												childSections : []
											};
											var model = $itemScope.$nodeScope.$modelValue.childSections[j];
											var title = model.sectionTitle;
											child.sectionTitle = title;
											child.sectionPosition=model.sectionPosition;
											child.sectionContent= model.sectionContent;

											if (model.childSections) {

												for (var i = 0; i<= model.childSections.length - 1;  i++) {
													var idchild2 = new ObjectId()
															.toString();
													child2 = {
														id : idchild2
													};
													var model2 = model.childSections[i];
													var title = model2.sectionTitle;
													child2.sectionContent=model2.sectionContent;
													child2.sectionPosition=model2.sectionPosition;
													child2.sectionTitle = title;
													child.childSections
															.push(child2);
												}

											}

											cloneModel.childSections.push(child);

										}

										$itemScope.$nodeScope.$parentNodesScope.$modelValue.push(cloneModel);
										$scope.editSection(cloneModel);
										console.log($itemScope.$nodeScope.$parentNodesScope.$modelValue);

										$scope.activeModel = $itemScope.$nodeScope.$parentNodesScope.$modelValue[$itemScope.$nodeScope.$parentNodesScope.$modelValue.length - 1].id;

									} ],
							null,
							[
									'delete',
									function($itemScope) {
										$itemScope.$nodeScope.remove();
										var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue
												.indexOf($itemScope.$nodeScope.$modelValue);
										if (index > -1) {
											$itemScope.$nodeScope.$parentNodesScope.$modelValue
													.splice(index, 1);
										}
										
										$scope.updatePositions($itemScope.$nodeScope.$parentNodesScope.$modelValue);

									} 
									
									
									]

					];

					$scope.fieldOption = [

							[
									'clone',
									function($itemScope) {
										var cloneModel = {};
										var name = $itemScope.$nodeScope.$modelValue.name;

										name = name
												+ $itemScope.$nodeScope.$parentNodesScope.$modelValue.length;
										cloneModel.name = name;
										$itemScope.$nodeScope.$parentNodesScope.$modelValue
												.push(cloneModel);

										$scope.activeModel = $itemScope.$nodeScope.$parentNodesScope.$modelValue[$itemScope.$nodeScope.$parentNodesScope.$modelValue.length - 1];

									} ],
							null,
							[
									'delete',
									function($itemScope) {
										$itemScope.$nodeScope.remove();
										var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue
												.indexOf($itemScope.$nodeScope.$modelValue);
										if (index > -1) {
											$itemScope.$nodeScope.$parentNodesScope.$modelValue
													.splice(index, 1);
										}
									} ]

					];

					$scope.MessagesOption = [

							[
									'clone',
									function($itemScope) {
										var cloneModel = {};
										var name = $itemScope.$nodeScope.$modelValue.name;

										name = name
												+ $itemScope.$nodeScope.$parentNodesScope.$modelValue.length;
										var description = $itemScope.$nodeScope.$modelValue.description;

										cloneModel.name = name;
										cloneModel.description = description;
										$itemScope.$nodeScope.$parentNodesScope.$modelValue
												.push(cloneModel);

										$scope.activeModel = $itemScope.$nodeScope.$parentNodesScope.$modelValue[$itemScope.$nodeScope.$parentNodesScope.$modelValue.length - 1].id;

									} ],
							null,
							[
									'delete',
									function($itemScope) {
										$itemScope.$nodeScope.remove();
										var index = $itemScope.$nodeScope.$parentNodesScope.$modelValue
												.indexOf($itemScope.$nodeScope.$modelValue);
										if (index > -1) {
											$itemScope.$nodeScope.$parentNodesScope.$modelValue
													.splice(index, 1);
										}
									} ], null,
							[ 'export', function($itemScope) {
								$scope.selectMessages($rootScope.igdocument);
							} ]

					];
					
					


					$scope.editSeg = function(seg) {
						console.log("EditSeg")
					
						$scope.$emit('event:openSegment', seg);
						// $scope.subview = "EditSegments.html";
					}

					$scope.editIg = function(ig) {
						$rootScope.igdocument=ig;
						$scope.$emit('event:openDocumentMetadata',
								$rootScope.igdocument);
					}

					$scope.editSection = function(section) {
						$rootScope.section = section;
						$scope.$emit('event:openSection', $rootScope.section);
					}

					$scope.editRoutSection = function(param) {

						$scope.$emit('event:openSection', $scope.getRoutSectionByname(param));
					}

					
					$scope.getRoutSectionByname= function(name){
						var section={};
						if (name.toLowerCase()==='conformance profiles'){
							section.sectionContents=$rootScope.igdocument.profile.messages.sectionContents;
							section.sectionTitle=$rootScope.igdocument.profile.messages.sectionTitle;
							section.sectionPosition=$rootScope.igdocument.profile.messages.sectionPosition;
							section.sectionType=$rootScope.igdocument.profile.messages.sectionType;
							section.sectionDescription=$rootScope.igdocument.profile.messages.Description;
						
						}else if (name.toLowerCase()==='segments and field descriptions'){
							section.sectionContents=$rootScope.igdocument.profile.segmentLibrary.sectionContents;
							section.sectionTitle=$rootScope.igdocument.profile.segmentLibrary.sectionTitle;
							section.sectionPosition=$rootScope.igdocument.profile.segmentLibrary.sectionPosition;								
							section.sectionDescription=$rootScope.igdocument.profile.segmentLibrary.Description;
						}else if (name.toLowerCase()==='value sets'){
							section.sectionContents=$rootScope.igdocument.profile.tableLibrary.sectionContents;
							section.sectionTitle=$rootScope.igdocument.profile.tableLibrary.sectionTitle;
							section.sectionPosition=$rootScope.igdocument.profile.tableLibrary.sectionPosition;							
							section.sectionType=$rootScope.igdocument.profile.tableLibrary.sectionType;
							section.sectionDescription=$rootScope.igdocument.profile.tableLibrary.Description;
						}else if (name.toLowerCase()==='datatypes'){
							section.sectionContents=$rootScope.igdocument.profile.datatypeLibrary.sectionContents;
							section.sectionTitle=$rootScope.igdocument.profile.datatypeLibrary.sectionTitle;
							section.sectionPosition=$rootScope.igdocument.profile.datatypeLibrary.sectionPosition;	
							section.sectionType=$rootScope.igdocument.profile.datatypeLibrary.sectionType;
							section.sectionDescription=$rootScope.igdocument.profile.datatypeLibrary.Description;
						}
						console.log(section);
						return section;
					}
					$scope.editDataType = function(data) {
						$rootScope.datatype = data;
						$scope.$emit('event:openDatatype', $rootScope.datatype);
						$scope.subview = "EditDatatypes.html";
					}

					$scope.editTable = function(table) {
						$rootScope.table = table;
						$scope.$emit('event:openTable', $rootScope.table);
					}

					$scope.editMessage = function(message) {
						$rootScope.message = message;
						$scope.$emit('event:openMessage', message);
					}
					$scope.editProfile = function() {

						$scope.$emit('event:openProfileMetadata',
								$rootScope.igdocument);
					}

					$scope.updateSections = function (source, dest) {

						
				        $scope.loading = true;
				        
				        var data = {source:source,dest:dest ,igDocument: $rootScope.igdocument};
				        $http.post('api/updateSections', data, {timeout: 60000}).then(function (response) {
				            $scope.clear();
				            console.log("ok");
				            
				        }, function (error) {
				        console.log("error")
				        });
						}			

					
				});
