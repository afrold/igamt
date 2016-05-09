angular.module('igl').factory(
    'CloneDeleteSvc',
//    function ($rootScope, $modal, ProfileAccessSvc, $cookies, DatatypeLibrarySvc,SegmentLibrarySvc,TableLibrarySvc,MessageService,MessageLibrarySvc) {
    function ($rootScope, $modal, ProfileAccessSvc, $cookies, DatatypeLibrarySvc,SegmentLibrarySvc,TableLibrarySvc,MessageService) {

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
            $rootScope.$broadcast('event:SetToC');
            $rootScope.$broadcast('event:openSection', newSection);
        }

        svc.copySegment = function (segment) {
            var newSegment = angular.copy(segment);
            newSegment.id = null;
            newSegment.libIds =[];
            newSegment.id = null;
            newSegment.participants = [];
            var segmentLink = SegmentLibrarySvc.findOneChild(segment.id, $rootScope.igdocument.profile.segmentLibrary);
            newSegment.ext = $rootScope.createNewExtension(segmentLink.ext);

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
//			                		angular.forEach(mapping.cases, function (case) {
//			                			case.id = new ObjectId().toString();
//			                		});
                    });
                });
            }
//            $rootScope.segments.push(newSegment);
//            $rootScope.igdocument.profile.segmentLibrary.children.splice(0, 0, newSegment);
//            $rootScope.igdocument.profile.segmentLibrary.children = positionElements($rootScope.igdocument.profile.segmentLibrary.children);
            $rootScope.segment = newSegment;
//            $rootScope.segmentsMap[newSegment.id] = newSegment;
            $rootScope.recordChanged();
//            $rootScope.$broadcast('event:SetToC');
            $rootScope.$broadcast('event:openSegment', newSegment);
        };

        svc.copyDatatype = function (datatype) {

            var newDatatype = angular.copy(datatype);
            newDatatype.libIds =[];
            newDatatype.id = null;
            newDatatype.participants = [];
            var datatypeLink = DatatypeLibrarySvc.findOneChild(datatype.id, $rootScope.igdocument.profile.datatypeLibrary);
            newDatatype.ext = $rootScope.createNewExtension(datatypeLink.ext);

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
            //$rootScope.igdocument.profile.datatypeLibrary.children.splice(0, 0, newDatatype);
            //$rootScope.igdocument.profile.datatypeLibrary.children = positionElements($rootScope.igdocument.profile.datatypeLibrary.children);

            //$rootScope.datatypes.splice(0, 0, newDatatype);
            $rootScope.datatype = newDatatype;
            //$rootScope.datatypesMap[newDatatype.id] = newDatatype;
            $rootScope.recordChanged();
            //$rootScope.$broadcast('event:SetToC');
            $rootScope.$broadcast('event:openDatatype', newDatatype);
        };

        svc.copyTable = function (table) {
            var newTable = angular.copy(table);
            newTable.libIds =[];
            newTable.id = null;
            newTable.participants = [];
            var link = TableLibrarySvc.findOneChild(table.id, $rootScope.igdocument.profile.tableLibrary);
            newTable.bindingIdentifier = $rootScope.createNewExtension(link.bindingIdentifier);
            newTable.codes = [];
            for (var i = 0, len1 = table.codes.length; i < len1; i++) {
                var newValue = {
                    id: new ObjectId().toString(),
                    type: 'value',
                    value: table.codes[i].value,
                    label: table.codes[i].label,
                    codeSystem: table.codes[i].codeSystem,
                    codeUsage: table.codes[i].codeUsage
                };

                newTable.codes.push(newValue);
            }

            $rootScope.table = newTable;
//            $rootScope.tablesMap[newTable.id] = newTable;

            $rootScope.codeSystems = [];

            for (var i = 0; i < $rootScope.table.codes.length; i++) {
                if ($rootScope.codeSystems.indexOf($rootScope.table.codes[i].codeSystem) < 0) {
                    if ($rootScope.table.codes[i].codeSystem && $rootScope.table.codes[i].codeSystem !== '') {
                        $rootScope.codeSystems.push($rootScope.table.codes[i].codeSystem);
                    }
                }
            }

//            $rootScope.igdocument.profile.tableLibrary.children.splice(0, 0, newTable);
//            $rootScope.igdocument.profile.tableLibrary.children = positionElements($rootScope.igdocument.profile.tableLibrary.children);
            $rootScope.recordChanged();
//            $rootScope.$broadcast('event:SetToC');
            $rootScope.$broadcast('event:openTable', newTable);
        };

        svc.copyMessage = function (message) {
            // TODO gcr: Need to include the user identifier in the
            // new label.
            // $rootScope.igdocument.metaData.ext should be just that,
            // but is currently
            // unpopulated in the profile.
            var newMessage = angular.copy(message);
            //newMessage.id = new ObjectId().toString();
            var groups = ProfileAccessSvc.Messages().getGroups(newMessage);
            angular.forEach(groups, function (group) {
                group.id = new ObjectId().toString();
            });
            newMessage.name = $rootScope.createNewFlavorName(message.name);
            //$rootScope.igdocument.profile.messages.children.splice(0, 0, newMessage);
            //$rootScope.$broadcast('event:SetToC');
            return newMessage;
        };

        svc.deleteValueSet = function (table) {
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                $rootScope.findTableRefs(table, segment);
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
                $scope.tableToDelete = table;
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
                tableToDelete = table;
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
//				console.log("deleteValueSets: vssIdsSincerelyDead=" + vssIdsSincerelyDead.length);
            return ProfileAccessSvc.ValueSets().removeDead(vssIdsSincerelyDead);
        }

        svc.deleteDatatype = function (datatype) {
            $rootScope.references = [];
            angular.forEach($rootScope.segments, function (segment) {
                $rootScope.findDatatypeRefs(datatype, segment);
            });
            if ($rootScope.references != null && $rootScope.references.length > 0) {
                abortDatatypeDelete(datatype);
            } else {
                confirmDatatypeDelete(datatype);
            }
        }

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
            $rootScope.references = ProfileAccessSvc.Segments().getParentalDependencies(segment);
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
            return rval;
        }

        svc.deleteMessage = function (message) {
            confirmMessageDelete(message);
        }

        svc.deleteSection = function (section) {

            var secLive = section.parent.childSections;

            var idxP = _.findIndex(secLive, function (child) {
                return child.id === section.reference.id;
            });
            section.parent.childSections.splice(idxP, 1);
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

        return svc;
    });
