angular.module('igl').factory(
    'CloneDeleteSvc',

    function ($rootScope, $modal, ProfileAccessSvc, $cookies, IgDocumentService, MessageService, SegmentLibrarySvc, SegmentService, DatatypeService, DatatypeLibrarySvc, TableLibrarySvc, TableService, MastermapSvc, SectionSvc, FilteringSvc) {

        var svc = this;
        svc.copySection = function (section) {
            var newSection = angular.copy(section.reference);
            newSection.id = new ObjectId();
            var rand = Math.floor(Math.random() * 100);
            if (!$rootScope.igdocument.profile.metaData.ext) {
                $rootScope.igdocument.profile.metaData.ext = "";
            }
            newSection.sectionTitle = section.reference.sectionTitle + "-"
                + $rootScope.igdocument.profile.metaData.ext + "-"
                + rand;
            newSection.label = newSection.sectionTitle;
            section.parent.childSections.splice(0, 0, newSection);
            section.parent.childSections = positionElements(section.parent.childSections);
            $rootScope.$broadcast('event:openSection', newSection);
        }

        svc.copySegment = function (segment) {
            var newSegment = angular.copy(segment);
            newSegment.participants = [];
            newSegment.scope = 'USER';
            newSegment.id = null;
            newSegment.libIds = [];
            newSegment.libIds.push($rootScope.igdocument.profile.segmentLibrary.id);
            newSegment.ext = $rootScope.createNewExtension(newSegment.ext);

            if (newSegment.fields != undefined && newSegment.fields != null && newSegment.fields.length != 0) {
                for (var i = 0; i < newSegment.fields.length; i++) {
                    newSegment.fields[i].id = new ObjectId().toString();
                }
            }


            var dynamicMappings = newSegment['dynamicMappings'];
            if (dynamicMappings != undefined && dynamicMappings != null && dynamicMappings.length != 0) {
                angular.forEach(dynamicMappings, function (dynamicMapping) {
                    dynamicMapping.id = new ObjectId().toString();
                    angular.forEach(dynamicMapping.mappings, function (mapping) {
                        mapping.id = new ObjectId().toString();
                    });
                });
            }

            SegmentService.save(newSegment).then(function (result) {
                newSegment = result;
                var newLink = angular.copy(SegmentLibrarySvc.findOneChild(segment.id, $rootScope.igdocument.profile.segmentLibrary.children));
                newLink.ext = newSegment.ext;
                newLink.id = newSegment.id;

                SegmentLibrarySvc.addChild($rootScope.igdocument.profile.segmentLibrary.id, newLink).then(function (link) {
                    $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newLink);
                    $rootScope.segments.splice(0, 0, newSegment);
                    $rootScope.segment = newSegment;
                    $rootScope.segmentsMap[newSegment.id] = newSegment;
                    //TODO MasterMap need to add Segment
                    $rootScope.processElement(newSegment);
//                	MastermapSvc.addSegmentObject(newSegment, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
                    $rootScope.filteredSegmentsList.push(newSegment);
                    $rootScope.filteredSegmentsList = _.uniq($rootScope.filteredSegmentsList);
                    $rootScope.$broadcast('event:openSegment', newSegment);
                }, function (error) {
                    $rootScope.saving = false;
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }, function (error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

        svc.copyDatatype = function (datatype) {
            var newDatatype = angular.copy(datatype, {});

            newDatatype.ext = $rootScope.createNewExtension(newDatatype.ext);
            newDatatype.scope = 'USER';
            newDatatype.participants = [];
            newDatatype.id = null;
            newDatatype.libIds = [];
            newDatatype.libIds.push($rootScope.igdocument.profile.datatypeLibrary.id);
            if(datatype.scope==='MASTER'){
            	//newDatatype.hl7versions=[$rootScope.igdocument.profile.metaData.hl7Version];
            	var temp=[];
            	temp.push($rootScope.igdocument.profile.metaData.hl7Version);
            	newDatatype.hl7versions=temp;
            	newDatatype.hl7Version=$rootScope.igdocument.profile.metaData.hl7Version;
            	
            }


            if (newDatatype.components != undefined && newDatatype.components != null && newDatatype.components.length != 0) {
                for (var i = 0; i < newDatatype.components.length; i++) {
                    newDatatype.components[i].id = new ObjectId().toString();
                }
            }

            var predicates = newDatatype['predicates'];
            if (predicates != undefined && predicates != null && predicates.length != 0) {
                angular.forEach(predicates, function (predicate) {
                    predicate.id = new ObjectId().toString();
                });
            }

            var conformanceStatements = newDatatype['conformanceStatements'];
            if (conformanceStatements != undefined && conformanceStatements != null && conformanceStatements.length != 0) {
                angular.forEach(conformanceStatements, function (conformanceStatement) {
                    conformanceStatement.id = new ObjectId().toString();
                });
            }

            DatatypeService.save(newDatatype).then(function (result) {
                newDatatype = result;
                var newLink = angular.copy(DatatypeLibrarySvc.findOneChild(datatype.id, $rootScope.igdocument.profile.datatypeLibrary.children));
                newLink.id = newDatatype.id;
                newLink.ext = newDatatype.ext;
                DatatypeLibrarySvc.addChild($rootScope.igdocument.profile.datatypeLibrary.id, newLink).then(function (link) {
                    $rootScope.igdocument.profile.datatypeLibrary.children.splice(0, 0, newLink);
                    $rootScope.datatypes.splice(0, 0, newDatatype);
                    $rootScope.datatype = newDatatype;
                    $rootScope.datatypesMap[newDatatype.id] = newDatatype;

                    //TODO MasterMap need to add Datatype

                    $rootScope.processElement(newDatatype);
//                    MastermapSvc.addDatatypeObject(newDatatype, [[$rootScope.igdocument.profile.id, "profile"], [$rootScope.igdocument.id, "ig"]]);
                    $rootScope.filteredDatatypesList.push(newDatatype);
                    $rootScope.filteredDatatypesList = _.uniq($rootScope.filteredDatatypesList);
                    $rootScope.$broadcast('event:openDatatype', newDatatype);
                }, function (error) {
                    $rootScope.saving = false;
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }, function (error) {
                $rootScope.saving = false;
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        };

        svc.createNewTable = function (scope, tableLibrary) {
            var newTable = {};
            newTable.participants = [];
            newTable.scope = scope;
            newTable.id = null;
            newTable.libIds = [];
            newTable.libIds.push(tableLibrary.id);
            newTable.bindingIdentifier = $rootScope.createNewFlavorName('NewTable');
            newTable.name = "New Table";
            newTable.description = "Description";
            newTable.codes = [];
            newTable.newTable = true;

            TableService.save(newTable).then(function (result) {
                newTable = result;
                console.log(newTable);
                var newLink = {};
                newLink.bindingIdentifier = newTable.bindingIdentifier;
                newLink.id = newTable.id;

                TableLibrarySvc.addChild(tableLibrary.id, newLink).then(function (link) {
                    tableLibrary.children.splice(0, 0, newLink);
                    $rootScope.tables.splice(0, 0, newTable);
                    $rootScope.table = newTable;
                    $rootScope.tablesMap[newTable.id] = newTable;

                    $rootScope.codeSystems = [];

                    if ($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
                        $rootScope.filteredTablesList.push(newTable);
                        $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                    }
                    $rootScope.$broadcast('event:openTable', newTable);
                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });

            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

        svc.copyTable = function (table) {
            TableService.getOne(table.id).then(function(newTable){
                newTable.participants = [];
                newTable.scope = 'USER';
                newTable.id = null;
                newTable.libIds = [];
                newTable.libIds.push($rootScope.igdocument.profile.tableLibrary.id);
                newTable.bindingIdentifier = $rootScope.createNewFlavorName(newTable.bindingIdentifier);

                if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
                    for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
                        newTable.codes[i].id = new ObjectId().toString();
                    }
                }

                TableService.save(newTable).then(function (result) {
                    newTable = result;
                    var newLink = angular.copy(TableLibrarySvc.findOneChild(table.id, $rootScope.igdocument.profile.tableLibrary.children));
                    newLink.bindingIdentifier = newTable.bindingIdentifier;
                    newLink.id = newTable.id;

                    TableLibrarySvc.addChild($rootScope.igdocument.profile.tableLibrary.id, newLink).then(function (link) {
                        $rootScope.igdocument.profile.tableLibrary.children.splice(0, 0, newLink);
                        $rootScope.tables.splice(0, 0, newTable);
                        $rootScope.table = newTable;
                        $rootScope.tablesMap[newTable.id] = newTable;

                        $rootScope.codeSystems = [];

                        for (var i = 0; i < $rootScope.table.codes.length; i++) {
                            if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
                                if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                                    $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
                                }
                            }
                        }
                        if ($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
                            $rootScope.filteredTablesList.push(newTable);
                            $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
                        }
                        $rootScope.$broadcast('event:openTable', newTable);

                    }, function (error) {
                        $rootScope.msg().text = error.data.text;
                        $rootScope.msg().type = error.data.type;
                        $rootScope.msg().show = true;
                    });


                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });

            }, function(error){
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };

        svc.copyTableINLIB = function (table, tableLibrary) {
        	console.log(tableLibrary);
            var newTable = angular.copy(table);
            newTable.participants = [];
            newTable.scope = tableLibrary.scope;
            newTable.status = "UNPUBLISHED";
            newTable.id = null;
            newTable.libIds = [];
            
            newTable.bindingIdentifier = table.bindingIdentifier+(Math.floor(Math.random() * 10000000) + 1);

            if (newTable.codes != undefined && newTable.codes != null && newTable.codes.length != 0) {
                for (var i = 0, len1 = newTable.codes.length; i < len1; i++) {
                    newTable.codes[i].id = new ObjectId().toString();
                }
            }

            TableService.save(newTable).then(function (result) {
                newTable = result;
                var newLink = angular.copy(TableLibrarySvc.findOneChild(table.id,tableLibrary.children));
                newLink.bindingIdentifier = newTable.bindingIdentifier;
                newLink.id = newTable.id;

                TableLibrarySvc.addChild(tableLibrary.id, newLink).then(function (link) {
                    tableLibrary.children.splice(0, 0, newLink);
                    $rootScope.tables.splice(0, 0, newTable);
                    $rootScope.table = newTable;
                    $rootScope.tablesMap[newTable.id] = newTable;

                    $rootScope.codeSystems = [];

                    for (var i = 0; i < $rootScope.table.codes.length; i++) {
                        if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
                            if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                                $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
                            }
                        }
                    }
//                    if ($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
//                        $rootScope.filteredTablesList.push(newTable);
//                        $rootScope.filteredTablesList = _.uniq($rootScope.filteredTablesList);
//                    }
                    $rootScope.$broadcast('event:openTable', newTable);

                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });


            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });
        };
        
        
        svc.copyMessage = function (message) {
            var newMessage={};
            var newMessage = angular.copy(message);
            newMessage.id = new ObjectId().toString();
            newMessage.position=$rootScope.igdocument.profile.messages.length+1;
            newMessage.name = $rootScope.createNewFlavorName(message.name);
            var groups = ProfileAccessSvc.Messages().getGroups(newMessage);
            angular.forEach(groups, function (group) {
                group.id = new ObjectId().toString();
            });

            var segRefs = ProfileAccessSvc.Messages().getSegmentRefs(newMessage);
            angular.forEach(segRefs, function (segRef) {
                segRef.id = new ObjectId().toString();
            });

            MessageService.save(newMessage).then(function (result) {
                newMessage = result;
                $rootScope.messagesMap[newMessage.id]=newMessage;
                //MessageService.merge($rootScope.messagesMap[newMessage.id], newMessage);                
                $rootScope.igdocument.profile.messages.children.push(newMessage);
                
                IgDocumentService.save($rootScope.igdocument).then(function (igd) {
                    $rootScope.messages = $rootScope.igdocument.profile.messages;
                    $rootScope.message = newMessage;

                    $rootScope.processElement(newMessage);
                    //TODO Mastermap need to add Message
//                    MastermapSvc.addMessageObject(newMessage, [[$rootScope.igdocument.id, "ig"], [$rootScope.igdocument.profile.id, "profile"]]);
//                    FilteringSvc.addMsgInFilter(newMessage.name, newMessage.id);
                    $rootScope.$broadcast('event:openMessage', newMessage);
                    return newMessage;
                }, function (error) {
                    $rootScope.msg().text = error.data.text;
                    $rootScope.msg().type = error.data.type;
                    $rootScope.msg().show = true;
                });
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = error.data.type;
                $rootScope.msg().show = true;
            });

        };

        svc.deleteValueSet = function (table) {
                 $rootScope.references = [];
                angular.forEach($rootScope.segments, function (segment) {
                    $rootScope.findTableRefs(table, segment, $rootScope.getSegmentLabel(segment),segment);
                });
                angular.forEach($rootScope.datatypes, function (dt) {
                    $rootScope.findTableRefs(table, dt, $rootScope.getDatatypeLabel(dt),dt);
                });

                if ($rootScope.references != null && $rootScope.references.length > 0) {
                    abortValueSetDelete(table);
                } else {
                    confirmValueSetDelete(table);
                }
         }


        svc.exportDisplayXML = function (messageID) {
            var form = document.createElement("form");
            form.action = $rootScope.api('api/igdocuments/' + $rootScope.igdocument.id + '/export/Display/' + messageID);
            form.method = "POST";
            form.target = "_target";
            var csrfInput = document.createElement("input");
            csrfInput.name = "X-XSRF-TOKEN";
            csrfInput.value = $cookies['XSRF-TOKEN'];
            form.appendChild(csrfInput);
            form.style.display = 'none';
            document.body.appendChild(form);
            form.submit();
        }

        function abortValueSetDelete(table) {
            var modalInstance = $modal.open({
                templateUrl: 'ValueSetReferencesCtrl.html',
                controller: 'ValueSetReferencesCtrl',
                resolve: {
                    tableToDelete: function () {
                        return table;
                    }
                }
            });
            modalInstance.result.then(function (table) {
                // $rootScope.tableToDelete = table;
            }, function () {
            });
        };

        function confirmValueSetDelete(table) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmValueSetDeleteCtrl.html',
                controller: 'ConfirmValueSetDeleteCtrl',
                resolve: {
                    tableToDelete: function () {
                        return table;
                    }
                }
            });
            modalInstance.result.then(function (table) {
//                tableToDelete = table;
                if (table.id === $rootScope.activeModel) {
                    $rootScope.displayNullView();
                }
                if($rootScope.filteredTablesList && $rootScope.filteredTablesList != null) {
                    var index = $rootScope.filteredTablesList.indexOf(table);
                    if(index >= 0)
                    $rootScope.filteredTablesList.splice(index, 1);
                }

            }, function () {
            });
        };

        function confirmMessageDelete(message) {
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmMessageDeleteCtrl.html',
                controller: 'ConfirmMessageDeleteCtrl',
                resolve: {
                    messageToDelete: function () {
                        return message;
                    }
                }
            });
            modalInstance.result.then(function (message) {
            }, function () {
            });
        };

        function deleteValueSets(vssIdsSincerelyDead) {
            return ProfileAccessSvc.ValueSets().removeDead(vssIdsSincerelyDead);
        }

        svc.deleteDatatype = function (datatype) {
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                if(segment && segment != null) {
                    $rootScope.findDatatypeRefs(datatype, segment, $rootScope.getSegmentLabel(segment),segment);
                }
            });
            angular.forEach($rootScope.datatypes, function (dt) {
                if (dt && dt != null && dt.id !== datatype.id) $rootScope.findDatatypeRefs(datatype, dt, $rootScope.getDatatypeLabel(dt),dt);
            });
            if ($rootScope.references != null && $rootScope.references.length > 0) {
                abortDatatypeDelete(datatype);
            } else {
                confirmDatatypeDelete(datatype);
            }
        }

        svc.deleteTableAndTableLink = function (table) {
            TableService.delete(table).then(function (result) {
                svc.deleteTableLink(table);
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        svc.deleteTableLink = function (table) {
            TableLibrarySvc.deleteChild($rootScope.igdocument.profile.tableLibrary.id, table.id).then(function (res) {
                var index = $rootScope.tables.indexOf(table);
                $rootScope.tables.splice(index, 1);
                var tmp = TableLibrarySvc.findOneChild(table.id, $rootScope.igdocument.profile.tableLibrary.children);
                index = $rootScope.igdocument.profile.tableLibrary.children.indexOf(tmp);
                $rootScope.igdocument.profile.tableLibrary.children.splice(index, 1);
                $rootScope.tablesMap[table.id] = null;
                $rootScope.references = [];
                if ($rootScope.table === table) {
                    $rootScope.table = null;
                }
                $rootScope.msg().text = "tableDeleteSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                //TODO MasterMap Need to delete Table
//                MastermapSvc.deleteTable(table.id);
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        svc.deleteSegmentAndSegmentLink = function (segment) {
            SegmentService.delete(segment).then(function (result) {
                svc.deleteSegmentLink(segment);
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        svc.deleteSegmentLink = function (segment) {
            SegmentLibrarySvc.deleteChild($rootScope.igdocument.profile.segmentLibrary.id, segment.id).then(function (res) {
                // We must delete from two collections.

                var tmp = SegmentLibrarySvc.findOneChild(segment.id, $rootScope.igdocument.profile.segmentLibrary.children);
                var index = $rootScope.igdocument.profile.segmentLibrary.children.indexOf(tmp);
                $rootScope.igdocument.profile.segmentLibrary.children.splice(index, 1);

                tmp = SegmentLibrarySvc.findOneChild(segment.id, $rootScope.segments);
                index = $rootScope.segments.indexOf(tmp);
                $rootScope.segments.splice(index, 1);
                $rootScope.segmentsMap[segment.id] = null;
                $rootScope.references = [];
                if ($rootScope.segment === segment) {
                    $rootScope.segment = null;
                }

                $rootScope.msg().text = "segDeleteSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };


        svc.deleteDatatypeAndDatatypeLink = function (datatype) {
            DatatypeService.delete(datatype).then(function (result) {
                svc.deleteDatatypeLink(datatype);
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        svc.deleteDatatypeLink = function (datatype) {
            DatatypeLibrarySvc.deleteChild($rootScope.igdocument.profile.datatypeLibrary.id, datatype.id).then(function (res) {
                var index = $rootScope.datatypes.indexOf(datatype);
                $rootScope.datatypes.splice(index, 1);

                var tmp = DatatypeLibrarySvc.findOneChild(datatype.id, $rootScope.igdocument.profile.datatypeLibrary.children);
                index = $rootScope.igdocument.profile.datatypeLibrary.children.indexOf(tmp);
                $rootScope.igdocument.profile.datatypeLibrary.children.splice(index, 1);
                $rootScope.datatypesMap[datatype.id] = null;
                $rootScope.references = [];
                if ($rootScope.datatype === datatype) {
                    $rootScope.datatype = null;
                }

                $rootScope.msg().text = "DatatypeDeleteSuccess";
                $rootScope.msg().type = "success";
                $rootScope.msg().show = true;
                //TODO MasterMap need to delete datatype
//                MastermapSvc.deleteDatatype($scope.segToDelete.id);
                $rootScope.$broadcast('event:SetToC');
            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
            });
        };

        function abortDatatypeDelete(datatype) {
            var dtToDelete;
            var modalInstance = $modal.open({
                templateUrl: 'DatatypeReferencesCtrl.html',
                controller: 'DatatypeReferencesCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                dtToDelete = datatype;
            }, function () {
            });
        };

        function confirmDatatypeDelete(datatype) {
            var dtToDelete;
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmDatatypeDeleteCtrl.html',
                controller: 'ConfirmDatatypeDeleteCtrl',
                resolve: {
                    dtToDelete: function () {
                        return datatype;
                    }
                }
            });
            modalInstance.result.then(function (datatype) {
                if (datatype.id === $rootScope.activeModel) {
                    $rootScope.displayNullView();
                }
                if ($rootScope.filteredDatatypesList && $rootScope.filteredDatatypesList != null) {
                    var index = $rootScope.filteredDatatypesList.indexOf(datatype);
                    if (index >= 0)
                        $rootScope.filteredDatatypesList.splice(index, 1);
                }
                dtToDelete = datatype;
            }, function () {
            });
        };


        function deleteDatatypes(dtIdsLive, dtsIdsSincerelyDead) {

            // Get all value sets that are contained in the sincerely dead datatypes.
            var vssIdsMerelyDead = ProfileAccessSvc.Datatypes().findValueSetsFromDatatypeIds(dtsIdsSincerelyDead);
            // then all value sets that are contained in the live datatypes.
            var vssIdsLive = ProfileAccessSvc.Datatypes().findValueSetsFromDatatypeIds(dtIdsLive);
            var vssIdsSincerelyDead = ProfileAccessSvc.ValueSets().findDead(vssIdsMerelyDead, vssIdsLive);
            deleteValueSets(vssIdsSincerelyDead);

            var rval = ProfileAccessSvc.Datatypes().removeDead(dtsIdsSincerelyDead);

//				console.log("deleteDatatypes: vssIdsMerelyDead=" + vssIdsMerelyDead.length);
//				console.log("deleteDatatypes: vssIdsLive=" + vssIdsLive.length);
//				console.log("deleteDatatypes: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);

            return rval;
        }

        svc.deleteSegment = function (segment) {
            $rootScope.references = [];
            angular.forEach($rootScope.igdocument.profile.messages.children, function (message) {
                $rootScope.findSegmentRefs(segment, message, '', '', message);
            });

            if ($rootScope.references != null && $rootScope.references.length > 0) {
                abortSegmentDelete(segment);
            } else {

                confirmSegmentDelete(segment);
            }
        }

        function abortSegmentDelete(segment) {
            var segToDelete;
            var modalInstance = $modal.open({
                templateUrl: 'SegmentReferencesCtrl.html',
                controller: 'SegmentReferencesCtrl',
                resolve: {
                    segToDelete: function () {
                        return segment;
                    }
                }
            });
            modalInstance.result.then(function (segment) {
                segToDelete = segment;
            }, function () {
            });
        };

        function confirmSegmentDelete(segment) {
            var segToDelete;
            var modalInstance = $modal.open({
                templateUrl: 'ConfirmSegmentDeleteCtrl.html',
                controller: 'ConfirmSegmentDeleteCtrl',
                resolve: {
                    segToDelete: function () {
                        return segment;
                    }
                }
            });
            modalInstance.result.then(function (segment) {
                segToDelete = segment;
                if (segment.id === $rootScope.activeModel) {
                    $rootScope.displayNullView();
                }
                if ($rootScope.filteredSegmentsList && $rootScope.filteredSegmentsList != null) {
                    var index = $rootScope.filteredSegmentsList.indexOf(segment);
                    if (index >= 0)
                        $rootScope.filteredSegmentsList.splice(index, 1);
                }

            }, function () {
            });
        };

        function deleteSegments(segmentRefsLive, segmentRefsSincerelyDead) {

            // Get all datatypes that are contained in the sincerely dead segments.
            var dtIdsMerelyDead = ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segmentRefsSincerelyDead);

            // then all datatypes that are contained in the live segments.
            var dtIdsLive = ProfileAccessSvc.Segments().findDatatypesFromSegmentRefs(segmentRefsLive);
            var dtsIdsSincerelyDead = ProfileAccessSvc.Datatypes().findDead(dtIdsMerelyDead, dtIdsLive);
            deleteDatatypes(dtIdsLive, dtsIdsSincerelyDead);

            var rval = ProfileAccessSvc.Segments().removeDead(segmentRefsSincerelyDead);

//				console.log("deleteSegments: dtIdsMerelyDead=" + dtIdsMerelyDead.length);
//				console.log("deleteSegments: dtIdsLive=" + dtIdsLive.length);
//				console.log("deleteSegments: dtsIdsSincerelyDead=" + dtsIdsSincerelyDead.length);

            return rval;
        }

        svc.execDeleteMessage = function (message) {

            // We do the delete in pairs: dead and live.  dead = things we are deleting and live = things we are keeping.
            // We are deleting the message so it's dead.
            // The message there is from the ToC so what we need is its reference,
            // and it must be an array of one.
            var msgDead = [message.id];
            // We are keeping the children so their live.
            var msgLive = ProfileAccessSvc.Messages().messages();

            // We remove the dead message from the living.
            var idxP = _.findIndex(msgLive, function (child) {
                return child.id === msgDead[0];
            });

            msgLive.splice(idxP, 1);
            if (0 === ProfileAccessSvc.Messages().messages().length) {
                ProfileAccessSvc.ValueSets().truncate();
                ProfileAccessSvc.Datatypes().truncate();
                ProfileAccessSvc.Segments().truncate();
                return;
            }
            // We get all segment refs that are contained in the dead message.
            var segmentRefsMerelyDead = ProfileAccessSvc.Messages()
                .getAllSegmentRefs(msgDead);
            // We get all segment refs that are contained in the live messages.
            var segmentRefsLive = ProfileAccessSvc.Messages()
                .getAllSegmentRefs(msgLive);
            // Until now, dead meant mearly dead.  We now remove those that are most sincerely dead.
            var segmentRefsSincerelyDead = ProfileAccessSvc.Segments().findDead(segmentRefsMerelyDead, segmentRefsLive);
            if (segmentRefsSincerelyDead.length === 0) {
                return;
            }

            var rval = deleteSegments(segmentRefsLive, segmentRefsSincerelyDead);
            //TODO mastermap need to delete message
//            MastermapSvc.deleteMessage(message.id);
//            FilteringSvc.removeMsgFromFilter(message.id);
            return rval;
        }

        svc.deleteMessage = function (message) {
            confirmMessageDelete(message);
        }

        svc.deleteSection = function (section) {
            SectionSvc.delete($rootScope.igdocument.id, section.reference).then(function (result) {
                var secLive = section.parent.childSections;

                var idxP = _.findIndex(secLive, function (child) {
                    return child.id === section.reference.id;
                });
                section.parent.childSections.splice(idxP, 1);

            }, function (error) {
                $rootScope.msg().text = error.data.text;
                $rootScope.msg().type = "danger";
                $rootScope.msg().show = true;
                $rootScope.manualHandle = true;
            });
        }

        svc.findMessageIndex = function (messages, id) {
            var idxT = _.findIndex(messages.children, function (child) {
                return child.reference.id === id;
            })
            return idxT;
        }

        function positionElements(chidren) {
            var sorted = _.sortBy(chidren, "sectionPosition");
            var start = sorted[0].sectionPosition;
            _.each(sorted, function (sortee) {
                sortee.sectionPosition = start++;
            });
            return sorted;
        }

        function sortElementsByAlphabetically(chidren) {
            return _.sortBy(chidren, "name");
        }

        return svc;
    });
